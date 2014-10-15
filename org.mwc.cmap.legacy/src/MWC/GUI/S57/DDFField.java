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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */

package MWC.GUI.S57;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.bbn.openmap.layer.vpf.MutableInt;
import com.bbn.openmap.util.Debug;

/**
 * This object represents one field in a DDFRecord. This models an
 * instance of the fields data, rather than it's data definition which
 * is handled by the DDFFieldDefn class. Note that a DDFField doesn't
 * have DDFSubfield children as you would expect. To extract subfield
 * values use GetSubfieldData() to find the right data pointer and
 * then use ExtractIntData(), ExtractFloatData() or
 * ExtractStringData().
 */
public class DDFField {

    protected DDFFieldDefinition poDefn;
    protected byte[] pachData;
    protected Hashtable<String, Object> subfields;
    protected int dataPosition;
    protected int dataLength;
    protected int headerOffset;

    public DDFField() {}

    public DDFField(final DDFFieldDefinition poDefnIn, final int dataPositionIn,
            final int dataLengthIn) {
        initialize(poDefnIn, null);
        dataPosition = dataPositionIn;
        dataLength = dataLengthIn;
    }

    public DDFField(final DDFFieldDefinition poDefnIn, final byte[] pachDataIn) {
        this(poDefnIn, pachDataIn, true);
    }

    public DDFField(final DDFFieldDefinition poDefnIn, final byte[] pachDataIn,
            final boolean doSubfields) {
        initialize(poDefnIn, pachDataIn);
        if (doSubfields) {
            buildSubfields();
        }
    }

    public void initialize(final DDFFieldDefinition poDefnIn, final byte[] pachDataIn) {
        pachData = pachDataIn;
        poDefn = poDefnIn;
        subfields = new Hashtable<String, Object>();
    }

    /**
     * Set how many bytes to add to the data position for absolute
     * position in the data file for the field data.
     */
    protected void setHeaderOffset(final int headerOffsetIn) {
        headerOffset = headerOffsetIn;
    }

    /**
     * Get how many bytes to add to the data position for absolute
     * position in the data file for the field data.
     */
    public int getHeaderOffset() {
        return headerOffset;
    }

    /**
     * Return the pointer to the entire data block for this record.
     * This is an internal copy, and shouldn't be freed by the
     * application. If null, then check the dataPosition and
     * daataLength for byte offsets for the data in the file, and go
     * get it yourself. This is done for really large files where it
     * doesn't make sense to load the data.
     */
    public byte[] getData() {
        return pachData;
    }

    /**
     * Return the number of bytes in the data block returned by
     * GetData().
     */
    public int getDataSize() {
        if (pachData != null) {
            return pachData.length;
        } else
            return 0;
    }

    /** Fetch the corresponding DDFFieldDefn. */
    public DDFFieldDefinition getFieldDefn() {
        return poDefn;
    }

    /**
     * If getData() returns null, it'll be your responsibilty to go
     * after the data you need for this field.
     * 
     * @return the byte offset into the source file to start reading
     *         this field.
     */
    public int getDataPosition() {
        return dataPosition;
    }

    /**
     * If getData() returns null, it'll be your responsibilty to go
     * after the data you need for this field.
     * 
     * @return the number of bytes contained in the source file for
     *         this field.
     */
    public int getDataLength() {
        return dataLength;
    }

