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

import org.openxml.wml.WMLMetaElement;

public class WMLMetaElementImpl extends WMLElementImpl implements WMLMetaElement {

  public WMLMetaElementImpl (DocumentImpl owner, String namespaceURI, String qualifiedName ) { 
    super( owner, namespaceURI, qualifiedName);
  }

  public void setForua(String newValue) {
    setAttribute("forua", newValue);
  }

  public String getForua() {
    return getAttribute("forua");
  }

  public void setScheme(String newValue) {
    setAttribute("scheme", newValue);
  }

  public String getScheme() {
    return getAttribute("scheme");
  }

  public void setClassName(String newValue) {
    setAttribute("class", newValue);
  }

  public String getClassName() {
    return getAttribute("class");
  }

  public void setHttpEquiv(String newValue) {
    setAttribute("http-equiv", newValue);
  }

  public String getHttpEquiv() {
    return getAttribute("http-equiv");
  }

  public void setId(String newValue) {
    setAttribute("id", newValue);
  }

  public String getId() {
    return getAttribute("id");
  }

  public void setContent(String newValue) {
    setAttribute("content", newValue);
  }

  public String getContent() {
    return getAttribute("content");
  }

  public void setName(String newValue) {
    setAttribute("name", newValue);
  }

  public String getName() {
    return getAttribute("name");
  }

}
