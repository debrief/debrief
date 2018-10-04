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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.StringTokenizer;

import MWC.GUI.Layers;
import MWC.GUI.Dialogs.DialogFactory;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.PlainImporterBase;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/** class to parse a label from a line of text
 */
public final class ImportNarrative extends AbstractPlainLineImporter
{
  /** the type for this string
   */
  private final String _myType = ";NARRATIVE:";


  /** read in this string and return a Label
   * @throws ParseException 
   */
  public final Object readThisLine(final String theLine) throws ParseException
  {

    // get a stream from the string
    final StringTokenizer st = new StringTokenizer(theLine);

    // declare local variables
    HiResDate DTG = null;
    String theTrack = null;
    String theEntry = null;

    // skip the comment identifier
    st.nextToken();
    
		// combine the date, a space, and the time
		final String dateToken = st.nextToken();
		final String timeToken = st.nextToken();

		// and extract the date
		DTG = DebriefFormatDateTime.parseThis(dateToken, timeToken);

    // now the track name
    theTrack = ImportFix.checkForQuotedName(st);

    // and now read in the message
    theEntry = st.nextToken("\r").trim();

    // can we trim any leading whitespace?
    theEntry = theEntry.trim();

    final NarrativeEntry entry = new NarrativeEntry(theTrack, DTG, theEntry);

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
  public final String exportThis(final MWC.GUI.Plottable theWrapper)
  {
    final NarrativeEntry theEntry = (NarrativeEntry) theWrapper;

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
  public final boolean canExportThis(final Object val)
  {
    boolean res = false;

    if(val instanceof NarrativeEntry)
    {
    	// right, we have our narrative.
    	final NarrativeEntry ne = (NarrativeEntry) val;
    	
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

    public testImport(final String val)
    {
      super(val);
    }
    
    public void setUp()
    {
      DialogFactory.setRunHeadless(true);
    }

    public void testImportWithWhitespace1() throws ParseException
    {
      final String theLine = "  ;NARRATIVE:	020421	121857	HMS_TORBAY 	GenComment	Mk Rge BAAA R121212";
      final ImportNarrative in = new ImportNarrative();
      final Object res = in.readThisLine(theLine);
      final NarrativeEntry ne = (NarrativeEntry)res;

      // check it contains the right data
      final String theDate = ne.getDTGString();
      assertEquals(theDate, "020421 121857");
      assertEquals("found track name", "HMS_TORBAY", ne.getTrackName());
    }
    
    public void testImportWithWhitespace2() throws ParseException
    {
      final String theLine = "\t;NARRATIVE:	020421	121857	HMS_TORBAY 	GenComment	Mk Rge BAAA R121212";
      final ImportNarrative in = new ImportNarrative();
      final Object res = in.readThisLine(theLine);
      final NarrativeEntry ne = (NarrativeEntry)res;

      // check it contains the right data
      final String theDate = ne.getDTGString();
      assertEquals(theDate, "020421 121857");
      assertEquals("found track name", "HMS_TORBAY", ne.getTrackName());
    }

    
    public void testImportSingleLine() throws ParseException
    {
      final String theLine = ";NARRATIVE:	020421	121857	HMS_TORBAY 	GenComment	Mk Rge BAAA R121212";
      final ImportNarrative in = new ImportNarrative();
      final Object res = in.readThisLine(theLine);
      final NarrativeEntry ne = (NarrativeEntry)res;

      // check it contains the right data
      final String theDate = ne.getDTGString();
      assertEquals(theDate, "020421 121857");
      assertEquals("found track name", "HMS_TORBAY", ne.getTrackName());
    }

    public void testImportQuotedLine() throws ParseException
    {
      final String theLine = ";NARRATIVE:	020421	121857	\"HMS TORBAY\" 	GenComment	Mk Rge BAAA R121212";
      final ImportNarrative in = new ImportNarrative();
      final Object res = in.readThisLine(theLine);
      final NarrativeEntry ne = (NarrativeEntry)res;

      // check it contains the right data
      final String theDate = ne.getDTGString();
      assertEquals("020421 121857", theDate);
      assertEquals("found track name", "HMS TORBAY", ne.getTrackName());
    }

    public void testImportNarrative()
    {

      final String testFile = "../org.mwc.debrief.legacy/src" +
      		"/Debrief/ReaderWriter/Replay/test_import_narrative.rep";

      
      // check we can find the file
      final java.io.File file = new java.io.File(testFile);
      assertTrue("test file not found:" + testFile, file.exists());
      
      // ok, now try to read it in
      final Layers theHolder = new Layers();

      // add the REP importer
      MWC.Utilities.ReaderWriter.ImportManager.addImporter
        (new Debrief.ReaderWriter.Replay.ImportReplay());

      final PlainImporterBase pib = new Debrief.ReaderWriter.Replay.ImportReplay();


      try
      {
        pib.importThis(testFile, new FileInputStream(testFile), theHolder);
      }
      catch(final FileNotFoundException e)
      {
        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }

      // check we have the right number of lines read in
      final NarrativeWrapper nw = (NarrativeWrapper)theHolder.findLayer(LayerHandler.NARRATIVE_LAYER);
      assertNotNull(nw);

      // and count the items
      assertEquals("read in correct num of lines", 11, nw.getData().size());
    }
  }


  /** do some narrative import checking
   *
   * @param args
   * @throws ParseException 
   */
  public static void main(final String[] args) throws ParseException
  {
    final testImport ti = new testImport("tester");
    ti.testImportSingleLine();
    ti.testImportQuotedLine();
    ti.testImportNarrative();

  }


}








