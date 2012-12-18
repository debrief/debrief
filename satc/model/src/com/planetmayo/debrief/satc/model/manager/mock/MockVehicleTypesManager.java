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
						.kts2MSec(30), GeoSupport.degsSec2radsMilli(0), GeoSupport
						.degsSec2radsMilli(2), 0.2, 0.4, 0.2, 0.4),
				new VehicleType("Medium Tanker", GeoSupport.kts2MSec(1), GeoSupport
						.kts2MSec(12), GeoSupport.degsSec2radsMilli(0), GeoSupport
						.degsSec2radsMilli(1), 1, 3, 1, 3),
				new VehicleType("Large Tanker", GeoSupport.kts2MSec(1), GeoSupport
						.kts2MSec(15), GeoSupport.degsSec2radsMilli(0), GeoSupport
						.degsSec2radsMilli(0.5), 1, 3, 1, 3)

		);
	}
}
