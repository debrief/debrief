// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: DestroyerSym.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: DestroyerSym.java,v $
// Revision 1.2  2004/05/25 15:37:57  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:36  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:24+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:25:56+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:17+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:00:52+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:08+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-16 19:29:47+00  novatech
// Initial revision
//

package MWC.GUI.Shapes.Symbols.Vessels;

import java.util.Vector;

public class DestroyerSym extends ScreenScaledSym
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public java.awt.Dimension getBounds()
	{
		// sort out the size of the symbol at the current scale factor
		java.awt.Dimension res = new java.awt.Dimension(
				(int) (2 * 4 * getScaleVal()), (int) (2 * 4 * getScaleVal()));
		return res;
	}

	protected Vector<double[][]> getCoords()
	{
		Vector<double[][]> hullLines = new Vector<double[][]>();

		// start off with the top
		hullLines.add(new double[][]
		{
		{ 5.8, -1 },
		{ 0, -9 },
		{ -5.8, -1 } });

		// now the bottom
		hullLines.add(new double[][]
		{
		{ 3.6, -4 },
		{ 3.6, 6 },
		{ 0, 9 },
		{ -3.6, 6 },
		{ -3.6, -4 } });

		return hullLines;

	}

	public String getType()
	{
		return "Destroyer";
	}

}
