#include "globals.h"
#include "javalink.h"
#include "NativePlayer.h"

#define FLOAT_CLIENT_PS_FOV		0
#define FLOAT_CLIENT_PS_BLEND	1		


usercmd_t *thinkCmd;
static cvar_t *cvar_gravity;

// handles to the NativePlayer and game.player class
static jclass class_NativePlayer;

// handle to PMoveResults class
static jclass class_PMoveResults;
static jmethodID method_PMoveResults_ctor;

static JNINativeMethod NativePlayer_methods[] = 
	{
	{"pMove0",			"(I)LPMoveResults;",		Java_NativePlayer_pMove0},
	{"setFloat0",		"(IIFFFF)V",				Java_NativePlayer_setFloat0},
	{"setStat0",		"(IIS)V",					Java_NativePlayer_setStat0},
	{"cprint0",			"(IILjava/lang/String;)V",	Java_NativePlayer_cprint0},
	{"centerprint0",	"(ILjava/lang/String;)V",	Java_NativePlayer_centerprint0}
	};

void NativePlayer_javaInit()
	{
	cvar_gravity = gi.cvar("sv_gravity", "800", 0);

	class_NativePlayer = (*java_env)->FindClass(java_env, "NativePlayer");
	CHECK_EXCEPTION();
	if (!class_NativePlayer)
		{
		debugLog("Couldn't get Java NativePlayer class\n");
		return;
		}

	(*java_env)->RegisterNatives(java_env, class_NativePlayer, NativePlayer_methods, 5); 
	CHECK_EXCEPTION();

	class_PMoveResults = (*java_env)->FindClass(java_env, "PMoveResults");
	CHECK_EXCEPTION();
	if (!class_PMoveResults)
		{
		debugLog("Couldn't get Java PMoveResults class\n");
		return;
		}

	method_PMoveResults_ctor = (*java_env)->GetMethodID(java_env, class_PMoveResults, "<init>", "(BSSSSSSB[LNativeEntity;FLNativeEntity;II)V");
	CHECK_EXCEPTION();
	if (!method_PMoveResults_ctor)
		debugLog("Couldn't get PMoveResults constructor\n");

	debugLog("NativePlayer_javaInit() finished\n");
	}
	

void NativePlayer_javaFinalize()
	{
	if (class_NativePlayer)
		(*java_env)->UnregisterNatives(java_env, class_NativePlayer);
	}



static edict_t	*pm_passent;

// pmove doesn't need to know about passent and contentmask
static trace_t	PM_trace(vec3_t start, vec3_t mins, vec3_t maxs, vec3_t end)
	{
//	if (pm_passent->health > 0)
		return gi.trace (start, mins, maxs, end, pm_passent, MASK_PLAYERSOLID);
//	else
//		return gi.trace (start, mins, maxs, end, pm_passent, MASK_DEADSOLID);
	}


static jobject JNICALL Java_NativePlayer_pMove0(JNIEnv *env, jclass cls, jint index)
	{
	jobject groundEnt;
	jobjectArray touched;
	int i;
	pmove_t pm;
	gclient_t *client;
	edict_t *ent;

	// sanity check
	if ((index < 0) || (index > global_maxClients))
		return 0;

	ent = ge.edicts + index;
	client = ent->client;

	client->ps.pmove.gravity = (int)(cvar_gravity->value);

	memset (&pm, 0, sizeof(pm));

	pm.s = client->ps.pmove;
/*
	if (memcmp(&client->old_pmove, &pm.s, sizeof(pm.s)))
		pm.snapinitial = true;
*/
	pm.cmd = *thinkCmd;

	pm.trace = PM_trace;	// adds default parms
	pm.pointcontents = gi.pointcontents;

	gi.Pmove(&pm);

	// save results of pmove
	client->ps.pmove = pm.s;
//	client->old_pmove = pm.s;

	for (i=0 ; i<3 ; i++)
		{
		ent->s.origin[i] = (float)(pm.s.origin[i]	* 0.125);
		ent->velocity[i] = (float)(pm.s.velocity[i]	* 0.125);
		ent->mins[i] = pm.mins[i];
		ent->maxs[i] = pm.maxs[i];
		client->ps.viewangles[i] = pm.viewangles[i];
		}

	touched = Entity_createArray(pm.touchents, pm.numtouch);
	if (!pm.groundentity)
		groundEnt = 0;
	else
		groundEnt = Entity_getEntity(pm.groundentity - ge.edicts);

	return (*env)->NewObject(env, class_PMoveResults, method_PMoveResults_ctor,
		pm.cmd.buttons, pm.cmd.angles[0], pm.cmd.angles[1], pm.cmd.angles[2],
		pm.cmd.forwardmove, pm.cmd.sidemove, pm.cmd.upmove, pm.cmd.lightlevel,
		touched, pm.viewheight, groundEnt, pm.watertype, pm.waterlevel);
	}


static void JNICALL Java_NativePlayer_setFloat0(JNIEnv *env, jclass cls, jint index, jint fieldindex, jfloat r, jfloat g, jfloat b, jfloat a)
	{
	edict_t *ent;
	
	// sanity check
	if ((index < 0) || (index > global_maxClients))
		return;

	ent = ge.edicts + index;
	
	switch (fieldindex)
		{
		case FLOAT_CLIENT_PS_FOV:
			ent->client->ps.fov = r;
			break;

		case FLOAT_CLIENT_PS_BLEND:
			ent->client->ps.blend[0] = r;
			ent->client->ps.blend[1] = g;
			ent->client->ps.blend[2] = b;
			ent->client->ps.blend[3] = a;
			break;

		default: ;  // ---FIXME-- should indicate an error somewhere
		}
	}

static void JNICALL Java_NativePlayer_setStat0(JNIEnv *env, jclass cls, jint index, jint fieldindex, jshort value)
	{
	edict_t *ent;

	// sanity check
	if ((index < 0) || (index > global_maxClients) || (fieldindex < 0) || (fieldindex > MAX_STATS))
		return;
	
	ent = ge.edicts + index;
	ent->client->ps.stats[fieldindex] = value;
	}

static void JNICALL Java_NativePlayer_cprint0(JNIEnv *env , jclass cls, jint index, jint printlevel, jstring js)
	{
	const char *str;
	edict_t *ent;

	// sanity check
	if ((index < 0) || (index > global_maxClients))
		return;

	ent = ge.edicts + index;

	str = (*env)->GetStringUTFChars(env, js, 0);
	gi.cprintf(ent, printlevel, "%s", str);
	(*env)->ReleaseStringUTFChars(env, js, str);
	}


static void JNICALL Java_NativePlayer_centerprint0(JNIEnv *env, jclass cls, jint index, jstring js)
	{
	const char *str;
	edict_t *ent;

	// sanity check
	if ((index < 0) || (index > global_maxClients))
		return;

	ent = ge.edicts + index;

	str = (*env)->GetStringUTFChars(env, js, 0);
	gi.centerprintf(ent, "%s", str);
	(*env)->ReleaseStringUTFChars(env, js, str);
	}
