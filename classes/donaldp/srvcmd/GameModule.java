package donaldp.srvcmd;

import java.beans.PropertyVetoException;
import java.util.Enumeration;
import q2java.NativeEntity;
import q2java.Engine;
import q2java.core.Game;
import q2java.core.event.*;
import q2java.baseq2.Player;

public class GameModule extends q2java.core.Gamelet implements ServerCommandListener
	{	
	
public GameModule(String moduleName)
	  {
	  super( moduleName );
	  Game.addServerCommandListener(this);
	  }  
public void serverCommandIssued(ServerCommandEvent e)
	  {
	if( e.getCommand().equals("blah") )
	    {
	      Engine.dprint("prepare to be blahed ... blah !!!!!!!!\n");
	      e.consume();
	    }
	  }  
public void unload() 
	  {
	  Game.addServerCommandListener(this);
	  }  
}