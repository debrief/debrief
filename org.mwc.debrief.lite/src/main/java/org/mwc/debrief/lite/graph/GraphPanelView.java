package org.mwc.debrief.lite.graph;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.mwc.debrief.lite.gui.LiteStepControl;
import org.mwc.debrief.lite.gui.custom.SimpleEditablePropertyPanel;

import MWC.GUI.ToolParent;
import MWC.GUI.Properties.PlainPropertyEditor;

public class GraphPanelView extends JPanel implements PlainPropertyEditor.EditorUsesToolParent
{
  /**
   * 
   */
  private static final long serialVersionUID = 5203809173295266164L;

  private final GraphPanelToolbar toolbar;

  /**
   * the chart we need to update
   */
  final MWC.GUI.PlainChart _theChart;
  
  /**
   * Busy cursor
   */
  private ToolParent _theParent;
  
  public GraphPanelView(final LiteStepControl stepControl)
  {
    super();
    setLayout(new BorderLayout());
    SimpleEditablePropertyPanel xyPanel = new SimpleEditablePropertyPanel();
    toolbar = new GraphPanelToolbar(stepControl, xyPanel);
    _theChart = stepControl.getXYChart();
    
    add(toolbar, BorderLayout.NORTH);
    
    add(xyPanel, BorderLayout.CENTER);
  }


  @Override
  public void setParent(ToolParent theParent)
  {
    this._theParent = theParent;
    toolbar.setParent(theParent);
  }
}
