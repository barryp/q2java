
package q2jgame;

import java.util.Enumeration;
import q2java.*;

public class func_plat extends GameEntity
	{
	private int fState;
	private Vec3 fCurrentPos;
	private Vec3 fRaisedPos;
	private Vec3 fLoweredPos;
	private Vec3 fMoveDir;
	private float fLowerTime;
	private float fRaisedAmount;
	private int fLip; //?
	
	private final static int STATE_LOWERED	= 0;
	private final static int STATE_RAISING	= 1;
	private final static int STATE_RAISED	= 2;
	private final static int STATE_LOWERING	= 3;
	
public func_plat(String[] spawnArgs) throws GameException
	{
	super(spawnArgs);
	
	setAngles(0, 0, 0); // not sure why this is necessary, but it's in the original Game DLL
	setSolid(SOLID_BSP);
	
	String s = getSpawnArg("model");
	if (s != null)
		setModel(s);

	s = getSpawnArg("lip");
	if (s != null)
		fLip = Integer.parseInt(s);
	else 
		fLip = 8;		
		
		
	// setup for opening and closing
	fRaisedPos = getOrigin();
	Vec3 mins = getMins();
	Vec3 maxs = getMaxs();

	fMoveDir = new Vec3(0, 0, (maxs.z - mins.z) - fLip);
		
	fLoweredPos = (new Vec3(fRaisedPos)).subtract(fMoveDir);	
	fCurrentPos = new Vec3(fLoweredPos);
	fRaisedAmount = 0.0F;
	setOrigin(fLoweredPos);
	linkEntity();		
	}
/**
 * This method was created by a SmartGuide.
 */
public void runFrame() 
	{
//		ent->moveinfo.sound_middle = gi.soundindex  ("doors/pt1_mid.wav");

	switch (fState)
		{
		case STATE_LOWERED:
			if (Game.fGameTime >= fLowerTime)		
				fState = STATE_RAISING;
			break;
			
		case STATE_RAISED:
			if (Game.fGameTime >= fLowerTime)		
				fState = STATE_LOWERING;
			break;
		
		case STATE_RAISING:
			// are we just starting to raise?
			if (fRaisedAmount == 0)
				{
				sound(CHAN_NO_PHS_ADD+CHAN_VOICE, Engine.soundIndex("plats/pt1_strt.wav"), 1, ATTN_STATIC, 0);				
				}
				
			fRaisedAmount += 0.1;
			if (fRaisedAmount < 1)
				setOrigin(fLoweredPos.vectorMA(fRaisedAmount, fMoveDir));
			else				
				{
				setOrigin(fRaisedPos);
				fState = STATE_RAISED;
				fRaisedAmount = 1.0F;
				fLowerTime = (float)(Game.fGameTime + 8); 
				}
			break;
			
		case STATE_LOWERING:
			fRaisedAmount -= 0.1;
			if (fRaisedAmount > 0)
				setOrigin(fLoweredPos.vectorMA(fRaisedAmount, fMoveDir));
			else
				{
				setOrigin(fLoweredPos);				
				fState = STATE_LOWERED;
				fRaisedAmount = 0;
				fLowerTime = (float)(Game.fGameTime + 8);
				sound(CHAN_NO_PHS_ADD+CHAN_VOICE, Engine.soundIndex("plats/pt1_end.wav"), 1, ATTN_STATIC, 0);				
				}
			break;			
		}
	}
}