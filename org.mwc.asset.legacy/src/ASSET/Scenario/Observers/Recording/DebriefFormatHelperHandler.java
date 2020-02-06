
package ASSET.Scenario.Observers.Recording;


/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class DebriefFormatHelperHandler extends MWCXMLReader
{
  public final static String type = "FormatHelper";
  public final static String TEXT = "Text";

  private String _text;

  public DebriefFormatHelperHandler()
  {
    // inform our parent what type of class we are
    super(type);

    addAttributeHandler(new HandleAttribute(TEXT)
    {
      @Override
      public void setValue(String name, String value)
      {
        _text = value;        
      }
    });
  }

  public void elementClosed()
  {
    storeMe(_text);
    _text = null;
  }

  abstract public void storeMe(String text);
}