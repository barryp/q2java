package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * Push button. See the BUTTON element definition in HTML 4.0.
 */
public interface HTMLButtonElement extends HTMLElement {
  /**
   * A single character access key to give access to the form control. See the 
   * accesskey attribute definition in HTML 4.0.
   */
  public String             getAccessKey();  
  /**
   * The control is unavailable in this context. See the disabled attribute 
   * definition in HTML 4.0.
   */
  public boolean            getDisabled();  
  /**
   * Returns the <code>FORM</code> element containing this control.Returns 
   * null if this control is not within the context of a form. 
   */
  public HTMLFormElement    getForm();  
  /**
   * Form control or object name when submitted with a form. See the name 
   * attribute definition in HTML 4.0.
   */
  public String             getName();  
  /**
   * Index that represents the element's position in the tabbing order. See 
   * the tabindex attribute definition in HTML 4.0.
   */
  public int                getTabIndex();  
  /**
   * The type of button. See the type attribute definition in HTML 4.0.
   */
  public String             getType();  
  /**
   * The current form control value. See the value attribute definition in 
   * HTML 4.0.
   */
  public String             getValue();  
  public void               setAccessKey(String accessKey);  
  public void               setDisabled(boolean disabled);  
  public void               setName(String name);  
  public void               setTabIndex(int tabIndex);  
  public void               setValue(String value);  
}