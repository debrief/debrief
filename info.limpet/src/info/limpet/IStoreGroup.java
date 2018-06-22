/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface IStoreGroup extends IStoreItem, Collection<IStoreItem>,
		IChangeListener {

	public interface StoreChangeListener {
		void changed();
	}

	public void removeChangeListener(StoreChangeListener listener);

	public void addChangeListener(StoreChangeListener listener);

	/**
	 * retrieve the named collection
	 * 
	 * @param name
	 * @return
	 */
	IStoreItem get(String name);

	/**
	 * add this item
	 * 
	 */
	boolean add(IStoreItem item);

	/**
	 * remove this item
	 * 
	 */
	boolean remove(Object item);

	IStoreItem get(UUID uuid);

	/**
	 * set the current "focus time"
	 * 
	 */
	void setTime(Date time);

	/**
	 * listen for time changes
	 * 
	 * @param listener
	 */
	void addTimeChangeListener(PropertyChangeListener listener);

	/**
	 * stop listening to time changes
	 * 
	 * @param listener
	 */
	void removeTimeChangeListener(PropertyChangeListener listener);

	Date getTime();

	void addAll(List<IStoreItem> results);

}
