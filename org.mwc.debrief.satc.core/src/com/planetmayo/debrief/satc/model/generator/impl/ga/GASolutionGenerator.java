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
package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.TerminationCondition;
import org.uncommons.watchmaker.framework.termination.ElapsedTime;

import com.planetmayo.debrief.satc.log.LogFactory;
import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.contributions.StraightLegForecastContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.generator.IGenerateSolutionsListener;
import com.planetmayo.debrief.satc.model.generator.IJobsManager;
import com.planetmayo.debrief.satc.model.generator.impl.AbstractSolutionGenerator;
import com.planetmayo.debrief.satc.model.generator.jobs.Job;
import com.planetmayo.debrief.satc.model.legs.CompositeRoute;
import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.LegType;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;
import com.planetmayo.debrief.satc.util.MathUtils;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;

public class GASolutionGenerator extends AbstractSolutionGenerator
{
  private static class TimePeriod
  {

    final private long _start;
    final private long _end;

    public TimePeriod(final long start, final long end)
    {
      _start = start;
      _end = end;
    }

    public boolean contains(final long time)
    {
      return time >= _start && time <= _end;
    }
  }

  private static final String GA_GENERATOR_GROUP = "gaGeneratorGroup";

  public static final String NAME = "Genetic Algorithm";

  private volatile List<StraightLeg> straightLegs;

  private volatile Job<Void, Void> mainJob;

  private final GAParameters parameters;

  public GASolutionGenerator(final IContributions contributions,
      final IJobsManager jobsManager, final SafeProblemSpace problemSpace)
  {
    super(contributions, jobsManager, problemSpace);
    parameters = new GAParameters();
    parameters.setMutationProbability(0.25);
    parameters.setTopRoutes(10);
    parameters.setTimeoutBetweenIterations(0);
    parameters.setUseAlteringLegs(true);
  }

  @Override
  public void cancel()
  {
    final Job<?, ?> job = mainJob;
    if (job != null)
    {
      jobsManager.cancelGroup(GA_GENERATOR_GROUP);
    }
  }

  @Override
  public void clear()
  {
    final Job<?, ?> job = mainJob;
    if (job != null)
    {
      jobsManager.cancelGroup(GA_GENERATOR_GROUP);
      try
      {
        jobsManager.waitFor(job);
      }
      catch (final InterruptedException ex)
      {
        LogFactory.getLog().error("Thread was interrupted", ex);
      }
    }
    if (straightLegs != null)
    {
      straightLegs.clear();
      straightLegs = null;
    }
  }

  /**
   * share news about the completed iteartion
   *
   * @param topRoutes
   *          the list of completed routes
   * @param topScore
   *          the score of the best performing route
   */
  protected void fireIterationComputed(final List<CompositeRoute> topRoutes,
      final double topScore)
  {
    for (final IGenerateSolutionsListener listener : _readyListeners)
    {
      if (listener instanceof IGASolutionsListener)
      {
        ((IGASolutionsListener) listener).iterationComputed(topRoutes,
            topScore);
      }
    }
  }

