package barryp.widgetwar.payload;

import java.util.*;
import javax.vecmath.*;

import q2java.*;
import q2java.core.Game;
import q2java.core.event.*;

import barryp.widgetwar.*;

/**
 * Steal energy from enemy players and transfer to the owner of this widget.
 *
 * @author Barry Pederson
 */
public class AutoDoc extends GenericWidgetComponent implements ServerFrameListener
	{
	public final static float HEALTH_INCREMENT = 2F; // per second
	public final static float HEALTH_COST = 1F; // cost in energy for each point of health given
	public final static float EFFECT_RADIUS = 120;
	public final static float EFFECT_RADIUS_SQUARED = EFFECT_RADIUS * EFFECT_RADIUS;

	protected int fSoundIndex;
	
/**
 * handleWidgetEvent method comment.
 */
public void handleWidgetEvent(int event, Object extra) 
	{
	switch (event)
		{
		case WidgetBody.DEPLOY:
			Game.addServerFrameListener(this, 0, 1F);
 			fSoundIndex = Engine.getSoundIndex("ctf/tech4.wav");			
			break;

		case WidgetBody.DESTROYED:
		case WidgetBody.TERMINATED:
			Game.removeServerFrameListener(this);
			break;
		}
	}
/**
 * Steal a little energy from nearby players.
 * @param phase int
 */
public void runFrame(int phase) 
	{
	WidgetBody wb = getWidgetBody();
	float energyAvailable = wb.getEnergy();

	// no energy? don't bother doing anything
	if (energyAvailable < 1)
		return;

	// drain a little energy, as overhead
	energyAvailable -= 1.0F;
	wb.setEnergy(energyAvailable);
	
	NativeEntity ent = wb.getWidgetEntity();
	Point3f p = ent.getOrigin();
	Team t = (Team) wb.getWidgetOwner().getTeam();
	
	Vector3f v = Q2Recycler.getVector3f();
	Vector healList = Q2Recycler.getVector();
	
	// iterate through the players, making a list of teammates
	// close to the widget who are hurting
	Enumeration enum = 	NativeEntity.enumeratePlayerEntities();
	while (enum.hasMoreElements())
		{
		NativeEntity ne = (NativeEntity) enum.nextElement();
		WidgetWarrior ww2 = (WidgetWarrior) ne.getReference();
		if (ww2.getTeam() != t)
			continue;  // player was on other team
			
		if (ww2.getHealth() >= 100)
			continue; // the picture of health
			
		Point3f p2 = ne.getOrigin();
		v.sub(p, p2);
		
		float f = v.lengthSquared();
		
		if (f <= EFFECT_RADIUS_SQUARED)
			healList.addElement(ww2);
		}

	// if there are any patients available
	int size = healList.size();
	if (size > 0)
		{
		// play a heal sound
		Game.getSoundSupport().fireEvent(ent, NativeEntity.CHAN_VOICE, fSoundIndex, 1, NativeEntity.ATTN_NORM, 0);

		// figure out how much energy we're going to suck from the widget body
		float drain = Math.min(energyAvailable, size * HEALTH_INCREMENT * HEALTH_COST);		
		wb.setEnergy(energyAvailable - drain);

		// given an amount of energy, and number of patients, figure
		// out how much health we are going to give each patient.
		float healthIncrement = (drain / size) / HEALTH_COST;

		// iterate through each patient, and give their health a boost
		for (int i = 0; i < size; i++)
			((WidgetWarrior) healList.elementAt(i)).heal(healthIncrement, false);
		}
		
	Q2Recycler.put(healList);
	Q2Recycler.put(v);
	}
}