package q2java.core;

/**
 * Interfaces for Player classes that can be unloaded.
 *
 * @author Barry Pederson
 */
public interface SwitchablePlayer 
	{
	
/**
 * Disassociate the object from the underlying NativeEntity.
 */
void dispose();
}