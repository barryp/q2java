#include <windows.h>	// for DLL handling
#include "globals.h"
#include "javalink.h"



static cvar_t *java_DLLHandle_cvar;
static cvar_t *java_VMPointer_cvar;
static cvar_t *java_EnvPointer_cvar;

static HINSTANCE hJavaDLL;
static JavaVM *java_vm;
JNIEnv *java_env;

// handle to the java.lang.Class class
static jclass class_Class;
static jmethodID method_Class_getName;

// handle for the (default?) Classloader
static jobject object_ClassLoader;

// handle to java.lang.Throwable class
static jclass class_Throwable;
static jmethodID method_Throwable_getMessage;

// handle to the Vec class
static jclass class_Vec3;
static jmethodID method_Vec3_ctor;

// for making our new Java classpath
static char classpath[1024];


void setSecurity()
	{
	jclass class_System;
	jmethodID method_System_setSecurityManager;

	jclass class_Q2JavaSecurityManager;
	jmethodID method_Q2JavaSecurityManager_ctor;
	jobject object_security_manager;

	jstring jsGameDir;

debugLog("In setSecurity()\n");

	class_Q2JavaSecurityManager = (*java_env)->FindClass(java_env, "q2java/Q2JavaSecurityManager");
	CHECK_EXCEPTION();
	if (!class_Q2JavaSecurityManager)
		{
		gi.dprintf("Can't get a handle on Q2JavaSecurityManager\n");
		return;
		}

	method_Q2JavaSecurityManager_ctor = (*java_env)->GetMethodID(java_env, class_Q2JavaSecurityManager, "<init>", "(Ljava/lang/String;)V");
	CHECK_EXCEPTION();
	if (!method_Q2JavaSecurityManager_ctor)
		{
		gi.dprintf("Can't get a handle on Q2JavaSecurityManager constructor\n");
		return;
		}

	jsGameDir = (*java_env)->NewStringUTF(java_env, global_gameDirName);
	debugLog("About to construct new SecurityManager\n");
	
	object_security_manager = (*java_env)->NewObject(java_env, class_Q2JavaSecurityManager, method_Q2JavaSecurityManager_ctor, jsGameDir);
	CHECK_EXCEPTION();
	if (!object_security_manager)
		{
		gi.dprintf("Couldn't create new security manager\n");
		return;
		}

	debugLog("New SecurityManager constructed\n");

	class_System = (*java_env)->FindClass(java_env, "java/lang/System");
	CHECK_EXCEPTION();
	if (!class_System)
		{
		gi.dprintf("Can't get a handle on java.lang.System\n");
		return;
		}

	debugLog("System class found\n");

	method_System_setSecurityManager = (*java_env)->GetStaticMethodID(java_env, class_System, "setSecurityManager", "(Ljava/lang/SecurityManager;)V");
	CHECK_EXCEPTION();
	if (!method_System_setSecurityManager)
		{
		gi.dprintf("Can't get a handle on System.setSecurityManager()\n");
		return;
		}

	debugLog("setSecurityManagerMethod found\n");

	(*java_env)->CallStaticVoidMethod(java_env, class_System, method_System_setSecurityManager, object_security_manager);
	CHECK_EXCEPTION();

	debugLog("setSecurity() finished\n");
	}


static void initClass()
	{
	jmethodID method_Class_getClassLoader;

	class_Class = (*java_env)->FindClass(java_env, "java/lang/Class");
	CHECK_EXCEPTION();
	if (!class_Class)
		{			
		debugLog("Couldn't get Java java.lang.Class class\n");
		return;
		}

	method_Class_getName = (*java_env)->GetMethodID(java_env, class_Class, "getName", "()Ljava/lang/String;");		
	CHECK_EXCEPTION();
	if (!method_Class_getName)
		debugLog("Couldn't get Class.getName() method handle\n");

	method_Class_getClassLoader = (*java_env)->GetMethodID(java_env, class_Class, "getClassLoader", "()Ljava/lang/ClassLoader;");
	CHECK_EXCEPTION();
	if (!method_Class_getClassLoader)
		debugLog("Couldn't get Class.getClassLoader() method handle\n");

	object_ClassLoader = (*java_env)->CallObjectMethod(java_env, class_Class, method_Class_getClassLoader);
	CHECK_EXCEPTION();
	if (!object_ClassLoader)
		debugLog("Couldn't get the default classloader\n");
	
	debugLog("Class initialization finished\n");
	}


static void initThrowable()
	{
	class_Throwable = (*java_env)->FindClass(java_env, "java/lang/Throwable");
	if (!class_Throwable)
		{			
		debugLog("Couldn't get Java java.lang.Throwable class\n");
		return;
		}

	method_Throwable_getMessage = (*java_env)->GetMethodID(java_env, class_Throwable, "getMessage", "()Ljava/lang/String;");		
	if (!method_Throwable_getMessage)
		debugLog("Couldn't get Throwable.getMessage() method handle\n");

	}

