//
// javalink_win32.c
//
// PORTING NOTES FOR OTHER PLATFORMS
//
// Functions and variables that are only used in this module are
// all marked "static".  Anything non-static is also used
// in other parts of the Q2Java C code.
//
// Everything that is specific to the Win32 environment is marked
// with "WINDOWS-SPECIFIC:", and will definitely need to be replaced
// in other platforms.
//
// There are some lines in setupPaths() and startJava() where directory
// paths are being built-up, and the backslashes will need to be replaced
// with other path-separators.  Fortunately, you can just do a
// search-and-replace, looking for a double-backslash pattern "\\", and
// replacing with the separator of your choice.
//
// The main complication in the Win32 javalink.c is that it's not possible
// to really destroy a JavaVM under Sun JDK 1.1.5 and lower, so it goes
// through some gyrations to squirrel-away some key pointers as Quake II
// CVARS, so that if the DLL is unloaded and reloaded - it can reattach
// itself to the running VM.  Other platforms may not have that problem,
// and can do away with the CVAR trickery.
//

#include <windows.h> // WINDOWS-SPECIFIC: for DLL handling & GetModuleFileName()
#include <stdio.h>   // needed for the debugLog() function
#include <stdarg.h>  // needed for the debugLog() function

#include "globals.h" // mainly so we can call other Q2Java module's
                     // initialization and cleanup functions

char *javalink_version = "Win32 Version";

JNIEnv *java_env;    // Pointer to Java environment - used by other modules.
char *java_error;    // used by other modules to indicate initialization errors

static HINSTANCE hJavaDLL; // WINDOWS-SPECIFIC: handle to javai.dll
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
    result = _vsnprintf(buf, sizeof(buf), fmt, args);

//  result = vsprintf(buf, fmt, args); // use this for compilers other than Visual C++

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

    // Get the DLL's operating parameters
    //
    q2java_debugLog = gi.cvar("q2java_debugLog", "0", 0);
    q2java_security = gi.cvar("q2java_security", "2", CVAR_NOSET);
    q2java_gamepath  = gi.cvar("q2java_gamepath", "", CVAR_NOSET);

    debugLog("in startJava()\n");

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
    if (q2java_DLLHandle->value)
        hJavaDLL = (HINSTANCE) atoi(q2java_DLLHandle->string);
    else
        {
        // no DLL handle was stashed in a CVAR, load it fresh.
        hJavaDLL = LoadLibrary("javai.dll"); // WINDOWS-SPECIFIC
        if (!hJavaDLL)
            {
            java_error = "Can't find javai.dll - perhaps you haven't installed a compatible Java on this machine?\n";
            return;
            }

        // save the Java DLL handle in a CVAR so we can get it back
        // if the DLL is reloaded
        sprintf(buffer, "%d", hJavaDLL);
        gi.cvar_forceset("q2java_DLLHandle", buffer);
        }

    debugLog("startJava() found DLL\n");

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
        p_JNI_GetDefaultJavaVMInitArgs = GetProcAddress(hJavaDLL, "JNI_GetDefaultJavaVMInitArgs");
        p_JNI_CreateJavaVM = GetProcAddress(hJavaDLL, "JNI_CreateJavaVM");

        vm_args.version = 0x00010001;
        (*p_JNI_GetDefaultJavaVMInitArgs)(&vm_args);

        // Build up a classpath that includes "<gamedir>\q2java.jar;"
        //
        strcpy(classpath, vm_args.classpath);
        strcat(classpath, ";");
        strcat(classpath, java_gameDirName);
        strcat(classpath, "\\q2java.jar;");

        // If the user hasn't specified a q2java_gamepath cvar, append
        // "<gamedir>\classes;<gamedir>\q2jgame.zip"
        //
        if (strlen(q2java_gamepath->string) == 0)
            {
            strcat(classpath, java_gameDirName);
            strcat(classpath, "\\classes;");
            strcat(classpath, java_gameDirName);
            strcat(classpath, "\\q2jgame.zip;");
            }
        else // append "<gamedir>\<q2java_gamepath>;"
            {
            strcat(classpath, java_gameDirName);
            strcat(classpath, "\\");
            strcat(classpath, q2java_gamepath->string);
            strcat(classpath, ";");
            }

        // set the new classpath
        vm_args.classpath = classpath;

        // hook up the output function (this could be omitted with no problem)
        vm_args.vfprintf = &jvfprintf;

        //
        // Let's kick the tires and light the fires!  
        // Come on big daddy..bring that bad boy on!
        //
        (*p_JNI_CreateJavaVM)(&java_vm, &java_env, &vm_args);
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

        FreeLibrary(hJavaDLL); // WINDOWS-SPECIFIC
        gi.cvar_forceset("q2java_DLLHandle", "0");
        gi.cvar_forceset("q2java_VMPointer", "0");
        gi.cvar_forceset("q2java_EnvPointer", "0");
        hJavaDLL = 0;
        java_vm = 0;
        java_env = 0;
        debugLog("It's a miracle, the JDK is fixed! VM Destroyed and Java DLL freed\n");
        }
    }
