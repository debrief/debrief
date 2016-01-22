/**
 * 
 */
package org.mwc.debrief.dis.ui.views;

import org.jfree.experimental.chart.swt.ChartComposite;
import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.listeners.IDISScenarioListener;

import edu.nps.moves.dis.Pdu;

/**
 * @author ian
 *
 */
public class PerformanceGraph implements IDISGeneralPDUListener, IDISScenarioListener
{

  private ChartComposite _chart;

  /**
   * @param chartComposite
   */
  public PerformanceGraph(ChartComposite chartComposite)
  {
    _chart = chartComposite;
  }

  /* (non-Javadoc)
   * @see org.mwc.debrief.dis.listeners.IDISScenarioListener#restart()
   */
  @Override
  public void restart()
  {
    // TODO Auto-generated method stub
    
  }

  /* (non-Javadoc)
   * @see org.mwc.debrief.dis.listeners.IDISGeneralPDUListener#logPDU(edu.nps.moves.dis.Pdu)
   */
  @Override
  public void logPDU(Pdu pdu)
  {
    System.out.println("STEP");
  }

  /* (non-Javadoc)
   * @see org.mwc.debrief.dis.listeners.IDISGeneralPDUListener#complete(java.lang.String)
   */
  @Override
  public void complete(String reason)
  {
    // TODO Auto-generated method stub
    
  }

}
