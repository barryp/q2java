// This may look like C code, but it is really -*- C++ -*-
//-----------------------------------------------------------------------------
//
// $Id:$
//
// Copyright (C) 1998 by authors.
//
// This program is available for distribution and/or modification
// only under the terms of the Q2Java License (Q2JL) as
// published by the Q2Java authors. All rights reserved.
//
// You should have received a copy of the Q2Java License along
// with this program; if not, write to the members of the
// Q2Java team at q2java@openquake.org.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// Q2Java License for more details.
//
// $Log:$
//
// DESCRIPTION:
//   Invocation of the Java Virtual Machine (JVM) 
//
//-----------------------------------------------------------------------------

// Revision control, not in binary when optimizing.
static char rcsid[] = "@(#) $Id:$";


#define DEBUG    1

#ifdef WIN32
// WINDOWS-SPECIFIC: for DLL handling & GetModuleFileName()
#include <windows.h>
#else
#include <unistd.h>
#include <dlfcn.h>
#endif

 // needed for the debugLog() function
#include <stdio.h>  
#include <stdarg.h>

#include <string.h>

// mainly so we can call
// initialization and cleanup functions
// of other Q2Java modules
#include "globals.h"



// Pointer to Java environment - used by other modules.
JNIEnv *java_env;

// used by other modules to indicate initialization errors
char *java_error;    


// WIN32 type for Java DLL handle, emulated here.
#ifndef WIN32
#define HINSTANCE void*
#endif

static HINSTANCE     hJavaDLL;

// Pointer to VM, used only by this module
static JavaVM *java_vm;   


// The next 3 cvar_t declarations are needed because
// the Java VM can not be shutdown, and we need
// to stash away a few key pointers where they can survive
// when the DLL is unloaded and reloaded.
static cvar_t *q2java_DLLHandle;
static cvar_t *q2java_VMPointer;
static cvar_t *q2java_EnvPointer;


#ifdef WIN32
char*  PathSep =	"\\";
char*  PathsSep =	";";
#else
char*  PathSep =	"/";
char*  PathsSep =	":";
#endif

#define BUFFER_LENGTH 1024
// place to save initial security settings
static char initialSecurity[BUFFER_LENGTH];

// name of Q2 game directory,
//  determined by location of calling app
char java_gameDirName[BUFFER_LENGTH];     

// VM classpath
static char classpath[BUFFER_LENGTH];

// name of file debugLog is written to
static char debugFileName[BUFFER_LENGTH];

// cvar indicating whether debugLog is on/off
static cvar_t *q2java_debugLog;   


//
// Write a message to the debugLog, if it is enabled
//
void debugLog( const char *msg, ... )
{
#ifdef DEBUG
  va_list ap;

  va_start(ap, msg);
  vfprintf(stderr, msg, ap);
  va_end(ap);
  fflush(stderr);
#else
  va_list ap;
  FILE *f;

  if (!q2java_debugLog->value)
    return; // debugLog is not enabled - so bail.
  
  f = fopen(debugFileName, "a");
  
  va_start(ap, msg);
  vfprintf(f, msg, ap);
  va_end(ap);

  fclose(f);
#endif
}


//
// Catch output from the VM and send to the console.
//
// If this is a pain to port, it could be removed, as long
// as this line in startJava(): vm_args.vfprintf = &jvfprintf;
// is also removed.
//
static int JNICALL
jvfprintf
( FILE*		f,
  const char*	fmt,
  va_list	args )
{
  static char buf[2048];
  int result;

#ifdef WIN32
  // WINDOWS-SPECIFIC: this is Microsoft VisualC++ specific,
  result = _vsnprintf(buf, sizeof(buf), fmt, args);
#else
  result = vsprintf(buf, fmt, args);
#endif
  
  gi.dprintf("%s", buf);
  return result;
}


//
//
//
void
getLocation
(  char*  location,
   int    location_length )
{
#ifdef WIN32
  GetModuleFileName( 0, location, location_length );
#else
  getcwd( location, location_length );
#endif

  debugLog( "getLocation:: location %s\n", location );
}


