// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: PlainLineImporter.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: PlainLineImporter.java,v $
// Revision 1.3  2004/08/19 14:12:54  Ian.Mayo
// Allow multi-word track names (using quote character)
//
// Revision 1.2  2004/05/24 16:24:39  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:27  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:51  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:05+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:13:56+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:25+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:42:47+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:41:36+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:52:10  ianmayo
// initial version
//
// Revision 1.1  2000-02-22 13:50:19+00  ian_mayo
// Initial revision
//
// Revision 1.2  1999-11-12 14:36:37+00  ian_mayo
// make classes do export aswell as import
//
// Revision 1.1  1999-10-12 15:34:15+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-07-27 09:27:29+01  administrator
// added more error handlign
//
// Revision 1.1  1999-07-07 11:10:17+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:08+01  sm11td
// Initial revision
//
// Revision 1.2  1999-06-16 15:24:21+01  sm11td
// before move around/ end of phase 1
//
// Revision 1.1  1999-06-04 08:45:31+01  sm11td
// Initial revision
//
// Revision 1.1  1999-01-31 13:33:04+00  sm11td
// Initial revision
//

package MWC.Utilities.ReaderWriter;


/** interface describing behavior for an import function which reads a line 
 * of data at a time
 */
public interface PlainLineImporter 
{

  /**
   * the normal token delimiter (for comma & white-space separated fields)
   */
  static final String normalDelimiters = " \t\n\r\f";

  /**
   * the quoted delimiter, for quoted track names
   */
  static final String quoteDelimiter = "\"";


	/** parse this line and return the object created
	 */
  public Object readThisLine(String theLine);
	
	/** @return the comment identifier for this line
	 */
  public String getYourType();
	
	/** export the specified shape as a string
	 * @return the shape in String form
	 * @param theShape the Shape we are exporting
	 */	
	public String exportThis(MWC.GUI.Plottable theShape);	
	
	/** indicate if you can export this type of object
	 */
	public boolean canExportThis(Object val);
	
}
