Jinsight Notes
-----------------

Jinsight is a special version of the JDK that contains a VM which collects 
tracing information from a Java program, and a visual tool for analyzing
that data.  Get Jinsight from the Alphaworks website, under:

    http://www.alphaworks.ibm.com/tech/Jinsight
    
There are some problems with Jinsight - it tends to goof up the keyboard
so you can't fire weapons, and can only move around if you turn freelook off
and use your mouse to walk.  But you can still gather some profiling
information, here's what you need to do....


Somewhere in the Java code, add the call:

    Runtime.getRuntime().traceMethodCalls(true);
    
to tell Jinsight when to start gathering data.  
The q2java.baseq2.Player.connect() method is a good place.  Call that
same method with a "false" parameter to turn tracing off.


In the q2java.properties file, tell the game where to find the Jinsight JVM
and tip Q2Java off that it's running Jinsight (so it can avoid a couple JNI
calls that Jinsight doesn't seem to like) with lines like this:

    q2java_vmdll=c:\jinsight1.1\jdk1.1.7-1.1\bin\javai_g.dll
    q2java_jinsight=1

Lastly, open a command-prompt window and type:

    set JINSIGHT=YES
    set JINSIGHT_TRACEFILE_NAME=c:\q2java.trc
    
to tell Jinsight to actually trace, and where to store the trace file.  If don't
set "JINSIGHT=YES", you'll get a ton of stuff displayed on your Q2 console.

Once that's all done, you an run Q2Java (use the same command prompt window that
you set the environment variables in).  Jinsight gathers a tremendous amount
of data, so you might only be able to run around for a minute or so (unless
you're only tracing a small section of code).    

A fancier version of this document is available as:

    http://www.planetquake.com/q2java/docs/jinsight
    
which includes a description of analyzing trace data gathered from Q2Java.

-----------------------------------------------------------------------------
                                                        Barry (4/7/2000)
       