package q2java.core.event;

/**
 * Class for q2java Event delegation model.
 * all events that deal with the actual process of setting up and runnning the game
 * should inherit from this class.
 *
 * @author Peter Donald 25/1/99
 */
abstract public class GameEvent extends GenericEvent
{
  public GameEvent(int type)
	{
	  // if ever implement single player then have to include
	  // the local gameinfo as source
	  super(null,type); 
	}
  public GameEvent(Object source, int type)
	{
	  super(source,type); 
	}
}