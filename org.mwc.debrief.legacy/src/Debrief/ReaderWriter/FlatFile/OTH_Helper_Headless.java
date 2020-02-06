/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package Debrief.ReaderWriter.FlatFile;

public class OTH_Helper_Headless implements OTH_Helper
{
  final private boolean _genEllipses;

  public OTH_Helper_Headless(final boolean genEllipses)
  {
    super();
    this._genEllipses = genEllipses;
  }

  @Override
  public Boolean generateEllipses()
  {
    return _genEllipses;
  }
}
