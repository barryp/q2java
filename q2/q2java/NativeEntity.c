#include "globals.h"
#include "javalink.h"
#include "NativeEntity.h"

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
#define VEC3_CLIENT_PS_VIEWANGLES	9
#define VEC3_CLIENT_PS_VIEWOFFSET	10
#define VEC3_CLIENT_PS_KICKANGLES	11
#define VEC3_CLIENT_PS_GUNANGLES	12
#define VEC3_CLIENT_PS_GUNOFFSET	13

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
#define INT_CLIENT_PS_GUNINDEX 15
#define INT_CLIENT_PS_GUNFRAME 16
#define INT_CLIENT_PS_RDFLAGS 17		

#define CALL_SOUND 1
#define CALL_POSITIONED_SOUND 2


// handles to the Entity class
static jclass class_NativeEntity;
static jfieldID  field_NativeEntity_fEntityIndex;
static jfieldID  field_NativeEntity_fEntityArray;
// static jmethodID method_NativeEntity_ctor;

static JNINativeMethod Entity_methods[] = 
	{
	{"allocateEntity",	"(Z)I", 					Java_NativeEntity_allocateEntity},
	{"freeEntity0",		"(I)V",						Java_NativeEntity_freeEntity0},
	{"setInt",			"(III)V",					Java_NativeEntity_setInt},
	{"getInt",			"(II)I",					Java_NativeEntity_getInt},
	{"setVec3",			"(IIFFF)V",					Java_NativeEntity_setVec3},
	{"getVec3",			"(II)LVec3;",				Java_NativeEntity_getVec3},
	{"sound0",			"(FFFIIIFFFI)V",			Java_NativeEntity_sound0},
	{"setModel0",		"(ILjava/lang/String;)V",	Java_NativeEntity_setModel0},	
	{"linkEntity0",		"(I)V",						Java_NativeEntity_linkEntity0},
	{"unlinkEntity0",	"(I)V",						Java_NativeEntity_unlinkEntity0}
	};

void Entity_javaInit()
	{
	debugLog("Entity_javaInit() started\n");
	class_NativeEntity = (*java_env)->FindClass(java_env, "NativeEntity");
	CHECK_EXCEPTION();
	if (!class_NativeEntity)
		{
		debugLog("Couldn't get Java Entity class\n");
		return;
		}

	(*java_env)->RegisterNatives(java_env, class_NativeEntity, Entity_methods, 10); 
	CHECK_EXCEPTION();

	field_NativeEntity_fEntityIndex = (*java_env)->GetFieldID(java_env, class_NativeEntity, "fEntityIndex", "I");
	field_NativeEntity_fEntityArray = (*java_env)->GetStaticFieldID(java_env, class_NativeEntity, "fEntityArray", "[LNativeEntity;");
//	method_NativeEntity_ctor = (*java_env)->GetMethodID(java_env, class_NativeEntity, "<init>", "(I)V");
	CHECK_EXCEPTION();
	debugLog("Entity_javaInit() finished\n");
	}

void Entity_arrayInit()
	{
	// make the C entity array the same size as the Java Entity array
	jobject array = (*java_env)->GetStaticObjectField(java_env, class_NativeEntity, field_NativeEntity_fEntityArray);
	ge.max_edicts = (*java_env)->GetArrayLength(java_env, array);
	ge.edicts = gi.TagMalloc(ge.max_edicts * sizeof(edict_t), TAG_GAME);
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

	if (!field_NativeEntity_fEntityArray)
		{			
		debugLog("Don't have handle for Java Entity.fEntityArray field\n");
		return 0;
		}

	array = (*java_env)->GetStaticObjectField(java_env, class_NativeEntity, field_NativeEntity_fEntityArray);
	result = (*java_env)->GetObjectArrayElement(java_env, array, index);
	CHECK_EXCEPTION();
	return result;
	}

void Entity_setEntity(int index, jobject value)
	{
	jobjectArray array;

	if (!field_NativeEntity_fEntityArray)
		{			
		debugLog("Don't have handle for Java Entity.fEntityArray field\n");
		return;
		}

	array = (*java_env)->GetStaticObjectField(java_env, class_NativeEntity, field_NativeEntity_fEntityArray);
	(*java_env)->SetObjectArrayElement(java_env, array, index, value);
	CHECK_EXCEPTION();
	}


