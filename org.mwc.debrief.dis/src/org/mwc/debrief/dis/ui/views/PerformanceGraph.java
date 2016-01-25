/**
 * 
 */
package org.mwc.debrief.dis.ui.views;

import java.util.Date;

import org.eclipse.swt.widgets.Display;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.listeners.IDISScenarioListener;

import edu.nps.moves.dis.Pdu;

/**
 * @author ian
 * 
 */
public class PerformanceGraph implements IDISGeneralPDUListener,
    IDISScenarioListener
{

  private static final int NULL_TIME = -1;

  private ChartComposite _chart;
  
  private long _lastTime=NULL_TIME;

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
    _lastTime = NULL_TIME;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.debrief.dis.listeners.IDISGeneralPDUListener#logPDU(edu.nps.moves.dis.Pdu)
   */
  @Override
  public void logPDU(Pdu pdu)
  {

    Display.getDefault().asyncExec(new Runnable()
    {

      @Override
      public void run()
      {
        TimeSeriesCollection data =
            (TimeSeriesCollection) _chart.getChart().getXYPlot().getDataset();

        // do we have any data?
        if (data.getSeriesCount() == 0)
        {
          data.addSeries(new TimeSeries("Sim"));
        }
        TimeSeries series = data.getSeries("Sim");
        
        long timeNow = System.currentTimeMillis();
        
        if(_lastTime != NULL_TIME)
        {
          long thisDelta = timeNow - _lastTime;
          double thisFreq = 1000d / thisDelta;
          series.addOrUpdate(new Second(new Date()), thisFreq);
        }
        
        _lastTime = timeNow;
      }
    });

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
