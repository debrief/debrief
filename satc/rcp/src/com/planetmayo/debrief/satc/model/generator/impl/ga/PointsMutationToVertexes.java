package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.List;
import java.util.Random;

import org.uncommons.maths.random.Probability;

import com.planetmayo.debrief.satc.model.legs.StraightLeg;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.MathUtils;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class PointsMutationToVertexes extends AbstractMutation
{
	
	public PointsMutationToVertexes(List<StraightLeg> legs, Probability mutationProbability, int generationsInEpoch)
	{
		super(legs, mutationProbability, generationsInEpoch);
	}

	private double distance(double T, Random rng) {
		double tmp =  Math.signum(rng.nextDouble() - 0.5) * T *
				(Math.pow(1 + 1 / T, 2 * rng.nextDouble() - 1) - 1);
		return tmp;
	}
	
	@Override
	protected Point mutatePoint(int iteration, Point current, StraightLeg leg,
			boolean useEndPoint, Random rng)
	{
		double T = Math.exp(-0.85 * Math.pow(iteration, 0.25));
		T = T * T;
		BoundedState state = useEndPoint ? leg.getLast() : leg.getFirst();		
		Geometry geometry = state.getLocation().getGeometry();
		
		int vertexIndex1 = rng.nextInt(geometry.getNumPoints());
		int vertexIndex2;
		do
		{
			vertexIndex2 = rng.nextInt(geometry.getNumPoints());
		}
		while (vertexIndex2 != vertexIndex1);
		
		Point vertex1 = GeoSupport.getFactory().createPoint(geometry.getCoordinates()[vertexIndex1]);
		Point vertex2 = GeoSupport.getFactory().createPoint(geometry.getCoordinates()[vertexIndex2]);
		Point x = MathUtils.calculateBezier(distance(T, rng), current, vertex1, null);
		Point y = MathUtils.calculateBezier(distance(T, rng), current, vertex2, null);
		return MathUtils.calculateBezier(rng.nextDouble(), x, y, null);
	}
	
	
}
