package org.mwc.debrief.dis.listener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.mwc.debrief.dis.listener.DISListenerTest.TestFixListener.Item;

import edu.nps.moves.dis.EntityID;
import edu.nps.moves.dis.EntityStatePdu;
import edu.nps.moves.dis.Orientation;
import edu.nps.moves.dis.Pdu;
import edu.nps.moves.dis.Vector3Double;
import edu.nps.moves.dis.Vector3Float;

public class DISListenerTest
{

  @Test
  public void testConfig()
  {
    IDISModule subject = new DISModule();
    IDISNetworkPrefs prefs = new TestPrefs();
    subject.setPrefs(prefs);
    assertNotNull(subject.getPrefs());
    assertNotNull(subject);
  }

  @Test
  public void testESHandling()
  {
    IDISModule subject = new DISModule();
    IDISNetworkPrefs prefs = new TestPrefs();
    IPDUProvider provider = new DummyData(3, 10, 3000, 5000);
    TestFixListener fixL = new TestFixListener();

    subject.setPrefs(prefs);
    subject.addFixListener(fixL);
    subject.setProvider(provider);

    assertEquals("correct num tracks", 3, fixL.getData().keySet().size());
    assertEquals("correct num fixes", 10, fixL.getData().values().iterator()
        .next().size());

    // get a fix & have a look at it
    Item thisF = fixL.getData().values().iterator().next().iterator().next();
    assertEquals("has time", 3000, thisF._time);
    assertTrue("has lat", thisF._lat != 0);
    System.out.println("lat is: " + thisF._lat);
    assertTrue("has long", thisF._long != 0);
    assertTrue("has depth", thisF._depth != 0);
    assertTrue("has course", thisF._course != 0);
    assertTrue("has speed", thisF._speed != 0);
  }

  static class TestFixListener implements IDISFixListener
  {
    HashMap<Long, List<Item>> _items = new HashMap<Long, List<Item>>();

    static class Item
    {
      public long _id;
      public long _time;
      public double _lat;
      public double _long;
      public double _depth;
      public double _course;
      public double _speed;
    }

    public Map<Long, List<Item>> getData()
    {
      return _items;
    }

    public Set<Long> getTracks()
    {
      return _items.keySet();
    }

    @Override
    public void add(long time, long id, double dLat, double dLong,
        double depth, double courseDegs, double speedMS)
    {
      Item newI = new Item();
      newI._id = id;
      newI._time = time;
      newI._lat = dLat;
      newI._long = dLong;
      newI._depth = depth;
      newI._course = courseDegs;
      newI._speed = speedMS;

      List<Item> thisL = _items.get(id);
      if (thisL == null)
      {
        thisL = new ArrayList<Item>();
        _items.put(id, thisL);
      }

      thisL.add(newI);
    }

  }
  
  /** data provider that listens for data on a network
   * multicast socket
   * @author Ian
   *
   */
  static class DISProvider implements IPDUProvider
  {
    public DISProvider(IDISNetworkPrefs prefs)
    {
      
    }
    
    /** start listening
     * 
     */
    public void connect()
    {
      
    }
    
    /** stop listening
     * 
     */
    public void disconnect()
    {
      
    }

    @Override
    public boolean hasMoreElements()
    {
      // TODO check if anything is waiting
      return false;
    }

    @Override
    public Pdu next()
    {
      // TODO return the next item off the queue
      return null;
    }
    
  }

  static class DummyData implements IPDUProvider
  {

    private int ctr = 0;
    final private int _numTracks;
    final private int _numPoints;
    final private long _timeStep;
    private long _timeNow;

    /**
     * 
     * @param num
     *          how many data points to generate
     * @param i
     */
    public DummyData(int numTracks, int numPoints, long timeNow, long timeStep)
    {
      _numTracks = numTracks;
      _numPoints = numPoints;
      _timeNow = timeNow;
      _timeStep = timeStep;
    }

    @Override
    public boolean hasMoreElements()
    {
      return ctr < (_numPoints * _numTracks);
    }

    HashMap<Integer, EntityStatePdu> dummyStates =
        new HashMap<Integer, EntityStatePdu>();

