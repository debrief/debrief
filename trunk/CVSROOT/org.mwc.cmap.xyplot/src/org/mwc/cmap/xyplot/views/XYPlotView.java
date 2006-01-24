package org.mwc.cmap.xyplot.views;

import java.awt.*;
import java.beans.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.xyplot.Activator;

import Debrief.GUI.Tote.StepControl;
import Debrief.Tools.FilterOperations.ShowTimeVariablePlot2.formattingOperation;
import MWC.Algorithms.Projections.FlatProjection;
import MWC.GUI.Canvas.MetafileCanvasGraphics2d;
import MWC.GUI.ptplot.jfreeChart.*;
import MWC.GUI.ptplot.jfreeChart.Utils.*;
import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

import com.jrefinery.chart.*;
import com.jrefinery.chart.tooltips.*;
import com.jrefinery.data.AbstractDataset;
import com.pietjonas.wmfwriter2d.ClipboardCopy;

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

public class XYPlotView extends ViewPart
{

	private final class SelectionHelper implements ISelectionProvider
	{
		private Vector _selectionListeners;

		public void addSelectionChangedListener(ISelectionChangedListener listener)
		{
			if (_selectionListeners == null)
				_selectionListeners = new Vector(0, 1);

			// see if we don't already contain it..
			if (!_selectionListeners.contains(listener))
				_selectionListeners.add(listener);
		}

		public ISelection getSelection()
		{
			return null;
		}

		public void removeSelectionChangedListener(ISelectionChangedListener listener)
		{
			_selectionListeners.remove(listener);
		}

		public void setSelection(ISelection selection)
		{
		}

		public void fireNewSelection(ISelection data)
		{
			SelectionChangedEvent sEvent = new SelectionChangedEvent(this, data);
			for (Iterator stepper = _selectionListeners.iterator(); stepper.hasNext();)
			{
				ISelectionChangedListener thisL = (ISelectionChangedListener) stepper.next();
				if (thisL != null)
				{
					thisL.selectionChanged(sEvent);
				}
			}
		}
	}

	/**
	 * resize the data to fill the window
	 */
	private Action _fitToWindow;

	/**
	 * output the plot as a WMF
	 */
	private Action _exportToWMF;

	/**
	 * put the plot on the clipboard
	 */
	private Action _exportToClipboard;

	private String _myTitle = "Empty";

	/**
	 * the Swing control we insert the plot into
	 */
	private Frame _plotControl;

	/**
	 * the data-area of the plot
	 */
	private StepperXYPlot _thePlot;

	/**
	 * the area surrounding the plot
	 */
	private FormattedJFreeChart _thePlotArea;

	/**
	 * object to tie the plot to the step control
	 */
	private StepperChartPanel _chartInPanel;

	/**
	 * somebody to listen to the time changes
	 */
	private PropertyChangeListener _timeListener;

	/**
	 * helper - to let the user edit us
	 */
	private SelectionHelper _selectionHelper;

	private Action _editMyProperties;

	/**
	 * The constructor.
	 */
	public XYPlotView()
	{
	}

	/**
	 * put some data into the view
	 * 
	 * @param title -
	 *          the title for the plot
	 * @param dataset -
	 *          the dataset to plot
	 * @param units -
	 *          the units (for the y axis)
	 * @param theFormatter -
	 *          an object capable of applying formatting to the plot
	 */
	public void showPlot(String title, AbstractDataset dataset, String units,
			formattingOperation theFormatter)
	{
		_myTitle = title;
		// ok, update the plot.
		this.setPartName(_myTitle);

		// ok, fill in the plot
		fillThePlot(title, units, theFormatter, dataset);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{

		// right, we need an SWT.EMBEDDED object to act as a holder
		Composite holder = new Composite(parent, SWT.EMBEDDED);

		// now we need a Swing object to put our chart into
		_plotControl = SWT_AWT.new_Frame(holder);
		_plotControl.setLayout(new BorderLayout());

		// and lastly do the remaining bits...
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		// put in the plot-copy support
		IActionBars actionBars = getViewSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), _exportToClipboard);

