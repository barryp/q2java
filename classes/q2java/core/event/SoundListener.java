package q2java.core.event;

/**
 * Interface for objects that want to be called with PrintEvents.
 */
public interface SoundListener 
	{
	
/**
 * Called when a SoundEvent is fired.
 * @param pe q2java.core.event.SoundEvent
 */
public void soundHeard(SoundEvent se);
}