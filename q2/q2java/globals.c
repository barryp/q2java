#include <windows.h> // only for GetModuleFileName() in setupPaths()
#include <stdio.h>
#include <stdarg.h>
#include "globals.h"
#include "javalink.h"

game_import_t	gi;
game_export_t	ge;

// to help with entity management
int global_frameCount;
float global_frameTime;
int global_maxClients;
gclient_t *global_clients;

char global_gameDirName[1024];
static char debugFileName[1024];

void debugLog(const char *msg, ...)
	{
	va_list ap;
	FILE *f = fopen(debugFileName, "a");

	va_start(ap, msg);
	vfprintf(f, msg, ap);
	va_end(ap);

	fclose(f);
	}

// Figure out what directory we're operating out of,
// so we can write a debug trace file and later
// setup a Java classpath
//
static void setupPaths()
	{	
	char *p;
	cvar_t *game_cvar = gi.cvar("game", "baseq2", 0);			

	// WINDOWS SPECIFIC: get the full pathname of the Quake2 .EXE file
    GetModuleFileName(0, global_gameDirName, 1024);

	// find the last backslash in the path
	p = strrchr(global_gameDirName, '\\');

	// p should never be null, but just in case....
	if (!p)
		p = global_gameDirName;
	else
		p++; // p is now just after the last backslash
	
	// append the name of the game
	strcpy(p, game_cvar->string);

	// save the name of our debugLog file
	sprintf(debugFileName, "%s\\q2java.log", global_gameDirName);

	// erase any existing debug file
	remove(debugFileName);
	}


game_export_t *GetGameAPI (game_import_t *import)
	{
	gi = *import;

	setupPaths();

	startJava();
	if (java_error)
		{
		gi.error(java_error);
		return 0;
		}

	gi.dprintf("Quake2 Java DLL initialized\n");

	// setup the game_export_t structure
	ge.apiversion = GAME_API_VERSION;

	Game_gameInit();
	Player_gameInit();

	ge.edict_size = sizeof(edict_t);

	return &ge;
	}
