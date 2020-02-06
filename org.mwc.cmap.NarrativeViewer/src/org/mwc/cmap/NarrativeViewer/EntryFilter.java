
package org.mwc.cmap.NarrativeViewer;

import MWC.TacticalData.NarrativeEntry;

public interface EntryFilter {

  /** is this entry suitable?
   * 
   * @param entry
   * @return
   */
	public boolean accept(final NarrativeEntry entry);

}
