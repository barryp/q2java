#!/bin/sh
#
# Sample shell script for running Q2Java using an older Blackdown JVM
#

JAVA_HOME="/usr/local/java/jdk117_v1a"
QUAKE2_HOME="/usr/local/games/quake2"
#QUAKE2_PARAMS="+set vid_ref softx"

# Preload JVM DLL, call quake2 next.
#
# Quake2 is linked against libdl.so,
#  but not linked against libjava.so. 
#
# It does use dlopen("gamei386.so"), 
#  and gamei386.so is linked
#  against libjava.so. 
#
# gamei386.so does invocation
#  of a JVM, Quake2 is not Java aware.
#
# Without the preload, this fails
#  on every RH 5.x and Debian 2.x tried so far.


LD_PRELOAD="libjava.so"
export LD_PRELOAD

LD_LIBRARY_PATH=$JAVA_HOME"/lib/i386/native_threads"
export LD_LIBRARY_PATH

Q2JAVA_CLASSPATH=$JAVA_HOME"/lib/classes.zip:"$QUAKE2_HOME"/q2java/classes"
export Q2JAVA_CLASSPATH

cd $QUAKE2_HOME
./quake2 +set game q2java $QUAKE2_PARAMS $@