  @Override
  public synchronized void generateSolutions(final boolean fullRerun)
  {
    if (mainJob != null)
    {
      return;
    }
    fireStartingGeneration();
    Job<Void, Void> previous = null;
    final Precision thePrecision = getPrecision();

    if (fullRerun || straightLegs == null)
    {
      previous = jobsManager.schedule(new Job<Void, Void>("Generate Legs",
          GA_GENERATOR_GROUP)
      {

        @Override
        protected <E> Void run(final IProgressMonitor monitor,
            final Job<Void, E> previous) throws InterruptedException
        {
          SATC_Activator.log(IStatus.INFO, "SATC GA - Generate Legs - Start",
              null);

          // try to hide states outside straight legs
          hideStatesOutsideStraightLegs();

          // does the user want to suppress?
          if (getAutoSuppress())
          {

            // ok - user wants to hide some states
            final int numStates = problemSpaceView.states().size();
            final int tgtNum;
            switch (thePrecision)
            {
              case LOW:
                tgtNum = (int) (numStates * 0.2);
                break;
              case MEDIUM:
                tgtNum = (int) (numStates * 0.5);
                break;
              case HIGH:
              default:
                tgtNum = (int) (numStates * 0.8);
                break;
            }
            // ok - cull the boring states
            suppressCuts(problemSpaceView.states(), tgtNum);
          }

          final List<CoreLeg> rawLegs = getTheLegs(problemSpaceView.states(),
              monitor);
          straightLegs = new ArrayList<StraightLeg>();
          for (final CoreLeg leg : rawLegs)
          {
            if (leg.getType() == LegType.STRAIGHT)
            {
              straightLegs.add((StraightLeg) leg);
            }
          }

          SATC_Activator.log(IStatus.INFO, "SATC GA - Generate Legs - Complete",
              null);
          return null;
        }
      });
    }
    mainJob = jobsManager.scheduleAfter(new Job<Void, Void>("Calculate GA",
        GA_GENERATOR_GROUP)
    {

      @Override
      protected void onComplete()
      {
        SATC_Activator.log(IStatus.INFO, "SATC GA - Run GA - Complete", null);
        synchronized (GASolutionGenerator.this)
        {
          mainJob = null;
        }
        fireFinishedGeneration(getException());
      }

      @Override
      protected <E> Void run(final IProgressMonitor monitor,
          final Job<Void, E> previous) throws InterruptedException
      {
        SATC_Activator.log(IStatus.INFO, "SATC GA - Run GA - Start", null);
        runGA(monitor);
        return null;
      }

    }, previous);
    if (mainJob != null && mainJob.isComplete())
    {
      mainJob = null;
    }
  }

  protected IContributions getContributions()
  {
    return contributions;
  }

  public GAParameters getParameters()
  {
    return parameters;
  }

  protected void hideStatesOutsideStraightLegs()
  {

    // get teh straight leg contributionsc
    long first = Long.MAX_VALUE;
    long last = Long.MIN_VALUE;
    for (final BaseContribution cont : contributions)
    {
      if (cont instanceof StraightLegForecastContribution)
      {
        final StraightLegForecastContribution sl =
            (StraightLegForecastContribution) cont;
        if (sl.isActive())
        {
          first = Math.min(first, sl.getStartDate().getTime());
          last = Math.max(last, sl.getFinishDate().getTime());
        }
      }
    }

    // and one last "outer" period
    final TimePeriod period = new TimePeriod(first, last);

    final Collection<BoundedState> states = problemSpaceView.states();
    final Collection<BoundedState> toRemove = new ArrayList<BoundedState>();
    for (final BoundedState state : states)
    {
      if (!period.contains(state.getTime().getTime()))
      {
        toRemove.add(state);
      }
    }

    states.removeAll(toRemove);

  }

  private void runGA(final IProgressMonitor progressMonitor)
      throws InterruptedException
  {
    final Random rng = MathUtils.getRNG();
    final RCPIslandEvolution engine = new RCPIslandEvolution(this, straightLegs,
        4, rng);
    final TerminationCondition progressMonitorCondition =
        new TerminationCondition()
        {
          @Override
          public boolean shouldTerminate(final PopulationData<?> populationData)
          {
            return progressMonitor.isCanceled();
          }
        };

    final List<StraightRoute> solution = engine.evolve(parameters
        .getPopulationSize(), parameters.getElitizm(), parameters
            .getEpochLength(), 1, progressMonitorCondition, new ElapsedTime(
                parameters.getTimeout()), new Stagnation(parameters
                    .getStagnationSteps())
    // , new TargetFitness(0.07, false)
    );
    if (progressMonitor.isCanceled())
    {
      throw new InterruptedException();
    }
    fireSolutionsReady(new CompositeRoute[]
    {solutionToRoute(solution, true)});
  }

  @Override
  public void setPrecision(final Precision precision)
  {
    super.setPrecision(precision);
    parameters.setPrecision(precision);
  }

  protected CompositeRoute solutionToRoute(final List<StraightRoute> solution,
      final boolean createAltering)
  {
    @SuppressWarnings(
    {"rawtypes", "unchecked"})
    List<CoreRoute> routes = (List) solution;
    if (createAltering)
    {
      routes = generateAlteringRoutes(routes);
    }
    return new CompositeRoute(routes);

  }
}
