
package q2jgame;

/**
 * An interface for those objects which wish to be notified when a package is
 * added or removed from a running game.
 * <P>
 * This is useful if packages wants to communicate amongst themselves, or
 * adjust behaviour based on other packages. It also allows package dependencies
 * to be managed. e.g. a package can unload itself if another package has been unloaded
 * thereby minimising some potential runtime errors.
 *
 * @version	0.1
 * @author 	Leigh Dodds
 */
public interface ModuleListener 
	{
	
	/**
	 * A given package has been loaded into the game.
	 * @param A reference to the GameModule class for that package
	 */
	public void moduleAdded(GameModule gm);
	/**
	 * A given package has been unloaded from the game.
	 * @param A reference to the GameModule class for that package
	 */
	public void moduleRemoved(GameModule gm);
}