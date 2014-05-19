package ASSET.Util.XML.Control;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ASSET.Scenario.Observers.ScenarioObserver;

import java.util.Vector;

abstract public class FactoryHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  private final static String type = "Factory";
  private final static String STARS = "Stars";
  private final static String GENES = "Genes";
  private final static String LOW_SCORES_HIGH = "LowScoresHigh";


  int _genes;
  int _stars;
  private Vector<ScenarioObserver> _myList;
  boolean _lowScoresHigh = false;

  public FactoryHandler()
  {
    // inform our parent what type of class we are
    super(type);


    addHandler(new ASSET.Util.XML.Control.Observers.ObserverListHandler()
    {
      public void setObserverList(final Vector<ScenarioObserver> list)
      {
        _myList = new Vector<ScenarioObserver>(list);
      }
    });

    addAttributeHandler(new HandleIntegerAttribute(GENES)
    {
      public void setValue(String name, final int value)
      {
        _genes = value;
      }
    });

    addAttributeHandler(new HandleBooleanAttribute(LOW_SCORES_HIGH)
    {
      public void setValue(String name, final boolean value)
      {
        _lowScoresHigh = value;
      }
    });
    addAttributeHandler(new HandleIntegerAttribute(STARS)
    {
      public void setValue(String name, final int value)
      {
        _stars = value;
      }
    });


  }


  public void elementClosed()
  {
    setFactory(_myList, _genes, _stars, _lowScoresHigh);

    _myList = null;
    _genes = -1;
    _stars = -1;
    _lowScoresHigh = false;
  }


  abstract public void setFactory(Vector<ScenarioObserver> list, int genes, int stars, boolean lowScoresHigh);


  public static void exportThis(final Vector<ScenarioObserver> list, final int stars, final int genes, final Element parent,
                                final Document doc)
  {
    // create ourselves
    final Element sens = doc.createElement(type);

    //
    sens.setAttribute(GENES, writeThis(genes));
    sens.setAttribute(STARS, writeThis(stars));

    // and the list
    ASSET.Util.XML.Control.Observers.ObserverListHandler.exportThis(list, sens, doc);

    parent.appendChild(sens);

  }


}