package org.mwc.debrief.lite.map;

import java.awt.event.ActionEvent;

import org.geotools.swing.MapPane;
import org.geotools.swing.action.MapAction;

public class DragElementAction extends MapAction
{

  /**
   * 
   */
  private static final long serialVersionUID = 8514990334295403151L;

  public DragElementAction(final MapPane mapPane)
  {
    super.init(mapPane, null, DragElementTool.TOOL_TIP, DragElementTool.ICON_IMAGE);
  }
  
  @Override
  public void actionPerformed(ActionEvent e)
  {
    getMapPane().setCursorTool(new DragElementTool());
  }

}
