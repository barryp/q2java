package baseq2;

import java.util.Vector;
import javax.vecmath.*;

import q2java.*;
import q2jgame.*;
import baseq2.*;
import baseq2.spawn.trigger_multiple;

/**
 * Delays triggering of a trigger_multiple.
 *
 * @author Peter Donald
 */
 
public class TriggerDelayer implements FrameListener
	{
	protected trigger_multiple fOwner;
	protected Object fActivator;
	
public TriggerDelayer(trigger_multiple owner, Object activator) 
	{
	fActivator = activator;
	fOwner = owner; 
	}
public void runFrame(int phase)
	{
	fOwner.trigger( fActivator );  
	}
}