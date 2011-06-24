package ASSET.Util.XML.Control.Observers;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian.Mayo
 * Date: 03-Sep-2003
 * Time: 09:55:35
 * Log:
 *  $Log: ObserverListHandler.java,v $
 *  Revision 1.1  2006/08/08 14:22:37  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:26:47  Ian.Mayo
 *  First versions
 *
 *  Revision 1.14  2004/08/20 13:32:58  Ian.Mayo
 *  Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
 *
 *  Revision 1.13  2004/08/18 15:45:31  Ian.Mayo
 *  Tidying some batch store algorithms, observer which can record Final State
 *
 *  Revision 1.12  2004/08/18 10:42:59  Ian.Mayo
 *  Finish StopOnProximityDetectionObserver
 *
 *  Revision 1.11  2004/08/17 10:41:48  Ian.Mayo
 *  Remove DetectionAchieved observer, since it's processing is now covered by the DetectionObserver (which includes optional batch processing)
 *
 *  Revision 1.10  2004/08/16 12:40:09  Ian.Mayo
 *  Batch collation changes
 *
 *  Revision 1.9  2004/08/13 09:54:06  Ian.Mayo
 *  More batch collation implementation
 *
 *  Revision 1.8  2004/08/12 11:09:34  Ian.Mayo
 *  Respect observer classes refactored into tidy directories
 *
 *  Revision 1.7  2004/08/10 14:00:36  Ian.Mayo
 *  Ditch the narrative observer, it was absorbed into Debrief Replay observer
 *
 *  Revision 1.6  2004/05/24 15:18:58  Ian.Mayo
 *  Commit changes conducted at home
 *
 *  Revision 1.3  2004/05/05 20:00:05  ian
 *  Include narrative observer handler
 *
 *  Revision 1.2  2004/04/03 21:54:48  ian
 *  Add new handler
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:59  ian
 *  no message
 *
 *  Revision 1.5  2003/11/07 10:32:32  Ian.Mayo
 *  Add new observer (detection achieved)
 *
 *  Revision 1.4  2003/09/03 14:11:00  Ian.Mayo
 *  Tidying, add new observer (record dipping sensor location to file)
 *
 *
 *
 */

import ASSET.GUI.SuperSearch.Observers.ProportionDetectedObserver;
import ASSET.GUI.SuperSearch.Observers.RemoveDetectedObserver;
import ASSET.Scenario.Observers.*;
import ASSET.Scenario.Observers.Plotting.PlotDetectionStatusObserver;
import ASSET.Scenario.Observers.Plotting.PlotInvestigationSubjectObserver;
import ASSET.Scenario.Observers.Plotting.PlotSensorObserver;
import ASSET.Scenario.Observers.Recording.CSVTrackObserver;
import ASSET.Scenario.Observers.Recording.DebriefDeployableSensorLocationObserver;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import ASSET.Scenario.Observers.Recording.RecordStatusToDBObserverType;
import ASSET.Scenario.Observers.Summary.BatchCollator;
import ASSET.Scenario.Observers.Summary.ElapsedTimeObserver;
import ASSET.Scenario.Observers.Summary.FinalStateObserver;
import ASSET.Scenario.Observers.Summary.TimeToLaunchObserver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Iterator;
import java.util.Vector;

/**
 * read in a list of observers from file
 */
