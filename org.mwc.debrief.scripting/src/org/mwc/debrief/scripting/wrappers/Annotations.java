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
  public static PlainSymbol createSymbol(final String symbolType)
  {
    return SymbolFactory.createSymbol(symbolType);
  }

  @WrapToScript
  public static LabelWrapper createLabel(final String label,
      final WorldLocation location, final java.awt.Color theColor,
      final HiResDate startDTG, final HiResDate endDTG)
  {
    return new LabelWrapper(label, location, theColor, startDTG, endDTG);
  }

  @WrapToScript
  public static LabelWrapper createLabel(final String label,
      final WorldLocation location, final java.awt.Color theColor)
  {
    return new LabelWrapper(label, location, theColor);
  }
  
  
}
