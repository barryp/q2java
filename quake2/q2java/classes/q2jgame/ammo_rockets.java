
public class ammo_rockets extends GenericAmmo
	{
	
public ammo_rockets(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/items/ammo/rockets/medium/tris.md2");
	linkEntity();
	}
}