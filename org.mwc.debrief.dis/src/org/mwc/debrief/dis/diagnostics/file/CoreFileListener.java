package org.mwc.debrief.dis.diagnostics.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class CoreFileListener
{
  /**
   * 
   */
  final private String _path;
  private FileWriter _outF;
  final private boolean _toFile;
  final private boolean _toScreen;
  private String _dataType;
  private String _header;

  final String LINE_BREAK = System.getProperty("line.separator");
  final String SUFFIX = ".csv";

  /**
   * 
   * @param root
   *          the path for the output file
   * @param toFile
   *          whether to write to file
   * @param toScreen
   *          whether to write to standard output
   * @param headlessDISLogger
   *          TODO
   */
  CoreFileListener(String root, boolean toFile, boolean toScreen,
      String dataType, String header)
  {
    _path = root;
    _dataType = dataType;
    _toFile = toFile;
    _toScreen = toScreen;
    _header = header;
  }

  protected void write(String output)
  {
    if (_toScreen)
    {
      // output the normal line
      System.out.print(_dataType + ":" +output);
    }

    if (_toFile)
    {
      // is our file created?
      try
      {
        if (_outF == null)
        {
          // write the header
          createOut(_dataType + SUFFIX, _header);
        }

        // and our output
        _outF.write(output);

        // flush - so we have as many lines in there as possible
        _outF.flush();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }

  protected void createOut(String filename, String header) throws IOException
  {
    // check the output folder exists
    File folder = new File(_path);
    if (!folder.exists())
    {
      // nope, create the necessary folders
      folder.mkdirs();
    }

    // ok, create it
    _outF = new FileWriter(new File(_path, filename));

    // and insert the header line
    _outF.write(header);
    _outF.write(LINE_BREAK);
  }
}