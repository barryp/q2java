package donaldp.util;

import java.io.*;
import java.util.*;
import q2java.*;
import q2java.core.*;

public class FileGibStatisticsLog extends GenericGibStatisticsLog
{
	/**
	 * Default constructor that puts log in sandbox under name of gibstats.log.
	 */
	public FileGibStatisticsLog()
	{
	  File sandbox = new File(Engine.getGamePath(), "sandbox");
	  File logFile = new File(sandbox, "gibstats.log");
	  init( logFile );
	}
	/**
	 * Constructor that takes a filename to log to.
	 * NB: if inSandbox parameter is true then will try to open file in sandbox.
	 * otherwise the file will be opened in current directory (or elsewhere
	 * if it is a fully qualified filename)
	 */
	public FileGibStatisticsLog(String filename, boolean inSandbox)
	{
	  if( inSandbox )
	{
	  File sandbox = new File(Engine.getGamePath(), "sandbox");
	  File logFile = new File(sandbox, filename + "_0.log");;

	  for(int i = 1; logFile.exists(); i ++ )
	    {
	      logFile = new File(sandbox, filename + "_" + i + ".log");
	    }

	  init( logFile );
	}
	  else
	{
	  File logFile = null;

	  for(int i = 0; logFile.exists(); i ++ )
	    {
	      logFile = new File(filename + "_" + i + ".log");
	    }

	  init( logFile );
	}
	}
	/**
	 * Call this when you wish to shut down the log.
	 */
	public void closeLog()
	{
	  try
	{
	  fOut.flush();
	  fOut.close();
	}
	  catch(IOException ioe) {}
	}
	public void flushLog()
	{
	try { fOut.flush(); }
	catch(IOException ioe) {}
	}
	/**
	 * Method to help with initialisation.
	 */
	protected void init(File file)
	{
	  FileOutputStream out = null;

	  try { out = new FileOutputStream( file ); }
	  catch(IOException ioe)
	{
	  throw new Error( ioe.toString() );
	}

	  BufferedWriter writer = 
	new BufferedWriter( new OutputStreamWriter(out) );//, "iso .. ? encoding" );

	  fMapStartTime = Game.getGameTime();
	  fOut = writer;

	  logVersion();
	  logPatch();
	  logDeathMatchFlags();
	  logHeader();
	}
	public void logHeader()
	{
	logDate();
	logTime();
	}
	/**
	 * Logs a map change and all associated data. 
	 * Must be called after all entities spawned.
	 */
	public void logMapChange()
	{
	fMapStartTime = Game.getGameTime();
		logMapName();

	Enumeration enum = NativeEntity.enumeratePlayerEntities();

	String name = "";
	String team = "";
	  
	while (enum.hasMoreElements())
	  {		
	    q2java.baseq2.Player p  = (q2java.baseq2.Player)
	      ((NativeEntity) enum.nextElement()).getReference();

	    name = p.getName();
	    /*
	    if( p instanceof TeamMember )
	      {
		team = ((TeamMember)p).getTeam().getName();
	      }
	    else
	      {
	    */
		team = "";
		/*
	      }
		*/
	    logPlayerName(name,team);
	  }
	}
}