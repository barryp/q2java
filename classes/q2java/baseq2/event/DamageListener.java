package q2java.baseq2.event;

/**
 * interface implemented to receive data about damage done to player.
 * 
 * Updated to delegation event mode Peter Donald
 * @author Brian Haskin
 */
public interface DamageListener  extends java.util.EventListener
{
  public void damageOccured(DamageEvent e);        
}