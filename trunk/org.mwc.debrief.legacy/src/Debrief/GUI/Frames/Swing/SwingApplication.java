// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingApplication.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.7 $
// $Log: SwingApplication.java,v $
// Revision 1.7  2006/10/26 10:00:30  Ian.Mayo
// Pass parent to painter
//
// Revision 1.6  2005/12/13 09:04:21  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.5  2005/01/28 09:31:47  Ian.Mayo
// Minor tidying, on Eclipse recommendation
//
// Revision 1.4  2004/08/20 11:56:14  Ian.Mayo
// Correct where we start the MRU list, and fix problem where name of property in property editor became editable
//
// Revision 1.3  2004/08/09 09:40:46  Ian.Mayo
// Lots of Idea tidying
//
// Revision 1.2  2004/06/10 15:11:15  Ian.Mayo
// IntelliJ tidying
//
// Revision 1.1.1.2  2003/07/21 14:47:10  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.13  2003-07-03 14:26:34+01  ian_mayo
// Use hard-coded image name, use new image
//
// Revision 1.12  2003-07-01 16:30:41+01  ian_mayo
// Remove third party mouse wheel library
//
// Revision 1.11  2003-06-25 15:44:38+01  ian_mayo
// Make tooltips stay visible for longer
//
// Revision 1.10  2003-05-14 16:10:12+01  ian_mayo
// Improved JRE1.3 wheel-mouse support (add zoom)
//
// Revision 1.9  2003-03-19 15:37:54+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.8  2003-03-12 13:39:32+00  ian_mayo
// Switch to new version (2003)
//
// Revision 1.7  2003-01-24 12:22:07+00  ian_mayo
// Put analysis view into frame which optionally supports mouse wheel under JDK1.3
//
// Revision 1.6  2002-12-16 15:40:06+00  ian_mayo
// Better debug information when splash screen not loaded
//
// Revision 1.5  2002-11-27 15:27:57+00  ian_mayo
// Minor tidying, extend MRU
//
// Revision 1.4  2002-10-01 15:39:29+01  ian_mayo
// Remove unused variables
//
// Revision 1.3  2002-05-29 10:05:52+01  ian_mayo
// Update year of application and put border around desktop
//
// Revision 1.2  2002-05-28 12:27:51+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:20+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:30:08+01  ian_mayo
// Initial revision
//
// Revision 1.13  2002-02-18 20:14:15+00  administrator
// Check that window creation worked
//
// Revision 1.12  2002-01-29 07:53:46+00  administrator
// Use trace methods instead of System.out
//
// Revision 1.11  2002-01-22 15:28:40+00  administrator
// Inform the SwingSession of its name from the start, so that it can correctly configure the toolbars
//
// Revision 1.10  2002-01-22 12:43:48+00  administrator
// Put frames into columns when more than 3
//
// Revision 1.9  2002-01-22 09:11:31+00  administrator
// Add cascade/tile, plus mnemonics
//
// Revision 1.8  2001-09-19 09:15:03+01  administrator
// switch to new logo
//
// Revision 1.7  2001-09-14 10:01:49+01  administrator
// use d2001 logo
//
// Revision 1.6  2001-09-09 08:41:48+01  administrator
// just whitespace, really
//
// Revision 1.5  2001-08-24 09:54:13+01  administrator
// switch to our 'other' logo
//
// Revision 1.4  2001-08-17 08:04:38+01  administrator
// Assorted tidying up
//
// Revision 1.3  2001-08-06 12:50:14+01  administrator
// Remove commented out lines
//
// Revision 1.2  2001-07-30 15:43:38+01  administrator
// Take the application name from our parent
//
// Revision 1.1  2001-07-20 10:36:28+01  administrator
// get the name from the parent
//
// Revision 1.0  2001-07-17 08:41:42+01  administrator
// Initial revision
//
// Revision 1.7  2001-06-14 15:41:37+01  novatech
// reflect new format for toolbar constructor
//
// Revision 1.6  2001-02-26 09:35:44+00  novatech
// call our own Exit method, instead of System.exit(0) in our windowClosing handler - so that MRU's are saved
//
// Revision 1.5  2001-02-01 09:38:33+00  novatech
// give Debrief an icon
//
// Revision 1.4  2001-01-22 12:30:17+00  novatech
// added Logo as backdrop
//
// Revision 1.3  2001-01-21 21:35:11+00  novatech
// white-space only
//
// Revision 1.2  2001-01-09 10:29:27+00  novatech
// replace deprecated method call
//
// Revision 1.1  2001-01-03 13:40:55+00  novatech
// Initial revision
//
// Revision 1.3  2000/12/13 08:24:24  ianmayo
// another "experimental" change
//
// Revision 1.2  2000/12/13 08:05:44  ianmayo
// after first change, through Tortoise
//
// Revision 1.1  2000/12/13 07:59:46  ianmayo
// after first, trial attempt
//
// Revision 1.1.1.1  2000/12/12 20:45:12  ianmayo
// initial import of files
//
// Revision 1.17  2000-10-10 12:22:01+01  ian_mayo
// provide support for drag & drop, and set initial size of new charts - so that they can redraw properly
//
// Revision 1.16  2000-10-09 13:37:46+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.15  2000-08-30 14:50:03+01  ian_mayo
// implementing MRU
//
// Revision 1.14  2000-08-21 15:29:14+01  ian_mayo
// tidying up
//
// Revision 1.13  2000-08-16 15:52:04+01  ian_mayo
// removed stupid hard-coding of divider location
//
// Revision 1.12  2000-08-15 15:29:00+01  ian_mayo
// register colour property editor early
//
// Revision 1.11  2000-08-09 16:04:27+01  ian_mayo
// remove redundant code
//
// Revision 1.10  2000-08-07 14:04:19+01  ian_mayo
// removed d-lines
//
// Revision 1.9  2000-07-05 16:36:08+01  ian_mayo
// trigger resize of slider, so that the new frame size filters down to the canvas
//
// Revision 1.8  2000-05-23 13:39:29+01  ian_mayo
// provide button to refresh window list
//
// Revision 1.7  2000-04-19 11:29:56+01  ian_mayo
// tidy up help menu
//
// Revision 1.6  2000-04-05 08:36:55+01  ian_mayo
// delete scrap code
//
// Revision 1.5  2000-02-22 13:43:06+00  ian_mayo
// put version number in title bar
//
// Revision 1.4  2000-02-04 15:52:44+00  ian_mayo
// removed test button
//
// Revision 1.3  2000-01-20 10:07:06+00  ian_mayo
// added application-wide clipboard
//
// Revision 1.2  1999-11-25 16:54:56+00  ian_mayo
// implementing Swing components
//
// Revision 1.1  1999-11-18 11:12:22+00  ian_mayo
// new Swing versions
//
// Revision 1.1  1999-10-12 15:34:25+01  ian_mayo
// Initial revision


