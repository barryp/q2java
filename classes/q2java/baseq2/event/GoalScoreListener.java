package q2java.baseq2.event;

/**
 * Interface to use when want to score goals acieved
 */
public interface GoalScoreListener extends java.util.EventListener
{
  public void goalAchieved(GoalScoreEvent e);    
}