package q2java.baseq2.event;

/**
 * Interface to use when want to find out a client side cvar.
 * NB the listener is immediately de-registered by Player
 * after receiving 1 PlayerCvarEvent
 */
public interface PlayerCvarListener extends java.util.EventListener
{
  public void cvarRetrieved(PlayerCvarEvent e);    
}