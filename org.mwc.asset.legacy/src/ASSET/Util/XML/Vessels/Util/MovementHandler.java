
package ASSET.Util.XML.Vessels.Util;

public abstract class MovementHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{


  public MovementHandler()
  {
    super("Movement");
  }
  
  /**
	 * 
	 */
	public void elementClosed()
	{
		super.elementClosed();
		
		// hey, now tell the significant other.
		setMovement(new ASSET.Models.Movement.CoreMovement());
	}



	abstract public void setMovement(final ASSET.Models.MovementType movement);

}
