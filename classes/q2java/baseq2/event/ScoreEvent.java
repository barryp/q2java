package q2java.baseq2.event;

import q2java.baseq2.GameObject;

abstract public class ScoreEvent extends RuleEvent
{
  protected ScoreEvent(int type)
	{
	  super(type);
	}
  abstract public GameObject getActive();  // who intiated the score event(ie killer/goal scorer)    
  abstract public GameObject getAgent(); // who directly caused the     
										 // event(ie weapon or goal scorer)
  abstract public String getAgentKey(); // describing what method the agent used to bring about    
  abstract public GameObject getPassive(); // who the event was done to(ie victim/flag)    
										// the event (usually obit key / string describing flag
										// capture protection etc)
  abstract public int getScoreChange(); // for the active object ...    
  abstract public void setScoreChange(int score); // for the active object ...    
}