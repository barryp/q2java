package q2java.baseq2.event;

import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import java.util.Enumeration;
import java.util.Vector;
import q2java.Engine;
import q2java.core.GameUtil;
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

  public PlayerCvarSupport()
	{
	}
  public void addPlayerCvarListener(Player p, PlayerCvarListener l, String cvar)
	{
	  if( !fListeners.contains(l) ) 
	{
	  fListeners.addElement(l);
	  fCvars.addElement(cvar);
	  if( !fIsCommandListening )
	    {
	      p.addPlayerCommandListener(this);
	      fIsCommandListening = true;
	    }
	}

	  GameUtil.stuffCommand(p.fEntity, "PLAYERCLIENTCVAR  $" + cvar);
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
	  
	  ce.recycle();

	  if( fListeners.size() == 0 && fIsCommandListening )
	{
	  e.getPlayer().removePlayerCommandListener(this);
	  fIsCommandListening = false;
	}
	}
  public void removePlayerCvarListener(Player p,PlayerCvarListener l)
	{
	  int index = fListeners.indexOf(l);
	  if( index != -1 )
	{
	  fListeners.removeElementAt(index);
	  fCvars.removeElementAt(index);
	  if( fListeners.size() == 0 && fIsCommandListening )
	    {
	      p.removePlayerCommandListener(this);
	      fIsCommandListening = false;
	    }
	}
	}
}