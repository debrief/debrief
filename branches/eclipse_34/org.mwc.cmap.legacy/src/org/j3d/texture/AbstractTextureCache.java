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

package org.j3d.texture;

// Standard imports
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Texture;

import java.awt.image.DataBuffer;

// Application specific imports
import org.j3d.util.ImageUtils;

/**
 * An abstract implementation of the cache with a collection of useful
 * utility methods for any cache implementation.
 * <p>
 *
 * This class does not provide the storage structures for caching as each
 * implementation will have different requirements. It just provides utility
 * methods that most implementations will find useful.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public abstract class AbstractTextureCache implements TextureCache
{
    /** The list of class types we prefer back from the content handler. */
    private static final Class[] CLASS_TYPES =
    {
        ImageProducer.class,
        BufferedImage.class,
        Image.class
    };

    /**
     * Construct a new instance of the empty cache. Empty implementation,
     * does nothing.
     */
    protected AbstractTextureCache()
    {
    }

    //------------------------------------------------------------------------
    // Local methods
    //------------------------------------------------------------------------

    /**
     * From the image component format, generate the appropriate texture
     * format.
     *
     * @param comp The image component to get the value from
     * @return The appropriate corresponding texture format value
     */
    protected int getTextureFormat(ImageComponent comp)
    {
        int ret_val = Texture.RGB;

        switch(comp.getFormat())
        {
            case ImageComponent.FORMAT_CHANNEL8:
                // could also be alpha, but we'll punt for now. We really need
                // the user to pass in this information. Need to think of a
                // good way of doing this.
                ret_val = Texture.LUMINANCE;
                break;

            case ImageComponent.FORMAT_LUM4_ALPHA4:
            case ImageComponent.FORMAT_LUM8_ALPHA8:
                ret_val = Texture.LUMINANCE_ALPHA;
                break;

            case ImageComponent.FORMAT_R3_G3_B2:
            case ImageComponent.FORMAT_RGB:
            case ImageComponent.FORMAT_RGB4:
            case ImageComponent.FORMAT_RGB5:
            case ImageComponent.FORMAT_RGB5_A1:
//            case ImageComponent.FORMAT_RGB8:
            case ImageComponent.FORMAT_RGBA:
            case ImageComponent.FORMAT_RGBA4:
//            case ImageComponent.FORMAT_RGBA8:
                ret_val = Texture.RGB;
                break;
        }

        return ret_val;
    }

    /**
     * Load the image component from the given filename. All images are
     * loaded by-reference. This does not automatically register the component
     * with the internal datastructures. That is the responsibility of the
     * caller.
     *
     * @param filename The name of the file to be loaded
     * @return An ImageComponent instance with byRef true and yUp false
     * @throws IOException Some error reading the file
     */
    protected ImageComponent2D load2DImage(String filename)
        throws IOException
    {
        // first try to locate the image as a fully qualified filename.
        File file = new File(filename);
        URL url;

        if(file.exists())
        {
            url = file.toURL();
        }
        else
        {
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            url = cl.getResource(filename);

            if(url == null)
                throw new FileNotFoundException("Couldn't find " + filename);
        }

        return load2DImage(url);
    }

    /**
     * Load the image component from the given url. All images are
     * loaded by-reference. This does not automatically register the component
     * with the internal datastructures. That is the responsibility of the
     * caller.
     *
     * @param url The URL of the file to be loaded
     * @return An ImageComponent instance with byRef true and yUp false
     * @throws IOException Some error reading the URL
     */
    protected ImageComponent2D load2DImage(URL url)
        throws IOException
    {
        Object content = url.getContent(CLASS_TYPES);

        if(content == null)
            throw new FileNotFoundException("No content for " + url);

        BufferedImage image = null;

        if(content instanceof ImageProducer)
            image = ImageUtils.createBufferedImage((ImageProducer)content);
        else if(content instanceof BufferedImage)
            image = (BufferedImage)content;
        else
            image = ImageUtils.createBufferedImage((Image)content);

        int format = ImageComponent2D.FORMAT_RGBA;

System.out.println("Image details = " + image);

switch(image.getColorModel().getTransferType())
{
    case DataBuffer.TYPE_BYTE:
        System.out.println("Byte data type");
        break;
    case DataBuffer.TYPE_DOUBLE:
        System.out.println("Double data type");
        break;
    case DataBuffer.TYPE_FLOAT:
        System.out.println("Float data type");
        break;
    case DataBuffer.TYPE_INT:
        System.out.println("Int data type");
        break;
    case DataBuffer.TYPE_SHORT:
        System.out.println("Short data type");
        break;
    case DataBuffer.TYPE_UNDEFINED:
        System.out.println("Undefined data type");
        break;
    case DataBuffer.TYPE_USHORT:
        System.out.println("UShort data type");
        break;
}

        switch(image.getType())
        {
            case BufferedImage.TYPE_3BYTE_BGR:
            case BufferedImage.TYPE_BYTE_BINARY:
            case BufferedImage.TYPE_INT_BGR:
            case BufferedImage.TYPE_INT_RGB:
System.out.println("3 component image");
                format = ImageComponent2D.FORMAT_RGB;
                break;

            case BufferedImage.TYPE_CUSTOM:
                // no idea what this should be, so default to RGBA
            case BufferedImage.TYPE_INT_ARGB:
            case BufferedImage.TYPE_INT_ARGB_PRE:
            case BufferedImage.TYPE_4BYTE_ABGR:
            case BufferedImage.TYPE_4BYTE_ABGR_PRE:
System.out.println("4 component image");
                format = ImageComponent2D.FORMAT_RGBA;
                break;

            case BufferedImage.TYPE_BYTE_GRAY:
            case BufferedImage.TYPE_USHORT_GRAY:
System.out.println("greyscale");
                format = ImageComponent2D.FORMAT_CHANNEL8;
                break;

            case BufferedImage.TYPE_BYTE_INDEXED:
System.out.println("byte index");
                format = ImageComponent2D.FORMAT_R3_G3_B2;
                break;

            case BufferedImage.TYPE_USHORT_555_RGB:
System.out.println("15 bit");
                format = ImageComponent2D.FORMAT_RGB5;
                break;

            case BufferedImage.TYPE_USHORT_565_RGB:
System.out.println("16 bit");
                format = ImageComponent2D.FORMAT_RGB5;
                break;
        }

        ImageComponent2D ret_val =
            new ImageComponent2D(format, image, true, false);

        return ret_val;
    }
}
