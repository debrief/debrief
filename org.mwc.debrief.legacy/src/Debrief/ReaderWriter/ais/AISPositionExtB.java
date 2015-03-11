/******************************************************************************
 * 	Freeais.org
 * 	http://www.freeais.org		info@freeais.org
 *
 *  Copyright (c) 2007
 *
 * 		ynnor systems GmbH
 * 		Mundsburger Damm 45
 * 		22087 Hamburg
 * 		Germany
 *
 * 		Alexander Lotter	lotter@ynnor.de
 * 		David Schmitz		schmitz@ynnor.de
 *
 *	This file is part of Freeais.org.
 *
 *  Freeais.org is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Freeais.org is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package Debrief.ReaderWriter.ais;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;


/**
 * This class represents an AIS position report
 *
 * @author David Schmitz
 * @author Alexander Lotter
 *
 */
public class AISPositionExtB extends AISPositionB {


	/** name of the vessel */
	private String name;

	/** type of vessel */
	private int shipType;

	/** a first part of length */
	private int dimensionA;

	/** a second part of length */
	private int dimensionB;

	/** a first part of width */
	private int dimensionC;

	/** a second part of width */
	private int dimensionD;

	/** countryId from mmsi */
	private int countryId;

	/** message source */
	private int msgSrc;

	public AISPositionExtB() {

	}

