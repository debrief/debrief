package Debrief.ReaderWriter.powerPoint.model;

import java.time.LocalDateTime;

public class TrackPoint
{
  private float latitude;
  private float longitude;
  private float elevation;
  private LocalDateTime time;
  private float course;
  private float speed;

  public TrackPoint()
  {
  }

  public float getCourse()
  {
    return course;
  }

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

  public float getSpeed()
  {
    return speed;
  }

  public LocalDateTime getTime()
  {
    return time;
  }

  public void setCourse(final float course)
  {
    this.course = course;
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

  public void setSpeed(final float speed)
  {
    this.speed = speed;
  }

  public void setTime(final LocalDateTime time)
  {
    this.time = time;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + Float.floatToIntBits(course);
    result = prime * result + Float.floatToIntBits(elevation);
    result = prime * result + Float.floatToIntBits(latitude);
    result = prime * result + Float.floatToIntBits(longitude);
    result = prime * result + Float.floatToIntBits(speed);
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
    if (Float.floatToIntBits(course) != Float.floatToIntBits(other.course))
      return false;
    if (Float.floatToIntBits(elevation) != Float.floatToIntBits(
        other.elevation))
      return false;
    if (Float.floatToIntBits(latitude) != Float.floatToIntBits(other.latitude))
      return false;
    if (Float.floatToIntBits(longitude) != Float.floatToIntBits(
        other.longitude))
      return false;
    if (Float.floatToIntBits(speed) != Float.floatToIntBits(other.speed))
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
