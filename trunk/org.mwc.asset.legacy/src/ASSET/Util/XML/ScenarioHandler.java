package ASSET.Util.XML;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.text.SimpleDateFormat;
import java.util.Date;

import ASSET.ScenarioType;
import ASSET.Models.Environment.*;
import ASSET.Util.XML.Utils.*;
import MWC.GUI.*;
import MWC.GenericData.Duration;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;

public class ScenarioHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  private static final String DEBRIEF_LAYER_NAME = "DebriefLayer";

	ScenarioType _theScenario;
	Layers _myLayers = null;

  static final public String SCENARIO_NAME = "Scenario";
  static final private String TIME = "StartTime";
  private static final String NAME_ATTRIBUTE = "Name";
  private static final String CASE_ATTRIBUTE = "Case";
  private static final String SCENARIO_STEP_TIME = "StepTime";
  private static final String SCENARIO_STEP_PAUSE = "StepPause";


  public ScenarioHandler(final ASSET.Scenario.CoreScenario theScenario)
  {
  	this(theScenario, null);
  }

  public ScenarioHandler(final ASSET.Scenario.CoreScenario theScenario, Layers theLayers)
  {
    // inform our parent what type of class we are
    super(SCENARIO_NAME);

    _theScenario = theScenario;
    _myLayers = theLayers;

    // sort out the handlers
    addAttributeHandler(new HandleDateTimeAttribute(TIME)
    {
      public void setValue(String name, final long val)
      {
        _theScenario.setTime(val);
      }
    });
    super.addAttributeHandler(new HandleAttribute(NAME_ATTRIBUTE)
    {
      public void setValue(String name, final String val)
      {
        // store the name
        _theScenario.setName(val);
      }
    });
    super.addAttributeHandler(new HandleAttribute(CASE_ATTRIBUTE)
    {
      public void setValue(String name, final String val)
      {
        // store the name
        _theScenario.setCaseId(val);
      }
    });

    // does the scenario have it's scenario object?
    addHandler(new ParticipantsHandler(theScenario));


    addHandler(new EnvironmentHandler()
    {
      public void setEnvironment(EnvironmentType theEnv)
      {
        _theScenario.setEnvironment(theEnv);
      }
    });

    addHandler(new DurationHandler(SCENARIO_STEP_TIME)
    {
      public void setDuration(Duration res)
      {
        _theScenario.setScenarioStepTime((int) res.getMillis());
      }
    });
    addHandler(new DurationHandler(SCENARIO_STEP_PAUSE)
    {
      public void setDuration(Duration res)
      {
        _theScenario.setStepTime((int) res.getMillis());
      }
    });
    addHandler(new MockLayerHandler(DEBRIEF_LAYER_NAME)
    {
      public void setLayer(BaseLayer theLayer)
      {
      	if(_myLayers != null)
      		_myLayers.addThisLayer(theLayer);
      }
    });
    
  }

  public static org.w3c.dom.Element exportScenario(final ScenarioType scenario, Layer theDecorations, final org.w3c.dom.Document doc)
  {
    final org.w3c.dom.Element scen = doc.createElement(SCENARIO_NAME);
    SimpleDateFormat xmlFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    xmlFormatter.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
    scen.setAttribute("Created",xmlFormatter.format(new java.util.Date()));
    scen.setAttribute(NAME_ATTRIBUTE, "ASSET Scenario");
    scen.setAttribute(TIME, writeThisInXML(new Date(scenario.getTime())));   
    
    DurationHandler.exportDuration(SCENARIO_STEP_TIME, new Duration(scenario.getScenarioStepTime(), Duration.MILLISECONDS), scen, doc);
    DurationHandler.exportDuration(SCENARIO_STEP_PAUSE, new Duration(scenario.getStepTime(), Duration.MILLISECONDS), scen, doc);

    if (scenario.getCaseId() != null)
      scen.setAttribute(CASE_ATTRIBUTE, scenario.getCaseId());

    EnvironmentType env = scenario.getEnvironment();
    if (env instanceof SimpleEnvironment)
      EnvironmentHandler.exportEnvironment((SimpleEnvironment) env, scen, doc);

    ParticipantsHandler.exportThis(scenario, scen, doc);
    
    // and now the graphic layers item
    final org.w3c.dom.Element layerHolder = doc.createElement(DEBRIEF_LAYER_NAME);
    scen.appendChild(layerHolder);
    Layer backdropLayer = scenario.getBackdrop();
    if(backdropLayer != null)
    	LayerHandler.exportLayer((BaseLayer) scenario.getBackdrop(), layerHolder, doc);

    return scen;
  }


}