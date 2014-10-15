/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package MWC.GUI.S57;

import java.util.Iterator;
import java.util.Vector;

import com.bbn.openmap.layer.vpf.MutableInt;
import com.bbn.openmap.util.Debug;
import com.bbn.openmap.util.PropUtils;

/**
 * Information from the DDR defining one field. Note that just because
 * a field is defined for a DDFModule doesn't mean that it actually
 * occurs on any records in the module. DDFFieldDefns are normally
 * just significant as containers of the DDFSubfieldDefinitions.
 */
public class DDFFieldDefinition implements DDFConstants {

    protected DDFModule poModule;
    protected String pszTag;

    protected String _fieldName;
    protected String _arrayDescr;
    protected String _formatControls;

    protected boolean bRepeatingSubfields;
    protected int nFixedWidth; // zero if variable.

    protected DataStructCode _data_struct_code;
    protected DataTypeCode _data_type_code;

    protected Vector <DDFSubfieldDefinition>paoSubfieldDefns;

    /**
     * Fetch a pointer to the field name (tag).
     * 
     * @return this is an internal copy and shouldn't be freed.
     */
    public String getName() {
        return pszTag;
    }

    /**
     * Fetch a longer descriptio of this field.
     * 
     * @return this is an internal copy and shouldn't be freed.
     */
    public String getDescription() {
        return _fieldName;
    }

    /**
     * Get the number of subfields.
     */
    public int getSubfieldCount() {
        if (paoSubfieldDefns != null) {
            return paoSubfieldDefns.size();
        }
        return 0;
    }

    /**
     * Get the width of this field. This function isn't normally used
     * by applications.
     * 
     * @return The width of the field in bytes, or zero if the field
     *         is not apparently of a fixed width.
     */
    public int getFixedWidth() {
        return nFixedWidth;
    }

    /**
     * Fetch repeating flag.
     * 
     * @return true if the field is marked as repeating.
     */
    public boolean isRepeating() {
        return bRepeatingSubfields;
    }

    /** this is just for an S-57 hack for swedish data */
    public void setRepeating(final boolean val) {
        bRepeatingSubfields = val;
    }

    /** ********************************************************************* */
    /* DDFFieldDefn() */
    /** ********************************************************************* */

    public DDFFieldDefinition() {
        poModule = null;
        pszTag = null;
        _fieldName = null;
        _arrayDescr = null;
        _formatControls = null;
        paoSubfieldDefns = null;
        bRepeatingSubfields = false;
    }

    public DDFFieldDefinition(final DDFModule poModuleIn, final String pszTagIn,
            final byte[] pachFieldArea) {

        initialize(poModuleIn, pszTagIn, pachFieldArea);
    }

