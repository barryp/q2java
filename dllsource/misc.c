/* CHANGES

1998-04-15 (BP)
    Commented out access to the Tuple3f class (turns out to be abstract)
   

*/

#include "globals.h"

// Tuple types
#define TYPE_TUPLE  0
#define TYPE_POINT  1
#define TYPE_VECTOR 2
#define TYPE_ANGLE  3


// handles to java.lang.Throwable class
static jclass class_Throwable;
static jmethodID method_Throwable_printStackTrace;

// handles to the javax.vecmath.Tuple3f class
static jclass class_Tuple3f;
static jmethodID method_Tuple3f_ctor;

// handles to the javax.vecmath.Point3f class
static jclass class_Point3f;
static jmethodID method_Point3f_ctor;

// handles to the javax.vecmath.Vector3f class
static jclass class_Vector3f;
static jmethodID method_Vector3f_ctor;

// handles to the q2java.Angle3f class
static jclass class_Angle3f;
static jmethodID method_Angle3f_ctor;

// handles to the q2java.PMoveResults class
static jclass class_PMoveResults;
static jmethodID method_PMoveResults_ctor;

// handles to q2java.TraceResults class
static jclass class_TraceResults;
static jmethodID method_TraceResults_ctor;

// handles to q2java.PlayerCmd class
static jclass class_PlayerCmd;
static jmethodID method_PlayerCmd_set;
jobject playerCmd;

void Misc_javaInit()
    {
    jmethodID method_PlayerCmd_ctor;

    javalink_debug("Misc_javaInit() starting\n");

    class_Throwable = (*java_env)->FindClass(java_env, "java/lang/Throwable");
    if (!class_Throwable)
        {           
        java_error = "Couldn't find java.lang.Throwable\n";
        return;
        }

    method_Throwable_printStackTrace = (*java_env)->GetMethodID(java_env, class_Throwable, "printStackTrace", "()V");       
    if (!method_Throwable_printStackTrace)
        {
        java_error = "Couldn't find java.lang.Throwable.printStackTrace() method\n";
        return;
        }

    // now that the java.lang.Class and java.lang.Throwable handles are obtained
    // we can start checking for exceptions


    class_Tuple3f = (*java_env)->FindClass(java_env, "javax/vecmath/Tuple3f");
    if (CHECK_EXCEPTION() || !class_Tuple3f)
        {
        java_error = "Couldn't find javax.vecmath.Tuple3f\n";
        return;
        }

    method_Tuple3f_ctor = (*java_env)->GetMethodID(java_env, class_Tuple3f, "<init>", "(FFF)V");        
    if (CHECK_EXCEPTION() || !method_Tuple3f_ctor)
        {
        java_error = "Couldn't find javax.vecmath.Tuple3f constructor method\n";
        return;
        }

    class_Point3f = (*java_env)->FindClass(java_env, "javax/vecmath/Point3f");
    if (CHECK_EXCEPTION() || !class_Point3f)
        {
        java_error = "Couldn't find javax.vecmath.Point3f\n";
        return;
        }

    method_Point3f_ctor = (*java_env)->GetMethodID(java_env, class_Point3f, "<init>", "(FFF)V");        
    if (CHECK_EXCEPTION() || !method_Point3f_ctor)
        {
        java_error = "Couldn't find javax.vecmath.Point3f constructor method\n";
        return;
        }

    class_Vector3f = (*java_env)->FindClass(java_env, "javax/vecmath/Vector3f");
    if (CHECK_EXCEPTION() || !class_Vector3f)
        {
        java_error = "Couldn't find javax.vecmath.Vector3f\n";
        return;
        }

    method_Vector3f_ctor = (*java_env)->GetMethodID(java_env, class_Vector3f, "<init>", "(FFF)V");      
    if (CHECK_EXCEPTION() || !method_Vector3f_ctor)
        {
        java_error = "Couldn't find javax.vecmath.Vector3f constructor method\n";
        return;
        }

    class_Angle3f = (*java_env)->FindClass(java_env, "q2java/Angle3f");
    if (CHECK_EXCEPTION() || !class_Angle3f)
        {
        java_error = "Couldn't find q2java.Angle3f\n";
        return;
        }

    method_Angle3f_ctor = (*java_env)->GetMethodID(java_env, class_Angle3f, "<init>", "(FFF)V");        
    if (CHECK_EXCEPTION() || !method_Angle3f_ctor)
        {
        java_error = "Couldn't find q2java.Angle3f constructor method\n";
        return;
        }

    class_PMoveResults = (*java_env)->FindClass(java_env, "q2java/PMoveResults");
    if (CHECK_EXCEPTION() || !class_PMoveResults)
        {
        java_error = "Couldn't find q2java.PMoveResults\n";
        return;
        }

    method_PMoveResults_ctor = (*java_env)->GetMethodID(java_env, class_PMoveResults, "<init>", "([Lq2java/NativeEntity;FLq2java/NativeEntity;II)V");
    if (CHECK_EXCEPTION() || !method_PMoveResults_ctor)
        {
        java_error = "Couldn't find q2java.PMoveResults constructor\n";
        return;
        }

    class_TraceResults = (*java_env)->FindClass(java_env, "q2java/TraceResults");
    if (CHECK_EXCEPTION() || !class_TraceResults)
        {
        java_error = "Couldn't find q2java.TraceResults\n";
        return;
        }

    method_TraceResults_ctor = (*java_env)->GetMethodID(java_env, class_TraceResults, "<init>", "(ZZFLjavax/vecmath/Point3f;Ljavax/vecmath/Vector3f;FBBLjava/lang/String;IIILq2java/NativeEntity;)V");
    if (CHECK_EXCEPTION() || !method_TraceResults_ctor)
        {
        java_error = "Couldn't find q2java.TraceResults constructor\n";
        return;
        }

    class_PlayerCmd = (*java_env)->FindClass(java_env, "q2java/PlayerCmd");
    if (CHECK_EXCEPTION() || !class_PlayerCmd)
        {
        java_error = "Couldn't find q2java.PlayerCmd\n";
        return;
        }

    method_PlayerCmd_ctor = (*java_env)->GetMethodID(java_env, class_PlayerCmd, "<init>", "()V");
    if (CHECK_EXCEPTION() || !method_PlayerCmd_ctor)
        {
        java_error = "Couldn't find q2java.PlayerCmd constructor\n";
        return;
        }

    method_PlayerCmd_set = (*java_env)->GetMethodID(java_env, class_PlayerCmd, "set", "(BBSSSSSSBB)V");
    if (CHECK_EXCEPTION() || !method_PlayerCmd_set)
        {
        java_error = "Couldn't find q2java.PlayerCmd set()\n";
        return;
        }

    playerCmd = (*java_env)->NewObject(java_env, class_PlayerCmd, method_PlayerCmd_ctor);
    if (CHECK_EXCEPTION() || !playerCmd)
        {
        java_error = "Couldn't create instance of q2java.PlayerCmd\n";
        return;
        }

    javalink_debug("Misc_javaInit() finished\n");
    }


