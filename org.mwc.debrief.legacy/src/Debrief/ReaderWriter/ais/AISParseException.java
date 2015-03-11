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


public class AISParseException extends Exception {

	private static final long serialVersionUID = -6332039076147587942L;

	public static final String INVALID_CHARACTER = "Wrong character in the string";

	public static final String NO_BYTES_TO_PARSE = "There are no bytes to parse";

	public static final String WRONG_MESSAGE_ID = "MessageId not supported";

	public static final String NO_AIS_MESSAGE = "Not an ais-message";

	public static final String EMPTY_AIS_MESSAGE = "Empty ais-message";

	public static final String BITSET_OUT_OF_RANGE = "BitSet is out of range";

	public static final String NO_DECODED_BYTES = "There are no bytes to decode";

	public static final String NOT_CONSISTENT_DECODED_STRING = "Decoded binary string is not consistent";
	
	public static final String LONGITUDE_OUT_OF_RANGE = "Longitude value is out of range";
	
	public static final String LATITUDE_OUT_OF_RANGE = "Latitude value is out of range";
	
	public AISParseException() {
	}

	public AISParseException(String errorMsg) {
		super(errorMsg);
	}
}
