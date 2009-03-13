package Debrief.ReaderWriter.XML;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.awt.Color;

import org.w3c.dom.Element;

import Debrief.GUI.Views.*;
import Debrief.ReaderWriter.XML.GUI.*;


public final class GUIHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  final Debrief.GUI.Frames.Session _session;
  private Debrief.GUI.Views.AnalysisView _analysisView;

  static private final java.util.Hashtable<String, StepperHandler> _myCreators = new java.util.Hashtable<String, StepperHandler>();

  static private StepperHandler _myStepperHandler;

  public GUIHandler(Debrief.GUI.Frames.Session session)
  {
    // inform our parent what type of class we are
    super("gui");

    _session = session;

    Debrief.GUI.Tote.AnalysisTote _theTote = null;
    Debrief.GUI.Views.PlainView pv = _session.getCurrentView();
    if(pv instanceof Debrief.GUI.Views.AnalysisView)
    {
      _analysisView = (Debrief.GUI.Views.AnalysisView) pv;
      _theTote = _analysisView.getTote();
    }

    MWC.GUI.Layers _theData = _session.getData();

    addHandler(new ToteHandler(_theTote, _theData));
    addHandler(new ComponentHandler(){
      public void addComponent(GUIHandler.ComponentDetails details)
      {
        addThisComponent(details);
      }
    });
    addHandler(new BackgroundHandler()
    {
      public void setBackgroundColor(Color theColor)
      {
        PlainView pv1 = _session.getCurrentView();
        if(pv1 instanceof AnalysisView)
        {
          AnalysisView av = (AnalysisView)pv1;
          av.getChart().getCanvas().setBackgroundColor(theColor);
        }
      }
    });

    // collate our list of exporters
    if(_myStepperHandler == null)
      _myStepperHandler = new StepperHandler();

    _myCreators.put("Stepper", _myStepperHandler);
  }

  void addThisComponent(ComponentDetails details)
  {
    // sort out this component
    String cType = details.type;

    ComponentCreator cc = _myCreators.get(cType);
    if(cc != null)
    {
      cc.makeThis(details, _analysisView);
    }
    else
      MWC.Utilities.Errors.Trace.trace("XML Handler not found for " + cType);
  }

  static public final class ComponentDetails
  {
    public final java.util.Hashtable<String, String> properties = new java.util.Hashtable<String, String>();
    public String type = null;
    public final void addProperty(String name, String val)
    {
      properties.put(name, val);
    }
    public final void exportTo(String title, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
    {
      Element comp = doc.createElement("component");
      comp.setAttribute("Type", title);
      java.util.Enumeration<String> iter = properties.keys();
      while(iter.hasMoreElements())
      {
        String thisK = iter.nextElement();
        String value = properties.get(thisK);
        MWC.Utilities.ReaderWriter.XML.Util.PropertyHandler.exportProperty(thisK, value, comp, doc);
      }

      parent.appendChild(comp);
    }
  }

  public static  interface ComponentCreator
  {
    public void makeThis(ComponentDetails details, Debrief.GUI.Views.AnalysisView view);
  }

  /////////////////////////////////////////////////////////////////////////
  // the constructors for our components
  /////////////////////////////////////////////////////////////////////////

  public static void exportThis(Debrief.GUI.Frames.Session session, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {
    // create ourselves
    Element gui = doc.createElement("gui");

    ////////////////////////////////////////////////
    // first the tote
    ////////////////////////////////////////////////
    ToteHandler.exportTote(session, gui, doc);

    // try to export the other features

    // check the stepper handler
    if(_myStepperHandler == null)
      _myStepperHandler = new StepperHandler();
    ComponentDetails stepperD = _myStepperHandler.exportThis(session);
    stepperD.exportTo("Stepper", gui, doc);

    PlainView pv = session.getCurrentView();
    if(pv instanceof AnalysisView)
    {
      AnalysisView av = (AnalysisView)pv;
      Color col = av.getChart().getCanvas().getBackgroundColor();
      BackgroundHandler.exportThis(col, gui, doc);
    }

    parent.appendChild(gui);
  }

}