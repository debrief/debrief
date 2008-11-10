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
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;

/**
 * A handler for viewpoint information for a given canvas.
 *
 * @author Justin Couch
 * @version $Revision $
 */
public class ViewpointManager implements ViewpointSelectionListener
{
    /** The default transition time in milliseconds */
    private static final int DEFAULT_TRANSITION_TIME = 2000;

    /** The toolbar for the viewpoint information */
    private ViewpointToolbar toolbar;

    /** The transition manager that generates movement */
    private ViewpointTransition transistor;   // :)

    /** The user set transition time in milliseconds */
    private int transitionTime;

    /** The view that we are moving about. */
    private View view;

    /** The transform group above the view that is being moved each frame */
    private TransformGroup viewTg;

    /** A temp variable to copy the transition info into */
    private Transform3D destinationTx;

    /**
     * Create a new manager for the viewpoint information with no handlers or
     * information set and the default transition time of 2 seconds.
     */
    public ViewpointManager()
    {
        this(DEFAULT_TRANSITION_TIME);
    }

    /**
     * Create a new manager for the viewpoint information with no handlers or
     * information set and the the given transition time.
     *
     * @param time The time to transit when changing
     * @throws IllegalArgumentException The time was negative
     */
    public ViewpointManager(int time) throws IllegalArgumentException
    {
        if(time < 0)
            throw new IllegalArgumentException("Negative transition time");

        transitionTime = time;
        transistor = new ViewpointTransition();
        destinationTx = new Transform3D();
    }

    /**
     * Set the toolbar instance to use. Setting a value of null will clear the
     * currently set instance.
     *
     * @param tbr The new toolbar instance to use
     */
    public void setToolbar(ViewpointToolbar tbr)
    {
        if(toolbar != null)
            toolbar.setViewpointSelectionListener(null);

        toolbar = tbr;

        if(toolbar != null)
        {
            toolbar.setEnabled(view != null);
            toolbar.setViewpointSelectionListener(this);
        }
    }

    /**
     * Change the transition time between viewpoints. Only takes effect for
     * the next transition. Will not change the current one. Value must be
     * non-negative. A value of zero results in a jump between the two
     * locations.
     *
     * @param time The new time to use in milliseconds
     * @throws IllegalArgumentException The time was negative
     */
    public void setTransitionTime(int time)
    {
        if(time < 0)
            throw new IllegalArgumentException("Negative transition time");

        transitionTime = time;
    }

    /**
     * Set the view and it's related transform group to use. If a canvas is
     * set and these are non-null the interaction will start immediately.
     * Calling with both values as null will remove them and stop the process
     * of updating the canvas.
     *
     * @param view is the View object that we're modifying.
     * @param tg The transform group above the view object that should be used
     */
    public void setViewInfo(View view, TransformGroup tg)
    {
        if(!((view != null) && (tg != null)) ||
            ((view == null) && (tg == null)))
            throw new IllegalArgumentException("View or TG is null when " +
                                               "the other isn't");

        if(toolbar != null)
            toolbar.setEnabled(view != null);

        this.view = view;
        this.viewTg = tg;
    }

    /**
     * Set the listener for frame update notifications. By setting a value of
     * null it will clear the currently set instance
     *
     * @param l The listener to use for this transition
     */
    public void setFrameUpdateListener(FrameUpdateListener l)
    {
        transistor.setFrameUpdateListener(l);
    }

    /**
     * A new viewpoint has been selected and this is it. Move to this viewpoint
     * location according to the requested means.
     *
     * @param vp The new viewpoint to use
     */
    public void viewpointSelected(ViewpointData vp)
    {
        if(view == null)
            return;

        vp.viewTg.getTransform(destinationTx);
        transistor.transitionTo(view, viewTg, destinationTx, transitionTime);
    }
}
