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
// $RCSfile: ImportNarrative2.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.5 $
// $Log: ImportNarrative2.java,v $
// Revision 1.5  2006/08/08 12:55:30  Ian.Mayo
// Restructure loading narrative entries (so we can see it from CMAP)
//
// Revision 1.4  2006/07/17 11:05:13  Ian.Mayo
// Export the type data
//
// Revision 1.3  2005/12/13 09:04:36  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2005/05/13 07:32:23  Ian.Mayo
// Sort out tests
//
// Revision 1.1  2005/05/12 14:11:44  Ian.Mayo
// Allow import of typed-narrative entry
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

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;

import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

/**
 * class to parse a label from a line of text
 */
public final class ImportNarrative2 extends AbstractPlainLineImporter
{
  /**
   * the type for this string
   */
  private final String _myType = ";NARRATIVE2:";

  /**
   * read in this string and return a Label
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
    String theType = null;

    // skip the comment identifier
    st.nextToken();

    // combine the date, a space, and the time
    final String dateToken = st.nextToken();
    final String timeToken = st.nextToken();

    // and extract the date
    DTG = DebriefFormatDateTime.parseThis(dateToken, timeToken);

    // now the track name
    theTrack = ImportFix.checkForQuotedName(st);

    // start off with the type
    theType = ImportFix.checkForQuotedName(st);

    // and now read in the message
    theEntry = st.nextToken("\r");

    // can we trim any leading whitespace?
    theEntry = theEntry.trim();

    final NarrativeEntry entry =
        new NarrativeEntry(theTrack, theType, DTG, theEntry);

    return entry;
  }

  /**
   * determine the identifier returning this type of annotation
   */
  public final String getYourType()
  {
    return _myType;
  }

  /**
   * export the specified shape as a string
   * 
   * @return the shape in String form
   * @param theWrapper
   *          the Shape we are exporting
   */
  public final String exportThis(final MWC.GUI.Plottable theWrapper)
  {
    final NarrativeEntry theEntry = (NarrativeEntry) theWrapper;

    String line = null;

    line = _myType;
    line = line + " " + DebriefFormatDateTime.toStringHiRes(theEntry.getDTG());

    // careful how we export it, in case it's a multi-word track name
    line = ImportFix.exportTrackName(theEntry.getTrackName(), line);

    line = line + " " + theEntry.getType() + " " + theEntry.getEntry();

    return line;
  }

  /**
   * indicate if you can export this type of object
   * 
   * @param val
   *          the object to test
   * @return boolean saying whether you can do it
   */
  public final boolean canExportThis(final Object val)
  {
    boolean res = false;

    if (val instanceof NarrativeEntry)
    {
      // right, it's narrative data, but does it have a type?
      final NarrativeEntry ne = (NarrativeEntry) val;
      if (ne.getType() != null)
        // yup, let's export it then.
        res = true;
    }

    return res;

  }

  // ///////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testImport extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public testImport(final String val)
    {
      super(val);
    }

    public void testImportSingleLine() throws ParseException
    {
      final String theLine =
          ";NARRATIVE2:	020421	121857	HMS_TORBAY 	GenComment	Mk Rge BAAA R121212";
      final ImportNarrative2 in = new ImportNarrative2();
      final Object res = in.readThisLine(theLine);
      final NarrativeEntry ne = (NarrativeEntry) res;

      // check it contains the right data
      final String theDate = ne.getDTGString();
      assertEquals(theDate, "020421 121857");
      assertEquals("found track name", "HMS_TORBAY", ne.getTrackName());
      assertEquals("type matches", "GenComment", ne.getType());
    }

