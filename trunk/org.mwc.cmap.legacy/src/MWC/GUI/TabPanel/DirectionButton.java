package MWC.GUI.TabPanel;

import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyVetoException;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeEvent;
import java.lang.IllegalArgumentException;


//	01/29/97	TWB	Integrated changes from Windows
//	05/23/97	LAB	Updated to support Java 1.1
//	06/27/97	LAB	Changed the way the button is drawn.  Now it uses the offscreen
//					Image.  Cleaned up the drawing code to be more universal.
//					Added the ArrowColor property to allow the arrow to have a
//					user definable color.  The disabled color is derived from this color.
//	07/02/97	LAB	Constrained the Arrow Indent Property, so the arrow wouldn't draw strangely
//					if the inset was set to a large value.
//	07/08/97	LAB	Changed the preferedSize() method to return a button whose arrow is 20 by 20.
//	07/13/97	RKM	Fixed misspelling of prefered
//	07/19/97	LAB	Replaced updatePolygon with fillTriangle and changed the drawing method from
//					using a polygon to using lines because polygons were problematic.  Changed
//					getPreferredSize to return the smallest size which the button looks good and
//					changed getMinimumSize to return the smallest possible size for the button and
//					still be recognized as a DirectionButton.
//  07/25/97    CAR marked fields transient as needed
//                  innerclasses implement java.io.Serializable

/**
 * The DirectionButton is a button component that has an arrow drawn in it that
 * points one of four ways (left, up, right, or down). At runtime, the button has
 * a raised look and a pressed look.
 * <p>
 * This component is usually used in conjunction with a combo or list box to
 * indicate a list that the user can view by clicking the arrow, or with spinners
 * to let the user scroll through available values.
 * <p>
 * @version 1.1, June 27, 1997
 * @author  Symantec
 */
public class DirectionButton extends ButtonBase implements java.io.Serializable
{
    /**
     * The point LEFT style constant.
     */
    public static final int LEFT = 0;

    /**
     * The point RIGHT style constant.
     */
    public static final int RIGHT = 1;

    /**
     * The point UP style constant.
     */
    public static final int UP = 2;

    /**
     * The point DOWN style constant.
     */
    public static final int DOWN = 3;

    /**
     * Constructs a default DirectionButton, which will point left.
     */
    public DirectionButton()
    {
        this(LEFT);
    }

    /**
     * Constructs a DirectionButton pointing the specified direction.
     * @param d a style constant indicating which direction to point the button
     * @see #LEFT
     * @see #UP
     * @see #RIGHT
     * @see #DOWN
     */
	public DirectionButton(int d)
	{
		direction = d;
		left      = 0;
		right     = 0;
		bottom    = 0;
		indent    = 0;
		try { setArrowColor(Color.black); } catch (PropertyVetoException exc) {}

	}

    /**
     * Sets the direction of the arrow after construction.
     * @param d constant indicating direction to point button
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     * @see #getDirection
     * @see #LEFT
     * @see #UP
     * @see #RIGHT
     * @see #DOWN
     */
	public void setDirection(int d) throws PropertyVetoException
	{
		if(direction != d)
		{
			Integer oldValue = new Integer(direction);
			Integer newValue = new Integer(d);

			vetos.fireVetoableChange("Direction", oldValue, newValue);

			direction = d;
			repaint();

			changes.firePropertyChange("Direction", oldValue, newValue);
		}
	}

    /**
     * Returns the direction the button is currently pointing.
     * @see #setDirection
     * @see #LEFT
     * @see #UP
     * @see #RIGHT
     * @see #DOWN
     */
    public int getDirection()
    {
        return direction;
    }

    /**
     * Sets the amount of blank space between the arrow and the button
     * border in pixels.
     * @param ai the margin around the arrow in pixels. 0=arrow takes up entire button
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
     * @see #getArrowIndent
     */
    public void setArrowIndent(int ai) throws PropertyVetoException
    {
    	if(indent != ai)
    	{
			Integer oldValue = new Integer(indent);
			Integer newValue = new Integer(ai);

			vetos.fireVetoableChange("ArrowIndent", oldValue, newValue);

	        indent = ai;
	        //Make sure that changes to indent don't make changes to shrinkTriangle
	        //give us a bad triangle.
	        shrinkTriangle(left, right, top, bottom);
	        repaint();

		    changes.firePropertyChange("ArrowIndent", oldValue, newValue);
		}
    }

