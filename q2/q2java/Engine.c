#include "globals.h"  // to get at the game_import_t structure
#include "javalink.h"  // for some helper functions
#include "Engine.h"


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

// handle to TraceResults class
static jclass class_TraceResults;
static jmethodID method_TraceResults_ctor;

static JNINativeMethod Engine_methods[] = 
	{
	{"dprint",		"(Ljava/lang/String;)V", 		Java_Engine_dprint},
	{"bprint",		"(ILjava/lang/String;)V",		Java_Engine_bprint},
	{"configString","(ILjava/lang/String;)V",		Java_Engine_configString},
	{"error",		"(Ljava/lang/String;)V",		Java_Engine_error},
	{"modelIndex",	"(Ljava/lang/String;)I",		Java_Engine_modelIndex},
	{"soundIndex",	"(Ljava/lang/String;)I",		Java_Engine_soundIndex},
	{"imageIndex",	"(Ljava/lang/String;)I",		Java_Engine_imageIndex},
	{"trace0",		"(FFFFFFFFFFFFLq2java/NativeEntity;I)Lq2java/TraceResults;",	Java_Engine_trace0},
	{"pointContents0",		"(FFF)I",				Java_Engine_pointContents0},
	{"inP0",				"(FFFFFFI)Z",			Java_Engine_inP0},
	{"setAreaPortalState",	"(IZ)V",				Java_Engine_setAreaPortalState},
	{"areasConnected",		"(II)Z",				Java_Engine_areasConnected},
	{"boxEntities0",		"(FFFFFFII)[Lq2java/NativeEntity;",	Java_Engine_boxEntities0},
	{"write0",		"(Ljava/lang/Object;FFFII)V",	Java_Engine_write0},
	{"argc",		"()I", 							Java_Engine_argc},
	{"argv",		"(I)Ljava/lang/String;",		Java_Engine_argv},
	{"args",		"()Ljava/lang/String;",			Java_Engine_args},
	{"addCommandString",	"(Ljava/lang/String;)V",Java_Engine_addCommandString},
	{"debugGraph",			"(FI)V",				Java_Engine_debugGraph},
	{"getGamePath",			"()Ljava/lang/String;",	Java_Engine_getGamePath}
	};


void Engine_javaInit()
	{
	class_Engine = (*java_env)->FindClass(java_env, "q2java/Engine");
	CHECK_EXCEPTION();
	if (!class_Engine)
		{
		debugLog("Couldn't get Java Engine class\n");
		return;
		}

	(*java_env)->RegisterNatives(java_env, class_Engine, Engine_methods, 20);
	CHECK_EXCEPTION();

	
	class_TraceResults = (*java_env)->FindClass(java_env, "q2java/TraceResults");
	CHECK_EXCEPTION();
	if (!class_TraceResults)
		{
		debugLog("Couldn't get Java TraceResults class\n");
		return;
		}

	method_TraceResults_ctor = (*java_env)->GetMethodID(java_env, class_TraceResults, "<init>", "(ZZFLq2java/Vec3;Lq2java/Vec3;FBBLjava/lang/String;IIILq2java/NativeEntity;)V");
	CHECK_EXCEPTION();
	if (!method_TraceResults_ctor)
		{
		debugLog("Couldn't get Java TraceResults constructor\n");
		return;
		}
	}

void Engine_javaFinalize()
	{
	if (class_Engine)
		(*java_env)->UnregisterNatives(java_env, class_Engine); 
	}

static void JNICALL Java_Engine_dprint(JNIEnv *env, jclass cls, jstring s)
	{
	const char *str;

	str = (*env)->GetStringUTFChars(env, s, 0);
	gi.dprintf("%s", str); 
	debugLog(str);
	(*env)->ReleaseStringUTFChars(env, s, str);
	}

static void JNICALL Java_Engine_bprint(JNIEnv *env, jclass cls, jint printlevel, jstring s)
	{
	const char *str;

	str = (*env)->GetStringUTFChars(env, s, 0);
	gi.bprintf(printlevel, "%s", str); 
	(*env)->ReleaseStringUTFChars(env, s, str);
	}

static void JNICALL Java_Engine_configString(JNIEnv *env, jclass cls, jint num, jstring js)
	{
	char *s;

	s = (char *)((*env)->GetStringUTFChars(env, js, 0));
	gi.configstring(num, s);
	(*env)->ReleaseStringUTFChars(env, js, s);
	}


static void JNICALL Java_Engine_error(JNIEnv *env, jclass cls, jstring js)
	{
	char *s;

	s = (char *)((*env)->GetStringUTFChars(env, js, 0));
	gi.error(s);
	(*env)->ReleaseStringUTFChars(env, js, s);
	}


static jint JNICALL Java_Engine_modelIndex(JNIEnv *env, jclass cls, jstring jname)
	{
	char *name;
	int result;

	name = (char *)((*env)->GetStringUTFChars(env, jname, 0));
	result = gi.modelindex(name);
	(*env)->ReleaseStringUTFChars(env, jname, name);
	return result;
	}

static jint JNICALL Java_Engine_soundIndex(JNIEnv *env, jclass cls, jstring jname)
	{
	char *name;
	int result;

	name = (char *)((*env)->GetStringUTFChars(env, jname, 0));
	result = gi.soundindex(name);
	(*env)->ReleaseStringUTFChars(env, jname, name);
	return result;
	}

static jint JNICALL Java_Engine_imageIndex(JNIEnv *env, jclass cls, jstring jname)
	{
	char *name;
	int result;

	name = (char *)((*env)->GetStringUTFChars(env, jname, 0));
	result = gi.imageindex(name);
	(*env)->ReleaseStringUTFChars(env, jname, name);
	return result;
	}


