
package baseq2;

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;
import javax.vecmath.*;
import q2java.*;
import q2jgame.*;

/**
 * Simple class to represent a spawnpoint in a map.
 *
 * @author Barry Pederson 
 */

public abstract class GenericSpawnpoint
	{
	protected Point3f fOrigin;
	protected Angle3f fAngles;	
	
public GenericSpawnpoint(String[] spawnArgs) throws GameException
	{
	String s = Game.getSpawnArg(spawnArgs, "origin", null);
	if (s != null)
		fOrigin = MiscUtil.parsePoint3f(s);

	s = Game.getSpawnArg(spawnArgs, "angles", null);
	if (s != null)
		fAngles = MiscUtil.parseAngle3f(s);

	s = Game.getSpawnArg(spawnArgs, "angle", null);
	if (s != null)
		{
		Float f = new Float(s);
		fAngles = new Angle3f(0, f.floatValue(), 0);
		}
		
	if (fAngles == null)
		fAngles = new Angle3f();
	}
/**
 * Fetch the spawnpoint's orientation.
 * @return javax.vecmath.Angle3f
 */
public Angle3f getAngles() 
	{
	return fAngles;
	}
/**
 * Fetch the spawnpoint's origin
 * @return javax.vecmath.Point3f
 */
public Point3f getOrigin() 
	{
	return fOrigin;
	}
/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String toString() 
	{
	StringBuffer sb = new StringBuffer();

	sb.append(getClass().getName());
	sb.append(" Origin: ");
	sb.append(fOrigin);
	sb.append(" Angles: ");
	sb.append(fAngles);
	
	return sb.toString();
	}
}