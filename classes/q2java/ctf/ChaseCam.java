package q2java.ctf;

/*
======================================================================================
==                                 Q2JAVA CTF                                       ==
==                                                                                  ==
==                   Author: Menno van Gangelen <menno@element.nl>                  ==
==                                                                                  ==
==            Based on q2java by: Barry Pederson <bpederson@geocities.com>          ==
==                                                                                  ==
== All sources are free for non-commercial use, as long as the licence agreement of ==
== ID software's quake2 is not violated and the names of the authors of q2java and  ==
== q2java-ctf are included.                                                         ==
======================================================================================
*/


import java.util.*;
import javax.vecmath.*;
import q2java.*;
import q2java.core.*;

/**
 * A misc_ctf_banner is a giant flag that
 * just sits and flutters in the wind.
 */


public class ChaseCam
{
	protected CTFPlayer  fOwner;		// The Player we're looking at.
	protected Vector  fListeners;	// The Players who are looking with us.

	protected Point3f  fGoalPosition;
	public Point3f  fPosition;
	protected Vector3f fForward;
	public Angle3f  fAngle;
	protected boolean fActive;

	// We'll keep a reference to all chasecams, so we can serch for them easily
	static Vector gChaseCams = new Vector();

	//===================================================
	// Constructor
	//===================================================
	public ChaseCam( CTFPlayer owner )
	{
		fOwner        = owner;
		fListeners    = new Vector();
		fForward      = new Vector3f();
		fGoalPosition = new Point3f();
		fActive       = false;
		gChaseCams.addElement( this );
	}
	//===================================================
	// Methods
	//===================================================

	public void addCameraListener( CameraListener listener )
	{
		// Don't add our owner...
		if ( listener != fOwner )
		{
			fListeners.addElement( listener );
			listener.cameraActivated( this );
		}
	}
	public void dispose()
	{
		setActive( false );
		fOwner  = null;
		gChaseCams.removeElement( this );
	}
	/**
	* Searches for the next chasecam which is not ours and not the current
	**/
	public ChaseCam getNextChaseCam( ChaseCam current )
	{
		int      numCams, index;
		ChaseCam cams[], nextCam;

		numCams = gChaseCams.size();
		cams    = new ChaseCam[ numCams ];
		gChaseCams.copyInto( cams );

		if ( current == null )
			current = this;

		index   = gChaseCams.indexOf( current );
		nextCam = null;
		do
		{
			index   = (index+1) % numCams;
			nextCam = cams[index];
			if ( nextCam == this )
				continue;
			else if ( nextCam.fActive )
				break;
		}
		while ( nextCam != current );

		if ( nextCam != this && nextCam != current )
			return nextCam;
		else
			return null;
	}
	/**
	* Searches for the previous chasecam which is not ours and not the current
	**/
	public ChaseCam getPreviousChaseCam( ChaseCam current )
	{
		int      numCams, index;
		ChaseCam cams[], nextCam;

		numCams = gChaseCams.size();
		cams    = new ChaseCam[ numCams ];
		gChaseCams.copyInto( cams );

		if ( current == null )
			current = this;

		index   = gChaseCams.indexOf( current );
		nextCam = null;
		do
		{
			index   = (numCams+index-1) % numCams;
			nextCam = cams[index];
			if ( nextCam == this )
				continue;
			else if ( nextCam.fActive )
				break;
		}
		while ( nextCam != current );

		if ( nextCam != this && nextCam != current )
			return nextCam;
		else
			return null;
	}
	public boolean isActive()
	{
		return fActive;
	}
	public boolean removeCameraListener( CameraListener listener )
	{
		return fListeners.removeElement( listener );
	}
	public void setActive( boolean active )
	{
		fActive = active;
		if ( !active )
		{
			// inform listeners that we're not active anymore
			Enumeration enum = fListeners.elements();
			while( enum.hasMoreElements() )
			{
				CameraListener listener = (CameraListener)enum.nextElement();
				listener.cameraDeactivated( this );
				fListeners.removeElement( listener );
			}
		}
	}
	public void update()
	{
		if ( !fActive )
			return;

		if ( fListeners == null || fListeners.size() == 0 )
			return;

		if ( fOwner == null || fOwner.getHealth() <= 0 )
			return;

		// set our position to the eye-position of our owner.
		fPosition    = fOwner.fEntity.getOrigin();
		fPosition.z += fOwner.fViewHeight;

		// set our position a little back of our owners position
		fAngle   = fOwner.fEntity.getPlayerViewAngles();
		fAngle.getVectors( fForward, null, null );
		fForward.normalize();
		fGoalPosition.scaleAdd( -30, fForward, fPosition );
	
		// make sure that we're not inside another object
		TraceResults tr = Engine.trace( fPosition, fGoalPosition, fOwner.fEntity, Engine.MASK_SOLID );
		fPosition.set( tr.fEndPos );

		// inform listsners that our position has changed
		Enumeration enum = fListeners.elements();
		while( enum.hasMoreElements() )
		{
			CameraListener listener = (CameraListener)enum.nextElement();
			listener.cameraUpdated( this );
		}
	}
}