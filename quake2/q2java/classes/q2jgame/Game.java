
import java.io.*;
import java.util.Enumeration;

public class Game
	{
	public static double fTime;
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
public static void init()
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
public static void readGame(String filename)
	{
	debugLog("readGame(\"" + filename + "\")");
	Engine.dprint("Java readGame(\"" + filename + "\")\n");
	}
public static void readLevel(String filename)
	{
	debugLog("readLevel(\"" + filename + "\")");
	Engine.dprint("Java readLevel(\"" + filename + "\")\n");
	}
public static void runFrame()
	{
	fFrameCount++;
	fTime = fFrameCount * 0.1;
	
	Enumeration enum = new EntityEnumeration();
	while (enum.hasMoreElements())
		{
		Entity e = (Entity) enum.nextElement();
		e.runEntity();
		}
	}
public static void shutdown()
	{
	debugLog("shutdown()");
	Engine.dprint("Java shutdown() called\n");
	NativeEntity.clearAllEntities();
	}
public static void spawnEntities(String mapname, String entString, String spawnPoint)
	{
	debugLog("spawnEntities()");
	Entity.spawnEntities(entString);
	}
public static void writeGame(String filename)
	{
	debugLog("writeGame(\"" + filename + "\")");		
	Engine.dprint("Java writeGame(\"" + filename + "\")\n");
	}
public static void writeLevel(String filename)
	{
	debugLog("writeLevel(\"" + filename + "\")");
	Engine.dprint("Java writeLevel(\"" + filename + "\")\n");
	}
}