int Entity_get_fEntityIndex(jobject jent)
	{
	int result;

	if (!field_NativeEntity_fEntityIndex)
		{
		debugLog("Don't have handle for fEntityIndex\n");
		return -1;						
		}

	result = (*java_env)->GetIntField(java_env, jent, field_NativeEntity_fEntityIndex);
	CHECK_EXCEPTION();
	return result;
	}

void Entity_set_fEntityIndex(jobject jent, int index)
	{
	if (!field_NativeEntity_fEntityIndex)
		{
		debugLog("Don't have handle for fEntityIndex\n");
		return;
		}

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
	if ((!ents) || (count < 1) || (!class_NativeEntity))
		return 0;

	// recount, elminiating duplicates and worldspawn
	realCount = 0;
	for (i = 0; i < count; i++)
		{
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

static jint JNICALL Java_NativeEntity_allocateEntity(JNIEnv *env, jclass cls, jboolean isWorld)
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
		if	(!ent->inuse && ( ent->freetime < 2 || (global_frameTime - ent->freetime) > 0.5 ) )
			break;
		}
	
	// we're totally drained
	if (i == ge.max_edicts)
		{
		gi.dprintf("Java_NativeEntity_allocateEntity: no free entities");
		return -1;			
		}

	// a never-before used entity
	if (i == ge.num_edicts)
		ge.num_edicts++;

	ent->inuse = true;
	ent->s.number = i;			
	return i;
	}		

static void JNICALL Java_NativeEntity_freeEntity0(JNIEnv *env, jclass cls, jint index)
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
	ent->freetime = global_frameTime;
	}		


static void JNICALL Java_NativeEntity_setInt(JNIEnv *env, jclass cls, jint index, jint fieldNum, jint val)
	{
	int *ip = lookupInt(index, fieldNum);

	if (ip)
		*ip = val;
	}


static jint JNICALL Java_NativeEntity_getInt(JNIEnv *env, jclass cls, jint index, jint fieldNum)
	{
	int *ip = lookupInt(index, fieldNum);
	if (ip)
		return *ip;
	else
		return 0;  // ---FIX--- this isn't a good error indicator
	}

static void JNICALL Java_NativeEntity_setVec3(JNIEnv *env, jclass cls, jint index, jint fieldNum, jfloat x, jfloat y, jfloat z)
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
		ent = ge.edicts + index;
		ent->client->ps.pmove.velocity[0] = (short)(x * 8);
		ent->client->ps.pmove.velocity[1] = (short)(y * 8);
		ent->client->ps.pmove.velocity[2] = (short)(z * 8);
		}
	}


static jobject JNICALL Java_NativeEntity_getVec3(JNIEnv *env, jclass cls, jint index, jint fieldNum)
	{
	vec3_t *v = lookupVec3(index, fieldNum);
	return newJavaVec3(v);
	}


static void JNICALL Java_NativeEntity_sound0(JNIEnv *env , jclass cls, jfloat x, jfloat y, jfloat z, jint index, jint channel, jint soundindex, jfloat volume, jfloat attenuation, jfloat timeofs, jint calltype)
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


static void JNICALL Java_NativeEntity_setModel0(JNIEnv *env, jclass cls, jint index, jstring js)
	{
	char *str;

	// sanity check
	if ((index < 0) || (index >= ge.max_edicts))
		return;

	str = (char *)((*env)->GetStringUTFChars(env, js, 0));
	gi.setmodel(ge.edicts + index, str);
	(*env)->ReleaseStringUTFChars(env, js, str);
	}

static void JNICALL Java_NativeEntity_linkEntity0(JNIEnv *env, jclass cls, jint index)
	{
	// sanity check
	if ((index < 0) || (index >= ge.max_edicts))
		return;
	
	gi.linkentity(ge.edicts + index);
	}

static void JNICALL Java_NativeEntity_unlinkEntity0(JNIEnv *env, jclass cls, jint index)
	{
	// sanity check
	if ((index < 0) || (index >= ge.max_edicts))
		return;

	gi.unlinkentity(ge.edicts + index);
	}
