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
import java.util.List;
import java.util.Random;

import org.uncommons.maths.random.Probability;

import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;

public class RandomMutation extends AbstractMutation
{
  private final RoutesCandidateFactory candidateFactory;

  public RandomMutation(List<StraightLeg> legs, Probability mutationProbability,
      RoutesCandidateFactory candidateFactory)
  {
    super(legs, mutationProbability);
    this.candidateFactory = candidateFactory;
  }

  @Override
  protected List<StraightRoute> mutate(List<StraightRoute> candidate,
      Random rng)
  {
    List<StraightRoute> random = null;
    int ctr2 = 0;
    while (random == null && ctr2 < 10)
    {
      ctr2++;
      random = candidateFactory.generateRandomCandidate(rng);
    }
    int mCtr = 0;
    List<StraightRoute> mutated = new ArrayList<StraightRoute>();
    boolean valid = false;
    while (!valid && mCtr < 10)
    {
      mCtr++;
      mutated.clear();
      for (int i = 0; i < candidate.size(); i++)
      {
        StraightRoute res = candidate.get(i);
        if (!mutationProbability.nextValue().nextEvent(rng))
        {
          for (int repeat = 0; repeat < 5; repeat++)
          {
            res = random.get(i);
            legs.get(i).decideAchievableRoute(res);
            if (res.isPossible())
            {
              break;
            }
            // the current leg isn't possible. create a new solution
            random = candidateFactory.generateRandomCandidate(rng);
            int ctr = 0;
            while (random == null && ctr < 5)
            {
              ctr++;
              random = candidateFactory.generateRandomCandidate(rng);
            }
          }
          if (!res.isPossible())
          {
            res = candidate.get(i);
          }
        }
        mutated.add(res);
      }
      valid = RoutesCandidateFactory.checkValid(mutated);
      if(!valid)
      {
      //  System.err.println("mutation failed");
      }
    }
    return mutated;
  }
}
