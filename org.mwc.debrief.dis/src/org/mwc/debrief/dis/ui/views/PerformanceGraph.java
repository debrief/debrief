/**
 * 
 */
package org.mwc.debrief.dis.ui.views;

import java.util.Date;

import org.eclipse.swt.widgets.Display;
import org.jfree.chart.axis.DateAxis;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.listeners.IDISScenarioListener;
import org.mwc.debrief.dis.listeners.impl.PerformanceQueue;

import edu.nps.moves.dis.Pdu;

/**
 * @author ian
 * 
 */
public class PerformanceGraph implements IDISGeneralPDUListener,
    IDISScenarioListener
{

  private static final String SCREEN_NAME = "Screen Updates";

  private static final String SIM_NAME = "Model Updates";

  private final ChartComposite _chart;

  /**
   * the length of time we display on the graph
   * 
   */
  private static final int GRAPH_PERIOD = 30000;

  /**
   * the length of time we average for
   * 
   */
  final private long avgPeriod = 3000;

  final private PerformanceQueue disQ =
      new PerformanceQueue(avgPeriod, "Model");
  final private PerformanceQueue screenQ = new PerformanceQueue(avgPeriod,
      "Screen");

  private Thread updateThread;

  /**
   * @param chartComposite
   */
  public PerformanceGraph(ChartComposite chartComposite)
  {
    _chart = chartComposite;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.debrief.dis.listeners.IDISScenarioListener#restart()
   */
  @Override
  public void restart()
  {
    screenQ.clear();
    disQ.clear();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.debrief.dis.listeners.IDISGeneralPDUListener#logPDU(edu.nps.moves.dis.Pdu)
   */
  @Override
  public void logPDU(final Pdu pdu)
  {
    // store the new time in the queue
    disQ.add(new Date().getTime());

    // run the graph, if we have to
    startUpdates();
  }

  public void screenUpdate()
  {
    screenQ.add(new Date().getTime());

    // run the graph, if we have to

    // no, don't. let's trigger updates based on model activity,
    // else the graph will start redrawing as we resize the plot
    // startUpdates();
  }

  protected boolean _terminate = false;

  private void startUpdates()
  {
    if (updateThread != null)
    {
      return;
    }

    _terminate = false;

    final Runnable doUpdate = new Runnable()
    {
      @Override
      public void run()
      {
        final Runnable theR = new Runnable()
        {
          @Override
          public void run()
          {
            // clear out the series
            TimeSeriesCollection data =
                (TimeSeriesCollection) _chart.getChart().getXYPlot()
                    .getDataset();
            data.removeAllSeries();
          }
        };
        if (Display.getCurrent() != null)
        {
          theR.run();
        }
        else
        {
          Display.getDefault().syncExec(theR);
        }

        while (!_terminate)
        {
          final Runnable theR2 = new Runnable()
          {
            private void doThisQueue(PerformanceQueue queue, String name)
            {
              // store the new time in the queue
              // queue.add(new Date().getTime());

              double freq = queue.freqAt(new Date().getTime()) * 1000d;

              TimeSeriesCollection data =
                  (TimeSeriesCollection) _chart.getChart().getXYPlot()
                      .getDataset();

              // do we know this series?
              TimeSeries series = data.getSeries(name);
              if (series == null)
              {
                series = new TimeSeries(name);
                data.addSeries(series);
              }

              if (!_chart.isDisposed())
              {
                series.addOrUpdate(new Second(new Date()), freq);
              }

            }

            @Override
            public void run()
            {
              if (_chart.isDisposed())
              {
                _terminate = true;
                return;
              }
              doThisQueue(disQ, SIM_NAME);
              doThisQueue(screenQ, SCREEN_NAME);

              // clear them out?
              DateAxis tAxis =
                  (DateAxis) _chart.getChart().getXYPlot().getDomainAxis();
              tAxis.setRange(new Date(new Date().getTime() - GRAPH_PERIOD),
                  new Date());

              _chart.getChart().getXYPlot().getRangeAxis().setAutoRange(true);

              // set the y axis minimum
              _chart.getChart().getXYPlot().getRangeAxis().setLowerBound(0);

            }

          };
          if (Display.getCurrent() != null)
          {
            theR2.run();
          }
          else
          {
            Display.getDefault().syncExec(theR2);
          }
          try
          {
            Thread.sleep(1000);
          }
          catch (InterruptedException e)
          {
            e.printStackTrace();
          }
        }
      }
    };

    updateThread = new Thread(doUpdate);
    updateThread.setDaemon(true);
    updateThread.start();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.debrief.dis.listeners.IDISGeneralPDUListener#complete(java.lang.String)
   */
  @Override
  public void complete(String reason)
  {
    _terminate = true;
    updateThread = null;
  }

}
