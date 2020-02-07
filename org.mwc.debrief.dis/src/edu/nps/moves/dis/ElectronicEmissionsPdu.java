package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 5.3.7.1. Information about active electronic warfare (EW) emissions
 * and active EW countermeasures shall be communicated using an Electromagnetic
 * Emission PDU. COMPLETE (I think)
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class ElectronicEmissionsPdu extends DistributedEmissionsFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** ID of the entity emitting */
	protected EntityID emittingEntityID = new EntityID();

	/** ID of event */
	protected EventID eventID = new EventID();

	/**
	 * This field shall be used to indicate if the data in the PDU represents a
	 * state update or just data that has changed since issuance of the last
	 * Electromagnetic Emission PDU [relative to the identified entity and emission
	 * system(s)].
	 */
	protected short stateUpdateIndicator;

	/**
	 * This field shall specify the number of emission systems being described in
	 * the current PDU.
	 */
	protected short numberOfSystems;

	/** padding */
	protected int paddingForEmissionsPdu;

	/** Electronic emmissions systems */
	protected List<ElectronicEmissionSystemData> systems = new ArrayList<ElectronicEmissionSystemData>();

	/** Constructor */
	public ElectronicEmissionsPdu() {
		setPduType((short) 23);
		setPaddingForEmissionsPdu(0);
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

		if (!(obj instanceof ElectronicEmissionsPdu))
			return false;

		final ElectronicEmissionsPdu rhs = (ElectronicEmissionsPdu) obj;

		if (!(emittingEntityID.equals(rhs.emittingEntityID)))
			ivarsEqual = false;
		if (!(eventID.equals(rhs.eventID)))
			ivarsEqual = false;
		if (!(stateUpdateIndicator == rhs.stateUpdateIndicator))
			ivarsEqual = false;
		if (!(numberOfSystems == rhs.numberOfSystems))
			ivarsEqual = false;
		if (!(paddingForEmissionsPdu == rhs.paddingForEmissionsPdu))
			ivarsEqual = false;

		for (int idx = 0; idx < systems.size(); idx++) {
			if (!(systems.get(idx).equals(rhs.systems.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public EntityID getEmittingEntityID() {
		return emittingEntityID;
	}

	public EventID getEventID() {
		return eventID;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + emittingEntityID.getMarshalledSize(); // emittingEntityID
		marshalSize = marshalSize + eventID.getMarshalledSize(); // eventID
		marshalSize = marshalSize + 1; // stateUpdateIndicator
		marshalSize = marshalSize + 1; // numberOfSystems
		marshalSize = marshalSize + 2; // paddingForEmissionsPdu
		for (int idx = 0; idx < systems.size(); idx++) {
			final ElectronicEmissionSystemData listElement = systems.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public short getNumberOfSystems() {
		return (short) systems.size();
	}

	public int getPaddingForEmissionsPdu() {
		return paddingForEmissionsPdu;
	}

	public short getStateUpdateIndicator() {
		return stateUpdateIndicator;
	}

	public List<ElectronicEmissionSystemData> getSystems() {
		return systems;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			emittingEntityID.marshal(dos);
			eventID.marshal(dos);
			dos.writeByte((byte) stateUpdateIndicator);
			dos.writeByte((byte) systems.size());
			dos.writeShort((short) paddingForEmissionsPdu);

			for (int idx = 0; idx < systems.size(); idx++) {
				final ElectronicEmissionSystemData aElectronicEmissionSystemData = systems.get(idx);
				aElectronicEmissionSystemData.marshal(dos);
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
		emittingEntityID.marshal(buff);
		eventID.marshal(buff);
		buff.put((byte) stateUpdateIndicator);
		buff.put((byte) systems.size());
		buff.putShort((short) paddingForEmissionsPdu);

		for (int idx = 0; idx < systems.size(); idx++) {
			final ElectronicEmissionSystemData aElectronicEmissionSystemData = systems.get(idx);
			aElectronicEmissionSystemData.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setEmittingEntityID(final EntityID pEmittingEntityID) {
		emittingEntityID = pEmittingEntityID;
	}

	public void setEventID(final EventID pEventID) {
		eventID = pEventID;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The getnumberOfSystems
	 * method will also be based on the actual list length rather than this value.
	 * The method is simply here for java bean completeness.
	 */
	public void setNumberOfSystems(final short pNumberOfSystems) {
		numberOfSystems = pNumberOfSystems;
	}

	public void setPaddingForEmissionsPdu(final int pPaddingForEmissionsPdu) {
		paddingForEmissionsPdu = pPaddingForEmissionsPdu;
	}

	public void setStateUpdateIndicator(final short pStateUpdateIndicator) {
		stateUpdateIndicator = pStateUpdateIndicator;
	}

	public void setSystems(final List<ElectronicEmissionSystemData> pSystems) {
		systems = pSystems;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			emittingEntityID.unmarshal(dis);
			eventID.unmarshal(dis);
			stateUpdateIndicator = (short) dis.readUnsignedByte();
			numberOfSystems = (short) dis.readUnsignedByte();
			paddingForEmissionsPdu = dis.readUnsignedShort();
			for (int idx = 0; idx < numberOfSystems; idx++) {
				final ElectronicEmissionSystemData anX = new ElectronicEmissionSystemData();
				anX.unmarshal(dis);
				systems.add(anX);
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

		emittingEntityID.unmarshal(buff);
		eventID.unmarshal(buff);
		stateUpdateIndicator = (short) (buff.get() & 0xFF);
		numberOfSystems = (short) (buff.get() & 0xFF);
		paddingForEmissionsPdu = buff.getShort() & 0xFFFF;
		for (int idx = 0; idx < numberOfSystems; idx++) {
			final ElectronicEmissionSystemData anX = new ElectronicEmissionSystemData();
			anX.unmarshal(buff);
			systems.add(anX);
		}

	} // end of unmarshal method
} // end of class
