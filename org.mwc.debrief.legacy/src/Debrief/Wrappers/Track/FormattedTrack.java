package Debrief.Wrappers.Track;

import java.awt.Color;
import java.awt.Font;
import java.util.Collection;
import java.util.Enumeration;

import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;

/** we've got two types of Track element - lightweight tracks, and full tracks.
 * This interface wraps methods that are common to both, as used in rendering
 * @author ian
 *
 */
public interface FormattedTrack
{
  Color getColor();
  int getLineStyle();
  boolean getNameVisible();
  Font getTrackFont();
  Enumeration<Editable> getPositionIterator();
  Collection<Editable> getUnfilteredItems(HiResDate start,
      HiResDate end);
  TimePeriod getVisiblePeriod();
}
