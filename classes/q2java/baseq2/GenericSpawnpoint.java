package q2java.baseq2;

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;
import javax.vecmath.*;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;

/**
 * Simple class to represent a spawnpoint in a map.
 *
 * @author Barry Pederson 
 */

public abstract class GenericSpawnpoint extends GameObject implements FixedObject
	{
	protected Point3f fOrigin;
	protected Angle3f fAngles;	
	protected String fTargetName;
	
public GenericSpawnpoint(Element spawnArgs) throws GameException
	{
	super();
	
	fOrigin = GameUtil.getPoint3f(spawnArgs, "origin");
	fAngles = GameUtil.getAngle3f(spawnArgs, "angles");
	
	if (fAngles == null)
		fAngles = new Angle3f();
		

	fTargetName = GameUtil.getSpawnArg(spawnArgs, "targetname", "id", null);
	if (fTargetName != null)
		fTargetGroup = Game.addLevelRegistry("target-" + fTargetName, this);
	}
/**
 * Fetch a copy of the spawnpoint's orientation.
 * @return javax.vecmath.Angle3f
 */
public Angle3f getAngles() 
	{
	return new Angle3f(fAngles);
	}
/**
 * Fetch a copy of the spawnpoint's origin
 * @return javax.vecmath.Point3f
 */
public Point3f getOrigin() 
	{
	return new Point3f(fOrigin);
	}
/**
 * This method was created by a SmartGuide.
 * @return java.lang.String
 */
public String toString() 
	{
	StringBuffer sb = new StringBuffer();

	sb.append(getClass().getName());
	sb.append(" TargetName: ");
	sb.append(fTargetName);
	sb.append(" Origin: ");
	sb.append(fOrigin);
	sb.append(" Angles: ");
	sb.append(fAngles);
	
	return sb.toString();
	}
}