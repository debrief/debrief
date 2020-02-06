package edu.nps.moves.dis7;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Nonstatic information about a particular entity may be communicated by
 * issuing an Entity State Update PDU. Section 7.2.5. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class EntityStateUpdatePdu extends EntityInformationFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This field shall identify the entity issuing the PDU, and shall be
	 * represented by an Entity Identifier record (see 6.2.28).
	 */
	protected EntityID entityID = new EntityID();

	/** Padding */
	protected byte padding1;

	/**
	 * This field shall specify the number of variable parameters present. This
	 * field shall be represented by an 8-bit unsigned integer (see Annex I).
	 */
	protected short numberOfVariableParameters;

	/**
	 * This field shall specify an entity’s linear velocity. The coordinate system
	 * for an entity’s linear velocity depends on the dead reckoning algorithm used.
	 * This field shall be represented by a Linear Velocity Vector record [see
	 * 6.2.95 item c)]).
	 */
	protected Vector3Float entityLinearVelocity = new Vector3Float();

	/**
	 * This field shall specify an entity’s physical location in the simulated world
	 * and shall be represented by a World Coordinates record (see 6.2.97).
	 */
	protected Vector3Double entityLocation = new Vector3Double();

	/**
	 * This field shall specify an entity’s orientation and shall be represented by
	 * an Euler Angles record (see 6.2.33).
	 */
	protected EulerAngles entityOrientation = new EulerAngles();

	/**
	 * This field shall specify the dynamic changes to the entity’s appearance
	 * attributes. This field shall be represented by an Entity Appearance record
	 * (see 6.2.26).
	 */
	protected long entityAppearance;

	/**
	 * This field shall specify the parameter values for each Variable Parameter
	 * record that is included (see 6.2.93 and Annex I).
	 */
	protected List<VariableParameter> variableParameters = new ArrayList<VariableParameter>();

	/** Constructor */
	public EntityStateUpdatePdu() {
		setPduType((short) 67);
		setProtocolFamily((short) 1);
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

		if (!(obj instanceof EntityStateUpdatePdu))
			return false;

		final EntityStateUpdatePdu rhs = (EntityStateUpdatePdu) obj;

		if (!(entityID.equals(rhs.entityID)))
			ivarsEqual = false;
		if (!(padding1 == rhs.padding1))
			ivarsEqual = false;
		if (!(numberOfVariableParameters == rhs.numberOfVariableParameters))
			ivarsEqual = false;
		if (!(entityLinearVelocity.equals(rhs.entityLinearVelocity)))
			ivarsEqual = false;
		if (!(entityLocation.equals(rhs.entityLocation)))
			ivarsEqual = false;
		if (!(entityOrientation.equals(rhs.entityOrientation)))
			ivarsEqual = false;
		if (!(entityAppearance == rhs.entityAppearance))
			ivarsEqual = false;

		for (int idx = 0; idx < variableParameters.size(); idx++) {
			if (!(variableParameters.get(idx).equals(rhs.variableParameters.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public long getEntityAppearance() {
		return entityAppearance;
	}

	public EntityID getEntityID() {
		return entityID;
	}

	public Vector3Float getEntityLinearVelocity() {
		return entityLinearVelocity;
	}

	public Vector3Double getEntityLocation() {
		return entityLocation;
	}

	public EulerAngles getEntityOrientation() {
		return entityOrientation;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + entityID.getMarshalledSize(); // entityID
		marshalSize = marshalSize + 1; // padding1
		marshalSize = marshalSize + 1; // numberOfVariableParameters
		marshalSize = marshalSize + entityLinearVelocity.getMarshalledSize(); // entityLinearVelocity
		marshalSize = marshalSize + entityLocation.getMarshalledSize(); // entityLocation
		marshalSize = marshalSize + entityOrientation.getMarshalledSize(); // entityOrientation
		marshalSize = marshalSize + 4; // entityAppearance
		for (int idx = 0; idx < variableParameters.size(); idx++) {
			final VariableParameter listElement = variableParameters.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public short getNumberOfVariableParameters() {
		return (short) variableParameters.size();
	}

	public byte getPadding1() {
		return padding1;
	}

	public List<VariableParameter> getVariableParameters() {
		return variableParameters;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			entityID.marshal(dos);
			dos.writeByte(padding1);
			dos.writeByte((byte) variableParameters.size());
			entityLinearVelocity.marshal(dos);
			entityLocation.marshal(dos);
			entityOrientation.marshal(dos);
			dos.writeInt((int) entityAppearance);

			for (int idx = 0; idx < variableParameters.size(); idx++) {
				final VariableParameter aVariableParameter = variableParameters.get(idx);
				aVariableParameter.marshal(dos);
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
		entityID.marshal(buff);
		buff.put(padding1);
		buff.put((byte) variableParameters.size());
		entityLinearVelocity.marshal(buff);
		entityLocation.marshal(buff);
		entityOrientation.marshal(buff);
		buff.putInt((int) entityAppearance);

		for (int idx = 0; idx < variableParameters.size(); idx++) {
			final VariableParameter aVariableParameter = variableParameters.get(idx);
			aVariableParameter.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setEntityAppearance(final long pEntityAppearance) {
		entityAppearance = pEntityAppearance;
	}

	public void setEntityID(final EntityID pEntityID) {
		entityID = pEntityID;
	}

	public void setEntityLinearVelocity(final Vector3Float pEntityLinearVelocity) {
		entityLinearVelocity = pEntityLinearVelocity;
	}

	public void setEntityLocation(final Vector3Double pEntityLocation) {
		entityLocation = pEntityLocation;
	}

	public void setEntityOrientation(final EulerAngles pEntityOrientation) {
		entityOrientation = pEntityOrientation;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfVariableParameters method will also be based on the actual list
	 * length rather than this value. The method is simply here for java bean
	 * completeness.
	 */
	public void setNumberOfVariableParameters(final short pNumberOfVariableParameters) {
		numberOfVariableParameters = pNumberOfVariableParameters;
	}

	public void setPadding1(final byte pPadding1) {
		padding1 = pPadding1;
	}

	public void setVariableParameters(final List<VariableParameter> pVariableParameters) {
		variableParameters = pVariableParameters;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			entityID.unmarshal(dis);
			padding1 = dis.readByte();
			numberOfVariableParameters = (short) dis.readUnsignedByte();
			entityLinearVelocity.unmarshal(dis);
			entityLocation.unmarshal(dis);
			entityOrientation.unmarshal(dis);
			entityAppearance = dis.readInt();
			for (int idx = 0; idx < numberOfVariableParameters; idx++) {
				final VariableParameter anX = new VariableParameter();
				anX.unmarshal(dis);
				variableParameters.add(anX);
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

		entityID.unmarshal(buff);
		padding1 = buff.get();
		numberOfVariableParameters = (short) (buff.get() & 0xFF);
		entityLinearVelocity.unmarshal(buff);
		entityLocation.unmarshal(buff);
		entityOrientation.unmarshal(buff);
		entityAppearance = buff.getInt();
		for (int idx = 0; idx < numberOfVariableParameters; idx++) {
			final VariableParameter anX = new VariableParameter();
			anX.unmarshal(buff);
			variableParameters.add(anX);
		}

	} // end of unmarshal method
} // end of class
