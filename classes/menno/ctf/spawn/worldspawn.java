package menno.ctf.spawn;


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
import q2jgame.*;

public class worldspawn extends baseq2.spawn.worldspawn
{

	public worldspawn(String[] spawnArgs) throws GameException
	{
		super( spawnArgs );

		// now it's time to spawn the techs.
		try 
		{
			new item_tech1();
			new item_tech2();
			new item_tech3();
			new item_tech4();
		}
		catch ( Exception e )
		{
			// do nothing here.
			System.out.println( "error in spwaning techs... " + e );
		}

	}
}