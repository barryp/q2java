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


void Game_javaInit()
	{
	class_Game = (*java_env)->FindClass(java_env, "Game");
	CHECK_EXCEPTION();
	if (!class_Game)
		debugLog("Couldn't get Java Game class\n");
	else
		{
		method_Game_init = (*java_env)->GetStaticMethodID(java_env, class_Game, "init", "()V");
		method_Game_shutdown = (*java_env)->GetStaticMethodID(java_env, class_Game, "shutdown", "()V");
		method_Game_spawnEntities = (*java_env)->GetStaticMethodID(java_env, class_Game, "spawnEntities", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
		method_Game_writeGame = (*java_env)->GetStaticMethodID(java_env, class_Game, "writeGame", "(Ljava/lang/String;)V");
		method_Game_readGame = (*java_env)->GetStaticMethodID(java_env, class_Game, "readGame", "(Ljava/lang/String;)V");
		method_Game_writeLevel = (*java_env)->GetStaticMethodID(java_env, class_Game, "writeLevel", "(Ljava/lang/String;)V");
		method_Game_readLevel = (*java_env)->GetStaticMethodID(java_env, class_Game, "readLevel", "(Ljava/lang/String;)V");
		method_Game_runFrame = (*java_env)->GetStaticMethodID(java_env, class_Game, "runFrame", "()V");
		}
	CHECK_EXCEPTION();
	}

void Game_javaFinalize()
	{
	}

//-------  Functions exported back to Quake2 ---------------


static void java_init(void)
	{
	int i;

	debugLog("In init()\n");

	startJava();

	if (!method_Game_init)
		{
		debugLog("Java init() method not available\n");
		return;
		}

	(*java_env)->CallStaticVoidMethod(java_env, class_Game, method_Game_init);
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
	if (!method_Game_shutdown)
		{
		debugLog("Java shutdown() method not available\n");
		return;
		}

	(*java_env)->CallStaticVoidMethod(java_env, class_Game, method_Game_shutdown);
	CHECK_EXCEPTION();

	stopJava();
	gi.FreeTags (TAG_GAME);
	}


static void java_spawnEntities(char *mapname, char *entString, char *spawnpoint)
	{
	jstring jmapname;
	jstring jentString;
	jstring jspawnpoint;

	if (!method_Game_spawnEntities)
		{
		debugLog("Java spawnEntities() method not available\n");
		return;
		}

	jmapname = (*java_env)->NewStringUTF(java_env, mapname);
	jentString = (*java_env)->NewStringUTF(java_env, entString);
	jspawnpoint = (*java_env)->NewStringUTF(java_env, spawnpoint);

	(*java_env)->CallStaticVoidMethod(java_env, class_Game, method_Game_spawnEntities, jmapname, jentString, jspawnpoint);
	CHECK_EXCEPTION();

	global_frameCount = 0;
	global_frameTime = 0.0;	
	}


static void java_writeGame(char *filename)
	{
	jstring jfilename;

	if (!method_Game_writeGame)
		{
		debugLog("Java writeGame() method not available\n");
		return;
		}

	jfilename = (*java_env)->NewStringUTF(java_env, filename);
	(*java_env)->CallStaticVoidMethod(java_env, class_Game, method_Game_writeGame, jfilename);
	CHECK_EXCEPTION();
	}


static void java_readGame(char *filename)
	{
	jstring jfilename;

	if (!method_Game_readGame)
		{
		debugLog("Java readGame() method not available\n");
		return;
		}

	jfilename = (*java_env)->NewStringUTF(java_env, filename);
	(*java_env)->CallStaticVoidMethod(java_env, class_Game, method_Game_readGame, jfilename);
	CHECK_EXCEPTION();
	}


static void java_writeLevel(char *filename)
	{
	jstring jfilename;

	if (!method_Game_writeLevel)
		{
		debugLog("Java writeLevel() method not available\n");
		return;
		}

	jfilename = (*java_env)->NewStringUTF(java_env, filename);
	(*java_env)->CallStaticVoidMethod(java_env, class_Game, method_Game_writeLevel, jfilename);
	CHECK_EXCEPTION();
	}


static void java_readLevel(char *filename)
	{
	jstring jfilename;

	if (!method_Game_readLevel)
		{
		debugLog("Java readLevel() method not available\n");
		return;
		}

	jfilename = (*java_env)->NewStringUTF(java_env, filename);
	(*java_env)->CallStaticVoidMethod(java_env, class_Game, method_Game_readLevel, jfilename);
	CHECK_EXCEPTION();
	}

static void java_runFrame(void)
	{
	if (!method_Game_runFrame)
		{
		debugLog("Java runFrame() method not available\n");
		return;
		}

	// track the running time to help with entity management
	global_frameCount++;
	global_frameTime= global_frameCount*FRAMETIME;

	(*java_env)->CallStaticVoidMethod(java_env, class_Game, method_Game_runFrame);
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