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
	
	public PointsMutationToVertexes(List<StraightLeg> legs, Probability mutationProbability)
	{
		super(legs, mutationProbability);
	}

	@Override
	protected Point mutatePoint(Point current, StraightLeg leg,
			boolean useEndPoint, Random rng)
	{
		double temp = Math.min(1 / Math.log(iteration + 1), 1);
		if (rng.nextDouble() < 0.2)
		{
			temp = 1.;
		}
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
		Point x = MathUtils.calculateBezier(rng.nextDouble() * temp, current, vertex1, null);
		Point y = MathUtils.calculateBezier(rng.nextDouble() * temp, current, vertex2, null);
		return MathUtils.calculateBezier(rng.nextDouble(), x, y, null);
	}
	
	
}
