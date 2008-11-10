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
// $Source: i:/mwc/coag/asset/cvsroot/util/MWC/GUI/S57/support/MutableInt.java,v $
// $RCSfile: MutableInt.java,v $
// $Revision: 1.1 $
// $Date: 2007/04/27 09:20:02 $
// $Author: ian.mayo $
// 
// **********************************************************************

package MWC.GUI.S57.support;

/**
 * Implement a wrapper class to allow mutable ints.
 */
public class MutableInt {
    /** our value */
    public int value;

    /**
     * Construct a object with a value
     * 
     * @param newval our value
     */
    public MutableInt(int newval) {
        value = newval;
    }

    /**
     * Construct an object with the default value.
     */
    public MutableInt() {}
}