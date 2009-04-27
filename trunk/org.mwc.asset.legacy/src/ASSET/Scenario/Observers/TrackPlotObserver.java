package ASSET.Scenario.Observers;

import ASSET.ParticipantType;
import ASSET.Participants.Category;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.ScenarioType;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.Projections.FlatProjection;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GUI.Chart.Painters.GridPainter;
import MWC.GUI.Chart.Painters.ScalePainter;
import MWC.GUI.Editable;
import MWC.GUI.Properties.UnitsPropertyEditor;
import MWC.GUI.Shapes.Symbols.Geog.SquareSymbol;
import MWC.GenericData.*;
import MWC.TacticalData.Fix;
import MWC.TacticalData.Track;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ian
 * Date: 24-Jul-2003
 * Time: 12:47:01
 * To change this template use Options | File Templates.
 */
public class TrackPlotObserver extends RecordToFileObserverType implements ASSET.Scenario.ScenarioSteppedListener
{

  /**
   * build up the tracks - so we can output them at the end (indexed by the participant
   */
  private HashMap<ParticipantType, Track> _myTracks = null;

  /**
   * keep track of the area of coverage
   */
  private WorldArea _myArea;

  /**
   * width of the image to create
   */
  private int _myWid;

  /**
   * height of the image to create
   */
  private int _myHeight;

  /**
   * the separation to use for the grid (if we want one)
   */
  private WorldDistance _gridDelta;

  /**
   * whether to show position fix symbols or not
   */
  private boolean _showPositions;

  /**
   * whether to plot a scale
   */
  private boolean _showScale;

  /**
   * the scale painter - if needed
   */
  private ScalePainter _scalePainter = null;

  /**
   * the grid painter - if needed
   */
  private GridPainter _gridPainter;

  /**
   * a list of additional items to plot
   */
  private Vector<WorldLocation> _additionalPoints = null;

  /**
   * whether to only show the final positions
   */
  private boolean _onlyFinalPositions = false;



  /***************************************************************
   *  constructor
   ***************************************************************/
  /**
   * create a new monitor
   *
   * @param directoryName the directory to output the plots to
   * @param wid           width of the image (in pixels)
   * @param height        height of the image (in pixels)
   * @param fileName      file name to use for results plot (or null to auto-generate one)
   * @param gridDelta     the separation to use for the grid lines - or null for no grid
   */
  public TrackPlotObserver(final String directoryName,
                           final int wid,
                           final int height,
                           final String fileName,
                           final WorldDistance gridDelta,
                           final boolean showPositions,
                           final boolean showScale,
                           final boolean onlyFinalPositions,
                           final String name,
                           final boolean isActive)
  {
    super(directoryName, fileName, name, isActive);

    _myWid = wid;
    _myHeight = height;

    _gridDelta = gridDelta;
    _showPositions = showPositions;
    _showScale = showScale;
    _onlyFinalPositions = onlyFinalPositions;
  }


  /**
   * ************************************************************
   * member methods
   * *************************************************************
   */

  /**
   * whether to only plot the final vessel positions
   *
   * @return yes/no
   */
  public boolean getOnlyFinalPositions()
  {
    return _onlyFinalPositions;
  }

  /**
   * whether to only plot the final vessel positions
   *
   * @param onlyFinalPositions
   */
  public void setOnlyFinalPositions(boolean onlyFinalPositions)
  {
    this._onlyFinalPositions = onlyFinalPositions;
  }


  public WorldDistance getGridDelta()
  {
    return _gridDelta;
  }

  public void setGridDelta(WorldDistance _gridDelta)
  {
    this._gridDelta = _gridDelta;
  }

  public boolean getShowPositions()
  {
    return _showPositions;
  }

  public void setShowPositions(boolean _showPositions)
  {
    this._showPositions = _showPositions;
  }

  public boolean getShowScale()
  {
    return _showScale;
  }

  public void setShowScale(boolean _showScale)
  {
    this._showScale = _showScale;
  }

  public Integer getWidth()
  {
    return _myWid;
  }

  public void setWidth(Integer _myWid)
  {
    this._myWid = _myWid;
  }

  public Integer getHeight()
  {
    return _myHeight;
  }

  public void setHeight(Integer _myHeight)
  {
    this._myHeight = _myHeight;
  }


  /**********************************************************************
   * scenario mangaement
   *********************************************************************/

  /**
   * we're getting up and running.  The observers have been created and we've remembered
   * the scenario
   *
   * @param scenario the new scenario we're looking at
   */
  protected void performSetupProcessing(ScenarioType scenario)
  {

    // are we holding any tracks?
    if (_myTracks != null)
    {
      _myTracks.clear();
      _myTracks = null;
    }

    // and create a new holder
    _myTracks = new HashMap<ParticipantType, Track>();
  }

