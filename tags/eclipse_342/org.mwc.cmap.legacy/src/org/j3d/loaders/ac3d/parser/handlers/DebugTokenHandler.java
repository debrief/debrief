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

package org.j3d.loaders.ac3d.parser.handlers;

import org.j3d.loaders.ac3d.models.*;
import org.j3d.loaders.ac3d.parser.exceptions.*;


/**
 *
 * @author  Ryan Wilhm (ryan@entrophica.com)
 * @version $Revision: 1.1.1.1 $
 */

public class DebugTokenHandler extends Ac3dTokenHandlerBase {


    public void token_MATERIAL(String[] tokens) throws AC3DParseException {
        super.token_MATERIAL(tokens);
        Ac3dMaterial m = (Ac3dMaterial)materials.elementAt(materialIndexPtr-1);
        debug("MATERIAL defined as: " + m.toString());
    }

    
    public void token_kids(String[] tokens) throws AC3DParseException {
        super.token_kids(tokens);
        Ac3dObject obj;        
        obj = (Ac3dObject)displayList.elementAt(displayList.size()-1);
        debug("OBJECT defined as: " + obj.toString());
    }
}
