package q2java.core;

import java.lang.reflect.*;
import q2java.*;

/**
 * Abstract class for Game Modules
 *
 * @author Barry Pederson
 */
public abstract class Gamelet 
	{
	// private fields because we don't want subclasses messing with them
	private String fGameletName;
	private boolean fIsInitialized;
	private boolean fIsUnloading;
	
/**
 * Constructor for all Gamelets.
 * @param moduleName java.lang.String
 */
public Gamelet(String gameletName) 
	{
	fGameletName = gameletName;	
	}
/**
 * Get which Gamelet classes this Gamelet requires.
 * @return array of Gamelet class names
 */
public String[] getGameletDependencies() 
	{
	return null;
	}
/**
 * Get the name of this gamelet.
 * @return java.lang.String
 */
public String getGameletName() 
	{
	return fGameletName;
	}
/**
 * Get the name of the package this gamelet belongs to.
 * @return java.lang.String
 */
public String getPackageName() 
	{
	String clsName = getClass().getName();
	int i = clsName.lastIndexOf('.');
	return clsName.substring(0, i);
	}
/**
 * Get which class (if any) this Gamelet wants to use for a Player class.
 * @return java.lang.Class
 */
public Class getPlayerClass() 
	{
	return null;
	}
/**
 * Associate players with this particular gamelet.
 */
void grabPlayers() 
	{
	try
		{
		Class[] paramTypes = new Class[1];
		paramTypes[0] = NativeEntity.class;
		Constructor con = getPlayerClass().getConstructor(paramTypes);
	
		Object[] params = new Object[1];
		
		java.util.Enumeration players = NativeEntity.enumeratePlayerEntities();
		while (players.hasMoreElements())
			{
			try
				{
				params[0] = (NativeEntity) players.nextElement();	
				con.newInstance(params);
			
	// if not changing map			
	//			p.playerBegin(false);
				}
			catch (Exception e)
				{
				e.printStackTrace();
				}
			}
		}
	catch (Exception e2)
		{
		e2.printStackTrace();
		}
	}
/**
 * Actually initialize the Gamelet for action.
 */
public void init() 
	{
	}
/**
 * Check if this gamelet has been initialized.
 * @return boolean
 */
public final boolean isInitialized() 
	{
	return fIsInitialized;
	}
/**
 * Check whether this Gamelet requires a level change to load/unload.
 * @return boolean
 */
public boolean isLevelChangeRequired() 
	{
	return false;
	}
/**
 * Check if this gamelet is being unloaded at the next level change.
 * @return boolean
 */
public final boolean isUnloading() 
	{
	return fIsUnloading;
	}
/**
 * Mark this gamelet as having been initialized - we have this
 * instead of a "setInitialized(boolean b)" because it should
 * never be able to be switched from true to false.
 */
public final void markInitialized() 
	{
	fIsInitialized = true;
	}
/**
 * Mark this gamelet as being unloaded on the next level change.
 */
public final void markUnloading() 
	{
	fIsUnloading = true;
	}
/**
 * Disassociate players from this particular gamelet.
 */
void releasePlayers() 
	{
	java.util.Enumeration players = NativeEntity.enumeratePlayerEntities();
	while (players.hasMoreElements())
		{
		try
			{
			NativeEntity ent = (NativeEntity) players.nextElement();
			Object obj = ent.getReference();

			if (obj instanceof SwitchablePlayer)
				{
				SwitchablePlayer sp = (SwitchablePlayer) obj;
				sp.dispose();
				}
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}
	
	}
/**
 * Default help svcmd for a Gamelet.
 * @param args java.lang.String[]
 */
public void svcmd_help(String[] args) 
	{
	Engine.dprint("Gamelet author was too lazy to write decent help\n");
	}
/**
 * Default do-nothing implementation for unloading a gamelet.
 */
public void unload() 
	{
	}
}