#include "globals.h"

// handles to the java.lang.Class class
static jclass class_Class;
static jmethodID method_Class_getName;

// handles to java.lang.Throwable class
static jclass class_Throwable;
static jmethodID method_Throwable_getMessage;

// handles to the q2java.Vec3 class
static jclass class_Vec3;
static jmethodID method_Vec3_ctor;

// handles to the q2java.PMoveResults class
static jclass class_PMoveResults;
static jmethodID method_PMoveResults_ctor;

// handles to q2java.TraceResults class
static jclass class_TraceResults;
static jmethodID method_TraceResults_ctor;


void Misc_javaInit()
	{
	class_Class = (*java_env)->FindClass(java_env, "java/lang/Class");
	if (!class_Class)
		{			
		java_error = "Couldn't find java.lang.Class\n";
		return;
		}

	method_Class_getName = (*java_env)->GetMethodID(java_env, class_Class, "getName", "()Ljava/lang/String;");		
	if (!method_Class_getName)
		{
		java_error = "Couldn't find java.lang.Class.getName() method\n";
		return;
		}

	class_Throwable = (*java_env)->FindClass(java_env, "java/lang/Throwable");
	if (!class_Throwable)
		{			
		java_error = "Couldn't find java.lang.Throwable\n";
		return;
		}

	method_Throwable_getMessage = (*java_env)->GetMethodID(java_env, class_Throwable, "getMessage", "()Ljava/lang/String;");		
	if (!method_Throwable_getMessage)
		{
		java_error = "Couldn't find java.lang.Throwable.getMessage() method\n";
		return;
		}

	// now that the java.lang.Class and java.lang.Throwable handles are obtained
	// we can start checking for exceptions

	class_Vec3 = (*java_env)->FindClass(java_env, "q2java/Vec3");
//	class_Vec3 = (*java_env)->DefineClass(java_env, "Vec3", object_ClassLoader, Vec3Class, sizeof(Vec3Class));
	if (CHECK_EXCEPTION() || !class_Vec3)
		{
		java_error = "Couldn't find q2java.Vec3\n";
		return;
		}

	method_Vec3_ctor = (*java_env)->GetMethodID(java_env, class_Vec3, "<init>", "(FFF)V");		
	if (CHECK_EXCEPTION() || !method_Vec3_ctor)
		{
		java_error = "Couldn't find q2java.Vec3 constructor method\n";
		return;
		}

	class_PMoveResults = (*java_env)->FindClass(java_env, "q2java/PMoveResults");
	if (CHECK_EXCEPTION() || !class_PMoveResults)
		{
		java_error = "Couldn't find q2java.PMoveResults\n";
		return;
		}

	method_PMoveResults_ctor = (*java_env)->GetMethodID(java_env, class_PMoveResults, "<init>", "(BSSSSSSB[Lq2java/NativeEntity;FLq2java/NativeEntity;II)V");
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

	method_TraceResults_ctor = (*java_env)->GetMethodID(java_env, class_TraceResults, "<init>", "(ZZFLq2java/Vec3;Lq2java/Vec3;FBBLjava/lang/String;IIILq2java/NativeEntity;)V");
	if (CHECK_EXCEPTION() || !method_TraceResults_ctor)
		{
		java_error = "Couldn't find q2java.TraceResults constructor\n";
		return;
		}
	}


int checkException(char *filename, int linenum)
	{
	jclass exClass;
	jthrowable ex;
	jstring jsName;
	jstring jsMsg;
	char *name;
	char *msg;

	ex = (*java_env)->ExceptionOccurred(java_env);
	if (!ex)
		return false;

	(*java_env)->ExceptionClear(java_env);

	exClass = (*java_env)->GetObjectClass(java_env, ex);
	jsName = (*java_env)->CallObjectMethod(java_env, exClass, method_Class_getName);
	if (jsName)
		name = (char *)((*java_env)->GetStringUTFChars(java_env, jsName, 0));
	else
		name = "(Unknown Java exception)";

	jsMsg = (*java_env)->CallObjectMethod(java_env, ex, method_Throwable_getMessage);
	if (jsMsg)
		{
		msg = (char *)((*java_env)->GetStringUTFChars(java_env, jsMsg, 0));
		debugLog("(%s:%d) %s: %s\n", filename, linenum, name, msg); 
		gi.dprintf("%s: %s\n", name, msg);
		(*java_env)->ReleaseStringUTFChars(java_env, jsMsg, msg);	
		}
	else
		{
		debugLog("(%s:%d)%s\n", filename, linenum, name); 
		gi.dprintf("%s\n", name);
		}

	if (jsName)
		(*java_env)->ReleaseStringUTFChars(java_env, jsName, name);	
	
	return true;
	}


void enableSecurity(int level)
	{
	jclass class_System;
	jmethodID method_System_setSecurityManager;

	jclass class_Q2JavaSecurityManager;
	jmethodID method_Q2JavaSecurityManager_ctor;
	jobject object_security_manager;

	jstring jsGameDir;

debugLog("In enableSecurity()\n");

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

	jsGameDir = (*java_env)->NewStringUTF(java_env, java_gameDirName);

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

	debugLog("setSecurity() finished ok\n");
	}



jobject newJavaVec3(vec3_t *v)
	{
	if (!v)
		return 0;
	else
		return (*java_env)->NewObject(java_env, class_Vec3, method_Vec3_ctor, (*v)[0], (*v)[1], (*v)[2]);
	}


jobject newPMoveResults(pmove_t pm)
	{
	jobjectArray touched;
	jobject groundEnt;

	touched = Entity_createArray(pm.touchents, pm.numtouch);

	if (!pm.groundentity)
		groundEnt = 0;
	else
		groundEnt = Entity_getEntity(pm.groundentity - ge.edicts);

	return (*java_env)->NewObject(java_env, class_PMoveResults, method_PMoveResults_ctor,
		pm.cmd.buttons, pm.cmd.angles[0], pm.cmd.angles[1], pm.cmd.angles[2],
		pm.cmd.forwardmove, pm.cmd.sidemove, pm.cmd.upmove, pm.cmd.lightlevel,
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

	resEndpos = newJavaVec3(&(result.endpos));
	resPlaneNormal = newJavaVec3(&(result.plane.normal));

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

	resEnt = Entity_getEntity(result.ent - ge.edicts);

	return (*java_env)->NewObject(java_env, class_TraceResults, method_TraceResults_ctor, 
		result.allsolid, result.startsolid, result.fraction, resEndpos, resPlaneNormal, 
		result.plane.dist, result.plane.type, result.plane.signbits,
		resSurfaceName, resSurfaceFlags, resSurfaceValue, result.contents,
		resEnt);
	}
