package barryp.widgetwar;

/**
 * Interface for Widget components, this includes both the
 * control components and the payload components.
 *
 * @author Barry Pederson
 */
public interface WidgetComponent 
	{
	
/**
 * Get the WidgetBody this component belongs to.
 * @return barryp.widgetwar.WidgetBody
 */
public WidgetBody getWidgetBody();
/**
 * Called by the widget body to signal something.
 *
 * @param event one of the WidgetBody.SIGNAL_* constants.
 * @param extra some Object relevant to the event.  SIGNAL_TARGET for example should also pass a Point3f
 */
public void handleWidgetEvent(int event, Object extra);
/**
 * Let the component know what WidgetBody it's been associated with.
 * @param wb barryp.widgetwar.WidgetBody
 */
public void setWidgetBody(WidgetBody wb);
}