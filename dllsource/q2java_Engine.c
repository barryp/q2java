#include "globals.h"  // to get at the game_import_t structure
#include "q2java_Engine.h"
#include <stdlib.h> // for qsort()

// headers required by performance counter functions.
#ifdef _WIN32
    #include <windows.h>
#endif
#include <time.h>

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

#define INDEX_IMAGE 9
#define INDEX_MODEL 10
#define INDEX_SOUND 11

// handle to Engine class
static jclass class_Engine;
static jmethodID method_Engine_startLevel;
static jmethodID method_Engine_runDeferred;


static JNINativeMethod Engine_methods[] = 
    {
    {"addCommandString0","(Ljava/lang/String;)V",    Java_q2java_Engine_addCommandString0},
    {"bprint0",      "(ILjava/lang/String;)V",       Java_q2java_Engine_bprint0},
    {"debugGraph0",  "(FI)V",                        Java_q2java_Engine_debugGraph0},
    {"debugLog0",    "(Ljava/lang/String;)V",        Java_q2java_Engine_debugLog0},
    {"dprint0",      "(Ljava/lang/String;)V",        Java_q2java_Engine_dprint0},
    {"error0",       "(Ljava/lang/String;)V",        Java_q2java_Engine_error0},
    {"getArgc0",     "()I",                          Java_q2java_Engine_getArgc0},
    {"getArgv0",     "(I)Ljava/lang/String;",        Java_q2java_Engine_getArgv0},
    {"getArgs0",     "()Ljava/lang/String;",         Java_q2java_Engine_getArgs0},
    {"getBoxEntities0", "(FFFFFFI)[Lq2java/NativeEntity;",  Java_q2java_Engine_getBoxEntities0},
    {"getGamePath",     "()Ljava/lang/String;",     Java_q2java_Engine_getGamePath},
    {"getPointContents0",   "(FFF)I",               Java_q2java_Engine_getPointContents0},
    {"getRadiusEntities0",  "(FFFFIZZ)[Lq2java/NativeEntity;", Java_q2java_Engine_getRadiusEntities0},
    {"getIndex0",     "(ILjava/lang/String;)I",Java_q2java_Engine_getIndex0},
    {"inP0",                "(FFFFFFI)Z",           Java_q2java_Engine_inP0},
    {"setAreaPortalState0",  "(IZ)V",                Java_q2java_Engine_setAreaPortalState0},
    {"setConfigString0",     "(ILjava/lang/String;)V",       Java_q2java_Engine_setConfigString0},
    {"areasConnected0",      "(II)Z",                Java_q2java_Engine_areasConnected0},
    {"trace0",      "(FFFFFFFFFFFFLq2java/NativeEntity;II)Lq2java/TraceResults;",   Java_q2java_Engine_trace0},
    {"write0",      "(Ljava/lang/Object;FFFII)V",   Java_q2java_Engine_write0},
    {"getPerformanceFrequency", "()J",              Java_q2java_Engine_getPerformanceFrequency},
    {"getPerformanceCounter",   "()J",              Java_q2java_Engine_getPerformanceCounter}
    };


void Engine_javaInit()
    {
    javalink_debug("[C   ] Engine_javaInit() starting\n");

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

    method_Engine_startLevel = (*java_env)->GetStaticMethodID(java_env, class_Engine, "startLevel", "()V");
    if (CHECK_EXCEPTION())
        {
        java_error = "Couldn't locate q2java.Engine.startLevel()\n";
        return;
        }

    method_Engine_runDeferred = (*java_env)->GetStaticMethodID(java_env, class_Engine, "runDeferred", "()V");
    if (CHECK_EXCEPTION())
        {
        java_error = "Couldn't locate q2java.Engine.runDeferred()\n";
        return;
        }

    javalink_debug("[C   ] Engine_javaInit() finished\n");
    }


void Engine_javaDetach()
    {
    if (class_Engine)
        (*java_env)->UnregisterNatives(java_env, class_Engine); 

    (*java_env)->DeleteLocalRef(java_env, class_Engine);
    }


static void JNICALL Java_q2java_Engine_dprint0(JNIEnv *env, jclass cls, jstring js)
    {
    char *str;

    if (js == NULL)
        return;

    str = convertJavaString(js);
    q2java_gi.dprintf("%s", str); 
    javalink_debug("%s", str);
    q2java_gi.TagFree(str);
    }


