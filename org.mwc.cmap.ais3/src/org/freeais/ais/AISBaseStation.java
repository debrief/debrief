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

package org.freeais.ais;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class represents an AIS base station report
 *
 * @author David Schmitz
 * @author Alexander Lotter
 *
 */
public class AISBaseStation implements IAISMessage, IAISDecodable, Cloneable {

	private static Logger logger = LogManager.getLogger(AISDecoder.class);

	private int msgId;

	private int repeatIndicator;

	private int mmsi;

	/** message timestamp in UTC */
	private Timestamp msgTimestamp;

	/** longitude in degrees +-180° east=positive west=negative */
	private double longitude;

	/** latitude in degrees +-90° north=positive south=negative */
	private double latitude;

	private int deviceType;

	public AISBaseStation() {

	}

	/**
	 * This is the default constructor of class AISBaseStationReport.
	 *
	 * @param msgId
	 * @param repeatIndicator
	 * @param mmsi
	 * @param msgTimestamp
	 * @param longitude
	 * @param latitude
	 * @param deviceType
	 */
	public AISBaseStation(int msgId, int repeatIndicator, int mmsi,
			Timestamp msgTimestamp, double longitude, double latitude,
			int deviceType) {
		this.msgId = msgId;
		this.repeatIndicator = repeatIndicator;
		this.mmsi = mmsi;
		this.msgTimestamp = msgTimestamp;
		this.longitude = longitude;
		this.latitude = latitude;
		this.deviceType = deviceType;
	}

	/**
	 * Simple getter for MSG ID
	 *
	 * @return msgid
	 */
	public int getMsgId() {
		return msgId;
	}

	/**
	 * Simple getter for repeat indicator
	 *
	 * @return repeatIndicator
	 */
	public int getRepeatIndicator() {
		return repeatIndicator;
	}

	public int getMmsi() {
		return mmsi;
	}

	public Timestamp getTimestamp() {
		return msgTimestamp;
	}

	/**
	 * Simple getter for latitude.
	 *
	 * @return latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Simple getter for longitude.
	 *
	 * @return longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Simple getter for device type.
	 *
	 * @return deviceType
	 */
	public int getDeviceType() {
		return deviceType;
	}

	@Override
	public String toString() {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("AISBaseStation\n");
		sBuffer.append("MSGID\t\t" + this.msgId + "\n");
		sBuffer.append("REPEAT IND\t" + this.repeatIndicator + "\n");
		sBuffer.append("MMSI\t\t" + this.mmsi + "\n");
		sBuffer.append("UTC TIMESTAMP\t" + this.msgTimestamp + "\n");
		sBuffer.append("LONGITUDE\t" + this.longitude + "\n");
		sBuffer.append("LATITUDE\t" + this.latitude + "\n");
		sBuffer.append("DEVICE\t\t" + this.deviceType + "\n");
		return sBuffer.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (!o.getClass().equals(o.getClass()))
			return false;

		AISBaseStation that = (AISBaseStation) o;
		boolean same = this.msgId == that.msgId
				&& this.repeatIndicator == that.repeatIndicator
				&& this.mmsi == that.mmsi
				&& this.msgTimestamp.equals(that.msgTimestamp)
				&& this.latitude == that.latitude
				&& this.longitude == that.longitude
				&& this.deviceType == that.deviceType;
		return same;
	}

	@Override
	public AISBaseStation clone() {
		try {
			return (AISBaseStation) super.clone();
		}

		catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	/**
	 * Parse decoded bytes for the base station data
	 *
	 * Message 4, base station report, ITU-R M.1371-1
	 *
	 * @param decBytes
	 * @throws AISParseException
	 */
	public IAISMessage decode(String decBytes) throws AISParseException {

		if (decBytes.length() < 168)
			throw new AISParseException(
					AISParseException.NOT_CONSISTENT_DECODED_STRING);

		/* Base Station Report message ID, bits 0-5 */
		this.msgId = AISDecoder.getDecValueByBinStr(decBytes.substring(0, 6),
				false);
		logger.debug("messageId = " + msgId);

		/* repeat indicator, bits 6-7 */
		this.repeatIndicator = AISDecoder.getDecValueByBinStr(
				decBytes.substring(6, 8), false);
		logger.debug("repeat ind = " + repeatIndicator);

		/* mmsi, bits 8-37 */
		this.mmsi = AISDecoder.getDecValueByBinStr(decBytes.substring(8, 38),
				false);
		logger.debug("mmsi = " + mmsi);

		/* year, bits 38-51 */
		int year = AISDecoder.getDecValueByBinStr(decBytes.substring(38, 52),
				false);
		logger.debug("year = " + year);

		/* month, bits 52-55 */
		int month = AISDecoder.getDecValueByBinStr(decBytes.substring(52, 56),
				false);
		logger.debug("month = " + month);

		/* day, bits 56-60 */
		int day = AISDecoder.getDecValueByBinStr(decBytes.substring(56, 61),
				false);
		logger.debug("day = " + day);

		/* hour, bits 61-65 */
		int hour = AISDecoder.getDecValueByBinStr(decBytes.substring(61, 66),
				false);
		logger.debug("hour = " + hour);

		/* minute, bits 66-71 */
		int minute = AISDecoder.getDecValueByBinStr(decBytes.substring(66, 72),
				false);
		logger.debug("minute = " + minute);

		/* second, bits 72-77 */
		int second = AISDecoder.getDecValueByBinStr(decBytes.substring(72, 78),
				false);
		logger.debug("second = " + second);

		Calendar cal = Calendar.getInstance();// TimeZone.getTimeZone("UTC"));
		cal.clear();
		cal.set(year, month - 1, day, hour, minute, second);
		this.msgTimestamp = new Timestamp(cal.getTimeInMillis());
		logger.debug("UTC timestamp = " + msgTimestamp);

		// bit 78 - position accuracy won't be read.

		/* longitude, bits 79-106 */
		int longitudeHour = AISDecoder.getDecValueByBinStr(
				decBytes.substring(79, 107), true);
		logger.debug("longitudeHour = " + longitudeHour);

		this.longitude = longitudeHour / 600000.0;
		logger.debug("longitude = " + longitude);

		if (this.longitude > 180.0 || this.longitude < -180.0)
			throw new AISParseException(
					AISParseException.LONGITUDE_OUT_OF_RANGE + " " + longitude);

		/* latitude, bits 107-133 */
		int latitudeHour = AISDecoder.getDecValueByBinStr(
				decBytes.substring(107, 134), true);
		this.latitude = latitudeHour / 600000.0;
		logger.debug("latitude = " + latitude);

		if (this.latitude > 90.0 || this.latitude < -90.0)
			throw new AISParseException(AISParseException.LATITUDE_OUT_OF_RANGE
					+ " " + latitude);

		/* device type, bits 134-137 */
		this.deviceType = AISDecoder.getDecValueByBinStr(
				decBytes.substring(134, 138), false);
		logger.debug("deviceType = " + deviceType);

		// TODO: implement the rest bits
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ temp >>> 32);
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ temp >>> 32);
		result = prime * result + mmsi;
		result = prime * result + msgId;
		result = prime * result + repeatIndicator;
		result = prime * result
				+ (msgTimestamp == null ? 0 : msgTimestamp.hashCode());
		return result;
	}

}
