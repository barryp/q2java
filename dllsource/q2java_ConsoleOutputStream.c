#include "globals.h"
#include "q2java_ConsoleOutputStream.h"


// handle to the ConsoleOutputStream class
static jclass class_ConsoleOutputStream;
static jmethodID method_ConsoleOutputStream_setConsole;


static JNINativeMethod ConsoleOutputStream_methods[] = 
    {
    {"write",   "(I)V",     Java_q2java_ConsoleOutputStream_write__I},
    {"write",   "([BII)V",  Java_q2java_ConsoleOutputStream_write___3BII}
    };

void ConsoleOutputStream_javaInit()
    {
    class_ConsoleOutputStream = (*java_env)->FindClass(java_env, "q2java/ConsoleOutputStream");
    if (CHECK_EXCEPTION() || !class_ConsoleOutputStream)
        {
        java_error = "Couldn't find q2java.ConsoleOutputStream\n";
        return;
        }

    (*java_env)->RegisterNatives(java_env, class_ConsoleOutputStream, ConsoleOutputStream_methods, sizeof(ConsoleOutputStream_methods)/sizeof(ConsoleOutputStream_methods[0]));
    if (CHECK_EXCEPTION())
        {
        java_error = "Couldn't register native methods for q2java.ConsoleOutputStream\n";
        return;
        }

    method_ConsoleOutputStream_setConsole = (*java_env)->GetStaticMethodID(java_env, class_ConsoleOutputStream, "setConsole", "()V");
    if (CHECK_EXCEPTION() || !method_ConsoleOutputStream_setConsole)
        {
        java_error = "Couldn't find q2java.ConsoleOutputStream.setConsole()\n";
        return;
        }

    (*java_env)->CallStaticVoidMethod(java_env, class_ConsoleOutputStream, method_ConsoleOutputStream_setConsole);
    if (CHECK_EXCEPTION())
        {
        java_error = "Couldn't redirect System.out and/or System.err to Quake2 Console\n";
        return;
        }
    }

void ConsoleOutputStream_javaDetach()
    {
    if (class_ConsoleOutputStream)
        (*java_env)->UnregisterNatives(java_env, class_ConsoleOutputStream);

    (*java_env)->DeleteLocalRef(java_env, class_ConsoleOutputStream);
    }


static void JNICALL Java_q2java_ConsoleOutputStream_write__I(JNIEnv *env , jobject obj, jint i)
    {
    char ca[2];

    switch (i)
        {
        case '\r': 
            break;

        case '\t': 
            // not the ideal way to handle tabs, but good enough 
            // for printing java Exceptions
            q2java_gi.dprintf("    "); 
            Game_consoleOutput("    ");
            javalink_debug("    "); 
            break; 

        default: 
            ca[0] = (char) i;
            ca[1] = 0;
            q2java_gi.dprintf("%c", i); 
            Game_consoleOutput(ca);
            javalink_debug("%c", i); 
            break;
        }
    }

static void JNICALL Java_q2java_ConsoleOutputStream_write___3BII(JNIEnv *env, jobject obj, jbyteArray jba, jint offset, jint len)
    {
    jboolean isCopy;
    jbyte *p;
    jbyte *q;
    char *r;
    char *s;
    int i;
    int count;

    p = (*env)->GetByteArrayElements(env, jba, &isCopy);
    q = p + offset;

    count = len + 1;
    for (i = 0; i < len; i++, q++)
        {
        if (*q == '\r')
            count--;
        if (*q == '\t')
            count += 3;
        }

    r = s = q2java_gi.TagMalloc(count, TAG_GAME);
    q = p + offset;
    for (i = 0; i < len; i++, q++)
        {
        switch (*q)
            {
            case '\r':
                break;
            case '\t':
                *(r++) = ' ';
                *(r++) = ' ';
                *(r++) = ' ';
                *(r++) = ' ';
                break;
            default:
                *(r++) = *q;
            }
        }
    *r = 0;

    q2java_gi.dprintf("%s", s);
    Game_consoleOutput(s);
    javalink_debug("%s", s);
    q2java_gi.TagFree(s);

    (*env)->ReleaseByteArrayElements(env, jba, p, JNI_ABORT);
    }
