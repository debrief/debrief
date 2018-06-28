package info.limpet.operations.grid;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.analysis.QuantityFrequencyBins;
import info.limpet.analysis.QuantityFrequencyBins.Bin;
import info.limpet.analysis.QuantityFrequencyBins.BinnedData;
import info.limpet.impl.Document;
import info.limpet.impl.DoubleListDocument;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.SampleData;
import info.limpet.impl.UIProperty;
import info.limpet.operations.AbstractCommand;
import info.limpet.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.Maths;
import org.eclipse.january.dataset.ObjectDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

public class GenerateGrid implements IOperation
{

  private static interface DataProcessor
  {
    Document<?> getOutputDocument(final ICommand predecessor,
        final Unit<?> units);

    String outName();

    Dataset processGrid(final List<Double>[][] grid, final double[] oneBins,
        final double[] twoBins);
  }

  public static class GenerateGridCommand extends AbstractCommand
  {
    final private Triplet triplet;
    final private double[] bins;
    final private DataProcessor helper;
    protected int angleBinSize = 20;

    public GenerateGridCommand(final String title, final String description,
        final IStoreGroup store, final List<IStoreItem> inputs,
        final IContext context, final Triplet triplet, final double[] bins,
        final DataProcessor helper)
    {
      super(title, description, store, true, true, inputs, context);
      this.triplet = triplet;
      this.bins = bins;
      this.helper = helper;
    }

    @UIProperty(name = "BinSize", category = UIProperty.CATEGORY_CALCULATION,
        min = 1, max = 45)
    public int getAngleBinSize()
    {
      return angleBinSize;
    }

    public void setAngleBinSize(int val)
    {
      angleBinSize = val;

      // ok, fire update
      this.recalculate(null);
    }

    public int binFor(final double[] bins, final double vOne)
    {
      // find the bin for this value
      for (int i = 0; i < bins.length; i++)
      {
        final double thisLimit = bins[i];

        if (thisLimit >= vOne)
        {
          return i;
        }
      }

      return bins.length - 1;
    }

    public double[] binsFor(final NumberDocument axis)
    {
      final double[] res;

      // are these degrees?
      if (axis.getUnits().equals(SampleData.DEGREE_ANGLE))
      {
        final int step = angleBinSize;
        final int numSteps = 360 / step;
        res = new double[360 / step];
        for (int i = 1; i <= numSteps; i++)
        {
          res[i - 1] = i * step;
        }
      }
      else
      {
        // collate the values into an array
        DoubleDataset dd = (DoubleDataset) axis.getDataset();
        double[] data = dd.getData();

        // ok, now bin the data
        BinnedData binnedData = QuantityFrequencyBins.binTheseValues(data);

        // convert to array
        res = new double[binnedData.size()];
        int ctr = 0;
        for (Bin d : binnedData)
        {
          res[ctr++] = d.getLowerVal();
        }
      }

      return res;
    }

    @Override
    public void execute()
    {
      // listen to the inputs
      triplet.axisOne.addDependent(this);
      triplet.axisTwo.addDependent(this);
      triplet.measurements.addDependent(this);

      // create the output document
      final Document<?> nd =
          helper.getOutputDocument(this, triplet.measurements.getUnits());

      super.getOutputs().add(nd);

      performCalc();

      // set the name
      nd.setName(helper.outName() + " of " + triplet.measurements.getName());

      // also set the index units
      nd.setIndexUnits(triplet.axisOne.getUnits());

      super.getStore().add(nd);

      super.execute(); // listens to the inputs
    }

