
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