//
// javalink_generic.c - for use with JDK 1.1 and 1.2
//
// Invokes the JavaVM, generic to both Win32 and Unix platforms.
//
// Functions and variables that are only used in this module are
// all marked "static".  Anything non-static is also used
// in other parts of the Q2Java C code.
//
// Everything that is specific to the Win32 environment is #ifdef'ed
// with "_WIN32".  Things specific to Microsoft Visual C++ are #ifdef'ed
// with "_MSC_VER".
// 
// Things specific to JDK 1.2 are #ifdef'ed with "JDK1_2" which
// is #define'ed in the JDK 1.2 "jni.h"
//

#ifdef _MSC_VER
    #include <direct.h>  // MSC specific for _getcwd()
#else
    #include <unistd.h>  // Unix getcwd() 
#endif

#include <stdio.h>   // needed for the javalink_debug() function
#include <stdarg.h>  // needed for the javalink_debug() function
#include <stdlib.h>  // for getenv()

#include "globals.h" 
#include "properties.h"
#include "q2string.h"


// Fix to allow DLL to compile with older versions of <jni.h>
#ifndef JNI_VERSION_1_1
    #define JNI_VERSION_1_1 0x00010001
#endif


// Platform-specific strings
#ifdef _WIN32
    char *javalink_version = "Win32 Version";
    static char *file_separator = "\\";
    static char *path_separator = ";";

    // Stuff for locating and dynamically loading the VM DLL
    #include <windows.h> // for registry and DLL functions
    static HINSTANCE hJavaDLL; // handle to VM DLL
    jint (JNICALL *p_JNI_GetCreatedJavaVMs)(JavaVM **, jsize, jsize *);
    jint (JNICALL *p_JNI_GetDefaultJavaVMInitArgs)(void *);
    jint (JNICALL *p_JNI_CreateJavaVM)(JavaVM **, void **, void *);
#else // assume Unix
    #ifdef KAFFE
        char *javalink_version = "Unix (Kaffe) Version";
        #define JDK1_1InitArgs JavaVMInitArgs
    #else
        char *javalink_version = "Unix Version";
    #endif
    static char *file_separator = "/";
    static char *path_separator = ":";
#endif

// Visual C++ specific function renamings
#ifdef _MSC_VER
    #define strcasecmp _stricmp
    #define getcwd _getcwd
    #define vsnprintf _vsnprintf
#endif

#define MAX_FILENAME_SIZE 256

// variables visible to other modules
JNIEnv *java_env;    // Pointer to Java environment - used by other modules.
char *java_error;    // used by other modules to indicate initialization errors
char *javalink_gameDirName;      // name of Q2 game directory, something
                                 // like "c:\quake2\q2java"
int q2java_jinsight;  // flag to indicate we're running on Jinsight

// Variables private to this module
static char *VMDLLName;         // name of the Java DLL we're going to invoke
static char *initialSecurity;   // place to save initial security settings
static char *debugFileName;     // name of file debugLog is written to (if any)

static int vmIsOurs; // was the VM created by this DLL - or something else?

