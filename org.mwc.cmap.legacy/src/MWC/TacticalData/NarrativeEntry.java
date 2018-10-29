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
package MWC.TacticalData;

import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import MWC.GUI.ExcludeFromRightClickEdit;
import MWC.GUI.FireExtended;
import MWC.GUI.FireReformatted;
import MWC.GUI.Griddable;
import MWC.GUI.Plottable;
import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

public final class NarrativeEntry implements MWC.GUI.Plottable, Serializable,
    ExcludeFromRightClickEdit, TimeStampedDataItem
{

  public final static class NarrativeEntryInfo extends Griddable
  {
    private PropertyDescriptor[] _cachedProps = null;

    public NarrativeEntryInfo(final NarrativeEntry data, final String theName)
    {
      super(data, theName, data.toString());
    }

    @Override
    public PropertyDescriptor[] getGriddablePropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res =
        {prop("Type", "the type of entry", FORMAT), prop("Source",
            "the source for this entry", FORMAT), prop("Visible",
                "whether to display this narrative entry", FORMAT), prop(
                    "Entry", "the content of this entry", FORMAT)};

        return res;

      }
      catch (final IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }

    @Override
    public NonBeanPropertyDescriptor[] getNonBeanGriddableDescriptors()
    {
      return null;
    }

    @Override
    public final PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        if (_cachedProps == null)
        {
          final PropertyDescriptor[] myRes =
          {prop("Type", "the type of entry", FORMAT), prop("Source",
              "the source for this entry", FORMAT), prop(DTG,
                  "the time this entry was recorded", FORMAT), prop(
                          "Visible", "whether to display this narrative entry",
                          FORMAT), prop("Entry", "the content of this entry",
                              FORMAT)};

          _cachedProps = myRes;
        }

        return _cachedProps;
      }
      catch (final IntrospectionException e)
      {
        e.printStackTrace();
        return super.getPropertyDescriptors();
      }
    }

  }

  // ////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testMe extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public testMe(final String val)
    {
      super(val);
    }

    public final void testMyParams()
    {
      final HiResDate hd = new HiResDate(new Date());
      final NarrativeEntry ne = new NarrativeEntry("aaa", "bbb", hd, "vvvv");
      editableTesterSupport.testParams(ne, this);
    }
  }

  public static final String DTG = "DTG";
  public static final Color DEFAULT_COLOR = new Color(178, 0, 0);
  /**
     *
     */
  private static final long serialVersionUID = 1L;

  public static int nullSafeStringComparator(final String one, final String two)
  {
    if (one == null ^ two == null)
    {
      return (one == null) ? -1 : 1;
    }

    if (one == null && two == null)
    {
      return 0;
    }

    return one.compareToIgnoreCase(two);
  }

  // ///////////////////////////////////////////
  // member variables
  // ///////////////////////////////////////////
  private String _track;
  private HiResDate _DTG;

  private String _entry;

  private String _type;

  private boolean _visible = true;

  String _DTGString = null;

  /**
   * cache the hashcode, it's an expensive operation
   */
  private transient Integer _hashCode = null;

  /**
   * also cache the String representation, it's quite expensive to re-create
   *
   */
  private transient String _cachedString = null;

  private transient NarrativeEntryInfo _myInfo;

  private Color _color = DEFAULT_COLOR;
  

  static public final String NARRATIVE_LAYER = "Narratives";

  /**
   * old constructor - for when narratives didn't include the type attribute
   *
   * @param track
   *          name of the track this applies to
   * @param DTG
   *          when the entry was recorded
   * @param entry
   *          the content of the entry
   */
  public NarrativeEntry(final String track, final HiResDate DTG,
      final String entry)
  {
    this(track, null, DTG, entry);
  }

  /**
   * new constructor - for narrative entries which include the type of entry (typically for SMNT
   * narratives)
   *
   * @param track
   *          name of the track this applies to
   * @param type
   *          what sort of entry this is (or null)
   * @param DTG
   *          when the entry was recorded
   * @param entry
   *          the content of the entry
   */
  public NarrativeEntry(final String track, final String type,
      final HiResDate DTG, final String entry)
  {
    _track = track;
    _DTG = DTG;
    _entry = entry;
    _type = type;
  }

  protected void clearHash()
  {
    _hashCode = null;
  }

  /**
   * member function to meet requirements of comparable interface *
   */
  @Override
  public final int compareTo(final Plottable o)
  {
    final NarrativeEntry other = (NarrativeEntry) o;
    int result = _DTG.compareTo(other._DTG);
    if (result == 0)
    {
      result = compareTo(getTrackName(), other.getTrackName());
    }
    if (result == 0)
    {
      result = compareTo(getType(), other.getType());
    }
    if (result == 0)
    {
      result = compareTo(getEntry(), other.getEntry());
    }
    return result;
  }

  // primarily by name, secondarily by value; null-safe; case-insensitive
  public int compareTo(final String myStr, final String other)
  {
    final int result = nullSafeStringComparator(myStr, other);
    if (result != 0)
    {
      return result;
    }

    return nullSafeStringComparator(myStr, other);
  }

  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final NarrativeEntry other = (NarrativeEntry) obj;

    return Objects.equals(_DTG, other._DTG) && Objects.equals(_entry,
        other._entry) && Objects.equals(_track, other._track) && Objects.equals(
            _type, other._type);
  }

  /**
   * find the data area occupied by this item
   */
  @Override
  public final MWC.GenericData.WorldArea getBounds()
  {
    return null;
  }

  @Override
  public Color getColor()
  {
    return _color;
  }

  @Override
  public final HiResDate getDTG()
  {
    return _DTG;
  }

  public final String getDTGString()
  {
    if (_DTGString == null)
      _DTGString = DebriefFormatDateTime.toStringHiRes(_DTG);

    return _DTGString;
  }

  public final String getEntry()
  {
    return _entry;
  }

  /**
   * get the editor for this item
   *
   * @return the BeanInfo data for this editable object
   */
  @Override
  public final MWC.GUI.Editable.EditorType getInfo()
  {
    if (_myInfo == null)
      _myInfo = new NarrativeEntryInfo(this, this.toString());
    return _myInfo;
  }

  /**
   * get the name of this entry, using the formatted DTG
   */
  @Override
  public final String getName()
  {
    // NOTE: if the name representation uses more than
    // the DTG, we need to clear the _cachedString
    // when that field is modified

    if (_cachedString == null)
    {
      _cachedString = DebriefFormatDateTime.toStringHiRes(_DTG);
    }
    return _cachedString;
  }

  public final String getSource()
  {
    return _track;
  }

  // ///////////////////////////////////////////
  // member methods to meet requirements of Plottable interface
  // ///////////////////////////////////////////

  // ///////////////////////////////////////////
  // accessor methods
  // ///////////////////////////////////////////
  public final String getTrackName()
  {
    return _track;
  }

  public final String getType()
  {
    return _type;
  }

  /**
   * it this item currently visible?
   */
  @Override
  public final boolean getVisible()
  {
    return _visible;
  }

  /**
   * whether there is any edit information for this item this is a convenience function to save
   * creating the EditorType data first
   *
   * @return yes/no
   */
  @Override
  public final boolean hasEditor()
  {
    return true;
  }

  @Override
  public int hashCode()
  {
    if (_hashCode == null)
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_DTG == null) ? 0 : _DTG.hashCode());
      result = prime * result + ((_entry == null) ? 0 : _entry.hashCode());
      result = prime * result + ((_track == null) ? 0 : _track.hashCode());
      result = prime * result + ((_type == null) ? 0 : _type.hashCode());
      _hashCode = result;
    }

    return _hashCode;
  }

  /**
   * paint this object to the specified canvas
   */
  @Override
  public final void paint(final MWC.GUI.CanvasType dest)
  {
  }

  /**
   * how far away are we from this point? or return null if it can't be calculated
   */
  @Override
  public final double rangeFrom(final MWC.GenericData.WorldLocation other)
  {
    return -1;
  }

  public void setColor(final Color color)
  {
    _color = color;
    clearHash();
  }

  @Override
  @FireExtended
  public void setDTG(final HiResDate date)
  {
    _DTG = date;

    // clear the cached string, since it dependds on the DTG
    _cachedString = null;

    // and clear the hash code
    clearHash();
  }

  @FireReformatted
  public void setEntry(final String val)
  {
    _entry = val;

    // and clear the hash code
    clearHash();
  }

  @FireReformatted
  public final void setSource(final String track)
  {
    _track = track;

    // and clear the hash code
    clearHash();
  }

  @FireReformatted
  public void setType(final String type)
  {
    _type = type;

    // and clear the hash code
    clearHash();
  }

  /**
   * set the visibility (although we ignore this)
   */
  @Override
  @FireReformatted
  public final void setVisible(final boolean val)
  {
    _visible = val;
  }

  @Override
  public final String toString()
  {
    return getName();
  }

}