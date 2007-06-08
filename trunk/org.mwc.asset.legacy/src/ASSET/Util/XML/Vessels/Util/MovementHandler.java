/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 17-Sep-02
 * Time: 15:55:42
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
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
