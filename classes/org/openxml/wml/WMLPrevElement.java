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


/**
 * The interface is model after DOM1 Spec for HTML from W3C.
 * The DTD used in this DOM model is from http://www.wapforum.org/DTD/wml_1.1.xml
 */

package org.openxml.wml;

public interface WMLPrevElement extends WMLElement {

  public void setClassName(String newValue);

  public String getClassName();

  public void setId(String newValue);

  public String getId();

}
