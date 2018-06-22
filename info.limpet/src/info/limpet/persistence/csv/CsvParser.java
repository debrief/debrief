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
package info.limpet.persistence.csv;

import static javax.measure.unit.NonSI.BAR;
import static javax.measure.unit.NonSI.DECIBEL;
import static javax.measure.unit.NonSI.NAUTICAL_MILE;
import static javax.measure.unit.NonSI.YARD;
import static javax.measure.unit.SI.CENTI;
import static javax.measure.unit.SI.GRAM;
import static javax.measure.unit.SI.HERTZ;
import static javax.measure.unit.SI.METER;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.MILLI;
import static javax.measure.unit.SI.SECOND;
import info.limpet.IDocumentBuilder;
import info.limpet.IStoreItem;
import info.limpet.impl.LocationDocumentBuilder;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.impl.SampleData;
import info.limpet.impl.StoreGroup;
import info.limpet.impl.StringDocumentBuilder;
import info.limpet.operations.spatial.GeoSupport;
import info.limpet.persistence.FileParser;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.measure.quantity.Angle;
import javax.measure.quantity.AngularVelocity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Length;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Velocity;
import javax.measure.quantity.VolumetricDensity;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class CsvParser extends FileParser
{
  private static final DateFormat DATE_SECS_FORMAT = new SimpleDateFormat(
      "dd/MM/yyyy hh:mm:ss");

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
      "dd/MM/yyyy hh:mm");

  private static final DateFormat TIME_FORMAT =
      new SimpleDateFormat("hh:mm:ss");

  private ArrayList<DataImporter> _candidates;

  /**
   * base helper class, to help importing series of data
   * 
   * @author ian
   * 
   */
  public abstract static class DataImporter
  {
    private final Unit<?> _units;
    private final String _colName;
    private final String[] _unitsStr;

    /**
     * constructor
     * 
     * @param units
     *          name of the units we store
     * @param colName
     *          name of the column we store
     * @param classType
     *          the type of series we represent (used for default constructor)
     */
    protected DataImporter(final Unit<?> units, final String colName,
        final String unitsStr)
    {
      this(units, colName, new String[]
      {unitsStr});
    }

    protected DataImporter(final Unit<?> units, final String colName,
        final String[] unitsStr)
    {
      _units = units;
      _colName = colName;
      _unitsStr = unitsStr;
    }

    abstract public void consume(IDocumentBuilder<?> thisS, double theIndex,
        int thisCol, CSVRecord record);

    /**
     * create an instance of this series, using the specified name
     * 
     * @param name
     * @return
     */
    abstract public IDocumentBuilder<?> create(String name, Unit<?> indexUnits);

    // {
    // Document res = null;
    // try
    // {
    // res = (Document) _classType.newInstance();
    // res.setName(name);
    // }
    // catch (InstantiationException | IllegalAccessException e)
    // {
    // e.printStackTrace();
    // }
    // return res;
    // }

    /**
     * can we handle this column name?
     * 
     * @param colName
     * @return
     */
    public final boolean handleName(final String colName)
    {
      if (_colName == null)
      {
        return false;
      }
      else
      {
        return _colName.equals(colName);
      }
    }

    /**
     * read some data from this record
     * 
     * @param series
     *          target series
     * @param thisTime
     *          this time stamp
     * @param colStart
     *          column to start reading from
     * @param row
     *          current row of data
     */
    // public abstract void consume(Document series, long thisTime,
    // int colStart, CSVRecord row);

    /**
     * can we handle this units type?
     * 
     * @param units
     * @return
     */
    public final boolean handleUnits(final String units)
    {
      if (_unitsStr == null)
      {
        return false;
      }
      else
      {
        // loop through our units
        for (final String un : _unitsStr)
        {
          if (un != null && un.equalsIgnoreCase(units))
          {
            return true;
          }
        }
        return false;
      }
    }

    /**
     * what should this series be called, if the supplied column name is found
     * 
     */
    public String nameFor(final String colName)
    {
      return colName;
    }
    
    protected Double parseDouble(final String input)
    {
      final Double res;
      if(input.length() > 0)
      {
        res = Double.parseDouble(input);
      }
      else
      {
        res = Double.NaN;
      }
      return res;
    }

    /**
     * how many columns do we consume?
     * 
     * @return
     */
    public int numCols()
    {
      return 1;
    }
  }

  /**
   * if we don't know the units, or data-type for a column, we'll defer creating the importer until
   * we've actually read in some data
   * 
   * @author Ian
   * 
   */
  final protected static class DeferredLoadSupporter extends DataImporter
  {

    public DeferredLoadSupporter(final String name)
    {
      super(null, name, (String) null);
    }

    @Override
    public void consume(final IDocumentBuilder<?> thisS, final double theIndex,
        final int thisCol, final CSVRecord record)
    {
      throw new IllegalArgumentException("Should not get called");
    }

    @Override
    public IDocumentBuilder<?> create(final String name,
        final Unit<?> indexUnits)
    {
      return null;
      // NumberDocumentBuilder res =
      // new NumberDocumentBuilder("Dummy", null, null);
      // return res;
    }

    public String getName()
    {
      return super._colName;
    }
  }

   /**
   * class to handle importing two columns of location data
   *
   * @author ian
   *
   */
  protected static class AbsoluteLocationImporter extends DataImporter
  {
    protected AbsoluteLocationImporter()
    {
      super(null, "Lat", (String) null);
    }

    @Override
    public void consume(final IDocumentBuilder<?> series,
        final double thisIndex, final int colStart, final CSVRecord row)
    {
      final String latVal = row.get(colStart);
      final Double valLat = Double.parseDouble(latVal);
      final String longVal = row.get(colStart + 1);
      final Double valLong = Double.parseDouble(longVal);

      final Point2D point =
          GeoSupport.getCalculatorWGS84().createPoint(valLong, valLat);
      final LocationDocumentBuilder builder = (LocationDocumentBuilder) series;
      builder.add(thisIndex, point);
    }

    /**
     * create an instance of this series, using the specified name
     * 
     * @param name
     * @param indexUnits
     * @return
     */
    @Override
    public IDocumentBuilder<?> create(final String name,
        final Unit<?> indexUnits)
    {
      final LocationDocumentBuilder res =
          new LocationDocumentBuilder(name, null, indexUnits);
      return res;
    }

    @Override
    public String nameFor(final String colName)
    {
      return "Location (Lat/Lon)";
    }

    @Override
    public int numCols()
    {
      return 2;
    }
  }
  
  /**
   * class to handle importing two columns of location data
   *
   * @author ian
   *
   */
  protected static class RelativeLocationImporter extends DataImporter
  {
    protected RelativeLocationImporter()
    {
      super(null, "X", (String) null);
    }

    @Override
    public void consume(final IDocumentBuilder<?> series,
        final double thisIndex, final int colStart, final CSVRecord row)
    {
      final String xValStr = row.get(colStart);
      final Double xVal = Double.parseDouble(xValStr);
      final String yValStr = row.get(colStart + 1);
      final Double yVal = Double.parseDouble(yValStr);

      final Point2D point =
          GeoSupport.getCalculatorGeneric2D().createPoint(xVal, yVal);
      final LocationDocumentBuilder builder = (LocationDocumentBuilder) series;
      builder.add(thisIndex, point);
    }

    /**
     * create an instance of this series, using the specified name
     * 
     * @param name
     * @param indexUnits
     * @return
     */
    @Override
    public IDocumentBuilder<?> create(final String name,
        final Unit<?> indexUnits)
    {
      final LocationDocumentBuilder res =
          new LocationDocumentBuilder(name, null, indexUnits, METER);
      return res;
    }

    @Override
    public String nameFor(final String colName)
    {
      return "Location (x/y)";
    }

    @Override
    public int numCols()
    {
      return 2;
    }
  }

  /**
   * generic class to handle importing series of data
   * 
   * @author ian
   * 
   * @param <T>
   */
  protected static class SeriesSupporter extends DataImporter
  {
    protected SeriesSupporter(final Unit<?> units, final String colName,
        final String unitsStr)
    {
      super(units, colName, unitsStr);
    }

    protected void add(final NumberDocumentBuilder series, final double index,
        final Number quantity)
    {
      series.add(quantity.doubleValue());
    }

    @Override
    public void consume(final IDocumentBuilder<?> series,
        final double thisIndex, final int colStart, final CSVRecord row)
    {
      final String thisVal = row.get(colStart);
      final Double val = parseDouble(thisVal);
      final NumberDocumentBuilder nm = (NumberDocumentBuilder) series;
      add(nm, thisIndex, val);
    }

    @Override
    public IDocumentBuilder<?> create(final String name,
        final Unit<?> indexUnits)
    {
      return new NumberDocumentBuilder(name, super._units, null, indexUnits);
    }

    @Override
    public int numCols()
    {
      return 1;
    }
  }

  /**
   * class to handle importing time-related strings
   * 
   * @author ian
   * 
   */
  protected static class StringImporter extends DataImporter
  {
    protected StringImporter()
    {
      super(null, null, (String) null);
    }

    @Override
    public void consume(final IDocumentBuilder<?> series,
        final double theIndex, final int colStart, final CSVRecord row)
    {
      final String thisVal = row.get(colStart);
      final StringDocumentBuilder builder = (StringDocumentBuilder) series;
      builder.add(theIndex, thisVal);
    }

    @Override
    public IDocumentBuilder<?> create(final String name,
        final Unit<?> indexUnits)
    {
      final StringDocumentBuilder res =
          new StringDocumentBuilder(name, null, indexUnits);
      return res;
    }
  }

  /**
   * generic class to handle importing series of data
   * 
   * @author ian
   * 
   * @param <T>
   */
  protected static class TemporalSeriesSupporter extends DataImporter
  {
    protected TemporalSeriesSupporter(final Unit<?> units,
        final String colName, final String unitsStr)
    {
      super(units, colName, unitsStr);
    }

    protected TemporalSeriesSupporter(final Unit<?> units,
        final String colName, final String[] unitsStr)
    {
      super(units, colName, unitsStr);
    }

    protected void add(final NumberDocumentBuilder series, final double time,
        final Number quantity)
    {
      series.add(time, quantity.doubleValue());
    }

    @Override
    public void consume(final IDocumentBuilder<?> series,
        final double thisTime, final int colStart, final CSVRecord row)
    {
      final String thisVal = row.get(colStart);
      final Double val = parseDouble(thisVal);
      final NumberDocumentBuilder inm = (NumberDocumentBuilder) series;
      add(inm, thisTime, val);
    }

    /**
     * create an instance of this series, using the specified name
     * 
     * @param name
     * @param indexUnits
     * @return
     */
    @Override
    public IDocumentBuilder<?> create(final String name,
        final Unit<?> indexUnits)
    {
      final NumberDocumentBuilder res =
          new NumberDocumentBuilder(name, super._units, null, indexUnits);
      return res;
    }

    @Override
    public int numCols()
    {
      return 1;
    }
  }

  /**
   * class to handle importing time-related strings
   * 
   * @author ian
   * 
   */
  protected static class TemporalStringImporter extends DataImporter
  {
    protected TemporalStringImporter()
    {
      super(null, null, (String) null);
    }

    @Override
    public void consume(final IDocumentBuilder<?> series,
        final double theIndex, final int colStart, final CSVRecord row)
    {
      final String thisVal = row.get(colStart);
      final StringDocumentBuilder builder = (StringDocumentBuilder) series;
      builder.add(theIndex, thisVal);
    }

    @Override
    public IDocumentBuilder<?> create(final String name,
        final Unit<?> indexUnits)
    {
      final StringDocumentBuilder res =
          new StringDocumentBuilder(name, null, indexUnits);
      return res;
    }
  }

  public static DateFormat getDateFormat()
  {
    return DATE_FORMAT;
  }

  public static DateFormat getTimeFormat()
  {
    return TIME_FORMAT;
  }

  public static boolean isNumeric(final String str)
  {
    try
    {
      @SuppressWarnings("unused")
      final double d = Double.parseDouble(str);
    }
    catch (final NumberFormatException nfe)
    {
      return false;
    }
    return true;
  }

  private void createImporters()
  {
    if (_candidates != null)
    {
      return;
    }

    _candidates = new ArrayList<DataImporter>();
    _candidates.add(new AbsoluteLocationImporter());
    _candidates.add(new RelativeLocationImporter());
    _candidates.add(new TemporalSeriesSupporter(SECOND.asType(Duration.class),
        null, new String[]
        {"secs", "s"}));
    _candidates.add(new TemporalSeriesSupporter(MILLI(SECOND).asType(
        Duration.class), null, "ms"));
    _candidates.add(new TemporalSeriesSupporter(HERTZ.asType(Frequency.class),
        null, "Hz"));
    _candidates.add(new TemporalSeriesSupporter(SampleData.DEGREE_ANGLE.divide(
        SECOND).asType(AngularVelocity.class), null, "Degs/sec"));
    _candidates.add(new TemporalSeriesSupporter(METRE.asType(Length.class),
        null, "m"));
    _candidates.add(new TemporalSeriesSupporter(MILLI(BAR).asType(
        Pressure.class), null, new String[]
    {"mb", "millibars"}));
    _candidates.add(new TemporalSeriesSupporter(YARD.asType(Length.class),
        null, "yds"));
    _candidates.add(new TemporalSeriesSupporter(SampleData.DEGREE_ANGLE
        .asType(Angle.class), null, new String[]
    {"Degs", "Degr", "Deg"}));
    _candidates.add(new TemporalSeriesSupporter(GRAM
        .divide(CENTI(METER).pow(3)).asType(VolumetricDensity.class), null,
        new String[]
        {"g/cm3", "g/cm"}));
    _candidates.add(new TemporalSeriesSupporter(NAUTICAL_MILE.divide(
        SECOND.times(3600)).asType(Velocity.class), null, "kts"));
    _candidates.add(new TemporalSeriesSupporter(NonSI.MILE.divide(
        SI.SECOND.times(60 * 60)).asType(Velocity.class), null, new String[]
    {"mph"}));
    _candidates.add(new TemporalSeriesSupporter(NonSI.REVOLUTION.divide(SECOND
        .times(60)), null, new String[]
    {"rpm"}));
    _candidates.add(new TemporalSeriesSupporter(METRE.divide(SECOND).asType(
        Velocity.class), null, new String[]
    {"M/Sec", "m/s"}));
    _candidates.add(new TemporalSeriesSupporter(SI.CELSIUS
        .asType(Temperature.class), null, new String[]
    {"C", "DegC"}));
    _candidates.add(new TemporalSeriesSupporter(DECIBEL
        .asType(Dimensionless.class), null, new String[]
    {"dB"}));
  }

  private DateFormat getDateThisFormat(final DateFormat customDateFormat,
      final String firstCell)
  {
    final DateFormat thisFormat;
    if (customDateFormat != null)
    {
      thisFormat = customDateFormat;
    }
    else
    {
      final int len = firstCell.length();
      if (len < 10)
      {
        thisFormat = TIME_FORMAT;
      }
      else
      {
        // hmm, are there secs present
        if (len == 16)
        {
          thisFormat = DATE_FORMAT;
        }
        else
        {
          thisFormat = DATE_SECS_FORMAT;
        }
      }
    }
    return thisFormat;
  }

  private DataImporter handleDeferredLoader(final String fileName,
      final List<DataImporter> importers,
      final List<IDocumentBuilder<?>> builders, final boolean isIndexed,
      final Unit<?> indexUnits, final CSVRecord record, final int thisCol,
      final DeferredLoadSupporter existingImporter)
  {
    DataImporter res = existingImporter;
    final String seriesName = existingImporter.getName();

    // ok, have a look at the next field
    final String nextVal = record.get(thisCol);

    // is it numeric?
    DataImporter importer = null;
    // ok, treat it as string data
    if (isIndexed)
    {
      if (isNumeric(nextVal))
      {
        // ok, we've got dimensionless quantity data
        importer = new TemporalSeriesSupporter(null, null, (String) null);
      }
      else
      {
        importer = new TemporalStringImporter();
      }
    }
    else
    {
      if (isNumeric(nextVal))
      {
        // ok, we've got dimensionless quantity data
        importer = new SeriesSupporter(null, null, null);
      }
      else
      {
        importer = new StringImporter();
      }
    }

    if (importer != null)
    {
      final int index = importers.indexOf(existingImporter);
      importers.set(index, importer);

      builders.set(index, importer.create(fileName + "-" + seriesName,
          indexUnits));

      res = importer;
    }
    return res;
  }

  @Override
  public List<IStoreItem> parse(final String filePath) throws IOException
  {
    final List<IStoreItem> res = new ArrayList<IStoreItem>();
    final File inFile = new File(filePath);
    final Reader in =
        new InputStreamReader(new FileInputStream(inFile), Charset
            .forName("UTF-8"));
    final String fullFileName = inFile.getName();
    final String fileName = filePrefix(fullFileName);
    final Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
    boolean first = true;

    // generate our list of importers
    createImporters();

    // store one importer per column-set
    final List<DataImporter> importers = new ArrayList<DataImporter>();

    // and store one series per column-set
    final List<IDocumentBuilder<?>> builders =
        new ArrayList<IDocumentBuilder<?>>();

    boolean isIndexed = false;
    Unit<?> indexUnits = null;
    DateFormat customDateFormat = null;
    boolean temporalIndex = false;

    final Pattern unitMatcher = Pattern.compile("(?:\\()(.*?)(?:\\))");

    for (final CSVRecord record : records)
    {
      if (first)
      {
        first = false;

        final String colHeader = record.get(0);
        int ctr = 0;

        if (colHeader != null && colHeader.toLowerCase().startsWith("time")
            && !colHeader.contains("(s)"))
        {
          // is it something other than plain time, and does it contain date format?
          if (!colHeader.equalsIgnoreCase("time") && colHeader.contains("(")
              && colHeader.contains(")"))
          {
            // ok, extract the format string
            final String formatStr =
                colHeader.substring(colHeader.indexOf("(") + 1, colHeader
                    .indexOf(")"));
            customDateFormat = new SimpleDateFormat(formatStr);
          }

          isIndexed = true;
          temporalIndex = true;
          indexUnits = MILLI(SECOND);
          ctr = 1;
        }
        else
        {
          // ok, we'll have to use another number parser

          // get the units
          final Matcher m = unitMatcher.matcher(colHeader);
          if (m.find())
          {
            // ok, get the units
            final String indexUnitsStr = m.group(1);

            // now find an importer for this type
            for (final DataImporter im : _candidates)
            {
              if (im.handleUnits(indexUnitsStr))
              {
                indexUnits = im._units;
                break;
              }
            }
          }
          // put it here
          temporalIndex = false;
          isIndexed = true;
          ctr = 1;
        }

        while (ctr < record.size())
        {
          final String nextVal = record.get(ctr);
          // have a look at it.
          final int i1 = nextVal.indexOf("(");

          final String colName;
          if (i1 > 0)
          {
            // ok, we have units
            colName = nextVal.substring(0, i1).trim();
          }
          else
          {
            // no, no units
            colName = nextVal.trim();
          }

          // see if anybody can handle this name
          boolean handled = false;
          final Iterator<DataImporter> cIter = _candidates.iterator();
          while (cIter.hasNext())
          {
            final DataImporter thisI = cIter.next();
            if (thisI.handleName(colName))
            {
              importers.add(thisI);
              builders.add(thisI.create(
                  fileName + "-" + thisI.nameFor(colName), indexUnits));
              handled = true;
              ctr += thisI.numCols();
              break;
            }
          }

          if (!handled)
          {
            final int i2 = nextVal.indexOf(")");
            if (i2 > 0 && i2 > i1 + 1)
            {
              final String units = nextVal.substring(i1 + 1, i2).trim();

              final Iterator<DataImporter> cIter2 = _candidates.iterator();
              while (cIter2.hasNext())
              {
                final DataImporter thisI = cIter2.next();
                if (thisI.handleUnits(units))
                {
                  importers.add(thisI);
                  builders.add(thisI.create(fileName + "-"
                      + thisI.nameFor(colName), indexUnits));
                  ctr += thisI.numCols();
                  handled = true;
                  break;
                }
              }
            }
          }

          // have we managed it?
          if (!handled)
          {
            // ok, in that case we don't know. Let's introduce a deferred
            // decision
            // maker, so we can make a decision once we've read in some data
            final DeferredLoadSupporter thisI =
                new DeferredLoadSupporter(colName);
            importers.add(thisI);
            builders.add(thisI.create(fileName + "-" + thisI.nameFor(colName),
                indexUnits));
            ctr += 1;
          }
        }
      }
      else
      {

        final String firstCell = record.get(0);
        double indexVal = -1d;
        int thisCol = 0;

        // ok, we're out of the first row
        if (isIndexed)
        {
          try
          {
            if (temporalIndex)
            {
              // ok, get the time field
              // do we have a custom date format
              final DateFormat thisFormat =
                  getDateThisFormat(customDateFormat, firstCell);

              final Date date = thisFormat.parse(firstCell);
              indexVal = date.getTime();
              thisCol = 1;
            }
            else
            {
              indexVal = Double.valueOf(firstCell);
              thisCol = 1;
            }
          }

          catch (final ParseException e)
          {
            e.printStackTrace();
          }
        }
        else
        {
          // not temporal, use this field
          thisCol = 0;
        }

        // now move through the other cols
        final int numImporters = importers.size();
        for (int i = 0; i < numImporters; i++)
        {
          DataImporter thisI = importers.get(i);

          // ok, just check if this is a deferred importer
          if (thisI instanceof DeferredLoadSupporter)
          {
            final DeferredLoadSupporter dImp = (DeferredLoadSupporter) thisI;
            // ok, see if we have enough data to be able to replace
            // the deferred importer with a concrete instance
            thisI =
                handleDeferredLoader(fileName, importers, builders, isIndexed,
                    indexUnits, record, thisCol, dImp);
          }

          final IDocumentBuilder<?> thisS = builders.get(i);

          thisI.consume(thisS, indexVal, thisCol, record);

          thisCol += thisI.numCols();
        }
      }
    }

    // ok, store the series
    if (builders.size() > 1)
    {
      final StoreGroup target = new StoreGroup(fileName);
      for (final IDocumentBuilder<?> builder : builders)
      {
        target.add(builder.toDocument());
      }
      res.add(target);
    }
    else
    {
      for (final IDocumentBuilder<?> builder : builders)
      {
        res.add(builder.toDocument());
      }
    }

    return res;
  }
}
