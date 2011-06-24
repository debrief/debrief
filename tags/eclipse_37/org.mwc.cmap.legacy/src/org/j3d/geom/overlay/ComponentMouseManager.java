/*****************************************************************************
 *                        Teseract Software, LLP (c) 2001
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.overlay;

// Standard imports
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.event.EventListenerList;

// Application specific imports
// none

/**
 * Manager of mouse events to and from a component and sub component.
 * <p>
 *
 * @author Will Holcomb
 * @version $Revision: 1.1.1.1 $
 */
public class ComponentMouseManager
    implements MouseListener,
               MouseMotionListener
{

    /** List of listeners added for the sub component */
    private EventListenerList listenerList;

    /** The sub component we are forwarding events for */
    private ScreenComponent subComponent;

    /** Flag indicating the mouse if currently over the sub component */
    private boolean mouseOver = false;

    /** Flag indicating the mouse is currently being clicked */
    private boolean clicked = false;

    /**
     * Create a new component manager that looks after the given primary
     * component and a single sub-component.
     *
     * @param primary The main component that takes mouse events
     * @param sub The child component to pass events on for
     */
    public ComponentMouseManager(Component primary,
                                 ScreenComponent subComponent)
    {
        listenerList = new EventListenerList();
        primary.addMouseListener(this);
        primary.addMouseMotionListener(this);
        this.subComponent = subComponent;
    }

    /**
     * Process a mouse move event from the parent component.
     *
     * @param e The event that caused the listener to be called
     */
    public void mouseMoved(MouseEvent e)
    {
        if(subComponent.getBounds().contains(e.getPoint()))
        {
            if(!mouseOver)
            {
                mouseOver = true;
                fireMouseEntered(e);
            }

            fireMouseMoved(e);
        }
        else if(mouseOver)
        {

            mouseOver = false;
            fireMouseExited(e);
        }
    }

    /**
     * Process a mouse drag event from the parent component.
     *
     * @param e The event that caused the listener to be called
     */
    public void mouseDragged(MouseEvent e)
    {
        if(subComponent.getBounds().contains(e.getPoint()))
        {
            if(!mouseOver)
            {
                mouseOver = true;
                fireMouseEntered(e);
            }
            fireMouseDragged(e);
        }
        else if(mouseOver)
        {
            mouseOver = false;
            fireMouseExited(e);
        }
    }

    /**
     * Process a mouse enter event from the parent component.
     *
     * @param e The event that caused the listener to be called
     */
    public void mouseEntered(MouseEvent e)
    {
    }

    /**
     * Process a mouse exit event from the parent component.
     *
     * @param e The event that caused the listener to be called
     */
    public void mouseExited(MouseEvent e)
    {
        if(mouseOver)
        {
            mouseOver = false;
            fireMouseExited(e);
        }
    }

    /**
     * Process a mouse press event from the parent component.
     *
     * @param e The event that caused the listener to be called
     */
    public void mousePressed(MouseEvent e)
    {
        if(mouseOver)
        {
            clicked = true;
            fireMousePressed(e);
        }
    }

    /**
     * Process a mouse release event from the parent component.
     *
     * @param e The event that caused the listener to be called
     */
    public void mouseReleased(MouseEvent e)
    {
        if(mouseOver)
        {
            fireMouseReleased(e);
            if(clicked)
            {
                clicked = false;
                fireMouseClicked(e);
            }
        }
    }

    /**
     * Process a mouse click event from the parent component.
     *
     * @param e The event that caused the listener to be called
     */
    public void mouseClicked(MouseEvent e)
    {
    }

    /**
     * Add a new mouse listener for sub-component events.
     *
     * @param listener The new instance to be added
     */
    public void addMouseListener(MouseListener listener)
    {
        listenerList.add(MouseListener.class, listener);
    }

    /**
     * Add a new mouse motion listener for sub-component events.
     *
     * @param listener The new instance to be added
     */
    public void removeMouseListener(MouseListener listener)
    {
        listenerList.remove(MouseListener.class, listener);
    }

    /**
     * Remove a mouse listener for sub-component events.
     *
     * @param listener The new instance to be added
     */
    public void addMouseMotionListener(MouseMotionListener listener)
    {
        listenerList.add(MouseMotionListener.class, listener);
    }

    /**
     * Remove a mouse motion listener for sub-component events.
     *
     * @param listener The new instance to be added
     */
    public void removeMouseMotionListener(MouseMotionListener listener)
    {
        listenerList.remove(MouseMotionListener.class, listener);
    }

    /**
     * Convenience method to send a mouse move event to the child listeners.
     *
     * @param e The event to be sent
     */
    private void fireMouseMoved(MouseEvent e)
    {
        //e.translatePoint(-bounds.x, -bounds.y);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if(listeners[i] == MouseMotionListener.class)
            {
                ((MouseMotionListener)listeners[i + 1]).mouseMoved(e);
            }
        }
    }

    /**
     * Convenience method to send a mouse drag event to the child listeners.
     *
     * @param e The event to be sent
     */
    private void fireMouseDragged(MouseEvent e)
    {
        //e.translatePoint(-bounds.x, -bounds.y);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if(listeners[i] == MouseMotionListener.class)
            {
                ((MouseMotionListener)listeners[i + 1]).mouseDragged(e);
            }
        }
    }

    /**
     * Convenience method to send a mouse enter event to the child listeners.
     *
     * @param e The event to be sent
     */
    private void fireMouseEntered(MouseEvent e)
    {
        //e.translatePoint(-bounds.x, -bounds.y);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if(listeners[i] == MouseListener.class)
            {
                ((MouseListener)listeners[i + 1]).mouseEntered(e);
            }
        }
    }

    /**
     * Convenience method to send a mouse exit event to the child listeners.
     *
     * @param e The event to be sent
     */
    private void fireMouseExited(MouseEvent e)
    {
        //e.translatePoint(-bounds.x, -bounds.y);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if(listeners[i] == MouseListener.class)
            {
                ((MouseListener)listeners[i + 1]).mouseExited(e);
            }
        }
    }

    /**
     * Convenience method to send a mouse click event to the child listeners.
     *
     * @param e The event to be sent
     */
    private void fireMouseClicked(MouseEvent e)
    {
        //e.translatePoint(-bounds.x, -bounds.y);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if(listeners[i] == MouseListener.class)
            {
                ((MouseListener)listeners[i + 1]).mouseClicked(e);
            }
        }
    }

    /**
     * Convenience method to send a mouse press event to the child listeners.
     *
     * @param e The event to be sent
     */
    private void fireMousePressed(MouseEvent e)
    {
        //e.translatePoint(-bounds.x, -bounds.y);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if(listeners[i] == MouseListener.class)
            {
                ((MouseListener)listeners[i + 1]).mousePressed(e);
            }
        }
    }

    /**
     * Convenience method to send a mouse release event to the child listeners.
     *
     * @param e The event to be sent
     */
    private void fireMouseReleased(MouseEvent e)
    {
        //e.translatePoint(-bounds.x, -bounds.y);
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if(listeners[i] == MouseListener.class)
            {
                ((MouseListener)listeners[i + 1]).mouseReleased(e);
            }
        }
    }
}
