package edu.nps.moves.dis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section 5.3.7.4.2 When present, layer 2 should follow layer 1 and have the
 * following fields. This requires manual cleanup. the beamData attribute
 * semantics are used in multiple ways. UNFINSISHED
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class IffAtcNavAidsLayer2Pdu extends IffAtcNavAidsLayer1Pdu implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** layer header */
	protected LayerHeader layerHeader = new LayerHeader();

	/** beam data */
	protected BeamData beamData = new BeamData();

	/** Secondary operational data, 5.2.57 */
	protected BeamData secondaryOperationalData = new BeamData();

	/** variable length list of fundamental parameters. ^^^This is wrong */
	protected List<FundamentalParameterDataIff> fundamentalIffParameters = new ArrayList<FundamentalParameterDataIff>();

	/** Constructor */
	public IffAtcNavAidsLayer2Pdu() {
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

		if (!(obj instanceof IffAtcNavAidsLayer2Pdu))
			return false;

		final IffAtcNavAidsLayer2Pdu rhs = (IffAtcNavAidsLayer2Pdu) obj;

		if (!(layerHeader.equals(rhs.layerHeader)))
			ivarsEqual = false;
		if (!(beamData.equals(rhs.beamData)))
			ivarsEqual = false;
		if (!(secondaryOperationalData.equals(rhs.secondaryOperationalData)))
			ivarsEqual = false;

		for (int idx = 0; idx < fundamentalIffParameters.size(); idx++) {
			if (!(fundamentalIffParameters.get(idx).equals(rhs.fundamentalIffParameters.get(idx))))
				ivarsEqual = false;
		}

		return ivarsEqual && super.equalsImpl(rhs);
	}

	public BeamData getBeamData() {
		return beamData;
	}

	public List<FundamentalParameterDataIff> getFundamentalIffParameters() {
		return fundamentalIffParameters;
	}

	public LayerHeader getLayerHeader() {
		return layerHeader;
	}

	@Override
	public int getMarshalledSize() {
		int marshalSize = 0;

		marshalSize = super.getMarshalledSize();
		marshalSize = marshalSize + layerHeader.getMarshalledSize(); // layerHeader
		marshalSize = marshalSize + beamData.getMarshalledSize(); // beamData
		marshalSize = marshalSize + secondaryOperationalData.getMarshalledSize(); // secondaryOperationalData
		for (int idx = 0; idx < fundamentalIffParameters.size(); idx++) {
			final FundamentalParameterDataIff listElement = fundamentalIffParameters.get(idx);
			marshalSize = marshalSize + listElement.getMarshalledSize();
		}

		return marshalSize;
	}

	public BeamData getSecondaryOperationalData() {
		return secondaryOperationalData;
	}

	@Override
	public void marshal(final DataOutputStream dos) {
		super.marshal(dos);
		try {
			layerHeader.marshal(dos);
			beamData.marshal(dos);
			secondaryOperationalData.marshal(dos);

			for (int idx = 0; idx < fundamentalIffParameters.size(); idx++) {
				final FundamentalParameterDataIff aFundamentalParameterDataIff = fundamentalIffParameters.get(idx);
				aFundamentalParameterDataIff.marshal(dos);
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
		layerHeader.marshal(buff);
		beamData.marshal(buff);
		secondaryOperationalData.marshal(buff);

		for (int idx = 0; idx < fundamentalIffParameters.size(); idx++) {
			final FundamentalParameterDataIff aFundamentalParameterDataIff = fundamentalIffParameters.get(idx);
			aFundamentalParameterDataIff.marshal(buff);
		} // end of list marshalling

	} // end of marshal method

	public void setBeamData(final BeamData pBeamData) {
		beamData = pBeamData;
	}

	public void setFundamentalIffParameters(final List<FundamentalParameterDataIff> pFundamentalIffParameters) {
		fundamentalIffParameters = pFundamentalIffParameters;
	}

	public void setLayerHeader(final LayerHeader pLayerHeader) {
		layerHeader = pLayerHeader;
	}

	public void setSecondaryOperationalData(final BeamData pSecondaryOperationalData) {
		secondaryOperationalData = pSecondaryOperationalData;
	}

	@Override
	public void unmarshal(final DataInputStream dis) {
		super.unmarshal(dis);

		try {
			layerHeader.unmarshal(dis);
			beamData.unmarshal(dis);
			secondaryOperationalData.unmarshal(dis);
			for (int idx = 0; idx < pad2; idx++) {
				final FundamentalParameterDataIff anX = new FundamentalParameterDataIff();
				anX.unmarshal(dis);
				fundamentalIffParameters.add(anX);
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

		layerHeader.unmarshal(buff);
		beamData.unmarshal(buff);
		secondaryOperationalData.unmarshal(buff);
		for (int idx = 0; idx < pad2; idx++) {
			final FundamentalParameterDataIff anX = new FundamentalParameterDataIff();
			anX.unmarshal(buff);
			fundamentalIffParameters.add(anX);
		}

	} // end of unmarshal method
} // end of class