//
// Write a message to the debugLog, if it's enabled
//
void javalink_debug(const char *msg, ...)
    {
    va_list ap;
    FILE *f;

    if (!debugFileName)
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
// as the lines in the Create VM functions that reference
// it are also removed.
//
static jint JNICALL jvfprintf(FILE *f, const char *fmt, va_list args)
    {
    static char buf[2048];
    int result;

    // if your compiler doesn't do "vsnprintf", then you 
    // could add a #define up above like
    //   #ifdef MY_COMPILER
    //      #define vsnprintf(a, b, c, d) vsprintf(a, c, d)
    //   #endif
    // and just hope that the buffer is big enough, or
    // remove it all together like the comment up above mentions
    result = vsnprintf(buf, sizeof(buf), fmt, args);

    q2java_gi.dprintf("%s", buf);
    return result;
    }



// Figure out what directory we're operating out of,
// so we can write a debug trace file and later
// setup a Java classpath
//
static void setupPaths()
    {
    char *buffer;
    cvar_t *game_cvar = q2java_gi.cvar("game", "baseq2", 0);

    // make sure pointer is not uninitialized
    javalink_gameDirName = 0;

    // allocate a buffer we can getcwd() into
    buffer = q2stralloc(MAX_FILENAME_SIZE);

    if (buffer)
        {
        getcwd(buffer, MAX_FILENAME_SIZE - 1);
        buffer[MAX_FILENAME_SIZE-1] = 0;

        javalink_gameDirName = q2strcpy3(buffer, file_separator, game_cvar->string);
        q2strfree(buffer);
        }
    }


/**
 * If the user enabled debug-logging in the old style, 
 * generate a filename for them.
 *
 * If debug-logging has been enabled, either in the old
 * style of setting the CVar to 1, or the new style
 * by listing a filename in a properties file, 
 * erase any existing files by that name.
 */ 
static void setupDebug(char *val)
    {
    if (!strcmp(val, "1"))
        // generate a name for our debugLog file if it was specified in the old style
        debugFileName = q2strcpy3(javalink_gameDirName, file_separator, "q2java_debug.log");
    else if ((strlen(val) < 1) || (!strcmp(val, "0")))
        // disable debug logging
        debugFileName = 0;
    else
        // use the name provided
        debugFileName = q2strcpy(val);

    // erase any existing debug file
    if (debugFileName)
        {            
        FILE *f = fopen(debugFileName, "w");
        if (!f)
            q2java_gi.dprintf("Can't create/truncate debug log [%s]\n", debugFileName);
        else
            {
            fprintf(f, "-------------------------\nQ2Java %s (%s) Debug Log\n-------------------------\n", q2java_version, javalink_version);
            fclose(f); 
            }
        }
    }


#ifdef _WIN32
/**
 * Figure out the name of the VM DLL under the Win32 platform
 *
 * First, check if the user set the full DLL path in an environment
 * variable named "q2java_vmdll".  If so, then return that.
 *
 * If they didn't specify a dll, check in the registry under
 * HKEY_LOCAL_MACHINE\Software\JavaSoft\Java Runtime Environment\CurrentVersion
 * to see what the current version is.
 *
 * If we have a version, and the registry entry
 * HKEY_LOCAL_MACHINE\Software\JavaSoft\Java Runtime Environment\<version>\RuntimeLib
 * exists, return that. 
 *
 * Otherwise return the default value "javai.dll"
 */
static void getWin32DLLName()
    {
    char *p;
    LONG rc;
    HKEY javaKey;
    HKEY javaVersionKey;
    DWORD keyType;
    DWORD dataSize;

    // check if the DLL name has already been set by a properties file
    if (VMDLLName)
        return;

    // check if the DLL name has been explicitly specified in 
    // an environment variable
    p = getenv("q2java_vmdll");
    if (p)
        {
        VMDLLName = q2strcpy(p);
        return;
        }

    // if the DLL wasn't explicitly set, we
    // need to start mucking around in the registry
    if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, "Software\\JavaSoft\\Java Runtime Environment", 0, KEY_QUERY_VALUE, &javaKey) != ERROR_SUCCESS)    
        {
        if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, "Software\\JavaSoft\\Java Development Kit", 0, KEY_QUERY_VALUE, &javaKey) != ERROR_SUCCESS)    
            {
            // couldn't open JRE or JDK registry key? just return the default name
            VMDLLName = q2strcpy("javai.dll");
            return;
            }
        }

    VMDLLName = q2stralloc(MAX_FILENAME_SIZE);
    // Find the version of the VM we want to use, store in the VMDLLName string temporarily
    dataSize = MAX_FILENAME_SIZE;
    if (RegQueryValueEx(javaKey, "CurrentVersion", 0,  &keyType, VMDLLName, &dataSize) == ERROR_SUCCESS)
        {
        // at this point, we know the version of the JDK or JRE
        // open up the subkey for that particular version
        rc = RegOpenKeyEx(javaKey, VMDLLName, 0, KEY_QUERY_VALUE, &javaVersionKey);        

        // no longer need to have the VMDLLName string hold the CurrentVersion
        VMDLLName[0] = 0;

        // if we opened the subkey, try to figure out the VM DLL name
        if (rc == ERROR_SUCCESS)
            {
            // look for a JDK 1.2 style registry entry
            dataSize = MAX_FILENAME_SIZE;
            if (RegQueryValueEx(javaVersionKey, "RuntimeLib", 0,  &keyType, VMDLLName, &dataSize) != ERROR_SUCCESS)
                {
                // No dice? assume a JDK 1.1 style VM
                dataSize = MAX_FILENAME_SIZE;
                if (RegQueryValueEx(javaVersionKey, "JavaHome", 0,  &keyType, VMDLLName, &dataSize) == ERROR_SUCCESS)
                    VMDLLName = q2strcat(VMDLLName, "\\bin\\javai.dll");
                }
            RegCloseKey(javaVersionKey);
            }
        }

    // couldn't figure out a dll name? use the default 
    if (!VMDLLName[0])
        {
        q2strfree(VMDLLName);
        VMDLLName = q2strcpy("javai.dll");
        }

    RegCloseKey(javaKey);
    }
