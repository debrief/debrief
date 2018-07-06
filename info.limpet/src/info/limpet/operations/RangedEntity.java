package info.limpet.operations;

import info.limpet.IChangeListener;
import info.limpet.impl.Range;


public interface RangedEntity
{
  double getValue();
  Range getRange();
  void setValue(double value);
  void addTransientChangeListener(IChangeListener listener);
  void removeTransientChangeListener(IChangeListener listener);
  String getName();
}