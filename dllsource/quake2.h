// Quake2.h -- game dll information visible to server
//
// This has been really chopped down from the original 
// source release's game.h, g_locals.h, and q_shared.h
// to just get the bare minimum necessary to cleanly 
// compile and run without crashing

#ifndef _Included_Quake2
#define _Included_Quake2

#include <jni.h> // for jobject and jstring in gclient_t

#define GAME_API_VERSION    3

#define SECONDS_PER_FRAME 0.1

#define SVF_NOCLIENT            0x00000001  // don't send entity to clients, even if it has effects

// memory tags to allow dynamic memory to be cleaned up
#define TAG_GAME    765     // clear when unloading the dll

// per-level limits
#define MAX_EDICTS          1024    // must change protocol to increase more

// CVar constants
#define CVAR_ARCHIVE    1   // set to cause it to be saved to vars.rc
#define CVAR_USERINFO   2   // added to userinfo  when changed
#define CVAR_SERVERINFO 4   // added to serverinfo when changed
#define CVAR_NOSET      8   // don't allow change from console at all,
                            // but can be set from the command line
#define CVAR_LATCH      16  // save changes until server restart


typedef unsigned char           byte;
typedef float vec_t;
typedef vec_t vec3_t[3];

typedef struct edict_s edict_t;

// link_t is only used for entity area links now
typedef struct link_s
    {
    struct link_s   *prev, *next;
    } link_t;




// entity_state_t is the information conveyed from the server
// in an update message about entities that the client will
// need to render in some way
typedef struct entity_state_s
    {
    int     number;         // edict index

    vec3_t  origin;
    vec3_t  angles;
    vec3_t  old_origin;     // for lerping
    int     modelindex;
    int     modelindex2, modelindex3, modelindex4;  // weapons, CTF flags, etc
    int     frame;
    int     skinnum;
    int     effects;
    int     renderfx;
    int     solid;          // for client side prediction, 8*(bits 0-4) is x/y radius
                            // 8*(bits 5-9) is z down distance, 8(bits10-15) is z up
                            // gi.linkentity sets this properly
    int     sound;          // for looping sounds, to guarantee shutoff
    int     event;          // impulse events -- muzzle flashes, footsteps, etc
                            // events only go out for a single frame, they
                            // are automatically cleared each frame
    } entity_state_t;


#define MAX_ENT_CLUSTERS    16

struct edict_s
    {
    entity_state_t  s;
    struct gclient_s    *client;
    int         inuse;
    int         linkcount;

    // FIXME: move these fields to a server private sv_entity_t
    link_t      area;               // linked to a division node or leaf
    
    int         num_clusters;       // if -1, use headnode instead
    int         clusternums[MAX_ENT_CLUSTERS];
    int         headnode;           // unused if num_clusters != -1
    int         areanum, areanum2;

    //================================

    int         svflags;            // SVF_NOCLIENT, SVF_DEADMONSTER, SVF_MONSTER, etc
    vec3_t      mins, maxs;
    vec3_t      absmin, absmax, size;
    int         solid;
    int         clipmask;
    edict_t     *owner;


    // DO NOT MODIFY ANYTHING ABOVE THIS, THE SERVER
    // EXPECTS THE FIELDS IN THAT ORDER!

    //================================
    

    // the game dll can add anything it wants after
    // this point in the structure
    float       freetime;           // frame when the object was freed, also used to sort entities by getRadiusEntities()
    vec3_t      velocity;
    edict_t     *groundentity;
    };



// usercmd_t is sent to the server each client frame
typedef struct usercmd_s
    {
    byte    msec;
    byte    buttons;
    short   angles[3];
    short   forwardmove, sidemove, upmove;
    byte    impulse;        // remove?
    byte    lightlevel;     // light level the player is standing on
    } usercmd_t;


// plane_t structure
// !!! if this is changed, it must be changed in asm code too !!!
typedef struct cplane_s
    {
    vec3_t  normal;
    float   dist;
    byte    type;           // for fast side tests
    byte    signbits;       // signx + (signy<<1) + (signz<<1)
    byte    pad[2];
    } cplane_t;

typedef struct csurface_s
    {
    char        name[16];
    int         flags;
    int         value;
    } csurface_t;

