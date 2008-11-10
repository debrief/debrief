/*****************************************************************************
 *                          J3D.org Copyright (c) 2000
 *                                Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom;

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
     * Create an exception that contains a prefabricated message.
     *
     * @param requested The required size of the array
     * @param given The supplied array size
     */
    public InvalidArraySizeException(int requested, int given)
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
    public InvalidArraySizeException(String msg, int requested, int given)
    {
        super(msg + ". Required " + requested + " was given " + given);
    }
}
