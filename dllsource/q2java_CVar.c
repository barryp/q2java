#include "globals.h"
#include "q2java_CVar.h"

#define CALL_CVAR  0
#define CALL_CVAR_SET 1
#define CALL_CVAR_FORCESET 2

// handle to the CVar class
static jclass class_CVar;

static JNINativeMethod CVar_methods[] = 
    {
    {"cvar0",       "(Ljava/lang/String;Ljava/lang/String;II)I",    Java_q2java_CVar_cvar0},
    {"getFloat0",   "(I)F",                                         Java_q2java_CVar_getFloat0},
    {"getString0",  "(I)Ljava/lang/String;",                        Java_q2java_CVar_getString0}
    };

void CVar_javaInit()
    {
    class_CVar = (*java_env)->FindClass(java_env, "q2java/CVar");
    if (CHECK_EXCEPTION() || !class_CVar)
        {
        java_error = "Couldn't find q2java.CVar\n";
        return;
        }

    (*java_env)->RegisterNatives(java_env, class_CVar, CVar_methods, sizeof(CVar_methods)/sizeof(CVar_methods[0]));
    if (CHECK_EXCEPTION())
        {
        java_error = "Couldn't register native methods for q2java.CVar\n";
        return;
        }
    }

void CVar_javaDetach()
    {
    if (class_CVar)
        (*java_env)->UnregisterNatives(java_env, class_CVar);

    (*java_env)->DeleteLocalRef(java_env, class_CVar);
    }

static jint JNICALL Java_q2java_CVar_cvar0(JNIEnv *env , jclass cls, jstring jname, jstring jval, jint flags, jint calltype)
    {
    char *name;
    char *val;
    cvar_t *result;

    name = (char *)((*env)->GetStringUTFChars(env, jname, 0));
    val = (char *)((*env)->GetStringUTFChars(env, jval, 0));
    switch (calltype)
        {
        case CALL_CVAR: result = q2java_gi.cvar(name, val, flags); break;
        case CALL_CVAR_SET: result = q2java_gi.cvar_set(name, val); break;
        case CALL_CVAR_FORCESET: result = q2java_gi.cvar_forceset(name, val); break;
        default: result = 0;
        }
    (*env)->ReleaseStringUTFChars(env, jname, name);
    (*env)->ReleaseStringUTFChars(env, jval, val);
    return (jint) result;
    }

static jstring JNICALL Java_q2java_CVar_getString0(JNIEnv *env, jclass cls, jint ptr)
    {
    cvar_t *cv = (cvar_t *)ptr;
    return (*env)->NewStringUTF(env, cv->string);
    }

static jfloat JNICALL Java_q2java_CVar_getFloat0(JNIEnv *env, jclass cls, jint ptr)
    {
    cvar_t *cv = (cvar_t *)ptr;
    return cv->value;
    }
