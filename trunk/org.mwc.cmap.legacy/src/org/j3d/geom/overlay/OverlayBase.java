/*****************************************************************************
 *                 Teseract Software, LLP Copyright(c)2001
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.geom.overlay;

// Standard imports
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color4f;
import javax.vecmath.Vector3d;

// Application specific imports
// none

/**
 * An implementation of the overlay and screen component interfaces to provide
 * a ready-made overlay system.
 * <P>
 *
 * The implementation uses textured objects that are mapped to screen space
 * coordinates. This can be both good and bad. The overlay can operate in
 * one of two modes - fixed size or dynamic size according to the canvas.
 * <P>
 * <B>Fixed Size Overlays</B>
 * <P>
 * The size should be set to a value that is a power of two for
 * the best performance. The code divides the supplied area into smaller
 * sections, with a maximum size of 256 pixels in either direction. Left over
 * pieces are then subdivided into lots that are power of two. The minimum size
 * of one of these pieces is 16 pixels. If you have an odd size, sich as 55
 * pixels, then you get weird artifacts appearing on screen.
 * <P>
 * <B>Resizable Overlays</B>
 * <P>
 * A resizable overlay is created when the bounds are set to null in the
 * constructor. In this case the overlay then listens for resizing information
 * from the component and resizes the internal subsections to accomodate this.
 * For this system, in order to remain visually accurate, we subdivide down to
 * shapes that are 1 pixel across. Obviously this impacts performance quite
 * dramatically to have so many tiny objects.
 * <P>
 * The class implements the AWT component listener interface so that it can
 * automatically resize the overlay's base image in response to the canvas
 * changing size. This will ensure that everything is correctly located on the
 * screen after the resize.
 * <P>
 *
 *
 * @author David Yazel, Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class OverlayBase
    implements Overlay,
               ScreenComponent,
               ComponentListener
{
    /** I do not undersand what this is for */
    private final static double CONSOLE_Z = 2.1f;

    /** The current background mode. Defaults to copy */
    protected int backgroundMode = BACKGROUND_COPY;

    /** Position of the overlay relative to the canvas */
    protected int[] relativePosition = {PLACE_LEFT, PLACE_TOP};

    /** The image that sits in the background of the overlay */
    private BufferedImage backgroundImage;
    private boolean hasAlpha;
    private boolean visible;
    private boolean antialiased;
    private int numBuffers;

    private final int minDivSize;

    /** Canvas bounds occupied by this overlay. */
    protected Rectangle overlayBounds;

    /** Offset in screen coordinates */
    private Dimension offset;

    /** The update manager for keeping us in sync */
    private UpdateManager updateManager;

    /** Canvas we are displayed on */
    private Canvas3D canvas3D;

    /** Drawing area that we scribble our stuff on */
    protected BufferedImage canvas;

    /** Background colour of the overlay */
    protected Color backgroundColor;

    /**
     * The list of sub-overlay areas. Starts pre-created with a zero length
     * array. Blocking is performed using this so it can't be null.
     */
    protected SubOverlay[] subOverlay;

    /** The currently active buffer index */
    protected int activeBuffer = SubOverlay.NEXT_BUFFER;

    /** Root branchgroup for the entire overlay system */
    protected BranchGroup consoleBG;

    /**
     * Contains the texture objects from the suboverlays. Each time the window
     * size changes, this instance is thrown away and replaced with a new one.
     * Ensures that we can change over the raster objects. Always set as child
     * 0 of the consoleBG.
     */
    protected BranchGroup overlayTexGrp;

    /** Transformation to make the raster become screen coords as well */
    protected TransformGroup consoleTG;

    // shared resources for the sub-overlays

    private RenderingAttributes renderAttributes;
    private PolygonAttributes polygonAttributes;
    private TextureAttributes textureAttributes;
    private TransparencyAttributes transparencyAttributes;

    // checks for altered elements

    public final static int DIRTY_VISIBLE = 0;
    public final static int DIRTY_POSITION = 1;
    public final static int DIRTY_ACTIVE_BUFFER = 2;
    public final static int DIRTY_SIZE = 3;

    /** List of the dirty flag settings */
    private boolean[] dirtyCheck = new boolean[DIRTY_SIZE + 1];

    /**
     * Flag indicating whether this is a fixed size or resizable overlay. Fixed
     * size is when the user gives us bounds. Resizable when they don't and we
     * track the canvas.
     */
    private boolean fixedSize;

    /** Fires appropriate mouse events */
    private ComponentMouseManager mouseManager;

    /** Used to avoid calls to repaint backing up */
    private boolean painting = false;

    /**
     * Creates a new overlay covering the given canvas bounds. It has two
     * buffers. Updates are managed automatically. This Overlay is not usable
     * until you attach it to the view platform transform. If the bounds are
     * null, then resize the overlay to fit the canvas and then track the size
     * of the canvas.
     *
     * @param canvas Canvas being drawn onto
     * @param bounds Bounds on the canvas covered by the overlay
     */
    public OverlayBase(Canvas3D canvas, Rectangle bounds)
    {
        this(canvas, bounds, true, false, null);
    }

    /**
     * Constructs an overlay window with an update manager. It has two buffers.
     * This window will not be visible unless it is added to the scene under
     * the view platform transform. If the bounds are null, then resize the
     * overlay to fit the canvas and then track the size of the canvas.
     *
     * @param canvas The canvas the overlay is drawn on
     * @param bounds The part of the canvas covered by the overlay
     * @param updateManager Responsible for allowing the Overlay to update
     *   between renders. If this is null a default manager is created
     */
    public OverlayBase(Canvas3D canvas, Rectangle bounds, UpdateManager manager)
    {
        this(canvas, bounds, true, false, manager);
    }

    /**
     * Constructs an overlay window that can have alpha capabilities. This
     * window will not be visible unless it is added to the scene under the
     * view platform transform. If the bounds are null, then resize the
     * overlay to fit the canvas and then track the size of the canvas.
     *
     * @param canvas The canvas the overlay is drawn on
     * @param bounds The part of the canvas covered by the overlay
     * @param clipAlpha Should the polygon clip where alpha is zero
     * @param blendAlpha Should we blend to background where alpha is < 1
     */
    public OverlayBase(Canvas3D canvas,
                       Rectangle bounds,
                       boolean clipAlpha,
                       boolean blendAlpha)
    {
        this(canvas, bounds, clipAlpha, blendAlpha, null);
    }

    /**
     * Constructs an overlay window. This window will not be visible
     * unless it is added to the scene under the view platform transform
     * If the bounds are null, then resize the overlay to fit the canvas
     * and then track the size of the canvas.
     *
     * @param canvas The canvas the overlay is drawn on
     * @param bounds The part of the canvas covered by the overlay
     * @param clipAlpha Should the polygon clip where alpha is zero
     * @param blendAlpha Should we blend to background where alpha is < 1
     * @param updateManager Responsible for allowing the Overlay to update
     *   between renders. If this is null a default manager is created
     */
    public OverlayBase(Canvas3D canvas,
                       Rectangle bounds,
                       boolean clipAlpha,
                       boolean blendAlpha,
                       UpdateManager updateManager)
    {
        this(canvas, bounds, clipAlpha, blendAlpha, updateManager, 2);
    }

    /**
     * Constructs an overlay window. This window will not be visible
     * unless it is added to the scene under the view platform transform
     * If the bounds are null, then resize the overlay to fit the canvas
     * and then track the size of the canvas.
     *
     * @param canvas The canvas the overlay is drawn on
     * @param bounds The part of the canvas covered by the overlay
     * @param clipAlpha Should the polygon clip where alpha is zero
     * @param blendAlpha Should we blend to background where alpha is < 1
     * @param updateManager Responsible for allowing the Overlay to update
     *   between renders. If this is null a default manager is created
     * @param numBuffers The number of buffers to generate, the default is two
     */
    public OverlayBase(Canvas3D canvas,
                       Rectangle bounds,
                       boolean clipAlpha,
                       boolean blendAlpha,
                       UpdateManager updateManager,
                       int numBuffers)
    {
        this.numBuffers = numBuffers;

        canvas3D = canvas;

        if(bounds == null)
        {
            overlayBounds = canvas.getBounds();
            fixedSize = false;
            minDivSize = 1;
        }
        else
        {
            overlayBounds = bounds;
            fixedSize = true;
            minDivSize = 16;
        }

        visible = true;
        antialiased = true;
        offset = new Dimension(overlayBounds.x, overlayBounds.y);
        hasAlpha = clipAlpha || blendAlpha;

        if(overlayBounds.width != 0 && overlayBounds.height != 0)
        {
            this.canvas =
                OverlayUtilities.createBufferedImage(overlayBounds.getSize(),
                                                     hasAlpha);
        }

        mouseManager = new ComponentMouseManager(canvas3D, this);

        canvas3D.addComponentListener(this);

        // define the branch group where we are putting all the sub-overlays

        consoleBG = new BranchGroup();
        consoleBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

        overlayTexGrp = new BranchGroup();
        overlayTexGrp.setCapability(BranchGroup.ALLOW_DETACH);

        consoleTG = new TransformGroup();
        consoleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        overlayTexGrp.addChild(consoleTG);
        consoleBG.addChild(overlayTexGrp);

        if(updateManager == null)
        {
            UpdateControlBehavior updateBehavior = new UpdateControlBehavior(this);
            updateBehavior.setSchedulingBounds(new BoundingSphere());
            consoleBG.addChild(updateBehavior);
            this.updateManager = updateBehavior ;
        }
        else
            this.updateManager = updateManager;

        // define the rendering attributes used by all sub-overlays
        renderAttributes = new RenderingAttributes();
        if(clipAlpha)
        {
            renderAttributes.setAlphaTestFunction(RenderingAttributes.NOT_EQUAL);
            renderAttributes.setAlphaTestValue(0);
        }

        renderAttributes.setDepthBufferEnable(true);
        renderAttributes.setDepthBufferWriteEnable(true);
        renderAttributes.setIgnoreVertexColors(true);
        renderAttributes.setCapability(RenderingAttributes.ALLOW_VISIBLE_READ);
        renderAttributes.setCapability(RenderingAttributes.ALLOW_VISIBLE_WRITE);

        // define the polygon attributes for all the sub-overlays
        polygonAttributes = new PolygonAttributes();
        polygonAttributes.setBackFaceNormalFlip(false);
        polygonAttributes.setCullFace(PolygonAttributes.CULL_NONE);
        polygonAttributes.setPolygonMode(PolygonAttributes.POLYGON_FILL);

        // define the texture attributes for all the sub-overlays
        textureAttributes = new TextureAttributes();
        textureAttributes.setTextureMode(TextureAttributes.REPLACE);
        textureAttributes.setPerspectiveCorrectionMode(TextureAttributes.FASTEST);

        // if this needs to support transparancy set up the blend
        if(hasAlpha)
        {
            transparencyAttributes =
                new TransparencyAttributes(TransparencyAttributes.BLENDED,
                                           1.0f);
            textureAttributes.setTextureBlendColor(new Color4f(0, 0, 0, 1));
        }

        List<Rectangle> overlays =
            OverlayUtilities.subdivide(overlayBounds.getSize(), minDivSize, 256);

        subOverlay = new SubOverlay[overlays.size()];
        int n = overlays.size();

        for(int i = 0; i < n; i++)
        {
            Rectangle current_space = (Rectangle)overlays.get(i);
            subOverlay[i] = new SubOverlay(current_space,
                                           numBuffers,
                                           hasAlpha,
                                           polygonAttributes,
                                           renderAttributes,
                                           textureAttributes,
                                           transparencyAttributes);

            consoleTG.addChild(subOverlay[i].getShape());
        }

        // Dirty everything and an initial WakeupOnActivation will sync everything
        dirtyCheck[DIRTY_VISIBLE] = true;
        dirtyCheck[DIRTY_POSITION] = true;
        dirtyCheck[DIRTY_ACTIVE_BUFFER] = true;
    }

    /**
     * Returns the UpdateManager responsible for seeing that updates to the
     * Overlay only take place between frames.
     *
     * @param The update manage instance for this overlay
     */
    public UpdateManager getUpdateManager()
    {
        return updateManager;
    }

    /**
     * Set the UpdateManager to the new value. If the reference is null, it
     * will clear the current manager.
     *
     * @param updateManager A reference to the new manage instance to use
     */
    public void setUpdateManager(UpdateManager updateManager)
    {
        this.updateManager = updateManager;
        updateManager.updateRequested();
    }

    /**
     * Sets the relative offset of the overlay. How this translates into
     * screen coordinates depends on the value of relativePosition()
     */
    public void setOffset(Dimension offset)
    {
        setOffset(offset.width, offset.height);
    }

    /**
     * Sets the relative offset of the overlay. How this translates into
     * screen coordinates depends on the value of relativePosition()
     *
     * @param width The width (X axis in 3D space) position
     * @param height The height (Y axis in 3D space) position
     */
    public void setOffset(int width, int height)
    {
        if(offset.width != width || offset.height != height)
        {
            synchronized(offset)
            {
                offset.width = width;
                offset.height = height;
                dirty(DIRTY_POSITION);
            }
        }
    }

    /**
     * Sets the relative position of the overlay on the screen using a 2 dimensional array.
     *
     * @param relativePosition[X_PLACEMENT] May be PLACE_LEFT, PLACE_RIGHT, or PLACE_CENTER
     * @param relativePosition[Y_PLACEMENT] May be PLACE_TOP, PLACE_BOTTOM, or PLACE_CENTER
     */
    public void setRelativePosition(int[] relativePositon)
    {
        setRelativePosition(relativePosition[X_PLACEMENT],
                            relativePosition[Y_PLACEMENT]);
    }

    /**
     * Sets the relative position of the overlay on the screen.
     *
     * @param xType May be PLACE_LEFT, PLACE_RIGHT, or PLACE_CENTER
     * @param yType May be PLACE_TOP, PLACE_BOTTOM, or PLACE_CENTER
     */
    public void setRelativePosition(int xType, int yType)
    {
        if(relativePosition[X_PLACEMENT] != xType ||
           relativePosition[Y_PLACEMENT] != yType)
        {
            relativePosition[X_PLACEMENT] = xType;
            relativePosition[Y_PLACEMENT] = yType;
            dirty(DIRTY_POSITION);
        }
    }

    /**
     * Return the root of the overlay and its sub-overlays so it can be
     * added to the scene graph. This should be added to the view transform
     * group of the parent application.
     *
     * @return The J3D branch group that holds the overlay
     */
    public BranchGroup getRoot()
    {
        return consoleBG;
    }

    /**
     * Sets whether drawing onto this Overlay is anialiased.
     *
     * @param antialiased The new setting for anti-aliasing.
     */
    public void setAntialiased(boolean antialiased)
    {
        if(this.antialiased != antialiased)
        {
            this.antialiased = antialiased;
            repaint();
        }
    }

    /**
     * Check to see whether this overlay is currently antialiased.
     *
     * @return true if this overlay is antialiased
     */
    public boolean isAntialiased()
    {
        return antialiased;
    }

    /**
     * Returns the canvas being drawn on.
     *
     * @return The current canvas instance
     */
    public Canvas3D getCanvas()
    {
        return canvas3D;
    }

    /**
     * Changes the visibility of the overlay.
     *
     * @param visible The new visibility state
     */
    public void setVisible(boolean visible)
    {
        if(this.visible != visible)
        {
            this.visible = visible;
            dirty(DIRTY_VISIBLE);
        }
    }

    /**
     * Returns the visiblity of the Overlay.
     *
     * @return true if the overlay is currently visible
     */
    public boolean isVisible()
    {
        return visible;
    }

    /**
     * Sets the background to a solid color. If a background image already exists then
     * it will be overwritten with this solid color.  It is completely appropriate to
     * have an alpha component in the color if this is a alpha capable overlay.
     * In general you should only use background images if this is an overlay that is
     * called frequently, since you could always paint it inside the paint()method.
     * BackgroundMode must be in BACKGROUND_COPY for the background to be shown.
     *
     * @param color The new color to use
     */
    public void setBackgroundColor(Color color)
    {
        backgroundColor = color;

        if(overlayBounds.width == 0 || overlayBounds.height == 0)
            return;

        updateBackgroundColor();
    }

    /**
     * Returns the background for the overlay. Updates to this image will not
     * be shown in the overlay until repaint()is called.
     * BackgroundMode must be in BACKGROUND_COPY for the background to be shown.
     *
     * @return The image used as the background
     */
    public BufferedImage getBackgroundImage()
    {
        if(backgroundImage == null)
        {
            backgroundImage =
                OverlayUtilities.createBufferedImage(overlayBounds.getSize(),
                                                     hasAlpha);
        }
        return backgroundImage;
    }

    /**
     * Sets the background image to the one specified.  It does not have to be
     * the same size as the overlay but the it should be at least as big.
     * BackgroundMode must be in BACKGROUND_COPY for the background to be shown.
     */
    public void setBackgroundImage(BufferedImage img)
    {
        if(backgroundImage != img)
        {
            backgroundImage = img;
            repaint();
        }
    }

    /**
     * Sets the background mode.  BACKGROUND_COPY will copy the raster data from the
     * background into the canvas before paint()is called. BACKGROUND_NONE will cause
     * the background to be disabled and not used.
     *
     * @param mode The new mode to use for the background
     */
    public void setBackgroundMode(int mode)
    {
        if(backgroundMode != mode)
        {
            backgroundMode = mode;
            repaint();
        }
    }

    //------------------------------------------------------------------------
    // Methods from the UpdatableEntity interface
    //------------------------------------------------------------------------

    /**
     * Notification from the update manager that something has changed and we
     * should fix up the appropriate bits.
     */
    public void update()
    {
        // Always size first as that may reset the position and we don't need
        // to calculate the position twice.
        if(dirtyCheck[DIRTY_SIZE])
            syncSize();

        if(dirtyCheck[DIRTY_POSITION])
            syncPosition();

        if(dirtyCheck[DIRTY_VISIBLE])
            syncVisible();

        if(dirtyCheck[DIRTY_ACTIVE_BUFFER])
            syncActiveBuffer();
    }

    //------------------------------------------------------------------------
    // Methods from the ScreenComponent interface
    //------------------------------------------------------------------------

    /**
     * Get the bounds of the visible object in screen space coordinates.
     *
     * @return A rectangle representing the bounds in screen coordinates
     */
    public Rectangle getBounds()
    {
        return overlayBounds;
    }

    //------------------------------------------------------------------------
    // Methods from the ComponentListener interface
    //------------------------------------------------------------------------

    /**
     * Notification that the component has been resized.
     *
     * @param e The event that caused this method to be called
     */
    public void componentResized(ComponentEvent e)
    {
        if(fixedSize)
            dirty(DIRTY_POSITION);
        else
            dirty(DIRTY_SIZE);
    }

    /**
     * Notification that the component has been moved.
     *
     * @param e The event that caused this method to be called
     */
    public void componentMoved(ComponentEvent e)
    {
        dirty(DIRTY_POSITION);
    }

    /**
     * Notification that the component has been shown. This is the component
     * being shown, not the window that it is contained in.
     *
     * @param e The event that caused this method to be called
     */
    public void componentShown(ComponentEvent e)
    {
        repaint();
    }

    /**
     * Notification that the component has been hidden.
     *
     * @param e The event that caused this method to be called
     */
    public void componentHidden(ComponentEvent e)
    {
    }

    //------------------------------------------------------------------------
    // Local convenience methods
    //------------------------------------------------------------------------

    /**
     * Empty method that can be used to provide post construction
     * initialisation. Never called by the overlay code, but derived overlays
     * may make use of it.
     */
    public void initialize()
    {
    }

    /**
     * Add a new mouse listener to this overlay. Each instance can only be
     * added once and null values are ignored.
     *
     * @param listener The listener instance to add
     */
    public void addMouseListener(MouseListener listener)
    {
        mouseManager.addMouseListener(listener);
    }

    /**
     * Remove a previously registered mouse listener. If the listener is not
     * registered or null is passed, the request is silently ignored.
     *
     * @param listener The listener instance to remove
     */
    public void removeMouseListener(MouseListener listener)
    {
        mouseManager.removeMouseListener(listener);
    }

    /**
     * Mark a specific property as being dirty and needing to be rechecked.
     *
     * @param property The index of the property to be updated
     */
    protected void dirty(int property)
    {
        dirtyCheck[property] = true;
        if(updateManager != null)
            updateManager.updateRequested();
        else
            System.err.println("Null update manager in: " + this);
    }

    /**
     * Set the active buffer to the new index.
     *
     * @param bufferIndex The index of the buffer to use
     */
    protected void setActiveBuffer(int bufferIndex)
    {
        activeBuffer = bufferIndex;
        dirty(DIRTY_ACTIVE_BUFFER);
    }

    /**
     * Prepares the canvas to be painted.  This should only be called internally
     * or from an owner like the ScrollingOverlay class. paint(Graphics2D g)
     * should be used to paint the OverlayBase.
     *
     * @return The current graphics context to work with
     */
    protected Graphics2D getGraphics()
    {
        if(backgroundMode == BACKGROUND_COPY && backgroundImage != null)
            canvas.setData(backgroundImage.getRaster());

        Graphics2D g = (Graphics2D)canvas.getGraphics();

        if(antialiased)
        {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                               RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        return g;
    }

    /**
     * This is where the actualy drawing of the window takes place.  Override
     * this to alter the contents of what is shown in the window.
     *
     * @param g The graphics context to paint with
     */
    public void paint(Graphics2D g)
    {
    }

    /**
     * This is called to trigger a repaint of the overlay. This will return once
     * the back buffer has been built, but before the swap.
     */
    public void repaint()
    {
        if(!painting)
        {
            painting = true;

            Graphics2D g = getGraphics();
            paint(g);
            g.dispose();

            updateBuffer(canvas, SubOverlay.NEXT_BUFFER);
            setActiveBuffer(SubOverlay.NEXT_BUFFER);

            painting = false;
        }
        else
        {
            System.err.println("Skipped paint in: " + this);
        }
    }

    /**
     * Force an update of the nominated buffer with the contents of the given
     * image.
     *
     * @param image The contents to display as the image
     * @param bufferIndex The buffer to update
     */
    protected void updateBuffer(BufferedImage image, int bufferIndex)
    {
        synchronized(subOverlay)
        {
            for(int i = subOverlay.length - 1; i >= 0; i--)
            {
                subOverlay[i].updateBuffer(image, bufferIndex);
            }
        }
    }

    /**
     * Update the background colour on the drawn image now.
     */
    private void updateBackgroundColor()
    {
        int pixels[] = new int[overlayBounds.width * overlayBounds.height];
        int rgb = backgroundColor.getRGB();
        for(int i = pixels.length - 1; i >= 0; i--)
        {
            pixels[i] = rgb;
        }

        getBackgroundImage().setRGB(0,
                                    0,
                                    overlayBounds.width,
                                    overlayBounds.height,
                                    pixels,
                                    0,
                                    overlayBounds.width);
        repaint();
    }

    /**
     * Update the visibility state to either turn on or off the overlay.
     */
    private void syncVisible()
    {
        renderAttributes.setVisible(visible);
        dirtyCheck[DIRTY_VISIBLE] = false;
    }

    /**
     * Update the active buffer to be the new index. Means that someone has
     * requested an update and this is making it happen.
     */
    private void syncActiveBuffer()
    {
        synchronized(subOverlay)
        {
            for(int i = subOverlay.length - 1; i >= 0; i--)
                subOverlay[i].setActiveBufferIndex(activeBuffer);

            dirtyCheck[DIRTY_ACTIVE_BUFFER] = false;
        }
    }

    /**
     * Update the position of the overlay in the overall window. Note that it
     * does not change the size of the overlay, just re-adjusts the transforms
     * in the scene graph so that the overlay maintains the correct position
     * relative the canvas.
     */
    private void syncPosition()
    {
        synchronized(overlayBounds)
        {
            Dimension canvas_size = canvas3D.getSize();

            if(canvas_size.width == 0 || canvas_size.height == 0)
                return;

            if(canvas == null)
                canvas = OverlayUtilities.createBufferedImage(overlayBounds.getSize(),
                                                              hasAlpha);

            if(backgroundColor != null)
                updateBackgroundColor();

            OverlayUtilities.repositonBounds(overlayBounds,
                                             relativePosition,
                                             canvas_size,
                                             offset);

            // get the field of view and then calculate the width in meters of the
            // screen

            double fov = canvas3D.getView().getFieldOfView();
            double c_width = 2 * CONSOLE_Z * Math.tan(fov / 2.0);

            // calculate the ratio between the canvas in pixels and the screen in
            // meters and use that to find the height of the screen in meters

            double scale = c_width / canvas_size.getWidth();
            double c_height = canvas_size.getHeight()* scale;

            // The texture is upside down relative to the canvas so this has to
            // be flipped to be in the right place. bounds needs to have the correct
            // value to be used in Overlays that relay on it to know their position
            // like mouseovers

            Point flipped_pt=
                new Point(overlayBounds.x,
                          canvas_size.height - overlayBounds.height - overlayBounds.y);

            // build the plane offset

            Transform3D plane_offset = new Transform3D();
            Vector3d loc =
                new Vector3d(-c_width / 2 + flipped_pt.getX()* scale,
                             -c_height / 2 + flipped_pt.getY()* scale,
                             -CONSOLE_Z);

            plane_offset.setTranslation(loc);
            plane_offset.setScale(scale);
            consoleTG.setTransform(plane_offset);

            dirtyCheck[DIRTY_POSITION] = false;
        }
    }

    /**
     * Fixup the size of the overlay textures. Resizes and clears the texture
     * to fit the new size. Current implementation is really dumb - just tosses
     * everything and starts again. A more intelligent one would only replace
     * the border parts.
     */
    private void syncSize()
    {
        if(!fixedSize)
            overlayBounds = canvas3D.getBounds();

        if((overlayBounds.width != 0) && (overlayBounds.height != 0))
        {
            overlayTexGrp = new BranchGroup();
            overlayTexGrp.setCapability(BranchGroup.ALLOW_DETACH);

            consoleTG = new TransformGroup();
            consoleTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

            overlayTexGrp.addChild(consoleTG);

            List<Rectangle> overlays = OverlayUtilities.subdivide(overlayBounds.getSize(),
                                                       minDivSize,
                                                       256);

            subOverlay = new SubOverlay[overlays.size()];
            int n = overlays.size();

            for(int i = 0; i < n; i++)
            {
                Rectangle current_space = (Rectangle)overlays.get(i);
                subOverlay[i] = new SubOverlay(current_space,
                                               numBuffers,
                                               hasAlpha,
                                               polygonAttributes,
                                               renderAttributes,
                                               textureAttributes,
                                               transparencyAttributes);
                consoleTG.addChild(subOverlay[i].getShape());
            }

            consoleBG.setChild(overlayTexGrp, 0);
        }
        else
        {
            overlayTexGrp = null;
            consoleTG = null;
            subOverlay = new SubOverlay[0];

            consoleBG.setChild(null, 0);
        }


        dirtyCheck[DIRTY_SIZE] = false;

        // now sync the position again as we've replaced the transform
        syncPosition();
    }
}
