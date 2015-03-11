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

import java.util.Arrays;

/**
 * Implementation of the AISDecoder in accordance with<br>
 * IMO Recommendation ITU-R.M.1371-1<br>
 * and INTERNATIONAL STANDARD IEC 61993-2:<br>
 * "Maritime navigation and radio communication equipment and systems –
 * Automatic identification systems (AIS) – Part 2: Class A shipborne equipment
 * of the universal automatic identification system (AIS) – Operational and
 * performance requirements, methods of test and required test results."
 *
 * @author Alexander Lotter
 * @author David Schmitz
 */
public class AISDecoder {


	/**
	 * Encoded message will be decoded and packed into corresponding object
	 *
	 * @param encodedMsg
	 * @return Corresponding IAISMessage implementation
	 * @throws AISParseException
	 */
	public static IAISMessage decode(String encodedMsg)
			throws AISParseException {

		if (encodedMsg == null || encodedMsg.isEmpty()) {
			throw new AISParseException(AISParseException.EMPTY_AIS_MESSAGE);
		}
		byte[] toDecBytes = encodedMsg.getBytes();
		byte[] decBytes = ascii8To6bitBin(toDecBytes);

		int msgId = decBytes[0];

		String decodedBinString = getDecodedStr(decBytes);

		switch (msgId) {
		case 1:
		case 2:
		case 3:
			return new AISPositionA().decode(decodedBinString);
		case 4:
		case 11:
			return new AISBaseStation().decode(decodedBinString);
		case 5:
			return new AISVessel().decode(decodedBinString);
		case 18:
			return new AISPositionB().decode(decodedBinString);
		case 19:
			return new AISPositionExtB().decode(decodedBinString);
		}


		throw new AISParseException(AISParseException.WRONG_MESSAGE_ID + " "
				+ msgId);
	}

	/**
	 * Method to convert ASCII-coded character to 6-bit binary
	 *
	 * @param toDecBytes
	 * @return decodedBytes
	 */
	private static byte[] ascii8To6bitBin(byte[] toDecBytes)
			throws AISParseException {

		byte[] convertedBytes = new byte[toDecBytes.length];
		int sum = 0;
		int _6bitBin = 0;

		for (int i = 0; i < toDecBytes.length; i++) {
			sum = 0;
			_6bitBin = 0;

			if (toDecBytes[i] < 48) {
				throw new AISParseException(AISParseException.INVALID_CHARACTER
						+ " " + (char) toDecBytes[i]);
			}
			if (toDecBytes[i] > 119) {
				throw new AISParseException(AISParseException.INVALID_CHARACTER
						+ " " + (char) toDecBytes[i]);
			}
			if (toDecBytes[i] > 87) {
				if (toDecBytes[i] < 96) {
					throw new AISParseException(
							AISParseException.INVALID_CHARACTER + " "
									+ (char) toDecBytes[i]);
				}
				sum = toDecBytes[i] + 40;
			} else {
				sum = toDecBytes[i] + 40;
			}
			if (sum != 0) {
				if (sum > 128) {
					sum += 32;
				} else {
					sum += 40;
				}
				_6bitBin = sum & 0x3F;
				convertedBytes[i] = (byte) _6bitBin;
			}
		}

		return convertedBytes;
	}

	/**
	 * Set a String of decoded bytes
	 *
	 * @param decBytes
	 * @throws AISParseException
	 */
	private static String getDecodedStr(byte[] decBytes) {

		String decStr = "";
		for (int i = 0; i < decBytes.length; i++) {

			int decByte = decBytes[i];
			String bitStr = Integer.toBinaryString(decByte);

			if (bitStr.length() < 6) {

				String zerosToAdd = "";

				for (int j = bitStr.length() - 1; j < 5; j++) {
					zerosToAdd += "0";
				}
				bitStr = zerosToAdd + bitStr;
			}
			for (int j = 0; j < 6; j++) {
				decStr += bitStr.charAt(j);
			}
		}

		return decStr;
	}

	/**
	 * Convert one 6 bit ASCII character to 8 bit ASCII character
	 *
	 * @param byteToDec
	 * @return
	 * @throws AISParseException
	 */
	private static byte convert6BitCharToStandartdAscii(byte byteToDec) {

		byte decByte = 0;
		if (byteToDec < 32) {
			decByte = (byte) (byteToDec + 64);
		} else if (byteToDec < 63) {
			decByte = byteToDec;
		}

		return decByte;
	}

	/**
	 * Get a value for specified bits from the binary string.
	 *
	 * @param fromBit
	 * @param toBit
	 * @return
	 * @throws AISParseException
	 */
	protected static int getDecValueByBinStr(String decStr, boolean signum) {

		Integer decValue = Integer.parseInt(decStr, 2);
		if (signum && decStr.charAt(0) == '1') {
			char[] invert = new char[decStr.length()];
			Arrays.fill(invert, '1');
			decValue ^= Integer.parseInt(new String(invert), 2);
			decValue += 1;
			decValue = -decValue;
		}

		return decValue;
	}

	/**
	 * Decode 6 bit String to standard ASCII String
	 *
	 * Input is a binary string of 0 and 1 each 6 bit is a character that will
	 * be converted to the standard ASCII character
	 *
	 * @param str
	 * @return
	 * @throws AISParseException
	 */
	protected static String getDecStringFrom6BitStr(String str) {

		String txt = "";
		for (int i = 0; i < str.length(); i = i + 6) {
			byte _byte = (byte) Integer.parseInt(str.substring(i, i + 6), 2);
			_byte = convert6BitCharToStandartdAscii(_byte);
			char convChar = (char) _byte;

			if (convChar == '@') {
				break;
			}
			txt += (char) _byte;
		}
		txt = txt.trim();

		return txt;
	}
}