package Debrief.GUI.Frames.Swing;


import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import Debrief.GUI.Frames.*;
import MWC.GUI.*;
import MWC.GUI.Tools.Swing.SwingMenuItem;

public final class SwingApplication extends Application
{

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  /**
   * when we are cascading windows, this is the offset we apply
   */
  private static final int FRAME_OFFSET = 20;

  /**
   * the menubar for the application
   */
  private javax.swing.JMenuBar theMenuBar;

  /**
   * copy of the window menu
   */
  private javax.swing.JMenu theWindowMenu;

  /**
   * the parent application
   */
  private javax.swing.JFrame theFrame;

  /**
   * the frame in use
   */
  private JDesktopPane theDesktop;

  /**
   * remember the file menu, we use it for the MRU stuff
   */
  private javax.swing.JMenu _fileMenu;

  /**
   * where the file name is
   */
  private static final String IMAGE_FILE_NAME = "images/D2003_logo.gif";

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /**
   * create the Swing Toolbar, set it in the parent application
   */
  public SwingApplication()
  {
    // create the parent first
    super();

    // create the GUI bits
    initForm();

    // tell the dialog factory to create the correct type of dialogs
    MWC.GUI.Dialogs.DialogFactory.useSwing(true);

    // now setup the menus
    buildTheInterface();

    // now fill in the window menu
    refreshWindowMenu();

    // store our swing-specific property editor
    java.beans.PropertyEditorManager.registerEditor(Color.class,
                      MWC.GUI.Properties.Swing.ColorPropertyEditor.class);


    try
    {
      // load the coastline data in the background
      new MWC.GUI.Chart.Painters.CoastPainter(this);
    }
    catch (Exception e)
    {
    }

    /** lastly do the help menu, so that we know it's the last item on the
     * menu bar
     */
    doHelpMenu();

    theFrame.setVisible(true);

    // also sort out the MRU
    _mru = new MWC.GUI.Dialogs.MruMenuManager(_fileMenu, 7, 10, _appProps, "MRU");

    // let the parent class finish itself off by hand.
    completeInitialisation();

    // make the tooltips stay open for longer.  This is only really so that we show our new multi-line
    // tooltips for a reasonable period when shown over the chart
    ToolTipManager.sharedInstance().setDismissDelay(10000);

  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  public final Session getCurrentSession()
  {
    // get the session currently 'on top'
    Session res = null;

    final int high = theDesktop.highestLayer();
    final Component[] lst = theDesktop.getComponentsInLayer(high);
    final int len = lst.length;

    // we have to go through the panels in the main bit,
    // and return the one which is visible
    for (int i = 0; i < len; i++)
    {
      final Component cp = lst[i];
      final JInternalFrame jf = (JInternalFrame) cp;
      res = getSessionNamed(jf.getName());
      break;
    }

    // so, we've either got the top (visible) session, or we're returning null.
    return res;
  }


  /**
   * add the session passed in, to include adding it to our stack
   */
  public final void newSession(Session theSession)
  {

    // see if we are being passed a null parameter,
    // if so, we are to create a fresh session

    if (theSession == null)
    {
      theSession = new SwingSession(this, getClipboard(), super.getNewSessionName());
    }

    SwingSession aws = (SwingSession) theSession;

    // pass the session to the parent
    super.newSession(theSession);


    // see if we've already been loaded
    boolean foundIt = false;
    for (int i = 0; i < theDesktop.getComponentCount(); i++)
    {
      Component comp = theDesktop.getComponent(i);
      if (comp == aws.getPanel())
      {
        foundIt = true;
        break;
      }
    }

    if (!foundIt)
    {
      // add the session to our 'stack'
      theDesktop.add(aws.getPanel());

      try
      {
        aws.getPanel().setMaximum(true);
      }
      catch (java.beans.PropertyVetoException e)
      {
        MWC.Utilities.Errors.Trace.trace(e);
      }

      // bring this new panel to the front and show it
      aws.getPanel().show();


      // tell the desktop that things have changed
      theDesktop.revalidate();

      // and try to trigger resize events
      final Debrief.GUI.Views.Swing.SwingAnalysisView sc = (Debrief.GUI.Views.Swing.SwingAnalysisView) aws.getCurrentView();

      // just check that the open failed
      if (sc == null)
        return;


      final java.awt.Dimension scrSize = sc.getChart().getScreenSize();
      sc.getChart().getCanvas().getProjection().setScreenArea(scrSize);

      // and refresh the window list
      refreshWindowMenu();
      // clear temp values
      aws = null;
    }
  }


  /**
   * fill in the UI details
   */
  private void initForm()
  {

    theFrame = new JFrame(getName() + " (" + Debrief.GUI.VersionInfo.getVersion() + ")");


    theFrame.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(final java.awt.event.WindowEvent e)
      {
        exit();
      }
    });

