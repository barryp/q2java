
package q2jgame.spawn;


import q2java.*;
import q2jgame.*;

public class target_speaker extends GameEntity
	{
	private String fNoise;
	private int    fNoiseIndex;
	private float  fVolume;
	private float  fAttenuation;
	
public target_speaker(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);

	fNoise = getSpawnArg( "noise", (String)null );

	if(fNoise == null)
		{
		PrintManager.dprint("target_speaker with no noise set at " + getOrigin() + "\n" );
		return;
		}
		
	if (!fNoise.endsWith(".wav") )
		fNoise += ".wav";

	fNoiseIndex  = Engine.soundIndex( fNoise );
	fVolume      = getSpawnArg( "volume", 1f );
	fAttenuation = getSpawnArg( "attenuation", ATTN_NORM );
	if ( fAttenuation == -1f )	// use -1 so 0 defaults to 1
		fAttenuation = ATTN_NONE;

	// check for prestarted looping sound
	if ( (fSpawnFlags & 1) != 0 )
		setSound( fNoiseIndex );
	}
public void use(Player p) 
	{
	int		chan;

	if ( (fSpawnFlags & 3) != 0 )
		{	// looping sound toggles
		if ( getSound() != 0 )
			setSound( 0 );	// turn it off
		else
			setSound( fNoiseIndex );	// start it
		}
	else
		{	// normal sound
		if ( (fSpawnFlags & 4) != 0 )
			chan = CHAN_VOICE|CHAN_RELIABLE;
		else
			chan = CHAN_VOICE;
		// use a positioned_sound, because this entity won't normally be
		// sent to any clients because it is invisible
		positionedSound( getOrigin(), chan, fNoiseIndex, fVolume, fAttenuation, 0 );
		}
	}
}