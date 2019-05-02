package MWC.TacticalData;

import junit.framework.TestCase;

/**
 * utility class to handle converting between slider range and time values
 *
 * @author ian
 *
 */
public class SliderConverter
{
  private int range;
  private long origin;
  // have one second steps
  private final int step = 1000;

  public int getCurrentAt(final long now)
  {
    return (int) Math.round((double) (now - origin) / step);
  }

  public int getEnd()
  {
    return range;
  }

  public int getStart()
  {
    return 0;
  }

  public long getTimeAt(final int position)
  {
    return origin + (position * step);
  }

  public void init(final long start, final long end)
  {
    origin = start;
    range = (int) ((end - start) / step);
  }
  
  public static class SliderConverterTest extends TestCase
  {
    public void testConverter()
    {
      final SliderConverter test = new SliderConverter();
      test.init(1240423198490L, 1240427422390L);

      final int originalStep = 21;
      final long originalTime = test.getTimeAt(originalStep);
      final long roundedTime = originalTime / 1000L * 1000L;
      final int newStep = test.getCurrentAt(roundedTime);
      assertEquals("Rounding slider converter", originalStep, newStep);
    }
  }
}
