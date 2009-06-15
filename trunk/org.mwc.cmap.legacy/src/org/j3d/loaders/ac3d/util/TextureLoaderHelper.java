/*****************************************************************************
 *                        J3D.org Copyright (c) 2001
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 * This software comes with the standard NO WARRANTY disclaimer for any
 * purpose. Use it at your own risk. If there's a problem you get to fix it.
 *
 ****************************************************************************/

package org.j3d.loaders.ac3d.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.media.j3d.Texture2D;

import com.sun.j3d.utils.image.TextureLoader;


/**
 * <p><code>TextureLoaderHelper</code> provides a helper class that will load 
 * specified file images into textures objects from the Java 3D API. This 
 * will accomplish this by cycling through the image codes available to find 
 * one that will load the image type presented. If the type does not match any 
 * of the codecs presented, this will attempt to load the image by using the 
 * default Java platform toolkit that is provided by the system runtime.</p>
 *
 * <p><strong>TODO:</strong>
 * <ul>
 * <li> Deal with hackeyness of using the default toolkit in general... 
 *      The way of dealing with URLs and Files grates against my nerves...
 * </ul>
 *
 * @author  Ryan Wilhm (ryan@entrophica.com)
 * @version $Revision: 1.1.1.1 $
 */

public class TextureLoaderHelper implements ImageObserver {
    
    /** Specifies the base URL that the image resides in. */
    private URL baseUrl;
         
    
    /** 
     * <p>Default constructor.</p>
     */
    
    public TextureLoaderHelper() {
        baseUrl=null;
    }
    
    
    /**
     *
     * @param filename The relative or fully qualified filename of the 
     *                 image to load.
     * @return The loaded texture, or <code>null</code> if it could not be 
     *         acquired.
     */
    
    public Texture2D loadTexture(String filename) {
        URL theUrl;
        
        try {
            theUrl = new URL(filename);
        } catch (MalformedURLException e) {
            // Do nothing, silently fail (FOR NOW)
            theUrl=null;
        }
        
        return loadTextureFromQualified(theUrl);
    }
    
    
    /**
     *
     * @param url
     * @return The loaded texture, or <code>null</code> if it could not be 
     *         acquired.
     */
    
    public Texture2D loadTexture(URL url) {
        Texture2D rVal;
        
        rVal=loadTextureFromQualified(url);
        
        return rVal;
    }
    
    
    /**
     * <p>Loads and returns a fully prepared texture, or <code>null</code> 
     * if the image file could not be found. The process involves checking 
     * the different types of codecs that are available to the system. If 
     * a suitable codec is found, it is constructed and will be used to load 
     * the appropriate image. If one is not found, we will fall back on the 
     * default Java toolkit that is manufactured by the runtime system to 
     * try and load the image. Once loaded, this will invoke the 
     * <code>buildTexture()</code> method to build the texture from the 
     * <code>BufferedImage</code> using the specified attributes. If the 
     * process fails at any point, a <code>null</code> pointer will be 
     * returned.</p>
     * 
     * @param source The <code>URL</code> containing the source file.
     * @return The loaded texture, or <code>null</code> if it could not be 
     *         acquired.
     */
    
    private synchronized Texture2D loadTextureFromQualified(URL source) {
        BufferedImage bufferedImage=null;
        boolean luminance=false, alpha=false;
        Texture2D rVal=null;
        
        // Delegate image loading responsibility to Java Runtime
        bufferedImage=loadImageWithToolkit(source, this);
        
        if (bufferedImage!=null) {
            rVal=buildTexture(bufferedImage, luminance, alpha);
        }
        
        return rVal;
    }


    /**
     * <p>Builds a <code>Texture2D</code> object from the image and attributes 
     * passed in as arguements.</p>
     *
     * @param image The image to build the return texture with.
     * @param limunance Whether or not the texture has any luminance.
     * @param alpha Whether or not the texture supports transluscency.
     */
    
    private static final Texture2D buildTexture(BufferedImage image, 
        boolean luminance, boolean alpha) {
            
        String atts;
        Texture2D rVal;
        TextureLoader textureLoader;
        
        if (luminance && alpha) {
            atts = "LUM8_ALPHA8";
        } else if (luminance) {
            atts = "LUMINANCE";
        } else if (alpha) {
            atts = "RGBA";
        } else {
            atts = "RGB";
        }

        textureLoader = new TextureLoader(image, atts, 
            TextureLoader.GENERATE_MIPMAP);
        
        rVal = (Texture2D)textureLoader.getTexture();
        rVal.setMinFilter(Texture2D.MULTI_LEVEL_LINEAR);
        
        return rVal;
    }


    /**
     * <p>Utility method that uses the default Java runtime toolkit to 
     * load the specified image file. If the file is not found, 
     * <code>null</code> is returned instead.</p>
     *
     * @param source The location of the source material.
     * @param instance The instance that is attempting to perform the 
     *                 loading.
     * @return The image in a buffer, or <code>null</code> if the load 
     *         attempt was unsuccessful.
     */
    
