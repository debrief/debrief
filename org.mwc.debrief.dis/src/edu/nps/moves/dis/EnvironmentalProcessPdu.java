package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 5.3.11.1: Information about environmental effects and processes. This
 * requires manual cleanup. the environmental record is variable, as is the
 * padding. UNFINISHED
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class EnvironmentalProcessPdu extends SyntheticEnvironmentFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Environmental process ID */
	protected EntityID environementalProcessID = new EntityID();

	/** Environment type */
	protected EntityType environmentType = new EntityType();

	/** model type */
	protected short modelType;

	/** Environment status */
	protected short environmentStatus;

	/** number of environment records */
	protected short numberOfEnvironmentRecords;

	/**
	 * PDU sequence number for the environmentla process if pdu sequencing required
	 */
	protected int sequenceNumber;

	/** environemt records */
	protected List<Environment> environmentRecords = new ArrayList<Environment>();

	/** Constructor */
	public EnvironmentalProcessPdu() {
		setPduType((short) 41);
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

	@Override
	public boolean equalsImpl(final Object obj) {
		boolean ivarsEqual = true;

		if (!(obj instanceof EnvironmentalProcessPdu))
			return false;

		final EnvironmentalProcessPdu rhs = (EnvironmentalProcessPdu) obj;

		if (!(environementalProcessID.equals(rhs.environementalProcessID)))
			ivarsEqual = false;
		if (!(environmentType.equals(rhs.environmentType)))
			ivarsEqual = false;
		if (!(modelType == rhs.modelType))
			ivarsEqual = false;
		if (!(environmentStatus == rhs.environmentStatus))
			ivarsEqual = false;
		if (!(numberOfEnvironmentRecords == rhs.numberOfEnvironmentRecords))
			ivarsEqual = false;
		if (!(sequenceNumber == rhs.sequenceNumber))
			ivarsEqual = false;

		for (int idx = 0; idx < environmentRecords.size(); idx++) {
			if (!(environmentRecords.get(idx).equals(rhs.environmentRecords.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public EntityID getEnvironementalProcessID() {
		return environementalProcessID;
	}

	public List<Environment> getEnvironmentRecords() {
		return environmentRecords;
	}

	public short getEnvironmentStatus() {
		return environmentStatus;
	}

	public EntityType getEnvironmentType() {
		return environmentType;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + environementalProcessID.getMarshalledSize(); // environementalProcessID
		marshalSize = marshalSize + environmentType.getMarshalledSize(); // environmentType
		marshalSize = marshalSize + 1; // modelType
		marshalSize = marshalSize + 1; // environmentStatus
		marshalSize = marshalSize + 1; // numberOfEnvironmentRecords
		marshalSize = marshalSize + 2; // sequenceNumber
		for (int idx = 0; idx < environmentRecords.size(); idx++) {
			final Environment listElement = environmentRecords.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public short getModelType() {
		return modelType;
	}

	public short getNumberOfEnvironmentRecords() {
		return (short) environmentRecords.size();
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			environementalProcessID.marshal(dos);
			environmentType.marshal(dos);
			dos.writeByte((byte) modelType);
			dos.writeByte((byte) environmentStatus);
			dos.writeByte((byte) environmentRecords.size());
			dos.writeShort((short) sequenceNumber);

			for (int idx = 0; idx < environmentRecords.size(); idx++) {
				final Environment aEnvironment = environmentRecords.get(idx);
				aEnvironment.marshal(dos);
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
	@Override
	public void marshal(final java.nio.ByteBuffer buff) {
		super.marshal(buff);
		environementalProcessID.marshal(buff);
		environmentType.marshal(buff);
		buff.put((byte) modelType);
		buff.put((byte) environmentStatus);
		buff.put((byte) environmentRecords.size());
		buff.putShort((short) sequenceNumber);

		for (int idx = 0; idx < environmentRecords.size(); idx++) {
			final Environment aEnvironment = environmentRecords.get(idx);
			aEnvironment.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setEnvironementalProcessID(final EntityID pEnvironementalProcessID) {
		environementalProcessID = pEnvironementalProcessID;
	}

	public void setEnvironmentRecords(final List<Environment> pEnvironmentRecords) {
		environmentRecords = pEnvironmentRecords;
	}

	public void setEnvironmentStatus(final short pEnvironmentStatus) {
		environmentStatus = pEnvironmentStatus;
	}

	public void setEnvironmentType(final EntityType pEnvironmentType) {
		environmentType = pEnvironmentType;
	}

	public void setModelType(final short pModelType) {
		modelType = pModelType;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfEnvironmentRecords method will also be based on the actual list
	 * length rather than this value. The method is simply here for java bean
	 * completeness.
	 */
	public void setNumberOfEnvironmentRecords(final short pNumberOfEnvironmentRecords) {
		numberOfEnvironmentRecords = pNumberOfEnvironmentRecords;
	}

	public void setSequenceNumber(final int pSequenceNumber) {
		sequenceNumber = pSequenceNumber;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			environementalProcessID.unmarshal(dis);
			environmentType.unmarshal(dis);
			modelType = (short) dis.readUnsignedByte();
			environmentStatus = (short) dis.readUnsignedByte();
			numberOfEnvironmentRecords = (short) dis.readUnsignedByte();
			sequenceNumber = dis.readUnsignedShort();
			for (int idx = 0; idx < numberOfEnvironmentRecords; idx++) {
				final Environment anX = new Environment();
				anX.unmarshal(dis);
				environmentRecords.add(anX);
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
	@Override
	public void unmarshal(final java.nio.ByteBuffer buff) {
		super.unmarshal(buff);

		environementalProcessID.unmarshal(buff);
		environmentType.unmarshal(buff);
		modelType = (short) (buff.get() & 0xFF);
		environmentStatus = (short) (buff.get() & 0xFF);
		numberOfEnvironmentRecords = (short) (buff.get() & 0xFF);
		sequenceNumber = buff.getShort() & 0xFFFF;
		for (int idx = 0; idx < numberOfEnvironmentRecords; idx++) {
			final Environment anX = new Environment();
			anX.unmarshal(buff);
			environmentRecords.add(anX);
		}

	} // end of unmarshal method
} // end of class
