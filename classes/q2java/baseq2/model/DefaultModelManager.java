package q2java.baseq2.model;

import q2java.NativeEntity;

public class DefaultModelManager implements ModelManager
{
  protected NativeEntity fEntity;
  protected int fModelIndex;
  protected int fAnimationFrame;
  protected int fAnimationPriority;
  protected int fAnimationEnd;
  protected boolean fReversed;
  protected boolean fIsRunning;
  protected boolean fIsDucking;  

  public NativeEntity getEntity() { return fEntity; }    
  public int getModelIndex() { return fModelIndex; }    
  public boolean isReversed() { return fReversed; }    
  public void setAnimation(int animation)
	{
	  setAnimation(animation,false,0);
	}
  public void setAnimation(int animation, boolean ignorePriority, int frameOffset)
	{
	}
  public void setEntity(NativeEntity entity) { fEntity = entity; }    
  public void setModelIndex(int modelIndex) { fModelIndex = modelIndex; }    
  public void setReversed(boolean reversed) { fReversed = reversed; }    
}