package donaldp.statechg;

import java.beans.PropertyVetoException;
import java.util.Enumeration;

import org.w3c.dom.Document;

import q2java.NativeEntity;
import q2java.Engine;
import q2java.baseq2.Player;
import q2java.baseq2.event.*;

public class GameModule extends q2java.core.Gamelet implements PlayerStateListener
	{	
	
public GameModule(Document gameletInfo)
	  {
	  super( gameletInfo );
	  // we no longer want to be notified of level changes
	  Enumeration enum = NativeEntity.enumeratePlayerEntities();
	  while( enum.hasMoreElements() )
	  {
	  Player p = (Player)(((NativeEntity)enum.nextElement()).getReference());
	  
	  p.addPlayerStateListener(this);
	  }
	  }  
public void playerStateChanged(PlayerStateEvent e)
	  {
	e.getPlayer().fEntity.centerprint("State changed");
	  }  
public void unload() 
	  {
	  // we no longer want to be notified of level changes
	  Enumeration enum = NativeEntity.enumeratePlayerEntities();

	  while( enum.hasMoreElements() )
	  {
	  Player p = (Player)(((NativeEntity)enum.nextElement()).getReference());
	  p.removePlayerStateListener(this);
	  }
	  }  
}