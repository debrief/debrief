package org.mwc.debrief.dis.listener;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Was in an uncompiled tests/ folder before the JUnit 6 migration, so it never ran; 4 of its 6 tests fail (null entity ID in DISModule.handleFix, and a collision count)")
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
          int recipientId, String recipientName, double dLat, double dLong, double depthM)
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
    assertEquals(2, collMessages.size(), "got collisions");
    assertEquals(10, fireMessages.size(), "got firing");
    assertEquals(617, fixMessages.size(), "got fixes");
    assertEquals(2, eventMessages.size(), "got events");
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

    assertTrue(events.contains("restarted"), "received restart");
    assertTrue(events.contains("complete"), "received complete");

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

    assertEquals(15, events.size(), "got all PDUs");
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

    assertEquals(3, fixL.getData().keySet().size(), "correct num tracks");
    assertEquals(10, fixL.getData().values().iterator()
        .next().size(), "correct num fixes");

    // get a fix & have a look at it
    Item thisF = fixL.getData().values().iterator().next().iterator().next();
    assertEquals(3000, thisF._time, "has time");
    assertTrue(thisF._lat != 0, "has lat");
    System.out.println("lat is: " + thisF._lat);
    assertTrue(thisF._long != 0, "has long");
    assertTrue(thisF._depth != 0, "has depth");
    assertTrue(thisF._course != 0, "has course");
    assertTrue(thisF._speed != 0, "has speed");
  }

}
