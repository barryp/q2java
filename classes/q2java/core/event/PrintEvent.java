package q2java.core.event;

import java.util.Locale;
import q2java.NativeEntity;
import q2java.Recycler;
/**
 * Event to represent something that needs to be printed on somebody's screen.
 */
public class PrintEvent extends GenericEvent implements Consumable
	{
	protected static Recycler gRecycler = Recycler.getRecycler(PrintEvent.class);
	
	protected boolean fConsumed;
	protected int fPrintChannel;
	protected int fPrintFlags;
	protected String fSourceName;
	protected Object fDestination;
	protected Locale fLocale;
	protected String fMessage;
	protected String fPlayerMessage; // hack where Player class can cache the message suitably formatted for player display

	// various print "channels"
	public final static int PRINT_JAVA				= 1; // System.out & System.err
	public final static int PRINT_SERVER_CONSOLE	= 2; // debug info sent to server console
	public final static int PRINT_ANNOUNCE			= 4; // gamewide announcements 
	public final static int PRINT_TALK				= 8; // players yapping
	public final static int PRINT_TALK_TEAM			= 16;// chats within a team
	public final static int PRINT_TALK_PRIVATE		= 32;// chats directed to a particular destination
	public final static int PRINT_ALL				= 0xffffffff;
	
/**
 * No-arg constructor, works with Recycler.
 * @param printType int
 * @param printFlags int
 * @param source NativeEntity
 * @param msg java.lang.String
 */
public PrintEvent() 
	{
	super(GAME_PRINT_EVENT);
	}
public final void consume() 
	{ 
	setConsumed(true); 
	}
/**
 * Get what object (if any) this print event is directed to.
 * @return java.lang.Object
 */
public Object getDestination() 
	{
	return fDestination;
	}
/**
 * This method was created in VisualAge.
 * @return q2java.core.event.PrintEvent
 * @param printType int
 * @param printFlags int
 * @param source java.lang.Object
 * @param dest java.lang.Object
 * @param msg java.lang.String
 */
public final static PrintEvent getEvent(int printChannel, int printFlags, Object source, String sourceName, Object dest, String msg) 
	{
	PrintEvent result = (PrintEvent) gRecycler.getObject();

	result.fConsumed = false;
	result.fPrintChannel = printChannel;
	result.fPrintFlags = printFlags;
	result.source = source;
	result.fSourceName = sourceName;
	result.fDestination = dest;
	result.fMessage = msg;
	
	return result;
	}
/**
 * Get the locale this message is formatted for.
 * @return java.util.Locale
 */
public Locale getLocale() 
	{
	return fLocale;
	}
/**
 * Get the string that's being printed.
 * @return java.lang.String
 */
public String getMessage() 
	{
	return fMessage;
	}
/**
 * Kind of hacky, but rather than format the same message over and over again,
 * the Player class may chose to check this property and use it if not null,
 * and set it if null.
 *
 * @return java.lang.String
 */
public String getPlayerMessage() 
	{
	return fPlayerMessage;
	}
/**
 * Get which type of print event this is.
 * @return int
 */
public int getPrintChannel() 
	{
	return fPrintChannel;
	}
/**
 * Get print flags (their meaning is undefined by this class).
 * @return int
 */
public int getPrintFlags() 
	{
	return fPrintFlags;
	}
/**
 * Get the name of the source object.
 * @return java.lang.String
 */
public String getSourceName() 
	{
	return fSourceName;
	}
  public final boolean isConsumed() { return fConsumed; }        
/**
 * Clean up a PrintEvent and make it available for reuse.
 * @param pe q2java.core.event.PrintEvent
 */
public final void recycle() 
	{
	// clear references
	source = null;
	fSourceName = null;
	fDestination = null;
	fMessage = null;
	fLocale = null;
	fPlayerMessage = null;

	// put back in recycler
	gRecycler.putObject(this);
	}
public final void setConsumed(boolean consumed) 
	{ 
	fConsumed = consumed; 
	}
/**
 * Set which locale this message is formatted for.
 * @param loc java.util.Locale
 */
void setLocale(Locale loc) 
	{	
	fLocale = loc;
	}
/**
 * Change the message this event is carrying.
 * @param msg java.lang.String
 */
public void setMessage(String msg) 
	{
	// clear the cached player version of the message if necessary
	if ((msg == null) || (!msg.equals(fMessage)))
		fPlayerMessage = null;
		
	fMessage = msg;
	}
/**
 * Kind of hacky, but rather than format the same message over and over again,
 * the Player class may chose to check this property and use it if not null,
 * and set it if null.
 *
 * @return java.lang.String
 */
public void setPlayerMessage(String msg) 
	{
	fPlayerMessage = msg;
	}
}