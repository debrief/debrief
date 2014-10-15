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
package ASSET.GUI.SuperSearch;

import MWC.GUI.Layers;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class SSBuilderSwing extends JPanel implements PropertyChangeListener
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/***************************************************************
   *  member variables
   ***************************************************************/
  private SSBuilder _myBuilder;

  /** the dropper for the blue participants
   *
   */
  private JLabel _blueLabel;

  /** the dropper for the template
   *
   */
  private JLabel _templateLabel;

  /** the dropper for the control file
   *
   */
  private JLabel _controlLabel;

  /** the build button
   *
   */
  private JButton _buildBtn;

  /***************************************************************
   *  constructor
   ***************************************************************/
  /**
   * Create a new JPanel with a double buffer and a flow layout
   */
  public SSBuilderSwing(final ASSET.Scenario.MultiForceScenario scenario, final Layers theData)
  {
    super.setName("Builder");

    _myBuilder = new SSBuilder(scenario, theData);
    _myBuilder.setListener(this);

    initForm();
  }
  /***************************************************************
   *  member methods
   ***************************************************************/

  /** build the interface
   *
   */
  private void initForm()
  {
    _blueLabel = new JLabel("Blue");
    _templateLabel = new JLabel("Red Template");
    _controlLabel = new JLabel("Control");

    _myBuilder._blueDropper.addComponent(_blueLabel);
    _myBuilder._templateDropper.addComponent(_templateLabel);
    _myBuilder._controlDropper.addComponent(_controlLabel);

    _buildBtn = new JButton("Build");
    _buildBtn.setEnabled(false);
    _buildBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        doBuild();
      }
    });

    this.setLayout(new GridLayout(0,1));

    add(_blueLabel);
    add(_templateLabel);
    add(_controlLabel);
    add(_buildBtn);
  }

  /** do the build operatoin
   *
   */
  void doBuild()
  {
    _myBuilder.doBuild();

    _buildBtn.setEnabled(false);
  }

  /**
   * This method gets called when a bound property is changed.
   * @param evt A PropertyChangeEvent object describing the event source
   *   	and the property that has changed.
   */

  public void propertyChange(final PropertyChangeEvent evt)
  {
    // check what has happened to our embedded builder
    if(_myBuilder._controlFile != null)
      _controlLabel.setEnabled(false);

    if(_myBuilder._templateFile != null)
      _templateLabel.setEnabled(false);

    if(evt.getPropertyName().equals("blue"))
      _blueLabel.setText("Blue Force: " +
                         _myBuilder._myScenario.getListOfParticipants().length
                         + " vessel(s) loaded");

    if((_myBuilder._controlFile != null) && (_myBuilder._templateFile != null))
      _buildBtn.setEnabled(true);
  }
}
