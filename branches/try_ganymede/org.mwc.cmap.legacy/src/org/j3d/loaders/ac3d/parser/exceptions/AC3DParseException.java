/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 * This software comes with the standard NO WARRANTY disclaimer for any
 * purpose. Use it at your own risk. If there's a problem you get to fix it.
 *
 ****************************************************************************/

package org.j3d.loaders.ac3d.parser.exceptions;


/**
 *
 * @author  Ryan Wilhm (ryan@entrophica.com)
 * @version $Revision: 1.1.1.1 $
 */

public class AC3DParseException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


		/**
     * Creates new <code>AC3DParseException</code> without detail message.
     */
    public AC3DParseException() {
    }


    /**
     * Constructs an <code>AC3DParseException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public AC3DParseException(String msg) {
        super(msg);
    }
}

