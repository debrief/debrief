package info.limpet.operations.arithmetic;

import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.Document;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.AbstractCommand;
import info.limpet.operations.CollectionComplianceTests;

import java.util.Iterator;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

public abstract class CoreQuantityCommand extends AbstractCommand
{

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  public CoreQuantityCommand(final String title, final String description,
      final IStoreGroup store, final boolean canUndo, final boolean canRedo,
      final List<IStoreItem> inputs, final IContext context)
  {
    super(title, description, store, canUndo, canRedo, inputs, context);
  }

  /**
   * empty the contents of any results collections
   * 
   * @param outputs
   */
  protected void clearOutputs(final List<Document<?>> outputs)
  {
    // clear out the lists, first
    final Iterator<Document<?>> iter = outputs.iterator();
    while (iter.hasNext())
    {
      final Document<?> qC = iter.next();
      qC.clearQuiet();
    }
  }

  /**
   * for binary operations we act on a set of inputs, so, if one has changed then we will
   * recalculate all of them.
   */
  @Override
  protected void recalculate(final IStoreItem subject)
  {
    // get the existing name
    final Document<?> outDoc = getOutputs().get(0);
    final String oldName = outDoc.getName();

    // calculate the results
    final IDataset newSet = performCalc();

    // and restore the name
    newSet.setName(oldName);

    // store the new dataset
    outDoc.setDataset(newSet);

    // and share the good news
    outDoc.fireDataChanged();
  }

  protected Dataset findIndexDataset()
  {
    Dataset ds = null;
    for (final IStoreItem inp : getInputs())
    {
      final Document<?> doc = (Document<?>) inp;
      if (doc.size() > 1 && doc.isIndexed())
      {
        final IDataset dataset = doc.getDataset();
        final AxesMetadata axes = dataset.getFirstMetadata(AxesMetadata.class);
        if (axes != null)
        {
          final DoubleDataset ds1 = (DoubleDataset) axes.getAxis(0)[0];
          ds = ds1;
          break;
        }
      }
    }

    return ds;
  }

  protected void assignOutputIndices(final IDataset output,
      final Dataset outputIndices)
  {
    if (outputIndices != null)
    {
      // now insert the new one
      final AxesMetadata am = new AxesMetadataImpl();
      am.initialize(1);
      am.setAxis(0, outputIndices);
      output.addMetadata(am);
    }
  }

  /**
   * what are the units for the resulting data?
   * 
   * @return
   */
  protected abstract Unit<?> getUnits();

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

  /**
   * what are the units for the index?
   * 
   * @return
   */
  protected Unit<?> getIndexUnits()
  {
    final Unit<?> res;

    // ok. are they both indexed?
    if (getATests().allEqualIndexed(getInputs()))
    {
      // ok, that's easy
      final Document<?> doc = (Document<?>) getInputs().get(0);
      res = doc.getIndexUnits();
    }
    else if (getATests().hasIndexed(getInputs()))
    {
      Unit<?> firstIndexed = null;
      // ok, find the series with an index
      for (final IStoreItem s : getInputs())
      {
        final Document<?> doc = (Document<?>) s;
        if (doc.isIndexed())
        {
          final Unit<?> thisIndexUnits = doc.getIndexUnits();
          if (thisIndexUnits != null)
          {
            firstIndexed = thisIndexUnits;
            break;
          }
        }
      }
      res = firstIndexed;
    }
    else
    {
      res = null;
    }

    return res;
  }

  protected void storeIndexUnits(final NumberDocument output,
      final Unit<?> indexUnits)
  {
    if (output.isIndexed())
    {
      output.setIndexUnits(indexUnits);
    }
  }

  protected void tidyOutput(final NumberDocument output)
  {
    // we don't need to do anything
  }

  abstract protected String generateName();

  /**
   * ok, do the calculation
   * 
   * @return
   */
  abstract protected IDataset performCalc();

  @Override
  public void execute()
  {
    // sort out the output unit
    final Unit<?> unit = getUnits();

    // also sort out the output's index units
    final Unit<?> indexUnits = getIndexUnits();

    // start adding values.
    final IDataset dataset = performCalc();

    // store the name
    dataset.setName(generateName());

    // ok, wrap the dataset
    final NumberDocument output =
        new NumberDocument((DoubleDataset) dataset, this, unit);

    // and the index units
    storeIndexUnits(output, indexUnits);

    // do any extra tidying, if necessary
    tidyOutput(output);

    // and fire out the update
    output.fireDataChanged();

    // store the output
    super.addOutput(output);

    // tell each series that we're a dependent
    final Iterator<IStoreItem> iter = getInputs().iterator();
    while (iter.hasNext())
    {
      final IStoreItem sItem = iter.next();
      if (sItem instanceof IDocument)
      {
        final IDocument<?> iCollection = (IDocument<?>) sItem;
        iCollection.addDependent(this);
      }
    }

    // ok, done
    getStore().add(output);
  }

}
