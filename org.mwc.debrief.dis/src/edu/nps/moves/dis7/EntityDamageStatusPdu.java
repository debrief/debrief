package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * shall be used to communicate detailed damage information sustained by an
 * entity regardless of the source of the damage Section 7.3.5 COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class EntityDamageStatusPdu extends WarfareFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Field shall identify the damaged entity (see 6.2.28), Section 7.3.4 COMPLETE
	 */
	protected EntityID damagedEntityID = new EntityID();

	/** Padding. */
	protected int padding1 = 0;

	/** Padding. */
	protected int padding2 = 0;

	/**
	 * field shall specify the number of Damage Description records, Section 7.3.5
	 */
	protected int numberOfDamageDescription = 0;

	/**
	 * Fields shall contain one or more Damage Description records (see 6.2.17) and
	 * may contain other Standard Variable records, Section 7.3.5
	 */
	protected List<DirectedEnergyDamage> damageDescriptionRecords = new ArrayList<DirectedEnergyDamage>();

	/** Constructor */
	public EntityDamageStatusPdu() {
		setPduType((short) 69);
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

		if (!(obj instanceof EntityDamageStatusPdu))
			return false;

		final EntityDamageStatusPdu rhs = (EntityDamageStatusPdu) obj;

		if (!(damagedEntityID.equals(rhs.damagedEntityID)))
			ivarsEqual = false;
		if (!(padding1 == rhs.padding1))
			ivarsEqual = false;
		if (!(padding2 == rhs.padding2))
			ivarsEqual = false;
		if (!(numberOfDamageDescription == rhs.numberOfDamageDescription))
			ivarsEqual = false;

		for (int idx = 0; idx < damageDescriptionRecords.size(); idx++) {
			if (!(damageDescriptionRecords.get(idx).equals(rhs.damageDescriptionRecords.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public EntityID getDamagedEntityID() {
		return damagedEntityID;
	}

	public List<DirectedEnergyDamage> getDamageDescriptionRecords() {
		return damageDescriptionRecords;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + damagedEntityID.getMarshalledSize(); // damagedEntityID
		marshalSize = marshalSize + 2; // padding1
		marshalSize = marshalSize + 2; // padding2
		marshalSize = marshalSize + 2; // numberOfDamageDescription
		for (int idx = 0; idx < damageDescriptionRecords.size(); idx++) {
			final DirectedEnergyDamage listElement = damageDescriptionRecords.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public int getNumberOfDamageDescription() {
		return damageDescriptionRecords.size();
	}

	public int getPadding1() {
		return padding1;
	}

	public int getPadding2() {
		return padding2;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			damagedEntityID.marshal(dos);
			dos.writeShort((short) padding1);
			dos.writeShort((short) padding2);
			dos.writeShort((short) damageDescriptionRecords.size());

			for (int idx = 0; idx < damageDescriptionRecords.size(); idx++) {
				final DirectedEnergyDamage aDirectedEnergyDamage = damageDescriptionRecords.get(idx);
				aDirectedEnergyDamage.marshal(dos);
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
		damagedEntityID.marshal(buff);
		buff.putShort((short) padding1);
		buff.putShort((short) padding2);
		buff.putShort((short) damageDescriptionRecords.size());

		for (int idx = 0; idx < damageDescriptionRecords.size(); idx++) {
			final DirectedEnergyDamage aDirectedEnergyDamage = damageDescriptionRecords.get(idx);
			aDirectedEnergyDamage.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setDamagedEntityID(final EntityID pDamagedEntityID) {
		damagedEntityID = pDamagedEntityID;
	}

	public void setDamageDescriptionRecords(final List<DirectedEnergyDamage> pDamageDescriptionRecords) {
		damageDescriptionRecords = pDamageDescriptionRecords;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfDamageDescription method will also be based on the actual list
	 * length rather than this value. The method is simply here for java bean
	 * completeness.
	 */
	public void setNumberOfDamageDescription(final int pNumberOfDamageDescription) {
		numberOfDamageDescription = pNumberOfDamageDescription;
	}

	public void setPadding1(final int pPadding1) {
		padding1 = pPadding1;
	}

	public void setPadding2(final int pPadding2) {
		padding2 = pPadding2;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			damagedEntityID.unmarshal(dis);
			padding1 = dis.readUnsignedShort();
			padding2 = dis.readUnsignedShort();
			numberOfDamageDescription = dis.readUnsignedShort();
			for (int idx = 0; idx < numberOfDamageDescription; idx++) {
				final DirectedEnergyDamage anX = new DirectedEnergyDamage();
				anX.unmarshal(dis);
				damageDescriptionRecords.add(anX);
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

		damagedEntityID.unmarshal(buff);
		padding1 = buff.getShort() & 0xFFFF;
		padding2 = buff.getShort() & 0xFFFF;
		numberOfDamageDescription = buff.getShort() & 0xFFFF;
		for (int idx = 0; idx < numberOfDamageDescription; idx++) {
			final DirectedEnergyDamage anX = new DirectedEnergyDamage();
			anX.unmarshal(buff);
			damageDescriptionRecords.add(anX);
		}

	} // end of unmarshal method
} // end of class
