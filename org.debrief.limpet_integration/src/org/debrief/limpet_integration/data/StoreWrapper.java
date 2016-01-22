package org.debrief.limpet_integration.data;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import Debrief.Wrappers.Measurements.SupplementalDataBlock;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Editable2;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import info.limpet.ICollection;
import info.limpet.IStore;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.data.store.IGroupWrapper;
import info.limpet.data.store.InMemoryStore;
import info.limpet.ui.data_provider.data.LimpetWrapper;
import info.limpet.ui.data_provider.data.ReflectivePropertySource;

public class StoreWrapper implements SupplementalDataBlock, Editable2,
    LimpetWrapper, Layer
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  /**
   * 
   */
  private static final String STORE_NAME = "Measurements";
  private final InMemoryStore _store;
  @SuppressWarnings("unused")
  private Editable _parent;

  public StoreWrapper(InMemoryStore store)
  {
    _store = store;
  }

  public IStore getStore()
  {
    return _store;
  }

  @Override
  public void paint(CanvasType dest)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public WorldArea getBounds()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean getVisible()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setVisible(boolean val)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public double rangeFrom(WorldLocation other)
  {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getName()
  {
    return STORE_NAME;
  }

  @Override
  public boolean hasEditor()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public EditorType getInfo()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int compareTo(Plottable o)
  {
    // TODO Auto-generated method stub
    return 0;
  }

  public String toString()
  {
    return STORE_NAME;
  }

  @Override
  public void setWrapper(Object parent)
  {
    _parent = (Editable) parent;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + _store.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    StoreWrapper other = (StoreWrapper) obj;
    if (other.hashCode() != hashCode())
    {
      return false;
    }
    return true;
  }

  public static ArrayList<Editable> getElementsFor(IStoreGroup store,
      LimpetWrapper parent)
  {
    ArrayList<Editable> res = new ArrayList<Editable>();
    Iterator<IStoreItem> iter = store.iterator();
    while (iter.hasNext())
    {
      IStoreItem storeItem = (IStoreItem) iter.next();
      final Editable thisE;

      if (storeItem instanceof IStoreGroup)
      {
        IStoreGroup group = (IStoreGroup) storeItem;
        GroupWrapper gw = new GroupWrapper(group);
        gw.setParent(parent);
        thisE = gw;
      }
      else if (storeItem instanceof ICollection)
      {
        ItemWrapper item = new ItemWrapper(storeItem);
        item.setParent(parent);
        thisE = item;
      }
      else
      {
        thisE = null;
      }

      res.add(thisE);
    }

    return res;
  }

  protected static class GroupWrapper extends ReflectivePropertySource
      implements Editable2, LimpetWrapper, IGroupWrapper
  {

    private IStoreGroup _group;
    private LimpetWrapper _parent;
    private EditorType _info;

    public GroupWrapper(IStoreGroup group)
    {
      super(group);
      _group = group;
    }

    /**
     * @param store
     */
    public void setParent(LimpetWrapper parent)
    {
      this._parent = parent;
      _group.setParent((IStoreGroup) parent.getSubject());
    }

    @Override
    public String getName()
    {
      return _group.getName();
    }

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + _group.hashCode();
      return result;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }
      if (obj == null)
      {
        return false;
      }
      if (getClass() != obj.getClass())
      {
        return false;
      }
      GroupWrapper other = (GroupWrapper) obj;
      if (other.hashCode() != hashCode())
      {
        return false;
      }
      return true;
    }

    @Override
    public String toString()
    {
      return getName();
    }

    @Override
    public Object getSubject()
    {
      return _group;
    }

    @Override
    public LimpetWrapper getParent()
    {
      return _parent;
    }

    @Override
    public boolean hasEditor()
    {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public EditorType getInfo()
    {
      if (_info == null)
      {
        _info = new EditorType(this, getName(), getName())
        {

          @Override
          public PropertyDescriptor[] getPropertyDescriptors()
          {
            return getPropertyDescriptors();
          }

          @Override
          public String getDisplayName()
          {
            return getDisplayName();
          }

          @Override
          public String getName()
          {
            return getName();
          }

        };
      }
      return _info;
    }

    @Override
    public boolean hasChildren()
    {
      return _group.size() > 0;
    }

    @Override
    public Collection<Editable> getChildren()
    {
      return getElementsFor(_group, this);
    }

    @Override
    public Object getValue(Object descriptor)
    {
      return super.getPropertyValue(descriptor);
    }

    @Override
    public void setValue(Object id, Object theValue)
    {
      Object oldVal = getValue(id);
      super.setPropertyValue(id, theValue);
      getInfo().fireChanged(this, getName(), oldVal, theValue);
      Runnable runnable = new Runnable()
      {

        @Override
        public void run()
        {
          IEditorPart editor = PlatformUI.getWorkbench()
              .getActiveWorkbenchWindow().getActivePage().getActiveEditor();
          Layers layers = (Layers) editor.getAdapter(Layers.class);
          if (layers != null)
          {
            layers.fireExtended();
          }
        }
      };
      Display.getDefault().asyncExec(runnable);
    }

    /*
     * (non-Javadoc)
     * 
     * @see info.limpet.data.store.IGroupWrapper#getGroup()
     */
    @Override
    public IStoreGroup getGroup()
    {
      return _group;
    }

  }

  protected static class ItemWrapper extends ReflectivePropertySource implements
      Editable2, LimpetWrapper
  {

    private IStoreItem _item;
    private LimpetWrapper _parent;
    private EditorType _info;

    public ItemWrapper(IStoreItem storeItem)
    {
      super(storeItem);
      _item = storeItem;
    }

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + _item.hashCode();
      return result;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }
      if (obj == null)
      {
        return false;
      }
      if (getClass() != obj.getClass())
      {
        return false;
      }
      ItemWrapper other = (ItemWrapper) obj;
      if (other.hashCode() != hashCode())
      {
        return false;
      }
      return true;
    }

    @Override
    public String getName()
    {
      return _item.getName();
    }

    @Override
    public String toString()
    {
      return getName();
    }

    @Override
    public boolean hasEditor()
    {
      return true;
    }

    @Override
    public EditorType getInfo()
    {
      if (_info == null)
      {
        _info = new EditorType(this, getName(), getName());
      }
      return _info;
    }

    @Override
    public Object getSubject()
    {
      return _item;
    }

    @Override
    public LimpetWrapper getParent()
    {
      return this._parent;
    }

    public void setParent(LimpetWrapper parent)
    {
      this._parent = parent;
    }

    @Override
    public Object getValue(Object descriptor)
    {
      return super.getPropertyValue(descriptor);
    }

    @Override
    public boolean hasChildren()
    {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public Collection<Editable> getChildren()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public void setValue(Object id, Object theValue)
    {
      Object oldVal = getValue(id);
      super.setPropertyValue(id, theValue);
      getInfo().fireChanged(this, getName(), oldVal, theValue);
      // TODO: we should only be updating the top level layer for this item,
      // not the whole layers object
      Runnable runnable = new Runnable()
      {

        @Override
        public void run()
        {
          IEditorPart editor = PlatformUI.getWorkbench()
              .getActiveWorkbenchWindow().getActivePage().getActiveEditor();
          Layers layers = (Layers) editor.getAdapter(Layers.class);
          if (layers != null)
          {
            layers.fireExtended();
          }
        }
      };
      Display.getDefault().asyncExec(runnable);
    }
  }

  @Override
  public LimpetWrapper getParent()
  {
    return null;
  }

  @Override
  public Object getSubject()
  {
    return _store;
  }

  @Override
  public boolean hasChildren()
  {
    return _store.size() > 0;
  }

  @Override
  public Collection<Editable> getChildren()
  {
    return getElementsFor(_store, this);
  }

  @Override
  public Object getValue(Object descriptor)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setValue(Object id, Object theValue)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#exportShape()
   */
  @Override
  public void exportShape()
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#append(MWC.GUI.Layer)
   */
  @Override
  public void append(Layer other)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#setName(java.lang.String)
   */
  @Override
  public void setName(String val)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#hasOrderedChildren()
   */
  @Override
  public boolean hasOrderedChildren()
  {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#getLineThickness()
   */
  @Override
  public int getLineThickness()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#add(MWC.GUI.Editable)
   */
  @Override
  public void add(Editable point)
  {
    throw new RuntimeException(
        "Adding Debrief item to StoreWrapper not implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#removeElement(MWC.GUI.Editable)
   */
  @Override
  public void removeElement(Editable point)
  {
    throw new RuntimeException(
        "Removing Debrief item from StoreWrapper not implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see MWC.GUI.Layer#elements()
   */
  @Override
  public Enumeration<Editable> elements()
  {
    Iterator<Editable> kids = getChildren().iterator();
    return new Plottables.IteratorWrapper(kids);
  }

}
