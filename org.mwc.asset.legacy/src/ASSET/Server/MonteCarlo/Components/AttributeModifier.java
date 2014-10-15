/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package ASSET.Server.MonteCarlo.Components;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian.Mayo
 * Date: 22-Sep-2003
 * Time: 15:41:51
 * Log:  
 *  $Log: AttributeModifier.java,v $
 *  Revision 1.1  2006/08/08 14:22:19  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:26:25  Ian.Mayo
 *  First versions
 *
 *  Revision 1.2  2004/05/24 16:21:11  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:56  ian
 *  no message
 *
 *  Revision 1.1  2003/09/22 15:50:33  Ian.Mayo
 *  New implementations
 *
 *
 */
public interface AttributeModifier
{
  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////
  
  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////
  
  /////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////
  public String getNewValue();

  //////////////////////////////////////////////////
  // testing
  //////////////////////////////////////////////////
}
