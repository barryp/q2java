package donaldp.srvcmd;

import java.beans.PropertyVetoException;
import java.util.Enumeration;

import org.w3c.dom.Document;

import q2java.NativeEntity;
import q2java.Engine;
import q2java.core.Game;
import q2java.core.event.*;
import q2java.baseq2.Player;

public class GameModule extends q2java.core.Gamelet implements ServerCommandListener
	{	
	
public GameModule(Document gameletInfo)
	  {
	  super( gameletInfo );
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