package q2java;

/**
 * An exception class to represent game errors.
 *
 * @author Barry Pederson
 */ 
public class GameException extends RuntimeException
	{

	
public GameException(String msg)
	{
	super(msg);
	}
}