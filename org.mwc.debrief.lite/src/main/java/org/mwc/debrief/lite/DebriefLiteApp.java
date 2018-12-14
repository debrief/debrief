/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.lite;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.geotools.map.MapContent;
import org.mwc.debrief.lite.custom.JPanelWithTitleBar;
import org.mwc.debrief.lite.gui.DebriefLiteToolParent;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.mwc.debrief.lite.map.GeoToolMapRenderer.MapRenderer;
import org.mwc.debrief.lite.map.MapBuilder;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Toolbar;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GUI.DragDrop.FileDropSupport;
import MWC.GUI.DragDrop.FileDropSupport.FileDropListener;
import MWC.GUI.Tools.Swing.SwingToolbar;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.ReaderWriter.ImportManager;
import MWC.Utilities.ReaderWriter.ImportManager.BaseImportCaller;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 * @author Unni Mana <unnivm@gmail.com>
 */

public class DebriefLiteApp implements FileDropListener
{

  public static final String appName = "Debrief Lite";
  public static final String NOTES_ICON = "images/16/note.png";

  private static JScrollPane createScrollPane(
      final JPanelWithTitleBar jTitleBar)
  {
    final JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(jTitleBar, BorderLayout.NORTH);
    final JScrollPane scrPane1 = new JScrollPane(panel);
    return scrPane1;
  }

  public static void main(final String[] args)
  {
    new DebriefLiteApp();
  }

  /**
   * @param filename
   *          autofilled
   */
  private static String suffixOf(final String filename)
  {
    String theSuffix = null;
    final int pos = filename.lastIndexOf(".");
    theSuffix = filename.substring(pos, filename.length());
    return theSuffix.toUpperCase();
  }

  private final MapContent mapComponent;

  protected final FileDropSupport _dropSupport = new FileDropSupport();
  private final JFrame theFrame;
  private JMenuBar theMenuBar;
  private JMenu theMenu;
  private JLabel statusBar;
  private JLabel _notesIconLabel;

  private boolean notesPaneExpanded = false;

  final private Layers _theLayers = new Layers();

  private SwingToolbar theToolbar;
  private final GeoToolMapRenderer geoMapRenderer;

  private JScrollPane mapPane;

  private final GeoToolMapProjection projection;

  public DebriefLiteApp()
  {

    geoMapRenderer = new GeoToolMapRenderer();
    geoMapRenderer.loadMapContent();
    mapComponent = geoMapRenderer.getMapComponent();

    projection = new GeoToolMapProjection(mapComponent, _theLayers);

    geoMapRenderer.addRenderer(new MapRenderer()
    {

      @Override
      public void paint(final Graphics gc)
      {
        doPaint(gc);
      }
    });

    // provide some file helpers
    ImportReplay.initialise(new DebriefLiteToolParent(
        ImportReplay.IMPORT_AS_OTG, 0L));
    ImportManager.addImporter(new ImportReplay());
    // add the XML importer
    // ImportManager.addImporter(
    // new DebriefXMLReaderWriter(this));

    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (ClassNotFoundException | InstantiationException
        | IllegalAccessException | UnsupportedLookAndFeelException e)
    {
      e.printStackTrace();
    }
    theFrame = new JFrame(appName + " (" + Debrief.GUI.VersionInfo.getVersion()
        + ")");

    initForm();
    createAppPanels();
    _dropSupport.setFileDropListener(this, " .REP, .XML, .DSF, .DTF");

    theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    theFrame.setVisible(true);
    /// start application
    startDebriefLiteApplication();

  }

  protected void doPaint(Graphics gc)
  {
    final CanvasAdaptor dest = new CanvasAdaptor(projection, gc);
    dest.setLineWidth(2f);
    dest.startDraw(gc);
    _theLayers.paint(dest);
    dest.endDraw(gc);
  }

  private void addMenus()
  {
    theMenu = new JMenu("File");
    theMenu.add(new JMenuItem("New"));
    theMenu.add(new JMenuItem("Open"));
    theMenu.add(new JMenuItem("Save"));
    theMenuBar.add(theMenu);
  }

  private void addStatusBar()
  {
    statusBar = new JLabel("Status bar for displaying statuses");
    theFrame.add(statusBar, BorderLayout.SOUTH);
  }

