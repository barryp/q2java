package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * Form field label text. See the LABEL element definition in HTML 4.0.
 */
public interface HTMLLabelElement extends HTMLElement {
  /**
   * A single character access key to give access to the form control. See the 
   * accesskey attribute definition in HTML 4.0.
   */
  public String             getAccessKey();  
  /**
   * Returns the <code>FORM</code> element containing this control.Returns 
   * null if this control is not within the context of a form. 
   */
  public HTMLFormElement    getForm();  
  /**
   * This attribute links this label with another form controlby 
   * <code>id</code> attribute. See the for attribute definition in HTML 4.0.
   */
  public String             getHtmlFor();  
  public void               setAccessKey(String accessKey);  
  public void               setHtmlFor(String htmlFor);  
}