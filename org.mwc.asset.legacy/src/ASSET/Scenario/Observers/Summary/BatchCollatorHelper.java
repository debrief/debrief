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

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 12-Aug-2004 Time: 13:43:21 To
 * change this template use File | Settings | File Templates.
 */
public class BatchCollatorHelper {

	// ////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////

	/**
	 * member class to average instances of results
	 */
	private static class AverageCollator extends StatsCollator {

		/**
		 * convenience class to store the components of the average
		 */
		protected static class ThisAverage {
			public int counter = 0;
			public double runningTotal = 0;

			/**
			 * ok, calculate the average
			 *
			 * @return
			 */
			public double getAverage() {
				double res;
				if (counter > 0) {
					res = runningTotal / counter;
				} else {
					res = 0;
				}
				return res;
			}

			/**
			 * get this average as a string
			 */
			public String getResult() {
				return "Count:," + counter + " ,Average:," + getAverage();
			}

			/**
			 * add this new data item
			 *
			 * @param newValue
			 */
			public void update(final double newValue) {
				counter++;
				runningTotal += newValue;
			}
		}

		ThisAverage _myAverage;

		/**
		 * constructor, to get us going
		 *
		 * @param perCase
		 */
		public AverageCollator(final boolean perCase) {
			super(perCase);

		}

		/**
		 * we haven't processed this case yet, create a container for it
		 *
		 * @return the container for this case
		 */
		@Override
		protected Object createCase() {
			Object thisCase;
			thisCase = new ThisAverage();

			return thisCase;
		}

		/**
		 * produce a results string from the supplied details
		 *
		 * @param caseId this case
		 * @param result the collated result
		 * @return string ready for output
		 */
		@Override
		protected String getThisResult(final Object caseId, final Object result) {
			final ThisAverage thisAvg = (ThisAverage) result;
			return "" + caseId + "," + thisAvg.getResult();
		}

		/**
		 * process this data item
		 *
		 * @param scenario_name
		 * @param thisCase      the current container for this data set
		 * @param datum         the current data item received
		 * @return the changed/updated container for the data set
		 */
		@Override
		protected Object processCaseResult(final String scenario_name, final Object thisCase, final double datum) {
			// convert back to average
			final ThisAverage thisAv = (ThisAverage) thisCase;

			// store the item
			thisAv.update(datum);

			// and return it
			return thisCase;
		}

		/**
		 * process this data item
		 *
		 * @param scenario_name
		 * @param thisCase      the current container for this data set
		 * @param datum         the current data item received
		 * @return the changed/updated container for the data set
		 */
		@Override
		protected Object processCaseResult(final String scenario_name, Object thisCase, final Object datum) {
			if (datum instanceof Number) {
				final Number thisNum = (Number) datum;
				thisCase = processCaseResult(scenario_name, thisCase, thisNum.doubleValue());
			}

			return thisCase;
		}

		/**
		 * ok, this hasn't got to be managed on a per-case basis, do the simple stuff
		 *
		 * @param scenario_name
		 * @param datum         the new data value
		 */
		@Override
		protected void processNonCaseResult(final String scenario_name, final double datum) {

			// have we started yet?
			if (_myAverage == null)
				_myAverage = new ThisAverage();

			// add the new item
			_myAverage.update(datum);
		}

		/**
		 * ok, this hasn't got to be managed on a per-case basis, do the simple stuff
		 *
		 * @param scenario_name
		 * @param datum         the new data value
		 */
		@Override
		protected void processNonCaseResult(final String scenario_name, final Object datum) {
			if (datum instanceof Number) {
				// ok, store the data item
				final Number thisNum = (Number) datum;
				processNonCaseResult(scenario_name, thisNum.doubleValue());
			}

		}

		/**
		 * collate the results into a string
		 *
		 * @return
		 */
		@Override
		protected String retrieveResult() {
			return _myAverage.getResult();
		}
	}

