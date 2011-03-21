package ASSET.Util.XML.Vessels;

import ASSET.Models.Vessels.SonarBuoyField;
import MWC.GenericData.WorldArea;
import MWC.Utilities.ReaderWriter.XML.Util.WorldAreaHandler;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

abstract public class BuoyFieldHandler extends ParticipantHandler{

  static private final String myType = "BuoyField";
  static private final String TYPE_NAME = "Coverage";
  
	protected WorldArea _coverage;

  public BuoyFieldHandler() {
    super(myType);

    // add handlers for the SU properties
    // none in this instance

    addHandler(new WorldAreaHandler(TYPE_NAME)
    {
			@Override
			public void setArea(WorldArea area)
			{
				_coverage = area;
			}
    });

  }

  protected ASSET.ParticipantType getParticipant(final int index)
  {
    final SonarBuoyField thisField = new SonarBuoyField(index, _coverage);
    
    _coverage = null;
    
    return thisField;
  }

  static public void exportThis(final Object toExport,final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
      // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(myType);

    ParticipantHandler.exportThis(toExport, thisPart, doc);

    SonarBuoyField field = (SonarBuoyField) toExport;
    WorldArea coverage = field.getCoverage();
    WorldAreaHandler.exportThis(coverage, thisPart, doc, TYPE_NAME);

    parent.appendChild(thisPart);

  }
}