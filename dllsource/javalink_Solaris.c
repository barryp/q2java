//
// javalink_Solaris.c
//
// PORTING NOTES FOR SOLARIS
//
// See javalink_win32.c for details on general porting issues.
// This documentation tracks what was done and what still
// needs doing for the Solaris port.
//
// Work done:
// Fixed all WINDOWS-SPECIFIC dependencies. These include:
//  - Removed all references to hJavaDLL. It is unnecessary
//    to explicitly load the shared library (gamesolaris.so)
//    in the code.
//    Q for Barry: Is this really necessary for Windows?
//    Or was there another reason you needed to do this?
//    Just want to make sure removing this code is okay.
//  - Changed all path separator references from \\ to /
//  - Changed all CLASPATH separators from ; to :
//  - Slight modification to how java_gameDirName is built
//  - Used vsprintf instead of _vsnprintf
//  - Changed *p_JNI_<whatever> references to JNI_<whatever>
//    Q for Barry: Where does this *p_JNI come from? Is that
//    something specific to Windows? Looks like a deviation
//    from the example in the Javasoft JNI tutorial.
//
// Work Remaining:
// There are a whole bunch of warnings that I made no attempt to
// clean up. I wanted to get the Solaris port working with a
// minimum number of changes to the C source. So far, only this
// file and the Makefile were added to the source base. No other
// changes were necessary. See the accomanying warnings.txt for
// details.
// 
// We should attempt to get our ports 'unified' so that we have
// a single Makefile for all Unix platforms (and maybe for Windows
// too?).
//
// I did not test this intensively. I got the server running with
// +set game q2java. I connected to the server and verified that
// the q2java banner displayed. That is the extent of my testing.
//


#include <stdio.h>   // needed for the debugLog() function
#include <stdarg.h>  // needed for the debugLog() function

#include "globals.h" // mainly so we can call other Q2Java module's
                     // initialization and cleanup functions

char *javalink_version = "Win32 Version";

JNIEnv *java_env;    // Pointer to Java environment - used by other modules.
char *java_error;    // used by other modules to indicate initialization errors

/* static HINSTANCE hJavaDLL; // WINDOWS-SPECIFIC: handle to javai.dll */
static JavaVM *java_vm;    // Pointer to VM, used only by this module


// The next 3 cvar_t declarations are needed in Win32, because
// the Java VM under Win32 can't be shutdown, and we need
// to stash away a few key pointers where they can survive
// when the DLL is unloaded and reloaded.
//
static cvar_t *q2java_DLLHandle;
static cvar_t *q2java_VMPointer;
static cvar_t *q2java_EnvPointer;

static char initialSecurity[256]; // place to save initial security settings

char java_gameDirName[1024];      // name of Q2 game directory, something
                                  // like "c:\quake2\q2java"

static char classpath[1024];      // VM classpath
static char debugFileName[1024];  // name of file debugLog is written to
static cvar_t *q2java_debugLog;   // cvar indicating whether debugLog is on/off



//
// Write a message to the debugLog, if it's enabled
//
void debugLog(const char *msg, ...)
    {
    va_list ap;
    FILE *f;

    if (!q2java_debugLog->value)
        return; // debugLog is not enabled - so bail.

    f = fopen(debugFileName, "a");

    va_start(ap, msg);
    vfprintf(f, msg, ap);
    va_end(ap);

    fclose(f);
    }


//
// Catch output from the VM and send to the console.
//
// If this is a pain to port, it could be removed, as long
// as this line in startJava(): vm_args.vfprintf = &jvfprintf;
// is also removed.
//
static jint JNICALL jvfprintf(FILE *f, const char *fmt, va_list args)
    {
    static char buf[2048];
    int result;

    // WINDOWS-SPECIFIC: this is Microsoft VisualC++ specific, 
    // but this is safer since it takes the size of the buffer
    // into consideration, and won't overflow it.
    //result = _vsnprintf(buf, sizeof(buf), fmt, args);

 // use this for compilers other than Visual C++
    result = vsprintf(buf, fmt, args);

    gi.dprintf("%s", buf);
    return result;
    }


