/*****************************************************************************
 *                          J3D.org Copyright (c) 2000
 *                                Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

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
    public UnsupportedTypeException(String msg) {
        super(msg);
    }
}
