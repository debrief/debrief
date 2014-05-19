// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: PlainImporter.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.4 $
// $Log: PlainImporter.java,v $
// Revision 1.4  2007/06/01 13:46:05  ian.mayo
// Improve performance of export text to clipboard
//
// Revision 1.3  2006/05/24 14:46:02  Ian.Mayo
// Reflect change in exportThis method (return string exported)
//
// Revision 1.2  2004/05/24 16:24:36  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:27  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:51  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:06+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:13:56+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:25+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:42:46+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:35+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:52:10  ianmayo
// initial version
//
// Revision 1.4  2000-11-17 08:49:22+00  ian_mayo
// switch to interface from class
//
// Revision 1.3  2000-11-02 16:44:38+00  ian_mayo
// changing Layer into Interface, replaced by BaseLayer
//
// Revision 1.2  2000-08-07 12:22:57+01  ian_mayo
// correct signature
//
// Revision 1.1  2000-02-22 13:50:19+00  ian_mayo
// Initial revision
//
// Revision 1.3  1999-11-24 08:46:46+00  ian_mayo
// added list of suffix strings which we can import
//
// Revision 1.2  1999-11-12 14:36:37+00  ian_mayo
// make classes do export aswell as import
//
// Revision 1.1  1999-10-12 15:34:14+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-07-27 09:27:28+01  administrator
// added more error handlign
//
// Revision 1.1  1999-07-07 11:10:17+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:08+01  sm11td
// Initial revision
//
// Revision 1.5  1999-06-16 15:24:22+01  sm11td
// before move around/ end of phase 1
//
// Revision 1.4  1999-06-01 16:49:18+01  sm11td
// Reading in tracks aswell as fixes, commenting large portions of source code
//
// Revision 1.3  1999-02-04 08:02:24+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.2  1999-02-01 16:08:48+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.1  1999-01-31 13:33:05+00  sm11td
// Initial revision
//

package MWC.Utilities.ReaderWriter;



import MWC.GUI.Layers;
import MWC.GUI.Plottable;



public interface PlainImporter
{

    /** general command used to import a whole file of a specific type
     */
    public void importThis(String fName,
                           java.io.InputStream is, Layers theData);

    public void importThis(String fName,
                                       java.io.InputStream is);

    /** read in this whole file
     */
    public boolean canImportThisFile(String theFile);

    /** export this item using this format
     */
    public void exportThis(Plottable item);
    
    /** export this item using this format
     */
    public void exportThis(String comment);

    /** signal problem importing data
     */
    public void readError(String fName, int line, String msg, String thisLine);

    /** start the export process
     * 
     * @param item identifier for this sequence
     */
		public void startExport(Plottable item);

		/** finish the export process
		 * 
     * @param item identifier for this sequence
		 */
		public void endExport(Plottable item);


}