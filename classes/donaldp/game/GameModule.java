package donaldp.game;

import java.beans.PropertyVetoException;
import java.util.Enumeration;
import q2java.*;
import q2java.core.*;
import q2java.core.event.*;

public class GameModule extends q2java.core.Gamelet 
  implements GameStatusListener
	{	
	
public GameModule(String moduleName)
	  {
	  super( moduleName );
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