/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class q2java_Engine */

#ifndef _Included_q2java_Engine
#define _Included_q2java_Engine
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     q2java_Engine
 * Method:    bprint
 * Signature: (ILjava/lang/String;)V
 */
static void JNICALL Java_q2java_Engine_bprint
  (JNIEnv *, jclass, jint, jstring);

/*
 * Class:     q2java_Engine
 * Method:    write0
 * Signature: (Ljava/lang/Object;FFFII)V
 */
static void JNICALL Java_q2java_Engine_write0
  (JNIEnv *, jclass, jobject, jfloat, jfloat, jfloat, jint, jint);

/*
 * Class:     q2java_Engine
 * Method:    getArgs
 * Signature: ()Ljava/lang/String;
 */
static jstring JNICALL Java_q2java_Engine_getArgs
  (JNIEnv *, jclass);

/*
 * Class:     q2java_Engine
 * Method:    getGamePath
 * Signature: ()Ljava/lang/String;
 */
static jstring JNICALL Java_q2java_Engine_getGamePath
  (JNIEnv *, jclass);

/*
 * Class:     q2java_Engine
 * Method:    setConfigString
 * Signature: (ILjava/lang/String;)V
 */
static void JNICALL Java_q2java_Engine_setConfigString
  (JNIEnv *, jclass, jint, jstring);

/*
 * Class:     q2java_Engine
 * Method:    debugLog
 * Signature: (Ljava/lang/String;)V
 */
static void JNICALL Java_q2java_Engine_debugLog
  (JNIEnv *, jclass, jstring);

/*
 * Class:     q2java_Engine
 * Method:    getBoxEntities0
 * Signature: (FFFFFFI)[Lq2java/NativeEntity;
 */
static jobjectArray JNICALL Java_q2java_Engine_getBoxEntities0
  (JNIEnv *, jclass, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat, jint);

/*
 * Class:     q2java_Engine
 * Method:    addCommandString
 * Signature: (Ljava/lang/String;)V
 */
static void JNICALL Java_q2java_Engine_addCommandString
  (JNIEnv *, jclass, jstring);

/*
 * Class:     q2java_Engine
 * Method:    getSoundIndex
 * Signature: (Ljava/lang/String;)I
 */
static jint JNICALL Java_q2java_Engine_getSoundIndex
  (JNIEnv *, jclass, jstring);

/*
 * Class:     q2java_Engine
 * Method:    getPointContents0
 * Signature: (FFF)I
 */
static jint JNICALL Java_q2java_Engine_getPointContents0
  (JNIEnv *, jclass, jfloat, jfloat, jfloat);

/*
 * Class:     q2java_Engine
 * Method:    areasConnected
 * Signature: (II)Z
 */
static jboolean JNICALL Java_q2java_Engine_areasConnected
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     q2java_Engine
 * Method:    error
 * Signature: (Ljava/lang/String;)V
 */
static void JNICALL Java_q2java_Engine_error
  (JNIEnv *, jclass, jstring);

/*
 * Class:     q2java_Engine
 * Method:    debugGraph
 * Signature: (FI)V
 */
static void JNICALL Java_q2java_Engine_debugGraph
  (JNIEnv *, jclass, jfloat, jint);

/*
 * Class:     q2java_Engine
 * Method:    trace0
 * Signature: (FFFFFFFFFFFFLq2java/NativeEntity;II)Lq2java/TraceResults;
 */
static jobject JNICALL Java_q2java_Engine_trace0
  (JNIEnv *, jclass, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat, jobject, jint, jint);

/*
 * Class:     q2java_Engine
 * Method:    getImageIndex
 * Signature: (Ljava/lang/String;)I
 */
static jint JNICALL Java_q2java_Engine_getImageIndex
  (JNIEnv *, jclass, jstring);

/*
 * Class:     q2java_Engine
 * Method:    getRadiusEntities0
 * Signature: (FFFFIZZ)[Lq2java/NativeEntity;
 */
jobjectArray JNICALL Java_q2java_Engine_getRadiusEntities0
  (JNIEnv *, jclass, jfloat, jfloat, jfloat, jfloat, jint, jboolean, jboolean);

/*
 * Class:     q2java_Engine
 * Method:    getArgv
 * Signature: (I)Ljava/lang/String;
 */
static jstring JNICALL Java_q2java_Engine_getArgv
  (JNIEnv *, jclass, jint);

/*
 * Class:     q2java_Engine
 * Method:    getArgc
 * Signature: ()I
 */
static jint JNICALL Java_q2java_Engine_getArgc
  (JNIEnv *, jclass);

/*
 * Class:     q2java_Engine
 * Method:    setAreaPortalState
 * Signature: (IZ)V
 */
static void JNICALL Java_q2java_Engine_setAreaPortalState
  (JNIEnv *, jclass, jint, jboolean);

/*
 * Class:     q2java_Engine
 * Method:    dprint
 * Signature: (Ljava/lang/String;)V
 */
static void JNICALL Java_q2java_Engine_dprint
  (JNIEnv *, jclass, jstring);

/*
 * Class:     q2java_Engine
 * Method:    inP0
 * Signature: (FFFFFFI)Z
 */
static jboolean JNICALL Java_q2java_Engine_inP0
  (JNIEnv *, jclass, jfloat, jfloat, jfloat, jfloat, jfloat, jfloat, jint);

/*
 * Class:     q2java_Engine
 * Method:    getModelIndex
 * Signature: (Ljava/lang/String;)I
 */
static jint JNICALL Java_q2java_Engine_getModelIndex
  (JNIEnv *, jclass, jstring);

#ifdef __cplusplus
}
#endif
#endif
