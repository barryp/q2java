package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * List item. See the LI element definition in HTML 4.0.
 */
public interface HTMLLIElement extends HTMLElement {
  /**
   * List item bullet style. See the type attribute definition in HTML 4.0. 
   * This attribute is deprecated in HTML 4.0.
   */
  public String             getType();  
  /**
   * Reset sequence number when used in <code>OL</code> See the value 
   * attribute definition in HTML 4.0. This attribute is deprecated in HTML 
   * 4.0.
   */
  public int                getValue();  
  public void               setType(String type);  
  public void               setValue(int value);  
}