    // try to give the application an icon
    final java.net.URL iconURL = getClass().getClassLoader().getResource("images/icon.gif");
    if (iconURL != null)
    {
      final ImageIcon myIcon = new ImageIcon(iconURL);
      if (myIcon != null)
        theFrame.setIconImage(myIcon.getImage());
    }

    theDesktop = new imageDesktop(IMAGE_FILE_NAME);
    theFrame.getContentPane().add(theDesktop, BorderLayout.CENTER);

    // create the components
    final MWC.GUI.Tools.Swing.SwingToolbar theToolbar =
      new MWC.GUI.Tools.Swing.SwingToolbar(Toolbar.HORIZONTAL, "Application", null);

    // pass the toolbar back to the parent
    setToolbar(theToolbar);

    // and the panel
    final JPanel topSection = new JPanel();
    topSection.setLayout(new BorderLayout());
    theMenuBar = new JMenuBar();
    theFrame.setJMenuBar(theMenuBar);

    // add them
    theFrame.getContentPane().add("North", theToolbar);

    // tidy up

    final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

    theFrame.setSize((int) (dim.width * 0.6),
                     (int) (dim.height * 0.6));
    final Dimension sz = theFrame.getSize();
    theFrame.setLocation((dim.width - sz.width) / 2,
                         (dim.height - sz.height) / 2);

    // implement drag/drop support
    _dropSupport.addComponent(theToolbar);
    _dropSupport.addComponent(theFrame);
    _dropSupport.addComponent(theMenuBar);

