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

import MWC.GUI.Layers;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import MWC.GUI.ToolParent;
import MWC.GenericData.WorldPath;

abstract public class WorldPathPropertyEditor  extends
           java.beans.PropertyEditorSupport implements PlainPropertyEditor.EditorUsesPropertyPanel,
                                            PlainPropertyEditor.EditorUsesToolParent,
                                            PlainPropertyEditor.EditorUsesLayers
{

  protected WorldPath _myPath;
  protected PropertiesPanel _thePanel;
  protected ToolParent _theParent;
  protected Layers _theLayers;

  abstract public java.awt.Component getCustomEditor();
  abstract protected void resetData();

  public void setValue(final Object p1)
  {
    if(p1 instanceof WorldPath)
    {
      _myPath = (WorldPath) p1;
      resetData();
    }
    else
      return;
  }

  public boolean supportsCustomEditor()
  {
    return true;
  }

  public Object getValue()
  {
    return _myPath;
  }

  @Override
  public void setLayers(Layers theLayers)
  {
    _theLayers = theLayers;
  }
  public void setPanel(final PropertiesPanel thePanel)
  {
    _thePanel = thePanel;
  }

  public void setParent(final ToolParent theParent)
  {
    _theParent = theParent;
  }
}