	/**
	 * Sets the color of the direction arrow.
	 * @param newValue the new arrow color.
     * @exception PropertyVetoException
     * if the specified property value is unacceptable
	 * @see #getArrowColor
	 */
	public void setArrowColor(Color newValue) throws PropertyVetoException
	{
		if (!GeneralUtils.objectsEqual(arrowColor, newValue))
		{
			Color oldValue = arrowColor;

			vetos.fireVetoableChange("ArrowColor", oldValue, newValue);

			arrowColor = newValue;
			try
			{
				disabledArrowColor = ColorUtils.fade(arrowColor, Color.lightGray, 0.50);
			}
			catch (IllegalArgumentException exc) {}

			repaint();

			changes.firePropertyChange("ArrowColor", oldValue, newValue);
		}
	}

    /**
     * Gets the current color of the direction arrow.
     * @return the current arrow color
     * @see #setArrowColor
     */
	public Color getArrowColor()
	{
	    return arrowColor;
	}


    /**
     * Returns the amount of blank space between the arrow and the button
     * border in pixels.
     * @see #setArrowIndent
     */
    public int getArrowIndent()
    {
        return indent;
    }

    /**
     * Sets the extra amount, in pixels, to shrink the arrow triangle.
     * Constrains the values such that the arrow will never be less than
     * three pixels.  If a value is entered that would exceed this limit,
     * the limit will be used instead.
     * @param left pixels to shrink from left side
     * @param right pixels to shrink from right side
     * @param top pixels to shrink from top
     * @param bottom pixels to shrink from bottom
     */
    public void shrinkTriangle(int l, int r, int t, int b)
    {
    	if(isAdded)
    	{
	    	Dimension s = getSize();
		    int maxWidth	= s.width - bevel - bevel - 2;
		    int maxHeight	= s.height - bevel - bevel - 2;

		    if(maxWidth - (l + r + indent + indent) >= 3)
		    {
		    	left	= l;
		    	right	= r;
		    }
		    else
		    {
		    	left	= (maxWidth - indent - indent - 3) / 2;
		    	right	= left;
		    }

		    if(maxHeight - (t + b + indent + indent) >= 3)
		    {
		    	top		= t;
		    	bottom	= b;
		    }
		    else
		    {
		    	top	= (maxHeight - indent - indent - 3) / 2;
		    	bottom	= top;
		    }
        }
    }

    /**
	 * Returns the recommended dimensions to properly display this component.
     * This is a standard Java AWT method which gets called to determine
     * the recommended size of this component.
     *
     * @return a button that has a content area of 7 by 7 pixels.
     * @see java.awt.Component#getMinimumSize
	 */
    public Dimension getPreferredSize()
    {
    	Dimension defaultSize = super.getPreferredSize();

		return new Dimension(defaultSize.width + 7, defaultSize.height + 7);
    }

    /**
	 * Returns the minimum dimensions to properly display this component.
     * This is a standard Java AWT method which gets called to determine
     * the minimum size of this component.
     *
     * @return a button that has a content area of 3 by 3 pixels.
     * @see java.awt.Component#getMinimumSize
	 */
    public Dimension getMinimumSize()
    {
    	Dimension defaultSize = super.getPreferredSize();

		return new Dimension(defaultSize.width + 3, defaultSize.height + 3);
    }

	/**
	 * Tells this component that it has been added to a container.
	 * This is a standard Java AWT method which gets called by the AWT when
	 * this component is added to a container. Typically, it is used to
	 * create this component's peer.
	 *
	 * It has been overridden here to hook-up event listeners.
	 *
	 * @see #removeNotify
	 */
	public synchronized void addNotify()
	{
		super.addNotify();

		//Hook up listeners
		if (sizeVeto == null)
		{
			sizeVeto = new SizeVeto();
			addDirectionListener(sizeVeto);
		}
		if (indentVeto == null)
		{
			indentVeto = new IndntVeto();
			addArrowIndentListener(indentVeto);
		}
	}

