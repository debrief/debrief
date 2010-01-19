/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 14-Jun-02
 * Time: 14:16:42
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Models.Decision.Tactical;

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.Movement.Trail;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Decision.Waterfall;
import ASSET.Models.DecisionType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Mediums.Optic;
import ASSET.Models.Movement.SSMovementCharacteristics;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;
import ASSET.ParticipantType;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import ASSET.Util.XML.ASSETReaderWriter;
import MWC.GUI.Editable;
import MWC.GenericData.*;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * behaviour relating to the launch of a weapon
 * The weapon to be launched is stored as a long text string.  The text string may contain
 * the following keywords which will be replaced with the correct value when the weapon
 * is launched (if known)
 * <ul>
 * <li>$BRG$  current bearing to the target (in degrees)</li>
 * <li>$RNG$  current range to the target (in yards)</li>
 * <ul>
 */

public class LaunchWeapon extends CoreDecision implements java.io.Serializable
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/****************************************************
   * member variables
   ***************************************************/

  /**
   * the activity to return when we launch
   */
  static final private String LAUNCH_MESSAGE = "Launching";

  /**
   * the type of participant to launch
   */
  protected String _launchThis = "blank";

  /**
   * the filename where we read the launch description
   * from
   */
  protected String _fileName = null;

  /**
   * the type of target to launch against
   */
  private TargetType _myTarget;

  /**
   * a local copy of our editable object
   */
  protected MWC.GUI.Editable.EditorType _myEditor = null;

  /**
   * the range at which we launch weapon (yds)
   */
  private WorldDistance _launchRange;

  /**
   * the amount of time to wait before launching another (millis)
   */
  private Duration _coolOffTime;

  /**
   * the time the last missile was fired
   */
  private long _lastLaunch = -1;

  /**
   * *************************************************
   * constructor
   * *************************************************
   */
  public LaunchWeapon()
  {
    super("Launch Weapon");
    _coolOffTime = new Duration(Duration.DAYS, 1);
  }

  /**
   * *************************************************
   * member methods
   * *************************************************
   */
  public TargetType getTargetType()
  {
    return _myTarget;
  }

  public void setTargetType(final TargetType target)
  {
    this._myTarget = target;
  }

  /**
   * get the range at which we launch (yds)
   */
  public WorldDistance getLaunchRange()
  {
    return _launchRange;
  }

  /**
   * get how long to wait before re-launch
   */
  public Duration getCoolOffTime()
  {
    return _coolOffTime;
  }

  /**
   * set how long to wait before re-launch
   */
  public void setCoolOffTime(final Duration coolOffTimeMillis)
  {
    _coolOffTime = coolOffTimeMillis;
  }


  /**
   * get the filename for the weapon description
   */
  public String getLaunchFilename()
  {
    return _fileName;
  }

  /**
   * set the filename for the description
   */
  public void setLaunchFilename(final String launchThis)
  {
    _fileName = launchThis;
  }


  /**
   * get the XML string describing what to launch
   */
  public String getLaunchType()
  {
    return _launchThis;
  }

  /**
   * set the XML string describing what to launch
   */
  public void setLaunchType(final String launchThis)
  {
    this._launchThis = launchThis;
  }

  /**
   * set the range at which we launch (yds)
   */
  public void setLaunchRange(final WorldDistance launchRangeYds)
  {
    this._launchRange = launchRangeYds;
  }


  /**
   * decide the course of action to take, or return null to no be used
   *
   * @param status     the current status of the participant
   * @param detections the current list of detections for this participant
   * @param time       the time this decision was made
   */
  public DemandedStatus decide(final Status status,
                               ASSET.Models.Movement.MovementCharacteristics chars, DemandedStatus demStatus, final DetectionList detections,
                               final ASSET.Scenario.ScenarioActivityMonitor monitor,
                               final long time)
  {
    // produce a steady-state demanded course - so we continue
    // what we're doing during the weapons launch
    DemandedStatus res = null;

    // clear the activity flag
    String activity = "Not in trail";

    // is it time to fire another yet?
    if ((_lastLaunch == -1) || (time > _lastLaunch + _coolOffTime.getValueIn(Duration.MILLISECONDS)))
    {

      // do we have any detections?
      if (detections != null)
      {
        // get bearing to first detection
        final int len = detections.size();
        if (len > 0)
        {
          for (int i = 0; i < len; i++)
          {

            final ASSET.Models.Detection.DetectionEvent de = detections.getDetection(i);
            final Float brg = de.getBearing();
            if (brg != null)
            {
              // do we have a target type?
              if (_myTarget != null)
              {
                // is this of our target type
                final ASSET.Participants.Category thisTarget = de.getTargetType();
                if (_myTarget.matches(thisTarget))
                {
                  // do we have range?
                  if (de.getRange() != null)
                  {

                    // work out distance from us to the target, not from the sensor to the target
                    WorldLocation sensorLocation = de.getSensorLocation();

                    // Work out the estimated target location
                    WorldVector sensorToTarget = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(de.getBearing().doubleValue()),
                                                                 de.getRange().getValueIn(WorldDistance.DEGS),
                                                                 0);

                    WorldLocation targetLocation = sensorLocation.add(sensorToTarget);

                    // how are are we from the target location
                    WorldVector meToTarget = status.getLocation().subtract(targetLocation);
                    double yds_to_target = MWC.Algorithms.Conversions.Degs2Yds(meToTarget.getRange());
                    double brg_to_target_degs = MWC.Algorithms.Conversions.Rads2Degs(meToTarget.getBearing());

                    // is it within range?
                    if (yds_to_target < _launchRange.getValueIn(WorldDistance.YARDS))
                    {
                      // continue in steady state
                      res = new SimpleDemandedStatus(time, status);

                      // remember the launch time
                      _lastLaunch = time;

                      // start the launch steps
                      launchWeapon(de, monitor, status, brg_to_target_degs, time);

                      activity = LaunchWeapon.LAUNCH_MESSAGE;

                      // ok, drop out, we don't need to launch any more weapons
                      return res;
                    }
                  }
                }
              }
            } // if we know the bearing
          } // looping through the detections
        } // if we have any detections
      } // if the detections object was received
    } // whether it's time to launch another
    else
    {
      //
    }

    super.setLastActivity(activity);

    // always return null, since we continue in steady state
    return res;
  }

  /**
   * utility class provided to overcome String.replace only being
   * present in JDK1.4
   */
  protected static String replaceAll(final String original, final String toBeSwappedOut, final String toBeSwappedIn)
  {
    if (original == null)
      return null;

    String temp = original;
    int a = 0;

    for (int i = 0; i < original.length(); i++)
    {
      a = temp.indexOf(toBeSwappedOut);
      if (a == -1)
        break;
      temp = temp.substring(0, a) + toBeSwappedIn + temp.substring(a + toBeSwappedOut.length());
      int tmp = toBeSwappedOut.length() - toBeSwappedIn.length();
      if (tmp < 0) tmp = (tmp * -1) + 1;
      if (tmp == 0) tmp = toBeSwappedIn.length();
      a += tmp;
    }

    return temp;
  }

  /**
   * put our keywords into the XML description
   */
  protected static String swapKeywords(final DetectionEvent detection,
                                     final Status currentLocation,
                                     final String weapon,
                                     final TargetType theTarget)
  {
    // amend string template to include available parameters
    final Float brg_degs = detection.getBearing();
    final WorldDistance rng = detection.getRange();

    // take a copy of the string
    String working = new String(weapon);

    // swap the bearing
    if (brg_degs != null)
    {
      final String brg_val = "" + brg_degs.floatValue();
      working = replaceAll(working, "$BRG$", brg_val);
    }

    // swap the range
    if (rng != null)
    {
      final String rng_val = "" + rng.getValueIn(WorldDistance.YARDS);
      working = replaceAll(working, "$RNG$", rng_val);
    }

    // insert the location of the target
    if (brg_degs != null)
    {
      final float brg_val = brg_degs.floatValue();

      // do we know range?
      if (rng != null)
      {
        // yes, compute target location
        final WorldVector newVector =
          new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(brg_val),
                          rng.getValueIn(WorldDistance.DEGS), 0);
        final WorldLocation newLoc = currentLocation.getLocation().add(newVector);

        // produce strings from this location
        final String theDepth = "" + newLoc.getDepth();
        final String theLat = "" + newLoc.getLat();
        final String theLong = "" + newLoc.getLong();

        // put these strings into the new behaviour
        working = replaceAll(working, "$TGT_DEPTH$", theDepth);
        working = replaceAll(working, "$TGT_LAT$", theLat);
        working = replaceAll(working, "$TGT_LONG$", theLong);


      }
      else
      {
        // no, send the weapon down a bearing for XXXX yds
        // compute target location
        final double TGT_RANGE = 5000;
        final WorldVector newVector = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(brg_val), MWC.Algorithms.Conversions.Yds2Degs(TGT_RANGE), 0);
        final WorldLocation newLoc = currentLocation.getLocation().add(newVector);

        // produce strings from this location
        final String theDepth = "" + newLoc.getDepth();
        final String theLat = "" + newLoc.getLat();
        final String theLong = "" + newLoc.getLong();

        // put these strings into the new behaviour
        working = replaceAll(working, "$TGT_DEPTH$", theDepth);
        working = replaceAll(working, "$TGT_LAT$", theLat);
        working = replaceAll(working, "$TGT_LONG$", theLong);
      }
    }


    if (theTarget != null)
    {
      // output the XML header stuff
      // output the plot
      final java.io.StringWriter newString = new StringWriter();
      //      final com.sun.xml.tree.XmlDocument doc = new com.sun.xml.tree.XmlDocument();
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      Document doc = null;
      try
      {
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.newDocument();
        final org.w3c.dom.Element type = ASSET.Util.XML.Decisions.Util.TargetTypeHandler.getElement(theTarget, doc);
        doc.appendChild(type);
        doc.setNodeValue(type.getTagName());
        //    doc.changeNodeOwner(type);
        //   doc.setSystemId("ASSET XML Version 1.0");


        // Use a Transformer for output
        TransformerFactory tFactory =
          TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(newString);
        transformer.transform(source, result);
      }
      catch (ParserConfigurationException e)
      {
        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }
      catch (DOMException e)
      {
        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }
      catch (TransformerFactoryConfigurationError transformerFactoryConfigurationError)
      {
        transformerFactoryConfigurationError.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }
      catch (TransformerException e)
      {
        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }



      //      // ok, we should be done now
      //      try
      //      {
      //        doc.write(newString);
      //      }
      //      catch(java.io.IOException e)
      //      {
      //        e.printStackTrace();
      //      }

      // try to extract the <target type portion
      if (newString != null)
      {
        final String val = newString.toString();
        final String startIdentifier = "<TargetType";
        final String endIdentifier = "</TargetType";
        final int start = val.indexOf(startIdentifier);
        final int end = val.lastIndexOf(endIdentifier);
        final String detail = val.substring(start, end + endIdentifier.length() + 1);

        // lastly, replace the string
        working = replaceAll(working, "<TargetType/>", detail);
      }

    }

    return working;

  }

  /**
   * get the participant
   */
  protected static ParticipantType getWeapon(final DetectionEvent detection,
                                           final Status currentLocation,
                                           final String weapon,
                                           final TargetType target)
  {
    ParticipantType res = null;

    final String fName = null;

    // swap in the keywords
    final String working = swapKeywords(detection, currentLocation, weapon, target);

    // put the string into a stream
//    final StringBufferInputStream sr = new StringBufferInputStream(working);
    final InputStream sr = new ByteArrayInputStream(working.getBytes()); 

    // extract the participant
    res = ASSETReaderWriter.importParticipant(fName, sr);

    return res;
  }


  /**
   * utility method to read the contents of a file, and return it as a string
   *
   * @param fileName
   * @return
   */
  public static String readWeaponFromThisFile(final String fileName)
  {
    final StringBuffer res = new StringBuffer();
    // we've received the weapon description as a filename, load it
    try
    {

      final java.io.FileReader fi = new java.io.FileReader(fileName);
      final java.io.BufferedReader bg = new java.io.BufferedReader(fi);
      String next = bg.readLine();
      while (next != null)
      {
        res.append(next);
        res.append("\n");
        next = bg.readLine();
      }
    }
    catch (java.io.IOException ee)
    {
      ee.printStackTrace();
    }
    return res.toString();
  }

  /**
   * do the actual launch
   */
  private void launchWeapon(final DetectionEvent detection,
                            final ASSET.Scenario.ScenarioActivityMonitor monitor,
                            final Status currentLocation,
                            final double initial_course_degs,
                            final long time)
  {

    // have we read in the weapon description yet?
    if ((_launchThis == null) || (_launchThis.length() == 0))
      _launchThis = readWeaponFromThisFile(this._fileName);

    final ParticipantType newPart = getWeapon(detection, currentLocation, _launchThis, _myTarget);

    // create the target
    monitor.createParticipant(newPart);

    // set it's status
    final Status newState = new Status(currentLocation);
    // update the status for the object's new id
    newState.setId(newPart.getId());

    // and put it in the participant
    newPart.setStatus(newState);

    // give it a faster speed though
    newState.setSpeed(new WorldSpeed(30, WorldSpeed.M_sec));

    // give the weapon a hint about direction to travel in
    SimpleDemandedStatus ds = new SimpleDemandedStatus(time, newState);
    ds.setCourse(initial_course_degs);

    newPart.setDemandedStatus(ds);

  }

  /**
   * reset this decision model
   */
  public void restart()
  {
    _lastLaunch = -1;
  }


  /**
   * indicate to this model that its execution has been interrupted by another (prob higher priority) model
   *
   * @param currentStatus
   */
  public void interrupted(Status currentStatus)
  {
    // ignore.
  }

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
  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new LaunchInfo(this);

    return _myEditor;
  }

  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: LaunchWeapon.java,v $
   * Revision 1.1  2006/08/08 14:21:36  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:44  Ian.Mayo
   * First versions
   *
   * Revision 1.21  2004/09/02 13:17:36  Ian.Mayo
   * Reflect CoreDecision handling the toString method
   *
   * Revision 1.20  2004/08/31 09:36:25  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.19  2004/08/26 16:27:06  Ian.Mayo
   * Implement editable properties
   * <p/>
   * Revision 1.18  2004/08/25 11:20:40  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.17  2004/08/20 13:32:31  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.16  2004/08/17 14:22:08  Ian.Mayo
   * Refactor to introduce parent class capable of storing name & isActive flag
   * <p/>
   * Revision 1.15  2004/08/12 11:09:24  Ian.Mayo
   * Respect observer classes refactored into tidy directories
   * <p/>
   * Revision 1.14  2004/08/09 15:50:33  Ian.Mayo
   * Refactor category types into Force, Environment, Type sub-classes
   * <p/>
   * Revision 1.13  2004/08/06 12:52:05  Ian.Mayo
   * Include current status when firing interruption
   * <p/>
   * Revision 1.12  2004/08/06 11:14:27  Ian.Mayo
   * Introduce interruptable behaviours, and recalc waypoint route after interruption
   * <p/>
   * Revision 1.11  2004/05/24 15:57:11  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.2  2004/04/08 20:27:17  ian
   * Restructured contructor for CoreObserver
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:52  ian
   * no message
   * <p/>
   * Revision 1.10  2004/02/18 08:48:10  Ian.Mayo
   * Sync from home
   * <p/>
   * Revision 1.7  2003/11/05 14:28:25  Ian.Mayo
   * correct path for testing
   * <p/>
   * Revision 1.6  2003/11/05 09:19:55  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  /**
   * *************************************************
   * editor support
   * *************************************************
   */

  static public class LaunchInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public LaunchInfo(final LaunchWeapon data)
    {
      super(data, data.getName(), "LaunchWeapon");
    }

    /**
     * editable GUI properties for our participant
     *
     * @return property descriptions
     */
    public java.beans.PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final java.beans.PropertyDescriptor[] res = {
          prop("TargetType", "the type of vessel this model is evading"),
          prop("LaunchRange", "the range at which to launch"),
          prop("Name", "the name of this evasion model"),
          //      prop("LaunchType", "the description of the weapon to be fired"),
          prop("CoolOffTime", "the period before we re-launch against another valid target (millis)"),
          prop("LaunchFilename", "the filename where the weapon is stored"),

        };
        //        res[0].setPropertyEditorClass(ASSET.GUI.Editors.TargetTypeEditor.class);
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }


  /**
   * *************************************************
   * testing
   * *************************************************
   */
  public static class LaunchWeaponTest extends SupportTesting.EditableTesting
  {


    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new LaunchWeapon();
    }

    public static final String launchBehaviour =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
      "<Torpedo Name=\"SPEARFISH\">" +
      "<Category Environment=\"SUBSURFACE\" Force=\"BLUE\" Type=\"TORPEDO\"/>" +
      "<SensorFit>" +
      "<BroadbandSensor Aperture=\"180\"/>" +
      "</SensorFit>" +
      "<Status Course=\"0\" Fuel=\"0\" Id=\"0\">" +
      "<Speed Units=\"m/s\" Value=\"20\"/>" +
      "<Location>" +
      "<shortLocation Depth=\"0\" Lat=\"0\" Long=\"0\">" +
      "<Height Units=\"m\" Value=\"0\"/>" +
      "</shortLocation>" +
      "</Location>" +
      "</Status>" +
      "<Waterfall>" +
      "<Detonate Name=\"DestroyRed\" DetonationPower=\"90.000\" > " +
      "<WorldDistance Units=\"yds\" Value=\"400\"/>" +
      "<TargetType>" +
      "<Type Name=\"RED\" />" +
      "<Type Name=\"SURFACE\" />" +
      "</TargetType>" +
      "</Detonate> " +
      "<Trail Name=\"Chase Red\" > " +
      "<AllowableError Units=\"yds\" Value=\"400\"/>" +
      "<TrailRange Units=\"yds\" Value=\"100\"/>" +
      "<TargetType>" +
      "<Type Name=\"RED\" />" +
      "<Type Name=\"SURFACE\" />" +
      "</TargetType>" +
      "</Trail> " +
      "<Transit Looping=\"FALSE\" Name=\"PROSECUTE\" Speed=\"30\">" +
      "<WorldPath>" +
      "<Point>" +
      "<shortLocation Depth=\"$TGT_DEPTH$\" Lat=\"$TGT_LAT$\" Long=\"$TGT_LONG$\"/>" +
      "</Point>" +
      "</WorldPath>" +
      "</Transit>" +
      "</Waterfall>" +
      "<RadiatedCharacteristics>" +
      "<Broadband BaseNoiseLevel=\"40\"/>" +
      "</RadiatedCharacteristics>" +
      "<SSMovementCharacteristics AccelerationRate=\"10\" DepthChangeRate=\"2\"" +
      " FuelUsageRate=\"0\" MaxDepth=\"200\" MaxSpeed=\"34\" Name=\"SPEARFISH\" TurningCircle=\"45\">" +
      " <MinSpeed Units=\"m/s\" Value=\"0\"/>" + 
      " <MaxSpeed Units=\"m/s\" Value=\"16\"/>" + 
      " <MinHeight Units=\"m\" Value=\"-100\"/>" + 
      " <MaxHeight Units=\"m\" Value=\"0\"/>" + 
      " <DefaultClimbRate Units=\"ft/s\" Value=\"20\"/>" +
      " <DefaultDiveRate Units=\"ft/s\" Value=\"20\"/>" +
      " <AccelerationRate Units=\"m/s/s\" Value=\"12\"/>" + 
      " <DecelerationRate Units=\"m/s/s\" Value=\"12\"/> </SSMovementCharacteristics>" + 
      "</Torpedo>";


    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public LaunchWeaponTest(final String val)
    {
      super(val);
    }

    /**
     * test the string swapping
     */
    public void testSubstitution()
    {
      final String sample = "eerrt$BRG$sdfsdfs$RNG$sdf<TargetType/>sdd";
      final String answer = "eerrt2.1sdfsdfs12.1sdf<TargetType><Type Name=\"RED\"/></TargetType>sdd";


      final WorldDistance rng = new WorldDistance(12.1, WorldDistance.YARDS);
      final Float brg = new Float(2.1f);

      final CoreSensor sensor = new ASSET.Models.Sensor.Initial.OpticSensor(12);
      final ASSET.ParticipantType host = new ASSET.Models.Vessels.SSK(12);
      final ASSET.ParticipantType target = new ASSET.Models.Vessels.Surface(22);

      final DetectionEvent de = new DetectionEvent(0,
                                                   host.getId(),
                                                   null,
                                                   sensor,
                                                   rng,
                                                   rng,
                                                   brg,
                                                   null,
                                                   null,
                                                   null,
                                                   null,
                                                   null,
                                                   target);

      final TargetType getRed = new TargetType();
      getRed.addTargetType(Category.Force.RED);


      final WorldLocation currentLocation = new WorldLocation(1, 1, 0);
      final Status currentStatus = new Status(12, 2000);
      currentStatus.setLocation(currentLocation);

      final String res = LaunchWeapon.swapKeywords(de, currentStatus, sample, getRed);
      assertEquals("strings swapped", answer, res);
    }

    /**
     * test we can read the weapon from file
     */
    public void testReplaceString()
    {
      final String original = "AABBCCDDCCEEFF";
      final String out1 = "AABB___DD___EEFF";
      final String swap_out = "CC";
      final String swap_in = "___";

      assertEquals("Input not found", LaunchWeapon.replaceAll(original, "CA", "bb"), original);
      final String test4 = LaunchWeapon.replaceAll(original, swap_out, swap_in);
      assertEquals("Swapping text worked", test4, out1);


    }

    /**
     * test we can read the weapon from file
     */
    public void testReadWeapon()
    {

      final WorldDistance rng = new WorldDistance(12.1, WorldDistance.YARDS);
      final Float brg = new Float(2.1f);

      final CoreSensor sensor = new ASSET.Models.Sensor.Initial.OpticSensor(12);
      final ASSET.ParticipantType host = new ASSET.Models.Vessels.SSK(12);
      final ASSET.ParticipantType target = new ASSET.Models.Vessels.Surface(22);

      final DetectionEvent de = new DetectionEvent(0,
                                                   host.getId(), null,
                                                   sensor,
                                                   rng,
                                                   rng,
                                                   brg,
                                                   null,
                                                   null,
                                                   null,
                                                   null,
                                                   null,
                                                   target);

      final WorldLocation currentLocation = new WorldLocation(1, 1, 0);
      final Status currentStatus = new Status(12, 2000);
      currentStatus.setLocation(currentLocation);

      final ParticipantType newPart = LaunchWeapon.getWeapon(de, currentStatus, launchBehaviour, null);
      assertNotNull("participant created", newPart);

      final DecisionType dt = newPart.getDecisionModel();
      assertEquals("correct model loaded", "Waterfall", dt.getName());

      System.out.println("part created");
    }

    /**
     * test case for importing a Launch command from XML (because it's fiddly)
     */
    public void NOTtestImportXML()
    {

      /** create the scenario
       *
       */
      final CoreScenario scenario = new CoreScenario();
      scenario.setScenarioStepTime(10000);
      scenario.setTime(0);
      scenario.setName("Testing importing Launch");

      final Waterfall theWaterfall = new Waterfall();

      /** read in the file
       *
       */

      String root = System.getProperty("TEST_ROOT");
      if (root == null)
      {
        System.err.println("Don't know test root, using hard-coded.");
        root = "src";
      }

      final String fName = root + "TEST_LAUNCH.XML";

      // check it exists
      assertTrue("Couldn't find tests data file", new java.io.File(fName).exists());

      try
      {
        //        ASSET.Util.XML.ASSETReaderWriter.importThis(scenario, fName, new java.io.FileInputStream(fName));
        ASSET.Util.XML.ASSETReaderWriter.importThis(theWaterfall, fName, new java.io.FileInputStream(fName));
      }
      catch (java.io.IOException ee)
      {
        ee.printStackTrace();
      }

      final Waterfall newWaterfall = (Waterfall) theWaterfall.getModels().firstElement();
      assertEquals("Read in launch behaviour", newWaterfall.getModels().firstElement().toString(), "Destroy Red");
      final LaunchWeapon theLaunch = (LaunchWeapon) newWaterfall.getModels().firstElement();
      //      final String launchType = theLaunch.getLaunchType();
      assertEquals("Behaviour is correct length", 1719, theLaunch.getLaunchType().length());

    }


    /**
     * test case for launching
     * Ownship: SSN
     * Target: SU
     * <p/>
     * SSN:
     * LaunchWeapon behavior: look for SU target, launch torpedo within 5000 yds
     * Normal behaviour: transit from A to B
     * Sensors: Visual sensor, in continuous contact
     * <p/>
     * SU:
     * Behaviour: Wander around origin at surface at 4 kts
     * <p/>
     * Torpedo:
     * Prosecute Behaviour: Prosecute SU, go bang.
     * Search behaviour: Transit in indicated direction
     */

    // todo: re-instate this test, we're having problems with the launch range

    public void testLaunch()
    {

      final double rngToHim = 5000;
      final double crseToHim = 45;

      final WorldLocation origin = new WorldLocation(0, 0, 0);
      final WorldLocation hisLoc = origin.add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(crseToHim),
                                                              MWC.Algorithms.Conversions.Yds2Degs(rngToHim),
                                                              0));
      final Status blueStat = new Status(13, 0);
      blueStat.setLocation(origin);
      blueStat.setCourse(0);
      blueStat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

      final Status redStat = new Status(blueStat);
      redStat.setLocation(hisLoc);

      // setup a couple of targets
      final ASSET.Models.Vessels.SSN ssn = new ASSET.Models.Vessels.SSN(1);
      ssn.setName("ssn");
      ssn.setMovementChars(SSMovementCharacteristics.generateDebug("scrap", 1, 1, 0, 20, 1, 300, 1, 1, 10, 100));
      ssn.setCategory(new ASSET.Participants.Category(Category.Force.BLUE, Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));
      ssn.setStatus(blueStat);
      final ASSET.Models.Sensor.Initial.OpticSensor periscope = new ASSET.Models.Sensor.Initial.OpticSensor(12);
      ssn.addSensor(periscope);
      RadiatedCharacteristics rc = new RadiatedCharacteristics();
      rc.add(EnvironmentType.VISUAL, new Optic(12, new WorldDistance(12, WorldDistance.METRES)));
      ssn.setRadiatedChars(rc);

      final ASSET.Models.Vessels.Surface su = new ASSET.Models.Vessels.Surface(4);
      su.setName("su");
      su.setMovementChars(SurfaceMovementCharacteristics.generateDebug("scrap", 1, 1, 0.001, 14, 1, 299));
      su.setCategory(new ASSET.Participants.Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.CARRIER));
      su.setStatus(redStat);
      rc = new RadiatedCharacteristics();
      rc.add(EnvironmentType.VISUAL, new Optic(12, new WorldDistance(12, WorldDistance.METRES)));
      su.setRadiatedChars(rc);

      /** create the scenario
       *
       */
      final CoreScenario scenario = new CoreScenario();
      scenario.setScenarioStepTime(10000);
      scenario.setTime(0);
      scenario.setName("Testing weapon launch");

      // get somebody to listen to the tracks
      final ASSET.Scenario.Observers.Recording.DebriefReplayObserver debrief_writer =
        new ASSET.Scenario.Observers.Recording.DebriefReplayObserver("Œtest_reports", null, true, "test observer", true);
      debrief_writer.setup(scenario);

      // DON'T BOTHER RECORDING JUST YET!
      debrief_writer.setActive(false);

      /** now the behaviours
       *
       */
      final Trail trailRed = new Trail(new WorldDistance(2000, WorldDistance.YARDS));
      final TargetType getRed = new TargetType();
      getRed.addTargetType(Category.Force.RED);
      trailRed.setTargetType(getRed);

      final LaunchWeapon launchWeapon = new LaunchWeapon();
      launchWeapon.setTargetType(getRed);
      launchWeapon.setLaunchRange(new WorldDistance(4000, WorldDistance.YARDS));
      launchWeapon.setCoolOffTime(new Duration(Duration.DAYS, 3)); // 24 hr cool off time
      launchWeapon.setLaunchType(launchBehaviour);

      final Waterfall blueWaterfall = new Waterfall();
      blueWaterfall.insertAtFoot(trailRed);
      blueWaterfall.insertAtHead(launchWeapon);
      ssn.setDecisionModel(blueWaterfall);

      scenario.addParticipant(1, ssn);
      scenario.addParticipant(4, su);

      // move forward a while
      boolean found = false;
      double lastRange = -1;
      double thisRange = -1;

      int counter = 0;

      while (!found && counter <= 100000)
      {
        scenario.step();
        final WorldVector sep = ssn.getStatus().getLocation().subtract(su.getStatus().getLocation());
        thisRange = MWC.Algorithms.Conversions.Degs2Yds(sep.getRange());

        if (lastRange == -1)
        {
          lastRange = thisRange;
        }

        if (ssn.getActivity().equals(LaunchWeapon.LAUNCH_MESSAGE))
        {
          found = true;
          System.out.println("LAUNCHED");
        }
        else
        {
          lastRange = thisRange;
        }

        // just do "MAD" check to ensure we don't go on for ever
        counter++;
      }

      // check we didn't time out
      assertTrue(" we just ran to end of loop (no fire)", counter < 100000);

      // check we launched at the right point
      final double launchYds = launchWeapon.getLaunchRange().getValueIn(WorldDistance.YARDS);
      assertTrue("didn't fire too soon", lastRange > launchYds);
      assertTrue("didn't fire too late, got:" + launchYds + " but wanted:" + thisRange, thisRange <= launchYds);

      // so, we've launched the weapon - see how many participants are in the scenario
      assertEquals("New participant created", 3, scenario.getListOfParticipants().length);

      // find the weapon
      //      final ParticipantType torpedo = scenario.getThisParticipant(scenario.getListOfParticipants()[0].intValue());

      // check if the torpedo gets to the target
      while (scenario.getListOfParticipants().length == 3)
      {
        scenario.step();
      }

      // check that the torpedo destroys the SU, and they are both removed
      assertEquals("surface participant destroyed", 1, scenario.getListOfParticipants().length);

      // move a bit further just to give us some more blue track
      for (int ii = 0; ii < 20; ii++)
      {
        scenario.step();
      }

    }
  }
}
