package q2java.core;

/**
 * An interface to mark classes that survive across levels.
 * This interface doesn't define any methods, it's just to
 * mark classes that shouldn't be purged from the various Listener
 * lists when a new level starts.  Similar to how java.lang.Clonable
 * marks classes that are allowed to be cloned, or how java.io.Serializable
 * marks classes allowed to be serialized.
 *
 * @author Barry Pederson
 */
public interface CrossLevel 
	{
	}