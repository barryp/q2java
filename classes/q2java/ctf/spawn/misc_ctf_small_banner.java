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

/**
 * A misc_ctf_banner is a small flag that
 * just sits and flutters in the wind.
 */

public class misc_ctf_small_banner extends misc_ctf_banner implements FrameListener
{
	
	public misc_ctf_small_banner( String[] spawnArgs ) throws GameException
	{
		super( spawnArgs );
		
		fEntity.setModel( "models/ctf/banner/tris.md2" );
	}
}