
package ASSET.Models.Vessels;



public class Buoy extends SSN
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Buoy(final int id)
  {
    super(id);
  }

  public Buoy(final int id, final ASSET.Participants.Status status, final ASSET.Participants.DemandedStatus demStatus, final String name)
  {
    super(id, status, demStatus, name);
  }

  public void initialise()
  {
    if(getStatus() != null)
      this.getStatus().setFuelLevel(100);
  }
}
