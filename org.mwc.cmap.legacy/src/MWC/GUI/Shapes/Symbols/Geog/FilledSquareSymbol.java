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
package MWC.GUI.Shapes.Symbols.Geog;

import MWC.GUI.Shapes.Symbols.PlainSymbol;

public class FilledSquareSymbol extends SquareSymbol
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FilledSquareSymbol()
	{
		super();

		// and set it to be filled
		super.setFillSymbol(true);
	}
	
	 
  @Override
  public PlainSymbol create()
  {
    return new FilledSquareSymbol();
  }
	
	/**
	 * getType
	 * 
	 * @return the returned String
	 */
	public String getType()
	{
		return "FilledSquare";
	}
	
	
}
