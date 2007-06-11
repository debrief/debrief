package org.j3d.geom.overlay;

import javax.media.j3d.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.event.EventListenerList;

/**
 * A scrolling overlay is built on top of the overlay system.  It maintains
 * optional overlays for the borders of the overlay area and an array of
 * equal size overlays to represent the lines.  This is designed to be fast enough to
 * support fast scrolling text and implementations like a chat box or debug window.
 * A single scroll will effectively swap textures on geometry and stream one overlay's
 * worth of data to the 3d card, so it should be maximally efficient.
 *
 * The design of this implementation is to keep an array of overlays one per virtual line from
 * top to bottom inside the overlay.  Because the position swaps (for scrolling) has to happen
 * within a single behavior to guarentee its transactional status, a single repaint() of the
 * OverlayScroller must set the re-order for the lines, even though the actual re-order
 * won't happen until the behavior triggers (on next frame).  We also have to handle the
 * case where several repaints happen between one frame.  In order to facilitate this, we keep
 * an array of double-indirection, mapping overlay-lines to virtual lines, then unwind them
 * when we commit the transaction to the card.
 *
 * So when we first start, or after a commit to the card, you would have a one-to-one mapping
 * of virutal lines to overlays.  If you issue a scroll-up or scroll-down
 * directive, we just adjust the virtual pointers, mapping the first virtual line to the bottom
 * overlay and adjusting from there.  This means you can do multiple scrolls between frames and
 * the buffer maintains consistancy.  If you want a line repaint and a scroll action to be handled
 * atomically, then you should call update() on the scroller.  This will in-turn lock the
 * overlay for update and call the paint() method, which you implement.  This is a bit different
 * than the paint method used at the overlay level since you are not given a graphics context.
 * But from within this method you can call the scroll() and scrollDown() methods and request
 * a graphics context for any virtual line.  You will be writing to the backBuffer of the
 * individual overlay's so it is safe, also none of the line-overlays will commit until you
 * exit the paint() method.
 *
 * Copyright:  Copyright (c) 2000,2001
 * Company:    Teseract Software, LLP
 * @author David Yazel
 *
 */

public class OverlayScroller implements Overlay, UpdateManager {
    public static final int BORDER_LEFT = 0;
    public static final int BORDER_RIGHT = 1;
    public static final int BORDER_TOP = 2;
    public static final int BORDER_BOTTOM = 3;
    
    private Rectangle bounds;
    private Insets margin;
    private int[] relativePosition = {Overlay.PLACE_LEFT, Overlay.PLACE_TOP};

    private Dimension offset;             // Screen position
    private boolean visible = true;
    private Color borderBackgroundColor;
    private BufferedImage borderBackgroundImage;
    private int backgroundMode;
    private boolean antialiased = true;
    private Canvas3D canvas3D;
    
    // title overlays
    private OverlayBase[] border;
    private Overlay[] line;
    
    // On an update each property is checked to see if it is dirty,
    //  if it is an appropriate update is made

    private final static int VISIBLE =                 Integer.parseInt("1", 2);
    private final static int ORDER =                   Integer.parseInt("10", 2);
    private final static int LINE_POSITION =           Integer.parseInt("100", 2);
    private final static int BORDER_POSITION =         Integer.parseInt("1000", 2);
    private final static int BORDER_BACKGROUND_IMAGE = Integer.parseInt("10000", 2);
    private final static int BORDER_BACKGROUND_COLOR = Integer.parseInt("100000", 2);
    private final static int BACKGROUND_MODE =         Integer.parseInt("1000000", 2);
    private final static int ANTIALIASED =             Integer.parseInt("10000000", 2);
    private final static int ITEM =                    Integer.parseInt("100000000", 2);

    private int dirtyCheck = 0;
    
    UpdateManager updateManager;

    private EventListenerList listeners = new EventListenerList();  // Set of ScrollEventListeners
    private BranchGroup consoleBranchGroup;                         // branch group for overlay
    
    public OverlayScroller( Canvas3D canvas3D, Overlay[] line, Insets margin ) {
	this(canvas3D, new Dimension(), null, line, margin);
    }

    public OverlayScroller( Canvas3D canvas3D, Dimension offset, Overlay[] line, Insets margin ) {
	this(canvas3D, offset, null, line, margin);
    }

