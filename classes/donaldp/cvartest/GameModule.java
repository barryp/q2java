package donaldp.cvartest;

import java.beans.PropertyVetoException;
import java.util.Enumeration;
import q2java.NativeEntity;
import q2java.Engine;
import q2java.baseq2.Player;
import q2java.baseq2.event.*;

/**
 * Module to test vetoing of player picking things up
 * all players that are in when this is started will not be able to
 * pick anything up 
 *
 * @author Peter Donald 25/1/99
 */
public class GameModule extends q2java.core.Gamelet implements PlayerCvarListener
	{	
	
public GameModule(String moduleName)
	  {
	  super( moduleName );
	  // we no longer want to be notified of level changes
	  Enumeration enum = NativeEntity.enumeratePlayerEntities();
	  while( enum.hasMoreElements() )
	  {
	  Player p = (Player)(((NativeEntity)enum.nextElement()).getReference());
	  
	  p.addPlayerCvarListener(this,"cl_blend");
	  }
	  }  
public void cvarRetrieved(PlayerCvarEvent e)
	  {
	e.getPlayer().fEntity.centerprint( e.getCvar() + " has a value of " + e.getValue() );
	  }  
public void unload() 
	  {
	  // we no longer want to be notified of level changes
	  Enumeration enum = NativeEntity.enumeratePlayerEntities();

	  while( enum.hasMoreElements() )
	  {
	  Player p = (Player)(((NativeEntity)enum.nextElement()).getReference());
	  p.removePlayerCvarListener(this);
	  }
	  }  
}