#include "globals.h"  // to get at the game_import_t structure
#include "q2java_Engine.h"
#include <stdlib.h> // for qsort()

#define CALL_MULTICAST 0
#define CALL_WRITEPOSITION 1
#define CALL_WRITEDIR 2
#define CALL_WRITECHAR 3
#define CALL_WRITEBYTE 4
#define CALL_WRITESHORT 5
#define CALL_WRITELONG 6
#define CALL_WRITEFLOAT 7
#define CALL_WRITEANGLE 8
#define CALL_WRITESTRING 9
#define CALL_UNICAST 10
#define CALL_INPVS 11
#define CALL_INPHS 12


// handle to Engine class
static jclass class_Engine;


static JNINativeMethod Engine_methods[] = 
    {
    {"addCommandString","(Ljava/lang/String;)V",    Java_q2java_Engine_addCommandString},
    {"bprint",      "(ILjava/lang/String;)V",       Java_q2java_Engine_bprint},
    {"debugGraph",  "(FI)V",                        Java_q2java_Engine_debugGraph},
    {"debugLog",    "(Ljava/lang/String;)V",        Java_q2java_Engine_debugLog},
    {"dprint",      "(Ljava/lang/String;)V",        Java_q2java_Engine_dprint},
    {"error",       "(Ljava/lang/String;)V",        Java_q2java_Engine_error},
    {"getArgc",     "()I",                          Java_q2java_Engine_getArgc},
    {"getArgv",     "(I)Ljava/lang/String;",        Java_q2java_Engine_getArgv},
    {"getArgs",     "()Ljava/lang/String;",         Java_q2java_Engine_getArgs},
    {"getBoxEntities0", "(FFFFFFI)[Lq2java/NativeEntity;",  Java_q2java_Engine_getBoxEntities0},
    {"getGamePath",     "()Ljava/lang/String;",     Java_q2java_Engine_getGamePath},
    {"getImageIndex",   "(Ljava/lang/String;)I",    Java_q2java_Engine_getImageIndex},
    {"getModelIndex",   "(Ljava/lang/String;)I",    Java_q2java_Engine_getModelIndex},
    {"getPointContents0",   "(FFF)I",               Java_q2java_Engine_getPointContents0},
    {"getRadiusEntities0",  "(FFFFIZZ)[Lq2java/NativeEntity;", Java_q2java_Engine_getRadiusEntities0},
    {"getSoundIndex",       "(Ljava/lang/String;)I",Java_q2java_Engine_getSoundIndex},
    {"inP0",                "(FFFFFFI)Z",           Java_q2java_Engine_inP0},
    {"setAreaPortalState",  "(IZ)V",                Java_q2java_Engine_setAreaPortalState},
    {"setConfigString",     "(ILjava/lang/String;)V",       Java_q2java_Engine_setConfigString},
    {"areasConnected",      "(II)Z",                Java_q2java_Engine_areasConnected},
    {"trace0",      "(FFFFFFFFFFFFLq2java/NativeEntity;II)Lq2java/TraceResults;",   Java_q2java_Engine_trace0},
    {"write0",      "(Ljava/lang/Object;FFFII)V",   Java_q2java_Engine_write0}
    };


void Engine_javaInit()
    {
    class_Engine = (*java_env)->FindClass(java_env, "q2java/Engine");
    if(CHECK_EXCEPTION() || !class_Engine)
        {
        java_error = "Couldn't find q2java.Engine\n";
        return;
        }

    (*java_env)->RegisterNatives(java_env, class_Engine, Engine_methods, sizeof(Engine_methods)/sizeof(Engine_methods[0]));
    if (CHECK_EXCEPTION())
        {
        java_error = "Couldn't register native methods for q2java.Engine\n";
        return;
        }
    }

void Engine_javaFinalize()
    {
    if (class_Engine)
        (*java_env)->UnregisterNatives(java_env, class_Engine); 

    (*java_env)->DeleteLocalRef(java_env, class_Engine);
    }





static void JNICALL Java_q2java_Engine_dprint(JNIEnv *env, jclass cls, jstring s)
    {
    const char *str;

    str = (*env)->GetStringUTFChars(env, s, 0);
    gi.dprintf("%s", str); 
    debugLog(str);
    (*env)->ReleaseStringUTFChars(env, s, str);
    }

static void JNICALL Java_q2java_Engine_bprint(JNIEnv *env, jclass cls, jint printlevel, jstring s)
    {
    const char *str;

    str = (*env)->GetStringUTFChars(env, s, 0);
    gi.bprintf(printlevel, "%s", str); 
    (*env)->ReleaseStringUTFChars(env, s, str);
    }

static void JNICALL Java_q2java_Engine_setConfigString(JNIEnv *env, jclass cls, jint num, jstring js)
    {
    char *s;

    s = (char *)((*env)->GetStringUTFChars(env, js, 0));
    gi.configstring(num, s);
    (*env)->ReleaseStringUTFChars(env, js, s);
    }


