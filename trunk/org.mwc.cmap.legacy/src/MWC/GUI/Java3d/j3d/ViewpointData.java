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


package MWC.GUI.Java3d.j3d;

// Standard imports
import javax.media.j3d.TransformGroup;

/**
 * A class to represent viewpoint information so that we can put it on screen,
 * move around to it etc etc.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class ViewpointData
{
    /** A string representing a name of the viewpoint. For screen display */
    public String name;

    /** An identifier used to know which view data this is */
    public int id;

    /** The transform group above the view */
    public TransformGroup viewTg;

    /** Flag indicating this is the currently selected viewpoint */
    public boolean selected = false;

    /**  User specific data they can associate with this viewpoint */
    public Object userData;

    /**
     * Create a new data object initialised to the set of values.
     *
     * @param name The name to use
     * @param id The id of this viewpoint
     * @param tg The transformgroup for the view
     */
    public ViewpointData(String name, int id, TransformGroup tg)
    {
        this.name = name;
        this.id = id;
        this.viewTg = tg;
    }

    /**
     * Return a string representation of this viewpoint. Just returns the
     * name string.
     *
     * @return The name of this viewpoint
     */
    public String toString()
    {
        return name;
    }
}
