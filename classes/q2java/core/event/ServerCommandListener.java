package q2java.core.event;

public interface ServerCommandListener  extends java.util.EventListener
{
  public void serverCommandIssued(ServerCommandEvent e);      
}