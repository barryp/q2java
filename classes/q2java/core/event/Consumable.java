package q2java.core.event;

public interface Consumable
{
  public void consume();      
  public boolean isConsumed();      
  public void setConsumed(boolean consumed);      
}