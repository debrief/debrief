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

import info.limpet.ICommand;
import info.limpet.IDocumentBuilder;

import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.StringDataset;

public class StringDocumentBuilder extends
    CoreDocumentBuilder<String, StringDocument> implements
    IDocumentBuilder<String>
{
  public StringDocumentBuilder(final String name, final ICommand predecessor,
      final Unit<?> indexUnits)
  {
    super(name, predecessor, indexUnits);
  }

  @Override
  protected IDataset getDataset(final List<String> values)
  {
    final String[] arr = values.toArray(new String[]
    {});
    return DatasetFactory.createFromObject(StringDataset.class, arr, null);
  }

  @Override
  protected StringDocument getDocument(final IDataset dataset,
      final ICommand _predecessor2)
  {
    return new StringDocument((StringDataset) dataset, _predecessor2);
  }
}
