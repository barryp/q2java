/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class NativeEntity */

#ifndef _Included_NativeEntity
#define _Included_NativeEntity
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     NativeEntity
 * Method:    setVec3
 * Signature: (IIFFF)V
 */
void JNICALL Java_NativeEntity_setVec3
  (JNIEnv *, jclass, jint, jint, jfloat, jfloat, jfloat);

/*
 * Class:     NativeEntity
 * Method:    sound0
 * Signature: (FFFIIIFFFI)V
 */
void JNICALL Java_NativeEntity_sound0
  (JNIEnv *, jclass, jfloat, jfloat, jfloat, jint, jint, jint, jfloat, jfloat, jfloat, jint);

/*
 * Class:     NativeEntity
 * Method:    freeEntity
 * Signature: (I)V
 */
void JNICALL Java_NativeEntity_freeEntity0
  (JNIEnv *, jclass, jint);

/*
 * Class:     NativeEntity
 * Method:    allocateEntity
 * Signature: (Z)I
 */
jint JNICALL Java_NativeEntity_allocateEntity
  (JNIEnv *, jclass, jboolean);

/*
 * Class:     NativeEntity
 * Method:    getVec3
 * Signature: (II)Vec3;
 */
jobject JNICALL Java_NativeEntity_getVec3
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     NativeEntity
 * Method:    unlinkEntity0
 * Signature: (I)V
 */
void JNICALL Java_NativeEntity_unlinkEntity0
  (JNIEnv *, jclass, jint);

/*
 * Class:     NativeEntity
 * Method:    setInt
 * Signature: (III)V
 */
void JNICALL Java_NativeEntity_setInt
  (JNIEnv *, jclass, jint, jint, jint);

/*
 * Class:     NativeEntity
 * Method:    getInt
 * Signature: (II)I
 */
jint JNICALL Java_NativeEntity_getInt
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     NativeEntity
 * Method:    setModel0
 * Signature: (ILjava/lang/String;)V
 */
void JNICALL Java_NativeEntity_setModel0
  (JNIEnv *, jclass, jint, jstring);

/*
 * Class:     NativeEntity
 * Method:    linkEntity0
 * Signature: (I)V
 */
void JNICALL Java_NativeEntity_linkEntity0
  (JNIEnv *, jclass, jint);


/*
 * Class:     NativeEntity
 * Method:    cprint0
 * Signature: (IILjava/lang/String;)V
 */
void JNICALL Java_NativeEntity_cprint0
  (JNIEnv *, jclass, jint, jint, jstring);

/*
 * Class:     NativeEntity
 * Method:    pMove0
 * Signature: (I)LPMoveResults;
 */
jobject JNICALL Java_NativeEntity_pMove0
  (JNIEnv *, jclass, jint);

/*
 * Class:     NativeEntity
 * Method:    setStat0
 * Signature: (IIS)V
 */
void JNICALL Java_NativeEntity_setStat0
  (JNIEnv *, jclass, jint, jint, jshort);

/*
 * Class:     NativeEntity
 * Method:    setFloat0
 * Signature: (IIFFFF)V
 */
void JNICALL Java_NativeEntity_setFloat0
  (JNIEnv *, jclass, jint, jint, jfloat, jfloat, jfloat, jfloat);

/*
 * Class:     NativeEntity
 * Method:    centerprint0
 * Signature: (ILjava/lang/String;)V
 */
void JNICALL Java_NativeEntity_centerprint0
  (JNIEnv *, jclass, jint, jstring);

#ifdef __cplusplus
}
#endif
#endif