static void JNICALL Java_q2java_Engine_error(JNIEnv *env, jclass cls, jstring js)
    {
    char *s;

    s = (char *)((*env)->GetStringUTFChars(env, js, 0));
    gi.error(s);
    (*env)->ReleaseStringUTFChars(env, js, s);
    }


static jint JNICALL Java_q2java_Engine_getModelIndex(JNIEnv *env, jclass cls, jstring jname)
    {
    char *name;
    int result;

    name = (char *)((*env)->GetStringUTFChars(env, jname, 0));
    result = gi.modelindex(name);
    (*env)->ReleaseStringUTFChars(env, jname, name);
    return result;
    }

static jint JNICALL Java_q2java_Engine_getSoundIndex(JNIEnv *env, jclass cls, jstring jname)
    {
    char *name;
    int result;

    name = (char *)((*env)->GetStringUTFChars(env, jname, 0));
    result = gi.soundindex(name);
    (*env)->ReleaseStringUTFChars(env, jname, name);
    return result;
    }

static jint JNICALL Java_q2java_Engine_getImageIndex(JNIEnv *env, jclass cls, jstring jname)
    {
    char *name;
    int result;

    name = (char *)((*env)->GetStringUTFChars(env, jname, 0));
    result = gi.imageindex(name);
    (*env)->ReleaseStringUTFChars(env, jname, name);
    return result;
    }


static jobject JNICALL Java_q2java_Engine_trace0(JNIEnv *env, jclass cls, 
    jfloat startx, jfloat starty, jfloat startz, 
    jfloat minsx, jfloat minsy, jfloat minsz, 
    jfloat maxsx, jfloat maxsy, jfloat maxsz, 
    jfloat endx, jfloat endy, jfloat endz, 
    jobject jpassEnt, jint contentMask, jint useMinMax)
    {
    vec3_t start;
    vec3_t mins;
    vec3_t maxs;
    vec3_t end;
    edict_t *passEnt;

    if (jpassEnt == NULL)
        passEnt = NULL;
    else
        passEnt = ge.edicts + Entity_get_fEntityIndex(jpassEnt);

    start[0] = startx;
    start[1] = starty;
    start[2] = startz;

    end[0] = endx;
    end[1] = endy;
    end[2] = endz;

    if (!useMinMax)
        return newTraceResults(gi.trace(start, NULL, NULL, end, passEnt, contentMask));
    else        
        {
        mins[0] = minsx;
        mins[1] = minsy;
        mins[2] = minsz;

        maxs[0] = maxsx;
        maxs[1] = maxsy;
        maxs[2] = maxsz;

        return newTraceResults(gi.trace(start, mins, maxs, end, passEnt, contentMask));
        }
    }




static jint JNICALL Java_q2java_Engine_getPointContents0(JNIEnv *env, jclass cls, jfloat x, jfloat y, jfloat z)
    {
    vec3_t v;
    v[0] = x;
    v[1] = y;
    v[2] = z;
    return gi.pointcontents(v);
    }


static jboolean JNICALL Java_q2java_Engine_inP0(JNIEnv *env, jclass cls, jfloat x1, jfloat y1, jfloat z1, jfloat x2, jfloat y2, jfloat z2, jint calltype)
    {
    vec3_t p1, p2;
    p1[0] = x1;
    p1[1] = y1;
    p1[2] = z1;
    p2[0] = x2;
    p2[1] = y2;
    p2[2] = z2;
    switch (calltype)
        {
        case CALL_INPHS: return (jboolean) gi.inPHS(p1, p2);
        case CALL_INPVS: return (jboolean) gi.inPVS(p1, p2);
        default: return 0;
        }
    }


static void JNICALL Java_q2java_Engine_setAreaPortalState(JNIEnv *env, jclass cls, jint portalnum, jboolean open)
    {
    gi.SetAreaPortalState(portalnum, open);
    }


static jboolean JNICALL Java_q2java_Engine_areasConnected(JNIEnv *env, jclass cls, jint area1, jint area2)
    {
    return (jboolean) gi.AreasConnected(area1, area2);
    }


static jobjectArray JNICALL Java_q2java_Engine_getBoxEntities0(JNIEnv *env, jclass cls, 
    jfloat minsx, jfloat minsy, jfloat minsz, 
    jfloat maxsx, jfloat maxsy, jfloat maxsz, jint areaType)
    {
    vec3_t mins;
    vec3_t maxs;
    int count;
    edict_t **list = gi.TagMalloc(MAXTOUCH * sizeof(edict_t *), TAG_GAME);
    jobjectArray result;

    mins[0] = minsx;
    mins[1] = minsy;
    mins[2] = minsz;

    maxs[0] = maxsx;
    maxs[1] = maxsy;
    maxs[2] = maxsz;

    count = gi.BoxEdicts(mins, maxs, list, MAXTOUCH, areaType);

    result = Entity_createArray(list, count);
    gi.TagFree(list);

    return result;
    }


