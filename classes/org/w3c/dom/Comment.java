package org.w3c.dom;

/*
 * Copyright (c) 1998 World Wide Web Consortium, (Massachusetts Institute of
 * Technology, Institut National de Recherche en Informatique et en
 * Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 */

/**
 * This represents the content of a comment, i.e., all the characters between 
 * the starting '<code>&lt;!--</code>' and ending '<code>--&gt;</code>'. Note 
 * that this is the definition of a comment in XML, and, in practice, HTML, 
 * although some HTML tools may implement the full SGML comment structure.
 */
public interface Comment extends CharacterData {
}