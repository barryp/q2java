package baseq2;

import q2java.*;

/**
 * Reduce damage based on armor the player is carrying.
 *
 * @author Barry Pederson
 */
public class ArmorDamageFilter implements DamageFilter
	{
	Player fOwner;
	
	private int fArmorCount;
	private int fArmorMaxCount;
	// what fraction of normal damage the player's armor absorbs. 
	protected float fArmorProtection;

	// fraction of energy damage the player's armor absorbs.
	protected float fArmorEnergyProtection;	
	
/**
 * No-arg constructor.
 */
public ArmorDamageFilter(Player p) 
	{
	fOwner = p;
	}
/**
 * This method was created by a SmartGuide.
 * @param amount int
 * @param maxAmount int
 * @param protection float
 * @param energyProtection float
 */
protected boolean addArmor(GenericArmor armor) 
	{
	int amount = armor.getArmorValue();
	int maxAmount = armor.getArmorMaxValue();
	
	// handle shards differently
	if (maxAmount == 0) 
		{
		if (fArmorCount == 0)
			{
			fOwner.fEntity.setPlayerStat(NativeEntity.STAT_ARMOR_ICON, (short) Engine.getImageIndex(armor.getIconName()));
			fArmorMaxCount = 50;
			fArmorProtection = 0.3F;			
			}
		fArmorCount += amount;
		fOwner.fEntity.setPlayerStat(NativeEntity.STAT_ARMOR, (short)fArmorCount);
		return true;
		}

	// is this an armor upgrade?
	float protection = armor.getProtectionFactor();
	if (protection > fArmorProtection)
		{ 
		int salvage = (int)((fArmorProtection / protection) * fArmorCount);
		fArmorCount = amount + salvage;
		fArmorMaxCount = maxAmount;
		fArmorProtection = protection;
		fArmorEnergyProtection = armor.getEnergyProtectionFactor();		
		fOwner.fEntity.setPlayerStat(NativeEntity.STAT_ARMOR, (short)fArmorCount);
		fOwner.fEntity.setPlayerStat(NativeEntity.STAT_ARMOR_ICON, (short) Engine.getImageIndex(armor.getIconName()));
		return true;
		}		

	// is our armor not up to capacity?		
	if (fArmorCount < fArmorMaxCount)		
		{
		fArmorCount = Math.min(fArmorMaxCount, fArmorCount + amount);
		fOwner.fEntity.setPlayerStat(NativeEntity.STAT_ARMOR, (short)fArmorCount);
		return true;
		}		

	// guess we don't need this armor
	return false;		
	}
/**
 * Method to implement in order to filter a player's damage.
 * @param DamageObject - damage to be filtered.
 */
public DamageObject filterDamage(DamageObject damage)
	{
	// decrease damage based on armor
	if ((damage.fDFlags & DamageObject.DAMAGE_NO_ARMOR) != 0)
		return damage;
		
	int save; // the amount of damage our armor protects us from
	if ((damage.fDFlags & DamageObject.DAMAGE_ENERGY) != 0)
		{
		save = (int) Math.ceil(damage.fAmount * fArmorEnergyProtection);
		damage.fPowerArmorSave += save;
		}
	else
		{
		save = (int) Math.ceil(damage.fAmount * fArmorProtection);
		damage.fArmorSave += save;
		}
			
	save = Math.min(save, fArmorCount);
		
	// FIXME: Do we need to do any adjustments here for armorSave or
	// powerArmorSave based on fArmorCount? (TSW)
	damage.fTakeDamage += damage.fAmount;				// for blends (TSW)

	if (save > 0)
		{
		damage.fAmount -= save;
		// Doesn't this mean that takeDamage ends up the same as amount? Can we just get rid of takeDamage? - BH
		damage.fTakeDamage -= damage.fArmorSave;		// for blends (TSW)
		damage.fTakeDamage -= damage.fPowerArmorSave;	// for blends (TSW)
		fArmorCount -= save;
		fOwner.fEntity.setPlayerStat(NativeEntity.STAT_ARMOR, (short)fArmorCount);
		if (fArmorCount == 0) // if armor is depleted we need to reset it
			{
			fArmorProtection = 0.3F;
			fArmorEnergyProtection = 0.0F;
			fArmorMaxCount = 50;
			fOwner.fEntity.setPlayerStat(NativeEntity.STAT_ARMOR_ICON, (short) 0);
			}
		fOwner.spawnDamage(Engine.TE_SPARKS, damage.fPoint, damage.fNormal, save);
		}
		
	return damage;		
	}
/** 
 * Get the Player's amount of Armor
 */
public int getArmorCount()
	{
	return fArmorCount;
	}
/**
 * Get the Player's max armor capacity
 */
public int getArmorMaxCount()
	{
	return fArmorMaxCount;
	}
/**
 * Reset armor to basic jacket-level protection.
 */
public void reset() 
	{
	fArmorCount = 0;
	fArmorMaxCount =  0;
	fArmorProtection = 0; // almost, but not quite as good as jacket_armor
	fArmorEnergyProtection = 0.0F;
	fOwner.fEntity.setPlayerStat(NativeEntity.STAT_ARMOR_ICON, (short) 0);	
	}
/** 
 * Set the player's armor.  It can't go below zero or above the max armor count.
 * @param amount amount of armor
 */
public void setArmorCount(int amount)
	{
	setArmorCount(amount, true);
	}
/** 
 * Set the player's armor.  It can't go below zero or above the max armor count.
 * @param amount amount of armor
 * @param isAbsolute Whether the amount is an absolute value, or relative to the current armor.
 */
public void setArmorCount(int amount, boolean isAbsolute)
	{
	int newValue;
	
	if (isAbsolute)
		newValue = amount;
	else
		newValue = fArmorCount + amount;
		
	fArmorCount = Math.max( 0, Math.min(newValue, fArmorMaxCount) );
	fOwner.fEntity.setPlayerStat(NativeEntity.STAT_ARMOR, (short)fArmorCount);
	}
/**
 * Set the Player's max possible armor amount
 */
public void setArmorMaxCount( int maxCount )
	{
	fArmorMaxCount = maxCount;
	}
}