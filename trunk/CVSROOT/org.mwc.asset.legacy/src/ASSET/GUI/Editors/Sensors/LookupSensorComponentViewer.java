package ASSET.GUI.Editors.Sensors;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Movement.Wander;
import ASSET.Models.Environment.*;
import ASSET.Models.Movement.*;
import ASSET.Models.Sensor.Initial.BroadbandSensor;
import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.OpticLookupSensor;
import ASSET.Models.SensorType;
import ASSET.Participants.Category;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;

public class LookupSensorComponentViewer extends BaseSensorViewer
{

  //////////////////////////////////////////////////////////////////////
  // GUI components
  //////////////////////////////////////////////////////////////////////


  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the table we show our data in
   */
  private JTable _myTable;

  /**
   * the data model for the table
   */
  private javax.swing.table.DefaultTableModel _myTableModel;

  /**
   * the list of column names for our data
   */
  private final Vector _cols;

  ////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////

  public LookupSensorComponentViewer()
  {
    // ok, create the columns
    _cols = new Vector(1, 1);
    _cols.add("Name");
    _cols.add("State");
    _cols.add("RP (m)");
    _cols.add("RI (m)");
    _cols.add("Actual (m)");
  }


  /**
   * build the form
   */
  public void initForm()
  {

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

  /**
   * we've received some new data, update the GUI
   */
  public void updateForm()
  {
    // step through the detections, collating them into the vector expected by the table
    Vector theData = new Vector(1, 1);
    Iterator it = _sensorEvents.iterator();
    while (it.hasNext())
    {
      LookupSensor.LookupSensorComponentsEvent sc = (LookupSensor.LookupSensorComponentsEvent) it.next();
      Vector thisV = new Vector(1, 1);
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

  protected void listenTo(SensorType newSensor)
  {
    LookupSensor ls = (LookupSensor) newSensor;

    // and listen to it!
    ls.addSensorCalculationListener(this);
  }

  ////////////////////////////////////////////////////
  //  TEST THIS GUI Class
  ////////////////////////////////////////////////////

  public static void main(String[] args)
  {

    // create a movement model
    Wander wander = new Wander(new WorldLocation(1, 1, 0), new WorldDistance(3000, WorldDistance.YARDS));

    // set up the Ssk
    ASSET.Models.Vessels.SSK ssk = new ASSET.Models.Vessels.SSK(12);
    ASSET.Participants.Status sskStat = new ASSET.Participants.Status(12, 0);
    WorldLocation origin = new WorldLocation(0, 0, 0);
    sskStat.setLocation(origin.add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(3), 40)));
    sskStat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
    ssk.setMovementChars(SSMovementCharacteristics.getSampleChars());
    ssk.setStatus(sskStat);
    ssk.setDecisionModel(wander);
    ssk.setName("SSK");


    // ok, setup the ssk radiation
    ASSET.Models.Mediums.BroadbandRadNoise brn = new ASSET.Models.Mediums.BroadbandRadNoise(134);
    ASSET.Models.Vessels.Radiated.RadiatedCharacteristics rc = new ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();
    rc.add(EnvironmentType.BROADBAND_PASSIVE, brn);
    ssk.setRadiatedChars(rc);

    // now setup the helo
    final ASSET.Models.Vessels.Helo merlin = new ASSET.Models.Vessels.Helo(33);
    merlin.setMovementChars(HeloMovementCharacteristics.getSampleChars());
    ASSET.Participants.Status merlinStat = new ASSET.Participants.Status(33, 0);
    merlinStat.setLocation(origin.add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(1), -400)));
    merlinStat.setSpeed(new WorldSpeed(60, WorldSpeed.M_sec));
    merlin.setStatus(merlinStat);
    merlin.setDecisionModel(wander);
    merlin.setName("Merlin");

    // and it's sensor
    ASSET.Models.Sensor.SensorList fit = new ASSET.Models.Sensor.SensorList();
    final OpticLookupSensor optic = OpticLookupSensor.OpticLookupTest.getTestOpticSensor();
    fit.add(optic);
    merlin.setSensorFit(fit);
    merlin.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));

    // now setup the su
    ASSET.Models.Vessels.Surface ff = new ASSET.Models.Vessels.Surface(31);
    ASSET.Participants.Status ffStat = new ASSET.Participants.Status(31, 0);
    WorldLocation sskLocation = ssk.getStatus().getLocation();
    ffStat.setLocation(sskLocation.add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(4), -40)));
    ffStat.setSpeed(new WorldSpeed(6, WorldSpeed.M_sec));
    ff.setMovementChars(MovementCharacteristics.getSampleChars());
    ff.setStatus(ffStat);

    ASSET.Models.Sensor.SensorList fit2 = new ASSET.Models.Sensor.SensorList();
    final BroadbandSensor bs2 = new BroadbandSensor(34);
    fit2.add(bs2);
    ff.setSensorFit(fit2);
    ASSET.Models.Mediums.BroadbandRadNoise ff_brn = new ASSET.Models.Mediums.BroadbandRadNoise(35);
    ASSET.Models.Vessels.Radiated.RadiatedCharacteristics ff_rc =
      new ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();
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
    cs.setEnvironment(new SimpleEnvironment(0,1,0));

    // and the viewer!!
    JFrame viewer = new JFrame();
    viewer.setSize(400, 300);
    viewer.setVisible(true);

    MWC.GUI.Properties.Swing.SwingPropertiesPanel props =
      new MWC.GUI.Properties.Swing.SwingPropertiesPanel(null, null, null, null);

    
    
////    props.addEditor(optic.getInfo(), null);
    
    LookupSensorComponentViewer sv = new LookupSensorComponentViewer();
    sv.setName("optic");
    sv.setObject(new SensorFitEditor.WrappedSensor(optic));
    props.add(sv);
    

    viewer.getContentPane().setLayout(new BorderLayout());
    viewer.getContentPane().add(props, BorderLayout.CENTER);

    JButton stepper = new JButton("Step");
    stepper.addActionListener(new java.awt.event.ActionListener()
    {
      /**
       * Invoked when an action occurs.
       */
      public void actionPerformed(ActionEvent e)
      {
        // move the scenario forward
        cs.step();
      }
    });
    viewer.getContentPane().add(stepper, BorderLayout.SOUTH);

    viewer.doLayout();
    viewer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    viewer.pack();

  }

}