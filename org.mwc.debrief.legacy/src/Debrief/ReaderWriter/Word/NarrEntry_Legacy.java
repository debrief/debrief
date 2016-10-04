package Debrief.ReaderWriter.Word;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import MWC.GenericData.HiResDate;

class NarrEntry_Legacy
{
  HiResDate dtg;
  String type;
  String platform;
  String text;

  boolean appendedToPrevious = false;

  // ///////////////////
  // static variables to help handle corrupt/incomplete data.
  // NOTE: any new ones should be included in the "reset() processing
  // ///////////////////

  private static Date lastDtg;
  private static String lastPlatform;
  private static NarrEntry_Legacy lastEntry;
  /**
   * we've encountered circumstances where copy/paste has ended up with the day being earlier than
   * the current one When we can detect this, we'll use the previous day.
   */
  private static String lastDay;

  static public NarrEntry_Legacy create(final String msg, final int lineNum)
  {
    NarrEntry_Legacy res = null;
    try
    {
      res = new NarrEntry_Legacy(msg);

      if (res.appendedToPrevious && res.text != null)
      {
        // that's ok - we'll let the parent handle it
      }
      else
      {
        // just check it's valid
        final boolean valid =
            (res.dtg != null) && (res.type != null) && (res.platform != null)
                && (res.text != null);
        if (!valid)
        {
          res = null;
        }
      }
    }
    catch (final ParseException e)
    {
      ImportNarrativeDocument.logThisError("Failed whilst parsing Word Document, at line:" + lineNum,
          e);
    }

    return res;
  }

  /**
   * reset the static variables we use to handle missing, or mangled data
   * 
   */
  public static void reset()
  {
    lastDtg = null;
    lastPlatform = null;
    lastEntry = null;
    lastDay = null;
  }