    @SuppressWarnings("unchecked")
		private static final BufferedImage loadImageWithToolkit(Object source, 
        TextureLoaderHelper instance) {
            
        BufferedImage rVal=null;
        boolean doneLoading = false, loadError;
        int flags=0, width, height;
            
        // This is hackey... very hackey...
        final Image[] anonImage=new Image[1];
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final String anonFilename;
        final URL anonUrl;
        
        if (source instanceof File) {
            anonFilename=((File)source).toString();
            
            // Deal with access privledges in applet environment
            AccessController.doPrivileged(
                new PrivilegedAction() {
                    public Object run() {
                        anonImage[0] = toolkit.getImage(anonFilename);            
                        return null;
                    }
                }
            );
        } else if (source instanceof URL) {
            anonUrl=(URL)source;
            
            // Deal with access privledges in applet environment
            AccessController.doPrivileged(
                new PrivilegedAction() {
                    public Object run() {
                        anonImage[0] = toolkit.getImage(anonUrl);            
                        return null;
                    }
                }
            );            
        }
        
        
            
        toolkit.prepareImage(anonImage[0], -1, -1, instance);

        try {
            do {
                flags = toolkit.checkImage(anonImage[0], -1, -1, instance);
                doneLoading = (flags & (ALLBITS | ERROR | ABORT)) != 0;
                Thread.sleep(10);
            } while (!doneLoading);
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }

        loadError = (flags & (ERROR | ABORT)) != 0;
        if (doneLoading && !loadError ) {

            width = anonImage[0].getWidth(instance);
            height = anonImage[0].getHeight(instance);

            rVal = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
            
            int intPixels[] =
                ((DataBufferInt)rVal.getRaster().getDataBuffer()).getData();
            PixelGrabber pixelGrabber = new PixelGrabber(anonImage[0], 0, 0, 
                width, height, intPixels, 0, width);
             
            try {
                pixelGrabber.grabPixels();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }
        
        return rVal;
    }
    
    
    /**
     * <p>Mutator for the <code>basePath</code> property, which is used in 
     * resolving relative file locations.</p>
     *
     * @param baseURL The base path used to set the local state to.
     */
    
    public void setBasePath(String basePath) {
        try {
            this.baseUrl=new URL("file", "", basePath);
        } catch (MalformedURLException e) {
            // Do nothing, silently fail
        }
    }
    
    
    /**
     * <p>Accessor for the <code>basePath</code> property, which is used in 
     * resolving relative file locations.</p>
     *
     * @return The base path used to resolve against.
     */
    
    public String getBasePath() {
        return baseUrl.getFile();
    }
    
    
    /**
     * <p>Mutator for the <code>baseUrl</code> property, which is used in 
     * resolving relative URL locations.</p>
     *
     * @param baseUrl The base URL used to set the local state to.
     */
    
    public void setBaseUrl(URL baseUrl) {
        this.baseUrl=baseUrl;
    }
    
    
    /**
     * <p>Accessor for the <code>baseURL</code> property, which is used in 
     * resolving relative URL locations.</p>
     *
     * @return The base URL used to resolve against.
     */
    
    public URL getBaseUrl() {
        return baseUrl;
    }
    
    
    /**
     * <p>Returns whether or not this instance in finished processing the 
     * image. Actually, all we are interested in is whether or not we 
     * are finished loading the image, either by having all of the bits or 
     * by aborting prior to completion.</p>
     * 
     * @param img
     * @param infoflags The flags denoting the current state of the image.
     * @param x
     * @param y
     * @param width
     * @param height
     * @return Whether or not this instance is done with the image.
     * @see java.awt.ImageObserver
     */
    
    public boolean imageUpdate(Image img, int infoflags, int x, int y, 
        int width, int height) {        
            
        return ((infoflags & (ALLBITS | ABORT)) == 0);
    }
    
    
    /**
     * <p>Generates a stringified version of the current state of the 
     * object.</p>
     *
     * @return A stringified version of the current state of the object.
     */
    
    public String toString() {
        StringBuffer rVal=new StringBuffer();
        
        rVal.append("[ ");
        
        /*
        rVal.append(" flags={ ");
        if (checkFlag(flags, ALLBITS)) {
            rVal.append("ALLBITS ");
        }
        if (checkFlag(flags, ERROR)) {
            rVal.append("ERROR ");
        }
        if (checkFlag(flags, ABORT)) {
            rVal.append("ABORT ");
        }
        if (checkFlag(flags,FRAMEBITS)) {
            rVal.append("FRAMEBITS ");
        }
        if (checkFlag(flags, HEIGHT)) {
            rVal.append("HEIGHT ");
        }
        if (checkFlag(flags, PROPERTIES)) {
            rVal.append("PROPERTIES ");
        }
        if (checkFlag(flags, SOMEBITS)) {
            rVal.append("SOMEBITS ");
        }
        if (checkFlag(flags, WIDTH)) {
            System.err.println("WIDTH ");
        }
        rVal.append("}");
        */
        
        rVal.append(" ]");
        
        return rVal.toString();
    }

    
    /*
     * <p>Function for determining whether the flag (or ORed flags) in the 
     * signature are violated by the flags being checked against.</p>
     * 
     * @param flagsA The flags being checked.
     * @param flagsB The flags being checked against.
     * @return Whether or not the flags are violated.
    
    private static final boolean checkFlags(int flagsA, int flagsB) {
        return ((flagsA&flagsB)!=0);
    }
    */
    
}
