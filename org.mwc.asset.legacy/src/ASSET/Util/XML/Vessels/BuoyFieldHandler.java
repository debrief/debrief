package ASSET.Util.XML.Vessels;

import ASSET.Models.Vessels.SonarBuoyField;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.Utilities.ReaderWriter.XML.Util.WorldAreaHandler;
import MWC.Utilities.ReaderWriter.XML.Util.XMLTimeRangeHandler;

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
  static private final String COVER_NAME = "Coverage";
  static private final String PERIOD_NAME = "TimePeriod";
  
	protected WorldArea _coverage;
	protected TimePeriod _period;

  public BuoyFieldHandler() {
    super(myType);

    // add handlers for the SU properties
    // none in this instance

    addHandler(new WorldAreaHandler(COVER_NAME)
    {
			@Override
			public void setArea(WorldArea area)
			{
				_coverage = area;
			}
    });
    
    addHandler(new XMLTimeRangeHandler(PERIOD_NAME)
		{
			@Override
			public void setTimeRange(HiResDate start, HiResDate end)
			{
				_period = new TimePeriod.BaseTimePeriod(start, end);
			}
		});

  }

  protected ASSET.ParticipantType getParticipant(final int index)
  {
    final SonarBuoyField thisField = new SonarBuoyField(index, _coverage);
    
    // do we have a period?
    if(_period != null)
    	thisField.setTimePeriod(_period);
    
    _coverage = null;
    _period = null;
    
    return thisField;
  }

  static public void exportThis(final Object toExport,final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
      // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(myType);

    ParticipantHandler.exportThis(toExport, thisPart, doc);

    SonarBuoyField field = (SonarBuoyField) toExport;
    WorldArea coverage = field.getCoverage();
    WorldAreaHandler.exportThis(coverage, thisPart, doc, COVER_NAME);
    
    TimePeriod period = field.getTimePeriod();
    if(period != null)
    	XMLTimeRangeHandler.exportThis(period.getStartDTG(), period.getEndDTG(), thisPart, doc, PERIOD_NAME);

    parent.appendChild(thisPart);

  }
}