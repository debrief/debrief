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
import java.io.IOException;
import java.util.HashMap;

import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.ImageComponent3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.Texture3D;

// Application specific imports
import org.j3d.util.ImageUtils;
import org.j3d.util.Queue;

/**
 * A cache for texture instance management where the objects stay according
 * to a Least-Recently-Used algorithm.
 * <p>
 *
 * The LRU cache maintains an ordered list of items that are stored. Each time
 * a new item is loaded, the queue is checked. If the queue contains more
 * items that the prescribed limit, then whatever item is at the end of the
 * queue is removed from the internal structures. Whenever an item is fetched
 * it is removed from its current place in the queue and placed on the front.
 * <p>
 * By default, the queue size is 20 items. The LRU cache has a property that
 * can control the number of items in the cache:
 * <pre>
 *     org.j3d.texture.LRUSize
 * </pre>
 * This property must be set before this class is first referenced.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
class LRUTextureCache extends AbstractTextureCache
{
    /** Default cache size if nothing else is set */
    private static final int DEFAULT_CACHE_SIZE = 20;

    /** The system property name */
    public static final String DEFAULT_SIZE_PROP =
        "org.j3d.texture.LRUSize";

    private HashMap textureMap;
    private HashMap componentMap;

    /** The maximum number of items in the cache */
    private final int maxCacheSize;

    /** The LRU queue for textures. Contains only the name strings. */
    private Queue textureQueue;

    /** The LRU queue for component. Contains only the name strings. */
    private Queue componentQueue;

    /**
     * Construct a new instance of the empty cache.
     */
    LRUTextureCache()
    {
        textureMap = new HashMap();
        componentMap = new HashMap();
        textureQueue = new Queue();
        componentQueue = new Queue();

        String prop = System.getProperty(DEFAULT_SIZE_PROP);
        if(prop != null)
        {
            int val = DEFAULT_CACHE_SIZE;

            try
            {
                val = Integer.parseInt(prop);
            }
            catch(NumberFormatException nfe)
            {
            }

            maxCacheSize = val;
        }
        else
            maxCacheSize = DEFAULT_CACHE_SIZE;

    }

    /**
     * Fetch the texture named by the filename. The filename may be
     * either absolute or relative to the classpath.
     *
     * @param filename The filename to fetch
     * @return The texture instance for that filename
     * @throws IOException An I/O error occurred during loading
     */
    public Texture fetchTexture(String filename)
        throws IOException
    {
        Texture texture = (Texture)textureMap.get(filename);

        if(texture == null)
        {
            ImageComponent img = (ImageComponent)componentMap.get(filename);

            if(img == null)
            {
                img = load2DImage(filename);
                addImageComponent(filename, img);
            }
            else
            {
                // shift the component from the position in the queue, back
                // to the start again.
                componentQueue.remove(filename);
                componentQueue.add(filename);
            }

            int format = getTextureFormat(img);

            if(img instanceof ImageComponent2D)
            {
                texture = new Texture2D(Texture.BASE_LEVEL,
                                        format,
                                        img.getWidth(),
                                        img.getHeight());
            }
            else
            {
                texture = new Texture3D(Texture.BASE_LEVEL,
                                        format,
                                        img.getWidth(),
                                        img.getHeight(),
                                        ((ImageComponent3D)img).getDepth());
            }

            texture.setImage(0, img);

            addTexture(filename, texture);
        }
        else
        {
            // shift the current texture from the position in the queue, back
            // to the start again.
            textureQueue.remove(filename);
            textureQueue.add(filename);
        }

        return texture;
    }

    /**
     * Fetch the texture named by the URL.
     *
     * @param url The URL to read data from
     * @return The texture instance for that URL
     * @throws IOException An I/O error occurred during loading
     */
    public Texture fetchTexture(URL url)
        throws IOException
    {
        String file_path = url.toExternalForm();

        Texture texture = (Texture)textureMap.get(file_path);

        if(texture == null)
        {
            ImageComponent img = (ImageComponent)componentMap.get(file_path);

            if(img == null)
            {
                img = load2DImage(file_path);
                addImageComponent(file_path, img);
            }
            else
            {
                // shift the component from the position in the queue, back
                // to the start again.
                componentQueue.remove(file_path);
                componentQueue.add(file_path);
            }

            int format = getTextureFormat(img);

            if(img instanceof ImageComponent2D)
            {
                texture = new Texture2D(Texture.BASE_LEVEL,
                                        format,
                                        img.getWidth(),
                                        img.getHeight());
            }
            else
            {
                texture = new Texture3D(Texture.BASE_LEVEL,
                                        format,
                                        img.getWidth(),
                                        img.getHeight(),
                                        ((ImageComponent3D)img).getDepth());
            }

            texture.setImage(0, img);

            addTexture(file_path, texture);
        }
        else
        {
            // shift the current texture from the position in the queue, back
            // to the start again.
            textureQueue.remove(file_path);
            textureQueue.add(file_path);
        }

        return texture;
    }

    /**
     * Param fetch the imagecomponent named by the filename. The filename may
     * be either absolute or relative to the classpath.
     *
     * @param filename The filename to fetch
     * @return The ImageComponent instance for that filename
     * @throws IOException An I/O error occurred during loading
     */
    public ImageComponent fetchImageComponent(String filename)
        throws IOException
    {
        ImageComponent ret_val = (ImageComponent)componentMap.get(filename);

        if(ret_val == null)
        {
            ret_val = load2DImage(filename);
            addImageComponent(filename, ret_val);
        }
        else
        {
            // shift the component from the position in the queue, back
            // to the start again.
            componentQueue.remove(filename);
            componentQueue.add(filename);
        }

        return ret_val;
    }

    /**
     * Fetch the image component named by the URL.
     *
     * @param url The URL to read data from
     * @return The ImageComponent instance for that URL
     * @throws IOException An I/O error occurred during loading
     */
    public ImageComponent fetchImageComponent(URL url)
        throws IOException
    {
        String file_path = url.toExternalForm();
        ImageComponent ret_val = (ImageComponent)componentMap.get(file_path);

        if(ret_val == null)
        {
            ret_val = load2DImage(file_path);
            addImageComponent(file_path, ret_val);
        }
        else
        {
            // shift the component from the position in the queue, back
            // to the start again.
            componentQueue.remove(file_path);
            componentQueue.add(file_path);
        }

        return ret_val;
    }

    /**
     * Explicitly remove the named texture and image component from the cache.
     * If the objects have already been freed according to the rules of the
     * cache system, this request is silently ignored.
     *
     * @param filename The name the texture was registered under
     */
    public void releaseTexture(String filename)
    {
        textureMap.remove(filename);
        componentMap.remove(filename);

        textureQueue.remove(filename);
        componentQueue.remove(filename);
    }

    /**
     * Explicitly remove the named texture and image component from the cache.
     * If the objects have already been freed according to the rules of the
     * cache system, this request is silently ignored.
     *
     * @param url The URL the texture was registered under
     */
    public void releaseTexture(URL url)
    {
        String file_path = url.toExternalForm();
        textureMap.remove(file_path);
        componentMap.remove(file_path);

        textureQueue.remove(file_path);
        componentQueue.remove(file_path);
    }

    /**
     * Clear the entire cache now. It will be empty after this call, forcing
     * all fetch requests to reload the data from the source. Use with
     * caution.
     */
    public void clearAll()
    {
        textureMap.clear();
        componentMap.clear();

        textureQueue.clear();
        componentQueue.clear();
    }

    //------------------------------------------------------------------------
    // Local methods
    //------------------------------------------------------------------------

    /**
     * Add a texture to the maps. Perform checks to remove any items from the
     * queues if they are overlimit.
     *
     * @param name The name of the texture to add (the map key)
     * @return The Texture instance to store
     */
    private void addTexture(String name, Texture texture)
    {
        textureQueue.add(name);
        textureMap.put(name, texture);

        if(textureQueue.size() > maxCacheSize)
        {
            // we should only ever overstep by one
            Object reject = textureQueue.getNext();
            textureMap.remove(name);
        }
    }

    /**
     * Add a texture to the maps. Perform checks to remove any items from the
     * queues if they are overlimit.
     *
     * @param name The name of the component to add (the map key)
     * @return The ImageComponent instance to store
     */
    private void addImageComponent(String name, ImageComponent img)
    {
        componentQueue.add(name);
        componentMap.put(name, img);

        if(componentQueue.size() > maxCacheSize)
        {
            // we should only ever overstep by one
            Object reject = componentQueue.getNext();
            componentMap.remove(name);
        }
    }
}
