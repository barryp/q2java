
public class GenericMobile extends Entity
	{
	
/**
 * Don't call this constructor, it's required for the DLL. But 
 * you can modify it if you need to initialize fields and methods
 * in the GenericMobile class.
 *
 * @param index int
 */
protected GenericMobile() throws GameException
	{
	Game.debugLog("Executing GenericMobile() constructor");	
	}	
public GenericMobile(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	}
}