// a trace is returned when a box is swept through the world
typedef struct
    {
    int    allsolid;   // if true, plane is not valid
    int    startsolid; // if true, the initial point was in a solid area
    float       fraction;   // time completed, 1.0 = didn't hit anything
    vec3_t      endpos;     // final position
    cplane_t    plane;      // surface normal at impact
    csurface_t  *surface;   // surface hit
    int         contents;   // contents on other side of surface hit
    struct edict_s  *ent;       // not set by CM_*() functions
    } trace_t;


// this structure needs to be communicated bit-accurate
// from the server to the client to guarantee that
// prediction stays in sync, so no floats are used.
// if any part of the game code modifies this struct, it
// will result in a prediction error of some degree.
typedef struct
    {
    int         pm_type;

    short       origin[3];      // 12.3
    short       velocity[3];    // 12.3
    byte        pm_flags;       // ducked, jump_held, etc
	byte		pm_time;		// each unit = 8 ms
    short       gravity;
    short       delta_angles[3];    // add to command angles to get view direction
                                    // changed by spawns, rotating objects, and teleporters
    } pmove_state_t;

#define MAXTOUCH    32
typedef struct
    {
    // state (in / out)
    pmove_state_t   s;

    // command (in)
    usercmd_t       cmd;
    int        snapinitial;    // if s has been changed outside pmove

    // results (out)
    int         numtouch;
    struct edict_s  *touchents[MAXTOUCH];

    vec3_t      viewangles;         // clamped
    float       viewheight;

    vec3_t      mins, maxs;         // bounding box size

    struct edict_s  *groundentity;
    int         watertype;
    int         waterlevel;

    // callbacks to test the world
    trace_t     (*trace) (vec3_t start, vec3_t mins, vec3_t maxs, vec3_t end);
    int         (*pointcontents) (vec3_t point);
    } pmove_t;


#define MAX_STATS               32
// player_state_t is the information needed in addition to pmove_state_t
// to rendered a view.  There will only be 10 player_state_t sent each second,
// but the number of pmove_state_t changes will be reletive to client
// frame rates
typedef struct
    {
    pmove_state_t   pmove;      // for prediction

    // these fields do not need to be communicated bit-precise

    vec3_t      viewangles;     // for fixed views
    vec3_t      viewoffset;     // add to pmovestate->origin
    vec3_t      kick_angles;    // add to view direction to get render angles
                                // set by weapon kicks, pain effects, etc

    vec3_t      gunangles;
    vec3_t      gunoffset;
    int         gunindex;
    int         gunframe;

    float       blend[4];       // rgba full screen effect

    float       fov;            // horizontal field of view

    int         rdflags;        // refdef flags

    short       stats[MAX_STATS];       // fast status bar updates
    } player_state_t;


typedef struct gclient_s
    {
    player_state_t  ps;     // communicated by server to clients
    int             ping;

    // the game dll can add anything it wants after
    // this point in the structure

    pmove_state_t       old_pmove;  // for detecting out-of-pmove changes

    jobject listener;
    jstring playerInfo;
    } gclient_t;



// nothing outside the Cvar_*() functions should modify these fields!
typedef struct cvar_s
{
    char        *name;
    char        *string;
    char        *latched_string;    // for CVAR_LATCH vars
    int         flags;
    int    modified;   // set each time the cvar is changed
    float       value;
    struct cvar_s *next;
} cvar_t;

//===============================================================

