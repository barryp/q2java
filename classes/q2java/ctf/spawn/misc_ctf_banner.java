package q2java.ctf.spawn;

/*
======================================================================================
==                                 Q2JAVA CTF                                       ==
==                                                                                  ==
==                   Author: Menno van Gangelen <menno@element.nl>                  ==
==                                                                                  ==
==            Based on q2java by: Barry Pederson <bpederson@geocities.com>          ==
==                                                                                  ==
== All sources are free for non-commercial use, as long as the licence agreement of ==
== ID software's quake2 is not violated and the names of the authors of q2java and  ==
== q2java-ctf are included.                                                         ==
======================================================================================
*/


import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/*-----------------------------------------------------------------------*/
/*QUAKED misc_ctf_banner (1 .5 0) (-4 -64 0) (4 64 248) TEAM2
The origin is the bottom of the banner.
The banner is 248 tall.
*/

/**
 * A misc_ctf_banner is a giant flag that
 * just sits and flutters in the wind.
 */

public class misc_ctf_banner extends GameObject implements FrameListener
{
	protected int fCurrentFrame;
	
	public misc_ctf_banner( String[] spawnArgs ) throws GameException
	{
		super( spawnArgs );
		
		fEntity.setSolid( NativeEntity.SOLID_NOT );
		fEntity.setModel( "models/ctf/banner/tris.md2" );

		if ( (fSpawnFlags & 1) != 0 ) // team2
			fEntity.setSkinNum( 1 );

		//fCurrentFrame = (MiscUtil.randomInt() & 0x0fff) % 16;
		fCurrentFrame = GameUtil.randomInt() % 16;
		fEntity.setFrame(fCurrentFrame);

		fEntity.linkEntity();
		
		// ask to be called back each server frame
		Game.addFrameListener(this, 0, 0);
	}
	/**
	 * Animate the banner.
	 * @param phase int
	 */
	public void runFrame(int phase) 
	{
		fCurrentFrame = (fCurrentFrame + 1) % 16;
		fEntity.setFrame(fCurrentFrame);
	}
}