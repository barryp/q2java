
public class ammo_cells extends GenericAmmo
	{
	
public ammo_cells(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/items/ammo/cells/medium/tris.md2");
	linkEntity();
	}
}