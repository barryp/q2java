
package q2jgame;


import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.Vector;
import q2java.*;
public class GameEntity extends NativeEntity
	{
	private String[] fSpawnArgs;
	protected int fSpawnFlags;
	
public GameEntity() throws GameException
	{
	Game.debugLog("Executing Entity() constructor");
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
		{
		Vec3 v = new Vec3(s);
		setOrigin(v);
		}

	s = getSpawnArg("angles");
	if (s != null)
		{
		Vec3 v = new Vec3(s);
		setAngles(v);
		}

	s = getSpawnArg("angle");
	if (s != null)
		{
		Float f = new Float(s);
		setAngle(f.floatValue());
		}
		
	s = getSpawnArg("spawnflags");
	if (s != null)
		fSpawnFlags = Integer.parseInt(s);
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
public void runEntity() 
	{
	}
static void spawnEntities(String entString)
	{
	try
		{
		StringReader sr = new StringReader(entString);
		StreamTokenizer st = new StreamTokenizer(sr);
		st.eolIsSignificant(false);
		st.quoteChar('"');
		int token;
		Vector v = new Vector(16);
		boolean foundClassname = false;
		String className = null;

		Object[] params = new Object[1];
		Class[] paramTypes = new Class[1];
		String[] sa = new String[1];
		paramTypes[0] = sa.getClass();

		FileOutputStream fos = new FileOutputStream(Engine.getGamePath() + "\\sandbox\\entity.log");
		PrintStream ps = new PrintStream(fos);		

		while ((token = st.nextToken()) != StreamTokenizer.TT_EOF)
			{
			switch (token)
				{
				case '"' : 
					if (foundClassname)
						{
						className = st.sval;
						foundClassname = false;
						break;
						}
					if (st.sval.equalsIgnoreCase("classname"))
						{
						foundClassname = true;
						break;
						}
					v.addElement(st.sval.intern()); 
					break;

				case '{' : foundClassname = false; break;

				case '}' : 
					sa = new String[v.size()];
					v.copyInto(sa);
					v.removeAllElements();
					params[0] = sa;
					try
						{
						Class entClass = Class.forName("q2jgame." + className);
						Constructor ctor = entClass.getConstructor(paramTypes);							
						GameEntity ent = (GameEntity) ctor.newInstance(params);
						ps.println(ent);
						}
					catch (Exception e)
						{
						ps.println("---- " + className + " [" + e + "]");
						}

					foundClassname = false;
					className = null;
					break;

				default  : 
					ps.println("Unknown entity-string token: [" + st.sval + "]\n");
					foundClassname = false;				
				}
			}
		ps.close();
		}
	catch (Exception e)
		{
		Engine.dprint(e.getMessage() + "\n");
		Game.debugLog(e.getMessage());
		}
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
}