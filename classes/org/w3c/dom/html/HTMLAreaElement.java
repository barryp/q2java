package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * Client-side image map area definition. See the AREA element definition in 
 * HTML 4.0.
 */
public interface HTMLAreaElement extends HTMLElement {
  /**
   * A single character access key to give access to the form control. See the 
   * accesskey attribute definition in HTML 4.0.
   */
  public String             getAccessKey();  
  /**
   * Alternate text for user agents not rendering the normal contentof this 
   * element. See the alt attribute definition in HTML 4.0.
   */
  public String             getAlt();  
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
   * Specifies that this area is inactive, i.e., has no associated action. See 
   * the nohref attribute definition in HTML 4.0.
   */
  public boolean            getNoHref();  
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
  public void               setAccessKey(String accessKey);  
  public void               setAlt(String alt);  
  public void               setCoords(String coords);  
  public void               setHref(String href);  
  public void               setNoHref(boolean noHref);  
  public void               setShape(String shape);  
  public void               setTabIndex(int tabIndex);  
  public void               setTarget(String target);  
}