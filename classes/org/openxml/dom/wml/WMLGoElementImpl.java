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

import org.openxml.wml.WMLGoElement;

public class WMLGoElementImpl extends WMLElementImpl implements WMLGoElement {

  public WMLGoElementImpl (DocumentImpl owner, String namespaceURI, String qualifiedName ) { 
    super( owner, namespaceURI, qualifiedName);
  }

  public void setSendreferer(String newValue) {
    setAttribute("sendreferer", newValue);
  }

  public String getSendreferer() {
    return getAttribute("sendreferer");
  }

  public void setAcceptCharset(String newValue) {
    setAttribute("accept-charset", newValue);
  }

  public String getAcceptCharset() {
    return getAttribute("accept-charset");
  }

  public void setHref(String newValue) {
    setAttribute("href", newValue);
  }

  public String getHref() {
    return getAttribute("href");
  }

  public void setClassName(String newValue) {
    setAttribute("class", newValue);
  }

  public String getClassName() {
    return getAttribute("class");
  }

  public void setId(String newValue) {
    setAttribute("id", newValue);
  }

  public String getId() {
    return getAttribute("id");
  }

  public void setMethod(String newValue) {
    setAttribute("method", newValue);
  }

  public String getMethod() {
    return getAttribute("method");
  }

}
