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
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.geotools.map.MapContent;
import org.geotools.swing.JMapPane;
import org.mwc.debrief.lite.gui.DebriefLiteToolParent;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;
import org.mwc.debrief.lite.gui.LiteStepControl;
import org.mwc.debrief.lite.gui.custom.JXCollapsiblePane.Direction;
import org.mwc.debrief.lite.gui.custom.JXCollapsiblePaneWithTitle;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.mwc.debrief.lite.map.GeoToolMapRenderer.MapRenderer;
import org.mwc.debrief.lite.map.MapBuilder;
import org.mwc.debrief.lite.menu.DebriefRibbon;
import org.mwc.debrief.lite.menu.MenuUtils;
import org.mwc.debrief.lite.menu.RibbonAppMenuProvider;
import org.mwc.debrief.lite.outline.OutlinePanelView;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.substance.api.SubstanceCortex;
import org.pushingpixels.substance.api.skin.BusinessBlueSteelSkin;

import Debrief.GUI.Tote.Painters.PainterManager;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.PlainChart;
import MWC.GUI.ToolParent;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GUI.DragDrop.FileDropSupport;
import MWC.GUI.DragDrop.FileDropSupport.FileDropListener;
import MWC.GUI.LayerManager.Swing.SwingLayerManager;
import MWC.GUI.Undo.UndoBuffer;
import MWC.GenericData.TimePeriod;
import MWC.TacticalData.temporal.TimeManager;
import MWC.TacticalData.temporal.TimeProvider;
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

  /**
   * creates a scroll pane with map
   *
   * @param geoMapRenderer
   * @param dropSupport
   *
   * @return
   */
  private static JMapPane createMapPane(
      final GeoToolMapRenderer geoMapRenderer,
      final FileDropSupport dropSupport)
  {
    geoMapRenderer.createMapLayout();
    final MapBuilder builder = new MapBuilder();
    final JMapPane mapPane = (JMapPane) builder.setMapRenderer(geoMapRenderer).build();
    dropSupport.addComponent(mapPane);
    return mapPane;
  }

  public static void main(final String[] args)
  {
    javax.swing.SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
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

  private SwingLayerManager layerManager;

  private final JXCollapsiblePaneWithTitle outlinePanel =
      new JXCollapsiblePaneWithTitle(Direction.LEFT, "Outline", 400);

  private final JRibbonFrame theFrame;

  final private Layers _theLayers = new Layers();

  private final DebriefLiteToolParent _toolParent = new DebriefLiteToolParent(
      ImportReplay.IMPORT_AS_OTG, 0L);
  private final GeoToolMapProjection projection;

  private final LiteApplication app;
  
  private final LiteSession session;
  private final JLabel statusBar = new JLabel(
      "Status bar for displaying statuses");
  private final LiteStepControl _stepControl;
  private final JMapPane mapPane;
  private final TimeManager timeManager = new TimeManager();
  private final PainterManager painterManager;
  private LiteTote theTote;
  private CanvasAdaptor _theCanvas;


  public DebriefLiteApp()
  {
  //set the substance look and feel
    JFrame.setDefaultLookAndFeelDecorated(true);
    SubstanceCortex.GlobalScope.setSkin(new BusinessBlueSteelSkin());
    DisplaySplash splashScreen = new DisplaySplash(5);
    Thread t = new Thread(splashScreen);
    t.start();
    try
    {
      t.join();
    }
    catch (InterruptedException e)
    {
       //ignore
    }
    
    theFrame = new JRibbonFrame(appName 
        + " (" + Debrief.GUI.VersionInfo.getVersion()+ ")");
    theFrame.setApplicationIcon(ImageWrapperResizableIcon.getIcon(MenuUtils
        .createImage("images/icon_533.png"), MenuUtils.ICON_SIZE_32));
    
    final GeoToolMapRenderer geoMapRenderer = new GeoToolMapRenderer();
    geoMapRenderer.loadMapContent();
    final MapContent mapComponent = geoMapRenderer.getMapComponent();

    final FileDropSupport dropSupport = new FileDropSupport();
    dropSupport.setFileDropListener(this, " .REP, .XML, .DSF, .DTF, .DPF");

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

    final Clipboard _theClipboard = new Clipboard("Debrief");
    session = new LiteSession(_theClipboard, _theLayers);
    final UndoBuffer undoBuffer = session.getUndoBuffer();
    app = new LiteApplication();
    
    mapPane = createMapPane(geoMapRenderer, dropSupport);
    _theCanvas = new CanvasAdaptor(projection, mapPane.getGraphics(), Color.GRAY);

    ImportManager.addImporter(new DebriefXMLReaderWriter(app));



    final DataListener dListener = new DataListener()
    {
      @Override
      public void dataExtended(final Layers theData)
      {
        mapPane.repaint();
      }

      @Override
      public void dataModified(final Layers theData, final Layer changedLayer)
      {
        mapPane.repaint();
      }

      @Override
      public void dataReformatted(final Layers theData,
          final Layer changedLayer)
      {
        mapPane.repaint();
      }
    };
    _theLayers.addDataReformattedListener(dListener);
    _theLayers.addDataExtendedListener(dListener);
    _theLayers.addDataModifiedListener(dListener);

    _stepControl = new LiteStepControl(_toolParent);
    timeManager.addListener(_stepControl, TimeProvider.PERIOD_CHANGED_PROPERTY_NAME);
    timeManager.addListener(_stepControl, TimeProvider.TIME_CHANGED_PROPERTY_NAME);

    painterManager = new PainterManager(_stepControl);
    PlainChart theChart = new LiteChart(_theLayers, _theCanvas, mapPane);
    theTote = new LiteTote(_theLayers, _stepControl);
    final Debrief.GUI.Tote.Painters.TotePainter sp =
        new Debrief.GUI.Tote.Painters.SnailPainter(theChart, _theLayers,
            theTote);
    final Debrief.GUI.Tote.Painters.TotePainter tp =
        new Debrief.GUI.Tote.Painters.TotePainter(theChart, _theLayers,
            theTote);
    painterManager.addPainter(sp);
    painterManager.addPainter(tp);
    painterManager.setCurrentListener(tp);

    // create the components
    initForm();
    createAppPanels(geoMapRenderer, undoBuffer, dropSupport, mapPane,
        _stepControl, timeManager, projection);

    theFrame.setApplicationIcon(ImageWrapperResizableIcon.getIcon(MenuUtils.createImage("images/icon_533.png"), MenuUtils.ICON_SIZE_32));

    theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    theFrame.setVisible(true);
    
    System.out.println(mapPane.getGraphics());
  }

  private void addOutlineView(final ToolParent toolParent,
      final UndoBuffer undoBuffer)
  {
    layerManager = new OutlinePanelView(undoBuffer);
    layerManager.setObject(_theLayers);
    layerManager.setParent(toolParent);
    outlinePanel.add(layerManager, BorderLayout.CENTER);
  }

  private void createAppPanels(final GeoToolMapRenderer geoMapRenderer,
      final UndoBuffer undoBuffer, final FileDropSupport dropSupport,
      final Component mapPane, final LiteStepControl stepControl, final TimeManager timeManager, GeoToolMapProjection projection2)
  {
    // final Dimension frameSize = theFrame.getSize();
    // final int width = (int) frameSize.getWidth();

    theFrame.add(mapPane, BorderLayout.CENTER);

    theFrame.add(outlinePanel, BorderLayout.WEST);
    addOutlineView(_toolParent, undoBuffer);

    theFrame.add(statusBar, BorderLayout.SOUTH);
    // dummy placeholder
    new DebriefRibbon(theFrame.getRibbon(), _theLayers, _toolParent,
        geoMapRenderer, stepControl, timeManager, projection);
  }

  protected void doPaint(final Graphics gc)
  {
//    final CanvasAdaptor dest = new CanvasAdaptor(projection, gc, Color.WHITE);
    _theCanvas.startDraw(gc);
    _theCanvas.setLineWidth(2f);

    _theLayers.paint(_theCanvas);
    System.out.println("paint");
    painterManager.newTime(null, timeManager.getTime(), _theCanvas);

    _theCanvas.endDraw(gc);
  }

  protected void exit()
  {
    theFrame.dispose();
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
    final DebriefLiteApp source = this;
    BaseImportCaller caller = new BaseImportCaller(fList, _theLayers)
    {
      // handle completion of the full import process
      @Override
      public void allFilesFinished(final File[] fNames, final Layers newData)
      {
        SwingUtilities.invokeLater(new Runnable()
        {
          @Override
          public void run()
          {
            layerManager.dataModified(null, null);
            mapPane.repaint();
            
            restoreCursor();
            // update the time panel
            TimePeriod period = _theLayers.getTimePeriod();
            timeManager.setPeriod(source, period);
            timeManager.setTime(source, period.getStartDTG(), true);
          }
        });
      }

      // handle the completion of each file
      @Override
      public void fileFinished(final File fName, final Layers newData)
      {
        // have we got a track?
        Enumeration<Editable> ele = newData.elements();
        while(ele.hasMoreElements())
        {
          Layer l = (Layer) ele.nextElement();
          if(l instanceof LightweightTrackWrapper)
          {
            LightweightTrackWrapper track = (LightweightTrackWrapper) l;
            theTote.setPrimary(track);
          }
        }
      }
    };
    // ok, start loading
    caller.start();
  }

  /**
   * fill in the UI details
   *
   * @param theToolbar
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
    final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

    theFrame.getRibbon().setApplicationMenu(new RibbonAppMenuProvider()
        .createApplicationMenu(theFrame));
    theFrame.setSize((int) (dim.width * 0.6), (int) (dim.height * 0.6));
    final Dimension sz = theFrame.getSize();
    theFrame.setLocation((dim.width - sz.width) / 2, (dim.height - sz.height)
        / 2);

    // do any final re-arranging
    theFrame.doLayout();
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
