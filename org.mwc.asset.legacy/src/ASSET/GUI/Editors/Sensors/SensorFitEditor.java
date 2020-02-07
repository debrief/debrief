
package ASSET.GUI.Editors.Sensors;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import ASSET.Models.SensorType;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Sensor.SensorList;

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

import ASSET.Models.Sensor.Initial.InitialSensor;
import ASSET.Models.Sensor.Lookup.LookupSensor;
import MWC.GUI.Editable;

public class SensorFitEditor extends MWC.GUI.Properties.Swing.SwingCustomEditor {

	//////////////////////////////////////////////////////////////////////
	// GUI components
	//////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////
	// the core sensor editor object
	////////////////////////////////////////////////////
	static public class CoreSensorViewer extends MWC.GUI.Editable.EditorType {
		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public CoreSensorViewer(final CoreSensor data) {
			super(new WrappedSensor(data), data.getName(), "View");
		}

		/**
		 * return a description of this bean, also specifies the custom editor we use
		 *
		 * @return the BeanDescriptor
		 */
		@Override
		public java.beans.BeanDescriptor getBeanDescriptor() {
			final java.beans.BeanDescriptor bp = new java.beans.BeanDescriptor(SensorList.class,
					ASSET.GUI.Editors.Sensors.CoreSensorComponentViewer.class);
			bp.setDisplayName(super.getName() + " Viewer");
			return bp;
		}

	}

	////////////////////////////////////////////////////
	// the core sensor editor object
	////////////////////////////////////////////////////
	static public class LookupSensorViewer extends MWC.GUI.Editable.EditorType {
		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public LookupSensorViewer(final LookupSensor data) {
			super(new WrappedSensor(data), data.getName(), "View");
		}

		/**
		 * return a description of this bean, also specifies the custom editor we use
		 *
		 * @return the BeanDescriptor
		 */
		@Override
		public java.beans.BeanDescriptor getBeanDescriptor() {
			final java.beans.BeanDescriptor bp = new java.beans.BeanDescriptor(SensorList.class,
					ASSET.GUI.Editors.Sensors.LookupSensorComponentViewer.class);
			bp.setDisplayName(super.getName() + " Viewer");
			return bp;
		}

	}

	////////////////////////////////////////////////////
	// embedded class containing sensor description with editors
	////////////////////////////////////////////////////
	private class SensorItem extends JPanel {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		SensorType _mySensor;

		public SensorItem(final SensorType thisSensor) {
			_mySensor = thisSensor;
			buildForm();
		}

		private void buildForm() {
			// give ourselves a nice border
			this.setBorder(BorderFactory.createTitledBorder(_mySensor.getName()));

			// create the edit/view buttons
			final JButton view = new JButton("Monitor");
			final JButton edit = new JButton("Edit");
			final JButton plot = new JButton("Plot");

			// set the layout as a column
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			final JPanel btnHolder = new JPanel();
			btnHolder.setLayout(new GridLayout(1, 0));
			btnHolder.add(view);
			btnHolder.add(plot);
			btnHolder.add(edit);

			this.add(btnHolder);

			// create the viewer (only for a core sensor)
			if (_mySensor instanceof InitialSensor) {
				final BaseSensorViewer cs = new SensorExcessViewer();
				cs.setObject(new WrappedSensor(_mySensor));
				this.add(cs);
			}

			view.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					viewThis(_mySensor);
				}
			});
			edit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					editThis(_mySensor);
				}
			});
			plot.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					plotThis(_mySensor);
				}
			});

			// disable the edit button if the sensor's not editable
			edit.setEnabled(_mySensor.hasEditor());
		}

	}

	//////////////////////////////////////////////////////////////////////
	// drag and drop components
	//////////////////////////////////////////////////////////////////////

	/**
	 * we wrap the sensor to give it a different id. we need to do this because we
	 * may open the same SensorFit twice in the properties panel, but the properties
	 * panel uses the object itself to see if a new panel is already open. When we
	 * are viewing a sensor and then ask to edit it we just get the sensor viewer
	 * re-opening (since it's based on the same object). To overcome this we wrap
	 * the sensor in this WrappedSensor class to fool the properties panel into
	 * showing the same sensor twice.
	 */
	public static class WrappedSensor {
		public SensorType MySensor;

		public WrappedSensor(final SensorType theSensor) {
			MySensor = theSensor;
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final JPanel sensorList = new JPanel();

	private final BorderLayout mainBorder = new BorderLayout();

	private ASSET.Models.Sensor.SensorList _myList;

	public SensorFitEditor() {

	}

	void editThis(final SensorType thisSensor) {
		if (thisSensor.hasEditor()) {
			super.getPanel().addEditor(thisSensor.getInfo(), null);
		}
	}

	private void initForm() {
		this.setLayout(mainBorder);
		sensorList.setLayout(new GridLayout(0, 1));

		final JPanel modHolder = new JPanel();
		modHolder.setLayout(new BorderLayout());
		modHolder.add(sensorList, BorderLayout.CENTER);
		this.add(modHolder, BorderLayout.CENTER);
	}

	void plotThis(final SensorType thisSensor) {

		Editable.EditorType theEditor = null;

		// sort out what type of sensor this is.
		theEditor = new Editable.EditorType(thisSensor, thisSensor.getName(), "Plot") {
			/**
			 * return a description of this bean, also specifies the custom editor we use
			 *
			 * @return the BeanDescriptor
			 */
			@Override
			public java.beans.BeanDescriptor getBeanDescriptor() {
				// final java.beans.BeanDescriptor bp = new
				// java.beans.BeanDescriptor(ASSET.ParticipantType.class,
				// ASSET.GUI.Editors.GraphicDetectionViewer.class);
				// bp.setDisplayName(super.getData().toString());
				return null;
			}

		};

		// open up the general plotter for this
		if (theEditor != null)
			super.getPanel().addEditor(theEditor, null);
	}

	public void propertyChange(final java.beans.PropertyChangeEvent pe) {
	}

	@Override
	public void setObject(final Object value) {
		setValue(value);
	}

	private void setValue(final Object value) {
		//
		if (value instanceof ASSET.Models.Sensor.SensorList) {
			_myList = (ASSET.Models.Sensor.SensorList) value;

			updateForm();

			initForm();
		}
	}

	public boolean supportsCustomEditor() {
		return true;
	}

	private void updateForm() {
		sensorList.removeAll();

		final Collection<SensorType> rawSensors = _myList.getSensors();
		final Iterator<SensorType> throughSensors = rawSensors.iterator();
		while (throughSensors.hasNext()) {
			final SensorType sensor = throughSensors.next();
			final SensorItem as = new SensorItem(sensor);
			sensorList.add(as);
		}

	}

	void viewThis(final SensorType thisSensor) {
		Editable.EditorType theEditor = null;

		// sort out what type of sensor this is.
		if (thisSensor instanceof InitialSensor)
			theEditor = new CoreSensorViewer((CoreSensor) thisSensor);
		else if (thisSensor instanceof LookupSensor)
			theEditor = new LookupSensorViewer((LookupSensor) thisSensor);

		// open up the general plotter for this
		if (theEditor != null)
			super.getPanel().addEditor(theEditor, null);
	}

}