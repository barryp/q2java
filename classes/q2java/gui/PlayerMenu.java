package q2java.gui;

import java.util.Vector;
import baseq2.Player;

/**
 * Menu bound to a baseq2 Player.
 *
 * @author Barry Pederson
 */
public class PlayerMenu extends GenericMenu 
	{
	protected Vector fCommands;
	protected Player fPlayer;
	
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
	fPlayer.removeCommandHandler(this);

	// forget about the player
	fPlayer = null;
	}
/**
 * Handle the inven command by closing the menu
 * (the player must have hit TAB again)
 */
public void cmd_inven(baseq2.Player source, String[] argv, String args)
	{
	close();
	}
/**
 * Player hit ']' probably.
 */
public void cmd_invnext(baseq2.Player source, String[] argv, String args)
	{
	selectNextItem();
	}
/**
 * Player probably hit '['
 */
public void cmd_invprev(baseq2.Player source, String[] argv, String args)
	{
	selectPreviousItem();
	}
/**
 * Called when player presses Enter key
 */
public void cmd_invuse(baseq2.Player source, String[] argv, String args)
	{
	String command = (String)fCommands.elementAt(getSelectedIndex());
	if (command != null)
		fPlayer.playerCommand(command);
	close();
	}
/**
 * Player hit ESC?
 */
public void cmd_putaway(baseq2.Player source, String[] argv, String args)
	{
	close();
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
	p.addCommandHandler(this);
	}
}