package com.planetmayo.debrief.satc.model.generator.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IProgressMonitor;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.generator.IJobsManager;
import com.planetmayo.debrief.satc.model.generator.ISolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.exceptions.GenerationException;
import com.planetmayo.debrief.satc.model.generator.jobs.Job;
import com.planetmayo.debrief.satc.model.legs.AlteringLeg;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;

public class SolutionGenerator implements ISolutionGenerator
{
	private static final String SOLUTION_GENERATOR_JOBS_GROUP = "solutionGeneratorGroup";

	private final IContributions contributions;
	
	private final IJobsManager jobsManager;
	
	private final SafeProblemSpace problemSpaceView;
	
	/**
	 * anybody interested in a new solution being ready?
	 * 
	 */
	final private Set<IGenerateSolutionsListener> _readyListeners;

	/**
	 * the current set of legs
	 * 
	 */
	private ArrayList<CoreLeg> _theLegs;

	/**
	 * how precisely to do the calcs
	 * 
	 */
	private Precision _myPrecision = Precision.LOW;
	
	private volatile Job<?, ?> mainGenerationJob = null;

	public SolutionGenerator(IContributions contributions, IJobsManager jobsManager, SafeProblemSpace problemSpace)
	{
		this.jobsManager = jobsManager;
		this.contributions = contributions;
		this.problemSpaceView = problemSpace;
		_readyListeners = Collections.newSetFromMap(
				new ConcurrentHashMap<IGenerateSolutionsListener, Boolean>()); 
	}
	
