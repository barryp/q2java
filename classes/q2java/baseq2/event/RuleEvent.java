package q2java.baseq2.event;

import q2java.core.event.GenericEvent;

/**
 * Class for q2java Event delegation model.
 * all events that deal with the actual process of scoring and rules 
 * should inherit from this class.
 *
 * @author Peter Donald 25/1/99
 */
abstract public class RuleEvent extends GenericEvent
{
  public RuleEvent(int type)
	{
	  super(null,type); 
	}
  public RuleEvent(Object source, int type)
	{
	  super(source,type); 
	}
}