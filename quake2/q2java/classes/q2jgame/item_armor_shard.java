
package q2jgame;

import q2java.*;

public class item_armor_shard extends GenericArmor
	{
	private Vec3 fOriginalOrigin;
	private double fRandSeed;
	
public item_armor_shard(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/items/armor/shard/tris.md2");
	fOriginalOrigin = getOrigin();
	fRandSeed = Math.random();
	linkEntity();
	}
/**
 * Have the shards bob up and down just as a test
 */
public void runFrame() 
	{
	Vec3 temp = new Vec3(fOriginalOrigin);
	temp.z = temp.z + (float)(Math.sin(Game.fGameTime + fRandSeed) * 10);
	setOrigin(temp);
	}
}