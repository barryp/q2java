package barryp.xmllink;

import org.xml.sax.AttributeList;

/**
 * A task that can be created from XML data, and run by the main game thread.
 */
public interface XMLJob
	{
	
/**
 * Run the job in the main game thread.
 * @param parent barryp.xmllink.GameModule running this job.
 */
void run(GameModule parent);

/**
 * Configure the XML Job.
 * @param attrs AttributeList
 * @param value java.lang.String
 */
void setParams(AttributeList attrs, String value);
}