package q2java.baseq2.gui;

import java.util.Vector;
import q2java.core.Game;
import q2java.baseq2.Player;
import q2java.baseq2.event.*;
import q2java.gui.*;

/**
 * Menu bound to a baseq2 Player.
 *
 * @author Barry Pederson
 */
public class PlayerMenu extends GenericMenu implements PlayerCommandListener
	{
	protected Vector fCommands;
	protected Player fPlayer;
	protected float fLastChangeTime;
	
/**
 * PlayerMenu constructor comment.
 * @param owner q2java.NativeEntity
 */
public PlayerMenu() 
	{
	fCommands = new Vector();
	}
/**
 * Add an item to the menu.
 * @param displayStrings array of strings to display as a single item.
 * @param command player command to execute if item is selected
 */
public void addMenuItem(String[] displayStrings, String command) 
	{
	super.addMenuItem(displayStrings);
	fCommands.addElement(command);
	}
/**
 * Close the menu and forget about the player we're bound to.
 */
public void close() 
	{
	super.close();
	
	// stop receiving player commands.
	fPlayer.removePlayerCommandListener(this);

	// forget about the player
	fPlayer = null;
	}
/**
 * Handle the inven command by closing the menu
 * (the player must have hit TAB again)
 */
public void cmd_inven(Player source, String[] argv, String args)
	{
	close();
	}
/**
 * Player hit ']' probably.
 */
public void cmd_invnext(Player source, String[] argv, String args)
	{
	float t;
	if ((t = Game.getGameTime()) != fLastChangeTime)
		{
		fLastChangeTime = t;
		selectNextItem();
		}
	}
/**
 * Player probably hit '['
 */
public void cmd_invprev(Player source, String[] argv, String args)
	{
	float t;
	if ((t = Game.getGameTime()) != fLastChangeTime)
		{
		fLastChangeTime = t;
		selectPreviousItem();
		}
	}
/**
 * Called when player presses Enter key
 */
public void cmd_invuse(Player source, String[] argv, String args)
	{
	String command = (String)fCommands.elementAt(getSelectedIndex());
	if (command != null)
		fPlayer.playerCommand(command);
	close();
	}
/**
 * Player hit ESC?
 */
public void cmd_putaway(Player source, String[] argv, String args)
	{
	close();
	}
public void commandIssued(PlayerCommandEvent e)
	{
	String command = e.getCommand();

	if( command.equals("inven") )
	    {
		cmd_inven( e.getPlayer(), null, e.getArgs() );
	    }
	else if( command.equals("putaway") )
	    {
		cmd_putaway( e.getPlayer(), null, e.getArgs() );
	    }
	else if( command.equals("invnext") )
	    {
		cmd_invnext( e.getPlayer(), null, e.getArgs() );
	    }
	else if( command.equals("invuse") )
	    {
		cmd_invuse( e.getPlayer(), null, e.getArgs() );
	    }
	else if( command.equals("invprev") )
	    {
		cmd_invprev( e.getPlayer(), null, e.getArgs() );
	    }
	}
/**
 * Show the menu on the player's screen.
 */
public void show(Player p) 
	{
	super.show(p.fEntity);

	// remember who we belong to
	fPlayer = p;
	
	// start intercepting menu commands
	p.addPlayerCommandListener(this);
	}
}