package barryp.widgetwar;

import java.util.Vector;

import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.baseq2.*;

/**
 * Home base for a team.
 * 
 */
public abstract class TeamBase extends GameObject 
	{
	protected NativeEntity fTriggerEntity;
	
/**
 * misc_teleporter constructor comment.
 * @param spawnArgs java.lang.String[]
 * @exception q2java.GameException The exception description.
 */
public TeamBase(Element spawnArgs) throws q2java.GameException 
	{
	super(spawnArgs);

	// hook up with the Team object
	getTeam().setTeamBase(this);	
	
	// setup the first entity to be the pad
	fEntity.setModel("models/objects/dmspot/tris.md2");
	fEntity.setSkinNum(1);
//	fEntity.setEffects(NativeEntity.EF_TELEPORTER);
	fEntity.setSound(Engine.getSoundIndex("world/comp_hum3.wav"));
	fEntity.setSolid(NativeEntity.SOLID_BBOX);

	fEntity.setMins(-32, -32, -24);
	fEntity.setMaxs(32, 32, -16);

/*
	// Make sure that flag doesn't start in a solid object
	Point3f dest = fEntity.getOrigin();
	dest.z -= 128;

	TraceResults tr = Engine.trace( fEntity.getOrigin(), fEntity.getMins(), fEntity.getMaxs(), dest, fEntity, Engine.MASK_SOLID );
	if ( tr.fStartSolid )
		{
		Engine.dprint( "Team base startsolid at " + fEntity.getOrigin() + "\n" );
		dispose();
		return;
		}
System.out.println("TeamBase() initial origin " + fEntity.getOrigin());			
	fEntity.setOrigin(tr.fEndPos);
System.out.println("TeamBase() final origin " + fEntity.getOrigin());	
	
*/	
	fEntity.linkEntity();

	// setup a second entity to be the trigger
	fTriggerEntity = new NativeEntity();
	fTriggerEntity.setReference(this);
	fTriggerEntity.setSolid(NativeEntity.SOLID_TRIGGER);
	fTriggerEntity.setOrigin(fEntity.getOrigin());
	fTriggerEntity.setMins( -8, -8, 8);
	fTriggerEntity.setMaxs(8, 8, 24);
	fTriggerEntity.linkEntity();
	}
/**
 * Get the NativeEntity used by the TeamBase.
 * @return q2java.NativeEntity
 */
public NativeEntity getBaseEntity() 
	{
	return fEntity;
	}
/**
 * This method was created in VisualAge.
 * @return barryp.widgetwar.Team
 */
public abstract Team getTeam();
/**
 * Called when the player touches the field above the base.
 * @param touchedBy Player
 */
public void touch(Player touchedBy) 
	{
	if (touchedBy.getTeam() == getTeam())
		((WidgetWarrior) touchedBy).touchBase();
	}
}