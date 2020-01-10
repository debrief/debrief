/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.lite.gui.custom;

public class AbstractSelection<T>
{
  private Boolean _selected;
  private T _item;

  public AbstractSelection(final T item, final Boolean selected)
  {
    this._selected = selected;
    this._item = item;
  }

  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final AbstractSelection<?> other = (AbstractSelection<?>) obj;
    if (_item == null)
    {
      if (other._item != null)
        return false;
    }
    else if (!_item.equals(other._item))
      return false;
    return true;
  }

  public T getItem()
  {
    return _item;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 111 * getClass().hashCode();
    if (_item != null)
    {
      result += prime * _item.hashCode();
    }
    return result;
  }

  public Boolean isSelected()
  {
    return _selected;
  }

  public void setItem(final T _item)
  {
    this._item = _item;
  }

  public void setSelected(final Boolean _selected)
  {
    this._selected = _selected;
  }

}
