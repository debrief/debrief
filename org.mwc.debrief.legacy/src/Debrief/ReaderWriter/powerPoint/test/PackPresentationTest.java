package Debrief.ReaderWriter.powerPoint.test;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import Debrief.ReaderWriter.powerPoint.DebriefException;
import Debrief.ReaderWriter.powerPoint.PackPresentation;
import net.lingala.zip4j.exception.ZipException;

public class PackPresentationTest
{

  public PackPresentationTest()
  {

  }

  private final String folderToPack = Utils.testFolder + File.separator
      + "PackPresentation" + File.separator + "designedFolder";
  private final String folderToPackTest = Utils.testFolder + File.separator
      + "PackPresentation" + File.separator + "designedFolderTest";
  private final String expectedPptx = Utils.testFolder + File.separator
      + "PackPresentation" + File.separator + "designed.pptx";
  private String generatedPptx = null;

  @Test
  public void testPack() throws IOException, ZipException, DebriefException
  {
    FileUtils.copyDirectory(new File(folderToPack), new File(folderToPackTest),
        true);
    generatedPptx = new PackPresentation().pack(null, folderToPackTest);

    assertFalse(new File(folderToPackTest).exists());
    Utils.assertZipEquals(generatedPptx, expectedPptx);
    new File(generatedPptx).delete();
  }

}
