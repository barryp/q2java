package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * The <code>FORM</code> element encompasses behavior similar to acollection 
 * and an element. It provides direct access to the containedinput elements 
 * as well as the attributes of the form element. See the FORM element 
 * definition in HTML 4.0.
 */
public interface HTMLFormElement extends HTMLElement {
  /**
   * List of character sets supported by the server. See the accept-charset 
   * attribute definition in HTML 4.0.
   */
  public String             getAcceptCharset();  
  /**
   * Server-side form handler. See the action attribute definition in HTML 4.0.
   */
  public String             getAction();  
  /**
   * Returns a collection of all control elements in the form. 
   */
  public HTMLCollection     getElements();  
  /**
   * The content type of the submitted form, generally 
   * "application/x-www-form-urlencoded".  See the enctype attribute 
   * definition in HTML 4.0.
   */
  public String             getEnctype();  
  /**
   * The number of form controls in the form.
   */
  public int                getLength();  
  /**
   * HTTP method used to submit form. See the method attribute definition in 
   * HTML 4.0.
   */
  public String             getMethod();  
  /**
   * Names the form. 
   */
  public String             getName();  
  /**
   * Frame to render the resource in. See the target attribute definition in 
   * HTML 4.0.
   */
  public String             getTarget();  
  /**
   * Restores a form element's default values. It performs  the same action as 
   * a reset button.
   */
  public void               reset();  
  public void               setAcceptCharset(String acceptCharset);  
  public void               setAction(String action);  
  public void               setEnctype(String enctype);  
  public void               setMethod(String method);  
  public void               setName(String name);  
  public void               setTarget(String target);  
  /**
   * Submits the form. It performs the same action as a  submit button.
   */
  public void               submit();  
}