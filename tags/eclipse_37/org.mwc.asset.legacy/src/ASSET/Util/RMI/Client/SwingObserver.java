/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: 17-Mar-02
 * Time: 15:43:21
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.RMI.Client;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ASSET.Participants.Status;
import ASSET.Util.RMI.ParticipantRMI;
import ASSET.Util.RMI.ScenarioRMI;
import ASSET.Util.RMI.ServerRMI;
import ASSET.Util.XML.ASSETReaderWriter;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Layers;
import MWC.GUI.Chart.Swing.SwingChart;
import MWC.GUI.Tools.Chart.Swing.SwingCursorPosition;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

public class SwingObserver extends ObserverBaseImpl
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*****************************************************************
   * member variables
   ****************************************************************/
  /** the main frame
   *
   */
  private JFrame _myFrame;

  /** the list of scenarios in this server
   *
   */
  JList _scenarioList;

  /** the list of participants in this scenario
   *
   */
  JList _participantList;

  /** the list of compliant servers we've found
   *
   */
  JList _serverList;

  /** the name of the server to connect to
   *
   */
  JTextField _serverName;

  /** the name of the text field we paint into
   *
   */
  private JTextArea _statusArea;

  /** the current time
   *
   */
  private JLabel _timeField;

  /** the canvas we write to
   *
   */
  private SwingChart _theChart;

  /** the data we are plotting
   *
   */
  private Layers _theData;

  /** the participant painter layer
   *
   */
  private ParticipantPainter _thePainter;

  /*****************************************************************
   * constructor
   ****************************************************************/
  private SwingObserver() throws RemoteException
  {
    super();

    buildGUI();

    // just have a go at importing the default layers
    final BaseLayer chartFeatures = new BaseLayer();
    chartFeatures.setName("Chart Features");
    chartFeatures.setBuffered(true);
    _theData.addThisLayer(chartFeatures);


    // and insert the initial layers
    createInitialLayers();
  }

  /*****************************************************************
   * member methods
   ****************************************************************/

  private void createInitialLayers()
  {
    // have a go at inserting the default layers
    // start with the chart
    final BaseLayer decs = new BaseLayer();
    decs.setName("Decs");
    _theData.addThisLayer(decs);
    try
    {
      final java.io.File defLayers = new java.io.File("default_layers.xml");
      ASSETReaderWriter.importThis(_theData, new java.io.FileInputStream(defLayers));
    }
    catch(java.io.FileNotFoundException fe)
    {
      fe.printStackTrace();
    }

    // add our participant painter
    _thePainter = new ParticipantPainter();
    _theData.addThisLayer(_thePainter);
  }

  private String getTitle()
  {
    return("ASSET Observer");
  }

  private void buildGUI()
  {
    _myFrame = new JFrame();
    _myFrame.setTitle(getTitle());

    // listen to the frame closing, so that we can do a disconnect first
    _myFrame.addWindowListener(new java.awt.event.WindowAdapter()
                               {
      public void windowClosing(final WindowEvent e)
      {
        disconnect();
        super.windowClosing(e);
      }
    });


    final JPanel leftHand = new JPanel();
    leftHand.setLayout(new GridLayout(0,1));

    final JPanel connection = new JPanel();
    connection.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    connection.setLayout(new GridLayout(0,2));
    connection.add(new JLabel("Server:"));
    _serverName = new JTextField("");
    _serverName.setColumns(12);
    connection.add(_serverName);

    _serverList = new JList();
    _serverList.addListSelectionListener(new ListSelectionListener()
    {
      public void valueChanged(final ListSelectionEvent e)
      {
        // retrieve the value
        if(!e.getValueIsAdjusting())
        {
          final StubWrapper selection = (StubWrapper)_serverList.getSelectedValue();
          if(selection != null)
          {
            _serverName.setText(selection.name);
          }
        }
      }
    });

    final JButton pingBtn = new JButton("Ping");
    pingBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        doPing();
      }
    });
    connection.add(pingBtn);
    final JButton connectBtn = new JButton("Connect");
    connectBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        final String srvName = _serverName.getText();
        connect(srvName);
      }
    });
    connection.add(connectBtn);
    final JButton disconnectBtn = new JButton("Disconnect");
    disconnectBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        disconnect();
      }
    });
    connection.add(disconnectBtn);
    final JButton quitBtn = new JButton("Quit");
    quitBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        disconnect();
        System.exit(0);
      }
    });
    connection.add(quitBtn);

    // and the scenario list
    final JPanel scenarios = new JPanel();
    scenarios.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    _scenarioList = new JList();
    final JList localList = _scenarioList;
    _scenarioList.addListSelectionListener(new ListSelectionListener()
    {
      public void valueChanged(final ListSelectionEvent e)
      {
        // retrieve the value
        if(!e.getValueIsAdjusting())
        {
          final StubWrapper selection = (StubWrapper)localList.getSelectedValue();
          if(selection != null)
          {
            final ScenarioRMI newVal = (ScenarioRMI)selection.value;
            scenarioSelected(newVal);
          }
        }
      }
    });
    scenarios.add(_scenarioList);

    // and the participant list
    final JPanel participants = new JPanel();
    participants.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    _participantList = new JList();
    _participantList.addListSelectionListener(new ListSelectionListener()
    {
      public void valueChanged(final ListSelectionEvent e)
      {
        // retrieve the value
        if(!e.getValueIsAdjusting())
        {
          final StubWrapper selection = (StubWrapper)_participantList.getSelectedValue();
          if(selection != null)
          {
            final ParticipantRMI newVal = (ParticipantRMI)selection.value;
            participantSelected(newVal);
            update();
          }
        }
      }
    });
    participants.add(_participantList);


    // and the time value
    final JPanel times = new JPanel();
    times.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    _timeField = new JLabel("00:00:00");
    times.add(new JLabel("Time:"));
    times.add(_timeField);

    // and the status area
    final JPanel statuses = new JPanel();
    statuses.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    _statusArea = new JTextArea("Blank", 4, 20);
    statuses.add(_statusArea);



    // and the right hand
    _theData = new Layers();
    _theChart = new SwingChart(_theData);

    // and configure the tote
    final JLabel cursorPos = new JLabel("000 00 00.00 N 000 00 00.00W");
    _theChart.addCursorMovedListener(new SwingCursorPosition(_theChart, cursorPos));

    // collate the left hand
    leftHand.add(connection);
    leftHand.add(_serverList);
    leftHand.add(scenarios);
    leftHand.add(participants);
    leftHand.add(times);
    leftHand.add(statuses);
    leftHand.add(cursorPos);



    // collate the frame
    final JSplitPane theSplit = new JSplitPane();
    theSplit.setLeftComponent(leftHand);
    theSplit.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    theSplit.setRightComponent(_theChart.getPanel());
    _myFrame.getContentPane().add(theSplit);
    _myFrame.setSize(700, 500);
    _myFrame.setLocation(500, 300);
    _myFrame.setVisible(true);
    _myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  }

  void connect(final String server_name)
  {
    super.connect(server_name);

    // clear the list of scenarios
    _scenarioList.removeAll();

    // did we make it?
    if(_myServer != null)
    {
      _scenarioList.setListData(_theScenarios);
      _scenarioList.updateUI();
    }
  }

  public void updateScenarioGUI()
  {
    super.updateScenarioGUI();

    // clear the list of participants
    _scenarioList.removeAll();

    System.out.println("we have:" + _theScenarios.size() + " scenarios");

    // did it work?
    if(super._theScenarios.size() > 0)
    {
      // store the list of participants
      _scenarioList.setListData(_theScenarios);
    }
  }

  public void updateParticipantGUI()
  {
    super.updateParticipantGUI();

    // clear the list of participants
    _participantList.removeAll();

    // did it work?
    if(super._theParticipants.size() > 0)
    {
      // store the list of participants
      _participantList.setListData(_theParticipants);

      // and update the canvas
      _theChart.rescale();
    }
  }

  void disconnect()
  {
    super.disconnect();

    // check it worked
    if(_myServer == null)
    {
      _participantList.removeAll();
      _scenarioList.removeAll();
      _timeField.setText("00:00:00");
      _statusArea.setText("blank");
      update();
    }
  }

  void update()
  {
    // do we know our participant
    if(_currentParticipant != null)
    {
      Status stat = null;
      String name = "unset";
      try
      {
        stat = _currentParticipant.getStatus();
        name = _currentParticipant.getName();
      }
      catch (RemoteException e)
      {
      }
      if(stat == null)
      	return;
      
      String val = name + ":" + stat.toString();
      val += System.getProperty("line.separator");
      val += "Course:" + MWC.Utilities.TextFormatting.GeneralFormat.formatBearing(stat.getCourse())
                + " Spd:" + MWC.Utilities.TextFormatting.GeneralFormat.formatOneDecimalPlace(stat.getSpeed().getValueIn(WorldSpeed.M_sec));
      _statusArea.setText(val);
    }

    System.out.println("_thePainter:" + _thePainter);

    // and update the plot
    _theChart.update(_thePainter);

  }

  public SwingChart getChart()
  {
    return _theChart;
  }

  /** flag to keep track of whether we are currently doing an update (for when we have threaded updates)
   *
   */
  boolean updating = false;

  protected class DoUpdate implements Runnable
  {
    public void run()
    {
        update();
        updating = false;
    }
  }

  private DoUpdate doUpdate = new DoUpdate();


  public void step(final long newTime) throws RemoteException
  {
    super.step(newTime);

    final String newT = MWC.Utilities.TextFormatting.FullFormatDateTime.toString(newTime);
    _timeField.setText(newT);

//    doUpdate.run();
    if(!updating)
    {
      updating = true;
      SwingUtilities.invokeLater(doUpdate);
    }
    else
    System.out.print(".");


  }

  /*****************************************************************
   * painter support
   ****************************************************************/
  class ParticipantPainter extends BaseLayer
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ParticipantPainter()
    {
      super.setName("Participant Plotter");
    }

    private java.awt.Color getColor(final ParticipantRMI  rmi)
    {
      Color res = null;
      try
      {
        final String force = rmi.getForce();
        if(force.equals(ASSET.Participants.Category.Force.BLUE))
         res = Color.blue;
        else
          if(force.equals(ASSET.Participants.Category.Force.RED))
           res = Color.red;
        else
          res = Color.green;
      }
      catch (RemoteException e)
      {
        e.printStackTrace();
      }
      return res;
    }

    public void paint(final CanvasType dest)
    {
      for (int i = 0; i < _theParticipants.size(); i++)
      {
        final StubWrapper wrapper = (StubWrapper)_theParticipants.elementAt(i);
        final ParticipantRMI rmi = (ParticipantRMI) wrapper.value;
        WorldLocation loc = null;
        try
        {

          final Status stat = rmi.getStatus();
          loc = stat.getLocation();
          final java.awt.Point pt = dest.toScreen(loc);

          final MWC.GUI.Shapes.Symbols.PlainSymbol sym = MWC.GUI.Shapes.Symbols.SymbolFactory.createSymbol(rmi.getCategory().getType());

          sym.setColor(getColor(rmi));
          sym.setScaleVal(MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor.LARGE);

          if(loc == null)
            System.out.println("CRAP LOC");

          sym.paint(dest, loc, MWC.Algorithms.Conversions.Degs2Rads(stat.getCourse()));

          final String activity = rmi.getActivity();
          dest.drawText(activity, pt.x + 3, pt.y + 3);
        }
        catch (RemoteException e)
        {
          e.printStackTrace();
        }
      }
    }

    public WorldArea getBounds()
    {
      WorldArea res = null;
      for (int i = 0; i < _theParticipants.size(); i++)
      {
        final StubWrapper stub = (StubWrapper) _theParticipants.elementAt(i);
        final ParticipantRMI rmi = (ParticipantRMI)stub.value;
        WorldLocation loc = null;
        try
        {
          loc = rmi.getStatus().getLocation();
          if(res == null)
            res = new WorldArea(loc, loc);
          else
            res.extend(loc);
        }
        catch (RemoteException e)
        {
          e.printStackTrace();
        }
      }

      return res;
    }
  }


  void doPing()
  {
    // clear the list
    _serverList.removeAll();
    // get the list of remote servers
    final java.rmi.Remote[] list = super.getServers();

    final Vector<StubWrapper> stubs = new Vector<StubWrapper>(0,1);


    if(list != null)
    {
      for(int i=0;i<list.length;i++)
      {
        final ServerRMI sr = (ServerRMI)list[i];
        try {
          stubs.addElement(new StubWrapper(sr.getHostname(), sr));
        } catch (RemoteException e) {
          System.out.println("failed to add new stub");
        }
      }
    }

    _serverList.setListData(stubs);
  }

  /*****************************************************************
   * main method
   ****************************************************************/
  public static void main(String[] args)
  {
    try
    {
      final SwingObserver so = new SwingObserver();
      System.err.println(so.toString());
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
  }




}
