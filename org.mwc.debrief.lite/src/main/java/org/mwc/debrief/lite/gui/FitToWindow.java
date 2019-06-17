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
    fitToWindow(_layers,_map);
  }
  
  public static void fitToWindow(Layers layers,JMapPane map) {
    final WorldArea area = layers.getBounds();
    if (area != null)
    {
      // check it's not the default area that gets returned when
      // no data is loaded
      if(area.equals(Layers.getDebriefOrigin()))
      {
        // ok, don't bother resizing. Leave it as-is
      }
      else
      {
        // ok, let's introduce a 5% border
        area.grow(area.getWidth() * 0.05, 0);

        final WorldLocation tl = area.getTopLeft();
        final WorldLocation br = area.getBottomRight();
        final CoordinateReferenceSystem crs = map.getMapContent()
            .getCoordinateReferenceSystem();
        final ReferencedEnvelope bounds = new ReferencedEnvelope(tl.getLong(),
            br.getLong(), tl.getLat(), br.getLat(), crs);
        map.getMapContent().getViewport().setBounds(bounds);

        // force repaint
        final ReferencedEnvelope paneArea = map.getDisplayArea();
        map.setDisplayArea(paneArea);
      }
    }
  }

}
