#include "globals.h"
#include "javalink.h"
#include "q2java_NativeEntity.h"

// handles to fields in a C entity
// (same constants as in NativeEntity.java)
#define VEC3_S_ORIGIN		0
#define VEC3_S_ANGLES		1
#define VEC3_S_OLD_ORIGIN	2
#define VEC3_MINS			3
#define VEC3_MAXS			4
#define VEC3_ABSMIN			5
#define VEC3_ABSMAX			6
#define VEC3_SIZE			7
#define VEC3_VELOCITY		8
#define VEC3_CLIENT_PS_VIEWANGLES	100
#define VEC3_CLIENT_PS_VIEWOFFSET	101
#define VEC3_CLIENT_PS_KICKANGLES	102
#define VEC3_CLIENT_PS_GUNANGLES	103
#define VEC3_CLIENT_PS_GUNOFFSET	104

#define INT_S_MODELINDEX 1
#define INT_S_MODELINDEX2 2
#define INT_S_MODELINDEX3 3
#define INT_S_MODELINDEX4 4
#define INT_SVFLAGS 5
#define INT_SOLID 6
#define INT_CLIPMASK 7
#define INT_S_FRAME 8
#define INT_S_SKINNUM 9
#define INT_S_EFFECTS 10
#define INT_S_RENDERFX 11
#define INT_S_SOLID 12
#define INT_S_SOUND 13
#define INT_S_EVENT 14
#define INT_CLIENT_PS_GUNINDEX 100
#define INT_CLIENT_PS_GUNFRAME 101
#define INT_CLIENT_PS_RDFLAGS 102

#define CALL_SOUND 1
#define CALL_POSITIONED_SOUND 2

#define FLOAT_CLIENT_PS_FOV		100
#define FLOAT_CLIENT_PS_BLEND	101		


usercmd_t *thinkCmd;
static cvar_t *cvar_gravity;

// handles to the Entity class
static jclass class_NativeEntity;
static jfieldID  field_NativeEntity_fEntityIndex;
static jfieldID  field_NativeEntity_fEntityArray;
static jfieldID  field_NativeEntity_fNumEntities;
static jfieldID	 field_NativeEntity_fMaxPlayers;

// handle to PMoveResults class
static jclass class_PMoveResults;
static jmethodID method_PMoveResults_ctor;

static JNINativeMethod Entity_methods[] = 
	{
	{"allocateEntity",	"(Z)I", 					Java_q2java_NativeEntity_allocateEntity},
	{"freeEntity0",		"(I)V",						Java_q2java_NativeEntity_freeEntity0},
	{"setInt",			"(III)V",					Java_q2java_NativeEntity_setInt},
	{"getInt",			"(II)I",					Java_q2java_NativeEntity_getInt},
	{"setVec3",			"(IIFFF)V",					Java_q2java_NativeEntity_setVec3},
	{"getVec3",			"(II)Lq2java/Vec3;",		Java_q2java_NativeEntity_getVec3},
	{"sound0",			"(FFFIIIFFFI)V",			Java_q2java_NativeEntity_sound0},
	{"setModel0",		"(ILjava/lang/String;)V",	Java_q2java_NativeEntity_setModel0},	
	{"boxEntity0",		"(II)[Lq2java/NativeEntity;", Java_q2java_NativeEntity_boxEntity0},
	{"linkEntity0",		"(I)V",						Java_q2java_NativeEntity_linkEntity0},
	{"unlinkEntity0",	"(I)V",						Java_q2java_NativeEntity_unlinkEntity0},

	// methods for players only
	{"pMove0",			"(I)Lq2java/PMoveResults;",	Java_q2java_NativeEntity_pMove0},
	{"setFloat0",		"(IIFFFF)V",				Java_q2java_NativeEntity_setFloat0},
	{"setStat0",		"(IIS)V",					Java_q2java_NativeEntity_setStat0},
	{"cprint0",			"(IILjava/lang/String;)V",	Java_q2java_NativeEntity_cprint0},
	{"centerprint0",	"(ILjava/lang/String;)V",	Java_q2java_NativeEntity_centerprint0}
	};

