package Debrief.ReaderWriter.powerPoint.model;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Track data to be added to the pptx file.
 */
public class TrackData
{
  private int height;
  private int width;
  private int intervals;
  private String name;
  private int scaleWidth;
  private final ArrayList<ExportNarrativeEntry> narrativeEntries =
      new ArrayList<>();
  private final ArrayList<Track> tracks = new ArrayList<>();

  public int getHeight()
  {
    return height;
  }

  public int getIntervals()
  {
    return intervals;
  }

  public String getName()
  {
    return name;
  }

  public ArrayList<ExportNarrativeEntry> getNarrativeEntries()
  {
    return narrativeEntries;
  }

  public ArrayList<Track> getTracks()
  {
    return tracks;
  }

  public int getWidth()
  {
    return width;
  }

  public void setHeight(final int height)
  {
    this.height = height;
  }

  public void setIntervals(final int intervals)
  {
    this.intervals = intervals;
  }

  public void setName(final String name)
  {
    this.name = name;
  }

  public void setWidth(final int width)
  {
    this.width = width;
  }

  public int getScaleWidth()
  {
    return scaleWidth;
  }

  public void setScaleWidth(int scaleWidth)
  {
    this.scaleWidth = scaleWidth;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + height;
    result = prime * result + intervals;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((narrativeEntries == null) ? 0 : narrativeEntries
        .hashCode());
    result = prime * result + ((tracks == null) ? 0 : tracks.hashCode());
    result = prime * result + width;
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    TrackData other = (TrackData) obj;
    return (classComparison(obj) && basicFieldComparison(other)
        && listFieldComparison(other));
  }

  private boolean listFieldComparison(TrackData other)
  {
    if ((narrativeEntries == null && other.narrativeEntries != null)
        || !narrativeEntries.equals(other.narrativeEntries))
    {
      return false;
    }
    return Objects.equals(tracks,  other.tracks);
  }

  private boolean basicFieldComparison(TrackData other)
  {
    return ! (height != other.height || intervals != other.intervals
        || width != other.width || (name == null && other.name != null) || !name
            .equals(other.name));
  }

  private boolean classComparison(Object obj)
  {
    return (obj != null && getClass() == obj.getClass());
  }
}