#endif



/**
 * Load the VM's DLL (does nothing in Unix)
 */
static void loadVMDLL()
    {
#ifdef _WIN32
    cvar_t *q2java_DLLHandle;

    javalink_debug("[C   ] loadVMDLL() started\n");

    // Get a hold of the Java VM DLL, checking first for a handle
    // stashed away as a CVAR.
    //
    q2java_DLLHandle = q2java_gi.cvar("q2java_DLLHandle", "0", CVAR_NOSET);
    if (q2java_DLLHandle->value)
        hJavaDLL = (HINSTANCE) atoi(q2java_DLLHandle->string);

    // Wasn't in CVAR, try loading fresh
    if (!hJavaDLL)
        {
        getWin32DLLName();
        hJavaDLL = LoadLibrary(VMDLLName);
        if (!hJavaDLL)
            q2java_gi.dprintf("Failed loading [%s]\n", VMDLLName);
        }

    // Update pointers
    if (hJavaDLL)
        {
        // The DLL is loaded, either freshly, or was stored in CVAR
        p_JNI_GetCreatedJavaVMs = GetProcAddress(hJavaDLL, "JNI_GetCreatedJavaVMs");
        p_JNI_GetDefaultJavaVMInitArgs = GetProcAddress(hJavaDLL, "JNI_GetDefaultJavaVMInitArgs");
        p_JNI_CreateJavaVM = GetProcAddress(hJavaDLL, "JNI_CreateJavaVM");
        javalink_debug("[C   ] loadVMDLL() finished, DLL found\n");
        }
    else
        {
        // No DLL loaded? make sure we don't have bogus pointers lying around
        p_JNI_GetCreatedJavaVMs = 0;
        p_JNI_GetDefaultJavaVMInitArgs = 0;
        p_JNI_CreateJavaVM = 0;
        javalink_debug("[C   ] loadVMDLL() finished, DLL not found\n");
        }
#endif
    }


/**
 * Unload the VM's DLL (does nothing in Unix)
 */
static void unloadVMDLL()
    {
#ifdef _WIN32
    if (hJavaDLL)
        {
        FreeLibrary(hJavaDLL); 
        q2java_gi.cvar_forceset("q2java_DLLHandle", "0");
        hJavaDLL = 0;    
        }
#endif
    }


/**
 * Stash away a pointer to the VM's DLL (does nothing in Unix)
 */
static void stashVMDLL()
    {
#ifdef _WIN32
    char buffer[16];
    sprintf(buffer, "%d", hJavaDLL);
    q2java_gi.cvar_forceset("q2java_DLLHandle", buffer);
#endif
    }


/**
 * Wrapper for JNI_GetCreatedJavaVMs
 *
 * calls statically linked function in Unix, dynamically loaded function in Win32
 */
static jint GetCreatedJavaVMs(JavaVM **vmBuf, jsize bufLen, jsize *nVMs)
    {
#ifdef _WIN32
    if (p_JNI_GetCreatedJavaVMs)
        return (*p_JNI_GetCreatedJavaVMs)(vmBuf, bufLen, nVMs);
    else
        return -1;
#else
    return JNI_GetCreatedJavaVMs(vmBuf, bufLen, nVMs);
#endif
    }


/**
 * Wrapper for JNI_GetDefaultJavaVMInitArgs
 *
 * calls statically linked function in Unix, dynamically loaded function in Win32
 */
static jint GetDefaultJavaVMInitArgs(void *vm_args)
    {
#ifdef _WIN32
    if (p_JNI_GetDefaultJavaVMInitArgs)
        return (*p_JNI_GetDefaultJavaVMInitArgs)(vm_args);
    else
        return -1;
#else
    return JNI_GetDefaultJavaVMInitArgs(vm_args);
#endif
    }


