package Debrief.ReaderWriter.powerPoint.model;

public class NarrativeEntry
{
  private final String text;
  private final String date;
  private final String elapsed;

  public NarrativeEntry(String text, String date, String elapsed)
  {
    this.text = text;
    this.date = date;
    this.elapsed= elapsed;
  }

  public String getDate()
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
    result = prime * result + ((date == null) ? 0 : date.hashCode());
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
    NarrativeEntry other = (NarrativeEntry) obj;
    if (date == null)
    {
      if (other.date != null)
        return false;
    }
    else if (!date.equals(other.date))
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
