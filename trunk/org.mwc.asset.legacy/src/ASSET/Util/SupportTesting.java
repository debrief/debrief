package ASSET.Util;

import ASSET.GUI.Core.CoreGUISwing;
import ASSET.Models.Decision.TargetType;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.ParticipantMovedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.Observers.Recording.CSVTrackObserver;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import ASSET.Scenario.Observers.TrackPlotObserver;
import ASSET.ScenarioType;
import MWC.GUI.Editable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * *******************************************************************
 * utility class providing testing
 * *******************************************************************
 */
public class SupportTesting extends junit.framework.TestCase
{

  static final String TEST_DIR = "c://temp//";


  /**
   * our track plot observer, if we have one
   */
  protected TrackPlotObserver _tpo;

  /**
   * the list of participants we're listening to
   */
  protected HashMap _listeningList;

  /**
   * our debrief plot observer, if we have one
   */
  protected DebriefReplayObserver _dro;

  /**
   * our csv track observer, if we have one
   */
  private CSVTrackObserver _cvo;

  /**
   * constructor - takes the name of this set of tests
   *
   * @param s
   */
  public SupportTesting(String s)
  {
    super(s);
  }

  protected void outputThisDocument(Document theDoc, String title)
  {
    outputThis(theDoc, title);
  }

  public static void outputThis(Document theDoc, String title)
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    javax.xml.transform.TransformerFactory factory
      = TransformerFactory.newInstance();

    try
    {
      javax.xml.transform.Transformer transformer
        = factory.newTransformer();

      StreamResult sr = new StreamResult(bos);

      transformer.transform(new DOMSource(theDoc), sr);

      bos.close();
    }
    catch (TransformerException e)
    {
      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }
    catch (IOException e)
    {
      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }

