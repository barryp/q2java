#include <jni.h>
#include "globals.h" 
#include "javalink.h"


// handles to the Game class
static jclass class_Game;
static jmethodID method_Game_init;
static jmethodID method_Game_shutdown;
static jmethodID method_Game_spawnEntities;
static jmethodID method_Game_writeGame;
static jmethodID method_Game_readGame;
static jmethodID method_Game_writeLevel;
static jmethodID method_Game_readLevel;
static jmethodID method_Game_runFrame;
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
	method_Game_spawnEntities = (*java_env)->GetMethodID(java_env, class_Game, "spawnEntities", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
	method_Game_writeGame = (*java_env)->GetMethodID(java_env, class_Game, "writeGame", "(Ljava/lang/String;)V");
	method_Game_readGame = (*java_env)->GetMethodID(java_env, class_Game, "readGame", "(Ljava/lang/String;)V");
	method_Game_writeLevel = (*java_env)->GetMethodID(java_env, class_Game, "writeLevel", "(Ljava/lang/String;)V");
	method_Game_readLevel = (*java_env)->GetMethodID(java_env, class_Game, "readLevel", "(Ljava/lang/String;)V");
	method_Game_runFrame = (*java_env)->GetMethodID(java_env, class_Game, "runFrame", "()V");
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
	int i;

	(*java_env)->CallVoidMethod(java_env, object_Game, method_Game_init);
	if (CHECK_EXCEPTION())
		return;

	Entity_arrayInit();

	global_maxClients = 9; // ---FIXME---
	global_clients = gi.TagMalloc(global_maxClients * sizeof(gclient_t), TAG_GAME);

	// link the two arrays together
	for (i = 0; i < global_maxClients; i++)
		{
		ge.edicts[i+1].client = global_clients + i;

		// a little tweak to get things going
		global_clients[i].ps.fov = 90;
		}

	ge.num_edicts = global_maxClients + 1; 
	}


static void java_shutdown(void)
	{
	(*java_env)->CallVoidMethod(java_env, object_Game, method_Game_shutdown);
	CHECK_EXCEPTION();

	stopJava();
	gi.FreeTags (TAG_GAME);
	}


static void java_spawnEntities(char *mapname, char *entString, char *spawnpoint)
	{
	jstring jmapname;
	jstring jentString;
	jstring jspawnpoint;

	jmapname = (*java_env)->NewStringUTF(java_env, mapname);
	jentString = (*java_env)->NewStringUTF(java_env, entString);
	jspawnpoint = (*java_env)->NewStringUTF(java_env, spawnpoint);

	(*java_env)->CallVoidMethod(java_env, object_Game, method_Game_spawnEntities, jmapname, jentString, jspawnpoint);
	CHECK_EXCEPTION();

	global_frameCount = 0;
	global_frameTime = 0.0;	
	}


static void java_writeGame(char *filename)
	{
	jstring jfilename;

	jfilename = (*java_env)->NewStringUTF(java_env, filename);
	(*java_env)->CallVoidMethod(java_env, object_Game, method_Game_writeGame, jfilename);
	CHECK_EXCEPTION();
	}


static void java_readGame(char *filename)
	{
	jstring jfilename;

	jfilename = (*java_env)->NewStringUTF(java_env, filename);
	(*java_env)->CallVoidMethod(java_env, object_Game, method_Game_readGame, jfilename);
	CHECK_EXCEPTION();
	}


static void java_writeLevel(char *filename)
	{
	jstring jfilename;

	jfilename = (*java_env)->NewStringUTF(java_env, filename);
	(*java_env)->CallVoidMethod(java_env, object_Game, method_Game_writeLevel, jfilename);
	CHECK_EXCEPTION();
	}


static void java_readLevel(char *filename)
	{
	jstring jfilename;

	jfilename = (*java_env)->NewStringUTF(java_env, filename);
	(*java_env)->CallVoidMethod(java_env, object_Game, method_Game_readLevel, jfilename);
	CHECK_EXCEPTION();
	}

static void java_runFrame(void)
	{
	// track the running time to help with entity management
	global_frameCount++;
	global_frameTime= global_frameCount*FRAMETIME;

	(*java_env)->CallVoidMethod(java_env, object_Game, method_Game_runFrame);
	CHECK_EXCEPTION();
	}

// -------- Hook us up with the Quake II program

void Game_gameInit()
	{
	ge.Init = java_init;
	ge.Shutdown = java_shutdown;
	ge.SpawnEntities = java_spawnEntities;

	ge.WriteGame = java_writeGame;
	ge.ReadGame = java_readGame;
	ge.WriteLevel = java_writeLevel;
	ge.ReadLevel = java_readLevel;

	ge.RunFrame = java_runFrame;
	}