
public class item_health_small extends GenericHealth
	{
	
public item_health_small(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setModel("models/items/healing/stimpack/tris.md2");
	linkEntity();
	}
}