package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Data about one electronic system
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class ElectronicEmissionSystemData extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This field shall specify the length of this emitter system�s data (including
	 * beam data and its track/jam information) in 32-bit words. The length shall
	 * include the System Data Length field.
	 */
	protected short systemDataLength;

	/**
	 * This field shall specify the number of beams being described in the current
	 * PDU for the system being described.
	 */
	protected short numberOfBeams;

	/** padding. */
	protected int emissionsPadding2 = 0;

	/** This field shall specify information about a particular emitter system */
	protected EmitterSystem emitterSystem = new EmitterSystem();

	/** Location with respect to the entity */
	protected Vector3Float location = new Vector3Float();

	/** variable length variablelist of beam data records */
	protected List<ElectronicEmissionBeamData> beamDataRecords = new ArrayList<ElectronicEmissionBeamData>();

	/** Constructor */
	public ElectronicEmissionSystemData() {
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

		if (!(obj instanceof ElectronicEmissionSystemData))
			return false;

		final ElectronicEmissionSystemData rhs = (ElectronicEmissionSystemData) obj;

		if (!(systemDataLength == rhs.systemDataLength))
			ivarsEqual = false;
		if (!(numberOfBeams == rhs.numberOfBeams))
			ivarsEqual = false;
		if (!(emissionsPadding2 == rhs.emissionsPadding2))
			ivarsEqual = false;
		if (!(emitterSystem.equals(rhs.emitterSystem)))
			ivarsEqual = false;
		if (!(location.equals(rhs.location)))
			ivarsEqual = false;

		for (int idx = 0; idx < beamDataRecords.size(); idx++) {
			if (!(beamDataRecords.get(idx).equals(rhs.beamDataRecords.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual;
	}

	public List<ElectronicEmissionBeamData> getBeamDataRecords() {
		return beamDataRecords;
	}

	public int getEmissionsPadding2() {
		return emissionsPadding2;
	}

	public EmitterSystem getEmitterSystem() {
		return emitterSystem;
	}

	public Vector3Float getLocation() {
		return location;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 1; // systemDataLength
		marshalSize = marshalSize + 1; // numberOfBeams
		marshalSize = marshalSize + 2; // emissionsPadding2
		marshalSize = marshalSize + emitterSystem.getMarshalledSize(); // emitterSystem
		marshalSize = marshalSize + location.getMarshalledSize(); // location
		for (int idx = 0; idx < beamDataRecords.size(); idx++) {
			final ElectronicEmissionBeamData listElement = beamDataRecords.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public short getNumberOfBeams() {
		return (short) beamDataRecords.size();
	}

	public short getSystemDataLength() {
		return systemDataLength;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeByte((byte) systemDataLength);
			dos.writeByte((byte) beamDataRecords.size());
			dos.writeShort((short) emissionsPadding2);
			emitterSystem.marshal(dos);
			location.marshal(dos);

			for (int idx = 0; idx < beamDataRecords.size(); idx++) {
				final ElectronicEmissionBeamData aElectronicEmissionBeamData = beamDataRecords.get(idx);
				aElectronicEmissionBeamData.marshal(dos);
			} // end of list marshalling

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
		buff.put((byte) systemDataLength);
		buff.put((byte) beamDataRecords.size());
		buff.putShort((short) emissionsPadding2);
		emitterSystem.marshal(buff);
		location.marshal(buff);

		for (int idx = 0; idx < beamDataRecords.size(); idx++) {
			final ElectronicEmissionBeamData aElectronicEmissionBeamData = beamDataRecords.get(idx);
			aElectronicEmissionBeamData.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setBeamDataRecords(final List<ElectronicEmissionBeamData> pBeamDataRecords) {
		beamDataRecords = pBeamDataRecords;
	}

	public void setEmissionsPadding2(final int pEmissionsPadding2) {
		emissionsPadding2 = pEmissionsPadding2;
	}

	public void setEmitterSystem(final EmitterSystem pEmitterSystem) {
		emitterSystem = pEmitterSystem;
	}

	public void setLocation(final Vector3Float pLocation) {
		location = pLocation;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The getnumberOfBeams
	 * method will also be based on the actual list length rather than this value.
	 * The method is simply here for java bean completeness.
	 */
	public void setNumberOfBeams(final short pNumberOfBeams) {
		numberOfBeams = pNumberOfBeams;
	}

	public void setSystemDataLength(final short pSystemDataLength) {
		systemDataLength = pSystemDataLength;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			systemDataLength = (short) dis.readUnsignedByte();
			numberOfBeams = (short) dis.readUnsignedByte();
			emissionsPadding2 = dis.readUnsignedShort();
			emitterSystem.unmarshal(dis);
			location.unmarshal(dis);
			for (int idx = 0; idx < numberOfBeams; idx++) {
				final ElectronicEmissionBeamData anX = new ElectronicEmissionBeamData();
				anX.unmarshal(dis);
				beamDataRecords.add(anX);
			}

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
		systemDataLength = (short) (buff.get() & 0xFF);
		numberOfBeams = (short) (buff.get() & 0xFF);
		emissionsPadding2 = buff.getShort() & 0xFFFF;
		emitterSystem.unmarshal(buff);
		location.unmarshal(buff);
		for (int idx = 0; idx < numberOfBeams; idx++) {
			final ElectronicEmissionBeamData anX = new ElectronicEmissionBeamData();
			anX.unmarshal(buff);
			beamDataRecords.add(anX);
		}

	} // end of unmarshal method
} // end of class
