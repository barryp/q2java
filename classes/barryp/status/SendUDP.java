package barryp.status;

import java.io.*;
import java.net.*;
import java.util.zip.*;

import org.w3c.dom.*;

import q2java.*;
import q2java.core.*;
import q2java.core.event.*;

/**
 * Send the game's status out as a UDP packet.
 *
 * @author Barry Pederson
 */
public class SendUDP extends Gamelet
implements GameStatusListener, ServerFrameListener, CrossLevel
	{
	protected final static int SERVER_FRAME_INTERVAL = 10; // how often we want to ponder life the universe and everything else
	
	protected InetAddress fAddress;
	protected int fPort;
	
	protected String fTemplateURL;
	protected String fTemplateType = "text/xsl";
	protected String fPIContents;

	protected ByteArrayOutputStream fBAOS;
	protected DatagramSocket fDS;
	protected DatagramPacket fDP;

	protected int fServerPort;
	protected byte[] fMsgBuffer = new byte[3];  // buffer for heartbeat and goodbye messages
	protected byte[] fAckBuffer = new byte[16];
	protected DatagramPacket fAck = new DatagramPacket(fAckBuffer, fAckBuffer.length);
	
	// flood prevention - keep Gamelet from spewing out
	// packets when things are rapidly changing
	protected long fLastTransmission;
	protected boolean fTransmissionNeeded;
	protected long fMinInterval = 15 * 1000;  // wait AT LEAST this long (in milliseconds) between transmissions
	protected long fMaxInterval = 5 * 60 * 1000; // wait AT MOST this long between sending a report

	// keep track of the packets, and whether they seem
	// to have gotten through to the destination
	protected int fSequence;
	protected boolean fWasAcknowledged = true; // start out not expecting an ack
	
/**
 * WriteXML constructor comment.
 * @param gameletName java.lang.String
 */
public SendUDP(Document gameletInfo) 
	{
	super(gameletInfo);

	// read any parameters passed from XML
	XMLTools.parseParams(gameletInfo.getDocumentElement(), this, SendUDP.class);

	// register to find out when the status document changes
	Game.addGameStatusListener(this);

	// get called every so often to consider whether an update or retransmission should be sent out
	Game.addServerFrameListener(this, Game.FRAME_BEGINNING, 0, SERVER_FRAME_INTERVAL);

	fServerPort = (int) (new CVar("port", "27910", 0).getFloat());
	
	// write out the current status -right now- if a map is running
	if (Game.getCurrentMapName() != null)
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
		case GameStatusEvent.GAME_POSTSPAWN:
		    // clear the clock so we can transmit right away on a level change
			fLastTransmission = 0;
			break;
			
		case GameStatusEvent.GAME_DOCUMENT_UPDATED:
			if (gse.getDocumentName().equals("q2java.status"))
				{
				if ((System.currentTimeMillis() - fLastTransmission) < fMinInterval)
					// make a note to do this later
					fTransmissionNeeded = true;
				else
					// do it now
					writeStatus();
				}
			break;
		}
	}
/**
 * Get the address this Gamelet sends UDP Packets to.
 */
public String getAddress() 
	{
	return fAddress + ":" + fPort;
	}
/**
 * Get the minimum interval (in seconds) between UDP transmissions.
 * @return java.lang.String
 */
public String getMaxInterval() 
	{
	return Long.toString(fMaxInterval / 1000);
	}
/**
 * Get the minimum interval (in seconds) between UDP transmissions.
 * @return java.lang.String
 */
