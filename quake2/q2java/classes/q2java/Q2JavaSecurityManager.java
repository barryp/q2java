
package q2java;

/**
 * Rudimentary security manager.
 * 
 * @author Barry Pederson
 */
 
import java.io.*;  
 
class Q2JavaSecurityManager extends SecurityManager 
	{
	private int fSecurityLevel;
	private String fSandboxPrefix;
	
/**
 * Q2JavaSecurityManager constructor comment.
 */ 
private Q2JavaSecurityManager(int level, String gamePath) 
	{
	super();
	fSecurityLevel = level;
	
	if (level == 1) // setup sandbox-level security
		{
		File gameDir = new File(gamePath);
		File sandboxDir = new File(gameDir, "sandbox");
		fSandboxPrefix = sandboxDir.getPath() + File.separator;
		fSandboxPrefix = fSandboxPrefix.toLowerCase(); // seems necessary for Win95
		}
	}
/**
 * This method was created by a SmartGuide.
 * @param host java.lang.String
 * @param port int
 */
public void checkConnect(String host, int port) 
	{
	if (fSecurityLevel >= 2)
		throw new SecurityException("Network access not allowed");
		
	if ((fSecurityLevel == 1) 
	&& (port > 0) && (port < 1024))
		throw new SecurityException("No access allowed to ports 1..1023");
	}
/**
 * This method was created by a SmartGuide.
 * @param file java.lang.String
 */
public void checkDelete(String filename) 
	{
	Engine.debugLog("checkDelete(\"" + filename + "\")\n");
	checkSandbox(filename);
	}
/**
 * This method was created by a SmartGuide.
 * @param host java.lang.String
 * @param port int
 */
public void checkListen(int port) 
	{
	if (fSecurityLevel >= 2)
		throw new SecurityException("Network access not allowed");
		
	if ((fSecurityLevel == 1) 
	&& (port > 0) && (port < 1024))
		throw new SecurityException("No access allowed to ports 1..1023");
	}
/**
 * This method was created by a SmartGuide.
 * @param clazz java.lang.Class
 * @param which int
 */
public void checkMemberAccess(Class clazz, int which) 
	{
//	Engine.debugLog("checkMemberAccess(" + clazz + ", " + which + ")\n");
	}
/**
 * This method was created by a SmartGuide.
 */
public void checkPropertyAccess(String key) 
	{
	Engine.debugLog("checkPropertyAccess(\"" + key + "\")\n");
	}
/**
 * This method was created by a SmartGuide.
 * @param filename java.lang.String
 */
public void checkRead(String filename) 
	{
	Engine.debugLog("checkRead(\"" + filename + "\")\n");
	checkSandbox(filename);
	}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @param filename java.lang.String
 */
private void checkSandbox(String filename) 
	{
	if (fSandboxPrefix == null)
		throw new SecurityException("No file access allowed at all");
		
	File f = new File(filename);
	try
		{
		filename = f.getCanonicalPath();
		}
	catch (IOException e)
		{
		throw new SecurityException(e.getMessage());
		}
				
	if (!filename.toLowerCase().startsWith(fSandboxPrefix))
		throw new SecurityException(filename + " is not in the protected sandbox: " + fSandboxPrefix);
	}
/**
 * This method was created by a SmartGuide.
 * @param filename java.lang.String
 */
public void checkWrite(String filename) 
	{
	Engine.debugLog("checkWrite(\"" + filename + "\")\n");
	checkSandbox(filename);
	}
}