	/**
	 * Tells this component that it is being removed from a container.
	 * This is a standard Java AWT method which gets called by the AWT when
	 * this component is removed from a container. Typically, it is used to
	 * destroy the peers of this component and all its subcomponents.
	 *
	 * It has been overridden here to unhook event listeners.
	 *
	 * @see #addNotify
	 */
	public synchronized void removeNotify()
	{
		//Unhook listeners
		if (sizeVeto != null)
		{
			removeDirectionListener(sizeVeto);
			sizeVeto = null;
		}
		if (indentVeto != null)
		{
			removeArrowIndentListener(indentVeto);
			indentVeto = null;
		}

		super.removeNotify();
	}

    /**
     * Adds a listener for all event changes.
     * @param listener the listener to add.
     * @see #removePropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
    	super.addPropertyChangeListener(listener);
    	changes.addPropertyChangeListener(listener);
    }

    /**
     * Removes a listener for all event changes.
     * @param listener the listener to remove.
     * @see #addPropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
    	super.removePropertyChangeListener(listener);
    	changes.removePropertyChangeListener(listener);
    }

    /**
     * Adds a vetoable listener for all event changes.
     * @param listener the listener to add.
     * @see #removeVetoableChangeListener
     */
    public void addVetoableChangeListener(VetoableChangeListener listener)
    {
     	super.addVetoableChangeListener(listener);
		vetos.addVetoableChangeListener(listener);
    }

    /**
     * Removes a vetoable listener for all event changes.
     * @param listener the listener to remove.
     * @see #addVetoableChangeListener
     */
    public void removeVetoableChangeListener(VetoableChangeListener listener)
    {
    	super.removeVetoableChangeListener(listener);
    	vetos.removeVetoableChangeListener(listener);
    }

    /**
     * Adds a listener for the Direction property changes.
     * @param listener the listener to add.
     * @see #removeDirectionChangeListener
     */
    public void addDirectionListener(PropertyChangeListener listener)
    {
    	changes.addPropertyChangeListener("Direction", listener);
    }

    /**
     * Removes a listener for the Direction property changes.
     * @param listener the listener to remove.
     * @see #addDirectionChangeListener
     */
    public void removeDirectionListener(PropertyChangeListener listener)
    {
    	changes.removePropertyChangeListener("Direction", listener);
    }

    /**
     * Adds a vetoable listener for the Direction property changes.
     * @param listener the listener to add.
     * @see #removeVetoableDirectionChangeListener
     */
    public void addDirectionListener(VetoableChangeListener listener)
    {
    	vetos.addVetoableChangeListener("Direction", listener);
    }

    /**
     * Removes a vetoable listener for the Direction property changes.
     * @param listener the listener to remove.
     * @see #addVetoableDirectionChangeListener
     */
    public void removeDirectionListener(VetoableChangeListener listener)
    {
    	vetos.removeVetoableChangeListener("Direction", listener);
    }

    /**
     * Adds a listener for the ArrowIndent property changes.
     * @param listener the listener to add.
     * @see #removeDirectionChangeListener
     */
    public void addArrowIndentListener(PropertyChangeListener listener)
    {
    	changes.addPropertyChangeListener("ArrowIndent", listener);
    }

    /**
     * Removes a listener for the ArrowIndent property changes.
     * @param listener the listener to remove.
     * @see #addDirectionChangeListener
     */
    public void removeArrowIndentListener(PropertyChangeListener listener)
    {
    	changes.removePropertyChangeListener("ArrowIndent", listener);
    }

    /**
     * Adds a vetoable listener for the ArrowIndent property changes.
     * @param listener the listener to add.
     * @see #removeVetoableDirectionChangeListener
     */
    public void addArrowIndentListener(VetoableChangeListener listener)
    {
    	vetos.addVetoableChangeListener("ArrowIndent", listener);
    }

