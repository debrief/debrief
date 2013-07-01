package com.planetmayo.debrief.satc.model.generator.impl.bf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.generator.IJobsManager;
import com.planetmayo.debrief.satc.model.generator.exceptions.GenerationException;
import com.planetmayo.debrief.satc.model.generator.impl.AbstractSolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.jobs.Job;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;

public class BFSolutionGenerator extends AbstractSolutionGenerator
{
	private static final String SOLUTION_GENERATOR_JOBS_GROUP = "solutionGeneratorGroup";

	/**
	 * the current set of legs
	 * 
	 */
	private List<CoreLeg> _theLegs;

	private List<LegWithRoutes> _routes;

	private volatile Job<?, ?> mainGenerationJob = null;

	public BFSolutionGenerator(IContributions contributions,
			IJobsManager jobsManager, SafeProblemSpace problemSpace)
	{
		super(contributions, jobsManager, problemSpace);
	}

	private synchronized void startRecalculateTopLegsJobs()
	{
		mainGenerationJob = jobsManager
				.schedule(new SolutionGeneratorJob<Void, Void>("Recalculate Top Legs")
				{

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
						synchronized (BFSolutionGenerator.this)
						{
							mainGenerationJob = null;
						}
					}
				});
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
			if (!fullRerun)
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
		Job<Void, Void> getLegsJob = jobsManager
				.schedule(new SolutionGeneratorJob<Void, Void>("Get the legs")
				{

					@Override
					public <E> Void doRun(IProgressMonitor monitor, Job<Void, E> previous)
							throws InterruptedException
					{
						_theLegs = getTheLegs(problemSpaceView.states(), monitor);
						return null;
					}
				});

		Job<Void, Void> generateRoutesJob = jobsManager.scheduleAfter(
				new SolutionGeneratorJob<Void, Void>("Generate routes")
				{

					@Override
					public <E> Void doRun(IProgressMonitor monitor, Job<Void, E> previous)
							throws InterruptedException
					{
						_routes = generateRoutes(_theLegs, monitor);
						return null;
					}
				}, getLegsJob);

		Job<Void, Void> decideAchievableJob = jobsManager.scheduleAfter(
				new SolutionGeneratorJob<Void, Void>("Decide achievable routes")
				{

					@Override
					public <E> Void doRun(IProgressMonitor monitor, Job<Void, E> previous)
							throws InterruptedException
					{
						decideAchievable(_routes, monitor);
						return null;
					}
				}, generateRoutesJob);

		mainGenerationJob = jobsManager.scheduleAfter(
				new SolutionGeneratorJob<Void, Void>("Recalculate top legs")
				{

					@Override
					public <E> Void doRun(IProgressMonitor monitor, Job<Void, E> previous)
							throws InterruptedException
					{
						recalculateTopLegs(monitor);
						System.out.println(" - generator complete at:" + new Date());
						return null;
					}

					@Override
					protected void onComplete()
					{
						fireFinishedGeneration(getException());
						synchronized (BFSolutionGenerator.this)
						{
							mainGenerationJob = null;
						}
					}
				}, decideAchievableJob);
		
		
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
		Map<StraightLeg, CoreRoute> topScores = calculateRouteScores(
				contributions.getContributions(), _routes, monitor);

		CompositeRoute result = new CompositeRoute(topScores.values());

		// share the news
		fireLegsScored(_routes);

		// and we're done, share the good news!
		fireSolutionsReady(new CompositeRoute[]
		{ result });
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
	public Map<StraightLeg, CoreRoute> calculateRouteScores(
			final Collection<BaseContribution> contribs, List<LegWithRoutes> theLegs,
			IProgressMonitor monitor) throws InterruptedException
	{
		final Map<StraightLeg, CoreRoute> top = new LinkedHashMap<StraightLeg, CoreRoute>();
		operateOn(theLegs, monitor, new LegOperation()
		{
			public void apply(LegWithRoutes thisLeg)
			{
				if (thisLeg.getLeg().getType() == LegType.STRAIGHT)
				{
					CoreRoute minRoute = null;
					double minScore = Double.MAX_VALUE;
					CoreRoute[][] routes = thisLeg.getRoutes();
					for (int i = 0; i < routes.length; i++)
					{
						for (CoreRoute route : routes[i])
						{
							if (route == null || !route.isPossible())
							{
								continue;
							}
							double score = 0;
							for (BaseContribution contributions : contribs)
							{
								score += contributions.calculateErrorScoreFor(route);
							}
							route.setScore(score);
							if (score < minScore)
							{
								minScore = score;
								minRoute = route;
							}
						}
					}
					top.put((StraightLeg) thisLeg.getLeg(), minRoute);
				}
			}
		});
		return top;
	}

	public List<LegWithRoutes> generateRoutes(List<CoreLeg> theLegs,
			IProgressMonitor monitor) throws InterruptedException
	{
		List<LegWithRoutes> result = new ArrayList<LegWithRoutes>(theLegs.size());
		for (CoreLeg leg : theLegs)
		{
			leg.generatePoints(_myPrecision.toMeters());
			result.add(new LegWithRoutes(leg));
		}
		return result;
	}

	/**
	 * get the legs to decide on their achievable routes
	 * 
	 * @param theLegs
	 */
	public void decideAchievable(List<LegWithRoutes> theLegs,
			IProgressMonitor monitor) throws InterruptedException
	{
		operateOn(theLegs, monitor, new LegOperation()
		{
			public void apply(LegWithRoutes thisLeg) throws InterruptedException
			{
				thisLeg.decideAchievableRoutes();
			}
		});
	}

	/**
	 * apply the specified operation on all legs
	 * 
	 * @param theLegs
	 * @param theStepper
	 */
	private static void operateOn(List<LegWithRoutes> theLegs,
			IProgressMonitor monitor, LegOperation theStepper)
			throws InterruptedException
	{
		for (Iterator<LegWithRoutes> iterator = theLegs.iterator(); iterator
				.hasNext();)
		{
			LegWithRoutes thisLeg = iterator.next();
			if (monitor.isCanceled())
				throw new InterruptedException();
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
			if (_theLegs != null)
			{
				_theLegs.clear();
				_theLegs = null;

			}
			if (_routes != null)
			{
				_routes.clear();
				_routes = null;
			}
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
	private void fireLegsScored(List<LegWithRoutes> routes)
	{
		List<LegWithRoutes> legs = Collections
				.unmodifiableList(new ArrayList<LegWithRoutes>(routes));
		for (IGenerateSolutionsListener listener : _readyListeners)
		{
			if (listener instanceof IBruteForceSolutionsListener)
			{
				((IBruteForceSolutionsListener) listener).legsScored(legs);
			}
		}
	}

	@Override
	public void setPrecision(Precision precision)
	{
		super.setPrecision(precision);
		if (_theLegs != null) 
		{
			generateSolutions(true);
		}
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
		public void apply(LegWithRoutes thisLeg) throws InterruptedException;
	}

	private abstract class SolutionGeneratorJob<T, P> extends Job<T, P>
	{

		public SolutionGeneratorJob(String name)
		{
			super(name, SOLUTION_GENERATOR_JOBS_GROUP);
		}

		protected abstract <E> T doRun(IProgressMonitor monitor, Job<P, E> previous)
				throws InterruptedException;

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
