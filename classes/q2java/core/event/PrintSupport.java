package q2java.core.event;

import q2java.Q2Recycler;
import q2java.core.*;
import java.text.*;
import java.util.*;

/**
 * Handles the dirty details of tracking and working with PrintListeners.
 *
 * @author Barry Pederson
 */
public class PrintSupport 
	{
	protected PrintSupportNode[] fPSNA = new PrintSupportNode[0];
	protected Locale[] fLocales = new Locale[0];
	
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
 * Add a print listener - if the specified listener has already been 
 * added, the old entry is replaced.  Threadsafe to support gamelets
 * like the telnet server.
 *
 * @param pl q2java.core.event.PrintListener
 * @param channels which print channels the listener wants to hear (PrintEvent.PRINT_* constants or'ed together)
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

	// make a new vector and add the listener
	PrintSupportNode[] psna = new PrintSupportNode[fPSNA.length+1];
	if (highPriority)
		{
		// insert at front
		psna[0] = psn;
		System.arraycopy(fPSNA, 0, psna, 1, fPSNA.length);
		}
	else
		{
		// add to end
		System.arraycopy(fPSNA, 0, psna, 0, fPSNA.length);
		psna[fPSNA.length] = psn;
		}

	// start using the new array
	fPSNA = psna;

	// if no locale, then nothing more to do
	if (loc == null)
		return;

	// nothing more to do if the locale is already known
	for (int i = 0; i < fLocales.length; i++)
		{
		if (fLocales[i] == loc)
			return;
		}

	// must be a new locale, so make a new locale array and
	// add the new locale to the end
	Locale[] la = new Locale[fLocales.length+1];
	System.arraycopy(fLocales, 0, la, 0, fLocales.length);
	la[fLocales.length] = loc;
	fLocales = la;
	}
/**
 * Add a print listener to the specified channels for the default Locale.
 *
 * @param pl q2java.core.event.PrintListener
 * @param channels which print channels the listener wants to hear (PrintEvent.PRINT_* constants or'ed together)
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
 * messages on its particular channel - NOT threadsafe.
 *
 * Be ***VERY*** careful putting debugging print statmements in this
 * method since they can throw the game into a loop.  Use the
 * Engine.dprint, NOT the System.out.print (unless you're very careful).
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
	PrintSupportNode[] psna = fPSNA;
	
	if (psna.length < 1)
		return;
				
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
				if (printChannel != PrintEvent.PRINT_JAVA)
					t.printStackTrace();
				// it's a very bad idea to print errors related
				// to Java printing, since there is a danger of 
				// the code getting into a loop here.
				}
			}		
		}
		
	pe.recycle();
	}
/**
 * Fire a PrintEvent off to whatever listeners have registered to receive
 * messages on its particular channel - NOT threadsafe.
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
	PrintSupportNode[] psna;
	Locale[] myLocales;
	
	// grab references to the lists of listeners and locales
	synchronized (this)
		{
		psna = fPSNA;
		myLocales = fLocales;
		}

	if (psna.length < 1)
		return; // bail if no listeners at all
			
	// borrow a PrintEvent object
	PrintEvent pe = PrintEvent.getEvent(printChannel, printFlags, source, sourceName, dest, null);
	
	// iterate through all the locales
	for (int i = 0; (i < myLocales.length) && !pe.isConsumed(); i++)
		{
		Locale loc = myLocales[i];
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
	pe.recycle();	
	}
/**
 * Remove all instances of a print listener - threadsafe to support
 * gamelets like the telnet server.
 *
 * @param pl q2java.core.event.PrintListener
 * @param channels int
 */
public synchronized void removePrintListener(PrintListener pl)
	{
	for (int i = 0; i < fPSNA.length; i++)
		{
		PrintSupportNode psn = fPSNA[i];
		if (psn.fListener == pl)
			{
			// found the listener we were looking for, make a new
			// array and copy everything except what we're removing
			PrintSupportNode[] psna = new PrintSupportNode[fPSNA.length-1];
			int k = 0;
			for (int j = 0; j < fPSNA.length; j++)
				{
				if (fPSNA[j].fListener != pl)
					psna[k++] = fPSNA[j];
				}
				
			// start using the new array
			fPSNA = psna;
			
			// see if it was the last of its locale
			Locale loc = psn.fLocale;

			// if it didn't have a locale, then we're done
			if (loc == null)
				return;

			// iterate through the remaining listeners, looking
			// to see if any of them used the same locale
			for (int j = 0; j < fPSNA.length; j++)
				{
				psn = fPSNA[j];
				if (loc.equals(psn.fLocale))
					return;  // there is..another!  we're done
				}

			// no others with the same locale, make a new Locale
			// array omitting the locale no longer being used
			k = 0;
			Locale[] la = new Locale[fLocales.length - 1];
			for (int j = 0; j < fLocales.length; j++)
				{
				if (fLocales[j] != loc)
					la[k++] = fLocales[j];
				}
			fLocales = la;
			return;
			}
		}
	}
}