// Figure out what directory we're operating out of,
// so we can write a debug trace file and later
// setup a Java classpath
//
static void setupPaths()
    {
    char *p;
    cvar_t *game_cvar = gi.cvar("game", "baseq2", 0);

    getcwd(java_gameDirName, 1023);

    // Add a path separator to the end of the cwd
    strcat(java_gameDirName, "/");

    // append the name of the game
    strcat(java_gameDirName, game_cvar->string);

    // save the name of our debugLog file
    sprintf(debugFileName, "%s/q2java.log", java_gameDirName);

    fprintf(stdout, "log is %s\n", debugFileName);
    fprintf(stdout, "gamedir %s\n", java_gameDirName);
    // erase any existing debug file
    remove(debugFileName);
    }




//
// Startup (or find an existing) Java VM, give each Q2Java module
// a chance to initialize itself.
//
void startJava()
    {
    JDK1_1InitArgs vm_args;
    jint (JNICALL *p_JNI_GetDefaultJavaVMInitArgs)(void *);
    jint (JNICALL *p_JNI_CreateJavaVM)(JavaVM **, JNIEnv **, void *);

    int alreadyStarted = 0;  // flag to indicate a VM is already running
    char buffer[64];
    cvar_t *q2java_security;
    cvar_t *q2java_gamepath;

    setupPaths();

    // Get the DLL operating parameters
    //
    q2java_debugLog = gi.cvar("q2java_debugLog", "1", 0);
    q2java_security = gi.cvar("q2java_security", "2", CVAR_NOSET);
    q2java_gamepath  = gi.cvar("q2java_gamepath", "", CVAR_NOSET);

    debugLog("in startJava()\n");
    debugLog("Current Dir is: ");
    debugLog(java_gameDirName);
    debugLog("\n");

    java_error = NULL;    // no outstanding errors initially

    // save the initial security settings in case the
    // Java games tries to modify the cvar
    //
    strcpy(initialSecurity, q2java_security->string);

    // see if there are old pointers lying around, from
    // a previous invocation of the VM.
    //
    q2java_DLLHandle = gi.cvar("q2java_DLLHandle", "0", CVAR_NOSET);
    q2java_VMPointer = gi.cvar("q2java_VMPointer", "0", CVAR_NOSET);
    q2java_EnvPointer = gi.cvar("q2java_EnvPointer", "0", CVAR_NOSET);

    // Get a hold of the Java VM DLL, checking first for a handle
    // stashed away as a CVAR.
    //
//    if (q2java_DLLHandle->value)
//        hJavaDLL = (HINSTANCE) atoi(q2java_DLLHandle->string);
//    else
//        {
//        // no DLL handle was stashed in a CVAR, load it fresh.
//        hJavaDLL = LoadLibrary("javai.dll"); // WINDOWS-SPECIFIC
//        if (!hJavaDLL)
//            {
//            java_error = "Can't find javai.dll - perhaps you haven't installed a compatible Java on this machine?\n";
//            return;
//            }

        // save the Java DLL handle in a CVAR so we can get it back
        // if the DLL is reloaded
//        sprintf(buffer, "%d", hJavaDLL);
//        gi.cvar_forceset("q2java_DLLHandle", buffer);
//        }

    debugLog("startJava() skipped loading of libjava.so\n");

    if (q2java_VMPointer->value)
        {
        // The VM was already started...convert the pointers
        // we stashed in CVARs to actual pointers
        java_vm = (void *) atoi(q2java_VMPointer->string);
        java_env = (void *) atoi(q2java_EnvPointer->string);
        alreadyStarted = 1; // note that we're recycling an existing VM
        }
    else
        {
        // VM pointers weren't stashed in CVARS, so invoke a new VM

        // WINDOWS-SPECIFIC: get pointers to functions in the DLL
        //
/*         p_JNI_GetDefaultJavaVMInitArgs = GetProcAddress(hJavaDLL, "JNI_GetDefaultJavaVMInitArgs"); */
/*         p_JNI_CreateJavaVM = GetProcAddress(hJavaDLL, "JNI_CreateJavaVM"); */

        vm_args.version = 0x00010001;
        JNI_GetDefaultJavaVMInitArgs(&vm_args);

        // Build up a classpath that includes "<gamedir>\q2java.jar:"
        //
        strcpy(classpath, vm_args.classpath);
        strcat(classpath, ":");
        strcat(classpath, java_gameDirName);
        strcat(classpath, "/q2java.jar:");

        // If the user hasn't specified a q2java_gamepath cvar, append
        // "<gamedir>\classes;<gamedir>\q2jgame.zip"
        //
        if (strlen(q2java_gamepath->string) == 0)
            {
            strcat(classpath, java_gameDirName);
            strcat(classpath, "/classes:");
            strcat(classpath, java_gameDirName);
            strcat(classpath, "/q2jgame.zip:");
            }
        else // append "<gamedir>\<q2java_gamepath>;"
            {
            strcat(classpath, java_gameDirName);
            strcat(classpath, "/");
            strcat(classpath, q2java_gamepath->string);
            strcat(classpath, ":");
            }

        // set the new classpath
        vm_args.classpath = classpath;

        // hook up the output function (this could be omitted with no problem)
        vm_args.vfprintf = &jvfprintf;

        //
        // Let's kick the tires and light the fires!  
        // Come on big daddy..bring that bad boy on!
        //
        JNI_CreateJavaVM(&java_vm, &java_env, &vm_args);
        if (!java_vm)
            {
            // oh no..you did -not- shoot that green shit at me...
            java_error = "Couldn't create a Java Virtual Machine for some reason\n";
            return;
            }

        // save some key pointers and handles in cvars, in case
        // the DLL is reloaded and we need them back
        //
        sprintf(buffer, "%d", java_vm);
        gi.cvar_forceset("q2java_VMPointer", buffer);
        sprintf(buffer, "%d", java_env);
        gi.cvar_forceset("q2java_EnvPointer", buffer);
        }

    debugLog("startJava() created VM\n");

    // If Java's up and running, let each Q2Java module initialize itself
    // if any one encounters a problem, it sets the "java_error" pointer
    // to a short message indicating what went wrong.  All problems
    // indicated this way are fatal, and will prevent the DLL from
    // functioning properly.

    if (java_env)
        {
        // initialize the "Misc" module first, so the other ones
        // can check for exceptions
        if (!java_error)
            Misc_javaInit();

        // initialize the ConsoleOutputStream module next, so that
        // when the Misc module prints exception stack traces,
        // the output goes to the Quake2 console instead
        // of disappearing into the void.
        if (!java_error)
            ConsoleOutputStream_javaInit();

        // Initialize the rest

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

        // Turn security on, if enabled and the VM wasn't already started
        // from a previous invocation of the DLL

        if (!java_error && !alreadyStarted && q2java_security->value)
            enableSecurity((int)q2java_security->value);
        }
    }


