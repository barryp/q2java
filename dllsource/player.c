#include "globals.h"

#include <string.h> // for memset()

static jclass interface_PlayerListener;
static jmethodID method_player_begin;
static jmethodID method_player_userinfoChanged;
static jmethodID method_player_command;
static jmethodID method_player_disconnect;
static jmethodID method_player_think;


void Player_javaInit()
    {
    interface_PlayerListener = (*java_env)->FindClass(java_env, "q2java/PlayerListener");
    if (CHECK_EXCEPTION() || !interface_PlayerListener)
        {
        java_error = "Can't find q2java.PlayerListener interface\n";
        return;
        }

    method_player_begin = (*java_env)->GetMethodID(java_env, interface_PlayerListener, "playerBegin", "(Z)V");
    method_player_userinfoChanged = (*java_env)->GetMethodID(java_env, interface_PlayerListener, "playerInfoChanged", "(Ljava/lang/String;)V");
    method_player_command = (*java_env)->GetMethodID(java_env, interface_PlayerListener, "playerCommand", "()V");
    method_player_disconnect = (*java_env)->GetMethodID(java_env, interface_PlayerListener, "playerDisconnect", "()V");
    method_player_think = (*java_env)->GetMethodID(java_env, interface_PlayerListener, "playerThink", "(Lq2java/PlayerCmd;)V");
    if (CHECK_EXCEPTION())
        {
        java_error = "Problem finding one or more of the player methods\n";
        return;
        }
    }

void Player_javaFinalize()
    {
    (*java_env)->DeleteLocalRef(java_env, interface_PlayerListener);
    }



static void java_clientDisconnect(edict_t *ent)
    {
    jobject javaPlayer;
    int index = ent - ge.edicts;

    // mark ourselves as not inuse, so that if any print calls
    // go through, they're ignored.
    ent->inuse = 0;

    javaPlayer = ent->client->listener;

    if (javaPlayer != NULL)
        {
        (*java_env)->CallVoidMethod(java_env, javaPlayer, method_player_disconnect);    
        CHECK_EXCEPTION();
        }

    // unlink from world (---FIXME--- not sure about this)
    gi.unlinkentity (ent);      

    // make the Java Entity forget where it is in the C array
    Entity_set_fEntityIndex(javaPlayer, -1);

    // remove the entity reference from the Java array
    Entity_setEntity(index, 0);

    // remove the global reference to the PlayerListener
    if (ent->client->listener)
        (*java_env)->DeleteGlobalRef(java_env, ent->client->listener);

    // remove the local reference to the player info
    if (ent->client->playerInfo)
        (*java_env)->DeleteLocalRef(java_env, ent->client->playerInfo);

    // wipe the old entity and client info out
    memset(ent->client, 0, sizeof(*(ent->client)));
    memset(ent, 0, sizeof(*ent));

    // relink the entity structure to the client structure
    ent->client = &(global_clients[index-1]);
    }


