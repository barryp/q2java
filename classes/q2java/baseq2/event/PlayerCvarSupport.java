package q2java.baseq2.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import java.util.Enumeration;
import java.util.Vector;
import q2java.Engine;
import q2java.baseq2.*;
import q2java.baseq2.event.*;

/**
 * Support class for delegation of PlayerCvar event.
 *
 * @author Peter Donald 24/1/99
 */
final public class PlayerCvarSupport implements PlayerCommandListener
{
  private static Method gInvokeMethod = null;
  private Vector fListeners = new Vector();
  private Vector fCvars = new Vector();
  private Player fPlayer = null;
  private boolean fIsCommandListening = false;

  static
	{
	  try
	{
	  gInvokeMethod = PlayerCvarListener.class.
	    getMethod("cvarRetrieved", new Class[] { PlayerCvarEvent.class } );	
	}
	  catch(NoSuchMethodException nsme) {}
	}

  public PlayerCvarSupport(Player player)
	{
	  fPlayer = player;
	}
  public void addPlayerCvarListener(PlayerCvarListener l, String cvar)
	{
	  if( !fListeners.contains(l) ) 
	{
	  fListeners.addElement(l);
	  fCvars.addElement(cvar);
	  if( !fIsCommandListening )
	    {
	      fPlayer.addPlayerCommandListener(this);
	      fIsCommandListening = true;
	    }
	}

	  MiscUtil.stuffCommand(fPlayer.fEntity, "PLAYERCLIENTCVAR  $" + cvar);
	}
  public void commandIssued(PlayerCommandEvent e)
	{
	  if( !e.getCommand().equals("PLAYERCLIENTCVAR") ) 
	{
	  return;
	}

	  e.consume();

	  // get alll of string not part of our command
	  PlayerCvarEvent ce = PlayerCvarEvent.getEvent( (String)fCvars.elementAt(0),
						     e.getPlayer(),
						     e.getArgs() );
	  
	  
	  PlayerCvarListener l = (PlayerCvarListener)fListeners.elementAt(0);

	  l.cvarRetrieved(ce);

	  fListeners.removeElementAt(0);
	  fCvars.removeElementAt(0);
	  
	  PlayerCvarEvent.releaseEvent(ce);

	  if( fListeners.size() == 0 && fIsCommandListening )
	{
	  fPlayer.removePlayerCommandListener(this);
	  fIsCommandListening = false;
	}
	}
  public void removePlayerCvarListener(PlayerCvarListener l)
	{
	  int index = fListeners.indexOf(l);
	  if( index != -1 )
	{
	  fListeners.removeElementAt(index);
	  fCvars.removeElementAt(index);
	  if( fListeners.size() == 0 && fIsCommandListening )
	    {
	      fPlayer.removePlayerCommandListener(this);
	      fIsCommandListening = false;
	    }
	}
	}
}