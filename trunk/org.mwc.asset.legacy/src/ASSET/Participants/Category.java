package ASSET.Participants;

import java.awt.*;

import junit.framework.TestCase;

/**
 * Title: Description: Copyright: Copyright (c) 2001 Company:
 * 
 * @author
 * @version 1.0
 */

public class Category implements java.io.Serializable
{

	/**
	 * the serializable id of this type
	 */
	private static final long serialVersionUID = 33;

	@Override
	public boolean equals(Object arg0)
	{
		return this.hashCode() == arg0.hashCode();
	}

	@Override
	public int hashCode()
	{
		return environment.hashCode() * force.hashCode() * type.hashCode();
	}

	public interface Force
	{
		/**
		 * force
		 */
		static public final String RED = "RED";
		static public final String BLUE = "BLUE";
		public static final String GREEN = "GREEN";
	}

	public interface Environment
	{
		/**
		 * environment
		 */
		static public final String SUBSURFACE = "SUBSURFACE";
		static public final String SURFACE = "SURFACE";
		public static final String AIRBORNE = "AIRBORNE";
		public static final String CROSS = "CROSS";
	}

	public interface Type
	{

		/**
		 * submarine types
		 */
		static public final String SUBMARINE = "SUBMARINE";
		public static final String MINISUB = "MINISUB";
		public static final String TORPEDO = "TORPEDO";
		public static final String SONAR_BUOY = "SONAR_BUOY";

		/**
		 * surface types
		 */
		static public final String CARRIER = "CARRIER";
		static public final String FRIGATE = "FRIGATE";
		static public final String DESTROYER = "DESTROYER";
		public static final String TROOP_CARRIER = "TROOP_CARRIER";
		public static final String OILER = "OILER";
		public static final String FISHING_VESSEL = "FISHING_VESSEL";

		/**
		 * airborne types
		 */
		public static final String HELO = "HELICOPTER";
		public static final String MPA = "MPA";
		static public final String AV_MISSILE = "AV_MISSILE";

	}

	/**
	 * the lists of objects
	 */
	static private java.util.Vector<String> _forces;
	static private java.util.Vector<String> _environments;
	static private java.util.Vector<String> _types;

	/**
	 * our local objects
	 */
	private String type;
	private String environment;
	private String force;

	// ////////////////////////////////////////////////////////////////////
	// constructors
	// ////////////////////////////////////////////////////////////////////
	public Category(final String force, final String environment,
			final String type)
	{
		setForce(force);
		setType(type);
		setEnvironment(environment);
	}

	public Category()
	{
	}

	// ////////////////////////////////////////////////////////////////////
	// property getter/setters
	// ////////////////////////////////////////////////////////////////////

	public String getType()
	{
		return type;
	}

	public void setType(final String newType)
	{
		type = newType;
	}

	public void setEnvironment(final String newEnvironment)
	{
		environment = newEnvironment;
	}

	public String getEnvironment()
	{
		return environment;
	}

	public void setForce(final String newForce)
	{
		force = newForce;
	}

	public String getForce()
	{
		return force;
	}

	public boolean isA(final String type1)
	{
		final boolean res;

		if ((getType() == type1) || (getForce() == type1)
				|| (getEnvironment() == type1))
			res = true;
		else
			res = false;

		return res;
	}

	public String toString()
	{
		return "Category force:" + getForce() + " type:" + getType() + " env:"
				+ getEnvironment();
	}

	public String toShortString()
	{
		return getForce() + " " + getType().substring(0, 4) + " "
				+ getEnvironment().substring(0, 3);
	}

	static public java.util.Vector<String> getTypes()
	{
		checkLists();
		return _types;
	}

	static public java.util.Vector<String> getForces()
	{
		checkLists();
		return _forces;
	}

	static public java.util.Vector<String> getEnvironments()
	{
		checkLists();
		return _environments;
	}

	static private void checkLists()
	{
		if (_types == null)
		{
			_forces = new java.util.Vector<String>(3, 1);
			_forces.addElement(Force.RED);
			_forces.addElement(Force.GREEN);
			_forces.addElement(Force.BLUE);

			_environments = new java.util.Vector<String>(4, 1);
			_environments.addElement(Environment.SUBSURFACE);
			_environments.addElement(Environment.SURFACE);
			_environments.addElement(Environment.AIRBORNE);
			_environments.addElement(Environment.CROSS);

			_types = new java.util.Vector<String>(6, 1);
			_types.addElement(Type.SUBMARINE);
			_types.addElement(Type.MINISUB);
			_types.addElement(Type.TORPEDO);
			_types.addElement(Type.SONAR_BUOY);
			_types.addElement(Type.CARRIER);
			_types.addElement(Type.FRIGATE);
			_types.addElement(Type.DESTROYER);
			_types.addElement(Type.TROOP_CARRIER);
			_types.addElement(Type.OILER);
			_types.addElement(Type.FISHING_VESSEL);
			_types.addElement(Type.HELO);
			_types.addElement(Type.MPA);
			_types.addElement(Type.AV_MISSILE);
		}
	}

	static public String checkEnv(final String val)
	{
		String res = null;

		final java.util.Iterator<String> it = getEnvironments().iterator();
		while (it.hasNext())
		{
			final String thisC = (String) it.next();
			if (thisC.equals(val))
			{
				res = thisC;
				break;
			}

		}
		return res;
	}

	static public String checkForce(final String val)
	{
		String res = null;

		final java.util.Iterator<String> it = getForces().iterator();
		while (it.hasNext())
		{
			final String thisC = (String) it.next();
			if (thisC.equals(val))
			{
				res = thisC;
				break;
			}

		}

		return res;
	}

	static public String checkType(final String val)
	{
		String res = null;

		final java.util.Iterator<String> it = getTypes().iterator();
		while (it.hasNext())
		{
			final String thisC = (String) it.next();
			if (thisC.equals(val))
			{
				res = thisC;
				break;
			}

		}
		return res;
	}

	private static Color getColorFor(final String category)
	{
		if (category.equals(ASSET.Participants.Category.Force.RED))
			return java.awt.Color.red;
		else if (category.equals(ASSET.Participants.Category.Force.BLUE))
			return java.awt.Color.blue;
		else
			return java.awt.Color.green;
	}

	static public Color getColorFor(final Category theCategory)
	{
		return getColorFor(theCategory.getForce());
	}

	/**
	 * and here's how to test it
	 * 
	 * @author ianmayo
	 * 
	 */
	public static class EqualityTest extends TestCase
	{
		public void testEquals()
		{
			Category catOne = new Category(Force.BLUE, Environment.SURFACE,
					Type.FRIGATE);
			Category catTwo = new Category(Force.RED, Environment.SURFACE,
					Type.FRIGATE);
			Category catThree = new Category(Force.BLUE, Environment.SURFACE,
					Type.FRIGATE);

			assertNotSame("not equal", catOne, catTwo);
			assertTrue(" equal", catOne.equals(catThree));
			assertTrue(" equal", catThree.equals(catOne));
			assertFalse(" equal", catOne.equals(catTwo));
		}
	}
}