	/**
	 * This is default constructor of class AISPositionExtB.
	 *
	 * @param msgId
	 * @param mmsi
	 * @param sog
	 * @param longitude
	 * @param latitude
	 * @param cog
	 * @param trueHeading
	 * @param name
	 * @param shipType
	 * @param dimensionA
	 * @param dimensionB
	 * @param dimensionC
	 * @param dimensionD
	 * @param countryId
	 * @param msgSrc
	 * @param msgTimestamp
	 */
	public AISPositionExtB(int msgId, int mmsi, double sog, double longitude,
			double latitude, double cog, int trueHeading, String name,
			int shipType, int dimensionA, int dimensionB, int dimensionC,
			int dimensionD, int countryId, int msgSrc, Timestamp msgTimestamp) {
		super(msgId, mmsi, sog, longitude, latitude, cog, trueHeading,
				msgTimestamp);
		this.name = name;
		this.shipType = shipType;
		this.dimensionA = dimensionA;
		this.dimensionB = dimensionB;
		this.dimensionC = dimensionC;
		this.dimensionD = dimensionD;
		this.countryId = countryId;
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public int getDimensionA() {
		return dimensionA;
	}

	public void setDimensionA(int dimensionA) {
		this.dimensionA = dimensionA;
	}

	public int getDimensionB() {
		return dimensionB;
	}

	public void setDimensionB(int dimensionB) {
		this.dimensionB = dimensionB;
	}

	public int getDimensionC() {
		return dimensionC;
	}

	public void setDimensionC(int dimensionC) {
		this.dimensionC = dimensionC;
	}

	public int getDimensionD() {
		return dimensionD;
	}

	public void setDimensionD(int dimensionD) {
		this.dimensionD = dimensionD;
	}

	public int getMsgSrc() {
		return msgSrc;
	}

	public void setMsgSrc(int msgSrc) {
		this.msgSrc = msgSrc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuffer sbuffer = new StringBuffer();
		sbuffer.append("AISPositionExtB\n");
		sbuffer.append(super.toString());
		sbuffer.append("AISPositionExtB\n");
		sbuffer.append("NAME\t\t" + this.name + "\n");
		sbuffer.append("SHIPTYPE\t" + this.shipType + "\n");
		sbuffer.append("DIMA\t\t" + this.dimensionA + "\n");
		sbuffer.append("DIMB\t\t" + this.dimensionB + "\n");
		sbuffer.append("DIMC\t\t" + this.dimensionC + "\n");
		sbuffer.append("DIMD\t\t" + this.dimensionD + "\n");
		sbuffer.append("COUNTRYID\t" + this.countryId + "\n");
		sbuffer.append("MSGSRC\t\t" + this.msgSrc + "\n");
		return sbuffer.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (!o.getClass().equals(o.getClass()))
			return false;

		AISPositionExtB that = (AISPositionExtB) o;

		boolean same = super.equals(that) && this.shipType == that.shipType
				&& this.dimensionA == that.dimensionA
				&& this.dimensionB == that.dimensionB
				&& this.dimensionC == that.dimensionC
				&& this.dimensionD == that.dimensionD
				&& this.countryId == that.countryId;

		return same;
	}

	@Override
	public AISPositionExtB clone() {
		return (AISPositionExtB) super.clone();

	}

	/**
	 * Parse decoded bytes for the static data
	 *
	 * Message 19: Extended Class B equipment position reposrt
	 *
	 * ITU-R M.1371-1
	 *
	 * @param decBytes decoded bytes
	 * @throws AISParseException
	 */
	@Override
	public IAISMessage decode(String decBytes) throws AISParseException {

		if (decBytes.length() < 301)
			throw new AISParseException(
					AISParseException.NOT_CONSISTENT_DECODED_STRING);
		/* Possition Reports Message ID 19 bits 0-5 */
		this.msgId = AISDecoder.getDecValueByBinStr(decBytes.substring(0, 6),
				false);

		/* User ID mmsi bits 8-37 */
		this.mmsi = AISDecoder.getDecValueByBinStr(decBytes.substring(8, 38),
				false);

		/* Speed over ground bits 46-55 - 10 bits */
		this.sog = AISDecoder.getDecValueByBinStr(decBytes.substring(46, 56),
				false) / 10.0;

		/* Longitude bits 57-84 */
		int longitudeHour = AISDecoder.getDecValueByBinStr(
				decBytes.substring(57, 85), true);
		this.longitude = longitudeHour / 600000.0;

		if (this.longitude > 180.0 || this.longitude < -180.0)
			throw new AISParseException(
					AISParseException.LONGITUDE_OUT_OF_RANGE + " " + longitude);

		/* Latitude bits 85-111 */
		int latitudeHour = AISDecoder.getDecValueByBinStr(
				decBytes.substring(85, 112), true);
		this.latitude = latitudeHour / 600000.0;

		if (this.latitude > 90.0 || this.latitude < -90.0)
			throw new AISParseException(AISParseException.LATITUDE_OUT_OF_RANGE
					+ " " + latitude);

		/* COG bits 112-123 */
		this.cog = AISDecoder.getDecValueByBinStr(decBytes.substring(112, 124),
				false) / 10.0;

		/* true heading bits 124-132 */
		this.trueHeading = AISDecoder.getDecValueByBinStr(
				decBytes.substring(124, 133), false);

		/* Name bits 143-262 - 120 bits */
		this.name = AISDecoder.getDecStringFrom6BitStr(decBytes.substring(143,
				263));

		// Type of ship and cargo type bits 263-270 - 8 bits

		this.shipType = AISDecoder.getDecValueByBinStr(
				decBytes.substring(263, 271), false);

		String mmsiStr = Integer.toString(this.mmsi);
		if (!(mmsiStr.length() < 3)) {
			this.countryId = Integer.valueOf(mmsiStr.substring(0, 3));
		} else {
			this.countryId = 0;
		}

		/* Dimension/reference for position bits 271-300 - 30 bits */
		this.dimensionA = AISDecoder.getDecValueByBinStr(
				decBytes.substring(271, 280), false);
		this.dimensionB = AISDecoder.getDecValueByBinStr(
				decBytes.substring(280, 289), false);
		this.dimensionC = AISDecoder.getDecValueByBinStr(
				decBytes.substring(289, 295), false);
		this.dimensionD = AISDecoder.getDecValueByBinStr(
				decBytes.substring(295, 301), false);

//		int length = dimensionA + dimensionB;
//		int width = dimensionC + dimensionD;

		// TODO: impelemt the rest bits
		// TODO: implement vesselType, cargoType, countryId, msgSrc

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		this.msgTimestamp = new Timestamp(cal.getTimeInMillis());

		return this;
	}

}