static jobject JNICALL Java_Engine_trace0(JNIEnv *env, jclass cls, 
	jfloat startx, jfloat starty, jfloat startz, 
	jfloat minsx, jfloat minsy, jfloat minsz, 
	jfloat maxsx, jfloat maxsy, jfloat maxsz, 
	jfloat endx, jfloat endy, jfloat endz, 
	jobject jpassEnt, jint contentMask)
	{
	vec3_t start;
	vec3_t mins;
	vec3_t maxs;
	vec3_t end;
	edict_t *passEnt;

	trace_t result;

	jobject resEndpos;
	jobject resPlaneNormal;
	jstring resSurfaceName;
	int resSurfaceFlags;
	int resSurfaceValue;
	jobject resEnt;

	start[0] = startx;
	start[1] = starty;
	start[2] = startz;

	mins[0] = minsx;
	mins[1] = minsy;
	mins[2] = minsz;

	maxs[0] = maxsx;
	maxs[1] = maxsy;
	maxs[2] = maxsz;

	end[0] = endx;
	end[1] = endy;
	end[2] = endz;

	passEnt = ge.edicts + Entity_get_fEntityIndex(jpassEnt);

	result = gi.trace(start, mins, maxs, end, passEnt, contentMask);

	resEndpos = newJavaVec3(&(result.endpos));
	resPlaneNormal = newJavaVec3(&(result.plane.normal));

	if (!result.surface)
		{
		resSurfaceName = 0;
		resSurfaceFlags = resSurfaceValue = 0;
		}
	else
		{
		resSurfaceName = (*env)->NewStringUTF(env, result.surface->name);
		resSurfaceFlags = result.surface->flags;
		resSurfaceValue = result.surface->value;
		}

	resEnt = Entity_getEntity(result.ent - ge.edicts);

	return (*env)->NewObject(java_env, class_TraceResults, method_TraceResults_ctor, 
		result.allsolid, result.startsolid, result.fraction, resEndpos, resPlaneNormal, 
		result.plane.dist, result.plane.type, result.plane.signbits,
		resSurfaceName, resSurfaceFlags, resSurfaceValue, result.contents,
		resEnt);
	}


static jint JNICALL Java_Engine_pointContents0(JNIEnv *env, jclass cls, jfloat x, jfloat y, jfloat z)
	{
	vec3_t v;
	v[0] = x;
	v[1] = y;
	v[2] = z;
	return gi.pointcontents(v);
	}

static jboolean JNICALL Java_Engine_inP0(JNIEnv *env, jclass cls, jfloat x1, jfloat y1, jfloat z1, jfloat x2, jfloat y2, jfloat z2, jint calltype)
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
		case CALL_INPHS: return gi.inPHS(p1, p2);
		case CALL_INPVS: return gi.inPVS(p1, p2);
		default: return 0;
		}
	}


static void JNICALL Java_Engine_setAreaPortalState(JNIEnv *env, jclass cls, jint portalnum, jboolean open)
	{
	gi.SetAreaPortalState(portalnum, open);
	}


static jboolean JNICALL Java_Engine_areasConnected(JNIEnv *env, jclass cls, jint area1, jint area2)
	{
	return gi.AreasConnected(area1, area2);
	}


static jobjectArray JNICALL Java_Engine_boxEntities0(JNIEnv *env, jclass cls, 
	jfloat minsx, jfloat minsy, jfloat minsz, 
	jfloat maxsx, jfloat maxsy, jfloat maxsz, jint maxCount, jint areaType)
	{
	vec3_t mins;
	vec3_t maxs;
	int count;
	edict_t **list = gi.TagMalloc(maxCount * sizeof(edict_t *), TAG_GAME);
	jobjectArray result;

	mins[0] = minsx;
	mins[1] = minsy;
	mins[2] = minsz;

	maxs[0] = maxsx;
	maxs[1] = maxsy;
	maxs[2] = maxsz;

	count = gi.BoxEdicts(mins, maxs, list, maxCount, areaType);

	result = Entity_createArray(list, count);
	gi.TagFree(list);

	return result;
	}


static void JNICALL Java_Engine_write0(JNIEnv *env, jclass cls, jobject obj, jfloat x, jfloat y, jfloat z, jint c, jint calltype)
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

static jint JNICALL Java_Engine_argc(JNIEnv *env, jclass cls)
	{
	return gi.argc();	
	}

static jstring JNICALL Java_Engine_argv(JNIEnv *env, jclass cls, jint n)
	{
	return (*env)->NewStringUTF(env, gi.argv(n));
	}

static jstring JNICALL Java_Engine_args(JNIEnv *env, jclass cls)
	{
	return (*env)->NewStringUTF(env, gi.args());
	}

static void JNICALL Java_Engine_addCommandString(JNIEnv *env, jclass cls, jstring js)
	{
	char *s;

	s = (char *)((*env)->GetStringUTFChars(env, js, 0));
	gi.AddCommandString(s);
	(*env)->ReleaseStringUTFChars(env, js, s);
	}

static void JNICALL Java_Engine_debugGraph(JNIEnv *env, jclass cls, jfloat value, jint color)
	{
	gi.DebugGraph(value, color);
	}


// ------ helper function not based on Quake II Game interface

static jstring JNICALL Java_Engine_getGamePath(JNIEnv *env, jclass cls)
	{
	return (*env)->NewStringUTF(env, global_gameDirName);
	}