static void JNICALL Java_q2java_Engine_bprint0(JNIEnv *env, jclass cls, jint printlevel, jstring js)
    {
    char *str;

    if (js == NULL)
        return;

    str = convertJavaString(js);
    q2java_gi.bprintf(printlevel, "%s", str); 
    q2java_gi.TagFree(str);
    }


static void JNICALL Java_q2java_Engine_setConfigString0(JNIEnv *env, jclass cls, jint num, jstring js)
    {
    char *s;

    if (js == NULL)
        return;

    s = (char *)((*env)->GetStringUTFChars(env, js, 0));
    q2java_gi.configstring(num, s);
    (*env)->ReleaseStringUTFChars(env, js, s);
    }


static void JNICALL Java_q2java_Engine_error0(JNIEnv *env, jclass cls, jstring js)
    {
    char *s;

    if (js == NULL)
        q2java_gi.error(NULL);
    else
        {
        s = (char *)((*env)->GetStringUTFChars(env, js, 0));
        q2java_gi.error(s);
        (*env)->ReleaseStringUTFChars(env, js, s);
        }
    }


static jint JNICALL Java_q2java_Engine_getIndex0(JNIEnv *env, jclass cls, jint indexType, jstring jname)
    {
    char *name;
    int result;

    if (jname == NULL)
        return 0;

    name = (char *)((*env)->GetStringUTFChars(env, jname, 0));

    switch (indexType)
        {
        case INDEX_MODEL:
            result = q2java_gi.modelindex(name);
            break;

        case INDEX_IMAGE:
            result = q2java_gi.imageindex(name);
            break;

        case INDEX_SOUND:
            result = q2java_gi.soundindex(name);
            break;

        default:
            result = 0;
        }

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
        passEnt = q2java_ge.edicts + Entity_get_fEntityIndex(jpassEnt);

    start[0] = startx;
    start[1] = starty;
    start[2] = startz;

    end[0] = endx;
    end[1] = endy;
    end[2] = endz;

    if (!useMinMax)
        return newTraceResults(q2java_gi.trace(start, NULL, NULL, end, passEnt, contentMask));
    else        
        {
        mins[0] = minsx;
        mins[1] = minsy;
        mins[2] = minsz;

        maxs[0] = maxsx;
        maxs[1] = maxsy;
        maxs[2] = maxsz;

        return newTraceResults(q2java_gi.trace(start, mins, maxs, end, passEnt, contentMask));
        }
    }


static jint JNICALL Java_q2java_Engine_getPointContents0(JNIEnv *env, jclass cls, jfloat x, jfloat y, jfloat z)
    {
    vec3_t v;
    v[0] = x;
    v[1] = y;
    v[2] = z;
    return q2java_gi.pointcontents(v);
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
        case CALL_INPHS: return (jboolean) q2java_gi.inPHS(p1, p2);
        case CALL_INPVS: return (jboolean) q2java_gi.inPVS(p1, p2);
        default: return 0;
        }
    }


static void JNICALL Java_q2java_Engine_setAreaPortalState0(JNIEnv *env, jclass cls, jint portalnum, jboolean open)
    {
    q2java_gi.SetAreaPortalState(portalnum, open);
    }


static jboolean JNICALL Java_q2java_Engine_areasConnected0(JNIEnv *env, jclass cls, jint area1, jint area2)
    {
    return (jboolean) q2java_gi.AreasConnected(area1, area2);
    }


