package q2java.baseq2.event;

/**
 * Interface to implement to receive events for player movement.
 *
 * @author Peter Donald
 */
public interface PlayerMoveListener  extends java.util.EventListener
{
  public void playerMoved(PlayerMoveEvent e);    
}