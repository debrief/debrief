package org.mwc.debrief.dis.diagnostics;

import java.util.Properties;

import org.mwc.debrief.dis.DisActivator;
import org.mwc.debrief.dis.core.DISModule;
import org.mwc.debrief.dis.core.IDISModule;
import org.mwc.debrief.dis.diagnostics.file.CollisionFileListener;
import org.mwc.debrief.dis.diagnostics.file.DetonateFileListener;
import org.mwc.debrief.dis.diagnostics.file.EventFileListener;
import org.mwc.debrief.dis.diagnostics.file.FireFileListener;
import org.mwc.debrief.dis.diagnostics.file.FixToFileListener;
import org.mwc.debrief.dis.diagnostics.file.StopFileListener;
import org.mwc.debrief.dis.diagnostics.senders.NetworkPduSender;
import org.mwc.debrief.dis.listeners.IDISFixListener;
import org.mwc.debrief.dis.listeners.IDISStopListener;
import org.mwc.debrief.dis.providers.IPDUProvider;
import org.mwc.debrief.dis.providers.network.CoreNetPrefs;
import org.mwc.debrief.dis.providers.network.IDISNetworkPrefs;
import org.mwc.debrief.dis.providers.network.NetworkDISProvider;

import edu.nps.moves.dis.EntityID;

public class HeadlessDISLogger
{

  private static final int SITE_ID = 778;
  private static final int APPLICATION_ID = 777;
  private boolean _terminated = false;
  private EntityID _ourID;

  public static void main(String[] args)
  {
    // start running
    new HeadlessDISLogger(args);
  }

  public HeadlessDISLogger(String[] args)
  {

    // setup the ID
    _ourID = new EntityID();
    _ourID.setApplication((short) APPLICATION_ID);
    _ourID.setSite((short) SITE_ID);
    
    // do we have a root?
    String root = System.getProperty("java.io.tmpdir");

    // do we have an IP address?
    String address = NetworkPduSender.DEFAULT_MULTICAST_GROUP;

    // do we have a PORT?
    int port = NetworkPduSender.PORT;

    // All system properties, passed in on the command line via
    // -Dattribute=value
    Properties systemProperties = System.getProperties();
    // IP address we send to
    String destinationIpString = systemProperties.getProperty("group");
    // Port we send to, and local port we open the socket on
    String portString = systemProperties.getProperty("port");

    // Port we send to, and local port we open the socket on
    String rootString = systemProperties.getProperty("root");

    if (destinationIpString != null)
    {
      address = destinationIpString;
    }
    if (portString != null)
    {
      port = Integer.parseInt(portString);
    }
    if (rootString != null)
    {
      root = rootString;
    }

    // setup the output destinations
    boolean toFile = true;
    boolean toScreen = true;

    if (toFile)
    {
      System.out.println("Writing datafiles to:" + root);
    }

    IDISModule subject = new DISModule();
    IDISNetworkPrefs netPrefs = new CoreNetPrefs(address, port);
    final IPDUProvider provider = new NetworkDISProvider(netPrefs);
    subject.setProvider(provider);

    // setup our loggers
    subject.addFixListener(new FixToFileListener(root, toFile, false));
    subject.addStopListener(new StopFileListener(root, toFile, toScreen));
    subject.addDetonationListener(new DetonateFileListener(root, toFile,
        toScreen));
    subject.addEventListener(new EventFileListener(root, toFile, toScreen));
    subject.addFireListener(new FireFileListener(root, toFile, toScreen));
    subject.addCollisionListener(new CollisionFileListener(root, toFile,
        toScreen));

    // output dot marker to screen, to demonstrate progress
    subject.addFixListener(new IDISFixListener()
    {
      @Override
      public void add(long time, short exerciseId, long id, String eName,
          short force, double dLat, double dLong, double depth,
          double courseDegs, double speedMS, final int damage)
      {
        System.out.print(".");
      }
    });

    // listen out for stop, so we can shut down.
    subject.addStopListener(new IDISStopListener()
    {
      @Override
      public void stop(long time,int appId, short eid, short reason)
      {
        System.out.println("== STOP RECEIVED ==");
        provider.detach();
        _terminated = true;
        System.exit(0);
      }
    });

    // tell the network provider to start
    provider.attach(null, _ourID);

    // get looping
    while (!_terminated)
    {
      // stay alive!
    }

  }
  
}
