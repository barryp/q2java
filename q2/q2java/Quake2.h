// Quake2.h -- game dll information visible to server
//
// This has been really chopped down from the original 
// source release's game.h, g_locals.h, and q_shared.h
// to just get the bare minimum necessary to cleanly 
// compile and run without crashing

#ifndef _Included_Quake2
#define _Included_Quake2

#define GAME_API_VERSION    2

#define SECONDS_PER_FRAME 0.1

// memory tags to allow dynamic memory to be cleaned up
#define	TAG_GAME	765		// clear when unloading the dll
#define	TAG_LEVEL	766		// clear when loading a new level

//
// per-level limits
//
#define	MAX_CLIENTS			256		// absolute limit
#define	MAX_EDICTS			1024	// must change protocol to increase more
#define	MAX_LIGHTSTYLES		256
#define	MAX_MODELS			256		// these are sent over the net as bytes
#define	MAX_SOUNDS			256		// so they cannot be blindly increased
#define	MAX_IMAGES			256
#define	MAX_ITEMS			256

/*
==============================================================

COLLISION DETECTION

==============================================================
*/

// lower bits are stronger, and will eat weaker brushes completely
#define	CONTENTS_SOLID			1		// an eye is never valid in a solid
#define	CONTENTS_WINDOW			2		// translucent, but not watery
#define	CONTENTS_AUX			4
#define	CONTENTS_LAVA			8
#define	CONTENTS_SLIME			16
#define	CONTENTS_WATER			32
#define	CONTENTS_MIST			64
#define	LAST_VISIBLE_CONTENTS	64

// remaining contents are non-visible, and don't eat brushes

#define	CONTENTS_AREAPORTAL		0x8000

#define	CONTENTS_PLAYERCLIP		0x10000
#define	CONTENTS_MONSTERCLIP	0x20000

// currents can be added to any other contents, and may be mixed
#define	CONTENTS_CURRENT_0		0x40000
#define	CONTENTS_CURRENT_90		0x80000
#define	CONTENTS_CURRENT_180	0x100000
#define	CONTENTS_CURRENT_270	0x200000
#define	CONTENTS_CURRENT_UP		0x400000
#define	CONTENTS_CURRENT_DOWN	0x800000

#define	CONTENTS_ORIGIN			0x1000000	// removed before bsping an entity

#define	CONTENTS_MONSTER		0x2000000	// should never be on a brush, only in game
#define	CONTENTS_DEADMONSTER	0x4000000
#define	CONTENTS_DETAIL			0x8000000	// brushes to be added after vis leafs
#define	CONTENTS_TRANSLUCENT	0x10000000	// auto set if any surface has trans
#define	CONTENTS_LADDER			0x20000000

// content masks
#define	MASK_ALL				(-1)
#define	MASK_SOLID				(CONTENTS_SOLID|CONTENTS_WINDOW)
#define	MASK_PLAYERSOLID		(CONTENTS_SOLID|CONTENTS_PLAYERCLIP|CONTENTS_WINDOW|CONTENTS_MONSTER)
#define	MASK_DEADSOLID			(CONTENTS_SOLID|CONTENTS_PLAYERCLIP|CONTENTS_WINDOW)
#define	MASK_MONSTERSOLID		(CONTENTS_SOLID|CONTENTS_MONSTERCLIP|CONTENTS_WINDOW|CONTENTS_MONSTER)
#define	MASK_WATER				(CONTENTS_WATER|CONTENTS_LAVA|CONTENTS_SLIME)
#define	MASK_OPAQUE				(CONTENTS_SOLID|CONTENTS_SLIME|CONTENTS_LAVA)
#define	MASK_SHOT				(CONTENTS_SOLID|CONTENTS_MONSTER|CONTENTS_WINDOW|CONTENTS_DEADMONSTER)
#define MASK_CURRENT			(CONTENTS_CURRENT_0|CONTENTS_CURRENT_90|CONTENTS_CURRENT_180|CONTENTS_CURRENT_270|CONTENTS_CURRENT_UP|CONTENTS_CURRENT_DOWN)

#define	CVAR_ARCHIVE	1	// set to cause it to be saved to vars.rc
#define	CVAR_USERINFO	2	// added to userinfo  when changed
#define	CVAR_SERVERINFO	4	// added to serverinfo when changed
#define	CVAR_NOSET		8	// don't allow change from console at all,
							// but can be set from the command line
#define	CVAR_LATCH		16	// save changes until server restart




