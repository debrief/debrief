
package ASSET.Util.XML.Control.Observers;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import ASSET.Models.Decision.TargetType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.Observers.Recording.BMRObserver;
import ASSET.Scenario.Observers.Recording.DebriefFormatHelperHandler;
import ASSET.Util.XML.Decisions.Util.TargetTypeHandler;

/**
 * read in a debrief replay observer from file
 */
abstract class BMRObserverHandler extends CoreFileObserverHandler
{

  private final static String type = "BMRObserver";

  private TargetType _targetType = null;
  final private List<String> _formatHelpers = new ArrayList<String>();
  private String _subjectSensor = null;

  private static final String TARGET_TYPE = "SubjectToTrack";
  private static final String SUBJECT_SENSOR = "SubjectSensor";

  public BMRObserverHandler(String type)
  {
    super(type);

    addAttributeHandler(new HandleAttribute(SUBJECT_SENSOR)
    {
      public void setValue(String name, final String val)
      {
        _subjectSensor = val;
      }
    });

    addHandler(new TargetTypeHandler(TARGET_TYPE)
    {
      public void setTargetType(TargetType type1)
      {
        _targetType = type1;
      }
    });
    addHandler(new DebriefFormatHelperHandler()
    {
      @Override
      public void storeMe(final String text)
      {
        _formatHelpers.add(text);
      }
    });
  }

  public BMRObserverHandler()
  {
    this(type);
  }

  public void elementClosed()
  {
    // create ourselves
    final BMRObserver debriefObserver = getObserver(_name, _isActive,
        _targetType, _formatHelpers, _subjectSensor);

    setObserver(debriefObserver);

    // close the parent
    super.elementClosed();

    // and clear the data
    _targetType = null;
    _subjectSensor = null;

    // and clear the format helpers
    _formatHelpers.clear();
  }

  protected BMRObserver getObserver(String name, boolean isActive,
      TargetType subject, List<String> formatHelpers,
      final String subjectSensor)
  {
    return new BMRObserver(_directory, _fileName, subject, name, isActive,
        subjectSensor);
  }

  abstract public void setObserver(ScenarioObserver obs);

  static public void exportThis(final Object toExport, final Element parent,
      final org.w3c.dom.Document doc)
  {
    // create ourselves
    final Element thisPart = doc.createElement(type);

    // get data item
    final BMRObserver bb = (BMRObserver) toExport;

    // output the parent ttributes
    CoreFileObserverHandler.exportThis(bb, thisPart);

    if (bb.getSubjectToTrack() != null)
    {
      TargetTypeHandler.exportThis(TARGET_TYPE, bb.getSubjectToTrack(),
          thisPart, doc);
    }

    // output it's attributes
    parent.appendChild(thisPart);

  }

}