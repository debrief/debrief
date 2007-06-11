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

package org.j3d.ui.navigation;

// Standard imports
import javax.media.j3d.*;

import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.vecmath.Point3d;

// Application specific imports
// none

/**
 * A Java3D Behavior based interface to drive navigation.
 * <p>
 *
 * The behavior looks for AWTEvents for mouse move and mouse motion. It will
 * automatically register itself and the bounds are set to infinity so that it
 * will always run.
 * <p>
 *
 * To set the classes needed by the underlying navigation handler, you can
 * either ask for the instance and set the values directly, or ask the
 * convenience methods here. These are just pass though and do not add any
 * extra functionality.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class MouseViewBehavior extends Behavior
{
    /** The criteria used to wake up for mouse events */
    private WakeupOnAWTEvent criteria;

    /** The navigation processor we are delegating to do the work */
    private NavigationHandler inputHandler;

    /**
     * Create a new behavior with default settings and creates its own
     * {@link NavigationHandler}.
     */
    public MouseViewBehavior()
    {
        this(null);
    }

    /**
     * Create a new behavior with default settings and uses the supplied
     * {@link NavigationHandler} for dealing with the input. If the supplied
     * reference is null it will create one itself.
     *
     * @param nav The handler for navigation to use
     */
    public MouseViewBehavior(NavigationHandler nav)
    {
        long mask = AWTEvent.MOUSE_EVENT_MASK |
                    AWTEvent.MOUSE_MOTION_EVENT_MASK;

        criteria = new WakeupOnAWTEvent(mask);

        if(nav != null)
            inputHandler = nav;
        else
            inputHandler = new NavigationHandler();

        Point3d center = new Point3d();
        BoundingSphere bounds =
            new BoundingSphere(center, Double.POSITIVE_INFINITY);

        setSchedulingBounds(bounds);
    }

    //----------------------------------------------------------
    // Methods required by the Behavior interface.
    //----------------------------------------------------------

    /**
     * Initialise the behavior to start running. This will register the
     * first criteria. Don't wake until we have arrived in the activation
     * area of the viewpoint and then run every frame.
     */
    public void initialize() {
        wakeupOn(criteria);
    }

    /**
     * Process the event that builds the current time.
     *
     * @param why The list of conditions why this was woken
     */
    public void processStimulus(Enumeration why)
    {

        Object critter;
        WakeupOnAWTEvent awt_critter;

        while(why.hasMoreElements())
        {
            critter = why.nextElement();

            if(critter instanceof WakeupOnAWTEvent)
            {
                awt_critter = (WakeupOnAWTEvent)critter;
                AWTEvent[] evts = awt_critter.getAWTEvent();

                // always just use the last event
                int last = evts.length - 1;
                if(evts[last] instanceof MouseEvent)
                {
                    MouseEvent evt = (MouseEvent)evts[last];
                    int type = evt.getID();

                    switch(type)
                    {
                        case MouseEvent.MOUSE_PRESSED:
                            inputHandler.mousePressed(evt);
                            break;

                        case MouseEvent.MOUSE_RELEASED:
                            inputHandler.mouseReleased(evt);
                            break;

                        case MouseEvent.MOUSE_DRAGGED:
                            inputHandler.mouseDragged(evt);
                            break;

                        default:
//                            System.out.println("Unknown event type " + evt);
                    }
                }
            }
        }

        wakeupOn(criteria);
    }

    //----------------------------------------------------------
    // Local Methods
    //----------------------------------------------------------

    /**
     * Get the NavigationHandler instance that this class is using.
     *
     * @param The navigation instance currently in use
     */
    public NavigationHandler getNavigationHandler()
    {
        return inputHandler;
    }

    /**
     * Set the branchgroups to use for terrain and collision information. The
     * two are treated separately for the different processes. The caller may
     * choose to make them the same reference, but the code internally treats
     * them separately.
     * <p>
     *
     * <b>Note</b> For picking purposes, the code currently assumes that both
     * groups do not have any parent transforms. That is, their world origin
     * is the same as the transform group presented in the view information.
     *
     * @param terrainGroup  The geometry to use as terrain for following
     * @param worldGroup The geometry to use for collisions
     */
    public void setWorldInfo(BranchGroup terrainGroup, BranchGroup worldGroup)
    {
        inputHandler.setWorldInfo(terrainGroup, worldGroup);
    }

    /**
     * Set the information about the avatar that is used for collisions and
     * navigation information.
     *
     * @param height The heigth of the avatar above the terrain
     * @param size The distance between the avatar and collidable objects
     * @param stepHeight The height that an avatar can step over
     */
    public void setAvatarInfo(float height, float size, float stepHeight)
    {
        inputHandler.setAvatarInfo(height, size, stepHeight);
    }

    /**
     * Set the navigation speed to the new value. The speed must be a
     * non-negative number.
     *
     * @param newSpeed The new speed value to use
     * @throws IllegalArgumentException The value was negative
     */
    public void setNavigationSpeed(float newSpeed)
    {
        inputHandler.setNavigationSpeed(newSpeed);
    }

    /**
     * Set the ability to use a given state within the handler for a
     * specific mouse button (up to 3). This allows the caller to control
     * exactly what states are allowed to be used and with which buttons.
     * Note that it is quite legal to set all three buttons to the same
     * navigation state
     *
     * @param button The mouse button value from
     *    {@link java.awt.event.MouseEvent}
     * @param state The navigation state to use for that button
     */
    public void setButtonNavigation(int button, int state)
    {
        inputHandler.setButtonNavigation(button, state);
    }

    /**
     * Set the view and it's related transform group to use. This view is what
     * we navigation around the scene with.
     *
     * @param view is the View object that we're modifying.
     * @param tg The transform group above the view object that should be used
     */
    public void setViewInfo(View view, TransformGroup tg)
    {
        inputHandler.setViewInfo(view, tg);
    }

    /**
     * Set the listener for frame update notifications. By setting a value of
     * null it will clear the currently set instance
     *
     * @param l The listener to use for this transition
     */
    public void setFrameUpdateListener(FrameUpdateListener l)
    {
        inputHandler.setFrameUpdateListener(l);
    }

    /**
     * Set the listener for collision notifications. By setting
     * a value of null it will clear the currently set instance
     *
     * @param l The listener to use for updates
     */
    public void setCollisionListener(CollisionListener l)
    {
        inputHandler.setCollisionListener(l);
    }

    /**
     * Set the listener for navigation state change notifications. By setting
     * a value of null it will clear the currently set instance
     *
     * @param l The listener to use for change updates
     */
    public void setNavigationStateListener(NavigationStateListener l)
    {
        inputHandler.setNavigationStateListener(l);
    }
}
