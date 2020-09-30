/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

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
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.SubActionBars2;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.ide.FileStoreEditorInput;
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
import org.mwc.cmap.geotools.gt2plot.GeoToolsLayer;
import org.mwc.cmap.geotools.gt2plot.GtProjection;
import org.mwc.cmap.geotools.gt2plot.ShapeFileLayer;
import org.mwc.cmap.geotools.gt2plot.WorldImageLayer;
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

public abstract class CorePlotEditor extends EditorPart implements IResourceProvider, IControllableViewport,
		ISelectionProvider, IPlotGUI, IChartBasedEditor, IUndoable, CanvasType.ScreenUpdateProvider, INameablePart {

	private static final String CONTEXT_ID = "org.mwc.cmap.plotEditorContext";

	/**
	 * the chart we store/manager
	 */
	protected SWTChart _myChart = null;

	/**
	 * we may learn the background color of the canvas before it has loaded.
	 * temporarily store the color here, and set the background color when we load
	 * the canvas
	 */
	private Color _pendingCanvasBackgroundColor;

	/**
	 * the graphic data we know about
	 */
	protected Layers _myLayers;

	/**
	 * the object which listens to time-change events. we remember it so that it can
	 * be deleted when we close
	 */
	protected PropertyChangeListener _timeListener;

	/**
	 * store a pending projection. we do this because sometimes we may learn about
	 * the projection before we create our child components, you see.
	 */
	protected PlainProjection _pendingProjection;

	protected DropTarget target;

	private Vector<ISelectionChangedListener> _selectionListeners;

	private ISelection _currentSelection;

	/**
	 * keep track of whether the current plot is dirty...
	 */
	protected boolean _plotIsDirty = false;

	protected DataListener2 _listenForMods;

	private boolean _ignoreDirtyCalls;

	protected PartMonitor _myPartMonitor;

	protected GeoToolsHandler _myGeoHandler;
	protected IContextActivation _myActivation;

	protected IResourceChangeListener resourceChangeListener = new IResourceChangeListener() {

		@Override
		public void resourceChanged(final IResourceChangeEvent event) {
			final IResourceDelta delta = event.getDelta();
			final int eventType = event.getType();
			if (delta != null) {
				try {
					delta.accept(new IResourceDeltaVisitor() {

						@Override
						public boolean visit(final IResourceDelta delta) throws CoreException {
							final IResource resource = delta.getResource();
							if (resource instanceof IWorkspaceRoot) {
								return true;
							}
							if (resource instanceof IProject) {
								final IEditorInput input = getEditorInput();
								if (input instanceof IFileEditorInput) {
									final IProject project = ((IFileEditorInput) input).getFile().getProject();
									if (resource.equals(project) && (eventType == IResourceChangeEvent.PRE_DELETE
											|| eventType == IResourceChangeEvent.PRE_CLOSE)) {
										closeEditor(false);
										return false;
									}
								}
								return true;
							}
							if (resource instanceof IFolder) {
								return true;
							}
							if (resource instanceof IFile) {
								final IEditorInput input = getEditorInput();
								if (input instanceof IFileEditorInput) {
									final IFile file = ((IFileEditorInput) input).getFile();
									if (resource.equals(file) && delta.getKind() == IResourceDelta.REMOVED) {
										final IPath movedToPath = delta.getMovedToPath();
										if (movedToPath != null) {
											final IResource path = ResourcesPlugin.getWorkspace().getRoot()
													.findMember(movedToPath);
											if (path instanceof IFile) {
												final FileEditorInput newInput = new FileEditorInput((IFile) path);
												Display.getDefault().asyncExec(new Runnable() {

													@Override
													public void run() {
														setInputWithNotify(newInput);
													}
												});
											}
										} else {
											closeEditor(false);
										}
									}
									if (resource.equals(file) && (delta.getKind() == IResourceDelta.CHANGED
											&& (delta.getFlags() & IResourceDelta.CONTENT) != 0)) {
										reload(file);
									}
								}
							}
							return false;
						}

					});
				} catch (final CoreException e) {
					final IStatus status = new Status(IStatus.INFO, PlotViewerPlugin.PLUGIN_ID, e.getLocalizedMessage(),
							e);
					PlotViewerPlugin.getDefault().getLog().log(status);
				}
			}
		}
	};

	private final IPartListener partListener = new IPartListener() {

		private void activateContext(final IWorkbenchPart part) {
			if (part == CorePlotEditor.this && (_myActivation == null)) {
				_myActivation = getContextService().activateContext(CONTEXT_ID);
			}
		}

		private void deactivateContext(final IWorkbenchPart part) {
			if (part == CorePlotEditor.this && _myActivation != null) {
				getContextService().deactivateContext(_myActivation);
				_myActivation = null;
			}
		}

		@Override
		public void partActivated(final IWorkbenchPart part) {
			activateContext(part);
		}

		@Override
		public void partBroughtToTop(final IWorkbenchPart part) {
			activateContext(part);
		}

		@Override
		public void partClosed(final IWorkbenchPart part) {
			deactivateContext(part);
		}

		@Override
		public void partDeactivated(final IWorkbenchPart part) {
			deactivateContext(part);
		}

		@Override
		public void partOpened(final IWorkbenchPart part) {
			activateContext(part);
		}
	};

	private ObjectUndoContext undoContext;

	private UndoActionHandler undoAction;

	private RedoActionHandler redoAction;

	public CorePlotEditor() {
		super();

		// create the projection, we're going to need it to load the data, before we
		// have the chart created
		_myGeoHandler = new GtProjection();

		_myLayers = new Layers() {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void addThisLayer(final Layer theLayer) {
				final Layer wrappedLayer;

				// ok, if this is an externally managed layer (and we're doing
				// GT-plotting, we will wrap it, and actually add the wrapped layer
				if (theLayer instanceof ExternallyManagedDataLayer) {
					final ExternallyManagedDataLayer dl = (ExternallyManagedDataLayer) theLayer;
					if (dl.getDataType().equals(MWC.GUI.Shapes.ChartBoundsWrapper.WORLDIMAGE_TYPE)) {
						final String filePath = getFixedFilePath(dl.getFilename());
						if(filePath!=null) {
							final GeoToolsLayer gt = new WorldImageLayer(dl.getName(), filePath);
							gt.setVisible(dl.getVisible());
							_myGeoHandler.addGeoToolsLayer(gt);
							wrappedLayer = gt;
						} else {
							wrappedLayer = null;
						}
					} else if (dl.getDataType().equals(MWC.GUI.Shapes.ChartBoundsWrapper.SHAPEFILE_TYPE)) {
						// just see if it's a raster extent layer (special processing)
						if (dl.getName().equals(WorldImageLayer.RASTER_FILE)) {
							// special processing - wrap it.
							wrappedLayer = WorldImageLayer.RasterExtentHelper.loadRasters(dl.getFilename(), this);
						} else {
							// ok, it's a normal shapefile: load it.
							final GeoToolsLayer gt = new ShapeFileLayer(dl.getName(), dl.getFilename());
							gt.setVisible(dl.getVisible());
							_myGeoHandler.addGeoToolsLayer(gt);
							wrappedLayer = gt;
						}
					} else if (ChartBoundsWrapper.NELAYER_TYPE.equals(dl.getDataType())) {
						final NELayer gt = new NELayer(Activator.getDefault().getDefaultStyleSet(), dl.getName());
						gt.setVisible(dl.getVisible());
						_myGeoHandler.addGeoToolsLayer(gt);
						wrappedLayer = gt;
					} else {
						wrappedLayer = null;
					}
					if (wrappedLayer != null)
						super.addThisLayer(wrappedLayer);
				} else {
					super.addThisLayer(theLayer);
				}

				layerAdded(theLayer);
			}

			@Override
			public void removeThisLayer(final Layer theLayer) {
				if (theLayer instanceof GeoToolsLayer) {
					// get the content
					final GtProjection gp = (GtProjection) _myChart.getCanvas().getProjection();
					final GeoToolsLayer gt = (GeoToolsLayer) theLayer;
					gt.clearMap();

					if (gp.numLayers() == 0) {
						// ok - we've got to force the data rea
						final WorldArea area = _myChart.getCanvas().getProjection().getDataArea();
						_myChart.getCanvas().getProjection().setDataArea(area);
					}

				}

				// and remove from the actual list
				super.removeThisLayer(theLayer);

			}

		};

		_listenForMods = new DataListener2() {

			@Override
			public void dataExtended(final Layers theData) {
				layersExtended();
				fireDirty();
			}

			@Override
			public void dataExtended(final Layers theData, final Plottable newItem, final HasEditables parent) {
				layersExtended();
				fireDirty();
			}

			@Override
			public void dataModified(final Layers theData, final Layer changedLayer) {
				fireDirty();
			}

			@Override
			public void dataReformatted(final Layers theData, final Layer changedLayer) {
				fireDirty();
			}

		};
		_myLayers.addDataExtendedListener(_listenForMods);
		_myLayers.addDataModifiedListener(_listenForMods);
		_myLayers.addDataReformattedListener(_listenForMods);

		// and listen for new times
		_timeListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent arg0) {

				// right, retrieve the time
				final HiResDate newDTG = (HiResDate) arg0.getNewValue();
				timeChanged(newDTG);

				// now make a note that the current DTG has changed
				fireDirty();
			}
		};
	}

	/**
	 * let someone listen to screen updates
	 *
	 * @param listener
	 */
	@Override
	public void addScreenUpdateListener(final SWTCanvas.ScreenUpdateListener listener) {
		if (getChart() != null) {
			getChart().getSWTCanvas().addScreenUpdateListener(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
		if (_selectionListeners == null)
			_selectionListeners = new Vector<ISelectionChangedListener>(0, 1);

		// see if we don't already contain it..
		if (!_selectionListeners.contains(listener))
			_selectionListeners.add(listener);
	}

	protected void closeEditor(final boolean save) {
		final IWorkbenchPage page = getSite().getPage();
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				page.closeEditor(CorePlotEditor.this, save);
			}
		});
	}

	/**
	 * sort out the file-drop target
	 */
	private void configureFileDropSupport() {
		final int dropOperation = DND.DROP_COPY;
		final Transfer[] dropTypes = { FileTransfer.getInstance() };

		target = new DropTarget(_myChart.getCanvasControl(), dropOperation);
		target.setTransfer(dropTypes);
		target.addDropListener(new DropTargetListener() {
			@Override
			public void dragEnter(final DropTargetEvent event) {
				if (FileTransfer.getInstance().isSupportedType(event.currentDataType)
						&& event.detail != DND.DROP_COPY) {
					event.detail = DND.DROP_COPY;
				}
			}

			@Override
			public void dragLeave(final DropTargetEvent event) {
			}

			@Override
			public void dragOperationChanged(final DropTargetEvent event) {
			}

			@Override
			public void dragOver(final DropTargetEvent event) {
			}

			@Override
			public void drop(final DropTargetEvent event) {
				if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
					final String[] fileNames = (String[]) event.data;
					if (fileNames != null) {
						filesDropped(fileNames);
					}
				}
			}

			@Override
			public void dropAccept(final DropTargetEvent event) {
			}

		});

	}

	@Override
	public void createPartControl(final Composite parent) {
		// hey, create the chart
		_myChart = createTheChart(parent);

		// set the chart color, if we have one
		if (_pendingCanvasBackgroundColor != null) {
			_myChart.getCanvas().setBackgroundColor(_pendingCanvasBackgroundColor);
			// and promptly forget it
			_pendingCanvasBackgroundColor = null;
		}

		// and update the projection, if we have one
		if (_pendingProjection != null) {
			_myChart.getCanvas().setProjection(_pendingProjection);
			_pendingProjection = null;
		}

		// and the drop support
		configureFileDropSupport();

		// and add our dbl click listener
		// and add our dbl click listener
		getChart().addCursorDblClickedListener(new DblClickEdit(null) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void addEditor(final Plottable res, final EditorType e, final Layer parentLayer) {
				selectPlottable(res, parentLayer);
			}

			@Override
			protected void handleItemNotFound(final PlainProjection projection) {
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

		getEditorSite().getActionBars().setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction);
		getEditorSite().getActionBars().setGlobalActionHandler(ActionFactory.REDO.getId(), redoAction);

		// put in the plot-copy support

		final IAction _copyClipboardAction = new Action() {
			@Override
			public void runWithEvent(final Event event) {
				final SWTCanvas canvas = (SWTCanvas) getChart().getCanvas();
				Image image = null;
				try {
					image = canvas.getImage();
					if (image != null) {
						final BufferedImage _awtImage = PlotViewerPlugin.convertToAWT(image.getImageData());
						final Transferable t = new Transferable() {

							@Override
							public Object getTransferData(final DataFlavor flavor)
									throws UnsupportedFlavorException, IOException {
								if (isDataFlavorSupported(flavor)) {
									return _awtImage;
								}
								return null;
							}

							@Override
							public DataFlavor[] getTransferDataFlavors() {
								return new DataFlavor[] { DataFlavor.imageFlavor };
							}

							@Override
							public boolean isDataFlavorSupported(final DataFlavor flavor) {
								if (flavor == DataFlavor.imageFlavor)
									return true;
								return false;
							}

						};

						final ClipboardOwner co = new ClipboardOwner() {

							@Override
							public void lostOwnership(final Clipboard clipboard, final Transferable contents) {
							}

						};
						final Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
						cb.setContents(t, co);
					}
				} finally {
					if (image != null) {
						image.dispose();
					}
				}
			}
		};

		final IActionBars actionBars = getEditorSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), _copyClipboardAction);

		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow().getPartService());

		// listen out for us gaining focus - so we can set the cursort tracker
		listenForMeGainingLosingFocus();

		listenForSelectionChange();

		getSite().getPage().addPartListener(partListener);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener,
				IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_CHANGE);
	}

	/**
	 * create the chart we're after
	 *
	 * @param parent the parent object to stick it into
	 */
	protected SWTChart createTheChart(final Composite parent) {
		final SWTChart res = new SWTChart(_myLayers, parent, (PlainProjection) _myGeoHandler) {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void chartFireSelectionChanged(final ISelection sel) {
				fireSelectionChanged(sel);
			}
		};
		return res;
	}

	@Override
	public void dispose() {
		// ok, tell the chart to self-destruct (And dispose/release of any objects)
		_myChart.close();
		_myChart = null;
		undoAction.dispose();
		redoAction.dispose();
		OperationHistoryFactory.getOperationHistory().dispose(undoContext, true, true, true);

		super.dispose();

		getSite().getPage().removePartListener(partListener);
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceChangeListener);

		// empty the part monitor
		if (_myPartMonitor != null) {
			_myPartMonitor.ditch();
			_myPartMonitor = null;
		}

		// and the layers
		_myLayers.close();

		if (_myGeoHandler != null) {
			_myGeoHandler.dispose();
			_myGeoHandler = null;
		}

		// some other items
		_timeListener = null;
	}

	/**
	 * process the files dropped onto this panel
	 *
	 * @param fileNames list of filenames
	 */
	protected void filesDropped(final String[] fileNames) {
		System.out.println("Files dropped");
	}

	/**
	 * make a note that the data is now dirty, and needs saving.
	 */
	public void fireDirty() {
		if (!_ignoreDirtyCalls) {
			_plotIsDirty = true;

			// fire the modified event, in the display thread
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					firePropertyChange(PROP_DIRTY);
				}
			});
		}
	}

	public void fireSelectionChanged(final ISelection sel) {
		// just double-check that we're not already processing this
		if (!sel.equals(_currentSelection)) {
			_currentSelection = sel;
			if (_selectionListeners != null) {
				final SelectionChangedEvent sEvent = new SelectionChangedEvent(this, sel);
				for (final Iterator<ISelectionChangedListener> stepper = _selectionListeners.iterator(); stepper
						.hasNext();) {
					final ISelectionChangedListener thisL = stepper.next();
					if (thisL != null) {
						thisL.selectionChanged(sEvent);
					}
				}
			}
		}
	}

	/**
	 * Returns the ActionbarContributor for the Editor. ISelectionChangedListener
	 *
	 * @return the ActionbarContributor for the Editor.
	 */
	public SubActionBars2 getActionbar() {
		return (SubActionBars2) getEditorSite().getActionBars();
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getAdapter(final Class adapter) {
		final Object res;

		// so, is he looking for the layers?
		if (adapter == CorePlotEditor.class) {
			res = this;
		} else if (adapter == ISelectionProvider.class) {
			res = this;
		} else if (adapter == IControllableViewport.class) {
			res = this;
		} else if (adapter == CanvasType.class) {
			res = _myChart.getCanvas();
		} else if (adapter == CanvasType.ScreenUpdateProvider.class) {
			res = this;
		} else if (IEditorPart.class.equals(adapter)) {
			res = this;
		} else if (adapter == INameablePart.class) {
			res = this;
		} else {
			res = null;
		}

		return res;
	}

	/**
	 * @return
	 */
	@Override
	public Color getBackgroundColor() {
		return _myChart.getCanvas().getBackgroundColor();
	}

	@Override
	public SWTChart getChart() {
		return _myChart;
	}

	private IContextService getContextService() {
		return getSite().getService(IContextService.class);
	}

	protected String getFixedFilePath(final String fileName) {
		final String tiffFilePath;
		final File tifFile = new File(fileName);
		PlotViewerPlugin.getDefault().getLog().log(new Status(IStatus.INFO,PlotViewerPlugin.PLUGIN_ID,"Trying to load file-"+tifFile.getAbsolutePath()));
		if (tifFile.exists()) {
			// this is a valid absolute path, so load this file
			tiffFilePath = tifFile.getAbsolutePath();
			
		} else {
			System.out.println("File does not exist");
			// check the file open in editor and get its file system location.
			final IEditorInput input = getEditorInput();
			if (input instanceof IFileEditorInput) {
				final String dpfFilePath = ((IFileEditorInput) getEditorInput()).getFile().getParent().getLocation()
						.toFile().getAbsolutePath();
				
				tiffFilePath = dpfFilePath + File.separator + getFileNameFromAbsoluteFile(tifFile.getAbsolutePath());
			} else if (input instanceof FileStoreEditorInput) {
				// if the file is dragged from outside workspace, get the location of the plot
				// file
				String tmpFilePath;
				
				try {
					final File localFile = new File(((FileStoreEditorInput) input).getURI().toURL().getFile());
					tmpFilePath = localFile.getParentFile().getAbsolutePath() + File.separator + getFileNameFromAbsoluteFile(tifFile.getAbsolutePath());
				} catch (final MalformedURLException e) {
					MWC.Utilities.Errors.Trace.trace(e, fileName + "File doesnt exist and couldnt be loaded");
					tmpFilePath = null;
				}
				tiffFilePath = tmpFilePath;
				
			} else {
				tiffFilePath = null;
			}
		}
		PlotViewerPlugin.getDefault().getLog().log(new Status(IStatus.INFO,PlotViewerPlugin.PLUGIN_ID,"finally, trying to load file - "+tiffFilePath));
		final File tiffFile = new File(tiffFilePath);
		if (!tiffFile.exists()) {
			CorePlugin.showMessage("Error loading file",
					"Could not find the GeoTiff File:"+fileName+"\n Please fix the path in the file and load again");
			return fileName;
		}
		PlotViewerPlugin.getDefault().getLog().log(new Status(IStatus.INFO,PlotViewerPlugin.PLUGIN_ID,"found file to load "+tiffFilePath));
		return tiffFilePath;
	}
	
	private String getFileNameFromAbsoluteFile(String fileName) {
		final String retVal;
		if(fileName.contains(File.separator)) {
			int lastIndex = fileName.lastIndexOf(File.separator);
			retVal =fileName.substring(lastIndex+1); 
		}
		else {
			retVal = fileName;
		}
		return retVal;
	}

	@Override
	public PlainProjection getProjection() {
		return getChart().getCanvas().getProjection();
	}

	public IAction getRedoAction() {
		return redoAction;
	}

	/**
	 * return the file representing where this plot is stored
	 *
	 * @return the file location
	 */
	@Override
	public IResource getResource() {
		// have we been saved yet?
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		return _currentSelection;
	}

	public IAction getUndoAction() {
		return undoAction;
	}

	@Override
	public IUndoContext getUndoContext() {
		return undoContext;
	}

	@Override
	public WorldArea getViewport() {
		return getChart().getCanvas().getProjection().getDataArea();
	}

	/**
	 * hmm, are we dirty?
	 *
	 * @return
	 */
	@Override
	public boolean isDirty() {
		return _plotIsDirty;
	}

	/**
	 * utility function for tracking new layers getting added
	 *
	 * @param layer the new layer
	 */
	protected void layerAdded(final Layer layer) {

	}

	/**
	 * new data has been added - have a look at the times
	 */
	protected void layersExtended() {

	}

	private void listenForMeGainingLosingFocus() {
		final EditorPart linkToMe = this;
		_myPartMonitor.addPartListener(CorePlotEditor.class, PartMonitor.DEACTIVATED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object instance, final IWorkbenchPart parentPart) {
				if (linkToMe.equals(instance))
					_currentSelection = null;
			}
		});
		_myPartMonitor.addPartListener(CorePlotEditor.class, PartMonitor.ACTIVATED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object instance, final IWorkbenchPart parentPart) {
				if (linkToMe.equals(instance)) {
					// tell the cursor track that we're it's bitch.
					RangeTracker.displayResultsIn(linkToMe);
					CursorTracker.trackThisChart(_myChart, linkToMe);
				}
			}
		});
	}

	@SuppressWarnings("unused")
	private void listenForSelectionChange() {
		_myPartMonitor.addPartListener(ISelectionProvider.class, PartMonitor.ACTIVATED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart) {
				final ISelectionProvider iS = (ISelectionProvider) part;
				// TODO- make it possible for use to indicate if highlights get
				// plotted (prob via Layer Manager)
				// iS.addSelectionChangedListener(_selectionChangeListener);
			}
		});
		_myPartMonitor.addPartListener(ISelectionProvider.class, PartMonitor.DEACTIVATED, new PartMonitor.ICallback() {
			@Override
			public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart) {
				final ISelectionProvider iS = (ISelectionProvider) part;
				// TODO- make it possible for use to indicate if highlights get
				// plotted (prob via Layer Manager)
				// iS.removeSelectionChangedListener(_selectionChangeListener);
			}
		});
	}

	/**
	 * place the chart in the properties window
	 *
	 */
	private final void putBackdropIntoProperties() {
		final SWTCanvas can = (SWTCanvas) getChart().getCanvas();
		final EditableWrapper wrapped = new EditableWrapper(can, getChart().getLayers());
		final ISelection sel = new StructuredSelection(wrapped);
		fireSelectionChanged(sel);
	}

	public void reload(final IFile file) {
		// not implemented
	}

	/**
	 * let someone listen to screen updates
	 *
	 * @param listener
	 */
	@Override
	public void removeScreenUpdateListener(final SWTCanvas.ScreenUpdateListener listener) {
		if (getChart() != null) {
			getChart().getSWTCanvas().removeScreenUpdateListener(listener);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
		_selectionListeners.remove(listener);
	}

	/**
	 * get the chart to fit to window
	 *
	 */
	@Override
	public void rescale() {
		_myChart.rescale();
	}

	/**
	 * ok - let somebody else select an item on the plot. The initial reason for
	 * making this public was so that when a new item is created, we can select it
	 * on the plot. The plot then fires a 'selected' event, and the new item is
	 * shown in the properties window. Cool.
	 *
	 * @param target1     - the item to select
	 * @param parentLayer - the item's parent layer. Used to decide which layers to
	 *                    update.
	 */
	@Override
	public void selectPlottable(final Plottable target1, final Layer parentLayer) {
		CorePlugin.logError(IStatus.INFO, "Double-click processed, opening property editor for:" + target1, null);
		final EditableWrapper parentP = new EditableWrapper(parentLayer, null, getChart().getLayers());
		final EditableWrapper wrapped = new EditableWrapper(target1, parentP, getChart().getLayers());
		final ISelection selected = new StructuredSelection(wrapped);
		fireSelectionChanged(selected);
	}

	/**
	 * @param theColor
	 */
	@Override
	public void setBackgroundColor(final Color theColor) {
		if (_myChart == null)
			_pendingCanvasBackgroundColor = theColor;
		else
			_myChart.getCanvas().setBackgroundColor(theColor);
	}

	@Override
	public void setFocus() {
		// just put some kind of blank object into the properties window
		// putBackdropIntoProperties();

		// ok, set the drag mode to whatever our common "mode" is.
		// - start off by getting the current mode
		final PlotMouseDragger curMode = PlotViewerPlugin.getCurrentMode();

		// has one been set?
		if (curMode != null) {
			// yup, better observe it then
			_myChart.setDragMode(curMode);
		}
		_myChart.getCanvasControl().forceFocus();

	}

	@Override
	public void setName(final String name) {
		final Runnable runner = new Runnable() {
			@Override
			public void run() {
				setPartName(name);
			}
		};

		// are we in the display thread?
		if (Display.getCurrent() == null) {
			// no - do update in correct thread
			Display.getDefault().asyncExec(runner);
		} else {
			runner.run();
		}
	}

	@Override
	public void setProjection(final PlainProjection proj) {
		// do we have a chart yet?
		if (_myChart == null) {
			// nope, better remember it
			_pendingProjection = proj;
		} else {
			// yes, just update it.
			_myChart.getCanvas().setProjection(proj);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface
	 * .viewers.ISelection)
	 */
	@Override
	public void setSelection(final ISelection selection) {
		_currentSelection = selection;
	}

	@Override
	public void setViewport(final WorldArea target) {
		getChart().getCanvas().getProjection().setDataArea(target);
	}

	/**
	 * start ignoring dirty calls, since we're loading the initial data (for
	 * instance)
	 */
	public void startIgnoringDirtyCalls() {
		_ignoreDirtyCalls = true;
	}

	/**
	 * start ignoring dirty calls, since we're loading the initial data (for
	 * instance)
	 */
	public void stopIgnoringDirtyCalls() {
		_ignoreDirtyCalls = false;
	}

	protected void timeChanged(final HiResDate newDTG) {
	}

	@Override
	public void update() {
		_myChart.update();
	}

}
