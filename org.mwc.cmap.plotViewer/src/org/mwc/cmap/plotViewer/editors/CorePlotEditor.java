/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.plotViewer.editors;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.SubActionBars2;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.cmap.core.interfaces.IPlotGUI;
import org.mwc.cmap.core.interfaces.IResourceProvider;
import org.mwc.cmap.core.operations.IUndoable;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.gt2plot.data.GeoToolsLayer;
import org.mwc.cmap.gt2plot.data.ShapeFileLayer;
import org.mwc.cmap.gt2plot.data.WorldImageLayer;
import org.mwc.cmap.gt2plot.proj.GtProjection;
import org.mwc.cmap.naturalearth.Activator;
import org.mwc.cmap.naturalearth.wrapper.NELayer;
import org.mwc.cmap.plotViewer.PlotViewerPlugin;
import org.mwc.cmap.plotViewer.actions.IChartBasedEditor;
import org.mwc.cmap.plotViewer.editors.chart.CursorTracker;
import org.mwc.cmap.plotViewer.editors.chart.RangeTracker;
import org.mwc.cmap.plotViewer.editors.chart.SWTCanvas;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart;
import org.mwc.cmap.plotViewer.editors.chart.SWTChart.PlotMouseDragger;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable.EditorType;
import MWC.GUI.ExternallyManagedDataLayer;
import MWC.GUI.GeoToolsHandler;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener2;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.ChartBoundsWrapper;
import MWC.GUI.Tools.Chart.DblClickEdit;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import interfaces.INameablePart;

