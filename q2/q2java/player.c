#include "globals.h"
#include "javalink.h"

static jclass class_player;
static jmethodID method_player_ctor;
static jmethodID method_player_begin;
static jmethodID method_player_userinfoChanged;
static jmethodID method_player_command;
static jmethodID method_player_think;
static jmethodID method_player_disconnect;


void Player_javaInit()
	{
	cvar_t *player_cvar;
	jclass interface_nativePlayer;
	char *p;
	char buffer[128];

	interface_nativePlayer = (*java_env)->FindClass(java_env, "q2java/NativePlayer");
	if (CHECK_EXCEPTION() || !interface_nativePlayer)
		{
		java_error = "Can't find q2java.NativePlayer interface\n";
		return;
		}

	player_cvar = gi.cvar("q2java_player", "q2jgame.Player", CVAR_NOSET);

	// convert classname to the strange internal format Java
	// uses, where the periods are replaced with
	// forward slashes
	strcpy(buffer, player_cvar->string);
	for (p = buffer; *p; p++)
		if (*p == '.')
			*p = '/';

	class_player = (*java_env)->FindClass(java_env, buffer);
	if (CHECK_EXCEPTION() || !class_player)
		{
		java_error = "Couldn't find the specified player class\n";
		return;
		}

	if (!((*java_env)->IsAssignableFrom(java_env, class_player, interface_nativePlayer)))
		{
		java_error = "The class specified player class doesn't implement q2java.NativePlayer\n";
		return;
		}

	method_player_ctor = (*java_env)->GetMethodID(java_env, class_player, "<init>", "(Ljava/lang/String;Z)V");
	method_player_begin = (*java_env)->GetMethodID(java_env, class_player, "begin", "(Z)V");
	method_player_userinfoChanged = (*java_env)->GetMethodID(java_env, class_player, "userinfoChanged", "(Ljava/lang/String;)V");
	method_player_command = (*java_env)->GetMethodID(java_env, class_player, "command", "()V");
	method_player_think = (*java_env)->GetMethodID(java_env, class_player, "think", "()V");
	method_player_disconnect = (*java_env)->GetMethodID(java_env, class_player, "disconnect", "()V");
	if (CHECK_EXCEPTION())
		{
		java_error = "Problem finding one or more of the player methods\n";
		return;
		}
	}

void Player_javaFinalize()
	{
	}

static qboolean java_clientConnect(edict_t *ent, char *userinfo, qboolean loadgame)
	{
	jobject newPlayer;
	jstring juserinfo;
	int index = ent - ge.edicts;

	juserinfo = (*java_env)->NewStringUTF(java_env, userinfo);

	// create a new Java player object
	newPlayer = (*java_env)->AllocObject(java_env, class_player);
	// set the fEntityIndex field
	Entity_set_fEntityIndex(newPlayer, index);
	// call the constructor to finish initialization
	(*java_env)->CallVoidMethod(java_env, newPlayer, method_player_ctor, juserinfo, loadgame);

	// if an exception was thrown, reject the connection
	if (CHECK_EXCEPTION())
		{
		// make the Java Entity forget about itself
		Entity_set_fEntityIndex(newPlayer, -1);

		// remove the Java Entity from the Java array
		Entity_setEntity(index, 0);

		// wipe the old entity and client info out
		memset(ent->client, 0, sizeof(*(ent->client)));
		memset(ent, 0, sizeof(*ent));

		// relink the entity structure to the client structure
		ent->client = &(global_clients[index-1]);		
		return false;
		}

	ent->inuse = true;
	ent->s.number = index;

	return true;
	}


static void java_clientBegin(edict_t *ent, qboolean loadgame)
	{
	jobject javaPlayer;
	int index = ent - ge.edicts;

	javaPlayer = Entity_getEntity(index);
	(*java_env)->CallVoidMethod(java_env, javaPlayer, method_player_begin, loadgame);	
	CHECK_EXCEPTION();
/*
	gi.WriteByte(1);
	gi.WriteShort(ent-ge.edicts);
	gi.WriteByte(9);
	gi.multicast(ent->s.origin, 2);	
*/
	}

static void java_clientUserinfoChanged(edict_t *ent, char *userinfo)
	{
	jobject javaPlayer;
	jstring juserinfo;
	int index = ent - ge.edicts;

	javaPlayer = Entity_getEntity(index);
	juserinfo = (*java_env)->NewStringUTF(java_env, userinfo);
	(*java_env)->CallVoidMethod(java_env, javaPlayer, method_player_userinfoChanged, juserinfo);	
	CHECK_EXCEPTION();
	}

static void java_clientThink(edict_t *ent, usercmd_t *cmd)
	{
	jobject javaPlayer;
	int index = ent - ge.edicts;

	thinkCmd = cmd;

	javaPlayer = Entity_getEntity(index);
	(*java_env)->CallVoidMethod(java_env, javaPlayer, method_player_think);	
	CHECK_EXCEPTION();
	}		


static void java_clientCommand(edict_t *ent)
	{
	jobject javaPlayer;
	int index = ent - ge.edicts;

	javaPlayer = Entity_getEntity(index);
	(*java_env)->CallVoidMethod(java_env, javaPlayer, method_player_command);	
	CHECK_EXCEPTION();
	}


static void java_clientDisconnect(edict_t *ent)
	{
	jobject javaPlayer;
	int index = ent - ge.edicts;

	javaPlayer = Entity_getEntity(index);
	(*java_env)->CallVoidMethod(java_env, javaPlayer, method_player_disconnect);	
	CHECK_EXCEPTION();

	// unlink from world (---FIXME--- not sure about this)
	gi.unlinkentity (ent);		

	// make the Java Entity forget where it is in the C array
	Entity_set_fEntityIndex(javaPlayer, -1);

	// remove the entity reference from the Java array
	Entity_setEntity(index, 0);

	// wipe the old entity and client info out
	memset(ent->client, 0, sizeof(*(ent->client)));
	memset(ent, 0, sizeof(*ent));

	// relink the entity structure to the client structure
	ent->client = &(global_clients[index-1]);
	}



void Player_gameInit()
	{
	ge.ClientThink = java_clientThink;
	ge.ClientConnect = java_clientConnect;
	ge.ClientUserinfoChanged = java_clientUserinfoChanged;
	ge.ClientDisconnect = java_clientDisconnect;
	ge.ClientBegin = java_clientBegin;
	ge.ClientCommand = java_clientCommand;
	}

