package ASSET.Util.XML.Control.Observers;

import ASSET.Scenario.Observers.Recording.DebriefDeployableSensorLocationObserver;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian.Mayo
 * Date: 03-Sep-2003
 * Time: 09:55:35
 * Log:
 * $Log: DebriefDeployableSensorLocationObserverHandler.java,v $
 * Revision 1.1  2006/08/08 14:22:36  Ian.Mayo
 * Second import
 *
 * Revision 1.1  2006/08/07 12:26:46  Ian.Mayo
 * First versions
 *
 * Revision 1.6  2004/08/20 13:32:53  Ian.Mayo
 * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
 *
 * Revision 1.5  2004/08/19 16:15:30  Ian.Mayo
 * Refactor outputting file-related XML attributes, minor changes to tests.
 * <p/>
 * Revision 1.4  2004/08/12 11:09:32  Ian.Mayo
 * Respect observer classes refactored into tidy directories
 * <p/>
 * Revision 1.3  2004/05/24 16:11:22  Ian.Mayo
 * Commit updates from home
 * <p/>
 * Revision 1.2  2004/04/08 20:27:44  ian
 * Restructured contructor for CoreObserver
 * <p/>
 * Revision 1.1.1.1  2004/03/04 20:30:59  ian
 * no message
 * <p/>
 * Revision 1.2  2003/10/30 08:52:21  Ian.Mayo
 * Minor refactoring
 * <p/>
 * Revision 1.1  2003/09/03 14:01:02  Ian.Mayo
 * Initial implementation
 */
abstract public class DebriefDeployableSensorLocationObserverHandler extends DebriefReplayObserverHandler
{
  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////
  private final static String type = "DebriefDeployableSensorLocationObserver";


  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////
  public DebriefDeployableSensorLocationObserverHandler()
  {
    super(type);
  }

  /////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////


  protected DebriefReplayObserver getObserver(String name, boolean isActive)
  {
    return new DebriefDeployableSensorLocationObserver(_directory, _fileName, false, name, isActive);
  }


  //////////////////////////////////////////////////
  // testing
  //////////////////////////////////////////////////
  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(type);

    // get data item
    final DebriefDeployableSensorLocationObserver bb = (DebriefDeployableSensorLocationObserver) toExport;

    // output the parent ttributes
    CoreFileObserverHandler.exportThis(bb, thisPart);

    // output it's attributes
    parent.appendChild(thisPart);

  }
}
