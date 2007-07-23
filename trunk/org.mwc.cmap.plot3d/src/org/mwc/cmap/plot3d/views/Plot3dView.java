package org.mwc.cmap.plot3d.views;

import java.awt.*;
import java.beans.*;
import java.util.*;

import javax.media.j3d.*;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.*;
import org.mwc.cmap.core.property_support.EditableWrapper;

import Debrief.GUI.Tote.StepControl;
import Debrief.Tools.Operations.View3dPlot;
import Debrief.Tools.Operations.Plot3D.*;
import Debrief.Wrappers.*;
import MWC.GUI.*;
import MWC.GUI.Chart.Painters.SpatialRasterPainter;
import MWC.GUI.ETOPO.*;
import MWC.GUI.Java3d.*;
import MWC.GUI.Java3d.Tactical.*;
import MWC.GUI.Properties.PropertiesPanel;
import MWC.GUI.StepperListener.StepperController;
import MWC.GUI.Tools.Palette.CreateTOPO;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.PlainImporterBase;

import com.sun.j3d.utils.universe.SimpleUniverse;

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

public class Plot3dView extends ViewPart
{

	// //////////////////////////////////////////////////
	// data we store between sessions
	// //////////////////////////////////////////////////

	// /**
	// * data-type names
	// */
	// private final String TITLE = "XYPlot_Title";
	//
	// private final String UNITS = "XYPlot_Units";
	//
	// private final String FORMATTER = "XYPlot_Formatter";
	//
	// private final String DATA = "XYPlot_Data";
	//
	// private static interface PLOT_ATTRIBUTES
	// {
	// final String AxisFont = "AxisFont";
	//
	// final String TickFont = "TickFont";
	//
	// final String TitleFont = "TitleFont";
	//
	// final String LineWidth = "LineWidth";
	//
	// final String Title = "Title";
	//
	// final String X_Title = "X_Title";
	//
	// final String Y_Title = "Y_Title";
	//
	// final String DateUnits = "DateUnits";
	//
	// final String RelativeTimes = "RelativeTimes";
	//
	// final String ShowSymbols = "ShowSymbols";
	// }

	/**
	 * title of plot
	 */
	private String _myTitle = "Empty";

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
	 * the Swing control we insert the plot into
	 */
	private Frame _plotControl;

	/**
	 * helper - to let the user edit us
	 */
	private SelectionHelper _selectionHelper;

	private Action _editMyProperties;

	/**
	 * store the plot information when we're reloading a plot in a fresh session
	 */
	private IMemento _myMemento = null;

	private Layers _myLayers;

	private StepperController _myTimer;

	private MouseWheelWorldHolder _myWorld;

	/**
	 * The constructor.
	 */
	public Plot3dView()
	{
	}

	/**
	 * put some data into the view
	 * 
	 * @param title -
	 *          the title for the plot
	 * @param timer
	 * @param dataset -
	 *          the dataset to plot
	 * @param units -
	 *          the units (for the y axis)
	 * @param theFormatter -
	 *          an object capable of applying formatting to the plot
	 */
	public void showPlot(String title, Layers theData, TimeProvider timer)
	{
		// right, store the incoming data, so we can save it when/if
		// Eclipse closes with this view still open
		_myTitle = title;
		_myLayers = theData;

		CorePlugin.logError(Status.INFO, "in show-plot method", null);
		
		// wrap the timer
		_myTimer = new TimeControllerWrapper(timer);

		// ok, update the plot.
		this.setPartName(_myTitle);

		// right - try to generate our new view
		// prepare the bathy data
		BathyProvider _bathyProvider = new ETOPO_2_Minute(CreateTOPO.getETOPOPath());

		CorePlugin.logError(Status.INFO, "found bathy data", null);
		
		try
		{
			_myWorld = new MouseWheelWorldHolder(null, _myTimer, _myLayers, _bathyProvider,
					true)
			{
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void dataExtended(final Layers theData)
				{
					//
					System.out.println("extended!");
					doDataExtended(theData, this);
				}

				public void dataModified(final Layers theData, final Layer changedLayer)
				{
					System.out.println("modified!");
				}

				public void dataReformatted(final Layers theData, final Layer changedLayer)
				{
					//
					System.out.println("reformatted!");
				}
			};
		}
		catch (java.lang.NoClassDefFoundError ne)
		{
			// right - we haven't found the 3d bits in the classpath
			CorePlugin.logError(Status.ERROR,
					"Sorry your Debrief can't find the 3d libraries in your classpath. "
							+ " Please verify your installation", ne);
		}
		catch (java.lang.UnsatisfiedLinkError le)
		{
			// right - we're using a jre that hasn't got Java3d installed
			CorePlugin
					.logError(
							Status.ERROR,
							"Sorry your Java installation hasn't got 3d installed."
									+ "  You may not be using the installation of Java distributed with Debrief NG",
							le);
		}

		CorePlugin.logError(Status.INFO, "about to call data-extended", null);
		
		doDataExtended(_myLayers, _myWorld);

		CorePlugin.logError(Status.INFO, "about to call world-finished", null);
		
		// add the buoyfields

		// done
		_myWorld.finish();

		CorePlugin.logError(Status.INFO, "about to put 3d control into panel", null);

		// put it in the holder.
		_plotControl.add(_myWorld, BorderLayout.CENTER);

		CorePlugin.logError(Status.INFO, "3d control placed into panel", null);
	}

