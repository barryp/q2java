package q2java.baseq2.rule;

import java.util.*;
import java.lang.reflect.*;
import q2java.Engine;
import q2java.core.event.EventPack;
import q2java.baseq2.Player;
import q2java.baseq2.event.*;

public class DefaultScoreManager implements ScoreManager
{
  protected static Method gInvokeMethod = null;
  protected Vector fListeners = new Vector();

  static
	{
	  try
	{
	  gInvokeMethod = DeathScoreListener.class.
	    getMethod("deathOccured", new Class[] { DeathScoreEvent.class } );	
	}
	  catch(NoSuchMethodException nsme) {}
	}

  public void addScoreListener(Object listener, Class eventClass)
	{
	  if( listener instanceof DeathScoreListener &&
	  eventClass.isAssignableFrom( DeathScoreEvent.class ) &&
	  !fListeners.contains(listener) )
	{
	  fListeners.addElement(listener);
	}
	}
  public void registerScoreEvent(ScoreEvent e)
	{
	  if( e instanceof DeathScoreEvent )
	{
	  e.setSource(this);

	  if( e.getActive() == e.getPassive() || !(e.getActive() instanceof Player) )
	    {
	      e.setScoreChange( -1 );

	      if( e.getPassive() instanceof Player )
		{
		  ((Player)e.getPassive()).setScore(-1,false); 
		}
	    }
	  else
	    {
	      e.setScoreChange(1);
	      ((Player)e.getActive()).setScore(1,false); 	      
	    }

	  try { EventPack.fireEvent( e, gInvokeMethod, fListeners ); }
	  catch(java.beans.PropertyVetoException pve) {}
	}
	  else
	{
	  Engine.debugLog("Unknown event received by default scorer " + e );
	}
	}
  public void removeScoreListener(Object listener, Class eventClass)
	{
	  if( listener instanceof DeathScoreListener &&
	  eventClass.isAssignableFrom( DeathScoreEvent.class ) )
	{
	  fListeners.removeElement(listener);
	}
	}
}