package org.mwc.debrief.lite.map;

import java.awt.event.ActionEvent;

import org.geotools.swing.MapPane;
import org.geotools.swing.action.ZoomInAction;

/** use our advanced zoom in tool, that also support zoom out
 * 
 * @author ian
 *
 */
public class AdvancedZoomInAction extends ZoomInAction
{

  public AdvancedZoomInAction(MapPane mapPane)
  {
    super(mapPane);
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  public void actionPerformed(ActionEvent ev)
  {
    getMapPane().setCursorTool(new AdvancedZoomInTool());
  }

  
  
}
