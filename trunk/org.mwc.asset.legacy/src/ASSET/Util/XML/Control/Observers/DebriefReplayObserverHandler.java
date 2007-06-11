package ASSET.Util.XML.Control.Observers;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian.Mayo
 * Date: 03-Sep-2003
 * Time: 09:55:35
 * Log:
 *  $Log: DebriefReplayObserverHandler.java,v $
 *  Revision 1.1  2006/08/08 14:22:36  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:26:46  Ian.Mayo
 *  First versions
 *
 *  Revision 1.11  2004/08/20 13:32:54  Ian.Mayo
 *  Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
 *
 *  Revision 1.10  2004/08/19 16:15:32  Ian.Mayo
 *  Refactor outputting file-related XML attributes, minor changes to tests.
 *
 *  Revision 1.9  2004/08/12 11:09:33  Ian.Mayo
 *  Respect observer classes refactored into tidy directories
 *
 *  Revision 1.8  2004/08/10 08:50:13  Ian.Mayo
 *  Change functionality of Debrief replay observer so that it can record decisions & detections aswell.  Also include ability to track particular type of target
 *
 *  Revision 1.7  2004/05/24 16:11:21  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.2  2004/04/08 20:27:44  ian
 *  Restructured contructor for CoreObserver
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:59  ian
 *  no message
 *
 *  Revision 1.6  2004/02/18 09:27:50  Ian.Mayo
 *  Add record_detections attribute
 *
 *  Revision 1.5  2003/10/30 08:52:34  Ian.Mayo
 *  Minor refactoring
 *
 *  Revision 1.4  2003/09/03 14:04:06  Ian.Mayo
 *  Refactor to help getting over-ridden
 *
 *
 */

import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.Decisions.Util.TargetTypeHandler;


/**
 * read in a debrief replay observer from file
 */
abstract class DebriefReplayObserverHandler extends CoreFileObserverHandler
{

  private final static String type = "DebriefReplayObserver";


  private boolean _recordDetections = false;
  private boolean _recordPositions = false;
  private boolean _recordDecisions = false;
  private TargetType _targetType = null;


  private static final String RECORD_DETECTIONS = "record_detections";
  private static final String RECORD_DECISIONS = "record_decisions";
  private static final String RECORD_POSITIONS = "record_positions";
  private static final String TARGET_TYPE = "SubjectToTrack";


  public DebriefReplayObserverHandler(String type)
  {
    super(type);


    addAttributeHandler(new HandleBooleanAttribute(RECORD_DETECTIONS)
    {
      public void setValue(String name, final boolean val)
      {
        _recordDetections = val;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(RECORD_DECISIONS)
    {
      public void setValue(String name, final boolean val)
      {
        _recordDecisions = val;
      }
    });
    addAttributeHandler(new HandleBooleanAttribute(RECORD_POSITIONS)
    {
      public void setValue(String name, final boolean val)
      {
        _recordPositions = val;
      }
    });

    addHandler(new TargetTypeHandler(TARGET_TYPE)
    {
      public void setTargetType(TargetType type)
      {
        _targetType = type;
      }
    });

  }

  public DebriefReplayObserverHandler()
  {
    this(type);
  }

  public void elementClosed()
  {
    // create ourselves
    final DebriefReplayObserver debriefObserver = getObserver(_name, _isActive, _recordDetections,
                                                              _recordDecisions, _recordPositions, _targetType);

    setObserver(debriefObserver);

    // close the parenet
    super.elementClosed();

    // and clear the data
    _recordDetections = false;
    _recordDecisions = false;
    _recordPositions = true;
    _targetType = null;

  }

  protected DebriefReplayObserver getObserver(String name, boolean isActive, boolean recordDetections,
                                              boolean recordDecisions, boolean recordPositions, TargetType subject)
  {
    return new DebriefReplayObserver(_directory, _fileName, recordDetections, recordDecisions, recordPositions, subject, name, isActive);
  }


  abstract public void setObserver(ScenarioObserver obs);

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final DebriefReplayObserver bb = (DebriefReplayObserver) toExport;

    // output the parent ttributes
    CoreFileObserverHandler.exportThis(bb, thisPart);

    // output it's attributes
    thisPart.setAttribute(RECORD_DETECTIONS, writeThis(bb.getRecordDetections()));
    thisPart.setAttribute(RECORD_DECISIONS, writeThis(bb.getRecordDecisions()));
    thisPart.setAttribute(RECORD_POSITIONS, writeThis(bb.getRecordPositions()));
    if (bb.getSubjectToTrack() != null)
    {
      TargetTypeHandler.exportThis(TARGET_TYPE, bb.getSubjectToTrack(), thisPart, doc);
    }

    // output it's attributes
    parent.appendChild(thisPart);

  }


}