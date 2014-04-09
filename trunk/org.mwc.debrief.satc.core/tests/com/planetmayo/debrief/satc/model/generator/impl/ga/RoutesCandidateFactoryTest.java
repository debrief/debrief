package com.planetmayo.debrief.satc.model.generator.impl.ga;

import org.junit.Before;
import org.junit.Test;

import com.planetmayo.debrief.satc.model.ModelTestBase;

public class RoutesCandidateFactoryTest extends ModelTestBase
{
	
//	List<CoreLeg> legs;
//	List<LegOperations> operations;	
//	RoutesCandidateFactory candidateFactory;

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
	public void testGenerateRandomCandidate() 
	{
//		List<Point> candidate = candidateFactory.generateRandomCandidate(new Random(System.currentTimeMillis()));
//		assertEquals(4, candidate.size());
//		assertTrue(legs.get(0).getStartPoints().contains(candidate.get(0)));
//		assertTrue(legs.get(0).getEndPoints().contains(candidate.get(1)));
//		assertTrue(legs.get(1).getStartPoints().contains(candidate.get(2)));
//		assertTrue(legs.get(1).getEndPoints().contains(candidate.get(3)));		
	}
	
	@Test
	public void testGenerateInitialPopulation()
	{
//		List<List<Point>> population = candidateFactory.generateInitialPopulation(200, new Random(System.currentTimeMillis()));
//		assertEquals(200, population.size());
//		for (List<Point> candidate : population) 
//		{
//			assertEquals(4, candidate.size());
//		}
//		// check that we don't have duplicate points		 
//		for (int i = 0; i < 4; i++)
//		{
//			Set<Point> setsOfPoints = new HashSet<Point>();
//			for (List<Point> candidate : population) 
//			{
//				setsOfPoints.add(candidate.get(i));
//			}
//			assertEquals(200, setsOfPoints.size());
//		}
	}
	
}
