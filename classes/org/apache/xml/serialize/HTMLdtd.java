/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */


package org.apache.xml.serialize;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Hashtable;


/**
 * Utility class for accessing information specific to HTML documents.
 * The HTML DTD is expressed as three utility function groups. Two methods
 * allow for checking whether an element requires an open tag on printing
 * ({@link #isEmptyTag}) or on parsing ({@link #isOptionalClosing}).
 * <P>
 * Two other methods translate character references from name to value and
 * from value to name. A small entities resource is loaded into memory the
 * first time any of these methods is called for fast and efficient access.
 *
 *
 * @version
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 */
final class HTMLdtd
{


    /**
     * Table of reverse character reference mapping. Character codes are held
     * as single-character strings, mapped to their reference name.
     */
    private static Hashtable        _byChar;


    /**
     * Table of entity name to value mapping. Entities are held as strings,
     * character references as <TT>Character</TT> objects.
     */
    private static Hashtable        _byName;


    private static Hashtable        _boolAttrs;


    /**
     * Holds element definitions.
     */
    private static Hashtable        _elemDefs;


    /**
     * Locates the HTML entities file that is loaded upon initialization.
     * This file is a resource loaded with the default class loader.
     */
    private static final String     ENTITIES_RESOURCE = "HTMLEntities.res";


    /**
     * Only opening tag should be printed.
     */
    private static final int ONLY_OPENING = 0x0001;

    /**
     * Element contains element content only.
     */
    private static final int ELEM_CONTENT = 0x0002;


    /**
     * Element preserve spaces.
     */
    private static final int PRESERVE     = 0x0004;


    /**
     * Optional closing tag.
     */
    private static final int OPT_CLOSING  = 0x0008;


    /**
     * Element is empty (also means only opening tag)
     */
    private static final int EMPTY        = 0x0010 | ONLY_OPENING;


    /**
     * Allowed to appear in head.
     */
    private static final int ALLOWED_HEAD = 0x0020;


    /**
     * When opened, closes P.
     */
    private static final int CLOSE_P      = 0x0040;


    /**
     * When opened, closes DD or DT.
     */
    private static final int CLOSE_DD_DT  = 0x0080;


    /**
     * When opened, closes itself.
     */
    private static final int CLOSE_SELF   = 0x0100;


    /**
     * When opened, closes another table section.
     */
    private static final int CLOSE_TABLE  = 0x0200;


    /**
     * When opened, closes TH or TD.
     */
    private static final int CLOSE_TH_TD  = 0x04000;


    /**
     * Returns true if element is declared to be empty. HTML elements are
     * defines as empty in the DTD, not by the document syntax.
     * 
     * @param tagName The element tag name (upper case)
     * @return True if element is empty
     */
    public static boolean isEmptyTag( String tagName )
    {
        return isElement( tagName, EMPTY );
    }


    /**
     * Returns true if element is declared to have element content.
     * Whitespaces appearing inside element content will be ignored,
     * other text will simply report an error.
     * 
     * @param tagName The element tag name (upper case)
     * @return True if element content
     */
    public static boolean isElementContent( String tagName )
    {
        return isElement( tagName, ELEM_CONTENT );
    }

    
    /**
     * Returns true if element's textual contents preserves spaces.
     * This only applies to PRE and TEXTAREA, all other HTML elements
     * do not preserve space.
     * 
     * @param tagName The element tag name (upper case)
     * @return True if element's text content preserves spaces
     */
    public static boolean isPreserveSpace( String tagName )
    {
        return isElement( tagName, PRESERVE );
    }


    /**
     * Returns true if element's closing tag is optional and need not
     * exist. An error will not be reported for such elements if they
     * are not closed. For example, <tt>LI</tt> is most often not closed.
     *
     * @param tagName The element tag name (upper case)
     * @return True if closing tag implied
     */
    public static boolean isOptionalClosing( String tagName )
    {
        return isElement( tagName, OPT_CLOSING );
    }


    /**
     * Returns true if element's closing tag is generally not printed.
     * For example, <tt>LI</tt> should not print the closing tag.
     *
     * @param tagName The element tag name (upper case)
     * @return True if only opening tag should be printed
     */
    public static boolean isOnlyOpening( String tagName )
    {
        return isElement( tagName, ONLY_OPENING );
    }


