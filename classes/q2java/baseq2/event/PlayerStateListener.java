package q2java.baseq2.event;

/**
 * Interface to receive PlayerStateChange events.
 *
 * @author Peter DOnald
 */
public interface PlayerStateListener  extends java.util.EventListener
{
  public void playerStateChanged(PlayerStateEvent e);      
}