  /**
   * right, the scenario is about to close.  We haven't removed the listeners
   * or forgotten the scenario (yet).
   *
   * @param scenario the scenario we're closing from
   */
  protected void performCloseProcessing(ScenarioType scenario)
  {
    String theName = "unset";
    if (_myScenario != null)
      theName = _myScenario.getName();

    final File outputFilename = new File(getDirectory() + "/" + newName(theName));

    // do we have any data?
    if (_myTracks.size() > 0)
    {

      // ok, create an image
      BufferedImage bi = produceTrackPlot();


      // ok, output the image
      writeImageToFile(outputFilename, bi);

    } // whether we had any tracks


    // clear the objects
    _myTracks.clear();
    _myTracks = null;

    // clear the coverage
    _myArea = null;
  }

  /**
   * add any applicable listeners
   */
  protected void addListeners()
  {
    _myScenario.addScenarioSteppedListener(this);
  }

  /**
   * remove any listeners
   */
  protected void removeListeners()
  {
    _myScenario.removeScenarioSteppedListener(this);
  }


  protected String newName(String scenario_name)
  {
    String res;
    if (getFileName() == null)
    {
      res = "res_" + scenario_name + "_" + MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(System.currentTimeMillis()) + ".png";
    }
    else
      res = getFileName();

    return res;

  }

  /**
   * determine the normal suffix for this file type
   */
  protected String getMySuffix()
  {
    return "png";
  }

  /**
   * right, look at our tracks and produce a track plot as an image
   *
   * @return an image containing the track plot
   */
  private BufferedImage produceTrackPlot()
  {
    // create the projection
    PlainProjection _myProj = new FlatProjection();
    _myProj.setDataArea(_myArea);
    _myProj.setScreenArea(new Dimension(_myWid, _myHeight));

    // ok, create the image
    BufferedImage bi = new BufferedImage(_myWid,
                                         _myHeight,
                                         BufferedImage.TYPE_INT_RGB);
    Graphics g2 = bi.getGraphics();
    CanvasAdaptor ca = new CanvasAdaptor(_myProj, g2);

    ca.setColor(Color.white);
    ca.fillRect(0, 0, _myWid, _myHeight);


    // do we want a grid?
    if (_gridDelta != null)
    {
      if (_gridPainter == null)
        _gridPainter = new GridPainter();

      _gridPainter.setDelta(new WorldDistance(_gridDelta.getValueIn(WorldDistance.NM), WorldDistance.MINUTES));
      _gridPainter.setPlotLabels(false);
      _gridPainter.setColor(Color.lightGray);
      _gridPainter.paint(ca);
    }

    // do we want a scale?
    if (_showScale)
    {
      if (_scalePainter == null)
        _scalePainter = new ScalePainter();

      _scalePainter.setDisplayUnits(UnitsPropertyEditor.METRES_UNITS);
      _scalePainter.setAutoMode(true);
      _scalePainter.setVisible(true);
      _scalePainter.setColor(Color.gray);
      _scalePainter.paint(ca);
    }

    SquareSymbol ss = new SquareSymbol();

    // step through our tracks
    for (Iterator<ParticipantType> thisTrack = _myTracks.keySet().iterator(); thisTrack.hasNext();)
    {
      CoreParticipant cp = (CoreParticipant) thisTrack.next();
      Track track = (Track) _myTracks.get(cp);
      Color thisTrackColor;
      if (cp.getCategory() == null)
      {
        thisTrackColor = Color.yellow;
      }
      else
      {
        if (cp.getCategory().getForce() == null)
          thisTrackColor = Color.yellow;
        else
          thisTrackColor = Category.getColorFor(cp.getCategory());
      }

      g2.setColor(thisTrackColor);
      Point lastFix = null;

      // are we only showing the final locations?
      if (getShowFinalPositions())
      {
        // yup, we're only showing the final vessel locations
        Fix fix = (Fix) track.getFinalFix();
        Point thisFix = _myProj.toScreen(fix.getLocation());

        // first the location
        ss.setColor(thisTrackColor);
        ss.paint(ca, fix.getLocation());

        // this must be the first one - output the track name
        if (cp.getName() != null)
          ca.drawText(cp.getName(), thisFix.x + 4, thisFix.y);
      }
      else
      {
        // nope, show the whole track
        Enumeration<Fix> enumer = track.getFixes();
        while (enumer.hasMoreElements())
        {
          Fix fix = (Fix) enumer.nextElement();
          Point thisFix = _myProj.toScreen(fix.getLocation());

          // do we want to show positions?
          if (_showPositions)
          {
            ss.setColor(thisTrackColor);
            ss.paint(ca, fix.getLocation());
          }

          if (lastFix != null)
          {
            ca.drawLine(lastFix.x, lastFix.y, thisFix.x, thisFix.y);

          }
          else
          {
            // this must be the first one - output the track name

            // does it hava aname?
            if (cp.getName() != null)
              ca.drawText(cp.getName(), thisFix.x + 4, thisFix.y);
          }
          // and remember the last fix
          lastFix = new Point(thisFix);
        }
      }
    }

    // do we have any additional points
    if (_additionalPoints != null)
    {
      ss.setColor(Color.magenta);

      for (int i = 0; i < _additionalPoints.size(); i++)
      {
        WorldLocation location = (WorldLocation) _additionalPoints.elementAt(i);
        ss.paint(ca, location);
      }
    }


    // put the comment in at the TL
    String comment;
    comment = "ASSET run performed at:" + MWC.Utilities.TextFormatting.DebriefFormatDateTime.toString(System.currentTimeMillis());
    g2.setColor(Color.DARK_GRAY);
    g2.drawString(comment, 5, 20);


    ca.endDraw(null);
    g2.dispose();
    return bi;
  }