    // do any final re-arranging
    theFrame.doLayout();

  }

  /**
   * create the help menu, plus it's commands
   */
  private void doHelpMenu()
  {
    ////////////////////////////////////////////////////////
    // help menus
    ////////////////////////////////////////////////////////
    // add the window menu, now that we know that the other menus
    // have been added
    final JMenu helpMen = new JMenu("Help");
    helpMen.setMnemonic('H');

    // handle the about bit
    final JMenuItem aboutMn = new JMenuItem("About");
    aboutMn.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        helpAbout();
      }
    });

    // handle the contents bit
    final JMenuItem contentsMn = new JMenuItem("Contents");
    contentsMn.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        helpContents();
      }
    });

    helpMen.add(contentsMn);
    helpMen.add(aboutMn);

    // do the menu.  Note that the "addHelpMenu" command isn't working under Swing
    theMenuBar.add(helpMen);

  }

  /**
   * set the title bar text to parameter
   *
   * @param theStr to assign to title bar of frame
   */
  protected final void setTitleName(final String theStr)
  {
    theFrame.setTitle("Debrief : " + theStr);
  }


  private void addMenu(final String theTitle)
  {
    final JMenu newMen = new JMenu(theTitle);
    theMenuBar.add(newMen);

    // see if this was the file menu
    if (theTitle.equals("File"))
      _fileMenu = newMen;

    // have a go at creating the mnemonic for this menu
    final char first = theTitle.charAt(0);
    newMen.setMnemonic(first);

  }

  protected final void addMenuSeparator(final String theMenu)
  {
    final JMenu mn = getMenu(theMenu);
    mn.addSeparator();
  }

  private JMenu getMenu(final String theMenu)
  {
    JMenu res = null;

    // find this menu.
    boolean foundIt = false;
    final int menuCount = theMenuBar.getMenuCount();

    // step through menus
    for (int i = 0; i < menuCount; i++)
    {

      // does this name match?
      final JMenu mu = theMenuBar.getMenu(i);

      if (mu.getText().equals(theMenu))
      {

        foundIt = true;
        res = mu;

        break;
      }
    }

    if (!foundIt)
    {
      // can't find it, we'll have to add it
      addMenu(theMenu);

      // now retrieve it
      res = getMenu(theMenu);
    }

    // return our result
    return res;
  }

  protected final void addMenuItem(final String theMenu,
                                   final String theLabel,
                                   final Tool theTool,
                                   final MenuShortcut theShortCut)
  {
    // create the new item
    final SwingMenuItem res = new SwingMenuItem(theLabel, theTool);

    if (theShortCut != null)
    {
      res.setAccelerator(KeyStroke.getKeyStroke(theShortCut.getKey(),
                                                java.awt.Event.CTRL_MASK));

      res.setMnemonic(theLabel.charAt(0));
    }

    // get its memu
    final JMenu thisMenu = getMenu(theMenu);

    // add to this item
    thisMenu.add(res);
  }


  protected final void closeSessionGUI(final Session theSession)
  {
    SwingSession theSess = (SwingSession) theSession;

    // get a pointer to the interface
    JInternalFrame jc = theSess.getPanel();

    // remove from the desktop
    theDesktop.remove(jc);

    // close the pane
    jc.setVisible(false);

    // try to delete it
    jc.dispose();

    // and refresh the window list
    refreshWindowMenu();

    // and refresh the pane
    theFrame.repaint();

    // and delete temp variables
    theSess = null;
    jc = null;
  }

  public final void showSession(final Session theSession)
  {
    setTitleName(theSession.getName());
  }

  private void refreshWindowMenu()
  {
    // refresh the list of files contained on this menu


    if (theWindowMenu == null)
    {
      theWindowMenu = new JMenu("Window");
      theWindowMenu.setMnemonic('W');
      theMenuBar.add(theWindowMenu);
    }

    // clear the menu
    theWindowMenu.removeAll();

    // put in the refresh button
    final JMenuItem refresh = new JMenuItem("Refresh window list");
    theWindowMenu.add(refresh);
    theWindowMenu.add(new JSeparator());
    refresh.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        refreshWindowMenu();
      }
    });

    // put in the cascade button
    final JMenuItem cascade = new JMenuItem("Cascade", KeyEvent.VK_C);
    cascade.setAccelerator(KeyStroke.getKeyStroke(new MenuShortcut(KeyEvent.VK_C).getKey(),
                                                  java.awt.Event.CTRL_MASK));
    theWindowMenu.add(cascade);
    cascade.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        cascadeWindows();
      }
    });

    // put in the tile button
    final JMenuItem tile = new JMenuItem("Tile", KeyEvent.VK_T);
    tile.setAccelerator(KeyStroke.getKeyStroke(new MenuShortcut(KeyEvent.VK_T).getKey(),
                                               java.awt.Event.CTRL_MASK));
    theWindowMenu.add(tile);
    theWindowMenu.add(new JSeparator());
    tile.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        tileWindows();
      }
    });

    // put in the tile button
    final JMenuItem next = new JMenuItem("Next Window", KeyEvent.VK_N);
    next.setAccelerator(KeyStroke.getKeyStroke(new MenuShortcut(KeyEvent.VK_F6).getKey(),
                                               java.awt.Event.CTRL_MASK));
    theWindowMenu.add(next);
    theWindowMenu.add(new JSeparator());
    next.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        selectNextWindow();
      }
    });


    final JInternalFrame[] frames = theDesktop.getAllFrames();
    for (int i = 0; i < frames.length; i++)
    {
      final JInternalFrame thisFrame = frames[i];
      final String name = thisFrame.getName();
      final JMenuItem mn = new JMenuItem(name);
      mn.addActionListener(new ActionListener()
      {
        public void actionPerformed(final ActionEvent e)
        {
          thisFrame.setVisible(true);
          thisFrame.moveToFront();
        }
      });

      theWindowMenu.add(mn);
    }

  }

  private void selectNextWindow()
  {
    final JInternalFrame[] frames = theDesktop.getAllFrames();

    // check that there is more than one window open
    if (frames.length > 1)
    {

      final Session currSess = this.getCurrentSession();

      final TreeMap sList = new TreeMap();

      // collate a list of frames, in alphabetical order
      for (int i = 0; i < frames.length; i++)
      {
        final String thisName = frames[i].getName();
        sList.put(thisName, frames[i]);
      }

      // did we find it?
      boolean found_frame = false;

      // did we set the new frame?
      boolean set_new_frame = false;

      // pass through the list to see if it's this one
      final Iterator theFrames = sList.keySet().iterator();
      while (theFrames.hasNext())
      {
        final String thisFrame = (String) theFrames.next();
        if (thisFrame.equals(currSess.getName()))
        {
          // hey, this is our one!
          found_frame = true;
        }
        else
        {
          // we're not looking at the target one

          if (found_frame == true)
          {
            // ok, we must be after our target
            final JInternalFrame jf = (JInternalFrame) sList.get(thisFrame);
            jf.setVisible(true);
            jf.moveToFront();
            set_new_frame = true;
            break;
          }
          else
          {
            // oh well, we haven't found our one yet
          }
        }
      }
      // did we find a new target frame?
      if (!set_new_frame)
      {
        // just set the first, since we must have found the last one
        final JInternalFrame jf = (JInternalFrame) sList.get(sList.firstKey());
        jf.setVisible(true);
        jf.moveToFront();
      }
    } // whether we have enough frames
  }

  /**
   * cascade the current set of windows taken from article at: http://www.javaworld.com/javaworld/jw-05-2001/jw-0525-mdi_p.html
   */
  private void cascadeWindows()
  {
    int x = 0;
    int y = 0;
    final JInternalFrame[] allFrames = theDesktop.getAllFrames();
    final int frameHeight = (theDesktop.getBounds().height - 5) - allFrames.length * FRAME_OFFSET;
    final int frameWidth = (theDesktop.getBounds().width - 5) - allFrames.length * FRAME_OFFSET;
    for (int i = allFrames.length - 1; i >= 0; i--)
    {
      allFrames[i].setSize(frameWidth, frameHeight);
      allFrames[i].setLocation(x, y);
      x = x + FRAME_OFFSET;
      y = y + FRAME_OFFSET;
    }
  }

  /**
   * cascade the current set of windows taken from article at: http://www.javaworld.com/javaworld/jw-05-2001/jw-0525-mdi_p.html
   */
  private void tileWindows()
  {
    final java.awt.Component[] allFrames = theDesktop.getAllFrames();

    int y = 0;

    // how many columns do we want?
    if (allFrames.length <= 3)
    {
      final int frameHeight = theDesktop.getBounds().height / allFrames.length;

      // 3 frames or less, show them as one column
      for (int i = 0; i < allFrames.length; i++)
      {
        allFrames[i].setSize(theDesktop.getBounds().width, frameHeight);
        allFrames[i].setLocation(0, y);
        y = y + frameHeight;
      }
    }
    else
    {
      // 4 frames or more, show them as two columns
      final int doubleFrameWidth = theDesktop.getBounds().width / 2;

      // are there an odd number of frames?

      final int frameHeight;
      if ((allFrames.length % 2) == 1)
        frameHeight = theDesktop.getBounds().height / ((allFrames.length + 1) / 2);
      else
        frameHeight = theDesktop.getBounds().height / (allFrames.length / 2);


      for (int i = 0; i < allFrames.length; i++)
      {

        allFrames[i].setSize(doubleFrameWidth, frameHeight);

        // is this an odd frame?
        if ((i % 2) == 1)
        {
          allFrames[i].setLocation(doubleFrameWidth + 1, y);
          y = y + frameHeight;
        }
        else
        {
          allFrames[i].setLocation(0, y);
        }
      }

    }

  }

  public final void setCursor(final int theCursor)
  {
    theFrame.getContentPane().setCursor(new Cursor(theCursor));
  }

  public final void restoreCursor()
  {
    theFrame.getContentPane().setCursor(null);
  }

  public final Session createSession()
  {
    return new SwingSession(this, getClipboard(), super.getNewSessionName());
  }

  private void helpAbout()
  {
    // show the help text
    String msg = getName() + ", from the Maritime Warfare Centre";
    msg += System.getProperties().getProperty("line.separator");
    msg += "Build date: " + Debrief.GUI.VersionInfo.getVersion();

    AboutDialog.showIt(theFrame, getName(), msg);

  }

  static final class imageDesktop extends JDesktopPane
  {
    // store the version id - to tidy thing up
    static final long serialVersionUID = 42L;
    
    // store the image
    private Image _icon;
    private int _iHeight = -1;
    private int _iWidth = -1;
    final private String imageName;

    public imageDesktop(final String imageFileName)
    {
      imageName = imageFileName;

      super.setBackground(new Color(184, 184, 184));

      this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED),
                                                      "From the Maritime Warfare Centre and PlanetMayo Ltd",
                                                      TitledBorder.RIGHT, TitledBorder.ABOVE_BOTTOM, this.getFont(), Color.darkGray));

      // load the image
      if (imageName != null)
      {
        // first try to get the URL of the image
        final java.lang.ClassLoader loader = getClass().getClassLoader();
        if (loader != null)
        {
          final java.net.URL imLoc = loader.getResource(imageName);
          if (imLoc != null)
          {
            final ImageIcon im = new ImageIcon(imLoc);
            _icon = im.getImage();
          }
          else
          {
            MWC.Utilities.Errors.Trace.trace("SwingApplication: loader didn't work", false);
          }
        }
        else
        {
          MWC.Utilities.Errors.Trace.trace("Failed to load image:" + imageName);
        }
      }
    }

    public final void paint(final Graphics g)
    {
      super.paint(g);
      // are there any frames open?
      if (getAllFrames().length == 0)
      {
        if (_icon == null)
        {
          // first try to get the URL of the image
          final java.lang.ClassLoader loader = getClass().getClassLoader();
          if (loader != null)
          {
            final java.net.URL imLoc = loader.getResource(imageName);
            if (imLoc != null)
            {
              final ImageIcon im = new ImageIcon(imLoc);
              _icon = im.getImage();
            }
            else
            {
              System.out.println("loader still didn't work for:" + imageName);
            }
          }
        }

        // check our icon got loaded
        if (_icon != null)
        {
          // do we know the sizes?
          if (_iHeight == -1)
          {
            _iHeight = _icon.getHeight(this);
            _iWidth = _icon.getWidth(this);
          }

          // sort out the window size
          final int ht = this.getHeight();
          final int wid = this.getWidth();

          // sort out the image location
          final int xLoc = (wid - _iWidth) / 2;
          final int yLoc = (ht - _iHeight) / 2;

          //          // fill in our background colour first
          //          Color backCol = new Color(184,184,184);
          //          g.setColor(backCol);
          //          g.fillRect(0,0,wid,ht);

          // put the image in the centre
          g.drawImage(_icon, xLoc, yLoc, this);
        }

      }
    }
  }

}