	static public class CollatorTest extends SupportTesting {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public CollatorTest(final String val) {
			super(val);
		}

		public void doCoreTest(final StatsCollator nonPerCaseCC, final StatsCollator perCaseCC, final String name,
				final String nonPerCaseString, final String perCaseString, final String units) {
			assertNotNull("didn't receive it", nonPerCaseCC);

			// ok, add some items
			nonPerCaseCC.addDatum("a1", "a", 2);
			nonPerCaseCC.addDatum("a3", "b", 3);
			nonPerCaseCC.addDatum("a4", "a", "sddfgsdfs");
			nonPerCaseCC.addDatum("a5", "b", new Double(1));
			nonPerCaseCC.addDatum("a5", "b", 3);
			nonPerCaseCC.addDatum("a4", "a", "sddfgsdfs");

			// get the result
			final String thisResult = nonPerCaseCC.getResult(name, units);
			assertTrue("count wrongly performed", thisResult.indexOf(nonPerCaseString) > 0);
			assertTrue("collator name not included", thisResult.indexOf(name) > 0);

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
			assertNotNull("store not created", perCaseCC._myData);

			final String newResult = perCaseCC.getResult(name, units);
			assertTrue("results wrongly performed", newResult.indexOf(perCaseString) > 0);

		}

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
			assertNull("store created", cc._myData);
			assertNotNull("items not stored", cc._myAverage);
			assertEquals("correct count not recorded", 4, cc._myAverage.counter);

			// and now the per-case instance
			// check we've stored them
			assertNotNull("store not created", cc2._myData);
			assertTrue("items not stored", cc2._myData.size() > 0);
			assertEquals("correct num cases not recorded", 2, cc2._myData.size());
		}

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
			assertNull("store created", cc._myData);
			assertTrue("items not stored", cc._myCounter > 0);
			assertEquals("correct count not recorded", 6, cc._myCounter);

