#ifndef _Included_javalink
#define _Included_javalink

#include <jni.h>
#include "Quake2.h"

#define CHECK_EXCEPTION() checkException(__FILE__, __LINE__)

extern JNIEnv *java_env;

int startJava();
void stopJava();
int checkException(char *filename, int linenum);
jobject newJavaVec3(vec3_t *v);

#endif