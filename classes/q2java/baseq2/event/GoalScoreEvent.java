package q2java.baseq2.event;

import javax.vecmath.*;
import q2java.baseq2.*;

/**
 * event used to notify of goals achieved for scoring purposes.
 *
 * @author Peter Donald
 */
public class GoalScoreEvent extends ScoreEvent
{
  protected GameObject fActive;
  protected GameObject fPassive;
  protected String fGoalKey;
  protected int fScoreChange;
  
  private static GoalScoreEvent gCachedEvent = null;

  protected GoalScoreEvent()
	{
	  super(RULE_GOALSCORE_EVENT);
	}
  public GoalScoreEvent( GameObject active, 
			 GameObject passive,
			 String goalKey,
			 int scoreChange )
	{
	  super(RULE_GOALSCORE_EVENT);

	  fActive = active;
	  fPassive = passive;
	  fGoalKey = goalKey;
	  fScoreChange = scoreChange;
	}
  public GameObject getActive()
	{
	  return fActive;
	}
  public GameObject getAgent()
	{
	  return fActive;
	}
  public String getAgentKey()
	{
	  return fGoalKey;
	}
  public static final GoalScoreEvent getEvent( GameObject active, 
					       GameObject passive,
					       String goalKey,
					       int scoreChange)
	{
	  GoalScoreEvent event = gCachedEvent;

	  if( event == null )
	{
	  event = new GoalScoreEvent();
	}

	  // the score object itself should set itself as source
	  //      event.source = source;
	  event.fActive = active;
	  event.fPassive = passive;
	  event.fGoalKey = goalKey;
	  event.fScoreChange = scoreChange;

	  return event; 
	}
  public GameObject getPassive()
	{
	  return fPassive;
	}
  public int getScoreChange()
	{
	  return fScoreChange;
	}
  public final static void releaseEvent(GoalScoreEvent event)
	{
	  gCachedEvent = event;
	  event.fActive = null;
	  event.fPassive = null;
	  event.fGoalKey = null;
	}
  public void setScoreChange(int score)
	{
	  fScoreChange = score;
	}
}