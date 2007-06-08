package ASSET.Util.XML.Decisions.Responses;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Responses.ChangeSensorLineUp;
import ASSET.Models.Decision.Responses.Response;
import ASSET.Models.Environment.EnvironmentType;

public class ChangeSensorLineUpHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
  {

  private final static String type = "ChangeSensorLineUp";

  private final static String MEDIUM = "Medium";
  private final static String SWITCH_ON = "SwitchOn";

  private int _medium;
  private String _name;
  private boolean _switchOn;

  public static EnvironmentType.MediumPropertyEditor _myEditor =
    new EnvironmentType.MediumPropertyEditor();


  public ChangeSensorLineUpHandler()
  {
    super("ChangeSensorLineUp");

    addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(String name, final String val)
      {
        _name = val;
      }
    });

    addAttributeHandler(new HandleAttribute(MEDIUM)
    {
      public void setValue(String name, final String val)
      {
        _myEditor.setValue(val);
        _medium = _myEditor.getIndex();
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(SWITCH_ON)
    {
      public void setValue(String name, final boolean val)
      {
        _switchOn = val;
      }
    });

  }


  public void elementClosed()
  {
    final Response ml = new ASSET.Models.Decision.Responses.ChangeSensorLineUp(_medium, _switchOn);
    ml.setName(_name);

    // finally output it
    setResponse(ml);

    // and reset
    _name = null;
  }

  public void setResponse(Response dec)
  {
  };

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final ChangeSensorLineUp bb = (ChangeSensorLineUp) toExport;

    // output it's attributes
    thisPart.setAttribute("Name", bb.getName());
    thisPart.setAttribute(SWITCH_ON, writeThis(bb.getSwitchOn()));

    _myEditor.setIndex(bb.getMedium());
    thisPart.setAttribute(MEDIUM, _myEditor.getAsText());

    parent.appendChild(thisPart);

  }


}