    /**
     * Removes a vetoable listener for the ArrowIndent property changes.
     * @param listener the listener to remove.
     * @see #addVetoableDirectionChangeListener
     */
    public void removeArrowIndentListener(VetoableChangeListener listener)
    {
    	vetos.removeVetoableChangeListener("ArrowIndent", listener);
    }

	/**
	 * This is the PropertyChangeEvent handling inner class for the constrained Direction property.
	 * Handles vetoing Directions that are not valid.
	 */
	class SizeVeto implements java.beans.VetoableChangeListener, java.io.Serializable
	{
	    /**
	     * This method gets called when an attempt to change the constrained Direction property is made.
	     * Ensures the given direction size is valid for this button.
	     *
	     * @param     e a <code>PropertyChangeEvent</code> object describing the
	     *   	      event source and the property that has changed.
	     * @exception PropertyVetoException if the recipient wishes the property
	     *              change to be rolled back.
	     */
	    public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException
	    {
	    	int i = ((Integer)e.getNewValue()).intValue();
	        if (!isValidDirection(i))
	        {
	            throw new PropertyVetoException("Invalid direction size: " + i, e);
	        }
	    }
	}

	/**
	 * This is the PropertyChangeEvent handling inner class for the constrained ArrowIndent property.
	 * Handles vetoing ArrowIndents that are not valid.
	 */
	class IndntVeto implements java.beans.VetoableChangeListener, java.io.Serializable
	{
	    /**
	     * This method gets called when an attempt to change the constrained ArrowIndent property is made.
	     * Ensures the given arrow indent size is valid for this button.
	     *
	     * @param     e a <code>PropertyChangeEvent</code> object describing the
	     *   	      event source and the property that has changed.
	     * @exception PropertyVetoException if the recipient wishes the property
	     *              change to be rolled back.
	     */
	    public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException
	    {
	    	int i = ((Integer)e.getNewValue()).intValue();
	        if (!isValidArrowIndent(i))
	        {
	            throw new PropertyVetoException("Invalid arrow indent: " + i, e);
	        }
	    }
	}


	/**
	 * Maintains the buttonImage size and draws the
	 * button in the buttonImage offscreen image.
	 * @see symantec.itools.awt.ButtonBase#updateButtonImage
	 */
	protected void updateButtonImage()
	{
		super.updateButtonImage();
		Graphics g		= buttonImage.getGraphics();
	    Dimension s		= getSize();
        int trueBevel	= bevel + 1;
        int centerHorizontal;
        int centerVertical;
        int topSide;
        int bottomSide;
        int leftSide;
        int rightSide;

        if(isEnabled())
        {
            g.setColor(arrowColor);
        }
        else
        {
            g.setColor(disabledArrowColor);
        }

		centerHorizontal	= ((s.width - 1) / 2)					+ pressedAdjustment;
		centerVertical		= ((s.height - 1) / 2)					+ pressedAdjustment;
		topSide				= (top + trueBevel - 1)					+ pressedAdjustment  + indent;
		bottomSide			= (s.height - 1 - bottom - trueBevel)	+ pressedAdjustment  - indent;
		leftSide			= (left + trueBevel - 1)				+ pressedAdjustment  + indent;
		rightSide			= (s.width - 1 - right - trueBevel)		+ pressedAdjustment  - indent;

		if (OS.isMacintosh())
		{
		    leftSide	+= 1;
		    topSide		+= 1;
		}

        switch (direction)
		{
			case UP:
			{
				fillTriangle(g, centerHorizontal, topSide, leftSide, bottomSide, rightSide, bottomSide, direction);
				break;
			}
			case DOWN:
			{
				fillTriangle(g, centerHorizontal, bottomSide, leftSide, topSide, rightSide, topSide, direction);
				break;
			}

			case LEFT:
			{
				fillTriangle(g, leftSide, centerVertical, rightSide, bottomSide, rightSide, topSide, direction);
				break;
			}

			case RIGHT:
			{
				fillTriangle(g, rightSide, centerVertical, leftSide, bottomSide, leftSide, topSide, direction);
				break;
            }
		}
		if (g != null)
		    g.dispose();
	}

