#ifndef _Included_javalink
#define _Included_javalink

#include <jni.h>

// global variables
extern JNIEnv *java_env;
extern char *java_error;
extern char *javalink_version;
extern char *javalink_gameDirName;

// global functions
void javalink_debug(const char *msg, ...);
void javalink_property(const char *name, const char *value);
void javalink_start();
void javalink_stop();

#endif