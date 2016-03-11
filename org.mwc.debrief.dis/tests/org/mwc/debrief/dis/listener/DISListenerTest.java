package org.mwc.debrief.dis.listener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mwc.debrief.dis.core.DISModule;
import org.mwc.debrief.dis.core.IDISModule;
import org.mwc.debrief.dis.diagnostics.PduGenerator;
import org.mwc.debrief.dis.diagnostics.TestFixListener;
import org.mwc.debrief.dis.diagnostics.TestFixListener.Item;
import org.mwc.debrief.dis.diagnostics.senders.PassThruPduSender;
import org.mwc.debrief.dis.listeners.IDISCollisionListener;
import org.mwc.debrief.dis.listeners.IDISEventListener;
import org.mwc.debrief.dis.listeners.IDISFireListener;
import org.mwc.debrief.dis.listeners.IDISFixListener;
import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.listeners.IDISScenarioListener;
import org.mwc.debrief.dis.providers.IPDUProvider;
import org.mwc.debrief.dis.providers.dummy.DummyDataProvider;

import edu.nps.moves.dis.Pdu;

public class DISListenerTest
{
  @Test
  public void testCustomSender()
  {
    PduGenerator sender = new PduGenerator();
    DISModule module = new DISModule();
    PassThruPduSender pSender = new PassThruPduSender(module);

    final List<String> collMessages = new ArrayList<String>();
    final List<String> fireMessages = new ArrayList<String>();
    final List<String> fixMessages = new ArrayList<String>();
    final List<String> eventMessages = new ArrayList<String>();

    module.addCollisionListener(new IDISCollisionListener()
    {
      public void add(long time, short eid, int movingId, String movingName,
          int recipientId, String recipientName)
      {
        collMessages.add("collision at:" + time);
      }
    });
    module.addFireListener(new IDISFireListener()
    {
      public void add(long time, short eid, int hisId, String hisName,
          int tgtId, String tgtName, double y, double x, double z)
      {
        fireMessages.add("fire at:" + time);
      }
    });
    module.addFixListener(new IDISFixListener()
    {
      public void add(long time, short exerciseId, long id, String eName,
          short force, short kind, short domain, short category,
          boolean isHighlighted, double dLat, double dLong, double depth, double courseDegs, double speedMS, final int damage)
      {
        fixMessages.add("fix at:" + time);
      }
    });
    module.addEventListener(new IDISEventListener()
    {
      public void add(long time, short exerciseId, long id, String hisName,
          int eventType, String message)
      {
        eventMessages.add("event at:" + time);
      }
    });

    sender.run(pSender, new String[]
    {"10", "6", "80"});

    // check some stuff happened
    assertEquals("got collisions", 2, collMessages.size());
    assertEquals("got firing", 10, fireMessages.size());
    assertEquals("got fixes", 617, fixMessages.size());
    assertEquals("got events", 2, eventMessages.size());
  }

  @Test
  public void testScenarioStateMonitoring()
  {
    IDISModule subject = new DISModule();
    IPDUProvider provider = new DummyDataProvider(3, 5, 3000, 5000);

    TestFixListener fixL = new TestFixListener();

    subject.addFixListener(fixL);
    subject.setProvider(provider);
    final List<String> events = new ArrayList<String>();
    subject.addScenarioListener(new IDISScenarioListener()
    {
      @Override
      public void restart()
      {
        events.add("restarted");
      }

      @Override
      public void complete(String reason)
      {
        events.add("complete");
      }
    });

    provider.attach(null, null);

    assertTrue("received restart", events.contains("restarted"));
    assertTrue("received complete", events.contains("complete"));

  }

  @Test
  public void testActivityMonitoring()
  {
    IDISModule subject = new DISModule();
    IPDUProvider provider = new DummyDataProvider(3, 5, 3000, 5000);

    TestFixListener fixL = new TestFixListener();

    subject.addFixListener(fixL);

    final List<String> events = new ArrayList<String>();

    subject.addGeneralPDUListener(new IDISGeneralPDUListener()
    {

      @Override
      public void logPDU(Pdu pdu)
      {
        events.add("");
      }

      @Override
      public void complete(String reason)
      {
        // TODO Auto-generated method stub

      }
    });

    subject.setProvider(provider);
    provider.attach(null, null);

    assertEquals("got all PDUs", 15, events.size());
  }

  @Test
  public void testESHandling()
  {
    IDISModule subject = new DISModule();
    IPDUProvider provider = new DummyDataProvider(3, 10, 3000, 5000);
    TestFixListener fixL = new TestFixListener();

    subject.addFixListener(fixL);
    subject.setProvider(provider);

    provider.attach(null, null);

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

}
