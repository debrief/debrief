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

import info.limpet.impl.UIProperty;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DoubleDataset;

public interface IDocument<T extends Object> extends IStoreItem
{

  Iterator<T> getIterator();
  


  @UIProperty(name = "Name", category = UIProperty.CATEGORY_LABEL)
  public String getName();

  public void setName(String name);

  public IStoreGroup getParent();

  public void setParent(IStoreGroup parent);

  public void addChangeListener(IChangeListener listener);

  public void removeChangeListener(IChangeListener listener);

  public void fireDataChanged();

  public void fireMetadataChanged();

  public UUID getUUID();

  @UIProperty(name = "Size", category = UIProperty.CATEGORY_LABEL)
  public int size();

  @UIProperty(name = "Indexed", category = UIProperty.CATEGORY_LABEL)
  public boolean isIndexed();

  public Iterator<Double> getIndexIterator();
  
  @UIProperty(name = "IndexUnits", category = UIProperty.CATEGORY_LABEL)
  public Unit<?> getIndexUnits();

  @UIProperty(name = "Quantity", category = UIProperty.CATEGORY_LABEL)
  public boolean isQuantity();

  public ICommand getPrecedent();

  public void addDependent(ICommand command);

  public void removeDependent(ICommand command);

  public List<ICommand> getDependents();

  void clearQuiet();
  
  void setIndexUnits(Unit<?> units);

  double getIndexAt(int i);



  DoubleDataset getIndexValues();

}