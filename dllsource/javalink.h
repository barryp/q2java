#ifndef _Included_javalink
#define _Included_javalink

#include <jni.h>

extern JNIEnv *java_env;
extern char *java_error;
extern char *javalink_version;

// global functions
extern char java_gameDirName[];
void debugLog(const char *msg, ...);
void startJava();
void stopJava();


#endif