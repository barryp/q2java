package barryp.misc;

import org.w3c.dom.*;

import q2java.NativeEntity;
import q2java.core.*;
import q2java.core.event.*;
import q2java.baseq2.Player;

/**
 * Prevent players from chatting while underwater.
 */
public class GlubGlub extends Gamelet implements PrintListener
	{
	protected String fMessage = "..glub...glub..glub..";
	
/**
 * Create the gamelet object.
 * @param gameletInfo org.w3c.dom.Document
 */
public GlubGlub(Document gameletInfo) 
	{
	super(gameletInfo);

	// set any properties specified in <param> tags.
	XMLTools.parseParams(gameletInfo.getDocumentElement(), this, GlubGlub.class);
	
	// sign up to receive broadcast messages using the default locale
	// specify high priority so that we see messages before players do.
	Game.getPrintSupport().addPrintListener(this, PrintEvent.PRINT_TALK+PrintEvent.PRINT_TALK_TEAM, null, true);		
	}
/**
 * Get the message displayed when players try to talk while underwater.
 * @return current message
 */
public String getMessage() 
	{
	return fMessage;
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
			pe.setMessage(fMessage);
		}
	catch (Exception e)
		{
		// probably no source specified, or class cast exception
		// but so what?..just catch the exception and ignore it
		}		
	}
/**
 * Change the message displayed when players try to talk while underwater.
 * @param msg java.lang.String
 */
public void setMessage(String msg) 
	{
	fMessage = msg;
	}
/**
 * Unload this gamelet.
 */
public void unload() 
	{
	Game.getPrintSupport().removePrintListener(this);	
	}
}