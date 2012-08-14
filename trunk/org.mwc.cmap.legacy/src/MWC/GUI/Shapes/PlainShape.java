// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: PlainShape.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: PlainShape.java,v $
// Revision 1.5  2006/05/02 13:21:38  Ian.Mayo
// Make things draggable
//
// Revision 1.4  2006/04/21 07:48:37  Ian.Mayo
// Make things draggable
//
// Revision 1.3  2006/03/31 14:29:21  Ian.Mayo
// Switch default color to off-white, from RED
//
// Revision 1.2  2004/05/25 15:37:15  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:22  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:33  Ian.Mayo
// Initial import
//
// Revision 1.11  2003-07-03 14:59:18+01  ian_mayo
// Always provide default colour for shapes (the same colour as the default in the colour editor)
//
// Revision 1.10  2003-06-23 08:28:52+01  ian_mayo
// Only return the Anchor point if we know our size (bounds)
//
// Revision 1.9  2003-03-18 12:07:17+00  ian_mayo
// extended support for transparent filled shapes
//
// Revision 1.8  2003-03-14 16:01:21+00  ian_mayo
// improve efficiency of naming label
//
// Revision 1.7  2003-03-03 11:54:33+00  ian_mayo
// Implement filled shape management
//
// Revision 1.6  2003-02-10 16:26:05+00  ian_mayo
// tidy comments, remove
//
// Revision 1.5  2003-02-07 09:49:20+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.4  2003-01-23 11:02:46+00  ian_mayo
// Add method to return points of shape as collection
//
// Revision 1.3  2003-01-21 16:30:44+00  ian_mayo
// minor comment improvement
//
// Revision 1.2  2002-05-28 09:25:52+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:23+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:08+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-03-19 11:04:04+00  administrator
// Add a "type" property to indicate type of shape (label, rectangle, etc)
//
// Revision 1.0  2001-07-17 08:43:16+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:24+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:49:14  ianmayo
// initial version
//
// Revision 1.6  2000-04-19 11:36:44+01  ian_mayo
// provide isVisible parameter
//
// Revision 1.5  1999-11-26 15:45:04+00  ian_mayo
// adding toString method
//
// Revision 1.4  1999-10-15 12:36:50+01  ian_mayo
// improved relative label locating
//
// Revision 1.3  1999-10-14 11:59:20+01  ian_mayo
// added property support and location editing
//
// Revision 1.2  1999-10-12 15:39:48+01  ian_mayo
// changed default constructor to use main constructor
//
// Revision 1.1  1999-10-12 15:36:36+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:37+01  administrator
// Initial revision
//
// Revision 1.3  1999-07-23 14:03:48+01  administrator
// Updating MWC utilities, & catching up on changes (removed deprecated code from PtPlot)
//
// Revision 1.2  1999-07-19 12:39:42+01  administrator
// Added painting to a metafile
//
// Revision 1.1  1999-07-07 11:10:04+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:57+01  sm11td
// Initial revision
//
// Revision 1.2  1999-02-01 14:25:01+00  sm11td
// Skeleton there, opening new sessions, window management.
//
// Revision 1.1  1999-01-31 13:33:02+00  sm11td
// Initial revision
//

package MWC.GUI.Shapes;

import java.awt.Color;
import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import MWC.GUI.CanvasType;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.HasDraggableComponents.ComponentConstruct;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

/**
 * parent for Shapes. Shapes are screen entities which are scaled using
 * geographic coordinates, not like @see PlainSymbol which is scaled using a
 * screen scale factor
 */
abstract public class PlainShape implements Serializable, DraggableItem
{

	// ////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////

	// keep track of versions
	static final long serialVersionUID = 1;

	/**
	 * the style the lines of this shape are drawn in
	 */
	private int _lineStyle;

	/**
	 * the colour this shape is drawn in
	 */
	private Color _foreColor;

	/**
	 * the width of the lines this shape is drawn in
	 */
	private int _lineWidth;

	/**
	 * the name of this shape
	 */
	private String _myName;

	/**
	 * property change support for this shape, this allows us to store a list of
	 * objects which are intererested in modification to this
	 */
	private PropertyChangeSupport _pSupport;

	private boolean _isVisible;

	/**
	 * the type of this shape
	 * 
	 */
	protected String _myType;

	/**
	 * whether this shape is filled
	 * 
	 */
	private boolean _isFilled = false;
	/**
	 * how transparent do we make the filled shapes?
	 * 
	 */
	protected static final int TRANSPARENCY_SHADE = 160;

	/**
	 * our default colour for new features
	 */
	public static final java.awt.Color DEFAULT_COLOR = new java.awt.Color(150,
			150, 150);


	
	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////

	/**
	 * constructor..
	 * 
	 * @param theLineStyle
	 * @param theLineWidth
	 * @param myType
	 */
	protected PlainShape(int theLineStyle, int theLineWidth, String myType)
	{
		_lineStyle = theLineStyle;
		_foreColor = DEFAULT_COLOR; // set the colour to the default one for our
																// colour editor
		_lineWidth = theLineWidth;

		StringBuffer sb = new StringBuffer();
		sb.append("Shape");
		sb.append(System.currentTimeMillis());

		setName(sb.toString());

		// declare the property support
		_pSupport = new PropertyChangeSupport(this);

		_myType = myType;

		_isVisible = true;
	}

	// ////////////////////////////////////////////////
	// member functions
	// ////////////////////////////////////////////////

