package donaldp.fixedskin;

import java.beans.PropertyVetoException;
import java.util.Enumeration;
import q2java.NativeEntity;
import q2java.Engine;
import q2java.baseq2.Player;
import q2java.baseq2.event.*;

/**
 * Module to test vetoing of player local info changes
 * all players that are in when this is started will no be able to
 * change skins ... :P~~~
 *
 * @author Peter Donald 25/1/99
 */
public class GameModule extends q2java.core.Gamelet implements PlayerInfoListener
	{	
	
public GameModule(String moduleName)
	  {
	  super( moduleName );
	  // we no longer want to be notified of level changes
	  Enumeration enum = NativeEntity.enumeratePlayerEntities();
	  while( enum.hasMoreElements() )
	  {
	  Player p = (Player)(((NativeEntity)enum.nextElement()).getReference());
	  
	  p.addPlayerInfoListener(this);
	  }
	  }  
public void infoChanged(PlayerInfoEvent e) throws PropertyVetoException
	  {
	  if( e.getKey().equalsIgnoreCase("skin") && 
	  e.getOldValue() != null
	  )
	  {
	  throw new PropertyVetoException("Cant change skins in this mod",null);
	  }
	  }  
public void unload() 
	  {
	  // we no longer want to be notified of level changes
	  Enumeration enum = NativeEntity.enumeratePlayerEntities();

	  while( enum.hasMoreElements() )
	  {
	  Player p = (Player)(((NativeEntity)enum.nextElement()).getReference());
	  p.removePlayerInfoListener(this);
	  }
	  }  
}