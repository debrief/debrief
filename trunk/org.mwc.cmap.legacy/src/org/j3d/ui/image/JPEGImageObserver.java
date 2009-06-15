/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.ui.image;

// Standard imports
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.j3d.ui.CapturedImageObserver;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * A one-shot image observer that turns the image into a JPEG image.
 * <P>
 * This oneshot will only write a single image the first time it is called.
 * After that it will ignore any incoming requests.
 * <P>
 *
 * If the filename already exists, it will automatically overwrite the existing
 * image. If the filename contains non-existant intermediate directories, these
 * will be automatically created.
 * <P>
 *
 * This uses the Sun codec classes to save the image to disk. Don't know how
 * this will react on non-sun JVMs.
 *
 * @author Justin Couch
 * @version $Revision $
 */
public class JPEGImageObserver implements CapturedImageObserver
{
    /** A flag to indicate if we have written the image */
    private boolean hasFired = false;

    /** A flag to indicate we should capture the next frame */
    private boolean captureNextFrame = false;

    /** The currently set filename. */
    private String filename;

    /**
     * Construct a default observer with no filename set and it has not yet
     * captured an image.
     */
    public JPEGImageObserver()
    {
    }

    /**
     * Tell the observer to capture the next frame it is told about. If the
     * observer has already fired, it will throw an IllegalStateException.
     *
     * @throws IllegalStateException The image capture has already fired
     */
    public void setCaptureNextFrame()
    {
        if(hasFired)
            throw new IllegalStateException("Image capture already occurred");

        captureNextFrame = true;
    }

    /**
     * Set the filename that this will write to. If the filename is null,
     * this will remove the previously set name. It can only be set if the
     * image has not yet been captured.
     *
     * @param name The name of the file to write to.
     * @throws IllegalStateException Calling to set the name after the image
     *    capture has fired
     */
    public void setFilename(String name) throws IllegalStateException
    {
        if(hasFired)
            throw new IllegalStateException("Image capture already occurred");

        filename = name;
    }

    /**
     * Notification that an image has been captured from the canvas and is
     * ready for processing. If the filename has not been set then this will
     * throw an exception.
     *
     * @param img The image that was captured
     * @throws IllegalStateException The filename has not been set
     */
    public void canvasImageCaptured(BufferedImage img)
    {
        if(hasFired || !captureNextFrame)
            return;

        if(filename == null)
            throw new IllegalStateException("The filename is not set");

        try
        {
            File file = new File(filename);

            File dirs = file.getParentFile();
            dirs.mkdirs();

            FileOutputStream out = new FileOutputStream(file);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);

            param.setQuality(0.9f, false); // 90% quality JPEG
            encoder.setJPEGEncodeParam(param);
            encoder.encode(img);

            out.close();
        }
        catch(IOException e)
        {
            System.out.println("I/O exception writing JPEG image! " + e);
            e.printStackTrace();
        }

        hasFired = true;
    }
}

