package com.planetmayo.debrief.satc.support.mock;

import java.util.Arrays;
import java.util.List;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.support.VehicleTypesRepository;
import com.planetmayo.debrief.satc.util.GeoSupport;

public class MockVehicleTypesRepository implements VehicleTypesRepository
{

	@Override
	public List<VehicleType> getAllTypes()
	{
		return Arrays.asList(
				new VehicleType("UK Ferry", GeoSupport.kts2MSec(2), GeoSupport
						.kts2MSec(30), 400, 1600, 0.2, 0.4, 0.2, 0.4),
				new VehicleType("Medium Tanker", GeoSupport.kts2MSec(1), GeoSupport
						.kts2MSec(12), 800, 2600, 1, 3, 1, 3), 
				new VehicleType(
						"Large Tanker", GeoSupport.kts2MSec(1), GeoSupport.kts2MSec(15),
						1400, 4600, 1, 3, 1, 3)

		);
	}
}
