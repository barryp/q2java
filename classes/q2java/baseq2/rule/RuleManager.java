package q2java.baseq2.rule;

public final class RuleManager
{
  private static  DefaultScoreManager gScoreManager = new DefaultScoreManager();
  
  private RuleManager() {}    
  public final static ScoreManager getScoreManager() { return gScoreManager; }    
}