    /**
     * Returns true if the opening of one element (<tt>tagName</tt>) implies
     * the closing of another open element (<tt>openTag</tt>). For example,
     * every opening <tt>LI</tt> will close the previously open <tt>LI</tt>,
     * and every opening <tt>BODY</tt> will close the previously open <tt>HEAD</tt>.
     *
     * @param tagName The newly opened element
     * @param openTag The already opened element
     * @return True if closing tag closes opening tag
     */    
    public static boolean isClosing( String tagName, String openTag )
    {
        // Several elements are defined as closing the HEAD
        if ( openTag.equalsIgnoreCase( "HEAD" ) )
            return ! isElement( tagName, ALLOWED_HEAD );
        // P closes iteself
        if ( openTag.equalsIgnoreCase( "P" ) )
            return isElement( tagName, CLOSE_P );
        // DT closes DD, DD closes DT
        if ( openTag.equalsIgnoreCase( "DT" ) || openTag.equalsIgnoreCase( "DD" ) )
            return isElement( tagName, CLOSE_DD_DT );
        // LI and OPTION close themselves
        if ( openTag.equalsIgnoreCase( "LI" ) || openTag.equalsIgnoreCase( "OPTION" ) )
            return isElement( tagName, CLOSE_SELF );
        // Each of these table sections closes all the others
        if ( openTag.equalsIgnoreCase( "THEAD" ) || openTag.equalsIgnoreCase( "TFOOT" ) ||
             openTag.equalsIgnoreCase( "TBODY" ) || openTag.equalsIgnoreCase( "TR" ) || 
             openTag.equalsIgnoreCase( "COLGROUP" ) )
            return isElement( tagName, CLOSE_TABLE );
        // TD closes TH and TH closes TD
        if ( openTag.equalsIgnoreCase( "TH" ) || openTag.equalsIgnoreCase( "TD" ) )
            return isElement( tagName, CLOSE_TH_TD );
        return false;
    }
    
    
    /**
     * Returns true if the specified attribute it a URI and should be
     * escaped appropriately. In HTML URIs are escaped differently
     * than normal attributes.
     *
     * @param tagName The element's tag name
     * @param attrName The attribute's name
     */
    public static boolean isURI( String tagName, String attrName )
    {
        // Stupid checks.
        return ( attrName.equalsIgnoreCase( "href" ) || attrName.equalsIgnoreCase( "src" ) );
    }

        
    /**
     * Returns true if the specified attribute is a boolean and should be
     * printed without the value. This applies to attributes that are true
     * if they exist, such as selected (OPTION/INPUT). 
     *
     * @param tagName The element's tag name
     * @param attrName The attribute's name
     */
    public static boolean isBoolean( String tagName, String attrName )
    {
        String[] attrNames;
        
        attrNames = (String[]) _boolAttrs.get( tagName.toUpperCase() );
        if ( attrNames == null )
            return false;
        for ( int i = 0 ; i < attrNames.length ; ++i )
            if ( attrNames[ i ].equalsIgnoreCase( attrName ) )
                return true;
        return false;
    }


    /**
     * Returns the value of an HTML character reference by its name. If the
     * reference is not found or was not defined as a character reference,
     * returns EOF (-1).
     *
     * @param name Name of character reference
     * @return Character code or EOF (-1)
     */
    public static int charFromName( String name )
    {
        Object    value;
        
        initialize();
        value = _byName.get( name );
        if ( value != null && value instanceof Character )
            return ( (Character) value ).charValue();
        else
            return -1;
    }
    
    
    /**
     * Returns the name of an HTML character reference based on its character
     * value. Only valid for entities defined from character references. If no
     * such character value was defined, return null.
     *
     * @param value Character value of entity
     * @return Entity's name or null
     */
    public static String fromChar( char value )
    {
        String    name;
        
        initialize();
        name = (String) _byChar.get( String.valueOf( value ) );
        if ( name == null )
            return null;
        else
            return name;
    }
    

