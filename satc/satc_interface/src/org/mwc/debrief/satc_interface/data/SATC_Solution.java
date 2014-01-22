package org.mwc.debrief.satc_interface.data;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Vector;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jfree.util.ReadOnlyIterator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.satc_interface.data.wrappers.BMC_Wrapper;
import org.mwc.debrief.satc_interface.data.wrappers.ContributionWrapper;
import org.mwc.debrief.satc_interface.data.wrappers.CourseForecastWrapper;
import org.mwc.debrief.satc_interface.data.wrappers.StraightLegWrapper;
import org.mwc.debrief.satc_interface.utilities.conversions;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.ISecondaryTrack;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ExtendedCanvasType;
import MWC.GUI.FireReformatted;
import MWC.GUI.Layers;
import MWC.GUI.Layers.NeedsToKnowAboutLayers;
import MWC.GUI.NeedsToBeInformedOfRemove;
import MWC.GUI.SupportsPropertyListeners;
import MWC.GUI.Canvas.CanvasTypeUtilities;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GUI.Shapes.Symbols.SymbolFactory;
import MWC.GenericData.HiResDate;
import MWC.GenericData.NonColoredWatchable;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.ContributionDataType;
import com.planetmayo.debrief.satc.model.contributions.CourseForecastContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.generator.IConstrainSpaceListener;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IContributionsChangedListener;
import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.legs.AlteringRoute;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;
import com.planetmayo.debrief.satc.model.manager.ISolversManager;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc.util.MathUtils;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.vividsolutions.jts.geom.Coordinate;