	/**
	 * paint the shape onto the destination. note that the shape knows <i> where
	 * </i> to plot itself to
	 * 
	 * @param dest
	 *          - the place to paint to
	 */
	public abstract void paint(CanvasType dest);

	/**
	 * get the are covered by the shape
	 * 
	 * @return WorldArea representing geographic coverage
	 */
	public abstract MWC.GenericData.WorldArea getBounds();

	/**
	 * get the range from the indicated world location - making this abstract
	 * allows for individual shapes to have 'hit-spots' in various locations.
	 */
	public abstract double rangeFrom(MWC.GenericData.WorldLocation point);


	/**
	 * is this shape filled? (where applicable)
	 * 
	 * @return
	 */
	public boolean getFilled()
	{
		return _isFilled;
	}

	/**
	 * is this shape filled? (where applicable)
	 * 
	 * @param isFilled
	 *          yes/no
	 */
	public void setFilled(boolean isFilled)
	{
		this._isFilled = isFilled;
	}

	/**
	 * accessor to get the type of this shape
	 * 
	 */
	public String getType()
	{
		return _myType;
	}

	/**
	 * setter function for name
	 * 
	 * @param val
	 *          String representing name of shape
	 */
	final public void setName(String val)
	{
		_myName = val;
	}

	/**
	 * return this item as a string
	 */
	public String toString()
	{
		return getName();
	}

	/**
	 * getter function for name
	 * 
	 * @return String representing name of this shape
	 */
	public String getName()
	{
		return _myName;
	}

	public int getLineStyle()
	{
		return _lineStyle;
	}

	public void setLineStyle(int lineStyle)
	{
		_lineStyle = lineStyle;
	}

	public int getLineWidth()
	{
		return _lineWidth;
	}

	public void setLineWidth(int lineWidth)
	{
		_lineWidth = lineWidth;
	}

	public Color getColor()
	{
		return _foreColor;
	}

	public void setColor(Color Color)
	{
		_foreColor = Color;
	}

	public void move()
	{
	}

	public boolean getVisible()
	{
		return _isVisible;
	}

	public void setVisible(boolean val)
	{
		_isVisible = val;
	}

	// ////////////////////////////////////////////////////
	// property change support
	// ///////////////////////////////////////////////////
	public void addPropertyListener(PropertyChangeListener list)
	{
		_pSupport.addPropertyChangeListener(list);
	}

	public void removePropertyListener(PropertyChangeListener list)
	{
		_pSupport.removePropertyChangeListener(list);
	}

	protected void firePropertyChange(String name, Object oldValue,
			Object newValue)
	{
		if (_pSupport != null)
			_pSupport.firePropertyChange(name, oldValue, newValue);
	}

	// ////////////////////////////////////////////////////
	// label/anchor support
	// ///////////////////////////////////////////////////
	public WorldLocation getAnchor(int location)
	{
		WorldLocation loc = null;

		WorldArea wa = getBounds();

		// did we find our bounds?
		if (wa != null)
		{
			WorldLocation centre = wa.getCentre();
			switch (location)
			{
			case MWC.GUI.Properties.LocationPropertyEditor.TOP:
			{
				WorldLocation res = new WorldLocation(wa.getTopLeft().getLat(),
						centre.getLong(), 0);
				loc = res;
				break;
			}
			case MWC.GUI.Properties.LocationPropertyEditor.BOTTOM:
			{
				WorldLocation res = new WorldLocation(wa.getBottomRight().getLat(),
						centre.getLong(), 0);
				loc = res;
				break;
			}
			case MWC.GUI.Properties.LocationPropertyEditor.LEFT:
			{
				WorldLocation res = new WorldLocation(centre.getLat(), wa.getTopLeft()
						.getLong(), 0);
				loc = res;
				break;
			}
			case MWC.GUI.Properties.LocationPropertyEditor.RIGHT:
			{
				WorldLocation res = new WorldLocation(centre.getLat(), wa
						.getBottomRight().getLong(), 0);
				loc = res;
				break;
			}
			case MWC.GUI.Properties.LocationPropertyEditor.CENTRE:
			{
				loc = centre;
			}
			}
		}

		return loc;
	}

	/**
	 * ok - see if we are any close to the target
	 * 
	 * @param cursorPos
	 * @param cursorLoc
	 * @param currentNearest
	 * @param parentLayer
	 */
	public final void findNearestHotSpotIn(Point cursorPos,
			WorldLocation cursorLoc, LocationConstruct currentNearest,
			Layer parentLayer, Layers theLayers)
	{

		// initialise thisDist, since we're going to be over-writing it
		WorldDistance thisDist = new WorldDistance(rangeFrom(cursorLoc),
				WorldDistance.DEGS);

		// is this our first item?
		currentNearest.checkMe(this, thisDist, null, parentLayer);
	}

	/**
	 * utility method to assist in checking draggable components
	 * 
	 * @param thisLocation
	 * @param cursorLoc
	 * @param currentNearest
	 * @param shape
	 * @param parentLayer
	 */
	protected static void checkThisOne(WorldLocation thisLocation,
			WorldLocation cursorLoc, ComponentConstruct currentNearest,
			HasDraggableComponents shape, Layer parentLayer)
	{
		// now for the BL
		WorldDistance blRange = new WorldDistance(
				thisLocation.rangeFrom(cursorLoc), WorldDistance.DEGS);

		// try range
		currentNearest.checkMe(shape, blRange, null, parentLayer, thisLocation);
	}
}
