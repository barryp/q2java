package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * Generic embedded object. Note. In principle, allproperties on the object 
 * element are read-write but in someenvironments some properties may be 
 * read-only once the underlyingobject is instantiated. See the OBJECT 
 * element definition in HTML 4.0.
 */
public interface HTMLObjectElement extends HTMLElement {
  /**
   * Aligns this object (vertically or horizontally) with respect to its 
   * surrounding text. See the align attribute definition in HTML 4.0. This 
   * attribute is deprecated in HTML 4.0.
   */
  public String             getAlign();  
  /**
   * Space-separated list of archives. See the archive attribute definition in 
   * HTML 4.0.
   */
  public String             getArchive();  
  /**
   * Width of border around the object. See the border attribute definition in 
   * HTML 4.0. This attribute is deprecated in HTML 4.0.
   */
  public String             getBorder();  
  /**
   * Applet class file. See the <code>code</code> attribute for
   * HTMLAppletElement. 
   */
  public String             getCode();  
  /**
   * Base URI for <code>classid</code>, <code>data</code>, and
   * <code>archive</code> attributes. See the codebase attribute definition 
   * in HTML 4.0.
   */
  public String             getCodeBase();  
  /**
   * Content type for data downloaded via <code>classid</code> attribute. See 
   * the codetype attribute definition in HTML 4.0.
   */
  public String             getCodeType();  
  /**
   * A URI specifying the location of the object's data.  See the data 
   * attribute definition in HTML 4.0.
   */
  public String             getData();  
  /**
   * Declare (for future reference), but do not instantiate, thisobject. See 
   * the declare attribute definition in HTML 4.0.
   */
  public boolean            getDeclare();  
  /**
   * Returns the <code>FORM</code> element containing this control.Returns 
   * null if this control is not within the context of a form. 
   */
  public HTMLFormElement    getForm();  
  /**
   * Override height. See the height attribute definition in HTML 4.0.
   */
  public String             getHeight();  
  /**
   * Horizontal space to the left and right of this image, applet, or object. 
   * See the hspace attribute definition in HTML 4.0. This attribute is 
   * deprecated in HTML 4.0.
   */
  public String             getHspace();  
  /**
   * Form control or object name when submitted with a form. See the name 
   * attribute definition in HTML 4.0.
   */
  public String             getName();  
  /**
   * Message to render while loading the object. See the standby attribute 
   * definition in HTML 4.0.
   */
  public String             getStandby();  
  /**
   * Index that represents the element's position in the tabbing order. See 
   * the tabindex attribute definition in HTML 4.0.
   */
  public int                getTabIndex();  
  /**
   * Content type for data downloaded via <code>data</code> attribute. See the 
   * type attribute definition in HTML 4.0.
   */
  public String             getType();  
  /**
   * Use client-side image map. See the usemap attribute definition in HTML 
   * 4.0.
   */
  public String             getUseMap();  
  /**
   * Vertical space above and below this image, applet, or object. See the 
   * vspace attribute definition in HTML 4.0. This attribute is deprecated in 
   * HTML 4.0.
   */
  public String             getVspace();  
  /**
   * Override width. See the width attribute definition in HTML 4.0.
   */
  public String             getWidth();  
  public void               setAlign(String align);  
  public void               setArchive(String archive);  
  public void               setBorder(String border);  
  public void               setCode(String code);  
  public void               setCodeBase(String codeBase);  
  public void               setCodeType(String codeType);  
  public void               setData(String data);  
  public void               setDeclare(boolean declare);  
  public void               setHeight(String height);  
  public void               setHspace(String hspace);  
  public void               setName(String name);  
  public void               setStandby(String standby);  
  public void               setTabIndex(int tabIndex);  
  public void               setType(String type);  
  public void               setUseMap(String useMap);  
  public void               setVspace(String vspace);  
  public void               setWidth(String width);  
}