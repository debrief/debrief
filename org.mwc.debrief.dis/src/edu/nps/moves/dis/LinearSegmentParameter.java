package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * 5.2.48: Linear segment parameters
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class LinearSegmentParameter extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** number of segments */
	protected short segmentNumber;

	/** segment appearance */
	protected SixByteChunk segmentAppearance = new SixByteChunk();

	/** location */
	protected Vector3Double location = new Vector3Double();

	/** orientation */
	protected Orientation orientation = new Orientation();

	/** segmentLength */
	protected int segmentLength;

	/** segmentWidth */
	protected int segmentWidth;

	/** segmentHeight */
	protected int segmentHeight;

	/** segment Depth */
	protected int segmentDepth;

	/** segment Depth */
	protected long pad1;

	/** Constructor */
	public LinearSegmentParameter() {
	}

	/*
	 * The equals method doesn't always work--mostly it works only on classes that
	 * consist only of primitives. Be careful.
	 */
	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass())
			return false;

		return equalsImpl(obj);
	}

	/**
	 * Compare all fields that contribute to the state, ignoring transient and
	 * static fields, for <code>this</code> and the supplied object
	 *
	 * @param obj the object to compare to
	 * @return true if the objects are equal, false otherwise.
	 */
	public boolean equalsImpl(final Object obj) {
		boolean ivarsEqual = true;

		if (!(obj instanceof LinearSegmentParameter))
			return false;

		final LinearSegmentParameter rhs = (LinearSegmentParameter) obj;

		if (!(segmentNumber == rhs.segmentNumber))
			ivarsEqual = false;
		if (!(segmentAppearance.equals(rhs.segmentAppearance)))
			ivarsEqual = false;
		if (!(location.equals(rhs.location)))
			ivarsEqual = false;
		if (!(orientation.equals(rhs.orientation)))
			ivarsEqual = false;
		if (!(segmentLength == rhs.segmentLength))
			ivarsEqual = false;
		if (!(segmentWidth == rhs.segmentWidth))
			ivarsEqual = false;
		if (!(segmentHeight == rhs.segmentHeight))
			ivarsEqual = false;
		if (!(segmentDepth == rhs.segmentDepth))
			ivarsEqual = false;
		if (!(pad1 == rhs.pad1))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public Vector3Double getLocation() {
		return location;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 1; // segmentNumber
		marshalSize = marshalSize + segmentAppearance.getMarshalledSize(); // segmentAppearance
		marshalSize = marshalSize + location.getMarshalledSize(); // location
		marshalSize = marshalSize + orientation.getMarshalledSize(); // orientation
		marshalSize = marshalSize + 2; // segmentLength
		marshalSize = marshalSize + 2; // segmentWidth
		marshalSize = marshalSize + 2; // segmentHeight
		marshalSize = marshalSize + 2; // segmentDepth
		marshalSize = marshalSize + 4; // pad1

		return marshalSize;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public long getPad1() {
		return pad1;
	}

	public SixByteChunk getSegmentAppearance() {
		return segmentAppearance;
	}

	public int getSegmentDepth() {
		return segmentDepth;
	}

	public int getSegmentHeight() {
		return segmentHeight;
	}

	public int getSegmentLength() {
		return segmentLength;
	}

	public short getSegmentNumber() {
		return segmentNumber;
	}

	public int getSegmentWidth() {
		return segmentWidth;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeByte((byte) segmentNumber);
			segmentAppearance.marshal(dos);
			location.marshal(dos);
			orientation.marshal(dos);
			dos.writeShort((short) segmentLength);
			dos.writeShort((short) segmentWidth);
			dos.writeShort((short) segmentHeight);
			dos.writeShort((short) segmentDepth);
			dos.writeInt((int) pad1);
		} // end try
		catch (final Exception e) {
			System.out.println(e);
		}
	} // end of marshal method

	/**
	 * Packs a Pdu into the ByteBuffer.
	 *
	 * @throws java.nio.BufferOverflowException if buff is too small
	 * @throws java.nio.ReadOnlyBufferException if buff is read only
	 * @see java.nio.ByteBuffer
	 * @param buff The ByteBuffer at the position to begin writing
	 * @since ??
	 */
	public void marshal(final java.nio.ByteBuffer buff) {
		buff.put((byte) segmentNumber);
		segmentAppearance.marshal(buff);
		location.marshal(buff);
		orientation.marshal(buff);
		buff.putShort((short) segmentLength);
		buff.putShort((short) segmentWidth);
		buff.putShort((short) segmentHeight);
		buff.putShort((short) segmentDepth);
		buff.putInt((int) pad1);
	} // end of marshal method

	public void setLocation(final Vector3Double pLocation) {
		location = pLocation;
	}

	public void setOrientation(final Orientation pOrientation) {
		orientation = pOrientation;
	}

	public void setPad1(final long pPad1) {
		pad1 = pPad1;
	}

	public void setSegmentAppearance(final SixByteChunk pSegmentAppearance) {
		segmentAppearance = pSegmentAppearance;
	}

	public void setSegmentDepth(final int pSegmentDepth) {
		segmentDepth = pSegmentDepth;
	}

	public void setSegmentHeight(final int pSegmentHeight) {
		segmentHeight = pSegmentHeight;
	}

	public void setSegmentLength(final int pSegmentLength) {
		segmentLength = pSegmentLength;
	}

	public void setSegmentNumber(final short pSegmentNumber) {
		segmentNumber = pSegmentNumber;
	}

	public void setSegmentWidth(final int pSegmentWidth) {
		segmentWidth = pSegmentWidth;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			segmentNumber = (short) dis.readUnsignedByte();
			segmentAppearance.unmarshal(dis);
			location.unmarshal(dis);
			orientation.unmarshal(dis);
			segmentLength = dis.readUnsignedShort();
			segmentWidth = dis.readUnsignedShort();
			segmentHeight = dis.readUnsignedShort();
			segmentDepth = dis.readUnsignedShort();
			pad1 = dis.readInt();
		} // end try
		catch (final Exception e) {
			System.out.println(e);
		}
	} // end of unmarshal method

	/**
	 * Unpacks a Pdu from the underlying data.
	 *
	 * @throws java.nio.BufferUnderflowException if buff is too small
	 * @see java.nio.ByteBuffer
	 * @param buff The ByteBuffer at the position to begin reading
	 * @since ??
	 */
	public void unmarshal(final java.nio.ByteBuffer buff) {
		segmentNumber = (short) (buff.get() & 0xFF);
		segmentAppearance.unmarshal(buff);
		location.unmarshal(buff);
		orientation.unmarshal(buff);
		segmentLength = buff.getShort() & 0xFFFF;
		segmentWidth = buff.getShort() & 0xFFFF;
		segmentHeight = buff.getShort() & 0xFFFF;
		segmentDepth = buff.getShort() & 0xFFFF;
		pad1 = buff.getInt();
	} // end of unmarshal method
} // end of class
