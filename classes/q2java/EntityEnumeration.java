
package q2java;

/**
 * Enumerate through Game entities.
 *
 * @author Barry Pederson
 */
class EntityEnumeration implements java.util.Enumeration
	{
	private NativeEntity fNext;
	private Class fTarget;
	
/**
 * This method was created by a SmartGuide.
 */
public EntityEnumeration() 
	{
	fTarget = null;
	fNext = NativeEntity.findNext(null, fTarget);
	}
public EntityEnumeration(String targetClassName)
	{
	try
		{
		fTarget = Class.forName(targetClassName);			
		fNext = NativeEntity.findNext(null, fTarget);
		}
	catch (ClassNotFoundException e)
		{
		}
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
	fNext = NativeEntity.findNext(result, fTarget);
	return result;
	}
}