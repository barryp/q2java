#include "globals.h"

game_import_t   q2java_gi;
game_export_t   q2java_ge;
char *q2java_version = "0.9.1";

// to help with entity management
int global_frameCount;
int global_maxClients;
gclient_t *global_clients;


game_export_t *GetGameAPI(game_import_t *import)
    {
    q2java_gi = *import;

    javalink_start();
    if (java_error)
        {
        q2java_gi.error(java_error);
        return 0;
        }

    q2java_gi.dprintf("=============================\nQ2Java Game Interface v%s\n", q2java_version);
    q2java_gi.dprintf("(c) 1999 Barry Pederson\n<bpederson@geocities.com>\n");
    q2java_gi.dprintf("--------------\n");
    q2java_gi.dprintf("%s\n=============================\n", javalink_version);

    // setup the game_export_t structure
    q2java_ge.apiversion = GAME_API_VERSION;

    Game_gameInit();
    Player_gameInit();

    q2java_ge.edict_size = sizeof(edict_t);

    return &q2java_ge;
    }
