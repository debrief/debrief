package ASSET;

public interface NetworkScenario
{

	/** the name of this scenario
	 * 
	 * @return
	 */
	public String getName();
	
	/** the list of participants in this scenario
	 * 
	 * @return
	 */
	public Integer[] getListOfParticipants();
}
