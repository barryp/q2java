package q2java.baseq2;

import java.util.Enumeration;
import q2java.NativeEntity;

/**
 * Utility class for enumerating through Players.  The elements it
 * returns are all instances of q2java.baseq2.Player (or a subclass).
 *
 * @author Barry Pederson
 */
class PlayerEnumeration implements Enumeration 
	{
	private Enumeration fEnum;
	private Object fNextPlayer;
	
/**
 * This method was created in VisualAge.
 */
public PlayerEnumeration() 
	{
	fEnum = NativeEntity.enumeratePlayerEntities();
	// prime the pump
	getNext();
	}
/**
 * Get the next element ready.
 */
private void getNext() 
	{
	fNextPlayer = null;
	while (fEnum.hasMoreElements())
		{
		Object obj = ((NativeEntity) fEnum.nextElement()).getReference();
		if (obj instanceof Player)
			{
			fNextPlayer = obj;
			return;
			}
		}
	}
/**
 * Are there any more players in the enumeration?
 */
public boolean hasMoreElements() 
	{
	return (fNextPlayer != null);
	}
/**
 * Get the next Player object.
 */
public Object nextElement() 
	{
	Object result = fNextPlayer;
	getNext();
	return result;
	}
}