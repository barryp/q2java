/**
 * The contents of this file are subject to the OpenXML Public
 * License Version 1.0; you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.openxml.org/license.html
 *
 * THIS SOFTWARE IS DISTRIBUTED ON AN "AS IS" BASIS WITHOUT WARRANTY
 * OF ANY KIND, EITHER EXPRESSED OR IMPLIED. THE INITIAL DEVELOPER
 * AND ALL CONTRIBUTORS SHALL NOT BE LIABLE FOR ANY DAMAGES AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. SEE THE LICENSE FOR THE SPECIFIC LANGUAGE GOVERNING
 * RIGHTS AND LIMITATIONS UNDER THE LICENSE.
 *
 * The Initial Developer of this code under the License is David Li (david@topware.com.tw)
 * Portions created by David Li are Copyright (C) 1999.
 * All Rights Reserved.
 */

package org.openxml.dom.wml;

import org.openxml.dom.DocumentImpl;

import org.openxml.wml.WMLInputElement;

public class WMLInputElementImpl extends WMLElementImpl implements WMLInputElement {

  public WMLInputElementImpl (DocumentImpl owner, String namespaceURI, String qualifiedName ) { 
    super( owner, namespaceURI, qualifiedName);
  }

  public void setSize(String newValue) {
    setAttribute("size", newValue);
  }

  public String getSize() {
    return getAttribute("size");
  }

  public void setFormat(String newValue) {
    setAttribute("format", newValue);
  }

  public String getFormat() {
    return getAttribute("format");
  }

  public void setValue(String newValue) {
    setAttribute("value", newValue);
  }

  public String getValue() {
    return getAttribute("value");
  }

  public void setMaxlength(String newValue) {
    setAttribute("maxlength", newValue);
  }

  public String getMaxlength() {
    return getAttribute("maxlength");
  }

  public void setTabindex(String newValue) {
    setAttribute("tabindex", newValue);
  }

  public String getTabindex() {
    return getAttribute("tabindex");
  }

  public void setClassName(String newValue) {
    setAttribute("class", newValue);
  }

  public String getClassName() {
    return getAttribute("class");
  }

  public void setXmlLang(String newValue) {
    setAttribute("xml:lang", newValue);
  }

  public String getXmlLang() {
    return getAttribute("xml:lang");
  }

  public void setEmptyok(String newValue) {
    setAttribute("emptyok", newValue);
  }

  public String getEmptyok() {
    return getAttribute("emptyok");
  }

  public void setTitle(String newValue) {
    setAttribute("title", newValue);
  }

  public String getTitle() {
    return getAttribute("title");
  }

  public void setId(String newValue) {
    setAttribute("id", newValue);
  }

  public String getId() {
    return getAttribute("id");
  }

  public void setType(String newValue) {
    setAttribute("type", newValue);
  }

  public String getType() {
    return getAttribute("type");
  }

  public void setName(String newValue) {
    setAttribute("name", newValue);
  }

  public String getName() {
    return getAttribute("name");
  }

}
