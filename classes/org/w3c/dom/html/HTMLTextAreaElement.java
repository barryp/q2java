package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * Multi-line text field. See the TEXTAREA element definition in HTML 4.0.
 */
public interface HTMLTextAreaElement extends HTMLElement {
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
   * Width of control (in characters). See the cols attribute definition in 
   * HTML 4.0.
   */
  public int                getCols();  
  /**
   * Stores the initial control value (i.e., the initial value of
   * <code>value</code>). 
   */
  public String             getDefaultValue();  
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
   * This control is read-only. See the readonly attribute definition in HTML 
   * 4.0.
   */
  public boolean            getReadOnly();  
  /**
   * Number of text rows. See the rows attribute definition in HTML 4.0.
   */
  public int                getRows();  
  /**
   * Index that represents the element's position in the tabbing order. See 
   * the tabindex attribute definition in HTML 4.0.
   */
  public int                getTabIndex();  
  /**
   * The type of this form control. 
   */
  public String             getType();  
  /**
   * The current textual content of the multi-line text field. If the entirety 
   * of the data can not fit into a single wstring, the implementation may 
   * truncate the data.
   */
  public String             getValue();  
  /**
   * Select the contents of the <code>TEXTAREA</code>.
   */
  public void               select();  
  public void               setAccessKey(String accessKey);  
  public void               setCols(int cols);  
  public void               setDefaultValue(String defaultValue);  
  public void               setDisabled(boolean disabled);  
  public void               setName(String name);  
  public void               setReadOnly(boolean readOnly);  
  public void               setRows(int rows);  
  public void               setTabIndex(int tabIndex);  
  public void               setValue(String value);  
}