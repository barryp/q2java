// AttributesImpl.java - default implementation of Attributes.
// Written by David Megginson, sax@megginson.com
// NO WARRANTY!  This class is in the public domain.

// $Id: AttributesImpl.java,v 1.1 2000/04/04 23:57:09 barryp Exp $


package org.xml.sax.helpers;

import org.xml.sax.Attributes;


/**
 * Default implementation of the Attributes interface.
 *
 * <blockquote>
 * <em>This module, both source code and documentation, is in the
 * Public Domain, and comes with <strong>NO WARRANTY</strong>.</em>
 * </blockquote>
 *
 * <p>This class provides a default implementation of the SAX2
 * Attributes interface, with the addition of manipulators
 * so that the list can be modified or reused.</p>
 *
 * <p>There are two typical uses of this class:</p>
 *
 * <ol>
 * <li>to take a persistent snapshot of an Attributes object
 *  in a ContentHandler.startElement event; or</li>
 * <li>to construct or modify an Attributes object in a SAX2
 *  XMLReader or filter.</li>
 * </ol>
 *
 * <p>This class replaces the now-deprecated SAX1 AttributeListImpl
 * class; in addition to supporting the updated Attributes
 * interface rather than the deprecated AttributeList interface,
 * it also includes a much more efficient implementation using
 * arrays rather than Vector.</p>
 *
 * @since SAX 2.0
 * @author David Megginson, 
 *         <a href="mailto:sax@megginson.com">sax@megginson.com</a>
 * @version 2.0beta
 */
public class AttributesImpl implements Attributes
{


    ////////////////////////////////////////////////////////////////////
    // Constructors.
    ////////////////////////////////////////////////////////////////////


    /**
     * Construct a new, empty AttributesImpl object.
     */
    public AttributesImpl ()
    {
	length = 0;
	data = null;
    }


    /**
     * Copy an existing Attributes object.
     *
     * <p>This constructor is especially useful inside a start
     * element event.</p>
     *
     * @param atts The existing Attributes object.
     */
    public AttributesImpl (Attributes atts)
    {
	length = atts.getLength();
	data = new String[length*5]; 
	for (int i = 0; i < length; i++) {
	    data[i*5] = atts.getURI(i);
	    data[i*5+1] = atts.getLocalName(i);
	    data[i*5+2] = atts.getRawName(i);
	    data[i*5+3] = atts.getType(i);
	    data[i*5+4] = atts.getValue(i);
	}
    }



    ////////////////////////////////////////////////////////////////////
    // Implementation of org.xml.sax.Attributes.
    ////////////////////////////////////////////////////////////////////


    /**
     * Return the number of attributes in the list.
     *
     * @return The number of attributes in the list.
     * @see org.xml.sax.Attributes#getLength
     */
    public int getLength ()
    {
	return length;
    }


    /**
     * Return an attribute's Namespace URI.
     *
     * @param index The attribute's index (zero-based).
     * @return The Namespace URI or the empty string if none is
     *         available.
     * @see org.xml.sax.Attributes#getURI
     */
    public String getURI (int index)
    {
	if (index >= 0 && index < length) {
	    return data[index*5];
	} else {
	    return null;
	}
    }


    /**
     * Return an attribute's local name.
     *
     * @param index The attribute's index (zero-based).
     * @return The attribute's local name or the empty string if 
     *         none is available.
     * @see org.xml.sax.Attributes#getLocalName
     */
    public String getLocalName (int index)
    {
	if (index >= 0 && index < length) {
	    return data[index*5+1];
	} else {
	    return null;
	}
    }


    /**
     * Return an attribute's raw XML 1.0 name.
     *
     * @param index The attribute's index (zero-based).
     * @return The attribute's raw name or the empty string if 
     *         none is available.
     * @see org.xml.sax.Attributes#getRawName
     */
    public String getRawName (int index)
    {
	if (index >= 0 && index < length) {
	    return data[index*5+2];
	} else {
	    return null;
	}
    }


    /**
     * Return an attribute's type by index.
     *
     * @param index The attribute's index (zero-based).
     * @return The attribute's type.
     * @see org.xml.sax.Attributes#getType(int)
     */
    public String getType (int index)
    {
	if (index >= 0 && index < length) {
	    return data[index*5+3];
	} else {
	    return null;
	}
    }


    /**
     * Return an attribute's value by index.
     *
     * @param index The attribute's index (zero-based).
     * @return The attribute's value.
     * @see org.xml.sax.Attributes#getValue(int)
     */
    public String getValue (int index)
    {
	if (index >= 0 && index < length) {
	    return data[index*5+4];
	} else {
	    return null;
	}
    }


