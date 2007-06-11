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

package org.j3d.texture;


/**
 * An exception for when an attempt is made to set a default cache type when
 * one has already been set.
 *
 * @author  Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class CacheAlreadySetException extends Exception
{

    /**
     * Creates a new exception without detail message.
     */
    public CacheAlreadySetException()
    {
    }


    /**
     * Constructs an exception with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CacheAlreadySetException(String msg)
    {
        super(msg);
    }
}