    /**
     * Initialize upon first access. Will load all the HTML character references
     * into a list that is accessible by name or character value and is optimized
     * for character substitution. This method may be called any number of times
     * but will execute only once.
     */
    private static void initialize()
    {
        InputStream     is = null;
        BufferedReader  reader = null;
        int             index;
        String          name;
        String          value;
        int             code;
        String          line;
        
        // Make sure not to initialize twice.
        if ( _byName != null )
            return;
        try {
            _byName = new Hashtable();
            _byChar = new Hashtable();
            is = HTMLdtd.class.getResourceAsStream( ENTITIES_RESOURCE );
            if ( is == null )
                throw new RuntimeException( "SER003 The resource [" + ENTITIES_RESOURCE + "] could not be found.\n" + ENTITIES_RESOURCE);
            reader = new BufferedReader( new InputStreamReader( is ) );
            line = reader.readLine();
            while ( line != null ) {
                if ( line.length() == 0 || line.charAt( 0 ) == '#' ) {
                    line = reader.readLine();
                    continue;
                }
                index = line.indexOf( ' ' );
                if ( index > 1 ) {
                    name = line.substring( 0, index );
                    ++index;
                    if ( index < line.length() ) {
                        value = line.substring( index );
                        index = value.indexOf( ' ' );
                        if ( index > 0 )
                            value = value.substring( 0, index );
                        code = Integer.parseInt( value );
                                        defineEntity( name, (char) code );
                    }
                }
                line = reader.readLine();
            }
            is.close();
        }  catch ( Exception except ) {
            throw new RuntimeException( "SER003 The resource [" + ENTITIES_RESOURCE + "] could not load: " +
                                        except.toString() + "\n" + ENTITIES_RESOURCE + "\t" + except.toString());
        } finally {
            if ( is != null ) {
                try {
                    is.close();
                } catch ( Exception except ) { }
            }
        }
    }
    

    /**
     * Defines a new character reference. The reference's name and value are
     * supplied. Nothing happens if the character reference is already defined.
     * <P>
     * Unlike internal entities, character references are a string to single
     * character mapping. They are used to map non-ASCII characters both on
     * parsing and printing, primarily for HTML documents. '&lt;amp;' is an
     * example of a character reference.
     *
     * @param name The entity's name
     * @param value The entity's value
     */
    private static void defineEntity( String name, char value )
    {
        if ( _byName.get( name ) == null ) {
            _byName.put( name, new Character( value ) );
            _byChar.put( String.valueOf( value ), name );
        }
    }


