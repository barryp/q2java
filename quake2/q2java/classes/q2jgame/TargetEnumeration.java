
package q2jgame;

import java.util.Enumeration;
import q2java.*;

public class TargetEnumeration implements java.util.Enumeration 
	{
	private Object fNext;
	private String fTargetName;
	private Enumeration fEntities;
	
/**
 * This method was created by a SmartGuide.
 * @param targetName java.lang.String
 */
public TargetEnumeration(String targetName) 
	{
	fTargetName = targetName;
	
	// if the targetName is null, we won't actually do anything,
	// just return false for hasMoreElements 
	if (targetName != null)
		{
		fEntities = NativeEntity.enumerateEntities();
		getNext();
		}
	}
/**
 * This method was created by a SmartGuide.
 */
private void getNext() 
	{
	fNext = null;
	while (fEntities.hasMoreElements())
		{
		GameEntity e = (GameEntity) fEntities.nextElement();
		if (e.fTargetName == fTargetName) // we can use == because we intern'ed the strings
			{
			fNext = e;
			return;
			}
		}
	}
/**
 * hasMoreElements method comment.
 */
public boolean hasMoreElements() 
	{
	return (fNext != null);
	}
/**
 * nextElement method comment.
 */
public Object nextElement() 
	{
	Object result = fNext;
	if (fNext != null)
		getNext();
	return result;
	}
}