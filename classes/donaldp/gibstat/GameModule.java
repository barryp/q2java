package donaldp.gibstat;

import java.beans.PropertyVetoException;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.w3c.dom.Document;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;
import q2java.baseq2.*;
import q2java.baseq2.event.*;
import q2java.baseq2.rule.*;
import donaldp.util.*;

/**
 * Module to add gibstat recording as per stdlog format 1.2.
 *
 * @author Peter Donald
 */
public class GameModule extends Gamelet 
  implements CrossLevel, OccupancyListener, PlayerInfoListener, DeathScoreListener 
{
  protected static FileGibStatisticsLog gGibStatisticsLog = null;

  public GameModule(Document gameletInfo)
	{
	  super(gameletInfo);

	if( gGibStatisticsLog == null )
		{
		CVar cvar = new CVar("gibstatslog", "gibstats", CVar.CVAR_LATCH);
		gGibStatisticsLog = new FileGibStatisticsLog( cvar.getString(), true );
		}

	Game.addOccupancyListener(this);

	Enumeration enum = Player.enumeratePlayers();
	  
	while( enum.hasMoreElements() )
		{
		Player p = (Player) enum.nextElement();
		p.addPlayerInfoListener(this);
		}      

	ScoreManager s = RuleManager.getScoreManager();
	s.addScoreListener(this, DeathScoreEvent.class);	  
	}
  public void deathOccured(DeathScoreEvent e)
	{
	  String killer = "";
	  String victim = "";
	  String weapon = "";
	  int score = 0;
	  int ping = 0;
	  String scoreType = "";

	  if( e.getAgent() instanceof GenericItem )
	{
	  weapon = ((GenericItem)e.getAgent()).getIdName();
	}

	  if( e.getActive() instanceof Player )
	{
	  killer = ((Player)e.getActive()).getName();
	  ping = ((Player)e.getActive()).fEntity.getPlayerPing();
	}

	  if( e.getPassive() instanceof Player )
	{
	  victim = ((Player)e.getPassive()).getName();
	}
	  else
	{
	  victim =  "unknown";
	}

	  gGibStatisticsLog.logScore( killer,
				  victim,
				  UtilPack.obitToScoreType( e.getAgentKey() ), 
				  UtilPack.obitToWeapon( e.getAgentKey() ), 
				  e.getScoreChange(), 
				  ping );
	}
  public void gameStatusChanged(GameStatusEvent e)
	{
	  if( e.getState() == GameStatusEvent.GAME_POSTSPAWN )
	{
	  gGibStatisticsLog.logMapChange();
	}
	}
  public static GenericGibStatisticsLog getDefaultGibStatisticsLog()
	{
	  return gGibStatisticsLog;
	}
  public void infoChanged(PlayerInfoEvent e) throws PropertyVetoException
	{
	  if( e.getKey().equalsIgnoreCase("name") && e.getOldValue() != null )
	{
	  gGibStatisticsLog.logPlayerRename( e.getNewValue(), e.getOldValue() );
	}
	}
public void playerChanged(OccupancyEvent e) throws PropertyVetoException
	{
	Player p = (Player)(e.getPlayerEntity().getReference());
	if( e.getState() == OccupancyEvent.PLAYER_CONNECTED )
		{
		gGibStatisticsLog.logPlayerConnect(p.getName(), ""/*team*/ );
		p.addPlayerInfoListener(this);
		}
	else if( e.getState() == OccupancyEvent.PLAYER_DISCONNECTED )
		{
		p.removePlayerInfoListener(this);
		gGibStatisticsLog.logPlayerLeft( p.getName() );
		}
	}
  public void svcmd_endlog(String[] args) 
	{
	  if( args.length != 2 )
	{
	  Game.dprint("Usage: sv endlog\n");
	}

	  gGibStatisticsLog.logGameEnd();
	}
  /**
   * Help svcmd for a GameModule.
   * @param args java.lang.String[]
   */
  public void svcmd_help(String[] args) 
	{
	  Game.dprint("sv commands:\n");
	  Game.dprint("  startlog\n");
	  Game.dprint("  endlog\n");
	}
  public void svcmd_startlog(String[] args) 
	{
	  if( args.length != 2 )
	{
	  Game.dprint("Usage: sv startlog\n");
	}
	  
	  gGibStatisticsLog.logGameStart();
	}
 public void unload()
	{
	super.unload();

	gGibStatisticsLog.flushLog();
	gGibStatisticsLog.closeLog();
	gGibStatisticsLog = null;

	Game.removeOccupancyListener(this);

	Enumeration enum = NativeEntity.enumeratePlayerEntities();
	  
	while( enum.hasMoreElements() )
		{
		Object obj = ((NativeEntity)enum.nextElement()).getReference();
		if (obj instanceof Player)
			((Player)obj).removePlayerInfoListener(this);
		}      

	ScoreManager s = RuleManager.getScoreManager();
	s.removeScoreListener(this, DeathScoreEvent.class);
	}
}