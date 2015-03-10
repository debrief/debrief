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

import org.freeais.i18n.I18N;

public class AISParseException extends Exception {

	private static final long serialVersionUID = -6332039076147587942L;

	public static final String INVALID_CHARACTER = I18N
			.getString("AISParseException.INVALID_CHARATER"); //$NON-NLS-1$

	public static final String NO_BYTES_TO_PARSE = I18N
			.getString("AISParseException.NO_BYTES_TO_PARSE"); //$NON-NLS-1$

	public static final String WRONG_MESSAGE_ID = I18N
			.getString("AISParseException.WRONG_MESSAGE_ID"); //$NON-NLS-1$

	public static final String NO_AIS_MESSAGE = I18N.getString("AISParseException.NO_AIS_MESSAGE"); //$NON-NLS-1$

	public static final String EMPTY_AIS_MESSAGE = I18N
			.getString("AISParseException.EMPTY_AIS_MESSAGE"); //$NON-NLS-1$

	public static final String BITSET_OUT_OF_RANGE = I18N
			.getString("AISParseException.BITSET_OUT_OF_RANGE"); //$NON-NLS-1$

	public static final String NO_DECODED_BYTES = I18N
	.getString("AISParseException.NO_DECODED_BYTES"); //$NON-NLS-1$

	public static final String NOT_CONSISTENT_DECODED_STRING = I18N
	.getString("AISParseException.NOT_CONSISTENT_DECODED_STRING"); //$NON-NLS-1$
	
	public static final String LONGITUDE_OUT_OF_RANGE = I18N
	.getString("AISParseException.LONGITUDE_OUT_OF_RANGE"); //$NON-NLS-1$
	
	public static final String LATITUDE_OUT_OF_RANGE = I18N
	.getString("AISParseException.LATITUDE_OUT_OF_RANGE"); //$NON-NLS-1$
	
	public AISParseException() {
	}

	public AISParseException(String errorMsg) {
		super(errorMsg);
	}
}
