package MWC.TacticalData;




/** somebody who provides a rolling sequence of narrative entries
 * 
 * @author ian.mayo
 *
 */
public interface IRollingNarrativeProvider
{
	
	
	/** somebody who wants to listen to all entries
	 * 
	 */
	public final String ALL_CATS = "ALL";	
	
	/** add a narrative listener
	 * 
	 * @param categories
	 * @param listener
	 */
	public void addNarrativeListener(String category, INarrativeListener listener);
	
	/** remove a narrative listener
	 * 
	 * @param categories
	 * @param listener
	 */
	public void removeNarrativeListener(String category, INarrativeListener listener);
	
	/** get the narrative history
	 * 
	 * @param categories the categories of narrative we're interested in
	 * @return
	 */
	public NarrativeEntry[] getNarrativeHistory(String[] categories);
	
	/** and somebody who listens out for new narrative entries
	 * 
	 * @author ian.mayo
	 *
	 */
	public static interface INarrativeListener
	{
		/** a new narrative item has been produced
		 * 
		 * @param entry
		 */
		public void newEntry(NarrativeEntry entry);
		
		/** a narrative entry has been removed
		 */
		public void entryRemoved(NarrativeEntry entry);
	}
	
}
