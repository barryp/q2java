/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class q2java_NativeEntity */

#ifndef _Included_q2java_NativeEntity
#define _Included_q2java_NativeEntity
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     q2java_NativeEntity
 * Method:    setVec3
 * Signature: (IIFFF)V
 */
void JNICALL Java_q2java_NativeEntity_setVec3
  (JNIEnv *, jclass, jint, jint, jfloat, jfloat, jfloat);

/*
 * Class:     q2java_NativeEntity
 * Method:    allocateEntity
 * Signature: (Z)I
 */
jint JNICALL Java_q2java_NativeEntity_allocateEntity
  (JNIEnv *, jclass, jboolean);

/*
 * Class:     q2java_NativeEntity
 * Method:    getInt
 * Signature: (II)I
 */
jint JNICALL Java_q2java_NativeEntity_getInt
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     q2java_NativeEntity
 * Method:    setShort
 * Signature: (IIS)V
 */
void JNICALL Java_q2java_NativeEntity_setShort
  (JNIEnv *, jclass, jint, jint, jshort);

/*
 * Class:     q2java_NativeEntity
 * Method:    linkEntity0
 * Signature: (I)V
 */
void JNICALL Java_q2java_NativeEntity_linkEntity0
  (JNIEnv *, jclass, jint);

/*
 * Class:     q2java_NativeEntity
 * Method:    setFloat0
 * Signature: (IIFFFF)V
 */
void JNICALL Java_q2java_NativeEntity_setFloat0
  (JNIEnv *, jclass, jint, jint, jfloat, jfloat, jfloat, jfloat);

/*
 * Class:     q2java_NativeEntity
 * Method:    getByte
 * Signature: (II)B
 */
jbyte JNICALL Java_q2java_NativeEntity_getByte
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     q2java_NativeEntity
 * Method:    getShort
 * Signature: (II)S
 */
jshort JNICALL Java_q2java_NativeEntity_getShort
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     q2java_NativeEntity
 * Method:    sound0
 * Signature: (FFFIIIFFFI)V
 */
void JNICALL Java_q2java_NativeEntity_sound0
  (JNIEnv *, jclass, jfloat, jfloat, jfloat, jint, jint, jint, jfloat, jfloat, jfloat, jint);

/*
 * Class:     q2java_NativeEntity
 * Method:    boxEntity0
 * Signature: (II)[Lq2java/NativeEntity;
 */
jobjectArray JNICALL Java_q2java_NativeEntity_boxEntity0
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     q2java_NativeEntity
 * Method:    traceMove0
 * Signature: (IIF)Lq2java/TraceResults;
 */
jobject JNICALL Java_q2java_NativeEntity_traceMove0
  (JNIEnv *, jclass, jint, jint, jfloat);

/*
 * Class:     q2java_NativeEntity
 * Method:    cprint0
 * Signature: (IILjava/lang/String;)V
 */
void JNICALL Java_q2java_NativeEntity_cprint0
  (JNIEnv *, jclass, jint, jint, jstring);

/*
 * Class:     q2java_NativeEntity
 * Method:    getVec3
 * Signature: (II)Lq2java/Vec3;
 */
jobject JNICALL Java_q2java_NativeEntity_getVec3
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     q2java_NativeEntity
 * Method:    pMove0
 * Signature: (IBBSSSSSSBB)Lq2java/PMoveResults;
 */
jobject JNICALL Java_q2java_NativeEntity_pMove0
  (JNIEnv *, jclass, jint, jbyte, jbyte, jshort, jshort, jshort, jshort, jshort, jshort, jbyte, jbyte);

/*
 * Class:     q2java_NativeEntity
 * Method:    setByte
 * Signature: (IIB)V
 */
void JNICALL Java_q2java_NativeEntity_setByte
  (JNIEnv *, jclass, jint, jint, jbyte);

/*
 * Class:     q2java_NativeEntity
 * Method:    unlinkEntity0
 * Signature: (I)V
 */
void JNICALL Java_q2java_NativeEntity_unlinkEntity0
  (JNIEnv *, jclass, jint);

/*
 * Class:     q2java_NativeEntity
 * Method:    setInt
 * Signature: (III)V
 */
void JNICALL Java_q2java_NativeEntity_setInt
  (JNIEnv *, jclass, jint, jint, jint);

/*
 * Class:     q2java_NativeEntity
 * Method:    setStat0
 * Signature: (IIS)V
 */
void JNICALL Java_q2java_NativeEntity_setStat0
  (JNIEnv *, jclass, jint, jint, jshort);

/*
 * Class:     q2java_NativeEntity
 * Method:    freeEntity0
 * Signature: (I)V
 */
void JNICALL Java_q2java_NativeEntity_freeEntity0
  (JNIEnv *, jclass, jint);

/*
 * Class:     q2java_NativeEntity
 * Method:    setModel0
 * Signature: (ILjava/lang/String;)V
 */
void JNICALL Java_q2java_NativeEntity_setModel0
  (JNIEnv *, jclass, jint, jstring);

/*
 * Class:     q2java_NativeEntity
 * Method:    centerprint0
 * Signature: (ILjava/lang/String;)V
 */
void JNICALL Java_q2java_NativeEntity_centerprint0
  (JNIEnv *, jclass, jint, jstring);

#ifdef __cplusplus
}
#endif
#endif
