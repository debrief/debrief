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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.eclipse.nebula.widgets.formattedtext;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Text;

/**
 * @author Akash-Gupta GeoPointFormatter is formats the text to degree minutes
 *         and seconds format but internally handles a Double format.
 */
public class GeoPointFormatter extends AbstractFormatter
{

	public static final int LAT = 1;
	public static final int LON = 2;
	Double geoPoint;
	private int geoPointType;

	// private KeyListener klistener;
	// private FocusListener focusListener;

	public GeoPointFormatter()
	{
		ignore = true;
	}

	public GeoPointFormatter(int geoPointType)
	{
		ignore = true;
		this.geoPointType = geoPointType;

		/*
		 * focusListener = new FocusListener() {
		 * 
		 * @Override public void focusGained(FocusEvent e) {
		 * 
		 * }
		 * 
		 * @Override public void focusLost(FocusEvent e) {
		 * 
		 * }
		 * 
		 * };
		 * 
		 * klistener = new KeyListener() { int position;
		 * 
		 * @Override public void keyPressed(KeyEvent e) { position =
		 * text.getCaretPosition(); }
		 * 
		 * @Override public void keyReleased(KeyEvent e) { switch (e.keyCode) { case
		 * SWT.ARROW_UP: return; case SWT.ARROW_DOWN: return; case SWT.ARROW_LEFT:
		 * return; case SWT.ARROW_RIGHT: return; case SWT.HOME: return; case
		 * SWT.END: return;
		 * 
		 * default:
		 * 
		 * } text.setSelection(position + 1); } };
		 */
	}

	@Override
	public String getDisplayString()
	{
		return text.getText();
	}

	@Override
	public String getEditString()
	{
		if (geoPoint instanceof Double)
			if (LAT == geoPointType)
				return decimalToDMS(Math.abs(geoPoint)) + (geoPoint < 0 ? "S" : "N");
			else
				return decimalToDMS(Math.abs(geoPoint)) + (geoPoint < 0 ? "W" : "E");

		else
			return "";
	}

	public static String decimalToDMS(double coord)
	{
		String output, degrees, minutes, seconds;

		double mod = coord % 1;
		int intPart = (int) coord;

		degrees = String.valueOf((int) intPart);

		coord = mod * 60;
		mod = coord % 1;
		intPart = (int) coord;

		minutes = String.valueOf((int) intPart);

		coord = mod * 60;
		intPart = (int) coord;

		seconds = String.valueOf(Math.round(coord * 100.0) / 100.0);

		output = degrees + "\u00B0 " + minutes + "' " + seconds + "\" ";
		return output;
	}

	@Override
	public Object getValue()
	{
		this.geoPoint = parseDMSString(text.getText());
		text.setText(getEditString());
		return geoPoint;
	}

	@Override
	public Class<Double> getValueType()
	{
		return Double.class;
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}

	@Override
	public boolean isValid()
	{
		return true;
	}

	@Override
	public void setValue(Object value)
	{
		this.geoPoint = (Double) value;
		text.setText(getEditString());
	}

	@Override
	public void verifyText(VerifyEvent arg0)
	{

	}

	private Double parseDMSString(String lat)
	{
		if (lat.indexOf("\u00B0 ") == -1 || lat.indexOf("\u00B0 ") == -1
				|| lat.indexOf("' ") == -1)
		{
			return geoPoint;
		}

		try
		{

			double deg = Double.parseDouble(lat.substring(0, lat.indexOf("\u00B0 ")));
			double min = Double.parseDouble(lat.substring(lat.indexOf("\u00B0 ") + 1,
					lat.indexOf("' ")));
			double sec = Double.parseDouble(lat.substring(lat.indexOf("' ") + 1,
					lat.indexOf("\" ")));

			double _geoPoint = dmsToDecimal(deg, min, sec);

			if (lat.indexOf("S") > 0)
			{
				_geoPoint *= -1;
			}

			if (lat.indexOf("W") > 0)
			{
				_geoPoint *= -1;
			}
			return _geoPoint;
		}
		catch (Exception e)
		{
			return this.geoPoint;

		}
	}

	public static double dmsToDecimal(double degree, double minutes,
			double seconds)
	{
		return degree + ((seconds / 60) + minutes) / 60;
	}

	@Override
	public void setText(Text text)
	{
		super.setText(text);
		// text.addKeyListener(klistener);
		// text.addFocusListener(focusListener);
	}

}
