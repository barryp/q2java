// SAX default implementation for AttributeList.
// No warranty; no copyright -- use this as you will.
// $Id: AttrListImpl.java,v 1.1 2000/04/04 23:57:05 barryp Exp $


package org.openxml.parser;


import java.util.Vector;
import org.xml.sax.AttributeList;


/**
 * Convenience implementation for AttributeList.
 *
 * <p>This class provides a convenience implementation of the SAX
 * AttributeList class.  This implementation is useful both
 * for SAX parser writers, who can use it to provide attributes
 * to the application, and for SAX application writers, who can
 * use it to create a persistent copy of an element's attribute
 * specifications:</p>
 *
 * <pre>
 * private AttributeList myatts;
 *
 * public void startElement (String name, AttributeList atts)
 * {
 *              // create a persistent copy of the attribute list
 *              // for use outside this method
 *   myatts = new AttributeListImpl(atts);
 *   [...]
 * }
 * </pre>
 *
 * <p>Please note that SAX parsers are not required to use this
 * class to provide an implementation of AttributeList; it is
 * supplied only as an optional convenience.  In particular, 
 * parser writers are encouraged to invent more efficient
 * implementations.</p>
 *
 * @author David Megginson (ak117@freenet.carleton.ca)
 * @version 1.0
 * @see org.xml.sax.AttributeList
 * @see org.xml.sax.DocumentHandler#startElement
 */
final class AttrListImpl
    implements AttributeList
{

    
    public void addAttribute( String name, String type, String value )
    {
	if ( _attrCount == _attrList.length )
	{
	    String[] newList;

	    newList = new String[ _attrCount + 30 ];
	    System.arraycopy( _attrList, 0, newList, 0, _attrCount );
	    _attrList = newList;
	}
	
        _attrList[ _attrCount ] = name;
	_attrList[ _attrCount + 1 ] = type;
	_attrList[ _attrCount + 2 ] = value;
	_attrCount += 3;
    }


    public void clear ()
    {
        _attrCount = 0;
    }


    public int getLength()
    {
        return ( _attrCount / 3 );
    }


    public String getName( int index )
    {
	index = index * 3;
	if ( index < 0 || index >= _attrCount )
	    return null;
	return _attrList[ index ];
    }


    public String getType( int index )
    {
	index = index * 3;
	if ( index < 0 || index >= _attrCount )
	    return null;
	return _attrList[ index + 1 ];
    }


    public String getValue( int index )
    {
	index = index * 3;
	if ( index < 0 || index >= _attrCount )
	    return null;
	return _attrList[ index + 2 ];
    }


    public String getType( String name )
    {
	int           index;

	for ( index = 0 ; index < _attrCount ; index += 3 )
	    if ( _attrList[ index ].equals( name ) )
		return _attrList[ index + 1 ];
	return null;
    }


    public String getValue( String name )
    {
	int           index;

	for ( index = 0 ; index < _attrCount ; index += 3 )
	    if ( _attrList[ index ].equals( name ) )
		return _attrList[ index + 2 ];
	return null;
    }


    protected boolean exists( String name )
    {
	int           index;

	for ( index = 0 ; index < _attrCount ; index += 3 )
	    if ( _attrList[ index ].equals( name ) )
		return true;
	return false;
    }


    public AttrListImpl()
    {
    }


    private String[]  _attrList = new String[ 30 ];
    private int       _attrCount = 0;

    
}
