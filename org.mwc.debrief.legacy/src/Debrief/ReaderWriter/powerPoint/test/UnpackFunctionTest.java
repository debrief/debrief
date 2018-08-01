package Debrief.ReaderWriter.powerPoint.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import Debrief.ReaderWriter.powerPoint.DebriefException;
import Debrief.ReaderWriter.powerPoint.UnpackFunction;
import net.lingala.zip4j.exception.ZipException;

public class UnpackFunctionTest
{
  private final String folderToUnpack = Utils.testFolder + File.separator
      + "UnpackPresentation" + File.separator + "designed.pptx";
  private final String expectedFolder = Utils.testFolder + File.separator
      + "PackPresentation" + File.separator + "designedFolder";

  @Test
  public void testUnpackFunctionString() throws ZipException, DebriefException,
      IOException
  {
    final String generatedFolder = new UnpackFunction().unpackFunction(folderToUnpack);
    assertTrue(Utils.compareDirectoriesStructures(new File(generatedFolder),
        new File(expectedFolder)));
    FileUtils.deleteDirectory(new File(generatedFolder));
  }

}
