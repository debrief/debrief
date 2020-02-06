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

public interface IStoreGroup extends IStoreItem, Collection<IStoreItem>, IChangeListener {

	public interface StoreChangeListener {
		void changed();
	}

	/**
	 * add this item
	 *
	 */
	@Override
	boolean add(IStoreItem item);

	void addAll(List<IStoreItem> results);

	public void addChangeListener(StoreChangeListener listener);

	/**
	 * listen for time changes
	 *
	 * @param listener
	 */
	void addTimeChangeListener(PropertyChangeListener listener);

	/**
	 * retrieve the named collection
	 *
	 * @param name
	 * @return
	 */
	IStoreItem get(String name);

	IStoreItem get(UUID uuid);

	Date getTime();

	/**
	 * remove this item
	 *
	 */
	@Override
	boolean remove(Object item);

	public void removeChangeListener(StoreChangeListener listener);

	/**
	 * stop listening to time changes
	 *
	 * @param listener
	 */
	void removeTimeChangeListener(PropertyChangeListener listener);

	/**
	 * set the current "focus time"
	 *
	 */
	void setTime(Date time);

}
