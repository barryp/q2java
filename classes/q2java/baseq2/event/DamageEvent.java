package q2java.baseq2.event;

import javax.vecmath.*;
import q2java.core.event.*;
import q2java.baseq2.*;

/**
 * event used to describe damage done to player.
 * fields can be modified and will effect damage accordingly
 *
 * Updated to delegation event model Peter Donald 25/1/99
 * @author Brian Haskin
 */
public class DamageEvent extends GenericEvent
{
  public final static int DAMAGE_RADIUS = 0x00000001; // damage was indirect
  public final static int DAMAGE_NO_ARMOR = 0x00000002; // armour does not protect from this damage
  public final static int DAMAGE_ENERGY = 0x00000004; // damage is from an energy based weapon
  public final static int DAMAGE_NO_KNOCKBACK = 0x00000008; // do not affect velocity, just view angles
  public final static int DAMAGE_BULLET = 0x00000010; // damage is from a bullet (used for ricochets)
  public final static int DAMAGE_NO_PROTECTION = 0x00000020; // armor, shields, invulnerability, and godmode have no effect			

  public GameObject fVictim = null;
  public GameObject fAttacker = null;
  public Vector3f fDirection = null;
  public Point3f fPoint = null;
  public Vector3f fNormal = null;
  public int fAmount = 0;
  public int fKnockback = 0;
  public int fDamageFlags = 0;
  public int fTempEvent = 0;
  public String fObitKey = null;
  public int fArmorSave = 0; // For calculating blends
  public int fPowerArmorSave = 0; // For calculating blends
  public int fTakeDamage = 0; // For calculating blends

  // prolly not more than 4 damage events floating round at any one time 
  private static DamageEvent gCachedEvent = null;

  protected DamageEvent() { super(PLAYER_DAMAGE_EVENT); }      
  public DamageEvent(Object source, GameObject victim, GameObject attacker)
	{
	  super(source, PLAYER_DAMAGE_EVENT);
	  fVictim = victim;
	  fAttacker = attacker;
	}
  public final int getAmount() { return fAmount; }      
  public final int getArmorSave() { return fArmorSave; }      
  public final GameObject getAttacker() { return fAttacker; }      
  public final Vector3f getDamageDirection() { return fDirection; }      
  public final int getDamageFlags() { return fDamageFlags; }      
  public final Vector3f getDamageNormal() { return fNormal; }      
  public final Point3f getDamagePoint() { return fPoint; }      
  public static final DamageEvent getEvent( Object inflictor, 
						  GameObject attacker,
						  GameObject victim,
						  Vector3f dir,
						  Point3f point,
						  Vector3f normal,
						  int damage,
						  int knockback,
						  int dflags,
						  int tempEvent,
						  String obitKey )
	{
	  DamageEvent event = gCachedEvent;
	  gCachedEvent = null;

	  if( event == null )
	{
	  event = new DamageEvent();
	}
	  
	  event.source = inflictor;
	  event.fAttacker = attacker;
	  event.fVictim = victim;
	  event.fDirection = dir;
	  event.fPoint = point;
	  event.fNormal = normal;
	  event.fAmount = damage;
	  event.fKnockback = knockback;
	  event.fDamageFlags = dflags;
	  event.fTempEvent = tempEvent;
	  event.fObitKey = obitKey;
	  
	  return event; 
	}
  public final Object getInflictor() { return getSource(); }      
  public final int getKnockback() { return fKnockback; }      
  public final String getObitKey() { return fObitKey; }      
  public final int getPowerArmorSave() { return fPowerArmorSave; }      
  public final int getTakeDamage() { return fTakeDamage; }      
  public final int getTempEvent() { return fTempEvent; }      
  public final GameObject getVictim() { return fVictim; }          
  public final static void releaseEvent(DamageEvent event)
	{
	  gCachedEvent = event;
	  event.source = null;
	  event.fAttacker = null;
	  event.fDirection = null;
	  event.fPoint = null;
	  event.fNormal = null;
	  event.fObitKey = null;
	}
  /*
   * setter/getter for property amount.
   * how much damage is actually done.
   */
  public final void setAmount( int amount ) { fAmount = amount; }      
  /*
   * setter/getter for property armorSave.
   * for calculating blends ?????
   */
  public final void setArmorSave( int armorSave ) { fArmorSave = armorSave; }      
  /*
   * setters/getters for property DamageDirection.
   * damage direction indicates where the damage comes from
   */
  public final void setDamageDirection( Vector3f direction ) { fDirection = direction; }      
  /*
   * setter/getter for property damageFlags.
   * type of damage as specified above
   */
  public final void setDamageFlags( int damageFlags ) { fDamageFlags = damageFlags; }      
  /*
   * setter/getter for property DamageNormal.
   * Normal to which the damage was done.
   */
  public final void setDamageNormal( Vector3f normal ) { fNormal = normal; }      
  /*
   * setter/getter for property DamagePoint.
   * I think this is where on model damage occured ????
   */
  public final void setDamagePoint( Point3f point ) { fPoint = point; }      
  /*
   * setter/getter for property knockback.
   * sets amount of amount of knockback ... measured in ???
   */
  public final void setKnockback( int knockback ) { fKnockback = knockback; }      
  /*
   * setter/getter for property obitKey.
   * how was damaged
   */
  public final void setObitKey( String obitKey ) { fObitKey = obitKey; }      
  /*
   * setter/getter for property PowerArmorSave.
   * for calculating blends
   */
  public final void setPowerArmorSave( int powerArmorSave ) { fPowerArmorSave = powerArmorSave; }      
  /*
   * setter/getter for property TakeDamage.
   * for calcing blends...
   */
  public final void setTakeDamage( int takeDamage ) { fTakeDamage = takeDamage; }      
  /*
   * setter/getter for property tempEvent.
   * tempEvent to occur when hit ...
   */
  public final void setTempEvent( int tempEvent ) { fTempEvent = tempEvent; }      
}