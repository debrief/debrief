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

/**
 * This class is representation of an AIS vessel report
 *
 * @author David Schmitz
 * @author Alexander Lotter
 */
public class AISVessel implements IAISMessage, IAISDecodable, Cloneable {

	/** message id */
	private int msgId;

	/** unique and persistent ship identification number */
	private int imo;

	/** maritime mobile service identity */
	private int mmsi;

	/** vessel callsign for radio */
	private String callSign;

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

	/** estimated time of arrival */
	private Timestamp eta;

	/** maximum present static draught */
	private double draught;

	/** vessels destionation */
	private String destination;

	/** countryId from mmsi */
	private int countryId;

	/** message source */
	private int msgSrc;

	public AISVessel() {

	}

	/**
	 * This is the default constructor of class AISVessel.
	 *
	 * @param msgId
	 * @param imo
	 * @param mmsi
	 * @param callSign
	 * @param name
	 * @param shipType
	 * @param dimensionA
	 * @param dimensionB
	 * @param dimensionC
	 * @param dimensionD
	 * @param eta
	 * @param draught
	 * @param destination
	 * @param countryId
	 * @param msgSrc
	 */
	public AISVessel(final int msgId, final int imo, final int mmsi, final String callSign, final String name,
			final int shipType, final int dimensionA, final int dimensionB, final int dimensionC, final int dimensionD,
			final Timestamp eta, final double draught, final String destination, final int countryId,
			final int msgSrc) {
		this.msgId = msgId;
		this.imo = imo;
		this.mmsi = mmsi;
		this.callSign = callSign;
		this.name = name;
		this.shipType = shipType;
		this.dimensionA = dimensionA;
		this.dimensionB = dimensionB;
		this.dimensionC = dimensionC;
		this.dimensionD = dimensionD;
		this.eta = eta;
		this.draught = draught;
		this.destination = destination;
		this.countryId = countryId;
		this.msgSrc = msgSrc;

	}

