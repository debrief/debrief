package com.planetmayo.debrief.satc.model.manager.mock;

import java.util.Arrays;
import java.util.List;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.manager.IVehicleTypesManager;
import com.planetmayo.debrief.satc.util.GeoSupport;

public class MockVehicleTypesManager implements IVehicleTypesManager
{

	@Override
	public List<VehicleType> getAllTypes()
	{
		return Arrays.asList(
				new VehicleType("UK Ferry", GeoSupport.kts2MSec(2), GeoSupport
						.kts2MSec(30), Math.toRadians(0), Math.toRadians(2), 0.2, 0.4, 0.2, 0.4),
				new VehicleType("Medium Tanker", GeoSupport.kts2MSec(1), GeoSupport
						.kts2MSec(12), Math.toRadians(0), Math.toRadians(1), 0.1, 0.4, 0.2, 0.3),
				new VehicleType("Large Tanker", GeoSupport.kts2MSec(1), GeoSupport
						.kts2MSec(15), Math.toRadians(0), Math.toRadians(0.5), 0.1, 0.3, 0.2, 0.3)

		);
	}
}
