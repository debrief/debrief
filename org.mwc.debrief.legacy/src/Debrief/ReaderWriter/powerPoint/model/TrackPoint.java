package Debrief.ReaderWriter.powerPoint.model;

import java.util.Date;

public class TrackPoint
{
  private float latitude;
  private float longitude;
  private float elevation;
  private Date time;
  private String formattedTime;

  public float getElevation()
  {
    return elevation;
  }

  public float getLatitude()
  {
    return latitude;
  }

  public float getLongitude()
  {
    return longitude;
  }

  public Date getTime()
  {
    return time;
  }

  public void setElevation(final float elevation)
  {
    this.elevation = elevation;
  }

  public void setLatitude(final float latitude)
  {
    this.latitude = latitude;
  }

  public void setLongitude(final float longitude)
  {
    this.longitude = longitude;
  }

  public void setTime(final Date time)
  {
    this.time = time;
  }
  
  public String getFormattedTime()
  {
    return formattedTime;
  }

  public void setFormattedTime(String formattedTime)
  {
    this.formattedTime = formattedTime;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + Float.floatToIntBits(elevation);
    result = prime * result + ((formattedTime == null) ? 0 : formattedTime
        .hashCode());
    result = prime * result + Float.floatToIntBits(latitude);
    result = prime * result + Float.floatToIntBits(longitude);
    result = prime * result + ((time == null) ? 0 : time.hashCode());
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
    TrackPoint other = (TrackPoint) obj;
    if (Float.floatToIntBits(elevation) != Float.floatToIntBits(
        other.elevation))
      return false;
    if (formattedTime == null)
    {
      if (other.formattedTime != null)
        return false;
    }
    else if (!formattedTime.equals(other.formattedTime))
      return false;
    if (Float.floatToIntBits(latitude) != Float.floatToIntBits(other.latitude))
      return false;
    if (Float.floatToIntBits(longitude) != Float.floatToIntBits(
        other.longitude))
      return false;
    if (time == null)
    {
      if (other.time != null)
        return false;
    }
    else if (!time.equals(other.time))
      return false;
    return true;
  }
  
  
}
