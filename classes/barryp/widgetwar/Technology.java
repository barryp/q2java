package barryp.widgetwar;

import org.w3c.dom.*;

import q2java.baseq2.InventoryList;

/**
 * Class for representing a known technology in the game. (CVS Check)
 *
 * @author Barry Pederson
 */
class Technology 
	{
	private int fTechType;
	private String fName;
	private String fDescription;
	private Class fClass;
	private float fEnergyCost;
	
	private int fInventoryIndex;

	// -- will be used separately by each team
	private float 	fFragmentsCaptured;
	private int 	fUseCounter;
	
	public final static int TYPE_BODY		= 0;
	public final static int TYPE_CONTROL	= 1;
	public final static int TYPE_PAYLOAD	= 2;

	private final static String[] COMPONENT_TYPES = {"body", "control", "payload"};
	
/**
 * Special constructor for null technologies.
 */
public Technology(int techType) 
	{
	fTechType = techType;
	fName = "---";
	fDescription = "not being used";
	InventoryList.addItem(fName);
	fInventoryIndex = InventoryList.getIndexOf(fName);
	}
/**
 * Copy an existing technology.
 * @param t barryp.widgetwar.Technology
 */
public Technology(Technology t) 
	{
	fTechType = t.fTechType;
	fName = t.fName;
	fDescription = t.fDescription;
	fClass = t.fClass;
	fEnergyCost = t.fEnergyCost;
	
	fInventoryIndex = t.fInventoryIndex;	
	}

	
/**
 * Create a Technology based on info from a DOM document.
 * @param e Element
 */
public Technology(Element e) throws Exception
	{
	String s = e.getAttribute("type");
	for (int i = 0; i < COMPONENT_TYPES.length; i++)
		{
		if (s.equals(COMPONENT_TYPES[i]))
			fTechType = i;
		}
		
	fName = e.getAttribute("name");
	fClass = Class.forName(e.getAttribute("class"));
	fEnergyCost = Float.valueOf(e.getAttribute("cost")).floatValue();
	try
		{		
		fDescription = ((NodeList)e.getElementsByTagName("description")).item(0).getFirstChild().getNodeValue();
		}
	catch (Exception ex)
		{
		ex.printStackTrace();
		}
	
	InventoryList.addItem(fName);
	fInventoryIndex = InventoryList.getIndexOf(fName);
	}	
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param o java.lang.Object
 */
public boolean equals(Object o) 
	{
	if (!(o instanceof Technology))
		return false;
		
	Technology t = (Technology) o;

	return (t.fTechType == fTechType)
		&& t.fName.equals(fName) 
		&& (t.fClass == fClass);
	}
/**
 * Get how many times this technology has been used.
 * @return int
 */
public int getCounter() 
	{
	return fUseCounter;
	}
/**
 * Get the more verbose description of the component.
 * @return java.lang.String
 */
public String getDescription() 
	{
	return fDescription;
	}
/**
 * Get how much energy is needed to create this component.
 * @return int
 */
public float getEnergyCost() 
	{
	return fEnergyCost;
	}
/**
 * This method was created in VisualAge.
 * @return float
 */
public float getFragmentsCaptured() 
	{
	return fFragmentsCaptured;
	}
/**
 * This method was created in VisualAge.
 * @return java.lang.Class
 */
public Class getImplementationClass() 
	{
	return fClass;
	}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getInventoryIndex() 
	{
	return fInventoryIndex;
	}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getName() 
	{
	return fName;
	}
/**
 * Get which kind of technology this is.
 * @return int
 */
public int getTechType() 
	{
	return fTechType;
	}
/**
 * Increment the count of how many times this technology has been used.
 */
public void incCounter() 
	{
	fUseCounter++;
	}
/**
 * This method was created in VisualAge.
 * @param f float
 */
public void incFragmentsCaptured(float f) 
	{
	fFragmentsCaptured += f;
	if (fFragmentsCaptured > 0.99F)
		fFragmentsCaptured = 1F;
	}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() 
	{
	return "Technology(\"" + fName + "\", " + fClass + ") inventoryIndex = " + fInventoryIndex;
	}
}