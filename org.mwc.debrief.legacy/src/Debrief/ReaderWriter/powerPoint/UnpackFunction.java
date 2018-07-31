package Debrief.ReaderWriter.powerPoint;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class UnpackFunction
{

  public String unpackFunction(final String pptx_path) throws ZipException,
      DebriefException
  {
    return unpackFunction(pptx_path, "");
  }

  public String unpackFunction(final String pptx_path, String unpack_path)
      throws ZipException, DebriefException
  {
    if (unpack_path.isEmpty())
    {
      unpack_path = pptx_path.substring(0, pptx_path.length() - 5);
    }

    // check if unpack_path is directory or not
    if (!Files.exists(Paths.get(pptx_path)) || !pptx_path.endsWith("pptx"))
    {
      throw new DebriefException("pptx_path provided is not a pptx file");
    }

    // Unpack the pptx file
    System.out.println("Unpacking pptx file...");
    if (Files.notExists(Paths.get(unpack_path)))
    {
      new File(unpack_path).mkdir();
    }

    if (Files.exists(Paths.get(unpack_path)))
    {
      try
      {
        FileUtils.deleteDirectory(new File(unpack_path));
      }
      catch (final IOException e)
      {
        throw new DebriefException("Impossible to remove the directory "
            + unpack_path);
      }
    }

    final ZipFile zip_ref = new ZipFile(pptx_path);
    zip_ref.extractAll(unpack_path);
    System.out.println("File unpacked at " + unpack_path);
    return unpack_path;
  }
}
