package barryp.flashgrenade;

import java.util.Enumeration;
import javax.vecmath.*;
import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**
 * Grenades that have been thrown by a player,
 * and are flying through the air.
 */
  
public class HandGrenade extends q2java.baseq2.HandGrenade
	{
	
/**
 * No-arg constructor.
 */
public HandGrenade() 
	{
	}
protected void explode( TraceResults tr )
	{
	// perform normal explosion
	super.explode(tr);

	// iterate through players to flash their screens and possibly
	// blind them
	Point3f grenadeOrigin = fEntity.getOrigin();
	Vector3f forward = new Vector3f();
	Vector3f line = new Vector3f();
	Angle3f pva;
	Enumeration enum = NativeEntity.enumeratePlayerEntities();
	while (enum.hasMoreElements())
		{
		NativeEntity playerEnt = (NativeEntity) enum.nextElement();
		Point3f playerOrigin = playerEnt.getOrigin();
		TraceResults tr2 = Engine.trace(playerOrigin, grenadeOrigin, playerEnt, Engine.MASK_SHOT);
		if (Engine.inPHS(grenadeOrigin, playerOrigin) || (tr2.fFraction >= 1))
			{
			// setup a vector between player and explosion
			line.set(grenadeOrigin);
			line.sub(playerOrigin);

			pva = playerEnt.getPlayerViewAngles();
			pva.getVectors(forward, null, null);
			
			// calculate initial flash power based on how far away the player is, 
			// the power of the grenade, and how close the player is to facing the explosion directly
			float flashPower = (fDamage * (3.5F - forward.angle(line))) / line.length();
			// I know this should really be fDamage /lengthCubed - but that doesn't take reflections off walls into account
			if (flashPower > 10F)
				flashPower = 10F;			

			// everybody nearby sees the initial flash 
			Player p = (Player)(playerEnt.getReference());
			p.addBlend(1F, 1F, 1F, (flashPower > 1 ? 1 : flashPower));	

			// but if nothing's blocking it, and they're not the thrower or a teammate
			// make a more persistant blindness affect them
			if ((tr2.fFraction == 1) && (p != fOwner) && (!p.isTeammate((Player)fOwner)))
				{
				if (flashPower > 0.5F);
					// have the player scream if they're significantly affected
					playerEnt.sound(NativeEntity.CHAN_VOICE, Engine.getSoundIndex("player/burn1.wav"), (flashPower > 1 ? 1 : flashPower), NativeEntity.ATTN_NORM, 0);					
				new FlashBlindness(p, flashPower);
				}
			}
		}
	}
}