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


/**
 * This class represents an AIS position report
 * 
 * @author David Schmitz
 * @author Alexander Lotter
 * 
 */
public class AISPositionA implements IAISMessage, IAISDecodable, Cloneable {


	private int msgId;
	
	private int repeatIndicator;

	private int mmsi;

	/** navigation status */
	private int navState;

	/** rate of turn +-127 */
	private double rot;

	/**
	 * speed over ground in knots range 0-102.2 where 102.2knots denotes
	 * 102.2knots and more
	 */
	private double sog;

	/** longitude in degrees +-180° east=positive west=negative */
	private double longitude;

	/** latitude in degrees +-90° north=positive south=negative */
	private double latitude;

	/** course over ground in knots */
	private double cog;

	/** degrees 0-359 or 511 for n/a */
	private int trueHeading;

	/** message timestamp in UTC */
	private Timestamp msgTimestamp;

	public AISPositionA() {

	}

	/**
	 * This is the default constructor of class AISPosition.
	 * 
	 * @param msgId
	 * @param repeatIndicator
	 * @param mmsi
	 * @param navState	navigation state
	 * @param rot	rate of turn
	 * @param sog	speed over ground
	 * @param longitude
	 * @param latitude
	 * @param cog	course over ground
	 * @param trueHeading
	 * @param msgTimestamp
	 */
	public AISPositionA(int msgId, int repeatIndicator, int mmsi, int navState, double rot,
			double sog, double longitude, double latitude, double cog,
			int trueHeading, Timestamp msgTimestamp) {
		this.msgId = msgId;
		this.repeatIndicator = repeatIndicator;
		this.mmsi = mmsi;
		this.navState = navState;
		this.rot = rot;
		this.sog = sog;
		this.longitude = longitude;
		this.latitude = latitude;
		this.cog = cog;
		this.trueHeading = trueHeading;
		this.msgTimestamp = msgTimestamp;
	}
	
	/**
	 * Simple getter for course over ground COG.
	 * @return COG
	 */
	public double getCog() {
		return cog;
	}
	
	/**
	 * Simple getter for MSG ID
	 * @return msgid
	 */
	public int getMsgId() {
		return msgId;
	}

	/**
	 * Simple getter for repeat indicator
	 * @return repeatIndicator
	 */
	public int getRepeatIndicator() {
		return repeatIndicator;
	}
	
	/**
	 * Simple getter for latitude.
	 * @return latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Simple getter for longitude.
	 * @return longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	public Timestamp getMsgTimestamp() {
		return msgTimestamp;
	}

	/**
	 * Simple getter for navigation state.
	 * @return navState
	 */
	public int getNavState() {
		return navState;
	}
	
	/**
	 * Simple getter for rate of turn.
	 * @return ROT
	 */
	public double getRot() {
		return rot;
	}

	/**
	 * Simple getter for speed over ground.
	 * @return SOG
	 */
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
	public String toString(){
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("AISPositionA\n");
		sBuffer.append("MSGID\t\t" + this.msgId + "\n");
		sBuffer.append("REPEAT IND\t" + this.repeatIndicator + "\n");
		sBuffer.append("MMSI\t\t" + this.mmsi + "\n");
		sBuffer.append("NAVSTATE\t" + this.navState + "\n");
		sBuffer.append("ROT\t\t" + this.rot + "\n");
		sBuffer.append("LONGITUDE\t" + this.longitude + "\n");
		sBuffer.append("LATITUDE\t" + this.latitude + "\n");
		sBuffer.append("COG\t\t" + this.cog + "\n");
		sBuffer.append("HEADING\t\t" + this.trueHeading + "\n");
		sBuffer.append("MSGTIMESTAMP\t" + this.msgTimestamp);
		return sBuffer.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(cog);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + mmsi;
		result = prime * result + msgId;
		result = prime * result + ((msgTimestamp == null) ? 0 : msgTimestamp.hashCode());
		result = prime * result + navState;
		result = prime * result + repeatIndicator;
		temp = Double.doubleToLongBits(rot);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(sog);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + trueHeading;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AISPositionA other = (AISPositionA) obj;
		if (Double.doubleToLongBits(cog) != Double.doubleToLongBits(other.cog))
			return false;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
			return false;
		if (mmsi != other.mmsi)
			return false;
		if (msgId != other.msgId)
			return false;
		if (msgTimestamp == null) {
			if (other.msgTimestamp != null)
				return false;
		} else if (!msgTimestamp.equals(other.msgTimestamp))
			return false;
		if (navState != other.navState)
			return false;
		if (repeatIndicator != other.repeatIndicator)
			return false;
		if (Double.doubleToLongBits(rot) != Double.doubleToLongBits(other.rot))
			return false;
		if (Double.doubleToLongBits(sog) != Double.doubleToLongBits(other.sog))
			return false;
		if (trueHeading != other.trueHeading)
			return false;
		return true;
	}

