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
package org.mwc.debrief.scripting.wrappers;

import org.eclipse.ease.modules.WrapToScript;

import Debrief.Wrappers.LabelWrapper;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GUI.Shapes.Symbols.SymbolFactory;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;

public class Annotations
{

  @WrapToScript
  /**
   * Creates a label given its name, location and color
   *
   * @param label
   *          Text to display
   * @param location
   *          Location of the label
   * @param theColor
   *          Color of the label
   * @return
   */
  public static LabelWrapper createLabel(final String name,
      final WorldLocation location, final java.awt.Color theColor)
  {
    return new LabelWrapper(name, location, theColor);
  }

  @WrapToScript
  /**
   * Creates a label given its name, location and color
   *
   * @param label
   *          the text to display
   * @param location
   *          the location to centre the label on
   * @param theColor
   *          the colour to plot the text
   * @param startDTG
   *          the start (or centre) time of the label
   * @param endDTG
   *          the end time, or null if single date value
   * @return
   */
  public static LabelWrapper createLabelDate(final String label,
      final WorldLocation location, final java.awt.Color theColor,
      final HiResDate startDTG, final HiResDate endDTG)
  {
    return new LabelWrapper(label, location, theColor, startDTG, endDTG);
  }

  @WrapToScript
  /**
   * Given an ID of a Symbol, returns a PlainSymbol.
   *
   * @param symbolType
   *          Symbol ID.
   * @return PlainSymbol based on the symbol id given.
   */
  public static PlainSymbol createSymbol(final String symbolType)
  {
    return SymbolFactory.createSymbol(symbolType);
  }

}
