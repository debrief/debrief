package MWC.GUI.Shapes;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.Swing.SwingWorldPathPropertyEditor;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class PolygonShape extends PlainShape implements Editable,
		HasDraggableComponents, Layer
{

	// ////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////

	public static class PolygonNode implements Editable
	{

		// ////////////////////////////////////////////////////
		// bean info for this class
		// ///////////////////////////////////////////////////
		public class PolygonNodeInfo extends Editable.EditorType
		{

			public PolygonNodeInfo(PolygonNode data, String theName)
			{
				super(data, theName, "");
			}

			public String getName()
			{
				return PolygonNode.this.getName();
			}

			public PropertyDescriptor[] getPropertyDescriptors()
			{
				try
				{
					PropertyDescriptor[] res =
					{ prop("Location", "the location of this node"), };
					return res;

				}
				catch (IntrospectionException e)
				{
					return super.getPropertyDescriptors();
				}
			}
		}

		private String _myName;
		private WorldLocation _myLocation;
		private EditorType _myEditor;

		public PolygonNode(String name, WorldLocation location)
		{
			_myName = name;
			_myLocation = location;
		}

		@Override
		public String getName()
		{
			return _myName;
		}

		public WorldLocation getLocation()
		{
			return _myLocation;
		}

		public void setLocation(WorldLocation loc)
		{
			_myLocation = loc;
		}

		@Override
		public boolean hasEditor()
		{
			return true;
		}

		@Override
		public EditorType getInfo()
		{
			if (_myEditor == null)
				_myEditor = new PolygonNodeInfo(this, this.getName());

			return _myEditor;
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the area covered by this polygon
	 */
	private WorldArea _theArea;

	private Vector<PolygonNode> _nodes = new Vector<PolygonNode>();

	/**
	 * our editor
	 */
	transient private Editable.EditorType _myEditor;

	/**
	 * the "anchor" which labels connect to
	 */
	private WorldLocation _theAnchor;

	/**
	 * whether to join the ends of the polygon
	 * 
	 */
	private boolean _closePolygon = true;

	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////

	/**
	 * constructor
	 * 
	 * @param vector
	 *          the WorldLocation marking the centre of the polygon
	 */
	public PolygonShape(Vector<PolygonNode> vector)
	{
		super(0, 1, "Polygon");

		// and store it
		_nodes = vector;

		// now represented our polygon as an area
		calcPoints();
	}

	// ////////////////////////////////////////////////
	// member functions
	// ////////////////////////////////////////////////

	public void paint(CanvasType dest)
	{
		// are we visible?
		if (!getVisible())
			return;

		if (this.getColor() != null)
		{
			// create a transparent colour
			Color newcol = getColor();

			dest.setColor(new Color(newcol.getRed(), newcol.getGreen(), newcol
					.getBlue(), TRANSPARENCY_SHADE));
		}

		// check we have some points
		if (_nodes == null)
			return;

		if (_nodes.size() > 0)
		{
			// create our point lists
			int[] xP = new int[_nodes.size()];
			int[] yP = new int[_nodes.size()];

			// ok, step through the area
			Iterator<PolygonNode> points = _nodes.iterator();

			int counter = 0;

			while (points.hasNext())
			{
				WorldLocation next = (WorldLocation) points.next().getLocation();

				// convert to screen
				Point thisP = dest.toScreen(next);

				// remember the coords
				xP[counter] = thisP.x;
				yP[counter] = thisP.y;

				// move the counter
				counter++;
			}

			// ok, now plot it
			if (getFilled())
			{
				dest.fillPolygon(xP, yP, xP.length);
			}
			else
			{
				if (getClosed())
					dest.drawPolygon(xP, yP, xP.length);
				else
					dest.drawPolyline(xP, yP, xP.length);
			}
		}

		// unfortunately we don't have a way of tracking edits to the underlying
		// worldpath object (since the polygon editor manipulates it directly.
		// so, we'll recalc our bounds at each repaint.
		calcPoints();

		// and inform the parent (so it can move the label)
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);

	}

	/**
	 * calculate some convenience values based on the radius and centre of the
	 * polygon
	 */
	protected void calcPoints()
	{
		// check we have some points
		if (_nodes == null)
			return;

		if (_nodes.size() > 0)
		{
			// running total of lat/longs which we can average to determine the centre
			double lats = 0;
			double longs = 0;

			// reset the area object
			_theArea = null;

			// ok, step through the area
			Iterator<PolygonNode> points = _nodes.iterator();
			while (points.hasNext())
			{
				WorldLocation next = points.next().getLocation();
				// is this our first point?
				if (_theArea == null)
					// yes, initialise the area
					_theArea = new WorldArea(next, next);
				else
					// no, just extend it
					_theArea.extend(next);

				lats += next.getLat();
				longs += next.getLong();
			}

			// ok, now produce the centre
			_theAnchor = new WorldLocation(lats / _nodes.size(), longs
					/ _nodes.size(), 0);
		}
	}

	public WorldArea getBounds()
	{
		return _theArea;
	}

	/**
	 * get the range from the indicated world location - making this abstract
	 * allows for individual shapes to have 'hit-spots' in various locations.
	 */
	public double rangeFrom(WorldLocation point)
	{
		double res = -1;

		if (_nodes.size() > 0)
		{
			// ok, step through the area
			Iterator<PolygonNode> points = _nodes.iterator();
			while (points.hasNext())
			{
				WorldLocation next = (WorldLocation) points.next().getLocation();

				double thisD = next.rangeFrom(point);

				// is this our first point?
				if (res == -1)
				{
					res = thisD;
				}
				else
					res = Math.min(res, thisD);
			}
		}

		return res;
	}

	/**
	 * the points representing the polygon
	 */
	public Vector<PolygonNode> getPoints()
	{
		// note, we have to return a fresh copy of the polygon
		// in order to know if an edit has been made the editor cannot edit the
		// original

		// NO - IGNORE THAT. We give the editor the actual polygon, since the
		// PolygonEditorView
		// will be changing the real polygon. that's all.

		return _nodes;

	}

	public void setPolygonColor(Color val)
	{
		super.setColor(val);
	}

	public Color getPolygonColor()
	{
		return super.getColor();
	}

	public boolean hasEditor()
	{
		return true;
	}

	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new PolygonInfo(this, this.getName());

		return _myEditor;
	}

	
	
	/**
	 * get the 'anchor point' for any labels attached to this shape
	 */
	public WorldLocation getAnchor()
	{
		return _theAnchor;
	}

	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public class PolygonInfo extends Editable.EditorType
	{

		public PolygonInfo(PolygonShape data, String theName)
		{
			super(data, theName, "");
		}

		@Override
		public final MethodDescriptor[] getMethodDescriptors()
		{
			// just add the reset color field first
			final Class<PolygonShape> c = PolygonShape.class;

			final MethodDescriptor[] mds =
			{ method(c, "addNode", null, "Add Node"), };

			return mds;
		}

		public String getName()
		{
			return PolygonShape.this.getName();
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				PropertyDescriptor[] res =
				{ prop("Filled", "whether to fill the polygon"),
						prop("Closed", "whether to close the polygon (ignored if filled)") };
				return res;

			}
			catch (IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}

	public void shift(WorldVector vector)
	{

		// ok, cycle through the points, moving each one
		Iterator<PolygonNode> pts = _nodes.iterator();
		while (pts.hasNext())
		{
			WorldLocation pt = (WorldLocation) pts.next().getLocation();
			WorldLocation newLoc = pt.add(vector);
			pt.setLat(newLoc.getLat());
			pt.setLong(newLoc.getLong());
			pt.setDepth(newLoc.getDepth());
		}

		// and update the outer bounding area
		calcPoints();

		// and inform the parent (so it can move the label)
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);
	}

	/**
	 * ************************************************************** embedded
	 * class which contains extended path editor, which renames a "Path" as a
	 * "Polygon" **************************************************************
	 */
	public static class PolygonPathEditor extends SwingWorldPathPropertyEditor
	{
		/**
		 * over-ride the type returned by the path editor
		 */
		protected String getMyType()
		{
			return "Polygon";
		}
	}

	public void shift(WorldLocation feature, WorldVector vector)
	{
		// ok, just shift it...
		feature.addToMe(vector);
		calcPoints();

		// and inform the parent (so it can move the label)
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);

	}

	public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
			ComponentConstruct currentNearest, Layer parentLayer)
	{
		// ok - pass through our corners
		Iterator<PolygonNode> myPts = _nodes.iterator();
		while (myPts.hasNext())
		{
			WorldLocation thisLoc = (WorldLocation) myPts.next().getLocation();
			// right, see if the cursor is at the centre (that's the easy component)
			checkThisOne(thisLoc, cursorLoc, currentNearest, this, parentLayer);
		}

	}

	public boolean getClosed()
	{
		return _closePolygon;
	}

	public void setClosed(boolean polygon)
	{
		_closePolygon = polygon;
	}

	@Override
	public int compareTo(Plottable arg0)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void exportShape()
	{
		// TODO Auto-generated method stub

	}

	public void addNode()
	{
		WorldLocation theLoc = getBounds().getCentreAtSurface();
		PolygonNode newNode = new PolygonNode("" + (_nodes.size() + 1), theLoc);
		_nodes.add(newNode);
	}

	@Override
	public void append(Layer other)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasOrderedChildren()
	{
		return true;
	}

	@Override
	public int getLineThickness()
	{
		return 0;
	}

	@Override
	public void add(Editable point)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeElement(Editable point)
	{
		if (point instanceof PolygonNode)
		{
			_nodes.remove(point);
		}
		else
		{
			System.err
					.println("tried to remove point from polygon that isn't a node!");
		}
	}

	@Override
	public Enumeration<Editable> elements()
	{
		// create suitable wrapper
		Vector<Editable> vec = new Vector<Editable>();

		// insert our nodes
		vec.addAll(_nodes);

		// done
		return vec.elements();
	}

}