static jobjectArray JNICALL Java_q2java_Engine_getBoxEntities0(JNIEnv *env, jclass cls, 
    jfloat minsx, jfloat minsy, jfloat minsz, 
    jfloat maxsx, jfloat maxsy, jfloat maxsz, jint areaType)
    {
    vec3_t mins;
    vec3_t maxs;
    int count;
    edict_t **list = q2java_gi.TagMalloc(MAXTOUCH * sizeof(edict_t *), TAG_GAME);
    jobjectArray result;

    mins[0] = minsx;
    mins[1] = minsy;
    mins[2] = minsz;

    maxs[0] = maxsx;
    maxs[1] = maxsy;
    maxs[2] = maxsz;

    count = q2java_gi.BoxEdicts(mins, maxs, list, MAXTOUCH, areaType);

    result = Entity_createArray(list, count);
    q2java_gi.TagFree(list);

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
            q2java_gi.multicast(v, c);
            break;

        case CALL_UNICAST:
            index = Entity_get_fEntityIndex(obj);
            if ((index >= 0) && (index < q2java_ge.max_edicts))
                q2java_gi.unicast(q2java_ge.edicts + index, c);
            break;

        case CALL_WRITECHAR:
            q2java_gi.WriteChar(c);
            break;

        case CALL_WRITEBYTE:
            q2java_gi.WriteByte(c);
            break;

        case CALL_WRITESHORT:
            q2java_gi.WriteShort(c);
            break;

        case CALL_WRITELONG:
            q2java_gi.WriteLong(c);
            break;

        case CALL_WRITEFLOAT:
            q2java_gi.WriteFloat(x);
            break;

        case CALL_WRITEANGLE:
            q2java_gi.WriteAngle(x);
            break;

        case CALL_WRITESTRING:
            s = (char *)((*env)->GetStringUTFChars(env, obj, 0));
            q2java_gi.WriteString(s);
            (*env)->ReleaseStringUTFChars(env, obj, s);
            break;

        case CALL_WRITEPOSITION:
            v[0] = x;
            v[1] = y;
            v[2] = z;
            q2java_gi.WritePosition(v);
            break;

        case CALL_WRITEDIR:
            v[0] = x;
            v[1] = y;
            v[2] = z;
            q2java_gi.WriteDir(v);
            break;
        }
    }


static jint JNICALL Java_q2java_Engine_getArgc0(JNIEnv *env, jclass cls)
    {
    return q2java_gi.argc();   
    }


static jstring JNICALL Java_q2java_Engine_getArgv0(JNIEnv *env, jclass cls, jint n)
    {
    return (*env)->NewStringUTF(env, q2java_gi.argv(n));
    }


static jstring JNICALL Java_q2java_Engine_getArgs0(JNIEnv *env, jclass cls)
    {
    return (*env)->NewStringUTF(env, q2java_gi.args());
    }


static void JNICALL Java_q2java_Engine_addCommandString0(JNIEnv *env, jclass cls, jstring js)
    {
    char *s;

    if (js == NULL)
        return;

    s = (char *)((*env)->GetStringUTFChars(env, js, 0));
    q2java_gi.AddCommandString(s);
    (*env)->ReleaseStringUTFChars(env, js, s);
    }


static void JNICALL Java_q2java_Engine_debugGraph0(JNIEnv *env, jclass cls, jfloat value, jint color)
    {
    q2java_gi.DebugGraph(value, color);
    }


// ------ helper function not based on Quake II Game interface

static jstring JNICALL Java_q2java_Engine_getGamePath(JNIEnv *env, jclass cls)
    {
    return (*env)->NewStringUTF(env, javalink_gameDirName);
    }


static void JNICALL Java_q2java_Engine_debugLog0(JNIEnv *env, jclass cls, jstring js)
    {
    char *s;

    if (js == NULL)
        return;

    s = (char *)((*env)->GetStringUTFChars(env, js, 0));
    javalink_debug("[Java] %s\n", s);
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
    edict_t **list;

    if (onlyPlayers)
        max = global_maxClients + 1;
    else
        max = q2java_ge.num_edicts;

	list = q2java_gi.TagMalloc(max * sizeof(edict_t *), TAG_GAME);

    radiusSquared = radius * radius;
    count = 0;
    for (i = 1; i < max; i++)
        {
        check = q2java_ge.edicts + i;

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
    q2java_gi.TagFree(list);

    return result;
    }


static jlong JNICALL Java_q2java_Engine_getPerformanceFrequency(JNIEnv *env , jclass cls)
    {
#ifdef _WIN32
    LARGE_INTEGER freq;
    if (QueryPerformanceFrequency(&freq))
        return freq.QuadPart;
    else
        return CLOCKS_PER_SEC;
#else
    return CLOCKS_PER_SEC;
#endif
    }


static jlong JNICALL Java_q2java_Engine_getPerformanceCounter(JNIEnv *env , jclass cls)
    {
#ifdef _WIN32
    LARGE_INTEGER count;
    if (QueryPerformanceCounter(&count))
        return count.QuadPart;
    else
        return clock();
#else
    return clock();
#endif
    }

/**
 * Let the Engine know a new level is starting
 */
void Engine_startLevel()
    {
    (*java_env)->CallStaticVoidMethod(java_env, class_Engine, method_Engine_startLevel);
    CHECK_EXCEPTION();
    }

/**
 * Give the Engine a chance to think
 */
void Engine_runDeferred()
    {
    (*java_env)->CallStaticVoidMethod(java_env, class_Engine, method_Engine_runDeferred);
    CHECK_EXCEPTION();
    }
