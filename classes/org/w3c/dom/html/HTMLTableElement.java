package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * The create* and delete* methods on the table allow authors to constructand 
 * modify tables. HTML 4.0 specifies that only one of each of the 
 * <code>CAPTION</code>, <code>THEAD</code>, and <code>TFOOT</code>elements 
 * may exist in a table. Therefore, if one exists, and thecreateTHead() or 
 * createTFoot() method is called, the method returnsthe existing THead or 
 * TFoot element. See the TABLE element definition in HTML 4.0.
 */
public interface HTMLTableElement extends HTMLElement {
  /**
   * Create a new table caption object or return an existing one.
   * @return A <code>CAPTION</code> element.
   */
  public HTMLElement        createCaption();  
  /**
   * Create a table footer row or return an existing one.
   * @return A footer element (<code>TFOOT</code>).
   */
  public HTMLElement        createTFoot();  
  /**
   * Create a table header row or return an existing one.
   * @return A new table header element (<code>THEAD</code>).
   */
  public HTMLElement        createTHead();  
  /**
   * Delete the table caption, if one exists.
   */
  public void               deleteCaption();  
  /**
   * Delete a table row.
   * @param index The index of the row to be deleted.
   */
  public void               deleteRow(int index);  
  /**
   * Delete the footer from the table, if one exists.
   */
  public void               deleteTFoot();  
  /**
   * Delete the header from the table, if one exists.
   */
  public void               deleteTHead();  
  /**
   * Specifies the table's position with respect to the rest of the document. 
   * See the align attribute definition in HTML 4.0. This attribute is 
   * deprecated in HTML 4.0.
   */
  public String             getAlign();  
  /**
   * Cell background color. See the bgcolor attribute definition in HTML 4.0. 
   * This attribute is deprecated in HTML 4.0.
   */
  public String             getBgColor();  
  /**
   * The width of the border around the table. See the border attribute 
   * definition in HTML 4.0.
   */
  public String             getBorder();  
  /**
   * Returns the table's <code>CAPTION</code>, or void if none exists. 
   */
  public HTMLTableCaptionElement getCaption();  
  /**
   * Specifies the horizontal and vertical space between cell content andcell 
   * borders. See the cellpadding attribute definition in HTML 4.0.
   */
  public String             getCellPadding();  
  /**
   * Specifies the horizontal and vertical separation between cells. See the 
   * cellspacing attribute definition in HTML 4.0.
   */
  public String             getCellSpacing();  
  /**
   * Specifies which external table borders to render. See the frame attribute 
   * definition in HTML 4.0.
   */
  public String             getFrame();  
  /**
   * Returns a collection of all the rows in the table, including all in 
   * <code>THEAD</code>, <code>TFOOT</code>, all <code>TBODY</code> elements. 
   */
  public HTMLCollection     getRows();  
  /**
   * Specifies which internal table borders to render. See the rules attribute 
   * definition in HTML 4.0.
   */
  public String             getRules();  
  /**
   * Supplementary description about the purpose or structureof a table. See 
   * the summary attribute definition in HTML 4.0.
   */
  public String             getSummary();  
  /**
   * Returns a collection of the defined table bodies. 
   */
  public HTMLCollection     getTBodies();  
  /**
   * Returns the table's <code>TFOOT</code>, or <code>null</code> if none 
   * exists. 
   */
  public HTMLTableSectionElement getTFoot();  
  /**
   * Returns the table's <code>THEAD</code>, or <code>null</code> if none 
   * exists. 
   */
  public HTMLTableSectionElement getTHead();  
  /**
   * Specifies the desired table width. See the width attribute definition in 
   * HTML 4.0.
   */
  public String             getWidth();  
  /**
   * Insert a new empty row in the table.Note. A table row cannot be empty
   * according to HTML 4.0 Recommendation. 
   * @param index The row number where to insert a new row.
   * @return The newly created row.
   */
  public HTMLElement        insertRow(int index);  
  public void               setAlign(String align);  
  public void               setBgColor(String bgColor);  
  public void               setBorder(String border);  
  public void               setCaption(HTMLTableCaptionElement caption);  
  public void               setCellPadding(String cellPadding);  
  public void               setCellSpacing(String cellSpacing);  
  public void               setFrame(String frame);  
  public void               setRules(String rules);  
  public void               setSummary(String summary);  
  public void               setTFoot(HTMLTableSectionElement tFoot);  
  public void               setTHead(HTMLTableSectionElement tHead);  
  public void               setWidth(String width);  
}