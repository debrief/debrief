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
package info.limpet.operations;

import info.limpet.IChangeListener;
import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.Document;
import info.limpet.impl.LocationDocument;
import info.limpet.impl.LocationDocumentBuilder;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.impl.UIProperty;
import info.limpet.operations.CollectionComplianceTests.TimePeriod;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.Maths;
import org.eclipse.january.metadata.AxesMetadata;

public abstract class AbstractCommand implements ICommand
{

  private final String title;
  private final String description;
  private final boolean canUndo;
  private final boolean canRedo;
  private final IStoreGroup store;

  private final List<IStoreItem> inputs;
  private final List<Document<?>> outputs;

  private IStoreGroup _parent;

  @Override
  public void beingDeleted()
  {
    // ok, detach ourselves from our parent
    final IStoreGroup parent = this.getParent();
    if(parent != null)
    {
      parent.remove(this);
    }
    
    // delete our children
    for(final Document<?> doc: outputs)
    {
      doc.beingDeleted();
    }
  }

  /**
   * whether the command should recalculate if its children change
   * 
   */
  private boolean dynamic = true;
  final private UUID uuid;
  private final transient IContext context;

  public AbstractCommand(final String title, final String description,
      final IStoreGroup store, final boolean canUndo, final boolean canRedo,
      final List<IStoreItem> inputs, final IContext context)
  {
    this.title = title;
    this.description = description;
    this.store = store;
    this.canUndo = canUndo;
    this.canRedo = canRedo;
    this.context = context;

    this.inputs = new ArrayList<IStoreItem>();
    this.outputs = new ArrayList<Document<?>>();
    
    this.uuid = UUID.randomUUID();

    // store any inputs, if we have any
    if (inputs != null)
    {
      this.getInputs().addAll(inputs);
    }
  }

  @Override
  public final void addChangeListener(final IChangeListener listener)
  {
    // TODO we should add change listener support
  }

  public final void addOutput(final Document<?> output)
  {
    getOutputs().add(output);

    // also register as a listener (esp for if it's being deleted)
    output.addChangeListener(this);
  }

  @Override
  public void addTransientChangeListener(final IChangeListener listener)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public final boolean canRedo()
  {
    return canRedo;
  }

  @Override
  public final boolean canUndo()
  {
    return canUndo;
  }

  @Override
  public void collectionDeleted(final IStoreItem subject)
  {

    // ok, we rely on all of our inputs and outputs.
    // if any of them change, then we will self-destruct

    // ok, is this an output?
    // yes, clear it
    final List<Document<?>> toDelete = new ArrayList<Document<?>>();
    toDelete.addAll(getOutputs());

    // ok, we have a list of outputs. clear the outputs,
    // so we won't get any more
    outputs.clear();

    for (final Document<?> t : toDelete)
    {
      // stop listening to it, so we don't get a delete message
      t.removeChangeListener(this);

      // and now delete it
      if (!t.equals(subject) && t.getParent() != null)
      {
        t.getParent().remove(t);
      }
    }

    ArrayList<IStoreItem> toDelete2 = new ArrayList<IStoreItem>();
    toDelete2.addAll(getInputs());

    // have safe list of inputs, now clear them
    getInputs().clear();

    for (final IStoreItem t : toDelete2)
    {
      if (t instanceof Document<?>)
      {
        Document<?> doc = (Document<?>) t;

        // ok, stop listening to it
        doc.removeDependent(this);
      }
      else
      {
        t.removeChangeListener(this);
      }
    }

    // finally remove ourselves from parent
    if (getParent() != null)
    {
      getParent().remove(this);
    }
  }

  @Override
  public final void dataChanged(final IStoreItem subject)
  {
    // are we doing live updates?
    if (dynamic)
    {
      // ok, walk the tree, to see if this is
      // one of our inputs
      boolean requiresChange = false;
      for (final IStoreItem d : getInputs())
      {
        if (d instanceof StoreGroup)
        {
          final StoreGroup sg = (StoreGroup) d;
          if (sg.contains(subject))
          {
            requiresChange = true;
            break;
          }
        }
        else
        {
          if (d.equals(subject))
          {
            requiresChange = true;
            break;
          }
        }
      }
      // is this an input to us?
      if (requiresChange)
      {
        // do the recalc
        recalculate(subject);
      }
    }
  }