  private void addTools(final SwingToolbar theToolbar)
  {
    final URL iconURL = getClass().getClassLoader().getResource(
        "images/16/new.png");
    final JButton newFile = new JButton("New");
    newFile.setIcon(new ImageIcon(iconURL));
    theToolbar.add(newFile);
  }

  private void createAppPanels()
  {
    final Dimension frameSize = theFrame.getSize();
    final int width = (int) frameSize.getWidth();
    final int height = (int) frameSize.getHeight();
    final JPanelWithTitleBar timeControllerPanel = new JPanelWithTitleBar(
        "Time Controller");
    final JPanelWithTitleBar outlinePanel = new JPanelWithTitleBar("Outline");
    final JPanelWithTitleBar editorPanel = new JPanelWithTitleBar(
        "Plot Editor");
    final JPanelWithTitleBar graphPanel = new JPanelWithTitleBar("Graph");
    final JScrollPane timeControllerPane = createScrollPane(
        timeControllerPanel);
    final JScrollPane outlinePane = createScrollPane(outlinePanel);
    final JScrollPane editorPane = createMapPane();// createScrollPane(editorPanel);
    geoMapRenderer.addMapTool(theToolbar);
    final JScrollPane graphPane = createScrollPane(graphPanel);
    final JScrollPane notesPane = createNotesPane();
    final JSplitPane controlPanelSplit = new JSplitPane(
        JSplitPane.VERTICAL_SPLIT, true, timeControllerPane, outlinePane);
    final JSplitPane graphSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        true, editorPane, graphPane);
    final JSplitPane leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        controlPanelSplit, graphSplit);
    final JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        leftSplit, notesPane);
    rightSplit.setOneTouchExpandable(true);

    // controlPanelSplit.setOneTouchExpandable(true);
    // graphSplit.setOneTouchExpandable(true);
    rightSplit.setDividerLocation(width - 50);
    graphSplit.setDividerLocation(height / 2 + height / 5);
    controlPanelSplit.setDividerLocation(height / 2);
    leftSplit.setDividerLocation(width / 3);
    rightSplit.setResizeWeight(0.9);
    graphSplit.setResizeWeight(0.5);
    editorPanel.addMaxListenerFor(leftSplit, graphSplit);
    graphPanel.addMinListenerFor(graphSplit);
    // leftSplit.setOneTouchExpandable(true);

    _notesIconLabel.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseClicked(final MouseEvent e)
      {

        rightSplit.getRightComponent().setMinimumSize(new Dimension());
        if (notesPaneExpanded)
        {
          rightSplit.setDividerLocation(0.97d);
        }
        else
        {
          rightSplit.setDividerLocation(0.7d);
        }
        // toggle the state
        notesPaneExpanded = !notesPaneExpanded;
      }
    });
    theFrame.add(rightSplit, BorderLayout.CENTER);
    addStatusBar();
    // dummy placeholder
    addMenus();
  }

  /**
   * creates a scroll pane with map
   *
   * @return
   */
  private JScrollPane createMapPane()
  {
    geoMapRenderer.createMapLayout();
    final MapBuilder builder = new MapBuilder();
    mapPane = builder.setMapRenderer(geoMapRenderer).enableToolbar(true)
        .setToolbar(theToolbar).build();
    _dropSupport.addComponent(mapPane);

    return mapPane;
  }

  private JScrollPane createNotesPane()
  {
    final JPanel notesPanel = new JPanel();
    notesPanel.setLayout(new FlowLayout());
    final JScrollPane notesPane = new JScrollPane(notesPanel);
    final URL url = getClass().getClassLoader().getResource(NOTES_ICON);
    _notesIconLabel = new JLabel();
    _notesIconLabel.setIcon(new ImageIcon(url));
    notesPanel.add(_notesIconLabel);
    return notesPane;
  }

  protected void exit()
  {
    _dropSupport.removeFileDropListener(this);
    System.exit(0);

  }

  @Override
  public void FilesReceived(final Vector<File> files)
  {
    setCursor(Cursor.WAIT_CURSOR);
    try
    {
      final Enumeration<File> iter = files.elements();
      while (iter.hasMoreElements())
      {
        final File file = iter.nextElement();
        openFile(file);
      }

    }
    catch (final Exception e)
    {
      Trace.trace(e);
    }

    restoreCursor();

  }

  private void handleImport(final File[] fList)
  {
    BaseImportCaller caller = new BaseImportCaller(fList, _theLayers)
    {
      // handle completion of the full import process
      @Override
      public void allFilesFinished(final File[] fNames, final Layers newData)
      {
        System.out.println("Finished reading all files");
        restoreCursor();
      }

      // handle the completion of each file
      @Override
      public void fileFinished(final File fName, final Layers newData)
      {
        System.out.println("Finished reading file" + fName);
      }
    };

    caller.start();
    // wait for a few secs and test the loaded file.
    try
    {
      Thread.sleep(400);
    }
    catch (final InterruptedException ie)
    {
    }
    System.out.println("num layers:" + _theLayers.size());
    tesTLoadedFile(_theLayers, fList[0].getName());
    caller = null;
  }

  /**
   * fill in the UI details
   */
  private void initForm()
  {
    theFrame.addWindowListener(new WindowAdapter()
    {
      @Override
      public void windowClosing(final java.awt.event.WindowEvent e)
      {
        exit();
      }
    });

    // try to give the application an icon
    final URL iconURL = getClass().getClassLoader().getResource(
        "images/icon.png");
    if (iconURL != null)
    {
      final ImageIcon myIcon = new ImageIcon(iconURL);
      if (myIcon != null)
        theFrame.setIconImage(myIcon.getImage());
    }
    // create the components
    theToolbar = new SwingToolbar(Toolbar.HORIZONTAL, "Application", null);
    addTools(theToolbar);

    // and the panel
    final JPanel topSection = new JPanel();
    topSection.setLayout(new BorderLayout());
    theMenuBar = new JMenuBar();
    theFrame.setJMenuBar(theMenuBar);

    // add them
    theFrame.getContentPane().add("North", theToolbar);

    final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

    theFrame.setSize((int) (dim.width * 0.6), (int) (dim.height * 0.6));
    final Dimension sz = theFrame.getSize();
    theFrame.setLocation((dim.width - sz.width) / 2, (dim.height - sz.height)
        / 2);

    // do any final re-arranging
    theFrame.doLayout();
  }

  /**
   * @param theFile
   *          the file to open
   */
  public final void openFile(final File theFile)
  {
    // indicate that we are busy
    this.setCursor(Cursor.WAIT_CURSOR);

    // wrap the single file into a list
    final File[] fList = new File[]
    {theFile};

    final String suff = suffixOf(theFile.getName());
    if (suff.equalsIgnoreCase(".DPL"))
    {
      MWC.GUI.Dialogs.DialogFactory.showMessage("Open File",
          "Sorry DPL file format no longer supported");
    }
    else
    {
      if ((suff.equalsIgnoreCase(".REP")) || (suff.equalsIgnoreCase(".DSF"))
          || (suff.equalsIgnoreCase(".DTF")))
      {
        handleImport(fList);
      }
      else if (suff.equalsIgnoreCase(".XML") || suff.equalsIgnoreCase(".DPF"))
      {
        handleImport(fList);
      }
      else
      {
        Trace.trace("This file type not handled:" + suff);
      }
    }

  }

  public final void restoreCursor()
  {
    theFrame.getContentPane().setCursor(null);
  }

  public final void setCursor(final int theCursor)
  {
    theFrame.getContentPane().setCursor(new Cursor(theCursor));
  }

  public void setStatus(final String message)
  {
    statusBar.setText(message);
  }

  private void startDebriefLiteApplication()
  {
  }

  // this will be removed when we have the plotting working
  private void tesTLoadedFile(final Layers _theLayers, final String fileName)
  {
    final TrackWrapper track = (TrackWrapper) _theLayers.findLayer("NELSON");
    if (track != null)
    {
      final Enumeration<Editable> enumerations = track.getPositionIterator();
      int cnt = 0;
      while (enumerations.hasMoreElements())
      {
        @SuppressWarnings("unused")
        final FixWrapper fix = (FixWrapper) enumerations.nextElement();
        cnt++;
      }

      JOptionPane.showMessageDialog(theFrame,
          "Total Number of records Read from " + fileName + " file " + cnt);
    }

  }
}
