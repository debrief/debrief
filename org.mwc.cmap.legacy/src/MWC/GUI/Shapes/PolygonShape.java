/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package MWC.GUI.Shapes;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GUI.CreateEditorForParent;
import MWC.GUI.Editable;
import MWC.GUI.ExtendedCanvasType;
import MWC.GUI.FireExtended;
import MWC.GUI.Layer;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class PolygonShape extends PlainShape implements Editable,
    HasDraggableComponents, Layer
{

  // ////////////////////////////////////////////////
  // member variables
  // ////////////////////////////////////////////////

  // ////////////////////////////////////////////////////
  // bean info for this class
  // ///////////////////////////////////////////////////
  public class PolygonInfo extends Editable.EditorType
  {

    public PolygonInfo(final PolygonShape data, final String theName)
    {
      super(data, theName, "");
    }

    @Override
    public final MethodDescriptor[] getMethodDescriptors()
    {
      // just add the reset color field first
      final Class<PolygonShape> c = PolygonShape.class;

      final MethodDescriptor[] mds =
      {method(c, "addNode", null, "Add Node"),};

      return mds;
    }

    @Override
    public String getName()
    {
      return PolygonShape.this.getName();
    }

    @Override
    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
        {prop("Filled", "whether to fill the polygon", FORMAT), displayProp(
            "SemiTransparent", "Semi transparent",
            "whether the filled polygon is semi-transparent", FORMAT),
            displayProp("ShowNodeLabels", "Show node labels",
                "whether to label the nodes", FORMAT), prop("Closed",
                    "whether to close the polygon (ignored if filled)",
                    FORMAT)};
        return res;

      }
      catch (final IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

  public static class PolygonNode implements Editable, Plottable,
      CreateEditorForParent, Serializable
  {

    // ////////////////////////////////////////////////////
    // bean info for this class
    // ///////////////////////////////////////////////////
    public class PolygonNodeInfo extends Editable.EditorType
    {

      public PolygonNodeInfo(final PolygonNode data, final String theName)
      {
        super(data, theName, "");
      }

      @Override
      public String getName()
      {
        return PolygonNode.this.getName();
      }

      @Override
      public PropertyDescriptor[] getPropertyDescriptors()
      {
        try
        {
          final PropertyDescriptor[] res =
          {prop("Location", "the location of this node"),};
          return res;

        }
        catch (final IntrospectionException e)
        {
          return super.getPropertyDescriptors();
        }
      }
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String _myName;
    private WorldLocation _myLocation;
    private transient EditorType _myEditor;
    private transient PolygonShape _myParent;

    public PolygonNode(final String name, final WorldLocation location,
        final PolygonShape parent)
    {
      _myName = name;
      _myLocation = location;
      _myParent = parent;
    }

    /**
     * self-destruct
     *
     */
    public void close()
    {
      _myEditor = null;
    }

    @Override
    public int compareTo(final Plottable o)
    {
      return this.toString().compareTo(o.toString());
    }

    @Override
    public WorldArea getBounds()
    {
      return new WorldArea(_myLocation, _myLocation);
    }

    @Override
    public EditorType getInfo()
    {
      if (_myEditor == null)
        _myEditor = new PolygonNodeInfo(this, this.getName());

      return _myEditor;
    }

    public WorldLocation getLocation()
    {
      return _myLocation;
    }

    @Override
    public String getName()
    {
      return _myName;
    }

    @Override
    public Editable getParent()
    {
      return _myParent;
    }

    @Override
    public boolean getVisible()
    {
      return true;
    }

    @Override
    public boolean hasEditor()
    {
      return true;
    }

    @Override
    public void paint(final CanvasType dest)
    {
      // ingore
      System.err.println("a polygon node should not be painting itself!");
    }

    @Override
    public double rangeFrom(final WorldLocation other)
    {
      return _myLocation.rangeFrom(other);
    }

    public void setLocation(final WorldLocation loc)
    {
      _myLocation = loc;
    }

    @Override
    public void setVisible(final boolean val)
    {
      // ignore
    }

    @Override
    public String toString()
    {
      return getName();
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

  // ////////////////////////////////////////////////
  // constructor
  // ////////////////////////////////////////////////

  /**
   * whether to join the ends of the polygon
   *
   */
  private boolean _closePolygon = true;

  // ////////////////////////////////////////////////
  // member functions
  // ////////////////////////////////////////////////

  /**
   * whether to show labels for the nodes
   *
   */
  private boolean _showLabels = true;

  /**
   * constructor
   *
   * @param vector
   *          the WorldLocation marking the centre of the polygon
   */
  public PolygonShape(final Vector<PolygonNode> vector)
  {
    super(0, "Polygon");

    // and store it
    _nodes = vector;

    // now represented our polygon as an area
    calcPoints();
  }

  @Override
  @FireExtended
  public void add(final Editable point)
  {
    if (point instanceof PolygonNode)
    {
      _nodes.add((PolygonNode) point);
      calcPoints();
    }
  }

  public void addNode()
  {
    final WorldLocation theLoc = getBounds().getCentreAtSurface();

    final String theName;
    if (_nodes.size() == 0)
      theName = "1";
    else
    {
      final String lastName = _nodes.lastElement().getName();
      final int nameCtr = Integer.parseInt(lastName);
      theName = "" + (nameCtr + 1);
    }

    final PolygonNode newNode = new PolygonNode(theName, theLoc, this);
    _nodes.add(newNode);
  }

  @Override
  public void append(final Layer other)
  {

  }

  /**
   * calculate some convenience values based on the radius and centre of the polygon
   */
  public void calcPoints()
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
      final Iterator<PolygonNode> points = _nodes.iterator();
      while (points.hasNext())
      {
        final WorldLocation next = points.next().getLocation();
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
      _theAnchor = new WorldLocation(lats / _nodes.size(), longs / _nodes
          .size(), 0);
    }
  }

  @Override
  protected WorldLocation centreFor(final WorldArea bounds)
  {
    final WorldLocation res;

    if (_nodes != null && _nodes.size() > 0)
    {
      final int len = _nodes.size();

      // work out total of lats & longs
      double latSum = 0;
      double longSum = 0;
      for (final PolygonNode n : _nodes)
      {
        final WorldLocation loc = n.getLocation();
        latSum += loc.getLat();
        longSum += loc.getLong();
      }
      // and finally the mean of those values
      res = new WorldLocation(latSum / len, longSum / len, 0d);
    }
    else
    {
      res = bounds.getCentre();
    }
    return res;

  }

  @Override
  public int compareTo(final Plottable arg0)
  {
    return 0;
  }

  @Override
  public Enumeration<Editable> elements()
  {
    // create suitable wrapper
    final Vector<Editable> vec = new Vector<Editable>();

    // insert our nodes
    vec.addAll(_nodes);

    // done
    return vec.elements();
  }

  @Override
  public void exportShape()
  {

  }

  @Override
  public void findNearestHotSpotIn(final Point cursorPos,
      final WorldLocation cursorLoc, final ComponentConstruct currentNearest,
      final Layer parentLayer)
  {
    // ok - pass through our corners
    final Iterator<PolygonNode> myPts = _nodes.iterator();
    while (myPts.hasNext())
    {
      final WorldLocation thisLoc = myPts.next().getLocation();
      // right, see if the cursor is at the centre (that's the easy component)
      checkThisOne(thisLoc, cursorLoc, currentNearest, this, parentLayer);
    }

  }

  /**
   * get the 'anchor point' for any labels attached to this shape
   */
  public WorldLocation getAnchor()
  {
    return _theAnchor;
  }

  @Override
  public WorldArea getBounds()
  {
    return _theArea;
  }

  public boolean getClosed()
  {
    return _closePolygon;
  }

  @Override
  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new PolygonInfo(this, this.getName());

    return _myEditor;
  }

  @Override
  public int getLineThickness()
  {
    return 0;
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

  public Color getPolygonColor()
  {
    return super.getColor();
  }

  public boolean getShowNodeLabels()
  {
    return _showLabels;
  }

  @Override
  public boolean hasEditor()
  {
    return true;
  }

  @Override
  public boolean hasOrderedChildren()
  {
    return true;
  }

  @Override
  public void paint(final CanvasType dest)
  {
    // are we visible?
    if (!getVisible())
      return;

    if (this.getColor() != null)
    {
      // create a transparent colour
      final Color newcol = getColor();

      dest.setColor(new Color(newcol.getRed(), newcol.getGreen(), newcol
          .getBlue(), TRANSPARENCY_SHADE));
    }

    // check we have some points
    if (_nodes == null)
      return;

    if (_nodes.size() > 0)
    {
      // create our point lists
      final int[] xP = new int[_nodes.size()];
      final int[] yP = new int[_nodes.size()];

      // ok, step through the area
      final Iterator<PolygonNode> points = _nodes.iterator();

      int counter = 0;

      while (points.hasNext())
      {
        final PolygonNode node = points.next();
        final WorldLocation next = node.getLocation();

        // convert to screen
        final Point thisP = dest.toScreen(next);

        // remember the coords
        xP[counter] = thisP.x;
        yP[counter] = thisP.y;

        // move the counter
        counter++;

        if (_showLabels)
        {
          // and show this label
          final int yPos = thisP.y + 5;
          dest.drawText(node.getName(), thisP.x + 5, yPos);
        }
      }

      // ok, now plot it
      if (getFilled())
      {
        if (getSemiTransparent() && dest instanceof ExtendedCanvasType)
        {
          final ExtendedCanvasType ext = (ExtendedCanvasType) dest;
          ext.semiFillPolygon(xP, yP, xP.length);
        }
        else
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
   * get the range from the indicated world location - making this abstract allows for individual
   * shapes to have 'hit-spots' in various locations.
   */
  @Override
  public double rangeFrom(final WorldLocation point)
  {
    double res = -1;

    if (_nodes.size() > 0)
    {

      // loop through the legs
      final Enumeration<PolygonNode> points = _nodes.elements();

      WorldLocation last = null;
      WorldDistance shortest = null;
      while (points.hasMoreElements())
      {
        final WorldLocation thisL = points.nextElement().getLocation();
        if (last != null)
        {
          final WorldDistance dist = point.rangeFrom(last, thisL);

          if (shortest == null)
          {
            shortest = dist;
          }
          else if (shortest.greaterThan(dist))
          {
            shortest = dist;
          }
        }
        last = thisL;
      }
      res = shortest.getValueIn(WorldDistance.DEGS);
    }

    return res;
  }

  @Override
  @FireExtended
  public void removeElement(final Editable point)
  {
    if (point instanceof PolygonNode)
    {
      _nodes.remove(point);
      final PolygonNode node = (PolygonNode) point;
      node.close();
    }
    else
    {
      System.err.println(
          "tried to remove point from polygon that isn't a node!");
    }
  }

  public void setClosed(final boolean polygon)
  {
    _closePolygon = polygon;
  }

  @Override
  public void setName(final String val)
  {
    _myName = val;
  }

  public void setPolygonColor(final Color val)
  {
    super.setColor(val);
  }

  public void setShowNodeLabels(final boolean showLabels)
  {
    _showLabels = showLabels;
  }

  @Override
  public void shift(final WorldLocation feature, final WorldVector vector)
  {
    // ok, just shift it...
    feature.addToMe(vector);
    calcPoints();

    // and inform the parent (so it can move the label)
    firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);

  }

  @Override
  public void shift(final WorldVector vector)
  {

    // ok, cycle through the points, moving each one
    final Iterator<PolygonNode> pts = _nodes.iterator();
    while (pts.hasNext())
    {
      final WorldLocation pt = pts.next().getLocation();
      final WorldLocation newLoc = pt.add(vector);
      pt.setLat(newLoc.getLat());
      pt.setLong(newLoc.getLong());
      pt.setDepth(newLoc.getDepth());
    }

    // and update the outer bounding area
    calcPoints();

    // and inform the parent (so it can move the label)
    firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);
  }

}
