package q2java.baseq2.spawn;

import org.w3c.dom.Element;

import q2java.*;
import q2java.core.*;
import q2java.core.event.ServerFrameListener;
import q2java.baseq2.*;

/**
 * A misc_banner seems to be a giant flag that
 * just sits and flutters in the wind.
 */

public class misc_banner extends GameObject implements ServerFrameListener
	{
	private int fCurrentFrame;
	
public misc_banner(Element spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	fEntity.setSolid(NativeEntity.SOLID_NOT);
	fEntity.setModel("models/objects/banner/tris.md2");

	fCurrentFrame = (GameUtil.randomInt() & 0x0fff) % 16;
	fEntity.setFrame(fCurrentFrame);

	fEntity.linkEntity();
	
	// ask to be called back each server frame
	Game.addServerFrameListener(this, 0, 0);
	}
/**
 * Animate the banner.
 * @param phase int
 */
public void runFrame(int phase) 
	{
	fCurrentFrame = (fCurrentFrame + 1) % 16;
	fEntity.setFrame(fCurrentFrame);
	}
}