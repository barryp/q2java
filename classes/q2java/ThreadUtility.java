package q2java;

import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

/**
 * Helper class to synchronize the running of code 
 * between different threads.  Not particularily Quake-related - maybe
 * useful for other programs too.
 *
 * This class implements Runnable, and has methods that take Runnable as a parameter -
 * hopefully that isn't too confusing.  It doesn't really need to implement Runnable -
 * but I figured it might as well, since the run() method is compatible.
 *
 * @author Barry Pederson
 */
 
public class ThreadUtility implements Runnable
	{
	private Vector fRunnableQueue = new Vector();

	// helper object for invokeAndWait()
	private static class WaitRunnable 
		{
		Runnable fRunnable;
		boolean fTaskFinished;
		Throwable fThrowable;
		}	
	
/**
 * Causes doRun.run() to be executed on some other thread - and waits 
 * until it's finished before returning.  Using this on a single-threaded
 * program, or calling from the same thread that's supposed to be
 * calling runDeferred() would cause the program to lockup.
 *
 * This is basically the same technique Java Swing uses to allow threads to
 * run code on the main thread.
 *
 * @param doRun An object that implements java.lang.Runnable
 *
 * @exception  InterruptedException If we're interrupted while waiting for
 *             the event dispatching thread to finish excecuting <i>doRun.run()</i>
 * @exception  InvocationTargetException  If <i>doRun.run()</i> throws
 *
 * @see #invokeLater 
 */
public void invokeAndWait(final Runnable doRun) throws InterruptedException, InvocationTargetException
	{
	WaitRunnable wr = new ThreadUtility.WaitRunnable();
	wr.fRunnable = doRun;
	
	// add the job object and notify any threads waiting for jobs
	fRunnableQueue.addElement(wr);

	// wait to be notified that the task is finished		
	synchronized (wr)
		{
		while (!wr.fTaskFinished)
			wr.wait();
		}

	// if the main thread caught an exception and passed it
	// back here, rethrow it.
	if (wr.fThrowable != null)
		throw new InvocationTargetException(wr.fThrowable);
	}

	
/**
 * Causes doRun.run() to be executed at some later time, 
 * probably by a different thread - but doesn't wait for it to finish.
 *
 * This is basically the same technique Java Swing uses to allow threads to
 * run code on the main thread.
 *
 * Any exceptions thrown on the main thread are quietly ignored.
 *
 * @param doRun An object that implements java.lang.Runnable
 */
public void invokeLater(Runnable doRun)
	{
	fRunnableQueue.addElement(doRun);
	}
/**
 * Run whatever Runnable jobs that are queued up, returns when the queue is empty.  
 *
 * Probably will be called repeatedly by a single thread, but is safe to be called
 * by multiple threads.
 */
public void run() 
	{
	while (true)
		{
		Object obj;
		Runnable doRun;
		WaitRunnable wr = null;
		Throwable thr = null;

		// pull an object off the queue or quit
		synchronized (fRunnableQueue)
			{
			if (fRunnableQueue.size() < 1)
				return;
				
			obj = fRunnableQueue.elementAt(0);
			fRunnableQueue.removeElementAt(0);			
			}

		// figure out what was on the queue, should be
		// either a java.lang.Runnable or a WaitRunnable
		// if another thread is waiting 
		if (obj instanceof Runnable)
			doRun = (Runnable) obj;
		else
			{
			wr = (WaitRunnable) obj;
			doRun = wr.fRunnable;
			}
			
		// execute the Runnable object
		try
			{
			doRun.run();
			}
		catch (Throwable t)
			{
			thr = t;
			}

		// If some other thread is waiting for the job to run, 
		// save any caught exceptions, and notify it that
		// the job is finished.
		if (wr != null)
			{
			wr.fThrowable = thr;
			wr.fTaskFinished = true;
			synchronized (wr)
				{
				wr.notify();
				}
			}
				
		}
	}	
}