package ASSET.Util.XML.Decisions;

import ASSET.Models.Decision.Movement.Transit;
import ASSET.Models.Decision.Movement.WorkingTransit;
import ASSET.Models.Decision.Sequence;
import ASSET.Models.DecisionType;

/**
 * Created by IntelliJ IDEA.
 * User: ian.mayo
 * Date: 29-Oct-2004
 * Time: 13:10:14
 * To change this template use File | Settings | File Templates.
 */
abstract public class WorkingTransitHandler extends TransitHandler
{

  private final static String _myType = "WorkingTransit";
  private final static String NUM_STOPS = "NumStops";
  private final static String ACTIVITY = "Activity";

  Sequence _myActivity;
  int _numStops=-1;

  public WorkingTransitHandler(int thisDepth)
  {
    super(_myType);

    // add our handlers
    addHandler(new SequenceHandler(ACTIVITY, thisDepth){
      public void setModel(DecisionType dec)
      {
        _myActivity = (Sequence) dec;
      }
    });

    addAttributeHandler(new HandleIntegerAttribute(NUM_STOPS){
      public void setValue(String name, int value)
      {
        _numStops = value;
      }
    });
  }

  protected Transit getBehaviour()
  {
    Transit res = new WorkingTransit(_myActivity, super._myPath, super._speed, super._looping, _numStops);
    _myActivity = null;
    _numStops = -1;
    return res;
  }

  static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
                                final org.w3c.dom.Document doc)
  {
    // create ourselves
    final org.w3c.dom.Element thisPart = doc.createElement(_myType);

    // get data item
    final ASSET.Models.Decision.Movement.WorkingTransit bb = (ASSET.Models.Decision.Movement.WorkingTransit) toExport;

    exportCoreAttributes(bb, thisPart, doc);

    // and our bits
    SequenceHandler.exportSequence(ACTIVITY, bb.getWorkingActivity(), thisPart, doc);
    thisPart.setAttribute(NUM_STOPS, writeThis(bb.getNumStops()));

    parent.appendChild(thisPart);

  }



}
