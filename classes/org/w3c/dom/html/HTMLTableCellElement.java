package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * The object used to represent the <code>TH</code> and <code>TD</code>
 * elements. See the TD element definition in HTML 4.0.
 */
public interface HTMLTableCellElement extends HTMLElement {
  /**
   * Abbreviation for header cells. See the abbr attribute definition in HTML 
   * 4.0.
   */
  public String             getAbbr();  
  /**
   * Horizontal alignment of data in cell. See the align attribute definition 
   * in HTML 4.0.
   */
  public String             getAlign();  
  /**
   * Names group of related headers. See the axis attribute definition in HTML 
   * 4.0.
   */
  public String             getAxis();  
  /**
   * Cell background color. See the bgcolor attribute definition in HTML 4.0. 
   * This attribute is deprecated in HTML 4.0.
   */
  public String             getBgColor();  
  /**
   * The index of this cell in the row. 
   */
  public int                getCellIndex();  
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
   * Number of columns spanned by cell. See the colspan attribute definition 
   * in HTML 4.0.
   */
  public int                getColSpan();  
  /**
   * List of <code>id</code> attribute values for header cells. See the 
   * headers attribute definition in HTML 4.0.
   */
  public String             getHeaders();  
  /**
   * Cell height. See the height attribute definition in HTML 4.0. This 
   * attribute is deprecated in HTML 4.0.
   */
  public String             getHeight();  
  /**
   * Suppress word wrapping. See the nowrap attribute definition in HTML 4.0. 
   * This attribute is deprecated in HTML 4.0.
   */
  public boolean            getNoWrap();  
  /**
   * Number of rows spanned by cell. See the rowspan attribute definition in 
   * HTML 4.0.
   */
  public int                getRowSpan();  
  /**
   * Scope covered by header cells. See the scope attribute definition in HTML 
   * 4.0.
   */
  public String             getScope();  
  /**
   * Vertical alignment of data in cell. See the valign attribute definition 
   * in HTML 4.0.
   */
  public String             getVAlign();  
  /**
   * Cell width. See the width attribute definition in HTML 4.0. This 
   * attribute is deprecated in HTML 4.0.
   */
  public String             getWidth();  
  public void               setAbbr(String abbr);  
  public void               setAlign(String align);  
  public void               setAxis(String axis);  
  public void               setBgColor(String bgColor);  
  public void               setCellIndex(int cellIndex);  
  public void               setCh(String ch);  
  public void               setChOff(String chOff);  
  public void               setColSpan(int colSpan);  
  public void               setHeaders(String headers);  
  public void               setHeight(String height);  
  public void               setNoWrap(boolean noWrap);  
  public void               setRowSpan(int rowSpan);  
  public void               setScope(String scope);  
  public void               setVAlign(String vAlign);  
  public void               setWidth(String width);  
}