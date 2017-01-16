package org.mwc.cmap.NarrativeViewer2;

import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.Color;

public class NATNarrativeEntry implements INatEntry {

	/**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private final String date;
	private final String time;
	private final String name;
	private final String type;
	private final String log;
	
	public NATNarrativeEntry(String date, String time, String name, String type, String log) {
		this.date = date;
		this.time = time;
		this.name = name;
		this.type = type;
		this.log = log;
	}

	/* (non-Javadoc)
   * @see org.mwc.cmap.NarrativeViewer2.INatEntry#getDate()
   */
	@Override
  public String getDate() {
		return date;
	}

	/* (non-Javadoc)
   * @see org.mwc.cmap.NarrativeViewer2.INatEntry#getTime()
   */
	@Override
  public String getTime() {
		return time;
	}

	/* (non-Javadoc)
   * @see org.mwc.cmap.NarrativeViewer2.INatEntry#getName()
   */
	@Override
  public String getName() {
		return name;
	}

	/* (non-Javadoc)
   * @see org.mwc.cmap.NarrativeViewer2.INatEntry#getType()
   */
	@Override
  public String getType() {
		return type;
	}

	/* (non-Javadoc)
   * @see org.mwc.cmap.NarrativeViewer2.INatEntry#getLog()
   */
	@Override
  public String getLog() {
		return log;
	}
	
	/* (non-Javadoc)
   * @see org.mwc.cmap.NarrativeViewer2.INatEntry#toString()
   */

	public String toString() {
		return "NarrativeEntry [date=" + date + ", time=" + time + ", name=" + name + ", type=" + type + ", log=" + log
				+ "]";
	}

  @Override
  public Color getColor()
  {
    if (this.name.equalsIgnoreCase("NELSON"))
    {
      return GUIHelper.COLOR_RED;
    }
    else
    {
      return GUIHelper.COLOR_BLUE;
    }
  }
}
