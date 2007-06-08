/**
 * 
 */
package org.mwc.cmap.core.DataTypes.Narrative;

/**
 * @author ian.mayo
 *
 */
public interface NarrativeProvider
{
	/** ok, supply the list of narrative entries
	 * 
	 * @return the narrative
	 */
	public Debrief.Wrappers.NarrativeWrapper getNarrative();
}