    public OverlayScroller( Canvas3D canvas3D, UpdateManager manager,
			     Overlay[] line, Insets margin ) {
	this(canvas3D, true, false, new Dimension(), manager, line, margin);
    }

    public OverlayScroller( Canvas3D canvas3D, Dimension offset,
			     UpdateManager manager,
			     Overlay[] line, Insets margin ) {
	this(canvas3D, true, false, offset, manager, line, margin);
    }

    public OverlayScroller( Canvas3D canvas3D,
			     boolean clipAlpha, boolean blendAlpha,
			     Dimension offset,
			     UpdateManager manager,
			     Overlay[] line, Insets margin) {
	this.canvas3D = canvas3D;
	this.margin = margin;
	this.line = line;
	this.offset = offset;

	canvas3D.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    dirty(LINE_POSITION | BORDER_POSITION);
                }
            });

	Dimension internalSize = new Dimension();
	for(int i = 0; i < line.length; i++) {
	    internalSize.width = Math.max(internalSize.width, line[i].getBounds().width);
	    internalSize.height += line[i].getBounds().height;
	}
	    
	bounds = new Rectangle(offset.width, offset.height,
			      margin.left + internalSize.width + margin.right,
			      margin.top + internalSize.height + margin.bottom);
	
	Rectangle borderBounds = null;
	border = new OverlayBase[4];
	for(int i = 0; i < border.length; i++) {
	    switch (i) {
	    case BORDER_LEFT:
		borderBounds = new Rectangle(0, 0, margin.left, internalSize.height);
		break;
	    case BORDER_RIGHT:
		borderBounds = new Rectangle(0, 0, margin.right, internalSize.height);
		break;
	    case BORDER_TOP:
		borderBounds = new Rectangle(0, 0, bounds.width, margin.top);
		break;
	    case BORDER_BOTTOM:
		borderBounds = new Rectangle(0, 0, bounds.width, margin.bottom);
		break;
	    }
	    border[i] = new OverlayBase(canvas3D, borderBounds, clipAlpha, blendAlpha);
	}
	
	consoleBranchGroup = new BranchGroup();
	
	if(updateManager == null) {
	    UpdateControlBehavior updateBehavior = new UpdateControlBehavior(this);
            updateBehavior.setSchedulingBounds(new BoundingSphere());
            consoleBranchGroup.addChild(updateBehavior);
	    updateManager = updateBehavior;
	}

	for (int i = line.length - 1; i >= 0; i--) {
	    consoleBranchGroup.addChild(line[i].getRoot());
	    line[i].setUpdateManager(this);
	}
	
	for (int i = border.length - 1; i >= 0; i--) {
	    consoleBranchGroup.addChild(border[i].getRoot());
	    border[i].setUpdateManager(this);
	}
	
	initialize();
	
	dirtyCheck = Integer.parseInt("111111111111111111", 2); // Just has to be > 12
	                                                        //  to dirty everything
    }

    /**
     * Implement this to do extra initialization before the node goes live
     */
    public void initialize() {
    }

  /**
   * Sets the background to a solid color. If a background image already exists then
   * it will be overwritten with this solid color.  It is completely appropriate to
   * have an alpha component in the color if this is a alpha capable overlay.
   * In general you should only use background images if this is an overlay that is
   * called frequently, since you could always paint it inside the paint() method.
   */
  public void setBackgroundColor(Color color)
  {
  }

    public Rectangle getBounds() {
	return bounds;
    }

    public Canvas3D getCanvas() {
	return canvas3D;
    }

    public BranchGroup getRoot() {
	return consoleBranchGroup;
    }
    
    public OverlayBase getBorder(int index) {
	return border[index];
    }

    public void setAntialiased(boolean antialiased) {
	if (this.antialiased != antialiased) {
	    this.antialiased = antialiased;
	    dirty(ANTIALIASED);
	}
    }
    
    public boolean isAntialiased() {
	return antialiased;
    }

    public UpdateManager getUpdateManager() {
	return updateManager;
    }

    public void setUpdateManager(UpdateManager updateManager) {
	if (this.updateManager != updateManager) {
	    this.updateManager = updateManager;
	    updateManager.updateRequested();
	}
    }

    public boolean isUpdating() {
	return updateManager.isUpdating();
    }

    public void setUpdating(boolean updating) {
	updateManager.setUpdating(updating);
    }

    /**
     * Returns the number of lines
     */
    public int getNumLines() {
	return line.length;
    }

    public Overlay getLine(int index) {
	synchronized(line) {
	    return line[index];
	}
    }

    /**
     * Sets the visibility of the overlay.
     *
     * This will be updated in the next frame
     *
     * TODO: Make thread safe
     */
    public void setVisible(boolean visible) {
	if(this.visible != visible) {
	    this.visible = visible;
	    dirty(VISIBLE);
	}
    }

    public boolean isVisible() {
	return visible;
    }

    /**
     * This will scroll lines starting at startLine the scrollDistance.
     * If the scroll distance is positive then items will be pushed off
     * the top if it is negative then they will be pushed off the bottom.
     * To scroll all the lines up one do scroll(0, 1); To scroll all the
     * lines down two do scroll(getNumLines() - 1, -2);
     *
     * This will be updated in the next frame
     */
    public void scroll(int startLine, int scrollDistance) {
        int i = 0, j = 0;
        if(scrollDistance > 0) {
	    Overlay[] holder = new Overlay[scrollDistance];
	    int max = line.length - 1;
            for (i = 0; i < scrollDistance; i++) {
                holder[i] = line[max - i];
            }
	    synchronized(line) {
		for (i = max, j = max - scrollDistance; j >= startLine; i--, j--) {
		    line[i] = line[j];
		}
		for (j = 0; i >= startLine; i--, j++) {
		    line[i] = holder[j];
		    fireItemScrolled(new ScrollEvent(this, line[i], ScrollEvent.SCROLLED_UP));
		}
		dirty(LINE_POSITION);
	    }
        } else if(scrollDistance < 0) {
	    scrollDistance *= -1;
	    Overlay[] holder = new Overlay[scrollDistance];
            for (i = scrollDistance - 1; i >= 0; i--) {
                holder[i] = line[i];
            }
	    synchronized(line) {
		for (i = 0, j = scrollDistance; j <= startLine; i++, j++) {
		    line[i] = line[j];
		}
		for (j = 0; i <= startLine; i++, j++) {
		    line[i] = holder[j];
		    fireItemScrolled(new ScrollEvent(this, line[i], ScrollEvent.SCROLLED_DOWN));
		}
		dirty(LINE_POSITION);
	    }
        }
    }

    /**
     * Sets the relative position of the overlay on the screen using a 2 dimensional array.
     *
     * @param relativePosition [X_PLACEMENT] May be PLACE_LEFT, PLACE_RIGHT, or PLACE_CENTER
     * @param relativePosition [Y_PLACEMENT] May be PLACE_TOP, PLACE_BOTTOM, or PLACE_CENTER
     */
    public void setRelativePosition(int[] relativePosition) {
        setRelativePosition(relativePosition[X_PLACEMENT], relativePosition[Y_PLACEMENT]);
    }

    /**
     * This will set the relative position of the scroller elements.
     *
     * TODO: Make it work! It needs to change the offsets of the subelements
     *       otherwise they flip over when put on the bottom and mess up
     *       completely in the center.
     */
    public void setRelativePosition(int xType, int yType) {
	if(relativePosition[Overlay.X_PLACEMENT] != xType 
	   || relativePosition[Overlay.Y_PLACEMENT] != yType) {
	    synchronized(relativePosition) {
		relativePosition[Overlay.X_PLACEMENT] = xType;
		relativePosition[Overlay.Y_PLACEMENT] = yType;
		dirty(LINE_POSITION | BORDER_POSITION);
	    }
	}
    }

    /**
     * Sets the position of the window.
     *
     * This will be updated in the next frame
     */
    public void setOffset(Dimension offset) {
	setOffset(offset.width, offset.height);
    }

    /**
     * Sets the position of the window.
     *
     * This will be updated in the next frame
     */
    public void setOffset(int width, int height) {
	if(offset.height != width || offset.height != height) {
	    synchronized(bounds) {
		offset.width = width;
		offset.height = height;
		dirty(BORDER_POSITION | LINE_POSITION);
	    }
	}
    }

    /**
     * Sets the border positions according to the current position.
     */
    private void syncBorderPositions() {
	synchronized(offset) {
	    OverlayUtilities.repositonBounds(bounds, relativePosition, canvas3D.getSize(), offset);

	    Dimension borderOffset = new Dimension();
	    for(int i = 0; i < border.length; i++) {
		switch(i) {
		case BORDER_TOP:
		    borderOffset.width = bounds.x;
		    borderOffset.height = bounds.y;
		    break;
		case BORDER_BOTTOM:
		    borderOffset.width = bounds.x;
		    borderOffset.height = bounds.y + bounds.height - margin.bottom;
		    break;
		case BORDER_LEFT:
		    borderOffset.width = bounds.x;
		    borderOffset.height = bounds.y + margin.top;
		    break;
		case BORDER_RIGHT:
		    borderOffset.width = bounds.x + bounds.width - margin.left;
		    borderOffset.height = bounds.y + margin.top;
		    break;
		}
		border[i].setOffset(borderOffset);
	    }
	    clean(BORDER_POSITION);
	}
    }

    /**
     * Sets all the lines into position according to the array order.
     */
    private void syncLinePositions() {
	synchronized(line) {
	    OverlayUtilities.repositonBounds(bounds, relativePosition, canvas3D.getSize(), offset);

	    Dimension currentOffset = new Dimension(bounds.x + margin.left,
						    bounds.y + bounds.height - margin.top);
	    
	    for (int i = 0; i < line.length; i++) {
		currentOffset.height -= line[i].getBounds().height;
		line[i].setOffset(currentOffset);
	    }
	    clean(LINE_POSITION);
	}
    }

    /**
     * Sets the visibility of all the overlays.
     *
     * TODO: Make thread safe
     */
    private void syncVisible() {
	for (int i = border.length - 1; i >= 0; i--) {
	    border[i].setVisible(visible);
	}
	for (int i = line.length - 1; i >= 0; i--) {
	    line[i].setVisible(visible);
	}
	clean(VISIBLE);
    }

    /**
     * Sets the color on the background.
     */
    private void syncBorderBackgroundColor() {
	if (borderBackgroundColor != null) {
	    synchronized(borderBackgroundColor) {
		for(int i = border.length - 1; i >= 0; i--) {
		    border[i].setBackgroundColor(borderBackgroundColor);
		}
		clean(BORDER_BACKGROUND_COLOR);
	    }
	}
    }

    /**
     * Sets the image on the background.
     */
    private void syncBorderBackgroundImage() {
	if (borderBackgroundImage != null) {
	    synchronized(borderBackgroundImage) {
		for(int i = border.length - 1; i >= 0; i--) {
		    border[i].setBackgroundImage(borderBackgroundImage);
		}
		clean(BORDER_BACKGROUND_IMAGE);
	    }
	}
    }

    public void dirty(int property) {
	dirtyCheck |= property;
	if(updateManager != null) {
	    updateManager.updateRequested();
	} else {
	    System.err.println("Null update manager in " + this);
	}
    }

    public void clean(int property) {
	dirtyCheck &= ~property;
    }

    public void updateRequested() {
	dirty(ITEM);
    }

    /**
     * This will commit any changes that have been made in the overlay. Calling this
     * from within a behavior guarantees that all changes will happen in one frame.
     */
    public void update() {
	if ((dirtyCheck & BORDER_POSITION) != 0) {
	    syncBorderPositions();
	}

	if ((dirtyCheck & LINE_POSITION ) != 0) {
	    syncLinePositions();
	}
	
	if ((dirtyCheck & VISIBLE) != 0) {
	    syncVisible();
	}

	if ((dirtyCheck & BORDER_BACKGROUND_COLOR) != 0) {
	    syncBorderBackgroundColor();
	}

	if ((dirtyCheck & BORDER_BACKGROUND_IMAGE) != 0) {
	    syncBorderBackgroundImage();
	}

	if ((dirtyCheck & ITEM) != 0) {
	    for (int i = line.length - 1; i >= 0; i--) {
		line[i].update();
	    }
	    for (int i = border.length - 1; i >= 0; i--) {
                border[i].update();
            }
	    clean(ITEM);
	}
    }

    public void addScrollEventListener(ScrollEventListener listener) {
	listeners.add(ScrollEventListener.class, listener);
    }

    public void removeScrollEventListener(ScrollEventListener listener) {
	listeners.remove(ScrollEventListener.class, listener);
    }

    public void fireItemScrolled(ScrollEvent e) {
	Object[] listeners = this.listeners.getListenerList();
	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == ScrollEventListener.class) {
		((ScrollEventListener)listeners[i + 1]).itemScrolled(e);
	    }
	}
    }
}
