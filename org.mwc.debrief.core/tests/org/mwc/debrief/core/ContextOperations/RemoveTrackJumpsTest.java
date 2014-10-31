package org.mwc.debrief.core.ContextOperations;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.commands.ExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.mwc.debrief.core.ContextOperations.RemoveTrackJumps.RemoveJumps;
import org.mwc.debrief.core.ContextOperations.RemoveTrackJumps.RemoveJumps.Leg;

import Debrief.Wrappers.FixWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

public class RemoveTrackJumpsTest
{

	private ArrayList<Editable> _positions= new ArrayList<Editable>();
	private ArrayList<WorldLocation> _lockPoints = new ArrayList<WorldLocation>();


	@Before
	public void setUp() throws Exception
	{
		// ok, generate the set of positions
		WorldLocation origin = new WorldLocation(1,1,0);
		HiResDate startTime = new HiResDate(2000);
		FixWrapper last = new FixWrapper(new Fix(startTime , origin, 0, 6));
		FixWrapper startP = last;
		_lockPoints.add(startP.getLocation());
		_positions.add(startP);
		last = createNext(last, _positions, 2000, 0, 6);
		last = createNext(last, _positions, 2000, 0, 6);
		last = createNext(last, _positions, 2000, 0, 6);
		last = createNext(last, _positions, 2000, 0, 6);
		last = createNext(last, _positions, 2000, 0, 6);
		last = createNext(last, _positions, 2000, 0, 6);
		last = createNext(last, _positions, 2000, 0, 6);
		last = createNext(last, _positions, 2000, 0, 6);
		startP = last = createNext(startP, _positions, 18000, 90, 8);
		_lockPoints.add(startP.getLocation());
		last = createNext(last, _positions, 2000, 180, 6);
		last = createNext(last, _positions, 2000, 180, 6);
		last = createNext(last, _positions, 2000, 180, 6);
		last = createNext(last, _positions, 2000, 180, 6);
		last = createNext(last, _positions, 2000, 180, 6);
		last = createNext(last, _positions, 2000, 180, 6);
		last = createNext(last, _positions, 2000, 180, 6);
		last = createNext(last, _positions, 2000, 180, 6);
		startP = last = createNext(startP, _positions, 18000, 90, 4);
		_lockPoints.add(startP.getLocation());
		last = createNext(last, _positions, 2000, 100, 6);
		last = createNext(last, _positions, 2000, 120, 6);
		last = createNext(last, _positions, 2000, 140, 6);
		last = createNext(last, _positions, 2000, 160, 6);
		last = createNext(last, _positions, 2000, 180, 6);
		last = createNext(last, _positions, 2000, 160, 6);
		last = createNext(last, _positions, 2000, 150, 6);
		last = createNext(last, _positions, 2000, 140, 6);
		startP = last = createNext(startP, _positions, 18000, 80, 6);
		_lockPoints.add(startP.getLocation());
	}

	
	

	private FixWrapper createNext(FixWrapper last,
			ArrayList<Editable> positions, int timeDelta, double courseDegs, double speedKts)
	{
		// how far did it travel?
		WorldSpeed spd = new WorldSpeed(speedKts, WorldSpeed.Kts);
		double distM = (timeDelta / 1000) *  spd.getValueIn(WorldSpeed.M_sec);
		WorldDistance dist = new WorldDistance(distM, WorldDistance.METRES);
		double dirRads = MWC.Algorithms.Conversions.Degs2Rads(courseDegs);
		
		WorldVector offset = new WorldVector(dirRads, dist.getValueIn(WorldDistance.DEGS), 0);
		WorldLocation newLoc = last.getLocation().add(offset);
		
		long newTime = last.getTime().getDate().getTime() + timeDelta;
		FixWrapper res = new FixWrapper(new Fix(new HiResDate(newTime), newLoc, dirRads, spd.getValueIn(WorldSpeed.ft_sec)/3));
		
		positions.add(res);
		
		return res;
	}

	@Test
	public void testFindLegs() throws ExecutionException
	{	
		ArrayList<Leg> legs = RemoveJumps.getLegs(_positions);
		junit.framework.Assert.assertEquals("found legs", 2, legs.size());
	}
	
	@Test
	public void testPerform() throws ExecutionException
	{
		
		System.out.println("GEOMETRYCOLLECTION (");
		emitPoints(_lockPoints);
		System.out.println(" , ");
		emitLine(_positions);
		
		System.out.println(" , ");
		
		RemoveJumps rj = new RemoveJumps("test", null, null, _positions);
		rj.execute(null, null);
		emitLine(_positions);
		
		
		System.out.println(" )");
	}




	private static void emitPoints(ArrayList<WorldLocation> list)
	{
		Iterator<WorldLocation> iter = list.iterator();
		boolean first = true;
		while (iter.hasNext())
		{
			if(first)
				first = false;
			else
				System.out.print(",");			

			WorldLocation worldLocation = (WorldLocation) iter.next();
			System.out.print("POINT (" + worldLocation.getLong() * 10 + ", " + worldLocation.getLat() * 10 + ") ");			
		}
	}




	private static void emitLine(ArrayList<Editable> list)
	{
		// LINESTRING(-18.6328125 12.3046875, -18.6328125 21.97265625, -6.50390625 21.796875)
		String msg = "LINESTRING(";
		
		// have a look at the positions
		Iterator<Editable> iter = list.iterator();
		boolean first = true;
		while (iter.hasNext())
		{
			if(first)
				first = false;
			else
				msg += ", ";			
			
			FixWrapper fix = (FixWrapper) iter.next();
			msg += fix.getLocation().getLong() * 10 + " " + fix.getLocation().getLat() * 10 + "";
		}
		msg += ")";
		System.out.println(msg);
	}

}
