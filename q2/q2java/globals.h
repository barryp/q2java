#ifndef _Included_globals
#define _Included_globals

#include <jni.h>
#include "Quake2.h"

// game import and export structures
extern game_import_t	gi;
extern game_export_t	ge;

// to help with entity management
extern int global_frameCount;
extern int global_maxClients;

extern gclient_t *global_clients;
extern usercmd_t *thinkCmd;

// global functions
extern char global_gameDirName[];
void debugLog(const char *msg, ...);

void CVar_javaInit();
void CVar_javaFinalize();

void Engine_javaInit();
void Engine_javaFinalize();

void Entity_javaInit();
void Entity_arrayInit();
jobject Entity_getEntity(int index);
void	Entity_setEntity(int index, jobject value);
int		Entity_get_fEntityIndex(jobject jent);
void	Entity_set_fEntityIndex(jobject jent, int index);
jobjectArray Entity_createArray(edict_t **list, int count);
void Entity_javaFinalize();

void Player_gameInit();
void Player_javaInit();
void Player_javaFinalize();
jobject Player_new(int index);

void Game_gameInit();
void Game_javaInit();
void Game_javaFinalize();

#endif