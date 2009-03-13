package ASSET.Util.XML.Decisions;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.Tactical.LaunchWeapon;
import ASSET.Models.Decision.TargetType;
import ASSET.Util.XML.Decisions.Tactical.CoreDecisionHandler;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;

abstract class LaunchWeaponHandler extends CoreDecisionHandler
  {

  private final static String type = "LaunchWeapon";
  private final static String TYPE = "LaunchType";
  private final static String TYPE_FILENAME = "LaunchTypeFileName";

  WorldDistance _launchRange;
  Duration _launchTime;
  String _launchType;
  String _fileName;
  TargetType _myTargetType;


  public LaunchWeaponHandler()
  {
    super(type);
    addAttributeHandler(new HandleAttribute(TYPE_FILENAME)
    {
      public void setValue(String name, final String val)
      {
        // store the data
        _fileName = val;
        _launchType = LaunchWeapon.readWeaponFromThisFile(val);
      }
    });
    addAttributeHandler(new HandleAttribute(TYPE)
    {
      public void setValue(String name, final String val)
      {
        // so, before we learn to read in the actual type,
        // set it to the hard-coded value used for testing launch

        _launchType = val;
      }
    });

    addHandler(new WorldDistanceHandler()
    {
      public void setWorldDistance(WorldDistance res)
      {
        _launchRange = res;
      }
    });

    addHandler(new ASSET.Util.XML.Decisions.Util.TargetTypeHandler()
    {
      public void setTargetType(final TargetType type)
      {
        _myTargetType = type;
      }
    });
    addHandler(new DurationHandler()
    {
      public void setDuration(Duration res)
      {
        _launchTime = res;
      }
    });

  }

  public void elementClosed()
  {
    final LaunchWeapon lnch = new LaunchWeapon();

    super.setAttributes(lnch);

    lnch.setCoolOffTime(_launchTime);
    lnch.setLaunchRange(_launchRange);
    lnch.setTargetType(_myTargetType);
    lnch.setLaunchType(_launchType);
    if (_fileName != null)
      lnch.setLaunchFilename(_fileName);

    // finally output it
    setModel(lnch);

    // clear the data
    _myTargetType = null;
    _fileName = null;
    _launchType = null;
    _launchTime = null;
  }

  abstract public void setModel(ASSET.Models.DecisionType dec);


  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final LaunchWeapon bb = (LaunchWeapon) toExport;

    // first the parent export
    CoreDecisionHandler.exportThis(bb, thisPart, doc);

    // output it's attributes
    // is there a filename?
    final String fName = bb.getLaunchFilename();
    if (fName != null)
    {
      try
      {

        // write the details to file
        final java.io.FileWriter fw = new java.io.FileWriter(fName);
        final java.io.BufferedWriter bw = new java.io.BufferedWriter(fw);
        bw.write(bb.getLaunchType());

        // insert the filename
        thisPart.setAttribute(TYPE_FILENAME, fName);

      }
      catch (java.io.IOException ee)
      {
        ee.printStackTrace();
      }

    }
    else
    {
      // insert the launch details
      // DON't INSERT THE DETAILS
    }
    ASSET.Util.XML.Decisions.Util.TargetTypeHandler.exportThis(bb.getTargetType(), thisPart, doc);
    WorldDistanceHandler.exportDistance(bb.getLaunchRange(), thisPart, doc);
    DurationHandler.exportDuration(bb.getCoolOffTime(), thisPart, doc);

    parent.appendChild(thisPart);

  }


}