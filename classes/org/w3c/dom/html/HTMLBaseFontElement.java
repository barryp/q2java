package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * Base font. See the BASEFONT element definition in HTML 4.0. This element is 
 * deprecated in HTML 4.0.
 */
public interface HTMLBaseFontElement extends HTMLElement {
  /**
   * Font color. See the color attribute definition in HTML 4.0. This 
   * attribute is deprecated in HTML 4.0.
   */
  public String             getColor();  
  /**
   * Font face identifier. See the face attribute definition in HTML 4.0. This 
   * attribute is deprecated in HTML 4.0.
   */
  public String             getFace();  
  /**
   * Font size. See the size attribute definition in HTML 4.0. This attribute 
   * is deprecated in HTML 4.0.
   */
  public String             getSize();  
  public void               setColor(String color);  
  public void               setFace(String face);  
  public void               setSize(String size);  
}