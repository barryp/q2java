#include "globals.h"

#include <string.h> // for memset()

#define ENTITY_PLAYER 2

static jclass interface_PlayerListener;


void Player_javaInit()
    {
    interface_PlayerListener = (*java_env)->FindClass(java_env, "q2java/PlayerListener");
    if (CHECK_EXCEPTION() || !interface_PlayerListener)
        {
        java_error = "Can't find q2java.PlayerListener interface\n";
        return;
        }
    }

void Player_javaDetach()
    {
    (*java_env)->DeleteLocalRef(java_env, interface_PlayerListener);
    }



static void java_clientDisconnect(edict_t *ent)
    {
    jobject javaPlayer;
    int index = ent - q2java_ge.edicts;

    // mark ourselves as not inuse, so that if any print calls
    // go through, they're ignored.
    ent->inuse = 0;

    javaPlayer = ent->client->listener;

    if (javaPlayer != NULL)
        {
        (*java_env)->CallVoidMethod(java_env, javaPlayer, ent->client->method_player_disconnect);    
        CHECK_EXCEPTION();
        }

    // unlink from world (---FIXME--- not sure about this)
    q2java_gi.unlinkentity (ent);      

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


static int java_clientConnect(edict_t *ent, char *userinfo)
    {
    jmethodID method_player_ctor;
    jclass class_player;
    jobject newPlayer;
    int rc;
    int index = ent - q2java_ge.edicts;

    javalink_debug("[C   ] clientConnect() starting ent = %d\n", (int)ent);

    // noticed in Q2 3.17 that you can connect multiple times
    // without disconnect automatically being called, try to catch that.
    if (ent->inuse)
        {
        javalink_debug("[C   ] java_clientConnect() caught connection to entity already in use\n");
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

    method_player_ctor = (*java_env)->GetMethodID(java_env, class_player, "<init>", "(II)V");
    if (CHECK_EXCEPTION())
        {
        java_error = "Couldn't get an (int, int) constructor  for the specified Player class\n";
        return 0;
        }


    // store a copy of the userinfo in the gclient_t structure
    if (userinfo)
        ent->client->playerInfo = (*java_env)->NewStringUTF(java_env, userinfo);
    if (CHECK_EXCEPTION())
        {
        java_error = "Couldn't create playerInfo string\n";
        return 0;
        }

    // create a new Java player object
    newPlayer = (*java_env)->NewObject(java_env, class_player, method_player_ctor, ENTITY_PLAYER, index);
    rc = CHECK_EXCEPTION();

    // if an exception was thrown, reject the connection
    if (rc || Game_playerConnect(newPlayer))
        {
        javalink_debug("[C   ] clientConnect() rejecting connection\n");
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

    // drop local references..this tends to crash with Jinsight
    if (!q2java_jinsight)
        {
        (*java_env)->DeleteLocalRef(java_env, newPlayer);
        (*java_env)->DeleteLocalRef(java_env, class_player);
        }

    javalink_debug("[C   ] clientConnect() finished ent = %d\n", (int)ent);
    return 1;
    }


static void java_clientBegin(edict_t *ent)
    {
    jobject javaPlayer = ent->client->listener;

    javalink_debug("[C   ] java_clientBegin() starting, javaPlayer = %d\n", (int)javaPlayer);
   
    if (javaPlayer != NULL)
        {
        (*java_env)->CallVoidMethod(java_env, javaPlayer, ent->client->method_player_begin);
        CHECK_EXCEPTION();
        }

    javalink_debug("[C   ] java_clientBegin() finished\n");
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
        (*java_env)->CallVoidMethod(java_env, client->listener, client->method_player_userinfoChanged, client->playerInfo); 
        CHECK_EXCEPTION();
        }
    }

static void java_clientCommand(edict_t *ent)
    {
    jobject javaPlayer = ent->client->listener;

    if (javaPlayer != NULL)
        {
        (*java_env)->CallVoidMethod(java_env, javaPlayer, ent->client->method_player_command);   
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

        (*java_env)->CallVoidMethod(java_env, javaPlayer, ent->client->method_player_think, playerCmd);
        CHECK_EXCEPTION();
        }
    }       



void Player_gameInit()
    {
    q2java_ge.ClientConnect = java_clientConnect;
    q2java_ge.ClientUserinfoChanged = java_clientUserinfoChanged;
    q2java_ge.ClientDisconnect = java_clientDisconnect;
    q2java_ge.ClientBegin = java_clientBegin;
    q2java_ge.ClientCommand = java_clientCommand;
    q2java_ge.ClientThink = java_clientThink;
    }