public class SATC_Solution extends BaseLayer implements
		NeedsToBeInformedOfRemove, NeedsToKnowAboutLayers, WatchableList,
		BaseLayer.ProvidesRange, ISecondaryTrack,  Editable.DoNoInspectChildren, NonColoredWatchable
{
	// ///////////////////////////////////////////////////////////
	// info class
	// //////////////////////////////////////////////////////////
	public class SATC_Info extends Editable.EditorType implements Serializable
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SATC_Info(SATC_Solution data)
		{
			super(data, data.getName(), "");
		}

		@Override
		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				PropertyDescriptor[] res =
				{
						prop("ShowLocationConstraints",
								"whether to display location constraints", FORMAT),
						prop("OnlyPlotLegEnds",
								"whether to only plot location bounds at leg ends", FORMAT),
						prop("ShowSolutions", "whether to display solutions", FORMAT),
						prop("Name", "the name for this solution", EditorType.FORMAT),
						prop("Color", "the color to display this solution",
								EditorType.FORMAT),
						prop("Visible", "whether to plot this solution", VISIBILITY) };

				return res;
			}
			catch (IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}

		@Override
		public final MethodDescriptor[] getMethodDescriptors()
		{
			final Class<SATC_Solution> c = SATC_Solution.class;

			final MethodDescriptor[] mds =
			{ method(c, "convertToLegs", null, "Convert to Composite Track (legs)"),
					method(c, "convertToTrack", null, "Convert to Standalone Track"),
					method(c, "recalculate", null, "Recalculate solutions") };

			return mds;
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ISolver _mySolver;

	private Color _myColor = Color.green;

	/**
	 * the plain font we use as a base
	 */
	static final Font LEG_NAME_FONT = new Font("Sans Serif", Font.BOLD, 12);

	private Layers _myLayers = null;

	private boolean _showLocationBounds = false;

	private boolean _onlyPlotLegEnds = false;

	private boolean _showSolutions = true;

	/**
	 * the last set of bounded states that we know about
	 * 
	 */
	protected Collection<BoundedState> _lastStates;

	/**
	 * any solutions returned by hte algorithm
	 * 
	 */
	protected CompositeRoute[] _newRoutes;

	private IGenerateSolutionsListener _readyListener;

	private IContributionsChangedListener _contributionsListener;

	private IConstrainSpaceListener _constrainListener;

	private PlainSymbol mySymbol;

	public SATC_Solution(ISolver newSolution)
	{
		super.setName(newSolution.getName());

		_mySolver = newSolution;

		// clear the solver, just to be sure
		_mySolver.getContributions().clear();

		// and listen for changes
		listenToSolver(_mySolver);
	}

	public void addContribution(BaseContribution cont)
	{
		_mySolver.getContributions().addContribution(cont);

		ContributionWrapper thisW;
		if (cont instanceof BearingMeasurementContribution)
			thisW = new BMC_Wrapper((BearingMeasurementContribution) cont);
		else if (cont instanceof StraightLegForecastContribution)
			thisW = new StraightLegWrapper(cont);
		else if (cont instanceof CourseForecastContribution)
			thisW = new CourseForecastWrapper((CourseForecastContribution) cont);
		else
			thisW = new ContributionWrapper(cont);
		super.add(thisW);
	}

	/**
	 * convert this solution into a formal track
	 * 
	 */
	public void convertToTrack()
	{
		// check if we have any solutions
		if ((_newRoutes == null) || (_newRoutes.length == 0))
		{
			CorePlugin.errorDialog("Convert solution to track",
					"Sorry, this solution contains no generated routes");
		}
		else
		{
			for (int i = 0; i < _newRoutes.length; i++)
			{
				CompositeRoute thisR = _newRoutes[i];

				// the output track
				TrackWrapper newT = new TrackWrapper();
				newT.setName(getName() + "_" + i);

				// loop through the legs
				Iterator<CoreRoute> legs = thisR.getLegs().iterator();
				while (legs.hasNext())
				{
					CoreRoute thisLeg = (CoreRoute) legs.next();

					// ok, loop through the states
					Iterator<State> iter = thisLeg.getStates().iterator();
					while (iter.hasNext())
					{
						State state = (State) iter.next();
						WorldLocation theLoc = conversions.toLocation(state.getLocation()
								.getCoordinate());
						double theCourse = state.getCourse();
						double theSpeed = new WorldSpeed(state.getSpeed(), WorldSpeed.M_sec)
								.getValueIn(WorldSpeed.ft_sec / 3);
						Fix newF = new Fix(new HiResDate(state.getTime().getTime()),
								theLoc, theCourse, theSpeed);
						FixWrapper newFW = new FixWrapper(newF);
						newT.addFix(newFW);
					}
				}

				// and store it
				_myLayers.addThisLayer(newT);
			}
		}
	}

	/**
	 * whether this type of BaseLayer is able to have shapes added to it
	 * 
	 * @return
	 */
	@Override
	public boolean canTakeShapes()
	{
		return false;
	}

	protected void fireRepaint()
	{
		super.firePropertyChange(SupportsPropertyListeners.FORMAT, null, this);
	}

	/**
	 * return our legs as a series of track segments - for the bearing residuals
	 * plot
	 * 
	 */
	@Override
	public Enumeration<Editable> elements()
	{
		Vector<Editable> res = new Vector<Editable>();

		// ok, loop through the legs, representing each one as a track segment
		// check if we have any solutions
		if ((_newRoutes == null) || (_newRoutes.length == 0))
		{
		}
		else
		{
			CompositeRoute thisR = _newRoutes[0];

			// loop through the legs
			Iterator<CoreRoute> legs = thisR.getLegs().iterator();
			while (legs.hasNext())
			{
				TrackSegment ts = new TrackSegment();

				CoreRoute thisLeg = (CoreRoute) legs.next();

				// ok, loop through the states
				Iterator<State> iter = thisLeg.getStates().iterator();
				while (iter.hasNext())
				{
					State state = (State) iter.next();
					WorldLocation theLoc = conversions.toLocation(state.getLocation()
							.getCoordinate());
					double theCourse = state.getCourse();
					double theSpeed = new WorldSpeed(state.getSpeed(), WorldSpeed.M_sec)
							.getValueIn(WorldSpeed.ft_sec / 3);
					Fix newF = new Fix(new HiResDate(state.getTime().getTime()), theLoc,
							theCourse, theSpeed);
					FixWrapper newFW = new FixWrapper(newF)
					{

						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						@Override
						public String getMultiLineName()
						{
							return super.getName();
						}

					};
					final Color thisCol;

					if (state.getColor() == null)
						thisCol = Color.red;
					else
						thisCol = state.getColor();
					newFW.setColor(thisCol);
					ts.addFix(newFW);
				}

				res.add(ts);
			}
		}

		return res.elements();
	}

	@Override
	public EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new SATC_Info(this);

		return _myEditor;
	}

	public void recalculate()
	{
		_mySolver.run(true, true);
	}

	public Color getColor()
	{
		return _myColor;
	}

	public void setColor(Color color)
	{
		this._myColor = color;
	}

	public boolean getShowLocationConstraints()
	{
		return _showLocationBounds;
	}

	public boolean getOnlyPlotLegEnds()
	{
		return _onlyPlotLegEnds;
	}

	public void setOnlyPlotLegEnds(boolean onlyPlotLegEnds)
	{
		this._onlyPlotLegEnds = onlyPlotLegEnds;
	}

	public ISolver getSolver()
	{
		return _mySolver;
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();

		_mySolver.getSolutionGenerator().removeReadyListener(_readyListener);
		_mySolver.getContributions().removeContributionsChangedListener(
				_contributionsListener);
		_mySolver.getBoundsManager().removeConstrainSpaceListener(
				_constrainListener);
		_myLayers = null;
		_mySolver = null;

	}

	@Override
	public boolean hasEditor()
	{
		return true;
	}

	@Override
	public boolean hasOrderedChildren()
	{
		return true;
	}

	private static class MeasureRange implements RouteStepper
	{

		final private WorldLocation origin;
		double minRange = Double.MAX_VALUE;

		public MeasureRange(WorldLocation origin)
		{
			this.origin = origin;
		}

		@Override
		public void step(CoreRoute thisRoute, State thisState)
		{
			com.vividsolutions.jts.geom.Point loc = thisState.getLocation();
			// convert to our coord system
			WorldLocation wLoc = conversions.toLocation(loc.getCoordinate());
			double range = wLoc.rangeFrom(origin);
			minRange = Math.min(range, minRange);
		}

		public final double getMinRange()
		{
			return minRange;
		}

		@Override
		public void legComplete(CoreRoute thisRoute)
		{
		}

		@Override
		public void finish()
		{
		}

		@Override
		public void reset()
		{
		}
	}

	@Override
	public double rangeFrom(final WorldLocation other)
	{
		double res = -1;
		if ((_newRoutes != null) && (_newRoutes.length > 0))
		{
			MeasureRange mr = new MeasureRange(other);
			walkRoute(_newRoutes, mr);
			res = mr.getMinRange();
		}
		return res;
	}

	@Override
	public boolean isBuffered()
	{
		return false;
	}

	public boolean getShowSolutions()
	{
		return _showSolutions;
	}

	@Override
	public void removeElement(Editable p)
	{
		// ditch it from the parent
		super.removeElement(p);

		// get the ocntribution itself
		ContributionWrapper cw = (ContributionWrapper) p;
		BaseContribution comp = cw.getContribution();

		// also remove it from the manager component
		_mySolver.getContributions().removeContribution(comp);
	}

	private void listenToSolver(ISolver solver)
	{
		_readyListener = new IGenerateSolutionsListener()
		{

			@Override
			public void finishedGeneration(Throwable error)
			{
			}

			@Override
			public void solutionsReady(CompositeRoute[] routes)
			{
				_newRoutes = routes;

				// tell the layer manager that we've changed

				fireTrackShifted();

				// hey, trigger repaint
				fireRepaint();
			}

			@Override
			public void startingGeneration()
			{
				// ditch any existing routes
				_newRoutes = null;
			}
		};

		_contributionsListener = new IContributionsChangedListener()
		{

			public void fireExtended()
			{
				firePropertyChange(SupportsPropertyListeners.EXTENDED, null, this);
			}

			@Override
			public void added(BaseContribution contribution)
			{
				// fireRepaint();
				fireExtended();
			}

			@Override
			public void removed(BaseContribution contribution)
			{

				// hey, are we still storing this?
				Editable toBeRemoved = null;

				// get read-only version of elements
				ReadOnlyIterator rIter = new ReadOnlyIterator(getData().iterator());
				while (rIter.hasNext())
				{
					Editable editable = (Editable) rIter.next();
					ContributionWrapper cw = (ContributionWrapper) editable;
					if (cw.getContribution() == contribution)
					{
						// _mySolver.getContributions().removeContribution(contribution);
						toBeRemoved = cw;
					}
				}

				if (toBeRemoved != null)
				{
					removeElement(toBeRemoved);
				}
				else
				{
					SATC_Activator
							.log(
									IStatus.ERROR,
									"We were asked to remove a contribution, but we didn't have it stored in the Layer",
									null);
				}

				fireExtended();
			}
		};

		_constrainListener = new IConstrainSpaceListener()
		{
			@Override
			public void error(IBoundsManager boundsManager,
					IncompatibleStateException ex)
			{
				_lastStates = null;
			}

			@Override
			public void restarted(IBoundsManager boundsManager)
			{
				_lastStates = null;
				_newRoutes = null;
			}

			@Override
			public void statesBounded(IBoundsManager boundsManager)
			{
				// ok, better to plot them then!
				_lastStates = _mySolver.getProblemSpace().states();
				fireRepaint();
			}

			@Override
			public void stepped(IBoundsManager boundsManager, int thisStep,
					int totalSteps)
			{
			}
		};

		solver.getSolutionGenerator().addReadyListener(_readyListener);
		solver.getContributions().addContributionsChangedListener(
				_contributionsListener);
		solver.getBoundsManager().addConstrainSpaceListener(_constrainListener);
	}

	protected void fireTrackShifted()
	{
		final WatchableList wl = this;
		Display.getDefault().asyncExec(new Runnable()
		{

			@Override
			public void run()
			{
				// if the current editor is a track data provider,
				// tell it that we've shifted
				final IWorkbench wb = PlatformUI.getWorkbench();
				final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				if (win != null)
				{
					final IWorkbenchPage page = win.getActivePage();
					final IEditorPart editor = page.getActiveEditor();
					final TrackDataProvider dataMgr = (TrackDataProvider) editor
							.getAdapter(TrackDataProvider.class);
					// is it one of ours?
					if (dataMgr != null)
					{
						{
							dataMgr.fireTrackShift((WatchableList) wl);
						}
					}
				}

			}
		});

	}

	@Override
	public void paint(CanvasType dest)
	{
		if (getVisible())
		{
			dest.setColor(_myColor);
			if (_lastStates != null)
			{
				if (_showLocationBounds)
					paintThese(dest, _lastStates);
			}

			dest.setColor(_myColor);
			if (_newRoutes != null)
			{
				paintThese(dest, _newRoutes);
			}
		}
	}

	private void paintThese(CanvasType dest, Collection<BoundedState> states)
	{
		// keep track of the leg name of the previous leg - we
		// use it to track which leg we're in.
		String lastName = null;

		// work through the location bounds
		for (Iterator<BoundedState> iterator = states.iterator(); iterator
				.hasNext();)
		{
			BoundedState thisS = iterator.next();

			// do some fancy tests for if users only want the
			// states that appear at leg ends
			boolean isLastOne = !iterator.hasNext();
			boolean isDifferentLeg = thisS.getMemberOf() != lastName;
			boolean isLegEnd = isLastOne || isDifferentLeg;

			boolean plotThisOne = !_onlyPlotLegEnds // users want all of them
					|| (_onlyPlotLegEnds && isLegEnd); // users only want
			// leg ends, and this is one

			if (plotThisOne && thisS.getLocation() != null)
			{
				// get the color for this state
				Color thisCol  = thisS.getColor();
				
				// do we have one? if not, use the color for the whole solution
				if(thisCol == null)
					thisCol = this.getColor();
				
				// ok, make the color a little darker
				Color newCol = thisCol.darker();
				dest.setColor(newCol);

				lastName = thisS.getMemberOf();

				LocationRange theLoc = thisS.getLocation();
				Coordinate[] pts = theLoc.getGeometry().getCoordinates();

				int[] xPoints = new int[pts.length];
				int[] yPoints = new int[pts.length];

				// collate polygon
				for (int i = 0; i < pts.length; i++)
				{
					Coordinate thisC = pts[i];
					WorldLocation thisLocation = conversions.toLocation(thisC);
					Point pt = dest.toScreen(thisLocation);
					xPoints[i] = pt.x;
					yPoints[i] = pt.y;
				}

				// fill in the polygons, if we can
				if (dest instanceof ExtendedCanvasType)
				{
					ExtendedCanvasType extended = (ExtendedCanvasType) dest;
					extended.semiFillPolygon(xPoints, yPoints, pts.length);
				}

				// and a border
				if (isLegEnd)
					dest.setLineStyle(CanvasType.SOLID);
				else
					dest.setLineStyle(CanvasType.DOTTED);

				dest.setLineWidth(0.0f);
				dest.drawPolygon(xPoints, yPoints, xPoints.length);
			}
		}
	}

	private void paintThese(CanvasType dest, CompositeRoute[] routes)
	{
		DoPaint painter = new DoPaint(dest);
		walkRoute(routes, painter);
	}

	private void walkRoute(CompositeRoute[] routes, RouteStepper stepper)
	{
		for (int i = 0; i < routes.length; i++)
		{
			CompositeRoute thisComposite = routes[i];
			Iterator<CoreRoute> legs = thisComposite.getLegs().iterator();

			while (legs.hasNext())
			{
				stepper.reset();
				CoreRoute thisRoute = legs.next();
				ArrayList<State> states = thisRoute.getStates();
				if (states != null)
				{
					Iterator<State> stateIter = states.iterator();
					while (stateIter.hasNext())
					{
						State thisState = stateIter.next();
						stepper.step(thisRoute, thisState);
					}
				}

				stepper.legComplete(thisRoute);

			}

		}
		stepper.finish();
	}

	private static interface RouteStepper
	{

		public abstract void step(CoreRoute thisRoute, State thisState);

		public abstract void legComplete(CoreRoute thisRoute);

		public abstract void finish();

		public abstract void reset();

	}

	private class DoPaint implements RouteStepper
	{
		private static final double LEG_LABEL_CLIPPING_THRESHOLD = 1.1;
		private Point lastPt = null;
		private final CanvasType _dest;
		private final float oldWid;

		public DoPaint(CanvasType dest)
		{
			_dest = dest;
			oldWid = _dest.getLineWidth();
			_dest.setLineWidth(5.0f);
		}

		@Override
		public void step(CoreRoute thisRoute, State thisState)
		{
			com.vividsolutions.jts.geom.Point loc = thisState.getLocation();
			// convert to screen
			WorldLocation wLoc = conversions.toLocation(loc.getCoordinate());

			Point screenPt = _dest.toScreen(wLoc);

			if (lastPt != null)
			{
				// is it straight? or altering
				if (thisRoute.getType() == LegType.STRAIGHT)
					_dest.setLineStyle(CanvasType.SOLID);
				else
					_dest.setLineStyle(CanvasType.DOTTED);

				// does this state have a color?
				if (thisState.getColor() != null)
					_dest.setColor(thisState.getColor());
				else
					_dest.setColor(_myColor);

				// draw the line
				_dest.drawLine(lastPt.x, lastPt.y, screenPt.x, screenPt.y);
			}

			lastPt = screenPt;
		}

		@Override
		public void reset()
		{
			lastPt = null;
		}

		@Override
		public void finish()
		{
			_dest.setLineWidth(oldWid);
			_dest.setLineStyle(CanvasType.SOLID);
		}

		@Override
		public void legComplete(CoreRoute thisRoute)
		{
			// is it straight? or altering
			if (thisRoute.getType() == LegType.STRAIGHT)
			{
				StraightRoute straight = (StraightRoute) thisRoute;

				// get the first point
				State firstState = thisRoute.getStates().get(0);
				State lastState = thisRoute.getStates().get(
						thisRoute.getStates().size() - 1);

				final Color theColor;

				if (firstState.getColor() != null)
					theColor = firstState.getColor();
				else
					theColor = Color.red;

				Font theFont = LEG_NAME_FONT;
				WorldLocation firstLoc = conversions.toLocation(firstState
						.getLocation().getCoordinate());
				WorldLocation lastLoc = conversions.toLocation(lastState.getLocation()
						.getCoordinate());

				CanvasTypeUtilities.drawLabelOnLine(_dest, thisRoute.getName(),
						theFont, theColor, firstLoc, lastLoc, LEG_LABEL_CLIPPING_THRESHOLD,
						true);

				final String vectorDescription = String.format("%.1f", new WorldSpeed(
						straight.getSpeed(), WorldSpeed.M_sec).getValueIn(WorldSpeed.Kts))
						+ " kts "
						+ String.format("%.0f",
								Math.toDegrees(MathUtils.normalizeAngle(straight.getCourse())))
						+ "\u00B0";

				CanvasTypeUtilities.drawLabelOnLine(_dest, vectorDescription, theFont,
						theColor, firstLoc, lastLoc, LEG_LABEL_CLIPPING_THRESHOLD, false);
			}

		}
	}

	@FireReformatted
	public void setShowLocationConstraints(boolean showLocationBounds)
	{
		_showLocationBounds = showLocationBounds;
	}

	public void setShowSolutions(boolean showSolutions)
	{
		_showSolutions = showSolutions;
	}

	@Override
	public void beingRemoved()
	{
		// get the manager
		ISolversManager mgr = SATC_Activator.getDefault().getService(
				ISolversManager.class, true);
		
		mgr.deactivateSolverIfActive(_mySolver);
	}

	/**
	 * the Solver has been populated from XML. Now we have to scan it, to make it
	 * visible as Debrief layers
	 */
	public void selfScan()
	{
		IContributions container = _mySolver.getContributions();
		SortedSet<BaseContribution> conts = container.getContributions();
		for (Iterator<BaseContribution> iterator = conts.iterator(); iterator
				.hasNext();)
		{
			BaseContribution baseC = (BaseContribution) iterator.next();
			ContributionWrapper wrapped = null;

			// we don't add analysis contributions
			if (!baseC.getDataType().equals(ContributionDataType.ANALYSIS))
			{
				if (baseC instanceof BearingMeasurementContribution)
				{
					BearingMeasurementContribution bmc = (BearingMeasurementContribution) baseC;
					wrapped = new BMC_Wrapper(bmc);
				}
				else if (baseC instanceof StraightLegForecastContribution)
				{
					wrapped = new StraightLegWrapper(baseC);
				}
				else if (baseC instanceof CourseForecastContribution)
				{
					wrapped = new CourseForecastWrapper(
							(CourseForecastContribution) baseC);
				}
				else
				{
					// we don't add analysis contributions - they're in there
					// already
					wrapped = new ContributionWrapper(baseC);
				}

				this.add(wrapped);
			}
		}
	}
	
	

	@Override
	@FireReformatted
	public void setName(String theName)
	{
		super.setName(theName);
		_mySolver.setName(theName);
		
		// also trigger a refresh in maintain contributions
		// get the manager
		ISolversManager mgr = SATC_Activator.getDefault().getService(
				ISolversManager.class, true);
		mgr.setActiveSolver(_mySolver);

	}

	@Override
	public void setLayers(Layers parent)
	{
		_myLayers = parent;
	}

	@Override
	public HiResDate getStartDTG()
	{
		return new HiResDate(_mySolver.getProblemSpace().getStartDate());
	}

	@Override
	public HiResDate getEndDTG()
	{
		return new HiResDate(_mySolver.getProblemSpace().getFinishDate());
	}

	@Override
	public Watchable[] getNearestTo(HiResDate DTG)
	{
		ArrayList<Watchable> items = new ArrayList<Watchable>();

		long time = DTG.getDate().getTime();

		// check if we have any solutions
		if ((_newRoutes != null) && (_newRoutes.length >= 0))
		{
			// ok, collate some data
			CompositeRoute route = _newRoutes[0];

			Iterator<CoreRoute> legs = route.getLegs().iterator();
			while (legs.hasNext())
			{
				CoreRoute thisLeg = (CoreRoute) legs.next();
				Iterator<State> states = thisLeg.getStates().iterator();
				while (states.hasNext())
				{
					State state = (State) states.next();
					// does it even have a location?
					if (state.getLocation() != null)
					{
						if (state.getTime().getTime() > time)
						{

							items.add(wrapThis(state));
							return items.toArray(new Watchable[]
							{});
						}
					}
				}
			}

		}

		return items.toArray(new Watchable[]
		{});
	}

	private WrappedState wrapThis(final State state)
	{
		return new WrappedState(state);
	}

	@Override
	public void filterListTo(HiResDate start, HiResDate end)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<Editable> getItemsBetween(HiResDate start, HiResDate end)
	{
		Collection<Editable> items = new ArrayList<Editable>();
		ArrayList<State> states = new ArrayList<State>();

		long startT = start.getDate().getTime();
		long finishT = end.getDate().getTime();

		// check if we have any solutions
		if ((_newRoutes != null) && (_newRoutes.length >= 0))
		{
			// ok, collate some data
			CompositeRoute route = _newRoutes[0];

			Iterator<CoreRoute> legs = route.getLegs().iterator();
			while (legs.hasNext())
			{
				CoreRoute thisLeg = (CoreRoute) legs.next();
				Iterator<State> theStates = thisLeg.getStates().iterator();
				while (theStates.hasNext())
				{
					State state = (State) theStates.next();

					// does it even have a location?
					if (state.getLocation() != null)
					{
						long thisTime = state.getTime().getTime();
						if ((thisTime >= startT) && (thisTime <= finishT))
						{
							// check we haven't just stored a state at this
							// time,
							// JFReeChart plotting doesn't like it.
							if (states.size() > 0)
							{
								Date lastTime = states.get(states.size() - 1).getTime();
								if (lastTime.getTime() != thisTime)
								{
									states.add(state);
								}
							}
							else
								states.add(state);
						}
					}
				}
			}
		}

		// ok, wrap the states
		Iterator<State> iter = states.iterator();
		while (iter.hasNext())
		{
			State state = (State) iter.next();
			items.add(wrapThis(state));
		}

		Collection<Editable> res = null;
		if (items.size() > 0)
		{
			res = items;
		}
		return res;
	}

	@Override
	public PlainSymbol getSnailShape()
	{
		if (mySymbol == null)
			mySymbol = SymbolFactory.createSymbol(SymbolFactory.DEFAULT_SYMBOL_TYPE);

		return mySymbol;
	}

	/**
	 * convert this solution into a formal track
	 * 
	 */
	public void convertToLegs()
	{
		// check if we have any solutions
		if ((_newRoutes == null) || (_newRoutes.length == 0))
		{
			CorePlugin.errorDialog("Convert solution to track",
					"Sorry, this solution contains no generated routes");
		}
		else
		{

			for (int i = 0; i < _newRoutes.length; i++)
			{
				CompositeRoute thisR = _newRoutes[i];

				// the output track
				TrackWrapper newT = new TrackWrapper();
				newT.setName(getName() + "_" + i);

				Iterator<CoreRoute> legs = thisR.getLegs().iterator();
				while (legs.hasNext())
				{
					CoreRoute thisLeg = (CoreRoute) legs.next();
					if (thisLeg instanceof StraightRoute)
					{
						StraightRoute straight = (StraightRoute) thisLeg;

						// ok - produce a TMA leg
						double courseDegs = Math.toDegrees(straight.getCourse());
						WorldSpeed speed = new WorldSpeed(straight.getSpeed(),
								WorldSpeed.M_sec);
						WorldLocation origin = conversions.toLocation(straight
								.getStartPoint().getCoordinate());
						HiResDate startTime = new HiResDate(straight.getStartTime()
								.getTime());
						HiResDate endTime = new HiResDate(straight.getEndTime().getTime());

						AbsoluteTMASegment abs = new AbsoluteTMASegment(courseDegs, speed,
								origin, startTime, endTime);
						abs.setName(straight.getName());
						newT.add(abs);
						abs.setName(straight.getName());
					}
					else if (thisLeg instanceof AlteringRoute)
					{
						AlteringRoute altering = (AlteringRoute) thisLeg;

						TrackSegment segment = new TrackSegment();
						segment.setName(altering.getName());

						ArrayList<State> states = altering.getStates();
						for (State thisS : states)
						{
							double theCourse = thisS.getCourse();
							WorldSpeed theSpeed = new WorldSpeed(thisS.getSpeed(),
									WorldSpeed.M_sec);
							WorldLocation theLocation = conversions.toLocation(thisS
									.getLocation().getCoordinate());
							HiResDate theTime = new HiResDate(thisS.getTime().getTime());

							Fix theFix = new Fix(theTime, theLocation, theCourse,
									theSpeed.getValueIn(WorldSpeed.ft_sec) / 3d);
							FixWrapper newFix = new FixWrapper(theFix);
							segment.addFix(newFix);
						}

						// make it dotted, that's our way of doing it.
						segment.setLineStyle(CanvasType.DOTTED);

						newT.add(segment);
					}
					else
						DebriefPlugin.logError(Status.ERROR,
								"Unexpected type of route encountered:" + thisLeg, null);
				}

				// and store it
				_myLayers.addThisLayer(newT);

			}
		}
	}

	protected static class WrappedState implements Watchable, Editable
	{

		private final State state;
		private WorldLocation loc;
		private boolean isVis = true;

		public WrappedState(State state)
		{
			this.state = state;
		}

		@Override
		public WorldLocation getLocation()
		{
			if (loc == null)
				loc = conversions.toLocation(state.getLocation().getCoordinate());

			return loc;
		}

		@Override
		public double getCourse()
		{
			return state.getCourse();
		}

		@Override
		public double getSpeed()
		{
			return MWC.Algorithms.Conversions.Mps2Kts(state.getSpeed());
		}

		@Override
		public double getDepth()
		{
			return 0;
		}

		@Override
		public WorldArea getBounds()
		{
			return new WorldArea(getLocation(), getLocation());
		}

		@Override
		public void setVisible(boolean val)
		{
			isVis = val;
		}

		@Override
		public boolean getVisible()
		{
			return isVis;
		}

		@Override
		public HiResDate getTime()
		{
			return new HiResDate(state.getTime().getTime());
		}

		@Override
		public String getName()
		{
			return DebriefFormatDateTime.toString(state.getTime().getTime());
		}

		@Override
		public Color getColor()
		{
			return state.getColor();
		}

		@Override
		public boolean hasEditor()
		{
			return false;
		}

		@Override
		public EditorType getInfo()
		{
			return null;
		}

	}

}
