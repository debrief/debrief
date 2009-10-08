/*
 * Desciption: Observer which stops the scenario once a
 * particular type of vessel has detected a particular type of target
 * User: administrator
 * Date: Nov 6, 2001
 * Time: 10:31:15 AM
 */
package ASSET.Scenario.Observers;

import ASSET.Models.Decision.TargetType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Util.SupportTesting;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.HashSet;

public class DetectionObserver extends WatchParticipantObserver implements
  ASSET.Participants.ParticipantDetectedListener,
  Plottable
{
  /***************************************************************
   *  member variables
   ***************************************************************/

  /**
   * the type of vessel it is looking out for
   */
  private TargetType _targetVessel = null;

  /**
   * the number of vessels we've detected
   */
  protected int _numDetected = 0;

  /**
   * the list of target's we've detected
   */
  protected HashSet<Integer> _myDetections = null;

  /**
   * the (optional) level of detection required
   */
  private Integer _detectionLevel = null;


  /***************************************************************
   *  constructor
   ***************************************************************/

  /**
   * create a detection observer
   *
   * @param watchVessel    the type of vessel we are monitoring
   * @param targetVessel   the type of vessel the monitored vessel is looking for,
   * @param name           the name of this observer
   * @param detectionLevel the (optional) detection level required
   * @param isActive       whether this is observer is active
   */


  public DetectionObserver(final TargetType watchVessel,
                           final TargetType targetVessel,
                           final String name,
                           final Integer detectionLevel,
                           final boolean isActive)
  {
    super(name, isActive, watchVessel);

    // remember the target types
    _targetVessel = targetVessel;
    _detectionLevel = detectionLevel;
  }

  /**
   * ************************************************************
   * member methods
   * *************************************************************
   */


  /**
   * return the calculated result for the batch processing
   *
   * @return string to be used in results collation
   */
  protected Number getBatchResult()
  {
    Number res = new Integer(0);
    if (_myDetections != null)
      res = new Integer(_myDetections.size());
    return res;
  }


  /**
   * ok, this vessel matches what we're looking for.  start listening to it
   *
   * @param newPart
   */
  protected void listenTo(final ParticipantType newPart)
  {
    // add as detection listener
    newPart.addParticipantDetectedListener(this);
  }


  //////////////////////////////////////////////////
  // scenario processing/listening
  //////////////////////////////////////////////////


  /**
   * get the types of vessel whose proximity we are checking for (targets)
   */
  public TargetType getTargetType()
  {
    return _targetVessel;
  }

  /**
   * get the scenario
   */
  protected ScenarioType getScenario()
  {
    return _myScenario;
  }


  /***************************************************************
   *  listen for detections
   ***************************************************************/
  /**
   * pass on the list of new detections
   */
  public void newDetections(final DetectionList detections)
  {
    // check we are active
    if (isActive())
    {

      // step through detections
      for (int i = 0; i < detections.size(); i++)
      {
        final DetectionEvent thisD = detections.getDetection(i);

        //
        boolean passedDetectionLevelTest = true;

        // do we mind what the detection level is?
        if (_detectionLevel != null)
        {
          if (thisD.getDetectionState() >= _detectionLevel.intValue())
          {
            // great, we can continue.
          }
          else
          {
            // bugger, invalid detection. drop out
            passedDetectionLevelTest = false;
          }
        }

        // are we ready to proceed?
        if (passedDetectionLevelTest)
        {
          // is this the type we are watching for?
          if (_targetVessel.matches(thisD.getTargetType()))
          {
            // increment counter
            _numDetected++;

            // process this valid event
            validDetection(thisD);

            // update our data
            if (_myEditor != null)
              _myEditor.fireChanged(this, "Removed", null, new Integer(_numDetected));
          }
        }
      }
    }
  }

  /**
   * valid detection happened, process it
   */
  protected void validDetection(final DetectionEvent detection)
  {

    // get the target
    final int tgt = detection.getTarget();
    final Integer thisI = new Integer(tgt);

    if ((_myDetections != null) && (_myDetections.contains(thisI)))
    {
      // ok, we've detected it already
    }
    else
    {
      // add this to our list
      if (_myDetections == null)
      {
        _myDetections = new HashSet<Integer>();
      }

      _myDetections.add(thisI);
      // set the size
      _numDetected = _myDetections.size();
    }

  }

  /***************************************************************
   *  handle participants being added/removed
   ***************************************************************/


  /***************************************************************
   * plottable props
   ***************************************************************/

  /**
   * the scenario has restarted
   */
  public void restart(ScenarioType scenario)
  {	
  	super.restart(scenario);
  	
    _numDetected = 0;
    if (_myDetections != null)
    {
      _myDetections.clear();
      _myDetections = null;
    }
  }


  /**
   * method indicating that we are to stop listening to this participant instance
   *
   * @param thisPart
   */
  protected void stopListeningTo(ParticipantType thisPart)
  {
    thisPart.removeParticipantDetectedListener(this);
  }


  /**
   * paint this object to the specified canvas
   */
  public void paint(CanvasType dest)
  {
    //
  }

  /**
   * find the data area occupied by this item
   */
  public WorldArea getBounds()
  {
    return null;
  }

  /**
   * Determine how far away we are from this point.
   * or return null if it can't be calculated
   */
  public double rangeFrom(WorldLocation other)
  {
    return -1;
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
      _myEditor = new DetectionInfo(this, getName());

    return _myEditor;
  }


  //////////////////////////////////////////////////
  // accessors
  //////////////////////////////////////////////////

  /**
   * how many targets have we deleted?
   */
  public int getNumDetected()
  {
    return _numDetected;
  }

  public void setNumDetected(final int numDetected)
  {
    _numDetected = numDetected;
  }

  /**
   * get the (optional)level of detection required to mark detection achieved
   *
   * @return the level (or null if we don't care about the level)
   * @see DetectionEvent.UNDETECTED
   */
  public Integer getDetectionLevel()
  {
    return _detectionLevel;
  }

  /**
   * set the (optional)level of detection required to mark detection achieved
   *
   * @param detectionLevel
   * @see DetectionEvent.UNDETECTED
   */
  public void setDetectionLevel(Integer detectionLevel)
  {
    this._detectionLevel = detectionLevel;
  }

  //////////////////////////////////////////////////////
  // bean info for this class
  /////////////////////////////////////////////////////
  public class DetectionInfo extends Editable.EditorType
  {

    public DetectionInfo(final DetectionObserver data, final String name)
    {
      super(data, name, "Count detections");
    }

    public String getName()
    {
      return DetectionObserver.this.getName();
    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res = {
          prop("Name", "the name of this observer"),
          prop("Active", "whether this listener is active"),
        };
        return res;
      }
      catch (IntrospectionException e)
      {
        System.out.println("::" + e.getMessage());
        return super.getPropertyDescriptors();
      }
    }
  }


  public static class StopOnDetectionObserver extends DetectionObserver
  {
    /***************************************************************
     *  constructor
     ***************************************************************/

    /**
     * create a detection observer
     *
     * @param watchVessel  the type of vessel we are monitoring
     * @param targetVessel the type of vessel the monitored vessel is looking for,
     * @param name
     */
    public StopOnDetectionObserver(final TargetType watchVessel,
                                   final TargetType targetVessel,
                                   final String name,
                                   final Integer detectionLevel,
                                   final boolean isActive)
    {
      super(watchVessel, targetVessel, name, detectionLevel, isActive);
    }

    /**
     * valid detection happened, process it
     */
    protected void validDetection(DetectionEvent detection)
    {
      _myScenario.stop("Stopped on detection:" + getName());
    }
  }

  /**
   * observer which will stop the scenario if a detection of a valid target happens when the  two
   * participants are within the specified range
   */
  public static class StopOnProximityDetectionObserver extends StopOnDetectionObserver
  {
    private WorldDistance _stopRange = null;

    /***************************************************************
     *  constructor
     ***************************************************************/

    /**
     * create a detection observer
     *
     * @param watchVessel    the type of vessel we are monitoring
     * @param targetVessel   the type of vessel the monitored vessel is looking for,
     * @param cutOffRange    the detection range at which we stop the scenario
     * @param name
     * @param detectionLevel the level of detection required of for this test
     * @param isActive       whether this observer is active
     */
    public StopOnProximityDetectionObserver(final TargetType watchVessel,
                                            final TargetType targetVessel,
                                            final WorldDistance cutOffRange,
                                            final String name,
                                            final Integer detectionLevel,
                                            final boolean isActive)
    {
      super(watchVessel, targetVessel, name, detectionLevel, isActive);
      _stopRange = cutOffRange;
    }

    /**
     * valid detection happened, process it
     */
    protected void validDetection(DetectionEvent detection)
    {
      // do we have a cutoff range?
      if (detection.getRange().lessThan(_stopRange))
      {
        _myScenario.stop("Stopped on proximity detection:" + getName());
      }
    }

    /**
     * get the range at which we stop
     *
     * @return the stop range
     */
    public WorldDistance getRange()
    {
      return _stopRange;
    }

    public void setRange(WorldDistance stopRange)
    {
      this._stopRange = stopRange;
    }
    //////////////////////////////////////////////////
    // property editing
    //////////////////////////////////////////////////

    private EditorType _myEditor1;

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
    public EditorType getInfo()
    {
      if (_myEditor1 == null)
        _myEditor1 = new StopOnProximityDetectionObserverInfo(this);

      return _myEditor1;
    }

    //////////////////////////////////////////////////
    // editable properties
    //////////////////////////////////////////////////
    static public class StopOnProximityDetectionObserverInfo extends EditorType
    {


      /**
       * constructor for editable details
       *
       * @param data the object we're going to edit
       */
      public StopOnProximityDetectionObserverInfo(final StopOnProximityDetectionObserver data)
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
            prop("Name", "the name of this "),
            prop("Active", "whether this listener is active"),
            prop("Range", "the range to stop at"),

          };
          return res;
        }
        catch (IntrospectionException e)
        {
          System.out.println("::" + e.getMessage());
          return super.getPropertyDescriptors();
        }
      }


    }

    //////////////////////////////////////////////////
    // property testing
    //////////////////////////////////////////////////
    public static class ObserverTest extends SupportTesting.EditableTesting
    {
      /**
       * get an object which we can test
       *
       * @return Editable object which we can check the properties for
       */
      public Editable getEditable()
      {
        return new DetectionObserver(null, null, "", null, true);
      }
    }

    //////////////////////////////////////////////////
    // property testing
    //////////////////////////////////////////////////
    public static class DetObserver2Test extends SupportTesting.EditableTesting
    {
      /**
       * get an object which we can test
       *
       * @return Editable object which we can check the properties for
       */
      public Editable getEditable()
      {
        return new StopOnDetectionObserver(null, null, null, null, true);
      }
    }

    //////////////////////////////////////////////////
    // property testing
    //////////////////////////////////////////////////
    public static class DetObserver3Test extends SupportTesting.EditableTesting
    {
      /**
       * get an object which we can test
       *
       * @return Editable object which we can check the properties for
       */
      public Editable getEditable()
      {
        return new StopOnProximityDetectionObserver(null, null, null, null, null, true);
      }
    }


  }
}
