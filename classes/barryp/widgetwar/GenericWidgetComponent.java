package barryp.widgetwar;

/**
 * Simple superclass for components
 *
 * @author Barry Pederson
 */
public abstract class GenericWidgetComponent implements WidgetComponent 
	{
	private WidgetBody fBody;
	
/**
 * getWidgetBody method comment.
 */
public WidgetBody getWidgetBody() 
	{
	return fBody;
	}
/**
 * handleWidgetEvent method comment.
 */
public void handleWidgetEvent(int event, Object extra) 
	{
	}
/**
 * setWidgetBody method comment.
 */
public void setWidgetBody(WidgetBody wb) 
	{
	fBody = wb;
	}
}