package barryp.flashgrenade;

import q2jgame.*;
import baseq2.*;

/**
 * Blind a player for a short time.
 *
 * @author Barry Pederson
 */
public class FlashBlindness implements FrameListener, PlayerStateListener
	{
	Player fPlayer;
	float fPower;
	int fClearDelay;

	protected final static int   CLEAR_DELAY = 20; // how many frames to wait before starting to restore player's vision
	protected final static float CLEAR_RATE = 0.95F; // how much fPower is multiplied by each frame.  smaller values mean faster restoration.
	protected final static float BLINDNESS_ENDS = 0.05F; // stop message with player once fPower drops below this value
	
/**
 * Start flashblinding a player.
 * @param p Player to flashblind.
 * @param flashPower initial intensity, 1.0 will totally white out screen, 0.5 half blinds the player, and so on.
 */
public FlashBlindness(Player p, float flashPower) 
	{
	fPlayer = p;
	fPower = flashPower;
	
	Game.addFrameListener(this, 0, 0);
	p.addPlayerStateListener(this);
	}
/**
 * Disassociate this object from the rest of the game.
 */
public void dispose() 
	{
	Game.removeFrameListener(this);
	fPlayer.removePlayerStateListener(this);
	}
/**
 * Called when the player dies, disconnects, or changes level.
 * @param p baseq2.Player
 * @param changeEvent int
 */
public void playerStateChanged(Player p, int changeEvent) 
	{
	dispose();
	}
/**
 * Blind the player a bit.
 * @param phase int
 */
public void runFrame(int phase) 
	{
	fPlayer.addBlend(1F, 1F, 1F, (fPower > 1 ? 1 : fPower));

	if (fClearDelay < CLEAR_DELAY)
		fClearDelay++;
	else
		{			
		fPower *= CLEAR_RATE;
		if (fPower < BLINDNESS_ENDS)
			dispose();
		}
	}
}