public String getMinInterval() 
	{
	return Long.toString(fMinInterval / 1000);
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
 * tick tock - check if we need to send out an update and if
 * enough time has passed since the last transmission
 * @param phase int
 */
public void runFrame(int phase) 
	{
	// can't do anything if we're not talking to anybody in particular
	if (fDP == null)
		return;
		
	long now = System.currentTimeMillis();
	long interval = now - fLastTransmission;

	// do we need to send something, and enough time has passed
	// since the last transmission?
	if (fTransmissionNeeded && (interval > fMinInterval))
		{
		writeStatus();
		return;
		}

	// has it been a while since the last update?
	// then send a packet with the last acknowledged sequence number 
	// just to let the server know we're still here
	if (fWasAcknowledged && (interval > fMaxInterval))
		{
		try
			{
			fMsgBuffer[0] = (byte) fSequence;
			fMsgBuffer[1] = (byte) ((fServerPort >> 8) & 0x00ff);
			fMsgBuffer[2] = (byte) (fServerPort & 0x00ff);
			fDP.setData(fMsgBuffer);
			fDP.setLength(3);

			fDS.send(fDP);
			fWasAcknowledged = false;
			fLastTransmission = now;
			}
		catch (IOException ioe)
			{
			}
		}
		
	// did we send something out and haven't heard anything back?
	if (!fWasAcknowledged)
		{
		try
			{			
			while (true) // won't be an infinite loop..the receive() will timeout
				{
				fAck.setLength(fAckBuffer.length);
				fDS.receive(fAck);
				if (fAckBuffer[0] == fSequence)
					{
					fWasAcknowledged = true;
					return; // groovy
					}
				}
			}
		catch (IOException ioe)
			{
			// probably an InterruptedIOException
			}
		try
			{
			// resend whatever we sent last
			fDS.send(fDP);
			}
		catch (IOException ioe)
			{
			}
		}
	}
/**
 * Send a goodbye packet to the server we're talking to.
 */
protected void sayGoodbye() 
	{
	if (fDP != null)
		{
		// send a special goodbye message to give the
		// server a hint that we're gone
		// (since UDP is unreliable, the server can't really
		//  count of always receiving these..but the ones
		//  that do make it through will let the server
		//  get a clue faster)	
		fMsgBuffer[0] = -1;
		fMsgBuffer[1] = (byte) ((fServerPort >> 8) & 0x00ff);
		fMsgBuffer[2] = (byte) (fServerPort & 0x00ff);
		fDP.setData(fMsgBuffer);
		fDP.setLength(3);

		try
			{
			// what the hell..send it a few times..who knows..maybe some will get through
			fDS.send(fDP);
			fDS.send(fDP);
			fDS.send(fDP);			
			}
		catch (IOException ioe)
			{
			}

		fDP = null;			
		}	
	}
/**
 * Set the address this gamelet sends udp packets to.
 * @param s java.lang.String
 */
public void setAddress(String s) throws Exception
	{
	// say goodbye to any previous server we were talking to	
	sayGoodbye();  

	// figure out the ip addr and port
	fAddress = null;
	int p = s.indexOf(':');
	fPort = Integer.parseInt(s.substring(p+1));
	fAddress = InetAddress.getByName(s.substring(0, p));

	// cause a full update to be sent to the new address
	if (fServerPort != 0)
		writeStatus();
	}
/**
 * Set the maximum interval in seconds 
 * between UDP transmissions.
 * @param s java.lang.String
 */
public void setMaxInterval(String s) 
	{
	fMaxInterval = Long.parseLong(s) * 1000;
	}
/**
 * Set the minimum interval in seconds 
 * between UDP transmissions.
 * @param s java.lang.String
 */
public void setMinInterval(String s) 
	{
	fMinInterval = Long.parseLong(s) * 1000;
	}
/**
 * Set the MIME-type of the stylesheet associated with the XML file.
 * @param s java.lang.String
 */
public void setTemplateType(String s) 
	{
	fTemplateType = s;
	fPIContents = null;
	}
/**
 * Set the URL of the stylesheet associated with the XML file.
 * @param s java.lang.String
 */
public void setTemplateURL(String s) 
	{
	fTemplateURL = s;
	fPIContents = null;
	}
/**
 * Remove the gamelet.
 */
public void unload() 
	{
	// remove as listener
	Game.removeGameStatusListener(this);
	Game.removeServerFrameListener(this, Game.FRAME_BEGINNING);
	
	if (fDS != null)
		{
		sayGoodbye();
		
		fDS.close();
		fDS = null;
		}

	// help with gc
	fBAOS = null;
	}
/**
 * Write the document out to a file.
 */
protected void writeStatus() 
	{
	if (fAddress == null)
		return;
		
	Document doc = Game.getDocument("q2java.status");
	ProcessingInstruction pi = null;
	
	// add stylesheet PI if needed
	if (fTemplateURL != null)
		{
		Element root = doc.getDocumentElement();

		if (fPIContents == null)
			fPIContents = "href=\"" + fTemplateURL + "\" type=\"" + fTemplateType + "\"";
			
		pi = doc.createProcessingInstruction("xml-stylesheet", fPIContents);	
		doc.insertBefore(pi, root);
		}
		
	// actually send the packet
	try
		{
		if (fBAOS == null)
			fBAOS = new ByteArrayOutputStream();
		else
			// reuse existing ByteArrayOutputStream
			fBAOS.reset();

		// add the sequence number and server port to
		// help identify this packet to the server
		fSequence = ++fSequence & 0x007f;
		fBAOS.write(fSequence);
		fBAOS.write((fServerPort >> 8) & 0x00ff);
		fBAOS.write(fServerPort & 0x00ff);
			
		// write the doc to the ByteArrayOutputStream
		OutputStreamWriter osw = new OutputStreamWriter(fBAOS);
		XMLTools.writeXMLDocument(doc, osw);
		osw.close();

		// get the contents 
		byte [] b = fBAOS.toByteArray();

		// make sure we have a socket to send out of
		if (fDS == null)
			{
			fDS = new DatagramSocket();
			fDS.setSoTimeout(1);
			}

		// get the DatagramPacket ready
		if (fDP == null)
			fDP = new DatagramPacket(b, b.length, fAddress, fPort);
		else
			{
			// reuse existing DatagramPacket
			fDP.setData(b);
			fDP.setLength(b.length);
			}
			
		// send the info
		fDS.send(fDP);
		fLastTransmission = System.currentTimeMillis();
		fTransmissionNeeded = false;
		fWasAcknowledged = false;
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