package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 5.3.3.1. Represents the postion and state of one entity in the world.
 * COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class EntityStatePdu extends EntityInformationFamilyPdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** Unique ID for an entity that is tied to this state information */
	protected EntityID entityID = new EntityID();

	/** What force this entity is affiliated with, eg red, blue, neutral, etc */
	protected short forceId;

	/** How many articulation parameters are in the variable length list */
	protected byte numberOfArticulationParameters;

	/** Describes the type of entity in the world */
	protected EntityType entityType = new EntityType();

	protected EntityType alternativeEntityType = new EntityType();

	/** Describes the speed of the entity in the world */
	protected Vector3Float entityLinearVelocity = new Vector3Float();

	/** describes the location of the entity in the world */
	protected Vector3Double entityLocation = new Vector3Double();

	/** describes the orientation of the entity, in euler angles */
	protected Orientation entityOrientation = new Orientation();

	/**
	 * a series of bit flags that are used to help draw the entity, such as smoking,
	 * on fire, etc.
	 */
	protected int entityAppearance;

	/** parameters used for dead reckoning */
	protected DeadReckoningParameter deadReckoningParameters = new DeadReckoningParameter();

	/**
	 * characters that can be used for debugging, or to draw unique strings on the
	 * side of entities in the world
	 */
	protected Marking marking = new Marking();

	/** a series of bit flags */
	protected int capabilities;

	/** variable length list of articulation parameters */
	protected List<ArticulationParameter> articulationParameters = new ArrayList<ArticulationParameter>();

	/** Constructor */
	public EntityStatePdu() {
		setPduType((short) 1);
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

		if (!(obj instanceof EntityStatePdu))
			return false;

		final EntityStatePdu rhs = (EntityStatePdu) obj;

		if (!(entityID.equals(rhs.entityID)))
			ivarsEqual = false;
		if (!(forceId == rhs.forceId))
			ivarsEqual = false;
		if (!(numberOfArticulationParameters == rhs.numberOfArticulationParameters))
			ivarsEqual = false;
		if (!(entityType.equals(rhs.entityType)))
			ivarsEqual = false;
		if (!(alternativeEntityType.equals(rhs.alternativeEntityType)))
			ivarsEqual = false;
		if (!(entityLinearVelocity.equals(rhs.entityLinearVelocity)))
			ivarsEqual = false;
		if (!(entityLocation.equals(rhs.entityLocation)))
			ivarsEqual = false;
		if (!(entityOrientation.equals(rhs.entityOrientation)))
			ivarsEqual = false;
		if (!(entityAppearance == rhs.entityAppearance))
			ivarsEqual = false;
		if (!(deadReckoningParameters.equals(rhs.deadReckoningParameters)))
			ivarsEqual = false;
		if (!(marking.equals(rhs.marking)))
			ivarsEqual = false;
		if (!(capabilities == rhs.capabilities))
			ivarsEqual = false;

		for (int idx = 0; idx < articulationParameters.size(); idx++) {
			if (!(articulationParameters.get(idx).equals(rhs.articulationParameters.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public EntityType getAlternativeEntityType() {
		return alternativeEntityType;
	}

	public List<ArticulationParameter> getArticulationParameters() {
		return articulationParameters;
	}

	public int getCapabilities() {
		return capabilities;
	}

	public DeadReckoningParameter getDeadReckoningParameters() {
		return deadReckoningParameters;
	}

	public int getEntityAppearance() {
		return entityAppearance;
	}

	/**
	 * 0 off 1 on
	 */
	public int getEntityAppearance_brakeLights() {
		final int val = this.entityAppearance & 0x4000;
		return val >> 14;
	}

	/**
	 * 0 desert 1 winter 2 forest 3 unused
	 */
	public int getEntityAppearance_camouflageType() {
		final int val = this.entityAppearance & 0x60000;
		return val >> 17;
	}

	/**
	 * 0 no damage, 1 slight damage, 2 moderate, 3 destroyed
	 */
	public int getEntityAppearance_damage() {
		final int val = this.entityAppearance & 0x18;
		return val >> 3;
	}

	/**
	 * 0 no firepower iill, 1 firepower kill
	 */
	public int getEntityAppearance_firepower() {
		final int val = this.entityAppearance & 0x4;
		return val >> 2;
	}

	/**
	 * 0 off 1 on
	 */
	public int getEntityAppearance_flaming() {
		final int val = this.entityAppearance & 0x8000;
		return val >> 15;
	}

	/**
	 * 0 NA 1 closed popped 3 popped and person visible 4 open 5 open and person
	 * visible
	 */
	public int getEntityAppearance_hatch() {
		final int val = this.entityAppearance & 0xe00;
		return val >> 9;
	}

	/**
	 * 0 off 1 on
	 */
	public int getEntityAppearance_headlights() {
		final int val = this.entityAppearance & 0x1000;
		return val >> 12;
	}

	/**
	 * 0 not raised 1 raised
	 */
	public int getEntityAppearance_launcher() {
		final int val = this.entityAppearance & 0x10000;
		return val >> 16;
	}

	/**
	 * 0 no mobility kill, 1 mobility kill
	 */
	public int getEntityAppearance_mobility() {
		final int val = this.entityAppearance & 0x2;
		return val >> 1;
	}

	/**
	 * 0 uniform color, 1 camouflage
	 */
	public int getEntityAppearance_paintScheme() {
		final int val = this.entityAppearance & 0x1;
		return val >> 0;
	}

	/**
	 * 0 no smoke, 1 smoke plume, 2 engine smoke, 3 engine smoke and plume
	 */
	public int getEntityAppearance_smoke() {
		final int val = this.entityAppearance & 0x60;
		return val >> 5;
	}

	/**
	 * 0 off 1 on
	 */
	public int getEntityAppearance_tailLights() {
		final int val = this.entityAppearance & 0x2000;
		return val >> 13;
	}

	/**
	 * dust cloud, 0 none 1 small 2 medium 3 large
	 */
	public int getEntityAppearance_trailingEffects() {
		final int val = this.entityAppearance & 0x180;
		return val >> 7;
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

	public Orientation getEntityOrientation() {
		return entityOrientation;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public short getForceId() {
		return forceId;
	}

	public Marking getMarking() {
		return marking;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + entityID.getMarshalledSize(); // entityID
		marshalSize = marshalSize + 1; // forceId
		marshalSize = marshalSize + 1; // numberOfArticulationParameters
		marshalSize = marshalSize + entityType.getMarshalledSize(); // entityType
		marshalSize = marshalSize + alternativeEntityType.getMarshalledSize(); // alternativeEntityType
		marshalSize = marshalSize + entityLinearVelocity.getMarshalledSize(); // entityLinearVelocity
		marshalSize = marshalSize + entityLocation.getMarshalledSize(); // entityLocation
		marshalSize = marshalSize + entityOrientation.getMarshalledSize(); // entityOrientation
		marshalSize = marshalSize + 4; // entityAppearance
		marshalSize = marshalSize + deadReckoningParameters.getMarshalledSize(); // deadReckoningParameters
		marshalSize = marshalSize + marking.getMarshalledSize(); // marking
		marshalSize = marshalSize + 4; // capabilities
		for (int idx = 0; idx < articulationParameters.size(); idx++) {
			final ArticulationParameter listElement = articulationParameters.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public byte getNumberOfArticulationParameters() {
		return (byte) articulationParameters.size();
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			entityID.marshal(dos);
			dos.writeByte((byte) forceId);
			dos.writeByte((byte) articulationParameters.size());
			entityType.marshal(dos);
			alternativeEntityType.marshal(dos);
			entityLinearVelocity.marshal(dos);
			entityLocation.marshal(dos);
			entityOrientation.marshal(dos);
			dos.writeInt(entityAppearance);
			deadReckoningParameters.marshal(dos);
			marking.marshal(dos);
			dos.writeInt(capabilities);

			for (int idx = 0; idx < articulationParameters.size(); idx++) {
				final ArticulationParameter aArticulationParameter = articulationParameters.get(idx);
				aArticulationParameter.marshal(dos);
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
		buff.put((byte) forceId);
		buff.put((byte) articulationParameters.size());
		entityType.marshal(buff);
		alternativeEntityType.marshal(buff);
		entityLinearVelocity.marshal(buff);
		entityLocation.marshal(buff);
		entityOrientation.marshal(buff);
		buff.putInt(entityAppearance);
		deadReckoningParameters.marshal(buff);
		marking.marshal(buff);
		buff.putInt(capabilities);

		for (int idx = 0; idx < articulationParameters.size(); idx++) {
			final ArticulationParameter aArticulationParameter = articulationParameters.get(idx);
			aArticulationParameter.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setAlternativeEntityType(final EntityType pAlternativeEntityType) {
		alternativeEntityType = pAlternativeEntityType;
	}

	public void setArticulationParameters(final List<ArticulationParameter> pArticulationParameters) {
		articulationParameters = pArticulationParameters;
	}

	public void setCapabilities(final int pCapabilities) {
		capabilities = pCapabilities;
	}

	public void setDeadReckoningParameters(final DeadReckoningParameter pDeadReckoningParameters) {
		deadReckoningParameters = pDeadReckoningParameters;
	}

	public void setEntityAppearance(final int pEntityAppearance) {
		entityAppearance = pEntityAppearance;
	}

	/**
	 * 0 off 1 on
	 */
	public void setEntityAppearance_brakeLights(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x4000); // clear bits
		aVal = val << 14;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 desert 1 winter 2 forest 3 unused
	 */
	public void setEntityAppearance_camouflageType(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x60000); // clear bits
		aVal = val << 17;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 no damage, 1 slight damage, 2 moderate, 3 destroyed
	 */
	public void setEntityAppearance_damage(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x18); // clear bits
		aVal = val << 3;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 no firepower iill, 1 firepower kill
	 */
	public void setEntityAppearance_firepower(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x4); // clear bits
		aVal = val << 2;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 off 1 on
	 */
	public void setEntityAppearance_flaming(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x8000); // clear bits
		aVal = val << 15;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 NA 1 closed popped 3 popped and person visible 4 open 5 open and person
	 * visible
	 */
	public void setEntityAppearance_hatch(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0xe00); // clear bits
		aVal = val << 9;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 off 1 on
	 */
	public void setEntityAppearance_headlights(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x1000); // clear bits
		aVal = val << 12;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 not raised 1 raised
	 */
	public void setEntityAppearance_launcher(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x10000); // clear bits
		aVal = val << 16;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 no mobility kill, 1 mobility kill
	 */
	public void setEntityAppearance_mobility(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x2); // clear bits
		aVal = val << 1;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 uniform color, 1 camouflage
	 */
	public void setEntityAppearance_paintScheme(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x1); // clear bits
		aVal = val << 0;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 no smoke, 1 smoke plume, 2 engine smoke, 3 engine smoke and plume
	 */
	public void setEntityAppearance_smoke(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x60); // clear bits
		aVal = val << 5;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * 0 off 1 on
	 */
	public void setEntityAppearance_tailLights(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x2000); // clear bits
		aVal = val << 13;
		this.entityAppearance = this.entityAppearance | aVal;
	}

	/**
	 * dust cloud, 0 none 1 small 2 medium 3 large
	 */
	public void setEntityAppearance_trailingEffects(final int val) {
		int aVal = 0;
		this.entityAppearance &= (~0x180); // clear bits
		aVal = val << 7;
		this.entityAppearance = this.entityAppearance | aVal;
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

	public void setEntityOrientation(final Orientation pEntityOrientation) {
		entityOrientation = pEntityOrientation;
	}

	public void setEntityType(final EntityType pEntityType) {
		entityType = pEntityType;
	}

	public void setForceId(final short pForceId) {
		forceId = pForceId;
	}

	public void setMarking(final Marking pMarking) {
		marking = pMarking;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfArticulationParameters method will also be based on the actual
	 * list length rather than this value. The method is simply here for java bean
	 * completeness.
	 */
	public void setNumberOfArticulationParameters(final byte pNumberOfArticulationParameters) {
		numberOfArticulationParameters = pNumberOfArticulationParameters;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			entityID.unmarshal(dis);
			forceId = (short) dis.readUnsignedByte();
			numberOfArticulationParameters = dis.readByte();
			entityType.unmarshal(dis);
			alternativeEntityType.unmarshal(dis);
			entityLinearVelocity.unmarshal(dis);
			entityLocation.unmarshal(dis);
			entityOrientation.unmarshal(dis);
			entityAppearance = dis.readInt();
			deadReckoningParameters.unmarshal(dis);
			marking.unmarshal(dis);
			capabilities = dis.readInt();
			for (int idx = 0; idx < numberOfArticulationParameters; idx++) {
				final ArticulationParameter anX = new ArticulationParameter();
				anX.unmarshal(dis);
				articulationParameters.add(anX);
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
		forceId = (short) (buff.get() & 0xFF);
		numberOfArticulationParameters = buff.get();
		entityType.unmarshal(buff);
		alternativeEntityType.unmarshal(buff);
		entityLinearVelocity.unmarshal(buff);
		entityLocation.unmarshal(buff);
		entityOrientation.unmarshal(buff);
		entityAppearance = buff.getInt();
		deadReckoningParameters.unmarshal(buff);
		marking.unmarshal(buff);
		capabilities = buff.getInt();
		for (int idx = 0; idx < numberOfArticulationParameters; idx++) {
			final ArticulationParameter anX = new ArticulationParameter();
			anX.unmarshal(buff);
			articulationParameters.add(anX);
		}

	} // end of unmarshal method
} // end of class
