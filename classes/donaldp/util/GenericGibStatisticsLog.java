package donaldp.util;

import java.io.*;
import java.util.*;
import q2java.CVar;
import q2java.core.*;

abstract public class GenericGibStatisticsLog
{
  protected boolean fLogging = false;
  protected float fMapStartTime = 0.0f;
  protected String fPatchName = "q2java";
  protected BufferedWriter fOut = null;
  protected String fEndOfLine = "\r\n";

  /**
   * remove any "\t" 's from the string and return it.
   */
  protected String cleanString(String string) 
	{
	  return string;
	}
  /**
   * return current date in gibstats format.
   */
  protected String getCurrentDateString()
	{
	  Calendar calendar = new GregorianCalendar();
	  
	  String s = "";
	  
	  if( calendar.get(Calendar.DAY_OF_MONTH) < 10 ) { s += "0"; }
	  s += calendar.get(Calendar.DAY_OF_MONTH) + ".";

	  if( calendar.get(Calendar.MONTH) < 10 ) { s += "0"; }
	  s += calendar.get(Calendar.MONTH) + ".";
	  
	  if( (calendar.get(Calendar.YEAR) % 100) < 10 ) { s += "0"; }
	  s += calendar.get(Calendar.YEAR) % 100;
	  
	  return s;
	}
  /**
   * return current time in gibstats format.
   */
  protected String getCurrentTimeString()
	{
	  Calendar calendar = new GregorianCalendar();
	  
	  String s = "";
	  
	  if( calendar.get(Calendar.HOUR) < 10 ) { s += "0"; }
	  s += calendar.get(Calendar.HOUR) + ":";
	  
	  if( calendar.get(Calendar.MINUTE) < 10 ) { s += "0"; }
	  s += calendar.get(Calendar.MINUTE) + ":";
	  
	  if( calendar.get(Calendar.SECOND) < 10 ) { s += "0"; }
	  s += calendar.get(Calendar.SECOND);
	  
	  return s;
	}
  /**
   * return current deathmatchflags.
   */
  protected String getDeathMatchFlagsString()
	{
	  return (new CVar("dmflags", "0", CVar.CVAR_SERVERINFO)).getString();
	}
  /**
   * get current map name.
   */
  protected String getMapName()
	{
	  return q2java.baseq2.BaseQ2.gWorld.getSpawnArg("message","");
	}
  /**
   * get time current map has been running.
   */
  protected float getMapTime()
	{
	  return Game.getGameTime() - fMapStartTime;
	}
  /**
   * log the date.
   * must occur once at start of file or else every time a map is restarted.
   */
  public void logDate()
	{ 
	  try { fOut.write("\t\tLogDate\t" + getCurrentDateString() + fEndOfLine); }
	  catch(IOException ioe) {} 
	}
  /**
   * log the deathmatch flags.
   * must occur once at start of file or else every time a map is restarted.
   */
  public void logDeathMatchFlags() 
	{ 
	  try { fOut.write("\t\tLogDeathFlags\t" + getDeathMatchFlagsString() + fEndOfLine); }
	  catch(IOException ioe) {} 
	}
  /**
   * log the end of scoring. 
   * This is for games such as clan matches when scoring 
   * may end at a certain point.
   */
  public void logGameEnd()
	{
	  if( !fLogging ) return;
	  try 
	{
	  fOut.write("\t\tGameEnd\t\t\t" + getMapTime() + fEndOfLine); 
	  fLogging = false;
	}
	  catch(IOException ioe) {} 
	}
  /**
   * log the start of scoring. 
   * This is for games such as clan matches when scoring 
   * may start at a certain point.
   */
  public void logGameStart()
	{
	  if( fLogging ) return;
	  try 
	{
	  fOut.write("\t\tGameStart\t\t\t" + getMapTime() + fEndOfLine); 
	  fLogging = true;
	}
	  catch(IOException ioe) {} 
	}
  /**
   * log a map name.
   * Must occur every time a map is changed.
   */
  public void logMapName() 
	{ 
	  try { fOut.write("\t\tMap\t" + getMapName() + fEndOfLine); }
	  catch(IOException ioe) {} 
	}
  /**
   * log the patch.
   * must occur once at start of file.
   * do not use subversions (ie don't add 1.2 at end of patch name, just add 1)
   */
  public void logPatch() 
	{ 
	  try { fOut.write("\t\tPatchName\t" + fPatchName  + fEndOfLine); }
	  catch(IOException ioe) {} 
	}
  /**
   * log when player connects.
   * Note that the player still has to have name logged
   * after this and each time a map restarts.
   */
  public void logPlayerConnect(String playerName, String team) 
	{ 
	  try 
	{
	  fOut.write("\t\tPlayerConnect\t" + cleanString(playerName) + "\t" +
		     cleanString(team) + "\t" + getMapTime() + fEndOfLine); 
	}
	  catch(IOException ioe) {} 
	}
  /**
   * log when a player leaves game.
   */
  public void logPlayerLeft(String playerName)
	{
	  try 
	{
	  fOut.write("\t\tPlayerLeft\t" + cleanString(playerName) + "\t\t" +
		     getMapTime() + fEndOfLine); 
	}
	  catch(IOException ioe) {} 
	}
  /**
   * log a players name.
   * This must occur after they have connected and become a player
   * and every time the map is changed.
   */
  public void logPlayerName(String playerName, String team) 
	{ 
	  try 
	{
	  fOut.write("\t\tPlayer\t" + cleanString(playerName) + "\t" +
		     cleanString(team) + "\t" + getMapTime() + fEndOfLine); 
	}
	  catch(IOException ioe) {} 
	}
  /**
   * log when player renames self.
   */
  public void logPlayerRename(String newPlayerName, String oldPlayerName) 
	{ 
	  try 
	{
	  fOut.write("\t\tPlayerRename\t" + cleanString(oldPlayerName) + "\t" +
		     cleanString(newPlayerName) + "\t" + getMapTime() + fEndOfLine); 
	}
	  catch(IOException ioe) {} 
	}
  /**
   * log when player changes team.
   */
  public void logPlayerTeamChange(String playerName, String team) 
	{ 
	  try 
	{
	  fOut.write("\t\tPlayerTeamChange\t" + cleanString(playerName) + "\t" +
		     cleanString(team) + "\t" + getMapTime() + fEndOfLine); 
	}
	  catch(IOException ioe) {} 
	}
  /**
   * log a score in gibstat format.
   * Note that no null must be passed into this but can pass in 
   * empty strings. Any "\t" 's will be removed from strings passed in.
   */
  public void logScore(String killer, 
		       String target,
		       String scoreType, 
		       String weapon, 
		       int score, 
		       int ping )
	{
	  try 
	{
	  fOut.write( cleanString(killer) + "\t" + cleanString(target) + "\t" +
		      cleanString(scoreType) + "\t" + cleanString(weapon) + "\t" +
		      score + "\t" + getMapTime() + "\t" + ping + fEndOfLine);
	}
	  catch(IOException ioe) {} 	
	}
  /**
   * log the time.
   * must occur once at start of file or else every time a map is restarted.
   */
  public void logTime()
	{ 
	  try { fOut.write("\t\tLogTime\t" + getCurrentTimeString() + fEndOfLine); }
	  catch(IOException ioe) {} 
	}
  /**
   * log the version of log.
   * must occur once at start of file.
   */
  public void logVersion()
	{ 
	  try { fOut.write("\t\tStdLog\t1.2" + fEndOfLine); }
	  catch(IOException ioe) {}
	}
  /**
   * Set the type of end of line protocol used.
   * This just to make sure all gibstatics programs etc will
   * handle the log.
   */
  protected void setDosEndOfLine(boolean dosEndOfLine)
	{
	  if( dosEndOfLine == true ) { fEndOfLine = "\r\n"; }
	  else { fEndOfLine = "\n";	}
	}
}