    private static void defineElement( String name, int flags )
    {
        _elemDefs.put( name, new Integer( flags ) );
    }
    
    
    private static void defineBoolean( String tagName, String attrName )
    {
        defineBoolean( tagName, new String[] { attrName } );
    }
    
    
    private static void defineBoolean( String tagName, String[] attrNames )
    {
        _boolAttrs.put( tagName, attrNames );
    }
    
    
    private static boolean isElement( String name, int flag )
    {
        Integer flags;
        
        flags = (Integer) _elemDefs.get( name.toUpperCase() );
        if ( flags == null )
            return false;
        else
            return ( ( flags.intValue() & flag ) != 0 );
    }
    
    
    static
    {
        _elemDefs = new Hashtable();
        defineElement( "ADDRESS", CLOSE_P );
        defineElement( "AREA", EMPTY );
        defineElement( "BASE",  EMPTY | ALLOWED_HEAD );
        defineElement( "BASEFONT", EMPTY );
        defineElement( "BLOCKQUOTE", CLOSE_P );
        defineElement( "BODY", OPT_CLOSING );
        defineElement( "BR", EMPTY );
        defineElement( "COL", EMPTY );
        defineElement( "COLGROUP", ELEM_CONTENT | OPT_CLOSING | CLOSE_TABLE );
        defineElement( "DD", OPT_CLOSING | ONLY_OPENING | CLOSE_DD_DT );
        defineElement( "DIV", CLOSE_P );
        defineElement( "DL", ELEM_CONTENT | CLOSE_P );
        defineElement( "DT", OPT_CLOSING | ONLY_OPENING | CLOSE_DD_DT );
        defineElement( "FIELDSET", CLOSE_P );
        defineElement( "FORM", CLOSE_P );
        defineElement( "FRAME", EMPTY | OPT_CLOSING );
        defineElement( "H1", CLOSE_P );
        defineElement( "H2", CLOSE_P );
        defineElement( "H3", CLOSE_P );
        defineElement( "H4", CLOSE_P );
        defineElement( "H5", CLOSE_P );
        defineElement( "H6", CLOSE_P );
        defineElement( "HEAD", ELEM_CONTENT | OPT_CLOSING );
        defineElement( "HR", EMPTY | CLOSE_P );
        defineElement( "HTML", ELEM_CONTENT | OPT_CLOSING );
        defineElement( "IMG", EMPTY );
        defineElement( "INPUT", EMPTY );
        defineElement( "ISINDEX", EMPTY | ALLOWED_HEAD );
        defineElement( "LI", OPT_CLOSING | ONLY_OPENING | CLOSE_SELF );
        defineElement( "LINK", EMPTY | ALLOWED_HEAD );
        defineElement( "MAP", EMPTY | ALLOWED_HEAD );
        defineElement( "META", EMPTY | ALLOWED_HEAD );
        defineElement( "OL", ELEM_CONTENT | CLOSE_P );
        defineElement( "OPTGROUP", ELEM_CONTENT );
        defineElement( "OPTION", OPT_CLOSING | ONLY_OPENING | CLOSE_SELF );
        defineElement( "P", OPT_CLOSING | CLOSE_P | CLOSE_SELF );
        defineElement( "PARAM", EMPTY );
        defineElement( "PRE", PRESERVE | CLOSE_P );
        defineElement( "SCRIPT", ALLOWED_HEAD | PRESERVE );
        defineElement( "NOSCRIPT", ALLOWED_HEAD | PRESERVE );
        defineElement( "SELECT", ELEM_CONTENT );
        defineElement( "STYLE", ALLOWED_HEAD | PRESERVE );
        defineElement( "TABLE", ELEM_CONTENT | CLOSE_P );
        defineElement( "TBODY", ELEM_CONTENT | OPT_CLOSING | CLOSE_TABLE );
        defineElement( "TD", OPT_CLOSING | CLOSE_TH_TD );
        defineElement( "TEXTAREA", PRESERVE );
        defineElement( "TFOOT", ELEM_CONTENT | OPT_CLOSING | CLOSE_TABLE );
        defineElement( "TH", OPT_CLOSING | CLOSE_TH_TD );
        defineElement( "THEAD", ELEM_CONTENT | OPT_CLOSING | CLOSE_TABLE );
        defineElement( "TITLE", ALLOWED_HEAD );
        defineElement( "TR", ELEM_CONTENT | OPT_CLOSING | CLOSE_TABLE );
        defineElement( "UL", ELEM_CONTENT | CLOSE_P );
        
        _boolAttrs = new Hashtable();
        defineBoolean( "AREA", "href" );
        defineBoolean( "BUTTON", "disabled" );
        defineBoolean( "DIR", "compact" );
        defineBoolean( "DL", "compact" );
        defineBoolean( "FRAME", "noresize" );
        defineBoolean( "HR", "noshade" );
        defineBoolean( "IMAGE", "ismap" );
        defineBoolean( "INPUT", new String[] { "defaultchecked", "checked", "readonly", "disabled" } );
        defineBoolean( "LINK", "link" );
        defineBoolean( "MENU", "compact" );
        defineBoolean( "OBJECT", "declare" );
        defineBoolean( "OL", "compact" );
        defineBoolean( "OPTGROUP", "disabled" );
        defineBoolean( "OPTION", new String[] { "default-selected", "selected", "disabled" } );
        defineBoolean( "SCRIPT", "defer" );
        defineBoolean( "SELECT", new String[] { "multiple", "disabled" } );
        defineBoolean( "STYLE", "disabled" );
        defineBoolean( "TD", "nowrap" );
        defineBoolean( "TH", "nowrap" );
        defineBoolean( "TEXTAREA", new String[] { "disabled", "readonly" } );
        defineBoolean( "UL", "compact" );
        
        initialize();
    }



}