    public void testImportQuotedLine() throws ParseException
    {
      final String theLine =
          ";NARRATIVE2:	020421	121857	\"HMS TORBAY\" 	GenComment2	Mk Rge BAAA R121212";
      final ImportNarrative2 in = new ImportNarrative2();
      final Object res = in.readThisLine(theLine);
      final NarrativeEntry ne = (NarrativeEntry) res;

      // check it contains the right data
      final String theDate = ne.getDTGString();
      assertEquals("020421 121857", theDate);
      assertEquals("found track name", "HMS TORBAY", ne.getTrackName());
      assertEquals("type matches", "GenComment2", ne.getType());

    }

  }

  /**
   * do some narrative import checking
   * 
   * @param args
   * @throws IOException
   */
  @SuppressWarnings("deprecation")
  public static void main(final String[] args) throws IOException
  {
    long startDate = new Date(1995, 11, 12, 5, 0, 0).getTime();
    long endDate = new Date(1995, 11, 12, 11, 45, 0).getTime();
    // long endDate = new Date(1995,11,11,11,44,0).getTime();

    String[] tracks = new String[]
    {"NELSON", "COLLINGWOOD"};
    String[] types = new String[]
    {"TYPE_1", "TYPE_2", "TYPE_3", "TYPE_4", "TYPE_5"};
    String[] phrases = new String[]
    {"URGENT", "MAJOR", "IMPORTANT", "DIAGONAL", "SV311"};

    FileWriter oFile = new FileWriter("test_out.rep");

    ImportNarrative2 in = new ImportNarrative2();
    LoremIpsum lorem = new LoremIpsum();

    for (long thisT = startDate; thisT < endDate; thisT += 2000)
    {
      String track = tracks[(int) (Math.random() * tracks.length)];
      String type = types[(int) (Math.random() * types.length)];
      HiResDate dtg = new HiResDate(thisT);

      String entry;

      if (Math.random() < 0.95)
      {
        entry = lorem.words(9);
      }
      else
      {
        entry = lorem.paragraphs(2);
        entry = entry.replace("\n", "<br/>");
      }

      if (Math.random() > 0.9)
      {
        entry += " " + phrases[(int) (Math.random() * phrases.length)];
      }
      else if (Math.random() > 0.95)
      {
        entry = phrases[(int) (Math.random() * phrases.length)] + " " + entry;
      }
      else if (Math.random() > 0.95)
      {
        entry =
            entry + " " + phrases[(int) (Math.random() * phrases.length)] + " "
                + entry;
      }

      NarrativeEntry newE = new NarrativeEntry(track, type, dtg, entry);
      oFile.write(in.exportThis(newE));
      oFile.write("\n");
    }

    oFile.close();

  }

  /*
   * Copyright 2010 Oliver C Dodd http://01001111.net Licensed under the MIT license:
   * http://www.opensource.org/licenses/mit-license.php
   */
  public static class LoremIpsum
  {

    /*
     * The Lorem Ipsum Standard Paragraph
     */
    protected final String standard =
        "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    private final String[] words =
    {"a", "ac", "accumsan", "ad", "adipiscing", "aenean", "aliquam", "aliquet",
        "amet", "ante", "aptent", "arcu", "at", "auctor", "augue", "bibendum",
        "blandit", "class", "commodo", "condimentum", "congue", "consectetur",
        "consequat", "conubia", "convallis", "cras", "cubilia", "cum",
        "curabitur", "curae", "cursus", "dapibus", "diam", "dictum",
        "dictumst", "dignissim", "dis", "dolor", "donec", "dui", "duis",
        "egestas", "eget", "eleifend", "elementum", "elit", "enim", "erat",
        "eros", "est", "et", "etiam", "eu", "euismod", "facilisi", "facilisis",
        "fames", "faucibus", "felis", "fermentum", "feugiat", "fringilla",
        "fusce", "gravida", "habitant", "habitasse", "hac", "hendrerit",
        "himenaeos", "iaculis", "id", "imperdiet", "in", "inceptos", "integer",
        "interdum", "ipsum", "justo", "lacinia", "lacus", "laoreet", "lectus",
        "leo", "libero", "ligula", "litora", "lobortis", "lorem", "luctus",
        "maecenas", "magna", "magnis", "malesuada", "massa", "mattis",
        "mauris", "metus", "mi", "molestie", "mollis", "montes", "morbi",
        "mus", "nam", "nascetur", "natoque", "nec", "neque", "netus", "nibh",
        "nisi", "nisl", "non", "nostra", "nulla", "nullam", "nunc", "odio",
        "orci", "ornare", "parturient", "pellentesque", "penatibus", "per",
        "pharetra", "phasellus", "placerat", "platea", "porta", "porttitor",
        "posuere", "potenti", "praesent", "pretium", "primis", "proin",
        "pulvinar", "purus", "quam", "quis", "quisque", "rhoncus", "ridiculus",
        "risus", "rutrum", "sagittis", "sapien", "scelerisque", "sed", "sem",
        "semper", "senectus", "sit", "sociis", "sociosqu", "sodales",
        "sollicitudin", "suscipit", "suspendisse", "taciti", "tellus",
        "tempor", "tempus", "tincidunt", "torquent", "tortor", "tristique",
        "turpis", "ullamcorper", "ultrices", "ultricies", "urna", "ut",
        "varius", "vehicula", "vel", "velit", "venenatis", "vestibulum",
        "vitae", "vivamus", "viverra", "volutpat", "vulputate"};
    private final String[] punctuation =
    {".", "?"};
    private final String _n = System.getProperty("line.separator");
    private Random random = new Random();

    public LoremIpsum()
    {
    }

    /**
     * Get a random word
     */
    public String randomWord()
    {
      return words[random.nextInt(words.length - 1)];
    }

    /**
     * Get a random punctuation mark
     */
    public String randomPunctuation()
    {
      return punctuation[random.nextInt(punctuation.length - 1)];
    }

    /**
     * Get a string of words
     * 
     * @param count
     *          - the number of words to fetch
     */
    public String words(int count)
    {
      StringBuilder s = new StringBuilder();
      while (count-- > 0)
        s.append(randomWord()).append(" ");
      return s.toString().trim();
    }

    /**
     * Get a sentence fragment
     */
    public String sentenceFragment()
    {
      return words(random.nextInt(10) + 3);
    }

    /**
     * Get a sentence
     */
    public String sentence()
    {
      // first word
      String w = randomWord();
      StringBuilder s =
          new StringBuilder(w.substring(0, 1).toUpperCase()).append(
              w.substring(1)).append(" ");
      // commas?
      if (random.nextBoolean())
      {
        int r = random.nextInt(3) + 1;
        for (int i = 0; i < r; i++)
          s.append(sentenceFragment()).append(", ");
      }
      // last fragment + punctuation
      return s.append(sentenceFragment()).append(randomPunctuation())
          .toString();
    }

    /**
     * Get multiple sentences
     * 
     * @param count
     *          - the number of sentences
     */
    public String sentences(int count)
    {
      StringBuilder s = new StringBuilder();
      while (count-- > 0)
        s.append(sentence()).append("  ");
      return s.toString().trim();
    }

    /**
     * Get a paragraph
     * 
     * @useStandard - get the standard Lorem Ipsum paragraph?
     */
    public String paragraph(boolean useStandard)
    {
      return useStandard ? standard : sentences(random.nextInt(3) + 2);
    }

    @SuppressWarnings("unused")
    public String paragraph()
    {
      return paragraph(false);
    }

    /**
     * Get multiple paragraphs
     * 
     * @param count
     *          - the number of paragraphs
     * @useStandard - begin with the standard Lorem Ipsum paragraph?
     */
    public String paragraphs(int count, boolean useStandard)
    {
      StringBuilder s = new StringBuilder();
      while (count-- > 0)
      {
        s.append(paragraph(useStandard)).append(_n);
        useStandard = false;
      }
      return s.toString().trim();
    }

    public String paragraphs(int count)
    {
      return paragraphs(count, false);
    }
  }

}
