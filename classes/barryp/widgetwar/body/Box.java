package barryp.widgetwar.body;

import java.util.Vector;
import q2java.*;
import q2java.baseq2.*;
import barryp.widgetwar.*;

/**
 * Simple body that just sits on the ground.
 *
 * @author Barry Pederson
 */
public class Box extends GenericWidgetBody
	{
	
/**
 * This method was created in VisualAge.
 */
protected void deployWidget() 
	{
	super.deployWidget();

	fEntity.setMins(-16, -16, -16);
	fEntity.setMaxs(16, 16, -2);
	
	fEntity.setModel("models/items/healing/large/tris.md2");

	// create a helper object that causes the model to fall to the ground
	(new DropHelper()).drop(null, this, null, 0);
	}
}