/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2016, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package Debrief.ReaderWriter.Word;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import junit.framework.TestCase;

public class ImportASWDataDocument
{
  /**
   * helper that can ask the user a question
   *
   */
  public static interface QuestionHelper
  {
    String getTrackName(String title, String message);
  }

  public static class TestImportWord extends TestCase
  {
    public void testIsValid()
    {
      List<String> input = new ArrayList<String>();
      for(int i=0;i<20;i++)
      {
        input.add("Some old duff information");
      }
      assertFalse("not suitable for import", canImport(input));
      for(int i=0;i<10;i++)
      {
        input.add("TMPOS/Here we go!");
      }
      assertTrue("now suitable for import", canImport(input));
      
    }
  }



  /**
   * helper class that can ask the user a question populated via Dependency Injection
   */
  private static QuestionHelper questionHelper = null;

  /**
   * match a 6 figure DTG
   *
   */
  static final String DATE_MATCH_SIX = "(\\d{6})";

  static final String DATE_MATCH_FOUR = "(\\d{4})";

  public static void logThisError(final int status, final String msg,
      final Exception e)
  {
    Application.logError3(status, msg, e, true);
  }

  /**
   * do some pre-processing of text, to protect robustness of data written to file
   *
   * @param raw_text
   * @return text with some control chars removed
   */
  public static String removeBadChars(final String raw_text)
  {
    // swap soft returns for hard ones
    String res = raw_text.replace('\u000B', '\n');

    // we learned that whilst MS Word includes the following
    // control chars, and we can persist them via XML, we
    // can't restore them via SAX. So, swap them for
    // spaces
    res = res.replace((char) 1, (char) 32);
    res = res.replace((char) 19, (char) 32);
    res = res.replace((char) 8, (char) 32); // backspace char, occurred in Oct 17, near an inserted
                                            // picture
    res = res.replace((char) 20, (char) 32);
    res = res.replace((char) 21, (char) 32);
    res = res.replace((char) 5, (char) 32); // MS Word comment marker
    res = res.replace((char) 31, (char) 32); // described as units marker, but we had it prior to
                                             // subscript "2"

    // done.
    return res;
  }

  public static void setQuestionHelper(final QuestionHelper helper)
  {
    questionHelper = helper;
  }

  /**
   * check if the layers contains a single track object
   *
   * @param layers
   * @return
   */
  private static boolean singleTrackIn(final Layers layers)
  {
    int ctr = 0;
    final int len = layers.size();
    for (int i = 0; i < len; i++)
    {
      final Layer next = layers.elementAt(i);
      if (next instanceof TrackWrapper)
      {
        ctr++;

        if (ctr > 1)
        {
          break;
        }
      }
    }
    return ctr == 1;
  }

  /**
   * where we write our data
   *
   */
  private final Layers _layers;

  public ImportASWDataDocument(final Layers destination)
  {
    _layers = destination;
  }
  
  /**
   * repeatably find a color for the specified track id The color will be RED if it's a master track
   * ("M01"). Shades of gray nor ownship blue are returned.
   *
   * @param trackId
   * @return
   */
  private static Color colorFor(final String trackId)
  {
    final Color res;

    // ok, is it a master track?
    if (trackId.startsWith("M"))
    {
      res = DebriefColors.RED;
    }
    else
    {
      // ok, get the hash code
      final int hash = trackId.hashCode();
      res = DebriefColors.RandomColorProvider.getRandomColor(hash);
    }
    return res;
  }


  public static void logError(final int status, final String msg, final Exception e)
  {
    logThisError(status, msg, e);
  }

  /**
   * parse a list of strings
   *
   * @param strings
   */
  @SuppressWarnings("unused")
  public void processThese(final ArrayList<String> strings)
  {

    if (strings.isEmpty())
    {
      return;
    }
    boolean proceed = true;

    // keep track of if we've added anything
    boolean dataAdded = false;

    // ok, now we can loop through the strings
    if (proceed)
    {
      @SuppressWarnings("unused")
      int ctr = 0;
      TrackWrapper track = null;
      for (final String raw_text : strings)
      {
        // increment counter, for num lines processed
        ctr++;

        // also remove any other control chars that may throw MS Word
        final String text = removeBadChars(raw_text);
        
        // process it
      }

      if (dataAdded)
      {
        _layers.fireModified(track);
      }
    }
  }

  final private static String marker = "TMPOS";
  
  public static boolean canImport(List<String> strings)
  {
    // run through strings, see if we have TMPOS in more than 5 lines in the first 50
    final int numLines = 100;
    final int requiredLines = 5;
    int matches = 0;
    int lines = 0;
    for(final String l: strings)
    {
      lines ++;
      
      if(l.startsWith(marker))
      {
        matches++;
      }
      
      if(matches >= requiredLines)
      {
        return true;
      }
      
      if(lines > numLines)
      {
        break;
      }
      
    }
    return false;
  }
  
}
