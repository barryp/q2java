package q2java;


/**
 * Rudimentary security manager.
 * 
 * @author Barry Pederson
 */
 
import java.io.*; 
import java.net.*; 
 
class Q2JavaSecurityManager extends SecurityManager 
	{
	private int fSecurityLevel;
	private String fSandboxPrefix;
	private Thread fGameThread;
	private ThreadGroup fGameThreadGroup;
	private ThreadGroup fSubthreadGroup;
	
	private String fSecurityPropertyName = System.getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + "java.security";
	
/**
 * Q2JavaSecurityManager constructor comment.
 */ 
private Q2JavaSecurityManager(int level, String gamePath) 
	{
	super();
	fSecurityLevel = level;
	
	// Get things we need for Thread security
	fGameThread = Thread.currentThread();
	fGameThreadGroup = fGameThread.getThreadGroup();
	fSubthreadGroup = new ThreadGroup("Q2Java Subthreads");
	fSubthreadGroup.setMaxPriority(Thread.NORM_PRIORITY); // don't let subthreads rise higher than the game thread
		
	File gameDir = new File(gamePath);
	File sandboxDir = new File(gameDir, "sandbox");
	fSandboxPrefix = sandboxDir.getPath() + File.separator;
	fSandboxPrefix = fSandboxPrefix.toLowerCase(); // seems necessary for Win95
	}
/**
 * Accept connections from anywhere.
 * @param host java.lang.String
 * @param port int
 */
public void checkAccept(String host, int port) 
	{
	if (fSecurityLevel > 1)
		throw new SecurityException("Not allowed to accept IP connections");
	}
/**
 * This method was created by a SmartGuide.
 * @param g java.lang.Thread
 */
public void checkAccess(Thread t) 
	{
	if (fGameThread.equals(t))
		throw new SecurityException("Mucking with game Thread not allowed");
	}
/**
 * This method was created by a SmartGuide.
 * @param g java.lang.Thread
 */
public void checkAccess(ThreadGroup g) 
	{
	if (fGameThreadGroup.equals(g))
		throw new SecurityException("Mucking with game ThreadGroup not allowed");
	}
/**
 * This method was created by a SmartGuide.
 * @param host java.lang.String
 * @param port int
 */
public void checkConnect(String host, int port) 
	{
	if (fSecurityLevel > 1)
		throw new SecurityException("Network access not allowed");
		
	if (port < 1024)
		throw new SecurityException("No access allowed to ports less than 1024");
	}
/**
 * Let the game create its own classloader
 */
public void checkCreateClassLoader() 
	{
	}
/**
 * This method was created by a SmartGuide.
 * @param file java.lang.String
 */
public void checkDelete(String filename) 
	{
	Engine.debugLog("checkDelete(\"" + filename + "\")");
	checkSandbox(filename);
	}
/**
 * Check whether loading libraries is allowed.  
 * For security level 1, we only want to allow the "net" library to be loaded.
 * For security level 2, no libraries allowed at all.
 * @param lib java.lang.String
 */
public void checkLink(String lib) 
	{
	if (fSecurityLevel > 1)
		throw new SecurityException("Loading libraries not allowed");
		
	if ((lib == null) || !lib.equals("net"))
		throw new SecurityException("Loading libraries not allowed");
	}
/**
 * This method was created by a SmartGuide.
 * @param host java.lang.String
 * @param port int
 */
public void checkListen(int port) 
	{
	if (fSecurityLevel > 1)
		throw new SecurityException("Network access not allowed");
		
	if (port < 1024)
		throw new SecurityException("No access allowed to ports less than 1024");
	}
/**
 * This method was created by a SmartGuide.
 * @param clazz java.lang.Class
 * @param which int
 */
public void checkMemberAccess(Class clazz, int which) 
	{
//	Engine.debugLog("checkMemberAccess(" + clazz + ", " + which + ")");
	}
/**
 * This method was created by a SmartGuide.
 */
public void checkPropertyAccess(String key) 
	{
	Engine.debugLog("checkPropertyAccess(\"" + key + "\")");
	}
/**
 * Allow reads on any FileDescriptor 
 * @param fd java.io.FileDescriptor
 */
public void checkRead(FileDescriptor fd) 
	{
	if (fSecurityLevel > 1)
		throw new SecurityException("Not allowed to read from FileDescriptor");
	}
/**
 * This method was created by a SmartGuide.
 * @param filename java.lang.String
 */
public void checkRead(String filename)
	{
	Engine.debugLog("checkRead(\"" + filename + "\")");

	File f = new File(filename);
	try
		{
		filename = f.getCanonicalPath();
		}
	catch (IOException e)
		{
		throw new SecurityException(e.getMessage());
		}

	// allow files underneath the game dir to be read
	if (filename.toLowerCase().startsWith(Engine.getGamePath().toLowerCase()))
		return;

	// this is horseshit...in order to be able to
	// serialize objects, you need a hash digest function,
	// the system uses SHA from the java.security package
	// which requires read-access to the
	// <jdk>\lib\security\java.security property file
	// in order to find the security provider to supply
	// the SHA algorithm that the java.io serialization
	// uses.
	if (filename.equals(fSecurityPropertyName))
		return;

	throw new SecurityException(filename + ": read access not allowed");
	}
/**
 * This method was created by a SmartGuide.
 * @return boolean
 * @param filename java.lang.String
 */
private void checkSandbox(String filename)
	{
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
 * Allow writes on any FileDescriptor 
 * @param fd java.io.FileDescriptor
 */
public void checkWrite(FileDescriptor fd) 
	{
	if (fSecurityLevel > 1)
		throw new SecurityException("Not allowed to write to FileDescriptor");
	}
/**
 * This method was created by a SmartGuide.
 * @param filename java.lang.String
 */
public void checkWrite(String filename) 
	{
	Engine.debugLog("checkWrite(\"" + filename + "\")");
	checkSandbox(filename);
	}
/**
 * This method was created by a SmartGuide.
 * @return java.lang.ThreadGroup
 */
public ThreadGroup getThreadGroup() 
	{
	return fSubthreadGroup;
	}
}