#include <windows.h>	// for DLL handling & GetModuleFileName() 
#include <stdio.h>		// for the debugLog() function
#include <stdarg.h>		// for the debugLog() function

#include "globals.h"

JNIEnv *java_env;
char *java_error;

static HINSTANCE hJavaDLL;
static JavaVM *java_vm;

static cvar_t *q2java_DLLHandle;
static cvar_t *q2java_VMPointer;
static cvar_t *q2java_EnvPointer;


// for making our new Java classpath
char java_gameDirName[1024];
static char classpath[1024];
static char debugFileName[1024];
static cvar_t *q2java_debugLog;

void debugLog(const char *msg, ...)
	{
	va_list ap;
	FILE *f;

	if (!q2java_debugLog->value)
		return;

	f = fopen(debugFileName, "a");

	va_start(ap, msg);
	vfprintf(f, msg, ap);
	va_end(ap);

	fclose(f);
	}

// Figure out what directory we're operating out of,
// so we can write a debug trace file and later
// setup a Java classpath
//
static void setupPaths()
	{	
	char *p;
	cvar_t *game_cvar = gi.cvar("game", "baseq2", 0);			

	// WINDOWS SPECIFIC: get the full pathname of the Quake2 .EXE file
    GetModuleFileName(0, java_gameDirName, 1024);

	// find the last backslash in the path
	p = strrchr(java_gameDirName, '\\');

	// p should never be null, but just in case....
	if (!p)
		p = java_gameDirName;
	else
		p++; // p is now just after the last backslash
	
	// append the name of the game
	strcpy(p, game_cvar->string);

	// save the name of our debugLog file
	sprintf(debugFileName, "%s\\q2java.log", java_gameDirName);

	// erase any existing debug file
	remove(debugFileName);
	}





void startJava()
    {
    JDK1_1InitArgs vm_args;
    jint (JNICALL *p_JNI_GetDefaultJavaVMInitArgs)(void *);
    jint (JNICALL *p_JNI_CreateJavaVM)(JavaVM **, JNIEnv **, void *);
	int alreadyStarted = 0;
	char buffer[64];
	cvar_t *q2java_security;

	q2java_debugLog = gi.cvar("q2java_debugLog", "0", 0);
	q2java_security = gi.cvar("q2java_security", "2", CVAR_NOSET);

	setupPaths();
	debugLog("in startJava()\n");

	java_error = NULL;

    q2java_DLLHandle = gi.cvar("q2java_DLLHandle", "0", CVAR_NOSET);
    q2java_VMPointer = gi.cvar("q2java_VMPointer", "0", CVAR_NOSET);
    q2java_EnvPointer = gi.cvar("q2java_EnvPointer", "0", CVAR_NOSET);

    if (q2java_DLLHandle->value)
        hJavaDLL = (HINSTANCE) atoi(q2java_DLLHandle->string);
    else
        {	
        hJavaDLL = LoadLibrary("javai.dll");
		if (!hJavaDLL)
			{
			java_error = "Can't find javai.dll - perhaps you haven't installed a compatible Java on this machine?\n";
			return;
			}

		// save the Java DLL handle in a CVAR so we can get it back if the DLL is reloaded
        sprintf(buffer, "%d", hJavaDLL);	
        gi.cvar_forceset("q2java_DLLHandle", buffer);
        }

	if (q2java_VMPointer->value)
        {
        java_vm = (void *) atoi(q2java_VMPointer->string);
        java_env = (void *) atoi(q2java_EnvPointer->string);
		alreadyStarted = 1;
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
		strcat(classpath, java_gameDirName);
		strcat(classpath, "\\q2java.jar;");
		strcat(classpath, java_gameDirName);
		strcat(classpath, "\\classes;");

		// set the new classpath
        vm_args.classpath = classpath;
	
        (*p_JNI_CreateJavaVM)(&java_vm, &java_env, &vm_args);
		if (!java_vm)
			{
			java_error = "Couldn't create a Java Virtual Machine for some reason\n";
			return;
			}

		// save some key pointers and handles in cvars, in case
		// the DLL is reloaded and we need them back
        sprintf(buffer, "%d", java_vm);	
        gi.cvar_forceset("q2java_VMPointer", buffer);
        sprintf(buffer, "%d", java_env);
        gi.cvar_forceset("q2java_EnvPointer", buffer);
        }


	// if Java's up and running, then get everything hooked up
    if (java_env)
		{
		// initialize the "Misc" module first, so the other ones 
		// can check for exceptions
		if (!java_error)
			Misc_javaInit();  

		if (!java_error)
			Engine_javaInit();

		if (!java_error)
			Game_javaInit();

		if (!java_error)
			CVar_javaInit();

		if (!java_error)
			Player_javaInit();

		if (!java_error)
			Entity_javaInit();

		if (!java_error && !alreadyStarted && q2java_security->value)
			enableSecurity((int)q2java_security->value);	
		}
	return;
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
        gi.cvar_forceset("q2java_DLLHandle", "0");
        gi.cvar_forceset("q2java_VMPointer", "0");
        gi.cvar_forceset("q2java_EnvPointer", "0");
        hJavaDLL = 0;
        java_vm = 0;
        java_env = 0;
        debugLog("It's a miracle, the JDK is fixed! VM Destroyed and Java DLL freed\n");
        }
    }
