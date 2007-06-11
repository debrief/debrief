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

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.image.codec.jpeg.JPEGEncodeParam;

// Application specific imports
import org.j3d.ui.CapturedImageObserver;

/**
 * An image observer that turns each call into an separate JPEG image as a
 * sequentially numbered set.
 * <P>
 *
 * The purpose of this observer is to create a sequence of images to be
 * captured to disk so that we could make a video type presentation out of
 * them at a later date. The generated filenames start from the base name
 * given and just add a sequence number and the <CODE>.jpg</CODE> extension.
 * <P>
 * This uses the Sun codec classes to save the image to disk. Don't know how
 * this will react on non-sun JVMs.
 *
 * @author Justin Couch
 * @version $Revision $
 */
public class JPEGSequenceObserver implements CapturedImageObserver
{
    /** A flag to indicate if we should be capturing the image */
    private boolean enabled = false;

    /** The currently set filename base */
    private String filename;

    /** The current directory we are writing them too */
    private File directory;

    /** The current index for the file sequence number */
    private int sequenceNumber;

    /**
     * Construct a default observer with no filename set and it has not yet
     * captured an image.
     */
    public JPEGSequenceObserver()
    {
    }

    /**
     * Set the recording details about the directory that should be written to
     * and the base filename to use. If the directory does not exist, the code
     * will create the requested directory silently. The filename base should
     * not include the extension because we automatically add it. Setting this
     * will also reset the sequence number to zero.
     *
     * @param dir The directory to use
     * @param fileBase The base filename to use
     * @throws IllegalArgumentException One or other of the params are null
     * @throws IllegalStateException The capturing is current enabled
     */
    public void setFileDetails(String dir, String fileBase)
    {
        if(enabled)
            throw new IllegalStateException("Currently capturing images");

        if(dir == null)
            throw new IllegalArgumentException("Directory reference is null");

        if(fileBase == null)
            throw new IllegalArgumentException("fileBase is null");

        directory = new File(dir);
        directory.mkdirs();

        filename = fileBase;
        sequenceNumber = 0;
    }

    /**
     * Tell the observer to start or stop capturing the next frame it is told
     * about.
     *
     * @param enabled true to start capturing images
     */
    public void setEnabled(boolean enable)
    {
        enabled = enable;
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
        if(!enabled)
            return;

        if(filename == null)
            throw new IllegalStateException("The filename is not set");

        try
        {
            File current =
                new File(directory, filename + sequenceNumber + ".jpg");

            FileOutputStream out = new FileOutputStream(current);
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

        sequenceNumber++;
    }
}