//
// Clean things up and try shutting down the VM.
//
void stopJava()
    {
    debugLog("in stopJava()\n");

    // restore the initial security setting, in case
    // the java game modified the cvar.
    gi.cvar_forceset("q2java_security", initialSecurity);

    if (!java_vm)
        {
        debugLog("Can't destroy Java VM, pointer was null\n");
        return;
        }

    // Give each module a chance to clean up, important
    // to avoid Java memory leaks in case the DLL is reloaded.
    Entity_javaFinalize();
    Player_javaFinalize();
    CVar_javaFinalize();
    Game_javaFinalize();
    Engine_javaFinalize();
    ConsoleOutputStream_javaFinalize();
    Misc_javaFinalize();

    //
    // Try killing the VM (will always fail on Sun's Win32 JDK 1.1.5 and lower)
    //

    if ((*java_vm)->DestroyJavaVM(java_vm))
        debugLog("Error destroying Java VM, stupid Sun JDK is probably still broke\n");
    else
        {
        // We actually destroyed the VM!, really clean things up now.

/*         FreeLibrary(hJavaDLL); // WINDOWS-SPECIFIC */
        gi.cvar_forceset("q2java_DLLHandle", "0");
        gi.cvar_forceset("q2java_VMPointer", "0");
        gi.cvar_forceset("q2java_EnvPointer", "0");
/*         hJavaDLL = 0; */
        java_vm = 0;
        java_env = 0;
        debugLog("It's a miracle, the JDK is fixed! VM Destroyed and Java DLL freed\n");
        }
    }

