package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * Regroups the <code>COL</code> and <code>COLGROUP</code> elements. See the 
 * COL element definition in HTML 4.0.
 */
public interface HTMLTableColElement extends HTMLElement {
  /**
   * Horizontal alignment of cell data in column. See the align attribute 
   * definition in HTML 4.0.
   */
  public String             getAlign();  
  /**
   * Alignment character for cells in a column. See the char attribute 
   * definition in HTML 4.0.
   */
  public String             getCh();  
  /**
   * Offset of alignment character. See the charoff attribute definition in 
   * HTML 4.0.
   */
  public String             getChOff();  
  /**
   * Indicates the number of columns in a group or affected by a grouping. See 
   * the span attribute definition in HTML 4.0.
   */
  public int                getSpan();  
  /**
   * Vertical alignment of cell data in column. See the valign attribute 
   * definition in HTML 4.0.
   */
  public String             getVAlign();  
  /**
   * Default column width. See the width attribute definition in HTML 4.0.
   */
  public String             getWidth();  
  public void               setAlign(String align);  
  public void               setCh(String ch);  
  public void               setChOff(String chOff);  
  public void               setSpan(int span);  
  public void               setVAlign(String vAlign);  
  public void               setWidth(String width);  
}