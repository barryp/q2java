package donaldp.command;

import java.beans.PropertyVetoException;
import java.util.Enumeration;

import org.w3c.dom.Document;

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
public class GameModule extends q2java.core.Gamelet implements PlayerCommandListener
	{	
	
public GameModule(Document gameletInfo)
	  {
	  super( gameletInfo );
	  // we no longer want to be notified of level changes
	  Enumeration enum = NativeEntity.enumeratePlayerEntities();
	  while( enum.hasMoreElements() )
	  {
	  Player p = (Player)(((NativeEntity)enum.nextElement()).getReference());
	  
	  p.addPlayerCommandListener(this);
	  }
	  }  
public void commandIssued(PlayerCommandEvent e)
	  {
	e.getPlayer().fEntity.centerprint( e.getCommand() + " " + e.getArgs() );
	  }  
public void unload() 
	  {
	  // we no longer want to be notified of level changes
	  Enumeration enum = NativeEntity.enumeratePlayerEntities();

	  while( enum.hasMoreElements() )
	  {
	  Player p = (Player)(((NativeEntity)enum.nextElement()).getReference());
	  p.removePlayerCommandListener(this);
	  }
	  }  
}