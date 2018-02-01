package org.mwc.debrief.track_shift.freq;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class InflectionPointDetector
{

  public static void doAssert(final Boolean cond, final String errMsg)
  {
    if (!cond)
    {
      throw new IllegalArgumentException(errMsg);
    }
  }

  public static void main(final String[] args) throws IOException,
      ParseException
  {
    String line;

    final ArrayList<Double> values = new ArrayList<Double>();
    final ArrayList<Long> timeStamps = new ArrayList<Long>();

    doAssert(args.length == 1,
        "Please provide a single parameter which is the name of the file to process");

    final File f = new File(args[0]);
    doAssert(f.exists(), String.format("File %s doesn't exist!", args[0]));

    System.out.format("Processing: %s\n", args[0]);
    
    final DateFormat df = new SimpleDateFormat("HH:mm:ss");
    df.setTimeZone(TimeZone.getTimeZone("GMT"));

    final BufferedReader br = new BufferedReader(new FileReader(args[0]));
    while ((line = br.readLine()) != null)
    {
      if (line.isEmpty())
      {
        continue;
      }
      final String[] cells = line.split(",");
      doAssert(cells.length == 2, "Problem with input file format");

      final double dVal = Double.parseDouble(cells[1]);
      values.add(dVal);
      final long tVal = df.parse(cells[0]).getTime();
      timeStamps.add(tVal);
      System.out.println(tVal + ", " + dVal);
    }
    br.close();

    final IDopplerCurve dc = new DopplerCurveFinMath(timeStamps, values);

    System.out.format("Inflection time stamp: %dms (%S) \n", dc
        .inflectionTime(), df.format(new java.util.Date(dc.inflectionTime())));
    System.out.format("Inflection frequency: %fHz\n", dc.inflectionFreq());
    System.out.print("Coords:");
    dc.printCoords();

    // output sample curve
    final long startT = timeStamps.get(0);
    final long endT = timeStamps.get(timeStamps.size() - 1);
    final long delta = (endT - startT) / 5;
    System.out.println("Coords for fitted curve");
    for (long i = startT; i <= endT; i += delta)
    {
      System.out.println(i + ", " + dc.valueAt(i));
    }

  }

}
