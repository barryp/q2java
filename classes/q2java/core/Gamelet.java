package q2java.core;

import java.lang.reflect.*;

import org.w3c.dom.*;

import q2java.*;
import q2java.core.event.*;

/**
 * Abstract class for Game Modules
 *
 * @author Barry Pederson
 */
public abstract class Gamelet
	{
	private Document fGameletInfo;
	
/**
 * Constructor for all Gamelets.
 * @param moduleName java.lang.String
 */
public Gamelet(Document gameletInfo)
	{
	fGameletInfo = gameletInfo;
	}
/**
 * Get the document that was passed to the Gamelet constructor.
 * @return org.w3c.dom.Document
 */
public Document getGameletDocument() 
	{
	return fGameletInfo;
	}
/**
 * Get which class (if any) this Gamelet wants to use for a Player class.
 *
 * @return java.lang.Class
 */
public Class getPlayerClass() 
	{
	return null;
	}
/**
 * Try to shame gamelet authors into overriding this.
 * @param args java.lang.String[]
 */
public void svcmd_help(String[] args) 
	{
	Engine.dprint(getClass().getName() + " author was too lazy to write decent help\n");
	}
/**
 * Default do-nothing implementation for unloading a gamelet.
 */
public void unload() 
	{
	}
}