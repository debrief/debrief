
package MWC.GUI.Shapes;

import static MWC.GUI.Properties.LabelLocationPropertyEditor.ALL;
import static MWC.GUI.Properties.LabelLocationPropertyEditor.BOTTOM;
import static MWC.GUI.Properties.LabelLocationPropertyEditor.LEFT;
import static MWC.GUI.Properties.LabelLocationPropertyEditor.RIGHT;
import static MWC.GUI.Properties.LabelLocationPropertyEditor.TOP;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GUI.Properties.LabelLocationPropertyEditor;
import MWC.GUI.Properties.TimeIntervalPropertyEditor;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/**
 * Class representing a furthest on circle 
 */
public class FurthestOnCircleShape extends PlainShape implements Editable
{

	// ////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////

	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public class FurthestInfo extends Editable.EditorType
	{

		public FurthestInfo(FurthestOnCircleShape data, String theName)
		{
			super(data, theName, "");
		}

		@Override
		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				PropertyDescriptor[] res =
				{
						prop("NumRings", "the number of rings to plot"),
						prop("ArcCentre",
								"the orientation for which to plot the arcs (degs)"),
						prop("Centre", "the centre of the furthest on circles"),
						prop("RangeLabelLocation", "where to position the labels"),
						prop("TimeInterval", "the Interval between the rings"),
						prop("Speed", "the speed for which the circles are calculated"),
						prop("ArcWidth", "the overall width of fan to plot (degs)") };

				res[3].setPropertyEditorClass(LabelLocationPropertyEditor.class);
				res[4].setPropertyEditorClass(TimeIntervalPropertyEditor.class);

				return res;

			}
			catch (IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class WheelTest extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public WheelTest(String val)
		{
			super(val);
		}

		public void testMyParams()
		{
			MWC.GUI.Editable ed = new FurthestOnCircleShape(new WorldLocation(2d, 2d,
					2d), 3, new WorldSpeed(2, WorldSpeed.Kts), 5000, 45, 180);
			MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
			ed = null;
		}
		
		public void testWidthCalc()
		{
			FurthestOnCircleShape ed = new FurthestOnCircleShape(new WorldLocation(2d, 2d,
					2d), 3, new WorldSpeed(60, WorldSpeed.Kts), 60 * 60 * 1000, 45, 180);	
			WorldDistance dist = ed.getRingWidth();
			assertEquals("the width", 1d, dist.getValueIn(WorldDistance.DEGS));
		}
	}

	// keep track of versions
	static final long serialVersionUID = 1;

	/**
	 * the area covered by this Wheel
	 */
	private WorldArea _theArea;

	/**
	 * the centre of this Wheel
	 */
	private WorldLocation _theCentre;

	/**
	 * the number of rings to plot
	 */
	private int _numRings;

	/**
	 * where to plot the range labels
	 * 
	 */
	private int _rangeLabelLocation = LabelLocationPropertyEditor.ALL;

	/**
	 * the speed the vehicle is travelling at
	 * 
	 */
	private WorldSpeed _speed = new WorldSpeed(5, WorldSpeed.Kts);

	/**
	 * the time interval to use between the rings
	 * 
	 */
	private int _intervalMillis;

	/**
	 * the centre bearing for the arcs
	 * 
	 */
	private int _arcCentre = 0;

	/**
	 * the overall width of the arcs
	 * 
	 */
	private int _arcWidth = 0;

	/**
	 * our editor
	 */
	transient private Editable.EditorType _myEditor;

	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////

	/**
	 * Normal constructor for object
	 * 
	 * @param theCentre
	 *          the centre of the wheel
	 * @param theInnerRadius
	 *          the inner radius of the wheel, in yds
	 * @param theOuterRadius
	 *          the outer radius of the wheel, in yds
	 * @param theColor
	 *          the colour to plot the wheel
	 */
	public FurthestOnCircleShape(WorldLocation theCentre, int numRings,
			WorldSpeed speed, int interval, int arcCentre, int arcWidth)
	{
		super(0, 1, "Range Ring");

		// store the values
		_theCentre = theCentre;
		_numRings = numRings;
		_speed = speed;
		_arcCentre = arcCentre;
		_arcWidth = arcWidth;
		_intervalMillis = interval;

		// store the corners of the area,
		calcPoints();

		setName("Range Ring");
	}

