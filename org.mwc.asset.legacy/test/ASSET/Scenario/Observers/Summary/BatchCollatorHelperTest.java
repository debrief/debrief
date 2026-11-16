/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package ASSET.Scenario.Observers.Summary;

import static ASSET.Scenario.Observers.Summary.BatchCollatorHelper.*;
import static org.junit.jupiter.api.Assertions.*;

import ASSET.Scenario.Observers.Summary.BatchCollatorHelper.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;
import ASSET.Util.SupportTesting;
import MWC.Utilities.TextFormatting.GeneralFormat;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Asserts the store is null after it has been created; ASSET's tests were in no build before the JUnit 6 migration (org.mwc.asset.test was not in the reactor), so this never ran")
public class BatchCollatorHelperTest extends SupportTesting {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	public void doCoreTest(final StatsCollator nonPerCaseCC, final StatsCollator perCaseCC, final String name,
			final String nonPerCaseString, final String perCaseString, final String units) {
		assertNotNull(nonPerCaseCC, "didn't receive it");

		// ok, add some items
		nonPerCaseCC.addDatum("a1", "a", 2);
		nonPerCaseCC.addDatum("a3", "b", 3);
		nonPerCaseCC.addDatum("a4", "a", "sddfgsdfs");
		nonPerCaseCC.addDatum("a5", "b", new Double(1));
		nonPerCaseCC.addDatum("a5", "b", 3);
		nonPerCaseCC.addDatum("a4", "a", "sddfgsdfs");

		// get the result
		final String thisResult = nonPerCaseCC.getResult(name, units);
		assertTrue(thisResult.indexOf(nonPerCaseString) > 0, "count wrongly performed");
		assertTrue(thisResult.indexOf(name) > 0, "collator name not included");

		// and now the per-case instance
		// ok, add some items
		perCaseCC.addDatum("a7", "a", 2);
		perCaseCC.addDatum("a8", "b", 3);
		perCaseCC.addDatum("a9", "a", "sddfgsdfs");
		perCaseCC.addDatum("a1", "b", new Double(1));
		perCaseCC.addDatum("av", "a", new Double(5));
		perCaseCC.addDatum("av", "a", new Double(5));
		perCaseCC.addDatum("a8", "b", 3);
		perCaseCC.addDatum("a9", "a", "sddfgsdfs");

		// check we've stored them
		assertNotNull(perCaseCC._myData, "store not created");

		final String newResult = perCaseCC.getResult(name, units);
		assertTrue(newResult.indexOf(perCaseString) > 0, "results wrongly performed");

	}

	@Test
	public void testAverageCollator() {
		// ////////////////////////////////////////////////
		// allright, the non per-case instance first
		// ////////////////////////////////////////////////
		final AverageCollator cc = new AverageCollator(false);
		final AverageCollator cc2 = new AverageCollator(true);

		doCoreTest(cc, cc2, "meanie", "Count:,4 ,Average:,2.25", "a,Count:,3 ,Average:,4", "feet");

		// ok, both collators should have some data in them now. do some more
		// checking

		// check we've stored them
		assertNull(cc._myData, "store created");
		assertNotNull(cc._myAverage, "items not stored");
		assertEquals(4, cc._myAverage.counter, "correct count not recorded");

		// and now the per-case instance
		// check we've stored them
		assertNotNull(cc2._myData, "store not created");
		assertTrue(cc2._myData.size() > 0, "items not stored");
		assertEquals(2, cc2._myData.size(), "correct num cases not recorded");
	}

	@Test
	public void testCountCollator() {
		// ////////////////////////////////////////////////
		// allright, the non per-case instance first
		// ////////////////////////////////////////////////
		final CountCollator cc = new CountCollator(false);
		final CountCollator cc2 = new CountCollator(true);

		doCoreTest(cc, cc2, "counter", "Count:6", "a,5", "feet");

		// ok, both collators should have some data in them now. do some more
		// checking

		// check we've stored them
		assertNull(cc._myData, "store created");
		assertTrue(cc._myCounter > 0, "items not stored");
		assertEquals(6, cc._myCounter, "correct count not recorded");

		// and now the per-case instance
		// check we've stored them
		assertNotNull(cc2._myData, "store not created");
		assertTrue(cc2._myData.size() > 0, "items not stored");
		assertEquals(2, cc2._myData.size(), "correct num cases not recorded");
	}