void Entity_javaInit()
	{
    cvar_gravity = gi.cvar("sv_gravity", "800", 0);

	class_NativeEntity = (*java_env)->FindClass(java_env, "q2java/NativeEntity");
	if(CHECK_EXCEPTION() || !class_NativeEntity)
		{
		java_error = "Couldn't find q2java.NativeEntity\n";
		return;
		}

	(*java_env)->RegisterNatives(java_env, class_NativeEntity, Entity_methods, sizeof(Entity_methods) / sizeof(Entity_methods[0])); 
	if (CHECK_EXCEPTION())
		{
		java_error = "Couldn't register native methods for q2java.NativeEntity\n";
		return;
		}

	field_NativeEntity_fEntityIndex = (*java_env)->GetFieldID(java_env, class_NativeEntity, "fEntityIndex", "I");
	field_NativeEntity_fEntityArray = (*java_env)->GetStaticFieldID(java_env, class_NativeEntity, "fEntityArray", "[Lq2java/NativeEntity;");
	field_NativeEntity_fNumEntities = (*java_env)->GetStaticFieldID(java_env, class_NativeEntity, "fNumEntities", "I");
	field_NativeEntity_fMaxPlayers  = (*java_env)->GetStaticFieldID(java_env, class_NativeEntity, "fMaxPlayers", "I");
//	method_NativeEntity_ctor = (*java_env)->GetMethodID(java_env, class_NativeEntity, "<init>", "(I)V");
	if (CHECK_EXCEPTION())
		{
		java_error = "Couldn't get field handles for NativeEntity\n";
		return;
		}

	class_PMoveResults = (*java_env)->FindClass(java_env, "q2java/PMoveResults");
	if (CHECK_EXCEPTION() || !class_PMoveResults)
		{
		java_error = "Couldn't find q2java.PMoveResults\n";
		return;
		}

	method_PMoveResults_ctor = (*java_env)->GetMethodID(java_env, class_PMoveResults, "<init>", "(BSSSSSSB[Lq2java/NativeEntity;FLq2java/NativeEntity;II)V");
	if (CHECK_EXCEPTION() || !method_PMoveResults_ctor)
		{
		java_error = "Couldn't find q2java.PMoveResults constructor\n";
		return;
		}
	}

void Entity_arrayInit()
	{
	jobjectArray ja;
	cvar_t *cvar_maxclients;
	cvar_t *cvar_maxentities;
	int i;

	// Create the C entity array
	cvar_maxentities = gi.cvar("maxentities", "1024", CVAR_LATCH);
	ge.max_edicts = (int)(cvar_maxentities->value);
	ge.edicts = gi.TagMalloc(ge.max_edicts * sizeof(edict_t), TAG_GAME);

	// Create a Java NativeEntity array the same size
	ja = (*java_env)->NewObjectArray(java_env, ge.max_edicts, class_NativeEntity, 0);
	(*java_env)->SetStaticObjectField(java_env, class_NativeEntity, field_NativeEntity_fEntityArray, ja);
	CHECK_EXCEPTION();

	// make a C array for client info 
	cvar_maxclients = gi.cvar ("maxclients", "4", CVAR_SERVERINFO | CVAR_LATCH);
	global_maxClients = (int) (cvar_maxclients->value);
	global_clients = gi.TagMalloc(global_maxClients * sizeof(gclient_t), TAG_GAME);

	// let Java know how big it is
	(*java_env)->SetStaticIntField(java_env, class_NativeEntity, field_NativeEntity_fMaxPlayers, global_maxClients);
	CHECK_EXCEPTION();

	// link the two C arrays together
	for (i = 0; i < global_maxClients; i++)
		{
		ge.edicts[i+1].client = global_clients + i;

		// a little tweak to get things going
//		global_clients[i].ps.fov = 90;
		}

	// note in C and Java how many entities are used so far
	ge.num_edicts = global_maxClients + 1; 
	(*java_env)->SetStaticIntField(java_env, class_NativeEntity, field_NativeEntity_fNumEntities, ge.num_edicts);
	CHECK_EXCEPTION();
	}


void Entity_javaFinalize()
	{
	if (class_NativeEntity)
		(*java_env)->UnregisterNatives(java_env, class_NativeEntity);
	}

