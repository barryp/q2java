
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
 * This method was created by a SmartGuide.
 */
public void runEntity() 
	{
	Vec3 temp = new Vec3(fOriginalOrigin);
	temp.z = temp.z + (float)(Math.sin(Game.fTime + fRandSeed) * 10);
	setOrigin(temp);
	}
}