    /**
     * Look up an attribute's index by Namespace name.
     *
     * @param uri The attribute's Namespace URI, or the empty
     *        string if none is available.
     * @param localName The attribute's local name.
     * @return The attribute's index, or -1 if none matches.
     * @see org.xml.sax.Attributes#getIndex(java.lang.String,java.lang.String)
     */
    public int getIndex (String uri, String localName)
    {
	int max = length * 5;
	for (int i = 0; i < max; i += 5) {
	    if (data[i].equals(uri) && data[i+1].equals(localName)) {
		return i / 5;
	    }
	} 
	return -1;
    }


    /**
     * Look up an attribute's index by raw XML 1.0 name.
     *
     * @param rawName The raw XML 1.0 (prefixed) name.
     * @return The attribute's index, or -1 if none matches.
     * @see org.xml.sax.Attributes#getIndex(java.lang.String)
     */
    public int getIndex (String rawName)
    {
	int max = length * 5;
	for (int i = 0; i < max; i += 5) {
	    if (data[i+2].equals(rawName)) {
		return i / 5;
	    }
	} 
	return -1;
    }


    /**
     * Look up an attribute's type by Namespace-qualified name.
     *
     * @param uri The Namespace URI, or the empty string for a name
     *        with no explicit Namespace URI.
     * @param localName The local name.
     * @return The attribute's type, or null if there is no
     *         matching attribute.
     * @see org.xml.sax.Attributes#getType(java.lang.String,java.lang.String)
     */
    public String getType (String uri, String localName)
    {
	int max = length * 5;
	for (int i = 0; i < max; i += 5) {
	    if (data[i].equals(uri) && data[i+1].equals(localName)) {
		return data[i+3];
	    }
	} 
	return null;
    }


    /**
     * Look up an attribute's type by raw XML 1.0 name.
     *
     * @param rawName The raw XML 1.0 name.
     * @return The attribute's type, or null if there is no
     *         matching attribute.
     * @see org.xml.sax.Attributes#getType(java.lang.String)
     */
    public String getType (String rawName)
    {
	int max = length * 5;
	for (int i = 0; i < max; i += 5) {
	    if (data[i+2].equals(rawName)) {
		return data[i+3];
	    }
	}
	return null;
    }


    /**
     * Look up an attribute's value by Namespace-qualified name.
     *
     * @param uri The Namespace URI, or the empty string for a name
     *        with no explicit Namespace URI.
     * @param localName The local name.
     * @return The attribute's value, or null if there is no
     *         matching attribute.
     * @see org.xml.sax.Attributes#getValue(java.lang.String,java.lang.String)
     */
    public String getValue (String uri, String localName)
    {
	int max = length * 5;
	for (int i = 0; i < max; i += 5) {
	    if (data[i].equals(uri) && data[i+1].equals(localName)) {
		return data[i+4];
	    }
	}
	return null;
    }


    /**
     * Look up an attribute's value by raw XML 1.0 name.
     *
     * @param rawName The raw XML 1.0 name.
     * @return The attribute's value, or null if there is no
     *         matching attribute.
     * @see org.xml.sax.Attributes#getValue(java.lang.String)
     */
    public String getValue (String rawName)
    {
	int max = length * 5;
	for (int i = 0; i < max; i += 5) {
	    if (data[i+2].equals(rawName)) {
		return data[i+4];
	    }
	}
	return null;
    }



    ////////////////////////////////////////////////////////////////////
    // Manipulators.
    ////////////////////////////////////////////////////////////////////


    /**
     * Clear the attribute list for reuse.
     *
     * <p>Note that no memory is actually freed by this call:
     * the current arrays are kept so that they can be 
     * reused.</p>
     */
    public void clear ()
    {
	length = 0;
    }


    /**
     * Add an attribute to the end of the list.
     *
     * <p>For the sake of speed, this method does no checking
     * to see if the attribute is already in the list: that is
     * the responsibility of the application.</p>
     *
     * @param uri The Namespace URI, or the empty string if
     *        none is available or Namespace processing is not
     *        being performed.
     * @param localName The local name, or the empty string if
     *        Namespace processing is not being performed.
     * @param rawName The raw XML 1.0 name, or the empty string
     *        if raw names are not available.
     * @param type The attribute type as a string.
     * @param value The attribute value.
     */
    public void addAttribute (String uri, String localName, String rawName,
			      String type, String value)
    {
	ensureCapacity(length+1);
	data[length*5] = uri;
	data[length*5+1] = localName;
	data[length*5+2] = rawName;
	data[length*5+3] = type;
	data[length*5+4] = value;
	length++;
    }


