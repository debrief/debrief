package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Description of one electronic emission beam
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class ElectronicEmissionBeamData extends Object implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** This field shall specify the length of this beams data in 32 bit words */
	protected short beamDataLength;

	/**
	 * This field shall specify a unique emitter database number assigned to
	 * differentiate between otherwise similar or identical emitter beams within an
	 * emitter system.
	 */
	protected short beamIDNumber;

	/**
	 * This field shall specify a Beam Parameter Index number that shall be used by
	 * receiving entities in conjunction with the Emitter Name field to provide a
	 * pointer to the stored database parameters required to regenerate the beam.
	 */
	protected int beamParameterIndex;

	/** Fundamental parameter data such as frequency range, beam sweep, etc. */
	protected FundamentalParameterData fundamentalParameterData = new FundamentalParameterData();

	/** beam function of a particular beam */
	protected short beamFunction;

	/** Number of track/jam targets */
	protected short numberOfTrackJamTargets;

	/**
	 * wheher or not the receiving simulation apps can assume all the targets in the
	 * scan pattern are being tracked/jammed
	 */
	protected short highDensityTrackJam;

	/** padding */
	protected short pad4 = (short) 0;

	/** identify jamming techniques used */
	protected long jammingModeSequence;

	/** variable length variablelist of track/jam targets */
	protected List<TrackJamTarget> trackJamTargets = new ArrayList<TrackJamTarget>();

	/** Constructor */
	public ElectronicEmissionBeamData() {
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

		if (!(obj instanceof ElectronicEmissionBeamData))
			return false;

		final ElectronicEmissionBeamData rhs = (ElectronicEmissionBeamData) obj;

		if (!(beamDataLength == rhs.beamDataLength))
			ivarsEqual = false;
		if (!(beamIDNumber == rhs.beamIDNumber))
			ivarsEqual = false;
		if (!(beamParameterIndex == rhs.beamParameterIndex))
			ivarsEqual = false;
		if (!(fundamentalParameterData.equals(rhs.fundamentalParameterData)))
			ivarsEqual = false;
		if (!(beamFunction == rhs.beamFunction))
			ivarsEqual = false;
		if (!(numberOfTrackJamTargets == rhs.numberOfTrackJamTargets))
			ivarsEqual = false;
		if (!(highDensityTrackJam == rhs.highDensityTrackJam))
			ivarsEqual = false;
		if (!(pad4 == rhs.pad4))
			ivarsEqual = false;
		if (!(jammingModeSequence == rhs.jammingModeSequence))
			ivarsEqual = false;

		for (int idx = 0; idx < trackJamTargets.size(); idx++) {
			if (!(trackJamTargets.get(idx).equals(rhs.trackJamTargets.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual;
	}

	public short getBeamDataLength() {
		return beamDataLength;
	}

	public short getBeamFunction() {
		return beamFunction;
	}

	public short getBeamIDNumber() {
		return beamIDNumber;
	}

	public int getBeamParameterIndex() {
		return beamParameterIndex;
	}

	public FundamentalParameterData getFundamentalParameterData() {
		return fundamentalParameterData;
	}

	public short getHighDensityTrackJam() {
		return highDensityTrackJam;
	}

	public long getJammingModeSequence() {
		return jammingModeSequence;
	}

	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = marshalSize + 1; // beamDataLength
		marshalSize = marshalSize + 1; // beamIDNumber
		marshalSize = marshalSize + 2; // beamParameterIndex
		marshalSize = marshalSize + fundamentalParameterData.getMarshalledSize(); // fundamentalParameterData
		marshalSize = marshalSize + 1; // beamFunction
		marshalSize = marshalSize + 1; // numberOfTrackJamTargets
		marshalSize = marshalSize + 1; // highDensityTrackJam
		marshalSize = marshalSize + 1; // pad4
		marshalSize = marshalSize + 4; // jammingModeSequence
		for (int idx = 0; idx < trackJamTargets.size(); idx++) {
			final TrackJamTarget listElement = trackJamTargets.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public short getNumberOfTrackJamTargets() {
		return (short) trackJamTargets.size();
	}

	public short getPad4() {
		return pad4;
	}

	public List<TrackJamTarget> getTrackJamTargets() {
		return trackJamTargets;
	}

	public void marshal(final DataOutputStream dos) {
		try {
			dos.writeByte((byte) beamDataLength);
			dos.writeByte((byte) beamIDNumber);
			dos.writeShort((short) beamParameterIndex);
			fundamentalParameterData.marshal(dos);
			dos.writeByte((byte) beamFunction);
			dos.writeByte((byte) trackJamTargets.size());
			dos.writeByte((byte) highDensityTrackJam);
			dos.writeByte((byte) pad4);
			dos.writeInt((int) jammingModeSequence);

			for (int idx = 0; idx < trackJamTargets.size(); idx++) {
				final TrackJamTarget aTrackJamTarget = trackJamTargets.get(idx);
				aTrackJamTarget.marshal(dos);
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
		buff.put((byte) beamDataLength);
		buff.put((byte) beamIDNumber);
		buff.putShort((short) beamParameterIndex);
		fundamentalParameterData.marshal(buff);
		buff.put((byte) beamFunction);
		buff.put((byte) trackJamTargets.size());
		buff.put((byte) highDensityTrackJam);
		buff.put((byte) pad4);
		buff.putInt((int) jammingModeSequence);

		for (int idx = 0; idx < trackJamTargets.size(); idx++) {
			final TrackJamTarget aTrackJamTarget = trackJamTargets.get(idx);
			aTrackJamTarget.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setBeamDataLength(final short pBeamDataLength) {
		beamDataLength = pBeamDataLength;
	}

	public void setBeamFunction(final short pBeamFunction) {
		beamFunction = pBeamFunction;
	}

	public void setBeamIDNumber(final short pBeamIDNumber) {
		beamIDNumber = pBeamIDNumber;
	}

	public void setBeamParameterIndex(final int pBeamParameterIndex) {
		beamParameterIndex = pBeamParameterIndex;
	}

	public void setFundamentalParameterData(final FundamentalParameterData pFundamentalParameterData) {
		fundamentalParameterData = pFundamentalParameterData;
	}

	public void setHighDensityTrackJam(final short pHighDensityTrackJam) {
		highDensityTrackJam = pHighDensityTrackJam;
	}

	public void setJammingModeSequence(final long pJammingModeSequence) {
		jammingModeSequence = pJammingModeSequence;
	}

	/**
	 * Note that setting this value will not change the marshalled value. The list
	 * whose length this describes is used for that purpose. The
	 * getnumberOfTrackJamTargets method will also be based on the actual list
	 * length rather than this value. The method is simply here for java bean
	 * completeness.
	 */
	public void setNumberOfTrackJamTargets(final short pNumberOfTrackJamTargets) {
		numberOfTrackJamTargets = pNumberOfTrackJamTargets;
	}

	public void setPad4(final short pPad4) {
		pad4 = pPad4;
	}

	public void setTrackJamTargets(final List<TrackJamTarget> pTrackJamTargets) {
		trackJamTargets = pTrackJamTargets;
	}

	public void unmarshal(final DataInputStream dis) {
		try {
			beamDataLength = (short) dis.readUnsignedByte();
			beamIDNumber = (short) dis.readUnsignedByte();
			beamParameterIndex = dis.readUnsignedShort();
			fundamentalParameterData.unmarshal(dis);
			beamFunction = (short) dis.readUnsignedByte();
			numberOfTrackJamTargets = (short) dis.readUnsignedByte();
			highDensityTrackJam = (short) dis.readUnsignedByte();
			pad4 = (short) dis.readUnsignedByte();
			jammingModeSequence = dis.readInt();
			for (int idx = 0; idx < numberOfTrackJamTargets; idx++) {
				final TrackJamTarget anX = new TrackJamTarget();
				anX.unmarshal(dis);
				trackJamTargets.add(anX);
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
		beamDataLength = (short) (buff.get() & 0xFF);
		beamIDNumber = (short) (buff.get() & 0xFF);
		beamParameterIndex = buff.getShort() & 0xFFFF;
		fundamentalParameterData.unmarshal(buff);
		beamFunction = (short) (buff.get() & 0xFF);
		numberOfTrackJamTargets = (short) (buff.get() & 0xFF);
		highDensityTrackJam = (short) (buff.get() & 0xFF);
		pad4 = (short) (buff.get() & 0xFF);
		jammingModeSequence = buff.getInt();
		for (int idx = 0; idx < numberOfTrackJamTargets; idx++) {
			final TrackJamTarget anX = new TrackJamTarget();
			anX.unmarshal(buff);
			trackJamTargets.add(anX);
		}

	} // end of unmarshal method
} // end of class
