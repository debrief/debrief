/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.core.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.ReaderWriter.Replay.ImportReplay;
import MWC.GUI.Layers;

/**
 * @author ian.mayo
 */
public class ReplayLoader extends CoreLoader
{

  public ReplayLoader(String fileType)
  {
    super("Replay", null);
  }

  /**
   * local copy of our loader - we store it so it can be accessed externally
   * 
   */
  private ImportReplay _loader;

  public ImportReplay getReplayLoader()
  {
    if (_loader == null)
      _loader = new Debrief.ReaderWriter.Replay.ImportReplay()
      {
        // override the count-lines method. We may only have a project-relative
        // to the data-file - and the legacy code won't be able to find the file.
        // we do, however have a stream for the input file - just count the
        // lines in this.
        public int countLinesFor(final String fName)
        {
          int lines = 0;
          try
          {
            // create a file-wrapper to see if we can open the file directly
            final File countFile = new File(fName);
            if (countFile.exists())
            {
              // create ourselves a fresh stream. we create some fresh streams
              // based on this one which get closed in processing
              final FileInputStream lineCounterStream = new FileInputStream(
                  fName);
              lines = super.countLinesInStream(lineCounterStream);
              lineCounterStream.close();
              DebriefPlugin.logError(Status.INFO, "Replay loader - counted:"
                  + lines + " lines", null);
            }
          }
          catch (final FileNotFoundException fe)
          {
            DebriefPlugin.logError(Status.INFO,
                "Ongoing problem related to counting lines in REP file, the counter isn't receiving sufficient file-path to open the file.",
                fe);
          }
          catch (final IOException e)
          {
            DebriefPlugin.logError(Status.ERROR,
                "Failed to open stream for counting lines:" + fName, null);
          }
          return lines;
        }

      };

    return _loader;
  }

  @Override
  protected IRunnableWithProgress getImporter(final IAdaptable target,
      final Layers theLayers, final InputStream inputStream,
      final String fileName)
  {
    return new IRunnableWithProgress()
    {
      public void run(final IProgressMonitor pm)
      {
        final ImportReplay importer = getReplayLoader();

        // clear the list of sensor names
        importer.clearPendingSensorList();

        // and do the import...
        importer.importThis(fileName, inputStream, theLayers);
      }
    };

  }
}
