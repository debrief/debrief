package org.mwc.debrief.lite.map;

import java.awt.event.ActionEvent;

import org.geotools.swing.MapPane;
import org.geotools.swing.action.ZoomInAction;
import org.pushingpixels.flamingo.api.common.CommandAction;
import org.pushingpixels.flamingo.api.common.CommandActionEvent;

/**
 * use our advanced zoom in tool, that also support zoom out
 *
 * @author ian
 *
 */
public class AdvancedZoomInAction extends ZoomInAction implements CommandAction
{

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public AdvancedZoomInAction(final MapPane mapPane)
  {
    super(mapPane);
  }

  @Override
  public void actionPerformed(final ActionEvent ev)
  {
    getMapPane().setCursorTool(new AdvancedZoomInTool());
  }

  @Override
  public void commandActivated(CommandActionEvent e)
  {
    actionPerformed(e);
    
  }
}
