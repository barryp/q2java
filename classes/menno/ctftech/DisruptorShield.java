package menno.ctftech;


/*
======================================================================================
==                                 Q2JAVA CTF                                       ==
==                                                                                  ==
==                   Author: Menno van Gangelen <menno@element.nl>                  ==
==                                                                                  ==
==            Based on q2java by: Barry Pederson <bpederson@geocities.com>          ==
==                                                                                  ==
== All sources are free for non-commercial use, as long as the licence agreement of ==
== ID software's quake2 is not violated and the names of the authors of q2java and  ==
== q2java-ctf are included.                                                         ==
======================================================================================
*/


import q2java.*;
import q2jgame.*;
import baseq2.DamageFilter;
import baseq2.DamageObject;
import javax.vecmath.*;


public class DisruptorShield extends GenericTech implements baseq2.DamageFilter
{
	protected final static float DAMAGE_MULTIPLIER = 0.5f;
	public DisruptorShield(int hudStat) throws GameException
	{
		super(hudStat);
	}
	/**
	 * Method to implement in order to filter a player's damage.
	 * @param DamageObject - damage to be filtered.
	 */
	public DamageObject filterDamage(DamageObject damage)
	{
		float volume = 1f;
		//if (self->owner->client->silencer_shots)
		//	volume = 0.2;
		getOwner().fEntity.sound( NativeEntity.CHAN_VOICE, Engine.getSoundIndex("ctf/tech1.wav"), volume, NativeEntity.ATTN_NORM, 0);
	
		damage.fAmount *= DAMAGE_MULTIPLIER;
		return damage;
	}
	/**
	 * Get the name of this item's icon.
	 * @return java.lang.String
	 */
	public String getIconName() 
	{
		return "tech1";
	}
	/**
	 * Get the name of this item.
	 * @return java.lang.String
 	*/
	public String getItemName() 
	{
		return "Disruptor Shield";
	}
	/**
	 * Get the name of this item's model.
 	* @return java.lang.String
	 */
	public String getModelName() 
	{
		return "models/ctf/resistance/tris.md2";	
	}
	/**
	 * Set which player is holding the tech.
	 * @param p menno.ctf.Player
	 */
	public void setOwner(baseq2.Player p) 
	{
		if (p == null)
			getOwner().removeDamageFilter(this);
		else
			p.addDamageFilter(this);
			
		super.setOwner(p);
	}
}