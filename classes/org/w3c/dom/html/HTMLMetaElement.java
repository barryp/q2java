package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * This contains generic meta-information about the document. See the META 
 * element definition in HTML 4.0.
 */
public interface HTMLMetaElement extends HTMLElement {
  /**
   * Associated information. See the content attribute definition in HTML 4.0.
   */
  public String             getContent();  
  /**
   * HTTP response header name. See the http-equiv attribute definition in 
   * HTML 4.0.
   */
  public String             getHttpEquiv();  
  /**
   * Meta information name. See the name attribute definition in HTML 4.0.
   */
  public String             getName();  
  /**
   * Select form of content. See the scheme attribute definition in HTML 4.0.
   */
  public String             getScheme();  
  public void               setContent(String content);  
  public void               setHttpEquiv(String httpEquiv);  
  public void               setName(String name);  
  public void               setScheme(String scheme);  
}