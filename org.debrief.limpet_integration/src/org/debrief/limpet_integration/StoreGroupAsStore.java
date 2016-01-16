/**
 * 
 */
package org.debrief.limpet_integration;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import info.limpet.IChangeListener;
import info.limpet.IStore;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.data.store.InMemoryStore.StoreChangeListener;
import info.limpet.data.store.InMemoryStore.StoreGroup;

/**
 * @author ian
 * 
 */
public class StoreGroupAsStore implements IStore, IStoreGroup
{

  final private StoreGroup _group;

  /**
   * @param name
   */
  public StoreGroupAsStore(IStoreGroup group)
  {
    _group = (StoreGroup) group;
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IStore#addAll(java.util.List)
   */
  @Override
  public void addAll(List<IStoreItem> items)
  {
    _group.children().addAll(items);
  }

  /*
   * (non-Javadoc)
   * 
   * @see info.limpet.IStore#get(java.util.UUID)
   */
  @Override
  public IStoreItem get(UUID uuid)
  {
    throw new RuntimeException("getUUID not implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * info.limpet.IStore#addChangeListener(info.limpet.data.store.InMemoryStore
   * .StoreChangeListener)
   */
  @Override
  public void addChangeListener(StoreChangeListener listener)
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * info.limpet.IStore#removeChangeListener(info.limpet.data.store.InMemoryStore
   * .StoreChangeListener)
   */
  @Override
  public void removeChangeListener(StoreChangeListener listener)
  {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see info.limpet.IStoreItem#getParent()
   */
  @Override
  public IStoreGroup getParent()
  {
    return _group.getParent();
  }

  /* (non-Javadoc)
   * @see info.limpet.IStoreItem#setParent(info.limpet.IStoreGroup)
   */
  @Override
  public void setParent(IStoreGroup parent)
  {
    _group.setParent(parent);
  }

  /* (non-Javadoc)
   * @see info.limpet.IStoreItem#getName()
   */
  @Override
  public String getName()
  {
    return _group.getName();
  }

  /* (non-Javadoc)
   * @see info.limpet.IStoreItem#fireDataChanged()
   */
  @Override
  public void fireDataChanged()
  {
    _group.fireDataChanged();
  }

  /* (non-Javadoc)
   * @see info.limpet.IStoreItem#getUUID()
   */
  @Override
  public UUID getUUID()
  {
    return _group.getUUID();
  }

  /* (non-Javadoc)
   * @see java.util.Collection#size()
   */
  @Override
  public int size()
  {
    return _group.size();
  }

  /* (non-Javadoc)
   * @see java.util.Collection#isEmpty()
   */
  @Override
  public boolean isEmpty()
  {
    return _group.isEmpty();
  }

  /* (non-Javadoc)
   * @see java.util.Collection#contains(java.lang.Object)
   */
  @Override
  public boolean contains(Object o)
  {
    return _group.contains(o);
  }

  /* (non-Javadoc)
   * @see java.util.Collection#iterator()
   */
  @Override
  public Iterator<IStoreItem> iterator()
  {
    return _group.iterator();
  }

  /* (non-Javadoc)
   * @see java.util.Collection#toArray()
   */
  @Override
  public Object[] toArray()
  {
    return _group.toArray();
  }

  /* (non-Javadoc)
   * @see java.util.Collection#toArray(T[])
   */
  @Override
  public <T> T[] toArray(T[] a)
  {
    return _group.toArray(a);
  }

  /* (non-Javadoc)
   * @see java.util.Collection#containsAll(java.util.Collection)
   */
  @Override
  public boolean containsAll(Collection<?> c)
  {
    return _group.containsAll(c);
  }

  /* (non-Javadoc)
   * @see java.util.Collection#addAll(java.util.Collection)
   */
  @Override
  public boolean addAll(Collection<? extends IStoreItem> c)
  {
    return _group.addAll(c);
  }

  /* (non-Javadoc)
   * @see java.util.Collection#removeAll(java.util.Collection)
   */
  @Override
  public boolean removeAll(Collection<?> c)
  {
    return _group.removeAll(c);
  }

  /* (non-Javadoc)
   * @see java.util.Collection#retainAll(java.util.Collection)
   */
  @Override
  public boolean retainAll(Collection<?> c)
  {
    return _group.retainAll(c);
  }

  /* (non-Javadoc)
   * @see java.util.Collection#clear()
   */
  @Override
  public void clear()
  {
    _group.clear();
  }

  /* (non-Javadoc)
   * @see info.limpet.IChangeListener#dataChanged(info.limpet.IStoreItem)
   */
  @Override
  public void dataChanged(IStoreItem subject)
  {
    _group.dataChanged(subject);
  }

  /* (non-Javadoc)
   * @see info.limpet.IChangeListener#metadataChanged(info.limpet.IStoreItem)
   */
  @Override
  public void metadataChanged(IStoreItem subject)
  {
    _group.metadataChanged(subject);
  }

  /* (non-Javadoc)
   * @see info.limpet.IChangeListener#collectionDeleted(info.limpet.IStoreItem)
   */
  @Override
  public void collectionDeleted(IStoreItem subject)
  {
    _group.collectionDeleted(subject);
  }

  /* (non-Javadoc)
   * @see info.limpet.IStoreGroup#get(java.lang.String)
   */
  @Override
  public IStoreItem get(String name)
  {
    return _group.get(name);
  }

  /* (non-Javadoc)
   * @see info.limpet.IStoreGroup#setName(java.lang.String)
   */
  @Override
  public void setName(String value)
  {
    _group.setName(value);
  }

  /* (non-Javadoc)
   * @see info.limpet.IStoreGroup#hasChildren()
   */
  @Override
  public boolean hasChildren()
  {
    return _group.hasChildren();
  }

  /* (non-Javadoc)
   * @see info.limpet.IStoreGroup#remove(java.lang.Object)
   */
  @Override
  public boolean remove(Object item)
  {
    return _group.remove(item);
  }

  /* (non-Javadoc)
   * @see info.limpet.IStoreGroup#addChangeListener(info.limpet.IChangeListener)
   */
  @Override
  public void addChangeListener(IChangeListener listener)
  {
    _group.addChangeListener(listener);
  }

  /* (non-Javadoc)
   * @see info.limpet.IStoreGroup#removeChangeListener(info.limpet.IChangeListener)
   */
  @Override
  public void removeChangeListener(IChangeListener listener)
  {
    _group.removeChangeListener(listener);
  }

  /* (non-Javadoc)
   * @see info.limpet.IStore#add(info.limpet.IStoreItem)
   */
  @Override
  public boolean add(IStoreItem items)
  {
    return _group.add(items);
  }

}
