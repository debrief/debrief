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
package Debrief.Wrappers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;

import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Defaults;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.PlottableSelection;
import MWC.GUI.Renamable;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GUI.Shapes.HasDraggableComponents;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GUI.Tools.Operations.RightClickCutCopyAdaptor;
import MWC.GUI.Tools.Operations.RightClickCutCopyAdaptor.CopyItem;
import MWC.GUI.Tools.Operations.RightClickPasteAdaptor.PasteItem;
import MWC.GUI.Undo.UndoBuffer;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class ShapeWrapper extends MWC.GUI.PlainWrapper implements
    java.beans.PropertyChangeListener, MWC.GenericData.WatchableList,
    MWC.GenericData.Watchable, DraggableItem, HasDraggableComponents,
    Editable.DoNotHighlightMe, Renamable
{
  public final class ShapeInfo extends Editable.EditorType
  {

    public ShapeInfo(final ShapeWrapper data, final String theName)
    {
      super(data, theName, data._theShape.getType() + ":");
    }

    /**
     * whether the normal editable properties should be combined with the additional editable
     * properties into a single list. This is typically used for a composite object which has two
     * lists of editable properties but which is seen by the user as a single object To be
     * overwritten to change it
     */
    @Override
    public final boolean combinePropertyLists()
    {
      return true;
    }

    @Override
    public final BeanInfo[] getAdditionalBeanInfo()
    {
      // get our shape back
      final ShapeWrapper sp = (ShapeWrapper) super.getData();
      final MWC.GUI.Shapes.PlainShape ps = sp._theShape;
      if (sp instanceof MWC.GUI.Editable)
      {
        final MWC.GUI.Editable et = (MWC.GUI.Editable) ps;
        if (et.hasEditor() == true)
        {
          final BeanInfo[] res =
          {et.getInfo()};
          return res;
        }
      }

      return null;
    }

    @Override
    public final MethodDescriptor[] getMethodDescriptors()
    {
      // just add the reset color field first
      final Class<?> c = ShapeWrapper.class;
      final MethodDescriptor[] mds =
      {method(c, "exportThis", null, "Export Shape")};
      return mds;
    }

    @Override
    public final String getName()
    {
      return getLabel();
    }

    @Override
    public final PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] myRes =
        {displayProp("LabelColor", "Label color", "the text color", FORMAT),
            prop("Label", "the text showing", FORMAT), prop("Font",
                "the label font", FORMAT), displayProp("LabelLocation",
                    "Label location", "the relative location of the label",
                    FORMAT), prop("Visible", "whether this shape is visible",
                        VISIBILITY), displayProp("LabelVisible",
                            "Label visible", "whether the label is visible",
                            VISIBILITY), displayProp("StartDTGProperty",
                                "Time start", "the start date time group",
                                TEMPORAL), displayLongProp("LineStyle",
                                    "Line style",
                                    "the dot-dash style to use for plotting this shape",
                                    LineStylePropertyEditor.class, FORMAT),
            displayLongProp("LineThickness", "Line thickness",
                "the line-thickness to use for this shape",
                MWC.GUI.Properties.LineWidthPropertyEditor.class), prop("Color",
                    "the color of the shape itself", FORMAT), displayProp(
                        "EndDTGProperty", "Time end",
                        "the end date time group \n\r(or leave blank for to use Start as Centre time)",
                        TEMPORAL),};
        myRes[3].setPropertyEditorClass(
            MWC.GUI.Properties.LocationPropertyEditor.class);

        return myRes;

      }
      catch (final IntrospectionException e)
      {
        e.printStackTrace();
        return super.getPropertyDescriptors();
      }
    }

  }

  // ////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testMe extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    private static int countItems(final Layers layers)
    {
      int ctr = 0;

      final Enumeration<Editable> lIter = layers.elements();
      while (lIter.hasMoreElements())
      {
        final Editable next = lIter.nextElement();
        ctr++;
        if (next instanceof Layer)
        {
          @SuppressWarnings("unused")
          final Layer nextL = (Layer) next;
          final Enumeration<Editable> lIter2 = nextL.elements();
          while (lIter2.hasMoreElements())
          {
            @SuppressWarnings("unused")
            final Editable next2 = lIter2.nextElement();
            ctr++;
          }
        }
      }
      return ctr;
    }

    public testMe(final String val)
    {
      super(val);
    }

    public void testCopyPaste() throws UnsupportedFlavorException, IOException
    {
      final Layers layers = new Layers();
      final Layer base1 = new BaseLayer();
      base1.setName("base 1");
      layers.addThisLayer(base1);
      final Layer base2 = new BaseLayer();
      base2.setName("base 2");
      layers.addThisLayer(base2);
      final WorldLocation tl = new WorldLocation(1, 2, 3);
      final WorldLocation br = new WorldLocation(2, 3, 4);
      final ShapeWrapper s1 = new ShapeWrapper("rect", new RectangleShape(tl,
          br), Color.red, null);
      base1.add(s1);

      assertEquals("got items", 3, countItems(layers));

      // ok, copy the item

      // first, try to copy/paste the leg
      final Clipboard clipboard = new Clipboard("Debrief");
      final UndoBuffer buffer = new UndoBuffer();

      // duplicate the track
      final CopyItem copier1 = new RightClickCutCopyAdaptor.CopyItem(s1,
          clipboard, null, layers, null, buffer);
      copier1.execute();

      final Transferable tr1 = clipboard.getContents(this);
      // see if there is currently a plottable on the clipboard

      // extract the plottable
      final Plottable theData1 = (Plottable) tr1.getTransferData(
          PlottableSelection.PlottableFlavor);
      final PasteItem paster1 = new PasteItem(theData1, clipboard, base2,
          layers, true);
      paster1.execute();

      // jave we now got 4 items?
      assertEquals("got more items", 4, countItems(layers));
    }

    public final void testMyParams()
    {
      final MWC.GUI.Shapes.PlainShape ps = new MWC.GUI.Shapes.CircleShape(
          new WorldLocation(2d, 2d, 2d), 12);
      MWC.GUI.Editable ed = new ShapeWrapper("", ps,
          MWC.GUI.Properties.DebriefColors.RED, new HiResDate(0));
      editableTesterSupport.testParams(ed, this);
      ed = null;
    }

    public final void testTimes()
    {

      final WorldLocation scrapLoc = new WorldLocation(1, 1, 1);
      final WorldLocation scrapLoc2 = new WorldLocation(1, 3, 1);

      final WatchableList.TestWatchables tw = new WatchableList.TestWatchables()
      {
        @Override
        public WatchableList getBothDates(final HiResDate startDate,
            final HiResDate endDate)
        {
          final ShapeWrapper sw = new ShapeWrapper("blank",
              new MWC.GUI.Shapes.LineShape(scrapLoc, scrapLoc2),
              MWC.GUI.Properties.DebriefColors.RED, null)
          {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected long getThreshold()
            {
              return 5000 * 1000;
            }
          };
          sw.setStartDTG(new HiResDate(startDate));
          sw.setEndDTG(new HiResDate(endDate));
          return sw;
        }

        @Override
        public WatchableList getNullDates()
        {
          final ShapeWrapper sw = new ShapeWrapper("blank",
              new MWC.GUI.Shapes.LineShape(scrapLoc, scrapLoc2),
              MWC.GUI.Properties.DebriefColors.RED, null)
          {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected long getThreshold()
            {
              return 5000 * 1000;
            }
          };
          sw.setStartDTG(null);
          sw.setEndDTG(null);
          return sw;
        }

        @Override
        public WatchableList getStartDateOnly(final HiResDate startDate)
        {
          final ShapeWrapper sw = new ShapeWrapper("blank",
              new MWC.GUI.Shapes.LineShape(scrapLoc, scrapLoc2),
              MWC.GUI.Properties.DebriefColors.RED, null)
          {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected long getThreshold()
            {
              return 5000 * 1000;
            }
          };
          sw.setStartDTG(new HiResDate(startDate));
          sw.setEndDTG(null);
          return sw;
        }
      };

      tw.doTest(this);
    }
  }

  /**
   * property name to indicate that the symbol visibility has been changed
   */
  public static final String LABEL_VIS_CHANGED = "LABEL_VIS_CHANGE";

  /**
   * keep track of versions
   */
  static final long serialVersionUID = 1;

  public static void main(final String[] args)
  {
    final testMe tm = new testMe("scrap");
    tm.testTimes();
  }

  /**
   * the label
   */
  private final MWC.GUI.Shapes.TextLabel _theLabel;

  /**
   * the symbol for this label
   */
  final MWC.GUI.Shapes.PlainShape _theShape;

  /**
   * the start dtg for this label (although) this will frequently be null, for non time-related
   * entities. Where no end DTG is provided, this will be used as a centre point
   */
  private HiResDate _theStartDTG = null;

  /**
   * the end dtg for this label (although) this will frequently be null, for non time-related
   * entities. Where no end DTG is provided, this will be used as a centre point
   */
  private HiResDate _theEndDTG = null;

  /**
   * our editor
   */
  protected transient Editable.EditorType _myEditor = null;

  // ///////////////////////////////////////////////////////////
  // member functions
  // //////////////////////////////////////////////////////////

  // ///////////////////////////////////////////////////////////
  // constructor
  // //////////////////////////////////////////////////////////
  public ShapeWrapper(final String label, final PlainShape theShape,
      final Color theColor, final HiResDate theDate)
  {
    _theLabel = new MWC.GUI.Shapes.TextLabel(theShape, label);
    // store the shape
    _theShape = theShape;

    // set the default font
    setFont(Defaults.getFont());

    // and the color.
    _theLabel.setColor(theColor);

    // override the shape, just to be sure...
    _theShape.setColor(theColor);

    // tell the parent object what colour we are
    super.setColor(theColor);

    // store the date (which is initially used as a centre time)
    setStartDTG(theDate);
    setEndDTG(null);

    // also update the location of the text anchor
    updateLabelLocation();

    // add ourselves as the listener to the shape and
    // the label
    _theShape.addPropertyListener(this);
    _theLabel.addPropertyListener(this);
  }

  /**
   * instruct this object to clear itself out, ready for ditching
   * 
   */
  @Override
  public void closeMe()
  {
    // stop listening to property changes
    _theShape.removePropertyListener(this);
    _theLabel.removePropertyListener(this);
    super.closeMe();
  }

  /**
   * filter the list to the specified time period
   * 
   * @param start
   *          the start dtg of the period
   * @param end
   *          the end dtg of the period
   */
  @Override
  public final void filterListTo(final HiResDate start, final HiResDate end)
  {
    // do we have a DTG?
    if (getStartDTG() == null)
    {
      // don't bother, we don't have a DTG
      return;
    }

    // see if we are visible between the period
    final Collection<Editable> list = getItemsBetween(start, end);

    this.setVisible(false);

    if (list != null)
    {
      if (list.size() > 0)
        this.setVisible(true);
    }

    // if we have a property support class, fire the filtered event
    getSupport().firePropertyChange(
        MWC.GenericData.WatchableList.FILTERED_PROPERTY, null, null);

  }

  @Override
  public void findNearestHotSpotIn(final Point cursorPos,
      final WorldLocation cursorLoc, final ComponentConstruct currentNearest,
      final Layer parentLayer)
  {
    if (_theShape instanceof HasDraggableComponents)
    {
      final HasDraggableComponents dragger = (HasDraggableComponents) _theShape;
      dragger.findNearestHotSpotIn(cursorPos, cursorLoc, currentNearest,
          parentLayer);
    }
  }

  @Override
  public void findNearestHotSpotIn(final Point cursorPos,
      final WorldLocation cursorLoc, final LocationConstruct currentNearest,
      final Layer parentLayer, final Layers theData)
  {
    _theShape.findNearestHotSpotIn(cursorPos, cursorLoc, currentNearest,
        parentLayer, theData);

  }

  @Override
  public WorldArea getBounds()
  {
    // get the bounds from the data object (or its location object)
    final WorldArea res = new WorldArea(_theLabel.getBounds());
    res.extend(_theShape.getBounds());
    return res;
  }

  /**
   * method to fulfil requirements of WatchableList
   */
  @Override
  public final Color getColor()
  {
    return super.getColor();
  }

  @Override
  public final double getCourse()
  {
    // null implementation, not valid
    return 0;
  }

  /**
   * get the depth of the object. In this case, return the depth of the centre of the area covered
   * by the shape
   * 
   * @return the depth
   */
  @Override
  public final double getDepth()
  {
    // return the centre of the shape
    return _theShape.getBounds().getCentre().getDepth();
  }

  @Override
  public final HiResDate getEndDTG()
  {
    // return value, or -1 to indicate not time related
    return _theEndDTG;
  }

  public final HiResDate getEndDTGProperty()
  {
    // return value, or -1 to indicate not time related
    return HiResDate.wrapped(_theEndDTG);
  }

  public final Font getFont()
  {
    return _theLabel.getFont();
  }

  @Override
  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new ShapeInfo(this, this.getName());

    return _myEditor;
  }

  @Override
  public final java.util.Collection<Editable> getItemsBetween(
      final HiResDate start, final HiResDate end)
  {
    java.util.Vector<Editable> res = null;

    final HiResDate myStart = getStartDTG();
    final HiResDate myEnd = getEndDTG();

    // do we have any time data at all?
    if (myStart == null)
    {
      // no, so just return ourselves anyway
      res = new Vector<Editable>(0, 1);
      res.add(this);
      return res;
    }

    boolean done = false; // whether we have been able to process the
    // times

    // do we have an end time?
    if (myEnd != null)
    {
      // see if our time period overlaps
      if ((myStart.lessThan(end)) && (myEnd.greaterThan(start)))
      {
        res = new Vector<Editable>(0, 1);
        res.addElement(this);
      }

      done = true;
    }

    // ok, handled instance where we have start and end times. if we just
    // have a start time, see if it is inside the valid period
    if (!done)
    {

      // use the start time as a centre time
      if ((myStart.greaterThanOrEqualTo(start)) && (myStart.lessThanOrEqualTo(
          end)))
      {
        res = new Vector<Editable>(0, 1);
        res.addElement(this);
      }
      else
      {
        // no, it's outside the period
      }

    }

    return res;
  }

  public final String getLabel()
  {
    final String label = _theLabel.getString();
    if (label == null)
      return "";
    return label;
  }

  public final Color getLabelColor()
  {
    return _theLabel.getColor();
  }

  public final Integer getLabelLocation()
  {
    return _theLabel.getRelativeLocation();
  }

  /**
   * whether to show the label for this shape
   */
  public final boolean getLabelVisible()
  {
    return _theLabel.getVisible();
  }

  public final int getLineStyle()
  {
    return getShape().getLineStyle();
  }

  /**
   * the line thickness (convenience wrapper around width)
   * 
   * @return
   */
  public int getLineThickness()
  {
    return getShape().getLineWidth();
  }

  @Override
  public final WorldLocation getLocation()
  {
    return this.getBounds().getCentre();
  }

  @Override
  public final String getName()
  {
    return _theLabel.getString();
  }

  @Override
  public final MWC.GenericData.Watchable[] getNearestTo(final HiResDate DTG)
  {
    // special case, have we been asked for an invalid time period?
    if (DTG == TimePeriod.INVALID_DATE)
    {
      // yes, just return ourselves
      return new Watchable[]
      {this};
    }

    // Let's assume It is inside, then we validate it.
    boolean itIsInside = true;
    // We check the start date.
    itIsInside &= getStartDTG() == null || getStartDTG().lessThanOrEqualTo(DTG);
    itIsInside &= getEndDTG() == null || getEndDTG().greaterThan(DTG);

    if (itIsInside)
    {
      // We know it is inside.
      return new MWC.GenericData.Watchable[]
      {this};
    }
    else
    {
      return EMPTY_WATCHABLE_LIST;
    }
  }

  /**
   * get the shape we are containing
   */
  public final MWC.GUI.Shapes.PlainShape getShape()
  {
    return _theShape;
  }

  @Override
  public final MWC.GUI.Shapes.Symbols.PlainSymbol getSnailShape()
  {
    return null;
  }

  @Override
  public final double getSpeed()
  {
    // null implementation, not valid
    return 0;
  }

  @Override
  public final HiResDate getStartDTG()
  {
    // return value, or -1 to indicate not time related
    return _theStartDTG;
  }

  public final HiResDate getStartDTGProperty()
  {
    // return value, or -1 to indicate not time related
    return HiResDate.wrapped(_theStartDTG);
  }

  /**
   * get the threshold for which points should be visible
   * 
   * @return time either side in milliseconds
   */
  protected long getThreshold()
  {
    long res = MWC.GenericData.WatchableList.TIME_THRESHOLD;
    final String appThreshold = Debrief.GUI.Frames.Application.getThisProperty(
        "STEP_THRESHOLD");

    if (appThreshold != null)
    {
      if (appThreshold.length() > 0)
      {
        try
        {
          // get actual value (in seconds)
          res = Long.parseLong(appThreshold);
          // convert to micros
          res *= 1000000;
        }
        catch (final Exception e)
        {
          MWC.Utilities.Errors.Trace.trace(e,
              "Retrieving step threshold from properties");
        }
      }

    }

    return res;
  }

  /**
   * method to fulfil requirements of Watchable
   */
  @Override
  public final HiResDate getTime()
  {
    return getStartDTG();
  }

  // ///////////////////////////////////
  // manage the time period
  // ///////////////////////////////////
  public final TimePeriod getTimePeriod()
  {
    TimePeriod res = null;

    if (getStartDTG() != null)
    {
      res = new TimePeriod.BaseTimePeriod(getStartDTG(), getEndDTG());
    }
    return res;
  }

  /**
   * does this item have an editor?
   */
  @Override
  public final boolean hasEditor()
  {
    return true;
  }

  @Override
  public final void paint(final CanvasType dest)
  {
    // check if we are visible
    if (getVisible())
    {
      // sort out the line style
      dest.setLineStyle(_theShape.getLineStyle());

      // store the current line width
      final float lineWid = dest.getLineWidth();

      // and the line width
      dest.setLineWidth(_theShape.getLineWidth());

      // first paint the symbol
      _theShape.paint(dest);

      // and restore the line style
      dest.setLineStyle(CanvasType.SOLID);

      // and restore the line width
      dest.setLineWidth(lineWid);

      // now paint the text
      _theLabel.paint(dest);
    }
  }

  // ////////////////////////////////////////////////////
  // property change support
  // ///////////////////////////////////////////////////
  @Override
  public final void propertyChange(final PropertyChangeEvent p1)
  {
    if (p1.getSource() == _theShape)
    {
      if (p1.getPropertyName().equals(LOCATION_CHANGED))
      {
        updateLabelLocation();

        if (_theShape != null)
        {
          if (_theShape.getBounds() != null)
          {
            this.getInfo().fireChanged(this, LOCATION_CHANGED, null, _theShape
                .getBounds().getCentre());
          }
        }
      }
    }
  }

  /**
   * find the range from this shape to some point
   */
  @Override
  public final double rangeFrom(final WorldLocation other)
  {
    return Math.min(_theLabel.rangeFrom(other), _theShape.rangeFrom(other));
  }

  @Override
  @FireReformatted
  public final void setColor(final Color theCol)
  {
    super.setColor(theCol);

    // and set the colour of the shape
    _theShape.setColor(theCol);

    // don't forget the color of the label
    _theLabel.setColor(theCol);
  }

  /**
   * set the start time for this shape
   */
  public final void setEndDTG(final HiResDate val)
  {
    _theEndDTG = val;
  }

  /**
   * set the start time for this shape
   */
  public final void setEndDTGProperty(final HiResDate val)
  {
    _theEndDTG = HiResDate.unwrapped(val);
  }

  public final void setFont(final Font theFont)
  {
    _theLabel.setFont(theFont);

    // we must also provide the font to the shape, in case it's able to display
    // text (such as the rng/brg on a line shape)
    _theShape.setFont(theFont);
  }

  @FireReformatted
  public void setLabel(final String val)
  {
    _theLabel.setString(val);
  }

  public final void setLabelColor(final Color theCol)
  {
    _theLabel.setColor(theCol);
  }

  public final void setLabelLocation(final Integer val)
  {
    _theLabel.setRelativeLocation(val);

    // and update the label relative to the shape if necessary
    updateLabelLocation();
  }

  // ////////////////////////////////////////////////////
  // watchable support
  // ///////////////////////////////////////////////////

  /**
   * whether to show the label for this shape
   */
  public final void setLabelVisible(final boolean val)
  {
    _theLabel.setVisible(val);

    // ok, inform any listeners
    getSupport().firePropertyChange(ShapeWrapper.LABEL_VIS_CHANGED, null,
        new Boolean(val));

  }

  public final void setLineStyle(final int style)
  {
    getShape().setLineStyle(style);
  }

  /**
   * the line thickness (convenience wrapper around width)
   */
  public void setLineThickness(final int val)
  {
    getShape().setLineWidth(val);
  }

  @Override
  public void setName(final String val)
  {
    setLabel(val);
  }

  /**
   * set the start time for this shape
   */
  public final void setStartDTG(final HiResDate val)
  {
    _theStartDTG = val;
  }

  /**
   * set the start time for this shape
   */
  public final void setStartDTGProperty(final HiResDate val)
  {
    _theStartDTG = HiResDate.unwrapped(val);
  }

  public final void setTimePeriod(final TimePeriod val)
  {
    this.setStartDTGProperty(val.getStartDTG());
    this.setEndDTGProperty(val.getEndDTG());
  }

  @Override
  public void shift(final WorldLocation feature, final WorldVector vector)
  {
    // ok, move the indicated point
    final WorldLocation theLoc = feature;
    theLoc.addToMe(vector);
  }

  @Override
  public void shift(final WorldVector vector)
  {
    // ok - apply the offset
    final DraggableItem dragee = _theShape;
    dragee.shift(vector);
  }

  @Override
  public String toString()
  {
    final String label = _theLabel.getString();
    if (label == null)
      return _theShape.getName();
    return _theShape.getName() + ":" + label;
  }

  private void updateLabelLocation()
  {
    final WorldLocation newLoc = _theShape.getAnchor(_theLabel
        .getRelativeLocation().intValue());
    _theLabel.setLocation(newLoc);
  }
}
