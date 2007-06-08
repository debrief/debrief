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

import java.util.Vector;

abstract public class StandaloneObserverListHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  private final static String type = "StandaloneObserverList";

  private Vector _myList;

  public StandaloneObserverListHandler()
  {
    // inform our parent what type of class we are
    super(type);


    addHandler(new ASSET.Util.XML.Control.Observers.ObserverListHandler()
    {
      public void setObserverList(final Vector list)
      {
        _myList = new Vector(list);
      }
    });
  }


  public void elementClosed()
  {
    setObserverList(_myList);

    _myList = null;
  }


  abstract public void setObserverList(Vector list);


  public static void exportThis(final Vector list, final Element parent, final Document doc)
  {
    // create ourselves
    final Element sens = doc.createElement(type);

    // and the list
    ASSET.Util.XML.Control.Observers.ObserverListHandler.exportThis(list, sens, doc);

    parent.appendChild(sens);

  }


}