
package q2jgame;

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import q2java.*;

public class Game implements NativeGame
	{
	// game clocks
	private static int fFrameCount;
	public static double fGameTime;
	
	// central place to check if this is a deathmatch game
	public static boolean fIsDeathmatch;

	// handy random number generator
	private static Random fRandom;
	
	// various CVars
	public static CVar fBobUp;	
	public static CVar fRollAngle;
	public static CVar fRollSpeed;
	
/**
 * This method was created by a SmartGuide.
 * @return double
 */
public static double cRandom() 
	{
	return (fRandom.nextFloat() - 0.5) * 2.0;
	}
public void init()
	{	
/*	
	// get setup so the debugLog() method has a place to write
	File gameDir = new File(Engine.getGamePath());
	File sandbox = new File(gameDir, "sandbox");
	fLogFile = new File(sandbox, "game.log");
	fLogFile.delete();
*/		
	// actually initialize the game
	Engine.debugLog("Game.init()");
	fRandom = new Random();
	
	// load cvars
	fBobUp = new CVar("bob_up", "0.005", 0);	
	fRollAngle = new CVar("sv_rollangle", "2", 0);
	fRollSpeed = new CVar("sv_rollspeed", "200", 0);
	
	System.out.println("Hello from Java");
	}
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public static float randomFloat() 
	{
	return fRandom.nextFloat();
	}
/**
 * This method was created by a SmartGuide.
 * @return int
 */
public static int randomInt() 
	{
	return fRandom.nextInt();
	}
public void readGame(String filename)
	{
	Engine.debugLog("Game.readGame(\"" + filename + "\")");
	}
public void readLevel(String filename)
	{
	Engine.debugLog("Game.readLevel(\"" + filename + "\")");
	}
public void runFrame()
	{
	Enumeration enum;
	Object obj;
	
	// increment the clocks
	fFrameCount++;
	fGameTime = fFrameCount * Engine.SECONDS_PER_FRAME;

	// notify all the players we're beginning a frame
	enum = new PlayerEnumeration();
	while (enum.hasMoreElements())
		((Player) enum.nextElement()).beginFrame();
				
	// give each non-player entity a chance to run				
	enum = new EntityEnumeration();
	while (enum.hasMoreElements())
		{
		obj = enum.nextElement();
		if (!(obj instanceof Player))
			((GameEntity) obj).runFrame();
		}

	// notify all the players we're ending a frame
	enum = new PlayerEnumeration();
	while (enum.hasMoreElements())
		((Player) enum.nextElement()).endFrame();
	}
public void shutdown()
	{
	Engine.debugLog("Game.shutdown()");
	}
public void spawnEntities(String mapname, String entString, String spawnPoint)
	{
	Engine.debugLog("Game.spawnEntities(\"" + mapname + ", <entString>, \"" + spawnPoint + "\")");


	try
		{
		StringReader sr = new StringReader(entString);
		StreamTokenizer st = new StreamTokenizer(sr);
		st.eolIsSignificant(false);
		st.quoteChar('"');
		int token;
		Vector v = new Vector(16);
		boolean foundClassname = false;
		String className = null;

		Object[] params = new Object[1];
		Class[] paramTypes = new Class[1];
		String[] sa = new String[1];
		paramTypes[0] = sa.getClass();

		FileOutputStream fos = new FileOutputStream(Engine.getGamePath() + "\\sandbox\\entity.log");
		PrintStream ps = new PrintStream(fos);		

		while ((token = st.nextToken()) != StreamTokenizer.TT_EOF)
			{
			switch (token)
				{
				case '"' : 
					if (foundClassname)
						{
						className = st.sval;
						foundClassname = false;
						break;
						}
					if (st.sval.equalsIgnoreCase("classname"))
						{
						foundClassname = true;
						break;
						}
					v.addElement(st.sval.intern()); 
					break;

				case '{' : foundClassname = false; break;

				case '}' : 
					sa = new String[v.size()];
					v.copyInto(sa);
					v.removeAllElements();
					params[0] = sa;
					try
						{
						Class entClass = Class.forName("q2jgame.spawn." + className);
						Constructor ctor = entClass.getConstructor(paramTypes);							
						GameEntity ent = (GameEntity) ctor.newInstance(params);
						ps.println(ent);
						}
					catch (Exception e)
						{
						ps.print("---- " + className + "(");
						if (sa != null)
							{
							String prefix = "";
							for (int i = 0; i < sa.length; i+=2)
								{
								ps.print(prefix + sa[i] + "=\"" + sa[i+1] + "\"");
								prefix = ", ";
								}
							}
						ps.println(")");	
						}

					foundClassname = false;
					className = null;
					break;

				default  : 
					ps.println("Unknown entity-string token: [" + st.sval + "]\n");
					foundClassname = false;				
				}
			}
		ps.close();
		}
	catch (Exception e)
		{
		Engine.dprint(e.getMessage() + "\n");
		Engine.debugLog(e.getMessage());
		}
	}
public void writeGame(String filename)
	{
	Engine.debugLog("Game.writeGame(\"" + filename + "\")");		
	}
public void writeLevel(String filename)
	{
	Engine.debugLog("Game.writeLevel(\"" + filename + "\")");
	}
}