/**
 * Wrapper for JNI_CreateJavaVM
 *
 * calls statically linked function in Unix, dynamically loaded function in Win32
 */
static jint CreateJavaVM(JavaVM **java_vm, void **java_env, void *vm_args)
    {
#ifdef _WIN32
    if (p_JNI_CreateJavaVM)
        return (*p_JNI_CreateJavaVM)(java_vm, java_env, vm_args);
    else
        return -1;
#else
    return JNI_CreateJavaVM(java_vm, java_env, vm_args);
#endif
    }



/**
 * Try to initialize VM using JNI 1.1
 *
 * @return 0 on success, negative value on failure
 */
static jint createVM_JNI_11(char **properties, JavaVM **java_vm)
    {
    JDK1_1InitArgs *pVM11Args;
    char **prop;
    char *p;
    char *classpath;
    jint rc;
    int argLength;

    javalink_debug("[C   ] createVM_JNI_11() starting\n");

    // initialize the vm_args structure
    argLength = sizeof(JDK1_1InitArgs);

    // Jinsight extended the arg structure, so we need some extra padding
    if (q2java_jinsight) 
        argLength += 256;
    
    pVM11Args = q2java_gi.TagMalloc(argLength, TAG_GAME);
    memset(pVM11Args, 0, argLength);
    pVM11Args->version = JNI_VERSION_1_1;

    if ((GetDefaultJavaVMInitArgs(pVM11Args) < 0) || (pVM11Args->version != JNI_VERSION_1_1))
        {
        javalink_debug("[C   ] createVM_JNI_11 init args returned %x\n", pVM11Args->version);
        q2java_gi.TagFree(pVM11Args);
        return -1;           
        }

    javalink_debug("[C   ] createVM_JNI_11() got default init args\n");
    javalink_debug("[C   ] default classpath=%s\n", pVM11Args->classpath);

    // start off with no classpath
    classpath = 0;

    // try to get one from an environment variable
    //Handy for Linux JDK..which can't determine the proper classpath itself
    p = getenv("Q2JAVA_CLASSPATH"); 
    if (p)
        {
        classpath = q2strcpy(p);
        javalink_debug("[C   ] Found Q2JAVA_CLASSPATH environment variable\n");
        }


    // still no classpath? see if one was specified in properties
    if (!classpath)
        {
        // look to see if the user specified the classpath as a property
        for (prop = properties; *prop; prop++)
            {
            if (!strncmp(*prop, "java.class.path=", 16))
                {
                // found it
                classpath = q2strcpy((*prop)+16);
                javalink_debug("[C   ] Found java.class.path System property\n");
                }
            }
        }

    // STILL no classpath? build up one that includes "<gamedir>\classes;"
    if (!classpath)
        {
        classpath = q2strcpy5(pVM11Args->classpath, path_separator, javalink_gameDirName, file_separator, "classes");
//      classpath = q2strcat(classpath, path_separator);
        }

    // set the new classpath
    pVM11Args->classpath = classpath;
    javalink_debug("[C   ] final classpath=%s\n", pVM11Args->classpath);

    // link to the properties we found in readProperties() (if any)
    pVM11Args->properties = properties;

    // dump VM properties to debuglog
    for (prop = properties; *prop; prop++)
        javalink_debug("[C   ] System Property [%s]\n", *prop);

    // hook up the output function (this could be omitted with no problem)
    pVM11Args->vfprintf = &jvfprintf;


#ifdef KAFFE
    pVM11Args->libraryhome = getenv("LD_LIBRARY_PATH");
#endif

    //
    // Let's kick the tires and light the fires!  
    // Come on big daddy..bring that bad boy on!
    //
    javalink_debug("[C   ] createVM_JNI_11() about to create vm\n");

    rc = CreateJavaVM(java_vm, (void **)&java_env, pVM11Args);

    if (rc)
        javalink_debug("[C   ] createVM_JNI_11 failed\n");

    // clean up
    q2strfree(classpath);
    q2java_gi.TagFree(pVM11Args);

    return rc;
    }



/**
 * shift a string to the right, pad with spaces
 * assume enough memory is allocated.
 */
