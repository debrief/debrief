package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * 5.2.42. Basic operational data ofr IFF ATC NAVAIDS
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class IffFundamentalData extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** system status */
	protected short systemStatus;

	/** Alternate parameter 4 */
	protected short alternateParameter4;

	/** eight boolean fields */
	protected short informationLayers;

	/** enumeration */
	protected short modifier;

	/** parameter, enumeration */
	protected int parameter1;

	/** parameter, enumeration */
	protected int parameter2;

	/** parameter, enumeration */
	protected int parameter3;

	/** parameter, enumeration */
	protected int parameter4;

	/** parameter, enumeration */
	protected int parameter5;

	/** parameter, enumeration */
	protected int parameter6;

	/** Constructor */
	public IffFundamentalData() {
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

		if (!(obj instanceof IffFundamentalData))
			return false;

		final IffFundamentalData rhs = (IffFundamentalData) obj;

		if (!(systemStatus == rhs.systemStatus))
			ivarsEqual = false;
		if (!(alternateParameter4 == rhs.alternateParameter4))
			ivarsEqual = false;
		if (!(informationLayers == rhs.informationLayers))
			ivarsEqual = false;
		if (!(modifier == rhs.modifier))
			ivarsEqual = false;
		if (!(parameter1 == rhs.parameter1))
			ivarsEqual = false;
		if (!(parameter2 == rhs.parameter2))
			ivarsEqual = false;
		if (!(parameter3 == rhs.parameter3))
			ivarsEqual = false;
		if (!(parameter4 == rhs.parameter4))
			ivarsEqual = false;
		if (!(parameter5 == rhs.parameter5))
			ivarsEqual = false;
		if (!(parameter6 == rhs.parameter6))
			ivarsEqual = false;

		return ivarsEqual;
	}

	public short getAlternateParameter4() {
		return alternateParameter4;
	}

	public short getInformationLayers() {
		return informationLayers;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 1; // systemStatus
		marshalSize = marshalSize + 1; // alternateParameter4
		marshalSize = marshalSize + 1; // informationLayers
		marshalSize = marshalSize + 1; // modifier
		marshalSize = marshalSize + 2; // parameter1
		marshalSize = marshalSize + 2; // parameter2
		marshalSize = marshalSize + 2; // parameter3
		marshalSize = marshalSize + 2; // parameter4
		marshalSize = marshalSize + 2; // parameter5
		marshalSize = marshalSize + 2; // parameter6

		return marshalSize;
	}

	public short getModifier() {
		return modifier;
	}

	public int getParameter1() {
		return parameter1;
	}

	public int getParameter2() {
		return parameter2;
	}

	public int getParameter3() {
		return parameter3;
	}

	public int getParameter4() {
		return parameter4;
	}

	public int getParameter5() {
		return parameter5;
	}

	public int getParameter6() {
		return parameter6;
	}

	public short getSystemStatus() {
		return systemStatus;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeByte((byte) systemStatus);
			dos.writeByte((byte) alternateParameter4);
			dos.writeByte((byte) informationLayers);
			dos.writeByte((byte) modifier);
			dos.writeShort((short) parameter1);
			dos.writeShort((short) parameter2);
			dos.writeShort((short) parameter3);
			dos.writeShort((short) parameter4);
			dos.writeShort((short) parameter5);
			dos.writeShort((short) parameter6);
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
		buff.put((byte) systemStatus);
		buff.put((byte) alternateParameter4);
		buff.put((byte) informationLayers);
		buff.put((byte) modifier);
		buff.putShort((short) parameter1);
		buff.putShort((short) parameter2);
		buff.putShort((short) parameter3);
		buff.putShort((short) parameter4);
		buff.putShort((short) parameter5);
		buff.putShort((short) parameter6);
	} // end of marshal method

	public void setAlternateParameter4(final short pAlternateParameter4) {
		alternateParameter4 = pAlternateParameter4;
	}

	public void setInformationLayers(final short pInformationLayers) {
		informationLayers = pInformationLayers;
	}

	public void setModifier(final short pModifier) {
		modifier = pModifier;
	}

	public void setParameter1(final int pParameter1) {
		parameter1 = pParameter1;
	}

	public void setParameter2(final int pParameter2) {
		parameter2 = pParameter2;
	}

	public void setParameter3(final int pParameter3) {
		parameter3 = pParameter3;
	}

	public void setParameter4(final int pParameter4) {
		parameter4 = pParameter4;
	}

	public void setParameter5(final int pParameter5) {
		parameter5 = pParameter5;
	}

	public void setParameter6(final int pParameter6) {
		parameter6 = pParameter6;
	}

	public void setSystemStatus(final short pSystemStatus) {
		systemStatus = pSystemStatus;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			systemStatus = (short) dis.readUnsignedByte();
			alternateParameter4 = (short) dis.readUnsignedByte();
			informationLayers = (short) dis.readUnsignedByte();
			modifier = (short) dis.readUnsignedByte();
			parameter1 = dis.readUnsignedShort();
			parameter2 = dis.readUnsignedShort();
			parameter3 = dis.readUnsignedShort();
			parameter4 = dis.readUnsignedShort();
			parameter5 = dis.readUnsignedShort();
			parameter6 = dis.readUnsignedShort();
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
		systemStatus = (short) (buff.get() & 0xFF);
		alternateParameter4 = (short) (buff.get() & 0xFF);
		informationLayers = (short) (buff.get() & 0xFF);
		modifier = (short) (buff.get() & 0xFF);
		parameter1 = buff.getShort() & 0xFFFF;
		parameter2 = buff.getShort() & 0xFFFF;
		parameter3 = buff.getShort() & 0xFFFF;
		parameter4 = buff.getShort() & 0xFFFF;
		parameter5 = buff.getShort() & 0xFFFF;
		parameter6 = buff.getShort() & 0xFFFF;
	} // end of unmarshal method
} // end of class
