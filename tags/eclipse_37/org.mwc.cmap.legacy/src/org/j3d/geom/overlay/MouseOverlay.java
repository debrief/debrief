/*****************************************************************************
 *                        J3D.org Copyright (c) 2001
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.overlay;

// Standard imports
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.vecmath.Point3d;

// Application specific imports
// none

/**
 * An overlay that is used like an ordinary drawing canvas that interacts with
 * the mouse.
 * <P>
 *
 * This class registers a behavior with the given scenegraph that listens for
 * all mouse events. You can use the constructor to filter for motion and
 * ordinary mouse events and turn these on and off at will.
 * <P>
 *
 * Mouse overlays will generate events for the entire canvas regardless of
 * whether the user sets the canvas to a smaller value. Ideally, the user
 * should find the canvas, ask for the size of it and then use that to set
 * the width and height of the underlying texture. Remember that texture
 * ccordinates are changed to a power of two, so texture drawing coordinates
 * probably will not match the mouse screen coordinates. You should use the
 * two provided coordinate convertors to swap between the two systems if you
 * wish for the drawn items to match the actual mouse locations.
 * <P>
 *
 * <B>Note:</B> This class does not call the <CODE>repaint()</CODE> method
 * after each mouse event. It is the responsibility of the derived class to
 * make sure the screen gets updated with any information regarding the mouse
 * event.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class MouseOverlay extends OverlayBase
{
    /** The criteria for the current event requirements */
    WakeupOnAWTEvent critter;

    /**
     * Flag describing whether we should pass all events through, or just the
     * last event received.
     */
    boolean processAllEvents;

    /**
     * Inner class that provides the mouse handling behaviour required by this
     * class. We use an inner class so that the user only need extend this class
     */
    private class EventBehavior extends Behavior
    {
        public EventBehavior()
        {
            Point3d origin = new Point3d();
            BoundingSphere bounds =
                new BoundingSphere(origin, Double.POSITIVE_INFINITY);

            setSchedulingBounds(bounds);
            setEnable(true);
        }

        public void initialize()
        {
            wakeupOn(critter);
        }

        @SuppressWarnings("rawtypes")
				public void processStimulus(Enumeration criteria)
        {
            WakeupOnAWTEvent input = (WakeupOnAWTEvent)criteria.nextElement();
            AWTEvent[] events = input.getAWTEvent();

            if(processAllEvents)
            {
                for(int i = 0; i < events.length; i++)
                    sendEvent((MouseEvent)events[i]);
            }
            else
            {
                sendEvent((MouseEvent)events[events.length - 1]);
            }

            wakeupOn(critter);
        }

        /**
         * Send one event to the derived class.
         *
         * @param evt The event to be sent
         */
        private void sendEvent(MouseEvent evt)
        {
            try
            {
                switch(evt.getID())
                {
                    case MouseEvent.MOUSE_CLICKED:
                        mouseClicked(evt);
                        break;
                    case MouseEvent.MOUSE_PRESSED:
                        mousePressed(evt);
                        break;
                    case MouseEvent.MOUSE_RELEASED:
                        mouseReleased(evt);
                        break;
                    case MouseEvent.MOUSE_ENTERED:
                        mouseEntered(evt);
                        break;
                    case MouseEvent.MOUSE_EXITED:
                        mouseExited(evt);
                        break;
                    case MouseEvent.MOUSE_DRAGGED:
                        mouseDragged(evt);
                        break;
                    case MouseEvent.MOUSE_MOVED:
                        mouseMoved(evt);
                        break;
                }
            }
            catch(Exception e)
            {
                System.out.println("Error sending mouse event to overlay");
                e.printStackTrace();
            }
        }
    }

    //------------------------------------------------------------------------
    // Class proper
    //------------------------------------------------------------------------

    /**
     * Creates a new overlay covering the given canvas bounds. It has two
     * buffers. Updates are managed automatically. This Overlay is not usable
     * until you attach it to the view platform transform.
     *
     * @param canvas3D Canvas being drawn onto
     * @param bounds Bounds on the canvas covered by the overlay
     */
    public MouseOverlay(Canvas3D canvas3D, Rectangle bounds)
    {
        super(canvas3D, bounds);
    }

    /**
     * Creates a new overlay covering the given canvas bounds. It has two
     * buffers. Updates are managed automatically. This Overlay is not usable
     * until you attach it to the view platform transform.
     *
     * @param canvas3D Canvas being drawn onto
     * @param bounds Bounds on the canvas covered by the overlay
     */
    public MouseOverlay(Canvas3D canvas3D,
                       Rectangle bounds,
                       boolean hasButtonEvents,
                       boolean hasMotionEvents,
                       boolean processAll)
    {
        super(canvas3D, bounds);

        init(hasButtonEvents, hasMotionEvents, processAll);
    }

    /**
     * Constructs an overlay window with an update manager. It has two buffers.
     * This window will not be visible unless it is added to the scene under
     * the view platform transform.
     *
     * @param canvas3D The canvas the overlay is drawn on
     * @param bounds The part of the canvas covered by the overlay
     * @param updateManager Responsible for allowing the Overlay to update
     *   between renders. If this is null a default manager is created
     */
    public MouseOverlay(Canvas3D canvas3D,
                       Rectangle bounds,
                       UpdateManager updateManager)
    {
        super(canvas3D, bounds, updateManager);
    }

    /**
     * Constructs an overlay window with an update manager. It has two buffers.
     * This window will not be visible unless it is added to the scene under
     * the view platform transform.
     *
     * @param canvas3D The canvas the overlay is drawn on
     * @param bounds The part of the canvas covered by the overlay
     * @param updateManager Responsible for allowing the Overlay to update
     *   between renders. If this is null a default manager is created
     * @param hasButtonEvents true to recieve mouse button events
     * @param hasMotionEvents true to recieve mouse motion events
     * @param processAll true to process all events from the behaviour, or
     *    false to use just the last one
     */
    public MouseOverlay(Canvas3D canvas3D,
                       Rectangle bounds,
                       UpdateManager updateManager,
                       boolean hasButtonEvents,
                       boolean hasMotionEvents,
                       boolean processAll)
    {
        super(canvas3D, bounds, updateManager);

        init(hasButtonEvents, hasMotionEvents, processAll);
    }

    /**
     * Constructs an overlay window that can have alpha capabilities. This
     * window will not be visible unless it is added to the scene under the
     * view platform transform.
     *
     * @param canvas3D The canvas the overlay is drawn on
     * @param bounds The part of the canvas covered by the overlay
     * @param clipAlpha Should the polygon clip where alpha is zero
     * @param blendAlpha Should we blend to background where alpha is < 1
     * @param hasButtonEvents true to recieve mouse button events
     * @param hasMotionEvents true to recieve mouse motion events
     * @param processAll true to process all events from the behaviour, or
     *    false to use just the last one
     */
    public MouseOverlay(Canvas3D canvas3D,
                       Rectangle bounds,
                       boolean clipAlpha,
                       boolean blendAlpha,
                       boolean hasButtonEvents,
                       boolean hasMotionEvents,
                       boolean processAll)
    {
        super(canvas3D, bounds, clipAlpha, blendAlpha);

        init(hasButtonEvents, hasMotionEvents, processAll);
    }

    /**
     * Constructs an overlay window. This window will not be visible
     * unless it is added to the scene under the view platform transform
     *
     * @param canvas3D The canvas the overlay is drawn on
     * @param bounds The part of the canvas covered by the overlay
     * @param clipAlpha Should the polygon clip where alpha is zero
     * @param blendAlpha Should we blend to background where alpha is < 1
     * @param updateManager Responsible for allowing the Overlay to update
     *   between renders. If this is null a default manager is created
     * @param hasButtonEvents true to recieve mouse button events
     * @param hasMotionEvents true to recieve mouse motion events
     * @param processAll true to process all events from the behaviour, or
     *    false to use just the last one
     */
    public MouseOverlay(Canvas3D canvas3D,
                       Rectangle bounds,
                       boolean clipAlpha,
                       boolean blendAlpha,
                       UpdateManager updateManager,
                       boolean hasButtonEvents,
                       boolean hasMotionEvents,
                       boolean processAll)
    {
        super(canvas3D, bounds, clipAlpha, blendAlpha, updateManager);

        init(hasButtonEvents, hasMotionEvents, processAll);
    }

    /**
     * Constructs an overlay window. This window will not be visible
     * unless it is added to the scene under the view platform transform
     *
     * @param canvas3D The canvas the overlay is drawn on
     * @param bounds The part of the canvas covered by the overlay
     * @param clipAlpha Should the polygon clip where alpha is zero
     * @param blendAlpha Should we blend to background where alpha is < 1
     * @param updateManager Responsible for allowing the Overlay to update
     *   between renders. If this is null a default manager is created
     * @param numBuffers The number of buffers to generate, the default is two
     * @param hasButtonEvents true to recieve mouse button events
     * @param hasMotionEvents true to recieve mouse motion events
     * @param processAll true to process all events from the behaviour, or
     *    false to use just the last one
     */
    public MouseOverlay(Canvas3D canvas3D,
                       Rectangle bounds,
                       boolean clipAlpha,
                       boolean blendAlpha,
                       UpdateManager updateManager,
                       int numBuffers,
                       boolean hasButtonEvents,
                       boolean hasMotionEvents,
                       boolean processAll)
    {
        super(canvas3D, bounds, clipAlpha, blendAlpha, updateManager, numBuffers);

        init(hasButtonEvents, hasMotionEvents, processAll);
    }

    /**
     * Internal common initialisation for all the constructors.
     *
     * @param hasButtonEvents true to recieve mouse button events
     * @param hasMotionEvents true to recieve mouse motion events
     * @param processAll true to process all events from the behaviour, or
     *    false to use just the last one
     */
    private void init(boolean hasButtonEvents,
                      boolean hasMotionEvents,
                      boolean processAll)
    {
        processAllEvents = processAll;
        setEventTypes(hasButtonEvents, hasMotionEvents);

        BranchGroup bg = new BranchGroup();
        bg.addChild(new EventBehavior());
        consoleBG.addChild(bg);
    }

    /**
     * Set the types of events to receive. If both masks are set to false, the
     * events will be disabled.
     *
     * @param hasMotionEvents true to be sent motion events
     * @param hasButtonEvents true to be sent ordinary events
     */
    public void setEventTypes(boolean hasButtonEvents, boolean hasMotionEvents)
    {
        long mask = 0;

        if(hasMotionEvents)
            mask = MouseEvent.MOUSE_MOTION_EVENT_MASK;

        if(hasButtonEvents)
            mask |= MouseEvent.MOUSE_EVENT_MASK;

        critter = new WakeupOnAWTEvent(mask);
    }

    /**
     * Initialise the overlay to build mouse input support
     */
    public void initialize()
    {

        // set the entire background to transparent
        setBackgroundColor(new Color(0.0f,0.0f,0.0f,0.0f));
    }

    //------------------------------------------------------------------------
    // Methods for MouseListener events
    //------------------------------------------------------------------------

    /**
     * Process a mouse press event.
     *
     * @param evt The event that caused this method to be called
     */
    public void mousePressed(MouseEvent evt)
    {
    }

    /**
     * Process a mouse release event.
     *
     * @param evt The event that caused this method to be called
     */
    public void mouseReleased(MouseEvent evt)
    {
    }

    /**
     * Process a mouse click event.
     *
     * @param evt The event that caused this method to be called
     */
    public void mouseClicked(MouseEvent evt)
    {
    }

    /**
     * Process a mouse enter event.
     *
     * @param evt The event that caused this method to be called
     */
    public void mouseEntered(MouseEvent evt)
    {
    }

    /**
     * Process a mouse exited event.
     *
     * @param evt The event that caused this method to be called
     */
    public void mouseExited(MouseEvent evt)
    {
    }

    //------------------------------------------------------------------------
    // Methods for MouseMotionListener events
    //------------------------------------------------------------------------

    /**
     * Process a mouse drag event
     *
     * @param evt The event that caused this method to be called
     */
    public void mouseDragged(MouseEvent evt)
    {
    }

    /**
     * Process a mouse movement event.
     *
     * @param evt The event that caused this method to be called
     */
    public void mouseMoved(MouseEvent evt)
    {
    }

    //------------------------------------------------------------------------
    // Local methods
    //------------------------------------------------------------------------
}