static void rshift(char *s, int n)
    {
    int i;

    // copy chars to the right <n> spaces
    memmove(s+n, s, strlen(s)+1);

    // pad with spaces
    for (i = 0; i < n; i++)
        s[i] = ' ';
    }




/**
 * Try to initialize VM using JNI 1.2
 *
 * @return 0 on success, negative value on failure
 */
static jint createVM_JNI_12(char **properties, JavaVM **java_vm)
    {
#ifdef JDK1_2
    char **prop;
    char *classpath;
    char *securityPolicy;
    char *p;
    int foundPath;
    int optionCount;
    int i;
    jint rc;
    int argLength;
    JavaVMOption *options;
    JavaVMInitArgs *pVM12Args;

    javalink_debug("[C   ] createVM_JNI_12() starting\n");

    // allocate and initialize a VM args structure, but make sure 
    // it's big enough to satisfy a JDK 1.1 VM
    argLength = sizeof(JDK1_1InitArgs);
    if (sizeof(JavaVMInitArgs) > argLength)
        argLength = sizeof(JavaVMInitArgs);
    argLength += 256; // ugly hack - pad for VM's that extend the structure like JInsight's JVM
    pVM12Args = q2java_gi.TagMalloc(argLength, TAG_GAME);
    memset(pVM12Args, 0, argLength);
    pVM12Args->version = JNI_VERSION_1_2;

    if ((GetDefaultJavaVMInitArgs(pVM12Args) < 0) || (pVM12Args->version != JNI_VERSION_1_2))
        {
        javalink_debug("[C   ] createVM_JNI_12 init args returned %x\n", pVM12Args->version);
        q2java_gi.TagFree(pVM12Args);
        return -1;           
        }

    javalink_debug("[C   ] createVM_JNI_12() got default init args\n");

    // allocate enough JavaVMOptions to hold the properties, plus a few extras
    options = q2java_gi.TagMalloc((getPropertyCount(properties) + 5) * sizeof(JavaVMOption), TAG_GAME);
    if (!options)
        {
        javalink_debug("[C   ] Couldn't allocate memory to hold JDK 1.2 options\n");
        q2java_gi.TagFree(pVM12Args);
        return -1;
        }

    // make sure we don't have uninitialized pointers
    classpath = 0;
    securityPolicy = 0;

    // try to get classpath from an environment variable
    // (to be consistent with the JDK 1.1 invocation code)
    p = getenv("Q2JAVA_CLASSPATH"); 
    if (p)
        {
        classpath = q2strcpy(p);
        javalink_debug("[C   ] Found Q2JAVA_CLASSPATH environment variable\n");
        }

    // copy the properties read from a properties file (if any)
    optionCount = 0;
    foundPath = 0;
    for (prop = properties; *prop; prop++)
        {
        char *s = *prop;

        // this is a bit ugly..but add "-D" to the front
        // of lines that didn't begin with a dash or underscore,
        // so that they're defined as system properties.
        //
        // the readProperties() function should have allocated
        // 2 extra bytes to each string so we could do this
        if ((*s != '-') && (*s != '_'))
            {
            rshift(s, 2);
            s[0] = '-';
            s[1] = 'D';
            }

        options[optionCount++].optionString = s;

        // check if one of the properties is a classpath
        if (!strncmp(s, "-Djava.class.path=", 18))
            {
            foundPath = 1;

            // if path was specified in environment variable - override the property
            if (classpath)
                {
                classpath = q2strins("-Djava.class.path=", classpath);
                options[optionCount-1].optionString = classpath;
                }
            }
        }

    javalink_debug("[C   ] createVM_JNI_12() copied properties\n");

    // if the properties file didn't have a classpath, and one wasn't specified as an environment variable - make one
    if (!foundPath)
        {            
        // Build up a classpath that includes "<gamedir>\classes;"
        classpath = q2strcpy5("-Djava.class.path=", javalink_gameDirName, file_separator, "classes", path_separator);
        options[optionCount++].optionString = classpath;
        }

    // enable Security if the user didn't set q2java_security 0
    if (strcmp(initialSecurity, "0"))
        {
        options[optionCount++].optionString = "-Djava.security.manager";

        securityPolicy = q2strcpy("-Djava.security.policy=file:/");

        if (isdigit(initialSecurity[0]) && (initialSecurity[1] == 0))
            {
            // security was specified with old-style '1' or '2'
            // generate default policy name
            securityPolicy = q2strcat(securityPolicy, javalink_gameDirName);
            securityPolicy = q2strcat(securityPolicy, "/q2java.policy");
            }
        else
            // user specified full policy file name
            securityPolicy = q2strcat(securityPolicy, initialSecurity);

#ifdef _WIN32
            {
            char *ch;
            // security policy filenames are specifed with forward slashes
            // so we gotta convert the windows-style backslashes to forward slashes
            for (ch = securityPolicy; *ch; ch++)
                {
                if (*ch == '\\')
                    *ch = '/';
                }
            }
#endif
        options[optionCount++].optionString = securityPolicy;
        }

    // hook print function into VM
    options[optionCount].optionString = "vprintf";
    options[optionCount++].extraInfo = &jvfprintf;
    
    pVM12Args->options = options;
    pVM12Args->nOptions = optionCount;
    pVM12Args->ignoreUnrecognized = JNI_TRUE;
   
    // dump VM options to debuglog
    for (i = 0; i < optionCount; i++)
        javalink_debug("[C   ] VM Option [%s]\n", options[i]);

    //
    // Let's kick the tires and light the fires!  
    // Come on big daddy..bring that bad boy on!
    //
    rc = CreateJavaVM(java_vm, (void **)&java_env, pVM12Args);

    if (rc)
        javalink_debug("[C   ] createVM_JNI_12 failed\n");

    // cleanup
    q2java_gi.TagFree(options);
    q2java_gi.TagFree(pVM12Args);
    q2strfree(classpath);
    q2strfree(securityPolicy);

    return rc;
#else
    return -1;
#endif
    }


