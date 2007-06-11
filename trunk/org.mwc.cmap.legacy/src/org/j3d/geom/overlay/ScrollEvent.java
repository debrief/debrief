/*****************************************************************************
 *                 Teseract Software, LLP Copyright (c) 2001
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.overlay;

// Standard imports
import java.util.EventObject;

// Application specific imports
// none

/**
 * An event describing a scroll action or request.
 * <p>
 *
 * @author Will Holcomb
 * @version $Revision: 1.1.1.1 $
 */
public class ScrollEvent extends EventObject
{
    /** The scroll type/direction is up */
    public final static int SCROLLED_UP = 0;

    /** The scroll type/direction is down */
    public final static int SCROLLED_DOWN = 1;

    /** The scroll type/direction is left */
    public final static int SCROLLED_LEFT = 2;

    /** The scroll type/direction is right */
    public final static int SCROLLED_RIGHT = 3;

    /** The item that has just been scrolled */
    private Object scrolledItem;

    /** The action that has taken place (up/down) */
    private int scrollType;

    /**
     * Create a new event instance that represents the collection of data.
     *
     * @param source The class that created the event
     * @param scrolledItem The item that was actually scrolled
     * @param scrollType the direction the scroll action has taken
     */
    public ScrollEvent(Object source, Object scrolledItem, int scrollType)
    {
        super(source);
        this.scrolledItem = scrolledItem;
        this.scrollType = scrollType;
    }

    /**
     * Get a reference to the item that has actually be scrolled, rather than
     * the class that generated the event
     *
     * @return The item that was scrolled
     */
    public Object getScrolledItem()
    {
        return scrolledItem;
    }

    /**
     * Get the direction flag for the scroll action.
     *
     * @return One of the constants defined in this class
     */
    public int getScrollType()
    {
        return scrollType;
    }
}
