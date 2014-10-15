/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

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

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		/** the invalid character that we found */
    final public char c;

    /**
     * Construct an object with no detail message
     * 
     * @param val the character encountered
     */
    public InvalidCharException(final char val) {
        super();
        c = val;
    }

    /**
     * Construct an object with a detail message
     * 
     * @param s the detail message
     * @param val the character encountered
     */
    public InvalidCharException(final String s, final char val) {
        super(s);
        c = val;
    }
}