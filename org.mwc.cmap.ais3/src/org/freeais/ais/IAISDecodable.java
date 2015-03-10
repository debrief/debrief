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

/**
 * 
 * All position reports, which implement this interface can be decoded by the
 * parser.
 * 
 * @author Alexander Lotter
 * @author David Schmitz
 * 
 */
public interface IAISDecodable {

	public IAISMessage decode(String decBytes) throws AISParseException;

}
