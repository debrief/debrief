/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Oct 31, 2001
 * Time: 2:11:33 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.MonteCarlo;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ASSET.Server.MonteCarlo.Components.Choice;
import ASSET.Util.RandomGenerator;

import java.util.Vector;
import java.util.Iterator;

public final class XMLChoice implements XMLOperation
{
	/***************************************************************
	 * member variables
	 ***************************************************************/

	/**
	 * the list of values to choose from
	 * 
	 */
	private final Vector<String> _myList;

	/**
	 * the element we will use for this permutation
	 * 
	 */
	private String _currentVal;

	/**
	 * the name given to this set of choices
	 * 
	 */
	private String _myName;

	/**
	 * the title of the name field
	 * 
	 */
	private final static String NAME = "name";

	/**
	 * the tag for a single value
	 * 
	 */
	private static final String VALUE_TYPE = "Value";

	/**
	 * the attribute within which a value is stored
	 * 
	 */
	private static final String VALUE = "value";

	/***************************************************************
	 * constructor
	 ***************************************************************/
	public XMLChoice(final Element element)
	{
		this();

		// you know, get the stuff
		_myName = element.getAttribute(NAME);

		// have a fish around inside it

		final NodeList vars = element.getElementsByTagName(VALUE_TYPE);

		final int len = vars.getLength();
		for (int i = 0; i < len; i++)
		{
			final Element thisE = (Element) vars.item(i);
			final String thisName = thisE.getAttribute(VALUE);
			_myList.add(thisName);
		}

		// stick in a random variable to start us off
		newPermutation();
	}

	private XMLChoice(final XMLChoice other)
	{
		this();

		// pass through and copy components
		final Iterator<String> iter = other._myList.iterator();
		while (iter.hasNext())
		{
			final String thisVal = (String) iter.next();
			_myList.add(thisVal);
		}
	}

	private XMLChoice()
	{
		_myList = new Vector<String>();
	}

	/***************************************************************
	 * member methods
	 ***************************************************************/
	/**
	 * produce a new value for this operation
	 * 
	 */
	public final void newPermutation()
	{
		// gen random value in the correct range (we will always be less than the
		// upper limit)
		double randDbl = ASSET.Util.RandomGenerator.generateRandomNumber(0, _myList
				.size(), RandomGenerator.UNIFORM);

		// convert to int
		final int index = (int) randDbl;

		_currentVal = (String) _myList.get(index);
	}

	/**
	 * return the current value of this permutation
	 * 
	 */
	public final String getValue()
	{
		return _currentVal;
	}

	/**
	 * clone this object
	 * 
	 */
	public final Object clone()
	{
		final XMLChoice res = new XMLChoice(this);
		return res;
	}

	public final String getName()
	{
		return _myName;
	}

	/**
	 * merge ourselves with the supplied operation
	 * 
	 */
	public final void merge(final XMLOperation other)
	{
	}

	public static void main(final String[] args)
	{
		final XMLChoice ch = new XMLChoice();

		ch._myList.add("a1");
		ch._myList.add("a2");
		ch._myList.add("a3");
		ch._myList.add("a4");

		ch.newPermutation();

		final String val = ch.getValue();
		System.out.println(val);

	}

	public final int size()
	{
		return _myList.size();
	}

	public final String get(final int index)
	{
		return (String) _myList.get(index);
	}

	public static class ChoiceTest extends junit.framework.TestCase
	{
		public ChoiceTest(final String val)
		{
			super(val);
		}

		public void testGenerate()
		{
			XMLChoice choice = new XMLChoice();
			choice._myList.add("a");
			choice._myList.add("b");
			choice._myList.add("c");

			assertEquals("they got stored", 3, choice._myList.size(), 0);

			assertTrue("found a", choice._myList.contains("a"));
			assertTrue("found b", choice._myList.contains("b"));
			assertTrue("found c", choice._myList.contains("c"));
		}

		public void test3WayDistribution()
		{
			XMLChoice choice = new XMLChoice();
			choice._myList.add("a");
			choice._myList.add("b");
			choice._myList.add("c");

			int size = 1000000;
			int numA = 0;
			int numB = 0;
			int numC = 0;

			for (int i = 0; i < size; i++)
			{
				choice.newPermutation();
				String thisVal = choice.getValue();
				if (thisVal.equals("a"))
				{
					numA++;
				}
				if (thisVal.equals("b"))
				{
					numB++;
				}
				if (thisVal.equals("c"))
				{
					numC++;
				}
			}

			// check the dist
			assertEquals("correct num of a", size / 3, numA, 2000);
			assertEquals("correct num of b", size / 3, numB, 2000);
			assertEquals("correct num of c", size / 3, numC, 2000);

		}

		public void test2WayDistribution()
		{
			XMLChoice choice = new XMLChoice();
			choice._myList.add("a");
			choice._myList.add("b");

			int size = 1000000;
			int numA = 0;
			int numB = 0;

			for (int i = 0; i < size; i++)
			{
				choice.newPermutation();
				String thisVal = choice.getValue();
				if (thisVal.equals("a"))
				{
					numA++;
				}
				if (thisVal.equals("b"))
				{
					numB++;
				}
			}

			// check the dist
			assertEquals("correct num of a", size / 2, numA, 2000);
			assertEquals("correct num of b", size / 2, numB, 2000);

		}
	}

}