	@Override
	public AISPositionA clone() {
		try {
			return (AISPositionA) super.clone();
		}

		catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	/**
	 * Parse decoded bytes for the position data
	 * 
	 * Message 1,2,3 position reports ITU-R M.1371-1
	 * 
	 * @param decBytes
	 * @throws AISParseException
	 */
	public IAISMessage decode(String decBytes) throws AISParseException {

		if (decBytes.length() < 137) {
			throw new AISParseException(
					AISParseException.NOT_CONSISTENT_DECODED_STRING);
		}
		/* Possition Reports Message ID 1,2,3 bits 0-5 */
		this.msgId = AISDecoder.getDecValueByBinStr(decBytes.substring(0, 6), false);

		/* repeat indicator, bits 6-7 */
		this.repeatIndicator = AISDecoder.getDecValueByBinStr(decBytes.substring(6, 8), false);

		/* user id mmsi bits 8-37 */
		this.mmsi = AISDecoder.getDecValueByBinStr(decBytes.substring(8, 38), false);

		/* navigational status bits 38-41 - 4 bits */
		this.navState = AISDecoder.getDecValueByBinStr(decBytes.substring(38, 42), false);

		/* rate of turn bits 42-49 - 8 bits */
		this.rot = AISDecoder.getDecValueByBinStr(decBytes.substring(42, 50), true);

		/* speed over ground bits 50-59 - 10 bits */
		this.sog = (AISDecoder.getDecValueByBinStr(decBytes.substring(50, 60), false) / 10.0);

		// bit 60 - position accuracy won't be read.

		/* longitude bits 61-88 */
		int longitudeHour = AISDecoder.getDecValueByBinStr(decBytes.substring(61, 89), true);

		this.longitude = (longitudeHour / 600000.0);

		if (this.longitude > 180.0 || this.longitude < -180.0)
		{
			throw new AISParseException(AISParseException.LONGITUDE_OUT_OF_RANGE + " " + longitude);
		}

		/* latitude bits 89-115 */
		int latitudeHour = AISDecoder.getDecValueByBinStr(decBytes.substring(89, 116), true);
		this.latitude = (latitudeHour / 600000.0);
		if (this.latitude > 90.0 || this.latitude < -90.0)
		{
			throw new AISParseException(AISParseException.LATITUDE_OUT_OF_RANGE + " " + latitude);
		}

		/* COG bits 117-127 */
		this.cog = (AISDecoder.getDecValueByBinStr(decBytes.substring(116, 128), false) / 10.0);

		/* true heading bits 128-136 */
		this.trueHeading = AISDecoder.getDecValueByBinStr(decBytes.substring(128, 137), false);

//		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//		this.msgTimestamp = new Timestamp(cal.getTimeInMillis());
		
		// time stamp bits 138-143
		int secs = AISDecoder.getDecValueByBinStr(decBytes.substring(138, 143), false);
		this.msgTimestamp = new Timestamp(secs * 1000);

		
		// TODO: implement the rest bits
		return this;
	}
}