	private static class TimeControllerWrapper implements StepperController
	{
		/** right, which time provider are we managing?
		 * 
		 */
		private TimeProvider _prov;
		
		/** hmm, keep track of the wrapped listeners. we need to remember the 
		 * wrapped instance so we can implement the 'remove' action
		 */
		private HashMap _listeners;

		
		public TimeControllerWrapper(TimeProvider prov)
		{
			_prov = prov;
			
			_listeners = new HashMap();
		}

		public void addStepperListener(final StepperListener listener)
		{
			PropertyChangeListener pcl = new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent arg0)
				{
					HiResDate newD = (HiResDate) arg0.getNewValue();
					HiResDate oldD = (HiResDate) arg0.getOldValue();
					listener.newTime(newD, oldD, null);
				}
			};

			// remember that listener before we go & forget it
			_listeners.put(listener, pcl);

			// and tell the timer about our new listener
			_prov.addListener(pcl, TimeManager.TIME_CHANGED_PROPERTY_NAME);
			
		}

		public void removeStepperListener(StepperListener listener)
		{
			PropertyChangeListener list = (PropertyChangeListener) _listeners.get(listener);
			
			// remove it from our list
			_listeners.remove(listener);
			
			// and from the timer manager			
			_prov.removeListener(list, TimeManager.TIME_CHANGED_PROPERTY_NAME);
		}

		public void doStep(boolean forward, boolean large_step)
		{
		}

		public HiResDate getCurrentTime()
		{
			return _prov.getTime();
		}

		public HiResDate getTimeZero()
		{
			return null;
		}

	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{

		// right, we need an SWT.EMBEDDED object to act as a holder
		Composite holder = new Composite(parent, SWT.EMBEDDED);

		// now we need a Swing object to put our chart into (though we don't put it
		// in there yet)
		_plotControl = SWT_AWT.new_Frame(holder);
		_plotControl.setLayout(new BorderLayout());

		// and lastly do the remaining bits...
		makeActions();
		hookContextMenu();
		contributeToActionBars();

		// and the selection provider bits
		_selectionHelper = new SelectionHelper();
		getSite().setSelectionProvider(_selectionHelper);

		// hey, have we got our data?
		if (_myMemento != null)
		{
			// yup, better restore it then.
			restorePreviousPlot();
		}

	}

	/**
	 * we're restoring a previous plot. retrieve the data from the memento, and
	 * stick it back into the plot
	 */
	private void restorePreviousPlot()
	{
		// // retrieve the obvious stuff
		// _myTitle = _myMemento.getString(TITLE);
		// _myUnits = _myMemento.getString(UNITS);
		//
		// // get our special streaming library ready
		// XStream xs = new XStream(new DomDriver());
		//
		// // formatter first
		// String theFormatterStr = _myMemento.getString(FORMATTER);
		//
		// // hey, do we have a formatter?
		// if (theFormatterStr != null)
		// _theFormatter = (formattingOperation) xs.fromXML(theFormatterStr);
		//
		// // and the data
		// String dataStr = _myMemento.getString(DATA);
		// _dataset = (AbstractDataset) xs.fromXML(dataStr);
		//
		// // right, that's the essential bits, now open the plot
		// showPlot(_myTitle, _dataset, _myUnits, _theFormatter);
		//
		// // right the plot's done, put back in our fancy formatting bits
		// String str;
		// str = _myMemento.getString(PLOT_ATTRIBUTES.AxisFont);
		// if(str != null)
		// _thePlotArea.setAxisFont((Font) xs.fromXML(str));
		// str = _myMemento.getString(PLOT_ATTRIBUTES.TickFont);
		// if(str != null)
		// _thePlotArea.setTickFont((Font) xs.fromXML(str));
		// str = _myMemento.getString(PLOT_ATTRIBUTES.TitleFont);
		// if(str != null)
		// _thePlotArea.setTitleFont((Font) xs.fromXML(str));
		// str = _myMemento.getString(PLOT_ATTRIBUTES.LineWidth);
		// if(str != null)
		// _thePlotArea.setDataLineWidth(((Integer) xs.fromXML(str)).intValue());
		// str = _myMemento.getString(PLOT_ATTRIBUTES.Title);
		// if(str != null)
		// _thePlotArea.setTitle((String) xs.fromXML(str));
		// str = _myMemento.getString(PLOT_ATTRIBUTES.X_Title);
		// if(str != null)
		// _thePlotArea.setX_AxisTitle((String) xs.fromXML(str));
		// str = _myMemento.getString(PLOT_ATTRIBUTES.Y_Title);
		// if(str != null)
		// _thePlotArea.setY_AxisTitle((String) xs.fromXML(str));
		// str = _myMemento.getString(PLOT_ATTRIBUTES.DateUnits);
		// if(str != null)
		// _thePlotArea.setDateTickUnits((MWCDateTickUnitWrapper) xs.fromXML(str));
		// str = _myMemento.getString(PLOT_ATTRIBUTES.RelativeTimes);
		// if(str != null)
		// _thePlotArea.setRelativeTimes(((Boolean)
		// xs.fromXML(str)).booleanValue());
		// str = _myMemento.getString(PLOT_ATTRIBUTES.ShowSymbols);
		// if(str != null)
		// _thePlotArea.setShowSymbols(((Boolean) xs.fromXML(str)).booleanValue());
	}

	// private void fillThePlot(String title, String units, formattingOperation
	// theFormatter,
	// AbstractDataset dataset)
	// {

	// StepControl _theStepper = null;
	//
	// // the working variables we rely on later
	// _thePlotArea = null;
	// ValueAxis xAxis = null;
	//
	// XYToolTipGenerator tooltipGenerator = null;
	//
	// // the y axis is common to hi & lo res. Format it here
	// ModifiedVerticalNumberAxis yAxis = new ModifiedVerticalNumberAxis(units);
	//
	// // hmm, see if we are in hi-res mode. If we are, don't use a formatted
	// // y-axis, just use the plain long microseconds
	// // value
	// if (HiResDate.inHiResProcessingMode())
	// {
	//
	// final SimpleDateFormat _secFormat = new SimpleDateFormat("ss");
	//
	// // ok, simple enough for us...
	// NumberAxis nAxis = new HorizontalNumberAxis("time (secs.micros)")
	// {
	// public String getTickLabel(double currentTickValue)
	// {
	// long time = (long) currentTickValue;
	// Date dtg = new HiResDate(0, time).getDate();
	// String res = _secFormat.format(dtg) + "."
	// + DebriefFormatDateTime.formatMicros(new HiResDate(0, time));
	// return res;
	// }
	// };
	// nAxis.setAutoRangeIncludesZero(false);
	// xAxis = nAxis;
	//
	// // just show the raw data values
	// tooltipGenerator = new StandardXYToolTipGenerator();
	// }
	// else
	// {
	// // create a date-formatting axis
	// final HorizontalDateAxis dAxis = new HorizontalDateAxis("time");
	// dAxis.setStandardTickUnits(DateAxisEditor.createStandardDateTickUnitsAsTickUnits());
	// xAxis = dAxis;
	//
	// // also create the date-knowledgable tooltip writer
	// tooltipGenerator = new DatedToolTipGenerator();
	// }
	//
	// // create the special stepper plot
	// _thePlot = new StepperXYPlot(null, xAxis, yAxis, _theStepper);
	//
	// // apply any formatting for this choice
	// if (theFormatter != null)
	// {
	// theFormatter.format(_thePlot);
	// }
	//
	// _thePlot.setRenderer(new ColourStandardXYItemRenderer(
	// StandardXYItemRenderer.SHAPES_AND_LINES, tooltipGenerator, null));
	//
	// _thePlotArea = new FormattedJFreeChart(title,
	// JFreeChart.DEFAULT_TITLE_FONT,
	// _thePlot, true, _theStepper);
	//
	// // set the color of the area surrounding the plot
	// // - naah, don't bother. leave it in the application background color.
	// // _plotArea.setBackgroundPaint(Color.white);
	//
	// // ////////////////////////////////////////////////
	// // put the holder into one of our special items
	// // ////////////////////////////////////////////////
	// _chartInPanel = new StepperChartPanel(_thePlotArea, true, _theStepper);
	//
	// // ok - we need to fire time-changes to the chart
	// setupFiringChangesToChart();
	//
	// // format the chart
	// _chartInPanel.setName(title);
	// _chartInPanel.setMouseZoomable(true, true);
	//
	// // and insert into the panel
	// _plotControl.add(_chartInPanel, BorderLayout.CENTER);
	//
	// // ////////////////////////////////////////////////////
	// // put the time series into the plot
	// // ////////////////////////////////////////////////////
	// _thePlot.setDataset(dataset);
	// }

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				Plot3dView.this.fillContextMenu(manager);
			}
		});
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(_editMyProperties);

		// and the help link
		manager.add(new Separator());
		manager.add(CorePlugin.createOpenHelpAction("org.mwc.debrief.help.Plot3d", null, this));
		
	}

	private void fillContextMenu(IMenuManager manager)
	{
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
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

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void editMeInProperties()
	{
		// do we have any data?
		if (_myWorld != null)
		{
			EditableWrapper wrappedEditable = new EditableWrapper(_myWorld.getWorld()
					.getWorldPlottingOptions(), null);
			StructuredSelection _propsAsSelection = new StructuredSelection(wrappedEditable);
			_selectionHelper.fireNewSelection(_propsAsSelection);
		}
		else
		{
			System.out.println("we haven't got any properties yet");
		}
	}

	/**
	 * right, load ourselves from the supplied dataset
	 * 
	 * @param site
	 * @param memento
	 * @throws PartInitException
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		// let our parent go for it first
		super.init(site, memento);

		// is there any data waiting? We get an empty memento if this is a fresh
		// view
		if (memento != null)
		{
			_myMemento = memento;
		}
	}

	/**
	 * right - store ourselves into the supplied memento object
	 * 
	 * @param memento
	 */
	public void saveState(IMemento memento)
	{
		// let our parent go for it first
		super.saveState(memento);

		// memento.putString(TITLE, _myTitle);
		// memento.putString(UNITS, _myUnits);
		//
		// XStream xs = new XStream(new DomDriver());
		// String str;
		//
		// // String str = xs.toXML(_theFormatter);
		// if (_theFormatter != null)
		// {
		// str = xs.toXML(_theFormatter);
		// memento.putString(FORMATTER, str);
		// }
		//
		// str = xs.toXML(_dataset);
		// memento.putString(DATA, str);
		//
		// // now the other plot bits
		// str = xs.toXML(_thePlotArea.getAxisFont());
		// memento.putString(PLOT_ATTRIBUTES.AxisFont, str);
		// str = xs.toXML(_thePlotArea.getTickFont());
		// memento.putString(PLOT_ATTRIBUTES.TickFont, str);
		// str = xs.toXML(_thePlotArea.getTitleFont());
		// memento.putString(PLOT_ATTRIBUTES.TitleFont, str);
		// str = xs.toXML(new Integer(_thePlotArea.getDataLineWidth()));
		// memento.putString(PLOT_ATTRIBUTES.LineWidth, str);
		// str = xs.toXML(_thePlotArea.getTitle());
		// memento.putString(PLOT_ATTRIBUTES.Title, str);
		// str = xs.toXML(_thePlotArea.getX_AxisTitle());
		// memento.putString(PLOT_ATTRIBUTES.X_Title, str);
		// str = xs.toXML(_thePlotArea.getY_AxisTitle());
		// memento.putString(PLOT_ATTRIBUTES.Y_Title, str);
		// str = xs.toXML(_thePlotArea.getDateTickUnits());
		// memento.putString(PLOT_ATTRIBUTES.DateUnits, str);
		// str = xs.toXML(new Boolean(_thePlotArea.getRelativeTimes()));
		// memento.putString(PLOT_ATTRIBUTES.RelativeTimes, str);
		// str = xs.toXML(new Boolean(_thePlotArea.isShowSymbols()));
		// memento.putString(PLOT_ATTRIBUTES.ShowSymbols, str);

	}

	public static class StringHolder
	{
		private String _myString;

		public StringHolder()
		{
		};

		public StringHolder(String theString)
		{
			_myString = theString;
		}

		public String get_myString()
		{
			return _myString;
		}

		public void set_myString(String string)
		{
			_myString = string;
		};
	}

	/**
	 * ************************************************************** layers
	 * support listening
	 * **************************************************************
	 */

	private Group createThisLayer(Layer thisLayer, World theWorld, WorldHolder holder)
	{
		Group res = null;

		// so, is this a track?
		if (thisLayer instanceof TrackWrapper)
		{
			// go on, stick it in!
			TrackWrapper thisTrack = (TrackWrapper) thisLayer;
			res = new Track3D(theWorld.getWorldPlottingOptions(), theWorld, thisTrack, _myTimer);
			// also tell the world holder that a new track is loaded
			holder.addTrack((Track3D) res);

		}
		// is it an ETOPO layer?
		else if (thisLayer instanceof SpatialRasterPainter)
		{
			// ok, get the world to add it's bathy
			theWorld.populateBathy((SpatialRasterPainter) thisLayer, holder.getUniverse());
		}
		else if (thisLayer instanceof BaseLayer)
		{
			// create the layer
			res = new Layer3D((BaseLayer) thisLayer);
		}
		return res;
	}

	private void checkThisPlottable(Plottable thisP, World world, Group layer)
	{
		// do we hold this plottable

		Node theLabel = world.containsThis(thisP);

		if (theLabel == null)
		{
			if (thisP instanceof LabelWrapper)
			{
				LabelWrapper thisLW = (LabelWrapper) thisP;
				theLabel = new LabelWrapper3D(world.getWorldPlottingOptions(), world, thisLW,
						_myTimer);
			}
			else if (thisP instanceof ShapeWrapper)
			{
				ShapeWrapper sw = (ShapeWrapper) thisP;
				theLabel = new ShapeWrapper3D(world.getWorldPlottingOptions(), world, sw,
						_myTimer);
			}

			if (theLabel != null)
			{
				layer.addChild(theLabel);
			}
		}
	}

	private void checkThis2dLayerIsPresentIn3d(Layer thisLayer, WorldHolder holder,
			World world, Group parent)
	{
		// right, check if we contain this layer
		Group layer = (Group) world.containsThis(thisLayer);
		if (layer == null)
		{
			// right, create this layer
			layer = createThisLayer(thisLayer, world, holder);

			// and add it (if applicable)
			if (layer != null)
			{
				// yup, we need to create it.
				// do we know our parent?
				if (parent != null)
				{
					world.detachForRemoval();
					parent.addChild(layer);
					world.andReattach(holder.getUniverse());
				}
				else
				{
					world.detachForRemoval();
					world.addThisItem(layer);
					world.andReattach(holder.getUniverse());
				}
			}
		}

		// just check if it was an ETOPO, in which case we drop out
		if (thisLayer instanceof SpatialRasterPainter)
			return;

		// also check if it was a track wrapper, in which case we can return
		if (thisLayer instanceof TrackWrapper)
			return;

		// now check for the children of the base layer
		Enumeration iter = thisLayer.elements();
		while (iter.hasMoreElements())
		{
			Plottable thisP = (Plottable) iter.nextElement();

			// what sort is it?
			if (thisP instanceof Layer)
			{
				checkThis2dLayerIsPresentIn3d((Layer) thisP, holder, world, layer);
			}
			else
			{
				checkThisPlottable(thisP, world, layer);
			}
		}

	}

	private void doDataExtended(final Layers theData, final WorldHolder theHolder)
	{
		// so, first pass down through our set of layers, and check that everything
		// in there
		// is present in 3d
		World myWorld = theHolder.getWorld();

		for (int i = 0; i < theData.size(); i++)
		{
			// get the next layer
			Layer thisLayer = theData.elementAt(i);

			// see if we have it
			checkThis2dLayerIsPresentIn3d(thisLayer, theHolder, myWorld, null);
		}

		// now do the reverse check, to see if all of the 3d items are in our layers
		// object
		Group parent = myWorld.getTransform();

		// ok, go for it!
		checkThis3dGroupIn2d(parent, null, myWorld, theHolder.getUniverse(), theHolder);

	}

	private void checkThis3dGroupIn2d(Group group, Group parent, World world,
			SimpleUniverse universe, WorldHolder holder)
	{
		if (group.getCapability(Group.ALLOW_CHILDREN_READ))
		{
			// first run through this group to look a the children
			for (int i = 0; i < group.numChildren(); i++)
			{
				Node child = group.getChild(i);

				// firstly see if this is a layer itself
				if (child instanceof Group)
				{
					Group grp = (Group) child;
					checkThis3dGroupIn2d(grp, group, world, universe, holder);
				}

				// now, does it have user data?
				Object o = child.getUserData();
				if (!containsThisItem(o))
				{
					world.detachForRemoval();
					group.removeChild(child);
					world.andReattach(universe);

					// right, was it a track?
					if (o instanceof TrackWrapper)
					{
						// yup, we've also got to remove it from the list of views
						holder.removeThisTrack((Participant3D) child);
					}

				}

			}

			// now check this parent
			if (!containsThisItem(group.getUserData()))
			{
				// we want to get rid of it, ditch it!
				if (parent != null)
				{
					world.detachForRemoval();
					parent.removeChild(group);
					world.andReattach(universe);
				}
				else
				{
					// hey, we're not going to ditch the top level!
				}
			} // whether there is user data
		} // whether we can read the children
	}

	public boolean containsThisItem(Object o)
	{
		boolean res = false;

		if (o == null)
			return true;

		if (o instanceof Plottable)
		{
			Plottable plottable = (Plottable) o;

			for (int i = 0; i < _myLayers.size(); i++)
			{
				// get the next layer
				Layer thisLayer = _myLayers.elementAt(i);

				// see if this layer contains our plottable
				boolean found = checkThisLayer(thisLayer, plottable);

				if (found)
				{
					res = true;
					break;
				} // whether it was found
			} // through the layers
		} // whether this is a plottable
		else
		{
			// user data is not a plottable - must be for our own 3d management, make
			// it "acceptable"
			res = true;
		}

		return res;
	}

	private boolean checkThisLayer(Layer thisLayer, Plottable plottable)
	{
		boolean res = false;

		// check the layer itself
		if (thisLayer == plottable)
		{
			res = true;
		}
		else
		{
			Enumeration iter = thisLayer.elements();
			while (iter.hasMoreElements())
			{
				Plottable pl = (Plottable) iter.nextElement();

				if (pl == plottable)
				{
					res = true;
					break;
				}

				if (pl instanceof Layer)
				{
					res = checkThisLayer((Layer) pl, plottable);
					if (res = true)
						break;
				}
			}
		}

		return res;
	}

	// ////////////////////////////////////////////////
	// testing code
	// ////////////////////////////////////////////////

	public static void main(String[] args)
	{
		MWC.GUI.Layers theLayers = new MWC.GUI.Layers();
		try
		{

			final String theFileName = "d:\\dev\\debrief\\debrief_out\\SOVEREIGN.REP";

			PlainImporterBase pi = new Debrief.ReaderWriter.Replay.ImportReplay();
			pi.importThis(theFileName, new java.io.FileInputStream(theFileName), theLayers);
		}
		catch (Exception e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}

		StepControl myStepper = new StepControl(null)
		{
			protected void doEditPainter()
			{
			}

			protected void formatTimeText()
			{
			}

			protected PropertiesPanel getPropertiesPanel()
			{
				return null;
			}

			public HiResDate getToolboxEndTime()
			{
				return null;
			}

			public HiResDate getToolboxStartTime()
			{
				return null;
			}

			protected void initForm()
			{
			}

			protected void painterIsDefined()
			{
			}

			public void setToolboxEndTime(HiResDate val)
			{
			}

			public void setToolboxStartTime(HiResDate val)
			{
			}

			protected void updateForm(HiResDate DTG)
			{
			}
		};
		myStepper.setStepSmall(1000);

		TrackWrapper tw = (TrackWrapper) theLayers.findLayer("sovere");

		myStepper.addParticipant(tw, tw.getStartDTG(), tw.getEndDTG());
		myStepper.doStep(true, true);

		// ok, now create the View3dPlot button
		View3dPlot plotter = new View3dPlot(null, null, theLayers, myStepper);

		plotter.execute();

		myStepper.gotoEnd();
	}

	public void setFocus()
	{
	}
}