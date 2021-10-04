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

package org.mwc.cmap.grideditor.chart;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.EventListener;
import java.util.ResourceBundle;

import javax.swing.event.EventListenerList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.plot.Zoomable;
import org.jfree.chart.swt.ChartComposite;
import org.jfree.chart.swt.editor.SWTChartEditor;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.swt.SWTGraphics2D;
import org.jfree.swt.SWTUtils;

/**
 * This is a local copy of the {@link FixedChartComposite} tweaked to allow
 * dragging the data points on plot in subclasses. Except the note about fixed
 * bugs below, we haven't changed anything in the {@link FixedChartComposite},
 * just added a few accessors to the class internals.
 *
 * In particular, we allow subclass to decide whether the given mouse down event
 * should initiate the zoom operation or not, by using forgetZoomPoint() which
 * have to access zoomPoint/zoonRectangle fields which are private in the
 * {@link ChartComposite}.
 *
 * All the dragging code is to be implemented in subclasses.
 * <p>
 * We have fixed broken search for the chart entities for given mouse event.
 *
 * @see FixedChartComposite
 */
public class FixedChartComposite extends Composite implements ChartChangeListener, ChartProgressListener, PaintListener,
		SelectionListener, MouseListener, MouseMoveListener, Printable {

	/** Default setting for buffer usage. */
	public static final boolean DEFAULT_BUFFER_USED = false;

	/** The default panel width. */
	public static final int DEFAULT_WIDTH = 680;

	/** The default panel height. */
	public static final int DEFAULT_HEIGHT = 420;

	/** The default limit below which chart scaling kicks in. */
	public static final int DEFAULT_MINIMUM_DRAW_WIDTH = 300;

	/** The default limit below which chart scaling kicks in. */
	public static final int DEFAULT_MINIMUM_DRAW_HEIGHT = 200;

	/** The default limit below which chart scaling kicks in. */
	public static final int DEFAULT_MAXIMUM_DRAW_WIDTH = 800;

	/** The default limit below which chart scaling kicks in. */
	public static final int DEFAULT_MAXIMUM_DRAW_HEIGHT = 600;

	/** The minimum size required to perform a zoom on a rectangle */
	public static final int DEFAULT_ZOOM_TRIGGER_DISTANCE = 10;

	/** Properties action command. */
	public static final String PROPERTIES_COMMAND = "PROPERTIES";

	/** Save action command. */
	public static final String SAVE_COMMAND = "SAVE";

	/** Print action command. */
	public static final String PRINT_COMMAND = "PRINT";

	/** Zoom in (both axes) action command. */
	public static final String ZOOM_IN_BOTH_COMMAND = "ZOOM_IN_BOTH";

	/** Zoom in (domain axis only) action command. */
	public static final String ZOOM_IN_DOMAIN_COMMAND = "ZOOM_IN_DOMAIN";

	/** Zoom in (range axis only) action command. */
	public static final String ZOOM_IN_RANGE_COMMAND = "ZOOM_IN_RANGE";

	/** Zoom out (both axes) action command. */
	public static final String ZOOM_OUT_BOTH_COMMAND = "ZOOM_OUT_BOTH";

	/** Zoom out (domain axis only) action command. */
	public static final String ZOOM_OUT_DOMAIN_COMMAND = "ZOOM_DOMAIN_BOTH";

	/** Zoom out (range axis only) action command. */
	public static final String ZOOM_OUT_RANGE_COMMAND = "ZOOM_RANGE_BOTH";

	/** Zoom reset (both axes) action command. */
	public static final String ZOOM_RESET_BOTH_COMMAND = "ZOOM_RESET_BOTH";

	/** Zoom reset (domain axis only) action command. */
	public static final String ZOOM_RESET_DOMAIN_COMMAND = "ZOOM_RESET_DOMAIN";

	/** Zoom reset (range axis only) action command. */
	public static final String ZOOM_RESET_RANGE_COMMAND = "ZOOM_RESET_RANGE";

	/** The resourceBundle for the localization. */
	protected static ResourceBundle localizationResources = ResourceBundleWrapper
			.getBundle("org.jfree.chart.LocalizationBundle");

	/** The chart that is displayed in the panel. */
	private JFreeChart chart;

	/** The canvas to display the chart. */
	private final Canvas canvas;

	/** Storage for registered (chart) mouse listeners. */
	private final EventListenerList chartMouseListeners;

	/** A flag that controls whether or not the off-screen buffer is used. */
	private final boolean useBuffer;

	/** A flag that indicates that the buffer should be refreshed. */
	private boolean refreshBuffer;

	/** A flag that indicates that the tooltips should be displayed. */
	private boolean displayToolTips;

	/** A buffer for the rendered chart. */
	private org.eclipse.swt.graphics.Image chartBuffer;

	/** The height of the chart buffer. */
	private int chartBufferHeight;

	/** The width of the chart buffer. */
	private int chartBufferWidth;

	/**
	 * The minimum width for drawing a chart (uses scaling for smaller widths).
	 */
	private final int minimumDrawWidth;

	/**
	 * The minimum height for drawing a chart (uses scaling for smaller heights).
	 */
	private final int minimumDrawHeight;

	/**
	 * The maximum width for drawing a chart (uses scaling for bigger widths).
	 */
	private final int maximumDrawWidth;

	/**
	 * The maximum height for drawing a chart (uses scaling for bigger heights).
	 */
	private final int maximumDrawHeight;

	/** The popup menu for the frame. */
	private Menu popup;

	/** The drawing info collected the last time the chart was drawn. */
	private final ChartRenderingInfo info;

	/** The chart anchor point. */
	private Point2D anchor;

	/** The scale factor used to draw the chart. */
	private double scaleX;

	/** The scale factor used to draw the chart. */
	private double scaleY;

	/** The plot orientation. */
	private PlotOrientation orientation = PlotOrientation.VERTICAL;

	/** A flag that controls whether or not domain zooming is enabled. */
	private boolean domainZoomable = false;

	/** A flag that controls whether or not range zooming is enabled. */
	private boolean rangeZoomable = false;

	/**
	 * The zoom rectangle starting point (selected by the user with a mouse click).
	 * This is a point on the screen, not the chart (which may have been scaled up
	 * or down to fit the panel).
	 */
	private org.eclipse.swt.graphics.Point zoomPoint = null;

	/** The zoom rectangle (selected by the user with the mouse). */
	private transient Rectangle zoomRectangle = null;

	/** Controls if the zoom rectangle is drawn as an outline or filled. */
	// TODO private boolean fillZoomRectangle = true;
	/** The minimum distance required to drag the mouse to trigger a zoom. */
	private final int zoomTriggerDistance;

	/** A flag that controls whether or not horizontal tracing is enabled. */
	private boolean horizontalAxisTrace = false;

	/** A flag that controls whether or not vertical tracing is enabled. */
	private boolean verticalAxisTrace = false;

	/** A vertical trace line. */
	private transient int verticalTraceLineX;

	/** A horizontal trace line. */
	private transient int horizontalTraceLineY;

	/** Menu item for zooming in on a chart (both axes). */
	private MenuItem zoomInBothMenuItem;

	/** Menu item for zooming in on a chart (domain axis). */
	private MenuItem zoomInDomainMenuItem;

	/** Menu item for zooming in on a chart (range axis). */
	private MenuItem zoomInRangeMenuItem;

	/** Menu item for zooming out on a chart. */
	private MenuItem zoomOutBothMenuItem;

	/** Menu item for zooming out on a chart (domain axis). */
	private MenuItem zoomOutDomainMenuItem;

	/** Menu item for zooming out on a chart (range axis). */
	private MenuItem zoomOutRangeMenuItem;

	/** Menu item for resetting the zoom (both axes). */
	private MenuItem zoomResetBothMenuItem;

	/** Menu item for resetting the zoom (domain axis only). */
	private MenuItem zoomResetDomainMenuItem;

	/** Menu item for resetting the zoom (range axis only). */
	private MenuItem zoomResetRangeMenuItem;

	/** A flag that controls whether or not file extensions are enforced. */
	private boolean enforceFileExtensions;

	/** The factor used to zoom in on an axis range. */
	private double zoomInFactor = 0.5;

	/** The factor used to zoom out on an axis range. */
	private double zoomOutFactor = 2.0;

	/**
	 * Create a new chart composite with a default FillLayout. This way, when drawn,
	 * the chart will fill all the space.
	 *
	 * @param comp  The parent.
	 * @param style The style of the composite.
	 */
	public FixedChartComposite(final Composite comp, final int style) {
		this(comp, style, null, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_MINIMUM_DRAW_WIDTH, DEFAULT_MINIMUM_DRAW_HEIGHT,
				DEFAULT_MAXIMUM_DRAW_WIDTH, DEFAULT_MAXIMUM_DRAW_HEIGHT, DEFAULT_BUFFER_USED, true, // properties
				true, // save
				true, // print
				true, // zoom
				true // tooltips
		);
	}

	/**
	 * Constructs a panel that displays the specified chart.
	 *
	 * @param comp  The parent.
	 * @param style The style of the composite.
	 * @param chart the chart.
	 */
	public FixedChartComposite(final Composite comp, final int style, final JFreeChart chart) {
		this(comp, style, chart, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_MINIMUM_DRAW_WIDTH, DEFAULT_MINIMUM_DRAW_HEIGHT,
				DEFAULT_MAXIMUM_DRAW_WIDTH, DEFAULT_MAXIMUM_DRAW_HEIGHT, DEFAULT_BUFFER_USED, true, // properties
				true, // save
				true, // print
				true, // zoom
				true // tooltips
		);
	}

	/**
	 * Constructs a panel containing a chart.
	 *
	 * @param comp      The parent.
	 * @param style     The style of the composite.
	 * @param chart     the chart.
	 * @param useBuffer a flag controlling whether or not an off-screen buffer is
	 *                  used.
	 */
	public FixedChartComposite(final Composite comp, final int style, final JFreeChart chart, final boolean useBuffer) {

		this(comp, style, chart, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_MINIMUM_DRAW_WIDTH, DEFAULT_MINIMUM_DRAW_HEIGHT,
				DEFAULT_MAXIMUM_DRAW_WIDTH, DEFAULT_MAXIMUM_DRAW_HEIGHT, useBuffer, true, // properties
				true, // save
				true, // print
				true, // zoom
				true // tooltips
		);
	}

	/**
	 * Constructs a JFreeChart panel.
	 *
	 * @param comp       The parent.
	 * @param style      The style of the composite.
	 * @param chart      the chart.
	 * @param properties a flag indicating whether or not the chart property editor
	 *                   should be available via the popup menu.
	 * @param save       a flag indicating whether or not save options should be
	 *                   available via the popup menu.
	 * @param print      a flag indicating whether or not the print option should be
	 *                   available via the popup menu.
	 * @param zoom       a flag indicating whether or not zoom options should be
	 *                   added to the popup menu.
	 * @param tooltips   a flag indicating whether or not tooltips should be enabled
	 *                   for the chart.
	 */
	public FixedChartComposite(final Composite comp, final int style, final JFreeChart chart, final boolean properties,
			final boolean save, final boolean print, final boolean zoom, final boolean tooltips) {
		this(comp, style, chart, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_MINIMUM_DRAW_WIDTH, DEFAULT_MINIMUM_DRAW_HEIGHT,
				DEFAULT_MAXIMUM_DRAW_WIDTH, DEFAULT_MAXIMUM_DRAW_HEIGHT, DEFAULT_BUFFER_USED, properties, save, print,
				zoom, tooltips);
	}

	/**
	 * Constructs a JFreeChart panel.
	 *
	 * @param comp         The parent.
	 * @param style        The style of the composite.
	 * @param jfreechart   the chart.
	 * @param width        the preferred width of the panel.
	 * @param height       the preferred height of the panel.
	 * @param minimumDrawW the minimum drawing width.
	 * @param minimumDrawH the minimum drawing height.
	 * @param maximumDrawW the maximum drawing width.
	 * @param maximumDrawH the maximum drawing height.
	 * @param usingBuffer  a flag that indicates whether to use the off-screen
	 *                     buffer to improve performance (at the expense of memory).
	 * @param properties   a flag indicating whether or not the chart property
	 *                     editor should be available via the popup menu.
	 * @param save         a flag indicating whether or not save options should be
	 *                     available via the popup menu.
	 * @param print        a flag indicating whether or not the print option should
	 *                     be available via the popup menu.
	 * @param zoom         a flag indicating whether or not zoom options should be
	 *                     added to the popup menu.
	 * @param tooltips     a flag indicating whether or not tooltips should be
	 *                     enabled for the chart.
	 */
	public FixedChartComposite(final Composite comp, final int style, final JFreeChart jfreechart, final int width,
			final int height, final int minimumDrawW, final int minimumDrawH, final int maximumDrawW,
			final int maximumDrawH, final boolean usingBuffer, final boolean properties, final boolean save,
			final boolean print, final boolean zoom, final boolean tooltips) {
		super(comp, style);
		setChart(jfreechart);
		this.chartMouseListeners = new EventListenerList();
		setLayout(new FillLayout());
		this.info = new ChartRenderingInfo();
		this.useBuffer = usingBuffer;
		this.refreshBuffer = false;
		this.minimumDrawWidth = minimumDrawW;
		this.minimumDrawHeight = minimumDrawH;
		this.maximumDrawWidth = maximumDrawW;
		this.maximumDrawHeight = maximumDrawH;
		this.zoomTriggerDistance = DEFAULT_ZOOM_TRIGGER_DISTANCE;
		setDisplayToolTips(tooltips);
		// create the canvas and add the required listeners
		this.canvas = new Canvas(this, SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND);
		this.canvas.addPaintListener(this);
		this.canvas.addMouseListener(this);
		this.canvas.addMouseMoveListener(this);

		// set up popup menu...
		this.popup = null;
		if (properties || save || print || zoom)
			this.popup = createPopupMenu(properties, save, print, zoom);

		this.enforceFileExtensions = true;
	}

	/**
	 * Adds a listener to the list of objects listening for chart mouse events.
	 *
	 * @param listener the listener (<code>null</code> not permitted).
	 */
	public void addChartMouseListener(final ChartMouseListener listener) {
		this.chartMouseListeners.add(ChartMouseListener.class, listener);
	}

	/**
	 * Hook an SWT listener on the canvas where the chart is drawn. The purpose of
	 * this method is to allow some degree of customization.
	 *
	 * @param listener The SWT listener to attach to the canvas.
	 */
	public void addSWTListener(final EventListener listener) {
		if (listener instanceof ControlListener) {
			this.canvas.addControlListener((ControlListener) listener);
		} else if (listener instanceof DisposeListener) {
			this.canvas.addDisposeListener((DisposeListener) listener);
			// }
			// else if (listener instanceof DragDetectListener) {
			// this.canvas.addDragDetectListener((DragDetectListener) listener);
		} else if (listener instanceof FocusListener) {
			this.canvas.addFocusListener((FocusListener) listener);
		} else if (listener instanceof HelpListener) {
			this.canvas.addHelpListener((HelpListener) listener);
		} else if (listener instanceof KeyListener) {
			this.canvas.addKeyListener((KeyListener) listener);
			// }
			// else if (listener instanceof MenuDetectListener) {
			// this.canvas.addMenuDetectListener((MenuDetectListener) listener);
		} else if (listener instanceof MouseListener) {
			this.canvas.addMouseListener((MouseListener) listener);
		} else if (listener instanceof MouseMoveListener) {
			this.canvas.addMouseMoveListener((MouseMoveListener) listener);
		} else if (listener instanceof MouseTrackListener) {
			this.canvas.addMouseTrackListener((MouseTrackListener) listener);
			// } else if (listener instanceof MouseWheelListener) {
			// this.canvas.addMouseWheelListener((MouseWheelListener) listener);
		} else if (listener instanceof PaintListener) {
			this.canvas.addPaintListener((PaintListener) listener);
		} else if (listener instanceof TraverseListener) {
			this.canvas.addTraverseListener((TraverseListener) listener);
		}
	}

	/**
	 * Displays a dialog that allows the user to edit the properties for the current
	 * chart.
	 */
	private void attemptEditChartProperties() {
		final SWTChartEditor editor = new SWTChartEditor(this.canvas.getDisplay(), this.chart);
		// ChartEditorManager.getChartEditor(canvas.getDisplay(), this.chart);
		editor.open();
	}

	/**
	 * Receives notification of changes to the chart, and redraws the chart.
	 *
	 * @param event details of the chart change event.
	 */
	@Override
	public void chartChanged(final ChartChangeEvent event) {
		this.refreshBuffer = true;
		final Plot plot = this.chart.getPlot();
		if (plot instanceof Zoomable) {
			final Zoomable z = (Zoomable) plot;
			this.orientation = z.getOrientation();
		}

		// note, we were getting a chart update problem after the grid editor
		// view had closed and then re-opened (the chart had been disposed).
		// just double-check things are ok before we call the update
		if (!this.canvas.isDisposed()) {
			this.canvas.redraw();
		}
	}

	/**
	 * Receives notification of a chart progress event.
	 *
	 * @param event the event.
	 */
	@Override
	public void chartProgress(final ChartProgressEvent event) {
		// does nothing - override if necessary
	}

	/**
	 * Creates a print job for the chart.
	 */
	public void createChartPrintJob() {
		// FIXME try to replace swing print stuff by swt
		final PrinterJob job = PrinterJob.getPrinterJob();
		final PageFormat pf = job.defaultPage();
		final PageFormat pf2 = job.pageDialog(pf);
		if (pf2 != pf) {
			job.setPrintable(this, pf2);
			if (job.printDialog()) {
				try {
					job.print();
				} catch (final PrinterException e) {
					final MessageBox messageBox = new MessageBox(this.canvas.getShell(), SWT.OK | SWT.ICON_ERROR);
					messageBox.setMessage(e.getMessage());
					messageBox.open();
				}
			}
		}
	}

	/**
	 * Creates a popup menu for the canvas.
	 *
	 * @param properties include a menu item for the chart property editor.
	 * @param save       include a menu item for saving the chart.
	 * @param print      include a menu item for printing the chart.
	 * @param zoom       include menu items for zooming.
	 *
	 * @return The popup menu.
	 */
	protected Menu createPopupMenu(final boolean properties, final boolean save, final boolean print,
			final boolean zoom) {

		final Menu result = new Menu(this);
		boolean separator = false;

		if (properties) {
			final MenuItem propertiesItem = new MenuItem(result, SWT.PUSH);
			propertiesItem.setText(localizationResources.getString("Properties..."));
			propertiesItem.setData(PROPERTIES_COMMAND);
			propertiesItem.addSelectionListener(this);
			separator = true;
		}
		if (save) {
			if (separator) {
				new MenuItem(result, SWT.SEPARATOR);
				separator = false;
			}
			final MenuItem saveItem = new MenuItem(result, SWT.NONE);
			saveItem.setText(localizationResources.getString("Save_as..."));
			saveItem.setData(SAVE_COMMAND);
			saveItem.addSelectionListener(this);
			separator = true;
		}
		if (print) {
			if (separator) {
				new MenuItem(result, SWT.SEPARATOR);
				separator = false;
			}
			final MenuItem printItem = new MenuItem(result, SWT.NONE);
			printItem.setText(localizationResources.getString("Print..."));
			printItem.setData(PRINT_COMMAND);
			printItem.addSelectionListener(this);
			separator = true;
		}
		if (zoom) {
			if (separator) {
				new MenuItem(result, SWT.SEPARATOR);
				separator = false;
			}

			final Menu zoomInMenu = new Menu(result);
			final MenuItem zoomInMenuItem = new MenuItem(result, SWT.CASCADE);
			zoomInMenuItem.setText(localizationResources.getString("Zoom_In"));
			zoomInMenuItem.setMenu(zoomInMenu);

			this.zoomInBothMenuItem = new MenuItem(zoomInMenu, SWT.PUSH);
			this.zoomInBothMenuItem.setText(localizationResources.getString("All_Axes"));
			this.zoomInBothMenuItem.setData(ZOOM_IN_BOTH_COMMAND);
			this.zoomInBothMenuItem.addSelectionListener(this);

			new MenuItem(zoomInMenu, SWT.SEPARATOR);

			this.zoomInDomainMenuItem = new MenuItem(zoomInMenu, SWT.PUSH);
			this.zoomInDomainMenuItem.setText(localizationResources.getString("Domain_Axis"));
			this.zoomInDomainMenuItem.setData(ZOOM_IN_DOMAIN_COMMAND);
			this.zoomInDomainMenuItem.addSelectionListener(this);

			this.zoomInRangeMenuItem = new MenuItem(zoomInMenu, SWT.PUSH);
			this.zoomInRangeMenuItem.setText(localizationResources.getString("Range_Axis"));
			this.zoomInRangeMenuItem.setData(ZOOM_IN_RANGE_COMMAND);
			this.zoomInRangeMenuItem.addSelectionListener(this);

			final Menu zoomOutMenu = new Menu(result);
			final MenuItem zoomOutMenuItem = new MenuItem(result, SWT.CASCADE);
			zoomOutMenuItem.setText(localizationResources.getString("Zoom_Out"));
			zoomOutMenuItem.setMenu(zoomOutMenu);

			this.zoomOutBothMenuItem = new MenuItem(zoomOutMenu, SWT.PUSH);
			this.zoomOutBothMenuItem.setText(localizationResources.getString("All_Axes"));
			this.zoomOutBothMenuItem.setData(ZOOM_OUT_BOTH_COMMAND);
			this.zoomOutBothMenuItem.addSelectionListener(this);

			new MenuItem(zoomOutMenu, SWT.SEPARATOR);

			this.zoomOutDomainMenuItem = new MenuItem(zoomOutMenu, SWT.PUSH);
			this.zoomOutDomainMenuItem.setText(localizationResources.getString("Domain_Axis"));
			this.zoomOutDomainMenuItem.setData(ZOOM_OUT_DOMAIN_COMMAND);
			this.zoomOutDomainMenuItem.addSelectionListener(this);

			this.zoomOutRangeMenuItem = new MenuItem(zoomOutMenu, SWT.PUSH);
			this.zoomOutRangeMenuItem.setText(localizationResources.getString("Range_Axis"));
			this.zoomOutRangeMenuItem.setData(ZOOM_OUT_RANGE_COMMAND);
			this.zoomOutRangeMenuItem.addSelectionListener(this);

			final Menu autoRangeMenu = new Menu(result);
			final MenuItem autoRangeMenuItem = new MenuItem(result, SWT.CASCADE);
			autoRangeMenuItem.setText(localizationResources.getString("Auto_Range"));
			autoRangeMenuItem.setMenu(autoRangeMenu);

			this.zoomResetBothMenuItem = new MenuItem(autoRangeMenu, SWT.PUSH);
			this.zoomResetBothMenuItem.setText(localizationResources.getString("All_Axes"));
			this.zoomResetBothMenuItem.setData(ZOOM_RESET_BOTH_COMMAND);
			this.zoomResetBothMenuItem.addSelectionListener(this);

			new MenuItem(autoRangeMenu, SWT.SEPARATOR);

			this.zoomResetDomainMenuItem = new MenuItem(autoRangeMenu, SWT.PUSH);
			this.zoomResetDomainMenuItem.setText(localizationResources.getString("Domain_Axis"));
			this.zoomResetDomainMenuItem.setData(ZOOM_RESET_DOMAIN_COMMAND);
			this.zoomResetDomainMenuItem.addSelectionListener(this);

			this.zoomResetRangeMenuItem = new MenuItem(autoRangeMenu, SWT.PUSH);
			this.zoomResetRangeMenuItem.setText(localizationResources.getString("Range_Axis"));
			this.zoomResetRangeMenuItem.setData(ZOOM_RESET_RANGE_COMMAND);
			this.zoomResetRangeMenuItem.addSelectionListener(this);
		}

		return result;
	}

	/**
	 * The idea is to modify the zooming options depending on the type of chart
	 * being displayed by the panel.
	 *
	 * @param x horizontal position of the popup.
	 * @param y vertical position of the popup.
	 */
	protected void displayPopupMenu(final int x, final int y) {
		if (this.popup != null) {
			// go through each zoom menu item and decide whether or not to
			// enable it...
			final Plot plot = this.chart.getPlot();
			boolean isDomainZoomable = false;
			boolean isRangeZoomable = false;
			if (plot instanceof Zoomable) {
				final Zoomable z = (Zoomable) plot;
				isDomainZoomable = z.isDomainZoomable();
				isRangeZoomable = z.isRangeZoomable();
			}
			if (this.zoomInDomainMenuItem != null) {
				this.zoomInDomainMenuItem.setEnabled(isDomainZoomable);
			}
			if (this.zoomOutDomainMenuItem != null) {
				this.zoomOutDomainMenuItem.setEnabled(isDomainZoomable);
			}
			if (this.zoomResetDomainMenuItem != null) {
				this.zoomResetDomainMenuItem.setEnabled(isDomainZoomable);
			}

			if (this.zoomInRangeMenuItem != null) {
				this.zoomInRangeMenuItem.setEnabled(isRangeZoomable);
			}
			if (this.zoomOutRangeMenuItem != null) {
				this.zoomOutRangeMenuItem.setEnabled(isRangeZoomable);
			}

			if (this.zoomResetRangeMenuItem != null) {
				this.zoomResetRangeMenuItem.setEnabled(isRangeZoomable);
			}

			if (this.zoomInBothMenuItem != null) {
				this.zoomInBothMenuItem.setEnabled(isDomainZoomable & isRangeZoomable);
			}
			if (this.zoomOutBothMenuItem != null) {
				this.zoomOutBothMenuItem.setEnabled(isDomainZoomable & isRangeZoomable);
			}
			if (this.zoomResetBothMenuItem != null) {
				this.zoomResetBothMenuItem.setEnabled(isDomainZoomable & isRangeZoomable);
			}

			this.popup.setLocation(x, y);
			this.popup.setVisible(true);
		}

	}

	/**
	 * Disposes the control.
	 */
	@Override
	public void dispose() {
		if (this.chartBuffer != null)
			this.chartBuffer.dispose();
		// de-register the composite as a listener for the chart.
		if (this.chart != null) {
			this.chart.removeChangeListener(this);
			this.chart.removeProgressListener(this);
		}
		super.dispose();
	}

	/**
	 * Opens a file chooser and gives the user an opportunity to save the chart in
	 * PNG format.
	 *
	 * @throws IOException if there is an I/O error.
	 */
	public void doSaveAs() throws IOException {
		final FileDialog fileDialog = new FileDialog(this.canvas.getShell(), SWT.SAVE);
		final String[] extensions = { "*.png" };
		fileDialog.setFilterExtensions(extensions);
		String filename = fileDialog.open();
		if (filename != null) {
			if (isEnforceFileExtensions()) {
				if (!filename.endsWith(".png")) {
					filename = filename + ".png";
				}
			}
			// TODO replace getSize by getBounds ?
			ChartUtils.saveChartAsPNG(new File(filename), this.chart, this.canvas.getSize().x,
					this.canvas.getSize().y);
		}
	}

	/**
	 * Forces a redraw of the canvas by invoking a new PaintEvent.
	 */
	public void forceRedraw() {
		final Event ev = new Event();
		ev.gc = new GC(this.canvas);
		ev.x = 0;
		ev.y = 0;
		ev.width = this.canvas.getBounds().width;
		ev.height = this.canvas.getBounds().height;
		ev.count = 0;
		this.canvas.notifyListeners(SWT.Paint, ev);
		ev.gc.dispose();
	}

	/**
	 * This method may be called by subclasses which overrides mouse-processing
	 * methods, in order to force the implementation to stop interpreting the mouse
	 * operation is related to zoom.
	 */
	public void forgetZoomPoints() {
		this.zoomPoint = null;
		this.zoomRectangle = null;
	}

	/**
	 * Returns the anchor point.
	 *
	 * @return The anchor point (possibly <code>null</code>).
	 */
	public Point2D getAnchor() {
		return this.anchor;
	}

	/**
	 * Returns the chart contained in the panel.
	 *
	 * @return The chart (possibly <code>null</code>).
	 */
	public JFreeChart getChart() {
		return this.chart;
	}

	/**
	 * Returns the chart rendering info from the most recent chart redraw.
	 *
	 * @return The chart rendering info (possibly <code>null</code>).
	 */
	public ChartRenderingInfo getChartRenderingInfo() {
		return this.info;
	}

	/**
	 * Returns the flag that controls whether or not a horizontal axis trace line is
	 * drawn over the plot area at the current mouse location.
	 *
	 * @return A boolean.
	 */
	public boolean getHorizontalAxisTrace() {
		return this.horizontalAxisTrace;
	}

	/**
	 * Returns a point based on (x, y) but constrained to be within the bounds of
	 * the given rectangle. This method could be moved to JCommon.
	 *
	 * @param x    the x-coordinate.
	 * @param y    the y-coordinate.
	 * @param area the rectangle (<code>null</code> not permitted).
	 *
	 * @return A point within the rectangle.
	 */
	private org.eclipse.swt.graphics.Point getPointInRectangle(final int x, final int y, final Rectangle area) {
		final int x1 = Math.max(area.x, Math.min(x, area.x + area.width));
		final int y1 = Math.max(area.y, Math.min(y, area.y + area.height));
		return new org.eclipse.swt.graphics.Point(x1, y1);
	}

	/**
	 * Returns the X scale factor for the chart. This will be 1.0 if no scaling has
	 * been used.
	 *
	 * @return The scale factor.
	 */
	public double getScaleX() {
		return this.scaleX;
	}

	/**
	 * Returns the Y scale factory for the chart. This will be 1.0 if no scaling has
	 * been used.
	 *
	 * @return The scale factor.
	 */
	public double getScaleY() {
		return this.scaleY;
	}

	/**
	 * Returns the data area for the chart (the area inside the axes) with the
	 * current scaling applied (that is, the area as it appears on screen).
	 *
	 * @return The scaled data area.
	 */
	public Rectangle getScreenDataArea() {
		final Rectangle2D dataArea = this.info.getPlotInfo().getDataArea();
		final Rectangle clientArea = this.getClientArea();
		final int x = (int) (dataArea.getX() * this.scaleX + clientArea.x);
		final int y = (int) (dataArea.getY() * this.scaleY + clientArea.y);
		final int w = (int) (dataArea.getWidth() * this.scaleX);
		final int h = (int) (dataArea.getHeight() * this.scaleY);
		return new Rectangle(x, y, w, h);
	}

	/**
	 * Returns the data area (the area inside the axes) for the plot or subplot,
	 * with the current scaling applied.
	 *
	 * @param x the x-coordinate (for subplot selection).
	 * @param y the y-coordinate (for subplot selection).
	 *
	 * @return The scaled data area.
	 */
	public Rectangle getScreenDataArea(final int x, final int y) {
		final PlotRenderingInfo plotInfo = this.info.getPlotInfo();
		Rectangle result;
		if (plotInfo.getSubplotCount() == 0)
			result = getScreenDataArea();
		else {
			// get the origin of the zoom selection in the Java2D space used for
			// drawing the chart (that is, before any scaling to fit the panel)
			final Point2D selectOrigin = translateScreenToJava2D(new Point(x, y));
			final int subplotIndex = plotInfo.getSubplotIndex(selectOrigin);
			if (subplotIndex == -1) {
				return null;
			}
			result = scale(plotInfo.getSubplotInfo(subplotIndex).getDataArea());
		}
		return result;
	}

	/**
	 * Returns a string for the tooltip.
	 *
	 * @param e the mouse event.
	 *
	 * @return A tool tip or <code>null</code> if no tooltip is available.
	 */
	public String getToolTipText(final org.eclipse.swt.events.MouseEvent e) {
		String result = null;
		if (this.info != null) {
			final EntityCollection entities = this.info.getEntityCollection();
			if (entities != null) {
				final Rectangle insets = getClientArea();
				final ChartEntity entity = entities.getEntity((int) ((e.x - insets.x) / this.scaleX),
						(int) ((e.y - insets.y) / this.scaleY));
				if (entity != null) {
					result = entity.getToolTipText();
				}
			}
		}
		return result;

	}

	/**
	 * Returns the flag that controls whether or not a vertical axis trace line is
	 * drawn over the plot area at the current mouse location.
	 *
	 * @return A boolean.
	 */
	public boolean getVerticalAxisTrace() {
		return this.verticalAxisTrace;
	}

	/**
	 * Returns the zoom in factor.
	 *
	 * @return The zoom in factor.
	 *
	 * @see #setZoomInFactor(double)
	 */
	public double getZoomInFactor() {
		return this.zoomInFactor;
	}

	/**
	 * Returns the zoom out factor.
	 *
	 * @return The zoom out factor.
	 *
	 * @see #setZoomOutFactor(double)
	 */
	public double getZoomOutFactor() {
		return this.zoomOutFactor;
	}

	/**
	 * Returns the flag that determines whether or not zooming is enabled for the
	 * domain axis.
	 *
	 * @return A boolean.
	 */
	public boolean isDomainZoomable() {
		return this.domainZoomable;
	}

	/**
	 * Returns <code>true</code> if file extensions should be enforced, and
	 * <code>false</code> otherwise.
	 *
	 * @return The flag.
	 */
	public boolean isEnforceFileExtensions() {
		return this.enforceFileExtensions;
	}

	/**
	 * Returns the flag that determines whether or not zooming is enabled for the
	 * range axis.
	 *
	 * @return A boolean.
	 */
	public boolean isRangeZoomable() {
		return this.rangeZoomable;
	}

	/**
	 * Does nothing - override if necessary.
	 *
	 * @param event the mouse event.
	 */
	@Override
	public void mouseDoubleClick(final MouseEvent event) {
		// do nothing, override if necessary
	}

	/**
	 * Handles a mouse down event.
	 *
	 * @param event the event.
	 */
	@Override
	public void mouseDown(final MouseEvent event) {

		final Rectangle scaledDataArea = getScreenDataArea(event.x, event.y);
		if (scaledDataArea == null)
			return;
		this.zoomPoint = getPointInRectangle(event.x, event.y, scaledDataArea);
		final int x = (int) ((event.x - getClientArea().x) / this.scaleX);
		final int y = (int) ((event.y - getClientArea().y) / this.scaleY);

		this.anchor = new Point2D.Double(x, y);
		this.chart.setNotify(true); // force a redraw
		this.canvas.redraw();

		// new entity code
		ChartEntity entity = null;
		if (this.info != null) {
			final EntityCollection entities = this.info.getEntityCollection();
			if (entities != null) {
				// [MG] -- the code below from original
				// org.jfree.experimental.chart.swt.ChartComposite
				// doesn't work -- because entities already placed with respect to the
				// scale factor.
				//
				// [IM] -- reinstated the code below, since other version wasn't
				// working. This one (
				// the one that doesn't shift them) now works. Feb 10.
				entity = entities.getEntity(x, y);
				//
				// entity = entities.getEntity(event.x - getClientArea().x, event.y -
				// getClientArea().y);
			}
		}

		final Object[] listeners = this.chartMouseListeners.getListeners(ChartMouseListener.class);
		if (listeners.length == 0) {
			return;
		}

		// pass mouse down event if some ChartMouseListener are listening
		final java.awt.event.MouseEvent mouseEvent = SWTUtils.toAwtMouseEvent(event);
		final ChartMouseEvent chartEvent = new ChartMouseEvent(getChart(), mouseEvent, entity);
		for (int i = listeners.length - 1; i >= 0; i -= 1) {
			((ChartMouseListener) listeners[i]).chartMouseClicked(chartEvent);
		}
	}

	/**
	 * Handles a mouse move event.
	 *
	 * @param event the mouse event.
	 */
	@Override
	public void mouseMove(final MouseEvent event) {

		// handle axis trace
		if (this.horizontalAxisTrace || this.verticalAxisTrace) {
			this.horizontalTraceLineY = event.y;
			this.verticalTraceLineX = event.x;
			this.canvas.redraw();
		}

		// handle tool tips in a simple way
		if (this.displayToolTips) {
			final String s = getToolTipText(event);
			if (s == null && this.canvas.getToolTipText() != null
					|| s != null && !s.equals(this.canvas.getToolTipText()))
				this.canvas.setToolTipText(s);
		}

		// handle zoom box
		boolean hZoom, vZoom;
		if (this.zoomPoint != null) {
			final Rectangle scaledDataArea = getScreenDataArea(this.zoomPoint.x, this.zoomPoint.y);
			final org.eclipse.swt.graphics.Point movingPoint = getPointInRectangle(event.x, event.y, scaledDataArea);
			if (this.orientation == PlotOrientation.HORIZONTAL) {
				hZoom = this.rangeZoomable;
				vZoom = this.domainZoomable;
			} else {
				hZoom = this.domainZoomable;
				vZoom = this.rangeZoomable;
			}
			if (hZoom && vZoom) {
				// selected rectangle shouldn't extend outside the data area...
				this.zoomRectangle = new Rectangle(this.zoomPoint.x, this.zoomPoint.y, movingPoint.x - this.zoomPoint.x,
						movingPoint.y - this.zoomPoint.y);
			} else if (hZoom) {
				this.zoomRectangle = new Rectangle(this.zoomPoint.x, scaledDataArea.y, movingPoint.x - this.zoomPoint.x,
						scaledDataArea.height);
			} else if (vZoom) {
				final int ymax = Math.max(movingPoint.y, scaledDataArea.y);
				this.zoomRectangle = new Rectangle(scaledDataArea.x, this.zoomPoint.y, scaledDataArea.width,
						ymax - this.zoomPoint.y);
			}
			this.canvas.redraw();
		}

		// new entity code
		ChartEntity entity = null;
		// [MG] the code below is not used now, see [MG] comment below
		// int x = (int) ((event.x - getClientArea().x) / this.scaleX);
		// int y = (int) ((event.y - getClientArea().y) / this.scaleY);

		if (this.info != null) {
			final EntityCollection entities = this.info.getEntityCollection();
			if (entities != null) {
				// [MG] -- the code below from original
				// org.jfree.experimental.chart.swt.ChartComposite
				// doewn't work -- because entities already placed with respect to the
				// scale factor.
				//
				// entity = entities.getEntity(x, y);
				//
				entity = entities.getEntity(event.x - getClientArea().x, event.y - getClientArea().y);
			}
		}

		final Object[] listeners = this.chartMouseListeners.getListeners(ChartMouseListener.class);
		if (listeners.length == 0) {
			return;
		}

		// pass mouse move event if some ChartMouseListener are listening
		final java.awt.event.MouseEvent mouseEvent = SWTUtils.toAwtMouseEvent(event);
		final ChartMouseEvent chartEvent = new ChartMouseEvent(getChart(), mouseEvent, entity);
		for (int i = listeners.length - 1; i >= 0; i -= 1) {
			((ChartMouseListener) listeners[i]).chartMouseMoved(chartEvent);
		}
	}

	/**
	 * Handles a mouse up event.
	 *
	 * @param event the event.
	 */
	@Override
	public void mouseUp(final MouseEvent event) {

		boolean hZoom, vZoom;
		if (this.zoomRectangle == null) {
			final Rectangle screenDataArea = getScreenDataArea(event.x, event.y);
			if (screenDataArea != null) {
				this.zoomPoint = getPointInRectangle(event.x, event.y, screenDataArea);
			}
			if (this.popup != null && event.button == 3) {
				final org.eclipse.swt.graphics.Point pt = this.canvas.toDisplay(event.x, event.y);
				displayPopupMenu(pt.x, pt.y);
			}
		} else {
			hZoom = false;
			vZoom = false;
			if (this.orientation == PlotOrientation.HORIZONTAL) {
				hZoom = this.rangeZoomable;
				vZoom = this.domainZoomable;
			} else {
				hZoom = this.domainZoomable;
				vZoom = this.rangeZoomable;
			}
			final boolean zoomTrigger1 = hZoom && Math.abs(this.zoomRectangle.width) >= this.zoomTriggerDistance;
			final boolean zoomTrigger2 = vZoom && Math.abs(this.zoomRectangle.height) >= this.zoomTriggerDistance;
			if (zoomTrigger1 || zoomTrigger2) {
				// if the box has been drawn backwards, restore the auto bounds
				if ((hZoom && (this.zoomRectangle.x + this.zoomRectangle.width < this.zoomPoint.x))
						|| (vZoom && (this.zoomRectangle.y + this.zoomRectangle.height < this.zoomPoint.y)))
					restoreAutoBounds();
				else {
					zoom(this.zoomRectangle);
				}
				this.canvas.redraw();
			}
		}
		this.zoomPoint = null;
		this.zoomRectangle = null;
	}

	/**
	 * Paints the control.
	 *
	 * @param e the paint event.
	 */
	@Override
	public void paintControl(final PaintEvent e) {
		// first determine the size of the chart rendering area...
		// TODO workout insets for SWT
		final Rectangle available = getBounds();
		// skip if chart is null
		if (this.chart == null) {
			this.canvas.drawBackground(e.gc, available.x, available.y, available.width, available.height);
			return;
		}
		final SWTGraphics2D sg2 = new SWTGraphics2D(e.gc);

		// work out if scaling is required...
		boolean scale = false;
		int drawWidth = available.width;
		int drawHeight = available.height;
		if (drawWidth == 0.0 || drawHeight == 0.0)
			return;
		this.scaleX = 1.0;
		this.scaleY = 1.0;
		if (drawWidth < this.minimumDrawWidth) {
			this.scaleX = (double) drawWidth / this.minimumDrawWidth;
			drawWidth = this.minimumDrawWidth;
			scale = true;
		} else if (drawWidth > this.maximumDrawWidth) {
			this.scaleX = (double) drawWidth / this.maximumDrawWidth;
			drawWidth = this.maximumDrawWidth;
			scale = true;
		}
		if (drawHeight < this.minimumDrawHeight) {
			this.scaleY = (double) drawHeight / this.minimumDrawHeight;
			drawHeight = this.minimumDrawHeight;
			scale = true;
		} else if (drawHeight > this.maximumDrawHeight) {
			this.scaleY = (double) drawHeight / this.maximumDrawHeight;
			drawHeight = this.maximumDrawHeight;
			scale = true;
		}
		// are we using the chart buffer?
		if (this.useBuffer) {
			// SwtGraphics2D sg2 = new SwtGraphics2D(e.gc);
			this.chartBuffer = (org.eclipse.swt.graphics.Image) this.canvas.getData("double-buffer-image");
			// do we need to fill the buffer?
			if (this.chartBuffer == null || this.chartBufferWidth != available.width
					|| this.chartBufferHeight != available.height) {
				this.chartBufferWidth = available.width;
				this.chartBufferHeight = available.height;
				if (this.chartBuffer != null) {
					this.chartBuffer.dispose();
				}
				this.chartBuffer = new org.eclipse.swt.graphics.Image(getDisplay(), this.chartBufferWidth,
						this.chartBufferHeight);
				this.refreshBuffer = true;
			}

			// do we need to redraw the buffer?
			if (this.refreshBuffer) {
				// Performs the actual drawing here ...
				final GC gci = new GC(this.chartBuffer);
				// anti-aliasing
				if (this.chart.getAntiAlias()) {
					gci.setAntialias(SWT.ON);
				}
				if (this.chart.getTextAntiAlias() == RenderingHints.KEY_TEXT_ANTIALIASING) {
					gci.setTextAntialias(SWT.ON);
				}
				final SWTGraphics2D sg2d = new SWTGraphics2D(gci);
				if (scale) {
					sg2d.scale(this.scaleX, this.scaleY);
					this.chart.draw(sg2d, new Rectangle2D.Double(0, 0, drawWidth, drawHeight), getAnchor(), this.info);
				} else {
					this.chart.draw(sg2d, new Rectangle2D.Double(0, 0, drawWidth, drawHeight), getAnchor(), this.info);
				}
				this.canvas.setData("double-buffer-image", this.chartBuffer);
				sg2d.dispose();
				gci.dispose();
				this.refreshBuffer = false;
			}

			// zap the buffer onto the canvas...
			sg2.drawImage(this.chartBuffer, 0, 0);
		}
		// or redrawing the chart every time...
		else {
			if (this.chart.getAntiAlias()) {
				e.gc.setAntialias(SWT.ON);
			}
			if (this.chart.getTextAntiAlias() == RenderingHints.KEY_TEXT_ANTIALIASING) {
				e.gc.setTextAntialias(SWT.ON);
			}
			this.chart.draw(sg2, new Rectangle2D.Double(0, 0, getBounds().width, getBounds().height), getAnchor(),
					this.info);
		}
		final Rectangle area = getScreenDataArea();
		// TODO see if we need to apply some line color and style to the
		// axis traces
		if (this.horizontalAxisTrace && area.x < this.verticalTraceLineX
				&& area.x + area.width > this.verticalTraceLineX) {
			e.gc.drawLine(this.verticalTraceLineX, area.y, this.verticalTraceLineX, area.y + area.height);
		}
		if (this.verticalAxisTrace && area.y < this.horizontalTraceLineY
				&& area.y + area.height > this.horizontalTraceLineY) {
			e.gc.drawLine(area.x, this.horizontalTraceLineY, area.x + area.width, this.horizontalTraceLineY);
		}
		this.verticalTraceLineX = 0;
		this.horizontalTraceLineY = 0;
		if (this.zoomRectangle != null) {
			e.gc.drawRectangle(this.zoomRectangle);
		}
		sg2.dispose();
	}

	/**
	 * Not implemented.
	 *
	 * @param graphics   the graphics.
	 * @param pageFormat the page format.
	 * @param pageIndex  the page index.
	 *
	 * @return ?.
	 *
	 * @throws PrinterException if there is a problem.
	 */
	@Override
	public int print(final Graphics graphics, final PageFormat pageFormat, final int pageIndex)
			throws PrinterException {
		if (pageIndex != 0) {
			return NO_SUCH_PAGE;
		}
		/*
		 * CairoImage image = new CairoImage( this.getBounds().width,
		 * this.getBounds().height); Graphics2D g2 = image.createGraphics2D(); double x
		 * = pageFormat.getImageableX(); double y = pageFormat.getImageableY(); double w
		 * = pageFormat.getImageableWidth(); double h = pageFormat.getImageableHeight();
		 * this.chart.draw( g2, new Rectangle2D.Double(x, y, w, h), this.anchor, null );
		 */
		return PAGE_EXISTS;
	}

	public void redrawCanvas() {
		if (!this.canvas.isDisposed())
			this.canvas.redraw();
	}

	/**
	 * Removes a listener from the list of objects listening for chart mouse events.
	 *
	 * @param listener the listener.
	 */
	public void removeChartMouseListener(final ChartMouseListener listener) {
		this.chartMouseListeners.remove(ChartMouseListener.class, listener);
	}

	/**
	 * Restores the auto-range calculation on both axes.
	 */
	public void restoreAutoBounds() {
		restoreAutoDomainBounds();
		restoreAutoRangeBounds();
	}

	/**
	 * Restores the auto-range calculation on the domain axis.
	 */
	public void restoreAutoDomainBounds() {
		final Plot p = this.chart.getPlot();
		if (p instanceof Zoomable) {
			final Zoomable z = (Zoomable) p;
			// we need to guard against this.zoomPoint being null
			final org.eclipse.swt.graphics.Point zp = (this.zoomPoint != null ? this.zoomPoint
					: new org.eclipse.swt.graphics.Point(0, 0));
			z.zoomDomainAxes(0.0, this.info.getPlotInfo(), SWTUtils.toAwtPoint(zp));
		}
	}

	/**
	 * Restores the auto-range calculation on the range axis.
	 */
	public void restoreAutoRangeBounds() {
		final Plot p = this.chart.getPlot();
		if (p instanceof ValueAxisPlot) {
			final Zoomable z = (Zoomable) p;
			// we need to guard against this.zoomPoint being null
			final org.eclipse.swt.graphics.Point zp = (this.zoomPoint != null ? this.zoomPoint
					: new org.eclipse.swt.graphics.Point(0, 0));
			z.zoomRangeAxes(0.0, this.info.getPlotInfo(), SWTUtils.toAwtPoint(zp));
		}
	}

	/**
	 * Applies any scaling that is in effect for the chart drawing to the given
	 * rectangle.
	 *
	 * @param rect the rectangle.
	 *
	 * @return A new scaled rectangle.
	 */
	public Rectangle scale(final Rectangle2D rect) {
		final Rectangle insets = this.getClientArea();
		final int x = (int) Math.round(rect.getX() * getScaleX()) + insets.x;
		final int y = (int) Math.round(rect.getY() * this.getScaleY()) + insets.y;
		final int w = (int) Math.round(rect.getWidth() * this.getScaleX());
		final int h = (int) Math.round(rect.getHeight() * this.getScaleY());
		return new Rectangle(x, y, w, h);
	}

	/**
	 * Sets the anchor point. This method is provided for the use of subclasses, not
	 * end users.
	 *
	 * @param anchor the anchor point (<code>null</code> permitted).
	 */
	protected void setAnchor(final Point2D anchor) {
		this.anchor = anchor;
	}

	/**
	 * Sets the chart that is displayed in the panel.
	 *
	 * @param chart the chart (<code>null</code> permitted).
	 */
	public void setChart(final JFreeChart chart) {
		// stop listening for changes to the existing chart
		if (this.chart != null) {
			this.chart.removeChangeListener(this);
			this.chart.removeProgressListener(this);
		}

		// add the new chart
		this.chart = chart;
		if (chart != null) {
			this.chart.addChangeListener(this);
			this.chart.addProgressListener(this);
			final Plot plot = chart.getPlot();
			this.domainZoomable = false;
			this.rangeZoomable = false;
			if (plot instanceof Zoomable) {
				final Zoomable z = (Zoomable) plot;
				this.domainZoomable = z.isDomainZoomable();
				this.rangeZoomable = z.isRangeZoomable();
				this.orientation = z.getOrientation();
			}
		} else {
			this.domainZoomable = false;
			this.rangeZoomable = false;
		}
		if (this.useBuffer) {
			this.refreshBuffer = true;
		}
	}

	/**
	 * @param displayToolTips the displayToolTips to set
	 */
	public void setDisplayToolTips(final boolean displayToolTips) {
		this.displayToolTips = displayToolTips;
	}

	/**
	 * Sets the flag that controls whether or not zooming is enable for the domain
	 * axis. A check is made to ensure that the current plot supports zooming for
	 * the domain values.
	 *
	 * @param flag <code>true</code> enables zooming if possible.
	 */
	public void setDomainZoomable(final boolean flag) {
		if (flag) {
			final Plot plot = this.chart.getPlot();
			if (plot instanceof Zoomable) {
				final Zoomable z = (Zoomable) plot;
				this.domainZoomable = flag && (z.isDomainZoomable());
			}
		} else {
			this.domainZoomable = false;
		}
	}

	/**
	 * Sets a flag that controls whether or not file extensions are enforced.
	 *
	 * @param enforce the new flag value.
	 */
	public void setEnforceFileExtensions(final boolean enforce) {
		this.enforceFileExtensions = enforce;
	}

	/**
	 * A flag that controls trace lines on the horizontal axis.
	 *
	 * @param flag <code>true</code> enables trace lines for the mouse pointer on
	 *             the horizontal axis.
	 */
	public void setHorizontalAxisTrace(final boolean flag) {
		this.horizontalAxisTrace = flag;
	}

	/**
	 * A flag that controls mouse-based zooming on the vertical axis.
	 *
	 * @param flag <code>true</code> enables zooming.
	 */
	public void setRangeZoomable(final boolean flag) {
		if (flag) {
			final Plot plot = this.chart.getPlot();
			if (plot instanceof Zoomable) {
				final Zoomable z = (Zoomable) plot;
				this.rangeZoomable = flag && (z.isRangeZoomable());
			}
		} else {
			this.rangeZoomable = false;
		}
	}

	/**
	 * A flag that controls trace lines on the vertical axis.
	 *
	 * @param flag <code>true</code> enables trace lines for the mouse pointer on
	 *             the vertical axis.
	 */
	public void setVerticalAxisTrace(final boolean flag) {
		this.verticalAxisTrace = flag;
	}

	/**
	 * Sets the zoom in factor.
	 *
	 * @param factor the factor.
	 *
	 * @see #getZoomInFactor()
	 */
	public void setZoomInFactor(final double factor) {
		this.zoomInFactor = factor;
	}

	/**
	 * Sets the zoom out factor.
	 *
	 * @param factor the factor.
	 *
	 * @see #getZoomOutFactor()
	 */
	public void setZoomOutFactor(final double factor) {
		this.zoomOutFactor = factor;
	}

	/**
	 * Translates a Java2D point on the chart to a screen location.
	 *
	 * @param java2DPoint the Java2D point.
	 *
	 * @return The screen location.
	 */
	public Point translateJava2DToScreen(final Point2D java2DPoint) {
		final Rectangle insets = this.getClientArea();
		final int x = (int) (java2DPoint.getX() * this.scaleX + insets.x);
		final int y = (int) (java2DPoint.getY() * this.scaleY + insets.y);
		return new Point(x, y);
	}

	/**
	 * Translates a screen location to a Java2D point.
	 *
	 * @param screenPoint the screen location.
	 *
	 * @return The Java2D coordinates.
	 */
	public Point2D translateScreenToJava2D(final Point screenPoint) {
		final Rectangle insets = this.getClientArea();
		final int x = (int) ((screenPoint.x - insets.x) / this.scaleX);
		final int y = (int) ((screenPoint.y - insets.y) / this.scaleY);
		return new Point2D.Double(x, y);
	}

	/**
	 * Translates a screen location to a Java SWT point.
	 *
	 * @param screenPoint the screen location.
	 *
	 * @return The Java2D coordinates.
	 */
	public Point translateScreenToJavaSWT(final Point screenPoint) {
		final Rectangle insets = this.getClientArea();
		final int x = (int) ((screenPoint.x - insets.x) / this.scaleX);
		final int y = (int) ((screenPoint.y - insets.y) / this.scaleY);
		return new Point(x, y);
	}

	/**
	 * Handles action events generated by the popup menu.
	 *
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
		widgetSelected(e);
	}

	/**
	 * Handles action events generated by the popup menu.
	 *
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(final SelectionEvent e) {
		final String command = (String) ((MenuItem) e.getSource()).getData();
		if (command.equals(PROPERTIES_COMMAND)) {
			attemptEditChartProperties();
		} else if (command.equals(SAVE_COMMAND)) {
			try {
				doSaveAs();
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		} else if (command.equals(PRINT_COMMAND)) {
			createChartPrintJob();
		}
		/*
		 * in the next zoomPoint.x and y replace by e.x and y for now. this helps to
		 * handle the mouse events and besides, those values are unused AFAIK.
		 */
		else if (command.equals(ZOOM_IN_BOTH_COMMAND)) {
			zoomInBoth(e.x, e.y);
		} else if (command.equals(ZOOM_IN_DOMAIN_COMMAND)) {
			zoomInDomain(e.x, e.y);
		} else if (command.equals(ZOOM_IN_RANGE_COMMAND)) {
			zoomInRange(e.x, e.y);
		} else if (command.equals(ZOOM_OUT_BOTH_COMMAND)) {
			zoomOutBoth(e.x, e.y);
		} else if (command.equals(ZOOM_OUT_DOMAIN_COMMAND)) {
			zoomOutDomain(e.x, e.y);
		} else if (command.equals(ZOOM_OUT_RANGE_COMMAND)) {
			zoomOutRange(e.x, e.y);
		} else if (command.equals(ZOOM_RESET_BOTH_COMMAND)) {
			restoreAutoBounds();
		} else if (command.equals(ZOOM_RESET_DOMAIN_COMMAND)) {
			restoreAutoDomainBounds();
		} else if (command.equals(ZOOM_RESET_RANGE_COMMAND)) {
			restoreAutoRangeBounds();
		}
		this.forceRedraw();
	}

	/**
	 * Zooms in on a selected region.
	 *
	 * @param selection the selected region.
	 */
	public void zoom(final Rectangle selection) {

		// get the origin of the zoom selection in the Java2D space used for
		// drawing the chart (that is, before any scaling to fit the panel)
		final Point2D selectOrigin = translateScreenToJava2D(new Point(selection.x, selection.y));
		final PlotRenderingInfo plotInfo = this.info.getPlotInfo();
		final Rectangle scaledDataArea = getScreenDataArea((selection.x + selection.width / 2),
				(selection.y + selection.height / 2));
		if ((selection.height > 0) && (selection.width > 0)) {

			final double hLower = (selection.x - scaledDataArea.x) / (double) scaledDataArea.width;
			final double hUpper = (selection.x + selection.width - scaledDataArea.x) / (double) scaledDataArea.width;
			final double vLower = (scaledDataArea.y + scaledDataArea.height - selection.y - selection.height)
					/ (double) scaledDataArea.height;
			final double vUpper = (scaledDataArea.y + scaledDataArea.height - selection.y)
					/ (double) scaledDataArea.height;
			final Plot p = this.chart.getPlot();
			if (p instanceof Zoomable) {
				final Zoomable z = (Zoomable) p;
				if (z.getOrientation() == PlotOrientation.HORIZONTAL) {
					z.zoomDomainAxes(vLower, vUpper, plotInfo, selectOrigin);
					z.zoomRangeAxes(hLower, hUpper, plotInfo, selectOrigin);
				} else {
					z.zoomDomainAxes(hLower, hUpper, plotInfo, selectOrigin);
					z.zoomRangeAxes(vLower, vUpper, plotInfo, selectOrigin);
				}
			}

		}

	}

	/**
	 * Zooms in on an anchor point (specified in screen coordinate space).
	 *
	 * @param x the x value (in screen coordinates).
	 * @param y the y value (in screen coordinates).
	 */
	public void zoomInBoth(final double x, final double y) {
		zoomInDomain(x, y);
		zoomInRange(x, y);
	}

	/**
	 * Decreases the length of the domain axis, centered about the given coordinate
	 * on the screen. The length of the domain axis is reduced by the value of
	 * {@link #getZoomInFactor()}.
	 *
	 * @param x the x coordinate (in screen coordinates).
	 * @param y the y-coordinate (in screen coordinates).
	 */
	public void zoomInDomain(final double x, final double y) {
		final Plot p = this.chart.getPlot();
		if (p instanceof Zoomable) {
			final Zoomable plot = (Zoomable) p;
			plot.zoomDomainAxes(this.zoomInFactor, this.info.getPlotInfo(),
					translateScreenToJava2D(new Point((int) x, (int) y)));
		}
	}

	/**
	 * Decreases the length of the range axis, centered about the given coordinate
	 * on the screen. The length of the range axis is reduced by the value of
	 * {@link #getZoomInFactor()}.
	 *
	 * @param x the x-coordinate (in screen coordinates).
	 * @param y the y coordinate (in screen coordinates).
	 */
	public void zoomInRange(final double x, final double y) {
		final Plot p = this.chart.getPlot();
		if (p instanceof Zoomable) {
			final Zoomable z = (Zoomable) p;
			z.zoomRangeAxes(this.zoomInFactor, this.info.getPlotInfo(),
					translateScreenToJava2D(new Point((int) x, (int) y)));
		}
	}

	/**
	 * Zooms out on an anchor point (specified in screen coordinate space).
	 *
	 * @param x the x value (in screen coordinates).
	 * @param y the y value (in screen coordinates).
	 */
	public void zoomOutBoth(final double x, final double y) {
		zoomOutDomain(x, y);
		zoomOutRange(x, y);
	}

	/**
	 * Increases the length of the domain axis, centered about the given coordinate
	 * on the screen. The length of the domain axis is increased by the value of
	 * {@link #getZoomOutFactor()}.
	 *
	 * @param x the x coordinate (in screen coordinates).
	 * @param y the y-coordinate (in screen coordinates).
	 */
	public void zoomOutDomain(final double x, final double y) {
		final Plot p = this.chart.getPlot();
		if (p instanceof Zoomable) {
			final Zoomable z = (Zoomable) p;
			z.zoomDomainAxes(this.zoomOutFactor, this.info.getPlotInfo(),
					translateScreenToJava2D(new Point((int) x, (int) y)));
		}
	}

	/**
	 * Increases the length the range axis, centered about the given coordinate on
	 * the screen. The length of the range axis is increased by the value of
	 * {@link #getZoomOutFactor()}.
	 *
	 * @param x the x coordinate (in screen coordinates).
	 * @param y the y-coordinate (in screen coordinates).
	 */
	public void zoomOutRange(final double x, final double y) {
		final Plot p = this.chart.getPlot();
		if (p instanceof Zoomable) {
			final Zoomable z = (Zoomable) p;
			z.zoomRangeAxes(this.zoomOutFactor, this.info.getPlotInfo(),
					translateScreenToJava2D(new Point((int) x, (int) y)));
		}
	}

}
