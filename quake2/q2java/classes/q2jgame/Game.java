
package q2jgame;

import java.io.*;
import java.util.Enumeration;

import q2java.*;

public class Game implements NativeGame
	{
	public static double fFrameTime;
	public static int fFrameCount;
	
	private static File fLogFile;	
	
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
	fLogFile = new File(sandbox, "game.log");
	fLogFile.delete();
		
	debugLog("init()");

	CVar maxclients = new CVar("maxclients", "4", CVar.CVAR_SERVERINFO | CVar.CVAR_LATCH);
	CVar maxentities = new CVar("maxentities", "1024", CVar.CVAR_LATCH);
	Engine.dprint(maxclients + "\n");
	Engine.dprint(maxentities + "\n");

	NativeEntity.setMaxEntities((int)maxentities.getFloat());
	debugLog("Done with init()");
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
	fFrameTime = fFrameCount * 0.1;
	
	Enumeration enum = new EntityEnumeration();
	while (enum.hasMoreElements())
		{
		GameEntity e = (GameEntity) enum.nextElement();
		e.runEntity();
		}
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