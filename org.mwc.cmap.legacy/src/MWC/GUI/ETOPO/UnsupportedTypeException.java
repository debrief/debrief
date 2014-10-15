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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */

package MWC.GUI.ETOPO;

// Standard imports
// none

// Application specific imports
// none

/**
 * Exception for when one of the requested geometry generation types is not
 * known or understood.
 * <P>
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class UnsupportedTypeException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/**
     * Create a blank exception with no message
     */
    public UnsupportedTypeException() {
    }

    /**
     * Create an exception that contains the given message.
     *
     * @param msg The message to associate with this exception
     */
    public UnsupportedTypeException(final String msg) {
        super(msg);
    }
}
