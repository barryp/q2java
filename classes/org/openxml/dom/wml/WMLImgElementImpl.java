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

import org.openxml.wml.WMLImgElement;

public class WMLImgElementImpl extends WMLElementImpl implements WMLImgElement {

  public WMLImgElementImpl (DocumentImpl owner, String namespaceURI, String qualifiedName ) { 
    super( owner, namespaceURI, qualifiedName);
  }

  public void setWidth(String newValue) {
    setAttribute("width", newValue);
  }

  public String getWidth() {
    return getAttribute("width");
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

  public void setLocalsrc(String newValue) {
    setAttribute("localsrc", newValue);
  }

  public String getLocalsrc() {
    return getAttribute("localsrc");
  }

  public void setHeight(String newValue) {
    setAttribute("height", newValue);
  }

  public String getHeight() {
    return getAttribute("height");
  }

  public void setAlign(String newValue) {
    setAttribute("align", newValue);
  }

  public String getAlign() {
    return getAttribute("align");
  }

  public void setVspace(String newValue) {
    setAttribute("vspace", newValue);
  }

  public String getVspace() {
    return getAttribute("vspace");
  }

  public void setAlt(String newValue) {
    setAttribute("alt", newValue);
  }

  public String getAlt() {
    return getAttribute("alt");
  }

  public void setId(String newValue) {
    setAttribute("id", newValue);
  }

  public String getId() {
    return getAttribute("id");
  }

  public void setHspace(String newValue) {
    setAttribute("hspace", newValue);
  }

  public String getHspace() {
    return getAttribute("hspace");
  }

  public void setSrc(String newValue) {
    setAttribute("src", newValue);
  }

  public String getSrc() {
    return getAttribute("src");
  }

}
