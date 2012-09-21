package com.planetmayo.debrief.satc.services.mock;

import java.util.Arrays;
import java.util.List;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.services.VehicleTypesRepository;

public class MockVehicleTypesRepository implements VehicleTypesRepository {

	@Override
	public List<VehicleType> getAllTypes() {
		return Arrays.asList(
				new VehicleType("UK Cross Channel Ferry", 12, 30,400, 1600, 0.2, 0.4), 
				new VehicleType("Medium Tanker", 8, 15, 800,2600, 1, 3), 
				new VehicleType("Large Tanker", 8, 12, 1400, 4600, 1,3));
	}
}
