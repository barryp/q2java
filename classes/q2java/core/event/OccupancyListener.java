package q2java.core.event;

import java.beans.PropertyVetoException;

/**
 * Interface to use when want to be notified of players connecting / disconnecting / changing class.
 */
public interface OccupancyListener extends java.util.EventListener
{
  /** Note that the exception when thrown
   * will not allow someone to connect if it is on a 
   * PlayerChangeEvent.PLAYER_CONNECTED.
   * while on a PlayerChangeEvent.PLAYER_CLASSCHANGE
   * it will not allow the change of class..
   * on a PlayerChangeEvent.PLAYER_DISCONNECTED it does nothing.
   * this can be used for banning players based on ip/clan/name etc
   */
  public void playerChanged(OccupancyEvent e) throws PropertyVetoException;      
}