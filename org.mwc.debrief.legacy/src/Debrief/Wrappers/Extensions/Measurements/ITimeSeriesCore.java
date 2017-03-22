package Debrief.Wrappers.Extensions.Measurements;

import java.util.Iterator;

public interface ITimeSeriesCore extends DataItem 
{

  public abstract String getPath();

  public abstract int size();

  public abstract String getUnits();

  public abstract String getName();

  public abstract Iterator<Long> getIndices();

  public abstract DataFolder getParent();

  public abstract void setParent(DataFolder parent);

  public abstract int getIndexNearestTo(long time);

  /** value used to indicate an invalid index
   * 
   */
  public static final int INVALID_INDEX = -1;

}
