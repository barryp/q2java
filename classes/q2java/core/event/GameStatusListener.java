package q2java.core.event;

/**
 * Implement this to updated when game status changes.
 *
 * @author Peter Donald 25/1/99
 */
public interface GameStatusListener extends java.util.EventListener
{
  public void gameStatusChanged(GameStatusEvent e);      
}