#include "globals.h" 


// handles to the Game class
static jclass class_Game;
static jmethodID method_Game_init;
static jmethodID method_Game_shutdown;
static jmethodID method_Game_startLevel;
static jmethodID method_Game_writeGame;
static jmethodID method_Game_readGame;
static jmethodID method_Game_writeLevel;
static jmethodID method_Game_readLevel;
static jmethodID method_Game_runFrame;
static jmethodID method_Game_serverCommand;
static jobject object_Game;


void Game_javaInit()
	{
	cvar_t *gameclass_cvar;
	jclass interface_nativeGame;
	jmethodID method_Game_ctor;
	char *p;
	char buffer[128];

	interface_nativeGame = (*java_env)->FindClass(java_env, "q2java/NativeGame");
	if (CHECK_EXCEPTION() || !interface_nativeGame)
		{
		java_error = "Can't find q2java.NativeGame interface\n";
		return;
		}

	gameclass_cvar = gi.cvar("q2java_game", "q2jgame.Game", CVAR_NOSET);

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

	if (!((*java_env)->IsAssignableFrom(java_env, class_Game, interface_nativeGame)))
		{
		java_error = "The specified game class doesn't implement q2java.NativeGame\n";
		return;
		}

	method_Game_init = (*java_env)->GetMethodID(java_env, class_Game, "init", "()V");
	method_Game_shutdown = (*java_env)->GetMethodID(java_env, class_Game, "shutdown", "()V");
	method_Game_startLevel = (*java_env)->GetMethodID(java_env, class_Game, "startLevel", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
	method_Game_writeGame = (*java_env)->GetMethodID(java_env, class_Game, "writeGame", "(Ljava/lang/String;)V");
	method_Game_readGame = (*java_env)->GetMethodID(java_env, class_Game, "readGame", "(Ljava/lang/String;)V");
	method_Game_writeLevel = (*java_env)->GetMethodID(java_env, class_Game, "writeLevel", "(Ljava/lang/String;)V");
	method_Game_readLevel = (*java_env)->GetMethodID(java_env, class_Game, "readLevel", "(Ljava/lang/String;)V");
	method_Game_runFrame = (*java_env)->GetMethodID(java_env, class_Game, "runFrame", "()V");
	method_Game_serverCommand = (*java_env)->GetMethodID(java_env, class_Game, "serverCommand", "()V");
	method_Game_ctor = (*java_env)->GetMethodID(java_env, class_Game, "<init>", "()V");
	if (CHECK_EXCEPTION())
		{
		java_error = "Problem getting handle for one or more of the game methods\n";
		return;
		}

	object_Game = (*java_env)->NewObject(java_env, class_Game, method_Game_ctor);
	if(CHECK_EXCEPTION())
		{
		java_error = "Couldn't create instance of game object\n";
		return;
		}
	}

void Game_javaFinalize()
	{
	}

//-------  Functions exported back to Quake2 ---------------


static void java_init(void)
	{
//	debugLog("java_init() started\n");
	Entity_arrayInit();
	(*java_env)->CallVoidMethod(java_env, object_Game, method_Game_init);
	CHECK_EXCEPTION();
//	debugLog("java_init() finished\n");
	}


static void java_shutdown(void)
	{
//	debugLog("java_shutdown() started\n");
	(*java_env)->CallVoidMethod(java_env, object_Game, method_Game_shutdown);
	CHECK_EXCEPTION();

	stopJava();
	gi.FreeTags (TAG_GAME);
//	debugLog("java_shutdown() finished\n");
	}


static void java_startLevel(char *mapname, char *entString, char *spawnpoint)
	{
	jstring jmapname;
	jstring jentString;
	jstring jspawnpoint;

//	debugLog("java_spawnEntities() started\n");

	Entity_arrayReset();

	jmapname = (*java_env)->NewStringUTF(java_env, mapname);
	jentString = (*java_env)->NewStringUTF(java_env, entString);
	jspawnpoint = (*java_env)->NewStringUTF(java_env, spawnpoint);

	(*java_env)->CallVoidMethod(java_env, object_Game, method_Game_startLevel, jmapname, jentString, jspawnpoint);
	CHECK_EXCEPTION();

	global_frameCount = 0;

//	debugLog("java_spawnEntities() finished\n");
	}


static void java_writeGame(char *filename)
	{
	jstring jfilename;

//	debugLog("java_writeGame() started\n");

	jfilename = (*java_env)->NewStringUTF(java_env, filename);
	(*java_env)->CallVoidMethod(java_env, object_Game, method_Game_writeGame, jfilename);
	CHECK_EXCEPTION();

//	debugLog("java_writeGame() finished\n");
	}


static void java_readGame(char *filename)
	{
	jstring jfilename;

//	debugLog("java_readGame() started\n");
	jfilename = (*java_env)->NewStringUTF(java_env, filename);
	(*java_env)->CallVoidMethod(java_env, object_Game, method_Game_readGame, jfilename);
	CHECK_EXCEPTION();
//	debugLog("java_readGame() finished\n");
	}


static void java_writeLevel(char *filename)
	{
	jstring jfilename;
//	debugLog("java_writeLevel() started\n");
	jfilename = (*java_env)->NewStringUTF(java_env, filename);
	(*java_env)->CallVoidMethod(java_env, object_Game, method_Game_writeLevel, jfilename);
	CHECK_EXCEPTION();
//	debugLog("java_writeLevel() finished\n");
	}


static void java_readLevel(char *filename)
	{
	jstring jfilename;

//	debugLog("java_readLevel() started\n");
	jfilename = (*java_env)->NewStringUTF(java_env, filename);
	(*java_env)->CallVoidMethod(java_env, object_Game, method_Game_readLevel, jfilename);
	CHECK_EXCEPTION();
//	debugLog("java_readLevel() finished\n");
	}

static void java_runFrame(void)
	{
//	debugLog("java_runFrame() started\n");
	// track the running time to help with entity management
	global_frameCount++;

	(*java_env)->CallVoidMethod(java_env, object_Game, method_Game_runFrame);
	CHECK_EXCEPTION();
//	debugLog("java_runFrame() finished\n");
	}

static void java_serverCommand(void)
	{
	(*java_env)->CallVoidMethod(java_env, object_Game, method_Game_serverCommand);
	CHECK_EXCEPTION();
	}


// -------- Hook us up with the Quake II program

void Game_gameInit()
	{
	ge.Init = java_init;
	ge.Shutdown = java_shutdown;
	ge.SpawnEntities = java_startLevel;

	ge.WriteGame = java_writeGame;
	ge.ReadGame = java_readGame;
	ge.WriteLevel = java_writeLevel;
	ge.ReadLevel = java_readLevel;

	ge.RunFrame = java_runFrame;
	ge.ServerCommand = java_serverCommand;
	}