#include "globals.h"

#define DLL_VERSION "0.3"

game_import_t	gi;
game_export_t	ge;

// to help with entity management
int global_frameCount;
int global_maxClients;
gclient_t *global_clients;


game_export_t *GetGameAPI (game_import_t *import)
	{
	gi = *import;

	startJava();
	if (java_error)
		{
		gi.error(java_error);
		return 0;
		}

	gi.dprintf("=============================\nQuake 2 Java DLL v %s\n(c) 1998 Barry Pederson\n<bpederson@geocities.com>\n=============================\n", DLL_VERSION);

	// setup the game_export_t structure
	ge.apiversion = GAME_API_VERSION;

	Game_gameInit();
	Player_gameInit();

	ge.edict_size = sizeof(edict_t);

	return &ge;
	}
