package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * Embedded image. See the IMG element definition in HTML 4.0.
 */
public interface HTMLImageElement extends HTMLElement {
  /**
   * Aligns this object (vertically or horizontally) with respect to its 
   * surrounding text. See the align attribute definition in HTML 4.0. This 
   * attribute is deprecated in HTML 4.0.
   */
  public String             getAlign();  
  /**
   * Alternate text for user agents not rendering the normal contentof this 
   * element. See the alt attribute definition in HTML 4.0.
   */
  public String             getAlt();  
  /**
   * Width of border around image. See the border attribute definition in HTML 
   * 4.0. This attribute is deprecated in HTML 4.0.
   */
  public String             getBorder();  
  /**
   * Override height. See the height attribute definition in HTML 4.0.
   */
  public String             getHeight();  
  /**
   * Horizontal space to the left and right of this image. See the hspace 
   * attribute definition in HTML 4.0. This attribute is deprecated in HTML 
   * 4.0.
   */
  public String             getHspace();  
  /**
   * Use server-side image map. See the ismap attribute definition in HTML 4.0.
   */
  public boolean            getIsMap();  
  /**
   * URI designating a long description of this image or frame. See the 
   * longdesc attribute definition in HTML 4.0.
   */
  public String             getLongDesc();  
  /**
   * URI designating the source of this image, for low-resolution output. 
   */
  public String             getLowSrc();  
  /**
   * The name of the element (for backwards compatibility). 
   */
  public String             getName();  
  /**
   * URI designating the source of this image. See the src attribute definition
   *  in HTML 4.0.
   */
  public String             getSrc();  
  /**
   * Use client-side image map. See the usemap attribute definition in HTML 
   * 4.0.
   */
  public String             getUseMap();  
  /**
   * Vertical space above and below this image. See the vspace attribute 
   * definition in HTML 4.0. This attribute is deprecated in HTML 4.0.
   */
  public String             getVspace();  
  /**
   * Override width. See the width attribute definition in HTML 4.0.
   */
  public String             getWidth();  
  public void               setAlign(String align);  
  public void               setAlt(String alt);  
  public void               setBorder(String border);  
  public void               setHeight(String height);  
  public void               setHspace(String hspace);  
  public void               setIsMap(boolean isMap);  
  public void               setLongDesc(String longDesc);  
  public void               setLowSrc(String lowSrc);  
  public void               setName(String name);  
  public void               setSrc(String src);  
  public void               setUseMap(String useMap);  
  public void               setVspace(String vspace);  
  public void               setWidth(String width);  
}