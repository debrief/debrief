/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
    public MutableInt(final int newval) {
        value = newval;
    }

    /**
     * Construct an object with the default value.
     */
    public MutableInt() {}
}