package Debrief.ReaderWriter.Word;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FCSEntry_Legacy
{
  static String getClassified(final String input)
  {
    String res = null;

    final String regexp = "Classified (.*$)";
    final Pattern pattern = Pattern.compile(regexp);

    final Matcher matcher = pattern.matcher(input);
    if (matcher.find())
    {
      res = matcher.group(1);
    }

    return res;
  }

  static Double
      getElement(final String identifier, final String input)
  {
    Double res = null;

    final String regexp = identifier + "-*(\\d+\\.?\\d*)";
    final Pattern pattern = Pattern.compile(regexp);

    final Matcher matcher = pattern.matcher(input);
    if (matcher.find())
    {
      final String found = matcher.group(1);
      try
      {
        res = Double.parseDouble(found);
      }
      catch (final NumberFormatException fe)
      {
        // ok, we failed :-(
      }
    }

    return res;
  }

  /**
   * extract the track number from the provided string
   * 
   * @param str
   * @return
   */
  static String parseTrack(final String str)
  {
    final String longTrackId = "[A-Z]{1,4}(\\d{3})";
    final Pattern longPattern = Pattern.compile(longTrackId);

    final Matcher matcher1 = longPattern.matcher(str);
    final String res;
    if (matcher1.find())
    {
      res = matcher1.group(1);
    }
    else
    {
      final String shortTrackId = "(M\\d{2})";
      final Pattern shortPattern = Pattern.compile(shortTrackId);

      final Matcher matcher = shortPattern.matcher(str);
      if (matcher.find())
      {
        res = matcher.group(1);
      }
      else
      {
        res = null;
      }
    }

    return res;
  }

  final double brgDegs;
  final double rangYds;
  final String tgtType;

  final String contact;

  final double crseDegs;

  final double spdKts;

  public FCSEntry_Legacy(final NarrEntry_Legacy thisN, final String msg)
  {
    // pull out the matching strings
    final Double bVal = getElement("B-", msg);
    final Double rVal = getElement("R-", msg);
    final Double cVal = getElement("C-", msg);
    final Double sVal = getElement("S-", msg);

    // extract the classification
    final String classStr = getClassified(msg);

    // try to extract the track id
    final String trackId = parseTrack(msg);

    this.crseDegs = cVal != null ? cVal : 0d;
    this.brgDegs = bVal != null ? bVal : 0d;
    this.rangYds = rVal != null ? rVal * 1000d : 0d;
    this.spdKts = sVal != null ? sVal : 0d;
    this.tgtType = classStr != null ? classStr : "N/A";
    this.contact = trackId != null ? trackId : "N/A";
  }

}