/**
 * Deal with with lines in the property files that began with "q2java_"
 */
void javalink_property(const char *name, const char *value)
    {
    // look for explicit DLL path
    if (!(strcasecmp(name, "q2java_vmdll")))
        {
        VMDLLName = q2strcpy(value);
        return;
        }

    // look for explicit debuglog
    if (!(strcasecmp(name, "q2java_debuglog")))
        {
        q2strfree(debugFileName);
        debugFileName = q2strcpy(value);
        return;
        }

    // look for explicit security settings
    if (!(strcasecmp(name, "q2java_security")))
        {
        q2strfree(initialSecurity);
        initialSecurity = q2strcpy(value);
        return;
        }

    // see if we need to act a little different for Jinsight
    if (!(strcasecmp(name, "q2java_jinsight")))
        {
        q2strfree(initialSecurity);
        q2java_jinsight = value[0] - '0';
        return;
        }
    }

//
// Startup (or find an existing) Java VM, give each Q2Java module
// a chance to initialize itself.
//
void javalink_start()
    {
    JavaVM *java_vm;
    jsize nVMs; // number of VM's active
    cvar_t *q2java_security;
    cvar_t *q2java_properties;
    cvar_t *q2java_debugLog;   
    int jdk_version;
    char *buffer;
    char **properties;

    // get the game's path figured out.
    setupPaths();

    // build up the name of the default properties file
    buffer = q2strcpy3(javalink_gameDirName, file_separator, "q2java.properties");

    // Let the properties file be overridden on the command line
    q2java_properties = q2java_gi.cvar("q2java_properties", buffer, CVAR_NOSET);

    // CVar now holds name of properties file..free the temp buffer
    q2strfree(buffer);

    // make sure global pointers are initialized
    VMDLLName = 0;

    // set default security to "2", may be overriden by
    // property file first, and then command line last
    initialSecurity = q2strcpy("2");

    debugFileName = q2strcpy("");

    // read the properties file(s), with 2 extra bytes in each string
    // so that JDK 1.2 can insert a "-D" if necessary
    properties = readProperties(q2java_properties->string, 2);

    // see if the debugLog was set on the command line, and 
    // take care if any initialization of the debug log
    q2java_debugLog = q2java_gi.cvar("q2java_debugLog", debugFileName, 0);

    q2strfree(debugFileName);
    setupDebug(q2java_debugLog->string);
    javalink_debug("[C   ] javalink_start() started\n");

    // see if security was overridden on the command line
    q2java_security = q2java_gi.cvar("q2java_security", initialSecurity, CVAR_NOSET);
    q2strfree(initialSecurity);

    // save whatever was in the cvar (may have been read from the property file, or may have been on the command line)
    initialSecurity = q2strcpy(q2java_security->string);

    java_error = NULL;    // no outstanding errors initially
    loadVMDLL();

    // look for an existing VM
    if (GetCreatedJavaVMs(&java_vm, 1, &nVMs))
        {
        javalink_debug("[C   ] javalink_start() unable to GetCreatedJavaVMs()\n");
        java_error = "Search for existing VM's failed\n";
        return;
        }

    // get a VM going one way or another
    if (nVMs)
        {
        // There is already a VM started...
        if ((*java_vm)->AttachCurrentThread(java_vm, (void **)&java_env, NULL))
            {
            javalink_debug("[C   ] javalink_start() couldn't attach to existing VM\n");
            java_error = "Couldn't attach to existing VM\n";
            return;
            }

        vmIsOurs = 0;  // VM was created elsewhere or elsewhen.
        javalink_debug("[C   ] javalink_start() attached to existing VM\n");
        }
    else
        {        
        jdk_version = 0;

        if (createVM_JNI_12(properties, &java_vm) == 0)
            jdk_version = 12;

        if (!jdk_version)
            {
            if (createVM_JNI_11(properties, &java_vm) == 0)
                jdk_version = 11;
            }

        if (jdk_version)
            {
            vmIsOurs = 1;  // I made this! (10:13)             
            javalink_debug("[C   ] javalink_start() created VM, JNI version = %d\n", jdk_version);
            }
        else
            {
            // oh no..you did -not- shoot that green shit at me...
            java_error = "Couldn't create a Java Virtual Machine for some reason\n";
            javalink_debug("[C   ] javalink_start() unable to create new VM\n");
            return;
            }
        }

    // free up the memory used in holding properties
    freeProperties(properties);

    // If Java's up and running, let each Q2Java module initialize itself
    // if any one encounters a problem, it sets the "java_error" pointer
    // to a short message indicating what went wrong.  All problems
    // indicated this way are fatal, and will prevent the DLL from
    // functioning properly.

    // initialize the "Misc" module first, so the other ones
    // can check for exceptions
    if (!java_error)
        Misc_javaInit();

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

    // If we're not running JDK 1.2, and the q2java_security wasn't set to 0,
    // and we created the VM, activate the q2java.SecurityManager.
    if ((jdk_version != 12) && vmIsOurs && !java_error && (initialSecurity[0] != '0'))
        enableSecurity(initialSecurity[0] - '0');
    }


