package Debrief.Tools.Reconstruction;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: DragTrackEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.14 $
// $Log: DragTrackEditor.java,v $
// Revision 1.14  2006/06/27 10:06:16  Ian.Mayo
// Correct how we drag tracks (allow for fixes being in child layer of trck)
//
// Revision 1.13  2005/12/13 09:04:52  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.12  2005/03/01 15:23:25  Ian.Mayo
// Recognise that some TUA's may be read in as relative, not just absolute.  Treat them accordingly in use, and when stored to file.
//
// Revision 1.11  2004/11/25 10:24:34  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.10  2004/11/22 13:41:04  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.9  2004/09/09 10:23:09  Ian.Mayo
// Reflect method name change in Layer interface
//
// Revision 1.8  2004/07/23 09:18:18  Ian.Mayo
// Oooh, too many technical problems
//
// Revision 1.7  2004/07/16 12:15:32  Ian.Mayo
// Fix problem encountered when multiple fixes at same DTG
//
// Revision 1.6  2004/07/08 15:45:31  Ian.Mayo
// Finish off undo operation, accept recommended inspection bits
//
// Revision 1.5  2004/07/08 10:27:40  Ian.Mayo
// Last tidying
//
// Revision 1.4  2004/07/07 15:12:16  Ian.Mayo
// Update after visible points changed
//
// Revision 1.3  2004/07/07 14:24:23  Ian.Mayo
// Up & running.
//
// Revision 1.2  2004/07/07 13:31:45  Ian.Mayo
// Work in progress, but looking good so far
//
// Revision 1.1.1.2  2003/07/21 14:49:07  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.8  2003-06-10 14:34:38+01  ian_mayo
// Make sure the rubber band is created afresh each time
//
// Revision 1.7  2003-06-09 09:22:55+01  ian_mayo
// refactored to remove extra rubberband parameter to set chart drag listener
//
// Revision 1.6  2003-06-05 16:30:42+01  ian_mayo
// Remove d-line
//
// Revision 1.5  2003-06-05 16:01:52+01  ian_mayo
// Clear up after ourselves, and stop creating a new rubber band on each call
//
// Revision 1.4  2003-03-19 15:37:09+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.3  2002-07-08 11:54:19+01  ian_mayo
// <>
//
// Revision 1.2  2002-06-05 12:56:30+01  ian_mayo
// unnecessarily loaded
//
// Revision 1.1  2002-05-31 16:18:25+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-05-31 16:18:01+01  ian_mayo
// <>
//
// Revision 1.1  2002-05-31 16:16:59+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-05-28 09:25:45+01  ian_mayo
// after switch to new system

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyEditorSupport;
import java.util.*;

import javax.swing.*;

import Debrief.Tools.Tote.Watchable;
import Debrief.Wrappers.*;
import MWC.Algorithms.*;
import MWC.GUI.*;
import MWC.GUI.Dialogs.DialogFactory;
import MWC.GUI.Properties.*;
import MWC.GUI.Properties.Swing.SwingPropertiesPanel;
import MWC.GUI.ptplot.jfreeChart.*;
import MWC.GUI.ptplot.jfreeChart.Utils.*;
import MWC.GenericData.*;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

import com.jrefinery.chart.*;
import com.jrefinery.chart.tooltips.XYToolTipGenerator;
import com.jrefinery.data.*;

