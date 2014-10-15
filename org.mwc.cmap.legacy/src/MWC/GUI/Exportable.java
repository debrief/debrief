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
// $RCSfile: Exportable.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: Exportable.java,v $
// Revision 1.2  2004/05/25 15:45:31  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:03  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:36+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:12+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:26+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:35+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:43:06+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:47  ianmayo
// initial version
//
// Revision 1.1  2000-09-26 10:50:24+01  ian_mayo
// Initial revision
//

package MWC.GUI;

//
public interface Exportable
{
	/**  export this item (normally to the clipboard)
	 */
	public void exportThis();
}