  @Override
  public final boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    final AbstractCommand other = (AbstractCommand) obj;
    return getUUID().equals(other.getUUID());
  }

  @Override
  public void execute()
  {
    // ok, register as a listener with the input files
    final Iterator<IStoreItem> iter = getInputs().iterator();
    while (iter.hasNext())
    {
      final IStoreItem t = iter.next();
      if (t instanceof Document<?>)
      {
        final Document<?> doc = (Document<?>) t;
        doc.addDependent(this);
      }
      else
      {
        t.addChangeListener(this);
      }
    }
  }

  @Override
  public void fireDataChanged()
  {
    // hmm, we don't really implement this, because apps listen to the
    // results collections, not the command.
    throw new IllegalArgumentException("Not implemented");
  }

  /**
   * provide access to the context object
   * 
   * @return the context object
   */
  protected final IContext getContext()
  {
    return context;
  }

  @UIProperty(name = "Description", category = UIProperty.CATEGORY_LABEL)
  @Override
  public final String getDescription()
  {
    return description;
  }

  @UIProperty(name = "Dynamic updates", category = UIProperty.CATEGORY_LABEL)
  @Override
  public boolean getDynamic()
  {
    return dynamic;
  }

  @Override
  public final List<IStoreItem> getInputs()
  {
    return inputs;
  }

  @UIProperty(name = "Name", category = UIProperty.CATEGORY_LABEL)
  @Override
  public String getName()
  {
    return title;
  }

  protected int getNonSingletonArrayLength(final List<IStoreItem> inputs)
  {
    int size = 0;

    final Iterator<IStoreItem> iter = inputs.iterator();
    while (iter.hasNext())
    {
      final IDocument<?> thisC = (IDocument<?>) iter.next();
      if (thisC.size() >= 1)
      {
        size = thisC.size();
        break;
      }
    }

    return size;
  }

  @Override
  public final List<Document<?>> getOutputs()
  {
    return outputs;
  }

  @Override
  public IStoreGroup getParent()
  {
    return _parent;
  }

  public final IStoreGroup getStore()
  {
    return store;
  }

  /**
   * convenience function, to return the datasets as a comma separated list
   * 
   * @return
   */
  protected String getSubjectList()
  {
    final StringBuffer res = new StringBuffer();

    final Iterator<IStoreItem> iter = getInputs().iterator();
    int ctr = 0;
    while (iter.hasNext())
    {
      final IStoreItem storeItem = iter.next();
      if (ctr++ > 0)
      {
        res.append(", ");
      }
      res.append(storeItem.getName());
    }

    return res.toString();
  }

  @Override
  public UUID getUUID()
  {
    return uuid;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + getUUID().hashCode();
    return result;
  }

  final protected LocationDocument locationsFor(final LocationDocument track1,
      final Document<?> times, final TimePeriod period)
  {
    // ok, get the time values
    final AxesMetadata axis =
        times.getDataset().getFirstMetadata(AxesMetadata.class);
    final DoubleDataset ds = (DoubleDataset) axis.getAxes()[0];

    final double[] data = ds.getData();
    return locationsFor(track1, data, period);
  }

  final protected LocationDocument locationsFor(final LocationDocument track,
      final double[] times, final TimePeriod period)
  {
    // trim the times to the period
    final ArrayList<Double> dTimes = new ArrayList<Double>();
    for (double thisT : times)
    {
      if (period.contains(thisT))
      {
        dTimes.add(thisT);
      }
    }
    final DoubleDataset ds =
        (DoubleDataset) DatasetFactory.createFromObject(dTimes);

    final Unit<?> indexUnits = times == null ? null : SampleData.MILLIS;
    final LocationDocumentBuilder ldb;

    // ok, put the lats & longs into arrays
    final ArrayList<Double> latVals = new ArrayList<Double>();
    final ArrayList<Double> longVals = new ArrayList<Double>();
    final ArrayList<Double> timeVals = new ArrayList<Double>();

    // special processing. If the document is a singleton, then
    // we just keep re-using the same position
    if (track.size() == 1)
    {
      ldb =
          new LocationDocumentBuilder("Interpolated locations", null,
              indexUnits);
      final Point2D pt = track.getLocationIterator().next();
      for (final double t : times)
      {
        if (period.contains(t))
        {
          ldb.add(t, pt);
        }
      }
    }
    else
    {
      final Iterator<Point2D> lIter = track.getLocationIterator();
      final Iterator<Double> tIter = track.getIndexIterator();
      while (lIter.hasNext() && tIter.hasNext())
      {
        final double thisT = tIter.next();
        final Point2D pt = lIter.next();

        if (period.contains(thisT))
        {
          latVals.add(pt.getY());
          longVals.add(pt.getX());
          timeVals.add(thisT);
        }
      }

      final DoubleDataset latDataset =
          DatasetFactory.createFromObject(DoubleDataset.class, latVals);
      final DoubleDataset DoubleDataset =
          DatasetFactory.createFromObject(DoubleDataset.class, longVals);
      final DoubleDataset timeDataset =
          DatasetFactory.createFromObject(DoubleDataset.class, timeVals);

      final DoubleDataset latInterpolated =
          (DoubleDataset) Maths.interpolate(timeDataset, latDataset, ds, 0, 0);
      final DoubleDataset longInterpolated =
          (DoubleDataset) Maths.interpolate(timeDataset, DoubleDataset, ds, 0,
              0);

      // ok, now we need to re-create a locations document
      ldb =
          new LocationDocumentBuilder("Interpolated locations", null,
              indexUnits);
      for (int i = 0; i < ds.getSize(); i++)
      {
        final Point2D pt =
            track.getCalculator().createPoint(longInterpolated.getDouble(i),
                latInterpolated.getDouble(i));
        ldb.add(ds.getLong(i), pt);
      }
    }

    return ldb.toDocument();
  }

  @Override
  public void metadataChanged(final IStoreItem subject)
  {
    // TODO: do a more intelligent/informed processing of metadata changed
    dataChanged(subject);
  }

  final protected NumberDocument numbersFor(final NumberDocument document,
      final Document<?> times, final TimePeriod period)
  {
    // ok, get the time values
    final AxesMetadata axis =
        times.getDataset().getFirstMetadata(AxesMetadata.class);
    final DoubleDataset ds = (DoubleDataset) axis.getAxes()[0];
    final double[] data = ds.getData();
    return numbersFor(document, data, period);
  }

  final protected NumberDocument numbersFor(final NumberDocument document,
      final double[] times, final TimePeriod period)
  {
    // trim the times to the period
    final ArrayList<Double> dTimes = new ArrayList<Double>();
    for (double thisT : times)
    {
      if (period.contains(thisT))
      {
        dTimes.add(thisT);
      }
    }
    final DoubleDataset ds =
        (DoubleDataset) DatasetFactory.createFromObject(dTimes);

    final Unit<?> indexUnits = times == null ? null : SampleData.MILLIS;
    final NumberDocumentBuilder ldb;

    // ok, put the lats & longs into arrays
    final ArrayList<Double> headings = new ArrayList<Double>();
    final ArrayList<Double> timeVals = new ArrayList<Double>();

    // special processing. If the document is a singleton, then
    // we just keep re-using the same position
    if (headings.size() == 1)
    {
      ldb =
          new NumberDocumentBuilder("Interpolated headings", document
              .getUnits(), null, indexUnits);
      final double pt = document.getIterator().next();
      for (final double t : times)
      {
        if(period.contains(t))
        {
          ldb.add(t, pt);
        }
      }
    }
    else
    {

      final Iterator<Double> lIter = document.getIterator();
      final Iterator<Double> tIter = document.getIndexIterator();
      while (lIter.hasNext() && tIter.hasNext())
      {
        final double thisT = tIter.next();
        final double pt = lIter.next();
        
        if (period.contains(thisT))
        {
          headings.add(pt);
          timeVals.add(thisT);
        }
      }

      final DoubleDataset hdgDataset =
          DatasetFactory.createFromObject(DoubleDataset.class, headings);
      final DoubleDataset timeDataset =
          DatasetFactory.createFromObject(DoubleDataset.class, timeVals);

      final DoubleDataset hdgInterpolated =
          (DoubleDataset) Maths.interpolate(timeDataset, hdgDataset, ds, 0, 0);
      final DoubleDataset timeInterpolated =
          (DoubleDataset) Maths.interpolate(timeDataset, timeDataset, ds, 0, 0);

      // ok, now we need to re-create a locations document
      ldb =
          new NumberDocumentBuilder("Interpolated locations", document
              .getUnits(), null, indexUnits);
      for (int i = 0; i < ds.getSize(); i++)
      {
        ldb.add(timeInterpolated.getDouble(i), hdgInterpolated.getDouble(i));
      }
    }

    return ldb.toDocument();
  }

  protected abstract void recalculate(IStoreItem subject);

  @Override
  public void redo()
  {
    throw new UnsupportedOperationException(
        "Should not be called, redo not provided");
  }

  @Override
  public final void removeChangeListener(final IChangeListener listener)
  {
    // TODO we should add change listener support
  }

  @Override
  public void removeTransientChangeListener(
      final IChangeListener collectionChangeListener)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void setDynamic(final boolean dynamic)
  {
    this.dynamic = dynamic;
  }

  @Override
  public final void setParent(final IStoreGroup parent)
  {
    _parent = parent;
  }

  @Override
  public String toString()
  {
    return getName();
  }

  @Override
  public void undo()
  {
    throw new UnsupportedOperationException(
        "Should not be called, undo not provided");
  }
}
