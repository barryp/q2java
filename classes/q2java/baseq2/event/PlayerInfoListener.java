package q2java.baseq2.event;

import java.beans.PropertyVetoException;

/**
 * Interface to implement to receive events notifying of localinfo updates of a player.
 * NB: can veto a change and the localinfo will be reverted.
 *
 * @author Peter Donald 24/1/99
 */
public interface PlayerInfoListener  extends java.util.EventListener
{
  public void infoChanged(PlayerInfoEvent e) throws PropertyVetoException;    
}