    @Override
    public Pdu next()
    {
      // create
      int hisId = (int) ctr % _numTracks;

      if (hisId == 0 && ctr != 0)
      {
        _timeNow += _timeStep;
      }

      // ok, where is he?
      EntityStatePdu lastLoc = dummyStates.get(hisId);
      if (lastLoc == null)
      {
        lastLoc = new EntityStatePdu();
        dummyStates.put(hisId, lastLoc);

        // and pre-populate it
        EntityID eId = new EntityID();
        eId.setEntity(hisId);
        lastLoc.setEntityID(eId);
        lastLoc
            .setTimestamp(1000 + ((long) (Math.random() * 1000) / 100 * 100));
        Vector3Double eLoc = new Vector3Double();
        lastLoc.setEntityLocation(eLoc);
        eLoc.setX((1 + hisId) * 1000);
        eLoc.setY((1 + hisId) * 100);
        eLoc.setZ(-1 + hisId);

        Orientation theO = new Orientation();
        theO.setPhi(30 * (1 + hisId));
        lastLoc.setEntityOrientation(theO);

        Vector3Float linearVel = new Vector3Float();
        linearVel.setX((float) Math.cos(theO.getPhi()));
        linearVel.setY((float) Math.sin(theO.getPhi()));
        lastLoc.setEntityLinearVelocity(linearVel);

      }

      // create our PDU
      EntityStatePdu res = new EntityStatePdu();

      res.setEntityID(lastLoc.getEntityID());

      // plot on fromt the last location
      Vector3Double newLoc = new Vector3Double();
      Vector3Double oldLoc = lastLoc.getEntityLocation();
      Vector3Float oldVel = lastLoc.getEntityLinearVelocity();
      newLoc.setX(oldLoc.getX() + oldVel.getX() * (_timeStep / 1000d));
      newLoc.setY(oldLoc.getY() + oldVel.getY() * (_timeStep / 1000d));
      newLoc.setZ(oldLoc.getZ() + oldVel.getZ() * (_timeStep / 1000d));

      res.setEntityLocation(newLoc);
      res.setEntityLinearVelocity(oldVel);
      res.setEntityOrientation(lastLoc.getEntityOrientation());

      res.setTimestamp(_timeNow);

      // increment counter
      ctr++;

      // done
      return res;

    }

  }

  interface IPDUProvider
  {
    /**
     * flag for if more PDUs are avaialble
     * 
     * @return
     */
    boolean hasMoreElements();

    /**
     * retrieve the next PDU
     * 
     * @return
     */
    Pdu next();
  }

  class TestScenarioHandler implements IDISScenarioListener
  {

    @Override
    public void restart(boolean newPlot)
    {
      // TODO Auto-generated method stub

    }

    @Override
    public void complete()
    {
      // TODO Auto-generated method stub

    }

  }

  static class TestPrefs implements IDISNetworkPrefs
  {
    final static String IP = "127.0.0.1";
    static final int PORT = 2000;

    @Override
    public String getIPAddress()
    {
      return IP;
    }

    @Override
    public int getPort()
    {
      return PORT;
    }

  }

  static interface IDISNetworkPrefs
  {
    String getIPAddress();

    int getPort();
  }

  static interface IDISPerformance
  {

  }

  static interface IDISModule
  {
    void addFixListener(IDISFixListener handler);

    void setProvider(IPDUProvider provider);

    Object getPrefs();

    void addScenarioListener(IDISScenarioListener handler);

    void setPrefs(IDISNetworkPrefs prefs);
  }

  static interface IDebriefDISListener
  {
  }

  static interface IDISScenarioListener
  {
    /**
     * we're starting a new scenario
     * 
     * @param newPlot
     *          if true, a new plot will be started, else clear this one and re-use it
     */
    void restart(boolean newPlot);

    /**
     * we've been told that the scenario has completed. we may wish to modify the UI or data
     * accordingly.
     */
    void complete();
  }

  static interface IDISFixListener
  {
    void add(long id, long time, double dLat, double dLong, double depth,
        double courseDegs, double speedMS);
  }

}