//
// Figure out what directory we are operating out of,
// so we can write a debug trace file and later
// setup a Java classpath
//
static void setupPaths()
{
  char*		p;
  cvar_t*	game_cvar = gi.cvar("game", "baseq2", 0);

  debugLog( "setupPaths:: setting up Q2Java gamedir path\n");
  
  // get the full pathname of the Quake2 .EXE file
  getLocation( java_gameDirName, BUFFER_LENGTH );
 
  // find the last backslash in the path
  p = strrchr(java_gameDirName, PathSep[0] );
  
  // p should never be null, but just in case....
  if (!p)
    p = java_gameDirName;
  else
    p++; // p is now just after the last backslash
  
  // append the name of the game
  strcpy(p, game_cvar->string);
  
  // save the name of our debugLog file
  sprintf(debugFileName, "%s%sq2java.log", java_gameDirName, PathSep );

  debugLog( "setupPaths:: debug log file is %s\n",
	    debugFileName );
  
  // erase any existing debug file
  remove(debugFileName);
}



static HINSTANCE
retrieveDLL( char* handle )
{
  debugLog( "retrieving DLL %s\n", handle );
  return (HINSTANCE)(atoi(handle));
}


static HINSTANCE
loadDLL()
{
  HINSTANCE hJavaDLL;

  debugLog( "loadDLL: loading JVM DLL\n" );
  
#ifdef WIN32
#define JAVA_DLL "javai.dll"
  hJavaDLL = LoadLibrary( JAVA_DLL ); // WINDOWS-SPECIFIC
#else
#define JAVA_DLL "libjava.so"
  hJavaDLL = dlopen( JAVA_DLL, RTLD_NOW | RTLD_GLOBAL );
#endif

  if (!hJavaDLL)
  {
    debugLog( "loadDLL: can't find %s", JAVA_DLL );
    java_error = "Can't find %s \n" JAVA_DLL;
#ifndef WIN32
    debugLog( "loadDLL: %s\n", dlerror() );
#endif
    return NULL;
  }

  debugLog( "loadDLL: loaded DLL %s\n", JAVA_DLL );
  return hJavaDLL;
}

static void
closeDLL( HINSTANCE handle )
{
  const char* err;
  debugLog( "closing DLL handle %i\n", (int)handle );
  
#ifdef WIN32
  FreeLibrary( handle ); // WINDOWS-SPECIFIC
#else
  dlclose( handle );
  if ( (err = dlerror())!= NULL )
    debugLog( "closeDLL: %s\n", err );
#endif
}


