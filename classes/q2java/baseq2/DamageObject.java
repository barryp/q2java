package baseq2;

import javax.vecmath.*;

/**
 * Complete representation of damage done to an object
 * @author Brian Haskin
 */
public class DamageObject
	{
	public GameObject fInflictor;
	public GameObject fAttacker;
	public GameObject fVictim;
	public Vector3f fDirection;
	public Point3f fPoint;
	public Vector3f fNormal;
	public int fAmount;
	public int fKnockback;
	public int fDFlags;
	public int fTempEvent;
	public String fObitKey;
	public int fArmorSave;		// For calculating blends
	public int fPowerArmorSave;	// For calculating blends
	public int fTakeDamage;		// For calculating blends

	// damage flags
	public final static int DAMAGE_RADIUS		= 0x00000001;	// damage was indirect
	public final static int DAMAGE_NO_ARMOR		= 0x00000002;	// armour does not protect from this damage
	public final static int DAMAGE_ENERGY		= 0x00000004;	// damage is from an energy based weapon
	public final static int DAMAGE_NO_KNOCKBACK	= 0x00000008;	// do not affect velocity, just view angles
	public final static int DAMAGE_BULLET		= 0x00000010; // damage is from a bullet (used for ricochets)
	public final static int DAMAGE_NO_PROTECTION	= 0x00000020; // armor, shields, invulnerability, and godmode have no effect			
	
/**
 * No-arg constructor.
 */
public DamageObject() 
	{
	}
public void set(GameObject inf, GameObject attacker, GameObject victim, Vector3f dir, Point3f pnt,
			 Vector3f norm, int dmg, int knock, int dflg, int tmpEvt, String obit)
	{
	fInflictor = inf;
	fAttacker = attacker;
	fVictim = victim;
	fDirection = dir;
	fPoint = pnt;
	fNormal = norm;
	fAmount = dmg;
	fKnockback = knock;
	fDFlags = dflg;
	fTempEvent = tmpEvt;
	fObitKey = obit;
	fArmorSave = 0;
	fPowerArmorSave = 0;
	fTakeDamage = 0;
	}
}