    /**
     * Set an attribute in the list.
     *
     * <p>For the sake of speed, this method does no checking
     * for name conflicts or well-formedness: such checks are the
     * responsibility of the application.</p>
     *
     * @param index The index of the attribute (zero-based).
     * @param uri The Namespace URI, or the empty string if
     *        none is available or Namespace processing is not
     *        being performed.
     * @param localName The local name, or the empty string if
     *        Namespace processing is not being performed.
     * @param rawName The raw XML 1.0 name, or the empty string
     *        if raw names are not available.
     * @param type The attribute type as a string.
     * @param value The attribute value.
     * @exception java.lang.ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setAttribute (int index, String uri, String localName,
			      String rawName, String type, String value)
    {
	if (index >= 0 && index < length) {
	    data[index*5] = uri;
	    data[index*5+1] = localName;
	    data[index*5+2] = rawName;
	    data[index*5+3] = type;
	    data[index*5+4] = value;
	} else {
	    badIndex(index);
	}
    }


    /**
     * Remove an attribute from the list.
     *
     * @param index The index of the attribute (zero-based).
     * @exception java.lang.ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void removeAttribute (int index)
    {
	if (index >= 0 && index < length) {
	    System.arraycopy(data, (index+1)*5, data, index*5,
			     (length-index)*5);
	    length--;
	} else {
	    badIndex(index);
	}
    }


    /**
     * Set the Namespace URI of a specific attribute.
     *
     * @param index The index of the attribute (zero-based).
     * @param uri The attribute's Namespace URI, or the empty
     *        string for none.
     * @exception java.lang.ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setURI (int index, String uri)
    {
	if (index >= 0 && index < length) {
	    data[index*5] = uri;
	} else {
	    badIndex(index);
	}
    }


    /**
     * Set the local name of a specific attribute.
     *
     * @param index The index of the attribute (zero-based).
     * @param localName The attribute's local name, or the empty
     *        string for none.
     * @exception java.lang.ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setLocalName (int index, String localName)
    {
	if (index >= 0 && index < length) {
	    data[index*5+1] = localName;
	} else {
	    badIndex(index);
	}
    }


    /**
     * Set the raw XML 1.0 name of a specific attribute.
     *
     * @param index The index of the attribute (zero-based).
     * @param rawName The attribute's raw name, or the empty
     *        string for none.
     * @exception java.lang.ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setRawName (int index, String rawName)
    {
	if (index >= 0 && index < length) {
	    data[index*5+2] = rawName;
	} else {
	    badIndex(index);
	}
    }


    /**
     * Set the type of a specific attribute.
     *
     * @param index The index of the attribute (zero-based).
     * @param type The attribute's type.
     * @exception java.lang.ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setType (int index, String type)
    {
	if (index >= 0 && index < length) {
	    data[index*5+3] = type;
	} else {
	    badIndex(index);
	}
    }


    /**
     * Set the value of a specific attribute.
     *
     * @param index The index of the attribute (zero-based).
     * @param value The attribute's value.
     * @exception java.lang.ArrayIndexOutOfBoundsException When the
     *            supplied index does not point to an attribute
     *            in the list.
     */
    public void setValue (int index, String value)
    {
	if (index >= 0 && index < length) {
	    data[index*5+4] = value;
	} else {
	    badIndex(index);
	}
    }



    ////////////////////////////////////////////////////////////////////
    // Internal methods.
    ////////////////////////////////////////////////////////////////////


    /**
     * Ensure the internal array's capacity.
     *
     * @param n The minimum number of attributes that the array must
     *        be able to hold.
     */
    private void ensureCapacity (int n)
    {
	if (n > 0 && data == null) {
	    data = new String[25];
	}

	int max = data.length;
	if (max >= n * 5) {
	    return;
	}


	while (max < n * 5) {
	    max *= 2;
	}
	String newData[] = new String[max];
	System.arraycopy(data, 0, newData, 0, length*5);
	data = newData;
    }


    /**
     * Report a bad array index in a manipulator.
     *
     * @param index The index to report.
     * @exception java.lang.ArrayIndexOutOfBoundsException Always.
     */
    private void badIndex (int index)
	throws ArrayIndexOutOfBoundsException
    {
	String msg =
	    "Attempt to reference attribute at illegal index: " + index;
	throw new ArrayIndexOutOfBoundsException(msg);
    }



    ////////////////////////////////////////////////////////////////////
    // Internal state.
    ////////////////////////////////////////////////////////////////////

    int length;
    String data [];

}

// end of AttributesImpl.java