    /**
     * Creates a string with variety of information about this field,
     * and all it's subfields is written to the given debugging file
     * handle. Note that field definition information (ala
     * DDFFieldDefn) isn't written.
     * 
     * @return String containing info.
     */
    @SuppressWarnings("unchecked")
		public String toString() {
        final StringBuffer buf = new StringBuffer("  DDFField:\n");
        buf.append("\tTag = " + poDefn.getName() + "\n");
        buf.append("\tDescription = " + poDefn.getDescription() + "\n");
        final int size = getDataSize();
        buf.append("\tDataSize = " + size + "\n");

        if (pachData == null) {
            buf.append("\tHeader offset = " + headerOffset + "\n");
            buf.append("\tData position = " + dataPosition + "\n");
            buf.append("\tData length = " + dataLength + "\n");
            return buf.toString();
        }

        buf.append("\tData = ");
        for (int i = 0; i < Math.min(size, 40); i++) {
            if (pachData[i] < 32 || pachData[i] > 126) {
                buf.append(" | " + (char) pachData[i]);
            } else {
                buf.append(pachData[i]);
            }
        }

        if (size > 40)
            buf.append("...");
        buf.append("\n");

        /* -------------------------------------------------------------------- */
        /* dump the data of the subfields. */
        /* -------------------------------------------------------------------- */
        if (Debug.debugging("iso8211.raw")) {
            int iOffset = 0;
            final MutableInt nBytesConsumed = new MutableInt(0);

            for (int nLoopCount = 0; nLoopCount < getRepeatCount(); nLoopCount++) {
                if (nLoopCount > 8) {
                    buf.append("      ...\n");
                    break;
                }

                for (int i = 0; i < poDefn.getSubfieldCount(); i++) {
                    final byte[] subPachData = new byte[pachData.length - iOffset];
                    System.arraycopy(pachData,
                            iOffset,
                            subPachData,
                            0,
                            subPachData.length);

                    buf.append(poDefn.getSubfieldDefn(i).dumpData(subPachData,
                            subPachData.length));

                    poDefn.getSubfieldDefn(i).getDataLength(subPachData,
                            subPachData.length,
                            nBytesConsumed);
                    iOffset += nBytesConsumed.value;
                }
            }
        } else {
            buf.append("      Subfields:\n");

            for (final Enumeration<String> enumeration = subfields.keys(); enumeration.hasMoreElements();) {
                final Object obj = subfields.get(enumeration.nextElement());

                if (obj instanceof List) {
                	final List<DDFSubfield> theList = (List<DDFSubfield>) obj;
                    for (final Iterator<DDFSubfield> it = theList.iterator(); it.hasNext();) 
                    {
                        final DDFSubfield ddfs = it.next();
                        buf.append("        " + ddfs.toString() + "\n");
                    }
                } else {
                    buf.append("        " + obj.toString() + "\n");
                }
            }
        }

        return buf.toString();
    }

    /**
     * Will return an ordered list of DDFSubfield objects. If the
     * subfield wasn't repeated, it will provide a list containing one
     * object. Will return null if the subfield doesn't exist.
     */
    @SuppressWarnings({ "unchecked" })
		public List<Object> getSubfields(final String subfieldName) {
        final Object obj = subfields.get(subfieldName);
        if (obj instanceof List) {
            return (List<Object>) obj;
        } else if (obj != null) {
            final LinkedList<Object> ll = new LinkedList<Object>();
            ll.add(obj);
            return ll;
        }

        return null;
    }

    /**
     * Will return a DDFSubfield object with the given name, or the
     * first one off the list for a repeating subfield. Will return
     * null if the subfield doesn't exist.
     */
    @SuppressWarnings("rawtypes")
		public DDFSubfield getSubfield(final String subfieldName) {
        Object obj = subfields.get(subfieldName);
        if (obj instanceof List) {
            final List l = (List) obj;
            if (!l.isEmpty()) {
                return (DDFSubfield) (l.get(0));
            }
            obj = null;
        }

        // May be null if subfield list above is empty. Not sure if
        // that's possible.
        return (DDFSubfield) obj;
    }

    /**
     * Fetch raw data pointer for a particular subfield of this field.
     * 
     * The passed DDFSubfieldDefn (poSFDefn) should be acquired from
     * the DDFFieldDefn corresponding with this field. This is
     * normally done once before reading any records. This method
     * involves a series of calls to DDFSubfield::GetDataLength() in
     * order to track through the DDFField data to that belonging to
     * the requested subfield. This can be relatively expensive.
     * <p>
     * 
     * @param poSFDefn The definition of the subfield for which the
     *        raw data pointer is desired.
     * @param pnMaxBytes The maximum number of bytes that can be
     *        accessed from the returned data pointer is placed in
     *        this int, unless it is null.
     * @param iSubfieldIndex The instance of this subfield to fetch.
     *        Use zero (the default) for the first instance.
     * 
     * @return A pointer into the DDFField's data that belongs to the
     *         subfield. This returned pointer is invalidated by the
     *         next record read (DDFRecord::ReadRecord()) and the
     *         returned pointer should not be freed by the
     *         application.
     */
    public byte[] getSubfieldData(final DDFSubfieldDefinition poSFDefn,
                                  final MutableInt pnMaxBytes, final int iSubfieldIndex) {
        int iOffset = 0;
        int iSubfieldIdx = iSubfieldIndex;

        if (poSFDefn == null)
            return null;

        if (iSubfieldIdx > 0 && poDefn.getFixedWidth() > 0) {
            iOffset = poDefn.getFixedWidth() * iSubfieldIdx;
            iSubfieldIdx = 0;
        }

        final MutableInt nBytesConsumed = new MutableInt(0);
        while (iSubfieldIdx >= 0) {
            for (int iSF = 0; iSF < poDefn.getSubfieldCount(); iSF++) {
                final DDFSubfieldDefinition poThisSFDefn = poDefn.getSubfieldDefn(iSF);

                final byte[] subPachData = new byte[pachData.length - iOffset];
                System.arraycopy(pachData,
                        iOffset,
                        subPachData,
                        0,
                        subPachData.length);

                if (poThisSFDefn == poSFDefn && iSubfieldIdx == 0) {

                    if (pnMaxBytes != null) {
                        pnMaxBytes.value = pachData.length - iOffset;
                    }

                    return subPachData;
                }

                poThisSFDefn.getDataLength(subPachData,
                        subPachData.length,
                        nBytesConsumed);

                iOffset += nBytesConsumed.value;
            }

            iSubfieldIdx--;
        }

        // We didn't find our target subfield or instance!
        return null;
    }

