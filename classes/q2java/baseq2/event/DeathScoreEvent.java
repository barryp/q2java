package q2java.baseq2.event;

import javax.vecmath.*;
import q2java.baseq2.*;

/**
 * event used to notify of player deaths for scoring purposes.
 *
 * @author Peter Donald 25/1/99
 */
public class DeathScoreEvent extends ScoreEvent
{
  protected GameObject fKiller;
  protected GameObject fVictim;
  protected String fObitKey;
  protected GameObject fInflictor;
  protected int fScoreChange;
  
  // prolly not more than 2 events floating round at any one time 
  private static DeathScoreEvent gCachedEvent = null;

  protected DeathScoreEvent()
	{
	  super(RULE_DEATHSCORE_EVENT);
	}
  public DeathScoreEvent( GameObject killer, 
			  GameObject victim,
			  String obitKey,
			  GameObject inflictor)
	{
	  super(RULE_DEATHSCORE_EVENT);

	  fVictim = victim;
	  fKiller = killer;
	  fObitKey = obitKey;
	  fInflictor = inflictor;
	}
  public final GameObject getActive()
	{
	  return fKiller;
	}
  public final GameObject getAgent()
	{
	  return fInflictor;
	}
  public final String getAgentKey()
	{
	  return fObitKey;
	}
  public static final DeathScoreEvent getEvent( GameObject killer, 
						GameObject victim,
						String obitKey,
						GameObject inflictor)
	{
	  DeathScoreEvent event = gCachedEvent;

	  if( event == null )
	{
	  event = new DeathScoreEvent();
	}

	  // the score object itself should set itself as source
	  // after it has recieved this event...
	  //      event.source = source;
	  event.fVictim = victim;
	  event.fKiller = killer;
	  event.fObitKey = obitKey;
	  event.fInflictor = inflictor;

	  return event; 
	}
  public final GameObject getPassive()
	{
	  return fVictim;
	}
  public final int getScoreChange()
	{
	  return fScoreChange;
	}
  public final static void releaseEvent(DeathScoreEvent event)
	{
	  gCachedEvent = event;
	  event.fVictim = null;
	  event.fKiller = null;
	  event.fObitKey = null;
	  event.fInflictor = null;
	}
  public void setScoreChange(int score)
	{
	  fScoreChange = score;
	}
}