			// and now the per-case instance
			// check we've stored them
			assertNotNull("store not created", cc2._myData);
			assertTrue("items not stored", cc2._myData.size() > 0);
			assertEquals("correct num cases not recorded", 2, cc2._myData.size());
		}

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
			assertNull("store created", cc._myData);
			assertNotNull("items not stored", cc._myList);
			assertEquals("correct count not recorded", 6, cc._myList.size());

			// and now the per-case instance
			// check we've stored them
			assertNotNull("store not created", cc2._myData);
			assertTrue("items not stored", cc2._myData.size() > 0);
			assertEquals("correct num cases not recorded", 2, cc2._myData.size());
		}

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
			assertNull("store created", cc._myData);
			assertNotNull("items not stored", cc._myList);
			assertEquals("correct count not recorded", 6, cc._myList.size());

			// and now the per-case instance
			// check we've stored them
			assertNotNull("store not created", cc2._myData);
			assertTrue("items not stored", cc2._myData.size() > 0);
			assertEquals("correct num cases not recorded", 2, cc2._myData.size());
		}

		public void testTopCollator() {
			BatchCollatorHelper helper = new BatchCollatorHelper("lister", true, BatchCollator.LIST, true, "feet");
			helper.submitResult("bb1", "a", new Double(12));
			helper.submitResult("bb2", "b", 122);
			helper.submitResult("bb3", "c", 32);
			helper.submitResult("bb4", "a", 2);
			helper.submitResult("bbb3", "a", "here");

			String res = helper.getResults();
			assertTrue("wrong listing", res.indexOf("a,12.0, 2.0, here") > 0);

			// and the inactive version
			helper = new BatchCollatorHelper("lister", true, BatchCollator.LIST, false, "feet");
			helper.submitResult("bbb1", "a", new Double(12));
			helper.submitResult("bbb2", "b", 122);
			helper.submitResult("bbb3", "c", 32);
			helper.submitResult("bbb4", "a", 2);
			helper.submitResult("bbb5", "a", "here");

			res = helper.getResults();
			assertNull("mistakenly returned result", res);
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
			assertNull("store created", cc._myData);
			assertNotNull("items not stored", cc._myList);
			assertEquals("correct count not recorded", 4, cc._myList.size());

			// and now the per-case instance
			// check we've stored them
			assertNotNull("store not created", cc2._myData);
			assertTrue("items not stored", cc2._myData.size() > 0);
			assertEquals("correct num cases not recorded", 2, cc2._myData.size());
		}
	}

	/**
	 * member class to count instances of results
	 */
	private static class CountCollator extends StatsCollator {

		int _myCounter;

		/**
		 * constructor, to get us going
		 *
		 * @param perCase
		 */
		public CountCollator(final boolean perCase) {
			super(perCase);
			_myCounter = 0;

		}

		/**
		 * we haven't processed this case yet, create a container for it
		 *
		 * @return the container for this case
		 */
		@Override
		protected Object createCase() {
			Object thisCase;
			thisCase = new Integer(0);
			return thisCase;
		}

		/**
		 * produce a results string from the supplied details
		 *
		 * @param caseId this case
		 * @param result the collated result
		 * @return string ready for output
		 */
		@Override
		protected String getThisResult(final Object caseId, final Object result) {
			final Integer thisInt = (Integer) result;
			final String thisResult = "" + caseId + "," + thisInt.intValue();
			return thisResult;
		}

		/**
		 * process this data item
		 *
		 * @param scenario_name
		 * @param thisCase      the current container for this data set
		 * @param datum         the current data item received
		 * @return the changed/updated container for the data set
		 */
		@Override
		protected Object processCaseResult(final String scenario_name, final Object thisCase, final double datum) {
			return processCaseResult(scenario_name, thisCase, null);
		}

		/**
		 * process this data item
		 *
		 * @param scenario_name
		 * @param thisCase      the current container for this data set
		 * @param datum         the current data item received
		 * @return the changed/updated container for the data set
		 */
		@Override
		protected Object processCaseResult(final String scenario_name, Object thisCase, final Object datum) {
			final Integer thisInt = (Integer) thisCase;
			// increment the counter
			int currentVal = thisInt.intValue();

			// put back into holder
			thisCase = new Integer(++currentVal);

			// and return it
			return thisCase;
		}

		/**
		 * ok, this hasn't got to be managed on a per-case basis, do the simple stuff
		 *
		 * @param scenario_name
		 * @param datum         the new data value
		 */
		@Override
		protected void processNonCaseResult(final String scenario_name, final double datum) {
			this.processNonCaseResult(scenario_name, null);
		}

		/**
		 * ok, this hasn't got to be managed on a per-case basis, do the simple stuff
		 *
		 * @param scenario_name
		 * @param datum         the new data value
		 */
		@Override
		protected void processNonCaseResult(final String scenario_name, final Object datum) {
			_myCounter++;
		}

		/**
		 * collate the results into a string
		 *
		 * @return
		 */
		@Override
		protected String retrieveResult() {
			return "Count:" + _myCounter;
		}
	}

	/**
	 * member class to average instances of results
	 */
	private static class FrequencyListCollator extends StatsCollator {

		// ////////////////////////////////////////////////
		// member variables
		// ////////////////////////////////////////////////
		protected HashMap<Object, Integer> _myList;

		// ////////////////////////////////////////////////
		// constructor
		// ////////////////////////////////////////////////

		/**
		 * constructor, to get us going
		 *
		 * @param perCase
		 */
		public FrequencyListCollator(final boolean perCase) {
			super(perCase);

		}

		// ////////////////////////////////////////////////
		// member methods
		// ////////////////////////////////////////////////

		/**
		 * we haven't processed this case yet, create a container for it
		 *
		 * @return the container for this case
		 */
		@Override
		protected Object createCase() {
			Object thisCase;
			thisCase = new HashMap<Object, Object>();

			return thisCase;
		}

		/**
		 * produce a results string from the supplied details
		 *
		 * @param caseId this case
		 * @param result the collated result
		 * @return string ready for output
		 */
		@Override
		@SuppressWarnings("unchecked")
		protected String getThisResult(final Object caseId, final Object result) {
			final HashMap<Object, Integer> thisList = (HashMap<Object, Integer>) result;
			return " " + caseId + "," + listToString(thisList);
		}

		private String listToString(final HashMap<Object, Integer> list) {
			String res = null;
			final Collection<Object> keys = list.keySet();
			for (final Iterator<Object> iterator = keys.iterator(); iterator.hasNext();) {
				final Object key = iterator.next();
				final Integer thisInt = list.get(key);
				if (res == null)
					res = " ";
				else
					res += ", ";

				res += "(" + key + ": " + thisInt.intValue() + ")";
			}
			return res;
		}

		/**
		 * process this data item
		 *
		 * @param scenario_name
		 * @param thisCase      the current container for this data set
		 * @param datum         the current data item received
		 * @return the changed/updated container for the data set
		 */
		@Override
		protected Object processCaseResult(final String scenario_name, final Object thisCase, final double datum) {
			return processCaseResult(scenario_name, thisCase, "" + datum);
		}

		/**
		 * process this data item
		 *
		 * @param scenario_name
		 * @param thisCase      the current container for this data set
		 * @param datum         the current data item received
		 * @return the changed/updated container for the data set
		 */
		@Override
		@SuppressWarnings("unchecked")
		protected Object processCaseResult(final String scenario_name, final Object thisCase, final Object datum) {

			final HashMap<Object, Integer> thisList = (HashMap<Object, Integer>) thisCase;

			// do we already hold this result?
			final Object item = thisList.get(datum);
			Integer thisItem;

			if (item == null)
				thisItem = new Integer(0);
			else
				thisItem = (Integer) item;

			final int newCount = thisItem.intValue() + 1;

			thisItem = new Integer(newCount);

			thisList.put(datum, thisItem);
			return thisList;
		}

		/**
		 * ok, this hasn't got to be managed on a per-case basis, do the simple stuff
		 *
		 * @param scenario_name
		 * @param datum         the new data value
		 */
		@Override
		protected void processNonCaseResult(final String scenario_name, final double datum) {
			processNonCaseResult(scenario_name, "" + datum);
		}

		/**
		 * ok, this hasn't got to be managed on a per-case basis, do the simple stuff
		 *
		 * @param scenario_name
		 * @param datum         the new data value
		 */
		@Override
		protected void processNonCaseResult(final String scenario_name, final Object datum) {
			if (_myList == null)
				_myList = new HashMap<Object, Integer>();

			// get our other class to update it
			processCaseResult(scenario_name, _myList, datum);
		}

		/**
		 * collate the results into a string
		 *
		 * @return
		 */
		@Override
		protected String retrieveResult() {
			return listToString(_myList);
		}
	}

	private static class ItemisedListCollator extends ListCollator {
		/**
		 * constructor, get on with it
		 *
		 * @param perCase
		 */
		public ItemisedListCollator(final boolean perCase) {
			super(perCase);
		}

		private String collatedResult(final String scenario_name, final double datum) {
			return collatedResult(scenario_name, "" + datum);
		}

		/**
		 * collate the two items into a string
		 *
		 * @param scenario_name
		 * @param datum
		 * @return
		 */
		private String collatedResult(final String scenario_name, final Object datum) {
			return "" + scenario_name + " , " + objectToString(datum) + "" + GeneralFormat.LINE_SEPARATOR;
		}

		/**
		 * process this data item
		 *
		 * @param scenario_name
		 * @param thisCase      the current container for this data set
		 * @param datum         the current data item received
		 * @return the changed/updated container for the data set
		 */
		@Override
		protected Object processCaseResult(final String scenario_name, final Object thisCase, final double datum) {
			return super.processCaseResult(scenario_name, thisCase, collatedResult(scenario_name, datum)); // To change
																											// body of
																											// overridden
																											// methods
																											// use
																											// File |
																											// Settings
																											// | File
																											// Templates.
		}

		/**
		 * process this data item
		 *
		 * @param scenario_name
		 * @param thisCase      the current container for this data set
		 * @param datum         the current data item received
		 * @return the changed/updated container for the data set
		 */
		@Override
		protected Object processCaseResult(final String scenario_name, final Object thisCase, final Object datum) {
			return super.processCaseResult(scenario_name, thisCase, collatedResult(scenario_name, datum)); // To change
																											// body of
																											// overridden
																											// methods
																											// use
																											// File |
																											// Settings
																											// | File
																											// Templates.
		}

		/**
		 * ok, this hasn't got to be managed on a per-case basis, do the simple stuff
		 *
		 * @param scenario_name
		 * @param datum         the new data value
		 */
		@Override
		protected void processNonCaseResult(final String scenario_name, final double datum) {
			super.processNonCaseResult(scenario_name, collatedResult(scenario_name, datum)); // To change body of
																								// overridden methods
																								// use File | Settings |
																								// File Templates.
		}

		/**
		 * ok, this hasn't got to be managed on a per-case basis, do the simple stuff
		 *
		 * @param scenario_name
		 * @param datum         the new data value
		 */
		@Override
		protected void processNonCaseResult(final String scenario_name, final Object datum) {
			super.processNonCaseResult(scenario_name, collatedResult(scenario_name, datum));
		}

	}

	/**
	 * member class to average instances of results
	 */
	private static class ListCollator extends StatsCollator {

		// ////////////////////////////////////////////////
		// member variables
		// ////////////////////////////////////////////////
		protected Vector<String> _myList;

		// ////////////////////////////////////////////////
		// constructor
		// ////////////////////////////////////////////////

		/**
		 * constructor, to get us going
		 *
		 * @param perCase
		 */
		public ListCollator(final boolean perCase) {
			super(perCase);

		}

		// ////////////////////////////////////////////////
		// member methods
		// ////////////////////////////////////////////////

		/**
		 * we haven't processed this case yet, create a container for it
		 *
		 * @return the container for this case
		 */
		@Override
		protected Object createCase() {
			Object thisCase;
			thisCase = new Vector<Object>();

			return thisCase;
		}

		/**
		 * produce a results string from the supplied details
		 *
		 * @param caseId this case
		 * @param result the collated result
		 * @return string ready for output
		 */
		@Override
		@SuppressWarnings("unchecked")
		protected String getThisResult(final Object caseId, final Object result) {
			final Vector<String> thisList = (Vector<String>) result;
			return "" + caseId + "," + listToString(thisList);
		}

		private String listToString(final Vector<String> list) {
			String res = null;
			for (int i = 0; i < list.size(); i++) {
				final String s = list.elementAt(i);
				if (res == null)
					res = s;
				else
					res += ", " + s;
			}
			return res;
		}

		/**
		 * convert to object to a number
		 *
		 * @param datum
		 * @return
		 */
		protected String objectToString(final Object datum) {
			String res;
			if (datum instanceof Integer || datum instanceof Long) {
				final Number thisNum = (Number) datum;
				res = "" + thisNum.longValue();
			} else if (datum instanceof Double || datum instanceof Float) {
				final Number thisNum = (Number) datum;
				res = "" + thisNum.doubleValue();
			} else

				res = datum.toString();

			return res;
		}

		/**
		 * process this data item
		 *
		 * @param scenario_name
		 * @param thisCase      the current container for this data set
		 * @param datum         the current data item received
		 * @return the changed/updated container for the data set
		 */
		@Override
		protected Object processCaseResult(final String scenario_name, final Object thisCase, final double datum) {
			return processCaseResult(scenario_name, thisCase, "" + datum);
		}

		/**
		 * process this data item
		 *
		 * @param scenario_name
		 * @param thisCase      the current container for this data set
		 * @param datum         the current data item received
		 * @return the changed/updated container for the data set
		 */
		@Override
		@SuppressWarnings("unchecked")
		protected Object processCaseResult(final String scenario_name, final Object thisCase, final Object datum) {

			final Vector<Object> thisList = (Vector<Object>) thisCase;
			thisList.add(objectToString(datum));
			return thisList;
		}

		/**
		 * ok, this hasn't got to be managed on a per-case basis, do the simple stuff
		 *
		 * @param scenario_name
		 * @param datum         the new data value
		 */
		@Override
		protected void processNonCaseResult(final String scenario_name, final double datum) {
			processNonCaseResult(scenario_name, "" + datum);
		}

		/**
		 * ok, this hasn't got to be managed on a per-case basis, do the simple stuff
		 *
		 * @param scenario_name
		 * @param datum         the new data value
		 */
		@Override
		protected void processNonCaseResult(final String scenario_name, final Object datum) {
			if (_myList == null)
				_myList = new Vector<String>(0, 1);

			_myList.add(objectToString(datum));
		}

		/**
		 * collate the results into a string
		 *
		 * @return
		 */
		@Override
		protected String retrieveResult() {
			return listToString(_myList);
		}
	}

	/**
	 * member class to help collate stats
	 */
	abstract private static class StatsCollator {
		// ////////////////////////////////////////////////
		// member variables
		// ////////////////////////////////////////////////

		/**
		 * my data
		 */
		protected final TreeMap<String, Object> _myData = new TreeMap<String, Object>();

		/**
		 * whether we do stats overall, or no a per-case basis
		 */
		private final boolean _perCase;

		// ////////////////////////////////////////////////
		// constructor
		// ////////////////////////////////////////////////
		/**
		 * build this stats collator
		 *
		 * @param perCase the name for it
		 */
		public StatsCollator(final boolean perCase) {
			_perCase = perCase;
		}

		/**
		 * process this new double value
		 *
		 * @param scenario_name
		 * @param caseIdentifier the id for this case
		 * @param datum          the new value to store
		 */
		public void addDatum(final String scenario_name, final String caseIdentifier, final double datum) {
			// are we on a per-case basis?
			if (_perCase || (caseIdentifier == null)) {
				// find any existing value
				final Object thisCase = getThisCase(caseIdentifier);

				// ok, process the new data value
				final Object newCase = processCaseResult(scenario_name, thisCase, datum);

				// and store it
				storeCase(newCase, caseIdentifier);
			} else {
				// ok, just process the simple data item
				processNonCaseResult(scenario_name, datum);
			}

		}

		/**
		 * store this data item
		 *
		 * @param scenario_name
		 * @param caseIdentifier the id of this permutation
		 * @param datum          the current data value
		 */
		public void addDatum(final String scenario_name, final String caseIdentifier, final Object datum) {
			// are we on a per-case basis?
			if (_perCase || (caseIdentifier == null)) {
				// find any existing value
				final Object thisCase = getThisCase(caseIdentifier);

				// ok, process the new data value
				final Object newCase = processCaseResult(scenario_name, thisCase, datum);

				// and store it
				storeCase(newCase, caseIdentifier);
			} else {
				// ok, just process the simple data item
				processNonCaseResult(scenario_name, datum);
			}

		}

		/**
		 * we haven't processed this case yet, create a container for it
		 *
		 * @return the container for this case
		 */
		protected abstract Object createCase();

		/**
		 * collate the results into a string
		 *
		 * @return
		 */
		public String getResult(final String name, final String units) {
			String res = null;

			// put in the header bits
			res = "Results for:" + name + MWC.Utilities.TextFormatting.GeneralFormat.LINE_SEPARATOR;
			res += " Data units:" + units + GeneralFormat.LINE_SEPARATOR;

			if (_perCase) {
				// insert the column header
				res += "Case, Results" + GeneralFormat.LINE_SEPARATOR;

				final Iterator<String> iter = _myData.keySet().iterator();
				while (iter.hasNext()) {
					final Object caseId = iter.next();
					final Object collatedResult = _myData.get(caseId);
					final String thisResult = getThisResult(caseId, collatedResult);
					res += thisResult + GeneralFormat.LINE_SEPARATOR;
				}
			} else {
				res += retrieveResult();
			}
			return res;
		}

		private Object getThisCase(final String caseIdentifier) {
			// find the matching counter
			Object thisCase = _myData.get(caseIdentifier);

			// create the counter if we didn't find it
			if (thisCase == null)
				thisCase = createCase();
			return thisCase;
		}

		/**
		 * collate a string from the supplied index and results object
		 *
		 * @param caseId
		 * @param result
		 * @return
		 */
		protected abstract String getThisResult(Object caseId, Object result);

		/**
		 * process this data item
		 *
		 * @param scenario_name
		 * @param thisCase      the current container for this data set
		 * @param datum         the current data item received
		 * @return the changed/updated container for the data set
		 */
		protected abstract Object processCaseResult(String scenario_name, Object thisCase, double datum);

		/**
		 * process this data item
		 *
		 * @param scenario_name
		 * @param thisCase      the current container for this data set
		 * @param datum         the current data item received
		 * @return the changed/updated container for the data set
		 */
		protected abstract Object processCaseResult(String scenario_name, Object thisCase, Object datum);

		/**
		 * ok, this hasn't got to be managed on a per-case basis, do the simple stuff
		 *
		 * @param scenario_name
		 * @param datum         the new data value
		 */
		protected abstract void processNonCaseResult(String scenario_name, double datum);

		/**
		 * ok, this hasn't got to be managed on a per-case basis, do the simple stuff
		 *
		 * @param scenario_name
		 * @param datum         the new data value
		 */
		protected abstract void processNonCaseResult(String scenario_name, Object datum);

		/**
		 * produce a string from the collated results
		 *
		 * @return
		 */
		protected abstract String retrieveResult();

		private void storeCase(final Object newCase, final String caseIdentifier) {
			// and replace it (it will automagically remove the old one anyway)
			_myData.put(caseIdentifier, newCase);
		}
	}

	/**
	 * whether to store data on a per-case basis
	 */
	private boolean _perCase;

	/**
	 * the type of collation to perform
	 */
	private String _collationStrategy;

	// ////////////////////////////////////////////////
	// member methods
	// ////////////////////////////////////////////////

	/**
	 * our collator
	 */
	private StatsCollator _myCollator;

	/**
	 * the name of this collator
	 */
	private String _myName;

	/**
	 * the directory to write to
	 */
	private File _myDirectory;

	/**
	 * the filename to write to
	 */
	private String _myFilename;

	// ////////////////////////////////////////////////
	// accessor methods
	// ////////////////////////////////////////////////

	/**
	 * whether the batch collator is active
	 */
	private boolean _isActive;

	/**
	 * the units for the data we're storing
	 */
	private String _myUnits;

	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////
	/**
	 * constructor
	 *
	 * @param perCase           whether to collate data on a per-case basis
	 * @param collationStrategy the way in which we should collate data
	 */
	public BatchCollatorHelper(final String name, final boolean perCase, final String collationStrategy,
			final boolean _isActive, final String units) {
		setActive(_isActive);
		if (getActive()) {
			_myName = name;
			_myUnits = units;
			this.setPerCase(perCase);
			this.setCollationStrategy(collationStrategy);
		}
	}

	/**
	 * factory method to create the right collator
	 *
	 * @param type    the type of collation required
	 * @param perCase whether the user wants per-case stats
	 * @return
	 */
	private StatsCollator createCollator(final String type, final boolean perCase) {
		StatsCollator res = null;
		if (type.equals(BatchCollator.AVERAGE))
			res = new AverageCollator(perCase);
		else if (type.equals(BatchCollator.COUNT))
			res = new CountCollator(perCase);
		else if (type.equals(BatchCollator.LIST))
			res = new ListCollator(perCase);
		else if (type.equals(BatchCollator.FREQUENCY_LIST))
			res = new FrequencyListCollator(perCase);
		else if (type.equals(BatchCollator.ITEMIZED_LIST))
			res = new ItemisedListCollator(perCase);

		return res;
	}

	/**
	 * ok, do the actual legwork of creating the file
	 *
	 * @return the stream created
	 * @throws java.io.IOException
	 */
	private FileWriter createOutputFileWriter() {
		String theName = null;
		theName = _myFilename;

		// check we have the output directory
		if (_myDirectory != null)
			_myDirectory.mkdirs();

		FileWriter os = null;
		try {
			os = new FileWriter(new File(_myDirectory, theName));
		} catch (final IOException e) {
			System.err.println("Suspect file not found when writing batch collator results");
			e.printStackTrace();
		}
		return os;
	}

	/**
	 * get whether this helper is active
	 *
	 * @return yes/no
	 */
	public boolean getActive() {
		return _isActive;
	}

	public String getCollationStrategy() {
		return _collationStrategy;
	}

	public String getFilename() {
		return _myFilename;
	}

	public boolean getPerCase() {
		return _perCase;
	}

	/**
	 * get a string containing the results from this collator
	 *
	 * @return string containing results
	 */
	String getResults() {
		String res = null;
		if (getActive())
			res = _myCollator.getResult(_myName, _myUnits);
		return res;
	}

	/**
	 * find out whether stats should be collated on a per-case basis
	 *
	 * @return yes/no
	 */
	public boolean isPerCase() {
		return _perCase;
	}

	/**
	 * set whether this helper is active
	 *
	 * @param isActive yes/no
	 */
	public void setActive(final boolean isActive) {
		this._isActive = isActive;
	}

	// ////////////////////////////////////////////////
	// embedded class capable of collating data
	// ////////////////////////////////////////////////

	public void setCollationStrategy(final String collationStrategy) {
		if (getActive()) {
			this._collationStrategy = collationStrategy;

			// and create the collator
			_myCollator = createCollator(_collationStrategy, _perCase);
		}

	}

	/**
	 * store the directory to write to
	 *
	 * @param dest
	 */
	public void setDirectory(final File dest) {
		_myDirectory = dest;
	}

	/**
	 * store the filename to write to
	 *
	 * @param fileName
	 */
	public void setFileName(final String fileName) {
		_myFilename = fileName;
	}

	/**
	 * specify whether stats should be recorded on a per-case basis
	 *
	 * @param perCase yes/no
	 */
	public void setPerCase(final boolean perCase) {
		this._perCase = perCase;
	}

	/**
	 * submit new data item
	 *
	 * @param scenario_name
	 * @param caseId        the id of this case
	 * @param datum         the new data item
	 */
	public void submitResult(final String scenario_name, final String caseId, final double datum) {
		if (getActive())
			_myCollator.addDatum(scenario_name, caseId, datum);
	}

	/**
	 * submit new data item
	 *
	 * @param caseId the id of this case
	 * @param datum  the new data item
	 */
	public void submitResult(final String scenario_name, final String caseId, final Object datum) {
		if (getActive())
			_myCollator.addDatum(scenario_name, caseId, datum);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * write our output to file
	 *
	 * @param headerContent
	 */
	public void writeOutput(final String headerContent) {
		if (getActive())
			try {
				FileWriter _myStream = createOutputFileWriter();
				if (_myStream != null) {
					_myStream.write(headerContent);
					_myStream.write(_myCollator.getResult(_myName, _myUnits));
					_myStream.close();
					_myStream = null;
				}
			} catch (final IOException e) {
				e.printStackTrace(); // To change body of catch statement use File |
										// Settings | File Templates.
			}
	}
}