    public void performCalc()
    {
      // ok, generate the bins.
      final double[] oneBins;
      final double[] twoBins;
      if (bins != null)
      {
        oneBins = bins;
        twoBins = bins;
      }
      else
      {
        oneBins = binsFor(triplet.axisOne);
        twoBins = binsFor(triplet.axisTwo);
      }

      // output dataset
      @SuppressWarnings("unchecked")
      final List<Double>[][] grid = new List[oneBins.length][twoBins.length];
      
      // ok, we've got to produce the axis datasets at the same indexes as
      // the measurement dataset
      final DoubleDataset mIndex = triplet.measurements.getIndexValues();
      final DoubleDataset oneIndex = triplet.axisOne.getIndexValues();
      final DoubleDataset oneValues = (DoubleDataset) triplet.axisOne.getDataset();
      final DoubleDataset twoIndex = triplet.axisTwo.getIndexValues();
      final DoubleDataset twoValues = (DoubleDataset) triplet.axisTwo.getDataset();
      
      final DoubleDataset oneInterp = (DoubleDataset) Maths.interpolate(oneIndex, oneValues, mIndex, null, null);
      final DoubleDataset twoInterp = (DoubleDataset) Maths.interpolate(twoIndex, twoValues, mIndex, null, null);
      

      // ok, loop through the data
      final Iterator<Double> oneIter = new NumberDocument.DoubleIterator(oneInterp.getData());
      final Iterator<Double> twoIter = new NumberDocument.DoubleIterator(twoInterp.getData());
      final Iterator<Double> valIter = triplet.measurements.getIterator();

      while (oneIter.hasNext())
      {
        final double vOne = oneIter.next();
        final double vTwo = twoIter.next();
        final double vVal = valIter.next();

        // work out the x axis
        final int i = binFor(oneBins, vOne);

        // work out the y axis
        final int j = binFor(twoBins, vTwo);

        // store the variable
        if (grid[i][j] == null)
        {
          grid[i][j] = new ArrayList<Double>();
        }
        grid[i][j].add(vVal);
      }

      final Dataset processed = helper.processGrid(grid, oneBins, twoBins);

      // insert the metadata
      final AxesMetadata am = new AxesMetadataImpl();
      am.initialize(2);
      final Dataset xAxis = DatasetFactory.createFromObject(oneBins);
      xAxis.setName(triplet.axisOne.getName());
      final Dataset yAxis = DatasetFactory.createFromObject(twoBins);
      yAxis.setName(triplet.axisTwo.getName());
      am.setAxis(0, xAxis);
      am.setAxis(1, yAxis);
      processed.addMetadata(am);

      // get the output doc
      final Document<?> nd = (Document<?>) super.getOutputs().get(0);

      // do we have a name?
      final String outName = nd.getName();
      if (outName != null)
      {
        processed.setName(nd.getName());
      }

      // store the results object in it
      nd.setDataset(processed);
    }

    @Override
    protected void recalculate(final IStoreItem subject)
    {
      performCalc();

      // share the good news
      super.getOutputs().get(0).fireDataChanged();
    }

    @Override
    public void redo()
    {
      // TODO Auto-generated method stub
      super.redo();
    }

    @Override
    public void undo()
    {
      // TODO Auto-generated method stub
      super.undo();
    }

  }

  private static class Triplet
  {
    private NumberDocument axisOne;
    private NumberDocument axisTwo;
    private NumberDocument measurements;
  }

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  private class SampleHelper implements DataProcessor
  {
    @Override
    public Document<?> getOutputDocument(final ICommand predecessor,
        final Unit<?> units)
    {
      return new DoubleListDocument(null, predecessor, units);
    }

    @Override
    public Dataset processGrid(final List<Double>[][] grid,
        final double[] oneBins, final double[] twoBins)
    {
      return doSampleGrid(grid, oneBins, twoBins);
    }

    @Override
    public String outName()
    {
      return "Collated samples of";
    }
  }

  private class MeanHelper implements DataProcessor
  {

    @Override
    public Document<?> getOutputDocument(final ICommand predecessor,
        final Unit<?> units)
    {
      return new NumberDocument(null, predecessor, units);
    }

    @Override
    public Dataset processGrid(final List<Double>[][] grid,
        final double[] oneBins, final double[] twoBins)
    {
      return doMeanGrid(grid, oneBins, twoBins);
    }

    @Override
    public String outName()
    {
      return "Calculated mean of";
    }

  };

  @Override
  public List<ICommand> actionsFor(final List<IStoreItem> selection,
      final IStoreGroup destination, final IContext context)
  {
    final List<ICommand> res = new ArrayList<ICommand>();

    // check the data
    final List<Triplet> perms = findPermutations(selection, aTests);
    if (perms != null && perms.size() > 0)
    {
      final DataProcessor meanHelper = new MeanHelper();
      final DataProcessor sampleHelper = new SampleHelper();

      // ok, put them into actions
      for (final Triplet thisP : perms)
      {
        // special case. check the units of the axes
        final Unit<?> aUnits = thisP.axisOne.getUnits();
        if (aUnits.equals(SampleData.DEGREE_ANGLE))
        {
          // ok, we'll do the fancy degree grid
          final String sampleTitle =
              "Collate 360 degree grid of samples of" + thisP.measurements + " based on "
                  + thisP.axisOne + " and " + thisP.axisTwo;
          final String meanTitle =
              "Collate 360 degree grid of mean of " + thisP.measurements + " based on "
                  + thisP.axisOne + " and " + thisP.axisTwo;
          
          // produce the description
          final String description = "Collate grid from data";

          res.add(new GenerateGridCommand(meanTitle, description, destination,
              selection, context, thisP, null, meanHelper));

          res.add(new GenerateGridCommand(sampleTitle, description,
              destination, selection, context, thisP, null, sampleHelper));
        }
        else
        {
          // produce the title
          final String sampleTitle =
              "Collate grid of samples of " + thisP.measurements + " based on "
                  + thisP.axisOne + " and " + thisP.axisTwo;
          final String meanTitle =
              "Collate grid of samples of " + thisP.measurements + " based on "
                  + thisP.axisOne + " and " + thisP.axisTwo;

          // produce the description
          final String description = "Collate grid from data";

          res.add(new GenerateGridCommand(meanTitle, description, destination,
              selection, context, thisP, null, meanHelper));
          res.add(new GenerateGridCommand(sampleTitle, description,
              destination, selection, context, thisP, null, sampleHelper));
        }
      }
    }
    return res;
  }

