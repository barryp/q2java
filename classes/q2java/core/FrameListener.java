package q2java.core;

/**
 * Interface a class needs to implement to be notified
 * of Game serverframe activities.
 *
 * @author Barry Pederson
 */
public interface FrameListener 
	{
	
/**
 * Method called to notify the running of a Server Frame.
 * @param phase Which phase of the processing the Game
 *   is in, one of the Game.FRAME_* constants.
 */
public void runFrame(int phase);
}