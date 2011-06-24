package ASSET.Util.XML.MonteCarlo;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */


import java.util.Date;

class MultiScenarioGeneratorHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  static final private String type = "MultiScenarioGenerator";

  public MultiScenarioGeneratorHandler()
  {
    // inform our parent what type of class we are
    super(type);
  }

  public void elementClosed()
  {
    super.elementClosed();

    //    ScenarioGenerator genny = new ScenarioGenerator(null, null);
    //    setGenerator(genny);
  }

  // abstract public void setGenerator(ScenarioGenerator genny);

  public static org.w3c.dom.Element exportScenario(final Object scenario,
                                                   final org.w3c.dom.Document doc)
  {
    final org.w3c.dom.Element scen = doc.createElement(type);
    scen.setAttribute("Created", new Date().toString());
    scen.setAttribute("Name", "ASSET Scenario");

    return scen;
  }


}