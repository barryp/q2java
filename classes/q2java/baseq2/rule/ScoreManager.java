package q2java.baseq2.rule;

import q2java.baseq2.event.ScoreEvent;

public interface ScoreManager
{
  public void addScoreListener(Object listener, Class eventClass);    
  public void registerScoreEvent(ScoreEvent e);    
  public void removeScoreListener(Object listener, Class eventClass);    
}