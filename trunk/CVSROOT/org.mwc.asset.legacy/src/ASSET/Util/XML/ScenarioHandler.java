package ASSET.Util.XML;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.ScenarioType;
import ASSET.Util.XML.Utils.EnvironmentHandler;
import MWC.GenericData.Duration;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;

import java.util.Date;

public class ScenarioHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  private ScenarioType _theScenario;

  static final public String SCENARIO_NAME = "Scenario";
  static final private String TIME = "StartTime";
  private static final String NAME_ATTRIBUTE = "Name";
  private static final String CASE_ATTRIBUTE = "Case";
  private static final String SCENARIO_STEP_TIME = "StepTime";
  private static final String SCENARIO_STEP_PAUSE = "StepPause";


  public ScenarioHandler(final ASSET.Scenario.CoreScenario theScenario)
  {
    // inform our parent what type of class we are
    super(SCENARIO_NAME);

    _theScenario = theScenario;

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

  }

  public static org.w3c.dom.Element exportScenario(final ScenarioType scenario, final org.w3c.dom.Document doc)
  {
    final org.w3c.dom.Element scen = doc.createElement(SCENARIO_NAME);
    scen.setAttribute("Created", new java.util.Date().toString());
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

    return scen;
  }


}