package donaldp.ipban;

import java.beans.PropertyVetoException;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.w3c.dom.Document;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;
import q2java.baseq2.*;
import q2java.baseq2.rule.*;
import donaldp.util.*;

/**
 * Module to add ipbanning based on ip/name.
 * ips can also reserve names.
 *
 * @author Peter Donald
 */
public class GameModule extends Gamelet
  implements CrossLevel, OccupancyListener 
{
  protected final static CVar fIpBanStrict = new CVar("sv_ipban_strict", "0",
						      CVar.CVAR_ARCHIVE);

  protected Vector fBannedIPAddresses = new Vector();
  protected Vector fBannedNames = new Vector();
  
  public GameModule(Document gameletInfo) throws Exception
	{
	  super(gameletInfo);
	  CVar cvar = new CVar("ipbanfile", "ipban.txt", CVar.CVAR_LATCH);
	  Game.addOccupancyListener(this);
	}
  protected boolean ipMatches(String ip, String ip2)
	{
	  String s1 = null;
	  String s2 = null;
	  int startIndex_s1 = 0;
	  int startIndex_s2 = 0;
	  int endIndex_s1 = 0;
	  int endIndex_s2 = 0;

	  for(int i = 0; i < 4; i++)
	{
	  if( i != 3 )
	    {
	      endIndex_s1 = ip.indexOf('.',startIndex_s1);
	      endIndex_s2 = ip2.indexOf('.',startIndex_s2);

	      // catches case where endIndex == -1 and when
	      // .. apears in string
	      if( endIndex_s1 - 1 < startIndex_s1 ||
		  endIndex_s2 - 1 < startIndex_s2 )
		{
		  return false;
		}
	      
	      s1 = ip.substring(startIndex_s1,endIndex_s1).trim();
	      s2 = ip2.substring(startIndex_s2,endIndex_s2).trim();
	    }
	  else
	    {
	      s1 = ip.substring(startIndex_s1).trim();
	      s2 = ip2.substring(startIndex_s2).trim();
	    }
  
	  if( !s1.equals("*") && !s1.equals(s2) )
	    {
	      return false;
	    }

	  startIndex_s1 = endIndex_s1 + 1;
	  startIndex_s2 = endIndex_s2 + 1;
	}

	  return true;
	}
  protected boolean isCorrectIPAddressFormat(String ip)
	{
	  String s = null;
	  int startIndex = 0, endIndex = 0;

	  for(int i = 0; i < 4; i++)
	{
	  if( i != 3 )
	    {
	      endIndex = ip.indexOf('.',startIndex);

	      // catches case where endIndex == -1 and when
	      // .. apears in string
	      if( endIndex - 1 < startIndex )
		{
		  return false;
		}
	      
	      s = ip.substring(startIndex,endIndex).trim();
	    }
	  else
	    {
	      s = ip.substring(startIndex).trim();
	      //endIndex = ip.length() + 1;
	    }

	  if( !s.equals("*") )
	    {
	      try 
		{ 
		  if( Integer.parseInt(s) > 256 )
		    {
		      return false;
		    }
		}
	      catch(NumberFormatException nfe)
		{
		  return false;
		}
	    }
	  startIndex = endIndex + 1;
	}
	  
	  return true;
	}
public void playerChanged(OccupancyEvent e) throws PropertyVetoException
	{ 
	if( e.getState() == OccupancyEvent.PLAYER_CONNECTED )
		{
		Player p = (Player) e.getPlayerEntity().getReference();
		String name = p.getName().toLowerCase().trim();
		verifyName(name);

		String ip = p.getPlayerInfo("ip").trim();
		verifyIp(ip);
		}
	}
  public void svcmd_addban(String[] args) 
	{
	  String s = null;

	  if( args.length != 3 && args.length != 4 )
	{
	  Game.dprint("Usage: sv addban [i|n] <banned>\n");
	  return;
	}

	  if(args.length == 4)
	{
	  if( args[2].equalsIgnoreCase("i") )
	    {
	      if( !isCorrectIPAddressFormat( args[3] ) )
		{
		  Game.dprint("ip address must be in numerical format like 127.0.0.1");
		  return;
		}

	      fBannedIPAddresses.addElement( args[3] );
	    }
	  else if( args[2].equalsIgnoreCase("n") )
	    {
	      fBannedNames.addElement( args[3].toLowerCase() );
	    }
	  else
	    {
	      Game.dprint("Usage: sv addban [i|n] <banned>");
	      return;
	    }
	}
	  else
	{
	  fBannedNames.addElement( args[2].toLowerCase() );
	}
	}
  /**
   * Help svcmd for a GameModule.
   * @param args java.lang.String[]
   */
  public void svcmd_help(String[] args) 
	{
	  Game.dprint("sv commands:\n");
	  Game.dprint("  addban [i|n] <banned>\n");
	  Game.dprint("  removeban [i|n] <banned>\n");
	  Game.dprint("  listbanned\n");
	  //      Game.dprint("  addreservedname <reserved name> <ipaddress>\n");
	  //      Game.dprint("  removereservedname [i|n] <reserved>\n");
	  Game.dprint("Note where [i|n] is specifies, i indicates\n");
	  Game.dprint(" that what follows is an ip address while n ");
	  Game.dprint(" indicates name. By default it is a name.\n");
	}
  public void svcmd_removeban(String[] args) 
	{
	  String s = null;

	  if( args.length != 2 && args.length != 3 )
	{
	  Game.dprint("Usage: sv removeban [i|n] <banned>");
	  return;
	}

	  if( args.length == 3)
	{
	  if( args[1].equalsIgnoreCase("i") )
	    {
	      fBannedIPAddresses.removeElement( args[2] );
	    }
	  else if( args[1].equalsIgnoreCase("n") )
	    {
	      fBannedNames.removeElement( args[2].toLowerCase() );
	    }
	  else
	    {
	      Game.dprint("Usage: sv removeban [i|n] <banned>");
	      return;
	    }
	}
	  else
	{
	  fBannedNames.removeElement( args[1].toLowerCase() );
	}
	}
  public void unload()
	{
	  super.unload();
	  Game.removeOccupancyListener(this);
	}
  protected void verifyIp(String ip) throws PropertyVetoException
	{
	  Enumeration enum = fBannedIPAddresses.elements();
	  String value = null;

	  while( enum.hasMoreElements() )
	{
	  value = (String)enum.nextElement();
	  if( ipMatches(value,ip) )
	    {
	      throw new PropertyVetoException("IP banned",null);
	    }
	}	
	}
  protected void verifyName(String name) throws PropertyVetoException
	{
	  boolean isStrict = false;
	  if( fIpBanStrict.getFloat() == 1.0f )
	{
	  isStrict = true;
	}
	  
	  Enumeration enum = fBannedNames.elements();
	  String value = null;

	  name = name.toLowerCase();
	  
	  if( isStrict )
	{
	  while( enum.hasMoreElements() )
	    {
	      value = (String)enum.nextElement();
	      if( name.indexOf( value ) != -1)
		    {
		      throw new PropertyVetoException("Name banned",null);
		    }
	    }	      
	}
	  else
	{
	  while( enum.hasMoreElements() )
	    {
	      value = (String)enum.nextElement();
	      if( name.equals( value ) )
		{
		  throw new PropertyVetoException("Name banned",null);
		}
	    }	
	}
	}
}