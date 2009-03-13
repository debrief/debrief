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
import java.awt.Toolkit;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

// Application specific imports
// none

/**
 * A convenience class that loads Icons for users and provides caching
 * mechanisms.
 * <p>
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class ImageLoader
{
    /** The default size of the map */
    private static final int DEFAULT_SIZE = 10;

    /** The image toolkit used to load images with */
    private static Toolkit toolkit;

    /**
     * A hashmap of the loaded image instances. Weak so that we can discard
     * them if if needed because we're running out of memory.
     */
    private static HashMap<String, WeakReference<Image>> loadedImages;

    /**
     * A hashmap of the loaded icon instances. Weak so that we can discard
     * them if if needed because we're running out of memory.
     */
    private static HashMap<String, WeakReference<Icon>> loadedIcons;

    /**
     * Static initialiser to get all the bits set up as needed.
     */
    static
    {
        toolkit = Toolkit.getDefaultToolkit();
        ClassLoader.getSystemClassLoader();
        loadedImages = new HashMap<String, WeakReference<Image>>(DEFAULT_SIZE);
        loadedIcons = new HashMap<String, WeakReference<Icon>>(DEFAULT_SIZE);
    }

    /**
     * Load an icon for the named image file. Looks in the classpath for the
     * image so the path provided must be fully qualified relative to the
     * classpath.
     *
     * @param path The path to load the icon for. If not found,
     *   no image is loaded.
     * @return An icon for the named path.
     */
    public static Icon loadIcon(String name)
    {
        // Check the map for an instance first
        Icon ret_val = null;

        WeakReference<Icon> ref = loadedIcons.get(name);
        if(ref != null)
        {
            ret_val = (Icon)ref.get();
            if(ret_val == null)
                loadedIcons.remove(name);
        }

        if(ret_val == null)
        {
            Image img = loadImage(name);

            if(img != null)
            {
                ret_val = new ImageIcon(img, name);
                loadedIcons.put(name, new WeakReference<Icon>(ret_val));
            }
        }

        return ret_val;
    }

    /**
     * Load an image for the named image file. Looks in the classpath for the
     * image so the path provided must be fully qualified relative to the
     * classpath.
     *
     * @param path The path to load the icon for. If not found,
     *   no image is loaded.
     * @return An image for the named path.
     */
    public static Image loadImage(String name)
    {
        // Check the map for an instance first
        Image ret_val = null;

        WeakReference<Image> ref = loadedImages.get(name);
        if(ref != null)
        {
            ret_val = (Image)ref.get();
            if(ret_val == null)
                loadedIcons.remove(name);
        }

        if(ret_val == null)
        {
            URL url = ClassLoader.getSystemResource(name);

            if(url != null)
            {
                ret_val = toolkit.createImage(url);
                loadedImages.put(name, new WeakReference<Image>(ret_val));
            }
        }

        return ret_val;
    }
}
