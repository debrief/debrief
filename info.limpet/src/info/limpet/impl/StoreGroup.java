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
package info.limpet.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import info.limpet.IChangeListener;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;

public class StoreGroup extends ArrayList<IStoreItem> implements IStoreGroup {
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

	public StoreGroup(final String name) {
		_name = name;
		_uuid = UUID.randomUUID();
	}

	@Override
	public boolean add(final IStoreItem item) {
		final boolean res = super.add(item);

		item.setParent(this);

		item.addChangeListener(this);

		fireModified();

		return res;
	}

	@Override
	public void addAll(final List<IStoreItem> results) {
		// add the items individually, so we can register as a listener
		final Iterator<IStoreItem> iter = results.iterator();
		while (iter.hasNext()) {
			final IStoreItem iCollection = iter.next();
			add(iCollection);
		}

		fireModified();
	}

	@Override
	public void addChangeListener(final IChangeListener listener) {
		checkListeners();

		_listeners.add(listener);
	}

	@Override
	public void addChangeListener(final StoreChangeListener listener) {
		checkListeners();

		_storeListeners.add(listener);
	}

	@Override
	public void addTimeChangeListener(final PropertyChangeListener listener) {
		if (_timeListeners == null) {
			_timeListeners = new ArrayList<PropertyChangeListener>();
		}
		_timeListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see info.limpet.IDocument#addChangeListener(info.limpet.IChangeListener)
	 */
	@Override
	public void addTransientChangeListener(final IChangeListener listener) {
		checkListeners();
		_transientStoreListeners.add(listener);
	}

	@Override
	public void beingDeleted() {
		// ok, detach ourselves from our parent
		final IStoreGroup parent = this.getParent();
		if (parent != null) {
			parent.remove(this);
		}

		// now tell everyone we're being deleted
		for (final IChangeListener thisL : _listeners) {
			thisL.collectionDeleted(this);
		}
	}

	private void checkListeners() {
		if (_storeListeners == null) {
			_storeListeners = new ArrayList<StoreChangeListener>();
		}
		if (_listeners == null) {
			_listeners = new ArrayList<IChangeListener>();
		}
		if (_transientStoreListeners == null) {
			_transientStoreListeners = new ArrayList<IChangeListener>();
		}

	}

	@Override
	public void clear() {
		// stop listening to the collections individually
		// - defer the clear until the end,
		// so we don't get concurrent modification
		final Iterator<IStoreItem> iter = super.iterator();
		while (iter.hasNext()) {
			final IStoreItem iC = iter.next();
			if (iC instanceof IDocument) {
				final IDocument<?> coll = (IDocument<?>) iC;
				coll.removeChangeListener(this);
			}
		}

		super.clear();
		fireModified();
	}

	@Override
	public void collectionDeleted(final IStoreItem subject) {
	}

	@Override
	public void dataChanged(final IStoreItem subject) {
		fireModified();
	}

	@Override
	public void fireDataChanged() {
		if (_listeners != null) {
			for (final IChangeListener listener : _listeners) {
				listener.dataChanged(this);
			}
		}
	}

	protected void fireModified() {
		if (_storeListeners != null) {
			for (final StoreChangeListener listener : _storeListeners) {
				listener.changed();
			}
		}
	}

	@Override
	public IStoreItem get(final String name) {
		for (final IStoreItem item : this) {
			if (item.getName().equals(name)) {
				// successS
				return item;
			} else if (item instanceof IStoreGroup) {
				final IStoreGroup group = (IStoreGroup) item;
				final IStoreItem match = group.get(name);
				if (match != null) {
					return match;
				}
			}
		}
		// nope, failed.
		return null;
	}

	@Override
	public IStoreItem get(final UUID uuid) {
		IStoreItem res = null;
		final Iterator<IStoreItem> iter = iterator();
		while (iter.hasNext()) {
			final IStoreItem item = iter.next();
			if (item instanceof IStoreGroup) {
				final IStoreGroup group = (IStoreGroup) item;
				// recurse down through groups
				res = group.get(uuid);
				if (res != null) {
					break;
				}
			}
			if (uuid.equals(item.getUUID())) {
				res = item;
				break;
			}
		}
		return res;
	}

	@Override
	@UIProperty(name = "Name", category = UIProperty.CATEGORY_LABEL)
	public String getName() {
		return _name;
	}

	@Override
	public IStoreGroup getParent() {
		return _parent;
	}

	@Override
	public Date getTime() {
		return _currentTime;
	}

	@Override
	public UUID getUUID() {
		if (_uuid == null) {
			_uuid = UUID.randomUUID();
		}
		return _uuid;
	}

	@Override
	public void metadataChanged(final IStoreItem subject) {
		dataChanged(subject);
	}

	@Override
	public boolean remove(final Object item) {
		final boolean res = super.remove(item);

		// stop listening to this one
		if (item instanceof IDocument) {
			final IDocument<?> collection = (IDocument<?>) item;
			collection.removeChangeListener(this);

			// and clear it's parent
			collection.setParent(null);
		}

		fireModified();
		fireDataChanged();

		return res;
	}

	@Override
	public void removeChangeListener(final IChangeListener listener) {
		checkListeners();

		_listeners.add(listener);
	}

	@Override
	public void removeChangeListener(final StoreChangeListener listener) {
		checkListeners();

		_storeListeners.remove(listener);
	}

	@Override
	public void removeTimeChangeListener(final PropertyChangeListener listener) {
		if (_timeListeners != null) {
			_timeListeners.remove(listener);
		}
	}

	@Override
	public void removeTransientChangeListener(final IChangeListener collectionChangeListener) {
		_transientStoreListeners.remove(collectionChangeListener);
	}

	public void setName(final String val) {
		_name = val;
	}

	@Override
	public void setParent(final IStoreGroup parent) {
		_parent = parent;
	}

	@Override
	public void setTime(final Date time) {
		final Date oldTime = _currentTime;
		_currentTime = time;
		if (_timeListeners != null) {
			final PropertyChangeEvent evt = new PropertyChangeEvent(this, "TIME", oldTime, time);
			for (final PropertyChangeListener thisL : _timeListeners) {
				thisL.propertyChange(evt);
			}
		}
	}

}
