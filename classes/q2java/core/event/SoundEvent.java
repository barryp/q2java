package q2java.core.event;

import q2java.NativeEntity;
import q2java.Recycler;
/**
 * Event to represent a sound event.
 *
 * @author Barry Pederson
 */
public class SoundEvent extends GenericEvent implements Consumable
	{
	protected static Recycler gRecycler = Recycler.getRecycler(SoundEvent.class);
	
	protected boolean fConsumed;
	protected NativeEntity fSource;
	protected int fType;
	protected int fSoundChannel;
	protected int fSoundIndex;
	protected float fVolume;
	protected float fAttenuation;
	protected float fTimeOfs;

	// various types of sound events
	public final static int TYPE_PLAIN = 1;
	public final static int TYPE_MUZZLE = 2;
	public final static int TYPE_TEMP_EVENT = 3;
	
/**
 * No-arg constructor, works with Recycler.
 * @param printType int
 * @param printFlags int
 * @param source NativeEntity
 * @param msg java.lang.String
 */
public SoundEvent() 
	{
	super(GAME_SOUND_EVENT);
	}
public final void consume() 
	{ 
	setConsumed(true); 
	}
/**
 * This method was created in VisualAge.
 * @return q2java.core.event.PrintEvent
 * @param printType int
 * @param printFlags int
 * @param source java.lang.Object
 * @param dest java.lang.Object
 * @param msg java.lang.String
 */
public final static SoundEvent getEvent(int soundType, NativeEntity source, int soundChannel, int soundIndex, float volume, float attenuation, float timeofs)
	{
	SoundEvent result = (SoundEvent) gRecycler.getObject();

	result.fConsumed = false;
	result.fType = soundType;
	result.source = result.fSource = source;
	result.fSoundChannel = soundChannel;
	result.fSoundIndex = soundIndex;
	result.fVolume = volume;
	result.fAttenuation = attenuation;
	result.fTimeOfs	= timeofs;
	
	return result;
	}
/**
 * Get the channel for this sound.
 * @return int
 */
public int getSoundChannel() 
	{
	return fSoundChannel;
	}
/**
 * Get which type of sound this event is describing.
 * @return one of the SoundEvent.TYPE_* constants.
 */
public int getSoundType() 
	{
	return fType;
	}
/**
 * Convenience shortcut to getting the source entity, equivalent to "(NativeEntity)getSource()".
 * @return q2java.NativeEntity
 */
public NativeEntity getSourceEntity() 
	{
	return fSource;
	}
  public final boolean isConsumed() { return fConsumed; }          
/**
 * Clean up a SoundEvent and make it available for reuse.
 * @param pe q2java.core.event.SoundEvent
 */
public final void recycle() 
	{
	source = fSource = null;
	
	// put back in recycler
	gRecycler.putObject(this);
	}
public final void setConsumed(boolean consumed) 
	{ 
	fConsumed = consumed; 
	}
}