package org.mwc.cmap.overview.views;

import java.awt.*;
import java.awt.Font;
import java.awt.Point;
import java.beans.*;
import java.util.Enumeration;

import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.core.ui_support.swt.SWTCanvasAdapter;
import org.mwc.cmap.gt2plot.proj.GtProjection;
import org.mwc.cmap.overview.Activator;
import org.mwc.cmap.plotViewer.editors.chart.*;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.*;
import MWC.GUI.CanvasType.PaintListener;
import MWC.GUI.Tools.Action;
import MWC.GenericData.*;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class ChartOverview extends ViewPart implements PropertyChangeListener
{

	/**
	 * helper application to help track creation/activation of new plots
	 */
	private PartMonitor _myPartMonitor;

	protected Layers _targetLayers;

	OverviewSWTChart _myOverviewChart;

	IControllableViewport _targetViewport;

	private org.eclipse.jface.action.Action _fitToWindow;

	private GtProjection _myProjection;

	/**
	 * The constructor.
	 */
	public ChartOverview()
	{
		_myProjection = new GtProjection();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{

		// declare our context sensitive help
		CorePlugin.declareContextHelp(parent, "org.mwc.debrief.help.OverviewChart");

		// hey, first create the chart
		_myOverviewChart = new OverviewSWTChart(parent)
		{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			IControllableViewport getParentViewport()
			{
				return _targetViewport;
			}
		};

		// use our special dragger
		_myOverviewChart.setDragMode(new MyZoomMode());

		// and update the chart
		_myOverviewChart.setChartOverview(this);

		// and our special painter
		_myOverviewChart.getCanvas().addPainter(new PaintListener()
		{
			public WorldArea getDataArea()
			{
				return null;
			}

			public String getName()
			{
				return "Overview data area";
			}

			public void paintMe(CanvasType dest)
			{
				// ok - just paint in our rectangle
				paintDataRect(dest);
			}

			public void resizedEvent(PlainProjection theProj, Dimension newScreenArea)
			{
			}
		});

		makeActions();
		contributeToActionBars();

		// /////////////////////////////////////////
		// ok - listen out for changes in the view
		// /////////////////////////////////////////
		watchMyParts();
	}

	/**
	 * sort out what we're listening to...
	 */
	private void watchMyParts()
	{
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						Layers provider = (Layers) part;

						// is this different to our current one?
						if (provider != _targetLayers)
						{
							// ok, start listening to the new one
							_targetLayers = provider;
							plotSelected(provider, parentPart);
						}
					}
				});
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						if (part == _targetLayers)
						{
							_targetLayers = null;
							clearPlot();
						}
					}
				});
		_myPartMonitor.addPartListener(IControllableViewport.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						IControllableViewport provider = (IControllableViewport) part;

						// is this different to our current one?
						if (provider != _targetViewport)
						{
							// ok, stop listening to the current viewport (if we have one)
							if (_targetViewport != null)
								stopListeningToViewport();

							// and start listening to the new one
							_targetViewport = provider;

							startListeningToViewport();

						}
					}
				});
		_myPartMonitor.addPartListener(IControllableViewport.class,
				PartMonitor.CLOSED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						if (part == _targetViewport)
						{
							_targetViewport = null;
						}
					}
				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
				.getActivePage());
	}

	/**
	 * paint the data-rectangle in our overview, to show the currently visible
	 * area
	 * 
	 * @param dest
	 */
	protected void paintDataRect(CanvasType dest)
	{
		// check we're alive
		if (_targetViewport == null)
			return;

		// get the projection
		final PlainProjection proj = _targetViewport.getProjection();

		// get the dimensions
		final java.awt.Dimension scrArea = proj.getScreenArea();

		// did we find any data?
		if (scrArea == null)
			return;

		// now convert to data coordinates
		WorldLocation loc = proj.toWorld(new Point(0, 0));

		// did it work?
		if (loc == null)
			return;

		// produce the screen coordinate in the overview
		final Point thePt = _myOverviewChart.getCanvas().getProjection()
				.toScreen(loc);

		// did it work?
		if (thePt == null)
			return;

		// and the other corner
		loc = proj.toWorld(new Point(scrArea.width, scrArea.height));

		// create the screen coordinates
		final Point tl = new Point(thePt);
		final Point br = new Point(_myOverviewChart.getCanvas().getProjection()
				.toScreen(loc));

		//
		// // also, draw in the data-area
		// WorldArea dataRect = _currentViewport.getViewport();
		// // convert to my coords
		// java.awt.Point tl = new
		// java.awt.Point(dest.getProjection().toScreen(dataRect.getTopLeft()));
		// java.awt.Point br = new
		// java.awt.Point(dest.getProjection().toScreen(dataRect.getBottomRight()));

		dest.setColor(new java.awt.Color(200, 200, 200));
		dest.drawRect(tl.x, tl.y, br.x - tl.x, br.y - tl.y);

	}

	/**
	 * disable the plot we-re no longer looking at anything...
	 */
	protected void clearPlot()
	{
		// ok - we're no longer looking at anything. clear the plot..
	}

	/**
	 * ok, a new plot is selected - better show it then
	 * 
	 * @param provider
	 *          the new plot
	 * @param parentPart
	 *          the part containing the plot
	 */
	protected void plotSelected(Layers provider, IWorkbenchPart parentPart)
	{
		// ok - update our chart to show the indicated plot.
		_myOverviewChart.setLayers(provider);
		_myOverviewChart.rescale();
		_myOverviewChart.repaint();
		// this.setPartName(parentPart.getTitle());
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(_fitToWindow);

		// and the help link
		manager.add(new Separator());
		manager.add(CorePlugin.createOpenHelpAction(
				"org.mwc.debrief.help.OverviewChart", null, this));
	}

	private void makeActions()
	{
		_fitToWindow = new org.eclipse.jface.action.Action()
		{
			public void run()
			{
				// ok, fit the plot to the window...
				fitTargetToWindow();
			}
		};
		_fitToWindow.setText("Fit to window");
		_fitToWindow
				.setToolTipText("Zoom the selected plot out to show the full data");
		_fitToWindow.setImageDescriptor(Activator
				.getImageDescriptor("icons/fit_to_win.gif"));

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{

	}

	public class MyZoomMode extends SWTChart.PlotMouseDragger
	{
		org.eclipse.swt.graphics.Point _startPoint;

		SWTCanvas _myCanvas;

		public void doMouseDrag(final org.eclipse.swt.graphics.Point pt,
				final int JITTER, final Layers theLayers, SWTCanvas theCanvas)
		{
			// just do a check that we have our start point (it may have been cleared
			// at the end of the move operation)
			if (_startPoint != null)
			{
				int deltaX = _startPoint.x - pt.x;
				int deltaY = _startPoint.y - pt.y;
				if (Math.abs(deltaX) < JITTER && Math.abs(deltaY) < JITTER)
					return;
				Tracker _dragTracker = new Tracker((Composite) _myCanvas.getCanvas(),
						SWT.RESIZE);
				Rectangle rect = new Rectangle(_startPoint.x, _startPoint.y, deltaX,
						deltaY);
				_dragTracker.setRectangles(new Rectangle[]
				{ rect });
				boolean dragResult = _dragTracker.open();
				if (dragResult)
				{
					Rectangle[] rects = _dragTracker.getRectangles();
					Rectangle res = rects[0];
					// get world area
					java.awt.Point tl = new java.awt.Point(res.x, res.y);
					java.awt.Point br = new java.awt.Point(res.x + res.width, res.y
							+ res.height);
					WorldLocation locA = new WorldLocation(_myCanvas.getProjection()
							.toWorld(tl));
					WorldLocation locB = new WorldLocation(_myCanvas.getProjection()
							.toWorld(br));
					WorldArea area = new WorldArea(locA, locB);

					// hmm, check we have a controllable viewport
					if (_targetViewport != null)
					{

						try
						{

							// ok, we also need to get hold of the target chart
							WorldArea oldArea = _targetViewport.getViewport();
							Action theAction = new OverviewZoomInAction(_targetViewport,
									oldArea, area);

							// and wrap it
							DebriefActionWrapper daw = new DebriefActionWrapper(theAction,
									null, null);

							// and add it to the clipboard
							CorePlugin.run(daw);
						}
						catch (RuntimeException re)
						{
							re.printStackTrace();
						}
					}

					_dragTracker = null;
					_startPoint = null;
				}
			}
		}

		public void doMouseUp(org.eclipse.swt.graphics.Point point, int keyState)
		{
			_startPoint = null;
		}

		public void mouseDown(org.eclipse.swt.graphics.Point point,
				SWTCanvas canvas, PlainChart theChart)
		{
			_startPoint = point;
			_myCanvas = canvas;
		}

	}

	public class OverviewZoomInAction implements Action
	{

		private IControllableViewport _theViewport;

		private WorldArea _oldArea;

		private WorldArea _newArea;

		public OverviewZoomInAction(IControllableViewport theChart,
				WorldArea oldArea, WorldArea newArea)
		{
			_theViewport = theChart;
			_oldArea = oldArea;
			_newArea = newArea;
		}

		public boolean isRedoable()
		{
			return true;
		}

		public boolean isUndoable()
		{
			return true;
		}

		public String toString()
		{
			return "Zoom in operation";
		}

		public void undo()
		{
			// set the data area for the chart to the specified area
			_theViewport.setViewport(_oldArea);

			_theViewport.update();

			_myOverviewChart.update();
		}

		public void execute()
		{
			_theViewport.setViewport(_newArea);

			_theViewport.update();

			_myOverviewChart.update();
		}
	}

	public MWC.GUI.Rubberband getRubberband()
	{
		return null;
	}

	protected class OverviewSWTCanvas extends SWTCanvas
	{
		public OverviewSWTCanvas(Composite parent)
		{
			super(parent, _myProjection);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void drawText(Font theFont, String theStr, int x, int y)
		{
			// ignore - we don't do text in overview
		}

		public void drawText(String theStr, int x, int y)
		{
			// ignore - we don't do text in overview
		}

	}

	protected static class OverviewSWTCanvasAdapter extends SWTCanvasAdapter
	{

		public OverviewSWTCanvasAdapter(PlainProjection proj)
		{
			super(proj);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void drawText(Font theFont, String theStr, int x, int y)
		{
			// ignore - we don't do text in overview
		}

		public void drawText(String theStr, int x, int y)
		{
			// ignore - we don't do text in overview
		}
	}

	abstract public class OverviewSWTChart extends SWTChart
	{

		ChartOverview _parentView;

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		abstract IControllableViewport getParentViewport();

		public OverviewSWTChart(Composite parent)
		{
			super(null, parent, _myProjection);

			// ok, setup double-click handler to zoom in on target location
			this.addCursorDblClickedListener(new ChartDoubleClickListener()
			{
				public void cursorDblClicked(PlainChart theChart,
						WorldLocation theLocation, Point thePoint)
				{
					// ok - got location centre plot on target loc
					WorldArea currentArea = new WorldArea(getParentViewport()
							.getViewport());

					currentArea.setCentre(theLocation);

					getParentViewport().setViewport(currentArea);

					// and trigger an update
					getParentViewport().update();
				}
			});
		}

		public void setChartOverview(ChartOverview view)
		{
			_parentView = view;
		}

		public void chartFireSelectionChanged(ISelection sel)
		{
		}

		/**
		 * over-ride the parent's version of paint, so that we can try to do it by
		 * layers.
		 */
		public final void paintMe(final CanvasType dest)
		{

			// just double-check we have some layers (if we're part of an overview
			// chart, we may not have...)
			if (_theLayers == null)
				return;

			// check that we have a valid canvas (that the sizes are set)
			final java.awt.Dimension sArea = dest.getProjection().getScreenArea();
			if (sArea != null)
			{
				if (sArea.width > 0)
				{

					// hey, we've plotted at least once, has the data area changed?
					if (_lastDataArea != _parentView._myOverviewChart.getCanvas()
							.getProjection().getDataArea())
					{
						// remember the data area for next time
						_lastDataArea = _parentView._myOverviewChart.getCanvas()
								.getProjection().getDataArea();

						// clear out all of the layers we are using
						_myLayers.clear();
					}

					int canvasHeight = _parentView._myOverviewChart.getCanvas().getSize()
							.getSize().height;
					int canvasWidth = _parentView._myOverviewChart.getCanvas().getSize().width;

					paintBackground(dest);

					// ok, pass through the layers, repainting any which need it
					Enumeration<Layer> numer = _theLayers.sortedElements();
					while (numer.hasMoreElements())
					{
						final Layer thisLayer = (Layer) numer.nextElement();

						boolean isAlreadyPlotted = false;

						// hmm, do we want to paint this layer?
						if (_parentView.doWePaintThisLayer(thisLayer))
						{

							// just check if this layer is visible
							if (thisLayer.getVisible())
							{
								// System.out.println("painting:" + thisLayer.getName());

								if (doubleBufferPlot())
								{
									// check we're plotting to a SwingCanvas, because we don't
									// double-buffer anything else
									if (dest instanceof SWTCanvas)
									{
										// does this layer want to be double-buffered?
										if (thisLayer instanceof BaseLayer)
										{
											// just check if there is a property which over-rides the
											// double-buffering
											final BaseLayer bl = (BaseLayer) thisLayer;
											if (bl.isBuffered())
											{
												isAlreadyPlotted = true;

												// do our double-buffering bit
												// do we have a layer for this object
												org.eclipse.swt.graphics.Image image = (org.eclipse.swt.graphics.Image) _myLayers
														.get(thisLayer);
												if (image == null)
												{
													// ok - create our image
													if (_myImageTemplate == null)
													{

														Image tmpTemplate = new Image(Display.getCurrent(),
																canvasWidth, canvasHeight);
														_myImageTemplate = tmpTemplate.getImageData();

														tmpTemplate.dispose();
														tmpTemplate = null;
													}
													image = createSWTImage(_myImageTemplate);

													GC newGC = new GC(image);

													// wrap the GC into something we know how to plot to.
													SWTCanvasAdapter ca = new OverviewSWTCanvasAdapter(
															dest.getProjection());

													ca.setScreenSize(dest.getProjection().getScreenArea());

													// and store the GC
													ca.startDraw(newGC);

													// ok, paint the layer into this canvas
													thisLayer.paint(ca);

													// done.
													ca.endDraw(null);

													// store this image in our list, indexed by the layer
													// object itself
													_myLayers.put(thisLayer, image);

													// and ditch the GC
													newGC.dispose();
												}

												// have we ended up with an image to paint?
												if (image != null)
												{
													// get the graphics to paint to
													SWTCanvas canv = (SWTCanvas) dest;

													// lastly add this image to our Graphics object
													canv.drawSWTImage(image, 0, 0, canvasWidth,
															canvasHeight, 255);

													// but, we also have to ditch the image
													// image.dispose();
												}

											}
										}
									} // whether we were plotting to a SwingCanvas (which may be
									// double-buffered
								} // whther we are happy to do double-buffering

								// did we manage to paint it
								if (!isAlreadyPlotted)
								{
									paintThisLayer(thisLayer, dest);

									isAlreadyPlotted = true;
								}
							}
						}
					}

				}
			}

		}

		/**
		 * over-rideable member function which allows us to over-ride the canvas
		 * which gets used.
		 * 
		 * @return the Canvas to use
		 */
		public final SWTCanvas createCanvas(Composite parent)
		{
			return new OverviewSWTCanvas(parent);
		}

	}

	/**
	 * do a fit-to-window of the target viewport
	 */
	protected void fitTargetToWindow()
	{
		_targetViewport.rescale();
		_targetViewport.update();

		// now, redraw our rectable
		_myOverviewChart.repaint();
	}

	/**
	 * decide whether to paint this layer...
	 * 
	 * @param thisLayer
	 *          the layer we're looking at
	 * @return
	 */
	public boolean doWePaintThisLayer(Layer thisLayer)
	{
		boolean res = true;

		// no, don't check for ETOPO data - just paint the lot.
		// if (thisLayer instanceof SpatialRasterPainter)
		// res = false;

		return res;
	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		// ok, we've had a range change. better update
		_myOverviewChart.repaint();
	}

	/**
	 * 
	 */
	void stopListeningToViewport()
	{
		_targetViewport.getProjection().removeListener(this);
	}

	/**
	 * 
	 */
	void startListeningToViewport()
	{
		_targetViewport.getProjection().addListener(this);
	}
}
