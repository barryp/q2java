
package q2jgame;

import java.io.*;
import java.util.Enumeration;
import java.util.Random;

import q2java.*;

public class Game implements NativeGame
	{
	public static double fGameTime;
	private static int fFrameCount;
	public static CVar fBobUp;
	
	private static File fLogFile;	
	private static Random fRandom;
	
public static void debugLog(String s)
	{		
	try
		{
		FileOutputStream fos = new FileOutputStream(fLogFile.getPath(), true);
		PrintStream ps = new PrintStream(fos);
		ps.println(s);
		ps.close();
		}
	catch (IOException e)
		{
		}				
	}
public void init()
	{	
	File gameDir = new File(Engine.getGamePath());
	File sandbox = new File(gameDir, "sandbox");
	fRandom = new Random();
	fLogFile = new File(sandbox, "game.log");
	fLogFile.delete();
		
	debugLog("init()");

	CVar maxclients = new CVar("maxclients", "4", CVar.CVAR_SERVERINFO | CVar.CVAR_LATCH);
	CVar maxentities = new CVar("maxentities", "1024", CVar.CVAR_LATCH);
	Engine.dprint(maxclients + "\n");
	Engine.dprint(maxentities + "\n");

	fBobUp = new CVar("bob_up", "0.005", 0);
	
	NativeEntity.setMaxEntities((int)maxentities.getFloat());
	debugLog("Done with init()");
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
	debugLog("readGame(\"" + filename + "\")");
	Engine.dprint("Java readGame(\"" + filename + "\")\n");
	}
public void readLevel(String filename)
	{
	debugLog("readLevel(\"" + filename + "\")");
	Engine.dprint("Java readLevel(\"" + filename + "\")\n");
	}
public void runFrame()
	{
	fFrameCount++;
	fGameTime = fFrameCount * 0.1;
	
	Enumeration enum = new EntityEnumeration();
	while (enum.hasMoreElements())
		((GameEntity) enum.nextElement()).runFrame();

	enum = new PlayerEnumeration();
	while (enum.hasMoreElements())
		((Player) enum.nextElement()).endFrame();
	}
public void shutdown()
	{
	debugLog("shutdown()");
	Engine.dprint("Java shutdown() called\n");
	NativeEntity.clearAllEntities();
	}
public void spawnEntities(String mapname, String entString, String spawnPoint)
	{
	debugLog("spawnEntities()");
	GameEntity.spawnEntities(entString);
	}
public void writeGame(String filename)
	{
	debugLog("writeGame(\"" + filename + "\")");		
	Engine.dprint("Java writeGame(\"" + filename + "\")\n");
	}
public void writeLevel(String filename)
	{
	debugLog("writeLevel(\"" + filename + "\")");
	Engine.dprint("Java writeLevel(\"" + filename + "\")\n");
	}
}