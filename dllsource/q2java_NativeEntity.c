#include "globals.h"
#include "q2java_NativeEntity.h"

#include <string.h> // for memset()

// for setting player delta_angles
#define ANGLE2SHORT(x)  ((short)((int)((x)*65536/360) & 65535))

// types of entities we can create
#define ENTITY_NORMAL 0
#define ENTITY_WORLD  1
#define ENTITY_PLAYER 2

// handles to fields in a C entity
// (same constants as in NativeEntity.java)
#define BYTE_CLIENT_PS_PMOVE_PMFLAGS 100
#define BYTE_CLIENT_PS_PMOVE_PMTIME 101

#define SHORT_CLIENT_PS_PMOVE_GRAVITY 100

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
#define INT_LINKCOUNT 15
#define INT_AREANUM 16
#define INT_CLIENT_PS_GUNINDEX 100
#define INT_CLIENT_PS_GUNFRAME 101
#define INT_CLIENT_PS_RDFLAGS 102
#define INT_CLIENT_PS_PMOVE_PMTYPE 103
#define INT_CLIENT_PING 104

#define FLOAT_CLIENT_PS_FOV     100
#define FLOAT_CLIENT_PS_BLEND   101     

#define VEC3_S_ORIGIN       0
#define VEC3_S_ANGLES       1
#define VEC3_S_OLD_ORIGIN   2
#define VEC3_MINS           3
#define VEC3_MAXS           4
#define VEC3_ABSMIN         5
#define VEC3_ABSMAX         6
#define VEC3_SIZE           7
#define VEC3_VELOCITY       8
#define VEC3_CLIENT_PS_VIEWANGLES   100
#define VEC3_CLIENT_PS_VIEWOFFSET   101
#define VEC3_CLIENT_PS_KICKANGLES   102
#define VEC3_CLIENT_PS_GUNANGLES    103
#define VEC3_CLIENT_PS_GUNOFFSET    104
#define VEC3_CLIENT_PS_PMOVE_DELTA_ANGLES 105 // not used in lookup_Vec3

#define ENTITY_OWNER    1
#define ENTITY_GROUND   2

#define CALL_SOUND 1
#define CALL_POSITIONED_SOUND 2


// handles to the NativeEntity class
jclass class_NativeEntity;
static jfieldID  field_NativeEntity_fEntityIndex;
static jfieldID  field_NativeEntity_gEntityArray;
static jfieldID  field_NativeEntity_gNumEntities;
static jfieldID  field_NativeEntity_gMaxPlayers;


