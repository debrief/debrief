
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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import ASSET.Models.SensorType;
import ASSET.Models.Decision.Movement.Wander;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Movement.HeloMovementCharacteristics;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Sensor.Initial.BroadbandSensor;
import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.OpticLookupSensor;
import ASSET.Participants.Category;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

public class LookupSensorComponentViewer extends BaseSensorViewer {

	//////////////////////////////////////////////////////////////////////
	// GUI components
	//////////////////////////////////////////////////////////////////////

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {

		// create a movement model
		final Wander wander = new Wander(new WorldLocation(1, 1, 0), new WorldDistance(3000, WorldDistance.YARDS));

		// set up the Ssk
		final ASSET.Models.Vessels.SSK ssk = new ASSET.Models.Vessels.SSK(12);
		final ASSET.Participants.Status sskStat = new ASSET.Participants.Status(12, 0);
		final WorldLocation origin = new WorldLocation(0, 0, 0);
		sskStat.setLocation(origin.add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(3), 40)));
		sskStat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
		ssk.setMovementChars(MovementCharacteristics.getSampleChars());
		ssk.setStatus(sskStat);
		ssk.setDecisionModel(wander);
		ssk.setName("SSK");

		// ok, setup the ssk radiation
		final ASSET.Models.Mediums.BroadbandRadNoise brn = new ASSET.Models.Mediums.BroadbandRadNoise(134);
		final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics rc = new ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();
		rc.add(EnvironmentType.BROADBAND_PASSIVE, brn);
		ssk.setRadiatedChars(rc);

		// now setup the helo
		final ASSET.Models.Vessels.Helo merlin = new ASSET.Models.Vessels.Helo(33);
		merlin.setMovementChars(HeloMovementCharacteristics.getSampleChars());
		final ASSET.Participants.Status merlinStat = new ASSET.Participants.Status(33, 0);
		merlinStat.setLocation(origin.add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(1), -400)));
		merlinStat.setSpeed(new WorldSpeed(60, WorldSpeed.M_sec));
		merlin.setStatus(merlinStat);
		merlin.setDecisionModel(wander);
		merlin.setName("Merlin");

		// and it's sensor
		final ASSET.Models.Sensor.SensorList fit = new ASSET.Models.Sensor.SensorList();
		final OpticLookupSensor optic = OpticLookupSensor.OpticLookupTest.getTestOpticSensor();
		fit.add(optic);
		merlin.setSensorFit(fit);
		merlin.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));

		// now setup the su
		final ASSET.Models.Vessels.Surface ff = new ASSET.Models.Vessels.Surface(31);
		final ASSET.Participants.Status ffStat = new ASSET.Participants.Status(31, 0);
		final WorldLocation sskLocation = ssk.getStatus().getLocation();
		ffStat.setLocation(sskLocation.add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(4), -40)));
		ffStat.setSpeed(new WorldSpeed(6, WorldSpeed.M_sec));
		ff.setMovementChars(MovementCharacteristics.getSampleChars());
		ff.setStatus(ffStat);

		final ASSET.Models.Sensor.SensorList fit2 = new ASSET.Models.Sensor.SensorList();
		final BroadbandSensor bs2 = new BroadbandSensor(34);
		fit2.add(bs2);
		ff.setSensorFit(fit2);
		final ASSET.Models.Mediums.BroadbandRadNoise ff_brn = new ASSET.Models.Mediums.BroadbandRadNoise(35);
		final ASSET.Models.Vessels.Radiated.RadiatedCharacteristics ff_rc = new ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();
		ff_rc.add(EnvironmentType.BROADBAND_PASSIVE, ff_brn);
		ff.setSelfNoise(ff_rc);
		ff.setDecisionModel(wander);
		ff.setRadiatedChars(rc);
		ff.setName("FF");
		ff.setCategory(new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FRIGATE));

		// setup the scenario
		final ASSET.Scenario.CoreScenario cs = new ASSET.Scenario.CoreScenario();
		cs.addParticipant(ff.getId(), ff);
		cs.addParticipant(merlin.getId(), merlin);
		cs.addParticipant(ssk.getId(), ssk);
		cs.setEnvironment(new SimpleEnvironment(0, 1, 0));

		// and the viewer!!
		final JFrame viewer = new JFrame();
		viewer.setSize(400, 300);
		viewer.setVisible(true);

		final MWC.GUI.Properties.Swing.SwingPropertiesPanel props = new MWC.GUI.Properties.Swing.SwingPropertiesPanel(
				null, null, null, null);

		//// props.addEditor(optic.getInfo(), null);

		final LookupSensorComponentViewer sv = new LookupSensorComponentViewer();
		sv.setName("optic");
		sv.setObject(new SensorFitEditor.WrappedSensor(optic));
		props.add(sv);

		viewer.getContentPane().setLayout(new BorderLayout());
		viewer.getContentPane().add(props, BorderLayout.CENTER);

		final JButton stepper = new JButton("Step");
		stepper.addActionListener(new java.awt.event.ActionListener() {
			/**
			 * Invoked when an action occurs.
			 */
			@Override
			public void actionPerformed(final ActionEvent e) {
				// move the scenario forward
				cs.step();
			}
		});
		viewer.getContentPane().add(stepper, BorderLayout.SOUTH);

		viewer.doLayout();
		viewer.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		viewer.pack();

	}

	/**
	 * the table we show our data in
	 */
	private JTable _myTable;

	/**
	 * the data model for the table
	 */
	private javax.swing.table.DefaultTableModel _myTableModel;

	////////////////////////////////////////////////////
	// constructor
	////////////////////////////////////////////////////

	/**
	 * the list of column names for our data
	 */
	private final Vector<String> _cols;

	public LookupSensorComponentViewer() {
		// ok, create the columns
		_cols = new Vector<String>(1, 1);
		_cols.add("Name");
		_cols.add("State");
		_cols.add("RP (m)");
		_cols.add("RI (m)");
		_cols.add("Actual (m)");
	}

	/**
	 * build the form
	 */
	@Override
	public void initForm() {

		this.setLayout(new BorderLayout());
		// and the table
		_myTableModel = new javax.swing.table.DefaultTableModel();
		_myTable = new JTable(_myTableModel);

		// format the table
		_myTable.setAutoCreateColumnsFromModel(true);
		_myTable.setRowSelectionAllowed(false);
		_myTable.setColumnSelectionAllowed(false);

		_myTable.setMinimumSize(new Dimension(200, 400));

		// and store the table
		this.add(_myTable, BorderLayout.CENTER);

		// sort out the table header
		this.add(_myTable.getTableHeader(), BorderLayout.NORTH);
	}

	@Override
	protected void listenTo(final SensorType newSensor) {
		final LookupSensor ls = (LookupSensor) newSensor;

		// and listen to it!
		ls.addSensorCalculationListener(this);
	}

	////////////////////////////////////////////////////
	// TEST THIS GUI Class
	////////////////////////////////////////////////////

	/**
	 * we've received some new data, update the GUI
	 */
	@Override
	public void updateForm() {
		// step through the detections, collating them into the vector expected by the
		// table
		final Vector<Vector<String>> theData = new Vector<Vector<String>>(1, 1);
		final Iterator<Object> it = _sensorEvents.iterator();
		while (it.hasNext()) {
			final LookupSensor.LookupSensorComponentsEvent sc = (LookupSensor.LookupSensorComponentsEvent) it.next();
			final Vector<String> thisV = new Vector<String>(1, 1);
			thisV.add(sc.getTgtName());
			thisV.add(sc.getStateString());
			thisV.add("" + (int) sc.getRP().getValueIn(WorldDistance.METRES));
			thisV.add("" + (int) sc.getRI().getValueIn(WorldDistance.METRES));
			thisV.add("" + (int) sc.getActual().getValueIn(WorldDistance.METRES));
			theData.add(thisV);
		}

		// and update the model
		_myTableModel.setDataVector(theData, _cols);

	}

}