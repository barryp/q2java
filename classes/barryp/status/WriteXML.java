package barryp.status;

import java.io.*;

import org.w3c.dom.*;

import q2java.Engine;
import q2java.core.*;
import q2java.core.event.*;

/**
 * Write the game's status out to an XML file whenever it changes.
 *
 * @author Barry Pederson
 */
public class WriteXML extends Gamelet
implements GameStatusListener
	{
	protected String fFilename;
	protected String fTemplateURL;
	protected String fTemplateType = "text/xsl";
	
/**
 * WriteXML constructor comment.
 * @param gameletName java.lang.String
 */
public WriteXML(Document gameletInfo) 
	{
	super(gameletInfo);

	// read any parameters passed from XML
	XMLTools.parseParams(gameletInfo.getDocumentElement(), this, WriteXML.class);

	// register to find out when the status document changes
	Game.addGameStatusListener(this);

	// write out the current status
	writeStatus();
	}
/**
 * Called when the game status changes.
 * @param gse q2java.core.event.GameStatusEvent
 */
public void gameStatusChanged(GameStatusEvent gse) 
	{
	switch (gse.getState())
		{
		case GameStatusEvent.GAME_DOCUMENT_UPDATED:
			if (gse.getDocumentName().equals("q2java.status"))
				writeStatus();
			break;
		}
	}
/**
 * Get the name of the file this gamelet writes to.
 */
public String getFilename() 
	{
	return fFilename;
	}
/**
 * Get the MIME-type of the stylesheet associated with the XML file.
 */
public String getTemplateType() 
	{
	return fTemplateType;
	}
/**
 * Get the URL of the stylesheet associated with the XML file.
 */
public String getTemplateURL() 
	{
	return fTemplateURL;
	}
/**
 * Set the name of the file this gamelet writes to.
 * @param s java.lang.String
 */
public void setFilename(String s) 
	{
	fFilename = s;
	}
/**
 * Set the MIME-type of the stylesheet associated with the XML file.
 * @param s java.lang.String
 */
public void setTemplateType(String s) 
	{
	fTemplateType = s;
	}
/**
 * Set the URL of the stylesheet associated with the XML file.
 * @param s java.lang.String
 */
public void setTemplateURL(String s) 
	{
	fTemplateURL = s;
	}
/**
 * Remove the gamelet.
 */
public void unload() 
	{
	// remove as listener
	Game.removeGameStatusListener(this);
	}
/**
 * Write the document out to a file.
 */
protected void writeStatus() 
	{
	if (fFilename == null)
		return;
		
	Document doc = Game.getDocument("q2java.status");
	ProcessingInstruction pi = null;
	
	// add stylesheet PI if needed
	if (fTemplateURL != null)
		{
		Element root = doc.getDocumentElement();
	
		pi = doc.createProcessingInstruction("xml-stylesheet", "href=\"" + fTemplateURL + "\" type=\"" + fTemplateType + "\"");	
		doc.insertBefore(pi, root);
		}
		
	// actually write the file
	try
		{
		FileWriter w = new FileWriter(fFilename);
		XMLTools.writeXMLDocument(doc, w);
		w.close();
		}
	catch (IOException ioe)
		{
		ioe.printStackTrace();
		}

	// take the PI back out if one was created
	if (pi != null)
		doc.removeChild(pi);
	}
}