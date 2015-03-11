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
public class AISPositionB implements IAISMessage, IAISDecodable, Cloneable {


	/** message id */
	protected int msgId;

	protected int mmsi;

	/**
	 * speed over ground in knots range 0-102.2 where 102.2knots denotes
	 * 102.2knots and more
	 */
	protected double sog;

	/** longitude in degrees +-180° east=positiv west=negative */
	protected double longitude;

	/** latitude in degrees +-90° north=positiv south=negative */
	protected double latitude;

	/** course over ground in knots */
	protected double cog;

	/** degrees 0-359 or 511 for n/a */
	protected int trueHeading;

	/** message timestamp */
	protected Timestamp msgTimestamp;

	public AISPositionB() {

	}

	/**
	 * This is the default constructor of class AISPosition.
	 *
	 * @param msgId
	 * @param mmsi
	 * @param sog
	 * @param longitude
	 * @param latitude
	 * @param cog
	 * @param trueHeading
	 * @param msgTimestamp
	 */
	public AISPositionB(int msgId, int mmsi, double sog, double longitude,
			double latitude, double cog, int trueHeading, Timestamp msgTimestamp) {
		this.msgId = msgId;
		this.mmsi = mmsi;
		this.sog = sog;
		this.longitude = longitude;
		this.latitude = latitude;
		this.cog = cog;
		this.trueHeading = trueHeading;
		this.msgTimestamp = msgTimestamp;
	}

	public double getCog() {
		return cog;
	}

	public int getMsgId() {
		return msgId;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public Timestamp getMsgTimestamp() {
		return msgTimestamp;
	}

	public double getSog() {
		return sog;
	}

	public int getTrueHeading() {
		return trueHeading;
	}

	public int getMmsi() {
		return mmsi;
	}

	@Override
	public String toString() {
		StringBuffer sbuffer = new StringBuffer();
		sbuffer.append("AISPositionB\n");
		sbuffer.append("MSGID\t\t" + this.msgId + "\n");
		sbuffer.append("MMSI\t\t" + this.mmsi + "\n");
		sbuffer.append("SOG\t\t" + this.sog + "\n");
		sbuffer.append("LONGITUTDE\t" + this.longitude + "\n");
		sbuffer.append("LATITUDE\t" + this.latitude + "\n");
		sbuffer.append("COG\t\t" + this.cog + "\n");
		sbuffer.append("HEADING\t\t" + this.trueHeading + "\n");
		sbuffer.append("MSGTIMESTAMP\t" + this.msgTimestamp + "\n");
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

		AISPositionB that = (AISPositionB) o;
		boolean same = this.cog == that.cog && this.msgId == that.msgId
				&& this.latitude == that.latitude
				&& this.longitude == that.longitude
				&& this.msgTimestamp.equals(that.msgTimestamp)
				&& this.sog == that.sog && this.trueHeading == that.trueHeading
				&& this.mmsi == that.mmsi;
		return same;
	}

	@Override
	public AISPositionB clone() {
		try {
			return (AISPositionB) super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	/**
	 * Parse decoded bytes for the position data
	 *
	 * Message 18: Standard Class B equipment position report Usually will be
	 * sent by small vessels
	 *
	 * ITU-R M.1371-1
	 *
	 * @param decBytes
	 * @throws AISParseException
	 */
	public IAISMessage decode(String decBytes) throws AISParseException {

		if (decBytes.length() < 133)
			throw new AISParseException(
					AISParseException.NOT_CONSISTENT_DECODED_STRING);
		/* Possition Reports Message ID 18 bits 0-5 */
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

		/* time stamp bits 138-143 */
		// TODO: impelemt the rest bits
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		this.msgTimestamp = new Timestamp(cal.getTimeInMillis());

		return this;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(cog);
		result = prime * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ temp >>> 32);
		result = prime * result + mmsi;
		result = prime * result + msgId;
		result = prime * result + (msgTimestamp == null ? 0 : msgTimestamp.hashCode());
		temp = Double.doubleToLongBits(sog);
		result = prime * result + (int) (temp ^ temp >>> 32);
		result = prime * result + trueHeading;
		return result;
	}
}
