package donaldp.game;

import java.beans.PropertyVetoException;
import java.util.Enumeration;

import org.w3c.dom.Document;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;

public class GameModule extends q2java.core.Gamelet 
  implements GameStatusListener
	{	
	
public GameModule(Document gameletInfo)
	  {
	  super( gameletInfo );
	  Game.addGameStatusListener(this);
	  }  
public void gameStatusChanged(GameStatusEvent e)
	  {
	  Engine.debugLog("Event occured at " + Game.getGameTime() + " with event state " + e.getState() );
	  }  
public void unload() 
	  {
	  Game.removeGameStatusListener(this);
	  }  
}