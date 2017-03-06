package Debrief.Wrappers.Extensions.Measurements;

import junit.framework.TestCase;

public class DataTest extends TestCase
{

  public void testStructure()
  {
    DataFolder d0 = new DataFolder("l0");
    DataFolder d0_1 = new DataFolder("l0_1");
    DataFolder d0_1_1 = new DataFolder("l0_1_1");
    DataFolder d0_1_2 = new DataFolder("l0_1_2");
    DataFolder d0_2 = new DataFolder("l0_2");
    
    d0.add(d0_1);
    d0_1.add(d0_1_1);    
    d0_1.add(d0_1_2);
    d0.add(d0_2);
    
    CoreDataset<Long, Double> timeD1 =
        new CoreDataset<Long, Double>("TimeDouble", "Some units");
    timeD1.add(12L, 13D);
    timeD1.add(14L, 15D);

    CoreDataset<Long, Double> timeD2 =
        new CoreDataset<Long, Double>("TimeDouble", "Some units");
    timeD2.add(22L, 23D);
    timeD2.add(34L, 25D);

    CoreDataset<Long, String> timeS1 =
        new CoreDataset<Long, String>("TimeString", "Some units");
    timeS1.add(12L, ""+ 13d);
    timeS1.add(14L, "" + 15D);
    
    d0_1.add(timeD1);
    d0_1.add(timeS1);    
    d0_1_1.add(timeD2);
    
    d0.printAll();
    
  }
  
  public void testDataset()
  {
    CoreDataset<Long, Double> timeD =
        new CoreDataset<Long, Double>("TimeDouble", "Some units");
    
    timeD.printAll();

    timeD.add(12L, 13D);
    
    CoreDataset<Long, Double>.Measurement newM = timeD.new Measurement(13L, 12D);
    timeD.add(newM);

    timeD.printAll();
  }
  
}
