package org.mwc.debrief.lite.gui;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.swing.JMapPane;
import org.pushingpixels.flamingo.api.common.CommandAction;
import org.pushingpixels.flamingo.api.common.CommandActionEvent;

import com.vividsolutions.jts.geom.Coordinate;

public class ZoomOut extends AbstractAction implements CommandAction
{
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private final JMapPane _map;

  public ZoomOut(final JMapPane map)
  {
    _map = map;
  }

  @Override
  public void actionPerformed(final ActionEvent e)
  {
    final Rectangle paneArea = ((JComponent) _map).getVisibleRect();

    // get the centre of the viewport
    final Coordinate centre = _map.getMapContent().getViewport().getBounds()
        .centre();
    final Point2D mapPos = new Point2D.Double(centre.x, centre.y);

    // decide on a new scale
    final double scale = _map.getWorldToScreenTransform().getScaleX();
    final double newScale = scale / 1.5;
    
    // don't bother zooming out too far
    if(newScale > 2.5E-5)
    {
      final DirectPosition2D corner = new DirectPosition2D(centre.x - 0.5d
          * paneArea.getWidth() / newScale, centre.y + 0.5d * paneArea.getHeight()
              / newScale);
  
      final Envelope2D newMapArea = new Envelope2D();
      newMapArea.setFrameFromCenter(mapPos, corner);
      _map.setDisplayArea(newMapArea);
    }
  }

  @Override
  public void commandActivated(CommandActionEvent e)
  {
    actionPerformed(e);
    
  }

}
