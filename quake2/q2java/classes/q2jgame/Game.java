
package q2jgame;

import java.io.*;
import java.util.Enumeration;
import java.util.Random;

import q2java.*;

public class Game implements NativeGame
	{
	// game clocks
	private static int fFrameCount;
	public static double fGameTime;

	// handy random number generator
	private static Random fRandom;
	
	// various CVars
	public static CVar fBobUp;	
	public static CVar fRollAngle;
	public static CVar fRollSpeed;
	
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
	fFrameCount++;
	fGameTime = fFrameCount * Engine.SECONDS_PER_FRAME;
		
	Enumeration enum = new EntityEnumeration();
	while (enum.hasMoreElements())
		((GameEntity) enum.nextElement()).runFrame();

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
	GameEntity.spawnEntities(entString);
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