static vec3_t *lookupVec3(int index, int fieldNum)
	{
	edict_t *ent; 
		
	// sanity check
	if ((index < 0) || (index >= ge.max_edicts))
		return NULL;

	ent	= ge.edicts + index;

	// check for attemt to access player field in a non-player entity
	if ((fieldNum >= 100) && !(ent->client))
		return NULL;
		

	switch (fieldNum)
		{
		case VEC3_S_ORIGIN: return &(ent->s.origin);
		case VEC3_S_ANGLES: return &(ent->s.angles); 
		case VEC3_S_OLD_ORIGIN: return &(ent->s.old_origin);
		case VEC3_MINS: return &(ent->mins);
		case VEC3_MAXS: return &(ent->maxs);
		case VEC3_ABSMIN: return &(ent->absmin);
		case VEC3_ABSMAX: return &(ent->absmax);
		case VEC3_SIZE: return &(ent->size);
		case VEC3_VELOCITY: return &(ent->velocity);
		case VEC3_CLIENT_PS_VIEWANGLES: return &(ent->client->ps.viewangles);
		case VEC3_CLIENT_PS_VIEWOFFSET: return &(ent->client->ps.viewoffset);
		case VEC3_CLIENT_PS_KICKANGLES: return &(ent->client->ps.kick_angles);
		case VEC3_CLIENT_PS_GUNANGLES: return &(ent->client->ps.gunangles);
		case VEC3_CLIENT_PS_GUNOFFSET: return &(ent->client->ps.gunoffset);
		default: return NULL; // ---FIX--- should record an error somewhere
		}
	}


static int *lookupInt(int index, int fieldNum)
	{
	edict_t *ent;

	// sanity check
	if ((index < 0) || (index >= ge.max_edicts))
		return NULL;

	ent = ge.edicts + index;

	// check for attemt to access player field in a non-player entity
	if ((fieldNum >= 100) && !(ent->client))
		return NULL;

	switch (fieldNum)
		{
		case INT_S_MODELINDEX: return &(ent->s.modelindex);
		case INT_S_MODELINDEX2: return &(ent->s.modelindex2);
		case INT_S_MODELINDEX3: return &(ent->s.modelindex3);
		case INT_S_MODELINDEX4: return &(ent->s.modelindex4);
		case INT_SVFLAGS: return &(ent->svflags);
		case INT_SOLID: return &(ent->solid);
		case INT_CLIPMASK: return &(ent->clipmask);
		case INT_S_FRAME: return &(ent->s.frame);
		case INT_S_SKINNUM: return &(ent->s.skinnum);
		case INT_S_EFFECTS: return &(ent->s.effects);
		case INT_S_RENDERFX: return &(ent->s.renderfx);
		case INT_S_SOLID: return &(ent->s.solid);
		case INT_S_SOUND: return &(ent->s.sound);
		case INT_S_EVENT: return &(ent->s.event);
		case INT_CLIENT_PS_GUNINDEX: return &(ent->client->ps.gunindex);
		case INT_CLIENT_PS_GUNFRAME: return &(ent->client->ps.gunframe);
		case INT_CLIENT_PS_RDFLAGS: return &(ent->client->ps.rdflags);
		default: return NULL; // ---FIX--- should record an error somewhere
		}
	}


jobject Entity_getEntity(int index)
	{
	jobjectArray array;
	jobject result;

	array = (*java_env)->GetStaticObjectField(java_env, class_NativeEntity, field_NativeEntity_fEntityArray);
	result = (*java_env)->GetObjectArrayElement(java_env, array, index);
	CHECK_EXCEPTION();
	return result;
	}

void Entity_setEntity(int index, jobject value)
	{
	jobjectArray array;

	array = (*java_env)->GetStaticObjectField(java_env, class_NativeEntity, field_NativeEntity_fEntityArray);
	(*java_env)->SetObjectArrayElement(java_env, array, index, value);
	CHECK_EXCEPTION();
	}


int Entity_get_fEntityIndex(jobject jent)
	{
	int result;

	result = (*java_env)->GetIntField(java_env, jent, field_NativeEntity_fEntityIndex);
	CHECK_EXCEPTION();
	return result;
	}

void Entity_set_fEntityIndex(jobject jent, int index)
	{
	(*java_env)->SetIntField(java_env, jent, field_NativeEntity_fEntityIndex, index);
	CHECK_EXCEPTION();
	}

//
// create an array of Java Entities, removing instances of worldspawn
// and any duplicates (we can do this faster in C than in Java)
//
jobjectArray Entity_createArray(edict_t **ents, int count)
	{
	int i,j;
	int realCount;
	jobjectArray result;

	// sanity check
	if ((!ents) || (count < 1))
		return 0;

	// recount, elminiating duplicates and worldspawn
	realCount = 0;
	for (i = 0; i < count; i++)
		{
		// skip unused entities
		if (!ents[i]->inuse)
			continue;

		// skip worldspawn
		if (ents[i] == ge.edicts)
			continue;

		for (j = 0; j < i; j++)
			{
			if (ents[i] == ents[j])
				break;
			}

		if (j == i)
			realCount++;
		}


	// bail if it was just worldspawns
	if (realCount < 1)
		return 0;
	
	result = (*java_env)->NewObjectArray(java_env, realCount, class_NativeEntity, 0);

	for (i = 0; i < count; i++)
		{
		// skip unused entities
		if (!ents[i]->inuse)
			continue;

		// skip worldspawn
		if (ents[i] == ge.edicts)
			continue;

		for (j = 0; j < i; j++)
			{
			if (ents[i] == ents[j])
				break;
			}

		if (j == i)
			(*java_env)->SetObjectArrayElement(java_env, result, --realCount, Entity_getEntity(ents[i] - ge.edicts));
		}
	
	return result;
	}


