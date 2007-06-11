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

package org.j3d.util;

// Standard imports
import java.util.LinkedList;

// Application specific imports
// None

/**
 * Simple 'First In First Out' (FIFO) queue. Backend is implemented
 * with a Collections LinkedList.
 * <P>
 *
 * This simple queue does not block if you request an item. If nothing is
 * in the queue then it just returns you a null value.
 * <P>
 *
 * Taken from the VLC common code library
 * <A HREF="http://www.vlc.com.au/common/">http://www.vlc.com.au/common/</A>
 * This softare is released under the
 * <A HREF="http://www.gnu.org/copyleft/lgpl.html">GNU LGPL</A>
 * <P>
 *
 * @author Justin Couch.
 * @version $Revision: 1.1.1.1 $
 * @see java.util.LinkedList
 */
public class Queue
{

    /** linked list queue */
    private LinkedList m_queue;

    /**
     * Constructor. Create a simple queue.
     */
    public Queue()
    {
        m_queue = new LinkedList();
    }

    /**
     * Add an element to the end of the queue.
     *
     * @param o Element to add.
     */
    public void add(Object o)
    {
        m_queue.addLast(o);
    }

    /**
     * Return the next element from the front and remove it from the queue.
     *
     * @return element at the from of the queue, or null if empty.
     */
    public Object getNext()
    {
        if(hasNext()) {
            return m_queue.removeFirst();
        }
        return null;
    }

    /**
     * Return the next element from the front of the queue.
     *
     * @return element at the from of the queue, or null if empty.
     */
    public Object peekNext()
    {
        if(hasNext())
            return m_queue.getFirst();

        return null;
    }

    /**
     * Check if queue has more elements.
     *
     * @return true if queue has more elements.
     */
    public boolean hasNext()
    {
        return (m_queue.size() > 0);
    }

    /**
     * Remove the given item from the list. If it is not in the queue it will
     * silently ignore the request.
     *
     * @param obj The object to remove
     */
    public void remove(Object obj)
    {
        m_queue.remove(obj);
    }

    /**
     * Return the number of elements in the queue.
     *
     * @return size of queue.
     */
    public int size()
    {
        return m_queue.size();
    }

    /**
     * Remove all elements in the queue.
     */
    public void clear()
    {
        m_queue.clear();
    }
}
