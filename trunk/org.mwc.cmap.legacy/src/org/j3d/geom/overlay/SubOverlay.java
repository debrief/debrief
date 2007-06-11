/*****************************************************************************
 *                        Teseract Software, LLP (c) 2001
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.overlay;

// Standard imports
import java.awt.geom.*;
import java.awt.*;

import javax.media.j3d.*;

import java.awt.image.Raster;
import java.awt.image.BufferedImage;

// Application specific imports
// none

/**
 * A SubOverlay is one of the pieces which displays a portion of the
 * overlay.  This is used internally by Overlay and should not be referenced
 * directly.
 *
 * @author David Yazel
 * @version $Revision: 1.1.1.1 $
 */
class SubOverlay
{
    /**
     * Represents that a buffer being activated or updated sould be the next
     * avaiable one.
     */
    final static int NEXT_BUFFER = -1;

    /** The list of images being used as the buffers */
    private BufferedImage[] buffer;

    /** The component version of the buffer images used by Java3D */
    private ImageComponent2D[] bufferHolder;

    /** The number of buffers we are using */
    private int numBuffers;

    /** The index of the currently active buffer */
    private int activeBufferIndex = 0;

    /** texture mapped to one double buffer */
    private Texture2D texture;

    /** Textured quad used to hold geometry */
    private Shape3D shape;

    /** The part of the overlay covered by this suboverlay */
    private Rectangle space;

    /** Used for transferring scan lines from main image to sub-image */
    private int[] transferBuffer;

    /**
     * Creates a double buffered suboverlay for the specified region that has
     * no transparency.
     *
     * @param space The area in screen space coords to create this for
     */
    SubOverlay(Rectangle space)
    {
        this(space, 2, false, null, null, null, null);
    }

    /**
     * Creates a suboverlay for the specified region that has a given number
     * of buffers and no transparency.
     *
     * @param space The area in screen space coords to create this for
     * @param numBuffers The number of buffers to create
     */
    SubOverlay(Rectangle space, int numBuffers)
    {
        this(space, numBuffers, false, null, null, null, null);
    }

    /**
     * Creates a double buffered suboverlay for the specified region with
     * the option to set the transparency.
     *
     * @param space The area in screen space coords to create this for
     * @param hasAlpha true If the overlay should include an alpha component
     */
    SubOverlay(Rectangle space, boolean hasAlpha)
    {
        this(space, 2, hasAlpha, null, null, null, null);
    }

    /**
     * Creates a buffered suboverlay for the specified region with
     * the option to set the transparency and number of buffers.
     *
     * @param space The area in screen space coords to create this for
     * @param numBuffers The number of buffers to create
     * @param hasAlpha true If the overlay should include an alpha component
     */
    SubOverlay(Rectangle space, int numBuffers, boolean hasAlpha)
    {
        this(space, numBuffers, hasAlpha, null, null, null, null);
    }

    /**
     * Creates the suboverlay with customisable attribute information. If any
     * parameter is null, defaults are used.
     *
     * @param space The area in screen space coords to create this for
     * @param numBuffers The number of buffers to create
     * @param hasAlpha true If the overlay should include an alpha component
     * @param polyAttr PolygonAttributes from the parent overlay
     * @param renderAttr RenderingAttributes from the parent overlay
     * @param texAttr TextureAttributes from the parent overlay
     * @param transAttr TransparencyAttributes from the parent overlay
     */
    SubOverlay(Rectangle space,
               int numBuffers,
               boolean hasAlpha,
               PolygonAttributes polyAttr,
               RenderingAttributes renderAttr,
               TextureAttributes texAttr,
               TransparencyAttributes transAttr)
    {
        this.space = space;
        this.numBuffers = numBuffers;
        buffer = new BufferedImage[numBuffers];
        bufferHolder = new ImageComponent2D[numBuffers];

        transferBuffer = new int[space.width];

        // create the two buffers

        int img_type = hasAlpha
                      ? ImageComponent2D.FORMAT_RGBA
                      : ImageComponent2D.FORMAT_RGB;
        Dimension tex_size =
            new Dimension(OverlayUtilities.smallestPower(space.width),
                          OverlayUtilities.smallestPower(space.height));

        for(int i = numBuffers - 1; i >= 0; i--)
        {
            buffer[i] = OverlayUtilities.createBufferedImage(tex_size, hasAlpha);
            bufferHolder[i] = new ImageComponent2D(img_type,
                                                   buffer[i],
                                                   true,
                                                   true);
        }

        Appearance appearance = new Appearance();

        appearance.setPolygonAttributes(polyAttr);
        appearance.setRenderingAttributes(renderAttr);
        appearance.setTextureAttributes(texAttr);
        appearance.setTransparencyAttributes(transAttr);

        Material material = new Material();
        material.setLightingEnable(false);
        appearance.setMaterial(material);

        texture = new Texture2D(Texture.BASE_LEVEL,
                                (hasAlpha ? Texture.RGBA : Texture.RGB),
                                tex_size.width,
                                tex_size.height);

        texture.setBoundaryModeS(Texture.WRAP);
        texture.setBoundaryModeT(Texture.WRAP);
        texture.setMagFilter(Texture.NICEST);
        texture.setMinFilter(Texture.FASTEST);
        texture.setImage(0, bufferHolder[activeBufferIndex]);
        texture.setCapability(Texture.ALLOW_IMAGE_WRITE);

        appearance.setTexture(texture);

        shape = buildShape(appearance, space);
    }

