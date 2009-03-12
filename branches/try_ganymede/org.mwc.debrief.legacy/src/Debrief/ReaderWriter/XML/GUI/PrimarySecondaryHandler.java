package Debrief.ReaderWriter.XML.GUI;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */





abstract public class PrimarySecondaryHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  String _name;

  public PrimarySecondaryHandler(String type)
  {
    // inform our parent what type of class we are
    super(type);

    addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(String name, String value)
      {
        _name = value;
      }
    });

  }

  public final void elementClosed()
  {
    setTrack(_name);

    // reset our variables
    _name = null;
  }

  // pass on to the parent the name of this track
  abstract public void setTrack(String name);
}