package org.mwc.debrief.lite.graph;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.mwc.debrief.lite.gui.LiteStepControl;

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
    toolbar = new GraphPanelToolbar(stepControl);
    _theChart = stepControl.getXYChart();
    
    add(toolbar, BorderLayout.NORTH);
    
    // Text component at center. We will have a map here.
    add(new JTextArea(), BorderLayout.CENTER);
  }


  @Override
  public void setParent(ToolParent theParent)
  {
    this._theParent = theParent;
    toolbar.setParent(theParent);
  }
}
