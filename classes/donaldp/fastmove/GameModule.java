package donaldp.fastmove;

import java.beans.PropertyVetoException;
import java.util.Enumeration;
import javax.vecmath.*;

import org.w3c.dom.Document;

import q2java.*;
import q2java.Engine;
import q2java.baseq2.Player;
import q2java.baseq2.event.*;

public class GameModule extends q2java.core.Gamelet implements PlayerMoveListener
	{	
	
public GameModule(Document gameletInfo)
	  {
	  super( gameletInfo );
	  // we no longer want to be notified of level changes
	  Enumeration enum = NativeEntity.enumeratePlayerEntities();
	  while( enum.hasMoreElements() )
	  {
	  Player p = (Player)(((NativeEntity)enum.nextElement()).getReference());
	  
	  p.addPlayerMoveListener(this);
	  }
	  }  
public void playerMoved(PlayerMoveEvent e)
	  {
	PlayerCmd pc = e.getMove();
	float forward = PlayerCmd.short2float( pc.fForwardMove ) * 10;
	
	pc.fForwardMove = PlayerCmd.float2Short( forward );
	/*
	NativeEntity ent = e.getPlayer().fEntity;

	Vector3f v = ent.getVelocity();
	Angle3f ang = ent.getPlayerViewAngles();
	Vector3f forward = new Vector3f();  
	ang.getVectors(forward, null, null);	

	v.add( forward );
	v.normalize();
	v.scale( 40 );

	ent.setVelocity(v.x,v.y,v.z);
	*/
	  }  
public void unload() 
	  {
	  // we no longer want to be notified of level changes
	  Enumeration enum = NativeEntity.enumeratePlayerEntities();

	  while( enum.hasMoreElements() )
	  {
	  Player p = (Player)(((NativeEntity)enum.nextElement()).getReference());
	  p.removePlayerMoveListener(this);
	  }
	  }  
}