	// ////////////////////////////////////////////////
	// member functions
	// ////////////////////////////////////////////////

	/**
	 * calculate some convenience values based on the radius and centre of the
	 * Wheel
	 */
	protected void calcPoints()
	{
		// create our area
		_theArea = new WorldArea(_theCentre, _theCentre);

		// create & extend to top left
		WorldLocation other = _theCentre.add(new WorldVector(0, getRingWidth()
				.getValueIn(WorldDistance.DEGS) * _numRings, 0));
		other.addToMe(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(270),
				getRingWidth().getValueIn(WorldDistance.DEGS) * _numRings, 0));
		_theArea.extend(other);

		// create & extend to bottom right
		other = _theCentre.add(new WorldVector(MWC.Algorithms.Conversions
				.Degs2Rads(180), getRingWidth().getValueIn(WorldDistance.DEGS)
				* _numRings, 0));
		other.addToMe(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(90),
				getRingWidth().getValueIn(WorldDistance.DEGS) * _numRings, 0));
		_theArea.extend(other);
	}

	/**
	 * sort out how far each ring covers
	 * 
	 * @return
	 */
	private WorldDistance getRingWidth()
	{
		// calculate the distance travelled at this speed, in this time interval
		double degsPerHour = _speed.getValueIn(WorldSpeed.Kts) / 60;
		double hours = _intervalMillis / 1000d / 60d / 60d;
		double degs = degsPerHour * hours;
		return new WorldDistance(degs, WorldDistance.DEGS);
	}

	
	
	public WorldSpeed getSpeed()
	{
		return _speed;
	}

	public void setSpeed(WorldSpeed _Speed)
	{
		this._speed = _Speed;
	}

	/**
	 * get the 'anchor point' for any labels attached to this shape
	 */
	public MWC.GenericData.WorldLocation getAnchor()
	{
		return _theCentre;
	}

	@Override
	public MWC.GenericData.WorldArea getBounds()
	{
		return _theArea;
	}

	/**
	 * return the centre of the Wheel
	 * 
	 * @return the centre of the Wheel
	 */
	public WorldLocation getCentre()
	{
		return _theCentre;
	}

	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new FurthestInfo(this, this.getName());

		return _myEditor;
	}

	public Color getWheelColor()
	{
		return super.getColor();
	}

	public boolean hasEditor()
	{
		return true;
	}

	/**
	 * paint the object
	 * 
	 * @param dest
	 *          the destination
	 */
	@Override
	public void paint(CanvasType dest)
	{
		// are we visible?
		if (!getVisible())
			return;
		// set the colour, if we know it
		if (this.getColor() != null)
			dest.setColor(this.getColor());

		dest.setColor(getColor());

		MWC.Algorithms.PlainProjection _proj = dest.getProjection();

		// sort out the centre in screen coords
		Point centre = new Point(_proj.toScreen(_theCentre));

		// sort out the range in screen coords
		WorldLocation outerEdge = _theCentre.add(new WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(0), getRingWidth().getValueIn(
						WorldDistance.DEGS), 0));
		Point screenOuterEdge = new Point(_proj.toScreen(outerEdge));
		int dx = screenOuterEdge.x - centre.x;
		int dy = screenOuterEdge.y - centre.y;
		final int ringRadius = (int) Math.sqrt(dx * dx + dy * dy);

		int thisRadius = ringRadius;

		// now the inner and outer range rings
		Point origin = new Point();

		final int lLoc = _rangeLabelLocation;

		// draw the ovals
		for (int i = 0; i < _numRings; i++)
		{
			origin.setLocation(centre);

			// shift the centre point to the TL corner of the area
			origin.translate(-thisRadius, -thisRadius);

			// draw in the arc itself
			dest.drawOval(origin.x, origin.y, thisRadius * 2, thisRadius * 2);

			// sort out the labels
			String thisLabel = ""
					+ (getRingWidth().getValue() + getRingWidth().getValue() * i);
			thisLabel += " " + getRingWidth().getUnitsLabel();

			int strWidth = dest.getStringWidth(null, thisLabel);
			int strHeight = dest.getStringHeight(null);

			if ((lLoc == ALL) || (lLoc == TOP))
				dest.drawText(thisLabel, (int) (centre.x - strWidth / 2.3),
						(int) (centre.y - thisRadius - strHeight * 0.4));
			if ((lLoc == ALL) || (lLoc == BOTTOM))
				dest.drawText(thisLabel, (int) (centre.x - strWidth / 2.3),
						(int) (centre.y + thisRadius + strHeight * 1.2));
			if ((lLoc == ALL) || (lLoc == LEFT))
				dest.drawText(thisLabel, centre.x - strWidth - thisRadius, centre.y
						+ strHeight / 2);
			if ((lLoc == ALL) || (lLoc == RIGHT))
				dest.drawText(thisLabel,
						(int) (centre.x + thisRadius + strWidth / 15d), centre.y
								+ strHeight / 2);

			// move on to the next radius
			thisRadius += ringRadius;
		}

	}

	/**
	 * get the range from the indicated world location - making this abstract
	 * allows for individual shapes to have 'hit-spots' in various locations.
	 */
	@Override
	public double rangeFrom(WorldLocation point)
	{
		final double thisRes = _theCentre.rangeFrom(point);
		double res = thisRes;

		// sort out the range from each radius
		final double ringWidthDegs = getRingWidth().getValueIn(WorldDistance.DEGS);
		for (int i = 0; i < _numRings; i++)
		{
			double thisR = i * ringWidthDegs;

			res = Math.min(Math.abs(thisR - thisRes), res);
		}

		return res;
	}

	/**
	 * set the centre location of the Wheel
	 */
	public void setCentre(WorldLocation centre)
	{
		// inform our listeners
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, _theCentre, centre);
		// make the change
		_theCentre = centre;
		// and calc the new summary data
		calcPoints();

		// and inform the parent (so it can move the label)
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);

	}

	public int getRangeLabelLocation()
	{
		return _rangeLabelLocation;
	}

	public void setRangeLabelLocation(int rangeLabelLocation)
	{
		_rangeLabelLocation = rangeLabelLocation;
	}

	public int getTimeInterval()
	{
		return _intervalMillis;
	}

	public void setTimeInterval(int millis)
	{
		_intervalMillis = millis;

		// and calc the new summary data
		calcPoints();

		// and inform the parent (so it can move the label)
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);
	}

	public BoundedInteger getArcWidth()
	{
		return new BoundedInteger(_arcWidth, 0, 360);
	}

	public void setArcWidth(BoundedInteger width)
	{
		_arcWidth = width.getCurrent();
	}

	public BoundedInteger getArcCentre()
	{
		return new BoundedInteger(_arcCentre, 0, 360);
	}

	public void setArcCentre(BoundedInteger centre)
	{
		_arcCentre = centre.getCurrent();
	}

	public BoundedInteger getNumRings()
	{
		return new BoundedInteger(_numRings, 1, 10);
	}

	public void setNumRings(BoundedInteger numRings)
	{
		_numRings = numRings.getCurrent();

		// and calc the new summary data
		calcPoints();

		// and inform the parent (so it can move the label)
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);
	}

	// ////////////////////////////////////////
	// convenience functions which pass calls back to parent
	// ////////////////////////////////////////
	public void setWheelColor(Color val)
	{
		super.setColor(val);
	}

	public void shift(WorldVector vector)
	{
		WorldLocation oldCentre = getCentre();
		WorldLocation newCentre = oldCentre.add(vector);
		setCentre(newCentre);

		// and calc the new summary data
		calcPoints();

		// and inform the parent (so it can move the label)
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);

	}
}