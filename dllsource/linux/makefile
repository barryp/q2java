#
# Q2Java gamei386.so Makefile for Linux 2.0
#
# Based on original Q2 Makefile - Jan '98 by Zoid <zoid@idsoftware.com>
#
# ELF only
#
# Probably requires GNU make
#

JDK_HOME=/usr/local/java/jdk117_v1a/

ARCH=i386
CC=gcc
BASE_CFLAGS=-I$(JDK_HOME)/include -I$(JDK_HOME)/include/genunix

#use these cflags to optimize it
CFLAGS=$(BASE_CFLAGS) -m486 -O6 -ffast-math -funroll-loops \
	-fomit-frame-pointer -fexpensive-optimizations -malign-loops=2 \
	-malign-jumps=2 -malign-functions=2
#use these when debugging 
#CFLAGS=$(BASE_CFLAGS) -g

LDFLAGS=-L$(JDK_HOME)/lib/i386/native_threads/ -ljava -ldl -lm -lc
SHLIBEXT=so
SHLIBCFLAGS=-fPIC
SHLIBLDFLAGS=-shared $(LDFLAGS)

DO_CC=$(CC) $(CFLAGS) $(SHLIBCFLAGS) -o $@ -c $<

#############################################################################
# SETUP AND BUILD
# GAME
#############################################################################

.c.o:
	$(DO_CC)

GAME_OBJS = \
	game.o globals.o javalink_generic.o misc.o player.o \
	q2java_CVar.o q2java_ConsoleOutputStream.o q2java_Engine.o \
	q2java_NativeEntity.o properties.o q2string.o

game$(ARCH).$(SHLIBEXT) : $(GAME_OBJS)
	$(CC) $(CFLAGS) $(SHLIBLDFLAGS) -o $@ $(GAME_OBJS)


#############################################################################
# MISC
#############################################################################

clean:
	-rm -f $(GAME_OBJS)

depend:
	gcc -MM $(GAME_OBJS:.o=.c)

