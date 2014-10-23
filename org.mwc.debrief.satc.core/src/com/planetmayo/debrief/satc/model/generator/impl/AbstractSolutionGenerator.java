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
package com.planetmayo.debrief.satc.model.generator.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.generator.IJobsManager;
import com.planetmayo.debrief.satc.model.generator.ISolutionGenerator;
import com.planetmayo.debrief.satc.model.legs.AlteringLeg;
import com.planetmayo.debrief.satc.model.legs.AlteringRoute;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;
import com.vividsolutions.jts.geom.Geometry;

public abstract class AbstractSolutionGenerator implements ISolutionGenerator
{
	/**
	 * utility class, uses to store a set of states in increasing score order
	 * 
	 * @author ian
	 * 
	 */
	private static class ScoredState implements Comparable<ScoredState>
	{
		final Double _overlap;
		final BoundedState _state;

		public ScoredState(double area, BoundedState boundedState)
		{
			_overlap = area;
			_state = boundedState;
		}

		@Override
		public int compareTo(ScoredState o)
		{
			return _overlap.compareTo(o._overlap);
		}

		public BoundedState getState()
		{
			return _state;
		}
	}

	/**
	 * pass through the cuts, and suppress the least significant
	 * 
	 * @param states
	 *          the set of states to process
	 * @param targetSize
	 *          the size we should aim for
	 */
	public static Collection<BoundedState> suppressCuts(
			final Collection<BoundedState> states, final int targetSize)
	{
		// maintain flag for if it's feasible to get to
		// the target size. There are lots of states that
		// aren't available to be culled, so it may not
		// be able to get to the target size
		boolean feasible = true;

		while (states.size() > targetSize && feasible)
		{

			// ok, find the
			final SortedSet<ScoredState> sortedStates = getCandidatesForSuppression(states);

			// did we get any?
			if (sortedStates.isEmpty())
			{
				feasible = false;
			}
			else
			{

				// ok - we should decided on some states to ditch
				final Iterator<ScoredState> sIter = sortedStates.iterator();

				int numAvailable = sortedStates.size();
				int numStates = states.size();
				int numRemainingToDelete = numStates - targetSize;
				int ToDelete = (int) (numAvailable * 0.2);
				ToDelete = Math.max(ToDelete, 1);
				ToDelete = Math.min(ToDelete, numRemainingToDelete);

				// do we have enough to remove?

				// loop through, ditching them
				int deleted = 0;
				while (deleted < ToDelete)
				{
					final ScoredState scoredState = (ScoredState) sIter.next();
					states.remove(scoredState.getState());
					deleted++;
				}

				// did we get to the point where there was only one cut available,
				// and we've just ditched it?
				if (numAvailable == 1)
				{
					// ok, we can't go any further.
					// God Damn, it's like blood out of a stone,
					// she just can't take any more!
					feasible = false;
				}
			}

		}
		return states;
	}

	private static SortedSet<ScoredState> getCandidatesForSuppression(
			final Collection<BoundedState> states)
	{
		// collate a list to store the overlap-ordered states
		final SortedSet<ScoredState> sortedStates = new TreeSet<ScoredState>();

		// remember the last state
		BoundedState lastState = null;
		ScoredState statesToDitch = null;

		Iterator<BoundedState> iter = states.iterator();
		while (iter.hasNext())
		{
			// get the next state
			final BoundedState boundedState = (BoundedState) iter.next();

			// do we have a previous set?
			if (lastState != null)
			{
				// does this state have location bounds?
				if (boundedState.getLocation() != null)
				{
					// ok, are in the same leg as the previous?
					if (boundedState.getMemberOf() == lastState.getMemberOf())
					{
						// ok - we're in the same leg - what's the overlap
						final Geometry overlap = boundedState.getLocation().getGeometry()
								.intersection(lastState.getLocation().getGeometry());
						final double area = overlap.getArea();
						statesToDitch = new ScoredState(-area, boundedState);
						sortedStates.add(statesToDitch);
					}
					else
					{
						// we've just changed leg. Make sure the previous state isn't a
						// candidate
						// for removal
						if (statesToDitch != null)
						{
							sortedStates.remove(statesToDitch);
						}
					}
				}
			}

			// does this state have a location bounds?
			if (boundedState.getLocation() != null)
			{
				lastState = boundedState;
			}
		}

		// ok - remove the last state from the list - we need it
		if (statesToDitch != null)
		{
			sortedStates.remove(statesToDitch);
		}
		
		return sortedStates;
	}