    System.out.println("=======" + title + "================");
    System.out.println(bos.toString());
    System.out.println("=====================");
  }


  /**
   * set up to start recording
   *
   * @param name        the name to prefix any files
   * @param doPlot      whether to produce a plot
   * @param doREP       whether to produce a replay file
   * @param theScenario
   */
  protected void startRecording(String name,
                                boolean doPlot,
                                boolean doREP, boolean doCSV, ScenarioType theScenario)
  {
    if (doPlot)
    {
      _tpo = new TrackPlotObserver(TEST_DIR, 600, 600, name + ".png", null, true, true, false, "track plot", true);
      _tpo.setup(theScenario);
    }

    if (doREP)
    {
      _dro = new DebriefReplayObserver(TEST_DIR, name + ".rep", false, false, true, null, "debrief plot", true);
      _dro.setup(theScenario);
    }

    if (doCSV)
    {
      _cvo = new CSVTrackObserver(TEST_DIR, name + ".csv", false, false, true, null, "CSV track", true);
      _cvo.setup(theScenario);
    }
  }

  /**
   * record this status snapshot
   *
   * @param stat current status
   * @param part participant we're looking at
   */
  protected void recordThis(Status stat,
                            CoreParticipant part, long newTime)
  {
    if (_tpo != null)
      _tpo.processTheseDetails(stat.getLocation(), stat, part);

    if (_dro != null)
    {
      _dro.writeThesePositionDetails(stat.getLocation(), stat, part, newTime);
    }
    if (_cvo != null)
    {
      _cvo.writeThesePositionDetails(stat.getLocation(), stat, part, newTime);
    }
  }

  /**
   * start listening to this particular participant
   */
  protected void startListeningTo(final CoreParticipant cp, String name,
                                  boolean doPlot, boolean doRep, boolean doCSV,
                                  ScenarioType theScenario)
  {
    // are we up and running?
    if ((_dro == null) && (_tpo == null) && (_cvo == null))
    {
      // no, start recording
      this.startRecording(name, doPlot, doRep, doCSV, theScenario);
    }

    ParticipantMovedListener pml = new ParticipantMovedListener()
    {
      public void moved(Status newStatus)
      {
        recordThis(newStatus, cp, newStatus.getTime());
      }

      public void restart()
      {
      }
    };

    // now listen to it
    cp.addParticipantMovedListener(pml);

    // and remember it
    if (_listeningList == null)
      _listeningList = new HashMap();

    _listeningList.put(cp, pml);
  }

  /**
   * tidy things up, close files
   *
   * @param theScenario
   */
  protected void endRecording(ScenarioType theScenario)
  {
    // stop listening to the participants
    if (_listeningList != null)
    {
      for (Iterator iterator = _listeningList.keySet().iterator(); iterator.hasNext();)
      {
        CoreParticipant coreParticipant = (CoreParticipant) iterator.next();
        ParticipantMovedListener pml = (ParticipantMovedListener) _listeningList.get(coreParticipant);
        coreParticipant.removeParticipantMovedListener(pml);
      }

      // now clear it
      _listeningList.clear();
      _listeningList = null;
    }

    if (_tpo != null)
    {
      _tpo.tearDown(theScenario);
      _tpo = null;
    }

    if (_dro != null)
    {
      _dro.tearDown(theScenario);
      _dro = null;
    }
    if (_cvo != null)
    {
      _cvo.tearDown(theScenario);
      _cvo = null;
    }
  }


  /**
   * create a random location within the indicated area
   *
   * @param bounding_area the area to create a location within
   * @return the new location
   */
  public static WorldLocation createLocation(WorldArea bounding_area)
  {
    double theLat = bounding_area.getBottomLeft().getLat();
    double theLong = bounding_area.getBottomLeft().getLong();

    double theLatDelta = ASSET.Util.RandomGenerator.nextRandom() * (bounding_area.getTopRight().getLat() - theLat);
    double theLongDelta = ASSET.Util.RandomGenerator.nextRandom() * (bounding_area.getTopRight().getLong() - theLong);

    return new WorldLocation(theLat + theLatDelta, theLong + theLongDelta, 0);
  }


  /**
   * create a location using user-configurable units
   *
   * @param latVal
   * @param longVal
   * @return
   */
  public static WorldLocation createLocation(WorldDistance latVal, WorldDistance longVal)
  {
    return new WorldLocation(latVal.getValueIn(WorldDistance.DEGS),
                             longVal.getValueIn(WorldDistance.DEGS), 0);
  }

  /**
   * quickly create a test location, using metre coordinates
   *
   * @param x_m longitude in metres
   * @param y_m latitude in metres
   * @return the new location
   */
  public static WorldLocation createLocation(double x_m, double y_m)
  {
    return new WorldLocation(MWC.Algorithms.Conversions.m2Degs(y_m),
                             MWC.Algorithms.Conversions.m2Degs(x_m),
                             0);
  }

  public static void outputLocation(WorldLocation loc)
  {
    if (loc != null)
    {
      String res = toXYString(loc);
      System.out.print(res);
    }
  }

  public static String toXYString(WorldLocation loc)
  {
    String res = " x," + (int) MWC.Algorithms.Conversions.Degs2m(loc.getLong());
    res += ",y," + (int) MWC.Algorithms.Conversions.Degs2m(loc.getLat());
    return res;
  }

  /**
   * output this series of destinations to a file, in replay format
   *
   * @param fileName
   * @param destinations
   */
  public void outputTheseToRep(String fileName, WorldPath destinations)
  {
    try
    {
      FileWriter writer = new FileWriter(TEST_DIR + fileName);

      Collection points = destinations.getPoints();
      int counter = 1;
      for (Iterator iterator = points.iterator(); iterator.hasNext();)
      {
        WorldLocation worldLocation = (WorldLocation) iterator.next();
        String thisLine = ";CIRCLE: @@ ";
        thisLine += MWC.Utilities.TextFormatting.DebriefFormatLocation.toString(worldLocation);
        thisLine += " 50  pt_" + counter++ + System.getProperty("line.separator");
        writer.write(thisLine);
      }

      writer.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }

  }

  ////////////////////////////////////////////////////////////
  // narrative support
  ////////////////////////////////////////////////////////////




  /**
   * a destination for writing our narrative
   */
  private static java.io.FileWriter _fo = null;

  /**
   * get ready to record a narrative
   *
   * @param fileName the file to record the narrative to
   */
  public static void setupNarrative(String fileName)
  {
    if (_fo == null)
    {
      try
      {
        _fo = new FileWriter(fileName);
      }
      catch (IOException e)
      {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
    }
  }

  /**
   * utility testing method to write a line of text to the narrative file (if one has been setup)
   *
   * @param msg    the message to store
   * @param trk    the track this message relates to
   * @param dtg    the time of the message
   * @param source
   */
  public static void recordThis(String msg, String trk, long dtg, Object source)
  {
    try
    {
      if (_fo != null)
      {
        _fo.write(";NARRATIVE: " + MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(dtg) + " " + trk + " " + msg + " (" + source.toString() + ")");
        _fo.write(System.getProperty("line.separator"));
        _fo.flush();
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }

  }

  public static void stopNarrative()
  {
    if (_fo != null)
    {
      try
      {
        _fo.flush();
        _fo.close();
      }
      catch (IOException e)
      {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
    }
  }

  ////////////////////////////////////////////////////////////
  // auto-test support
  ////////////////////////////////////////////////////////////



  public static void callTestMethods(SupportTesting tt)
  {
    // find and run all methods beginning with test
    Method[] methods = tt.getClass().getMethods();
    for (int i = 0; i < methods.length; i++)
    {
      Method thisMethod = methods[i];
      if (thisMethod.getName().startsWith("test"))
      {
        Class params[] = {};
        try
        {
          thisMethod.invoke(tt, params);
          System.out.println("called:" + thisMethod.getName());
        }
        catch (IllegalAccessException e)
        {
          e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        catch (IllegalArgumentException e)
        {
          e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        catch (InvocationTargetException e)
        {
          e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
      }
    }
  }

  //////////////////////////////////////////////////
  // add property editing testing
  //////////////////////////////////////////////////
  abstract public static class EditableTesting extends SupportTesting
  {
    public EditableTesting()
    {
      super("Testing editable properties");
    }

    public EditableTesting(String name)
    {
      super(name);
    }

    /**
     * run through tests of the editable properties
     */
    public final void testMyParams()
    {
      // just check that our ASSET-specific editors are loaded
      TargetType tt = new TargetType();
      PropertyEditor pe = PropertyEditorManager.findEditor(TargetType.class);
      if (pe == null)
        CoreGUISwing.registerEditors();


      // ok, get on with it
      Editable toBeTested = getEditable();
      Editable.editableTesterSupport.testTheseParameters(toBeTested);
    }

    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    abstract public Editable getEditable();

  }


}
