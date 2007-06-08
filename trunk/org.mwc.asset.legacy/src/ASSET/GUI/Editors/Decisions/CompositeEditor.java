package ASSET.GUI.Editors.Decisions;

import MWC.GUI.PlainChart;
import MWC.GUI.Properties.PlainPropertyEditor;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Properties.Swing.SwingPropertiesPanel;
import MWC.GUI.Properties.Swing.SwingPropertyEditor2;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyEditor;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

public class CompositeEditor extends MWC.GUI.Properties.Swing.SwingCustomEditor
  implements PlainPropertyEditor.EditorUsesChart,
  PlainPropertyEditor.EditorUsesPropertyPanel, MWC.GUI.Properties.NoEditorButtons

{

  //////////////////////////////////////////////////////////////////////
  // GUI components
  //////////////////////////////////////////////////////////////////////

  SwingPropertyEditor2 _compositeEditor = null;
  SwingPropertyEditor2 _conditionEditor = null;
  SwingPropertyEditor2 _responseEditor = null;

  //  PlainChart _theChart;
  PropertyEditor _theEditor;

  //////////////////////////////////////////////////////////////////////
  // member
  //////////////////////////////////////////////////////////////////////
  private ASSET.Models.Decision.Composite _myComposite;

  ////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////
  public CompositeEditor()
  {

  }

  public boolean supportsCustomEditor()
  {
    return true;
  }

  public void setChart(PlainChart theChart)
  {
    _theChart = theChart;
  }

  public void setPanel(PropertiesPanel thePanel)
  {
    _thePanel = thePanel;
  }

  public void setObject(final Object value)
  {
    setValue(value);
  }

  private void setValue(final Object value)
  {
    //
    if (value instanceof ASSET.Models.Decision.Composite)
    {
      _myComposite = (ASSET.Models.Decision.Composite) value;

      updateForm();

      initForm();
    }
  }


  private void initForm()
  {
    // right, at the top we need our name editor
    this.setLayout(new GridLayout(0, 1));


    // create the 3 property editors
    JComponent cond = (JComponent) _conditionEditor.getPanel();
    cond.setBorder(BorderFactory.createTitledBorder("Condition"));
    JComponent resp = (JComponent) _responseEditor.getPanel();
    resp.setBorder(BorderFactory.createTitledBorder("Response"));

    this.add(cond);
    this.add(resp);
  }

  private void updateForm()
  {
    //   _compositeEditor = new SwingPropertyEditor2(_myComposite.getInfo(), (SwingPropertiesPanel)_thePanel, (PlainChart)_theChart, null, null);
    _conditionEditor = new SwingPropertyEditor2(_myComposite.getCondition().getInfo(), (SwingPropertiesPanel) _thePanel, (PlainChart) _theChart, null, null);
    _responseEditor = new SwingPropertyEditor2(_myComposite.getResponse().getInfo(), (SwingPropertiesPanel) _thePanel, (PlainChart) _theChart, null, null);
  }

}