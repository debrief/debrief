package MWC.GUI.Properties;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import java.awt.event.*;
import MWC.GUI.Properties.*;
import MWC.GUI.ToolParent;
import MWC.GenericData.*;

abstract public class WorldPathPropertyEditor  extends
           java.beans.PropertyEditorSupport implements PlainPropertyEditor.EditorUsesChart,
                                            PlainPropertyEditor.EditorUsesPropertyPanel,
                                            PlainPropertyEditor.EditorUsesToolParent
{

  protected WorldPath _myPath;
  protected MWC.GUI.PlainChart _myChart;
  protected PropertiesPanel _thePanel;
  protected ToolParent _theParent;

  abstract public java.awt.Component getCustomEditor();
  abstract protected void resetData();

  public void setValue(Object p1)
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

	public void setChart(MWC.GUI.PlainChart theChart)
  {
    _myChart = theChart;
  }

  public void setPanel(PropertiesPanel thePanel)
  {
    _thePanel = thePanel;
  }

  public void setParent(ToolParent theParent)
  {
    _theParent = theParent;
  }
}