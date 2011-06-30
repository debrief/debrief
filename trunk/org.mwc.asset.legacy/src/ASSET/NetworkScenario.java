package ASSET;

public class NetworkScenario
{

	public NetworkScenario(){};
	
	public NetworkScenario(ScenarioType scen)
	{
		name = scen.getName();
	}

	/** the name of this scenario
	 * 
	 * @return
	 */
	 public String name;

	/** retrieve list of participants
	 * 
	 * @return
	 */
//  public Integer[] getListOfParticipants();
  
  /** get the specified participant
   * 
   * @return the subject participant
   */
//  public NetworkParticipant getThisParticipant(int id);

}
