/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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