static jint JNICALL Java_q2java_NativeEntity_allocateEntity(JNIEnv *env, jclass cls, jboolean isWorld)
	{
	int			i;
	edict_t		*ent;

	// handle special case of the world entity
	if (isWorld)
		{
		ent = ge.edicts;
		ent->inuse = true;
		ent->s.number = 0;
		return 0;
		}

	i = global_maxClients + 1;	
	ent = ge.edicts + i;
	for (; i<ge.num_edicts ; i++, ent++)
		{
		// the first couple seconds of server time can involve a lot of
		// freeing and allocating, so relax the replacement policy
		if	(!ent->inuse && ( ent->freetime < 20 || (global_frameCount - ent->freetime) > 5 ) )
			break;
		}
	
	// we're totally drained
	if (i == ge.max_edicts)
		{
		gi.dprintf("Java_q2java_NativeEntity_allocateEntity: no free entities");
		return -1;			
		}

	// a never-before used entity
	if (i == ge.num_edicts)
		{
		ge.num_edicts++;
		(*java_env)->SetStaticIntField(java_env, class_NativeEntity, field_NativeEntity_fNumEntities, ge.num_edicts);
		}

	ent->inuse = true;
	ent->s.number = i;			
	return i;
	}		


static void JNICALL Java_q2java_NativeEntity_freeEntity0(JNIEnv *env, jclass cls, jint index)
	{
	gclient_t *cli;
	edict_t *ent;

	// sanity check
	if ((index < 0) || (index >= ge.max_edicts))
		return;

	ent = ge.edicts + index;
	cli = ent->client;

	// unlink from world
	gi.unlinkentity (ent);		

	// wipe the old entity info out
	memset (ent, 0, sizeof(*ent));

	// was this a player entity? (this shouldn't happen..but if it does let's tidy things up)
	if (cli)
		{
		// wipe out the old client info
		memset(cli, 0, sizeof(*cli));

		// relink the entity structure to the client structure
		ent->client = cli;
		}

	// make a note of when this entity was freed
	ent->freetime = global_frameCount;
	}		


static void JNICALL Java_q2java_NativeEntity_setInt(JNIEnv *env, jclass cls, jint index, jint fieldNum, jint val)
	{
	int *ip = lookupInt(index, fieldNum);

	if (ip)
		*ip = val;
	}


static jint JNICALL Java_q2java_NativeEntity_getInt(JNIEnv *env, jclass cls, jint index, jint fieldNum)
	{
	int *ip = lookupInt(index, fieldNum);
	if (ip)
		return *ip;
	else
		return 0;  // ---FIX--- this isn't a good error indicator
	}


static void JNICALL Java_q2java_NativeEntity_setVec3(JNIEnv *env, jclass cls, jint index, jint fieldNum, jfloat x, jfloat y, jfloat z)
	{
	edict_t  *ent;
	vec3_t *v = lookupVec3(index, fieldNum);

	// sanity check
	if (!v)
		return;

	if (v)
		{
		(*v)[0] = x;
		(*v)[1] = y;
		(*v)[2] = z;
		}

	// handle some special cases with player fields
	ent = ge.edicts + index;

	if ((ent->client) && (fieldNum == VEC3_S_ORIGIN))
		{
		ent->client->ps.pmove.origin[0] = (short)(x * 8);
		ent->client->ps.pmove.origin[1] = (short)(y * 8);
		ent->client->ps.pmove.origin[2] = (short)(z * 8);
		return;
		}

	if ((ent->client) && (fieldNum == VEC3_VELOCITY))
		{
		ent->client->ps.pmove.velocity[0] = (short)(x * 8);
		ent->client->ps.pmove.velocity[1] = (short)(y * 8);
		ent->client->ps.pmove.velocity[2] = (short)(z * 8);
		}
	}


static jobject JNICALL Java_q2java_NativeEntity_getVec3(JNIEnv *env, jclass cls, jint index, jint fieldNum)
	{
	vec3_t *v = lookupVec3(index, fieldNum);
	return newJavaVec3(v);
	}


