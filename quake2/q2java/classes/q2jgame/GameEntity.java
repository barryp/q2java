
package q2jgame;


import java.io.*;
import java.util.Enumeration;
import q2java.*;

public class GameEntity extends NativeEntity
	{
	private String[] fSpawnArgs;
	protected int fSpawnFlags;
	protected float fThinkTime;
	
	protected String fTargetName;
	protected String fTarget;
	
public GameEntity() throws GameException
	{
	}
public GameEntity(String[] spawnArgs) throws GameException
	{
	this(spawnArgs, false);
	}
public GameEntity(String[] spawnArgs, boolean isWorld) throws GameException
	{
	super(isWorld);
	fSpawnArgs = spawnArgs;

	// look for common spawn arguments

	String s = getSpawnArg("origin");
	if (s != null)
		setOrigin(new Vec3(s));

	s = getSpawnArg("angles");
	if (s != null)
		setAngles(new Vec3(s));

	s = getSpawnArg("angle");
	if (s != null)
		{
		Float f = new Float(s);
		setAngles(0, f.floatValue(), 0);
		}
		
	s = getSpawnArg("spawnflags");
	if (s != null)
		fSpawnFlags = Integer.parseInt(s);
		
	s = getSpawnArg("target");
	if (s != null)
		fTarget = s.intern();		

	s = getSpawnArg("targetname");
	if (s != null)
		fTargetName = s.intern();		
	}
/**
 * This method was created by a SmartGuide.
 * @return java.util.Enumeration
 */
public Enumeration enumerateTargets() 
	{
	return new TargetEnumeration(fTarget);
	}
public String getSpawnArg(String keyword)
	{
	if (fSpawnArgs == null)
		return null;

	keyword = keyword.intern();
	for (int i = 0; i < fSpawnArgs.length; i+=2)
		if (keyword == fSpawnArgs[i])
			return fSpawnArgs[i+1];

	return null;
	}
/**
 * This method was created by a SmartGuide.
 */
public void runFrame() 
	{
	}
public String toString()
	{
	if (fSpawnArgs == null)
		return super.toString();
		
	// we have spawn args, so we'll return something a little fancier		
	StringBuffer sb = new StringBuffer(super.toString());

	sb.append("(");
	for (int i = 0; i < fSpawnArgs.length; i+=2)
		{
		sb.append(fSpawnArgs[i]);
		sb.append("=\"");
		sb.append(fSpawnArgs[i+1]);
		sb.append("\"");
		if (i < (fSpawnArgs.length - 2))
			sb.append(", ");
		}
	sb.append(")");

	return sb.toString();
	}
/**
 * This method was created by a SmartGuide.
 * @param touchedBy q2jgame.GameEntity
 */
public void touch(GenericCharacter touchedBy) 
	{
	return;
	}
}