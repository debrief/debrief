package ASSET.GUI.MonteCarlo;

import ASSET.Scenario.CoreScenario;
import MWC.GUI.PlainChart;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.Properties.Swing.SwingCustomEditor;
import MWC.GUI.ToolParent;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 07-Oct-2004
 * Time: 13:58:18
 * To change this template use File | Settings | File Templates.
 */
abstract public class Loader extends SwingCustomEditor
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the loader to handle actually building the scenario
   */
  LoaderCore _myLoader;

  /**
   * the build scenario button
   */
  JButton _buildButton;

  /**
   * the label where we show the dropped control file
   */
  private JLabel _controllerCatcher;

  /**
   * the label where we show the dropped scenario file
   */
  private JLabel _scenarioCatcher;

  /**
   * label to use to prefix the scenario file name
   */
  private static final String SCENARIO_LABEL = "<i>Scenario file:</i>";

  /**
   * label to use to prefix the control file name
   */
  private static final String CONTROL_LABEL = "<i>Control file:</i>";

  /**
   * window to track build progress
   */
  JTextArea _progressWindow;

  //////////////////////////////////////////////////
  //
  //////////////////////////////////////////////////


  /**
   * constructor - builds & connects the front-end, loads data-files
   */
  public Loader(CoreScenario myScenario, 
                String scenarioFile, 
                String controlFile,
                PlainChart theChart,
                ToolParent theParent,
                PropertiesPanel theProperties)
  {
    this(myScenario, theChart, theParent, theProperties);

    // and configure the files
    setScenario(new File(scenarioFile));
    setController(new File(controlFile));

  }

  /**
   * constructor - builds & connects the front-end
   */
  public Loader(CoreScenario theScenario,
                PlainChart theChart,
                ToolParent theParent,
                PropertiesPanel theProperties)
  {

    // setup the loader
    _myLoader = new LoaderCore(theScenario)
    {
      public void buildEnabled(boolean enabled)
      {
        _buildButton.setEnabled(enabled);
      }

      SimpleDateFormat sdf = new SimpleDateFormat("[hh:mm:ss]");

      /**
       * write a message to a message tracking window
       */
      void writeMessage(String msg)
      {
        Date now = new Date();
        String dtg = sdf.format(now);
        msg = dtg + " " + msg;
        _progressWindow.setText(_progressWindow.getText() + "\n" + msg);
      }
    };


    // tell our parent object about the important stuff
    setObject(null, theChart, theParent, theProperties);


    // build the form
    initForm();

  }


  //////////////////////////////////////////////////
  // Swing custom editor methods
  //////////////////////////////////////////////////

  /**
   * update the editor with the supplied object
   */
  public void setObject(Object data)
  {
    // hey, don't worry about this.
  }


  //////////////////////////////////////////////////
  //
  //////////////////////////////////////////////////


  public void suspendUpdates(boolean override)
  {
    super.getChart().setSuspendUpdates(override);
  }

  /**
   * get the loader to load itself.  Note that we do it in a separate thread so that the GUI
   * can still get updated.
   */
  void startGenerate()
  {
    Thread runner = new Thread()
    {
      public void run()
      {
        super.run();    //To change body of overridden methods use File | Settings | File Templates.

        // suspend updates
        suspendUpdates(true);

        _myLoader.buildScenario();

        // suspend updates
        suspendUpdates(false);

        // and rebuild the plot
        getChart().update();

      }
    };

    runner.start();
  }

  /**
   * build the interface
   */
  private void initForm()
  {

    // first the create button
    _buildButton = new JButton("Import");
    _buildButton.setEnabled(false);
    _buildButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        // ok, get the generation going.
        startGenerate();
      }
    });

    // and the close button
    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(new ActionListener()
    {
      /**
       * Invoked when an action occurs.
       */
      public void actionPerformed(ActionEvent e)
      {
        doClose();
      }
    });

    // now the scenario catcher
    _scenarioCatcher = new JLabel("<HTML>" + SCENARIO_LABEL + " [drop here]</html>");
    _scenarioCatcher.setBorder(BorderFactory.createLoweredBevelBorder());
    DropTarget scenarioTarget = new DropTarget();
    scenarioTarget.setActive(true);
    try
    {
      scenarioTarget.addDropTargetListener(new DropTargetAdapter()
      {
        @SuppressWarnings("rawtypes")
				public void drop(DropTargetDropEvent dtde)
        {
          // see if it's an XML file
          if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
          {
            try
            {
              dtde.acceptDrop(DnDConstants.ACTION_COPY);

              final Transferable tr = dtde.getTransferable();
              final List list = (List) tr.getTransferData(DataFlavor.javaFileListFlavor);
              if (list.size() > 1)
              {
                MWC.Utilities.Errors.Trace.trace("One file at a time please", true);
              }
              else
              {
                File thisFile = (File) list.iterator().next();
                setScenario(thisFile);
              }
              //          ASSET.Util.XML.ASSETReaderWriter.importThis(_myList, s, new java.io.FileInputStream(s));
              dtde.dropComplete(true);
            }
            catch (UnsupportedFlavorException e)
            {
              e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            catch (IOException e)
            {
              e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
          }
        }
      });
    }
    catch (TooManyListenersException e)
    {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    _scenarioCatcher.setDropTarget(scenarioTarget);

    // and the controller catcher
    _controllerCatcher = new JLabel("<HTML>" + CONTROL_LABEL + " [drop here]</html>");
    _controllerCatcher.setBorder(BorderFactory.createLoweredBevelBorder());
    DropTarget controllerTarget = new DropTarget();
    try
    {
      controllerTarget.addDropTargetListener(new DropTargetAdapter()
      {
        @SuppressWarnings("rawtypes")
				public void drop(DropTargetDropEvent dtde)
        {
          // see if it's an XML file
          if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
          {
            try
            {
              dtde.acceptDrop(DnDConstants.ACTION_COPY);

              final Transferable tr = dtde.getTransferable();
              final List list = (List) tr.getTransferData(DataFlavor.javaFileListFlavor);
              if (list.size() > 1)
              {
                MWC.Utilities.Errors.Trace.trace("One file at a time please", true);
              }
              else
              {
                File thisFile = (File) list.iterator().next();
                setController(thisFile);
              }
              //          ASSET.Util.XML.ASSETReaderWriter.importThis(_myList, s, new java.io.FileInputStream(s));
              dtde.dropComplete(true);
            }
            catch (UnsupportedFlavorException e)
            {
              e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            catch (IOException e)
            {
              e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
          }
        }
      });
    }
    catch (TooManyListenersException e)
    {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    _controllerCatcher.setDropTarget(controllerTarget);


    JPanel fileHolder = new JPanel();
    fileHolder.setLayout(new GridLayout(2, 0));
    fileHolder.add(_scenarioCatcher);
    fileHolder.add(_controllerCatcher);

    _progressWindow = new JTextArea();
    _progressWindow.setBorder(BorderFactory.createLoweredBevelBorder());

    JPanel holder = new JPanel();
    holder.setLayout(new BorderLayout());
    holder.add(fileHolder, BorderLayout.NORTH);
    holder.add(new JScrollPane(_progressWindow), BorderLayout.CENTER);

    JPanel buttonHolder = new JPanel();
    buttonHolder.setLayout(new GridLayout(2, 0));
    buttonHolder.add(_buildButton);
    buttonHolder.add(closeButton);

    holder.add(buttonHolder, BorderLayout.SOUTH);

    this.setLayout(new BorderLayout());
    this.add(holder, BorderLayout.CENTER);

    this.setName("Loader");
  }

  /**
   * user has tried to close the loader panel = we'll just hide it
   */
  abstract public void doClose();

  /**
   * store the controller filename
   *
   * @param thisFile
   */
  void setController(File thisFile)
  {
    _myLoader.setControllerFile(thisFile);
    _controllerCatcher.setText("<html>" + CONTROL_LABEL + thisFile.getName() + "</html>");
    _buildButton.setText("Generate");
  }

  /**
   * store the scenario filename
   *
   * @param thisFile
   */
  void setScenario(File thisFile)
  {
    _myLoader.setScenarioFile(thisFile);
    _scenarioCatcher.setText("<html>" + SCENARIO_LABEL + thisFile.getName() + "</html>");
  }

}
