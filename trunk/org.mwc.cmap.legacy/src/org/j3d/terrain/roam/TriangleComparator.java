/*****************************************************************************
 *                      Modified version (c) j3d.org 2002
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.terrain.roam;

// Standard imports
import java.util.Comparator;

// Application specific imports
// none

/**
 * Comparator for dealing with triangle tree nodes.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
class TriangleComparator implements Comparator
{
    //----------------------------------------------------------
    // Methods defined by Comparator
    //----------------------------------------------------------

    /**
     * Check to see if this comparator is the same as another comparator.
     * Only return true if is is another instance of this same class.
     *
     * @param o The object to check
     * @return true if the other object is also a TriangleComparator
     */
    public boolean equals(Object o)
    {
        return (o instanceof TriangleComparator);
    }

    /**
     * Compare this object to the passed object.
     *
     * @param The object to be compared
     * @return a negative integer, zero, or a positive integer as this object
     *    is less than, equal to, or greater than the specified object
     */
    public int compare(Object o1, Object o2)
    {
        TreeNode t1 = (TreeNode)o1;
        TreeNode t2 = (TreeNode)o2;

        return (int)(t1.diamondVariance - t2.diamondVariance);
    }
}

