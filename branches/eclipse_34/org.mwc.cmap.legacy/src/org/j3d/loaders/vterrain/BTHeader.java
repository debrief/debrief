/*****************************************************************************
 *                            (c) j3d.org 2002
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.loaders.vterrain;

// Standard imports
// none

// Application specific imports
// none

/**
 * Representation of the BT File format header information.
 * <p>
 *
 * Not included in the header is data size or floating point flags as these
 * are only needed internally by the parser and not useful to the end user.
 *
 * The definition of the file format can be found at:
 * <a href="http://www.vterrain.org/Implementation/BT.html">
 *  http://www.vterrain.org/Implementation/BT.html
 * </a>
 *
 * @author  Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class BTHeader
{
    // Constants for headers

    /** Header string constant representing V1.0 */
    public static final String VERSION_1_0 = "binterr1.0";

    /** Header string constant representing V1.1 */
    public static final String VERSION_1_1 = "binterr1.1";

    /** Header string constant representing V1.2 */
    public static final String VERSION_1_2 = "binterr1.2";

    // Contants for datum values. Values taken from the file format.

    public static final int NO_DATUM = -2;
    public static final int UNKNOWN_DATUM = -1;
    public static final int ADINDAN = 0;
    public static final int ARC1950 = 1;
    public static final int ARC1960 = 2;
    public static final int AUSTRALIAN_GEODETIC_1966 = 3;
    public static final int AUSTRALIAN_GEODETIC_1984 = 4;
    public static final int CAMP_AREA_ASTRO = 5;
    public static final int CAPE = 6;
    public static final int EUROPEAN_DATUM_1950 = 7;
    public static final int EUROPEAN_DATUM_1979 = 8;
    public static final int GEODETIC_DATUM_1949 = 9;
    public static final int HONG_KONG_1963 = 10;
    public static final int HU_TZU_SHAN = 11;
    public static final int INDIAN = 12;
    public static final int NAD27 = 13;
    public static final int NAD83 = 14;
    public static final int OLD_HAWAIIAN_MEAN = 15;
    public static final int OMAN = 16;
    public static final int ORDNANCE_SURVEY_1936 = 17;
    public static final int PUERTO_RICO = 18;
    public static final int PULKOVO_1942 = 19;
    public static final int PROVISIONAL_S_AMERICAN_1956 = 20;
    public static final int TOKYO = 21;
    public static final int WGS_72 = 22;
    public static final int WGS_84 = 23;


    /** The header string indicating version number. */
    public String version;

    /** The number of columns of data in the file */
    public int columns;

    /** The number of rows of data in the file */
    public int rows;

    /** True if this file is in UTM projection. False for Geographic. */
    public boolean utmProjection;

    /** The std UTM zone for this file */
    public int utmZone;

    /** The Datum used for the UTM zone */
    public int datum;

    /** The left-most extent Lat-Long if not UTM. */
    public double leftExtent;

    /** The right-most extent Lat-Long if not UTM. */
    public double rightExtent;

    /** The bottom-most extent Lat-Long if not UTM. */
    public double bottomExtent;

    /** The top-most extent Lat-Long if not UTM. */
    public double topExtent;

    /** True if this needs the external projection file for more information */
    public boolean needsExternalProj;
}
