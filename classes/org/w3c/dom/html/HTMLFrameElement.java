package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * Create a frame. See the FRAME element definition in HTML 4.0.
 */
public interface HTMLFrameElement extends HTMLElement {
  /**
   * Request frame borders. See the frameborder attribute definition in HTML 
   * 4.0.
   */
  public String             getFrameBorder();  
  /**
   * URI designating a long description of this image or frame. See the 
   * longdesc attribute definition in HTML 4.0.
   */
  public String             getLongDesc();  
  /**
   * Frame margin height, in pixels. See the marginheight attribute definition 
   * in HTML 4.0.
   */
  public String             getMarginHeight();  
  /**
   * Frame margin width, in pixels. See the marginwidth attribute definition 
   * in HTML 4.0.
   */
  public String             getMarginWidth();  
  /**
   * The frame name (object of the <code>target</code> attribute). See the 
   * name attribute definition in HTML 4.0.
   */
  public String             getName();  
  /**
   * When true, forbid user from resizing frame. See the noresize attribute 
   * definition in HTML 4.0.
   */
  public boolean            getNoResize();  
  /**
   * Specify whether or not the frame should have scrollbars. See the 
   * scrolling attribute definition in HTML 4.0.
   */
  public String             getScrolling();  
  /**
   * A URI designating the initial frame contents. See the src attribute 
   * definition in HTML 4.0.
   */
  public String             getSrc();  
  public void               setFrameBorder(String frameBorder);  
  public void               setLongDesc(String longDesc);  
  public void               setMarginHeight(String marginHeight);  
  public void               setMarginWidth(String marginWidth);  
  public void               setName(String name);  
  public void               setNoResize(boolean noResize);  
  public void               setScrolling(String scrolling);  
  public void               setSrc(String src);  
}