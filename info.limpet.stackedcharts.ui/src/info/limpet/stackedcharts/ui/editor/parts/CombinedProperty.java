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

class CombinedProperty implements IPropertySource
{
  final private Object parentObject;
  final private IPropertySource parentPropertySource;
  final private IItemPropertySource parentItemPropertySource;
  final private Object childObject;
  final private IPropertySource childPropertySource;
  final private IItemPropertySource childItemPropertySource;
  final private String childName;
  static final private ComposedAdapterFactory adapterFactory =
      createAdapterFactory();

  public CombinedProperty(final Object parent, final Object child,
      final String childName)
  {
    this.parentObject = parent;
    this.childObject = child;
    this.childName = childName;

    parentItemPropertySource =
        (IItemPropertySource) adapterFactory.adapt(parent,
            IItemPropertySource.class);
    parentPropertySource = new PropertySource(parent, parentItemPropertySource);

    childItemPropertySource =
        (IItemPropertySource) adapterFactory.adapt(child,
            IItemPropertySource.class);
    childPropertySource = new PropertySource(child, childItemPropertySource);

  }

  private static ComposedAdapterFactory createAdapterFactory()
  {
    final ComposedAdapterFactory factory =
        new ComposedAdapterFactory(
            ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
    factory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

    return factory;
  }

  private IPropertySource getSourceById(Object id)
  {
    IPropertyDescriptor[] propertyDescriptors =
        childPropertySource.getPropertyDescriptors();
    for (IPropertyDescriptor iPropertyDescriptor : propertyDescriptors)
    {
      if (iPropertyDescriptor.getId().equals(id))
      {
        return childPropertySource;
      }
    }
    return parentPropertySource;
  }

  @Override
  public void setPropertyValue(Object id, Object value)
  {
    getSourceById(id).setPropertyValue(id, value);

  }

  @Override
  public void resetPropertyValue(Object id)
  {
    getSourceById(id).resetPropertyValue(id);

  }

  @Override
  public boolean isPropertySet(Object id)
  {
    return getSourceById(id).isPropertySet(id);
  }

  @Override
  public Object getPropertyValue(Object id)
  {
    return getSourceById(id).getPropertyValue(id);
  }

  /** utility class to add all properties to supplied list
   * 
   * @param result
   * @param object
   * @param source
   * @param name
   */
  private static void storeProperties(
      final Collection<IPropertyDescriptor> result, final Object object,
      final IItemPropertySource source, final String name)
  {
    for (IItemPropertyDescriptor itemPropertyDescriptor : source
        .getPropertyDescriptors(object))
    {
      result.add(new PropertyDescriptor(object, itemPropertyDescriptor)
      {
        public String getCategory()
        {
          return name;
        };

      });
    }
  }

  public IPropertyDescriptor[] getPropertyDescriptors()
  {
    Collection<IPropertyDescriptor> result =
        new ArrayList<IPropertyDescriptor>();

    // ok, start with the main properties
    storeProperties(result, parentObject, parentItemPropertySource, "Core");
    
    // and now the child ones (probably style related)
    storeProperties(result, childObject, childItemPropertySource, childName);

    // ok, return as array
    return result.toArray(new IPropertyDescriptor[result.size()]);
  }

  @Override
  public Object getEditableValue()
  {
    return parentObject;
  }
}