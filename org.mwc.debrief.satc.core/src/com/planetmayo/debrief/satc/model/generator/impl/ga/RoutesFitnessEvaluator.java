/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.List;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.generator.IContributions;
import com.planetmayo.debrief.satc.model.legs.AlteringRoute;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;
import com.planetmayo.debrief.satc.model.states.SafeProblemSpace;
import com.planetmayo.debrief.satc.util.MathUtils;
import com.vividsolutions.jts.geom.Point;

public class RoutesFitnessEvaluator implements FitnessEvaluator<List<StraightRoute>>
{	
	private final List<StraightLeg> legs;
	private final IContributions contributions;
	private final SafeProblemSpace problemSpace;
	private final boolean useAlterings;
	
	public RoutesFitnessEvaluator(List<StraightLeg> legs, boolean useAlterings, 
			IContributions contributions, SafeProblemSpace problemSpace)
	{
		super();
		this.useAlterings = useAlterings;
		this.legs = legs;
		this.contributions = contributions;
		this.problemSpace = problemSpace;
	}

	@Override
	public double getFitness(List<StraightRoute> candidate,	List<? extends List<StraightRoute>> population)
	{
		int length = candidate.size();
		double error = 0;
		boolean impossible = false;
		for (int i = 0; i < length; i++)
		{
			StraightRoute route = candidate.get(i);
			StraightLeg leg = legs.get(i);
			if (route.isPossible())
			{
				if (route.getScore() == 0.)
				{
					leg.decideAchievableRoute(route);
					if (route.isPossible())
					{
						error += calculateContributionsScore(route);
					}
					else 
					{
						impossible = true;
					}
				}
				else
				{
					error += route.getScore();
				}
			}
			else
			{
				impossible = true;
			}
		}
		if (impossible)
		{
			return Double.MAX_VALUE;
		}
		if (useAlterings)
		{
			for (int i = 0; i < candidate.size() - 1; i++)
			{
				StraightRoute prev = candidate.get(i);
				StraightRoute next = candidate.get(i + 1);
				AlteringRoute route = new AlteringRoute("", prev.getEndPoint(), prev.getEndTime(), next.getStartPoint(), next.getStartTime());
				route.generateSegments(problemSpace.getBoundedStatesBetween(prev.getEndTime(), next.getStartTime()));
				route.constructRoute(prev, next);
				error += calculateContributionsScore(route);
				error += calculateAlteringRouteScore(route, prev, next);
			}
		}
		return error;
	}
	
	public double calculateContributionsScore(CoreRoute route)
	{
		double score = 0;
		for (BaseContribution contribution : contributions)
		{
			score += contribution.calculateErrorScoreFor(route);
		}
		route.setScore(score);
		return score;
	}
	
	private double calculateAlteringRouteScore(AlteringRoute route, StraightRoute previous, StraightRoute next)
	{
		return calculateCompliantSpeedError(route, previous, next) + calculateSShapeScore(route);
	}
	
	private double calculateCompliantSpeedError(AlteringRoute route, StraightRoute previous, StraightRoute next) 
	{
		double startSpeed = previous.getSpeed();
		double endSpeed = next.getSpeed();
		double minAlteringSpeed = route.getMinSpeed();
		double maxAlteringSpeed = route.getMaxSpeed();
		double error = 0;
		if (route.getExtremumsCount() == 0) 
		{
			error = 0;
		} 
		else if (route.getExtremumsCount() == 1) 
		{
			double min = Math.min(startSpeed, endSpeed);
			double max = Math.max(startSpeed, endSpeed);
			if (minAlteringSpeed < min) 
			{				
				error += alteringSpeedError(min - minAlteringSpeed);
			}
			if (maxAlteringSpeed > max) 
			{
				error += alteringSpeedError(maxAlteringSpeed - max);
			}			
		} 
		else 
		{
			error += 1.5 * alteringSpeedError(maxAlteringSpeed - minAlteringSpeed);
		}
		return error;		
	}
	
	private double alteringSpeedError(double speedDiff)
	{
		double x = 1 + speedDiff / problemSpace.getVehicleType().getMaxSpeed();
		return x * x - 1; 
	}
	
	private double calculateSShapeScore(AlteringRoute route) 
	{
		Point[] pts = route.getBezierControlPoints();
		
		// first line start-end in parametric form: B(t1) = start * (1 - t1) + end * t1
		double sx = route.getStartPoint().getX(), sy = route.getStartPoint().getY();
		double ex = route.getEndPoint().getX(), ey = route.getEndPoint().getY();

		// second line control point 1-control point 2 in parametric form: C(t2) = pts[0] * (1 - t2) + pts[1] * t2		
		double p0x = pts[0].getX(), p0y = pts[0].getY();
		double p1x = pts[1].getX(), p1y = pts[1].getY();
		
		// if this two lines intersect - we have S shape, C shape otherwise
		// solve linear eq system:
		//    ----                            ---- 
		//    |    Bx(t1) = Cx(t2)           |  sx * (1 - t1) + ex * t1 = p0x * (1-t2) + p1x * t2
    //   <                       =>     <
		//    |    By(t1) = Cy(t2)           |  sy * (1 - t1) + ey * t1 = p0y * (1-t2) + p1y * t2
		//    ----                           ----
		if (Math.abs(ex - sx) < MathUtils.EPS) 
		{
			// C shape
			return 0;
		}
		double c1 = (p0x - sx) / (ex - sx);
		double c2 = (p1x - p0x) / (ex - sx);
		double d = (p1y - p0y) * (sy - ey) * c2;
		if (Math.abs(d) < MathUtils.EPS) 
		{
			// C shape
			return 0;
		}
		double t2 = (sy - p0y + c1 * (ey - sy)) / d;
		double t1 = c1 + c2 * t2;
		// check that two line intersect, so s-shape
		if (t1 >= 0 && t1 <= 1 && t2 >= 0 && t2 <= 1) 
		{
			Point intersection = MathUtils.calculateBezier(t2, pts[0], pts[1], null);
			double a = MathUtils.calcAbsoluteValue(pts[0].getX() - intersection.getX(), pts[0].getY() - intersection.getY());
			double b = MathUtils.calcAbsoluteValue(pts[1].getX() - intersection.getX(), pts[1].getY() - intersection.getY());
			return Math.min(a, b);
		}
		return 0;
	}
	
	@Override
	public boolean isNatural()
	{
		return false;
	}
}