  @SuppressWarnings("deprecation")
  public NarrEntry_Legacy(final String entry) throws ParseException
  {
    final String trimmed = entry.trim();
    final String[] parts = trimmed.split(",");
    int ctr = 0;

    // sort out our date formats
    final DateFormat fourBlock = new SimpleDateFormat("HHmm");
    fourBlock.setTimeZone(TimeZone.getTimeZone("UTC"));

    // final DateFormat sixBlock = new SimpleDateFormat("ddHHmm");
    // sixBlock.setTimeZone(TimeZone.getTimeZone("UTC"));

    final boolean correctLength = parts.length > 5;
    final boolean sixFigDTG =
        correctLength && parts[0].length() == 6
            && parts[0].matches(ImportNarrativeDocument.DATE_MATCH_SIX);
    final boolean fourFigDTG =
        correctLength && parts[0].length() == 4
            && parts[0].matches(ImportNarrativeDocument.DATE_MATCH_FOUR);
    final boolean hasDTG = sixFigDTG || fourFigDTG;

    if (hasDTG)
    {
      final String dtgStr;
      if (fourFigDTG)
      {
        dtgStr = parts[ctr++];
      }
      else
      {
        dtgStr = parts[ctr++].substring(2, 6);
      }

      // ok, sort out the time first
      String dayStr = parts[ctr++];
      final String monStr = parts[ctr++];
      final String yrStr = parts[ctr++];
      platform = parts[ctr++].trim();
      type = parts[ctr++].trim();

      /**
       * special processing, to overcome the previous day being used
       * 
       */
      if (lastDay != null
          && Integer.parseInt(dayStr) < Integer.parseInt(lastDay))
      {
        dayStr = lastDay;
      }
      else
      {
        // it's valid, update the last day
        lastDay = dayStr;
      }

      // hmm, on occasion we don't get the closing comma on the entry type
      if (type.length() > 20)
      {
        final int firstSpace = type.indexOf(" ");
        // note: should actually be looking for non-alphanumeric, since it may be a tab
        type = type.substring(0, firstSpace - 1);
      }

      final int year;
      if (yrStr.length() == 2)
      {
        final int theYear = Integer.parseInt(yrStr);

        // is this from the late 80's onwards?
        if (theYear > 80)
        {
          year = 1900 + theYear;
        }
        else
        {
          year = 2000 + theYear;
        }
      }
      else
      {
        year = Integer.parseInt(yrStr);
      }

      final Date datePart =
          new Date(year - 1900, Integer.parseInt(monStr) - 1, Integer
              .parseInt(dayStr));

      final Date timePart = fourBlock.parse(dtgStr);

      dtg = new HiResDate(new Date(datePart.getTime() + timePart.getTime()));

      // ok, and the message part
      final int ind = entry.indexOf(type);

      text = entry.substring(ind + type.length() + 1).trim();

      // remember what's happening, so we can refer back to previous entries
      lastDtg = new Date(dtg.getDate().getTime());
      lastPlatform = platform;
      lastEntry = this;
    }
    else
    {

      final int firstTab = trimmed.indexOf("\t");
      int blockToUse = 6;
      if (firstTab != -1 && firstTab <= 7)
      {
        blockToUse = firstTab;
      }

      // see if the first few characters are date
      final String dateStr =
          trimmed.substring(0, Math.min(trimmed.length(), blockToUse));

      // is this all numeric
      boolean probIsDate = false;

      try
      {
        if (dateStr.length() == 6 || dateStr.length() == 4)
        {
          @SuppressWarnings("unused")
          final int testInt = Integer.parseInt(dateStr);
          probIsDate = true;
        }
      }
      catch (final NumberFormatException e)
      {
      }

      final boolean probHasContent = entry.length() > 8;

      if (probIsDate && probHasContent)
      {
        // yes, go for it.

        // ooh, do we have some stored data?
        if (lastDtg != null && lastPlatform != null)
        {
          final String parseStr;
          if (dateStr.length() == 6)
          {
            // reduce to four charts
            parseStr = dateStr.substring(2, 6);
          }
          else
          {
            parseStr = dateStr;
          }

          // first try to parse it
          final Date timePart = fourBlock.parse(parseStr);

          // ok, we can go for it
          final Date newDate =
              new Date(lastDtg.getYear(), lastDtg.getMonth(), lastDtg
                  .getDate());

          // ok, we're ready for the DTG
          dtg = new HiResDate(newDate.getTime() + timePart.getTime());

          // stash the platform
          platform = lastPlatform;

          // and catch the rest of the text
          text = trimmed.substring(dateStr.length()).trim();

          // see if we can recognise the first word as a track number
          if (text.length() == 0)
          {
            System.out.println("here");
          }

          final String startOfLine =
              text.substring(0, Math.min(20, text.length() - 1));
          final String trackNum = FCSEntry_Legacy.parseTrack(startOfLine);
          if (trackNum != null)
          {
            type = "FCS";
          }
          else
          {
            // explain we don't know what type of comment this is
            type = "N/A";
          }

          // try to replace soft returns with hard returns
          text = text.replace("\r", "\n");
        }
      }
      else
      {
        // hmm, see if it's just text. If it is, stick it on the end of the previous one

        // ooh, it may be a next day marker. have a check
        final DateFormat dtgBlock = new SimpleDateFormat("dd MMM yy");
        dtgBlock.setTimeZone(TimeZone.getTimeZone("GMT"));

        boolean hasDate = false;
        try
        {
          @SuppressWarnings("unused")
          final Date scrapDate = dtgBlock.parse(trimmed);
          hasDate = true;
        }
        catch (final ParseException e)
        {
          // it's ok, we can silently fail
        }

        if (hasDate)
        {
          // ok. skip it. it's just a date
        }
        else
        {
          // ooh, do we have a previous one?
          if (lastEntry != null)
          {
            text = trimmed;

            // now flag that we've just added ourselves to the previous one
            appendedToPrevious = true;
          }
        }
      }
    }
  }
}