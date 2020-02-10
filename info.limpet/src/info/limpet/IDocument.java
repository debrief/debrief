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
package info.limpet;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DoubleDataset;

import info.limpet.impl.UIProperty;

public interface IDocument<T extends Object> extends IStoreItem {

	@Override
	public void addChangeListener(IChangeListener listener);

	public void addDependent(ICommand command);

	void clearQuiet();

	@Override
	public void fireDataChanged();

	public void fireMetadataChanged();

	public List<ICommand> getDependents();

	double getIndexAt(int i);

	public Iterator<Double> getIndexIterator();

	@UIProperty(name = "IndexUnits", category = UIProperty.CATEGORY_LABEL)
	public Unit<?> getIndexUnits();

	DoubleDataset getIndexValues();

	Iterator<T> getIterator();

	@Override
	@UIProperty(name = "Name", category = UIProperty.CATEGORY_LABEL)
	public String getName();

	@Override
	public IStoreGroup getParent();

	public ICommand getPrecedent();

	@Override
	public UUID getUUID();

	@UIProperty(name = "Indexed", category = UIProperty.CATEGORY_LABEL)
	public boolean isIndexed();

	@UIProperty(name = "Quantity", category = UIProperty.CATEGORY_LABEL)
	public boolean isQuantity();

	@Override
	public void removeChangeListener(IChangeListener listener);

	public void removeDependent(ICommand command);

	void setIndexUnits(Unit<?> units);

	public void setName(String name);

	@Override
	public void setParent(IStoreGroup parent);

	@UIProperty(name = "Size", category = UIProperty.CATEGORY_LABEL)
	public int size();

}