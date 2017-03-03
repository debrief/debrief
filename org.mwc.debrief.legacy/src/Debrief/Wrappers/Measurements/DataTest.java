package Debrief.Wrappers.Measurements;

import Debrief.Wrappers.Measurements.CoreDataset.Measurement;
import junit.framework.TestCase;

public class DataTest extends TestCase
{

  public void testMe()
  {
    CoreDataset<Long, Double> timeD =
        new CoreDataset<Long, Double>("TimeDouble");
    timeD.printAll();

    timeD.add(12L, 13D);
    
    Measurement newM = timeD.new Measurement(13L, 12D);
    timeD.add(newM);

    timeD.printAll();
  }

}