static JNINativeMethod Entity_methods[] = 
    {
    {"allocateEntity",  "(I)I",                     Java_q2java_NativeEntity_allocateEntity},
    {"copySettings0",   "(II)V",                    Java_q2java_NativeEntity_copySettings0},
    {"freeEntity0",     "(I)V",                     Java_q2java_NativeEntity_freeEntity0},
    {"getByte",         "(II)B",                    Java_q2java_NativeEntity_getByte},  
    {"setByte",         "(IIB)V",                   Java_q2java_NativeEntity_setByte},
    {"getShort",        "(II)S",                    Java_q2java_NativeEntity_getShort}, 
    {"setShort",        "(IIS)V",                   Java_q2java_NativeEntity_setShort},
    {"getInt",          "(II)I",                    Java_q2java_NativeEntity_getInt},
    {"setInt",          "(III)V",                   Java_q2java_NativeEntity_setInt},
    {"getVec3",         "(III)Ljavax/vecmath/Tuple3f;",     Java_q2java_NativeEntity_getVec3},
    {"setVec3",         "(IIFFF)V",                 Java_q2java_NativeEntity_setVec3},
    {"getEntity",       "(II)Lq2java/NativeEntity;",Java_q2java_NativeEntity_getEntity},
    {"setEntity",       "(III)V",                   Java_q2java_NativeEntity_setEntity},
    {"getPlayerListener0", "(I)Lq2java/PlayerListener;", Java_q2java_NativeEntity_getPlayerListener0},
    {"setPlayerListener0", "(ILq2java/PlayerListener;)V", Java_q2java_NativeEntity_setPlayerListener0},
    {"getPlayerInfo0",  "(I)Ljava/lang/String;",    Java_q2java_NativeEntity_getPlayerInfo0},
    {"getRadiusEntities0","(IFZZ)[Lq2java/NativeEntity;",Java_q2java_NativeEntity_getRadiusEntities0},
    {"sound0",          "(FFFIIIFFFI)V",            Java_q2java_NativeEntity_sound0},
    {"setModel0",       "(ILjava/lang/String;)V",   Java_q2java_NativeEntity_setModel0},    
    {"getBoxEntities0",     "(II)[Lq2java/NativeEntity;", Java_q2java_NativeEntity_getBoxEntities0},
    {"linkEntity0",     "(I)V",                     Java_q2java_NativeEntity_linkEntity0},
    {"unlinkEntity0",   "(I)V",                     Java_q2java_NativeEntity_unlinkEntity0},
    {"traceMove0",      "(IIF)Lq2java/TraceResults;",Java_q2java_NativeEntity_traceMove0},
    {"getPotentialPushed0", "(IFFFFFFI)[Lq2java/NativeEntity;", Java_q2java_NativeEntity_getPotentialPushed0},

    // methods for players only
    {"pMove0",          "(IBBSSSSSSBBI)Lq2java/PMoveResults;",   Java_q2java_NativeEntity_pMove0},
    {"setFloat0",       "(IIFFFF)V",                Java_q2java_NativeEntity_setFloat0},
    {"setStat0",        "(IIS)V",                   Java_q2java_NativeEntity_setStat0},
    {"cprint0",         "(IILjava/lang/String;)V",  Java_q2java_NativeEntity_cprint0},
    {"centerprint0",    "(ILjava/lang/String;)V",   Java_q2java_NativeEntity_centerprint0}
    };

void Entity_javaInit()
    {
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
    field_NativeEntity_gEntityArray = (*java_env)->GetStaticFieldID(java_env, class_NativeEntity, "gEntityArray", "[Lq2java/NativeEntity;");
    field_NativeEntity_gNumEntities = (*java_env)->GetStaticFieldID(java_env, class_NativeEntity, "gNumEntities", "I");
    field_NativeEntity_gMaxPlayers  = (*java_env)->GetStaticFieldID(java_env, class_NativeEntity, "gMaxPlayers", "I");
    if (CHECK_EXCEPTION())
        {
        java_error = "Couldn't get field handles for NativeEntity\n";
        return;
        }
    }



void Entity_javaFinalize()
    {
    if (class_NativeEntity)
        (*java_env)->UnregisterNatives(java_env, class_NativeEntity);

    (*java_env)->DeleteLocalRef(java_env, class_NativeEntity);
    }



void Entity_arrayInit()
    {
    jobjectArray ja;
    cvar_t *cvar_maxclients;
    cvar_t *cvar_maxentities;
    int i;

    debugLog("Entity_arrayInit() start\n");

    cvar_maxentities = gi.cvar("maxentities", "1024", CVAR_LATCH);
    ge.max_edicts = (int)(cvar_maxentities->value);
    ge.edicts = gi.TagMalloc(ge.max_edicts * sizeof(edict_t), TAG_GAME);

    // Create a Java NativeEntity array the same size
    ja = (*java_env)->NewObjectArray(java_env, ge.max_edicts, class_NativeEntity, 0);
    (*java_env)->SetStaticObjectField(java_env, class_NativeEntity, field_NativeEntity_gEntityArray, ja);
    CHECK_EXCEPTION();

    // drop the local reference to the array
    (*java_env)->DeleteLocalRef(java_env, ja);

    // make a C array for client info 
    debugLog("Creating client array\n");
    cvar_maxclients = gi.cvar ("maxclients", "4", CVAR_SERVERINFO | CVAR_LATCH);
    global_maxClients = (int) (cvar_maxclients->value);
    global_clients = gi.TagMalloc(global_maxClients * sizeof(gclient_t), TAG_GAME);

    // let Java know how big it is
    (*java_env)->SetStaticIntField(java_env, class_NativeEntity, field_NativeEntity_gMaxPlayers, global_maxClients);
    CHECK_EXCEPTION();

    // link the two C arrays together
    debugLog("Linking C arrays\n");
    for (i = 0; i < global_maxClients; i++)
        ge.edicts[i+1].client = global_clients + i;

    // note in C and Java how many entities are used so far
    debugLog("setting counters\n");
    ge.num_edicts = global_maxClients + 1; 
    (*java_env)->SetStaticIntField(java_env, class_NativeEntity, field_NativeEntity_gNumEntities, ge.num_edicts);
    CHECK_EXCEPTION();

    debugLog("Entity_arrayInit() finished\n");
    }



