// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ImportNarrative.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.7 $
// $Log: ImportNarrative.java,v $
// Revision 1.7  2006/08/08 12:55:29  Ian.Mayo
// Restructure loading narrative entries (so we can see it from CMAP)
//
// Revision 1.6  2006/07/17 11:06:15  Ian.Mayo
// Tidy export formatting, only export if not Narrative2 entrty
//
// Revision 1.5  2005/12/13 09:04:36  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.4  2004/11/25 10:24:16  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.3  2004/11/11 11:52:44  Ian.Mayo
// Reflect new directory structure
//
// Revision 1.2  2004/08/19 14:12:47  Ian.Mayo
// Allow multi-word track names (using quote character)
//
// Revision 1.1.1.2  2003/07/21 14:47:49  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.5  2003-07-01 14:11:50+01  ian_mayo
// extend test
//
// Revision 1.4  2003-06-03 16:25:32+01  ian_mayo
// Minor tidying, lots of testing
//
// Revision 1.3  2003-03-19 15:37:49+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 11:34:44+01  ian_mayo
// Check we're of the correct type
//
// Revision 1.1  2002-05-28 09:12:10+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:29:37+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-02-26 16:35:47+00  administrator
// DateFormat object in ImportReplay isn't public, access via static method
//
// Revision 1.0  2001-07-17 08:41:33+01  administrator
// Initial revision
//
// Revision 1.2  2001-07-12 12:14:18+01  novatech
// Trim any leading whitespace from the narrative text
//
// Revision 1.1  2001-07-09 13:57:58+01  novatech
// Initial revision
//


package Debrief.ReaderWriter.Replay;

import MWC.TacticalData.NarrativeEntry;
import MWC.Utilities.ReaderWriter.*;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

import java.io.*;
import java.util.*;

import MWC.GenericData.*;
import MWC.GUI.Layers;
import Debrief.Wrappers.*;

/** class to parse a label from a line of text
 */
public final class ImportNarrative implements PlainLineImporter
{
  /** the type for this string
   */
  private final String _myType = ";NARRATIVE:";


  /** read in this string and return a Label
   */
  public final Object readThisLine(String theLine)
  {

    // get a stream from the string
    StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    HiResDate DTG = null;
    String theTrack = null;
    String theEntry = null;
    String dateStr = null;

    // skip the comment identifier
    st.nextToken();

    // combine the date, a space, and the time
    dateStr = st.nextToken() + " " + st.nextToken();

    // and extract the date
    DTG = DebriefFormatDateTime.parseThis(dateStr);

    // now the track name
    theTrack = ImportFix.checkForQuotedTrackName(st);

    // and now read in the message
    theEntry = st.nextToken("\r").trim();

    // can we trim any leading whitespace?
    theEntry = theEntry.trim();

    NarrativeEntry entry = new NarrativeEntry(theTrack, DTG, theEntry);

    return entry;
  }

  /** determine the identifier returning this type of annotation
   */
  public final String getYourType()
  {
    return _myType;
  }

  /** export the specified shape as a string
   * @return the shape in String form
   * @param theWrapper the Shape we are exporting
   */
  public final String exportThis(MWC.GUI.Plottable theWrapper)
  {
    NarrativeEntry theEntry = (NarrativeEntry) theWrapper;

    String line = null;

    line = _myType;
    line = line + " " +  DebriefFormatDateTime.toStringHiRes(theEntry.getDTG());

    // careful how we export it, in case it's a multi-word track name
    ImportFix.exportTrackName(theEntry.getTrackName(), line);

    line = line + " " + theEntry.getEntry();


    return line;
  }


  /** indicate if you can export this type of object
   * @param val the object to test
   * @return boolean saying whether you can do it
   */
  public final boolean canExportThis(Object val)
  {
    boolean res = false;

    if(val instanceof NarrativeEntry)
    {
    	// right, we have our narrative.
    	NarrativeEntry ne = (NarrativeEntry) val;
    	
    	// does it have a type?  if it does, it should be the type-2 narrative exporter that's used
    	if(ne.getType() == null)
    		res = true;
    }

    return res;

  }


  /////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testImport extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public testImport(String val)
    {
      super(val);
    }

    public void testImportSingleLine()
    {
      final String theLine = ";NARRATIVE:	020421	121857	HMS_TORBAY 	GenComment	Mk Rge BAAA R121212";
      ImportNarrative in = new ImportNarrative();
      Object res = in.readThisLine(theLine);
      NarrativeEntry ne = (NarrativeEntry)res;

      // check it contains the right data
      String theDate = ne.getDTGString();
      assertEquals(theDate, "020421 121857");
      assertEquals("found track name", "HMS_TORBAY", ne.getTrackName());
    }

    public void testImportQuotedLine()
    {
      final String theLine = ";NARRATIVE:	020421	121857	\"HMS TORBAY\" 	GenComment	Mk Rge BAAA R121212";
      ImportNarrative in = new ImportNarrative();
      Object res = in.readThisLine(theLine);
      NarrativeEntry ne = (NarrativeEntry)res;

      // check it contains the right data
      String theDate = ne.getDTGString();
      assertEquals("020421 121857", theDate);
      assertEquals("found track name", "HMS TORBAY", ne.getTrackName());
    }

    public void testImportNarrative()
    {

      final String testFile = "src" +
      		"/Debrief/ReaderWriter/Replay/test_import_narrative.rep";

      
      // check we can find the file
      java.io.File file = new java.io.File(testFile);
      assertTrue("test file not found:" + testFile, file.exists());
      
      // ok, now try to read it in
      Layers theHolder = new Layers();

      // add the REP importer
      MWC.Utilities.ReaderWriter.ImportManager.addImporter
        (new Debrief.ReaderWriter.Replay.ImportReplay());

      PlainImporterBase pib = new Debrief.ReaderWriter.Replay.ImportReplay();


      try
      {
        pib.importThis(testFile, new FileInputStream(testFile), theHolder);
      }
      catch(FileNotFoundException e)
      {
        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }

      // check we have the right number of lines read in
      NarrativeWrapper nw = (NarrativeWrapper)theHolder.findLayer(ImportReplay.NARRATIVE_LAYER);
      assertNotNull(nw);

      // and count the items
      assertEquals("read in correct num of lines", 11, nw.getData().size());
    }
  }


  /** do some narrative import checking
   *
   * @param args
   */
  public static void main(String[] args)
  {
    testImport ti = new testImport("tester");
    ti.testImportSingleLine();
    ti.testImportQuotedLine();
    ti.testImportNarrative();

  }


}








