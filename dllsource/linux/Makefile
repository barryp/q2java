# ---------------------------------------------------------------
# 
# $Id:$
#
# Copyright (C) 1998 by "Difference Engine"
#
# This program is available for distribution and/or modification
# only under the terms of the Difference Engine License (DEL) as
# published by Difference Engine. All rights reserved.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# Difference Engine License for more details.
#
# DESCRIPTION: "Q2 Java" Linux Makefile.
#
# $Log:$
#
# ---------------------------------------------------------------


# ---------------------------------------------------------------
# Basics.
PACKAGE=	q2java
GAME_VER=	v3_14
Q2JAVA_VER=	v0_3
OS=		LINUX
ARCH=		i386

JDK_HOME=/home/bk/java/jdk/

# ---------------------------------------------------------------
# Archives.
DISTFILE=$(PACKAGE)-$(shell date +%y%m%d)
DIST_W32=$(PACKAGE)-$(shell date +%y%m%d)

# ---------------------------------------------------------------
# Sources.

CMODULES = \
	game			\
	globals			\
	misc			\
	player			\
	javalink_linux

JMODULES = \
	q2java_CVar			\
	q2java_ConsoleOutputStream	\
	q2java_Engine			\
	q2java_NativeEntity



# -----------------------------------------------------------
# Compiler, linker tools.
CC=gcc
JC=javac
JH=javah


# -----------------------------------------------------------
# use these clflags for debugging
#  -Wconversion 
#  -Wmissing-prototypes
#  -Wmissing-declarations
#  -Wimplicit
#
# use these cflags to optimize it
#  -O3         - fastest optimization under linux gcc, 
#                 is identical to -O6
#  -pipe       - do not use temporary files,
#                 instead use pipes between compile
#  -m486       - for i486 architecture (but also runs on i386)
#  -DC_ONLY    - do not compile inline assembly
#                 (its not gcc compatible)
#  -fomit...   - removes redundant pointers in code,
#                 reduces file size.
#  -ffast...   - break ansi/ieee rules, 
#                 produce fastest possible math.
#  -funroll... - unrolls small loops making faster code.
#  -fexpen...  - choose to optimize for speed
#                 when speed/size ratio is low.
#  -malign...  - align code on 16bit boundaries,
#                 makes addressing easier, hence smaller code.
#
DEBUG_FLAGS= 	-g3 -DDEBUG -Wall

PROD_CFLAGS=	-pipe -DC_ONLY -fPIC \
		-m486 -O6 -ffast-math -funroll-loops \
		-fomit-frame-pointer -fexpensive-optimizations \
		-malign-loops=2 -malign-jumps=2 -malign-functions=2

CJFLAGS =	-I$(JDK_HOME)/include -I$(JDK_HOME)/include/genunix
CFLAGS=		-D$(OS) -DGAME_VERSION=$(GAME_VERSION) \
		$(DEBUG_FLAGS)  -I. $(CJFLAGS)


LDFLAGS=#-ldl -lm   -lc_p  # glibc problem
SHLIBEXT=so
SHLIBCFLAGS=-fPIC
SHLIBLDFLAGS=-shared

INSTALL_DIR=../lib

DO_CC=$(CC) $(CFLAGS) $(SHLIBCFLAGS) -o $@ -c $<

# ------------------------------------------------------------------
# RULES

.c.o:
	$(DO_CC)




GAME_HEADER = Quake2.h

# Substitution rules do not like \ linebreaks - blech.
# Modules are listed externally.
GAME_CSRC =  $(JMODULES:=.c) $(CMODULES:=.c)  

# Uses substitution rule.
GAME_OBJS := $(GAME_CSRC:.c=.o)
GAME_HSRC := $(GAME_CSRC:.c=.h)

GAME_SRCS = README MODULES LICENSE ChangeLog Makefile index.html \
	$(GAME_HEADER) $(GAME_HSRC) $(GAME_CSRC)

GAME_WIN32 = game.def game.dsp game.plg

ALL_HSRC = $(GAME_HEADER) $(GAME_HSRC)

# ------------------------------------------------------------------
# TARGETS - SETUP AND BUILD GAME

all: game$(ARCH).$(SHLIBEXT) #install

game$(ARCH).$(SHLIBEXT) : $(GAME_OBJS)
	$(CC) $(CFLAGS) $(SHLIBLDFLAGS) -L$(JDK_HOME)/lib/i586/green_threads/ -ljava -o $@ $(GAME_OBJS)
  
# --------------------------------------------------------------
# MISC


clean:
	-rm -f $(GAME_OBJS) 

distclean: clean
	-rm -f *.~*~ *.so 

depend:
	gcc -MM $(GAME_OBJS:.c=.c)

install: game$(ARCH).$(SHLIBEXT)
	strip game$(ARCH).$(SHLIBEXT)
	cp game$(ARCH).$(SHLIBEXT) $(INSTALL_DIR)

dist: distclean
	rm -f $(DISTFILE).tar $(DISTFILE).tar.gz; \
        GZIP=-9 tar hczvf $(DISTFILE).tar.gz \
	  $(GAME_SRCS) $(GAME_WIN32)

dist32: distclean
	rm -f $(DISTFILE).zip; \
        echo "Warped Space TeC/Q2 Game Library $(DISTFILE)." | \
	zip -l9z $(DISTFILE).zip $(GAME_SRCS) $(GAME_WIN32)


# -----------------------------------------------------------
# Local Variables:
# tab-width: 8
# comment-column: 30
# End:
# ------------------------------------------------------------
