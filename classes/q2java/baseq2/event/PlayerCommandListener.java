package q2java.baseq2.event;

/**
 * interface for PlayerCommand event
 *
 * @author Peter Donald
 */
public interface PlayerCommandListener  extends java.util.EventListener
{
  public void commandIssued(PlayerCommandEvent e);    
}