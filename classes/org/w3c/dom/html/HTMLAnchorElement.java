package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * The anchor element. See the A element definition in HTML 4.0.
 */
public interface HTMLAnchorElement extends HTMLElement {
  /**
   * Removes keyboard focus from this element.
   */
  public void               blur();  
  /**
   * Gives keyboard focus to this element.
   */
  public void               focus();  
  /**
   * A single character access key to give access to the form control. See the 
   * accesskey attribute definition in HTML 4.0.
   */
  public String             getAccessKey();  
  /**
   * The character encoding of the linked resource. See the charset attribute 
   * definition in HTML 4.0.
   */
  public String             getCharset();  
  /**
   * Comma-separated list of lengths, defining an active region geometry.See 
   * also <code>shape</code> for the shape of the region. See the coords 
   * attribute definition in HTML 4.0.
   */
  public String             getCoords();  
  /**
   * The URI of the linked resource. See the href attribute definition in HTML 
   * 4.0.
   */
  public String             getHref();  
  /**
   * Language code of the linked resource. See the hreflang attribute 
   * definition in HTML 4.0.
   */
  public String             getHreflang();  
  /**
   * Anchor name. See the name attribute definition in HTML 4.0.
   */
  public String             getName();  
  /**
   * Forward link type. See the rel attribute definition in HTML 4.0.
   */
  public String             getRel();  
  /**
   * Reverse link type. See the rev attribute definition in HTML 4.0.
   */
  public String             getRev();  
  /**
   * The shape of the active area. The coordinates are givenby 
   * <code>coords</code>. See the shape attribute definition in HTML 4.0.
   */
  public String             getShape();  
  /**
   * Index that represents the element's position in the tabbing order. See 
   * the tabindex attribute definition in HTML 4.0.
   */
  public int                getTabIndex();  
  /**
   * Frame to render the resource in. See the target attribute definition in 
   * HTML 4.0.
   */
  public String             getTarget();  
  /**
   * Advisory content type. See the type attribute definition in HTML 4.0.
   */
  public String             getType();  
  public void               setAccessKey(String accessKey);  
  public void               setCharset(String charset);  
  public void               setCoords(String coords);  
  public void               setHref(String href);  
  public void               setHreflang(String hreflang);  
  public void               setName(String name);  
  public void               setRel(String rel);  
  public void               setRev(String rev);  
  public void               setShape(String shape);  
  public void               setTabIndex(int tabIndex);  
  public void               setTarget(String target);  
  public void               setType(String type);  
}