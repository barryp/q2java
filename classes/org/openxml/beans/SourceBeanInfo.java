package org.openxml.beans;

import java.awt.*;
import java.beans.*;


public class SourceBeanInfo
	extends SimpleBeanInfo
{

	
	private final Class     _beanClass = SourceBean.class;
	

	public SourceBeanInfo()
	{
	}
	/**
	 * Gets a BeanInfo for the superclass of this bean.
	 
	 * @return BeanInfo[] containing this bean's superclass BeanInfo
	 */
	public BeanInfo[] getAdditionalBeanInfo()
	{
	    BeanInfo[]  info;
	    
		try
		{
			info = new BeanInfo[ 1 ];
			info[0] = Introspector.getBeanInfo( _beanClass.getSuperclass() );
			return info;
		}
		catch ( IntrospectionException except )
		{
			throw new Error( except.toString() );
		}
	}
	/**
	* Gets the BeanDescriptor for this bean.
	
	* @return an object of type BeanDescriptor
	* @see java.beans.BeanDescriptor
	*/
	public BeanDescriptor getBeanDescriptor()
	{
		BeanDescriptor descript;
		
		descript = new BeanDescriptor( _beanClass );
		return descript;
	}
	/**
	 * Gets an image that may be used to visually represent this bean
	 * (in the toolbar, on a form, etc).
	 
	 * @param iconKind the type of icon desired, one of: BeanInfo.ICON_MONO_16x16,
	 * BeanInfo.ICON_COLOR_16x16, BeanInfo.ICON_MONO_32x32, or BeanInfo.ICON_COLOR_32x32.
	 * @return an image for this bean
	 * @see BeanInfo#ICON_MONO_16x16
	 * @see BeanInfo#ICON_COLOR_16x16
	 * @see BeanInfo#ICON_MONO_32x32
	 * @see BeanInfo#ICON_COLOR_32x32
	 */
	public Image getIcon( int nIconKind )
	{
//	    if ( nIconKind == BeanInfo.ICON_COLOR_16x16 )
//            return loadImage( "DSC00001.JPG" );
		return null;
	}
	/**
	 * Returns descriptions of this bean's properties.
	 */
	public PropertyDescriptor[] getPropertyDescriptors()
	{
	    PropertyDescriptor[]    descript;
	    
		try
		{
		    descript = new PropertyDescriptor[ 6 ];
		    descript[ 0 ] = new PropertyDescriptor( "URI", _beanClass, "getURI", "setURI");
		    descript[ 0 ].setBound( true );
		    descript[ 1 ] = new PropertyDescriptor( "PublicID", _beanClass, "getPublicID", "setPublicID");
		    descript[ 1 ].setBound( true );
		    descript[ 2 ] = new PropertyDescriptor( "Encoding", _beanClass, "getEncoding", "setEncoding");
		    descript[ 2 ].setBound( true );
		    descript[ 3 ] = new PropertyDescriptor( "Read-Only", _beanClass, "getReadOnly", "setReadOnly");
		    descript[ 3 ].setBound( true );
		    descript[ 4 ] = new PropertyDescriptor( "Document Class", _beanClass, "getDocClassName", "getDocClassName");
		    descript[ 4 ].setBound( true );
		   	descript[ 5 ] = new PropertyDescriptor( "Last Exception", _beanClass, "getLastError", null );
			return descript;
		}
		catch ( IntrospectionException except )
		{
			throw new Error( except.toString() );
		}
	}
}