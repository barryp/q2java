package barryp.widgetwar.payload;

import java.util.*;
import javax.vecmath.*;

import q2java.*;
import q2java.core.Game;
import q2java.core.event.*;
import q2java.baseq2.GameObject;
import q2java.baseq2.event.*;

import barryp.widgetwar.*;

/**
 * Protect the widget from damage.
 *
 * @author Barry Pederson
 */
public class ForceField extends GenericWidgetComponent 
implements DamageListener, ServerFrameListener
	{
	private final static float PROTECTION_FACTOR = 0.8F; // Can cut damage by up to 75%
	private final static float ENERGY_FACTOR = 2.0F; // 2 units of damage chew up one energy unit

	private final static float ENERGY_DRAIN = 0.1F; // per tick
	
	private final static float MINIMUM_DRAIN_ENERGY = 5; // minimum amount of energy a player must have for it to even drain energy
	private final static float MINIMUM_ACTIVATE_ENERGY = 10; // minimum amount of energy a body must have for the forcefield to work
	
public void damageOccured(DamageEvent damage)
	{
	float energy = getWidgetBody().getEnergy();
	if (energy < MINIMUM_ACTIVATE_ENERGY)
		return;
		
	int dflags = damage.getDamageFlags();

	// decrease damage based on armor
	if ((dflags & DamageEvent.DAMAGE_NO_ARMOR) != 0) 
		return;
		
 	// the amount of damage our armor protects us from
 	int save = (int) (damage.getAmount() * PROTECTION_FACTOR);
	if ((dflags & DamageEvent.DAMAGE_ENERGY) != 0)
		damage.setPowerArmorSave(damage.getPowerArmorSave() + save);
	else
		damage.setArmorSave(damage.getArmorSave() + save);

	save = (int) Math.min(save, energy * ENERGY_FACTOR);
	getWidgetBody().setEnergy(energy - (save / ENERGY_FACTOR));
		
	// FIXME: Do we need to do any adjustments here for armorSave or
	// powerArmorSave based on fArmorCount? (TSW)
	damage.setTakeDamage( damage.getTakeDamage() + damage.getAmount() );				// for blends (TSW)

	if (save > 0)
		{
		damage.setAmount( damage.getAmount() - save );
		
		// Doesn't this mean that takeDamage ends up the same as amount? Can we just get rid of takeDamage? - BH
		damage.setTakeDamage( damage.getTakeDamage() - damage.getArmorSave() ); // for blends (TSW)
		damage.setTakeDamage( damage.getTakeDamage() - damage.getPowerArmorSave() ); // for blends (TSW)
					
		((GameObject) getWidgetBody().getWidgetEntity().getReference()).spawnDamage( Engine.TE_SPARKS, 
				    damage.getDamagePoint(), 
				    damage.getDamageNormal(),
				    save);
		}
	}
/**
 * handleWidgetEvent method comment.
 */
public void handleWidgetEvent(int event, Object extra) 
	{
	WidgetBody wb = getWidgetBody();
	NativeEntity ent = wb.getWidgetEntity();
	
	switch (event)
		{
		case WidgetBody.DEPLOY:		
			wb.addDamageListener(this);
			Game.addServerFrameListener(this, Game.FRAME_BEGINNING, 0, 0);
			Game.addServerFrameListener(this, Game.FRAME_MIDDLE, 0, 0);
			break;

		case WidgetBody.DESTROYED:
		case WidgetBody.TERMINATED:
			// clear the shell around the model
			ent.setRenderFX(ent.getRenderFX() & ~NativeEntity.RF_SHELL_GREEN);
			ent.setEffects(ent.getEffects() & ~NativeEntity.EF_COLOR_SHELL);
			
			Game.removeServerFrameListener(this, Game.FRAME_MIDDLE);
			Game.removeServerFrameListener(this, Game.FRAME_BEGINNING);
			wb.removeDamageListener(this);
			break;
		}
	}
/**
 * Drain the widget body's energy a bit.
 * @param phase int
 */
public void runFrame(int phase) 
	{
	WidgetBody wb = getWidgetBody();
	NativeEntity ent = wb.getWidgetEntity();
	
	switch (phase)
		{
		case Game.FRAME_BEGINNING:
			// clear the shell around the model
			ent.setRenderFX(ent.getRenderFX() & ~NativeEntity.RF_SHELL_GREEN);
			ent.setEffects(ent.getEffects() & ~NativeEntity.EF_COLOR_SHELL);		
			break;

		case Game.FRAME_MIDDLE:
			float energy = wb.getEnergy();
			if (energy >= MINIMUM_DRAIN_ENERGY)
				wb.setEnergy(wb.getEnergy() - ENERGY_DRAIN);
			
			if (energy >= MINIMUM_ACTIVATE_ENERGY)
				{				
				// activate the shell
				ent.setEffects(ent.getEffects() | NativeEntity.EF_COLOR_SHELL);
				ent.setRenderFX(ent.getRenderFX() | NativeEntity.RF_SHELL_GREEN);

				// alter the player's view if necessary
				if (wb instanceof barryp.widgetwar.body.BodyHarness)
					{
					wb.getWidgetOwner().addBlend(0F, 1F, 0F, 0.35F);
					}
				}
		}
	}
}