package Debrief.ReaderWriter.powerPoint.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashMap;

import org.junit.Test;

import Debrief.ReaderWriter.powerPoint.DebriefException;
import Debrief.ReaderWriter.powerPoint.FindMap;

public class FindMapTest
{
  public FindMapTest()
  {

  }

  @Test
  public void testGetMapDetails() throws DebriefException
  {
    final String sampleDonorPathFile = Utils.testFolder + File.separator
        + "FindMap";

    assertEquals(FindMap.getMapDetails(sampleDonorPathFile),
        new HashMap<String, String>()
        {
          /**
           * Known Result
           */
          private static final long serialVersionUID = -4264437335359313998L;

          {
            put("cx", "6703821");
            put("cy", "4670507");
            put("name", "map");
            put("x", "2486111");
            put("y", "265548");
          }
        });
  }

}
