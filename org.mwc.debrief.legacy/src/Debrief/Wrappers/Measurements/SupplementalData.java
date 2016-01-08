package Debrief.Wrappers.Measurements;

import java.util.ArrayList;

public class SupplementalData extends ArrayList<SupplementalDataBlock>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SupplementalData()
	{
		MeasurementBlock measurements = new MeasurementBlock();
		measurements.setName("Measurements");
		this.add(measurements);
	}
}
