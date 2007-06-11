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

package org.j3d.loaders.ac3d.parser;


/**
 * <p><code>OutputHandler</code> provides a simple interface for exporting 
 * output to the real world.</p>
 *
 * @author  Ryan Wilhm (ryan@entrophica.com)
 * @version $Revision: 1.1.1.1 $
 */

public interface OutputHandler {
    
    /**
     * <p>This is what you call to write output to.</p>
     *
     * @param message The message to export.
     */
    
    public void println(String message);
}

