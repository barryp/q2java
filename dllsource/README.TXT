Q2Java v0.5 Sourcecode
======================

Platform-specific files

    javalink_win32.c    - Invokes the Sun Win32 JVM
    javalink_Solaris.c  - Invokes the Sun Solaris/SPARC JVM
    javalink_linux.c    - Invokes a Linux JVM by implicitly loading the .so
    javalink_linux_dl.c - Invokes a Linux JVM by explicitly loading the .so
    win32dll.def        - Win32 DLL Definition file

Compiler-specific files

    win32-vcpp5\*       - Microsoft Visual C++ 5.0 workspace and project files
                          the target of this project (relative to
                          <quake2>\q2java\dllsource\win32-vcpp5) is
                          "..\..\gamex86.dll", which works out to be
                          <quake2>\q2java\gamex86.dll
                          
    linux\*             - Makefiles for Linux
    
    solaris\*           - Makefiles and notes for Solaris                          
