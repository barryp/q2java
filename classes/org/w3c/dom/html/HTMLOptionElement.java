package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * A selectable choice. See the OPTION element definition in HTML 4.0.
 */
public interface HTMLOptionElement extends HTMLElement {
  /**
   * Stores the initial value of the <code>selected</code> attribute. 
   */
  public boolean            getDefaultSelected();  
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
   * The index of this <code>OPTION</code> in its parent <code>SELECT</code>. 
   */
  public int                getIndex();  
  /**
   * Option label for use in hierarchical menus. See the label attribute 
   * definition in HTML 4.0.
   */
  public String             getLabel();  
  /**
   * Means that this option is initially selected. See the selected attribute 
   * definition in HTML 4.0.
   */
  public boolean            getSelected();  
  /**
   * The text contained within the option element. 
   */
  public String             getText();  
  /**
   * The current form control value. See the value attribute definition in 
   * HTML 4.0.
   */
  public String             getValue();  
  public void               setDefaultSelected(boolean defaultSelected);  
  public void               setDisabled(boolean disabled);  
  public void               setIndex(int index);  
  public void               setLabel(String label);  
  public void               setValue(String value);  
}