package Debrief.Wrappers.Extensions;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import MWC.GUI.Editable;
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
  public void add(Editable point)
  {
    _provider.add(point);
  }

  @Override
  public void removeElement(Editable point)
  {
    _provider.remove(point);
  }

}