// drop our local references
void Misc_javaDetach()
    {
    (*java_env)->DeleteLocalRef(java_env, class_Throwable);
    (*java_env)->DeleteLocalRef(java_env, class_Tuple3f);
    (*java_env)->DeleteLocalRef(java_env, class_Point3f);
    (*java_env)->DeleteLocalRef(java_env, class_Vector3f);
    (*java_env)->DeleteLocalRef(java_env, class_Angle3f);
    (*java_env)->DeleteLocalRef(java_env, class_PMoveResults);
    (*java_env)->DeleteLocalRef(java_env, class_TraceResults);
    (*java_env)->DeleteLocalRef(java_env, class_PlayerCmd);
    (*java_env)->DeleteLocalRef(java_env, playerCmd);
    }


int checkException(char *filename, int linenum)
    {
    jthrowable ex;

    ex = (*java_env)->ExceptionOccurred(java_env);
    if (!ex)
        return 0;

    (*java_env)->ExceptionClear(java_env);
    (*java_env)->CallVoidMethod(java_env, ex, method_Throwable_printStackTrace);
    return 1;
    }


void enableSecurity(int level)
    {
    jclass class_System;
    jmethodID method_System_setSecurityManager;

    jclass class_Q2JavaSecurityManager;
    jmethodID method_Q2JavaSecurityManager_ctor;
    jobject object_security_manager;

    jstring jsGameDir;


    class_Q2JavaSecurityManager = (*java_env)->FindClass(java_env, "q2java/Q2JavaSecurityManager");
    if (CHECK_EXCEPTION() || !class_Q2JavaSecurityManager)
        {
        java_error = "Can't get a handle on Q2JavaSecurityManager\n";
        return;
        }

    method_Q2JavaSecurityManager_ctor = (*java_env)->GetMethodID(java_env, class_Q2JavaSecurityManager, "<init>", "(ILjava/lang/String;)V");
    if (CHECK_EXCEPTION() || !method_Q2JavaSecurityManager_ctor)
        {
        java_error = "Can't get a handle on Q2JavaSecurityManager constructor\n";
        return;
        }

    jsGameDir = (*java_env)->NewStringUTF(java_env, javalink_gameDirName);

    object_security_manager = (*java_env)->NewObject(java_env, class_Q2JavaSecurityManager, method_Q2JavaSecurityManager_ctor, level, jsGameDir);
    if (CHECK_EXCEPTION() || !object_security_manager)
        {
        java_error = "Couldn't create new security manager\n";
        return;
        }

    class_System = (*java_env)->FindClass(java_env, "java/lang/System");
    if (CHECK_EXCEPTION() || !class_System)
        {
        java_error = "Can't get a handle on java.lang.System\n";
        return;
        }

    method_System_setSecurityManager = (*java_env)->GetStaticMethodID(java_env, class_System, "setSecurityManager", "(Ljava/lang/SecurityManager;)V");
    if (CHECK_EXCEPTION() || !method_System_setSecurityManager)
        {
        java_error = "Can't get a handle on System.setSecurityManager()\n";
        return;
        }

    (*java_env)->CallStaticVoidMethod(java_env, class_System, method_System_setSecurityManager, object_security_manager);
    if (CHECK_EXCEPTION())
        {
        java_error = "System.setSecurityManager() failed\n";
        return;
        }

    (*java_env)->DeleteLocalRef(java_env, class_System);
    (*java_env)->DeleteLocalRef(java_env, class_Q2JavaSecurityManager);
    (*java_env)->DeleteLocalRef(java_env, object_security_manager);
    (*java_env)->DeleteLocalRef(java_env, jsGameDir);

    javalink_debug("setSecurity() finished ok\n");
    }



