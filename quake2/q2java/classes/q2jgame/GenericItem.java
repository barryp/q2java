
package q2jgame;

import q2java.*;

public class GenericItem extends GameEntity
	{
	public int fPickupSoundIndex;
	
public GenericItem(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setRenderFX(RF_GLOW); // all items glow
	setSolid(SOLID_TRIGGER);
	fPickupSoundIndex = Engine.soundIndex("items/pkup.wav");
	}
/**
 * This method was created by a SmartGuide.
 * @param mob q2jgame.GenericMobile
 */
public void touch(GenericCharacter mob) 
	{
	sound(CHAN_ITEM, fPickupSoundIndex, 1, ATTN_NORM, 0);
	}
}