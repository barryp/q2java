package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * An embedded Java applet. See the APPLET element definition in HTML 4.0. 
 * This element is deprecated in HTML 4.0.
 */
public interface HTMLAppletElement extends HTMLElement {
  /**
   * Aligns this object (vertically or horizontally) with respect to its 
   * surrounding text. See the align attribute definition in HTML 4.0. This 
   * attribute is deprecated in HTML 4.0.
   */
  public String             getAlign();  
  /**
   * Alternate text for user agents not rendering the normal contentof this 
   * element. See the alt attribute definition in HTML 4.0. This attribute is 
   * deprecated in HTML 4.0.
   */
  public String             getAlt();  
  /**
   * Comma-separated archive list. See the archive attribute definition in 
   * HTML 4.0. This attribute is deprecated in HTML 4.0.
   */
  public String             getArchive();  
  /**
   * Applet class file.  See the code attribute definition in HTML 4.0. This 
   * attribute is deprecated in HTML 4.0.
   */
  public String             getCode();  
  /**
   * Optional base URI for applet. See the codebase attribute definition in 
   * HTML 4.0. This attribute is deprecated in HTML 4.0.
   */
  public String             getCodeBase();  
  /**
   * Override height. See the height attribute definition in HTML 4.0. This 
   * attribute is deprecated in HTML 4.0.
   */
  public String             getHeight();  
  /**
   * Horizontal space to the left and right of this image, applet, or object. 
   * See the hspace attribute definition in HTML 4.0. This attribute is 
   * deprecated in HTML 4.0.
   */
  public String             getHspace();  
  /**
   * The name of the applet. See the name attribute definition in HTML 4.0. 
   * This attribute is deprecated in HTML 4.0.
   */
  public String             getName();  
  /**
   * Serialized applet file. See the object attribute definition in HTML 4.0. 
   * This attribute is deprecated in HTML 4.0.
   */
  public String             getObject();  
  /**
   * Vertical space above and below this image, applet, or object. See the 
   * vspace attribute definition in HTML 4.0. This attribute is deprecated in 
   * HTML 4.0.
   */
  public String             getVspace();  
  /**
   * Override width. See the width attribute definition in HTML 4.0. This 
   * attribute is deprecated in HTML 4.0.
   */
  public String             getWidth();  
  public void               setAlign(String align);  
  public void               setAlt(String alt);  
  public void               setArchive(String archive);  
  public void               setCode(String code);  
  public void               setCodeBase(String codeBase);  
  public void               setHeight(String height);  
  public void               setHspace(String hspace);  
  public void               setName(String name);  
  public void               setObject(String object);  
  public void               setVspace(String vspace);  
  public void               setWidth(String width);  
}