// clear info from entity arrays..for a new level
void Entity_arrayReset()
    {
    jobjectArray ja;
    jobject jobj;
    jstring jstr;
    int inuse;
    int i;

    debugLog("Clearing existing arrays\n");

    // get a local reference to the Java array
    ja = (*java_env)->GetStaticObjectField(java_env, class_NativeEntity, field_NativeEntity_gEntityArray);

    // clear worldspawn entity
    memset(ge.edicts, 0, sizeof (ge.edicts[0])); 
    (*java_env)->SetObjectArrayElement(java_env, ja, 0, 0);

    // clear player entities, except for inuse flags
    // (leave java objects alone)
    for (i = 0; i < global_maxClients; i++)
        {
        inuse = ge.edicts[i+1].inuse;
        jobj = ge.edicts[i+1].client->listener;
        jstr = ge.edicts[i+1].client->playerInfo;

        memset(ge.edicts + i + 1, 0, sizeof(ge.edicts[0]));
        memset(global_clients + i, 0, sizeof(global_clients[0]));

        ge.edicts[i+1].client = global_clients + i;
        ge.edicts[i+1].inuse = inuse;
        ge.edicts[i+1].client->listener = jobj;
        ge.edicts[i+1].client->playerInfo = jstr;
        }

    // clear all other non-player entities
    memset (ge.edicts + global_maxClients + 1, 0, (ge.max_edicts - global_maxClients - 1) * sizeof (ge.edicts[0]));
    for (i = global_maxClients + 1; i < ge.max_edicts; i++)
        (*java_env)->SetObjectArrayElement(java_env, ja, i, 0);

    // drop the local reference to the Java array
    (*java_env)->DeleteLocalRef(java_env, ja);  
    }




static jbyte *lookupByte(int index, int fieldNum)
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
        case BYTE_CLIENT_PS_PMOVE_PMFLAGS: return (jbyte *) &(ent->client->ps.pmove.pm_flags);
        case BYTE_CLIENT_PS_PMOVE_PMTIME: return (jbyte *) &(ent->client->ps.pmove.pm_time);
        default: return NULL; // ---FIX--- should record an error somewhere
        }
    }


static short *lookupShort(int index, int fieldNum)
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
        case SHORT_CLIENT_PS_PMOVE_GRAVITY: return &(ent->client->ps.pmove.gravity);
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
        case INT_LINKCOUNT: return &(ent->linkcount);
        case INT_AREANUM: return &(ent->areanum);
        case INT_CLIENT_PS_GUNINDEX: return &(ent->client->ps.gunindex);
        case INT_CLIENT_PS_GUNFRAME: return &(ent->client->ps.gunframe);
        case INT_CLIENT_PS_RDFLAGS: return &(ent->client->ps.rdflags);
        case INT_CLIENT_PS_PMOVE_PMTYPE: return &(ent->client->ps.pmove.pm_type);
        case INT_CLIENT_PING: return &(ent->client->ping);
        default: return NULL; // ---FIX--- should record an error somewhere
        }
    }


