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

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.GenericSpawnpoint;

public class info_player_team1 extends GenericSpawnpoint
	{
	public final static String REGISTRY_KEY = "spawn-team1";
	
public info_player_team1(Element spawnArgs) throws GameException
	{
	super(spawnArgs);
	Game.addLevelRegistry(REGISTRY_KEY, this);
	}
}