package org.mwc.debrief.satc_interface.data;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;

import org.eclipse.core.runtime.IStatus;
import org.jfree.util.ReadOnlyIterator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.satc_interface.data.wrappers.BMC_Wrapper;
import org.mwc.debrief.satc_interface.data.wrappers.ContributionWrapper;
import org.mwc.debrief.satc_interface.utilities.conversions;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.Conversions;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.Layers;
import MWC.GUI.Layers.NeedsToKnowAboutLayers;
import MWC.GUI.NeedsToBeInformedOfRemove;
import MWC.GUI.SupportsPropertyListeners;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.contributions.ContributionDataType;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.generator.IConstrainSpaceListener;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IContributionsChangedListener;
import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.generator.impl.SwitchableSolutionGenerator;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.manager.ISolversManager;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.vividsolutions.jts.geom.Coordinate;

public class SATC_Solution extends BaseLayer implements
		NeedsToBeInformedOfRemove, NeedsToKnowAboutLayers
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
						prop("ShowLocationBounds", "whether to display location bounds",
								FORMAT),
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
			{ method(c, "convertToTrack", null, "Convert to track") };

			return mds;
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ISolver _mySolver;

	private Color _myColor = Color.green;

	private Layers _myLayers = null;

	private boolean _showLocationBounds = false;

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
			TrackGenerator genny = new TrackGenerator("T-" + this.getName());
			walkRoute(_newRoutes, genny);

			// we should now have a track
			TrackWrapper newT = genny.getTrack();
			_myLayers.addThisLayer(newT);
		}
	}

	private static class TrackGenerator implements RouteStepper
	{

		private TrackWrapper _myTrack;

		public TrackGenerator(String name)
		{
			_myTrack = new TrackWrapper();
			_myTrack.setName(name);
		}

		@Override
		public void step(State thisState)
		{
			// ok, convert the state to a fix
			Fix theF = produceFix(thisState);

			// and wrap it
			FixWrapper thisF = new FixWrapper(theF);

			// put the DTG into the label
			thisF.resetName();

			// and store it.
			_myTrack.addFix(thisF);
		}

		private Fix produceFix(State thisState)
		{
			com.vividsolutions.jts.geom.Point loc = thisState.getLocation();
			// convert to screen
			WorldLocation wLoc = conversions.toLocation(loc.getCoordinate());
			double theCourse = thisState.getCourse();
			double theSpeedKts = Conversions.Mps2Kts(thisState.getSpeed());
			double theSpeedYps = Conversions.Kts2Yps(theSpeedKts);
			HiResDate theTime = new HiResDate(thisState.getTime().getTime());

			Fix theF = new Fix(theTime, wLoc, theCourse, theSpeedYps);
			return theF;
		}

		public TrackWrapper getTrack()
		{
			return _myTrack;
		}

		@Override
		public void reset()
		{
			// TODO Auto-generated method stub

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

	@Override
	public EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new SATC_Info(this);

		return _myEditor;
	}

	public Color getColor()
	{
		return _myColor;
	}

	public void setColor(Color color)
	{
		this._myColor = color;
	}

	public boolean getShowLocationBounds()
	{
		return _showLocationBounds;
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

	@Override
	public void paint(CanvasType dest)
	{
		dest.setColor(_myColor);
		if (getVisible())
		{
			if (_lastStates != null)
			{
				if (_showLocationBounds)
					paintThese(dest, _lastStates);
			}

			if (_newRoutes != null)
			{
				paintThese(dest, _newRoutes);
			}
		}
	}

	private void paintThese(CanvasType dest, Collection<BoundedState> states)
	{
		for (Iterator<BoundedState> iterator = states.iterator(); iterator
				.hasNext();)
		{
			BoundedState thisS = iterator.next();
			if (thisS.getLocation() != null)
			{
				LocationRange theLoc = thisS.getLocation();
				Coordinate[] pts = theLoc.getGeometry().getCoordinates();
				Point lastPt = null;
				for (int i = 0; i < pts.length; i++)
				{
					Coordinate thisC = pts[i];
					WorldLocation thisLocation = conversions.toLocation(thisC);
					Point pt = dest.toScreen(thisLocation);

					if (lastPt != null)
					{
						dest.drawLine(lastPt.x, lastPt.y, pt.x, pt.y);
					}
					lastPt = new Point(pt);
				}
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
			CompositeRoute thisR = routes[i];
			Iterator<CoreRoute> legs = thisR.getLegs().iterator();

			while (legs.hasNext())
			{
				stepper.reset();
				CoreRoute thisR2 = legs.next();
				ArrayList<State> states = thisR2.getStates();
				if (states != null)
				{
					Iterator<State> stateIter = states.iterator();
					while (stateIter.hasNext())
					{
						State thisState = stateIter.next();
						stepper.step(thisState);
					}
				}
			}
		}
	}

	private static interface RouteStepper
	{

		public abstract void step(State thisState);

		public abstract void reset();

	}

	private static class DoPaint implements RouteStepper
	{
		private Point lastPt = null;
		private final CanvasType _dest;

		public DoPaint(CanvasType dest)
		{
			_dest = dest;
		}

		@Override
		public void step(State thisState)
		{
			com.vividsolutions.jts.geom.Point loc = thisState.getLocation();
			// convert to screen
			WorldLocation wLoc = conversions.toLocation(loc.getCoordinate());

			Point screenPt = _dest.toScreen(wLoc);

			if (lastPt != null)
			{
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
	}

	@FireReformatted
	public void setShowLocationBounds(boolean showLocationBounds)
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
		@SuppressWarnings("unused")
		ISolversManager mgr = SATC_Activator.getDefault().getService(
				ISolversManager.class, true);

		// ok, better tell the manager that we're being removed
		// TODO: replace next line once the capability is in SATC
		// mgr.solverRemoved(_mySolver);
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
			if (baseC instanceof BearingMeasurementContribution)
			{
				BearingMeasurementContribution bmc = (BearingMeasurementContribution) baseC;
				wrapped = new BMC_Wrapper(bmc);
			}
			else
			{
				// we don't add analysis contributions - they're in there already
				if (!baseC.getDataType().equals(ContributionDataType.ANALYSIS))
					wrapped = new ContributionWrapper(baseC);
			}

			this.add(wrapped);

		}
	}

	@Override
	public void setLayers(Layers parent)
	{
		_myLayers = parent;
	}

}
