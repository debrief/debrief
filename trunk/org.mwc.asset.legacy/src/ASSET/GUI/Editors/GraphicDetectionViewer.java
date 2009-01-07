/*
 * Created by Ian Mayo, PlanetMayo Ltd.
 * User: Ian.Mayo
 * Date: 26-Sep-2002
 * Time: 10:37:26
 */
package ASSET.GUI.Editors;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyEditorSupport;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ASSET.Models.Detection.DetectionEvent;
import ASSET.Participants.Category;
import MWC.GUI.Properties.Swing.SwingPropertyEditor2;
import MWC.GUI.ptplot.PlotBox;
import MWC.GenericData.Duration;

public class GraphicDetectionViewer extends DetectionViewer implements java.beans.PropertyChangeListener
{
  ////////////////////////////////////////////////////
  // member objects
  ////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the plot we maintain
   */
  MWC.GUI.ptplot.MWCPlot _thePlot = null;

  /**
   * the length of back-track to plot
   */
  private MWC.GUI.Properties.Swing.SwingDurationPropertyEditor _backTrack = null;

  /**
   * the default duration to show
   */
  final private Duration _defaultDuration = new Duration(10, Duration.MINUTES);

  /**
   * the most recent time we have data for
   */
  private long _mostRecentTime = -1;

  /**
   * the currently selected date format
   */
  SwingPropertyEditor2.TickableComboBox _dateFormatSelecter = null;

  ////////////////////////////////////////////////////
  // member constructor
  ////////////////////////////////////////////////////

  public GraphicDetectionViewer()
  {
    // initialise the parent
    super();

    // get the parent to build itself
    super.buildGUI();

    // and build ourself
    buildGUI();

  }

  /**
   * build the interface
   */
  void buildGUI()
  {
    this.setName("Detection Viewer");

    // the panel to store everything
    this.setLayout(new BorderLayout());

    // ok, create the plot.
    _thePlot = new MWC.GUI.ptplot.MWCPlot(null);

    this.add("Center", _thePlot);

    // format the plot
    _thePlot.setXRange(-180, 180);

    // the formatter
    //  final HorizontalDateAxis da = new HorizontalDateAxis("Time");
    //   _thePlot.setYDateAxis(da);
    _thePlot.setXDateAxis(null);
    _thePlot.setColor(true);

    _thePlot.setYFormatter(new PlotBox.LabelFormatter()
    {
      final java.text.SimpleDateFormat _df = new java.text.SimpleDateFormat("hh:mm:ss");

      public String formatThis(final double val)
      {
        _df.applyPattern((String) _dateFormatSelecter.getSelectedItem());
        final String res = _df.format(new java.util.Date((long) val));
        return res;
      }

      public double formatVal(final double val)
      {
        return val;
      }
    });

    _thePlot.setMarksStyle("points");

    // now create the duration editor
    _backTrack = new MWC.GUI.Properties.Swing.SwingDurationPropertyEditor();
    _backTrack.addPropertyChangeListener(this);
    _backTrack.setColumns(3);


    // initialise the duration
    _backTrack.setValue(_defaultDuration);

    // and create a tickable combo box for the date format
    _dateFormatSelecter = SwingPropertyEditor2.createChoiceEditor(new DateFormatPropertyEditor(), DateFormatPropertyEditor.getTagList());
    _dateFormatSelecter.addItemListener(new java.awt.event.ItemListener()
    {
      /**
       * Invoked when an item has been selected or deselected by the user.
       * The code written for this method performs the operations
       * that need to occur when an item is selected (or deselected).
       */
      public void itemStateChanged(ItemEvent e)
      {
        repaintPlot();
      }
    });

    // also create a button to switch the legend on and off
    final javax.swing.JCheckBox _showLegend = new JCheckBox();
    _showLegend.setText("Legend");
    _showLegend.setToolTipText("Show a legend of track details");
    _showLegend.addItemListener(new java.awt.event.ItemListener()
    {
      /**
       * Invoked when an item has been selected or deselected by the user.
       * The code written for this method performs the operations
       * that need to occur when an item is selected (or deselected).
       */
      public void itemStateChanged(final ItemEvent e)
      {
        _thePlot.setLegendVisible(e.getStateChange() == ItemEvent.SELECTED);
        _thePlot.repaint();
      }
    });

    final JPanel settingHolder = new JPanel();
    settingHolder.setLayout(new BorderLayout());
    settingHolder.add("West", _backTrack.getCustomEditor());
    settingHolder.add("East", _dateFormatSelecter);
    settingHolder.add("Center", _showLegend);

    this.add("South", settingHolder);
  }

