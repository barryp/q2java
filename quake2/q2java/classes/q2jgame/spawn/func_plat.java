
package q2jgame.spawn;

import java.util.Enumeration;
import q2java.*;
import q2jgame.*;

public class func_plat extends GenericPusher
	{
	private int fLip;
	
	private final static int PLAT_LOW_TRIGGER	= 1;	
	
public func_plat(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	setAngles(0, 0, 0);
	setSolid(SOLID_BSP);
	
	String s = getSpawnArg("model", null);
	if (s != null)
		setModel(s);

	fSpeed = getSpawnArg("speed", 200) * 0.1F;
	fAccel = getSpawnArg("accel", 50) * 0.1F;
	fDecel = getSpawnArg("decel", 50) * 0.1F;	
	fDmg = getSpawnArg("dmg", 2);
	fWait = 3;
	fLip = getSpawnArg("lip", 8);
	int height = getSpawnArg("height", 0);

	fEndOrigin = getOrigin();
	fStartOrigin = new Vec3(fEndOrigin);
	Vec3 mins = getMins();
	Vec3 maxs = getMaxs();

	if (height != 0)
		fStartOrigin.z -= height;
	else
		fStartOrigin.z -=	 (maxs.z - mins.z) - fLip;

	// pos1 is the top position, pos2 is the bottom
						
	// setup for opening and closing
	setOrigin(fStartOrigin);
	spawnInsideTrigger();	
	linkEntity();	
	}
/**
 * This method was created by a SmartGuide.
 */
protected void hitBottom() 
	{
	}
/**
 * This method was created by a SmartGuide.
 */
protected void hitTop() 
	{
	}
/**
 * This method was created by a SmartGuide.
 */
private void spawnInsideTrigger() 
	{
	Vec3 mins = getMins();
	Vec3 maxs = getMaxs();

	Vec3 tmin = new Vec3(mins);
	Vec3 tmax = new Vec3(maxs);
		
	tmin.add(25, 25, 0);
	tmax.add(-25, -25, 8);
	tmin.z = tmax.z - (fEndOrigin.z - fStartOrigin.z + fLip);	
//	tmin[2] = tmax[2] - (ent->pos1[2] - ent->pos2[2] + st.lip);

	if ((fSpawnFlags & PLAT_LOW_TRIGGER) != 0)
		tmax.z = tmin.z + 8;

	if (tmax.x - tmin.x <= 0)
		{
		tmin.x = (mins.x + maxs.x) * 0.5F;
		tmax.x = tmin.x + 1;
		}

	if ((tmax.y - tmin.y) <= 0)			
		{
		tmin.y = (mins.y + maxs.y) * 0.5F;
		tmax.y = tmin.y + 1;
		}
	
	try
		{
		new AreaTrigger(this, tmin, tmax);
		}
	catch (GameException e)
		{
		e.printStackTrace();
		}		
	}
/**
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void use(Player touchedBy) 
	{
	open();
	}
}