abstract public class ObserverListHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  private final static String type = "ObserverList";

  protected Vector<ScenarioObserver> _myList;

  public ObserverListHandler()
  {
    // inform our parent what type of class we are
    super(type);

    _myList = new Vector<ScenarioObserver>(0, 1);

    addHandler(new TrackPlotObserverHandler()
    {
      public void setObserver(final ScenarioObserver obs)
      {
        _myList.add(obs);
      }
    });

    addHandler(new ElapsedTimeObserverHandler()
    {
      public void setObserver(final ScenarioObserver obs)
      {
        _myList.add(obs);
      }
    });

    addHandler(new FinalStateObserverHandler()
    {
      public void setObserver(final ScenarioObserver obs)
      {
        _myList.add(obs);
      }
    });

    addHandler(new DebriefReplayObserverHandler()
    {
      public void setObserver(final ScenarioObserver obs)
      {
        _myList.add(obs);
      }
    });
    
    addHandler(new RecordToDatabaseObserverHandler()
    {
      public void setObserver(final ScenarioObserver obs)
      {
        _myList.add(obs);
      }
    });

    addHandler(new DebriefDeployableSensorLocationObserverHandler()
    {
      public void setObserver(final ScenarioObserver obs)
      {
        _myList.add(obs);
      }
    });

    addHandler(new CSVTrackObserverHandler()
    {
      public void setObserver(final ScenarioObserver obs)
      {
        _myList.add(obs);
      }
    });
    addHandler(new PlotSensorObserverHandler()
    {
      public void setObserver(final ScenarioObserver obs)
      {
        _myList.add(obs);
      }
    });
    addHandler(new PlotInvestigationSubjectObserverHandler()
		{
	      public void setObserver(final ScenarioObserver obs)
      {
        _myList.add(obs);
      }
    });
    addHandler(new PlotDetectionStatusObserverHandler()
    {
      public void setObserver(final ScenarioObserver obs)
      {
        _myList.add(obs);
      }
    });

    addHandler(new DetectionObserverHandler()
    {
      public void setObserver(final ScenarioObserver obs)
      {
        _myList.add(obs);
      }
    });


    addHandler(new StopOnDetectionObserverHandler()
    {
      public void setObserver(final ScenarioObserver obs)
      {
        _myList.add(obs);
      }
    });

    addHandler(new StopOnProximityDetectionObserverHandler()
    {
      public void setObserver(final ScenarioObserver obs)
      {
        _myList.add(obs);
      }
    });


    addHandler(new RemoveInAreaObserverHandler()
    {
			@Override
			public void setObserver(final BatchCollator obs)
			{
        _myList.add(obs);
			}
    });

    addHandler(new ProportionDetectedObserverHandler()
    {
      public void setObserver(final ScenarioObserver obs)
      {
        _myList.add(obs);
      }
    });

    addHandler(new RemoveDetectedObserverHandler()
    {
      public void setObserver(final ScenarioObserver obs)
      {
        _myList.add(obs);
      }
    });

    addHandler(new ProximityObserverHandler.StopOnProximityHandler()
    {
      public void setObserver(final BatchCollator obs)
      {
        _myList.add(obs);
      }
    });

    addHandler(new ProximityObserverHandler()
    {
      public void setObserver(final BatchCollator obs)
      {
        _myList.add(obs);
      }
    });

    addHandler(new TimeObserverHandler()
    {
      public void setObserver(final ScenarioObserver obs)
      {
        _myList.add(obs);
      }
    });


    addHandler(new TimeToLaunchObserverHandler()
    {
      public void setObserver(final ScenarioObserver obs)
      {
        _myList.add(obs);
      }
    });

  }


  public void elementClosed()
  {
    setObserverList(_myList);

    // forget our reference, don't just clear the list - it may not get used just yet.
    _myList = null;
  }


  abstract public void setObserverList(Vector<ScenarioObserver> list);


  public static void exportThis(final Vector<ScenarioObserver> list, final Element parent, final Document doc)
  {
    // create ourselves
    final Element sens = doc.createElement(type);

    // step through data
    final Iterator<ScenarioObserver> it = list.iterator();
    while (it.hasNext())
    {
      final ScenarioObserver observer = (ScenarioObserver) it.next();
      if (observer instanceof DebriefDeployableSensorLocationObserver)
      {
        DebriefDeployableSensorLocationObserverHandler.exportThis(observer, sens, doc);
      }
      else if (observer instanceof FinalStateObserver)
      {
        FinalStateObserverHandler.exportThis(observer, sens, doc);
      }
      else if (observer instanceof ElapsedTimeObserver)
      {
        ElapsedTimeObserverHandler.exportThis(observer, sens, doc);
      }
      else if (observer instanceof DebriefReplayObserver)
      {
        DebriefReplayObserverHandler.exportThis(observer, sens, doc);
      }
      else if (observer instanceof TrackPlotObserver)
      {
        TrackPlotObserverHandler.exportThis(observer, sens, doc);
      }
      else if (observer instanceof CSVTrackObserver)
      {
        CSVTrackObserverHandler.exportThis(observer, sens, doc);
      }
      else if (observer instanceof StopOnElapsedObserver)
      {
        TimeObserverHandler.exportThis(observer, sens, doc);
      }
      else if (observer instanceof TimeToLaunchObserver)
      {
        TimeToLaunchObserverHandler.exportThis(observer, sens, doc);
      }
      else if (observer instanceof DetectionObserver.StopOnProximityDetectionObserver)
      {
        ProximityObserverHandler.exportThis(observer, sens, doc);
      }
      else if (observer instanceof RemoveInAreaObserver)
      {
      	RemoveInAreaObserverHandler.exportThis(observer, sens, doc);
      }
      else if (observer instanceof ProximityObserver)
      {
        ProximityObserverHandler.exportThis(observer, sens, doc);
      }
      else if (observer instanceof DetectionObserver.StopOnDetectionObserver)
      {
        StopOnDetectionObserverHandler.exportThis(observer, sens, doc);
      }
      else if (observer instanceof RemoveDetectedObserver)
      {
        RemoveDetectedObserverHandler.exportThis(observer, sens, doc);
      }
      else if (observer instanceof ProportionDetectedObserver)
      {
        ProportionDetectedObserverHandler.exportThis(observer, sens, doc);
      }
      else if (observer instanceof PlotSensorObserver)
      {
        PlotSensorObserverHandler.exportThis(observer, sens, doc);
      }
      else if (observer instanceof PlotDetectionStatusObserver)
      {
        PlotDetectionStatusObserverHandler.exportThis(observer, sens, doc);
      }
      else if (observer instanceof PlotInvestigationSubjectObserver)
      {
        PlotInvestigationSubjectObserverHandler.exportThis(observer, sens, doc);
      }
      else if (observer instanceof DetectionObserver)
      {
        DetectionObserverHandler.exportThis(observer, sens, doc);
      }
      else if (observer instanceof RecordStatusToDBObserverType)
      {
        RecordToDatabaseObserverHandler.exportThis(observer, sens, doc);
      }

    }

    parent.appendChild(sens);

  }


}