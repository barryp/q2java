Linux Notes

Q2Java v0.7 is the first release that -should- support Linux.  Files 
that Linux users should be interested in include:

    q2java/q2java_blackdown 
    
        A shell script for invoking Quake2 and Q2Java using the Blackdown 
        JVM. You'll need to edit this and change a few key paths to
        reflect your particular setup.  You might also want to copy or
        move this into a directory in your PATH so you can more easily
        run Q2Java
        
    q2java/dllsource/linux/makefile
    
        A makefile for compiling a gamei386.so that uses the Blackdown
        JVM. Copy or move this into q2java/dllsource, modify it to reflect
        the location of the Blackdown JDK on your machine,  and type
        "make". 

        Copy or move the resulting gamei386.so to the q2java directory
        to run it.  
     
    q2java/dllsource/linux/makefile-kaffe

        A first stab at a gamei386.so that uses the Kaffe VM.  As above,
        copy or move it into q2java/dllsource, modify slightly to 
        reflect where Kaffe lives on your system, and type: 

          "make -f makefile-kaffe"

        Copy or move the resulting gamei386.so up to the q2java directory.

    q2java/q2java_kaffe

        A shell script for invoking the Kaffe version of gamei386.so.

        Unfortunately, as of this writing, the Kaffe version only works in
        dedicated mode, must be started with security level 0, seems to 
        redirect the Q2 console output into never-never land, and quits
        when players try to connect.  Aside from those minor glitches, it
        seems to work somewhat :)  Who knows though, Transvirtual is still
        working on Kaffe, and someday it might start working better.

   
If you run a Q2Java server and try to connect to it from a Q2 client on
the same box, you might need to start the client with the same invocation
script used to start the server (something to do with preloading
libraries).

People connecting to a Linux Q2Java server from other boxes -shouldn't-
need to do anything special.

Most successful testing has been done using the glibc versions of Quake2
3.20 and Blackdown JDK 1.1.7_v1a with the native-threads addon.

I myself am fairly clueless about Linux (as you may suspect from the shell
script and makefile provided), so if you have Linux-specific 
questions, you'd be much better off sending them to the JavaQuake mailing
list (subscribe through majordomo@openquake.org), or to 
Bernd Kreimeier <bk@gamers.org> who's been working with Quake2/Java/Linux
for quite some time.

    Barry
