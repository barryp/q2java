package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * Ordered list. See the OL element definition in HTML 4.0.
 */
public interface HTMLOListElement extends HTMLElement {
  /**
   * Reduce spacing between list items. See the compact attribute definition 
   * in HTML 4.0. This attribute is deprecated in HTML 4.0.
   */
  public boolean            getCompact();  
  /**
   * Starting sequence number. See the start attribute definition in HTML 4.0. 
   * This attribute is deprecated in HTML 4.0.
   */
  public int                getStart();  
  /**
   * Numbering style. See the type attribute definition in HTML 4.0. This 
   * attribute is deprecated in HTML 4.0.
   */
  public String             getType();  
  public void               setCompact(boolean compact);  
  public void               setStart(int start);  
  public void               setType(String type);  
}