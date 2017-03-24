package Debrief.Wrappers.Extensions;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.Wrappers.DataItemWrapper;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;
import MWC.GUI.HasEditables;

/** make a list of additional data items suitable
 * for showing in Debrief's Outline View
 * @author ian
 *
 */
public class AdditionalProviderWrapper implements Editable, HasEditables, Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /** the list of additional data items
   * 
   */
  private final AdditionalData _provider;
  
  /** set of helpers that are able to wrap additional data items
   * 
   */
  private final List<ExtensionContentProvider> _contentProviderExtensions;

  public AdditionalProviderWrapper(final AdditionalData additionalData,
      final List<ExtensionContentProvider> providers)
  {
    _provider = additionalData;
    _contentProviderExtensions = providers;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_provider == null) ? 0 : _provider.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AdditionalProviderWrapper other = (AdditionalProviderWrapper) obj;
    if (_provider == null)
    {
      if (other._provider != null)
        return false;
    }
    else if (!_provider.equals(other._provider))
      return false;
    return true;
  }

  @Override
  public String getName()
  {
    return DataFolder.DEFAULT_NAME;
  }

  public String toString()
  {
    final int size = _provider.size();
    final String numStr;
    if (size == 0)
    {
      numStr = "Empty";
    }
    else
    {
      numStr = "" + size + " items";
    }

    return getName() + " (" + numStr + ")";
  }

  @Override
  public Enumeration<Editable> elements()
  {
    Vector<Editable> res = new Vector<Editable>();

    // ok, are there any data items in there?
    for (Object item : _provider)
    {
      // see if we have a content provider for this data type
      List<ExtensionContentProvider> cp = _contentProviderExtensions;
      for (ExtensionContentProvider provider : cp)
      {
        List<Editable> items = provider.itemsFor(item);
        res.addAll(items);
      }
    }

    // ok, we have to wrap the data items

    return res.elements();
  }

  @Override
  public boolean hasEditor()
  {
    return false;
  }

  @Override
  public EditorType getInfo()
  {
    return null;
  }

  @Override
  public boolean hasOrderedChildren()
  {
    // TODO Auto-generated method stub
    return false;
  }

  protected static final class IteratorWrapper implements
      java.util.Enumeration<Editable>
  {
    private final java.util.Iterator<Editable> _val;

    public IteratorWrapper(final java.util.Iterator<Editable> iterator)
    {
      _val = iterator;
    }

    public final boolean hasMoreElements()
    {
      return _val.hasNext();

    }

    public final Editable nextElement()
    {
      return _val.next();
    }
  }

  @Override
  @FireExtended
  public void add(Editable point)
  {
    if(point instanceof DataItemWrapper)
    {
      DataItemWrapper itemW = (DataItemWrapper) point;
      DataFolder additionalData = (DataFolder) _provider.get(0);
      additionalData.add(itemW.getDataItem());
    }
    else
    {
      System.err.println("Can't add this data object to measured data:" + point);
    }
  }

  @Override
  @FireExtended
  public void removeElement(Editable point)
  {
    if(point instanceof DataItemWrapper)
    {
      DataItemWrapper itemW = (DataItemWrapper) point;
      DataFolder additionalData = (DataFolder) _provider.get(0);
      additionalData.remove(itemW.getDataItem());
    }
    else
    {
      System.err.println("Can't remove this data object to measured data:" + point);
    }
  }

}
