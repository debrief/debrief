package Debrief.ReaderWriter.powerPoint.model;

import java.util.Date;

public class ExportNarrativeEntry
{
  private final String text;
  private final String dateString;
  private final Date date;
  private final String elapsed;

  public ExportNarrativeEntry(String text, String dateString, String elapsed, Date date)
  {
    this.text = text;
    this.dateString = dateString;
    this.elapsed= elapsed;
    this.date = date;
  }

  public String getDateString()
  {
    return dateString;
  }

  public Date getDate()
  {
    return date;
  }

  public String getElapsed()
  {
    return elapsed;
  }

  public String getText()
  {
    return text;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dateString == null) ? 0 : dateString.hashCode());
    result = prime * result + ((elapsed == null) ? 0 : elapsed.hashCode());
    result = prime * result + ((text == null) ? 0 : text.hashCode());
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
    ExportNarrativeEntry other = (ExportNarrativeEntry) obj;
    if (dateString == null)
    {
      if (other.dateString != null)
        return false;
    }
    else if (!dateString.equals(other.dateString))
      return false;
    if (elapsed == null)
    {
      if (other.elapsed != null)
        return false;
    }
    else if (!elapsed.equals(other.elapsed))
      return false;
    if (text == null)
    {
      if (other.text != null)
        return false;
    }
    else if (!text.equals(other.text))
      return false;
    return true;
  }
  
  
}
