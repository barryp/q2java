package q2java.core;

import java.util.*;

import q2java.Engine;
import q2java.core.event.*;

/**
 * leighd 04/11/99
 *
 * Class to handle basic svcmds.  This class will always receive events 
 * first as it is the first to be registered with the ServerCommandSupport class
 */
class BasicServerCommands implements ServerCommandListener 
	{
	
/**
 * Print timing info to the console.
 */
public static void commandHelp() 
	{    
	Game.dprint("Q2Java Game Framework\n\n");
	Game.dprint("   commands:\n");
	Game.dprint("      sv addgamelet <class-name> [<alias>]\n");
	Game.dprint("      sv removegamelet <alias>\n");
	Game.dprint("      sv gamelets      // list loaded gamelets\n");
	Game.dprint("\n");
	Game.dprint("      sv javamem       // show Java memory usage\n");
	Game.dprint("      sv javagc        // force a Java GC\n");
	Game.dprint("\n");
	Game.dprint("      sv time          // show performance timing\n");
	Game.dprint("      sv help          // this screen\n");
	Game.dprint("      sv <gamelet>.help // help for a loaded module\n");

	//leighd 04/10/99 - moved most of gamelet info code to
	//GameletManager command listgamelet. Now just list the number
	//of running gamelets.
	Game.dprint("\n");
	int i = Game.getGameletManager().getGameletCount();
	String gamelets = (i > 1 ? " gamelets" : " gamelet");
	Game.dprint("      " + i + gamelets + " currently loaded\n");	
	}
/**
 * Force the Java Garbage collector to run.
 */
public static void commandJavagc() 
	{
	long oldFree, freeMem, totalMem, gcStart, gcStop;
	Runtime rt = Runtime.getRuntime();

	oldFree = rt.freeMemory();
	gcStart = Engine.getPerformanceCounter();
	System.gc();
	gcStop = Engine.getPerformanceCounter();
	freeMem = rt.freeMemory();
	totalMem = rt.totalMemory();
		
	Game.dprint("GC freed " + (freeMem - oldFree) + " bytes in " + (((double)(gcStop-gcStart) / Engine.getPerformanceFrequency()) * 1000.0) + " msec\n");
	Game.dprint("Total Java memory: " + totalMem + " bytes    Used: " + (totalMem - freeMem) + " Free: " + freeMem + "\n");
	}
/**
 * Print Java memory info to the console.
 */
public static void commandJavamem() 
	{
	long totalMem, freeMem;
	Runtime rt = Runtime.getRuntime();
	totalMem = rt.totalMemory();
	freeMem = rt.freeMemory();
	Game.dprint("Total Java memory: " + totalMem + " bytes    Used: " + (totalMem - freeMem) + " Free: " + freeMem + "\n");
	}
/**
 * Dump a list of system properties.
 */
public static void commandProperties() 
	{
	Properties props = System.getProperties();
	Enumeration names = props.propertyNames();
	while (names.hasMoreElements())
		{
		String name = (String) names.nextElement();
		String value = props.getProperty(name);
		Game.dprint("[" + name + "] = [" + value + "]\n");
		}
	}
	public void serverCommandIssued(ServerCommandEvent e)
		{
		String command = e.getCommand();
		if (command.equals("properties"))
			{
			commandProperties();
			e.consume();
			}
		if (command.equals("javamem"))
			{
			commandJavamem();
			e.consume();
			}
		if (command.equals("javagc"))
			{
			commandJavagc();
			e.consume();                
			}
		if (command.equals("help"))
			{
			commandHelp();
			e.consume();
			}
		}
}