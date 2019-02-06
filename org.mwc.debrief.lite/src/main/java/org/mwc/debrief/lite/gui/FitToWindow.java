package org.mwc.debrief.lite.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.JMapPane;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import MWC.GUI.Layers;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class FitToWindow extends AbstractAction
{
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private final Layers _layers;
  private final JMapPane _map;

  public FitToWindow(final Layers layers, final JMapPane map)
  {
    _layers = layers;
    _map = map;
  }

  @Override
  public void actionPerformed(final ActionEvent e)
  {
    final WorldArea area = _layers.getBounds();
    if (area != null)
    {
      final WorldLocation tl = area.getTopLeft();
      final WorldLocation br = area.getBottomRight();
      final CoordinateReferenceSystem crs = _map.getMapContent()
          .getCoordinateReferenceSystem();
      final ReferencedEnvelope bounds = new ReferencedEnvelope(tl.getLong(), br
          .getLong(), tl.getLat(), br.getLat(), crs);
      _map.getMapContent().getViewport().setBounds(bounds);

      // force repaint
      final ReferencedEnvelope paneArea = _map.getDisplayArea();
      _map.setDisplayArea(paneArea);
    }
  }

}