  /**
   * an image has been drawn.  Now output it to file.
   *
   * @param outputFilename
   * @param bi
   */
  private static void writeImageToFile(final File outputFilename, BufferedImage bi)
  {
    // and write to file
    try
    {
      // check we have the parent directories
      File parentFile = outputFilename.getParentFile();
      parentFile.mkdirs();

      ImageIO.write(bi, "png", outputFilename);
    }
    catch (IOException e)
    {
      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }
  }

  /**
   * the scenario has stepped forward
   */
  public void step(long newTime)
  {
    if (!isActive())
      return;

    // get the positions of the participants
    final Integer[] lst = _myScenario.getListOfParticipants();
    for (int thisIndex = 0; thisIndex < lst.length; thisIndex++)
    {
      final Integer integer = lst[thisIndex];
      if (integer != null)
      {
        final ParticipantType pt = _myScenario.getThisParticipant(integer.intValue());
        final Status stat = pt.getStatus();
        final WorldLocation loc = stat.getLocation();

        // and store the data
        processTheseDetails(loc, stat, pt);

      }
    }

  }

  /**
   * store the data for this time step
   *
   * @param loc  the current location of this participant
   * @param stat the status of this participant
   * @param pt   this participant
   */
  public void processTheseDetails(final WorldLocation loc, final Status stat, final ParticipantType pt)
  {
    // ok, now output these details in our special format
    writeTheseDetails(loc, stat, pt);

    // extend the world area to contain this
    if (_myArea == null)
      _myArea = new WorldArea(loc, loc);
    else
      _myArea.extend(loc);
  }


  /**
   * add a series of additional points to the plot
   */
  public void addPoints(WorldPath newPoints)
  {
    if (_additionalPoints == null)
      _additionalPoints = new Vector<WorldLocation>();

    Collection<WorldLocation> pts = newPoints.getPoints();

    for (Iterator<WorldLocation> iterator = pts.iterator(); iterator.hasNext();)
    {
      WorldLocation location = (WorldLocation) iterator.next();
      _additionalPoints.add(location);
    }

  }


  /**
   * write this set of details to file
   *
   * @param loc  the current location
   * @param stat the current status
   * @param pt   the participant in question
   */
  protected void writeTheseDetails(WorldLocation loc, Status stat, ParticipantType pt)
  {
    // do we hold this participant
    Track trk = (Track) _myTracks.get(pt);

    // did we find it?
    if (trk == null)
    {
      trk = new Track();
      trk.setName(pt.getName());
      _myTracks.put(pt, trk);
    }

    // create the fix
    HiResDate hrd = new HiResDate(stat.getTime(), 0);
    Fix fix = new Fix(hrd, loc,
                      stat.getCourse(),
                      stat.getSpeed().getValueIn(WorldSpeed.Kts));

    // and add it
    trk.addFix(fix);
  }

  //////////////////////////////////////////////////
  // property editing
  //////////////////////////////////////////////////

  /**
   * whether there is any edit information for this item
   * this is a convenience function to save creating the EditorType data
   * first
   *
   * @return yes/no
   */
  public boolean hasEditor()
  {
    return true;
  }

