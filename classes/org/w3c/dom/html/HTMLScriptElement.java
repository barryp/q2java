package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * Script statements. See the SCRIPT element definition in HTML 4.0.
 */
public interface HTMLScriptElement extends HTMLElement {
  /**
   * The character encoding of the linked resource. See the charset attribute 
   * definition in HTML 4.0.
   */
  public String             getCharset();  
  /**
   * Indicates that the user agent can defer processing of the script.  See 
   * the defer attribute definition in HTML 4.0.
   */
  public boolean            getDefer();  
  /**
   * Reserved for future use. 
   */
  public String             getEvent();  
  /**
   * Reserved for future use. 
   */
  public String             getHtmlFor();  
  /**
   * URI designating an external script. See the src attribute definition in 
   * HTML 4.0.
   */
  public String             getSrc();  
  /**
   * The script content of the element. 
   */
  public String             getText();  
  /**
   * The content type of the script language. See the type attribute definition
   *  in HTML 4.0.
   */
  public String             getType();  
  public void               setCharset(String charset);  
  public void               setDefer(boolean defer);  
  public void               setEvent(String event);  
  public void               setHtmlFor(String htmlFor);  
  public void               setSrc(String src);  
  public void               setText(String text);  
  public void               setType(String type);  
}