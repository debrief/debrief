/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.generator.impl.ga;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;
import com.planetmayo.debrief.satc.model.legs.CoreLeg;

public class PointsMutationTest extends ModelTestBase
{
	
	List<CoreLeg> legs;
//	List<LegOperations> operations;
	RoutesCandidateFactory candidateFactory;	
	
	@Before
	public void prepareLegs() throws Exception 
	{
//		WKTReader wkt = new WKTReader();
//		legs = new ArrayList<CoreLeg>();		
//
//		List<BoundedState> states = new ArrayList<BoundedState>();
//		states.add(new BoundedState(DateUtils.date(2012, 5, 5, 12, 0, 0)));
//		states.add(new BoundedState(DateUtils.date(2012, 5, 5, 12, 10, 0)));
//
//		// apply location bounds
//		states.get(0).constrainTo(
//				new LocationRange(wkt.read("POLYGON ((0 3, 2 4, 4 4, 2 3, 0 3))"))
//		);
//		states.get(1).constrainTo(
//				new LocationRange(wkt.read("POLYGON ((2.63 2.56, 3.5 3.16, 4.11 3.42, 3.33 2.3, 2.63 2.56))"))
//		);
//		legs.add(new StraightLeg("1"));
//
//		states = new ArrayList<BoundedState>();		
//		states.add(new BoundedState(DateUtils.date(2012, 5, 5, 12, 20, 0)));
//		states.add(new BoundedState(DateUtils.date(2012, 5, 5, 12, 30, 0)));
//		states.get(0).constrainTo(
//				new LocationRange(wkt.read("POLYGON ((3.32 1.99,3.93 2.71,4.64 2.87,3.81 1.78, 3.32 1.99))"))
//		);
//		states.get(1).constrainTo(
//				new LocationRange(wkt.read("POLYGON ((5 1, 5.5 2, 6 2, 6 1, 5 1))"))
//		);
//		legs.add(new AlteringLeg("2"));
//		
//		operations = new ArrayList<LegOperations>();
//		Random random = new MersenneTwisterRNG();
//		for (CoreLeg leg : legs) 
//		{
//			leg.generatePoints(Precision.LOW.getNumPoints());
//			operations.add(new LegOperations(leg, random));
//		}
//		candidateFactory = new RoutesCandidateFactory(operations);
	}	
	
	
	@Test
	public void testApply() 
	{
//		Random rng = new Random(System.currentTimeMillis());
//		List<List<Point>> candidates = candidateFactory.generateInitialPopulation(200, rng);
//		PointsMutation mutation = new PointsMutation(operations, new Probability(0.2));
//		
//		List<List<Point>> applied = mutation.apply(candidates, rng);
//		assertEquals(candidates.size(), applied.size());
//		int allCount = 0;
//		int modifiedCount = 0;
//		for (int i = 0; i < candidates.size(); i++) 
//		{
//			List<Point> original = candidates.get(i);
//			List<Point> modified = applied.get(i);
//			assertEquals(original.size(), modified.size());
//			if (original == modified)
//			{
//				allCount += original.size();
//				continue;
//			}
//			for (int j = 0; j < original.size(); j++) 
//			{
//				if (! original.get(j).equals(modified.get(j))) 
//				{
//					modifiedCount++;
//				}
//				allCount++;				
//			}
//		}
//		double modifiedPart = ((double) modifiedCount) / allCount;
//		assertTrue(modifiedPart > 0.15 && modifiedPart < 0.25);
	}
}
