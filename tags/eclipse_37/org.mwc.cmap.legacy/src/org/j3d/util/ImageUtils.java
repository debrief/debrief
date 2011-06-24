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
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;

// Application specific imports
// none

/**
 * A utility class that allows you to do various actions with images, such
 * as conversions and creating items that are useful as textures.
 * <p>
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class ImageUtils
{
    /**
     * Create a {@link java.awt.image.BufferedImage} from the given source
     * image. If the source image is already a buffered image it will
     * immediately return with the parameter as the return value.
     *
     * @param img The source image to create buffered image from
     * @return A complete buffered image representation of this image
     */
    public static BufferedImage createBufferedImage(Image img)
    {
        if(img instanceof BufferedImage)
            return (BufferedImage)img;

        ImageProducer prod = img.getSource();

        ImageGenerator gen = new ImageGenerator();
        prod.startProduction(gen);

        return gen.getImage();
    }

    /**
     * Create a {@link java.awt.image.BufferedImage} from the given source
     * image producer.
     *
     * @param prod The source imageproducer to create buffered image from
     * @return A complete buffered image representation of this image
     */
    public static BufferedImage createBufferedImage(ImageProducer prod)
    {
        ImageGenerator gen = new ImageGenerator();
        prod.startProduction(gen);

        return gen.getImage();
    }
}
