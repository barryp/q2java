package barryp.misc;

import q2java.NativeEntity;
import q2java.core.*;
import q2java.core.event.*;
import q2java.baseq2.Player;

/**
 * Prevent players from chatting while underwater.
 */
public class GlubGlub extends Gamelet implements PrintListener
	{
	
/**
 * Create the gamelet object.
 * @param gameletName java.lang.String
 */
public GlubGlub(String gameletName) 
	{
	super(gameletName);
	}
/**
 * Actually initialize the Gamelet for action.
 */
public void init() 
	{
	// sign up to receive broadcast messages using the default locale
	// specify high priority so that we see messages before players do.
	Game.getPrintSupport().addPrintListener(this, PrintEvent.PRINT_TALK+PrintEvent.PRINT_TALK_TEAM, null, true);	
	}
/**
 * Called when a PrintEvent is fired.
 * @param pe q2java.core.event.PrintEvent
 */
public void print(PrintEvent pe)
	{
	try
		{
		if (((Player)((NativeEntity) pe.getSource()).getReference()).getWaterLevel() > 2)
			pe.setMessage("..glub...glub..glub..");
		}
	catch (Exception e)
		{
		// probably no source specified, or class cast exception
		// but so what?..just catch the exception and ignore it
		}		
	}
/**
 * Unload this gamelet.
 */
public void unload() 
	{
	Game.getPrintSupport().removePrintListener(this);	
	}
}