#ifndef _Included_globals
#define _Included_globals

#include "javalink.h"
#include "quake2.h"

extern char *q2java_version;
extern int q2java_jinsight;

// game import and export structures
extern game_import_t	q2java_gi;
extern game_export_t	q2java_ge;

// to help with entity management
extern int global_frameCount;
extern int global_maxClients;

extern gclient_t *global_clients;
extern jclass class_NativeEntity;

void Misc_javaInit();
void Misc_javaDetach();
#define CHECK_EXCEPTION() checkException(__FILE__, __LINE__)
int checkException(char *filename, int linenum);
void enableSecurity(int level);
jobject newPMoveResults(pmove_t pm);
jobject newTraceResults(trace_t trace);
jobject newJavaVec3(vec3_t *v, int vecType);
void setPlayerCmd(jbyte, jbyte, short, short, short, short, short, short, jbyte, jbyte);
extern jobject playerCmd;
char *convertJavaString(jstring jstr);

void CVar_javaInit();
void CVar_javaDetach();

void Engine_javaInit();
void Engine_javaDetach();
void Engine_startLevel();
void Engine_runDeferred();
// Engine method called by a NativeEntity method
jobjectArray JNICALL Java_q2java_Engine_getRadiusEntities0
  (JNIEnv *, jclass, jfloat, jfloat, jfloat, jfloat, jint, jboolean, jboolean);


void Entity_javaInit();
void Entity_javaDetach();
void Entity_gameInit();
void Entity_arrayInit();
void Entity_arrayReset();
jobject Entity_getEntity(int index);
void	Entity_setEntity(int index, jobject value);
int		Entity_get_fEntityIndex(jobject jent);
void	Entity_set_fEntityIndex(jobject jent, int index);
jobjectArray Entity_createArray(edict_t **list, int count);

void Player_gameInit();
void Player_javaInit();
void Player_javaDetach();
jobject Player_new(int index);

void Game_gameInit();
void Game_javaInit();
void Game_javaDetach();
jclass Game_getPlayerClass();
int Game_playerConnect(jobject ent);

#endif