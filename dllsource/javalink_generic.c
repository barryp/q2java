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

#include <direct.h>  // WINDOWS-SPECIFIC? for _getcwd()
#include <stdio.h>   // needed for the debugLog() function
#include <stdarg.h>  // needed for the debugLog() function

#include "globals.h" // mainly so we can call other Q2Java module's
                     // initialization and cleanup functions

// Platform-specific strings
#ifdef _WIN32
    char *javalink_version = "Win32 Version";
    static char *file_separator = "\\";
    static char *path_separator = ";";
#else // assume Unix
    char *javalink_version = "Unix Version";
    static char *file_separator = "/";
    static char *path_separator = ":";
#endif


JNIEnv *java_env;    // Pointer to Java environment - used by other modules.
char *java_error;    // used by other modules to indicate initialization errors

static JavaVM *java_vm;    // Pointer to VM, used only by this module


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

#ifdef _MSC_VER
    // This is Microsoft VisualC++ specific, 
    // but this is safer since it takes the size of the buffer
    // into consideration, and won't overflow it.
    result = _vsnprintf(buf, sizeof(buf), fmt, args);
#else
    result = vsprintf(buf, fmt, args); // use this for compilers other than Visual C++
#endif

    gi.dprintf("%s", buf);
    return result;
    }



// Figure out what directory we're operating out of,
// so we can write a debug trace file and later
// setup a Java classpath
//
static void setupPaths()
    {
    cvar_t *game_cvar = gi.cvar("game", "baseq2", 0);

#ifdef _MSC_VER
    _getcwd(java_gameDirName, 1023);
#else
    getcwd(java_gameDirName, 1023);
#endif

    // Add a path separator to the end of the cwd
    strcat(java_gameDirName, file_separator);

    // append the name of the game
    strcat(java_gameDirName, game_cvar->string);

    // save the name of our debugLog file
    sprintf(debugFileName, "%s%sq2java.log", java_gameDirName, file_separator);

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

    jsize nVMs; // number of VM's active
    int alreadyStarted = 0;  // flag to indicate a VM is already running
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

    // look for an existing VM
    if (JNI_GetCreatedJavaVMs(&java_vm, 1, &nVMs))
        {
        java_error = "Search for existing VM's failed\n";
        return;
        }

    // get a VM going one way or another
    if (nVMs)
        {
        // There is already a VM started...
        if ((*java_vm)->AttachCurrentThread(java_vm, &java_env, NULL))
            {
            java_error = "Couldn't attach to existing VM\n";
            return;
            }

        alreadyStarted = 1; // note that we're recycling an existing VM
        debugLog("startJava() found existing VM\n");
        }
    else
        {
        // Invoke a new VM

        vm_args.version = 0x00010001;
        if (JNI_GetDefaultJavaVMInitArgs(&vm_args))
            {
            java_error = "Couldn't get default VM InitArgs, wrong VM version perhaps?\n";
            return;
            }

        // Build up a classpath that includes "<gamedir>\q2java.jar;"
        strcpy(classpath, vm_args.classpath);
        strcat(classpath, path_separator);
        strcat(classpath, java_gameDirName);
        strcat(classpath, file_separator);
        strcat(classpath, "q2java.jar;");

        // If the user hasn't specified a q2java_gamepath cvar, append
        // "<gamedir>\classes;<gamedir>\q2jgame.zip"
        if (strlen(q2java_gamepath->string) == 0)
            {
            strcat(classpath, java_gameDirName);
            strcat(classpath, file_separator);
            strcat(classpath, "classes");
            strcat(classpath, path_separator);
            strcat(classpath, java_gameDirName);
            strcat(classpath, file_separator);
            strcat(classpath, "q2jgame.zip");
            strcat(classpath, path_separator);
            }
        else // append "<gamedir>\<q2java_gamepath>;"
            {
            strcat(classpath, java_gameDirName);
            strcat(classpath, file_separator);
            strcat(classpath, q2java_gamepath->string);
            strcat(classpath, path_separator);
            }

        // set the new classpath
        vm_args.classpath = classpath;

        // hook up the output function (this could be omitted with no problem)
        vm_args.vfprintf = &jvfprintf;

        //
        // Let's kick the tires and light the fires!  
        // Come on big daddy..bring that bad boy on!
        //
        if (JNI_CreateJavaVM(&java_vm, &java_env, &vm_args))
            {
            // oh no..you did -not- shoot that green shit at me...
            java_error = "Couldn't create a new VM\n";
            return;
            }

        debugLog("startJava() created new VM\n");
        }



    // If Java's up and running, let each Q2Java module initialize itself
    // if any one encounters a problem, it sets the "java_error" pointer
    // to a short message indicating what went wrong.  All problems
    // indicated this way are fatal, and will prevent the DLL from
    // functioning properly.

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
    // Try killing the VM (will always fail on Sun's Win32 JDK 1.1.6 and lower)
    //
    if ((*java_vm)->DestroyJavaVM(java_vm))
        debugLog("Error destroying Java VM, stupid Sun JDK is probably still broke\n");
    else
        debugLog("It's a miracle, the JDK is fixed! VM Destroyed\n");
    }
