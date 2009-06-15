// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingGrabControl.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: SwingGrabControl.java,v $
// Revision 1.3  2004/10/07 14:23:17  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.2  2004/05/25 15:37:18  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:27  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:48  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:03+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:00+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-05-23 13:15:54+01  ian
// end of 3d development
//
// Revision 1.1  2002-04-11 13:03:32+01  ian_mayo
// Initial revision
//
// Revision 1.4  2001-08-31 10:47:35+01  administrator
// Append AVI suffix, if it's missing
//
// Revision 1.3  2001-08-24 09:56:51+01  administrator
// Corrections following Andy's feedback
//
// Revision 1.2  2001-07-30 15:38:53+01  administrator
// pass in the details of the properties panel, so that we can implement a close button
//
// Revision 1.1  2001-07-27 17:09:25+01  administrator
// further evolution
//
// Revision 1.0  2001-07-24 16:55:36+01  administrator
// Initial revision
//
// Revision 1.0  2001-07-24 15:13:47+01  administrator
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:34+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:43:05+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:45  ianmayo
// initial version
//
// Revision 1.1  2000-09-26 10:49:49+01  ian_mayo
// Initial revision
//
package MWC.GUI.Video;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.PlugInManager;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.format.RGBFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SwingGrabControl extends JPanel
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// supporting class containing the detailed grabbing stuff
  GrabControlSupport _grabber = null;
  // the component we are listening to
  Component _target = null;

  /**
   * the text box for the filename
   */
  JTextField _destination = null;

  /**
   * the text box for the frame rate
   */
  JTextField _frameRate = null;

  /**
   * the start button (which we enable after configuring)
   */
  JButton starter;

  /**
   * the stop button (which we enable after starting)
   */
  JButton stopper;

  /**
   * the properties panel which we get inserted into
   */
  MWC.GUI.Properties.PropertiesPanel _thePropertiesPanel;

  /**
   * the directory to place the file into
   */
  protected String _destinationPath = null;

  public SwingGrabControl(Component target,
                          MWC.GUI.ToolParent parent,
                          MWC.GUI.Properties.PropertiesPanel parentPanel)
  {
    setName("Video");

    // store the target
    _target = target;

    // create the support application
    _grabber = new GrabControlSupport();

    // remember the properties panel which we've been inserted into
    _thePropertiesPanel = parentPanel;

    // sort out the directory name
    if (parent != null)
    {
      String res = parent.getProperty(MWC.GUI.Tools.Chart.WriteMetafile.PROP_NAME);
      if (res != null)
        _destinationPath = res;
    }

    // create the ui
    initForm();
  }

  protected void initForm()
  {
    this.setLayout(new BorderLayout());
    JPanel btnHolder = new JPanel();
    btnHolder.setLayout(new GridLayout(0, 3));
    JButton conner = new JButton("Configure");
    conner.setToolTipText("Prepare output files & video streams");
    conner.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        configure();
      }
    });
    starter = new JButton("Start");
    starter.setToolTipText("Start recording to file");
    starter.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        start();

      }
    });
    starter.setEnabled(false);
    stopper = new JButton("Stop");
    stopper.setToolTipText("Stop recording to file");
    stopper.setEnabled(false);
    stopper.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        stop();

      }
    });
    JButton closer = new JButton("Close");
    closer.setToolTipText("Close this panel");
    closer.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        close();
      }
    });

    btnHolder.add(conner, BorderLayout.NORTH);
    btnHolder.add(starter, BorderLayout.WEST);
    btnHolder.add(stopper, BorderLayout.EAST);


    _destination = new JTextField("Debrief2K1.avi");
    _destination.setToolTipText("Destination for recorded video (appended to WMF_Directory in properties file)");
    _frameRate = new JTextField("2");
    _frameRate.setToolTipText("Frames recorded in snapshots per second");
    btnHolder.add(new JLabel("Filename:", JLabel.RIGHT));
    btnHolder.add(_destination);
    btnHolder.add(new JLabel(" ", JLabel.RIGHT));
    btnHolder.add(new JLabel("Frame Rate:", JLabel.RIGHT));
    btnHolder.add(_frameRate);

    JPanel closeHolder = new JPanel();
    closeHolder.setLayout(new GridLayout(0, 3));
    closeHolder.add(new JLabel(" "));
    closeHolder.add(closer);
    closeHolder.add(new JLabel(" "));

    add(closeHolder, BorderLayout.SOUTH);
    add(btnHolder, BorderLayout.NORTH);
  }

  protected void close()
  {
    if (_thePropertiesPanel != null)
    {
      _thePropertiesPanel.remove((Object) this);
    }
  }

  protected String getPath()
  {
    String res = new String();

    // try to get the path
    if (this._destinationPath != null)
    {
      res += _destinationPath + "/";
    }

    res += _destination.getText();

    if (res.toUpperCase().endsWith(".AVI"))
    {
      // everything's ok, relax
    }
    else
    {
      // missing, append it
      System.out.println("Video grabber: appending AVI file suffix");
      res += ".avi";
    }

    return res;
  }

  @SuppressWarnings("deprecation")
	protected void configure()
  {

    // get the dimensions, using the screen location of the target
    Rectangle rect = new Rectangle(_target.getLocationOnScreen(), _target.getSize());

    // check if there is a file of this name already
    String thePath = getPath();
    java.io.File theFile = new java.io.File(thePath);
    if (theFile.exists())
    {

      final JPanel jp = new JPanel();
      final JOptionPane pane = new JOptionPane("File already exists, do you wish to overwrite?",
                                               JOptionPane.QUESTION_MESSAGE,
                                               JOptionPane.YES_NO_OPTION);
      final JDialog dialog = pane.createDialog(jp, "Start recording");
      dialog.show();
      Integer value = (Integer) pane.getValue();
      if (value.intValue() == JOptionPane.NO_OPTION)
      {
        return;
      }

      // so we want to continue, and we are happy to overwrite this file -
      // let's delete it
      theFile.delete();
    }


    // configure the grabber
    _grabber.setDestination(getPath());
    _grabber.setFileType(FileTypeDescriptor.MSVIDEO);

    // can we get an integer from the frame rate?
    try
    {
      int rate = Integer.parseInt(_frameRate.getText());
      _grabber.setFrameRate(rate);
    }
    catch (java.lang.NumberFormatException fe)
    {
      MWC.Utilities.Errors.Trace.trace(fe, "Failed to get integer from string:" + _frameRate.getText());
      fe.printStackTrace();
    }

    _grabber.setArea(rect);

    try
    {
      // do the preparations
      _grabber.configure();

      // so, the configure must have worked,  enable the start button
      starter.setEnabled(true);

      // and disable the text fields
      _destination.setEnabled(false);
      _frameRate.setEnabled(false);
    }
    catch (java.io.FileNotFoundException fee)
    {
      MWC.GUI.Dialogs.DialogFactory.showMessage("Configure Video", "Video output not ready, please try again");
    }
    catch (Exception me)
    {
      MWC.Utilities.Errors.Trace.trace(me, "Failed to configure video:");
    }

  }

  protected void start()
  {
    _grabber.start();
    stopper.setEnabled(true);
    starter.setEnabled(false);
  }

  protected void stop()
  {
    _grabber.stop();
    stopper.setEnabled(false);

    // and disable the text fields
    _destination.setEnabled(true);
    _frameRate.setEnabled(true);
  }


  protected static class Mobile extends JComponent implements Runnable
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Thread updater;

    public Mobile()
    {
      updater = new Thread(this);
    }

    public void run()
    {
      while (updater.isAlive())
      {
        try
        {
          //     this.invalidate();
          this.repaint();
          Thread.sleep(500);
        }
        catch (java.lang.InterruptedException e)
        {
          e.printStackTrace();
        }
      }
    }

    private static java.text.DateFormat df = new java.text.SimpleDateFormat("hh:mm:ss.SSS");

    public void paint(Graphics g)
    {
      if (!updater.isAlive())
        updater.start();

      super.paint(g);
      g.setColor(Color.red);
      Dimension dim = this.getSize();
      g.drawLine(0, 0, dim.width, dim.height);
      g.drawLine(0, dim.height, dim.width, 0);


      g.setColor(Color.black);
      g.drawString(df.format(new java.util.Date()), 30, 30);
    }
  }

  @SuppressWarnings("unchecked")
	public static void main6(String[] args)
  {
    Vector lst = PlugInManager.getPlugInList(null, null, PlugInManager.CODEC);
    Enumeration enumer = lst.elements();
    while (enumer.hasMoreElements())
    {
      Object val = enumer.nextElement();
      System.out.println("mgr: " + val);
    }
  }

  public static void main3(String[] args)
  {

    JFrame fr = new JFrame("tester");
    fr.setSize(600, 400);
    fr.getContentPane().setLayout(new GridLayout(1, 0));
    JComponent watched = new Mobile();
    watched.setForeground(Color.red);
    watched.setBackground(Color.blue);
    fr.getContentPane().add(new SwingGrabControl(watched, null, null));
    fr.getContentPane().add(watched);
    fr.setVisible(true);
    fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  @SuppressWarnings("unchecked")
	public static void main(String[] args)
  {
    System.out.println("working");

    String location = "screen:/352,264,320,240/2";
    MediaLocator ml = new MediaLocator(location);
    JDataSource jd = new JDataSource();
    jd.setLocator(ml);

    try
    {
      // artificially kisk-start the streams
      Object[] the_streams = jd.getStreams();
      LiveStream str = (LiveStream) the_streams[0];
      jd.connect();
      Format[] formats = new Format[1];
      formats[0] = str.getFormat();


      CaptureDeviceInfo cdi = new CaptureDeviceInfo("Screen grab", ml, formats);
      CaptureDeviceManager.addDevice(cdi);
      CaptureDeviceInfo c_info = null;

      Vector dev = CaptureDeviceManager.getDeviceList(new RGBFormat());
      Enumeration enumer = dev.elements();
      while (enumer.hasMoreElements())
      {
        CaptureDeviceInfo cd = (CaptureDeviceInfo) enumer.nextElement();
        System.out.println("device:" + cd);
        if (c_info == null)
          c_info = cd;
      }

      dev = Manager.getProcessorClassList("RGB");
      while (enumer.hasMoreElements())
      {
        System.out.println("processor:" + enumer.nextElement());
      }


      FileTypeDescriptor output_format = new FileTypeDescriptor(FileTypeDescriptor.MSVIDEO);
      ProcessorModel _myProcessor = new ProcessorModel(jd, formats, output_format);
      Processor p = Manager.createRealizedProcessor(_myProcessor);
      System.out.println("processor:" + p);
      DataSource source = p.getDataOutput();

      MediaLocator dest = new MediaLocator("file:c://foo.avi");
      DataSink fileWriter = Manager.createDataSink(source, dest);
      fileWriter.open();
      fileWriter.start();

      System.out.println("about to start!");
      p.start();
      System.out.println("about to sleep!");
      Thread.sleep(1000);
      System.out.println("about to stop!");
      p.stop();
      fileWriter.stop();
      System.out.println("about to close!");
      p.close();
      System.out.println("about to exit!");
      System.exit(0);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }


  }


}