static void JNICALL Java_q2java_Engine_write0(JNIEnv *env, jclass cls, jobject obj, jfloat x, jfloat y, jfloat z, jint c, jint calltype)
    {
    vec3_t v;
    char *s;
    int index;

    switch (calltype)
        {
        case CALL_MULTICAST:
            v[0] = x;
            v[1] = y;
            v[2] = z;
            gi.multicast(v, c);
            break;

        case CALL_UNICAST:
            index = Entity_get_fEntityIndex(obj);
            if ((index >= 0) && (index < ge.max_edicts))
                gi.unicast(ge.edicts + index, c);
            break;

        case CALL_WRITECHAR:
            gi.WriteChar(c);
            break;

        case CALL_WRITEBYTE:
            gi.WriteByte(c);
            break;

        case CALL_WRITESHORT:
            gi.WriteShort(c);
            break;

        case CALL_WRITELONG:
            gi.WriteLong(c);
            break;

        case CALL_WRITEFLOAT:
            gi.WriteFloat(x);
            break;

        case CALL_WRITEANGLE:
            gi.WriteAngle(x);
            break;

        case CALL_WRITESTRING:
            s = (char *)((*env)->GetStringUTFChars(env, obj, 0));
            gi.WriteString(s);
            (*env)->ReleaseStringUTFChars(env, obj, s);
            break;

        case CALL_WRITEPOSITION:
            v[0] = x;
            v[1] = y;
            v[2] = z;
            gi.WritePosition(v);
            break;

        case CALL_WRITEDIR:
            v[0] = x;
            v[1] = y;
            v[2] = z;
            gi.WriteDir(v);
            break;
        }
    }

static jint JNICALL Java_q2java_Engine_getArgc(JNIEnv *env, jclass cls)
    {
    return gi.argc();   
    }

static jstring JNICALL Java_q2java_Engine_getArgv(JNIEnv *env, jclass cls, jint n)
    {
    return (*env)->NewStringUTF(env, gi.argv(n));
    }

static jstring JNICALL Java_q2java_Engine_getArgs(JNIEnv *env, jclass cls)
    {
    return (*env)->NewStringUTF(env, gi.args());
    }

static void JNICALL Java_q2java_Engine_addCommandString(JNIEnv *env, jclass cls, jstring js)
    {
    char *s;

    s = (char *)((*env)->GetStringUTFChars(env, js, 0));
    gi.AddCommandString(s);
    (*env)->ReleaseStringUTFChars(env, js, s);
    }

static void JNICALL Java_q2java_Engine_debugGraph(JNIEnv *env, jclass cls, jfloat value, jint color)
    {
    gi.DebugGraph(value, color);
    }


// ------ helper function not based on Quake II Game interface

static jstring JNICALL Java_q2java_Engine_getGamePath(JNIEnv *env, jclass cls)
    {
    return (*env)->NewStringUTF(env, java_gameDirName);
    }


static void JNICALL Java_q2java_Engine_debugLog(JNIEnv *env, jclass cls, jstring js)
    {
    char *s;

    s = (char *)((*env)->GetStringUTFChars(env, js, 0));
    debugLog("%s\n", s);
    (*env)->ReleaseStringUTFChars(env, js, s);
    }



static int radiusSort(const void *e1, const void *e2)
    {
    edict_t *a;
    edict_t *b;

    a = *((edict_t **) e1);
    b = *((edict_t **) e2);

//  return (int) (a->freetime - b->freetime); // not sure if this could overflow when converting to int

    if (a->freetime == b->freetime)
        return 0;
    if (a->freetime < b->freetime)
        return 1;
    else
        return -1;
    }

jobjectArray JNICALL Java_q2java_Engine_getRadiusEntities0
  (JNIEnv *env, jclass cls, jfloat x, jfloat y, jfloat z, jfloat radius, jint ignoreIndex, jboolean onlyPlayers, jboolean sortResults)
    {
    jobjectArray result;
    float radiusSquared;
    float dx, dy, dz, distSquared;
    int count;
    int max;
    int i;
    edict_t *check;

    edict_t **list = gi.TagMalloc(MAXTOUCH * sizeof(edict_t *), TAG_GAME);

    radiusSquared = radius * radius;

    if (onlyPlayers)
        max = global_maxClients + 1;
    else
        max = ge.num_edicts;

    count = 0;
    for (i = 1; i < max; i++)
        {
        check = ge.edicts + i;

        if (!(check->inuse))
            continue;

        // no sense checking entities the clients can't see
        if (check->svflags & SVF_NOCLIENT)
            continue;

        if (i == ignoreIndex)
            continue;

        dx = check->s.origin[0] - x;
        dy = check->s.origin[1] - y;
        dz = check->s.origin[2] - z;

        distSquared = (dx * dx) + (dy * dy) + (dz * dz);

        if (distSquared <= radiusSquared)
            {
            check->freetime = distSquared;  // a bit of a dirty trick, reusing the freetime field
            list[count++] = check;
            }
        }

    if (sortResults)
        qsort(list, count, sizeof(edict_t *), &radiusSort);

    result = Entity_createArray(list, count);
    gi.TagFree(list);

    return result;
    }