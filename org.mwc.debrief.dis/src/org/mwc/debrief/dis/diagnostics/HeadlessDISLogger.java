package org.mwc.debrief.dis.diagnostics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.mwc.debrief.dis.core.DISModule;
import org.mwc.debrief.dis.core.IDISModule;
import org.mwc.debrief.dis.diagnostics.senders.NetworkPduSender;
import org.mwc.debrief.dis.listeners.IDISDetonationListener;
import org.mwc.debrief.dis.listeners.IDISEventListener;
import org.mwc.debrief.dis.listeners.IDISFixListener;
import org.mwc.debrief.dis.listeners.IDISStopListener;
import org.mwc.debrief.dis.providers.IPDUProvider;
import org.mwc.debrief.dis.providers.network.CoreNetPrefs;
import org.mwc.debrief.dis.providers.network.IDISNetworkPrefs;
import org.mwc.debrief.dis.providers.network.NetworkDISProvider;

public class HeadlessDISLogger
{

  final String LINE_BREAK = System.getProperty("line.separator");

  private boolean _terminated = false;

  public static void main(String[] args)
  {
    // start running
    new HeadlessDISLogger(args);
  }

  public HeadlessDISLogger(String[] args)
  {

    // do we have a root?
    String root = System.getProperty("java.io.tmpdir");

    // do we have an IP address?
    String address = NetworkPduSender.DEFAULT_MULTICAST_GROUP;

    // do we have a PORT?
    int port = NetworkPduSender.PORT;

    // TODO: retrieve the above params from the args

    // TODO: add support for outputting the expected args

    // setup the output destinations
    boolean toFile = true;
    boolean toScreen = false;

    IDISModule subject = new DISModule();
    IDISNetworkPrefs netPrefs = new CoreNetPrefs(address, port);
    IPDUProvider provider = new NetworkDISProvider(netPrefs);
    subject.setProvider(provider);

    // setup our loggers
    subject.addFixListener(new FixToFileListener(root, toFile, toScreen));
    subject.addStopListener(new IDISStopListener()
    {
      @Override
      public void stop(long time, short eid, short reason)
      {
        System.out.println("STOP: time:" + time + " eid:" + eid + " reason:"
            + reason);
        _terminated = true;
      }
    });
    subject.addDetonationListener(new IDISDetonationListener()
    {
      @Override
      public void add(long time, short eid, int hisId, double dLat,
          double dLon, double depth)
      {
        System.out.println("DETONATION: time:" + time + " eid:" + eid
            + " hisID:" + hisId + " lat:" + dLat + " lon:" + dLon + " depth:"
            + depth);
      }
    });
    subject.addEventListener(new IDISEventListener()
    {
      @Override
      public void add(long time, short exerciseId, long id, String msg)
      {
        System.out.println("EVENT: time: " + time + " eid:" + exerciseId
            + " hisId:" + id + " msg:" + msg);
      }
    });
    subject.addFixListener(new IDISFixListener()
    {
      @Override
      public void add(long time, short exerciseId, long id, double dLat,
          double dLong, double depth, double courseDegs, double speedMS, final int damage)
      {
        System.out.print(".");
        // System.out.println("STATE: time:" + time + " eid:" + exerciseId
        // + " entity:" + id + " dLat:" + dLat + " dLon:" + dLong + " depth:"
        // + depth + " course:" + courseDegs + " speed" + speedMS);
      }
    });

    // tell the network provider to start
    provider.attach();

    // ok, run the ESPDU pusher
    PduGenerator.main(new String[]
    {"100", "3", "100"});

    while (!_terminated)
    {
      // stay alive!
    }

  }

  protected class CoreFileListener
  {
    final private String _path;
    private FileWriter _outF;
    final private boolean _toFile;
    final private boolean _toScreen;
    private String _filename;
    private boolean _firstStep = true;
    private String _header;

    /**
     * 
     * @param root
     *          the path for the output file
     * @param toFile
     *          whether to write to file
     * @param toScreen
     *          whether to write to standard output
     */
    private CoreFileListener(String root, boolean toFile, boolean toScreen,
        String filename, String header)
    {
      _path = root;
      _filename = filename;
      _toFile = toFile;
      _toScreen = toScreen;
      _header = header;
    }

    protected void write(String output)
    {
      if (_toScreen)
      {
        // do we need the header?
        if (_firstStep)
        {
          System.out.println(_header);
        }

        // ok, and the normal line
        System.out.print(output);
      }

      if (_toFile)
      {
        // is our file created?
        try
        {
          if (_outF == null)
          {
            // write the header
            createOut(_filename, _header);
          }

          // and our output
          _outF.write(output);

          // flush - so we have as many lines in there as possible
          _outF.flush();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }

    protected void createOut(String filename, String header) throws IOException
    {
      // ok, create it
      _outF = new FileWriter(new File(_path, filename));

      // and insert the header line
      _outF.write(header);
      _outF.write(LINE_BREAK);
    }
  }

  protected class FixToFileListener extends CoreFileListener implements
      IDISFixListener
  {

    public FixToFileListener(String root, boolean toFile, boolean toScreen)
    {
      super(root, toFile, toScreen, "fix.csv",
          "time, id, dLat, dLong, depth, courseDegs, speedMS, damage");
    }

    @Override
    public void add(long time, short exerciseId, long id, double dLat,
        double dLong, double depth, double courseDegs, double speedMS, int damage)
    {
      // create the line
      StringBuffer out = new StringBuffer();
      out.append(time);
      out.append(", ");
      out.append(id);
      out.append(", ");
      out.append(dLat);
      out.append(", ");
      out.append(dLong);
      out.append(", ");
      out.append(depth);
      out.append(", ");
      out.append(courseDegs);
      out.append(", ");
      out.append(speedMS);
      out.append(", ");
      out.append(damage);
      out.append(LINE_BREAK);

      // done, write it
      write(out.toString());
    }

  }
}