  protected Dataset doMeanGrid(final List<Double>[][] grid,
      final double[] oneBins, final double[] twoBins)
  {
    final double[][] means = new double[oneBins.length][twoBins.length];

    // ok, populate it
    for (int i = 0; i < oneBins.length; i++)
    {
      for (int j = 0; j < twoBins.length; j++)
      {
        final List<Double> list = grid[i][j];
        final double thisV;
        if (list != null)
        {
          double total = 0;
          for (final Double d : list)
          {
            total += d;
          }
          thisV = total / list.size();
        }
        else
        {
          thisV = Double.NaN;
        }
        means[i][j] = thisV;
      }
    }
    // now put the grid into a dataset
    final DoubleDataset ds =
        (DoubleDataset) DatasetFactory.createFromObject(means);

    return ds;
  }

  protected Dataset doSampleGrid(final List<Double>[][] grid,
      final double[] oneBins, final double[] twoBins)
  {
    final ObjectDataset ds =
        DatasetFactory.zeros(ObjectDataset.class, new int[]
        {oneBins.length, twoBins.length});

    // ok, populate it
    for (int i = 0; i < oneBins.length; i++)
    {
      for (int j = 0; j < twoBins.length; j++)
      {
        final List<Double> list = grid[i][j];
        ds.set(list, i, j);
      }
    }

    return ds;
  }

  public static List<Triplet> findPermutations(
      final List<IStoreItem> selection, CollectionComplianceTests aTests)
  {
    final List<Triplet> res = new ArrayList<Triplet>();

    if (selection.size() == 3 && aTests.allEqualIndexed(selection))
    {
      final Map<Unit<?>, ArrayList<NumberDocument>> matches =
          new HashMap<Unit<?>, ArrayList<NumberDocument>>();

      Unit<?> commonIndexUnits = null;

      // we need to keep track of the index ranges,
      // to ensure there is an overlap
      double minIndexVal = Double.MIN_VALUE;
      double maxIndexVal = Double.MAX_VALUE;

      // do the binning
      for (final IStoreItem item : selection)
      {
        if (item instanceof NumberDocument)
        {
          final NumberDocument doc = (NumberDocument) item;

          // check the index units
          final Unit<?> index = doc.getIndexUnits();
          if (commonIndexUnits == null)
          {
            commonIndexUnits = index;
          }
          else
          {
            if (!index.equals(commonIndexUnits))
            {
              return null;
            }
          }

          // ok, it's valid - get the index range
          double thisMin = doc.getIndexAt(0);
          double thisMax = doc.getIndexAt(doc.size() - 1);
          minIndexVal = Math.max(minIndexVal, thisMin);
          maxIndexVal = Math.min(maxIndexVal, thisMax);

          // now check the units
          final Unit<?> units = doc.getUnits();

          ArrayList<NumberDocument> list = matches.get(units);
          if (list == null)
          {
            list = new ArrayList<NumberDocument>();
            matches.put(units, list);
          }

          list.add(doc);
        }
      }

      // check the indices
      if (minIndexVal < maxIndexVal)
      {

        // ok, do we have enough items
        if (matches.size() == 1)
        {
          // ok, we need to offer all three as the measurement
          final ArrayList<NumberDocument> list =
              matches.get(matches.keySet().iterator().next());
          res.add(tripletFor(list));

          // ok, push the first item to the end
          list.add(list.remove(0));
          res.add(tripletFor(list));

          // ok, push the next item to the end
          list.add(list.remove(0));
          res.add(tripletFor(list));

        }
        else if (matches.size() == 2)
        {
          final Triplet thisT = new Triplet();

          // ok, it's obvious. find the singleton
          for (final ArrayList<NumberDocument> thisL : matches.values())
          {
            if (thisL.size() == 1)
            {
              // here is it
              thisT.measurements = thisL.get(0);
            }
            else if (thisL.size() == 2)
            {
              // here they are
              thisT.axisOne = thisL.get(0);
              thisT.axisTwo = thisL.get(1);
            }
            else
            {
              throw new IllegalArgumentException(
                  "Unable to organise inputs for gridding operation");
            }
          }

          res.add(thisT);
        }

      }
    }

    return res;
  }

  private static Triplet tripletFor(final ArrayList<NumberDocument> list)
  {
    final Triplet res = new Triplet();
    res.axisOne = list.get(0);
    res.axisTwo = list.get(1);
    res.measurements = list.get(2);

    return res;
  }
}