static void initVec3()
	{
	class_Vec3 = (*java_env)->FindClass(java_env, "q2java/Vec3");
//	class_Vec3 = (*java_env)->DefineClass(java_env, "Vec3", object_ClassLoader, Vec3Class, sizeof(Vec3Class));
	if (!class_Vec3)
		debugLog("Couldn't get Java Vec3 class\n");
	else
		method_Vec3_ctor = (*java_env)->GetMethodID(java_env, class_Vec3, "<init>", "(FFF)V");		
	}



int startJava()
    {
    JDK1_1InitArgs vm_args;
    jint (JNICALL *p_JNI_GetDefaultJavaVMInitArgs)(void *);
    jint (JNICALL *p_JNI_CreateJavaVM)(JavaVM **, JNIEnv **, void *);
	int alreadyStarted = 0;
	char buffer[64];

	debugLog("in startJava()\n");

    java_DLLHandle_cvar = gi.cvar("java_DLLHandle", "0", 0);
    java_VMPointer_cvar = gi.cvar("java_VMPointer", "0", 0);
    java_EnvPointer_cvar = gi.cvar("java_EnvPointer", "0", 0);

    if (java_DLLHandle_cvar->value)
        {
        hJavaDLL = (HINSTANCE) atoi(java_DLLHandle_cvar->string);
        debugLog("Using Pure Java DLL Handle found in cvar\n");
        }
    else
        {	
        hJavaDLL = LoadLibrary("javai.dll");
        sprintf(buffer, "%d", hJavaDLL);	
        gi.cvar_set("java_DLLHandle", buffer);
        debugLog("Loaded the Java DLL\n");
        }

	if (java_VMPointer_cvar->value)
        {
        java_vm = (void *) atoi(java_VMPointer_cvar->string);
        java_env = (void *) atoi(java_EnvPointer_cvar->string);
		alreadyStarted = 1;
        debugLog("Using Java VM and ENV Pointers found in cvar\n");
        }
    else
        {	
        p_JNI_GetDefaultJavaVMInitArgs = GetProcAddress(hJavaDLL, "JNI_GetDefaultJavaVMInitArgs");
        p_JNI_CreateJavaVM = GetProcAddress(hJavaDLL, "JNI_CreateJavaVM");

        vm_args.version = 0x00010001;
        (*p_JNI_GetDefaultJavaVMInitArgs)(&vm_args);

        // make a new classpath with the directory of
        // this game appended
        //
		strcpy(classpath, vm_args.classpath);
		strcat(classpath, ";");
		strcat(classpath, global_gameDirName);
		strcat(classpath, "\\classes");

		// set the new classpath
        vm_args.classpath = classpath;
	
        debugLog("Java Classpath: [%s]\n", vm_args.classpath);

        (*p_JNI_CreateJavaVM)(&java_vm, &java_env, &vm_args);
        debugLog("Created a new Java VM\n");

		// save some key pointers and handles in cvars, in case
		// the DLL is reloaded and we need them back
        sprintf(buffer, "%d", java_vm);	
        gi.cvar_set("java_VMPointer", buffer);
        sprintf(buffer, "%d", java_env);
        gi.cvar_set("java_EnvPointer", buffer);
        }


	// if Java's up and running, then get everything hooked up
    if (java_env)
		{
		initClass();
		initThrowable();
		initVec3();

		Engine_javaInit();
		Game_javaInit();
		CVar_javaInit();
		Player_javaInit();
		Entity_javaInit();
/*
		if (!alreadyStarted)
			setSecurity();	
*/
		}
	return 1;
    }



void stopJava()
    {
debugLog("in stopJava()\n");
    if (!java_vm)
		{
        debugLog("Can't destroy Java VM, pointer was null\n");
		return;
		}

	Engine_javaFinalize();
	Game_javaFinalize();
	Entity_javaFinalize();
	Player_javaFinalize();
	CVar_javaFinalize();

	if ((*java_vm)->DestroyJavaVM(java_vm))
		debugLog("Error destroying Java VM, stupid Sun JDK is probably still broke\n");
    else
        {			
        FreeLibrary(hJavaDLL);
        gi.cvar_set("java_DLLHandle", "0");
        gi.cvar_set("java_VMPointer", "0");
        gi.cvar_set("java_EnvPointer", "0");
        hJavaDLL = 0;
        java_vm = 0;
        java_env = 0;
        debugLog("It's a miracle, the JDK is fixed! VM Destroyed and Java DLL freed\n");
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


/*******   Helper functions ***********/



jobject newJavaVec3(vec3_t *v)
	{
	if (!v)
		return 0;
	else
		return (*java_env)->NewObject(java_env, class_Vec3, method_Vec3_ctor, (*v)[0], (*v)[1], (*v)[2]);
	}

