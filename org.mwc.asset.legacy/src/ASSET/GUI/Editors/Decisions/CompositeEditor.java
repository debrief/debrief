
package ASSET.GUI.Editors.Decisions;

import java.awt.GridLayout;
import java.beans.PropertyEditor;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import MWC.GUI.Properties.PlainPropertyEditor;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Properties.Swing.SwingPropertiesPanel;
import MWC.GUI.Properties.Swing.SwingPropertyEditor2;

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

public class CompositeEditor extends MWC.GUI.Properties.Swing.SwingCustomEditor
    implements PlainPropertyEditor.EditorUsesPropertyPanel,
    MWC.GUI.Properties.NoEditorButtons
{

  //////////////////////////////////////////////////////////////////////
  // GUI components
  //////////////////////////////////////////////////////////////////////

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  SwingPropertyEditor2 _compositeEditor = null;
  SwingPropertyEditor2 _conditionEditor = null;
  SwingPropertyEditor2 _responseEditor = null;

  // PlainChart _theChart;
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

  private void initForm()
  {
    // right, at the top we need our name editor
    this.setLayout(new GridLayout(0, 1));

    // create the 3 property editors
    final JComponent cond = (JComponent) _conditionEditor.getPanel();
    cond.setBorder(BorderFactory.createTitledBorder("Condition"));
    final JComponent resp = (JComponent) _responseEditor.getPanel();
    resp.setBorder(BorderFactory.createTitledBorder("Response"));

    this.add(cond);
    this.add(resp);
  }

  @Override
  public void setObject(final Object value)
  {
    setValue(value);
  }

  @Override
  public void setPanel(final PropertiesPanel thePanel)
  {
    _thePanel = thePanel;
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

  public boolean supportsCustomEditor()
  {
    return true;
  }

  private void updateForm()
  {
    // _compositeEditor = new SwingPropertyEditor2(_myComposite.getInfo(),
    // (SwingPropertiesPanel)_thePanel, (PlainChart)_theChart, null, null);
    _conditionEditor = new SwingPropertyEditor2(_myComposite.getCondition()
        .getInfo(), (SwingPropertiesPanel) _thePanel, _theLayers, null, null,
        null);
    _responseEditor = new SwingPropertyEditor2(_myComposite.getResponse()
        .getInfo(), (SwingPropertiesPanel) _thePanel, _theLayers, null, null,
        null);
  }

}