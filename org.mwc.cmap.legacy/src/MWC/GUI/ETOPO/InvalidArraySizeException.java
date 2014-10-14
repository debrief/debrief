/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package MWC.GUI.ETOPO;

// Standard imports
// none

// Application specific imports
// none

/**
 * Exception for when one of the requested geometry generation arrays is not
 * big enough to contain the data requested.
 * <P>
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class InvalidArraySizeException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/**
     * Create an exception that contains a prefabricated message.
     *
     * @param requested The required size of the array
     * @param given The supplied array size
     */
    public InvalidArraySizeException(final int requested, final int given)
    {
        super("Required " + requested + " was given " + given);
    }

    /**
     * Create an exception that contains the given message plus a
     * pre-fabricated part. Typically the base message is just the array
     * that does not have the right size.
     *
     * @param msg The base message to use
     * @param requested The required size of the array
     * @param given The supplied array size
     */
    public InvalidArraySizeException(final String msg, final int requested, final int given)
    {
        super(msg + ". Required " + requested + " was given " + given);
    }
}