    /**
     * Draws the portion of fullOverlayImage corresponding to space into the
     * buffer at bufferIndex.
     *
     * @param fullOverlayImage The full image that we take a section from
     * @param bufferIndex The index of the buffer to use to do the update
     */
    void updateBuffer(BufferedImage fullOverlayImage, int bufferIndex)
    {
        // Ok, I have neve done this sort of thing before so I am going to have to step
        // through it. The image coming in is the entire overlay. There are two
        // problems with its current form. A it is too big and B the scan lines are
        // the reverse of what they need to be.
        //
        // So, we are going to read in lines from a subsection of the image and then
        // write them into the buffer in the opposite order of what they came in.

        int width = fullOverlayImage.getWidth();
        int height= fullOverlayImage.getHeight();

        if(bufferIndex == NEXT_BUFFER)
            bufferIndex = getNextBufferIndex();

        int y_pos = height - space.y - space.height;

        synchronized(buffer[bufferIndex])
        {
            // For each line in the output buffer
            for (int line = 0; line < space.height; line++)
            {
                // Copy the appropriate line out of the buffer
                fullOverlayImage.getRGB(space.x,
                                        y_pos + line,
                                        transferBuffer.length,
                                        1,
                                        transferBuffer,
                                        0,
                                        width);

                // Put the line into the output
                buffer[bufferIndex].setRGB(0,
                                           space.height - line - 1,
                                           transferBuffer.length,
                                           1,
                                           transferBuffer,
                                           0,
                                           width);
            }
        }
    }

    /**
     * Returns the index of the next buffer in line to be painted
     */
    int getNextBufferIndex()
    {
        return (activeBufferIndex + 1) % numBuffers;
    }

    /**
     * This will change the buffer being displayed. It does not write anything,
     * only switched the image so it must be used carefully. It is intended for
     * use where more than one buffer has been prepped ahead of time. If you
     * do this without having the buffers preprepped then you will get strange
     * things.
     */
    void setActiveBufferIndex(int newIndex)
    {
        if(newIndex == NEXT_BUFFER)
            newIndex = getNextBufferIndex();

        if(activeBufferIndex != newIndex)
        {
            activeBufferIndex = newIndex;
            texture.setImage(0, bufferHolder[activeBufferIndex]);
        }
    }

    /**
     * Return the underlying shape that this partial overlay is drawn on.
     *
     * @return The shape representing this texture
     */
    Shape3D getShape()
    {
        return shape;
    }

    /**
     * Builds a Shape3D with the specified appearance covering the specified rectangle.
     * The shape is created as a rectangle in the X,Y plane.
     *
     * @param appearance The appearance to use for the shape
     * @param texture The texture used for the buffer
     * @param space The coordinates of the shape to create
     * @return A shape3D object representing the given information
     */
    private Shape3D buildShape(Appearance appearance, Rectangle space)
    {
        int format = QuadArray.COORDINATES | QuadArray.TEXTURE_COORDINATE_2;
        QuadArray geom = new QuadArray(4, format);

        float[] vertices = {
            space.x + space.width, space.y,                 0,
            space.x + space.width, space.y + space.height,  0,
            space.x,               space.y + space.height,  0,
            space.x,               space.y,                 0
        };

        geom.setCoordinates(0, vertices);

        float w_ratio = space.width / texture.getWidth();
        float h_ratio = space.height / texture.getHeight();

        float[] textureCoordinates = {
            w_ratio, 0,
            w_ratio, h_ratio,
            0,       h_ratio,
            0,       0
        };

        geom.setTextureCoordinates(0, 0, textureCoordinates);

        Shape3D shape = new Shape3D();
        shape.setGeometry(geom);
        shape.setAppearance(appearance);

        return shape;
    }
}
