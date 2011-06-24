/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.util.device;

// Standard imports
import java.io.InvalidClassException;

// Application specific imports
// none

/**
 * A generalised class used to dynamically load other classes according to a
 * preset set of rules.
 * <P>
 * 
 * The class loader uses the CLASSPATH setting to locate and load a given class.
 * If the appropriate methods are called, it will attempt to confirm that the
 * class conforms to a specific interface or base class before actually
 * instantiating that class. Various options are provided for this and the
 * loader automatically checks and issues the appropriate errors.
 */
class DynamicClassLoader
{
	/** Message in the exceptions when the class name is not useful */
	private static final String NULL_NAME_MSG = "Name supplied is null or zero length";

	/** Message in the exceptions when the base class name is not useful */
	private static final String NULL_BASE_MSG = "Base class name supplied is null or zero length";

	/**
	 * Message in the exceptions when a class does not implement the correct base
	 * class required by the caller.
	 */
	private static final String BACKGROUND_MSG = "The class does not implement the correct base class";

	/** Message in the exceptions when a class fails to load correctly */
	private static final String INIT_MSG = "The class failed to load correctly";

	/**
	 * Private constructor to prevent instantiation of this static only class.
	 */
	private DynamicClassLoader()
	{
	}

	/**
	 * Load the named class with no checking of the background. The limitation to
	 * the loading and instantiation process is that the class must have a public
	 * default constructor. As this method does not take any arguments,
	 * constructors that do require parameters cannot be called.
	 * 
	 * @param name
	 *          The fully qualified name of the class to be loaded
	 * @returns An instance of the named class if it could be found.
	 * @throws NullPointerException
	 *           The class name supplied is null or zero length
	 * @throws ClassNotFoundException
	 *           We couldn't locate the class anywhere
	 * @throws InvalidClassException
	 *           The class could not be instantiated either due to internal errors
	 *           or no default constructor
	 */
	public static Object loadBasicClass(String name)
			throws ClassNotFoundException, InvalidClassException
	{
		if ((name == null) || (name.trim().length() == 0))
			throw new NullPointerException(NULL_NAME_MSG);

		Object ret_val = null;

		try
		{
			Class<?> new_class = Class.forName(name);
			ret_val = new_class.newInstance();
		}
		catch (ClassNotFoundException cnfe)
		{
			// Just rethrow this particular error. Done to save more mess
			// later on in the catch list.
			throw cnfe;
		}
		catch (Exception e)
		{
			throw new InvalidClassException(INIT_MSG);
		}
		catch (LinkageError le)
		{
			throw new InvalidClassException(INIT_MSG);
		}

		return ret_val;
	}

	/**
	 * Load the class that has the given class as a super class. This will check
	 * for both the interface and derived class being of the given type.
	 * 
	 * @param name
	 *          The fully qualified name of the class to be loaded
	 * @param base
	 *          The fully qualified name of the base class to be checked against
	 * @returns An instance of the named class if it could be found.
	 * @throws NullPointerException
	 *           The class name or base class name supplied is null or zero length
	 * @throws ClassNotFoundException
	 *           We couldn't locate the class anywhere
	 * @throws InvalidClassException
	 *           The class could not be instantiated either due to internal errors
	 *           or no default constructor
	 */
	public static Object loadCheckedClass(String name, String base)
			throws ClassNotFoundException, InvalidClassException
	{
		if ((name == null) || (name.trim().length() == 0))
			throw new NullPointerException(NULL_NAME_MSG);

		if ((base == null) || (base.trim().length() == 0))
			throw new NullPointerException(NULL_BASE_MSG);

		Object ret_val = null;

		try
		{
			Class<?> base_class = Class.forName(base);
			ret_val = loadCheckedClass(name, base_class);
		}
		catch (LinkageError le)
		{
			throw new InvalidClassException(INIT_MSG);
		}

		return ret_val;
	}

	/**
	 * Load the class that has the given class as a super class. This will check
	 * for both the interface and derived class being of the given type.
	 * 
	 * @param name
	 *          The fully qualified name of the class to be loaded
	 * @param base
	 *          The fully qualified name of the base class to be checked against
	 * @returns An instance of the named class if it could be found.
	 * @throws NullPointerException
	 *           The class name or base class name supplied is null or zero length
	 * @throws ClassNotFoundException
	 *           We couldn't locate the class anywhere
	 * @throws InvalidClassException
	 *           The class could not be instantiated either due to internal errors
	 *           or no default constructor
	 */
	public static Object loadCheckedClass(String name, Class<?> base)
    throws ClassNotFoundException, InvalidClassException
  {
    if((name == null) || (name.trim().length() == 0))
      throw new NullPointerException(NULL_NAME_MSG);

    if(base == null)
      throw new NullPointerException(NULL_BASE_MSG);

    Object ret_val = null;
    boolean check_ok = true;

    try
    {
      Class<?> new_class = Class.forName(name);
      check_ok = backgroundChecks(new_class, base);
      if(check_ok)
        ret_val = new_class.newInstance();
    }
    catch(Exception e)
    {
      throw new InvalidClassException(INIT_MSG);
    }

    if(!check_ok)
      throw new InvalidClassException(BACKGROUND_MSG);

    return ret_val;
  }

	/**
	 * Check the current class to see if it conforms to the required base class
	 * type. This method may be recursive if the class is derived from more than
	 * one class. It will also recursively check the derived interfaces of the
	 * interfaces that this class implements
	 * 
	 * @param current
	 *          The class to be checked for conformity
	 * @param source
	 *          The class to be checked against
	 * @return true if the the class is or implements the base class.
	 */
	private static boolean backgroundChecks(Class<?> current, Class<?> source)
	{
		boolean ret_val = false;

		ret_val = source.isAssignableFrom(current);

		// if this is not an instance of the source class then let's check
		// the base class (if it has one) for a match.
		if (!ret_val)
		{
			Class<?> base = current.getSuperclass();
			if (base != null)
				ret_val = backgroundChecks(base, source);
		}

		return ret_val;
	}
}
