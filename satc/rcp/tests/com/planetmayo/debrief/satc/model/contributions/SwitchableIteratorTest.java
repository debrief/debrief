package com.planetmayo.debrief.satc.model.contributions;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.contributions.BaseAnalysisContribution.SwitchableIterator;
import com.planetmayo.debrief.satc.model.states.BoundedState;

public class SwitchableIteratorTest
{

	int count = 0;

	@SuppressWarnings("unused")
	@Test
	public void testCount()
	{
		ArrayList<BoundedState> states = new ArrayList<BoundedState>();
		states.add(new BoundedState(new Date(333)));
		states.add(new BoundedState(new Date(444)));
		states.add(new BoundedState(new Date(555)));
		states.add(new BoundedState(new Date(666)));

		SwitchableIterator si = new SwitchableIterator(true);

		assertEquals("counter at zero", 0, count);

		ListIterator<BoundedState> iter = si.getIterator(states);
		while (si.canStep(iter))
		{
			BoundedState ss = si.next(iter);
			count++;
		}

		assertEquals("counter at 4", 4, count);

		count = 0;

		si = new SwitchableIterator(false);


		assertEquals("counter at zero", 0, count);

		iter = si.getIterator(states);
		while (si.canStep(iter))
		{
			BoundedState ss = si.next(iter);
			count++;
		}

	}

}