static int java_clientConnect(edict_t *ent, char *userinfo, int loadgame)
    {
    jmethodID method_player_ctor;
    jclass class_player;
    jobject newPlayer;
    int index = ent - ge.edicts;

    // noticed in Q2 3.17 that you can connect multiple times
    // without disconnect automatically being called, try to catch that.
    if (ent->inuse)
        {
        debugLog("player.c::java_clientConnect() caught connection to entity already in use\n");
        java_clientDisconnect(ent);
        }

    class_player = Game_getPlayerClass();
    if (CHECK_EXCEPTION() || !class_player)
        {
        java_error = "Couldn't find the player class\n";
        return 0;
        }

    if (!((*java_env)->IsAssignableFrom(java_env, class_player, class_NativeEntity)))
        {
        java_error = "The current player class doesn't extend q2java.NativeEntity\n";
        return 0;
        }

    method_player_ctor = (*java_env)->GetMethodID(java_env, class_player, "<init>", "()V");
    if (CHECK_EXCEPTION())
        {
        java_error = "Couldn't get a no-arg constructor for the specified Player class\n";
        return 0;
        }

    // create a new Java player object
    newPlayer = (*java_env)->AllocObject(java_env, class_player);

    // set the fEntityIndex field
    Entity_set_fEntityIndex(newPlayer, index);

    // store a copy of the userinfo in the gclient_t structure
    if (userinfo)
        ent->client->playerInfo = (*java_env)->NewStringUTF(java_env, userinfo);

    // call the constructor to finish initialization
    (*java_env)->CallVoidMethod(java_env, newPlayer, method_player_ctor);

    // if an exception was thrown, reject the connection
    if (CHECK_EXCEPTION() || Game_playerConnect(newPlayer, loadgame))
        {
        // make the Java Entity forget about itself
        Entity_set_fEntityIndex(newPlayer, -1);

        // drop local references
        (*java_env)->DeleteLocalRef(java_env, newPlayer);
        (*java_env)->DeleteLocalRef(java_env, class_player);

        // remove the Java Entity from the Java array
        Entity_setEntity(index, 0);

        // delete any old java objects
        if (ent->client->playerInfo != NULL)
            (*java_env)->DeleteLocalRef(java_env, ent->client->playerInfo);
        if (ent->client->listener != NULL)
            (*java_env)->DeleteGlobalRef(java_env, ent->client->listener);

        // wipe the old entity and client info out
        memset(ent->client, 0, sizeof(*(ent->client)));
        memset(ent, 0, sizeof(*ent));

        // relink the entity structure to the client structure
        ent->client = &(global_clients[index-1]);       
        return 0;
        }

    ent->inuse = 1;
    ent->s.number = index;

    // drop local references
    (*java_env)->DeleteLocalRef(java_env, newPlayer);
    (*java_env)->DeleteLocalRef(java_env, class_player);

    return 1;
    }


static void java_clientBegin(edict_t *ent, int loadgame)
    {
    jobject javaPlayer = ent->client->listener;
    if (javaPlayer != NULL)
        {
        (*java_env)->CallVoidMethod(java_env, javaPlayer, method_player_begin, loadgame);   
        CHECK_EXCEPTION();
        }
    }

static void java_clientUserinfoChanged(edict_t *ent, char *userinfo)
    {
    gclient_t *client = ent->client;

    // delete any old java strings
    if (client->playerInfo != NULL)
        (*java_env)->DeleteLocalRef(java_env, client->playerInfo);

    // store a copy in the gclient_t structure
    if (userinfo)
        client->playerInfo = (*java_env)->NewStringUTF(java_env, userinfo);
    else
        client->playerInfo = NULL;

    // notify the listener that the userinfo changed
    if (client->listener != NULL)
        {
        (*java_env)->CallVoidMethod(java_env, client->listener, method_player_userinfoChanged, client->playerInfo); 
        CHECK_EXCEPTION();
        }
    }

static void java_clientCommand(edict_t *ent)
    {
    jobject javaPlayer = ent->client->listener;

    if (javaPlayer != NULL)
        {
        (*java_env)->CallVoidMethod(java_env, javaPlayer, method_player_command);   
        CHECK_EXCEPTION();
        }
    }



static void java_clientThink(edict_t *ent, usercmd_t *cmd)
    {
    jobject javaPlayer = ent->client->listener;

    if (javaPlayer != NULL)
        {
        setPlayerCmd(cmd->msec, cmd->buttons, 
            cmd->angles[0], cmd->angles[1], cmd->angles[2], 
            cmd->forwardmove, cmd->sidemove, cmd->upmove, 
            cmd->impulse, cmd->lightlevel); 

        (*java_env)->CallVoidMethod(java_env, javaPlayer, method_player_think, playerCmd);

        CHECK_EXCEPTION();
        }
    }       



void Player_gameInit()
    {
    ge.ClientConnect = java_clientConnect;
    ge.ClientUserinfoChanged = java_clientUserinfoChanged;
    ge.ClientDisconnect = java_clientDisconnect;
    ge.ClientBegin = java_clientBegin;
    ge.ClientCommand = java_clientCommand;
    ge.ClientThink = java_clientThink;
    }