static vec3_t *lookupVec3(int index, int fieldNum)
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
        case VEC3_CLIENT_PS_PMOVE_DELTA_ANGLES: return NULL; // special case, fail on purpose
        default: return NULL; // ---FIX--- should record an error somewhere
        }
    }


static edict_t **lookupEntity(int index, int fieldNum)
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
        case ENTITY_OWNER: return &(ent->owner);
        case ENTITY_GROUND: return &(ent->groundentity);
        default: return NULL; // ---FIX--- should record an error somewhere
        }
    }


jobject Entity_getEntity(int index)
    {
    jobjectArray array;
    jobject result;

    array = (*java_env)->GetStaticObjectField(java_env, class_NativeEntity, field_NativeEntity_gEntityArray);
    result = (*java_env)->GetObjectArrayElement(java_env, array, index);
    CHECK_EXCEPTION();

    // drop the local reference to the Java array
    (*java_env)->DeleteLocalRef(java_env, array);   

    return result;
    }

void Entity_setEntity(int index, jobject value)
    {
    jobjectArray array;

    array = (*java_env)->GetStaticObjectField(java_env, class_NativeEntity, field_NativeEntity_gEntityArray);
    (*java_env)->SetObjectArrayElement(java_env, array, index, value);
    CHECK_EXCEPTION();

    // drop the local reference to the Java array
    (*java_env)->DeleteLocalRef(java_env, array);   
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


static jint JNICALL Java_q2java_NativeEntity_allocateEntity(JNIEnv *env, jclass cls, jint entType)
    {
    int         i;
    edict_t     *ent;

    // handle special case of the world entity
    if (entType == ENTITY_WORLD)
        {
        ent = ge.edicts;
        ent->inuse = 1;
        ent->s.number = 0;
        return 0;
        }

    // handle special case of requesting a player entity (for bots)
    if (entType == ENTITY_PLAYER)
        {
        for (i = global_maxClients; i > 0; i--)
            {
            if (!ge.edicts[i].inuse)
                {
                ge.edicts[i].inuse = 1;
                ge.edicts[i].s.number = i;
                return i;
                }
            }

        // failed finding an unused player slot
        return -1;
        }

    // all that's left are normal entities..make sure something
    // weird wasn't requested.
    if (entType != ENTITY_NORMAL)
        return -1;

    i = global_maxClients + 1;  
    ent = ge.edicts + i;
    for (; i<ge.num_edicts ; i++, ent++)
        {
        // the first couple seconds of server time can involve a lot of
        // freeing and allocating, so relax the replacement policy
        if  (!ent->inuse && ( ent->freetime < 20 || (global_frameCount - ent->freetime) > 5 ) )
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
        (*java_env)->SetStaticIntField(java_env, class_NativeEntity, field_NativeEntity_gNumEntities, ge.num_edicts);
        }

    ent->inuse = 1;
    ent->s.number = i;          
    return i;
    }       


static void JNICALL Java_q2java_NativeEntity_freeEntity0(JNIEnv *env, jclass cls, jint index)
    {
    edict_t *ent;
    gclient_t *cli;

    // sanity check
    if ((index < 0) || (index >= ge.max_edicts))
        return;

    ent = ge.edicts + index;
    cli = ent->client;

    // unlink from world
    gi.unlinkentity (ent);      

    // wipe the old entity info out
    memset(ent, 0, sizeof(*ent));

    // restore the client pointer
    ent->client = cli;

    // was this a player entity? (probably a bot)
    if (ent->client)
        {
        // remove the global reference to the PlayerListener
        if (ent->client->listener)
            (*java_env)->DeleteGlobalRef(java_env, ent->client->listener);

        // remove the local reference to the player info
        if (ent->client->playerInfo)
            (*java_env)->DeleteLocalRef(java_env, ent->client->playerInfo);

        // wipe the old entity and client info out
        memset(ent->client, 0, sizeof(*(ent->client)));
        }

    // make a note of when this entity was freed
    ent->freetime = (float) global_frameCount;
    }       


static jbyte JNICALL Java_q2java_NativeEntity_getByte(JNIEnv *env, jclass cls, jint index , jint fieldNum)
    {
    jbyte *bp = lookupByte(index, fieldNum);
    if (bp)
        return *bp;
    else
        return 0;   // ---FIXME-- should indicate an error somehow
    }


static void JNICALL Java_q2java_NativeEntity_setByte(JNIEnv *env, jclass cls, jint index, jint fieldNum, jbyte val)
    {
    jbyte *bp = lookupByte(index, fieldNum);

    if (bp)
        *bp = val;
    }


static jshort JNICALL Java_q2java_NativeEntity_getShort(JNIEnv *env, jclass cls, jint index , jint fieldNum)
    {
    short *sp = lookupShort(index, fieldNum);
    if (sp)
        return *sp;
    else
        return 0;   // ---FIXME-- should indicate an error somehow
    }


static void JNICALL Java_q2java_NativeEntity_setShort(JNIEnv *env, jclass cls, jint index, jint fieldNum, jshort val)
    {
    short *sp = lookupShort(index, fieldNum);

    if (sp)
        *sp = val;
    }


static jint JNICALL Java_q2java_NativeEntity_getInt(JNIEnv *env, jclass cls, jint index, jint fieldNum)
    {
    int *ip = lookupInt(index, fieldNum);
    if (ip)
        return *ip;
    else
        return 0;  // ---FIX--- this isn't a good error indicator
    }


static void JNICALL Java_q2java_NativeEntity_setInt(JNIEnv *env, jclass cls, jint index, jint fieldNum, jint val)
    {
    int *ip = lookupInt(index, fieldNum);

    if (ip)
        *ip = val;
    }


static jobject JNICALL Java_q2java_NativeEntity_getVec3(JNIEnv *env, jclass cls, jint index, jint fieldNum, jint vecType)
    {
    vec3_t *v = lookupVec3(index, fieldNum);
    return newJavaVec3(v, vecType);
    }


static void JNICALL Java_q2java_NativeEntity_setVec3(JNIEnv *env, jclass cls, jint index, jint fieldNum, jfloat x, jfloat y, jfloat z)
    {
    vec3_t *v;
    edict_t *ent;

    // sanity check
    if ((index < 0) || (index >= ge.max_edicts))
        return;

    // normal vector setting
    v = lookupVec3(index, fieldNum);
    if (v)
        {
        (*v)[0] = x;
        (*v)[1] = y;
        (*v)[2] = z;
        }
    
    // special player cases(I hate special cases)
    // it makes the C code more complicated, 
    // but keeps the Java code simpler

    ent = ge.edicts + index;

    // bail if not a player entity
    if (!(ent->client))
        return;

    switch (fieldNum)
        {
        case VEC3_S_ORIGIN:
            ent->client->ps.pmove.origin[0] = (short) (x * 8);
            ent->client->ps.pmove.origin[1] = (short) (y * 8);
            ent->client->ps.pmove.origin[2] = (short) (z * 8);
            break;

        case VEC3_VELOCITY:
            ent->client->ps.pmove.velocity[0] = (short) (x * 8);
            ent->client->ps.pmove.velocity[1] = (short) (y * 8);
            ent->client->ps.pmove.velocity[2] = (short) (z * 8);
            break;

        case VEC3_CLIENT_PS_PMOVE_DELTA_ANGLES:
            ent->client->ps.pmove.delta_angles[0] = ANGLE2SHORT(x);
            ent->client->ps.pmove.delta_angles[1] = ANGLE2SHORT(y);
            ent->client->ps.pmove.delta_angles[2] = ANGLE2SHORT(z);
            break;
        }
    
    }


static jobject JNICALL Java_q2java_NativeEntity_getEntity(JNIEnv *env, jclass cls, jint index, jint fieldNum)
    {
    edict_t **entp = lookupEntity(index, fieldNum);
    if ((entp) && (*entp))
        return Entity_getEntity((*entp) - ge.edicts);
    else
        return 0;
    }


static void JNICALL Java_q2java_NativeEntity_setEntity(JNIEnv *ent, jclass cls, jint index, jint fieldNum, jint valIndex)
    {
    edict_t **entp = lookupEntity(index, fieldNum);

    if (entp)
        {
        if (valIndex >= 0)
            *entp = ge.edicts + valIndex;
        else
            *entp = 0;
        }
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


static jobjectArray JNICALL Java_q2java_NativeEntity_getBoxEntities0(JNIEnv *env, jclass cls, jint index, jint areaType)
    {
    edict_t *ent;
    edict_t *list[MAX_EDICTS];
    int count;

    // sanity check
    if ((index < 0) || (index >= ge.max_edicts))
        return 0;

    ent = ge.edicts + index;
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

static edict_t  *pm_passent;
static int pm_tracemask;

// pmove doesn't need to know about passent and contentmask
static trace_t  PM_trace(vec3_t start, vec3_t mins, vec3_t maxs, vec3_t end)
    {
    return gi.trace (start, mins, maxs, end, pm_passent, pm_tracemask);
    }

static jobject JNICALL Java_q2java_NativeEntity_pMove0(JNIEnv *env, jclass cls, jint index, jbyte msec, jbyte buttons, jshort angle0, jshort angle1, jshort angle2, jshort forward, jshort side, jshort up, jbyte impulse, jbyte lightlevel, jint traceMask)
    {
    int i;
    pmove_t pm;
    gclient_t *client;
    edict_t *ent;

    // sanity check
    if ((index < 0) || (index > global_maxClients))
        return 0;

    ent = ge.edicts + index;
    client = ent->client;

    memset (&pm, 0, sizeof(pm));

    pm.s = client->ps.pmove;

    if (memcmp(&client->old_pmove, &pm.s, sizeof(pm.s)))
        pm.snapinitial = 1;

    pm.cmd.msec = msec;
    pm.cmd.buttons = buttons;
    pm.cmd.angles[0] = angle0;
    pm.cmd.angles[1] = angle1;
    pm.cmd.angles[2] = angle2;
    pm.cmd.forwardmove = forward;
    pm.cmd.sidemove = side;
    pm.cmd.upmove = up;
    pm.cmd.impulse = impulse;
    pm.cmd.lightlevel = lightlevel;

    pm.trace = PM_trace;    // adds default parms
    pm.pointcontents = gi.pointcontents;
    pm_passent = ent;
    pm_tracemask = traceMask;

    gi.Pmove(&pm);

    // save results of pmove
    client->ps.pmove = pm.s;
    client->old_pmove = pm.s;

    for (i=0 ; i<3 ; i++)
        {
        ent->s.origin[i] = (float)(pm.s.origin[i]   * 0.125);
        ent->velocity[i] = (float)(pm.s.velocity[i] * 0.125);
        ent->mins[i] = pm.mins[i];
        ent->maxs[i] = pm.maxs[i];
        client->ps.viewangles[i] = pm.viewangles[i];
        }

    return newPMoveResults(pm);
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


static jobject JNICALL Java_q2java_NativeEntity_traceMove0(JNIEnv *env, jclass cls, jint index, jint contentMask, float frameFraction)
    {
    vec3_t end;
    edict_t *ent;
    int i;
    trace_t result;

    ent = ge.edicts + index;
    for (i = 0; i < 3; i++)
        end[i] = (float)(ent->s.origin[i] + ent->velocity[i] * SECONDS_PER_FRAME * frameFraction);

    result = gi.trace(ent->s.origin, ent->mins, ent->maxs, end, ent, contentMask);

    // set the origin of the entity to its new position
    for (i = 0; i < 3; i++)
            ent->s.origin[i] = result.endpos[i];
    
    gi.linkentity(ent);

    return newTraceResults(result);
    }


static jobjectArray JNICALL Java_q2java_NativeEntity_getPotentialPushed0(JNIEnv *env, jclass cls, jint index, 
    jfloat minx, jfloat miny, jfloat minz, 
    jfloat maxx, jfloat maxy, jfloat maxz,
    jint defaultMask)
    {
    edict_t *hits[MAX_EDICTS];
    edict_t *check;
    edict_t *pusher;
    trace_t trace;
    int count;
    int i;
    int mask;

    pusher = ge.edicts + index;

    count = 0;
    for (i = 1; i < ge.num_edicts; i++)
        {
        if (i == index)
            continue;

        check = ge.edicts + i;
        
        if (!check->inuse)
            continue;

        // if the entity is standing on the pusher, it will definitely be moved
        if (check->groundentity != pusher)
            {
            // see if the ent needs to be tested
            if ( check->absmin[0] >= maxx
                || check->absmin[1] >= maxy
                || check->absmin[2] >= maxz
                || check->absmax[0] <= minx
                || check->absmax[1] <= miny
                || check->absmax[2] <= minz )
                continue;

            // see if the ent's bbox is inside the pusher's final position
            if (check->clipmask)
                mask = check->clipmask;
            else
                mask = defaultMask;

            trace = gi.trace(check->s.origin, check->mins, check->maxs, check->s.origin, check, mask);

            if (!trace.startsolid)
                continue;
            }

        hits[count++] = check;
        }

    return Entity_createArray(hits, count);
    }


static void JNICALL Java_q2java_NativeEntity_setPlayerListener0(JNIEnv *env, jclass cls, jint index, jobject obj)
    {
    gclient_t *client;

    // sanity check
    if ((index < 0) || (index > global_maxClients))
        return;

    client = ge.edicts[index].client;

    if (client->listener != NULL)
        (*env)->DeleteGlobalRef(env, client->listener);

    if (obj == NULL)
        client->listener = NULL;
    else
        client->listener  = (*env)->NewGlobalRef(env, obj);
    }


static jobject JNICALL Java_q2java_NativeEntity_getPlayerListener0(JNIEnv *env, jclass cls, jint index)
    {
    // sanity check
    if ((index < 0) || (index > global_maxClients))
        return NULL;
    
    return ge.edicts[index].client->listener;
    }


static jstring JNICALL Java_q2java_NativeEntity_getPlayerInfo0(JNIEnv *env, jclass cls, jint index)
    {
    // sanity check
    if ((index < 0) || (index > global_maxClients))
        return NULL;
    
    return ge.edicts[index].client->playerInfo;
    }

static jobjectArray JNICALL Java_q2java_NativeEntity_getRadiusEntities0
  (JNIEnv *env, jclass cls, jint index, jfloat radius, jboolean onlyPlayers, jboolean sortResults)
    {
    edict_t *ent = ge.edicts + index;
    return Java_q2java_Engine_getRadiusEntities0(env, cls, ent->s.origin[0], ent->s.origin[1], ent->s.origin[2],
        radius, index, onlyPlayers, sortResults);
    }

static void JNICALL Java_q2java_NativeEntity_copySettings0(JNIEnv *env, jclass cls, jint sourceIndex, jint destIndex)
    {
    edict_t *source;
    edict_t *dest;
    int i;

    // sanity check
    if ((sourceIndex < 0) || (sourceIndex >= ge.max_edicts))
        return;
    if ((destIndex < 0) || (destIndex >= ge.max_edicts))
        return;

    source = ge.edicts + sourceIndex;
    dest = ge.edicts + destIndex;

	dest->s = source->s;
	dest->s.number = destIndex;

	dest->svflags = source->svflags;
    for (i = 0; i < 3; i++)
        {
        dest->mins[i] = source->mins[i];
        dest->maxs[i] = source->maxs[i];
        dest->absmin[i] = source->absmin[i];
        dest->absmax[i] = source->absmax[i];
        dest->size[i] = source->size[i];
        dest->velocity[i] = source->velocity[i];
        }
	dest->solid = source->solid;
	dest->clipmask = source->clipmask;
	dest->owner = source->owner;
    dest->groundentity = source->groundentity;
    }
