Linux Notes

Q2Java v0.7 is the first release that -should- support Linux.  Files 
that Linux users should be interested in include:

    q2java/linux_q2java 
    
        A shell script for invoking Quake2 and Q2Java. You'll need
        to edit this and change a few key paths to reflect your
        particular setup.  You might also want to copy or move
        this into a directory in your PATH so you can more easily
        run Q2Java
        
    q2java/dllsource/linux/makefile
    
        A makefile for recompiling the gamei386.so.  Copy or move
        this into q2java/dllsource and type "make"  Copy or
        move the resulting gamei386.so to the q2java directory
        to run it.  
        
The exact requirements for Linux are a bit foggy yet...I've run it myself
in single-player mode with a RedHat 5.1 installation, but multiplayer
refused to work.  Bernd seemed to have better luck with multiplayer under
Debian 2.1 something, so your milage may vary.                

In both cases we used the glibc versions of Quake2 3.20 and Blackdown
JDK 1.1.7_v1a with the native-threads addon.

I myself am fairly clueless about Linux (as you may suspect from the shell
script and makefile provided), so if you have Linux-specific 
questions, you'd be much better off sending them to the JavaQuake mailing
list (subscribe through majordomo@openquake.org), or to 
Bernd Kreimeier <bk@gamers.org> who's been working with Quake2/Java/Linux
for quite some time.

    Barry