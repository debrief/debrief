package ASSET.GUI.Editors.Sensors;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Sensor.Initial.InitialSensor;
import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.SensorList;
import ASSET.Models.SensorType;
import MWC.GUI.Editable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

public class SensorFitEditor extends MWC.GUI.Properties.Swing.SwingCustomEditor
{

  //////////////////////////////////////////////////////////////////////
  // GUI components
  //////////////////////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel sensorList = new JPanel();
  private BorderLayout mainBorder = new BorderLayout();

  //////////////////////////////////////////////////////////////////////
  // drag and drop components
  //////////////////////////////////////////////////////////////////////

  private ASSET.Models.Sensor.SensorList _myList;

  public SensorFitEditor()
  {

  }

  public boolean supportsCustomEditor()
  {
    return true;
  }

  public void setObject(final Object value)
  {
    setValue(value);
  }

  private void setValue(final Object value)
  {
    //
    if (value instanceof ASSET.Models.Sensor.SensorList)
    {
      _myList = (ASSET.Models.Sensor.SensorList) value;

      updateForm();

      initForm();
    }
  }


  private void initForm()
  {
    this.setLayout(mainBorder);
    sensorList.setLayout(new GridLayout(0, 1));

    final JPanel modHolder = new JPanel();
    modHolder.setLayout(new BorderLayout());
    modHolder.add(sensorList, BorderLayout.CENTER);
    this.add(modHolder, BorderLayout.CENTER);
  }


  private void updateForm()
  {
    sensorList.removeAll();

    Collection<SensorType> rawSensors = _myList.getSensors();
    Iterator<SensorType> throughSensors = rawSensors.iterator();
    while (throughSensors.hasNext())
    {
      SensorType sensor = (SensorType) throughSensors.next();
      SensorItem as = new SensorItem(sensor);
      sensorList.add(as);
    }

  }

  public void propertyChange(final java.beans.PropertyChangeEvent pe)
  {
  }


  void viewThis(SensorType thisSensor)
  {
    Editable.EditorType theEditor = null;

    // sort out what type of sensor this is.
    if (thisSensor instanceof InitialSensor)
      theEditor = new CoreSensorViewer((InitialSensor) thisSensor);
    else if (thisSensor instanceof LookupSensor)
      theEditor = new LookupSensorViewer((LookupSensor) thisSensor);

    // open up the general plotter for this
    if (theEditor != null)
      super.getPanel().addEditor(theEditor, null);
  }

  void plotThis(SensorType thisSensor)
  {

    Editable.EditorType theEditor = null;

    // sort out what type of sensor this is.
    theEditor = new Editable.EditorType(thisSensor, thisSensor.getName(), "Plot")
    {
      /** return a description of this bean, also specifies the custom editor we use
       * @return the BeanDescriptor
       */
        public java.beans.BeanDescriptor getBeanDescriptor()
        {
          final java.beans.BeanDescriptor bp = new java.beans.BeanDescriptor(ASSET.ParticipantType.class,
                                                 ASSET.GUI.Editors.GraphicDetectionViewer.class);
          bp.setDisplayName(super.getData().toString());
          return bp;
        }

    };

    // open up the general plotter for this
    if (theEditor != null)
      super.getPanel().addEditor(theEditor, null);
  }

  void editThis(SensorType thisSensor)
  {
    if (thisSensor.hasEditor())
    {
      super.getPanel().addEditor(thisSensor.getInfo(), null);
    }
  }


  ////////////////////////////////////////////////////
  // the core sensor editor object
  ////////////////////////////////////////////////////
  static public class CoreSensorViewer extends MWC.GUI.Editable.EditorType
  {
    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public CoreSensorViewer(final InitialSensor data)
    {
      super(new WrappedSensor(data), data.getName(), "View");
    }


    /**
     * return a description of this bean, also specifies the custom editor we use
     *
     * @return the BeanDescriptor
     */
    public java.beans.BeanDescriptor getBeanDescriptor()
    {
      final java.beans.BeanDescriptor bp =
          new java.beans.BeanDescriptor(SensorList.class,
              ASSET.GUI.Editors.Sensors.CoreSensorComponentViewer.class);
      bp.setDisplayName(super.getName() + " Viewer");
      return bp;
    }


  }

  /**
   * we wrap the sensor to give it a different id.
   * we need to do this because we may open the same SensorFit twice in the properties panel,
   * but the properties panel uses the object itself to see if a new panel is already open.
   * When we are viewing a sensor and then ask to edit it we just get the sensor
   * viewer re-opening (since it's based on the same object).
   * To overcome this we wrap the sensor in this WrappedSensor class to fool the
   * properties panel into showing the same sensor twice.
   */
  public static class WrappedSensor
  {
    public SensorType MySensor;

    public WrappedSensor(SensorType theSensor)
    {
      MySensor = theSensor;
    }
  }

  ////////////////////////////////////////////////////
  // the core sensor editor object
  ////////////////////////////////////////////////////
  static public class LookupSensorViewer extends MWC.GUI.Editable.EditorType
  {
    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public LookupSensorViewer(final LookupSensor data)
    {
      super(new WrappedSensor(data), data.getName(), "View");
    }


    /**
     * return a description of this bean, also specifies the custom editor we use
     *
     * @return the BeanDescriptor
     */
    public java.beans.BeanDescriptor getBeanDescriptor()
    {
      final java.beans.BeanDescriptor bp =
          new java.beans.BeanDescriptor(SensorList.class,
              ASSET.GUI.Editors.Sensors.LookupSensorComponentViewer.class);
      bp.setDisplayName(super.getName() + " Viewer");
      return bp;
    }

  }


  ////////////////////////////////////////////////////
  // embedded class containing sensor description with editors
  ////////////////////////////////////////////////////
  private class SensorItem extends JPanel
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		SensorType _mySensor;

    public SensorItem(SensorType thisSensor)
    {
      _mySensor = thisSensor;
      buildForm();
    }

    private void buildForm()
    {
      // give ourselves a nice border
      this.setBorder(BorderFactory.createTitledBorder(_mySensor.getName()));

      // create the edit/view buttons
      JButton view = new JButton("Monitor");
      JButton edit = new JButton("Edit");
      JButton plot = new JButton("Plot");

      // set the layout as a column
      this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      JPanel btnHolder = new JPanel();
      btnHolder.setLayout(new GridLayout(1, 0));
      btnHolder.add(view);
      btnHolder.add(plot);
      btnHolder.add(edit);

      this.add(btnHolder);

      // create the viewer (only for a core sensor)
      if (_mySensor instanceof InitialSensor)
      {
        BaseSensorViewer cs = new SensorExcessViewer();
        cs.setObject(new WrappedSensor(_mySensor));
        this.add(cs);
      }

      view.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          viewThis(_mySensor);
        }
      });
      edit.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          editThis(_mySensor);
        }
      });
      plot.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          plotThis(_mySensor);
        }
      });

      // disable the edit button if the sensor's not editable
      edit.setEnabled(_mySensor.hasEditor());
    }

  }


}