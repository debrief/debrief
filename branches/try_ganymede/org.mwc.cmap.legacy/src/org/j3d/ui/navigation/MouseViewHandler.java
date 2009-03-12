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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;

// Application specific imports
// none

/**
 * An AWT based listener to drive navigation.
 * <p>
 *
 * Instead of using behaviors this class does the job in a much smoother way
 * by hooking AWT events directly from the canvas.
 * <p>
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class MouseViewHandler extends NavigationHandler
    implements MouseListener, MouseMotionListener

{
    /** The canvas this handler is operating on */
    private Canvas3D canvas;

    /** Flag indicating the view has been set */
    private boolean viewSet;

    /**
     * Create a new mouse handler with no canvas or view information set. This
     * handler will not do anything until both canvas and view transform
     * references have been set.
     */
    public MouseViewHandler()
    {
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
        super.setViewInfo(view, tg);

        viewSet = (view != null);

        // If the canvas isn't null then set up the listeners or remove them
        // depending on the input values here.
        if(canvas != null)
        {
            if(viewSet)
            {
                canvas.addMouseListener(this);
                canvas.addMouseMotionListener(this);
            }
            else
            {
                this.canvas.removeMouseListener(this);
                this.canvas.removeMouseMotionListener(this);
            }
        }
    }

    /**
     * Set the canvas to use for this handler. Setting this will immediately
     * register the listeners and start the operation if there is also a view
     * and transform group. To remove the listener then call this method with
     * the canvas parameter null.
     *
     * @param canvas The new canvas to use for this handler
     */
    public void setCanvas(Canvas3D canvas)
    {
        if(canvas != null)
        {
            if(viewSet)
            {
                canvas.addMouseListener(this);
                canvas.addMouseMotionListener(this);
            }
        }
        else if(this.canvas != null)
        {
            this.canvas.removeMouseListener(this);
            this.canvas.removeMouseMotionListener(this);
        }

        this.canvas = canvas;
    }

    /**
     * Not used by this class
     */
    public void mouseMoved(MouseEvent evt)
    {
    }

    /**
     * Not used by this class
     */
    public void mouseClicked(MouseEvent evt)
    {
    }

    /**
     * Process a mouse enter event. We use this to request focus for the
     * canvas so that mouse handling works nicely.
     *
     * @param evt The event that caused this method to be called
     */
    public void mouseEntered(MouseEvent evt)
    {
        canvas.requestFocus();
    }

    /**
     * Not used by this class
     */
    public void mouseExited(MouseEvent evt)
    {
    }
}
