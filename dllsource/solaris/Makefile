SRCS = game.c globals.c misc.c player.c javalink_Solaris.c q2java_ConsoleOutputStream.c q2java_CVar.c q2java_Engine.c q2java_NativeEntity.c

OBJS = $(SRCS:.c=.o)
LIB = gamesparc.so

TARCONTENTS = $(LIB) Makefile javalink_Solaris.c readme.solaris warnings.txt
VERSION = 056

# CFLAGS explanation:
# -G Make a shared library
# -xCC Allow C++ comments
# -o Output file name
# -I Include the JNI headers
# -L Search path for link
# -l Libraries to include for link
CFLAGS = -xCC -I$(JDK_HOME)/include -I$(JDK_HOME)/include/solaris -L$(JDK_HOME)/lib/sparc/green_threads -ljava

LDFLAGS = -G

all: $(LIB)

$(LIB): $(OBJS)
	$(LINK.c) -o $@ $(OBJS)

clean:
	rm -f $(OBJS) $(LIB) solaris*.tar

install:
	mv $(LIB) ..

tar:
	tar cvf solaris$(VERSION).tar $(TARCONTENTS)
