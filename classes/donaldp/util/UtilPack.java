package donaldp.util;

import q2java.NativeEntity;
import q2java.Engine;
import java.util.*;

final public class UtilPack
{ 
	private static ResourceBundle gObitToScoreTypeResourceBundle = null;
	private static ResourceBundle gObitToWeaponResourceBundle = null;

	// so cant instantiate class
	private UtilPack() {}
	public static String obitToScoreType(String obitKey)
	{
	  try
	{
	  if( gObitToScoreTypeResourceBundle == null )
	    {
	      gObitToScoreTypeResourceBundle = 
		ResourceBundle.getBundle("donaldp.util.ObitToScoreType");
	    }
	  return gObitToScoreTypeResourceBundle.getString(obitKey);
	}
	  catch(MissingResourceException mre) 
	{
	  return null;
	}
	}
	public static String obitToWeapon(String obitKey)
	{
	  try
	{
	  if( gObitToWeaponResourceBundle == null )
	    {
	      gObitToWeaponResourceBundle = 
		ResourceBundle.getBundle("donaldp.util.ObitToWeapon");
	    }
	  return gObitToWeaponResourceBundle.getString(obitKey);
	}
	  catch(MissingResourceException mre) 
	{
	  return null;
	}
	}
}