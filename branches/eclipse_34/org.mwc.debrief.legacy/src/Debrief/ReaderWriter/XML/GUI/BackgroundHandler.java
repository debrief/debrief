package Debrief.ReaderWriter.XML.GUI;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.awt.Color;

import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;




abstract public class BackgroundHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  private static final String MY_TYPE = "Background";

  private Color _theColor;

  public BackgroundHandler()
  {
    // inform our parent what type of class we are
    super(MY_TYPE);

    addHandler(new ColourHandler()
    {
      public void setColour(Color res)
      {
        _theColor = res;
      }
    });

  }



  public final void elementClosed()
  {
    // pass on to the listener class
    setBackgroundColor(_theColor);
  }

  abstract public void setBackgroundColor(Color theColor);


  public static void exportThis(Color color, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {
    // create the element to put it in
    org.w3c.dom.Element tote = doc.createElement(MY_TYPE);
    ColourHandler.exportColour(color, tote, doc);

    //////////////////////////////
    // and finally add ourselves to the parent
    parent.appendChild(tote);
  }

}