    /**
     * Initialize the field definition from the information in the DDR
     * record. This is called by DDFModule.open().
     * 
     * @param poModuleIn DDFModule reprsenting file being read.
     * @param pszTagIn the name of this field.
     * @param pachFieldArea the data bytes in the file representing
     *        the field from the header.
     */
    public boolean initialize(final DDFModule poModuleIn, final String pszTagIn,
                              final byte[] pachFieldArea) {

        /// pachFieldArea needs to be specified better. It's an
        /// offset into a character array, and we need to know what
        // it
        /// is to scope it better in Java.

        int iFDOffset = poModuleIn._fieldControlLength;

        poModule = poModuleIn;
        pszTag = pszTagIn;

        /* -------------------------------------------------------------------- */
        /* Set the data struct and type codes. */
        /* -------------------------------------------------------------------- */
        _data_struct_code = DataStructCode.get((char) pachFieldArea[0]);
        _data_type_code = DataTypeCode.get((char) pachFieldArea[1]);

        if (Debug.debugging("iso8211")) {
            Debug.output("DDFFieldDefinition.initialize(" + pszTagIn
                    + "):\n\t\t data_struct_code = " + _data_struct_code
                    + "\n\t\t data_type_code = " + _data_type_code
                    + "\n\t\t iFDOffset = " + iFDOffset);
        }

        /* -------------------------------------------------------------------- */
        /* Capture the field name, description (sub field names), and */
        /* format statements. */
        /* -------------------------------------------------------------------- */

        byte[] tempData = new byte[pachFieldArea.length - iFDOffset];
        System.arraycopy(pachFieldArea,
                iFDOffset,
                tempData,
                0,
                pachFieldArea.length - iFDOffset);

        final MutableInt nCharsConsumed = new MutableInt();

        _fieldName = DDFUtils.fetchVariable(tempData,
                tempData.length,
                DDF_UNIT_TERMINATOR,
                DDF_FIELD_TERMINATOR,
                nCharsConsumed);
        if (Debug.debugging("iso8211")) {
            Debug.output("DDFFieldDefinition.initialize(" + pszTagIn
                    + "): created field name " + _fieldName);
        }

        iFDOffset += nCharsConsumed.value;

        tempData = new byte[pachFieldArea.length - iFDOffset];
        System.arraycopy(pachFieldArea,
                iFDOffset,
                tempData,
                0,
                pachFieldArea.length - iFDOffset);
        _arrayDescr = DDFUtils.fetchVariable(tempData,
                tempData.length,
                DDF_UNIT_TERMINATOR,
                DDF_FIELD_TERMINATOR,
                nCharsConsumed);
        iFDOffset += nCharsConsumed.value;

        tempData = new byte[pachFieldArea.length - iFDOffset];
        System.arraycopy(pachFieldArea,
                iFDOffset,
                tempData,
                0,
                pachFieldArea.length - iFDOffset);

        _formatControls = DDFUtils.fetchVariable(tempData,
                tempData.length,
                DDF_UNIT_TERMINATOR,
                DDF_FIELD_TERMINATOR,
                nCharsConsumed);

        /* -------------------------------------------------------------------- */
        /* Parse the subfield info. */
        /* -------------------------------------------------------------------- */
        if (_data_struct_code != DataStructCode.ELEMENTARY) {
            if (!buildSubfieldDefns(_arrayDescr)) {
                return false;
            }

            if (!applyFormats(_formatControls)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Write out field definition info.
     * 
     * A variety of information about this field definition, and all
     * its subfields are written out too.
     */
    public String toString() {
        final StringBuffer buf = new StringBuffer("  DDFFieldDefn:\n");
        buf.append("      Tag = " + pszTag + "\n");
        buf.append("      _fieldName = " + _fieldName + "\n");
        buf.append("      _arrayDescr = " + _arrayDescr + "\n");
        buf.append("      _formatControls = " + _formatControls + "\n");
        buf.append("      _data_struct_code = " + _data_struct_code + "\n");
        buf.append("      _data_type_code = " + _data_type_code + "\n");

        if (paoSubfieldDefns != null) {
            for (final Iterator<DDFSubfieldDefinition> it = paoSubfieldDefns.iterator(); it.hasNext();) {
                buf.append(it.next());
            }
        }

        return buf.toString();
    }

    /**
     * Based on the list contained in the string, build a set of
     * subfield definitions.
     */
    @SuppressWarnings("rawtypes")
		protected boolean buildSubfieldDefns(final String pszSublist) {
    	String thePszSublist = pszSublist;

        if (thePszSublist.charAt(0) == '*') {
            bRepeatingSubfields = true;
            thePszSublist = thePszSublist.substring(1);
        }

        final Vector papszSubfieldNames = PropUtils.parseMarkers(thePszSublist, "!");

        paoSubfieldDefns = new Vector<DDFSubfieldDefinition>();

        for (final Iterator it = papszSubfieldNames.iterator(); it.hasNext();) {
            final DDFSubfieldDefinition ddfsd = new DDFSubfieldDefinition();
            ddfsd.setName((String) it.next());
            paoSubfieldDefns.add(ddfsd);
        }

        return true;
    }

    /**
     * Extract a substring terminated by a comma (or end of string).
     * Commas in brackets are ignored as terminated with bracket
     * nesting understood gracefully. If the returned string would
     * being and end with a bracket then strip off the brackets.
     * <P>
     * Given a string like "(A,3(B,C),D),X,Y)" return "A,3(B,C),D".
     * Give a string like "3A,2C" return "3A".
     */
    protected String extractSubstring(final String pszSrc) {
        int nBracket = 0;
        int i;
        String pszReturn;

        for (i = 0; i < pszSrc.length()
                && (nBracket > 0 || pszSrc.charAt(i) != ','); i++) {
            if (pszSrc.charAt(i) == '(') {
                nBracket++;
            } else if (pszSrc.charAt(i) == ')') {
                nBracket--;
            }
        }

        if (pszSrc.charAt(0) == '(') {
            pszReturn = pszSrc.substring(1, i - 2);
        } else {
            pszReturn = pszSrc.substring(0, i);
        }

        return pszReturn;
    }

    /**
     * Given a string that contains a coded size symbol, expand it
     * out.
     */
    protected String expandFormat(final String pszSrc) {
        final StringBuffer szDest = new StringBuffer();
        int iSrc = 0;
        int nRepeat = 0;

        while (iSrc < pszSrc.length()) {
            /*
             * This is presumably an extra level of brackets around
             * some binary stuff related to rescaning which we don't
             * care to do (see 6.4.3.3 of the standard. We just strip
             * off the extra layer of brackets
             */
            if ((iSrc == 0 || pszSrc.charAt(iSrc - 1) == ',')
                    && pszSrc.charAt(iSrc) == '(') {
                final String pszContents = extractSubstring(pszSrc + iSrc);
                final String pszExpandedContents = expandFormat(pszContents);

                szDest.append(pszExpandedContents);
                iSrc = iSrc + pszContents.length() + 2;

            } else if ((iSrc == 0 || pszSrc.charAt(iSrc - 1) == ',') /*
                                                                      * this
                                                                      * is a
                                                                      * repeated
                                                                      * subclause
                                                                      */
                    && Character.isDigit(pszSrc.charAt(iSrc))) {

                final int orig_iSrc = iSrc;

                // skip over repeat count.
                for (; Character.isDigit(pszSrc.charAt(iSrc)); iSrc++) {
                }
                final String nRepeatString = pszSrc.substring(orig_iSrc, iSrc);
                nRepeat = Integer.parseInt(nRepeatString);

                final String pszContents = extractSubstring(pszSrc.substring(iSrc));
                final String pszExpandedContents = expandFormat(pszContents);

                for (int i = 0; i < nRepeat; i++) {
                    szDest.append(pszExpandedContents);
                    if (i < nRepeat - 1) {
                        szDest.append(",");
                    }
                }

                if (iSrc == '(') {
                    iSrc += pszContents.length() + 2;
                } else {
                    iSrc += pszContents.length();
                }

            } else {
                szDest.append(pszSrc.charAt(iSrc++));
            }
        }

        return szDest.toString();
    }

    /**
     * This method parses the format string partially, and then
     * applies a subfield format string to each subfield object. It in
     * turn does final parsing of the subfield formats.
     */
    @SuppressWarnings("rawtypes")
		protected boolean applyFormats(final String _formatControls1) {
        String pszFormatList;
        Vector papszFormatItems;

        /* -------------------------------------------------------------------- */
        /* Verify that the format string is contained within brackets. */
        /* -------------------------------------------------------------------- */
        if (_formatControls1.length() < 2 || !_formatControls1.startsWith("(")
                || !_formatControls1.endsWith(")")) {

            Debug.error("DDFFieldDefinition: Format controls for " + pszTag
                    + " field missing brackets {" + _formatControls1
                    + "} : length = " + _formatControls1.length()
                    + ", starts with {" + _formatControls1.charAt(0)
                    + "}, ends with {"
                    + _formatControls1.charAt(_formatControls1.length() - 1)
                    + "}");

            return false;
        }

        /* -------------------------------------------------------------------- */
        /* Duplicate the string, and strip off the brackets. */
        /* -------------------------------------------------------------------- */

        pszFormatList = expandFormat(_formatControls1);

        if (Debug.debugging("iso8211")) {
            Debug.output("DDFFieldDefinition.applyFormats{" + _formatControls1
                    + "} expanded to {" + pszFormatList + "} ");
        }

        /* -------------------------------------------------------------------- */
        /* Tokenize based on commas. */
        /* -------------------------------------------------------------------- */
        papszFormatItems = PropUtils.parseMarkers(pszFormatList, ",");

        /* -------------------------------------------------------------------- */
        /* Apply the format items to subfields. */
        /* -------------------------------------------------------------------- */

        int iFormatItem = 0;
        for (final Iterator it = papszFormatItems.iterator(); it.hasNext(); iFormatItem++) {

            String pszPastPrefix = (String) it.next();

            int pppIndex = 0;
            // Skip over digits...
            for (; Character.isDigit(pszPastPrefix.charAt(pppIndex)); pppIndex++) {
            }
            pszPastPrefix = pszPastPrefix.substring(pppIndex);

            ///////////////////////////////////////////////////////////////
            // Did we get too many formats for the subfields created
            // by names? This may be legal by the 8211 specification,
            // but
            // isn't encountered in any formats we care about so we
            // just
            // blow.

            if (iFormatItem > paoSubfieldDefns.size()) {
                Debug.error("DDFFieldDefinition: Got more formats than subfields for field "
                        + pszTag);
                break;
            }

            if (!paoSubfieldDefns.elementAt(iFormatItem).setFormat(pszPastPrefix)) {
                Debug.output("DDFFieldDefinition had problem setting format for "
                        + pszPastPrefix);
                return false;
            }
        }

        /* -------------------------------------------------------------------- */
        /* Verify that we got enough formats, cleanup and return. */
        /* -------------------------------------------------------------------- */
        if (iFormatItem < paoSubfieldDefns.size()) {
            Debug.error("DDFFieldDefinition: Got fewer formats than subfields for field "
                    + pszTag
                    + " got ("
                    + iFormatItem
                    + ", should have "
                    + paoSubfieldDefns.size() + ")");
            return false;
        }

        /* -------------------------------------------------------------------- */
        /* If all the fields are fixed width, then we are fixed width */
        /* too. This is important for repeating fields. */
        /* -------------------------------------------------------------------- */
        nFixedWidth = 0;
        for (int i = 0; i < paoSubfieldDefns.size(); i++) {
            final DDFSubfieldDefinition ddfsd = paoSubfieldDefns.elementAt(i);
            if (ddfsd.getWidth() == 0) {
                nFixedWidth = 0;
                break;
            } else {
                nFixedWidth += ddfsd.getWidth();
            }
        }

        return true;
    }

    /**
     * Find a subfield definition by it's mnemonic tag.
     * 
     * @param pszMnemonic The name of the field.
     * 
     * @return The subfield pointer, or null if there isn't any such
     *         subfield.
     */
    public DDFSubfieldDefinition findSubfieldDefn(final String pszMnemonic) {
        if (paoSubfieldDefns != null) {
            for (final Iterator<DDFSubfieldDefinition> it = paoSubfieldDefns.iterator(); pszMnemonic != null
                    && it.hasNext();) {
                final DDFSubfieldDefinition ddfsd = it.next();
                if (pszMnemonic.equalsIgnoreCase(ddfsd.getName())) {
                    return ddfsd;
                }
            }
        }

        return null;
    }

    /**
     * Fetch a subfield by index.
     * 
     * @param i The index subfield index. (Between 0 and
     *        GetSubfieldCount()-1)
     * @return The subfield pointer, or null if the index is out of
     *         range.
     */
    public DDFSubfieldDefinition getSubfieldDefn(final int i) {
        if (paoSubfieldDefns == null || i < 0 || i >= paoSubfieldDefns.size()) {
            return null;
        }

        return paoSubfieldDefns.elementAt(i);
    }

    public static class DataStructCode {
        public final static DataStructCode ELEMENTARY = new DataStructCode('0', "elementary");
        public final static DataStructCode VECTOR = new DataStructCode('1', "vector");
        public final static DataStructCode ARRAY = new DataStructCode('2', "array");
        public final static DataStructCode CONCATENATED = new DataStructCode('3', "concatenated");

        char code = '0';
        String prettyName;

        public DataStructCode(final char structCode, final String name) {
            code = structCode;
            prettyName = name;
        }

        public char getCode() {
            return code;
        }

        public String toString() {
            return prettyName;
        }

        public static DataStructCode get(final char c) {
            if (c == CONCATENATED.getCode())
                return CONCATENATED;
            if (c == VECTOR.getCode())
                return VECTOR;
            if (c == ARRAY.getCode())
                return ARRAY;
            if (c == ELEMENTARY.getCode())
                return ELEMENTARY;

            if (Debug.debugging("iso8211")) {
                Debug.output("DDFFieldDefinition tested for unknown code: " + c);
            }
            return ELEMENTARY;
        }
    }

    public static class DataTypeCode {
        public final static DataTypeCode CHAR_STRING = new DataTypeCode('0', "character string");
        public final static DataTypeCode IMPLICIT_POINT = new DataTypeCode('1', "implicit point");
        public final static DataTypeCode EXPLICIT_POINT = new DataTypeCode('2', "explicit point");
        public final static DataTypeCode EXPLICIT_POINT_SCALED = new DataTypeCode('3', "explicit point scaled");
        public final static DataTypeCode CHAR_BIT_STRING = new DataTypeCode('4', "character bit string");
        public final static DataTypeCode BIT_STRING = new DataTypeCode('5', "bit string");
        public final static DataTypeCode MIXED_DATA_TYPE = new DataTypeCode('6', "mixed data type");

        char code = '0';
        String prettyName;

        public DataTypeCode(final char structCode, final String desc) {
            code = structCode;
            prettyName = desc;
        }

        public char getCode() {
            return code;
        }

        public String toString() {
            return prettyName;
        }

        public static DataTypeCode get(final char c) {
            if (c == IMPLICIT_POINT.getCode())
                return IMPLICIT_POINT;
            if (c == EXPLICIT_POINT.getCode())
                return EXPLICIT_POINT;
            if (c == EXPLICIT_POINT_SCALED.getCode())
                return EXPLICIT_POINT_SCALED;
            if (c == CHAR_BIT_STRING.getCode())
                return CHAR_BIT_STRING;
            if (c == BIT_STRING.getCode())
                return BIT_STRING;
            if (c == MIXED_DATA_TYPE.getCode())
                return MIXED_DATA_TYPE;
            if (c == CHAR_STRING.getCode())
                return CHAR_STRING;

            if (Debug.debugging("iso8211")) {
                Debug.output("DDFFieldDefinition tested for unknown data type code: "
                        + c);
            }
            return CHAR_STRING;
        }
    }
}