public abstract class CorePlotEditor extends EditorPart implements
    IResourceProvider, IControllableViewport, ISelectionProvider, IPlotGUI,
    IChartBasedEditor, IUndoable, CanvasType.ScreenUpdateProvider,
    INameablePart
{

  private static final String CONTEXT_ID = "org.mwc.cmap.plotEditorContext";
  // //////////////////////////////
  // member data
  // //////////////////////////////

  /**
   * the chart we store/manager
   */
  protected SWTChart _myChart = null;

  /**
   * we may learn the background color of the canvas before it has loaded. temporarily store the
   * color here, and set the background color when we load the canvas
   */
  private Color _pendingCanvasBackgroundColor;

  /**
   * the graphic data we know about
   */
  protected Layers _myLayers;

  /**
   * the object which listens to time-change events. we remember it so that it can be deleted when
   * we close
   */
  protected PropertyChangeListener _timeListener;

  /**
   * store a pending projection. we do this because sometimes we may learn about the projection
   * before we create our child components, you see.
   */
  protected PlainProjection _pendingProjection;

  // drag-drop bits

  protected DropTarget target;

  private Vector<ISelectionChangedListener> _selectionListeners;

//  private ISelectionChangedListener _selectionChangeListener;

  private ISelection _currentSelection;

//  private CanvasType.PaintListener _selectionPainter;

  /**
   * keep track of whether the current plot is dirty...
   */
  protected boolean _plotIsDirty = false;

  // ///////////////////////////////////////////////
  // dummy bits applicable for our dummy interface
  // ///////////////////////////////////////////////

  protected DataListener2 _listenForMods;

  private boolean _ignoreDirtyCalls;

  protected PartMonitor _myPartMonitor;

  protected GeoToolsHandler _myGeoHandler;
  protected IContextActivation _myActivation;

  protected IResourceChangeListener resourceChangeListener =
      new IResourceChangeListener()
      {

        @Override
        public void resourceChanged(IResourceChangeEvent event)
        {
          IResourceDelta delta = event.getDelta();
          final int eventType = event.getType();
          if (delta != null)
          {
            try
            {
              delta.accept(new IResourceDeltaVisitor()
              {

                @Override
                public boolean visit(IResourceDelta delta) throws CoreException
                {
                  IResource resource = delta.getResource();
                  if (resource instanceof IWorkspaceRoot)
                  {
                    return true;
                  }
                  if (resource instanceof IProject)
                  {
                    IEditorInput input = getEditorInput();
                    if (input instanceof IFileEditorInput)
                    {
                      IProject project =
                          ((IFileEditorInput) input).getFile().getProject();
                      if (resource.equals(project)
                          && (eventType == IResourceChangeEvent.PRE_DELETE || eventType == IResourceChangeEvent.PRE_CLOSE))
                      {
                        closeEditor(false);
                        return false;
                      }
                    }
                    return true;
                  }
                  if (resource instanceof IFolder)
                  {
                    return true;
                  }
                  if (resource instanceof IFile)
                  {
                    IEditorInput input = getEditorInput();
                    if (input instanceof IFileEditorInput)
                    {
                      IFile file = ((IFileEditorInput) input).getFile();
                      if (resource.equals(file)
                          && delta.getKind() == IResourceDelta.REMOVED)
                      {
                        IPath movedToPath = delta.getMovedToPath();
                        if (movedToPath != null)
                        {
                          IResource path =
                              ResourcesPlugin.getWorkspace().getRoot()
                                  .findMember(movedToPath);
                          if (path instanceof IFile)
                          {
                            final FileEditorInput newInput =
                                new FileEditorInput((IFile) path);
                            Display.getDefault().asyncExec(new Runnable()
                            {

                              @Override
                              public void run()
                              {
                                setInputWithNotify(newInput);
                              }
                            });
                          }
                        }
                        else
                        {
                          closeEditor(false);
                        }
                      }
                      if (resource.equals(file)
                          && (delta.getKind() == IResourceDelta.CHANGED && (delta
                              .getFlags() & IResourceDelta.CONTENT) != 0))
                      {
                        reload(file);
                      }
                    }
                  }
                  return false;
                }

              });
            }
            catch (CoreException e)
            {
              IStatus status =
                  new Status(IStatus.INFO, PlotViewerPlugin.PLUGIN_ID, e
                      .getLocalizedMessage(), e);
              PlotViewerPlugin.getDefault().getLog().log(status);
            }
          }
        }
      };

  // //////////////////////////////
  // constructor
  // //////////////////////////////

  public CorePlotEditor()
  {
    super();

    // create the projection, we're going to need it to load the data, before we
    // have the chart created
    _myGeoHandler = new GtProjection();

    _myLayers = new Layers()
    {

      /**
			 * 
			 */
      private static final long serialVersionUID = 1L;

      @Override
      public void addThisLayer(final Layer theLayer)
      {
        Layer wrappedLayer = null;

        // ok, if this is an externally managed layer (and we're doing
        // GT-plotting, we will wrap it, and actually add the wrapped layer
        if (theLayer instanceof ExternallyManagedDataLayer)
        {
          final ExternallyManagedDataLayer dl =
              (ExternallyManagedDataLayer) theLayer;
          if (dl.getDataType().equals(
              MWC.GUI.Shapes.ChartBoundsWrapper.WORLDIMAGE_TYPE))
          {
            final GeoToolsLayer gt =
                new WorldImageLayer(dl.getName(), dl.getFilename());

            gt.setVisible(dl.getVisible());
            _myGeoHandler.addGeoToolsLayer(gt);
            wrappedLayer = gt;
          }
          else if (dl.getDataType().equals(
              MWC.GUI.Shapes.ChartBoundsWrapper.SHAPEFILE_TYPE))
          {
            // just see if it's a raster extent layer (special processing)
            if (dl.getName().equals(WorldImageLayer.RASTER_FILE))
            {
              // special processing - wrap it.
              wrappedLayer =
                  WorldImageLayer.RasterExtentHelper.loadRasters(dl
                      .getFilename(), this);
            }
            else
            {
              // ok, it's a normal shapefile: load it.
              final GeoToolsLayer gt =
                  new ShapeFileLayer(dl.getName(), dl.getFilename());
              gt.setVisible(dl.getVisible());
              _myGeoHandler.addGeoToolsLayer(gt);
              wrappedLayer = gt;
            }
          }
          else if (ChartBoundsWrapper.NELAYER_TYPE.equals(dl.getDataType()))
          {
            final NELayer gt = new NELayer(Activator.getDefault()
                .getDefaultStyleSet(), dl.getName());
            gt.setVisible(dl.getVisible());
            _myGeoHandler.addGeoToolsLayer(gt);
            wrappedLayer = gt;
          }
          if (wrappedLayer != null)
            super.addThisLayer(wrappedLayer);
        }
        else
        {
          super.addThisLayer(theLayer);
        }
        
        layerAdded(theLayer);
      }

      @Override
      public void removeThisLayer(final Layer theLayer)
      {
        if (theLayer instanceof GeoToolsLayer)
        {
          // get the content
          final GtProjection gp =
              (GtProjection) _myChart.getCanvas().getProjection();
          final GeoToolsLayer gt = (GeoToolsLayer) theLayer;
          gt.clearMap();

          if (gp.numLayers() == 0)
          {
            // ok - we've got to force the data rea
            final WorldArea area =
                _myChart.getCanvas().getProjection().getDataArea();
            _myChart.getCanvas().getProjection().setDataArea(area);
          }

        }

        // and remove from the actual list
        super.removeThisLayer(theLayer);

      }

    };

    _listenForMods = new DataListener2()
    {

      public void dataModified(final Layers theData, final Layer changedLayer)
      {
        fireDirty();
      }

      public void dataExtended(final Layers theData)
      {
        layersExtended();
        fireDirty();
      }

      public void
          dataReformatted(final Layers theData, final Layer changedLayer)
      {
        fireDirty();
      }

      @Override
      public void dataExtended(final Layers theData, final Plottable newItem,
          final HasEditables parent)
      {
        layersExtended();
        fireDirty();
      }

    };
    _myLayers.addDataExtendedListener(_listenForMods);
    _myLayers.addDataModifiedListener(_listenForMods);
    _myLayers.addDataReformattedListener(_listenForMods);

    // and listen for new times
    _timeListener = new PropertyChangeListener()
    {
      public void propertyChange(final PropertyChangeEvent arg0)
      {

        // right, retrieve the time
        final HiResDate newDTG = (HiResDate) arg0.getNewValue();
        timeChanged(newDTG);

        // now make a note that the current DTG has changed
        fireDirty();
      }
    };

//    _selectionChangeListener = new ISelectionChangedListener()
//    {
//
//      @Override
//      public void selectionChanged(final SelectionChangedEvent event)
//      {
//        final ISelection sel = event.getSelection();
//        if (!(sel instanceof IStructuredSelection))
//          return;
//
//        final IStructuredSelection ss = (IStructuredSelection) sel;
//        SWTChart theChart = getChart();
//        if (theChart == null)
//          return;
//
//        final CanvasType can = theChart.getCanvas();
//
//        // unselect the current selection
//        if (_currentSelection != null
//            && _currentSelection instanceof IStructuredSelection)
//        {
//          can.removePainter(_selectionPainter);
//          final List<EditableWrapper> eds =
//              getSelItems((IStructuredSelection) _currentSelection);
//          for (EditableWrapper ed : eds)
//          {
//            getChart().update(ed.getTopLevelLayer());
//          }
//        }
//        // store the new selection
//        _currentSelection = ss;
//
//        // select the current selection
//        can.addPainter(_selectionPainter);
//        final List<EditableWrapper> eds = getSelItems(ss);
//        for (EditableWrapper ed : eds)
//        {
//          getChart().update(ed.getTopLevelLayer());
//        }
//      }
//    };

//    _selectionPainter = new CanvasType.PaintAdaptor()
//    {
//      @Override
//      public void paintMe(CanvasType dest)
//      {
//        if (_currentSelection != null
//            && _currentSelection instanceof IStructuredSelection)
//        {
//          List<EditableWrapper> selItems =
//              getSelItems((IStructuredSelection) _currentSelection);
//          for (EditableWrapper ed : selItems)
//          {
//            if (ed != null)
//            {
//              Plottable theE = (Plottable) ed.getEditable();
//              if (theE.getVisible())
//              {
//                // if (!(theE instanceof DoNotHighlightMe))
//                drawHighlightedBorder(dest, theE.getBounds());
//              }
//            }
//          }
//        }
//      }
//
//      @Override
//      public String getName()
//      {
//        return "SELECTION PAINTER";
//      }
//    };
  }

  /** utility function for tracking new layers getting added
   * 
   * @param layer the new layer
   */
  protected void layerAdded(final Layer layer)
  {
    
  }
  
  /**
   * get the first NNN valid items from the selection
   * 
   * @param ss
   *          the current structured selection
   * @return the first 20 items
   */
//  private List<EditableWrapper> getSelItems(final IStructuredSelection ss)
//  {
//    List<EditableWrapper> res = new ArrayList<EditableWrapper>();
//    final Iterator<?> selIterator = ss.iterator();
//
//    // limit how many entities to highlight
//    final int MAX_HIGHLIGHT = 20;
//
//    while (selIterator.hasNext() && res.size() < MAX_HIGHLIGHT)
//    {
//      final Object o = selIterator.next();
//      if (o instanceof EditableWrapper)
//      {
//        final EditableWrapper pw = (EditableWrapper) o;
//        if (pw.getEditable() instanceof Plottable)
//        {
//          res.add(pw);
//        }
//      }
//    }
//    return res;
//  }

  private IPartListener partListener = new IPartListener()
  {

    @Override
    public void partOpened(IWorkbenchPart part)
    {
      activateContext(part);
    }

    @Override
    public void partDeactivated(IWorkbenchPart part)
    {
      deactivateContext(part);
    }

    @Override
    public void partClosed(IWorkbenchPart part)
    {
      deactivateContext(part);
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part)
    {
      activateContext(part);
    }

    @Override
    public void partActivated(IWorkbenchPart part)
    {
      activateContext(part);
    }

    private void activateContext(IWorkbenchPart part)
    {
      if (part == CorePlotEditor.this && (_myActivation == null))
      {
        _myActivation = getContextService().activateContext(CONTEXT_ID);
      }
    }

    private void deactivateContext(IWorkbenchPart part)
    {
      if (part == CorePlotEditor.this && _myActivation != null)
      {
        getContextService().deactivateContext(_myActivation);
        _myActivation = null;
      }
    }
  };

  private ObjectUndoContext undoContext;

  private UndoActionHandler undoAction;

  private RedoActionHandler redoAction;

  public void dispose()
  {
    // ok, tell the chart to self-destruct (And dispose/release of any objects)
    _myChart.close();
    _myChart = null;
    undoAction.dispose();
    redoAction.dispose();
    OperationHistoryFactory.getOperationHistory().dispose(undoContext, true,
        true, true);

    super.dispose();

    getSite().getPage().removePartListener(partListener);
    ResourcesPlugin.getWorkspace().removeResourceChangeListener(
        resourceChangeListener);

    // empty the part monitor
    if (_myPartMonitor != null)
    {
      _myPartMonitor.ditch();
      _myPartMonitor = null;
    }

    // and the layers
    _myLayers.close();

    if (_myGeoHandler != null)
    {
      _myGeoHandler.dispose();
      _myGeoHandler = null;
    }

    // some other items
    _timeListener = null;
  }

  protected void closeEditor(final boolean save)
  {
    Display.getDefault().asyncExec(new Runnable()
    {

      @Override
      public void run()
      {
        getSite().getPage().closeEditor(CorePlotEditor.this, save);
      }
    });
  }

  public void createPartControl(final Composite parent)
  {
    // hey, create the chart
    _myChart = createTheChart(parent);

    // set the chart color, if we have one
    if (_pendingCanvasBackgroundColor != null)
    {
      _myChart.getCanvas().setBackgroundColor(_pendingCanvasBackgroundColor);
      // and promptly forget it
      _pendingCanvasBackgroundColor = null;
    }

    // and update the projection, if we have one
    if (_pendingProjection != null)
    {
      _myChart.getCanvas().setProjection(_pendingProjection);
      _pendingProjection = null;
    }

    // and the drop support
    configureFileDropSupport();

    // and add our dbl click listener
    // and add our dbl click listener
    getChart().addCursorDblClickedListener(new DblClickEdit(null)
    {
      /**
			 * 
			 */
      private static final long serialVersionUID = 1L;

      protected void addEditor(final Plottable res, final EditorType e,
          final Layer parentLayer)
      {
        selectPlottable(res, parentLayer);
      }

      protected void handleItemNotFound(final PlainProjection projection)
      {
        putBackdropIntoProperties();
      }
    });

    getSite().setSelectionProvider(this);

    // and over-ride the undo button
    undoContext = new ObjectUndoContext(this, "CMAP");
    undoAction = new UndoActionHandler(getEditorSite(), undoContext);
    undoAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_UNDO);
    redoAction = new RedoActionHandler(getEditorSite(), undoContext);
    redoAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_REDO);

    getEditorSite().getActionBars().setGlobalActionHandler(
        ActionFactory.UNDO.getId(), undoAction);
    getEditorSite().getActionBars().setGlobalActionHandler(
        ActionFactory.REDO.getId(), redoAction);

    // put in the plot-copy support

    final IAction _copyClipboardAction = new Action()
    {
      public void runWithEvent(final Event event)
      {
        SWTCanvas canvas = (SWTCanvas) getChart().getCanvas();
        Image image = null;
        try
        {
          image = canvas.getImage();
          if (image != null)
          {
            final BufferedImage _awtImage =
                PlotViewerPlugin.convertToAWT(image.getImageData());
            Transferable t = new Transferable()
            {

              public DataFlavor[] getTransferDataFlavors()
              {
                return new DataFlavor[]
                {DataFlavor.imageFlavor};
              }

              public boolean isDataFlavorSupported(DataFlavor flavor)
              {
                if (flavor == DataFlavor.imageFlavor)
                  return true;
                return false;
              }

              public Object getTransferData(DataFlavor flavor)
                  throws UnsupportedFlavorException, IOException
              {
                if (isDataFlavorSupported(flavor))
                {
                  return _awtImage;
                }
                return null;
              }

            };

            ClipboardOwner co = new ClipboardOwner()
            {

              public void lostOwnership(Clipboard clipboard,
                  Transferable contents)
              {
              }

            };
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            cb.setContents(t, co);
          }
        }
        finally
        {
          if (image != null)
          {
            image.dispose();
          }
        }
      }
    };

    final IActionBars actionBars = getEditorSite().getActionBars();
    actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(),
        _copyClipboardAction);

    _myPartMonitor =
        new PartMonitor(getSite().getWorkbenchWindow().getPartService());

    // listen out for us gaining focus - so we can set the cursort tracker
    listenForMeGainingLosingFocus();

    listenForSelectionChange();

    getSite().getPage().addPartListener(partListener);
    ResourcesPlugin.getWorkspace().addResourceChangeListener(
        resourceChangeListener,
        IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE
            | IResourceChangeEvent.POST_CHANGE);
  }

  private IContextService getContextService()
  {
    return (IContextService) getSite().getService(IContextService.class);
  }

  private void listenForMeGainingLosingFocus()
  {
    final EditorPart linkToMe = this;
    _myPartMonitor.addPartListener(CorePlotEditor.class,
        PartMonitor.DEACTIVATED, new PartMonitor.ICallback()
        {
          public void eventTriggered(final String type, final Object instance,
              final IWorkbenchPart parentPart)
          {
            if (linkToMe.equals(instance))
              _currentSelection = null;
          }
        });
    _myPartMonitor.addPartListener(CorePlotEditor.class, PartMonitor.ACTIVATED,
        new PartMonitor.ICallback()
        {
          public void eventTriggered(final String type, final Object instance,
              final IWorkbenchPart parentPart)
          {
            if (linkToMe.equals(instance))
            {
              // tell the cursor track that we're it's bitch.
              RangeTracker.displayResultsIn(linkToMe);
              CursorTracker.trackThisChart(_myChart, linkToMe);
            }
          }
        });
  }

  @SuppressWarnings("unused")
  private void listenForSelectionChange()
  {
    _myPartMonitor.addPartListener(ISelectionProvider.class,
        PartMonitor.ACTIVATED, new PartMonitor.ICallback()
        {
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final ISelectionProvider iS = (ISelectionProvider) part;
            // TODO- make it possible for use to indicate if highlights get
            // plotted (prob via Layer Manager)
            // iS.addSelectionChangedListener(_selectionChangeListener);
          }
        });
    _myPartMonitor.addPartListener(ISelectionProvider.class,
        PartMonitor.DEACTIVATED, new PartMonitor.ICallback()
        {
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final ISelectionProvider iS = (ISelectionProvider) part;
            // TODO- make it possible for use to indicate if highlights get
            // plotted (prob via Layer Manager)
            // iS.removeSelectionChangedListener(_selectionChangeListener);
          }
        });
  }

  /**
   * ok - let somebody else select an item on the plot. The initial reason for making this public
   * was so that when a new item is created, we can select it on the plot. The plot then fires a
   * 'selected' event, and the new item is shown in the properties window. Cool.
   * 
   * @param target1
   *          - the item to select
   * @param parentLayer
   *          - the item's parent layer. Used to decide which layers to update.
   */
  public void selectPlottable(final Plottable target1, final Layer parentLayer)
  {
    CorePlugin.logError(Status.INFO,
        "Double-click processed, opening property editor for:" + target1, null);
    final EditableWrapper parentP =
        new EditableWrapper(parentLayer, null, getChart().getLayers());
    final EditableWrapper wrapped =
        new EditableWrapper(target1, parentP, getChart().getLayers());
    final ISelection selected = new StructuredSelection(wrapped);
    fireSelectionChanged(selected);
  }