//
// Clean things up and try shutting down the VM.
//
void javalink_stop()
    {
    javalink_debug("[C   ] javalink_stop()\n");

    // restore the initial security setting, in case
    // the java game modified the cvar.
    q2java_gi.cvar_forceset("q2java_security", initialSecurity);

    // make sure there's actually a VM out there
    if (!java_env)
        {
        javalink_debug("[C   ] Can't stop Java VM, java_env pointer was null\n");
        return;
        }

    // Give each module a chance to clean up, important
    // to avoid Java memory leaks in case the DLL is reloaded.
    //
    // Also..if this DLL is going away, we can't have native methods
    // registered that point to functions here...if they were called
    // after this DLL was gone, it would almost certainly cause an
    // access violation.  
    // 
    // If the native methods are deregistered, then trying to call
    // them should only throw an exception, rather than bring the
    // whole process down.
    Entity_javaDetach();
    Player_javaDetach();
    CVar_javaDetach();
    Game_javaDetach();
    Engine_javaDetach();
    Misc_javaDetach();

    //
    // Try killing the VM if we created it 
    //
    if (vmIsOurs)
        {
        JavaVM *java_vm;

        (*java_env)->GetJavaVM(java_env, &java_vm);
        if ((*java_vm)->DestroyJavaVM(java_vm))
            {
            javalink_debug("[C   ] Error destroying Java VM, stupid Sun JDK is probably still broke\n");
            stashVMDLL();
            }
        else
            {
            javalink_debug("[C   ] It's a miracle, the JDK is fixed! VM Destroyed\n");
            unloadVMDLL();
            }
        }
    else
        stashVMDLL();
    }
