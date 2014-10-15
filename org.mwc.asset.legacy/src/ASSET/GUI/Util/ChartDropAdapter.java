/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package ASSET.GUI.Util;

import java.io.File;

import ASSET.NetworkParticipant;
import ASSET.Util.XML.ASSETReaderWriter;
import MWC.GenericData.WorldLocation;

abstract public class ChartDropAdapter implements MWC.GUI.DragDrop.FileDropLocationSupport.FileDropLocationListener
{
  /** the chart adapter we are accepting drops for
   *
   */
  private MWC.GUI.PlainChart _myChart = null;


  /** support for drag/drop of files onto this view
   */
  private transient MWC.GUI.DragDrop.FileDropLocationSupport _dropSupport = new MWC.GUI.DragDrop.FileDropLocationSupport();

  /***************************************************************
   *  constructor
   ***************************************************************/

  public ChartDropAdapter(final MWC.GUI.PlainChart myChart)
  {
    _myChart = myChart;

    _dropSupport = new MWC.GUI.DragDrop.FileDropLocationSupport();
    _dropSupport.addComponent(_myChart.getPanel());
    _dropSupport.setFileDropListener(this,".XML");
  }

  /***************************************************************
   *  member methods
   ***************************************************************/
  /** a participant has been read in
   * @param newPart the new participant
   */
  abstract public void addParticipant(NetworkParticipant newPart);

  /** process this list of file
 * @param files the list of files
 */
  public void FilesReceived(final java.util.Vector<File> files, final java.awt.Point pt) {
    // import these files
    try
    {

      // step through the files
      for (int i = 0; i < files.size(); i++)
      {
        NetworkParticipant nextPart = null;

        // get the next file
        final java.io.File file = (java.io.File) files.elementAt(i);

        // try to import it
        nextPart = ASSETReaderWriter.importParticipant(file.getName(), new java.io.FileInputStream(file));

        // did it work?
        if(nextPart != null)
        {
          // find out where it was dropped
          final MWC.GenericData.WorldLocation loc = new WorldLocation(_myChart.getCanvas().getProjection().toWorld(pt));
          nextPart.getStatus().setLocation(loc);

          // inform the listener
          addParticipant(nextPart);
        }
      }

    }
    catch(Exception e){
      MWC.Utilities.Errors.Trace.trace(e);
    }

  }


}
