package MWC.GUI;

import java.awt.Color;

import MWC.GenericData.WorldLocation;

public interface CanPlotFaded
{

  /** paint this item
   * 
   * @param dest where to paint to
   * @param centre the origin to use
   * @param theColor the faded color to use
   */
  void paintMe(final CanvasType dest, final WorldLocation centre, final Color theColor);

}
