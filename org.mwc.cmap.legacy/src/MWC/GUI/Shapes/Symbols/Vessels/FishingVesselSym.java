/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
// $RCSfile: FishingVesselSym.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: FishingVesselSym.java,v $
// Revision 1.2  2004/05/25 15:37:59  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:36  Ian.Mayo
// Initial import
//
// Revision 1.3  2003-02-07 09:49:23+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.2  2002-05-28 09:25:56+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:18+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:00:56+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:09+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-16 19:29:48+00  novatech
// Initial revision
//

package MWC.GUI.Shapes.Symbols.Vessels;

import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.WorldLocation;

public class FishingVesselSym extends ScreenScaledSym
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void paint(final CanvasType dest, final WorldLocation theLocation, final double direction)
	{
		// override the scale values
		super.setScaleVal(1);
		super.paint(dest, theLocation, direction);
	}

  @Override
  public PlainSymbol create()
  {
    return new FishingVesselSym();
  }
  
	protected Vector<double[][]> getCoords()
	{
		final Vector<double[][]> hullLines = new Vector<double[][]>();

		hullLines.add(new double[][]
		{
		{ -9, -4 },
		{ 9, -4 },
		{ 8.9, -1.45 },
		{ 7.91, 0.3 },
		{ 6.74, 1.1 },
		{ 4.08, 3.41 },
		{ 0, 4.02 },
		{ -4.08, 3.41 },
		{-6.74, 1.1 },
		{ -7.91, 0.3 },
		{ -8.9, -1.45 },
		{ -9, -4 } });

		return hullLines;

	}

	public String getType()
	{
		return "Fishing_Vessel";
	}

}
