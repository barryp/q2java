package q2java.core.event;

import java.lang.reflect.*;

/**
 * Base class of all event in q2java game.
 *
 * @author Peter Donald
 */
abstract public class GenericEvent extends java.util.EventObject
{
  //NB these statics are used so in the future
  // it may be possible to record all events and use
  // this as a partiall debuggin method...
  public final static int NULL_EVENT = 0;

  public final static int FIRST_PLAYER_EVENT = 1;
  public final static int PLAYER_DAMAGE_EVENT = 1;
  public final static int PLAYER_INFO_EVENT = 2;
  public final static int PLAYER_INVENTORY_EVENT = 3;
  public final static int PLAYER_STATE_EVENT = 4;
  public final static int PLAYER_COMMAND_EVENT = 5;
  public final static int PLAYER_CVAR_EVENT = 6;
  public final static int PLAYER_MOVE_EVENT = 6;
  public final static int LAST_PLAYER_EVENT = 1023;

  public final static int FIRST_RULE_EVENT = 1024;
  public final static int RULE_DEATHSCORE_EVENT = 1024;
  public final static int RULE_GOALSCORE_EVENT = 1025;
  public final static int LAST_RULE_EVENT = 2047;

  public final static int FIRST_GAME_EVENT = 2048;
  public final static int GAME_OCCUPANCY_EVENT = 2048;
  public final static int GAME_STATUS_EVENT = 2049;
  public final static int GAME_GAMELET_EVENT = 2050;
  public final static int GAME_SERVERCOMMAND_EVENT = 2051;
  public final static int LAST_GAME_EVENT = 4095;

  protected int fType = NULL_EVENT;

  protected final static Object fSourceHack = new Object();

  public GenericEvent(Object source, int type)
	{
	  // ugly hack to get around not allowed to have null sources
	  super(fSourceHack); 
	  fType = type;
	  this.source = source; 
	}
  public final int getType() { return fType; }      
  public final void setSource(Object o) { source = o; }      
  public String toString()
	{
	  StringBuffer sb = new StringBuffer();
	  Class c = getClass();

	  sb.append( c.toString() );
	  sb.append(" [ ");

	  while(c != GenericEvent.class)
	{
	  Field fields[] = c.getDeclaredFields();

	  c = c.getSuperclass();

	  for( int i = 0; i < fields.length; i++ )
	    {
	      if( i != 0 && i != fields.length - 1 )
		{
		  sb.append(", ");					
		}

	      if( Modifier.isStatic( fields[i].getModifiers() ) )
		  {
		    continue;
		  }

	      sb.append( fields[i].getName() );	
	      sb.append( "=" );
		  
	      try { sb.append( fields[i].get(this) ); }
	      catch(IllegalArgumentException iae) {}
	      catch(IllegalAccessException iae) {}
	    }
	}

	  sb.append(" ] ");	

	  return sb.toString();
	}
}