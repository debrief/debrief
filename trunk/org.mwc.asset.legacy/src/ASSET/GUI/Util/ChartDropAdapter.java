/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Oct 26, 2001
 * Time: 3:32:33 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.GUI.Util;

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
  abstract public void addParticipant(ASSET.ParticipantType newPart);

  /** process this list of file
 * @param files the list of files
 */
  public void FilesReceived(final java.util.Vector files, final java.awt.Point pt) {
    // import these files
    try
    {

      // step through the files
      for (int i = 0; i < files.size(); i++)
      {
        ASSET.ParticipantType nextPart = null;

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
