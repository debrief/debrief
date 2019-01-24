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
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.mwc.debrief.lite.custom.JPanelWithTitleBar;
import org.mwc.debrief.lite.gui.DebriefLiteToolParent;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.mwc.debrief.lite.map.GeoToolMapRenderer.MapRenderer;
import org.mwc.debrief.lite.map.MapBuilder;
import org.mwc.debrief.lite.menu.DebriefRibbon;
import org.mwc.debrief.lite.outline.OutlinePanelView;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.substance.api.SubstanceCortex;
import org.pushingpixels.substance.api.skin.BusinessBlueSteelSkin;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.ToolParent;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GUI.DragDrop.FileDropSupport;
import MWC.GUI.DragDrop.FileDropSupport.FileDropListener;
import MWC.GUI.LayerManager.Swing.SwingLayerManager;
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
  private SwingLayerManager layerManager;
  private final JPanel outlinePanel = new JPanel();
  
  public static boolean parentLoaded;

  private void addOutlineView(final JPanelWithTitleBar jTitleBar,
      final ToolParent toolParent, UndoBuffer undoBuffer)
  {
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

  private final JRibbonFrame theFrame;

  final private Layers _theLayers = new Layers();

  private final DebriefLiteToolParent _toolParent = new DebriefLiteToolParent(
      ImportReplay.IMPORT_AS_OTG, 0L);
  private final GeoToolMapProjection projection;

  private final LiteApplication app;
  
  private final LiteSession session;
  private final JLabel statusBar = new JLabel("Status bar for displaying statuses");
  
  private final MapContent mapComponent;
    
  public DebriefLiteApp()
  {
    final GeoToolMapRenderer geoMapRenderer = new GeoToolMapRenderer();
    geoMapRenderer.loadMapContent();
    
    mapComponent = geoMapRenderer.getMapComponent();

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
    UndoBuffer undoBuffer = session.getUndoBuffer();
    app = new LiteApplication();

    ImportManager.addImporter(new DebriefXMLReaderWriter(app));
    
    final Component mapPane = createMapPane(geoMapRenderer, dropSupport);

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

    //set the substance look and feel
    JFrame.setDefaultLookAndFeelDecorated(true);
    SubstanceCortex.GlobalScope.setSkin(new BusinessBlueSteelSkin());
    
    theFrame = new JRibbonFrame(appName + " (" + Debrief.GUI.VersionInfo.getVersion()
        + ")");

    // create the components
    initForm();
    createAppPanels(geoMapRenderer, undoBuffer, dropSupport, mapPane);

    theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    theFrame.setVisible(true);
    
    // register resize listener
    theFrame.addComponentListener(new ComponentAdapter() {
    	@Override
    	public void componentResized(ComponentEvent e) {
    		
    		SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					  if(!parentLoaded)
						  parentLoaded = true;
					  else {
						  theFrame.validate();
					  }
					  //theFrame.repaint();
					  //theFrame.validate();
				}
    		});
     	}
	});

  }

  private void createAppPanels(GeoToolMapRenderer geoMapRenderer,
      UndoBuffer undoBuffer, FileDropSupport dropSupport,
      final Component mapPane)
  {
    final Dimension frameSize = theFrame.getSize();
    final int width = (int) frameSize.getWidth();
    final JPanelWithTitleBar outlineTitlePanel = new JPanelWithTitleBar(
        "Outline");
    final JPanelWithTitleBar editorPanel = new JPanelWithTitleBar(
        "Plot Editor");
    final JPanelWithTitleBar graphPanel = new JPanelWithTitleBar("Graph");
    addOutlineView(outlineTitlePanel, _toolParent, undoBuffer);
    final JScrollPane graphPane = createScrollPane(graphPanel);
    final JPanelWithTitleBar notesPane = new JPanelWithTitleBar(
        "Notes");
    final JSplitPane graphSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        true, mapPane, graphPane);
    
    graphSplit.addPropertyChangeListener(new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// TODO Auto-generated method stub
		}
	});
    
    final JSplitPane leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        outlinePanel, graphSplit);
    final JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        leftSplit, notesPane);
    rightSplit.setOneTouchExpandable(true);
    leftSplit.setOneTouchExpandable(true);
    graphSplit.setOneTouchExpandable(true);
    
    graphSplit.setDividerSize(42);

    rightSplit.setDividerLocation(3 * width / 4);
    graphSplit.setDividerLocation(300);
    leftSplit.setDividerLocation(width / 3);
    rightSplit.setResizeWeight(0.9);
    graphSplit.setResizeWeight(0.5);
    editorPanel.addMaxListenerFor(leftSplit, graphSplit);
    graphPanel.addMinListenerFor(graphSplit);

    theFrame.add(rightSplit, BorderLayout.CENTER);
    theFrame.add(statusBar, BorderLayout.SOUTH);
    // dummy placeholder
    DebriefRibbon ribbon = new DebriefRibbon(theFrame, _theLayers, _toolParent, geoMapRenderer);
    ribbon.addMenus();

  }

  /**
   * creates a scroll pane with map
   * @param geoMapRenderer 
   * @param dropSupport 
   *
   * @return
   */
  private static Component createMapPane(GeoToolMapRenderer geoMapRenderer, FileDropSupport dropSupport)
  {
    geoMapRenderer.createMapLayout();
    final MapBuilder builder = new MapBuilder();
    Component mapPane = builder.setMapRenderer(geoMapRenderer).build();
    dropSupport.addComponent(mapPane);
    return mapPane;
  }

  protected void doPaint(final Graphics gc)
  {
    final CanvasAdaptor dest = new CanvasAdaptor(projection, gc);
    dest.setLineWidth(2f);
    dest.startDraw(gc);
    _theLayers.paint(dest);
    dest.endDraw(gc);
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
            SwingUtilities.invokeLater(new Runnable()
            {
              
              @Override
              public void run()
              {
                handleImportRep(fList);    
              }
            });
            
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
      }

      // handle the completion of each file
      @Override
      public void fileFinished(final File fName, final Layers newData)
      {
        System.out.println("Finished reading file" + fName);
        SwingUtilities.invokeLater(new Runnable()
        {
          @Override
          public void run()
          {
            outlinePanel.invalidate();
            layerManager.setObject(_theLayers);
            outlinePanel.validate();
            restoreCursor();
          }
        });
      }
    };

    caller.start();
    // wait for a few secs and test the loaded file.
    
    System.out.println("num layers:" + _theLayers.size());
    caller = null;
  }
  
  protected void exit()
  {
    System.exit(0);
  }

  /**
   * fill in the UI details
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
    final URL iconURL = getClass().getClassLoader().getResource(
        "images/icon.png");
    if (iconURL != null)
    {
      final ImageIcon myIcon = new ImageIcon(iconURL);
      if (myIcon != null)
        theFrame.setIconImage(myIcon.getImage());
    }
    final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

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
