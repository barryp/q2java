/*
 * Copyright (c) 1999 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

package org.w3c.dom.html;

/**
 * Organizes form controls into logical groups. See the  FIELDSET  element 
 * definition in HTML 4.0.
 */
public interface HTMLFieldSetElement extends HTMLElement {
  /**
   * Returns the <code>FORM</code> element containing this control. Returns 
   * <code>null</code> if this control is not within the context of a form. 
   */
  public HTMLFormElement    getForm();
}

