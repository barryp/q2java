package q2java.baseq2;

/**
 * Simple class to hold a welcome message to show 
 * new users as they connect.  It should be easy to 
 * make changes to this class and recompile to change the
 * message.  Perhaps a Server command could also be added
 * to change it at runtime.
 * 
 * @author Barry Pederson
 */
public class WelcomeMessage 
	{
	// some parts of the welcome message don't change from moment-to-moment
	// so we might as well figure out things like VM versions one time
	// when the class is first loaded instead of doing it over and over
	// each time someone connects
	private static String gPrefix = BaseQ2.getVersion() 
		+ "\nVM: [" + System.getProperty("java.vendor") + " " + System.getProperty("java.version") + "]"
		+ "\nOS: [" + System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch") + "]";
	
/**
 * Return the message to display to new players.
 *
 * @return java.lang.String
 */
public static String getMessage() 
	{
	StringBuffer sb = new StringBuffer();
	
	sb.append(gPrefix);

	// Kaffe might freak out when formatting a date, comment this
	// try/catch block out if necessary (BP 3/27/99)
	try
		{
		java.util.Date d = new java.util.Date();
		String ds = d.toString();  
		sb.append("\n\n");
		sb.append(ds);
		}
	catch (ExceptionInInitializerError eiie)
		{
		eiie.getException().printStackTrace();
		}
	catch (Exception e)
		{
		e.printStackTrace();
		}

	return sb.toString();		
	}
}