package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * Parameters fed to the <code>OBJECT</code> element. See the PARAM element 
 * definition in HTML 4.0.
 */
public interface HTMLParamElement extends HTMLElement {
  /**
   * The name of a run-time parameter. See the name attribute definition in 
   * HTML 4.0.
   */
  public String             getName();  
  /**
   * Content type for the <code>value</code> attribute when
   * <code>valuetype</code> has the value "ref". See the type attribute 
   * definition in HTML 4.0.
   */
  public String             getType();  
  /**
   * The value of a run-time parameter. See the value attribute definition in 
   * HTML 4.0.
   */
  public String             getValue();  
  /**
   * Information about the meaning of the <code>value</code> attributevalue. 
   * See the valuetype attribute definition in HTML 4.0.
   */
  public String             getValueType();  
  public void               setName(String name);  
  public void               setType(String type);  
  public void               setValue(String value);  
  public void               setValueType(String valueType);  
}