public final class DragTrackEditor extends PropertyEditorSupport implements ActionListener,
  PlainPropertyEditor.EditorUsesChart,
  PlainChart.ChartDragListener,
  PlainPropertyEditor.EditorUsesPropertyPanel, Layers.DataListener
{
  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  JLabel _theLabel;
  private JPanel _theHolder;
  private JToggleButton _doDragBtn;

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  /**
   * the value we are editing
   */
  TrackWrapper _theTrack;

  /**
   * the chart object which we are letting the user select from
   */
  PlainChart _theChart;

  /**
   * whether to put the dtgs on the track
   */
  boolean _showDTG;

  /**
   * the old chart drag listener
   */
  private transient PlainChart.ChartDragListener _oldListener;

  /**
   * working location
   */
  final transient WorldLocation _dragOrigin = new WorldLocation(0.0, 0.0, 0.0);

  /**
   * working location
   */
  private final transient WorldLocation _dragDestination = new WorldLocation(0.0, 0.0, 0.0);

  /**
   * working location
   */
  final transient WorldLocation _tmpLocation = new WorldLocation(0.0, 0.0, 0.0);

  /**
   * working location
   */
  final transient Point _tmpPointA = new Point();

  /**
   * working location
   */
  final transient Point _tmpPointB = new Point();

  /**
   * our rubber band singleton.  We create this afresh each time to get a new draggable track
   */
  private transient Rubberband _myRubberband;

  /**
   * the properties panel we're being shown in (so we can insert our stacked dots into it)
   */
  private PropertiesPanel _myPanel;

  /**
   * the graph of stacked dots we're showing
   */
  private JComponent _stackedDots;

  /**
   * the panel which our graph gets inserted into. this gets returned when we add our panel to the properties window. We
   * keep a handle to it so that we can close the graph when we're closed, or when the drag is cancelled.
   */
  private JPanel _stackedDotHolder;

  /**
   * utility class to help generate the stacked dot values
   */
  private StackedDotHelper _myHelper;

  /**
   * the plot we're updating
   */
  XYPlot _myPlot;

  /**
   * flag indicating whether we should override the y-axis to ensure that zero is always in the centre
   */
  boolean _centreYAxis = true;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /**
   * provide the gui to edit this property
   *
   * @return the property editor component
   */
  public final Component getCustomEditor()
  {
    // the panel we return
    _theHolder = new JPanel();
    _theHolder.setLayout(new BorderLayout());


    final JPanel buttonBar = new JPanel();
    buttonBar.setLayout(new GridLayout(1, 0));
    final JCheckBox showDTGBtn = new JCheckBox("DTG", false);
    showDTGBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        _showDTG = showDTGBtn.isSelected();
      }
    });
    showDTGBtn.setToolTipText("Show DTG for points");
    buttonBar.add(showDTGBtn);


    _theHolder.add("West", buttonBar);


    _doDragBtn = new JToggleButton("Drag", false);
    _doDragBtn.setToolTipText("Click to start dragging the position");
    _doDragBtn.setName("SelectPoint");
    _doDragBtn.setMargin(new Insets(0, 0, 0, 0));
    _doDragBtn.addActionListener(this);
    _theHolder.add("East", _doDragBtn);
    resetData();

    // and make it the smallest size possible
    _theHolder.setPreferredSize(_theHolder.getMinimumSize());

    // all done.
    return _theHolder;
  }


  /**
   * handle any button clicks from this dialog
   *
   * @param p1 details of what happened
   */
  public final void actionPerformed(final ActionEvent p1)
  {
    if (p1.getSource() == _doDragBtn)
    {
      if (_doDragBtn.isSelected())
      {
        _oldListener = _theChart.getChartDragListener();
        _doDragBtn.setText("Cancel");
        _theChart.setChartDragListener(this);

        // ok, popup the graph
        showStackedDots();

      }
      else
      {
        // ok, finished, stop dragging
        cancelDrag();
      }
    }
  }

  /**
   * User has stopped dragging. clear the dragging settings
   */
  private void cancelDrag()
  {
    _doDragBtn.setText("Drag");

    if (_oldListener != null)
    {
      _theChart.setChartDragListener(_oldListener);
      _oldListener = null;
    }

    // clear the rubber band
    _myRubberband.setActive(false);
    _myRubberband = null;

    // and now hide the dragging graph
    hideStackedDots();
  }

  /**
   * method to create a working plot (to contain our data)
   *
   * @return the chart, in it's own panel
   */
  private ChartPanel createStackedPlot()
  {

    // first create the x (time) axis
    final HorizontalDateAxis xAxis = new HorizontalDateAxis("time");

    xAxis.setStandardTickUnits(DateAxisEditor.createStandardDateTickUnitsAsTickUnits());

    // now the y axis, inverting it if applicable
    final ModifiedVerticalNumberAxis yAxis = new ModifiedVerticalNumberAxis("Error (degs)");

    // create the special stepper plot
    _myPlot = new XYPlot(null, xAxis, yAxis);
    // create the bit to create custom tooltips
    final XYToolTipGenerator tooltipGenerator = new DatedToolTipGenerator();

    // and the bit to plot individual points in discrete colours
    _myPlot.setRenderer(new ColourStandardXYItemRenderer(StandardXYItemRenderer.SHAPES_AND_LINES,
                                                         tooltipGenerator,
                                                         null));
    // put the plot into a chart
    final FormattedJFreeChart fj = new FormattedJFreeChart("Bearing error", null, _myPlot, false);
    fj.setShowSymbols(true);

    final ChartPanel plotHolder = new ChartPanel(fj)
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/**
       * Handles a 'mouse released' event. <P> On Windows, we need to check if this is a popup trigger, but only if we
       * haven't already been tracking a zoom rectangle.
       *
       * @param e Information about the event.
       */
      public void mouseReleased(final MouseEvent e)
      {
        super.mouseReleased(e);    //To change body of overridden methods use File | Settings | File Templates.

        // so, has the user just zoomed out?  if so, we will continue to do the rescale at each
        // step
        if (_myPlot.getVerticalValueAxis().isAutoRange())
        {
          // ok. user has zoomed out. continue to centre the y axis
          _centreYAxis = true;

          // just do a quick re-plot, to re-centre the data
          updateStackedDots(new WorldVector(0.0, 0.0, 0.0));
        }
        else
        {
          _centreYAxis = false;
        }
      }

    };
    plotHolder.setMouseZoomable(true, true);
    plotHolder.setDisplayToolTips(false);

    return plotHolder;
  }

  /**
   * user has started dragging, show the graph of stacked dots
   */
  private void showStackedDots()
  {
    final SwingPropertiesPanel spp = (SwingPropertiesPanel) _myPanel;

    // just check we haven't already created it.
    if (_stackedDots != null)
    {
      // don't bother. we've created it already.
      // just bring it to the front
      spp.show(_stackedDotHolder);
    }
    else
    {
      _stackedDots = createStackedPlot();
      _stackedDots.setName("Match brg fans");

      _stackedDotHolder = spp.addThisPanel(_stackedDots);

      // get the layers we're looking at
      final Layers theLayers = _theChart.getLayers();

      // ok. create the helper
      _myHelper = new StackedDotHelper(theLayers, _theTrack);

      // done. just trigger an update to show some data
      updateStackedDots(new WorldVector(0.0, 0.0, 0.0));


      // also, set ourselves up to listen for tracks being hidden/revealed, so we can regenerate
      // the series of sensors shown
      theLayers.addDataReformattedListener(this);


    }
  }

  /**
   * dragging is now complete. Hide the graph
   */
  private void hideStackedDots()
  {
    // ok. remove the stacked dots panel
    _myPanel.remove(_stackedDotHolder);

    // and reset the helper
    _myHelper.reset();

    // now ditch our working objects
    _stackedDots = null;
    _stackedDotHolder = null;
    _myHelper = null;

    // stop listening to the layers being reformatted
    _theChart.getLayers().removeDataReformattedListener(this);
  }


  /**
   * the track has been moved, update the dots
   */
  void updateStackedDots(final WorldVector offset)
  {
    // get the current set of data to plot
    final TimeSeriesCollection newData = _myHelper.getUpdatedSeries(offset);

    if (_centreYAxis)
    {
      // set the y axis to autocalculate
      _myPlot.getVerticalValueAxis().setAutoRange(true);
    }

    // store the new data (letting it autocalcualte)
    _myPlot.setDataset(newData);

    // we will only centre the y-axis if the user hasn't performed a zoom operation
    if (_centreYAxis)
    {
      // do a quick fudge to make sure zero is in the centre
      final Range rng = _myPlot.getVerticalValueAxis().getRange();
      final double maxVal = Math.max(Math.abs(rng.getLowerBound()), Math.abs(rng.getUpperBound()));
      _myPlot.getVerticalValueAxis().setRange(-maxVal, maxVal);
    }
  }


  //////////////////////////////////////////////////
  // support for data-reformatted listener
  //////////////////////////////////////////////////

  public void dataModified(final Layers theData, final Layer changedLayer)
  {
  }

  public void dataExtended(final Layers theData)
  {
  }

  public void dataReformatted(final Layers theData, final Layer changedLayer)
  {
    // re-initialise the set of contacts we're listening to
    _myHelper.initialise();

    // and get it to replot itself
    updateStackedDots(new WorldVector(0.0, 0.0, 0.0));
  }

  /**
   * operation cancelled (or being initialised)
   */
  private void resetData()
  {

  }


  public final boolean supportsCustomEditor()
  {
    return true;
  }

  public final Object getValue()
  {
    return _theTrack;
  }

  public final void setChart(final PlainChart theChart)
  {
    _theChart = theChart;
  }

  public final void doClose()
  {
    if (_oldListener != null)
    {
      // ok, stop dragging
      cancelDrag();
    }
  }

  public final void setValue(final Object p1)
  {
    if (p1 instanceof TrackWrapper)
    {
      _theTrack = (TrackWrapper) p1;
      resetData();
    }
    else
      return;
  }


  //////////////////////////////////////////////////
  // support for handling property pages (used for the stacked dots)
  //////////////////////////////////////////////////
  /**
   * this is the property panel we're using
   *
   * @param thePanel
   */
  public void setPanel(final PropertiesPanel thePanel)
  {
    // store the property panel
    _myPanel = thePanel;
  }

  /////////////////////////////////////////////////////////////////
  // support for dragging
  //////////////////////////////////////////////////////////////////
  public final void areaSelected(final WorldLocation theLocation, final Point thePoint)
  {
    // produce the offset
    final WorldVector offset = theLocation.subtract(_dragOrigin);

    // create an action to represent the shift
    final MWC.GUI.Tools.Action doShift = new DragTrackAction(offset, _theTrack, _theChart.getLayers());

    // put the action on the buffer
    _myPanel.getBuffer().add(doShift);

    // apply the action
    doShift.execute();
  }

  public final void startDrag(final WorldLocation theLocation, final Point thePoint)
  {
    _dragOrigin.copy(theLocation);
  }

  public final void dragging(final WorldLocation theLocation, final Point thePoint)
  {
    _dragDestination.copy(theLocation);
  }

  public final Rubberband getRubberband()
  {
    if (_myRubberband == null)
    {
      _myRubberband = new RubberbandTrack();
    }
    return _myRubberband;
  }


  //////////////////////////////////////////////////
  // helper class to provide support to the stacked dots
  //////////////////////////////////////////////////
  public static final class StackedDotHelper
  {
    /**
     * the set of layers for this plot
     */
    private Layers _myData;

    /**
     * the track being dragged
     */
    private TrackWrapper _primaryTrack;

    /**
     * the secondary track we're monitoring
     */
    private TrackWrapper _secondaryTrack;

    /**
     * the set of points to watch on the primary track
     */
    private Vector<Doublet> _primaryDoublets;

    /**
     * the set of points to watch on the secondary track
     */
    private Vector<Doublet> _secondaryDoublets;

    //////////////////////////////////////////////////
    // CONSTRUCTOR
    //////////////////////////////////////////////////

    /**
     * constructor - takes a set of layers, within which it identifies the track wrappers
     *
     * @param theData the set of data to provide stacked dots for
     * @param myTrack the track being dragged
     */
    StackedDotHelper(final Layers theData, final TrackWrapper myTrack)
    {
      setData(theData);
      _primaryTrack = myTrack;
    }

    //////////////////////////////////////////////////
    // MEMBER METHODS
    //////////////////////////////////////////////////

    /**
     * ok, our track has been dragged, calculate the new series of offsets
     *
     * @param currentOffset how far the current track has been dragged
     * @return the set of data items to plot
     */
    public TimeSeriesCollection getUpdatedSeries(final WorldVector currentOffset)
    {
      // ok, find the track wrappers
      if (_secondaryTrack == null)
        initialise();

      // create the collection of series
      final TimeSeriesCollection theTimeSeries = new TimeSeriesCollection();

      // produce a dataset for each track
      final BasicTimeSeries primarySeries = new BasicTimeSeries(_primaryTrack.getName(), FixedMillisecond.class);
      final BasicTimeSeries secondarySeries = new BasicTimeSeries(_secondaryTrack.getName(), FixedMillisecond.class);

      // ok, run through the points on the primary track
      Iterator<Doublet> iter = _primaryDoublets.iterator();
      while (iter.hasNext())
      {
        final Doublet thisD = (Doublet) iter.next();

        final Color thisColor = thisD.getColor();
        final double thisValue = thisD.calculateError(currentOffset, null);
        final HiResDate currentTime = thisD.getDTG();

        // create a new, correctly coloured data item
        // HI-RES NOT DONE - should provide FixedMicrosecond structure
        final ColouredDataItem newItem = new ColouredDataItem(new FixedMillisecond(currentTime.getDate().getTime()),
                                                              thisValue,
                                                              thisColor,
                                                              false,
                                                              null);

        try
        {
          // and add it to the series
          primarySeries.add(newItem);
        }
        catch (SeriesException e)
        {
          // hack:  we shouldn't be allowing this exception.  Look at why we're getting the same
          // time period being entered twice for this track.

          // Stop catching the error, load Dave W's holistic approach plot file,
          // and check the track/fix which is causing the problem.

          //          e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

      }

      // ok, run through the points on the primary track
      iter = _secondaryDoublets.iterator();
      while (iter.hasNext())
      {
        final Doublet thisD = (Doublet) iter.next();

        final Color thisColor = thisD.getColor();
        final double thisValue = thisD.calculateError(null, currentOffset);
        final HiResDate currentTime = thisD.getDTG();

        // create a new, correctly coloured data item
        // HI-RES NOT DONE - should have FixedMicrosecond structure
        final ColouredDataItem newItem = new ColouredDataItem(new FixedMillisecond(currentTime.getDate().getTime()),
                                                              thisValue,
                                                              thisColor,
                                                              false,
                                                              null);

        try
        {
          // and add it to the series
          secondarySeries.add(newItem);
        }
        catch (SeriesException e)
        {
          MWC.Utilities.Errors.Trace.trace("Multiple fixes at same DTG when producing stacked dots - prob ignored", false);
        }
      }

      // ok, add these new series
      theTimeSeries.addSeries(primarySeries);
      theTimeSeries.addSeries(secondarySeries);

      return theTimeSeries;
    }


    /**
     * initialise the data, check we've got sensor data & the correct number of visible tracks
     */
    void initialise()
    {

      _secondaryTrack = null;

      final Enumeration<Editable> theLayers = getData().elements();
      while (theLayers.hasMoreElements())
      {
        final Layer thisLayer = (Layer) theLayers.nextElement();

        // is this layer visible?
        if (thisLayer.getVisible())
        {

          if (thisLayer != _primaryTrack)
          {
            // is this a track wrapper?
            if (thisLayer instanceof TrackWrapper)
            {
              // ok.  now see if there are any sensorwrappers
              final TrackWrapper thisTrack = (TrackWrapper) thisLayer;

              if (thisTrack.getVisible())
              {
                // ok.  found some sensor data/
                // do we already have a track?
                if (_secondaryTrack == null)
                {
                  // no, cool.  sorted
                  _secondaryTrack = thisTrack;
                }
                else
                {
                  DialogFactory.showMessage("Drag Tracks", "Only one visible track allowed in addition to track being edited. Please hide one.");
                }
              } // whether this track is visible
            } // whether this is a track at all
          } // whether this is a track other than us
        } // whether this layer is visible
      } // looping through the tracks

      // so, do we have primary and secondary tracks?
      if (_primaryTrack != null && _secondaryTrack != null)
      {
        // cool sort out the list of sensor locations for these tracks
        _primaryDoublets = getDoublets(_primaryTrack, _secondaryTrack);
        _secondaryDoublets = getDoublets(_secondaryTrack, _primaryTrack);
      }
    }

    private Vector<Doublet> getDoublets(final TrackWrapper sensorHost, final TrackWrapper targetTrack)
    {
      final Vector<Doublet> res = new Vector<Doublet>(0, 1);

      // ok, cycle through the sensor points on the host track
      final Enumeration<Editable> iter = sensorHost.elements();
      while (iter.hasMoreElements())
      {
        final PlainWrapper pw = (PlainWrapper) iter.nextElement();
        if (pw.getVisible())
        {
          if (pw instanceof SensorWrapper)
          {
            final SensorWrapper sw = (SensorWrapper) pw;

            // right, work through the contacts in this sensor
            final Enumeration<Editable> theContacts = sw.elements();
            while (theContacts.hasMoreElements())
            {
              final SensorContactWrapper scw = (SensorContactWrapper) theContacts.nextElement();

              if (scw.getVisible())
              {
                final Watchable[] matches = targetTrack.getNearestTo(scw.getDTG());
                FixWrapper targetFix = null;
                final int len = matches.length;
                if (len > 0)
                {
                  for (int i = 0; i < len; i++)
                  {
                    final Watchable thisOne = matches[i];
                    if (thisOne instanceof FixWrapper)
                    {
                      targetFix = (FixWrapper) thisOne;
                      continue;
                    }
                  }
                }
                if (targetFix != null)
                {
                  // ok. found match. store it
                  final Doublet thisDub = new Doublet(scw, targetFix.getLocation());
                  res.add(thisDub);
                }
              } // if this sensor contact is visible
            } // looping through these sensor contacts
          } // is this is a sensor wrapper
        } // if this item is visible
      } // looping through the items on this track

      return res;
    }

    /**
     * clear our data, all is finished
     */
    public void reset()
    {
      setData(null);
      _primaryDoublets.removeAllElements();
      _primaryDoublets = null;
      _secondaryDoublets.removeAllElements();
      _secondaryDoublets = null;
      _primaryTrack = null;
      _secondaryTrack = null;
    }

    public Layers getData()
    {
      return _myData;
    }

    public void setData(final Layers myData)
    {
      _myData = myData;
    }

    //////////////////////////////////////////////////
    // class to store combination of sensor & target at same time stamp
    //////////////////////////////////////////////////
    public static final class Doublet
    {
      private final SensorContactWrapper _sensor;
      private final WorldLocation _targetLocation;

      //////////////////////////////////////////////////
      // working variables to help us along.
      //////////////////////////////////////////////////
      private static final WorldLocation _workingSensorLocation = new WorldLocation(0.0, 0.0, 0.0);
      private static final WorldLocation _workingTargetLocation = new WorldLocation(0.0, 0.0, 0.0);

      //////////////////////////////////////////////////
      // constructor
      //////////////////////////////////////////////////
      Doublet(final SensorContactWrapper sensor,
              final WorldLocation targetLocation)
      {
        _sensor = sensor;
        _targetLocation = targetLocation;
      }

      //////////////////////////////////////////////////
      // member methods
      //////////////////////////////////////////////////
      /**
       * get the DTG of this contact
       *
       * @return the DTG
       */
      public HiResDate getDTG()
      {
        return _sensor.getDTG();
      }

      /**
       * get the colour of this sensor fix
       */
      public Color getColor()
      {
        return _sensor.getColor();
      }


      /**
       * ok find what the current bearing error is for this track
       *
       * @param sensorOffset if the sensor track has been dragged
       * @param targetOffset if the target track has been dragged
       * @return
       */
      public double calculateError(final WorldVector sensorOffset,
                                   final WorldVector targetOffset)
      {
        // copy our locations
        _workingSensorLocation.copy(_sensor.getOrigin(null));
        _workingTargetLocation.copy(_targetLocation);

        // apply the offsets
        if (sensorOffset != null)
          _workingSensorLocation.addToMe(sensorOffset);
        if (targetOffset != null)
          _workingTargetLocation.addToMe(targetOffset);

        // calculate the current bearing
        final WorldVector error = _workingTargetLocation.subtract(_workingSensorLocation);
        double thisError = error.getBearing();
        thisError = Conversions.Rads2Degs(thisError);

        // and calculate the bearing error
        final double measuredBearing = _sensor.getBearing();
        thisError = measuredBearing - thisError;

        while (thisError > 180)
          thisError -= 360.0;

        while (thisError < -180)
          thisError += 360.0;

        return thisError;
      }
    }

  }


  /**
   * embedded class to draw the track whilst it is being dragged
   */

  public final class RubberbandTrack extends Rubberband
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public RubberbandTrack()
    {
    }

    /**
     * override the parent dragging operation - so that we can update our stacked dots
     *
     * @param p1 where the cursor is
     */
    public void mouseDragged(final MouseEvent p1)
    {
      // call the parent - to allow the normal processing
      super.mouseDragged(p1);

      // and now update the stacked dots
      updateStackedDots(calculateCurrentOffset(p1.getPoint()));
    }

    public final void drawLast(final Graphics graphics)
    {
      drawHere(graphics, lastPt);
    }

    public final void drawNext(final Graphics graphics)
    {
      drawHere(graphics, stretchedPt);
    }

    /**
     * dragging happening.  Either draw (or erase) the previous point
     *
     * @param graphics where we're plotting to
     * @param pt       where the cursor is
     */
    private void drawHere(final Graphics graphics, final Point pt)
    {
      // ok, calc the offset
      final WorldVector thisOffset = calculateCurrentOffset(pt);

      boolean firstFix = true;
      final Point lastPoint = new Point();

      // apply this offset to the track
      final Enumeration<Editable> iter = _theTrack.elements();
      while (iter.hasMoreElements())
      {
        final Object thisO = iter.nextElement();
        if (thisO instanceof FixWrapper)
        {
          final FixWrapper fw = (FixWrapper) thisO;

          if (fw.getVisible())
          {

            final WorldLocation thisLoc = fw.getLocation();

            // shift the location
            final WorldLocation newLoc = thisLoc.add(thisOffset);

            // draw the symbol
            final Point thisPoint = _theChart.getCanvas().getProjection().toScreen(newLoc);

            // is there a symbol shown?
            if (fw.getSymbolShowing())
            {
              graphics.drawRect(thisPoint.x - 1, thisPoint.y - 1, 3, 3);
            }

            if (firstFix)
            {
              // don't bother drawing the connecting line
              firstFix = false;
            }
            else
            {
              // draw the line
              graphics.drawLine(lastPoint.x, lastPoint.y, thisPoint.x, thisPoint.y);

            }

            // remember the last location (so that we can connect them)

            lastPoint.setLocation(thisPoint);
          }

        } // whether this was a fix wrapper
        else if (thisO instanceof SensorWrapper)
        {
          final SensorWrapper sw = (SensorWrapper) thisO;
          if (sw.getVisible())
          {
            final Enumeration<Editable> enumS = sw.elements();
            while (enumS.hasMoreElements())
            {
              final SensorContactWrapper scw = (SensorContactWrapper) enumS.nextElement();
              if (scw.getVisible())
              {
                // does this fix have it's own origin?
                final WorldLocation sensorOrigin = scw.getOrigin(_theTrack);

                // did we get a sensor origin?
                // we don't if the sensor data is outside the track period
                if (sensorOrigin != null)
                {

                  // shift the origin
                  _tmpLocation.copy(sensorOrigin);
                  _tmpLocation.addToMe(thisOffset);

                  // take a copy of the screen coordinates of the start
                  _tmpPointA.setLocation(_theChart.getCanvas().getProjection().toScreen(_tmpLocation));

                  // calculate the location of the end of the bearing
                  _tmpLocation.addToMe(new WorldVector(Conversions.Degs2Rads(scw.getBearing()),
                                                       Conversions.Yds2Degs(scw.getRange()), 0.0));

                  // take a copy of the screen coordinates of the end
                  _tmpPointB.setLocation(_theChart.getCanvas().getProjection().toScreen(_tmpLocation));

                  // draw a line between them
                  graphics.drawLine(_tmpPointA.x, _tmpPointA.y, _tmpPointB.x, _tmpPointB.y);

                  // do we put the DTG at the end?
                  if (_showDTG)
                  {
                    final String lbl = DebriefFormatDateTime.toStringHiRes(scw.getDTG());
                    graphics.drawString(lbl, _tmpPointB.x + 3, _tmpPointB.y + 3);
                  }
                }

              } // whether this point was visible
            } // looping through the contacts
          } // if this sensor is visible
        } // whether this is a sensor wrapper
      }
    }

    /**
     * work out how far we have currently dragged the track
     *
     * @param pt where the mouse is
     * @return the distance dragged
     */
    private WorldVector calculateCurrentOffset(final Point pt)
    {
      CanvasType canvas = _theChart.getCanvas();
      PlainProjection projection = canvas.getProjection();
      _tmpLocation.copy(projection.toWorld(pt));
      final WorldVector thisOffset = _tmpLocation.subtract(_dragOrigin);
      return thisOffset;
    }
  }


  //////////////////////////////////////////////////
  // embedded class to store the drag operation
  //////////////////////////////////////////////////

  /**
   * action representing a track being dragged.  It's undo-able and redo-able, since it's quite simple really.
   */
  public static final class DragTrackAction implements MWC.GUI.Tools.Action
  {
    /**
     * the offset we're going to apply
     */
    private final WorldVector _theOffset;

    /**
     * the track we're going to apply it to
     */
    private final TrackWrapper _trackToDrag;

    /**
     * the set of layers we're need to update on completion
     */
    private final Layers _theLayers;

    /**
     * constructor - providing the parameters to store to execute/reproduce the operation
     *
     * @param theOffset
     * @param theTrack
     * @param theLayers
     */
    public DragTrackAction(final WorldVector theOffset,
                           final TrackWrapper theTrack,
                           final Layers theLayers)
    {
      _theOffset = theOffset;
      _trackToDrag = theTrack;
      _theLayers = theLayers;
    }

    /**
     * @return a string representation of the object.
     */
    public String toString()
    {
      final String res = "Drag " + _trackToDrag.getName() + _theOffset.toString();
      return res;
    }

    /**
     * this method calls the 'do' event in the parent tool, passing the necessary data to it
     */
    public void execute()
    {
      // apply the shift
      _trackToDrag.shiftTrack(null, _theOffset);

      // update the layers
      _theLayers.fireModified(_trackToDrag);
    }

    /**
     * this method calls the 'undo' event in the parent tool, passing the necessary data to it
     */
    public void undo()
    {
      // reverse the drag direction
      final WorldVector reverseVector = new WorldVector(0.0, 0.0, 0.0);
      reverseVector.setValues(_theOffset.getBearing() + Math.PI, _theOffset.getRange(), _theOffset.getBearing());

      // and apply it
      _trackToDrag.shiftTrack(null, reverseVector);

      _theLayers.fireModified(_trackToDrag);
    }

    /**
     * @return boolean flag to indicate whether this action may be redone
     */
    public boolean isRedoable()
    {
      return true;
    }

    /**
     * @return boolean flag to describe whether this operation may be undone
     */
    public boolean isUndoable()
    {
      return true;
    }
  }


}
