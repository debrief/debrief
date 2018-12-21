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
import java.awt.datatransfer.Clipboard;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
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
import org.mwc.debrief.lite.outline.OutlinePanelView;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.IconRibbonBandResizePolicy;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.ToolParent;
import MWC.GUI.Toolbar;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GUI.DragDrop.FileDropSupport;
import MWC.GUI.DragDrop.FileDropSupport.FileDropListener;
import MWC.GUI.LayerManager.Swing.SwingLayerManager;
import MWC.GUI.Tools.Swing.SwingToolbar;
import MWC.GUI.Undo.UndoBuffer;
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
  private static MapContent mapComponent;

  private static SwingLayerManager layerManager;

  private static JPanel outlinePanel;
  

  private void addOutlineView(final JPanelWithTitleBar jTitleBar,
      final ToolParent toolParent)
  {
    outlinePanel = new JPanel();
    outlinePanel.setLayout(new BorderLayout());
    outlinePanel.add(jTitleBar, BorderLayout.NORTH);
    layerManager = new OutlinePanelView(undoBuffer);
    layerManager.setParent(toolParent);
    outlinePanel.add(layerManager, BorderLayout.CENTER);
  }

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
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new DebriefLiteApp();
      }
  });
    
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

  protected final FileDropSupport _dropSupport = new FileDropSupport();
  private final JRibbonFrame theFrame;
  //private JMenuBar theMenuBar;

