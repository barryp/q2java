package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * The <code>THEAD</code>, <code>TFOOT</code>, and <code>TBODY</code>elements. 
 */
public interface HTMLTableSectionElement extends HTMLElement {
  /**
   * Delete a row from this section.
   * @param index The index of the row to be deleted.
   */
  public void               deleteRow(int index);  
  /**
   * Horizontal alignment of data in cells. See the <code>align</code>
   * attribute for HTMLTheadElement for details. 
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
   * The collection of rows in this table section. 
   */
  public HTMLCollection     getRows();  
  /**
   * Vertical alignment of data in cells.See the <code>valign</code>attribute 
   * for HTMLTheadElement for details. 
   */
  public String             getVAlign();  
  /**
   * Insert a row into this section.
   * @param index The row number where to insert a new row.
   * @return The newly created row.
   */
  public HTMLElement        insertRow(int index);  
  public void               setAlign(String align);  
  public void               setCh(String ch);  
  public void               setChOff(String chOff);  
  public void               setVAlign(String vAlign);  
}