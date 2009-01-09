/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.ui;

import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import java.util.Iterator;
import java.util.LinkedList;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Raster;

import javax.vecmath.Point3f;

/**
 * A version of the standard Java3D Canvas3D class that allows you to capture
 * the contents and write out the image information.
 * <P>
 *
 * The canvas uses a callback mechanism to capture an image and notify the
 * observer of the image data.
 * <P>
 * The original code for this was written by Peter Z. Kunszt of John Hopkins
 * Uni and posted to the Java 3D interest list. This version has been modified
 * to make it more reusable and flexible. The image can be used to pass to a
 * printer or written to a file for example.
 * <P>
 * When the observer is notified, this class does not provide any separation
 * of the notification from the rendering thread. A call to the observer will
 * prevent the renderer from starting the next frame. If you are intending to
 * save a lot of images, you should implement some form of buffering system to
 * take the conversion process into a separate thread otherwise on-screen
 * performance will be <I>severely</I> impacted.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $ $Date: 2003/07/17 10:08:15 $
 */
public class ImageCaptureCanvas3D extends Canvas3D
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/**
     * Flag to indicate that the canvas should notify of an image. Faster than
     * making a call to observers.size() each frame.
     */
    private transient boolean captureImage;

    /** The list of registered observers */
    private transient LinkedList<CapturedImageObserver> observers;

    /**
     * Create a new canvas given the graphics configuration that runs as an
     * onscreen canvas.
     *
     * @param gc The graphics configuration for this canvas
     */
    public ImageCaptureCanvas3D(GraphicsConfiguration gc)
    {
        super(gc);

        captureImage = false;

        observers = new LinkedList<CapturedImageObserver>();
    }

    /**
     * Create a new canvas that allows capture and may operate either on screen
     * or off-screen.
     *
     * @param gc Thr graphics configuration to use for the canvas
     * @param offScreen True if this is to operate in an offscreen mode
     */
    public ImageCaptureCanvas3D(GraphicsConfiguration gc, boolean offScreen)
    {
        super(gc, offScreen);

        captureImage = false;

        observers = new LinkedList<CapturedImageObserver>();
    }

    /**
     * Process code after we have swapped the image to the foreground.
     * Overrides the standard implementation to fetch the image to call to
     * the observers if needed.
     */
    public void postSwap()
    {
        if(!captureImage)
            return;

        GraphicsContext3D ctx = getGraphicsContext3D();
        Rectangle rect = this.getBounds();

        BufferedImage img = new BufferedImage(rect.width,rect.height,
                                              BufferedImage.TYPE_INT_RGB);

        ImageComponent2D comp =
            new ImageComponent2D(ImageComponent.FORMAT_RGB, img, true, false);

        // The raster components need all be set!

        Raster ras = new Raster(new Point3f(-1.0f,-1.0f,-1.0f),
                                Raster.RASTER_COLOR,
                                0,
                                0,
                                rect.width,
                                rect.height,
                                comp,
                                null);

        ctx.readRaster(ras);

        // Now strip out the image info
        // BufferedImage output_img = ras.getImage().getImage();

        notifyObservers(img);
    }

    /**
     * Add an observer to this canvas to listen for images. Each instance can
     * only be registered once.
     *
     * @param obs The observer to be registered
     */
    public void addCaptureObserver(CapturedImageObserver obs)
    {
        if((obs != null) && !observers.contains(obs))
        {
            observers.add(obs);
            captureImage = true;
        }
    }

    /**
     * Remove a registered observer from this canvas. If the reference is null
     * or cannot be found registered here it will silently ignore the request.
     *
     * @param obs The observer to be removed
     */
    public void removeCaptureObserver(CapturedImageObserver obs)
    {
        if(obs != null)
        {
            observers.remove(obs);

            if(observers.size() == 0)
                captureImage = false;
        }
    }

    /**
     * Notify all the observers that we have an image to send them.
     *
     * @param img The image to be sent to the observers
     */
    private void notifyObservers(BufferedImage img)
    {
        Iterator<CapturedImageObserver> itr = observers.iterator();
        CapturedImageObserver obs;

        while(itr.hasNext())
        {
            obs = (CapturedImageObserver)itr.next();

            try
            {
                obs.canvasImageCaptured(img);
            }
            catch(Exception e)
            {
                System.err.println("Error sending image to observer");
                e.printStackTrace();
            }
        }
    }
}
