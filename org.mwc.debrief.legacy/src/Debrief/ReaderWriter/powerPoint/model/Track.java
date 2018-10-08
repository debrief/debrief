package Debrief.ReaderWriter.powerPoint.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Objects;

public class Track
{
  private final String name;
  private final int stepsToSkip;
  private final ArrayList<TrackPoint> segments = new ArrayList<>();
  private final Color color;

  public Track(final String name, final String color, final int stepsToSkip_in)
  {
    this(name, makeColor(color), stepsToSkip_in);
  }

  public Track(final String name, final Color color, final int stepsToSkip_in)
  {
    this.name = name;
    this.color = color;
    this.stepsToSkip = stepsToSkip_in;
  }

  public Color getColor()
  {
    return color;
  }

  /**
   * Hexadecimal color in the format %02X%02X%02X lowerCased
   *
   * @return Hexadecimal color in the format %02X%02X%02X
   */
  public String getColorAsString()
  {
    return String.format("%02X%02X%02X", this.color.getRed(), this.color
        .getGreen(), this.color.getBlue()).toLowerCase();
  }

  public String getName()
  {
    return name;
  }

  public ArrayList<TrackPoint> getSegments()
  {
    return segments;
  }

  public int getStepsToSkip()
  {
    return stepsToSkip;
  }

  private static Color makeColor(final String colorStr)
  {
    final String colors = colorStr.substring(colorStr.indexOf("[") + 1, colorStr
        .length() - 1);
    final String[] temp = colors.split(",");
    final int r = Integer.parseInt(temp[0].split("=")[1]);
    final int g = Integer.parseInt(temp[1].split("=")[1]);
    final int b = Integer.parseInt(temp[2].split("=")[1]);
    return new Color(r, g, b);
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((color == null) ? 0 : color.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((segments == null) ? 0 : segments.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Track other = (Track) obj;
    if (!Objects.equals(color, other.color))
    {
      return false;
    }
    if (!Objects.equals(name, other.name))
    {
      return false;
    }
    return Objects.equals(segments, other.segments);
  }
}
