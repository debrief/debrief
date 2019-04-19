/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package Debrief.GUI;

import Debrief.Wrappers.LabelWrapper;
import MWC.GUI.Editable;
import MWC.GUI.HasEditables;
import MWC.GUI.Chart.Painters.CoastPainter;
import MWC.GUI.Chart.Painters.Grid4WPainter;
import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GUI.Chart.Painters.ScalePainter;
import MWC.GUI.VPF.VPFDatabase;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class CoreImageHelper
{
  public static String getImageKeyFor(Editable editable) {
    String imageKey = "client_network.png";

    if (editable instanceof GridPainter)
      imageKey = "grid.png";
    else if (editable instanceof Grid4WPainter)
      imageKey = "grid4w.png";
    else if (editable instanceof ScalePainter)
      imageKey = "scale.png";
    else if (editable instanceof CoastPainter)
      imageKey = "coast.png";
    else if (editable instanceof VPFDatabase)
      imageKey = "vpf.png";
    else if (editable instanceof HasEditables)
      imageKey = "layer.png";
    else if (editable instanceof LabelWrapper)
      imageKey = "label.png";
    return imageKey;
  }
}