//  private void drawHighlightedBorder(final CanvasType can,
//      final WorldArea worldArea)
//  {
//    if (worldArea == null)
//      return;
//
//    can.setColor(new Color(255, 255, 255, 45));
//    can.setLineStyle(CanvasType.DOT_DASH);
//    can.setLineWidth(2);
//
//    // ok, get the TL & BR coordinates
//    WorldLocation tl = worldArea.getTopLeft();
//    WorldLocation br = worldArea.getBottomRight();
//
//    // now put them into a rectangle in screen coords
//    Rectangle rect = new Rectangle(can.toScreen(tl));
//    rect.add(can.toScreen(br));
//
//    // now expand the rectangle
//    final int BORDER = 3;
//    rect.grow(BORDER, BORDER);
//
//    // and draw the rectangle
//    can.drawRect(rect.x, rect.y, rect.width, rect.height);
//
//    // lastly, loop through the points
//    PathIterator pi = rect.getPathIterator(new AffineTransform());
//    double[] coords = new double[2];
//    while (!pi.isDone())
//    {
//      int code = pi.currentSegment(coords);
//      // is this a new coordinate?
//      if (code == PathIterator.SEG_LINETO)
//      {
//        // yes, draw a corner marker
//        can.fillRect((int) coords[0] - 2, (int) coords[1] - 2, 4, 4);
//      }
//      // and move to the next
//      pi.next();
//    }
//
//  }

  /**
   * place the chart in the properties window
   * 
   */
  private final void putBackdropIntoProperties()
  {
    final SWTCanvas can = (SWTCanvas) getChart().getCanvas();
    final EditableWrapper wrapped =
        new EditableWrapper(can, getChart().getLayers());
    final ISelection sel = new StructuredSelection(wrapped);
    fireSelectionChanged(sel);
  }

  /**
   * create the chart we're after
   * 
   * @param parent
   *          the parent object to stick it into
   */
  protected SWTChart createTheChart(final Composite parent)
  {
    final SWTChart res = new SWTChart(_myLayers, parent,(PlainProjection) _myGeoHandler)
    {

      /**
			 * 
			 */
      private static final long serialVersionUID = 1L;

      public void chartFireSelectionChanged(final ISelection sel)
      {
        fireSelectionChanged(sel);
      }
    };
    return res;
  }

  /**
   * sort out the file-drop target
   */
  private void configureFileDropSupport()
  {
    final int dropOperation = DND.DROP_COPY;
    final Transfer[] dropTypes =
    {FileTransfer.getInstance()};

    target = new DropTarget(_myChart.getCanvasControl(), dropOperation);
    target.setTransfer(dropTypes);
    target.addDropListener(new DropTargetListener()
    {
      public void dragEnter(final DropTargetEvent event)
      {
        if (FileTransfer.getInstance().isSupportedType(event.currentDataType)
            && event.detail != DND.DROP_COPY)
        {
          event.detail = DND.DROP_COPY;
        }
      }

      public void dragLeave(final DropTargetEvent event)
      {
      }

      public void dragOperationChanged(final DropTargetEvent event)
      {
      }

      public void dragOver(final DropTargetEvent event)
      {
      }

      public void dropAccept(final DropTargetEvent event)
      {
      }

      public void drop(final DropTargetEvent event)
      {
        if (FileTransfer.getInstance().isSupportedType(event.currentDataType))
        {
          final String[] fileNames = (String[]) event.data;
          if (fileNames != null)
          {
            filesDropped(fileNames);
          }
        }
      }

    });

  }

  /**
   * process the files dropped onto this panel
   * 
   * @param fileNames
   *          list of filenames
   */
  protected void filesDropped(final String[] fileNames)
  {
    System.out.println("Files dropped");
  }

  @Override
  public void setName(final String name)
  {
    final Runnable runner = new Runnable()
    {
      @Override
      public void run()
      {
        setPartName(name);
      }
    };

    // are we in the display thread?
    if (Display.getCurrent() == null)
    {
      // no - do update in correct thread
      Display.getDefault().asyncExec(runner);
    }
    else
    {
      runner.run();
    }
  }

  public void setFocus()
  {
    // just put some kind of blank object into the properties window
    // putBackdropIntoProperties();

    // ok, set the drag mode to whatever our common "mode" is.
    // - start off by getting the current mode
    final PlotMouseDragger curMode = PlotViewerPlugin.getCurrentMode();

    // has one been set?
    if (curMode != null)
    {
      // yup, better observe it then
      _myChart.setDragMode(curMode);
    }
    _myChart.getCanvasControl().forceFocus();

  }

  @SuppressWarnings(
  {"rawtypes", "unchecked"})
  public Object getAdapter(final Class adapter)
  {
    final Object res;

    // so, is he looking for the layers?
    if (adapter == CorePlotEditor.class)
    {
      res = this;
    }
    else if (adapter == ISelectionProvider.class)
    {
      res = this;
    }
    else if (adapter == IControllableViewport.class)
    {
      res = this;
    }
    else if (adapter == CanvasType.class)
    {
      res = _myChart.getCanvas();
    }
    else if (adapter == CanvasType.ScreenUpdateProvider.class)
    {
      res = this;
    }
    else if (IEditorPart.class.equals(adapter))
    {
      res = this;
    }
    else if (adapter == INameablePart.class)
    {
      res = this;
    }
    else
    {
      res = null;
    }

    return res;
  }

  protected void timeChanged(final HiResDate newDTG)
  {
  }

  /**
   * return the file representing where this plot is stored
   * 
   * @return the file location
   */
  public IResource getResource()
  {
    // have we been saved yet?
    return null;
  }

  public WorldArea getViewport()
  {
    return getChart().getCanvas().getProjection().getDataArea();
  }

  public void setViewport(final WorldArea target)
  {
    getChart().getCanvas().getProjection().setDataArea(target);
  }

  public PlainProjection getProjection()
  {
    return getChart().getCanvas().getProjection();
  }

  public void setProjection(final PlainProjection proj)
  {
    // do we have a chart yet?
    if (_myChart == null)
    {
      // nope, better remember it
      _pendingProjection = proj;
    }
    else
    {
      // yes, just update it.
      _myChart.getCanvas().setProjection(proj);
    }

  }

  public SWTChart getChart()
  {
    return _myChart;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener
   * (org.eclipse.jface.viewers.ISelectionChangedListener)
   */
  public void addSelectionChangedListener(
      final ISelectionChangedListener listener)
  {
    if (_selectionListeners == null)
      _selectionListeners = new Vector<ISelectionChangedListener>(0, 1);

    // see if we don't already contain it..
    if (!_selectionListeners.contains(listener))
      _selectionListeners.add(listener);
  }

  /**
   * let someone listen to screen updates
   * 
   * @param listener
   */
  @Override
  public void addScreenUpdateListener(SWTCanvas.ScreenUpdateListener listener)
  {
    if (getChart() != null)
    {
      getChart().getSWTCanvas().addScreenUpdateListener(listener);
    }
  }

  /**
   * let someone listen to screen updates
   * 
   * @param listener
   */
  @Override
  public void
      removeScreenUpdateListener(SWTCanvas.ScreenUpdateListener listener)
  {
    if (getChart() != null)
    {
      getChart().getSWTCanvas().removeScreenUpdateListener(listener);
    }
  }

  /**
   * Returns the ActionbarContributor for the Editor. ISelectionChangedListener
   * 
   * @return the ActionbarContributor for the Editor.
   */
  public SubActionBars2 getActionbar()
  {
    return (SubActionBars2) getEditorSite().getActionBars();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
   */
  public ISelection getSelection()
  {
    return _currentSelection;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener
   * (org.eclipse.jface.viewers.ISelectionChangedListener)
   */
  public void removeSelectionChangedListener(
      final ISelectionChangedListener listener)
  {
    _selectionListeners.remove(listener);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface
   * .viewers.ISelection)
   */
  public void setSelection(final ISelection selection)
  {
    _currentSelection = selection;
  }

  public void fireSelectionChanged(final ISelection sel)
  {
    // just double-check that we're not already processing this
    if (!sel.equals(_currentSelection))
    {
      _currentSelection = sel;
      if (_selectionListeners != null)
      {
        final SelectionChangedEvent sEvent =
            new SelectionChangedEvent(this, sel);
        for (final Iterator<ISelectionChangedListener> stepper =
            _selectionListeners.iterator(); stepper.hasNext();)
        {
          final ISelectionChangedListener thisL = stepper.next();
          if (thisL != null)
          {
            thisL.selectionChanged(sEvent);
          }
        }
      }
    }
  }

  /**
   * hmm, are we dirty?
   * 
   * @return
   */
  public boolean isDirty()
  {
    return _plotIsDirty;
  }

  /**
   * make a note that the data is now dirty, and needs saving.
   */
  public void fireDirty()
  {
    if (!_ignoreDirtyCalls)
    {
      _plotIsDirty = true;

      // fire the modified event, in the display thread
      Display.getDefault().syncExec(new Runnable()
      {
        public void run()
        {
          firePropertyChange(PROP_DIRTY);
        }
      });
    }
  }

  /**
   * new data has been added - have a look at the times
   */
  protected void layersExtended()
  {

  }

  /**
   * start ignoring dirty calls, since we're loading the initial data (for instance)
   */
  public void startIgnoringDirtyCalls()
  {
    _ignoreDirtyCalls = true;
  }

  /**
   * start ignoring dirty calls, since we're loading the initial data (for instance)
   */
  public void stopIgnoringDirtyCalls()
  {
    _ignoreDirtyCalls = false;
  }

  /**
   * @return
   */
  public Color getBackgroundColor()
  {
    return _myChart.getCanvas().getBackgroundColor();
  }

  /**
   * @param theColor
   */
  public void setBackgroundColor(final Color theColor)
  {
    if (_myChart == null)
      _pendingCanvasBackgroundColor = theColor;
    else
      _myChart.getCanvas().setBackgroundColor(theColor);
  }

  public void update()
  {
    _myChart.update();
  }

  /**
   * get the chart to fit to window
   * 
   */
  public void rescale()
  {
    _myChart.rescale();
  }

  public IAction getRedoAction()
  {
    return redoAction;
  }

  public IAction getUndoAction()
  {
    return undoAction;
  }

  public IUndoContext getUndoContext()
  {
    return undoContext;
  }

  public void reload(IFile file)
  {
    // not implemented
  }

}