//  private JMenu theMenu;

  private JLabel statusBar;

  private JLabel _notesIconLabel;
  private boolean notesPaneExpanded = false;
  final private Layers _theLayers = new Layers();

  private SwingToolbar theToolbar;

  private final GeoToolMapRenderer geoMapRenderer;

  private final DebriefLiteToolParent _toolParent;
  private JScrollPane mapPane;
  private final GeoToolMapProjection projection;

  private final Clipboard _theClipboard = new Clipboard("Debrief");

  private final LiteSession session;

  private final LiteApplication app;
  
  private final UndoBuffer undoBuffer;

  public DebriefLiteApp()
  {

    geoMapRenderer = new GeoToolMapRenderer();
    geoMapRenderer.loadMapContent();
    mapComponent = geoMapRenderer.getMapComponent();

    _toolParent = new DebriefLiteToolParent(ImportReplay.IMPORT_AS_OTG, 0L);

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
    session = new LiteSession(_theClipboard, _theLayers);
    undoBuffer = session.getUndoBuffer();
    app = new LiteApplication(session);

    ImportManager.addImporter(new DebriefXMLReaderWriter(app));

    final DataListener dListener = new DataListener()
    {

      @Override
      public void dataExtended(final Layers theData)
      {
        repaint();
      }

      @Override
      public void dataModified(final Layers theData, final Layer changedLayer)
      {
        repaint();
      }

      @Override
      public void dataReformatted(final Layers theData,
          final Layer changedLayer)
      {
        repaint();
      }
    };
    _theLayers.addDataReformattedListener(dListener);
    _theLayers.addDataExtendedListener(dListener);
    _theLayers.addDataModifiedListener(dListener);

    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (ClassNotFoundException | InstantiationException
        | IllegalAccessException | UnsupportedLookAndFeelException e)
    {
      e.printStackTrace();
    }
    theFrame = new JRibbonFrame(appName + " (" + Debrief.GUI.VersionInfo.getVersion()
        + ")");

    initForm();
    createAppPanels();
    _dropSupport.setFileDropListener(this, " .REP, .XML, .DSF, .DTF, .DPF");

    theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    theFrame.setVisible(true);
  }

  private void addMenus() {
    JRibbonBand fileMenu = new JRibbonBand("File", null);
    JRibbonBand editMenu = new JRibbonBand("Edit", null);
    JRibbonBand timeControllerMenu = new JRibbonBand("Time Controller", null);
    timeControllerMenu.setResizePolicies((List) Arrays.asList(new IconRibbonBandResizePolicy(timeControllerMenu.getControlPanel())));
    JCommandButton button1 = new JCommandButton("New", null);
    JCommandButton button2 = new JCommandButton("Open Plot", null);
    JCommandButton button3 = new JCommandButton("Save", null);
    JCommandButton button4 = new JCommandButton("Exit", null);
    fileMenu.addCommandButton(button1, RibbonElementPriority.TOP);
    fileMenu.addCommandButton(button2, RibbonElementPriority.MEDIUM);
    fileMenu.addCommandButton(button3, RibbonElementPriority.MEDIUM);
    fileMenu.addCommandButton(button4, RibbonElementPriority.MEDIUM);
    fileMenu.setResizePolicies((List) Arrays.asList(
        new CoreRibbonResizePolicies.None(fileMenu.getControlPanel()),
        new IconRibbonBandResizePolicy(fileMenu.getControlPanel())));
    JCommandButton editButton1 = new JCommandButton("Undo", null);
    JCommandButton editButton2 = new JCommandButton("Redo", null);
    JCommandButton editButton3 = new JCommandButton("Cut", null);
    JCommandButton editButton4 = new JCommandButton("Copy", null);
    JCommandButton editButton5 = new JCommandButton("Paste", null);
    editMenu.addCommandButton(editButton1, RibbonElementPriority.TOP);
    editMenu.addCommandButton(editButton2, RibbonElementPriority.MEDIUM);
    editMenu.addCommandButton(editButton3, RibbonElementPriority.MEDIUM);
    editMenu.addCommandButton(editButton4, RibbonElementPriority.MEDIUM);
    editMenu.addCommandButton(editButton5, RibbonElementPriority.MEDIUM);
    editMenu.setResizePolicies((List) Arrays.asList(
        new CoreRibbonResizePolicies.None(editMenu.getControlPanel()),
        new IconRibbonBandResizePolicy(editMenu.getControlPanel())));
    /*timeControllerMenu.setResizePolicies((List) Arrays.asList(
        new CoreRibbonResizePolicies.None(timeControllerMenu.getControlPanel()),
        new IconRibbonBandResizePolicy(timeControllerMenu.getControlPanel())));
    */
    RibbonTask fileTask = new RibbonTask("File", fileMenu);
    RibbonTask editTask = new RibbonTask("Edit", editMenu);
    RibbonTask timeControllerTask = new RibbonTask("Time Controller",timeControllerMenu);
    theFrame.getRibbon().addTask(fileTask);
    theFrame.getRibbon().addTask(editTask);
    theFrame.getRibbon().addTask(timeControllerTask);
    
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
    final JPanelWithTitleBar outlineTitlePanel = new JPanelWithTitleBar(
        "Outline");
    final JPanelWithTitleBar editorPanel = new JPanelWithTitleBar(
        "Plot Editor");
    final JPanelWithTitleBar graphPanel = new JPanelWithTitleBar("Graph");
    final JScrollPane timeControllerPane = createScrollPane(
        timeControllerPanel);
    addOutlineView(outlineTitlePanel, _toolParent);
    final JScrollPane editorPane = createMapPane();// createScrollPane(editorPanel);
    geoMapRenderer.addMapTool(theToolbar);
    final JScrollPane graphPane = createScrollPane(graphPanel);
    final JScrollPane notesPane = createNotesPane();
    final JSplitPane controlPanelSplit = new JSplitPane(
        JSplitPane.VERTICAL_SPLIT, true, timeControllerPane, outlinePanel);
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

  protected void doPaint(final Graphics gc)
  {
    final CanvasAdaptor dest = new CanvasAdaptor(projection, gc);
    dest.setLineWidth(2f);
    dest.startDraw(gc);
    _theLayers.paint(dest);
    dest.endDraw(gc);
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

        final String suff = suffixOf(file.getName());
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
            // fake wrap it
            final File[] fList = new File[]
            {file};
            handleImportRep(fList);
          }
          else if (suff.equalsIgnoreCase(".XML") || suff.equalsIgnoreCase(
              ".DPF"))
          {
            handleImportDPF(file);
          }
          else
          {
            Trace.trace("This file type not handled:" + suff);
          }
        }
      }

    }
    catch (final Exception e)
    {
      Trace.trace(e);
    }

    restoreCursor();

  }

  private void handleImportDPF(final File file)
  {
    final DebriefXMLReaderWriter reader = new DebriefXMLReaderWriter(app);
    try
    {
      reader.importThis(file.getName(), new FileInputStream(file), session);
    }
    catch (final FileNotFoundException e)
    {
      _toolParent.logError(ToolParent.ERROR, "Failed to read DPF File", e);
    }
    _theLayers.fireModified(null);
  }

  private void handleImportRep(final File[] fList)
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
    layerManager.setObject(_theLayers);
    outlinePanel.validate();
    System.out.println("num layers:" + _theLayers.size());
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
   /* theMenuBar = new JMenuBar();
    theFrame.setJMenuBar(theMenuBar);

    // add them
    theFrame.getContentPane().add("North", theToolbar);
*/
    final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

    theFrame.setSize((int) (dim.width * 0.6), (int) (dim.height * 0.6));
    final Dimension sz = theFrame.getSize();
    theFrame.setLocation((dim.width - sz.width) / 2, (dim.height - sz.height)
        / 2);

    // do any final re-arranging
    theFrame.doLayout();
  }

  protected void repaint()
  {
    mapPane.repaint();
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

}