static void*
getAddress
( HINSTANCE	dll,
  char*		symbol )
{
  void* tmp;
  const char* err;
  debugLog( "getting DLL address of %s\n", symbol );
  
#ifdef WIN32
  // WINDOWS-SPECIFIC: get pointers to functions in the DLL
  tmp = GetProcAddress( dll, symbol );
  return tmp;
#else
  tmp = dlsym( dll, symbol );
  if ( tmp == NULL )
  {
    if ( (err = dlerror())!= NULL )
      debugLog( "loadDLL: %s\n", err );
    else
      debugLog( "loadDLL: symbol %s not found\n", symbol );  
  }
  return tmp;
#endif
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

  // flag to indicate a VM is already running
  int alreadyStarted = 0;
  
  char buffer[64];
  cvar_t*	q2java_security;
  cvar_t*	q2java_gamepath;

  fprintf( stderr, "Q2Java %s\n", rcsid ); fflush( stderr );

  debugLog("Q2Java %s\n", rcsid );
  debugLog("startJava:: setting up JVM and environment\n");
  
  setupPaths();

  // Get the operating parameters of the DLL
  //
  q2java_debugLog = gi.cvar("q2java_debugLog", "0", 0);
  q2java_security = gi.cvar("q2java_security", "2", CVAR_NOSET);
  q2java_gamepath  = gi.cvar("q2java_gamepath", "", CVAR_NOSET);
  
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

  debugLog( "startJava:: load or retrieve JVM\n");
  
  // Get a hold of the Java VM DLL, checking first for a handle
  // stashed away as a CVAR.
  //
  if (q2java_DLLHandle->value)
    hJavaDLL = retrieveDLL( q2java_DLLHandle->string );
  else
  {
    // no DLL handle was stashed in a CVAR, load it fresh.
    hJavaDLL = loadDLL();
    
    // save the Java DLL handle in a CVAR so we can get it back
    // if the DLL is reloaded
    sprintf(buffer, "%d", (int)hJavaDLL);
    gi.cvar_forceset("q2java_DLLHandle", buffer);
  }

  debugLog("startJava:: found DLL\n");
  
  if (q2java_VMPointer->value)
  {
    // The VM was already started...convert the pointers
    // we stashed in CVARs to actual pointers
    java_vm = (void *) atoi(q2java_VMPointer->string);
    java_env = (void *) atoi(q2java_EnvPointer->string);

    // note that we are recycling an existing VM
    alreadyStarted = 1; 
  }
  else
  {
    // VM pointers were not stashed in CVARS, so invoke a new VM
    // get pointers to functions in the DLL
    p_JNI_GetDefaultJavaVMInitArgs
      = getAddress(hJavaDLL, "JNI_GetDefaultJavaVMInitArgs");
    
    p_JNI_CreateJavaVM
      = getAddress(hJavaDLL, "JNI_CreateJavaVM");

    vm_args.version = 0x00010001;

    // Call It.
    (*p_JNI_GetDefaultJavaVMInitArgs)(&vm_args);
    
    // Build up a classpath that includes "<gamedir>\q2java.jar;"
    //
    strcpy(classpath, vm_args.classpath);
    strcat(classpath, PathsSep );
    strcat(classpath, java_gameDirName);
    strcat(classpath, PathSep );
    strcat(classpath, "q2java.jar");
    strcat(classpath, PathsSep );
    
    
    // If the user has not specified a q2java_gamepath cvar,
    // append "<gamedir>\classes;<gamedir>\q2jgame.zip"
    //
    if (strlen(q2java_gamepath->string) == 0)
    {
      strcat(classpath, java_gameDirName);
      strcat(classpath, PathSep );
      strcat(classpath, "classes");
      strcat(classpath, PathsSep );
      strcat(classpath, java_gameDirName);
      strcat(classpath, PathSep );
      strcat(classpath, "q2jgame.zip");
      strcat(classpath, PathsSep );
    }
    else // append "<gamedir>\<q2java_gamepath>;"
    {
      strcat(classpath, java_gameDirName);
      strcat(classpath, PathSep );
      strcat(classpath, q2java_gamepath->string);
      strcat(classpath, PathsSep );
    }
    
    // set the new classpath
    vm_args.classpath = classpath;
    
    // hook up the output function (this could be omitted with no problem)
    vm_args.vfprintf = &jvfprintf;
    
    //
    // Kick the tires and light the fires!
    //
    (*p_JNI_CreateJavaVM)(&java_vm, &java_env, &vm_args);

    if (!java_vm)
    {
      java_error = "Couldn't create a Java Virtual Machine\n";
      return;
    }
    
    // save some key pointers and handles in cvars, in case
    // the DLL is reloaded and we need them back
    //
    sprintf(buffer, "%d", (int)java_vm);
    gi.cvar_forceset("q2java_VMPointer", buffer);
    sprintf(buffer, "%d", (int)java_env);
    gi.cvar_forceset("q2java_EnvPointer", buffer);
  }
  
  debugLog("startJava:: created JVM and environment\n");
  
  // If Java is up and running, let each Q2Java module initialize itself
  // if any one encounters a problem, it sets the "java_error" pointer
  // to a short message indicating what went wrong.  All problems
  // indicated this way are fatal, and will prevent the DLL from
  // functioning properly.

  debugLog("startJava:: initializing Q2Java modules\n");
  
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
    
    // Turn security on, if enabled
    // and the VM was not already started
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
  debugLog("stopJava:: shutting down\n");
  
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
  // Try killing the VM.
  // This will always fail on Win32 and Linux JDK 1.1.5 and lower.
  //
  
  if ((*java_vm)->DestroyJavaVM(java_vm))
    debugLog("Error destroying Java VM\n");
  else
  {
    // We actually destroyed the VM!, really clean things up now.
    closeDLL( hJavaDLL );
    
    gi.cvar_forceset("q2java_DLLHandle", "0");
    gi.cvar_forceset("q2java_VMPointer", "0");
    gi.cvar_forceset("q2java_EnvPointer", "0");
    hJavaDLL = 0;
    java_vm = 0;
    java_env = 0;
    debugLog("JVM Destroyed and Java DLL freed\n");
  }

  debugLog( "stopJava:: all shut down\n");
}
