package q2java.core;

import java.lang.reflect.*;
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
 * Handle the "sv set" command, as in: "sv get xxx.yyy" by
 * locating the "xxx" gamelet, and printing the result of getYyy()
 *
 * @param sce q2java.core.event.ServerCommandEvent
 */
public static void commandGet(ServerCommandEvent sce) 
	{
	String[] args = sce.getArgs();
	
	// make sure there are enough args
	if (args.length < 3)
		{
		Game.dprint("Usage: sv get xxx.yyy\n");
		return;
		}

	// make sure the target has a gamelet name and a property
	String target = args[2];
	int p = target.indexOf('.');
	if ((p < 1) || (p > (target.length() - 2)))
		{
		Game.dprint("Usage: sv set xxx.yyy\n");
		return;
		}

	String gameletName = target.substring(0, p);
	Gamelet g = Game.getGameletManager().getGamelet(gameletName);
	if (g == null)
		{
		Game.dprint(gameletName + " was not found\n");
		return;
		}
		
	String gameletGetter = "get" + Character.toUpperCase(target.charAt(p+1)) + target.substring(p+2);
		
	try
		{
		Class gameletClass= g.getClass();
		Method setter = gameletClass.getMethod(gameletGetter, null);

		Game.dprint(setter.invoke(g, null) + "\n");
		}
	catch (NoSuchMethodException nsme)
		{
		Game.dprint(gameletGetter + "() not found\n");
		}
	catch (InvocationTargetException ite)
		{
		ite.getTargetException().printStackTrace();
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}
	}
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
/**
 * Handle the "sv set" command, as in: "sv set xxx.yyy zzz" by
 * locating the "xxx" gamelet, and invoking setYyy(zzz)
 *
 * @param sce q2java.core.event.ServerCommandEvent
 */
public static void commandSet(ServerCommandEvent sce) 
	{
	String[] args = sce.getArgs();
	
	// make sure there are enough args
	if (args.length < 4)
		{
		Game.dprint("Usage: sv set xxx.yyy zzz\n");
		return;
		}

	// make sure the target has a gamelet name and a property
	String target = args[2];
	int p = target.indexOf('.');
	if ((p < 1) || (p > (target.length() - 2)))
		{
		Game.dprint("Usage: sv set xxx.yyy zzz\n");
		return;
		}

	String gameletName = target.substring(0, p);
	Gamelet g = Game.getGameletManager().getGamelet(gameletName);
	if (g == null)
		{
		Game.dprint(gameletName + " was not found\n");
		return;
		}
		
	String gameletSetter = "set" + Character.toUpperCase(target.charAt(p+1)) + target.substring(p+2);
		
	try
		{
		Class gameletClass= g.getClass();
		Class[] methodParamTypes = new Class[1];
		methodParamTypes[0] = String.class;
		Method setter = gameletClass.getMethod(gameletSetter, methodParamTypes);

		Object[] methodParams = new Object[1];
		methodParams[0] = args[3];

		setter.invoke(g, methodParams);
		}
	catch (NoSuchMethodException nsme)
		{
		Game.dprint(gameletSetter + " method not found\n");
		}
	catch (InvocationTargetException ite)
		{
		ite.getTargetException().printStackTrace();
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}
	}
	public void serverCommandIssued(ServerCommandEvent e)
		{
		String command = e.getCommand();
		
		if (command.equals("properties"))
			{
			commandProperties();
			e.consume();
			return;
			}
			
		if (command.equals("javamem"))
			{
			commandJavamem();
			e.consume();
			return;
			}
			
		if (command.equals("javagc"))
			{
			commandJavagc();
			e.consume();
			return;
			}
			
		if (command.equals("help"))
			{
			commandHelp();
			e.consume();
			return;
			}
			
		if (command.equals("set"))
			{
			commandSet(e);
			e.consume();
			return;
			}

		if (command.equals("get"))
			{
			commandGet(e);
			e.consume();
			return;
			}			
		}
}