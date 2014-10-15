/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package ASSET.GUI.Editors.Sensors;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.Iterator;

import javax.swing.JLabel;

import ASSET.Models.SensorType;
import ASSET.Models.Sensor.Initial.InitialSensor;

public class SensorExcessViewer extends BaseSensorViewer
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//////////////////////////////////////////////////////////////////////
  // GUI components
  //////////////////////////////////////////////////////////////////////


  /** the label we show our data in
   *
   */
  private JLabel _myLabel;

  public SensorExcessViewer()
  {
    new DecimalFormat(" 000;-000");
  }


  protected void listenTo(SensorType newSensor)
  {
    newSensor.addSensorCalculationListener(this);
  }

  /** build the form
   *
   */
  public void initForm(){

    this.setLayout(new BorderLayout());

    // and the label
    _myLabel = new JLabel();
    _myLabel.setFont(new Font("DialogInput", Font.PLAIN, _myLabel.getFont().getSize()));

    // and store the table
    this.add(_myLabel, BorderLayout.CENTER);

  }

  /** we've received some new data, update the GUI
   *
   */
  public void updateForm()
  {
    // step through the detections, collating them into the vector expected by the table
    Iterator<Object> it = _sensorEvents.iterator();
    String theLabel = "";
    while(it.hasNext())
    {
      InitialSensor.InitialSensorComponentsEvent sc = (InitialSensor.InitialSensorComponentsEvent)it.next();
      theLabel += sc.getTgtName() + ":" + (int)sc.getSE() + " ";
    }

    // and update the model
    _myLabel.setText(theLabel);

  }

}