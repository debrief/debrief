package org.mwc.cmap.grideditor.command;

import java.lang.reflect.Method;

import org.mwc.cmap.core.property_support.DebriefProperty;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;

import MWC.GUI.TimeStampedDataItem;

/**
 * Very simplified version of the Apache Commons BeanUtil's helpers.
 * <p>
 * Provides utility methods to invoke setters/getters of some bean' property.
 */
public class BeanUtil
{

	/**
	 * Reflectively invokes getter associated with given
	 * {@link GriddableItemDescriptor} on given subject
	 * {@link TimeStampedDataItem}
	 * 
	 * @param item
	 *          subject item to invoke getter on
	 * @param descriptor
	 *          meta-data describing the bean property
	 * @param resultType
	 *          desired result type, used to avoid casts at the caller side only
	 * @return the value of bean property for given subject
	 * @throws IllegalArgumentException
	 *           if descriptor is not applicable to given item or not configured
	 *           properly (e.g, if getter not accessible, or does not conform to
	 *           Beans specification, etc)
	 * @throws ClassCastException
	 *           if desired result type is not compatible with actual value
	 * @throws NullPointerException
	 *           if given item is <code>null</code>
	 */
	public static <T> T getItemValue(TimeStampedDataItem item,
			GriddableItemDescriptor descriptor, Class<T> resultType)
	{
		if (item == null)
		{
			throw new NullPointerException();
		}
		String getterName = getGetterName(descriptor);
		Method getter;
		try
		{
			getter = item.getClass().getMethod(getterName);
		}
		catch (NoSuchMethodException e)
		{
			throw new IllegalArgumentException(//
					"Descriptor: " + descriptor.getTitle() + //
							" is not applicable to item: " + item.getClass() + //
							", there are no getters with name : " + getterName);
		}
		Class<?> actualGetterType = getter.getReturnType();
		// if (!descriptor.getType().isAssignableFrom(actualGetterType)) {
		// throw new IllegalArgumentException(//
		// "Descriptor: " + descriptor.getTitle() + //
		// " is not applicable to item: " + item.getClass() + ", expected type: " +
		// descriptor.getType() + //
		// ", actual getter type: " + actualGetterType);
		// }
		if (!resultType.isAssignableFrom(actualGetterType))
		{
			// take auto-boxing into account
			boolean autoBoxable = actualGetterType.isPrimitive()
					&& resultType.equals(Object.class);
			if (!autoBoxable)
			{
				autoBoxable = actualGetterType.isPrimitive()
						&& resultType.equals(Number.class);
			}
			if (!autoBoxable)
			{
				throw new ClassCastException(//
						"Can not cast actual getter type: " + actualGetterType + //
								" to desired type " + resultType);
			}
		}

		Object result;
		try
		{
			result = getter.invoke(item);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(//
					"Can't invoke getter " + getterName + //
							" for object " + item + //
							" of class " + item.getClass(), //
					e);
		}
		return resultType.cast(result);
	}

	/**
	 * Reflectively invokes setter associated with given
	 * {@link GriddableItemDescriptor} on given subject
	 * {@link TimeStampedDataItem} with given value (<code>null</code> value
	 * allowed).
	 * 
	 * NOTE: this method is intentionally package local, all items modifications
	 * should go through the {@link SetDescriptorValueOperation}.
	 * 
	 * @param item
	 *          subject item to invoke setter on
	 * @param descriptor
	 *          meta-data describing the bean property
	 * @param value
	 *          new value to set
	 * 
	 * @throws IllegalArgumentException
	 *           if descriptor is not applicable to given item or not configured
	 *           properly (e.g, if setter is not accessible, or does not conform
	 *           to Beans specification, etc)
	 * @throws ClassCastException
	 *           if actual value type is not compatible with descriptor meta-data
	 * @throws NullPointerException
	 *           if given item is <code>null</code>
	 */
	static void setItemValue(TimeStampedDataItem item,
			GriddableItemDescriptor descriptor, Object value)
	{
		if (item == null)
		{
			throw new NullPointerException();
		}

		// convert the value from text editor to target datat ype
		value = descriptor.getEditor().translateFromSWT(value);

		if (value != null
				&& !descriptor.getType().isAssignableFrom(value.getClass()))
		{
			// not that strong of a check, but we will fail later (on invocation) in
			// any case
			boolean autoUnboxable = descriptor.getType().isPrimitive()
					&& Number.class.isAssignableFrom(value.getClass());
			if (!autoUnboxable)
			{
				throw new ClassCastException(//
						"Can not cast actual value of type: " + value.getClass() + //
								" to descriptor type " + descriptor.getType());
			}
		}

		String setterName = getSetterName(descriptor);
		Method setter = null;
		try
		{
			setter = item.getClass().getMethod(setterName, descriptor.getType());
		}
		catch (NoSuchMethodException e)
		{
		}

		// did it work?
		if (setter == null)
		{
			// is it because we're using a complex primitive?
			if (descriptor.getType() == Double.class)
			{
				try
				{
					setter = item.getClass().getMethod(setterName, double.class);
				}
				catch (NoSuchMethodException e)
				{
				}
			}
		}

		if (setter == null)
			throw new IllegalArgumentException(//
					"Descriptor: " + descriptor.getTitle() + //
							" is not applicable to item: " + item.getClass() + //
							", there are no setters with name : " + setterName + //
							" and parameter type : " + descriptor.getType());

		try
		{
			setter.invoke(item, value);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException(//
					"Can't invoke setter " + setterName + //
							" for object " + item + //
							" of class " + item.getClass() + //
							" and actual parameter : " + value, e);
		}
	}

	public static Object getItemValue(TimeStampedDataItem item,
			GriddableItemDescriptor descriptor)
	{
		return getItemValue(item, descriptor, Object.class);
	}

	private static String getGetterName(GriddableItemDescriptor descriptor)
	{
		return "get" + capitalize(descriptor.getName());
	}

	private static String getSetterName(GriddableItemDescriptor descriptor)
	{
		return "set" + capitalize(descriptor.getName());
	}

	private static String capitalize(String text)
	{
		if (text == null)
		{
			throw new NullPointerException();
		}
		if (text.length() == 0)
		{
			return text;
		}
		char firstChar = text.charAt(0);
		return Character.isUpperCase(firstChar) ? text : Character
				.toUpperCase(firstChar)
				+ text.substring(1);
	}

}
