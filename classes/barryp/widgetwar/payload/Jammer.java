package barryp.widgetwar.payload;

import java.util.Vector;
import javax.vecmath.*;

import q2java.*;
import q2java.baseq2.*;
import q2java.core.*;
import q2java.core.event.*;

import barryp.widgetwar.*;
/**
 * Jam enemy team chats.  The effect varies inversely with the distance
 * between the sender and the jammer.
 *
 * @author Barry Pederson
 */
public class Jammer extends GenericWidgetComponent implements PrintListener
	{
	protected final static float CUTOFF = 1200.0F;  // radius of the jamming field...sort of
	protected final static char JAM_CHAR = '.';
	
/**
 * handleWidgetEvent method comment.
 */
public void handleWidgetEvent(int event, Object extra) 
	{
	switch (event)
		{
		case WidgetBody.DEPLOY:		
			Game.getPrintSupport().addPrintListener(this, PrintEvent.PRINT_TALK_TEAM, true);
			break;

		case WidgetBody.DESTROYED:
		case WidgetBody.TERMINATED:
			Game.getPrintSupport().removePrintListener(this);
			break;
		}
	}
/**
 * Called when a PrintEvent is fired.
 * @param pe q2java.core.event.PrintEvent
 */
public void print(PrintEvent pe)
	{
	Object obj = pe.getSource();

	// don't do anything of the print event didn't come from an object in the world
	if (!(obj instanceof NativeEntity))
		return;

	NativeEntity ent = (NativeEntity) obj;
	obj = ent.getReference();

	// don't do anything if the source isn't a player
	if (!(obj instanceof Player))
		return;
		
	WidgetBody b = getWidgetBody();

	// don't do anything if the person chatting is on the same
	// team as our jammer
	if (((Player)obj).getTeam() == b.getWidgetOwner().getTeam())
		return;

	Point3f p1 = b.getWidgetEntity().getOrigin();
	Point3f p2 = ent.getOrigin();

	// The jamFactor will indicate what percentage of
	// chars make it through the jamming.  So 0 = no chars
	// 0.5 = half the chars, anything >= 1.0 means
	// no jamming occured
	// I'll give them a minimum of 0.3, so they see
	// enough to be frustrated :)
	
	float jamFactor = 0.3F;

	// restrict scope of temp Vector3f
		{
		Vector3f v = Q2Recycler.getVector3f();
		v.sub(p1, p2);
		jamFactor += v.length() / CUTOFF;
		Q2Recycler.put(v);
		}

	// don't do anything if the sender was too far from the Jammer
	if (jamFactor >= 1.0F)
		return;

	// go through the print message, and goof it up a bit
	char [] ca = pe.getMessage().toCharArray();
	for (int i = 0; i < ca.length; i++)
		{
		if ((ca[i] != ' ') && (GameUtil.randomFloat() > jamFactor))
			ca[i] = JAM_CHAR;
		}
		
	// update the PrintEvent's message
	pe.setMessage(new String(ca));
	}
}