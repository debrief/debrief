package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Section 5.3.7.2. Handles designating operations. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class DesignatorPdu extends DistributedEmissionsFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** ID of the entity designating */
	protected EntityID designatingEntityID = new EntityID();

	/**
	 * This field shall specify a unique emitter database number assigned to
	 * differentiate between otherwise similar or identical emitter beams within an
	 * emitter system.
	 */
	protected int codeName;

	/** ID of the entity being designated */
	protected EntityID designatedEntityID = new EntityID();

	/**
	 * This field shall identify the designator code being used by the designating
	 * entity
	 */
	protected int designatorCode;

	/** This field shall identify the designator output power in watts */
	protected float designatorPower;

	/** This field shall identify the designator wavelength in units of microns */
	protected float designatorWavelength;

	/** designtor spot wrt the designated entity */
	protected Vector3Float designatorSpotWrtDesignated = new Vector3Float();

	/** designtor spot wrt the designated entity */
	protected Vector3Double designatorSpotLocation = new Vector3Double();

	/** Dead reckoning algorithm */
	protected byte deadReckoningAlgorithm;

	/** padding */
	protected int padding1;

	/** padding */
	protected byte padding2;

	/** linear accelleration of entity */
	protected Vector3Float entityLinearAcceleration = new Vector3Float();

	/** Constructor */
	public DesignatorPdu() {
		setPduType((short) 24);
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

		if (!(obj instanceof DesignatorPdu))
			return false;

		final DesignatorPdu rhs = (DesignatorPdu) obj;

		if (!(designatingEntityID.equals(rhs.designatingEntityID)))
			ivarsEqual = false;
		if (!(codeName == rhs.codeName))
			ivarsEqual = false;
		if (!(designatedEntityID.equals(rhs.designatedEntityID)))
			ivarsEqual = false;
		if (!(designatorCode == rhs.designatorCode))
			ivarsEqual = false;
		if (!(designatorPower == rhs.designatorPower))
			ivarsEqual = false;
		if (!(designatorWavelength == rhs.designatorWavelength))
			ivarsEqual = false;
		if (!(designatorSpotWrtDesignated.equals(rhs.designatorSpotWrtDesignated)))
			ivarsEqual = false;
		if (!(designatorSpotLocation.equals(rhs.designatorSpotLocation)))
			ivarsEqual = false;
		if (!(deadReckoningAlgorithm == rhs.deadReckoningAlgorithm))
			ivarsEqual = false;
		if (!(padding1 == rhs.padding1))
			ivarsEqual = false;
		if (!(padding2 == rhs.padding2))
			ivarsEqual = false;
		if (!(entityLinearAcceleration.equals(rhs.entityLinearAcceleration)))
			ivarsEqual = false;

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public int getCodeName() {
		return codeName;
	}

	public byte getDeadReckoningAlgorithm() {
		return deadReckoningAlgorithm;
	}

	public EntityID getDesignatedEntityID() {
		return designatedEntityID;
	}

	public EntityID getDesignatingEntityID() {
		return designatingEntityID;
	}

	public int getDesignatorCode() {
		return designatorCode;
	}

	public float getDesignatorPower() {
		return designatorPower;
	}

	public Vector3Double getDesignatorSpotLocation() {
		return designatorSpotLocation;
	}

	public Vector3Float getDesignatorSpotWrtDesignated() {
		return designatorSpotWrtDesignated;
	}

	public float getDesignatorWavelength() {
		return designatorWavelength;
	}

	public Vector3Float getEntityLinearAcceleration() {
		return entityLinearAcceleration;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + designatingEntityID.getMarshalledSize(); // designatingEntityID
		marshalSize = marshalSize + 2; // codeName
		marshalSize = marshalSize + designatedEntityID.getMarshalledSize(); // designatedEntityID
		marshalSize = marshalSize + 2; // designatorCode
		marshalSize = marshalSize + 4; // designatorPower
		marshalSize = marshalSize + 4; // designatorWavelength
		marshalSize = marshalSize + designatorSpotWrtDesignated.getMarshalledSize(); // designatorSpotWrtDesignated
		marshalSize = marshalSize + designatorSpotLocation.getMarshalledSize(); // designatorSpotLocation
		marshalSize = marshalSize + 1; // deadReckoningAlgorithm
		marshalSize = marshalSize + 2; // padding1
		marshalSize = marshalSize + 1; // padding2
		marshalSize = marshalSize + entityLinearAcceleration.getMarshalledSize(); // entityLinearAcceleration

		return marshalSize;
	}

	public int getPadding1() {
		return padding1;
	}

	public byte getPadding2() {
		return padding2;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			designatingEntityID.marshal(dos);
			dos.writeShort((short) codeName);
			designatedEntityID.marshal(dos);
			dos.writeShort((short) designatorCode);
			dos.writeFloat(designatorPower);
			dos.writeFloat(designatorWavelength);
			designatorSpotWrtDesignated.marshal(dos);
			designatorSpotLocation.marshal(dos);
			dos.writeByte(deadReckoningAlgorithm);
			dos.writeShort((short) padding1);
			dos.writeByte(padding2);
			entityLinearAcceleration.marshal(dos);
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
		designatingEntityID.marshal(buff);
		buff.putShort((short) codeName);
		designatedEntityID.marshal(buff);
		buff.putShort((short) designatorCode);
		buff.putFloat(designatorPower);
		buff.putFloat(designatorWavelength);
		designatorSpotWrtDesignated.marshal(buff);
		designatorSpotLocation.marshal(buff);
		buff.put(deadReckoningAlgorithm);
		buff.putShort((short) padding1);
		buff.put(padding2);
		entityLinearAcceleration.marshal(buff);
	} // end of marshal method

	public void setCodeName(final int pCodeName) {
		codeName = pCodeName;
	}

	public void setDeadReckoningAlgorithm(final byte pDeadReckoningAlgorithm) {
		deadReckoningAlgorithm = pDeadReckoningAlgorithm;
	}

	public void setDesignatedEntityID(final EntityID pDesignatedEntityID) {
		designatedEntityID = pDesignatedEntityID;
	}

	public void setDesignatingEntityID(final EntityID pDesignatingEntityID) {
		designatingEntityID = pDesignatingEntityID;
	}

	public void setDesignatorCode(final int pDesignatorCode) {
		designatorCode = pDesignatorCode;
	}

	public void setDesignatorPower(final float pDesignatorPower) {
		designatorPower = pDesignatorPower;
	}

	public void setDesignatorSpotLocation(final Vector3Double pDesignatorSpotLocation) {
		designatorSpotLocation = pDesignatorSpotLocation;
	}

	public void setDesignatorSpotWrtDesignated(final Vector3Float pDesignatorSpotWrtDesignated) {
		designatorSpotWrtDesignated = pDesignatorSpotWrtDesignated;
	}

	public void setDesignatorWavelength(final float pDesignatorWavelength) {
		designatorWavelength = pDesignatorWavelength;
	}

	public void setEntityLinearAcceleration(final Vector3Float pEntityLinearAcceleration) {
		entityLinearAcceleration = pEntityLinearAcceleration;
	}

	public void setPadding1(final int pPadding1) {
		padding1 = pPadding1;
	}

	public void setPadding2(final byte pPadding2) {
		padding2 = pPadding2;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			designatingEntityID.unmarshal(dis);
			codeName = dis.readUnsignedShort();
			designatedEntityID.unmarshal(dis);
			designatorCode = dis.readUnsignedShort();
			designatorPower = dis.readFloat();
			designatorWavelength = dis.readFloat();
			designatorSpotWrtDesignated.unmarshal(dis);
			designatorSpotLocation.unmarshal(dis);
			deadReckoningAlgorithm = dis.readByte();
			padding1 = dis.readUnsignedShort();
			padding2 = dis.readByte();
			entityLinearAcceleration.unmarshal(dis);
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

		designatingEntityID.unmarshal(buff);
		codeName = buff.getShort() & 0xFFFF;
		designatedEntityID.unmarshal(buff);
		designatorCode = buff.getShort() & 0xFFFF;
		designatorPower = buff.getFloat();
		designatorWavelength = buff.getFloat();
		designatorSpotWrtDesignated.unmarshal(buff);
		designatorSpotLocation.unmarshal(buff);
		deadReckoningAlgorithm = buff.get();
		padding1 = buff.getShort() & 0xFFFF;
		padding2 = buff.get();
		entityLinearAcceleration.unmarshal(buff);
	} // end of unmarshal method
} // end of class
