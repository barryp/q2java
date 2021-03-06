package q2java;

/** 
 * Enumerate just through player entities.
 *
 * @author Barry Pederson
 */ 
class PlayerEntityEnumeration implements java.util.Enumeration
	{
	private NativeEntity fNext;
	
/**
 * This method was created by a SmartGuide.
 */
public PlayerEntityEnumeration() 
	{
	fNext = NativeEntity.findNextPlayer(null);
	}
public boolean hasMoreElements()
	{
	return (fNext != null);
	}
public Object nextElement()
	{
	if (fNext == null)
		return null;

	NativeEntity result = fNext;
	fNext = NativeEntity.findNextPlayer(result);
	return result;
	}
}