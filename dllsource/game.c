#include "globals.h" 
#ifdef _WIN32
    #include "windows.h" // for performance counters
    #include <time.h> // for fallback functions
    static double ticksPerMillisecond;
    static LONGLONG levelTickCounter;
    static int levelFrameCounter;
#endif

// handles to the Game class
static jclass class_Game;
static jobject object_Game;
static jmethodID method_GameListener_init;
static jmethodID method_GameListener_shutdown;
static jmethodID method_GameListener_startLevel;
static jmethodID method_GameListener_writeGame;
static jmethodID method_GameListener_readGame;
static jmethodID method_GameListener_writeLevel;
static jmethodID method_GameListener_readLevel;
static jmethodID method_GameListener_runFrame;
static jmethodID method_GameListener_serverCommand;
static jmethodID method_GameListener_getPlayerClass;
static jmethodID method_GameListener_playerConnect;


void Game_javaInit()
    {
    cvar_t *gameclass_cvar;
    jclass interface_GameListener;
    jmethodID method_GameListener_ctor;
    char *p;
    char buffer[128];


#ifdef _WIN32
    LARGE_INTEGER tickFrequency;
    if (QueryPerformanceFrequency(&tickFrequency))
        ticksPerMillisecond = tickFrequency.QuadPart / 1000.0;
    else 
        ticksPerMillisecond = CLOCKS_PER_SEC / 1000.0;
    levelFrameCounter = 0;
    levelTickCounter = 0;
#endif
    javalink_debug("Game_javaInit() started\n");

    interface_GameListener = (*java_env)->FindClass(java_env, "q2java/GameListener");
    if (CHECK_EXCEPTION() || !interface_GameListener)
        {
        java_error = "Can't find q2java.GameListener interface\n";
        return;
        }

    gameclass_cvar = q2java_gi.cvar("q2java_game", "q2java.core.Game", CVAR_NOSET);

    // convert classname to the strange internal format Java
    // uses, where the periods are replaced with
    // forward slashes
    strcpy(buffer, gameclass_cvar->string);
    for (p = buffer; *p; p++)
        if (*p == '.')
            *p = '/';

    class_Game = (*java_env)->FindClass(java_env, buffer);
    if (CHECK_EXCEPTION() || !class_Game)
        {
        java_error = "Couldn't find the specified Game class\n";
        return;
        }

    if (!((*java_env)->IsAssignableFrom(java_env, class_Game, interface_GameListener)))
        {
        java_error = "The specified game class doesn't implement q2java.GameListener\n";
        return;
        }

    // drop the local reference to the GameListener interface
    (*java_env)->DeleteLocalRef(java_env, interface_GameListener);

    // perhaps we should be GameListener interface to get the method ids instead of 
    // the game class....not sure about this
    method_GameListener_init = (*java_env)->GetMethodID(java_env, class_Game, "init", "()V");
    method_GameListener_shutdown = (*java_env)->GetMethodID(java_env, class_Game, "shutdown", "()V");
    method_GameListener_startLevel = (*java_env)->GetMethodID(java_env, class_Game, "startLevel", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    method_GameListener_writeGame = (*java_env)->GetMethodID(java_env, class_Game, "writeGame", "(Ljava/lang/String;Z)V");
    method_GameListener_readGame = (*java_env)->GetMethodID(java_env, class_Game, "readGame", "(Ljava/lang/String;)V");
    method_GameListener_writeLevel = (*java_env)->GetMethodID(java_env, class_Game, "writeLevel", "(Ljava/lang/String;)V");
    method_GameListener_readLevel = (*java_env)->GetMethodID(java_env, class_Game, "readLevel", "(Ljava/lang/String;)V");
    method_GameListener_runFrame = (*java_env)->GetMethodID(java_env, class_Game, "runFrame", "()V");
    method_GameListener_serverCommand = (*java_env)->GetMethodID(java_env, class_Game, "serverCommand", "()V");
    method_GameListener_getPlayerClass = (*java_env)->GetMethodID(java_env, class_Game, "getPlayerClass", "()Ljava/lang/Class;");
    method_GameListener_playerConnect = (*java_env)->GetMethodID(java_env, class_Game, "playerConnect", "(Lq2java/NativeEntity;)V");
    method_GameListener_ctor = (*java_env)->GetMethodID(java_env, class_Game, "<init>", "()V");
    if (CHECK_EXCEPTION())
        {
        java_error = "Problem getting handle for one or more of the game methods\n";
        return;
        }

    object_Game = (*java_env)->NewObject(java_env, class_Game, method_GameListener_ctor);
    if(CHECK_EXCEPTION())
        {
        java_error = "Couldn't create instance of game object\n";
        return;
        }

    javalink_debug("Game_javaInit() finished\n");
    }


void Game_javaDetach()
    {
    (*java_env)->DeleteLocalRef(java_env, class_Game);
    (*java_env)->DeleteLocalRef(java_env, object_Game);
    }


jclass Game_getPlayerClass()
    {
    jclass result = (*java_env)->CallObjectMethod(java_env, object_Game, method_GameListener_getPlayerClass);
    if (CHECK_EXCEPTION())
        return NULL;
    else
        return result;
    }

int Game_playerConnect(jobject ent)
    {
    (*java_env)->CallVoidMethod(java_env, object_Game, method_GameListener_playerConnect, ent);
    return CHECK_EXCEPTION();
    }


//-------  Functions exported back to Quake2 ---------------


static void java_init(void)
    {
    Entity_arrayInit();
    (*java_env)->CallVoidMethod(java_env, object_Game, method_GameListener_init);
    CHECK_EXCEPTION();
    }


static void java_shutdown(void)
    {
    int i;
    gclient_t *client;

    (*java_env)->CallVoidMethod(java_env, object_Game, method_GameListener_shutdown);
    CHECK_EXCEPTION();

    // clear up Player java objects
    for (i = 1; i < global_maxClients + 1; i++)
        if (q2java_ge.edicts[i].inuse)
            {
            client = q2java_ge.edicts[i].client;
            if (client->listener)
                {
                (*java_env)->DeleteGlobalRef(java_env, client->listener);
                client->listener = NULL;
                }

            if (client->playerInfo)
                {
                (*java_env)->DeleteLocalRef(java_env, client->playerInfo);
                client->playerInfo = NULL;
                }
            }


    javalink_stop();
    q2java_gi.FreeTags (TAG_GAME);
    }


static void java_startLevel(char *mapname, char *entString, char *spawnpoint)
    {
    jstring jmapname;
    jstring jentString;
    jstring jspawnpoint;

    Entity_arrayReset();

    Engine_startLevel();  // so the Engine class can track VWep indexes properly

    jmapname = (*java_env)->NewStringUTF(java_env, mapname);
    jentString = (*java_env)->NewStringUTF(java_env, entString);
    jspawnpoint = (*java_env)->NewStringUTF(java_env, spawnpoint);

    (*java_env)->CallVoidMethod(java_env, object_Game, method_GameListener_startLevel, jmapname, jentString, jspawnpoint);
    CHECK_EXCEPTION();

    (*java_env)->DeleteLocalRef(java_env, jmapname);
    (*java_env)->DeleteLocalRef(java_env, jentString);
    (*java_env)->DeleteLocalRef(java_env, jspawnpoint);

    global_frameCount = 0;

#ifdef _WIN32
    levelTickCounter = 0;
    levelFrameCounter = 0;
#endif
    }


static void java_writeGame(char *filename, int autosave)
    {
    jstring jfilename;

    jfilename = (*java_env)->NewStringUTF(java_env, filename);
    (*java_env)->CallVoidMethod(java_env, object_Game, method_GameListener_writeGame, jfilename, autosave);
    CHECK_EXCEPTION();

    (*java_env)->DeleteLocalRef(java_env, jfilename);
    }


static void java_readGame(char *filename)
    {
    jstring jfilename;

    jfilename = (*java_env)->NewStringUTF(java_env, filename);
    (*java_env)->CallVoidMethod(java_env, object_Game, method_GameListener_readGame, jfilename);
    CHECK_EXCEPTION();

    (*java_env)->DeleteLocalRef(java_env, jfilename);
    }


static void java_writeLevel(char *filename)
    {
    jstring jfilename;

    jfilename = (*java_env)->NewStringUTF(java_env, filename);
    (*java_env)->CallVoidMethod(java_env, object_Game, method_GameListener_writeLevel, jfilename);
    CHECK_EXCEPTION();

    (*java_env)->DeleteLocalRef(java_env, jfilename);   
    }


static void java_readLevel(char *filename)
    {
    jstring jfilename;

    jfilename = (*java_env)->NewStringUTF(java_env, filename);
    (*java_env)->CallVoidMethod(java_env, object_Game, method_GameListener_readLevel, jfilename);
    CHECK_EXCEPTION();

    (*java_env)->DeleteLocalRef(java_env, jfilename);
    }

static void java_runFrame(void)
    {
#ifdef _WIN32
    LARGE_INTEGER tick, tock;
    if (!QueryPerformanceCounter(&tick))
        tick.QuadPart = clock();
#endif

    // track the running time to help with entity management
    global_frameCount++;

    (*java_env)->CallVoidMethod(java_env, object_Game, method_GameListener_runFrame);
    CHECK_EXCEPTION();

    // give the engine a chance to think about things
    Engine_runDeferred();

#ifdef _WIN32
    if (!QueryPerformanceCounter(&tock))
        tock.QuadPart = clock();
    levelTickCounter += (tock.QuadPart - tick.QuadPart);
    levelFrameCounter++;
#endif
    }

static void java_serverCommand(void)
    {
#ifdef _WIN32
    if (!strcmp(q2java_gi.argv(1), "time"))
        {
        double msec = (levelTickCounter / levelFrameCounter) / ticksPerMillisecond;
        q2java_gi.dprintf(" DLL: %.0f ticks, %d frames, %.3f ticks/msec, %.3f msec/frame average\n", (float) levelTickCounter, levelFrameCounter, ticksPerMillisecond, msec);
        levelTickCounter = 0;
        levelFrameCounter = 0;
        }
#endif

    (*java_env)->CallVoidMethod(java_env, object_Game, method_GameListener_serverCommand);
    CHECK_EXCEPTION();
    }


// -------- Hook us up with the Quake II program

void Game_gameInit()
    {
    q2java_ge.Init = java_init;
    q2java_ge.Shutdown = java_shutdown;
    q2java_ge.SpawnEntities = java_startLevel;

    q2java_ge.WriteGame = java_writeGame;
    q2java_ge.ReadGame = java_readGame;
    q2java_ge.WriteLevel = java_writeLevel;
    q2java_ge.ReadLevel = java_readLevel;

    q2java_ge.RunFrame = java_runFrame;
    q2java_ge.ServerCommand = java_serverCommand;
    }
