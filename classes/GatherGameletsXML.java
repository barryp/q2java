import java.io.*;

/**
 * Search through a directory tree looking for "*.gamelet" files
 * and combine them together into a larger XML file.
 *
 * @author Barry Pederson
 */
class GatherGameletsXML 
	{
	private static FileWriter gFW;
	private static char[] gBuffer = new char[4096];
	
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String args[]) throws IOException
	{
	if (args.length < 2)
		{
		System.out.println("Usage: GatherGamelets <starting-dir> <output-file-name>");
		return;
		}

	File startDir = new File(args[0]);
	if (!startDir.isDirectory())
		{
		System.out.println("[" + args[0] + "] is not a directory\n");
		return;
		}
		
	gFW = new FileWriter(args[1]);
	gFW.write("<?xml version=\"1.0\"?>\n");
	gFW.write("<gamelets>\n");

	processDir(startDir);
	
	gFW.write("</gamelets>\n");
	gFW.close();
	}
/**
 * Process a given directory.
 * @param dir java.io.File
 */
private static void processDir(File dir) 
	{
	System.out.println("Processing " + dir.getPath());
	
	String[] contents = dir.list();

	for (int i = 0; i < contents.length; i++)
		{
		File f = new File(dir, contents[i]);
		if (contents[i].endsWith(".gamelet") && f.isFile())
			processGamelet(f);
		else if (f.isDirectory())
			processDir(f);		
		}
	}
/**
 * Read a particular gamelet file and add it to the new file.
 * @param f java.io.File
 */
private static void processGamelet(File f) 
	{
	try
		{
		FileReader r = new FileReader(f);
		gFW.write("\n");
		int nRead;
		while ((nRead = r.read(gBuffer)) > 0)
			gFW.write(gBuffer, 0, nRead);

		r.close();
		gFW.write("\n");
		}
	catch (IOException ioe)
		{
		ioe.printStackTrace();
		}	
	}
}