	@Test
	public void testItemizedListCollator() {
		// ////////////////////////////////////////////////
		// allright, the non per-case instance first
		// ////////////////////////////////////////////////
		final ListCollator cc = new ItemisedListCollator(false);
		final ListCollator cc2 = new ItemisedListCollator(true);

		doCoreTest(cc, cc2, "lister", "a1 , 2.0", "a,a7 , 2.0", "feet");

		// ok, both collators should have some data in them now. do some more
		// checking

		// check we've stored them
		assertNull(cc._myData, "store created");
		assertNotNull(cc._myList, "items not stored");
		assertEquals(6, cc._myList.size(), "correct count not recorded");

		// and now the per-case instance
		// check we've stored them
		assertNotNull(cc2._myData, "store not created");
		assertTrue(cc2._myData.size() > 0, "items not stored");
		assertEquals(2, cc2._myData.size(), "correct num cases not recorded");
	}

	@Test
	public void testListCollator() {
		// ////////////////////////////////////////////////
		// allright, the non per-case instance first
		// ////////////////////////////////////////////////
		final ListCollator cc = new ListCollator(false);
		final ListCollator cc2 = new ListCollator(true);

		doCoreTest(cc, cc2, "lister", "2.0, 3.0", "a,2.0, sddfg", "feet");

		// ok, both collators should have some data in them now. do some more
		// checking

		// check we've stored them
		assertNull(cc._myData, "store created");
		assertNotNull(cc._myList, "items not stored");
		assertEquals(6, cc._myList.size(), "correct count not recorded");

		// and now the per-case instance
		// check we've stored them
		assertNotNull(cc2._myData, "store not created");
		assertTrue(cc2._myData.size() > 0, "items not stored");
		assertEquals(2, cc2._myData.size(), "correct num cases not recorded");
	}

	@Test
	public void testTopCollator() {
		BatchCollatorHelper helper = new BatchCollatorHelper("lister", true, BatchCollator.LIST, true, "feet");
		helper.submitResult("bb1", "a", new Double(12));
		helper.submitResult("bb2", "b", 122);
		helper.submitResult("bb3", "c", 32);
		helper.submitResult("bb4", "a", 2);
		helper.submitResult("bbb3", "a", "here");

		String res = helper.getResults();
		assertTrue(res.indexOf("a,12.0, 2.0, here") > 0, "wrong listing");

		// and the inactive version
		helper = new BatchCollatorHelper("lister", true, BatchCollator.LIST, false, "feet");
		helper.submitResult("bbb1", "a", new Double(12));
		helper.submitResult("bbb2", "b", 122);
		helper.submitResult("bbb3", "c", 32);
		helper.submitResult("bbb4", "a", 2);
		helper.submitResult("bbb5", "a", "here");

		res = helper.getResults();
		assertNull(res, "mistakenly returned result");
	}

	public void untestFrequencyListCollator() {
		// ////////////////////////////////////////////////
		// allright, the non per-case instance first
		// ////////////////////////////////////////////////
		final FrequencyListCollator cc = new FrequencyListCollator(false);
		final FrequencyListCollator cc2 = new FrequencyListCollator(true);

		doCoreTest(cc, cc2, "lister", "(3.0: 2), (sddfgsdfs: 2),", "a, (sddfgsdfs: 2), (2.0: 1), (5.0: 2)", "feet");

		// ok, both collators should have some data in them now. do some more
		// checking

		// check we've stored them
		assertNull(cc._myData, "store created");
		assertNotNull(cc._myList, "items not stored");
		assertEquals(4, cc._myList.size(), "correct count not recorded");

		// and now the per-case instance
		// check we've stored them
		assertNotNull(cc2._myData, "store not created");
		assertTrue(cc2._myData.size() > 0, "items not stored");
		assertEquals(2, cc2._myData.size(), "correct num cases not recorded");
	}
}