static void JNICALL Java_q2java_NativeEntity_sound0(JNIEnv *env , jclass cls, jfloat x, jfloat y, jfloat z, jint index, jint channel, jint soundindex, jfloat volume, jfloat attenuation, jfloat timeofs, jint calltype)
	{
	vec3_t v;

	// sanity check
	if ((index < 0) || (index >= ge.max_edicts))
		return;
	
	switch (calltype)
		{
		case CALL_SOUND: 
			gi.sound(ge.edicts + index, channel, soundindex, volume, attenuation, timeofs); 
			break;

		case CALL_POSITIONED_SOUND:
			v[0] = x;
			v[1] = y;
			v[2] = z;
			gi.positioned_sound(v, ge.edicts + index, channel, soundindex, volume, attenuation, timeofs);
			break;
		}
	}


static jobjectArray JNICALL Java_q2java_NativeEntity_boxEntity0(JNIEnv *env, jclass cls, jint index, jint areaType)
	{
	edict_t *ent;
	edict_t *list[MAX_EDICTS];
	int count;
// debugLog("boxEntity0(%d, %d)\n", index, areaType);
	// sanity check
	if ((index < 0) || (index >= ge.max_edicts))
		return 0;

	ent = ge.edicts + index;
// debugLog("absmin = (%f, %f, %f)  absmax = (%f, %f, %f)\n", ent->absmin[0], ent->absmin[1], ent->absmin[2], ent->absmax[0], ent->absmax[1], ent->absmax[2]);
	count = gi.BoxEdicts(ent->absmin, ent->absmax, list, MAX_EDICTS, areaType);

	return Entity_createArray(list, count);
	}


static void JNICALL Java_q2java_NativeEntity_setModel0(JNIEnv *env, jclass cls, jint index, jstring js)
	{
	char *str;

	// sanity check
	if ((index < 0) || (index >= ge.max_edicts))
		return;

	str = (char *)((*env)->GetStringUTFChars(env, js, 0));
	gi.setmodel(ge.edicts + index, str);
	(*env)->ReleaseStringUTFChars(env, js, str);
	}

static void JNICALL Java_q2java_NativeEntity_linkEntity0(JNIEnv *env, jclass cls, jint index)
	{
	// sanity check
	if ((index < 0) || (index >= ge.max_edicts))
		return;
	
	gi.linkentity(ge.edicts + index);
	}

static void JNICALL Java_q2java_NativeEntity_unlinkEntity0(JNIEnv *env, jclass cls, jint index)
	{
	// sanity check
	if ((index < 0) || (index >= ge.max_edicts))
		return;

	gi.unlinkentity(ge.edicts + index);
	}


// --------------- Player Methods ----------------------

static edict_t	*pm_passent;

// pmove doesn't need to know about passent and contentmask
static trace_t	PM_trace(vec3_t start, vec3_t mins, vec3_t maxs, vec3_t end)
	{
//	if (pm_passent->health > 0)
		return gi.trace (start, mins, maxs, end, pm_passent, MASK_PLAYERSOLID);
//	else
//		return gi.trace (start, mins, maxs, end, pm_passent, MASK_DEADSOLID);
	}


static jobject JNICALL Java_q2java_NativeEntity_pMove0(JNIEnv *env, jclass cls, jint index)
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
	pm_passent = ent;
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


static void JNICALL Java_q2java_NativeEntity_setFloat0(JNIEnv *env, jclass cls, jint index, jint fieldindex, jfloat r, jfloat g, jfloat b, jfloat a)
	{
	edict_t *ent;
	
	// sanity check
	if ((index < 0) || (index > global_maxClients))
		return;

	ent = ge.edicts + index;
	// check for attemt to access player field in a non-player entity
	if ((fieldindex >= 100) && !(ent->client))
		return;
	
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

static void JNICALL Java_q2java_NativeEntity_setStat0(JNIEnv *env, jclass cls, jint index, jint fieldindex, jshort value)
	{
	edict_t *ent;

	// sanity check
	if ((index < 1) || (index > global_maxClients) || (fieldindex < 0) || (fieldindex > MAX_STATS))
		return;
	
	ent = ge.edicts + index;
	ent->client->ps.stats[fieldindex] = value;
	}

static void JNICALL Java_q2java_NativeEntity_cprint0(JNIEnv *env , jclass cls, jint index, jint printlevel, jstring js)
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


static void JNICALL Java_q2java_NativeEntity_centerprint0(JNIEnv *env, jclass cls, jint index, jstring js)
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