	@Override
	public AISVessel clone() {
		try {
			return (AISVessel) super.clone();
		}

		catch (final CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	/**
	 * Parse decoded bytes for the static data
	 *
	 * Message 5: Ship static and voyage related data ITU-R M.1371-1
	 *
	 *
	 * @param decBytes decoded bytes
	 * @throws AISParseException
	 */
	@Override
	public IAISMessage decode(final String decBytes) throws AISParseException {

		if (decBytes.length() < 421)
			throw new AISParseException(AISParseException.NOT_CONSISTENT_DECODED_STRING);
		/* Possition Reports Message ID 1,2,3 bits 0-5 */
		this.msgId = AISDecoder.getDecValueByBinStr(decBytes.substring(0, 6), false);

		/* User ID mmsi bits 8-37 */
		this.mmsi = AISDecoder.getDecValueByBinStr(decBytes.substring(8, 38), false);

		// AIS version indicator bits 38-39 - 2 bits
		// int navState = decBytes[6] & 0x3;
		// System.out.println("navState = "+navState);

		/* IMO number bits 40-69 - 30 bits */
		this.imo = AISDecoder.getDecValueByBinStr(decBytes.substring(40, 70), false);

		/* Call sign bits 70-111 - 42 bits */
		this.callSign = AISDecoder.getDecStringFrom6BitStr(decBytes.substring(70, 112));

		/* Name bits 112-231 - 120 bits */
		this.name = AISDecoder.getDecStringFrom6BitStr(decBytes.substring(112, 232));

		/* Type of ship and cargo type bits 232-239 - 8 bits */
		// :TODO Ship and Cargo digits should be splitted
		this.shipType = AISDecoder.getDecValueByBinStr(decBytes.substring(232, 240), false);

		final String mmsiStr = Integer.toString(this.mmsi);
		if (!(mmsiStr.length() < 3)) {
			this.countryId = Integer.valueOf(mmsiStr.substring(0, 3));
		} else {
			this.countryId = 0;
		}

		/* Dimension/reference for position bits 240-269 - 30 bits */
		this.dimensionA = AISDecoder.getDecValueByBinStr(decBytes.substring(240, 249), false);
		this.dimensionB = AISDecoder.getDecValueByBinStr(decBytes.substring(249, 258), false);
		this.dimensionC = AISDecoder.getDecValueByBinStr(decBytes.substring(258, 264), false);
		this.dimensionD = AISDecoder.getDecValueByBinStr(decBytes.substring(264, 270), false);

//		int length = this.dimensionA + this.dimensionB;
//		int width = this.dimensionC + this.dimensionD;

		// Type of electronic positon fixing device
		// bits 270-273 - 4 bits

		/* ETA bits 274-293 - 20 bits */
		final int etaMinute = AISDecoder.getDecValueByBinStr(decBytes.substring(288, 294), false);
		final int etaHour = AISDecoder.getDecValueByBinStr(decBytes.substring(283, 288), false);
		final int etaDay = AISDecoder.getDecValueByBinStr(decBytes.substring(278, 283), false);
		final int etaMonth = AISDecoder.getDecValueByBinStr(decBytes.substring(274, 278), false);
		final Calendar cal = Calendar.getInstance();// TimeZone.getTimeZone("UTC"));
		final int year = cal.get(Calendar.YEAR);
		cal.clear();
		cal.set(year, etaMonth - 1, etaDay, etaHour, etaMinute);

		this.eta = new Timestamp(cal.getTimeInMillis());

		/* Maximum present static draught bits 294-301 - 8 bits */
		final int draughtInt = AISDecoder.getDecValueByBinStr(decBytes.substring(294, 302), false);
		this.draught = draughtInt / 10.0;

		/* Destionation bits 302-421 - 120 bits */
		this.destination = AISDecoder.getDecStringFrom6BitStr(decBytes.substring(302, 422));

		// TODO: impelemt the rest bits

		// TODO: implement vesselType, countryId, msgSrc, cargoType
		// AISVessel aisVessel = new
		// AISVessel(messageId,imo,mmsi,callSign,name,vesselType,cargoType,
		// dimA,dimB,dimC,dimD,countryId,msgSrc);

		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (!o.getClass().equals(o.getClass()))
			return false;

		final AISVessel that = (AISVessel) o;

		final boolean same = this.destination.equals(that.destination) && this.callSign.equals(that.callSign)
				&& this.countryId == that.countryId && this.dimensionA == that.dimensionA
				&& this.dimensionB == that.dimensionB && this.dimensionC == that.dimensionC
				&& this.dimensionD == that.dimensionD && this.msgId == that.msgId && this.imo == that.imo
				&& this.mmsi == that.mmsi && this.msgSrc == that.msgSrc && this.name.equals(that.name)
				&& this.shipType == that.shipType;

		return same;
	}

	public String getCallSign() {
		return callSign;
	}

	public int getCountryId() {
		return countryId;
	}

	public String getDestination() {
		return destination;
	}

	public int getDimensionA() {
		return dimensionA;
	}

	public int getDimensionB() {
		return dimensionB;
	}

	public int getDimensionC() {
		return dimensionC;
	}

	public int getDimensionD() {
		return dimensionD;
	}

	public double getDraught() {
		return draught;
	}

	public Timestamp getEta() {
		return eta;
	}

	public int getImo() {
		return imo;
	}

	public int getMmsi() {
		return mmsi;
	}

	public int getMsgId() {
		return msgId;
	}

	public int getMsgSrc() {
		return msgSrc;
	}

	public String getName() {
		return name;
	}

	public int getShipType() {
		return shipType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(draught);
		result = prime * result + (int) (temp ^ temp >>> 32);
		result = prime * result + msgId;
		result = prime * result + imo;
		result = prime * result + mmsi;
		result = prime * result + shipType;
		result = prime * result + (eta == null ? 0 : eta.hashCode());
		result = prime * result + (callSign == null ? 0 : callSign.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (destination == null ? 0 : destination.hashCode());
		result = prime * result + countryId;
		result = prime * result + msgSrc;
		result = prime * result + dimensionA;
		result = prime * result + dimensionB;
		result = prime * result + dimensionC;
		result = prime * result + dimensionD;
		return result;
	}

	@Override
	public String toString() {
		final StringBuffer sbuffer = new StringBuffer();
		sbuffer.append("AISVessel\n");
		sbuffer.append("MSGID\t\t" + this.msgId + "\n");
		sbuffer.append("IMO\t\t" + this.imo + "\n");
		sbuffer.append("MMSI\t\t" + this.mmsi + "\n");
		sbuffer.append("CALLSIGN\t" + this.callSign + "\n");
		sbuffer.append("NAME\t\t" + this.name + "\n");
		sbuffer.append("SHIPTYPE\t" + this.shipType + "\n");
		sbuffer.append("DIMA\t\t" + this.dimensionA + "\n");
		sbuffer.append("DIMB\t\t" + this.dimensionB + "\n");
		sbuffer.append("DIMC\t\t" + this.dimensionC + "\n");
		sbuffer.append("DIMD\t\t" + this.dimensionD + "\n");
		sbuffer.append("ETA\t\t" + this.eta + "\n");
		sbuffer.append("DRAUGHT\t\t" + this.draught + "\n");
		sbuffer.append("DESTINATION\t" + this.destination + "\n");
		sbuffer.append("COUNTRYID\t" + this.countryId + "\n");
		sbuffer.append("MSGSRC\t\t" + this.msgSrc + "\n");
		return sbuffer.toString();
	}
}