  /**
   * get the editor for this item
   *
   * @return the BeanInfo data for this editable object
   */
  public EditorType createEditor()
  {
    return new TrackPlotObserverInfo(this);
  }

  /**
   * whether to only plot the final participant locations
   *
   * @return yes/no
   */
  public boolean getShowFinalPositions()
  {
    return _onlyFinalPositions;
  }

  /**
   * whether to only plot the final participant locations
   *
   * @param onlyFinalPositions
   */
  public void setShowFinalPositions(boolean onlyFinalPositions)
  {
    this._onlyFinalPositions = onlyFinalPositions;
  }

  //////////////////////////////////////////////////
  // editable properties
  //////////////////////////////////////////////////
  static public class TrackPlotObserverInfo extends EditorType
  {


    /**
     * constructor for editable details
     *
     * @param data the object we're going to edit
     */
    public TrackPlotObserverInfo(final TrackPlotObserver data)
    {
      super(data, data.getName(), "Edit");
    }

    /**
     * editable GUI properties for our participant
     *
     * @return property descriptions
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res = {
          prop("Name", "the name of this observer"),
          prop("GridDelta", "the grid spacing to show"),
          prop("ShowPositions", "whether to show individual positions"),
          prop("ShowScale", "whether to show a scale"),
          prop("Height", "the height of the image to produce"),
          prop("Width", "the width of the image to produce"),
          prop("OnlyFinalPositions", "whether to only show final positions"),

        };
        return res;
      }
      catch (IntrospectionException e)
      {
        e.printStackTrace();
        return super.getPropertyDescriptors();
      }
    }
  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public class TrackPlotObsTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public TrackPlotObsTest(final String val)
    {
      super(val);
    }

    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      TrackPlotObserver tpo = new TrackPlotObserver("a", 400, 400, "b",
                                                    new WorldDistance(10, WorldDistance.KM),
                                                    true, true, false, "test observer", true);
      return tpo;
    }

    public void testWrite()
    {


      final String directoryName = "./";
      final String fileName = "res.png";

      TrackPlotObserver tpo = new TrackPlotObserver(directoryName, 400, 400, fileName,
                                                    new WorldDistance(10, WorldDistance.KM),
                                                    true, true, false, "test observer", true);
      CoreScenario cs = new CoreScenario();
      tpo.setup(cs);

      WorldLocation loc = new WorldLocation(1, 2, 3);
      Status stat = new Status(12, 0);
      stat.setCourse(21);
      stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

      CoreParticipant ssn = new CoreParticipant(12);
      ssn.setName("Bingo");
      ssn.setCategory(new Category(Category.Force.BLUE, Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));


      tpo.processTheseDetails(loc,
                              stat, ssn);

      // move location
      loc = new WorldLocation(loc.add(new WorldVector(0.101, 0.01, 1)));
      tpo.processTheseDetails(loc,
                              stat, ssn);

      loc = new WorldLocation(loc.add(new WorldVector(0.201, 0.05, 1)));
      tpo.processTheseDetails(loc,
                              stat, ssn);

      loc = new WorldLocation(loc.add(new WorldVector(0.101, 0.03, 1)));
      tpo.processTheseDetails(loc,
                              stat, ssn);

      loc = new WorldLocation(loc.add(new WorldVector(0.011, 0.05, 1)));
      tpo.processTheseDetails(loc,
                              stat, ssn);

      ssn = new CoreParticipant(14);
      ssn.setName("Spooner");
      ssn.setCategory(new Category(Category.Force.RED, Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));


      loc = new WorldLocation(loc.add(new WorldVector(2.701, 0.12, 1)));
      tpo.processTheseDetails(loc,
                              stat, ssn);

      // move location
      loc = new WorldLocation(loc.add(new WorldVector(2.201, 0.12, 1)));
      tpo.processTheseDetails(loc,
                              stat, ssn);

      loc = new WorldLocation(loc.add(new WorldVector(2.101, 0.05, 1)));
      tpo.processTheseDetails(loc,
                              stat, ssn);

      loc = new WorldLocation(loc.add(new WorldVector(1.301, 0.02, 1)));
      tpo.processTheseDetails(loc,
                              stat, ssn);

      loc = new WorldLocation(loc.add(new WorldVector(2.031, 0.04, 1)));
      tpo.processTheseDetails(loc,
                              stat, ssn);

      tpo.tearDown(cs);

      // check file exists
      File file = new File(directoryName + fileName);
      assertTrue("file got created", file.exists());
      System.out.println("file size is:" + file.length());
      assertEquals("file is of correct size", (float) 3707, (float) file.length(), 100);
    }
  }

}