  public void setObject(Object data)
  {
    super.setObject(data);

    // let's just see if there are any pending detections
    if (_pendingDetections != null)
    {
      // ok, set our time to the period supplied
      _mostRecentTime = _pendingDetections.getTimeCoverage().getEndDTG().getMicros() / 1000;

      // and set the back-track so that we can see all of the points
      Duration newDuration = new Duration(_pendingDetections.getTimeCoverage().getEndDTG().getMicros() -
                                          _pendingDetections.getTimeCoverage().getStartDTG().getMicros(), Duration.MICROSECONDS);

      // just check there's a real period covered
      if (newDuration.getMillis() == 0)
        newDuration = new Duration(5, Duration.SECONDS);

      _backTrack.setValue(newDuration);

      // and update the plot
      super.updateList(null);
    }
  }


  /**
   * the detections are updated, update our list
   *
   * @param vec the new detections
   */
  protected void updateGUI(final Vector<DetectionEvent> vec)
  {
    for (int i = 0; i < vec.size(); i++)
    {
      final DetectionEvent event = (DetectionEvent) vec.elementAt(i);
      // do we have bearing
      if (event.getBearing() != null)
      {
        final float brg = event.getBearing().floatValue();

        final long time = event.getTime();

        // put each sensor into a different data set
        final int dataSet = getDataSetIndexFor(event.getSensor(), event.getTarget());

        _thePlot.addPoint(dataSet, brg, time, true);

        // do we know the category?
        final Category cat = event.getTargetType();
        if (cat != null)
        {
          // build up the name
          String thisTargetName = getTargetName(event);
          _thePlot.setSeriesDetails(dataSet, thisTargetName, null);
        }
      }
      _mostRecentTime = Math.max(_mostRecentTime, event.getTime());
    }

    repaintPlot();
  }

  /**
   * produce a string describing the target
   *
   * @param event the detection event we're trying to describe
   * @return a string to be used in the Legend
   */
  protected String getTargetName(final DetectionEvent event)
  {
    return event.getTargetType().toShortString();
  }

  /**
   * trigger a repaint of the data
   */
  void repaintPlot()
  {
    // how long is the plot?
    final Duration theDuration = (Duration) _backTrack.getValue();

    // change the y axis length
    _thePlot.setYRange(_mostRecentTime - theDuration.getValueIn(Duration.MILLISECONDS), _mostRecentTime);

    // and request redraw (So that the axes update)
    _thePlot.repaint();
  }


  /**
   * This method gets called when a bound property is changed.
   *
   * @param evt A PropertyChangeEvent object describing the event source
   *            and the property that has changed.
   */

  public void propertyChange(PropertyChangeEvent evt)
  {
    repaintPlot();
  }

  /**
   * the scenario has moved forward, clear the GUI
   */
  protected void clearGUI()
  {
    super.clearGUI();
  }

  /**
   * we are closing, ditch the components
   */
  protected void closeGUI()
  {
    super.closeGUI();
  }

  public static void main(String[] args)
  {
    final JFrame fr = new JFrame("gere");
    fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    fr.setSize(300, 300);
    fr.setVisible(true);
    final Container holder = fr.getContentPane();
    holder.setLayout(new BorderLayout());
    final MWC.GUI.Properties.Swing.SwingPropertiesPanel sp = new MWC.GUI.Properties.Swing.SwingPropertiesPanel(null, null, null, null);

    final ASSET.Participants.CoreParticipant cp = new ASSET.Models.Vessels.Surface(12)
    {

      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;
//			long lastTime = -1;

   
    };
    sp.addEditor(new VesselPane.DetectionViewerHolder(cp), null);

    holder.add("Center", sp);
    final JButton test = new JButton("Test");
    holder.add("South", test);
    test.addActionListener(new java.awt.event.ActionListener()
    {
      /**
       * Invoked when an action occurs.
       */
      public void actionPerformed(ActionEvent e)
      {
        cp.doDecision(0, 1200, null);
        cp.doMovement(0, 1200, null);
        cp.doDetection(0, 1200, null);
      }
    });
    fr.doLayout();


  }

  ////////////////////////////////////////////////////
  // embedded class which allows choice of plot formats
  ////////////////////////////////////////////////////
  public static class DateFormatPropertyEditor extends PropertyEditorSupport
  {
    String[] _myTags;

    String _myFormat;

    public static String[] getTagList()
    {
      String[] theTags = new String[]{"hh:mm:ss", "mm:ss", "dd hhmm"};
      return theTags;
    }

    public String[] getTags()
    {
      if (_myTags == null)
      {
        _myTags = getTagList();
        _myFormat = _myTags[0];
      }
      return _myTags;
    }

    public Object getValue()
    {
      return _myFormat;
    }

    public void setValue(final Object p1)
    {
      if (p1 instanceof String)
      {
        final String val = (String) p1;
        setAsText(val);
      }
    }

    public void setAsText(final String val)
    {
      for (int i = 0; i < getTags().length; i++)
      {
        final String thisStr = getTags()[i];
        if (thisStr.equals(val))
          _myFormat = val;
      }
    }

    public String getAsText()
    {
      return _myFormat;
    }
  }
}
