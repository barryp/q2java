package q2java.core.event;

import org.w3c.dom.Document;

import q2java.Recycler;
import q2java.core.Gamelet;

public class GameletEvent extends GameEvent
	{
	public final static int GAMELET_ADDED = 0; // fired -after- gamelet is loaded
	public final static int GAMELET_UNLOADING = 1; // fired -before- gamelet is actually unloaded
	public final static int GAMELET_LOADING = 2; // fired -before- gamelet is actually loaded
	
	protected int fState;
	protected Gamelet fGamelet;
	protected Document fGameletInfo;

	protected static Recycler gRecycler = Recycler.getRecycler(GameletEvent.class);
	
public GameletEvent()
	{
	super(GAME_GAMELET_EVENT);
	}
/**
 * if you want conserve memory use this method to get message objects.
 * Note you must then call releaseEvent to conserve memory
 */
public static final GameletEvent getEvent(int state, Gamelet g, Document d) 
	{
	GameletEvent event = (GameletEvent) gRecycler.getObject();

	// if ever implement single player then have to include
	// the local gameinfo as source
	//event.source = 
	event.fGamelet = g;
	event.fGameletInfo = d;
	event.fState = state;
	
	return event; 
	}
public final Gamelet getGamelet() 
	{ 
	return fGamelet; 
	}
/**
 * Get the Document describing a Gamelet that will be loaded shortly.
 * @return org.w3c.dom.Document
 */
public Document getGameletDocument() 
	{
	return fGameletInfo;
	}
/**
 * state of game that caused this message. 
 * see above list of constants for possible values.
 */
public final int getState() 
	{ 
	return fState; 
	}
/**
 * Releases an event and may put it in cache to be re-used.
 */
public final static void releaseEvent(GameletEvent event)
	{
	event.fGamelet = null;
	event.fGameletInfo = null;
	gRecycler.putObject(event);
	}
}