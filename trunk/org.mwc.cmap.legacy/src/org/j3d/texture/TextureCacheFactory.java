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
import java.io.IOException;
import java.net.URL;

import javax.media.j3d.ImageComponent;
import javax.media.j3d.Texture;

// Application specific imports
import org.j3d.util.IntHashMap;

/**
 * A representation of global cache for texture instance management.
 * <p>
 *
 * The cache works at the Java3D Texture or ImageComponent instance level
 * rather than down at the individual images. This allows the VM to discard
 * the lower level image instances if needed, allowing Java3D to do its own
 * management. In addition, it benefits runtime performance of the Java3D
 * scene graph by allowing textures instances to be shared, rather than
 * duplicated.
 * <p>
 *
 * Different types of cache implementations are allowed (ie different ways of
 * deciding when an texture no longer needs to be in the cache).
 * <p>
 *
 * The factory also supports the concept of the "default cache". This is used
 * when you want a simple system that doesn't really care about the cache type
 * used and just wants to use this class as a global singleton for storing the
 * texture information. The default cache type can be controlled through either
 * directly setting the value in this class, or using a system property. By
 * defining a value for the property
 * <pre>
 *   org.j3d.texture.DefaultCacheType
 * </pre>
 *
 * with one of the values (case-sensitive) <code>fixed</code>, <code>lru</code>
 * or <code>weakref</code>. Setting the type through the method call will
 * override this setting. However, the cache type can only be set once. All
 * further attempts will result in an exception.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class TextureCacheFactory
{
    /** ID of the unset cache type */
    private static final int NO_CACHE_SET = -1;

    /** ID for a fixed contents cache implementation */
    public static final int FIXED_CACHE = 1;

    /** ID for a Least-Recently-Used cache implementation */
    public static final int LRU_CACHE = 2;

    /** ID for a cache implementation using weak references */
    public static final int WEAKREF_CACHE = 3;

    /** The last ID for global, inbuilt cache types */
    public static final int LAST_CACHE_ID = 10;

    /** Default cache type if nothing else is set */
    private static final int DEFAULT_CACHE_ID = FIXED_CACHE;

    /** The system property name */
    public static final String DEFAULT_CACHE_PROP =
        "org.j3d.texture.DefaultCacheType";

    /** Mapping of cacheType int to the cache implementation */
    private static IntHashMap cacheMap;

    /** The ID of the default cache type */
    private static int defaultCacheType;

    /**
     * Static initialiser to set up the class vars as needed.
     */
    static
    {
        cacheMap = new IntHashMap();
        defaultCacheType = NO_CACHE_SET;
    }

    /**
     * Private constructor to prevent direct instantiation of this class.
     */
    private TextureCacheFactory()
    {
    }

    /**
     * Set the default cache type to be used.
     *
     * @param type The default type ID
     * @throws CacheAlreadySetException The default type has already been set
     */
    public static void setDefaultCacheType(int type)
        throws CacheAlreadySetException
    {
        if(defaultCacheType != NO_CACHE_SET)
            throw new CacheAlreadySetException();

        defaultCacheType = type;
    }

    /**
     * Fetch the default cache provided by the factory. The type may be
     * previously specified using the system property or the
     * {@link #setDefaultCacheType(int)} method.
     *
     * @return The default cache implementation
     */
    public static TextureCache getCache()
    {
        if(defaultCacheType == NO_CACHE_SET)
        {
            // look up the system property
            String str = System.getProperty(DEFAULT_CACHE_PROP);

            if(str == null)
                defaultCacheType = DEFAULT_CACHE_ID;
            else if(str.equals("fixed"))
                defaultCacheType = FIXED_CACHE;
            else if(str.equals("lru"))
                defaultCacheType = LRU_CACHE;
            else if(str.equals("weakref"))
                defaultCacheType = WEAKREF_CACHE;
            else
                defaultCacheType = DEFAULT_CACHE_ID;
        }

        return getCache(defaultCacheType);
    }

    /**
     * Fetch the cache instance for the given type, creating a new instance
     * if necessary. If the cacheType refers to one of the standard, inbuilt
     * types then it will be automatically generated. If it is not a standard
     * type, an exception will be generated.
     *
     * @param cacheType An identifier of the required caching algorithm
     * @return A reference to the global cache of that type
     * @throws IllegalArgumentException The cacheType is not a valid type
     */
    public static TextureCache getCache(int cacheType)
    {
        TextureCache ret_val = (TextureCache)cacheMap.get(cacheType);

        if(ret_val == null)
        {
            switch(cacheType)
            {
                case FIXED_CACHE:
                    ret_val = new FixedTextureCache();
                    cacheMap.put(FIXED_CACHE, ret_val);
                    break;

                case LRU_CACHE:
                    ret_val = new LRUTextureCache();
                    cacheMap.put(LRU_CACHE, ret_val);
                    break;

                case WEAKREF_CACHE:
                    ret_val = new WeakRefTextureCache();
                    cacheMap.put(WEAKREF_CACHE, ret_val);
                    break;

                default:
                    throw new IllegalArgumentException("Invalid cache type");
            }
        }

        return ret_val;
    }

    /**
     * Register your custom instance of a texture cache. If the cacheType has
     * a value less than or equal to the last ID then an exception will be
     * generated. The ID, if it already exists will replace the existing
     * instance with the new one. Passing a value of null will de-register
     * the existing cache if previously registered. Standard types cannot be
     * re-registered.
     *
     * @param cacheType The ID to associate with this cache
     * @param cache The instance of the cache to register
     * @throws IllegalArgumentException The cacheType is invalid
     */
    public static void registerCacheType(int cacheType, TextureCache cache)
    {
        if(cacheType <= LAST_CACHE_ID)
            throw new IllegalArgumentException("Invalid cacheType value");

        cacheMap.put(cacheType, cache);
    }
}
