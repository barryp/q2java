package q2java.core.event;

import java.lang.reflect.*;
import q2java.Engine;
import q2java.NativeEntity;

/**
 * Support class for SoundEvent delegation.
 *
 * @author Barry Pederson
 */
public final class SoundSupport extends GenericEventSupport
	{
	private static Method gInvokeMethod;
  
  	static
		{
	  	try
			{
	  		gInvokeMethod = SoundListener.class.
	    	  getMethod("soundHeard", new Class[] { SoundEvent.class } );	
			}
	 	catch (NoSuchMethodException nsme) {}
		}
	
/**
 * Add a SoundListener
 *
 * @param sl SoundListener
 * @param highPriority true if the listener should be added to the front
 *   of the listener list so it sees events sooner - important if the
 *   listener might want to consume an event, like a silencer might do.
 *   If false, the listener is added to the end of the list, and sees
 *   the events after all the existing listeners.
 */
public void addSoundListener(SoundListener sl, boolean highPriority)
	{
	addListener(sl, highPriority);
	}
/**
 * Fire a SoundEvent, and actually cause the sound to be heard if the event isn't consumed.
 */
public void fireEvent(NativeEntity source, int soundChannel, int soundIndex, float volume, float attenuation, float timeofs)
	{
	SoundEvent se = SoundEvent.getEvent(SoundEvent.TYPE_PLAIN, source, soundChannel, soundIndex, volume, attenuation, timeofs);

	fireEvent(se, gInvokeMethod);

	// actually emit the sound
	if (!se.isConsumed())
		((NativeEntity)se.getSource()).sound(se.fSoundChannel, se.fSoundIndex, se.fVolume, se.fAttenuation, se.fTimeOfs);
		
	se.recycle(); 
	}
/**
 * Fire a Muzzle SoundEvent, and add the silence bit if one of the listeners consumed the event.
 *
 * @return the muzzleFlag, with the silence bit possibly added.
 */
public int fireMuzzleEvent(NativeEntity source, int muzzleFlag)
	{
	SoundEvent se = SoundEvent.getEvent(SoundEvent.TYPE_MUZZLE, source, 0, muzzleFlag, 0, 0, 0);

	fireEvent(se, gInvokeMethod);

	// mark the flag as silenced if something consumed the event
	if (se.isConsumed())
		muzzleFlag |= Engine.MZ_SILENCED;
		
	se.recycle();

	return muzzleFlag;
	}
/**
 * Fire a Muzzle SoundEvent, and add the silence bit if one of the listeners consumed the event.
 *
 */
public void fireTempEvent(NativeEntity source, int tempEvent)
	{
	SoundEvent se = SoundEvent.getEvent(SoundEvent.TYPE_TEMP_EVENT, source, 0, tempEvent, 0, 0, 0);

	fireEvent(se, gInvokeMethod);

	// mark the flag as silenced if something consumed the event
	if (!se.isConsumed())
		source.setEvent(tempEvent);
		
	se.recycle();
	}
public void removeSoundListener(SoundListener sl)
	{
	removeListener(sl);
	}
}