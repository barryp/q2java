package q2java.baseq2;

import java.io.*;
import java.lang.reflect.*;
import java.text.*;
import java.util.*;
import javax.vecmath.*;

import org.w3c.dom.*;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;

/**
 * Plain old boring deathmatch.
 *
 * @author Barry Pederson 
 */

public class Deathmatch extends q2java.core.Gamelet
	{
	protected Object fBaseQ2Token;
	
/**
 * Create the Gamelet.
 * @param gameletName java.lang.String
 */
public Deathmatch(Document gameletDoc)
	{
	super(gameletDoc);

	//leighd 04/10/99 - need to register package path for spawning.
	Game.addPackagePath("q2java.baseq2");

	// make sure there's some environment for the GameObjects
	// to operate in.
	fBaseQ2Token = BaseQ2.getReference();
	}
/**
 * Add spaces to a StringBuffer (called by svcmd_scores)
 *
 * @param sb StringBuffer to add to
 * @param int num Number of spaces to append
 */

public static void addSpaces(StringBuffer sb, int spaces) 
	{
	for ( int i = 0; i <= spaces; i++ ) 
		sb.append(" ");
	} 
/**
 * Get which class (if any) this Gamelet wants to use for a Player class.
 * @return java.lang.Class
 */
public Class getPlayerClass() 
	{
	return Player.class;
	}
/**
 * Display help info to the console.
 */
public void svcmd_help(String[] args) 
	{
	Game.dprint(BaseQ2.getVersion());
	Game.dprint("\n\n    sv commands:\n");
	Game.dprint("       scores\n");
	}
/**
 * Runs the svcmd.  For now, ignores arguments.
 * Later: { ping score time ascend descend }
 * and so on.
 * @param args java.lang.String[]
 * @author _Quinn <tmiller@haverford.edu>
 * @version 2
 */

public void svcmd_scores(String[] args) 
	{
	// _Quinn: 04/20/98: shamelessly looted from baseq2.Player
	// _Quinn: 05.04.98: made pretty printing prettier
	// Barryp: 1999-05-27: reworked for less string concatenations and StringBuffer/String object creation

	// Name           Score Ping Time Rate RelPing Rank
	// Rate is Score/Time
	// RelPing is relative ping, 0 to 1.0 with 1 the max.
	// Rank is Rate times RPing

	Enumeration enum = Player.enumeratePlayers();
	Vector playerData = new Vector();
	Vector players = new Vector();

	float minPing = 25000;
	float maxPing = 0;
	int i = 0;

	while (enum.hasMoreElements())
		{
		Player p = (Player) enum.nextElement();
		float playerD[] = new float[7];
		playerD[0] = p.getScore(); // score
		playerD[1] = p.fEntity.getPlayerPing(); // ping
		playerD[2] = ((Game.getGameTime() - p.fStartTime) / 60); //time
		playerD[3] = playerD[0] / playerD[2]; // rate
		playerD[4] = 0; // RelPing
		playerD[5] = 0; // K/M * RelPing
		playerD[6] = 15 - p.getName().length() - 1; // padding.

		if ( playerD[1] < minPing ) { minPing = playerD[1]; }
		if ( playerD[1] > maxPing ) { maxPing = playerD[1]; }

		// perform sorting based on playerD 0 to 6
		// ( constants ) later.

		playerData.addElement( playerD );
		players.addElement( p.getName() );
		} // end while loop.

		int playerCount = players.size();

	// calculate relPing.
	float range = maxPing - minPing;
	if ( range == 0 ) { range = 1; } // no division errors...
	float difference = 0;
	for ( i = 0; i < playerCount; i++ ) 
		{
		difference = ((float[])playerData.elementAt(i))[1] - minPing;
		if ( difference == 0 ) { difference = 1; } // head off zeroresults.
		((float[])playerData.elementAt(i))[4] = difference / range;
		} // end calculating loop.

	// calculate rank.
	float pD[] = new float[7];
	for ( i = 0; i < playerCount; i++ ) 
		{
		pD = (float[])playerData.elementAt(i);
		pD[5] = pD[3] * pD[4];
		playerData.setElementAt( pD, i);
		} // end rank calculation

	// later, sort based on args passed in.

	// prepare to pretty print the numbers
	DecimalFormat dfThree = new DecimalFormat( "##0" );
	DecimalFormat dfTwoDotOne = new DecimalFormat( "##.0" );
	DecimalFormat dfDotThree = new DecimalFormat( "#.0##" );
	
		// generate the pretty printing.
	StringBuffer sb = Q2Recycler.getStringBuffer();
	sb.append("Name            Score Ping  Time Rate RelPing Rank\n" );
	
	String s;
	for (i = 0; i < playerCount; i++)
		{
		pD = (float[])playerData.elementAt(i);
		// name
		sb.append( players.elementAt(i).toString());
		addSpaces(sb, (int) pD[6]);
		
		// score
		s = dfThree.format(pD[0]);
		addSpaces(sb, 5 - s.length());
		sb.append(s);
		
		// ping
		s = dfThree.format(pD[1]);
		addSpaces(sb, 4 - s.length());
		sb.append(s);
		
		// time
		s = dfThree.format(pD[2]);
		addSpaces(sb, 4 - s.length());
		sb.append(s);
		
		// rate
		s = dfTwoDotOne.format(pD[3]);
		addSpaces(sb, 4 - s.length());
		sb.append(s);
		
		// relping
		s = dfDotThree.format(pD[4]);
		addSpaces(sb, 7 - s.length());
		sb.append(s);
		
		// rank
		s = dfDotThree.format(pD[5]);
		addSpaces(sb, 4 - s.length());
		sb.append(s);
		sb.append('\n');
		}

	System.out.print(sb.toString());
	Q2Recycler.put(sb);
	} // end scores ();
/**
 * Unload baseq2 from the game - do this at your own risk.
 */
public void unload() 
	{
	Game.removePackagePath("q2java.baseq2");
	BaseQ2.freeReference(fBaseQ2Token);
	}
}