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

        As of version 0.9.1, Q2Java and Kaffe are getting along somewhat - 
        be sure to get the latest Kaffe through CVS.

        The good news is that you can run around, fire weapons, ride plats,
        etc.  The telnet server even works!
        
        However, there are still problems.  For one thing, some of the 
        event-delegation code causes bogus CloneNotSupportExceptions to
        be thrown.  In order to get Kaffe started you'll need to at least
        modify q2java.baseq2.BaseQ2 so that it's declared to implement 
        "Cloneable" and recompile.  Other classes will have problems too,
        but the BaseQ2 is critical to get the game at least started.  This
        must be some sort of problem with reflection in Kaffe, and hopefully
        it will be fixed eventually.
        
        Another problem is that Kaffe lists a known bug in formatting dates,
        you'll have to modify q2java.baseq2.WelcomeMessage to not try
        and print the current date in the welcome message.

        Also, security must be turned off - Kaffe doesn't like the Q2Java
        Security Manager.
        
        Other problems include: no HUD, several "malformed property: `" 
        errors, and general weirdness when accessing ResourceBundles.  When
        running under Kaffe, the game tends to crash when changing maps - 
        no idea why.  Overall it runs but is just not stable yet - stick
        with the Blackdown JDK when running a server to really play on.                       
   
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
questions, you'd be much better off sending them to the quake-dev mailing
list (subscribe through http://www.planetquake.com/q2java/discussion.html), 
or to Bernd Kreimeier <bk@gamers.org> who's been working with Quake2/Java/Linux
for quite some time.

    Barry
