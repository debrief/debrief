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
  private ChartComposite _chart;

  PerformanceQueue perfQ = new PerformanceQueue(5000);

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
    perfQ.clear();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.debrief.dis.listeners.IDISGeneralPDUListener#logPDU(edu.nps.moves.dis.Pdu)
   */
  @Override
  public void logPDU(Pdu pdu)
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
            perfQ.add(new Date().getTime());

            double freq = perfQ.freqAt(new Date().getTime()) * 1000d;
            TimeSeriesCollection data =
                (TimeSeriesCollection) _chart.getChart().getXYPlot()
                    .getDataset();

            // do we have any data?
            if (data.getSeriesCount() == 0)
            {
              data.addSeries(new TimeSeries("Sim"));
            }
            TimeSeries series = data.getSeries("Sim");

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
