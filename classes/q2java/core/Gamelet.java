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
 * Get which Gamelet classes this Gamelet requires.
 * @deprecated Don't do this any more!
 * @return array of Gamelet class names
 */
public final String[] getGameletDependencies() 
	{
	return null;
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
 * Get the name of this gamelet.
 * @deprecated The name of a gamelet is really a property of the GameletManager and not of the gamelet itself.
 * @return java.lang.String
 */
public final String getGameletName() 
	{
	return Game.getGameletManager().getGameletName(this);
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
 * Actually initialize the Gamelet for action.
 * @deprecated don't do this any more..initialize in the
 *   constructor.
 */
public final void init() 
	{
	}
/**
 * Check whether this Gamelet requires a level change to load/unload.
 * @deprecated don't do this..store in the Gamelet Description file
 * @return boolean
 */
public final boolean isLevelChangeRequired() 
	{
	return false;
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