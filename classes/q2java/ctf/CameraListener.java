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


public interface CameraListener
{
	// called when we succesfully connected to the camera...
	public void cameraActivated( ChaseCam cam );
	// called when camara has been disposed...
	public void cameraDeactivated( ChaseCam cam );
	// called when camaraposition has been updated...
	public void cameraUpdated( ChaseCam cam );
}