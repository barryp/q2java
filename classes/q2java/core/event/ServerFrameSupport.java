package q2java.core.event;

import q2java.*;
import q2java.core.CrossLevel;

/**
 * Private class used by Game to track ServerFrameListeners.
 *
 * @author Barry Pederson
 */
public class ServerFrameSupport 
	{
	private ServerFrameListener[] fListeners;
	private float[] fTime;
	private float[] fInterval;
	
	private int fArraySize;
	private int fGrowSize;
	private int fTop;
	private int fCurrentEntry;
	private float fCurrentTime;
	
/**
 * constructor
 */
public ServerFrameSupport(int initialSize, int growSize) 
	{
	fListeners = new ServerFrameListener[initialSize];
	fTime = new float[initialSize];
	fInterval = new float[initialSize];		
	fArraySize = initialSize;
	fGrowSize = growSize;
	
	
	fTop = fCurrentEntry = 0;
	fCurrentTime = 0;
	}	
/**
 * Register an object that implements FrameListener to receive frame events.
 * If an object is already registered, its time and interval will be updated.
 *
 * @param f object that wants its runFrame() method called.
 * @param delay Number of seconds to wait before calling the listener, use 0 to start calling right away.
 * @param interval Number of seconds between calls, use 0 to call on every frame, a negative interval will
 *   be a one-shot notification with the listener removed automatically afterwards.
 */
public void addServerFrameListener(ServerFrameListener f, float delay, float interval) 
	{
	// scan through the whole table looking for a pre-existing
	// entry. starting at the current entry (often what we're looking for)
	for (int i = fCurrentEntry; i < fTop; i++)
		{
		if (fListeners[i] == f)
			{
			fTime[i] = fCurrentTime + delay;
			fInterval[i] = interval;		
			return;
			}
		}

	// wrap around and look over the rest
	for (int i = 0; i < fCurrentEntry; i++)
		{
		if (fListeners[i] == f)
			{
			fTime[i] = fCurrentTime + delay;
			fInterval[i] = interval;		
			return;
			}
		}

	// we must be adding a new entry	

	if (fTop == fArraySize)
		grow();
		
	fListeners[fTop] = f;
	fTime[fTop] = fCurrentTime + delay;
	fInterval[fTop] = interval;	
	
	fTop++;
	}
/**
 * Increase the size of the arrays.
 */
private void grow() 
	{	
	fArraySize += fGrowSize;
	
	ServerFrameListener[] fla = new ServerFrameListener[fArraySize];
	System.arraycopy(fListeners, 0, fla, 0, fListeners.length);
	fListeners = fla;

	float[] fa = new float[fArraySize];
	System.arraycopy(fTime, 0, fa, 0, fTime.length);
	fTime = fa;	

	fa = new float[fArraySize];
	System.arraycopy(fInterval, 0, fa, 0, fInterval.length);
	fInterval = fa;		
	}
/**
 * Purge non-CrossLevel objects from the tables.
 */
public void purge() 
	{
	for (int i = 0; i < fTop; i++)
		{
		if (!(fListeners[i] instanceof CrossLevel))
			fListeners[i] = null;
		}
		
	shrinkTop();
	}
/**
 * Remove a FrameListener.
 * @param f q2jgame.FrameListener
 */
public void removeServerFrameListener(ServerFrameListener f) 
	{
	// scan through the table, starting at the current entry
	for (int i = fCurrentEntry; i < fTop; i++)
		{
		if (fListeners[i] == f)
			{
			fListeners[i] = null;
			if (i == (fTop - 1))
				shrinkTop();
			return;
			}
		}
				
	// wrap around and scan through the rest of the table
	for (int i = 0; i < fCurrentEntry; i++)
		{
		if (fListeners[i] == f)
			{
			fListeners[i] = null;
			if (i == (fTop - 1))
				shrinkTop();
			return;
			}
		}
	}
/**
 * Notify all the listeners that are due for a nudge.
 */
public void runFrame(int phase, float gameTime) 
	{
	fCurrentTime = gameTime;
	
	for (fCurrentEntry = 0; fCurrentEntry < fTop; fCurrentEntry++)
		{
		// pack the list down if possible
		if (fListeners[fCurrentEntry] == null)
			{
			fTop--;
			fListeners[fCurrentEntry] = fListeners[fTop];
			fTime[fCurrentEntry] = fTime[fTop];
			fInterval[fCurrentEntry] = fInterval[fTop];
			fListeners[fTop] = null;
			shrinkTop();
			}
						
		float time = fTime[fCurrentEntry];

		if ((fListeners[fCurrentEntry] != null) && (time <= fCurrentTime))
			{
			float intrvl = fInterval[fCurrentEntry];

			// update the time of the next call first...in case
			// the object ends up changing it itself;
			if (intrvl < 0)
				time = fTime[fCurrentEntry] = -1;
			else
				{
				time += intrvl;
				fTime[fCurrentEntry] = time;
				}
				
			try
				{	
				fListeners[fCurrentEntry].runFrame(phase);
				}
			catch (Exception e)
				{
				e.printStackTrace();
				}

			// delete any one-shot entries
			// that haven't been rescheduled.
			if ((intrvl < 0) && (time == fTime[fCurrentEntry])	 && (intrvl == fInterval[fCurrentEntry]))
				{
				fListeners[fCurrentEntry] = null;
				fCurrentEntry--;
				}
			else
				{
				// may have been deleted because it was a one-shot, or the
				// object may have deleted itself, so we need to 
				// check for null again.				
				if (fListeners[fCurrentEntry] == null)
					fCurrentEntry--; // reprocess this slot
				}
			}
		}
		
	fCurrentEntry = 0;
	}	
/**
 * Adjust the top of the lists so that it's just past
 * the last active entry.
 */
private void shrinkTop() 
	{
	while ((fTop > 0) && (fListeners[fTop-1] == null))
		fTop--;
	}
}