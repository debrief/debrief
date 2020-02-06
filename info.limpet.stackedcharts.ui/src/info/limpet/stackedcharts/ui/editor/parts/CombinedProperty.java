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
package info.limpet.stackedcharts.ui.editor.parts;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.PropertyDescriptor;
import org.eclipse.emf.edit.ui.provider.PropertySource;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

class CombinedProperty implements IPropertySource {
	static final private ComposedAdapterFactory adapterFactory = createAdapterFactory();

	private static ComposedAdapterFactory createAdapterFactory() {
		final ComposedAdapterFactory factory = new ComposedAdapterFactory(
				ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
		factory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

		return factory;
	}

	/**
	 * utility class to add all properties to supplied list
	 *
	 * @param result
	 * @param object
	 * @param source
	 * @param name
	 */
	private static void storeProperties(final Collection<IPropertyDescriptor> result, final Object object,
			final IItemPropertySource source, final String name) {
		for (final IItemPropertyDescriptor itemPropertyDescriptor : source.getPropertyDescriptors(object)) {
			result.add(new PropertyDescriptor(object, itemPropertyDescriptor) {
				@Override
				public String getCategory() {
					return name;
				};

			});
		}
	}

	final private Object parentObject;
	final private IPropertySource parentPropertySource;
	final private IItemPropertySource parentItemPropertySource;
	final private Object childObject;
	final private IPropertySource childPropertySource;

	final private IItemPropertySource childItemPropertySource;

	final private String childName;

	public CombinedProperty(final Object parent, final Object child, final String childName) {
		this.parentObject = parent;
		this.childObject = child;
		this.childName = childName;

		parentItemPropertySource = (IItemPropertySource) adapterFactory.adapt(parent, IItemPropertySource.class);
		parentPropertySource = new PropertySource(parent, parentItemPropertySource);

		childItemPropertySource = (IItemPropertySource) adapterFactory.adapt(child, IItemPropertySource.class);
		childPropertySource = new PropertySource(child, childItemPropertySource);

	}

	@Override
	public Object getEditableValue() {
		return parentObject;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		final Collection<IPropertyDescriptor> result = new ArrayList<IPropertyDescriptor>();

		// ok, start with the main properties
		storeProperties(result, parentObject, parentItemPropertySource, "Core");

		// and now the child ones (probably style related)
		storeProperties(result, childObject, childItemPropertySource, childName);

		// ok, return as array
		return result.toArray(new IPropertyDescriptor[result.size()]);
	}

	@Override
	public Object getPropertyValue(final Object id) {
		return getSourceById(id).getPropertyValue(id);
	}

	private IPropertySource getSourceById(final Object id) {
		final IPropertyDescriptor[] propertyDescriptors = childPropertySource.getPropertyDescriptors();
		for (final IPropertyDescriptor iPropertyDescriptor : propertyDescriptors) {
			if (iPropertyDescriptor.getId().equals(id)) {
				return childPropertySource;
			}
		}
		return parentPropertySource;
	}

	@Override
	public boolean isPropertySet(final Object id) {
		return getSourceById(id).isPropertySet(id);
	}

	@Override
	public void resetPropertyValue(final Object id) {
		getSourceById(id).resetPropertyValue(id);

	}

	@Override
	public void setPropertyValue(final Object id, final Object value) {
		getSourceById(id).setPropertyValue(id, value);

	}
}