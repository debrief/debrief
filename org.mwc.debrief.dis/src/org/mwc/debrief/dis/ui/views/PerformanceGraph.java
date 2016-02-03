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

  final private PerformanceQueue disQ = new PerformanceQueue(5000);
  final private PerformanceQueue screenQ = new PerformanceQueue(5000);

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
    doUpdate(SIM_NAME, disQ);
  }
  
  public void screenUpdate()
  {
    doUpdate(SCREEN_NAME, screenQ);
  }

  private void doUpdate(final String name, final PerformanceQueue queue)
  {
    Runnable doUpdate = new Runnable()
    {
      @Override
      public void run()
      {
        Display.getDefault().asyncExec(new Runnable()
        {
          @Override
          public void run()
          {
            // store the new time in the queue
            queue.add(new Date().getTime());

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

            series.addOrUpdate(new Second(new Date()), freq);

            // clear them out?
            DateAxis tAxis =
                (DateAxis) _chart.getChart().getXYPlot().getDomainAxis();
            tAxis.setRange(new Date(new Date().getTime() - 20000), new Date());
          }

        });
        try
        {
          Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
          e.printStackTrace();
        }
      }
    };

    Thread updateThread = new Thread(doUpdate);
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
    // TODO Auto-generated method stub

  }

}
