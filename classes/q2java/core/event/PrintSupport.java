package q2java.core.event;

import q2java.Q2Recycler;
import q2java.core.*;
import java.text.*;
import java.util.*;

/**
 * Handles the dirty details of tracking and working with PrintListeners.
 */
public class PrintSupport 
	{
	protected Vector fListeners = new Vector();
	protected Vector fLocales = new Vector();

	/**
	 * Class just used by PrintSupport to help track PrintListeners.
	 */
	protected static class PrintSupportNode 
		{
		PrintListener fListener;
		int fChannels;
		Locale fLocale;
		}	
	
/**
 * Add a print listener - if the specified listener has already been added, the old entry is replaced.
 *
 * @param pl q2java.core.event.PrintListener
 * @param channels int
 * @param loc Locale that the PrintListener wants to receive - use null to receive all locales
 * @param highPriority use true for PrintListeners that need to be called
 *  as early on as possible (so they can consume the event or change it), false
 *  for normal Listeners that don't care.
 */
public synchronized void addPrintListener(PrintListener pl, int channels, Locale loc, boolean highPriority) 
	{
	removePrintListener(pl);
	
	PrintSupportNode psn = new PrintSupportNode();
	psn.fListener = pl;
	psn.fChannels = channels;
	psn.fLocale = loc;
	
	if (highPriority)
		fListeners.insertElementAt(psn, 0); // insert at front
	else
		fListeners.addElement(psn); // add to end

	// update list of active locales
	if ((loc != null) && !fLocales.contains(loc))
		fLocales.addElement(loc);
	}
/**
 * Add a print listener to the specified channels.
 * @param pl q2java.core.event.PrintListener
 * @param channels int
 * @param highPriority use true for PrintListeners that need to be called
 *  as early on as possible (so they can consume the event or change it), false
 *  for normal Listeners that don't care.
 */
public void addPrintListener(PrintListener pl, int channels, boolean highPriority) 
	{
	addPrintListener(pl, channels, Locale.getDefault(), highPriority);
	}
/**
 * Fire a PrintEvent off to whatever listeners have registered to receive
 * messages on its particular channel.
 *
 * @param printChannel one of the PrintEvent.PRINT_* constants.
 * @param printFlags probably one of the Engine.PRINT_* constants.
 * @param source object causing the print event.  For players use the player's NativeEntity, which is fairly constant and makes
 *   spam supression or ignoring somebody easier, since changing name or class doesn't affect it.
 * @param sourceName for conveniently passing a Player name - avoids each PrintListener having to look it up.
 * @param dest Object this message is being sent to - most likely a "Team" object.
 * @param msg What we're printing over the given channel
 */
public void fireEvent(int printChannel, int printFlags, Object source, String sourceName, Object dest, String msg) 
	{
	PrintSupportNode[] psna = null;
	
	synchronized(this)
		{
		int n = fListeners.size();
		if (n < 1)
			return;
			
		psna = new PrintSupportNode[n];
		fListeners.copyInto(psna);
		}
		
	PrintEvent pe = PrintEvent.getEvent(printChannel, printFlags, source, sourceName, dest, msg);

	for (int i = 0; (i < psna.length) && !pe.isConsumed(); i++)
		{
		PrintSupportNode psn = psna[i];
		if ((psn.fChannels & printChannel) != 0)
			{
			try
				{
				psn.fListener.print(pe);
				}
			catch (Throwable t)
				{
				t.printStackTrace();
				}
			}		
		}
		
	PrintEvent.releaseEvent(pe);
	}
/**
 * Fire a PrintEvent off to whatever listeners have registered to receive
 * messages on its particular channel.
 *
 * @param printChannel one of the PrintEvent.PRINT_* constants.
 * @param printFlags probably one of the Engine.PRINT_* constants.
 * @param source object causing the print event.  For players use the player's NativeEntity, which is fairly constant and makes
 *   spam supression or ignoring somebody easier, since changing name or class doesn't affect it.
 * @param sourceName for conveniently passing a Player name - avoids each PrintListener having to look it up.
 * @param dest Object this message is being sent to - most likely a "Team" object.
 * @param basename of ResourceBundle
 * @param key for looking up entry in ResourceBundles
 * @param args args for formatted messages - may be null for unformatted messages.
 */
public void fireLocalizedEvent(int printChannel, int printFlags, Object source, String sourceName, Object dest, String basename, String key, Object[] args) 
	{
	PrintSupportNode[] psna = null;
	Locale[] la = null;
	
	// make a snapshot of the list of listeners and locales
	synchronized(this)
		{
		int count = fListeners.size();
		if (count < 1)
			return; // bail if no listeners at all
			
		psna = new PrintSupportNode[count];
		fListeners.copyInto(psna);

		count = fLocales.size();
		la = new Locale[count];
		fLocales.copyInto(la);
		}
		
	// borrow a PrintEvent object
	PrintEvent pe = PrintEvent.getEvent(printChannel, printFlags, source, sourceName, dest, null);
	
	// iterate through all the locales
	for (int i = 0; (i < la.length) && !pe.isConsumed(); i++)
		{
		Locale loc = la[i];
		String msg = null;
		
		// iterate through the listeners
		for (int j = 0; j < psna.length; j++)
			{
			PrintSupportNode psn = psna[j];

			// check if this listener is interested in this channel
			if ((psn.fChannels & printChannel) == 0)
				continue;

			// send the message if the listener cares about this locale
			if ((psn.fLocale == null) || psn.fLocale.equals(loc))
				{
				// check if a message has already been formatted
				if (msg == null)
					{
					String pattern = Game.getResourceGroup(loc).getRandomString(basename, key);
					if (args == null)
						msg = pattern; // the pattern -is- the message
					else
						{
						MessageFormat mf = new MessageFormat(pattern);
						mf.setLocale(loc);
						msg = mf.format(args);
						}
						
					// update the PrintEvent to reflect this particular locale
					pe.setLocale(loc);
					pe.setMessage(msg);
					}

				// fire that puppy off
				try
					{
					psn.fListener.print(pe);
					}
				catch (Throwable t)
					{
					t.printStackTrace();
					}					
				}				
			}		
		}

	// give back what we borrowed
	PrintEvent.releaseEvent(pe);	
	}
/**
 * Remove all instances of a print listener.
 * @param pl q2java.core.event.PrintListener
 * @param channels int
 */
public synchronized void removePrintListener(PrintListener pl)
	{
	int n = fListeners.size();
	for (int i = 0; i < n; i++)
		{
		PrintSupportNode psn = (PrintSupportNode) fListeners.elementAt(i);
		if (psn.fListener == pl)
			{
			// found the listener we were looking for
			fListeners.removeElementAt(i);
			
			// see if it was the last of its locale
			Locale loc = psn.fLocale;

			// if it didn't have a locale, then we're done
			if (loc == null)
				return;

			// iterate through the listeners again, looking
			// to see if any of them used the same locale
			n--;  // fListeners is one element shorter now
			for (int j = 0; j < n; j++)
				{
				psn = (PrintSupportNode) fListeners.elementAt(j);
				if (loc.equals(psn.fLocale))
					return;  // there is..another!  we're done
				}

			// no others with the same locale
			fLocales.removeElement(loc);
			return;
			}
		}
	}
}