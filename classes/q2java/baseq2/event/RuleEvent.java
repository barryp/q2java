package q2java.baseq2.event;

import java.lang.reflect.*;
import q2java.Q2Recycler;
import q2java.core.event.GenericEvent;

/**
 * Class for q2java Event delegation model.
 * all events that deal with the actual process of scoring and rules 
 * should inherit from this class.
 *
 * @author Peter Donald 25/1/99
 */
public abstract class RuleEvent extends GenericEvent
	{
	
  public RuleEvent(int type)
	{
	  super(null,type); 
	}
  public RuleEvent(Object source, int type)
	{
	  super(source,type); 
	}
//leighd 04/14/99 - modified for event debugging, need to override
//GenericEvent toString, so that protected Fields get output correctly
public String toString()
	{
	StringBuffer sb = Q2Recycler.getStringBuffer();
	Class c = getClass();

	sb.append( c.toString() );
	sb.append(" [ ");

	//loop until we get to this class
	while(c != GenericEvent.class)
		{
		//get accessible(?) fields
		Field fields[] = c.getDeclaredFields();

		//get reference to superclass for next loop
		c = c.getSuperclass();

		//loop through all fields
		for( int i = 0; i < fields.length; i++ )
			{
			//if we hit a static field then ignore it
			if( Modifier.isStatic( fields[i].getModifiers() ) )
				{
				continue;
				}

			//add the field name
			sb.append( fields[i].getName() );    
			sb.append( "=" );
		  
			//try and add the field value
			try { sb.append( fields[i].get(this) ); }
			catch(IllegalArgumentException iae) {}
			catch(IllegalAccessException iae) {}
			sb.append(", ");          
			}
		}

	sb.append(" ] ");    

	String result = sb.toString();
	Q2Recycler.put(sb);
	return result;	
	}
}