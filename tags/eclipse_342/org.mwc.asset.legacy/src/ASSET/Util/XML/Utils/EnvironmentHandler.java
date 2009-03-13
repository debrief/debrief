package ASSET.Util.XML.Utils;


/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import ASSET.Models.Environment.*;
import ASSET.Models.Sensor.Lookup.*;
import ASSET.Util.XML.Utils.LookupEnvironment.*;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;


abstract public class EnvironmentHandler extends MWCXMLReader
{

  private static String type = "Environment";
  public static final String NAME = "Name";
  
  int _seaState;
  String _atmos;
  String _light;
  String _myName;
  private static final String SEA_STATE = "SeaState";
  private static final String ATMOS_ATTEN = "AtmosphericAttenuation";
  private static final String LIGHT_LEVEL = "LightLevel";

  private static final String RADAR_LOOKUP = "RadarLookupEnvironment";
  private static final String OPTIC_LOOKUP = "VisualLookupEnvironment";
  private static final String MAD_LOOKUP = "MADLookupEnvironment";


  RadarLookupSensor.RadarEnvironment _radarEnv;
  OpticLookupSensor.OpticEnvironment _opticEnv;
  MADLookupSensor.MADEnvironment _madEnv;


  public EnvironmentHandler()
  {
    this(type);
  }
  
  

  public EnvironmentHandler(final String theType)
  {
    super(theType);

    addAttributeHandler(new HandleAttribute(NAME)
    {
      public void setValue(String name, final String val)
      {
        _myName = val;
      }
    });    
    addAttributeHandler(new HandleIntegerAttribute(SEA_STATE)
    {
      public void setValue(String name, int val)
      {
        _seaState = val;
      }
    });
    addAttributeHandler(new HandleAttribute(ATMOS_ATTEN)
    {
      public void setValue(String name, String val)
      {
        _atmos = val;
      }
    });
    addAttributeHandler(new HandleAttribute(LIGHT_LEVEL)
    {
      public void setValue(String name, String val)
      {
        _light = val;
      }
    });

    addHandler(new RadarLookupTableHandler(RADAR_LOOKUP)
    {
      public void setRadarEnvironment(RadarLookupSensor.RadarEnvironment env)
      {
        _radarEnv = env;
      }
    });

    addHandler(new OpticLookupTableHandler(OPTIC_LOOKUP)
    {
      public void setOpticEnvironment(OpticLookupSensor.OpticEnvironment env)
      {
        _opticEnv = env;
      }
    });

    addHandler(new MADLookupTableHandler(MAD_LOOKUP)
    {
      public void setMADEnvironment(MADLookupSensor.MADEnvironment env)
      {
        _madEnv = env;
      }
    });

  }


  public void elementClosed()
  {
    // get an integer value for the atmos
    EnvironmentType.AtmosphericAttenuationPropertyEditor atmosConverter
      = new EnvironmentType.AtmosphericAttenuationPropertyEditor();

    // and one for the light
    EnvironmentType.LightLevelPropertyEditor lightConverter
      = new EnvironmentType.LightLevelPropertyEditor();

    // and get the integer index
    atmosConverter.setAsText(_atmos);
    int atmosId = atmosConverter.getIndex();

    // and get the integer index
    lightConverter.setAsText(_light);
    int lightId = lightConverter.getIndex();

    // produce a value using these units
    CoreEnvironment newEnv = getNewEnvironment(atmosId, lightId);
    newEnv.setName(_myName);

    // also set our other values
    if (_radarEnv != null)
      newEnv.setRadarEnvironment(_radarEnv);

    if (_opticEnv != null)
      newEnv.setOpticEnvironment(_opticEnv);

    if (_madEnv != null)
      newEnv.setMADEnvironment(_madEnv);

    // and store it
    setEnvironment(newEnv);

    // and clear
    _atmos = null;
    _radarEnv = null;
    _opticEnv = null;
    _madEnv = null;
  }

  /**
   * overrideable getter method to get our environment
   *
   * @param atmosId
   * @param lightId
   * @return
   */
  protected SimpleEnvironment getNewEnvironment(int atmosId, int lightId)
  {
    return new SimpleEnvironment(atmosId, _seaState, lightId);
  }

  /**
   * send out the results data
   *
   * @param res
   */
  abstract public void setEnvironment(EnvironmentType res);


  /**
   * export this environment object
   *
   * @param environment
   * @param parent
   * @param doc
   */
  public static void exportEnvironment(SimpleEnvironment environment,
                                       org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {
    org.w3c.dom.Element envElement = doc.createElement(type);

    // set the attributes for this object
    setCoreEnvAttributes(environment, envElement, doc);

    parent.appendChild(envElement);
  }

  /**
   * set the attributes common to all core environments
   *
   * @param environment
   * @param envElement
   */
  protected static void setCoreEnvAttributes(SimpleEnvironment environment, org.w3c.dom.Element envElement,
                                             org.w3c.dom.Document doc)
  {
    // set the attributes
    // get an integer value for the atmos
    EnvironmentType.AtmosphericAttenuationPropertyEditor atmosConverter
      = new EnvironmentType.AtmosphericAttenuationPropertyEditor();

    EnvironmentType.LightLevelPropertyEditor lightConverter
      = new EnvironmentType.LightLevelPropertyEditor();

    // and get the integer index
    atmosConverter.setIndex(environment.getAtmosphericAttentuationFor(0, null));
    String atmosStr = atmosConverter.getAsText();

    // and get the integer index
    lightConverter.setIndex(environment.getLightLevelFor(0, null));
    String lightStr = lightConverter.getAsText();

    envElement.setAttribute(NAME, environment.getName());
    envElement.setAttribute(SEA_STATE, writeThis(environment.getSeaStateFor(0, null)));
    envElement.setAttribute(ATMOS_ATTEN, atmosStr);
    envElement.setAttribute(LIGHT_LEVEL, lightStr);

    
    // hey, we've also got to export our lookup tables
  RadarLookupSensor.RadarEnvironment radar = environment.getRadarEnvironment();
  if (radar != null)
  {
    RadarLookupTableHandler.exportThis(RADAR_LOOKUP, radar, envElement, doc);
    }
    
    OpticLookupSensor.OpticEnvironment optic = environment.getOpticEnvironment();
    if (optic != null)
    {
      OpticLookupTableHandler.exportThis(OPTIC_LOOKUP, optic, envElement, doc);
    }

   
    
    
  }

}