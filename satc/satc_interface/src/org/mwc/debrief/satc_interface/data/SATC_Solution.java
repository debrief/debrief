package org.mwc.debrief.satc_interface.data;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.Status;
import org.jfree.util.ReadOnlyIterator;
import org.mwc.debrief.satc_interface.data.wrappers.BMC_Wrapper;
import org.mwc.debrief.satc_interface.data.wrappers.ContributionWrapper;
import org.mwc.debrief.satc_interface.utilities.conversions;

import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.SupportsPropertyListeners;
import MWC.GenericData.WorldLocation;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.BearingMeasurementContribution;
import com.planetmayo.debrief.satc.model.generator.IBoundsManager;
import com.planetmayo.debrief.satc.model.generator.IConstrainSpaceListener;
import com.planetmayo.debrief.satc.model.generator.IContributionsChangedListener;
import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.State;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.vividsolutions.jts.geom.Coordinate;

public class SATC_Solution extends BaseLayer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ISolver _mySolver;

	private boolean _showLocationBounds = true;

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

	public SATC_Solution(String solName)
	{
		super.setName(solName);

		_mySolver = createSolver();

		// clear the solver, just to be sure
		_mySolver.getContributions().clear();

		listenToSolver(_mySolver);

	}

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

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				PropertyDescriptor[] res =
				{
						prop("ShowLocationBounds", "whether to display location bounds",
								FORMAT),
						prop("Visible", "whether to plot this solution", VISIBILITY) };

				return res;
			}
			catch (IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}

	private void listenToSolver(ISolver solver)
	{
		solver.getSolutionGenerator().addReadyListener(
				new IGenerateSolutionsListener()
				{

					@Override
					public void startingGeneration()
					{
						// ditch any existing routes
						_newRoutes = null;
					}

					@Override
					public void solutionsReady(CompositeRoute[] routes)
					{
						_newRoutes = routes;

						// hey, trigger repaint
						fireRepaint();
					}

					@Override
					public void finishedGeneration(Throwable error)
					{
					}
				});

		solver.getContributions().addContributionsChangedListener(
				new IContributionsChangedListener()
				{

					@Override
					public void removed(BaseContribution contribution)
					{

						// hey, are we still storing this?
						boolean idiotTest_DidWeFindIt = false;

						// get read-only version of elements
						ReadOnlyIterator rIter = new ReadOnlyIterator(getData().iterator());
						while (rIter.hasNext())
						{
							Editable editable = (Editable) rIter.next();
							ContributionWrapper bc = (ContributionWrapper) editable;
							if (bc.getContribution() == contribution)
							{
								removeElement(bc);
								idiotTest_DidWeFindIt = true;
							}
						}

						fireRepaint();

						// just check that we contained the contribution
						if (!idiotTest_DidWeFindIt)
						{
							SATC_Activator
									.log(
											Status.ERROR,
											"We were asked to remove a contribution, but we didn't have it stored in the Layer",
											null);
						}

					}

					@Override
					public void added(BaseContribution contribution)
					{
						fireRepaint();
					}
				});

		solver.getBoundsManager().addConstrainSpaceListener(
				new IConstrainSpaceListener()
				{
					@Override
					public void stepped(IBoundsManager boundsManager, int thisStep,
							int totalSteps)
					{
					}

					@Override
					public void statesBounded(IBoundsManager boundsManager)
					{
						// ok, better to plot them then!
						_lastStates = _mySolver.getProblemSpace().states();

						fireRepaint();
					}

					@Override
					public void restarted(IBoundsManager boundsManager)
					{
						_lastStates = null;
						_newRoutes = null;
					}

					@Override
					public void error(IBoundsManager boundsManager,
							IncompatibleStateException ex)
					{
						_lastStates = null;
					}
				});
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

	@Override
	public boolean hasOrderedChildren()
	{
		return true;
	}

	@Override
	public void paint(CanvasType dest)
	{
		dest.setColor(Color.green);
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

	private void paintThese(CanvasType dest, CompositeRoute[] _newRoutes2)
	{
		dest.setColor(Color.yellow);
		for (int i = 0; i < _newRoutes2.length; i++)
		{
			CompositeRoute thisR = _newRoutes2[i];
			Iterator<CoreRoute> legs = thisR.getLegs().iterator();
			Point lastPt = null;

			while (legs.hasNext())
			{
				CoreRoute thisR2 = (CoreRoute) legs.next();
				ArrayList<State> states = thisR2.getStates();
				Iterator<State> stateIter = states.iterator();
				while (stateIter.hasNext())
				{
					State thisState = (State) stateIter.next();
					com.vividsolutions.jts.geom.Point loc = thisState.getLocation();
					// convert to screen
					WorldLocation wLoc = conversions.toLocation(loc.getCoordinate());
					Point screenPt = dest.toScreen(wLoc);

					if (lastPt != null)
					{
						// draw the line
						dest.drawLine(lastPt.x, lastPt.y, screenPt.x, screenPt.y);
					}

					lastPt = screenPt;
				}
			}
		}
	}

	private void paintThese(CanvasType dest, Collection<BoundedState> states)
	{
		for (Iterator<BoundedState> iterator = states.iterator(); iterator
				.hasNext();)
		{
			BoundedState thisS = (BoundedState) iterator.next();
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

	public boolean getShowLocationBounds()
	{
		return _showLocationBounds;
	}

	@FireReformatted
	public void setShowLocationBounds(boolean showLocationBounds)
	{
		_showLocationBounds = showLocationBounds;
	}

	@Override
	public boolean hasEditor()
	{
		return true;
	}

	@Override
	public EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new SATC_Info(this);

		return _myEditor;
	}

	protected void fireRepaint()
	{
		super.firePropertyChange(SupportsPropertyListeners.FORMAT, null, this);
	}

	private ISolver createSolver()
	{
		return SATC_Activator.getDefault().getService(ISolver.class, true);
	}

	public void addContribution(BaseContribution cont)
	{
		_mySolver.getContributions().addContribution(cont);

		System.err.println("adding:" + cont.getName());

		ContributionWrapper thisW;
		if (cont instanceof BearingMeasurementContribution)
			thisW = new BMC_Wrapper((BearingMeasurementContribution) cont);
		else
			thisW = new ContributionWrapper(cont);
		super.add(thisW);
	}

	@Override
	public void removeElement(Editable p)
	{
		// ok, ditch this element
		super.removeElement(p);

		// get the actual contribution
		ContributionWrapper cw = (ContributionWrapper) p;
		BaseContribution bc = cw.getContribution();
		_mySolver.getContributions().removeContribution(bc);
	}

	public ISolver getSolver()
	{
		return _mySolver;
	}

}
