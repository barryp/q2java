package org.w3c.dom.html;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 * The select element allows the selection of an option. The containedoptions 
 * can be directly accessed through the select element as acollection. See 
 * the SELECT element definition in HTML 4.0.
 */
public interface HTMLSelectElement extends HTMLElement {
  /**
   * Add a new element to the collection of <code>OPTION</code> elementsfor 
   * this <code>SELECT</code>.
   * @param element The element to add.
   * @param before The element to insert before, or NULL for the head of the 
   *   list.
   */
  public void               add(HTMLElement element, 
								HTMLElement before);
  /**
   * Removes keyboard focus from this element.
   */
  public void               blur();  
  /**
   * Gives keyboard focus to this element.
   */
  public void               focus();  
  /**
   * The control is unavailable in this context. See the disabled attribute 
   * definition in HTML 4.0.
   */
  public boolean            getDisabled();  
  /**
   * Returns the <code>FORM</code> element containing this control.Returns 
   * null if this control is not within the context of a form. 
   */
  public HTMLFormElement    getForm();  
  /**
   * The number of options in this <code>SELECT</code>. 
   */
  public int                getLength();  
  /**
   * If true, multiple <code>OPTION</code> elements may be selected in this 
   * <code>SELECT</code>. See the multiple attribute definition in HTML 4.0.
   */
  public boolean            getMultiple();  
  /**
   * Form control or object name when submitted with a form. See the name 
   * attribute definition in HTML 4.0.
   */
  public String             getName();  
  /**
   * The collection of <code>OPTION</code> elements contained by this element. 
   */
  public HTMLCollection     getOptions();  
  /**
   * The ordinal index of the selected option. The value -1 is returned ifno 
   * element is selected. If multiple options are selected, the index ofthe 
   * first selected option is returned. 
   */
  public int                getSelectedIndex();  
  /**
   * Number of visible rows. See the size attribute definition in HTML 4.0.
   */
  public int                getSize();  
  /**
   * Index that represents the element's position in the tabbing order. See 
   * the tabindex attribute definition in HTML 4.0.
   */
  public int                getTabIndex();  
  /**
   * The type of control created. 
   */
  public String             getType();  
  /**
   * The current form control value. 
   */
  public String             getValue();  
  /**
   * Remove an element from the collection of <code>OPTION</code> elementsfor 
   * this <code>SELECT</code>. Does nothing if no element has the givenindex.
   * @param index The index of the item to remove.
   */
  public void               remove(int index);  
  public void               setDisabled(boolean disabled);  
  public void               setMultiple(boolean multiple);  
  public void               setName(String name);  
  public void               setSelectedIndex(int selectedIndex);  
  public void               setSize(int size);  
  public void               setTabIndex(int tabIndex);  
  public void               setValue(String value);  
}