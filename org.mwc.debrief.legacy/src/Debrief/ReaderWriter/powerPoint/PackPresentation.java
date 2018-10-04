package Debrief.ReaderWriter.powerPoint;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import Debrief.GUI.Frames.Application;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class PackPresentation
{

  /**
   * Given an unpacked pptx, it creates a pptx file.
   * 
   * @param pptx_path
   *          pptx file path (Optional). Null to create the unpack_path filename
   * @param unpack_path
   *          Folder that contains the pptx slides/documents
   * @throws DebriefException
   *           In case we don't provide the unpack path or it is not a directory
   */
  public String pack(final String pptx_path, final String unpack_path)
      throws IOException, ZipException, DebriefException
  {
    if (unpack_path == null)
    {
      throw new DebriefException(
          "Provide unpack_path (path to directory containing unpacked pptx)");
    }

    final String path;
    
    if (pptx_path != null)
    {
      path = pptx_path;
    }
    else if (unpack_path.charAt(unpack_path.length() - 1) == '/' || unpack_path
          .charAt(unpack_path.length() - 1) == '\\')
      {
      path = unpack_path.substring(0, unpack_path.length() - 1)
            + ".pptx";
      }
      else
      {
        path = unpack_path + ".pptx";
      }
    

    // check if unpack_path is directory or not
    if (!Files.isDirectory(Paths.get(unpack_path)))
    {
      throw new DebriefException("unpack_path provided is not a directory");
    }

    // Pack the unpack_path folder to pptx_path pptx file
    Files.deleteIfExists(new File(path).toPath());
    final ZipFile zipFile = new ZipFile(path);

    final ZipParameters parameters = new ZipParameters();
    parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

    parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
    parameters.setIncludeRootFolder(false);
    zipFile.addFolder(unpack_path + "/", parameters);
    Application.logError2(Application.INFO, "File packed at " + path, null);

    try
    {
      FileUtils.deleteDirectory(new File(unpack_path));
    }
    catch (final IOException e)
    {
      throw new DebriefException("Impossible to remove the directory "
          + unpack_path);
    }
    return path;
  }
}
