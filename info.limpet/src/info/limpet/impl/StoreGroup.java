package info.limpet.impl;

import info.limpet.IChangeListener;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class StoreGroup extends ArrayList<IStoreItem> implements IStoreGroup
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String _name;
  private UUID _uuid;
  private IStoreGroup _parent;

  private transient List<StoreChangeListener> _storeListeners;
  private transient List<IChangeListener> _transientStoreListeners;
  private transient List<IChangeListener> _listeners;
  private transient List<PropertyChangeListener> _timeListeners;
  private Date _currentTime;

  public StoreGroup(String name)
  {
    _name = name;
    _uuid = UUID.randomUUID();
  }

  private void checkListeners()
  {
    if (_storeListeners == null)
    {
      _storeListeners = new ArrayList<StoreChangeListener>();
    }
    if (_listeners == null)
    {
      _listeners = new ArrayList<IChangeListener>();
    }
    if (_transientStoreListeners == null)
    {
      _transientStoreListeners = new ArrayList<IChangeListener>();
    }

  }

  public void clear()
  {
    // stop listening to the collections individually
    // - defer the clear until the end,
    // so we don't get concurrent modification
    Iterator<IStoreItem> iter = super.iterator();
    while (iter.hasNext())
    {
      IStoreItem iC = iter.next();
      if (iC instanceof IDocument)
      {
        IDocument<?> coll = (IDocument<?>) iC;
        coll.removeChangeListener(this);
      }
    }

    super.clear();
    fireModified();
  }

  public boolean remove(Object item)
  {
    final boolean res = super.remove(item);

    // stop listening to this one
    if (item instanceof IDocument)
    {
      IDocument<?> collection = (IDocument<?>) item;
      collection.removeChangeListener(this);
      
      // and clear  it's parent
      collection.setParent(null);
    }

    fireModified();
    fireDataChanged();

    return res;
  }

  @Override
  public boolean add(IStoreItem item)
  {
    final boolean res = super.add(item);

    item.setParent(this);

    item.addChangeListener(this);

    fireModified();

    return res;
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IDocument#addChangeListener(info.limpet.IChangeListener)
   */
  @Override
  public void addTransientChangeListener(IChangeListener listener)
  {
    checkListeners();
    _transientStoreListeners.add(listener);
  }

  @Override
  public void removeTransientChangeListener(
      IChangeListener collectionChangeListener)
  {
    _transientStoreListeners.remove(collectionChangeListener);
  }

  @Override
  public void addAll(List<IStoreItem> results)
  {
    // add the items individually, so we can register as a listener
    Iterator<IStoreItem> iter = results.iterator();
    while (iter.hasNext())
    {
      IStoreItem iCollection = iter.next();
      add(iCollection);
    }

    fireModified();
  }

  @Override
  public IStoreGroup getParent()
  {
    return _parent;
  }

  @Override
  public void setParent(IStoreGroup parent)
  {
    _parent = parent;
  }

  @Override
  @UIProperty(name = "Name", category = UIProperty.CATEGORY_LABEL)
  public String getName()
  {
    return _name;
  }
  
  public void setName(final String val)
  {
    _name = val;
  }

  @Override
  public void addChangeListener(IChangeListener listener)
  {
    checkListeners();

    _listeners.add(listener);
  }

  @Override
  public void removeChangeListener(IChangeListener listener)
  {
    checkListeners();

    _listeners.add(listener);
  }

  public void addChangeListener(StoreChangeListener listener)
  {
    checkListeners();

    _storeListeners.add(listener);
  }

  public void removeChangeListener(StoreChangeListener listener)
  {
    checkListeners();

    _storeListeners.remove(listener);
  }


  @Override
  public UUID getUUID()
  {
    if (_uuid == null)
    {
      _uuid = UUID.randomUUID();
    }
    return _uuid;
  }

  @Override
  public void dataChanged(IStoreItem subject)
  {
    fireModified();
  }

  @Override
  public void metadataChanged(IStoreItem subject)
  {
    dataChanged(subject);
  }
  
  @Override
  public void fireDataChanged()
  {
    if (_listeners != null)
    {
      for (IChangeListener listener : _listeners)
      {
        listener.dataChanged(this);
      }
    }
  }

  protected void fireModified()
  {
    if(_storeListeners != null)
    {
      for(StoreChangeListener listener: _storeListeners)
      {
        listener.changed();
      }
    }
  }

  @Override
  public void collectionDeleted(IStoreItem subject)
  {
  }

  @Override
  public IStoreItem get(UUID uuid)
  {
    IStoreItem res = null;
    Iterator<IStoreItem> iter = iterator();
    while (iter.hasNext())
    {
      IStoreItem item = iter.next();
      if (item instanceof IStoreGroup)
      {
        IStoreGroup group = (IStoreGroup) item;
        // recurse down through groups
        res = group.get(uuid);
        if(res != null)
        {
          break;
        }
      }
      if (uuid.equals(item.getUUID()))
      {
        res = item;
        break;
      }
    }
    return res;
  }

  @Override
  public IStoreItem get(final String name)
  {
    for (final IStoreItem item : this)
    {
      if (item.getName().equals(name))
      {
        // successS
        return item;
      }
      else if (item instanceof IStoreGroup)
      {
        IStoreGroup group = (IStoreGroup) item;
        IStoreItem match = group.get(name);
        if (match != null)
        {
          return match;
        }
      }
    }
    // nope, failed.
    return null;
  }

  @Override
  public Date getTime()
  {
    return _currentTime;
  }

  @Override
  public void setTime(final Date time)
  {
    final Date oldTime = _currentTime;
    _currentTime = time;
    if (_timeListeners != null)
    {
      PropertyChangeEvent evt =
          new PropertyChangeEvent(this, "TIME", oldTime, time);
      for (PropertyChangeListener thisL : _timeListeners)
      {
        thisL.propertyChange(evt);
      }
    }
  }

  @Override
  public void addTimeChangeListener(PropertyChangeListener listener)
  {
    if (_timeListeners == null)
    {
      _timeListeners = new ArrayList<PropertyChangeListener>();
    }
    _timeListeners.add(listener);
  }

  @Override
  public void removeTimeChangeListener(PropertyChangeListener listener)
  {
    if (_timeListeners != null)
    {
      _timeListeners.remove(listener);
    }
  }

  @Override
  public void beingDeleted()
  {
    // ok, detach ourselves from our parent
    final IStoreGroup parent = this.getParent();
    if(parent != null)
    {
      parent.remove(this);
    }
    
    // now tell everyone we're being deleted
    for(final IChangeListener thisL: _listeners)
    {
      thisL.collectionDeleted(this);
    }
  }

}
