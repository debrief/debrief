package ASSET.GUI.Editors;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Sensor.Initial.BroadbandSensor;
import ASSET.Models.Sensor.SensorDataProvider;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.ParticipantDetectedListener;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

class DetectionViewer extends MWC.GUI.Properties.Swing.SwingCustomEditor
  implements MWC.GUI.Properties.NoEditorButtons,
  ASSET.Participants.ParticipantDetectedListener
{
  ////////////////////////////////////////////////////
  // member objects
  ////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
   * GUI components
   */
  private BorderLayout mainBorder = new BorderLayout();
  private JLabel detectionLabel = new JLabel();
  JList detList = new JList();

  /**
   * the vessel we listen to
   */
  private ASSET.Models.Sensor.SensorDataProvider _sensorProvider;

  /**
   * any pending detections we have made
   * - this allows us to retrieve any existing detections when we
   * first open, then plot them the first time we do a redraw
   */
  DetectionList _pendingDetections = null;


  /**
   * keep our own list of indices used to represent target held
   */
  private Vector<String> _datasetIndices;

  //////////////////////////////////////////////////
  // CONSTRUCTOR
  //////////////////////////////////////////////////

  /**
   * constructor
   */
  public DetectionViewer()
  {
  }


  /**
   * build the interface
   */
  void buildGUI()
  {
    detectionLabel.setHorizontalAlignment(SwingConstants.CENTER);
    detectionLabel.setText("Detections");
    this.setLayout(mainBorder);
    this.add(detectionLabel, BorderLayout.NORTH);
    this.add(detList, BorderLayout.CENTER);
  }

  /**
   * the editor is telling us what we are viewing
   *
   * @param data the vessel to monitor
   */
  public void setObject(final Object data)
  {
    // this is our vessel, start listening to it
    if (data instanceof SensorDataProvider)
    {
      _sensorProvider = (SensorDataProvider) data;
      _sensorProvider.addParticipantDetectedListener(this);

      // also get a copy of the vessel's existing detections, and view them
      _pendingDetections = _sensorProvider.getAllDetections();
    }
  }

  /**
   * have a look for new detections
   *
   * @param list the list of new detections from the vessel
   */
  protected final void updateList(final ASSET.Models.Detection.DetectionList list)
  {
    // clear the existing list
    clearGUI();


    // my working list
    final java.util.Vector<DetectionEvent> vec = new java.util.Vector<DetectionEvent>();

    // do we have any pending detections?
    if (_pendingDetections != null)
    {
      for (int i = 0; i < _pendingDetections.size(); i++)
      {
        DetectionEvent event = (DetectionEvent) _pendingDetections.elementAt(i);
        vec.add(event);
      }

      // finally clear the list
      _pendingDetections = null;
    }

    // now check we have received some data
    if (list != null)
    {
      for (int i = 0; i < list.size(); i++)
      {
        DetectionEvent event = (DetectionEvent) list.elementAt(i);
        vec.add(event);
      }
    }

    // and inform the GUI about this new data
    updateGUI(vec);
  }

  /**
   * the detections are updated, update our list
   *
   * @param vec the new detections
   */
  void updateGUI(final Vector<DetectionEvent> vec)
  {
    // set the data, whether it's empty or not
    detList.setListData(vec);
  }

  /**
   * the scenario has moved forward, clear the GUI
   */
  void clearGUI()
  {
    detList.removeAll();
  }


  /**
   * pass on the list of new detections
   */
  public void newDetections(final DetectionList detections)
  {
    // and update the data
    updateList(detections);
  }

  /**
   * the scenario has restarted
   */
  public void restart(ScenarioType scenario)
  {
    newDetections(null);
  }

  /**
   * handle close event
   */
  public void doClose()
  {
    // do our bit
    _sensorProvider.removeParticipantDetectedListener(this);

    closeGUI();

    // do the parent bit
    super.doClose();
  }

  /**
   * we are closing, ditch the components
   */
  void closeGUI()
  {
    detList.removeAll();
  }

  /**
   * for each combination of dataset & target we want to create a unique dataset number so they
   * are nicely colour coded. Create a hashing code of the two components, put it into a Vector, and
   * use the vector index as the dataset id
   *
   * @param sensorId the id of the sensor for this detection
   * @param targetId the id of the subject target of this detection
   * @return unique index for this permutation
   */
  protected int getDataSetIndexFor(int sensorId, int targetId)
  {
    int res = 0;

    // check we've got our list
    if (_datasetIndices == null)
      _datasetIndices = new Vector<String>(0, 1);

    // create hash-code representing this data item
    String code = "" + sensorId + " " + targetId;

    // do we hold it?
    if (!_datasetIndices.contains(code))
    {
      // nope, better add it
      _datasetIndices.add(code);
    }

    // cool, it must be there now
    res = _datasetIndices.indexOf(code);

    return res;
  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  public static class ViewerTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public ViewerTest(final String val)
    {
      super(val);
    }

    boolean set = false;

    public void testMe()
    {
      // create the object
      final DetectionViewer dv = new DetectionViewer();

      set = false;
      final CoreParticipant cp = new CoreParticipant(12)
      {
        /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void addParticipantDetectedListener(final ParticipantDetectedListener list)
        {
          super.addParticipantDetectedListener(list);
          if (list instanceof DetectionViewer)
          {
            set = true;
          }
        }
      };

      dv.setObject(cp);

      assertTrue("has been set as detected listener", set == true);

      assertEquals("list starts off being empty", dv.detList.getModel().getSize(), 0);

      // try setting the list
      dv.newDetections(null);
      assertEquals("we can set null data", dv.detList.getModel().getSize(), 0);

      final DetectionList dl = new DetectionList();
      dv.newDetections(dl);
      assertEquals("we can set zero length data", dv.detList.getModel().getSize(), 0);

      final BroadbandSensor bb = new BroadbandSensor(122);
      final CoreParticipant target = new ASSET.Models.Vessels.Surface(12);
      target.setName("surfare target");
      dl.add(new DetectionEvent(0, cp.getId(), null, bb, null, null, null, null, null, null, null, null, target));
      dv.newDetections(dl);
      assertEquals("we can set  data", dv.detList.getModel().getSize(), 1);

      // check restart
      dv.restart(null);
      assertEquals("we can do restart", dv.detList.getModel().getSize(), 0);

      System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      System.out.println("POSSIBLE JUNIT PROBLEM - THIS THREAD (DetectionViewer) SHOULD DIE");

      // close down
      dv.doClose();


    }
  }

}