package q2java.baseq2.spawn;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

public class target_speaker extends GameObject
	{
	private String fNoise;
	private int    fNoiseIndex;
	private float  fVolume;
	private float  fAttenuation;
	
public target_speaker(Element spawnArgs) throws GameException
	{
	super(spawnArgs);

	fNoise = getSpawnArg( "noise", (String)null );

	if(fNoise == null)
		{
		Game.dprint("target_speaker with no noise set at " + fEntity.getOrigin() + "\n" );
		return;
		}
		
	if (!fNoise.endsWith(".wav") )
		fNoise += ".wav";

	fNoiseIndex  = Engine.getSoundIndex( fNoise );
	fVolume      = getSpawnArg("volume", 1f );
	fAttenuation = getSpawnArg("attenuation", NativeEntity.ATTN_NORM );
	if ( fAttenuation == -1f )	// use -1 so 0 defaults to 1
		fAttenuation = NativeEntity.ATTN_NONE;

	// check for prestarted looping sound
	if ((fSpawnFlags & 1) != 0)
		fEntity.setSound( fNoiseIndex );

	fEntity.linkEntity(); // <-- thanks to James Bielby for pointing out that this was missing
	}
public void use(Player p) 
	{
	int		chan;
	if ((fSpawnFlags & 3) != 0)
		{	// looping sound toggles
		if (fEntity.getSound() != 0)
			fEntity.setSound(0);	// turn it off
		else
			fEntity.setSound(fNoiseIndex);	// start it
		}
	else
		{	// normal sound
		if ((fSpawnFlags & 4) != 0)
			chan = NativeEntity.CHAN_VOICE | NativeEntity.CHAN_RELIABLE;
		else
			chan = NativeEntity.CHAN_VOICE;
		// use a positioned_sound, because this entity won't normally be
		// sent to any clients because it is invisible
		fEntity.positionedSound(fEntity.getOrigin(), chan, fNoiseIndex, fVolume, fAttenuation, 0 );
		}
	}
}