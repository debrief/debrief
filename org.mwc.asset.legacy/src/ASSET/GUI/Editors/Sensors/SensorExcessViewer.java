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