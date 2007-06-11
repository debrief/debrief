// **********************************************************************
// 
// <copyright>
// 
//  BBN Technologies
//  10 Moulton Street
//  Cambridge, MA 02138
//  (617) 873-8000
// 
//  Copyright (C) BBNT Solutions LLC. All rights reserved.
// 
// </copyright>
// **********************************************************************
// 
// $Source: i:/mwc/coag/asset/cvsroot/util/MWC/GUI/S57/support/InvalidCharException.java,v $
// $RCSfile: InvalidCharException.java,v $
// $Revision: 1.1 $
// $Date: 2007/04/27 09:20:01 $
// $Author: ian.mayo $
// 
// **********************************************************************

package MWC.GUI.S57.support;

/**
 * An invalid character occured on in input stream.
 */
public class InvalidCharException extends FormatException {

    /** the invalid character that we found */
    final public char c;

    /**
     * Construct an object with no detail message
     * 
     * @param val the character encountered
     */
    public InvalidCharException(char val) {
        super();
        c = val;
    }

    /**
     * Construct an object with a detail message
     * 
     * @param s the detail message
     * @param val the character encountered
     */
    public InvalidCharException(String s, char val) {
        super(s);
        c = val;
    }
}