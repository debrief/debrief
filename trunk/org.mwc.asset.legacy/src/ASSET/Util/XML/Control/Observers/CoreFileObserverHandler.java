package ASSET.Util.XML.Control.Observers;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Scenario.Observers.ScenarioObserver;

abstract class CoreFileObserverHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{


  private final static String ACTIVE = "Active";
  private final static String DIR_NAME = "directory_name";
  private final static String FILE_NAME = "file_name";
  private final static String NAME = "Name";

  protected String _directory;
  protected String _fileName;
  protected boolean _isActive;
  protected String _name;

  public CoreFileObserverHandler(String type)
  {
    super(type);

    addAttributeHandler(new HandleBooleanAttribute(ACTIVE)
    {
      public void setValue(String name, final boolean val)
      {
        _isActive = val;
      }
    });


    addAttributeHandler(new HandleAttribute(DIR_NAME)
    {
      public void setValue(String name, final String val)
      {
        _directory = val;
      }
    });

    addAttributeHandler(new HandleAttribute(FILE_NAME)
    {
      public void setValue(String name, final String val)
      {
        // just check we haven't received a duff file-name
        if (val != "")
        {
          _fileName = val;
        }
      }
    });

    addAttributeHandler(new HandleAttribute(NAME)
    {
      public void setValue(String name, final String val)
      {
        _name = val;
      }
    });
  }

  public void elementClosed()
  {

    // and reset
    _name = null;
    _directory = null;
    _fileName = null;
  }


  /**
   * object is read in, somebody store it.
   *
   * @param obs
   */
  abstract public void setObserver(ScenarioObserver obs);


  /**
   * export this core file-related parameters
   *
   * @param toExport
   * @param thisPart
   */
  static public void exportThis(ScenarioObserver toExport,
                                final org.w3c.dom.Element thisPart)
  {
    thisPart.setAttribute(NAME, toExport.getName());
    thisPart.setAttribute(ACTIVE, writeThis(toExport.isActive()));

  }


}