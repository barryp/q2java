package org.w3c.dom.fi;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

import org.w3c.dom.*;

/**
 */
public interface NodeFilter {
  /**
   *  
   * @param n The node to check to see if it passes the filter or not.
   * @return  TRUE if a this node is to be passed through the filter and 
   *   returned by the <code>NodeIterator::nextNode()</code> method, FALSE if 
   *   this node is to be ignored.&lt;/ 
   */
  public boolean            acceptNode(Node n);  
}