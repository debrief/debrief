
package ASSET.GUI.Editors.Sensors;

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

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.Iterator;

import javax.swing.JLabel;

import ASSET.Models.SensorType;
import ASSET.Models.Sensor.Initial.InitialSensor;

public class SensorExcessViewer extends BaseSensorViewer {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	//////////////////////////////////////////////////////////////////////
	// GUI components
	//////////////////////////////////////////////////////////////////////

	/**
	 * the label we show our data in
	 *
	 */
	private JLabel _myLabel;

	public SensorExcessViewer() {
		new DecimalFormat(" 000;-000");
	}

	/**
	 * build the form
	 *
	 */
	@Override
	public void initForm() {

		this.setLayout(new BorderLayout());

		// and the label
		_myLabel = new JLabel();
		_myLabel.setFont(new Font("DialogInput", Font.PLAIN, _myLabel.getFont().getSize()));

		// and store the table
		this.add(_myLabel, BorderLayout.CENTER);

	}

	@Override
	protected void listenTo(final SensorType newSensor) {
		newSensor.addSensorCalculationListener(this);
	}

	/**
	 * we've received some new data, update the GUI
	 *
	 */
	@Override
	public void updateForm() {
		// step through the detections, collating them into the vector expected by the
		// table
		final Iterator<Object> it = _sensorEvents.iterator();
		String theLabel = "";
		while (it.hasNext()) {
			final InitialSensor.InitialSensorComponentsEvent sc = (InitialSensor.InitialSensorComponentsEvent) it
					.next();
			theLabel += sc.getTgtName() + ":" + (int) sc.getSE() + " ";
		}

		// and update the model
		_myLabel.setText(theLabel);

	}

}