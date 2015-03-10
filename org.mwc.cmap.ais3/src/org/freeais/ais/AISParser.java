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

import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class parses an AIS message. <br>
 * AIS-Message format:<br>
 * !AIVDM,[1],[2],[3],[4],[5]*[6]<br>
 * 1 - Total number of sentences needed to<br>
 * transfer the message (1-9)<br>
 * 2 - Sentence number (1-9)<br>
 * 3 - Sequential Message identifier (Channel A-B)<br>
 * 4 - Decoded AIS-Message<br>
 * 5 - Number of fill-bits (0-5)<br>
 * 6 - Check sum Compare IEC - 61993-2 Standard<br>
 *
 * @author David Schmitz
 * @author Alexander Lotter
 *
 */
public class AISParser {

	private static Logger logger = LogManager.getLogger(AISParser.class);

	/**
	 * previous total number of sentences needed to transfer the message, in
	 * case the message was sent in multiparts
	 */
	private int oldTotalNumOfMsgs = 0;

	/**
	 * current total number of sentences needed to transfer the message, in case
	 * the message was sent in multiparts
	 */
	private int currTotalNumOfMsgs = 0;

	/**
	 * previous number of sentences, in case the message was sent in multiparts
	 */
	private int oldSentenceNumber = 0;

	/**
	 * current number of sentences, in case the message was sent in multiparts
	 */
	private int currSentenceNumber = 0;

	/**
	 * previous sequence number identifier, in case the message was sent in
	 * multiparts
	 */
	private int oldSequenceNumber = 0;

	/**
	 * current sequence number identifier, in case the message was sent in
	 * multiparts
	 */
	private int currSequenceNumber = 0;

	/**
	 * current decoded message
	 */
	private String currMsg = "";

	private boolean isWholeMsg = false;

	/** Regular expression for AIS-Messages */
	private static final Pattern pattern = Pattern
			.compile("!AIVDM\\,[1-9]{1}\\,[1-9]{1}\\,([0-9]{0,1})\\,[0-3A-B]{1}\\,([0-9\\:\\;\\<\\=\\>\\?\\@A-W\\`a-w]+)\\,[0-5]\\*[A-F0-9]{2}");

	/**
	 * Method for parsing encoded AIS-Messages. All messages are passed to the
	 * parser in a raw format, will be validated and decoded.
	 *
	 * @param encodedMsg
	 * @return decoded object of a type IAISMessage
	 * @throws AISParseException
	 */
	public IAISMessage parse(String encodedMsg) throws AISParseException {
		logger.info("parse(encodedMsg) - Entry");

		if (isValidAIS(encodedMsg)) {

			String[] msgTokens = new String[6];
			int tokenCnt = 0;
			int index = 5;
			while ((index = encodedMsg.indexOf(",", index)) != -1) {

				int currIndex = encodedMsg.indexOf(",", index + 1);
				if (currIndex != -1) {
					msgTokens[tokenCnt] = encodedMsg.substring(index + 1,
							currIndex);
				} else {
					msgTokens[tokenCnt] = encodedMsg.substring(index + 1,
							encodedMsg.length());
				}
				tokenCnt++;
				index++;
			}
			isWholeMsg = false;
			if (msgTokens[0].equals("1")) {
				currMsg = msgTokens[4];
				isWholeMsg = true;
			} else {
				currTotalNumOfMsgs = Integer.valueOf(msgTokens[0]).intValue();
				currSentenceNumber = Integer.valueOf(msgTokens[1]).intValue();
				currSequenceNumber = Integer.valueOf(msgTokens[2]).intValue();

				if (currSentenceNumber == 1) {
					oldTotalNumOfMsgs = currTotalNumOfMsgs;
					oldSentenceNumber = currSentenceNumber;
					oldSequenceNumber = currSequenceNumber;
					currMsg = msgTokens[4];
				} else {
					if (currTotalNumOfMsgs > oldTotalNumOfMsgs
							|| currSentenceNumber != oldSentenceNumber + 1
							|| currSequenceNumber != oldSequenceNumber) {
						initMsgParams();
						return null;
					}
					currMsg += msgTokens[4];
					oldSentenceNumber = currSentenceNumber;
					if (currSentenceNumber == oldTotalNumOfMsgs) {
						isWholeMsg = true;
					}
				}
			}
			if (isWholeMsg) {
				initMsgParams();
				IAISMessage aisMessage = AISDecoder.decode(currMsg);
				return aisMessage;
			}
			return null;
		}
		throw new AISParseException(AISParseException.NO_AIS_MESSAGE);

	}

	/**
	 * Prepare parser for a new raw AIS-Message to parse.
	 */
	private void initMsgParams() {
		logger.info("initMsgParams() - Entry");

		oldTotalNumOfMsgs = 0;
		oldSentenceNumber = 0;
		oldSequenceNumber = 0;

		logger.info("initMsgParams() - Exit");
	}

	/**
	 * Validation of an AIS-Message
	 *
	 * @param ais encoded AIS-Message
	 * @return true if message is valid false if message is invalid
	 */
	public static boolean isValidAIS(String ais) {
		logger.info("isValidAIS(ais) - Entry");
		boolean isValid;
		int index = ais.indexOf("!AIVDM");
		String msg = ais;
		if (index != -1) {
			msg = ais.substring(index, ais.length());
		}
		if (!validCRC(msg)) {
			isValid = false;
		} else {
			isValid = pattern.matcher(msg).matches();
		}
		logger.info("isValidAIS(ais) - Exit");
		return isValid;
	}

	/**
	 * This method calculates the checksum for a plain AIS string AIVDM,,,,,,0
	 * or fullfletched string !AIVDM,,,,,,0*11
	 */
	public static String calcCRC(String ais) {
		logger.info("calcCRC(ais) - Entry");

		byte[] data = null;
		if (ais.contains("!") && ais.contains("*")) {
			data = ais.substring(1, ais.indexOf("*")).getBytes();
		} else {
			data = ais.getBytes();
		}
		int crc = 0;
		for (byte pos : data) {
			if (crc == 0) {
				crc = pos;
			} else {
				crc ^= pos;
			}
		}
		String result = String.format("%1$02X", crc);

		logger.info("calcCRC(ais) - Exit");
		return result;
	}

	/**
	 * This method extracts the checksum of a AIS string and returns it in hex
	 * notation.
	 *
	 * @param ais AIS string to extract the checksum from
	 * @return checksum
	 */
	public static String extractCRC(String ais) {
		logger.info("extractCRC(ais) - Entry");

		String crc = ais.substring(ais.indexOf('*') + 1);

		logger.info("extractCRC(ais) - Exit");
		return crc;
	}

	/**
	 * Proof whether the checksum of an AIS string is valid.
	 *
	 * @param ais AIS message to check for correct CRC.
	 * @return true if CRC checksum is correct
	 */
	public static boolean validCRC(String ais) {
		logger.info("validCRC(ais) - Entry");

		boolean result = extractCRC(ais).equals(calcCRC(ais));

		logger.info("validCRC(ais) - Exit");
		return result;
	}

}
