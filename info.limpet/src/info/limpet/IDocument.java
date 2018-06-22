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