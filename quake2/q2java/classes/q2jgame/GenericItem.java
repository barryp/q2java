
public class GenericItem extends Entity
	{
	
public GenericItem(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	setRenderFX(RF_GLOW); // all items glow
	setSolid(SOLID_BBOX);
	}
}