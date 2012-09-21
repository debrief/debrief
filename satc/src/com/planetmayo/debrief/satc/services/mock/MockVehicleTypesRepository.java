package com.planetmayo.debrief.satc.services.mock;

import java.util.Arrays;
import java.util.List;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.services.VehicleTypesRepository;

public class MockVehicleTypesRepository implements VehicleTypesRepository {

	@Override
	public List<VehicleType> getAllTypes() {
		return Arrays.asList(
				createType("UK Cross Channel Ferry"),
				createType("Medium Tanker"),
				createType("Large Tanker")
		);
	}
	
	private VehicleType createType(String name) {
		VehicleType type = new VehicleType();
		type.setName(name);
		return type;
	}
}