	/**
	 * Fills a triangle which has at least one side that is straight up and down or left and right.
	 * @param g the Graphics to use to draw with.
	 * @param tipX the horizontal coordinate of the point opposite a straight side.
	 * @param tipY the vertical coordinate of the point opposite a straight side.
	 * @param aX the horizontal coordinate of one of the two points defining the straight side.
	 * @param aY the vertical coordinate of one of the two points defining the straight side.
	 * @param bX the horizontal coordinate of one of the two points defining the straight side.
	 * @param bY the vertical coordinate of one of the two points defining the straight side.
	 * @param direction the direction of the straight line UP, DOWN, or LEFT, RIGHT.
	 *
	 * aX and bX should be the same for UP or Down.  aY and bY should be the same for LEFT or RIGHT.
	 * If not, then the a coordinates are used.
	 *
	 * @see #UP
	 * @see #DOWN
	 * @see #LEFT
	 * @see #RIGHT
	 */
	protected void fillTriangle(Graphics g, int tipX, int tipY, int aX, int aY, int bX, int bY, int direction)
	{
		int dist, max, min;

		switch(direction)
		{
			case UP:
			case DOWN:
				dist = Math.abs(aX - bX);
				max = Math.max(aX, bX);
				min = Math.min(aX, bX);
				for(int i = min; i <= max; ++i)
				{
					g.drawLine(tipX, tipY, i, aY);
				}
				break;
			case RIGHT:
			case LEFT:
				dist = Math.abs(aY - bY);
				max = Math.max(aY, bY);
				min = Math.min(aY, bY);
				for(int i = min; i <= max; ++i)
				{
					g.drawLine(tipX, tipY, aX, i);
				}
				break;
		}
	}

    /**
     * Is the given bevel size valid for this button.
     * @param i the given bevel size
     * @return true if the given bevel size is acceptable, false if not.
     */
    protected boolean isValidBevelSize(int i)
    {
        Dimension s = getSize();

        int temp = i * 2 + 4;

        if (i < 0 || s.width < temp || s.height < temp)
            return false;
        else
        	return true;
    }

    /**
     * Is the given direction valid for this button.
     * @param i the given bevel size
     * @return true if the given direction is acceptable, false if not.
     */
    protected boolean isValidDirection(int i)
    {
    	switch(i)
    	{
    		case LEFT:
    		case RIGHT:
    		case UP:
    		case DOWN:
    			return true;
    		default:
    			return false;
    	}
    }

    /**
     * Is the given arrow indent is valid for this button.
     * @param i the given bevel size
     * @return true if the given indent size is acceptable, false if not.
     */
    protected boolean isValidArrowIndent(int i)
    {
    	Dimension s = getSize();

    	int temp = (i * 2) + (bevel + 1) * 2 + 4;

    	if(i < 0 || s.width < temp || s.height < temp)
    		return false;
    	else
    		return true;
    }

	/**
	 * The color of the arrow in the button.
	 */
	protected Color	arrowColor			= null;
	/**
	 * The color of the arrow when the button is disabled.
	 */
	protected Color	disabledArrowColor	= null;
	/**
	 * The direction the arrow points.
	 * One of: LEFT, UP, RIGHT, or DOWN.
     * @see #LEFT
     * @see #UP
     * @see #RIGHT
     * @see #DOWN
	 */
	protected int		direction;
	/**
	 * The number of pixels to shrink the arrow from the left side of the button.
	 */
	protected int		left;
	/**
	 * The number of pixels to shrink the arrow from the right side of the button.
	 */
	protected int		right;
	/**
	 * The number of pixels to shrink the arrow from the top side of the button.
	 */
	protected int		top;
	/**
	 * The number of pixels to shrink the arrow from the bottom side of the button.
	 */
	protected int		bottom;
	/**
     * The margin around the arrow in pixels. 0 = arrow takes up entire button.
	 */
	protected int		indent;
    private SizeVeto	sizeVeto	= null;
    private IndntVeto	indentVeto	= null;
    private VetoableChangeSupport vetos = new VetoableChangeSupport(this);
    private PropertyChangeSupport changes = new PropertyChangeSupport(this);
}
