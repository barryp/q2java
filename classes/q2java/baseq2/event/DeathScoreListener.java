package q2java.baseq2.event;

/**
 * Interface to use when want to score deaths.
 */
public interface DeathScoreListener extends java.util.EventListener
{
  public void deathOccured(DeathScoreEvent e);    
}