	private synchronized void startRecalculateTopLegsJobs() 
	{
		mainGenerationJob = jobsManager.schedule(new SolutionGeneratorJob<Void, Void>("Recalculate Top Legs") {

			@Override
			protected <E> Void doRun(IProgressMonitor monitor,
					Job<Void, E> previous) throws InterruptedException
			{
				recalculateTopLegs(monitor);
				return null;
			}

			@Override
			protected void onComplete()
			{
				fireFinishedGeneration(getException());
				synchronized (SolutionGenerator.this) 
				{
					mainGenerationJob = null;
				}
			}			
		});
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

	@Override
	public synchronized void generateSolutions(boolean fullRerun)
	{
		if (mainGenerationJob != null) 
		{
			return;
		}
		System.out.println("running generator at:" + new Date());

		// spread the good news
		fireStartingGeneration();

		// clear the legs
		if (_theLegs != null)
		{
			if (! fullRerun) 
			{
				startRecalculateTopLegsJobs();
				return; 
			}
			else 
			{
				_theLegs.clear();
			}			
		}

		// get the legs (JOB)
		Job<Void, Void> getLegsJob = jobsManager.schedule(new SolutionGeneratorJob<Void, Void>("Get the legs")
		{

			@Override
			public <E> Void doRun(IProgressMonitor monitor, Job<Void, E> previous) throws InterruptedException
			{
				_theLegs = getTheLegs(problemSpaceView.states(), monitor);
				return null;
			}
		});
		

		Job<Void, Void> generateRoutesJob = jobsManager.scheduleAfter(new SolutionGeneratorJob<Void, Void>("Generate routes") 
		{

			@Override
			public <E> Void doRun(IProgressMonitor monitor, Job<Void, E> previous) throws InterruptedException
			{
				generateRoutes(_theLegs, monitor);
				return null;
			}			
		}, getLegsJob);
		
		Job<Void, Void> decideAchievableJob = jobsManager.scheduleAfter(new SolutionGeneratorJob<Void, Void>("Decide achievable routes") 
		{

			@Override
			public <E> Void doRun(IProgressMonitor monitor, Job<Void, E> previous) throws InterruptedException
			{
				decideAchievable(_theLegs, monitor);
				return null;
			}			
		}, generateRoutesJob);		
		
		Job<int[][], Void> achievableResJob = jobsManager.scheduleAfter(new SolutionGeneratorJob<int[][], Void>("achievableRes") 
		{

			@Override
			public <E> int[][] doRun(IProgressMonitor monitor, Job<Void, E> previous) throws InterruptedException
			{
				return calculateAchievableRoutesFor(_theLegs, monitor);
			}			
		}, decideAchievableJob);
		
		Job<Void, int[][]> cancelUnachievableJob = jobsManager.scheduleAfter(new SolutionGeneratorJob<Void, int[][]>("Cancel unachievable routes") 
		{

			@Override
			public <E> Void doRun(IProgressMonitor monitor, Job<int[][], E> previous) throws InterruptedException
			{
				cancelUnachievable(_theLegs, previous.getResult(), monitor);
				return null;
			}			
		}, achievableResJob);				

		mainGenerationJob = jobsManager.scheduleAfter(new SolutionGeneratorJob<Void, Void>("Recalculate top legs") 
		{

			@Override
			public <E> Void doRun(IProgressMonitor monitor, Job<Void, E> previous) throws InterruptedException
			{
				recalculateTopLegs(monitor);
				System.out.println(" - generator complete at:" + new Date());
				return null;
			}

			@Override
			protected void onComplete()
			{
				fireFinishedGeneration(getException());
				synchronized (SolutionGenerator.this) 
				{
					mainGenerationJob = null;
				}
			}
		}, cancelUnachievableJob);
		if (mainGenerationJob != null && mainGenerationJob.isComplete()) 
		{
			mainGenerationJob = null;
		}
	}

	/**
	 * calculate the top performer. this method is refactored on its own since it
	 * may get called when an estimate has changed - so the algorithm doesn't need
	 * to re-do all the leg definition bits
	 */
	void recalculateTopLegs(IProgressMonitor monitor) throws InterruptedException
	{
		// just check we have data
		if (_theLegs == null)
			return;

		// score the possible routes
		calculateRouteScores(contributions.getContributions(), _theLegs, monitor);

		// share the news
		fireLegsScored(_theLegs);

		// generate some candidate solutions
		CompositeRoute[] routes = generateCandidates(_theLegs, monitor);

		// and we're done, share the good news!
		fireSolutionsReady(routes);
	}
	
	

	@Override
	public SafeProblemSpace getProblemSpace()
	{
		return problemSpaceView;
	}

	/**
	 * for the set of generated routes, work out which have the highest score
	 * 
	 * @param boundsManager
	 *          the c
	 * @param theLegs
	 */
	public void calculateRouteScores(final Collection<BaseContribution> contribs,
			ArrayList<CoreLeg> theLegs, IProgressMonitor monitor) throws InterruptedException
	{
		operateOn(theLegs, monitor, new LegOperation()
		{
			public void apply(CoreLeg thisLeg)
			{
				if (thisLeg.getType() == LegType.STRAIGHT)
				{
					StraightLeg leg = (StraightLeg) thisLeg;
					leg.calculateRouteScores(contribs);
				}
			}
		});
	}

	public void generateRoutes(ArrayList<CoreLeg> theLegs, IProgressMonitor monitor) throws InterruptedException
	{
		operateOn(theLegs, monitor, new LegOperation()
		{
			public void apply(CoreLeg thisLeg) throws InterruptedException
			{
				thisLeg.generateRoutes(_myPrecision);
			}
		});
	}

	/**
	 * get the legs to decide on their achievable routes
	 * 
	 * @param theLegs
	 */
	public void decideAchievable(ArrayList<CoreLeg> theLegs, IProgressMonitor monitor) throws InterruptedException
	{
		operateOn(theLegs, monitor, new LegOperation()
		{
			public void apply(CoreLeg thisLeg) throws InterruptedException
			{
				thisLeg.decideAchievableRoutes();
			}
		});
	}

	/**
	 * using best performing routes through the straight legs, generate a set of
	 * overall solutions
	 * 
	 * @param theLegs
	 *          the set of legs we're looking at
	 * @return a set of routes through the data
	 */
	CompositeRoute[] generateCandidates(ArrayList<CoreLeg> theLegs, IProgressMonitor monitor) throws InterruptedException
	{
		CompositeRoute res = new CompositeRoute();

		// PHASE 1 = just do it for straight legs
		for (Iterator<CoreLeg> iterator = theLegs.iterator(); iterator.hasNext();)
		{
			if (monitor.isCanceled()) throw new InterruptedException();
			CoreLeg coreLeg = iterator.next();

			// get the top solutions
			SortedSet<CoreRoute> topR = coreLeg.getTopRoutes();

			// have we found any?
			if (topR != null)
				if (topR.size() > 0)
				{
					// and remember it the top one
					res.add(topR.first());
				}
		}

		// and return the top route
		return new CompositeRoute[]
		{ res };
	}

	void cancelUnachievable(ArrayList<CoreLeg> theLegs, int[][] routes, IProgressMonitor monitor) throws InterruptedException
	{

		// check we've got some legs
		if (theLegs.size() > 0)
		{
			// get the routes for the first leg
			CoreLeg firstLeg = theLegs.get(0);

			// get the routes
			CoreRoute[][] firstRoutes = firstLeg.getRoutes();

			for (int x = 0; x < routes.length; x++)
			{
				boolean possible = false;
				for (int y = 0; y < routes[0].length; y++)
				{
					if (monitor.isCanceled()) throw new InterruptedException();
					if (routes[x][y] > 0)
					{
						// this one is possible, drop out
						possible = true;
						break;
					}
				}
				if (!possible)
				{
					// mark all of that route impossible
					CoreRoute[] thisSet = firstRoutes[x];
					for (int i = 0; i < thisSet.length; i++)
					{
						if (monitor.isCanceled()) throw new InterruptedException();
						CoreRoute thisRoute = thisSet[i];
						if (thisRoute != null)
							thisRoute.setImpossible();
					}
				}
			}
		}
	}

	int[][] calculateAchievableRoutesFor(ArrayList<CoreLeg> theLegs, IProgressMonitor monitor) throws InterruptedException
	{
		// ok, loop through the legs, doing the multiplication
		int[][] res = null;
		for (Iterator<CoreLeg> iterator = theLegs.iterator(); iterator.hasNext();)
		{
			if (monitor.isCanceled()) throw new InterruptedException();
			CoreLeg thisLeg = (CoreLeg) iterator.next();
			int[][] mat = thisLeg.asMatrix();

			// is this our first one?
			if (res == null)
				res = mat;
			else
			{
				res = CoreLeg.multiply(res, mat);
			}
		}

		return res;
	}

	/**
	 * apply the specified operation on all legs
	 * 
	 * @param theLegs
	 * @param theStepper
	 */
	private static void operateOn(ArrayList<CoreLeg> theLegs, IProgressMonitor monitor,
			LegOperation theStepper) throws InterruptedException
	{
		for (Iterator<CoreLeg> iterator = theLegs.iterator(); iterator.hasNext();)
		{
			CoreLeg thisLeg = (CoreLeg) iterator.next();
			if (monitor.isCanceled()) throw new InterruptedException();
			try 
			{
				theStepper.apply(thisLeg);
			} 
			catch (RuntimeException ex) 
			{
				throw new GenerationException(ex.getMessage());
			}
		}
	}

	/**
	 * extract a set of legs from the space
	 * 
	 * @param space
	 * @return
	 */
	ArrayList<CoreLeg> getTheLegs(Collection<BoundedState> theStates, IProgressMonitor monitor) throws InterruptedException
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

		Iterator<BoundedState> iterator = theStates.iterator();
		while (iterator.hasNext())
		{
			if (monitor.isCanceled()) throw new InterruptedException();
			
			BoundedState thisS = iterator.next();
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
					currentLeg = new StraightLeg(thisLegName,
							new ArrayList<BoundedState>());
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
					currentLeg = new AlteringLeg(thisName, new ArrayList<BoundedState>());
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

	@Override
	public void clear()
	{
		Job<?, ?> job = mainGenerationJob;
		if (job != null) 
		{
			jobsManager.cancelGroup(SOLUTION_GENERATOR_JOBS_GROUP);
			try 
			{
				jobsManager.waitFor(job);
			} 
			catch (InterruptedException ex) 
			{
				LogFactory.getLog().error("Thread was interrupted", ex);
			}
		}
		if (_theLegs != null)
		{
			_theLegs.clear();
			_theLegs = null;
		}
	}
	
	

	@Override
	public void cancel()
	{
		Job<?, ?> job = mainGenerationJob;
		if (job != null) 
		{
			jobsManager.cancelGroup(SOLUTION_GENERATOR_JOBS_GROUP);
		}
	}

	/**
	 * we've sorted out the leg scores
	 * 
	 * @param theLegs
	 * 
	 */
	private void fireLegsScored(ArrayList<CoreLeg> theLegs)
	{
		List<CoreLeg> legs = Collections.unmodifiableList(new ArrayList<CoreLeg>(theLegs));
		for (IGenerateSolutionsListener listener : _readyListeners)
		{
			listener.legsScored(legs);
		}

	}

	/**
	 * we've sorted out the leg scores
	 * 
	 * @param theLegs
	 * 
	 */
	private void fireStartingGeneration()
	{
		for (IGenerateSolutionsListener listener : _readyListeners)
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
	private void fireFinishedGeneration(Throwable error)
	{
		for (IGenerateSolutionsListener listener : _readyListeners)
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
	private void fireSolutionsReady(CompositeRoute[] routes)
	{
		for (IGenerateSolutionsListener listener : _readyListeners)
		{
			listener.solutionsReady(routes);
		}

	}

	@Override
	public void setPrecision(Precision precision)
	{
		_myPrecision = precision;
		if (_theLegs != null) 
		{
			generateSolutions(true);
		}
	}
	
	
	@Override
	public Precision getPrecision()
	{
		return _myPrecision;
	}



	/**
	 * utility interface to make it easy to operate on all legs
	 * 
	 * @author Ian
	 * 
	 */
	public static interface LegOperation
	{
		/**
		 * operate on this leg
		 * 
		 * @param thisLeg
		 */
		public void apply(CoreLeg thisLeg) throws InterruptedException;
	}	
	
	private abstract class SolutionGeneratorJob<T, P> extends Job<T, P> 
	{

		public SolutionGeneratorJob(String name)
		{
			super(name, SOLUTION_GENERATOR_JOBS_GROUP);
		}
		
		protected abstract <E> T doRun(IProgressMonitor monitor, Job<P, E> previous) throws InterruptedException;

		@Override
		protected final <E> T run(IProgressMonitor monitor, Job<P, E> previous)
				throws InterruptedException
		{
			monitor.beginTask(getName(), 1);
			T result = doRun(monitor, previous);
			monitor.done();
			return result;
		}
	}
}
