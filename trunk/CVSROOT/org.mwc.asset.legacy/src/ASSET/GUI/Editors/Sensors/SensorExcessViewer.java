package ASSET.GUI.Editors.Sensors;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.text.DecimalFormat;

import ASSET.Models.Sensor.*;
import ASSET.Models.Sensor.Initial.InitialSensor;
import ASSET.Models.SensorType;

public class SensorExcessViewer extends BaseSensorViewer
{

  //////////////////////////////////////////////////////////////////////
  // GUI components
  //////////////////////////////////////////////////////////////////////


  /** the label we show our data in
   *
   */
  private JLabel _myLabel;

  /** the text formatter we use to align the se values
   *
   */
  private java.text.DecimalFormat _numberFormat;

  ////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////

  public SensorExcessViewer()
  {
    _numberFormat = new DecimalFormat(" 000;-000");
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
    Iterator it = _sensorEvents.iterator();
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