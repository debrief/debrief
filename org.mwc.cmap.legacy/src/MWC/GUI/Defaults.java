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
package MWC.GUI;

import java.awt.Font;

public class Defaults
{
  public static interface PreferenceProvider
  {
    public Font getDefaultFont();
    
    public String getPreference(final String name);
  }

  private static PreferenceProvider _provider;

  public static void setProvider(PreferenceProvider provider)
  {
    _provider = provider;
  }

  public static Font getScaledFont(final float scale)
  {
    return getFont().deriveFont(getFont().getSize() * scale);
  }
  
  public static Font getFont()
  {
    final Font pref = _provider != null ? _provider.getDefaultFont() : null;
    final Font res = pref != null ? pref : new Font("Arial",
        Font.PLAIN, 10);
      
    return res;
  }
  
  public static String getPreference(String prefName)
  {
    final String res = _provider != null ? _provider.getPreference(prefName) : null;
    return res;
  }
}