typedef enum {false, true}      qboolean;
typedef unsigned char           byte;
typedef float vec_t;
typedef vec_t vec3_t[3];

typedef struct edict_s edict_t;

// edict->solid values
typedef enum
    {
    SOLID_NOT,          // no interaction with other objects
    SOLID_TRIGGER,      // only touch when inside, after moving
    SOLID_BBOX,         // touch on edge
    SOLID_BSP           // bsp clip, touch on edge
    } solid_t;



// link_t is only used for entity area links now
typedef struct link_s
    {
    struct link_s   *prev, *next;
    } link_t;



#define MAX_ENT_CLUSTERS    16



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



typedef struct edict_s
    {
    entity_state_t  s;
    struct gclient_s    *client;
    qboolean    inuse;
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
    solid_t     solid;
    int         clipmask;
    edict_t     *owner;


	// DO NOT MODIFY ANYTHING ABOVE THIS, THE SERVER
	// EXPECTS THE FIELDS IN THAT ORDER!

	//================================
	

    // the game dll can add anything it wants after
    // this point in the structure
	int 		freetime;			// frame when the object was freed
	vec3_t		velocity;
    } edict_t;



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
    qboolean    allsolid;   // if true, plane is not valid
    qboolean    startsolid; // if true, the initial point was in a solid area
    float       fraction;   // time completed, 1.0 = didn't hit anything
    vec3_t      endpos;     // final position
    cplane_t    plane;      // surface normal at impact
    csurface_t  *surface;   // surface hit
    int         contents;   // contents on other side of surface hit
    struct edict_s  *ent;       // not set by CM_*() functions
	} trace_t;


// pmove_state_t is the information necessary for client side movement
// prediction
typedef enum
	{
    // can accelerate and turn
    PM_NORMAL,
    PM_SPECTATOR,
    // no acceleration or turning
    PM_DEAD,
    PM_GIB,     // different bounding box
    PM_FREEZE
	} pmtype_t;


// this structure needs to be communicated bit-accurate
// from the server to the client to guarantee that
// prediction stays in sync, so no floats are used.
// if any part of the game code modifies this struct, it
// will result in a prediction error of some degree.
typedef struct
	{
    pmtype_t    pm_type;

    short       origin[3];      // 12.3
    short       velocity[3];    // 12.3
    byte        pm_flags;       // ducked, jump_held, etc
    byte        teleport_time;
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
    qboolean        snapinitial;    // if s has been changed outside pmove

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

	pmove_state_t		old_pmove;	// for detecting out-of-pmove changes
    } gclient_t;


// destination class for gi.multicast()
typedef enum
    {
    MULTICAST_ALL,
    MULTICAST_PHS,
    MULTICAST_PVS,
    MULTICAST_ALL_R,
    MULTICAST_PHS_R,
    MULTICAST_PVS_R
    } multicast_t;

// nothing outside the Cvar_*() functions should modify these fields!
typedef struct cvar_s
{
    char        *name;
    char        *string;
    char        *latched_string;    // for CVAR_LATCH vars
    int         flags;
    qboolean    modified;   // set each time the cvar is changed
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
    qboolean    (*inPVS) (vec3_t p1, vec3_t p2);
    qboolean    (*inPHS) (vec3_t p1, vec3_t p2);
    void        (*SetAreaPortalState) (int portalnum, qboolean open);
    qboolean    (*AreasConnected) (int area1, int area2);

    // an entity will never be sent to a client or used for collision
    // if it is not passed to linkentity.  If the size, position, or
    // solidity changes, it must be relinked.
    void    (*linkentity) (edict_t *ent);
    void    (*unlinkentity) (edict_t *ent);     // call before removing an interactive edict
    int     (*BoxEdicts) (vec3_t mins, vec3_t maxs, edict_t **list, int maxcount, int areatype);
    void    (*Pmove) (pmove_t *pmove);      // player movement code common with client prediction

    // network messaging
    void    (*multicast) (vec3_t origin, multicast_t to);
    void    (*unicast) (edict_t *ent, qboolean reliable);
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

    qboolean    (*ClientConnect) (edict_t *ent, char *userinfo, qboolean loadgame);
    void        (*ClientBegin) (edict_t *ent, qboolean loadgame);
    void        (*ClientUserinfoChanged) (edict_t *ent, char *userinfo);
    void        (*ClientDisconnect) (edict_t *ent);
    void        (*ClientCommand) (edict_t *ent);
    void        (*ClientThink) (edict_t *ent, usercmd_t *cmd);

    void        (*RunFrame) (void);

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