package q2java.core.event;

/**
 * Interface for objects that want to be called with PrintEvents.
 */
public interface PrintListener 
	{
	
/**
 * Called when a PrintEvent is fired.
 * @param pe q2java.core.event.PrintEvent
 */
public void print(PrintEvent pe);
}