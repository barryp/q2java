
package q2jgame;

import java.util.*;
import q2java.Engine;

/**
 * A hub for receiving and distributing global printing information.
 * 
 */
  
public class PrintManager 
	{
	private static Vector gListeners = new Vector();
	
/**
 * Add a print listener.
 * @param pl q2jgame.PrintListener
 */
public static void addPrintListener(PrintListener pl) 
	{
	if (!gListeners.contains(pl))
		gListeners.addElement(pl);
	}
/**
 * Handle broadcast print messages.
 * @param flags int
 * @param msg java.lang.String
 */
public static void bprint(int flags, String msg) 
	{
	Engine.bprint(flags, msg);
	
	Enumeration enum = gListeners.elements();
	while (enum.hasMoreElements())
		{
		try
			{
			((PrintListener) enum.nextElement()).bprint(flags, msg);
			}
		catch (Exception e)
			{
			}
		}
	}
/**
 * Handle debugging print messages.
 * @param msg java.lang.String
 */
public static void dprint(String msg) 
	{
	Engine.dprint(msg);
	
	Enumeration enum = gListeners.elements();
	while (enum.hasMoreElements())
		{
		try
			{
			((PrintListener) enum.nextElement()).dprint(msg);
			}
		catch (Exception e)
			{
			}
		}	
	}
/**
 * Remove a listener.
 * @param pl q2jgame.PrintListener
 */
public static void removePrintListener(PrintListener pl) 
	{
	gListeners.removeElement(pl);
	}
}