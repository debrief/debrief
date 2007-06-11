package MWC.GUI.ptplot;

import MWC.GUI.CanvasType;
import MWC.GUI.StepperListener;
import MWC.GUI.ptplot.jfreeChart.MWCHorizontalDateAxis;
import MWC.GenericData.HiResDate;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class MWCPlot extends Plot implements StepperListener
{
  ///////////////////////////////////////
  // member variables
  ///////////////////////////////////////

  /**
   * list of data series wer plot
   */
  private Hashtable _mySeries;

  /**
   * the step control we listen to
   */
  private StepperListener.StepperController _theStepper;

  /**
   * the most recent time we got from the stepper
   * (or -1 if we don't know the time)
   */
  private long _currentTime = -1;

  ///////////////////////////////////////
  // constructor
  ///////////////////////////////////////
  public MWCPlot(StepperListener.StepperController theStepper)
  {
    // create the list of data series
    _mySeries = new Hashtable();

    // remember the stepoper
    _theStepper = theStepper;

    // listen to the stepper (if we know it)
    if (_theStepper != null)
    {
      _theStepper.addStepperListener(this);
    }

    // listen out for double-click
    this.addMouseListener(new MouseAdapter()
    {
      public void mouseClicked(MouseEvent e)
      {
        if (e.getClickCount() == 2)
        {
          // call the parent method which does the fill operation
          fillPlot();
        }
      }
    });

  }

  ///////////////////////////////////////
  // member methods
  ///////////////////////////////////////

  public synchronized void addPoint(int dataset, double x, double y, boolean connected)
  {

    // can we reformat the data points?
    if (_xFormatter != null)
    {
      x = _xFormatter.formatVal(x);
    }
    if (_yFormatter != null)
    {
      y = _yFormatter.formatVal(y);
    }

    // also sort out the time period
    if (this._xDateAxis != null)
    {
      this._xDateAxis.addValue(new Date((long) y));
    }

    // add to the parents, so that they can handle the scaling
    super.addPoint(dataset, x, y, connected);

    // also add it to ourselves.
    // do we have a series with this index?
    DataSeries thisS = getSeries(dataset);

    // put this new point in our series
    thisS.addPoint(new DataPoint(x, y, connected));

  }

  public void setSeriesDetails(int dataset, String name, Color color)
  {
    // also add it to ourselves.
    // do we have a series with this index?
    DataSeries thisS = getSeries(dataset);

    // has a colour been supplied?
    if (color != null)
      thisS.setColor(color);

    // has a name been supplied?
    if ((name != null) && (thisS.getName() == null))
    {
      thisS.setName(name);
      // and in the other legend details
      super.addLegend(dataset, name);
    }
  }

  private DataSeries getSeries(int dataset)
  {
    // also add it to ourselves.
    // do we have a series with this index?
    DataSeries thisS = (DataSeries) _mySeries.get(new Integer(dataset));

    // or shall we create it?
    if (thisS == null)
    {
      // create it
      thisS = new DataSeries(dataset);

      // and store it
      _mySeries.put(new Integer(dataset), thisS);

      // give it a random color
      int color = dataset % _colors.length;
      thisS.setColor(_colors[color]);
    }

    return thisS;
  }

  protected java.awt.Color getColorFor(int dataSet)
  {
    java.awt.Color res = getSeries(dataSet).getColor();

    return res;
  }

  /**
   * allow the legend to be made visible
   */
  public void setLegendVisible(boolean val)
  {
    this._showLegend = val;
  }

  // the panel is being closed, do any handling needed
  public void doClose()
  {
    if (_theStepper != null)
      _theStepper.removeStepperListener(this);
  }

  ///////////////////////////////////////
  // support for stepper!
  ///////////////////////////////////////

  /**
   * the stepping mode has been changed
   */
  public void steppingModeChanged(boolean on)
  {
  }

  /**
   * the stepper has stepped to a new time
   */
  public void newTime(HiResDate oldDTG, HiResDate newDTG, CanvasType canvas)
  {
    _currentTime = newDTG.getMicros();
    this.repaint();
  }

  ///////////////////////////////////////
  // nested classes
  ///////////////////////////////////////
  public class DataSeries
  {
    private Vector _myPoints;
    private Color _myCol;
    private int _index;
    private String _name;
    private DataPoint _lastPointPlotted = null;

    public DataSeries(int index)
    {
      _myPoints = new Vector(0, 1);
      _index = index;
    }

    public void setName(String val)
    {
      _name = val;
    }

    public void setColor(Color val)
    {
      _myCol = val;
    }

    public Color getColor()
    {
      return _myCol;
    }

    public void addPoint(DataPoint val)
    {
      _myPoints.addElement(val);
    }

    public DataPoint getPoint(int index)
    {
      // take a copy of the point, so that we can look back on it
      DataPoint res = (DataPoint) _myPoints.elementAt(index);
      _lastPointPlotted = res;
      return res;
    }

    public DataPoint getLastPoint()
    {
      return _lastPointPlotted;
    }

    public Enumeration elements()
    {
      return _myPoints.elements();
    }

    public int getIndex()
    {
      return _index;
    }

    public String getName()
    {
      return _name;
    }
  }

  public class DataPoint
  {
    private double _x;
    private double _y;
    private boolean _connected;

    public DataPoint(double x, double y, boolean connected)
    {
      _x = x;
      _y = y;
      _connected = connected;
    }

    public double x()
    {
      return _x;
    }

    public double y()
    {
      return _y;
    }

    public boolean connected()
    {
      return _connected;
    }
  }


  protected synchronized void _drawPlot(Graphics graphics, boolean clearfirst)
  {
    // We must call PlotBox._drawPlot() before calling _drawPlotPoint
    // so that _xscale and _yscale are set.
    super._drawPlot(graphics, clearfirst);

    // Plot the points in reverse order so that the first colors
    // appear on top.
    // work through our series
    Enumeration enumer = _mySeries.elements();
    while (enumer.hasMoreElements())
    {
      DataSeries ds = (DataSeries) enumer.nextElement();
      Enumeration pts = ds.elements();
      boolean firstPoint = true;
      while (pts.hasMoreElements())
      {
        DataPoint dp = (DataPoint) pts.nextElement();
        //
        //        if(this._xDateAxis != null)
        //        {
        //          _xDateAxis.addValue(new java.util.Date((long)dp._x));
        //        }

        // ok, now draw the point
        _drawPlotPoint(graphics, dp, ds.getIndex(), firstPoint);

        // ok, we've finished with the first point (which doesn't need a joining line)
        //  now continue as normal
        if (firstPoint)
          firstPoint = false;
      }
    }

    // now draw in our axis line
    _drawTimeSlider(graphics);

    _painted = true;
    notifyAll();
  }

  private void _drawTimeSlider(Graphics dest)
  {
    if (_currentTime != -1)
    {
      // convert the time to x units
      double value = _currentTime;
      double axisMin = _xMin;
      double axisMax = _xMax;
      double plotX = _ulx;
      double plotMaxX = _lrx;
      double newI = plotX + ((value - axisMin) / (axisMax - axisMin)) * (plotMaxX - plotX);

      // limit the line position to the limits
      newI = Math.max(_ulx, newI);
      newI = Math.min(_lrx, newI);

      dest.setColor(Color.darkGray);
      // see if we have a graphics 2d
      if (dest instanceof Graphics2D)
      {
        Graphics2D g2 = (Graphics2D) dest;
        Stroke oldStroke = g2.getStroke();
        dest.setXORMode(Color.green);
        g2.setStroke(new java.awt.BasicStroke(3));
        dest.drawLine((int) newI - 1, _lry - 1, (int) newI - 1, _uly + 1);
        dest.setPaintMode();
        g2.setStroke(oldStroke);
      }
      else
      {
        dest.drawLine((int) newI, _lry, (int) newI, _uly);
      }

    }
  }

  private synchronized void _drawPlotPoint(Graphics graphics,
                                           DataPoint dp,
                                           int dataset,
                                           boolean firstPoint)
  {
    // Set the color

    // set the current colour
    graphics.setColor(getColorFor(dataset));

    // retrieve the current datapoint

    // Use long here because these numbers can be quite large
    // (when we are zoomed out a lot).
    long ypos = _lry - (long) ((dp.y() - _yMin) * _yscale);
    long xpos = _ulx + (long) ((dp.x() - _xMin) * _xscale);

    // Draw the line to the previous point.
    long prevx = ((Long) _prevx.elementAt(dataset)).longValue();
    long prevy = ((Long) _prevy.elementAt(dataset)).longValue();
    // MIN_VALUE is a flag that there has been no previous x or y.

    if (dp.connected() && !firstPoint)
    {
      // now check if this point is a direction (0..360)
      _drawLine(graphics, dataset, xpos, ypos, prevx, prevy, true);
    }

    // Save the current point as the "previous" point for future
    // line drawing.
    _prevx.setElementAt(new Long(xpos), dataset);
    _prevy.setElementAt(new Long(ypos), dataset);

    // Draw decorations that may be specified on a per-dataset basis
    Format fmt = (Format) _formats.elementAt(dataset);
    if (fmt.impulsesUseDefault)
    {
      if (_impulses) _drawImpulse(graphics, xpos, ypos, true);
    }
    else
    {
      if (fmt.impulses) _drawImpulse(graphics, xpos, ypos, true);
    }

    // Check to see whether the dataset has a marks directive
    int marks = _marks;
    if (!fmt.marksUseDefault) marks = fmt.marks;
    if (marks != 0) _drawPoint(graphics, dataset, xpos, ypos, true);

    if (_bars) _drawBar(graphics, dataset, xpos, ypos, true);

    // Restore the color, in case the box gets redrawn.
    graphics.setColor(_foreground);
  }

  public static void main(String[] args)
  {
    boolean firstPoint = true;

    javax.swing.JFrame jr = new javax.swing.JFrame();
    jr.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    jr.setSize(600, 400);
    jr.setTitle("test plot");

    // create the Graph
    MWCPlot myPlot = new MWCPlot(null);
    jr.getContentPane().add("Center", myPlot);
    jr.getContentPane().add("South", new javax.swing.JButton("fishy"));
    myPlot.setName("Some Title");
    //   MWC.GUI.ptplot.Swing.SwingPlot swingPlot = new MWC.GUI.ptplot.Swing.SwingPlot(myPlot, _thePanel);

    // format the axes
    myPlot.setXLabel("time (s)");
    myPlot.setYLabel("range (m)");


    MWCHorizontalDateAxis da = new MWCHorizontalDateAxis("Time");

    // set the axis formatters
    myPlot.setXFormatter(new PlotBox.TimeLabelFormatter());
    myPlot.setXDateAxis(da);

    // sort out the details for this series
    myPlot.setSeriesDetails(1,
                            "Alpha",
                            Color.green);

    Calendar cal = Calendar.getInstance();
    cal.set(2002, 0, 22, 12, 00);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    da.addValue(cal.getTime());
    myPlot.addPoint(1, cal.getTime().getTime(), 5, !firstPoint);
    System.out.println("adding:" + cal.getTime() + " " + 5);

    cal.add(Calendar.DAY_OF_MONTH, 2);

    da.addValue(cal.getTime());
    myPlot.addPoint(1, cal.getTime().getTime(), 10, firstPoint);
    System.out.println("adding:" + cal.getTime() + " " + 10);

    cal.add(Calendar.HOUR, 5);

    da.addValue(cal.getTime());
    myPlot.addPoint(1, cal.getTime().getTime(), 15, firstPoint);
    System.out.println("adding:" + cal.getTime() + " " + 15);

    cal.add(Calendar.HOUR, 1);

    da.addValue(cal.getTime());
    myPlot.addPoint(1, cal.getTime().getTime(), 20, firstPoint);
    System.out.println("adding:" + cal.getTime() + " " + 20);

    cal.add(Calendar.MINUTE, 12);

    da.addValue(cal.getTime());
    myPlot.addPoint(1, cal.getTime().getTime(), 25, firstPoint);
    System.out.println("adding:" + cal.getTime() + " " + 25);

    cal.add(Calendar.SECOND, 1233);

    da.addValue(cal.getTime());
    myPlot.addPoint(1, cal.getTime().getTime(), 30, firstPoint);
    System.out.println("adding:" + cal.getTime() + " " + 30);

    cal.add(Calendar.SECOND, 12);

    da.addValue(cal.getTime());
    myPlot.addPoint(1, cal.getTime().getTime(), 32, firstPoint);
    System.out.println("adding:" + cal.getTime() + " " + 32);

    cal.add(Calendar.SECOND, 2);

    da.addValue(cal.getTime());
    myPlot.addPoint(1, cal.getTime().getTime(), 31, firstPoint);
    System.out.println("adding:" + cal.getTime() + " " + 31);

    cal.add(Calendar.MILLISECOND, 123);

    da.addValue(cal.getTime());
    myPlot.addPoint(1, cal.getTime().getTime(), 29, firstPoint);
    System.out.println("adding:" + cal.getTime() + " " + 29);



    /*    for(int i=0; i<45; i++)
        {
          dateVal += 3400000;

          da.addValue(new java.util.Date(dateVal));

          myPlot.addPoint(1, dateVal, 45 + i * Math.random(), !firstPoint);

          if(firstPoint)
            firstPoint = false;
        }*/


    jr.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    jr.setSize(300, 300);
    jr.doLayout();
    jr.setVisible(true);


    // @@ insert test code here
    /*    javax.swing.JFrame jr = new javax.swing.JFrame();
        jr.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        jr.setSize(400, 400);
        jr.setTitle("test plot");
        MWCPlot mp = new MWCPlot();
        jr.getContentPane().add("Center", mp);
        mp.setSeriesDetails(1, "first", java.awt.Color.red);
        Date dNow = new Date();
        mp.addPoint(1, dNow.getTime() + 2000, 120, false);
        mp.addPoint(1, dNow.getTime() + 6000, 80, true);
        mp.addPoint(1, dNow.getTime() + 12000, 3, true);
        mp.addPoint(1, dNow.getTime() + 43000, 330, true);
        mp.addPoint(1, dNow.getTime() + 99000, 53, true);
        mp.setYRange(0, 360);
        mp.setXRange(dNow.getTime(), dNow.getTime() + 100000);
        jr.setVisible(true);
        jr.doLayout();*/
  }


}
