package ASSET;



public interface NetworkScenario
{

	/** the name of this scenario
	 * 
	 * @return
	 */
	 public String getName();

	/** retrieve list of participants
	 * 
	 * @return
	 */
  public Integer[] getListOfParticipants();
  
  /** get the specified participant
   * 
   * @return the subject participant
   */
  public ParticipantType getThisParticipant(int id);

}
