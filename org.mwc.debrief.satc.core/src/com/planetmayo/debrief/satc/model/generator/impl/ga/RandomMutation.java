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
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.calculator.GeodeticCalculator;
import com.vividsolutions.jts.geom.Point;

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
    List<StraightRoute> mutated = new ArrayList<StraightRoute>();
    boolean valid = false;
    while (!valid)
    {
      List<StraightRoute> random = candidateFactory.generateRandomCandidate(
          rng);
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
            random = candidateFactory.generateRandomCandidate(rng);
          }
          if (!res.isPossible())
          {
            res = candidate.get(i);
          }
        }
        mutated.add(res);
      }
      valid = isValid(mutated);
    }
    return mutated;
  }

  public static boolean isValid(final List<StraightRoute> mutated)
  {
    StraightRoute last = null;
    final double maxSpeed = 10;
    for(StraightRoute route: mutated)
    {
      if(last != null)
      {
        final Point startP = last.getEndPoint();
        final long startT = last.getEndTime().getTime();
        final Point endP = route.getStartPoint();
        final long endT = route.getStartTime().getTime();
        
        final GeodeticCalculator calc = GeoSupport.createCalculator();
        calc.setStartingGeographicPoint(startP.getX(), startP.getY());
        calc.setDestinationGeographicPoint(endP.getX(),  endP.getY());
        final double distM = calc.getOrthodromicDistance();
        
        final long deltaT = endT - startT;
        final double deltaSecs = deltaT / 1000d;
        
        final double speedMs = distM / deltaSecs;
        
        if(speedMs > maxSpeed)
        {
          return false;
        }
      }
      
      last = route;
    }
    return true;
  }
}
