package q2java.baseq2.model;

import q2java.NativeEntity;

public interface ModelManager
{
  // basic animations
  public final static int ANIMATE_NORMAL = 0; // normal = stand, normal+1 = run
  public final static int ANIMATE_ATTACK = 2;
  public final static int ANIMATE_PAIN = 3;
  public final static int ANIMATE_DEATH = 6;
  public final static int ANIMATE_VWEP_THROW = 9; 
  public final static int ANIMATE_VWEP_ACTIVATE = 10;
  public final static int ANIMATE_VWEP_DEACTIVATE = 11;
		
  // gesture animations
  public final static int ANIMATE_FLIPOFF = 24;
  public final static int ANIMATE_SALUTE = 25;
  public final static int ANIMATE_TAUNT = 26;
  public final static int ANIMATE_WAVE = 27;
  public final static int ANIMATE_POINT = 28;
  
  // jumping animations
  public final static int ANIMATE_JUMP = 29;
  public final static int ANIMATE_FLAIL = 30;
  public final static int ANIMATE_LAND = 31;

  public NativeEntity getEntity();    
  public int getModelIndex();    
  public boolean isReversed();    
  public void setAnimation(int animation);    
  public void setAnimation(int animation, boolean ignorePriority, int frameOffset);    
  public void setEntity(NativeEntity entity);    
  public void setModelIndex(int modelIndex);    
  public void setReversed(boolean reversed);    
}