jobject newJavaVec3(vec3_t *v, int vecType)
    {
    if (!v)
        return 0;

    switch (vecType)
        {
        case TYPE_ANGLE:
            return (*java_env)->NewObject(java_env, class_Angle3f, method_Angle3f_ctor, (*v)[0], (*v)[1], (*v)[2]);

        case TYPE_POINT:
            return (*java_env)->NewObject(java_env, class_Point3f, method_Point3f_ctor, (*v)[0], (*v)[1], (*v)[2]);

        case TYPE_VECTOR:
            return (*java_env)->NewObject(java_env, class_Vector3f, method_Vector3f_ctor, (*v)[0], (*v)[1], (*v)[2]);

        default:
            return (*java_env)->NewObject(java_env, class_Tuple3f, method_Tuple3f_ctor, (*v)[0], (*v)[1], (*v)[2]);
//            return 0;
        }
    }


jobject newPMoveResults(pmove_t pm)
    {
    jobjectArray touched;
    jobject groundEnt;

    touched = Entity_createArray(pm.touchents, pm.numtouch);

    if (!pm.groundentity)
        groundEnt = 0;
    else
        groundEnt = Entity_getEntity(pm.groundentity - q2java_ge.edicts);

    return (*java_env)->NewObject(java_env, class_PMoveResults, method_PMoveResults_ctor,
        touched, pm.viewheight, groundEnt, pm.watertype, pm.waterlevel);
    }

jobject newTraceResults(trace_t result)
    {
    jobject resEndpos;
    jobject resPlaneNormal;
    jstring resSurfaceName;
    int resSurfaceFlags;
    int resSurfaceValue;
    jobject resEnt;

    resEndpos = newJavaVec3(&(result.endpos), TYPE_POINT);
    resPlaneNormal = newJavaVec3(&(result.plane.normal), TYPE_VECTOR);

    if (!result.surface)
        {
        resSurfaceName = 0;
        resSurfaceFlags = resSurfaceValue = 0;
        }
    else
        {
        resSurfaceName = (*java_env)->NewStringUTF(java_env, result.surface->name);
        resSurfaceFlags = result.surface->flags;
        resSurfaceValue = result.surface->value;
        }

    resEnt = Entity_getEntity(result.ent - q2java_ge.edicts);

    return (*java_env)->NewObject(java_env, class_TraceResults, method_TraceResults_ctor, 
        result.allsolid, result.startsolid, result.fraction, resEndpos, resPlaneNormal, 
        result.plane.dist, result.plane.type, result.plane.signbits,
        resSurfaceName, resSurfaceFlags, resSurfaceValue, result.contents,
        resEnt);
    }

void setPlayerCmd(jbyte msec, jbyte buttons, 
    short angle0, short angle1, short angle2,
    short forward, short side, short up,
    jbyte impulse, jbyte lightlevel)
    {
    (*java_env)->CallVoidMethod(java_env, playerCmd, method_PlayerCmd_set,
        msec, buttons, angle0, angle1, angle2, forward, side, up, impulse, lightlevel);
    }


// table for translating accented latin-1 characters to closest non-accented chars
// Icelandic thorn and eth are difficult, so I just used an asterisk
// German sz ligature ß is also difficult, chose to just put in 's'
// AE ligatures are just replaced with *
// perhaps some sort of multi-character substitution scheme would be helpful
//
// this translation table starts at decimal 192 (capital A, grave accent: À)
static char *translateTable = "AAAAAA*CEEEEIIIIDNOOOOOxOUUUUY*saaaaaa*ceeeeiiii*nooooo/ouuuuy*y";


// convert a Java string (which is Unicode) to reasonable
// 7-bit ASCII
// Be sure to q2java_gi.TagFree() the result when finished.
char *convertJavaString(jstring jstr)
    {
    jsize jStrLen;
    const jchar *unicodeChars;
    char *result;
    int i;
    char *p;
    
    jStrLen = (*java_env)->GetStringLength(java_env, jstr);
    p = result = q2java_gi.TagMalloc(jStrLen + 1, TAG_GAME);
    unicodeChars = (*java_env)->GetStringChars(java_env, jstr, NULL);

    for (i = 0; i < jStrLen; i++)
        {
        jchar ch = unicodeChars[i];
        if (ch < 192)
            *p++ = (char) ch;
        else
            {
            if (ch < 256)
                *p++ = translateTable[ch - 192];
            else
                *p++ = '*';
            }

        }

    (*java_env)->ReleaseStringChars(java_env, jstr, unicodeChars);
    *p = 0;
    return result;
    }