//
// functions provided by the main engine
//
typedef struct
    {
    // special messages
    void    (*bprintf) (int printlevel, char *fmt, ...);
    void    (*dprintf) (char *fmt, ...);
    void    (*cprintf) (edict_t *ent, int printlevel, char *fmt, ...);
    void    (*centerprintf) (edict_t *ent, char *fmt, ...);
    void    (*sound) (edict_t *ent, int channel, int soundindex, float volume, float attenuation, float timeofs);
    void    (*positioned_sound) (vec3_t origin, edict_t *ent, int channel, int soundinedex, float volume, float attenuation, float timeofs);

    // config strings hold all the index strings, the lightstyles,
    // and misc data like the sky definition and cdtrack.
    // All of the current configstrings are sent to clients when
    // they connect, and changes are sent to all connected clients.
    void    (*configstring) (int num, char *string);

    void    (*error) (char *fmt, ...);

    // new names can only be added during spawning
    // existing names can be looked up at any time
    int     (*modelindex) (char *name);
    int     (*soundindex) (char *name);
    int     (*imageindex) (char *name);

    void    (*setmodel) (edict_t *ent, char *name);

    // collision detection
    trace_t (*trace) (vec3_t start, vec3_t mins, vec3_t maxs, vec3_t end, edict_t *passent, int contentmask);
    int     (*pointcontents) (vec3_t point);
    int    (*inPVS) (vec3_t p1, vec3_t p2);
    int    (*inPHS) (vec3_t p1, vec3_t p2);
    void        (*SetAreaPortalState) (int portalnum, int open);
    int    (*AreasConnected) (int area1, int area2);

    // an entity will never be sent to a client or used for collision
    // if it is not passed to linkentity.  If the size, position, or
    // solidity changes, it must be relinked.
    void    (*linkentity) (edict_t *ent);
    void    (*unlinkentity) (edict_t *ent);     // call before removing an interactive edict
    int     (*BoxEdicts) (vec3_t mins, vec3_t maxs, edict_t **list, int maxcount, int areatype);
    void    (*Pmove) (pmove_t *pmove);      // player movement code common with client prediction

    // network messaging
    void    (*multicast) (vec3_t origin, int to);
    void    (*unicast) (edict_t *ent, int reliable);
    void    (*WriteChar) (int c);
    void    (*WriteByte) (int c);
    void    (*WriteShort) (int c);
    void    (*WriteLong) (int c);
    void    (*WriteFloat) (float f);
    void    (*WriteString) (char *s);
    void    (*WritePosition) (vec3_t pos);  // some fractional bits
    void    (*WriteDir) (vec3_t pos);       // single byte encoded, very coarse
    void    (*WriteAngle) (float f);

    // managed memory allocation
    void    *(*TagMalloc) (int size, int tag);
    void    (*TagFree) (void *block);
    void    (*FreeTags) (int tag);

    // console variable interaction
    cvar_t  *(*cvar) (char *var_name, char *value, int flags);
    cvar_t  *(*cvar_set) (char *var_name, char *value);
    cvar_t  *(*cvar_forceset) (char *var_name, char *value);

    // ClientCommand and coneole command parameter checking
    int     (*argc) (void);
    char    *(*argv) (int n);
    char    *(*args) (void);

    // add commands to the server console as if they were typed in
    // for map changing, etc
    void    (*AddCommandString) (char *text);

    void    (*DebugGraph) (float value, int color);
    } game_import_t;

//
// functions exported by the game subsystem
//
typedef struct
    {
    int         apiversion;

    // the init function will only be called when a game starts,
    // not each time a level is loaded.  Persistant data for clients
    // and the server can be allocated in init
    void        (*Init) (void);
    void        (*Shutdown) (void);

    // each new level entered will cause a call to SpawnEntities
    void        (*SpawnEntities) (char *mapname, char *entstring, char *spawnpoint);

    // Read/Write Game is for storing persistant cross level information
    // about the world state and the clients.
    // WriteGame is called every time a level is exited.
    // ReadGame is called on a loadgame.
    void        (*WriteGame) (char *filename);
    void        (*ReadGame) (char *filename);

    // ReadLevel is called after the default map information has been
    // loaded with SpawnEntities, so any stored client spawn spots will
    // be used when the clients reconnect.
    void        (*WriteLevel) (char *filename);
    void        (*ReadLevel) (char *filename);

    int    (*ClientConnect) (edict_t *ent, char *userinfo, int loadgame);
    void        (*ClientBegin) (edict_t *ent, int loadgame);
    void        (*ClientUserinfoChanged) (edict_t *ent, char *userinfo);
    void        (*ClientDisconnect) (edict_t *ent);
    void        (*ClientCommand) (edict_t *ent);
    void        (*ClientThink) (edict_t *ent, usercmd_t *cmd);

    void        (*RunFrame) (void);

    // ServerCommand will be called when an "sv <command>" command is issued on the
    // server console.
    // The game can issue gi.argc() / gi.argv() commands to get the rest
    // of the parameters
    void        (*ServerCommand) (void);

    //
    // global variables shared between game and server
    //

    // The edict array is allocated in the game dll so it
    // can vary in size from one game to another.
    // 
    // The size will be fixed when ge->Init() is called
    struct edict_s  *edicts;
    int         edict_size;
    int         num_edicts;     // current number, <= max_edicts
    int         max_edicts;
    } game_export_t;

game_export_t *GetGameApi (game_import_t *import);

#endif