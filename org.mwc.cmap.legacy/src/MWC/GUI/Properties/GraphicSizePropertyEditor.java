/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */

package MWC.GUI.Properties;

import java.beans.PropertyEditorSupport;

public class GraphicSizePropertyEditor extends PropertyEditorSupport
{

  protected Double _mySize;

  private final String stringTags[] =
  {"Small", "Medium", "Large"};

  private final double vals[] =
  {6, 10, 14};

  @Override
  public String getAsText()
  {
    String res = null;
    final double current = _mySize.doubleValue();
    for (int i = 0; i < vals.length; i++)
    {
      final double v = vals[i];
      if (v == current)
      {
        res = stringTags[i];
      }
    }

    return res;
  }

  @Override
  public String[] getTags()
  {
    return stringTags;
  }

  @Override
  public Object getValue()
  {
    return _mySize;
  }

  @Override
  public void setAsText(final String val)
  {
    for (int i = 0; i < stringTags.length; i++)
    {
      final String thisS = stringTags[i];
      if (thisS.equals(val))
      {
        _mySize = new Double(vals[i]);
      }
    }
  }

  @Override
  public void setValue(final Object p1)
  {
    if (p1 instanceof Double)
    {
      _mySize = (Double) p1;
    }
    if (p1 instanceof String)
    {
      final String val = (String) p1;
      setAsText(val);
    }
  }
}