    public void buildSubfields() {
        byte[] pachFieldData = pachData;
        int nBytesRemaining = pachData.length;

        for (int iRepeat = 0; iRepeat < getRepeatCount(); iRepeat++) {

            /* -------------------------------------------------------- */
            /* Loop over all the subfields of this field, advancing */
            /* the data pointer as we consume data. */
            /* -------------------------------------------------------- */
            for (int iSF = 0; iSF < poDefn.getSubfieldCount(); iSF++) {

                final DDFSubfield ddfs = new DDFSubfield(poDefn.getSubfieldDefn(iSF), pachFieldData, nBytesRemaining);

                addSubfield(ddfs);

                // Reset data for next subfield;
                final int nBytesConsumed = ddfs.getByteSize();
                nBytesRemaining -= nBytesConsumed;
                final byte[] tempData = new byte[pachFieldData.length
                        - nBytesConsumed];
                System.arraycopy(pachFieldData,
                        nBytesConsumed,
                        tempData,
                        0,
                        tempData.length);
                pachFieldData = tempData;
            }
        }

    }

    @SuppressWarnings("unchecked")
		protected void addSubfield(final DDFSubfield ddfs) {
        if (Debug.debugging("iso8211")) {
            Debug.output("DDFField(" + getFieldDefn().getName()
                    + ").addSubfield(" + ddfs + ")");
        }

        final String sfName = ddfs.getDefn().getName().trim().intern();
        final Object sf = subfields.get(sfName);
        if (sf == null) {
            subfields.put(sfName, ddfs);
        } else {
            if (sf instanceof List) {
                ((List<DDFSubfield>) sf).add(ddfs);
            } else {
                final Vector<Object> subList = new Vector<Object>();
                subList.add(sf);
                subList.add(ddfs);
                subfields.put(sfName, subList);
            }
        }
    }

    /**
     * How many times do the subfields of this record repeat? This
     * will always be one for non-repeating fields.
     * 
     * @return The number of times that the subfields of this record
     *         occur in this record. This will be one for
     *         non-repeating fields.
     */
    public int getRepeatCount() {
        if (!poDefn.isRepeating()) {
            return 1;
        }

        /* -------------------------------------------------------------------- */
        /* The occurance count depends on how many copies of this */
        /* field's list of subfields can fit into the data space. */
        /* -------------------------------------------------------------------- */
        if (poDefn.getFixedWidth() != 0) {
            return pachData.length / poDefn.getFixedWidth();
        }

        /* -------------------------------------------------------------------- */
        /* Note that it may be legal to have repeating variable width */
        /* subfields, but I don't have any samples, so I ignore it for */
        /* now. */
        /*                                                                      */
        /*
         * The file data/cape_royal_AZ_DEM/1183XREF.DDF has a
         * repeating
         */
        /* variable length field, but the count is one, so it isn't */
        /* much value for testing. */
        /* -------------------------------------------------------------------- */
        int iOffset = 0;
        int iRepeatCount = 1;
        final MutableInt nBytesConsumed = new MutableInt(0);

        while (true) {
            for (int iSF = 0; iSF < poDefn.getSubfieldCount(); iSF++) {
                final DDFSubfieldDefinition poThisSFDefn = poDefn.getSubfieldDefn(iSF);

                if (poThisSFDefn.getWidth() > pachData.length - iOffset) {
                    nBytesConsumed.value = poThisSFDefn.getWidth();
                } else {
                    final byte[] tempData = new byte[pachData.length - iOffset];
                    System.arraycopy(pachData,
                            iOffset,
                            tempData,
                            0,
                            tempData.length);
                    poThisSFDefn.getDataLength(tempData,
                            tempData.length,
                            nBytesConsumed);
                }

                iOffset += nBytesConsumed.value;
                if (iOffset > pachData.length) {
                    return iRepeatCount - 1;
                }
            }

            if (iOffset > pachData.length - 2)
                return iRepeatCount;

            iRepeatCount++;
        }
    }
}