		// and the selection provider bits
		_selectionHelper = new SelectionHelper();
		getSite().setSelectionProvider(_selectionHelper);

	}

	private void fillThePlot(String title, String units, formattingOperation theFormatter,
			AbstractDataset dataset)
	{

		StepControl _theStepper = null;

		// the working variables we rely on later
		_thePlotArea = null;
		ValueAxis xAxis = null;

		XYToolTipGenerator tooltipGenerator = null;

		// the y axis is common to hi & lo res. Format it here
		ModifiedVerticalNumberAxis yAxis = new ModifiedVerticalNumberAxis(units);

		// hmm, see if we are in hi-res mode. If we are, don't use a formatted
		// y-axis, just use the plain long microseconds
		// value
		if (HiResDate.inHiResProcessingMode())
		{

			final SimpleDateFormat _secFormat = new SimpleDateFormat("ss");

			// ok, simple enough for us...
			NumberAxis nAxis = new HorizontalNumberAxis("time (secs.micros)")
			{
				public String getTickLabel(double currentTickValue)
				{
					long time = (long) currentTickValue;
					Date dtg = new HiResDate(0, time).getDate();
					String res = _secFormat.format(dtg) + "."
							+ DebriefFormatDateTime.formatMicros(new HiResDate(0, time));
					return res;
				}
			};
			nAxis.setAutoRangeIncludesZero(false);
			xAxis = nAxis;

			// just show the raw data values
			tooltipGenerator = new StandardXYToolTipGenerator();
		}
		else
		{
			// create a date-formatting axis
			final HorizontalDateAxis dAxis = new HorizontalDateAxis("time");
			dAxis.setStandardTickUnits(DateAxisEditor.createStandardDateTickUnitsAsTickUnits());
			xAxis = dAxis;

			// also create the date-knowledgable tooltip writer
			tooltipGenerator = new DatedToolTipGenerator();
		}

		// create the special stepper plot
		_thePlot = new StepperXYPlot(null, xAxis, yAxis, _theStepper);

		// apply any formatting for this choice
		if (theFormatter != null)
		{
			theFormatter.format(_thePlot);
		}

		_thePlot.setRenderer(new ColourStandardXYItemRenderer(
				StandardXYItemRenderer.SHAPES_AND_LINES, tooltipGenerator, null));

		_thePlotArea = new FormattedJFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
				_thePlot, true, _theStepper);

		// set the color of the area surrounding the plot
		// - naah, don't bother. leave it in the application background color.
		// _plotArea.setBackgroundPaint(Color.white);

		// ////////////////////////////////////////////////
		// put the holder into one of our special items
		// ////////////////////////////////////////////////
		_chartInPanel = new StepperChartPanel(_thePlotArea, true, _theStepper);

		// ok - we need to fire time-changes to the chart
		setupFiringChangesToChart();

		// format the chart
		_chartInPanel.setName(title);
		_chartInPanel.setMouseZoomable(true, true);

		// and insert into the panel
		_plotControl.add(_chartInPanel, BorderLayout.CENTER);

		// ////////////////////////////////////////////////////
		// put the time series into the plot
		// ////////////////////////////////////////////////////
		_thePlot.setDataset(dataset);
	}

	private void setupFiringChangesToChart()
	{
		// get the document being edited
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		IEditorPart editor = page.getActiveEditor();

		// get it's time-provider interface
		TimeProvider prov = (TimeProvider) editor.getAdapter(TimeProvider.class);

		if (prov != null)
		{
			// create our listener
			_timeListener = new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					// ok - fire the time change to the chart
					HiResDate newDTG = (HiResDate) evt.getNewValue();

					// right tell the plot it's new time
					_thePlot.newTime(null, newDTG, null);

					// and tell the plot holder to redraw everything
					_chartInPanel.newTime(null, newDTG, null);
				}
			};

			// add our listener to the time object
			prov.addListener(_timeListener, TimeProvider.TIME_CHANGED_PROPERTY_NAME);

			// fire the current time to our chart (just to start us off)
			_chartInPanel.newTime(null, prov.getTime(), null);
		}
	}

	private final void wmfToFile()
	{

		// create the metafile graphics
		final MetafileCanvasGraphics2d mf = new MetafileCanvasGraphics2d("c:/",
				(Graphics2D) _chartInPanel.getGraphics());

		doWMF(mf);
	}

	private final void wmfToClipboard()
	{

		// create the metafile graphics
		final MetafileCanvasGraphics2d mf = new MetafileCanvasGraphics2d("c:/",
				(Graphics2D) _chartInPanel.getGraphics());

		doWMF(mf);

		// try to get the filename
		String fName = MetafileCanvasGraphics2d.getLastFileName();

		// get the dimensions of the last plot operation
		Dimension dim = MetafileCanvasGraphics2d.getLastScreenSize();

		// try to copy the wmf to the clipboard
		try
		{
			// create the clipboard
			ClipboardCopy cc = new ClipboardCopy();

			cc.copyWithPixelSize(fName, dim.width, dim.height, false);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private final void doWMF(MetafileCanvasGraphics2d mf)
	{

		// get the old background colour
		Paint oldColor = _thePlot.getBackgroundPaint();
		Paint oldAreaColor = _thePlotArea.getBackgroundPaint();

		// set the background to clear
		_thePlotArea.setBackgroundPaint(Color.white);
		_thePlot.setBackgroundPaint(Color.white);

		// copy the projection
		final MWC.Algorithms.Projections.FlatProjection fp = new FlatProjection();
		fp.setScreenArea(_plotControl.getSize());
		mf.setProjection(fp);

		// start drawing
		mf.startDraw(null);

		// sort out the background colour
		Dimension dim = _plotControl.getSize();
		mf.setBackgroundColor(java.awt.Color.white);
		mf.setColor(mf.getBackgroundColor());
		mf.fillRect(0, 0, dim.width, dim.height);

		// ask the canvas to paint the image
		_chartInPanel.paintWMFComponent(mf);

		// and finish
		mf.endDraw(null);

		// and restore the background colour
		_thePlot.setBackgroundPaint(oldColor);
		_thePlotArea.setBackgroundPaint(oldAreaColor);

	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				XYPlotView.this.fillContextMenu(manager);
			}
		});
		// Menu menu = menuMgr.createContextMenu(viewer.getControl());
		// viewer.getControl().setMenu(menu);
		// getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(_fitToWindow);
		manager.add(new Separator());
		manager.add(_exportToWMF);
		manager.add(_exportToClipboard);
		manager.add(_editMyProperties);		
	}

	private void fillContextMenu(IMenuManager manager)
	{
		manager.add(_fitToWindow);
		manager.add(_exportToWMF);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(_fitToWindow);
		manager.add(_exportToWMF);
		manager.add(_exportToClipboard);
		manager.add(_editMyProperties);
	}

	private void makeActions()
	{

		_editMyProperties = new Action()
		{
			public void run()
			{
				editMeInProperties();
			}
		};
		_editMyProperties.setText("Configure plot");
		_editMyProperties.setToolTipText("Change editable properties for this chart");
		_editMyProperties.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/properties.gif"));

		_fitToWindow = new Action()
		{
			public void run()
			{
				_thePlot.zoom(0.0);
			}
		};
		_fitToWindow.setText("Fit to window");
		_fitToWindow.setToolTipText("Scale the graph to show all data");
		_fitToWindow.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/fit_to_size.png"));

		_exportToWMF = new Action()
		{
			public void run()
			{
				wmfToFile();
			}
		};
		_exportToWMF.setText("Export to WMF");
		_exportToWMF.setToolTipText("Produce a WMF file of the graph");
		_exportToWMF.setImageDescriptor(Activator
				.getImageDescriptor("icons/photo_scenery.png"));

		_exportToClipboard = new Action()
		{
			public void run()
			{
				wmfToClipboard();
			}
		};
		_exportToClipboard.setText("Copy to Clipboard");
		_exportToClipboard.setToolTipText("Place a WMF file of the graph on the clipboard");
		_exportToClipboard
				.setImageDescriptor(CorePlugin.getImageDescriptor("icons/copy.png"));

	}

	private void hookDoubleClickAction()
	{
		// viewer.addDoubleClickListener(new IDoubleClickListener() {
		// public void doubleClick(DoubleClickEvent event) {
		// doubleClickAction.run();
		// }
		// });
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		// viewer.getControl().setFocus();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void editMeInProperties()
	{
		// do we have any data?
		if (_thePlotArea != null)
		{
			EditableWrapper wrappedEditable = new EditableWrapper(_thePlotArea, null);
			StructuredSelection _propsAsSelection = new StructuredSelection(wrappedEditable);

			_selectionHelper.fireNewSelection(_propsAsSelection);
		}
		else
		{
			System.out.println("we haven't got any properties yet");
		}
	}
}