	protected final IContributions contributions;

	protected final IJobsManager jobsManager;

	protected final SafeProblemSpace problemSpaceView;

	/**
	 * how precisely to do the calcs
	 * 
	 */
	protected Precision _myPrecision = Precision.LOW;

	/**
	 * anybody interested in a new solution being ready?
	 * 
	 */
	protected final Set<IGenerateSolutionsListener> _readyListeners;

	/**
	 * whether the algorithm is allowed to suppress insignifican cuts
	 * 
	 */
	private boolean _autoSuppress = true;

	public AbstractSolutionGenerator(IContributions contributions,
			IJobsManager jobsManager, SafeProblemSpace problemSpace)
	{
		this.jobsManager = jobsManager;
		this.contributions = contributions;
		this.problemSpaceView = problemSpace;
		_readyListeners = Collections
				.synchronizedSet(new HashSet<IGenerateSolutionsListener>());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.planetmayo.debrief.satc.model.generator.ISolutionGenerator#addReadyListener
	 * (com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener)
	 */
	@Override
	public void addReadyListener(IGenerateSolutionsListener listener)
	{
		_readyListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.planetmayo.debrief.satc.model.generator.ISolutionGenerator#
	 * removeReadyListener
	 * (com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener)
	 */
	@Override
	public void removeReadyListener(IGenerateSolutionsListener listener)
	{
		_readyListeners.remove(listener);
	}

	private Set<IGenerateSolutionsListener> cloneListeners()
	{
		Set<IGenerateSolutionsListener> listeners = new HashSet<IGenerateSolutionsListener>();
		synchronized (_readyListeners)
		{
			listeners.addAll(_readyListeners);
		}
		return listeners;
	}

	/**
	 * we've sorted out the leg scores
	 * 
	 * @param theLegs
	 * 
	 */
	protected void fireStartingGeneration()
	{
		for (IGenerateSolutionsListener listener : cloneListeners())
		{
			listener.startingGeneration();
		}
	}

	/**
	 * we've sorted out the leg scores
	 * 
	 * @param theLegs
	 * 
	 */
	protected void fireFinishedGeneration(Throwable error)
	{
		for (IGenerateSolutionsListener listener : cloneListeners())
		{
			listener.finishedGeneration(error);
		}
	}

	/**
	 * we have some solutions
	 * 
	 * @param routes
	 * 
	 */
	protected void fireSolutionsReady(CompositeRoute[] routes)
	{
		for (IGenerateSolutionsListener listener : cloneListeners())
		{
			listener.solutionsReady(routes);
		}
	}

	@Override
	public void setPrecision(Precision precision)
	{
		_myPrecision = precision;
	}

	@Override
	public Precision getPrecision()
	{
		return _myPrecision;
	}

	/**
	 * whether insignificant cuts should be suppressed (only in mid-low)
	 * 
	 * @param autoSuppress
	 *          yes/no
	 */
	public void setAutoSuppress(boolean autoSuppress)
	{
		_autoSuppress = autoSuppress;
	}

	/**
	 * whether insignificant cuts should be suppressed (only in mid-low)
	 * 
	 * @return yes/no
	 */
	public boolean getAutoSuppress()
	{
		return _autoSuppress;
	}

	/**
	 * extract a set of legs from the space
	 * 
	 * @param space
	 * @return
	 */
	protected List<CoreLeg> getTheLegs(Collection<BoundedState> theStates,
			IProgressMonitor monitor) throws InterruptedException
	{

		// extract the straight legs
		ArrayList<CoreLeg> theLegs = new ArrayList<CoreLeg>();

		CoreLeg currentLeg = null;

		// remember the last state, since end the first/last items in a straight leg
		// are also in the altering
		// leg before/after them
		BoundedState previousState = null;

		// increementing counter, to number turns
		int counter = 1;

		for (BoundedState thisS : theStates)
		{
			if (monitor.isCanceled())
			{
				throw new InterruptedException();
			}
			if (thisS.getLocation() == null)
			{
				// leg algorithms work with location polygons so we don't need
				// to consider states which doesn't have locations here
				continue;
			}
			String thisLegName = thisS.getMemberOf();

			// is this the current leg?
			if (thisLegName != null)
			{
				// right - this is a state that is part of a straight leg

				// ok, do we have a straight leg for this name
				CoreLeg newLeg = findLeg(thisLegName, theLegs);

				// are we already in this leg?
				if (newLeg == null)
				{
					// right, we're just starting a straight leg. this state also goes on
					// the end
					// of the previous altering leg
					if (currentLeg != null)
					{
						if (currentLeg.getType() == LegType.ALTERING)
						{
							// ok, add this state to the previous altering leg
							currentLeg.add(thisS);
						}
						else
						{
							throw new RuntimeException(
									"A straight leg can only follow an altering leg - some problem here");
						}
					}

					// ok, now go for the straight leg
					currentLeg = new StraightLeg(thisLegName);
					theLegs.add(currentLeg);
				}
			}
			else
			{
				// a leg with no name = must be altering

				// were we in a straight leg?
				if (currentLeg != null)
				{
					if (currentLeg.getType() == LegType.STRAIGHT)
					{
						// ok, the straight leg is now complete. trigger a new altering leg
						currentLeg = null;
					}
				}

				// ok, are we currently in a leg?
				if (currentLeg == null)
				{
					String thisName = "Alteration " + counter++;
					currentLeg = new AlteringLeg(thisName);
					theLegs.add(currentLeg);

					// but, we need to start this altering leg with the previous state, if
					// there was one
					if (previousState != null)
						currentLeg.add(previousState);
				}
			}

			// ok, we've got the leg - now add the state
			if (currentLeg == null)
				LogFactory.getLog().error(
						"Logic problem, currentLeg should not be null");
			else
				currentLeg.add(thisS);

			// and remember it
			previousState = thisS;
		}
		return theLegs;
	}

	private CoreLeg findLeg(String thisLegName, ArrayList<CoreLeg> theLegs)
	{
		CoreLeg res = null;

		for (Iterator<CoreLeg> iterator = theLegs.iterator(); iterator.hasNext();)
		{
			CoreLeg coreLeg = (CoreLeg) iterator.next();
			if (coreLeg.getName().equals(thisLegName))
			{
				res = coreLeg;
				break;
			}
		}
		return res;
	}

	protected List<CoreRoute> generateAlteringRoutes(
			Collection<CoreRoute> straightRoutes)
	{
		List<CoreRoute> result = new ArrayList<CoreRoute>();
		if (straightRoutes.isEmpty())
		{
			return result;
		}
		Iterator<CoreRoute> iterator = straightRoutes.iterator();
		StraightRoute before = null;
		StraightRoute after = (StraightRoute) iterator.next();
		while (after == null && iterator.hasNext())
		{
			after = (StraightRoute) iterator.next();
		}
		while (iterator.hasNext())
		{
			StraightRoute nxt = (StraightRoute) iterator.next();
			if (nxt == null)
			{
				continue;
			}
			before = after;
			after = nxt;
			AlteringRoute altering = new AlteringRoute("", before.getEndPoint(),
					before.getEndTime(), after.getStartPoint(), after.getStartTime());
			altering.constructRoute(before, after);
			altering.generateSegments(problemSpaceView.getBoundedStatesBetween(
					before.getEndTime(), after.getStartTime()));
			result.add(before);
			result.add(altering);
		}
		result.add(after);
		return result;
	}

	@Override
	public SafeProblemSpace getProblemSpace()
	{
		return problemSpaceView;
	}
}
