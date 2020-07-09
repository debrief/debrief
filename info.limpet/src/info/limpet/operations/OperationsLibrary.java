/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.operations;

import static javax.measure.unit.NonSI.MINUTE;
import static javax.measure.unit.NonSI.NAUTICAL_MILE;
import static javax.measure.unit.SI.CELSIUS;
import static javax.measure.unit.SI.HERTZ;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.METRES_PER_SECOND;
import static javax.measure.unit.SI.METRES_PER_SQUARE_SECOND;
import static javax.measure.unit.SI.RADIAN;
import static javax.measure.unit.SI.SECOND;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Velocity;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.Maths;

import info.limpet.IOperation;
import info.limpet.IStoreItem;
import info.limpet.impl.SampleData;
import info.limpet.operations.admin.AddLayerOperation;
import info.limpet.operations.admin.CopyCsvToClipboardAction;
import info.limpet.operations.admin.CreateNewIndexedDatafileOperation;
import info.limpet.operations.admin.CreateNewLookupDatafileOperation;
import info.limpet.operations.admin.CreateSingletonGenerator;
import info.limpet.operations.admin.DeleteCollectionOperation;
import info.limpet.operations.admin.ExportCsvToFileAction;
import info.limpet.operations.admin.GenerateDummyDataOperation;
import info.limpet.operations.arithmetic.SimpleMovingAverageOperation;
import info.limpet.operations.arithmetic.UnaryQuantityOperation;
import info.limpet.operations.arithmetic.simple.AddQuantityOperation;
import info.limpet.operations.arithmetic.simple.DivideQuantityOperation;
import info.limpet.operations.arithmetic.simple.MultiplyQuantityOperation;
import info.limpet.operations.arithmetic.simple.RateOfChangeOperation;
import info.limpet.operations.arithmetic.simple.SubtractQuantityOperation;
import info.limpet.operations.arithmetic.simple.UnitConversionOperation;
import info.limpet.operations.filter.MaxMinFilterOperation;
import info.limpet.operations.grid.GenerateGrid;
import info.limpet.operations.spatial.BearingBetweenTracksOperation;
import info.limpet.operations.spatial.DistanceBetweenTracksOperation;
import info.limpet.operations.spatial.DopplerShiftBetweenTracksOperation;
import info.limpet.operations.spatial.GenerateCourseAndSpeedOperation;
import info.limpet.operations.spatial.ProplossBetweenTwoTracksOperation;
import info.limpet.operations.spatial.msa.BistaticAngleOperation;

public class OperationsLibrary {
	public abstract static class UnitaryAngleOperation extends UnaryQuantityOperation {
		public UnitaryAngleOperation(final String opName) {
			super(opName);
		}

		@Override
		protected final boolean appliesTo(final List<IStoreItem> selection) {
			return selection.size() > 0 && getATests().allHaveDimension(selection, SI.RADIAN.getDimension())
					&& getATests().allOneDim(selection);
		}
	}

	public static final String SPATIAL = "Spatial";

	public static final String ADMINISTRATION = "Administration";
	public static final String FILTER = "Filter";
	public static final String CONVERSIONS = "Conversions";
	public static final String ARITHMETIC = "Arithmetic";
	public static final String CREATE = "Create";

	private static List<IOperation> getAdmin() {
		final List<IOperation> admin = new ArrayList<IOperation>();
		admin.add(new GenerateDummyDataOperation("small", 20));
		admin.add(new GenerateDummyDataOperation("large", 1000));
		admin.add(new GenerateDummyDataOperation("monster", 1000000));
		admin.add(new CreateNewIndexedDatafileOperation());
		admin.add(new CreateNewLookupDatafileOperation());
		admin.add(new UnaryQuantityOperation("Clear units") {
			@Override
			protected boolean appliesTo(final List<IStoreItem> selection) {
				return true;
			}

			@Override
			public Dataset calculate(final Dataset input) {
				throw new RuntimeException("Not implemented");
			}

			@Override
			protected String getUnaryNameFor(final String name) {
				return "Clear units";
			}

			@Override
			protected Unit<?> getUnaryOutputUnit(final Unit<?> first) {
				return Dimensionless.UNIT;
			}
		});

		// and the export operations
		admin.add(new ExportCsvToFileAction());
		admin.add(new CopyCsvToClipboardAction());

		return admin;
	}

	private static List<IOperation> getArithmetic() {
		final List<IOperation> arithmetic = new ArrayList<IOperation>();
		arithmetic.add(new MultiplyQuantityOperation());
		arithmetic.add(new AddQuantityOperation());
		arithmetic.add(new SubtractQuantityOperation());
		arithmetic.add(new DivideQuantityOperation());
		arithmetic.add(new SimpleMovingAverageOperation(3));
		arithmetic.add(new RateOfChangeOperation());

		// also our generic maths operators
		arithmetic.add(new UnaryQuantityOperation("Abs") {
			@Override
			protected boolean appliesTo(final List<IStoreItem> selection) {
				return getATests().allQuantity(selection) && getATests().allOneDim(selection);
			}

			@Override
			public Dataset calculate(final Dataset input) {
				return Maths.abs(input);
			}

			@Override
			protected Unit<?> getUnaryOutputUnit(final Unit<?> first) {
				return first;
			}
		});
		arithmetic.add(new UnitaryAngleOperation("Sin") {
			@Override
			public Dataset calculate(final Dataset input) {
				return Maths.sin(input);
			}

			@Override
			protected Unit<?> getUnaryOutputUnit(final Unit<?> first) {
				return first;
			}
		});
		arithmetic.add(new UnitaryAngleOperation("Cos") {
			@Override
			public Dataset calculate(final Dataset input) {
				return Maths.cos(input);
			}

			@Override
			protected Unit<?> getUnaryOutputUnit(final Unit<?> first) {
				return first;
			}
		});
		arithmetic.add(new UnitaryAngleOperation("Tan") {
			@Override
			public Dataset calculate(final Dataset input) {
				return Maths.tan(input);
			}

			@Override
			protected Unit<?> getUnaryOutputUnit(final Unit<?> first) {
				return first;
			}
		});
		arithmetic.add(new UnaryQuantityOperation("Inv") {
			@Override
			protected boolean appliesTo(final List<IStoreItem> selection) {
				return getATests().allQuantity(selection) && getATests().allOneDim(selection);
			}

			@Override
			public Dataset calculate(final Dataset input) {
				return Maths.divide(1, input);
			}

			@Override
			protected Unit<?> getUnaryOutputUnit(final Unit<?> first) {
				return first;
			}
		});
		arithmetic.add(new UnaryQuantityOperation("Sqrt") {
			@Override
			protected boolean appliesTo(final List<IStoreItem> selection) {
				return getATests().allQuantity(selection) && getATests().allOneDim(selection);
			}

			@Override
			public Dataset calculate(final Dataset input) {
				return Maths.sqrt(input);
			}

			@Override
			protected Unit<?> getUnaryOutputUnit(final Unit<?> first) {
				return first;
			}
		});
		arithmetic.add(new UnaryQuantityOperation("Sqr") {
			@Override
			protected boolean appliesTo(final List<IStoreItem> selection) {
				return getATests().allQuantity(selection) && getATests().allOneDim(selection);
			}

			@Override
			public Dataset calculate(final Dataset input) {
				return Maths.square(input);
			}

			@Override
			protected Unit<?> getUnaryOutputUnit(final Unit<?> first) {
				return first;
			}
		});
		arithmetic.add(new UnaryQuantityOperation("Log") {
			@Override
			protected boolean appliesTo(final List<IStoreItem> selection) {
				return getATests().allQuantity(selection) && getATests().allOneDim(selection);
			}

			@Override
			public Dataset calculate(final Dataset input) {
				return Maths.log(input);
			}

			@Override
			protected Unit<?> getUnaryOutputUnit(final Unit<?> first) {
				return first;
			}
		});

		return arithmetic;
	}

	private static List<IOperation> getConversions() {
		final List<IOperation> conversions = new ArrayList<IOperation>();

		// Length
		conversions.add(new UnitConversionOperation(METRE));

		// Time
		conversions.add(new UnitConversionOperation(SECOND));
		conversions.add(new UnitConversionOperation(MINUTE));

		// Speed
		conversions.add(new UnitConversionOperation(METRES_PER_SECOND));
		conversions.add(new UnitConversionOperation(NAUTICAL_MILE.divide(SECOND.times(3600)).asType(Velocity.class)));

		// Acceleration
		conversions.add(new UnitConversionOperation(METRES_PER_SQUARE_SECOND));

		// Temperature
		conversions.add(new UnitConversionOperation(CELSIUS));

		// Angle
		conversions.add(new UnitConversionOperation(RADIAN));
		conversions.add(new UnitConversionOperation(SampleData.DEGREE_ANGLE));
		return conversions;
	}

	private static List<IOperation> getCreate() {
		final List<IOperation> create = new ArrayList<IOperation>();

		create.add(new AddLayerOperation());

		create.add(new CreateSingletonGenerator("dimensionless", Dimensionless.UNIT));

		create.add(new CreateSingletonGenerator("frequency", HERTZ.asType(Frequency.class)));

		create.add(new CreateSingletonGenerator("decibels", NonSI.DECIBEL));

		create.add(new CreateSingletonGenerator("speed (m/s)", METRE.divide(SECOND).asType(Velocity.class)));

		create.add(new CreateSingletonGenerator("course (degs)", SampleData.DEGREE_ANGLE.asType(Angle.class)));
		create.add(new CreateSingletonGenerator("time (secs)", SI.SECOND.asType(Duration.class)));
		// create.add(new CreateLocationAction());
		create.add(new GenerateGrid());

		return create;
	}

	private static List<IOperation> getFilter() {
		final List<IOperation> filter = new ArrayList<IOperation>();
		filter.add(new MaxMinFilterOperation());
		return filter;
	}

	public static HashMap<String, List<IOperation>> getOperations() {
		final HashMap<String, List<IOperation>> res = new HashMap<String, List<IOperation>>();

		res.put(ARITHMETIC, getArithmetic());
		res.put(CONVERSIONS, getConversions());
		res.put(ADMINISTRATION, getAdmin());
		res.put(SPATIAL, getSpatial());
		res.put(CREATE, getCreate());
		res.put(FILTER, getFilter());
		return res;
	}

	private static List<IOperation> getSpatial() {
		final List<IOperation> spatial = new ArrayList<IOperation>();
		spatial.add(new DistanceBetweenTracksOperation());
		spatial.add(new BearingBetweenTracksOperation());
		spatial.add(new GenerateCourseAndSpeedOperation());
		spatial.add(new DopplerShiftBetweenTracksOperation());
		spatial.add(new ProplossBetweenTwoTracksOperation());
		spatial.add(new BistaticAngleOperation());
		return spatial;
	}

	public static List<IOperation> getTopLevel() {
		final List<IOperation> topLevel = new ArrayList<IOperation>();
		topLevel.add(new DeleteCollectionOperation());
		return topLevel;
	}

	/**
	 * use protected constructor to prevent accidental declaration
	 *
	 */
	protected OperationsLibrary() {

	}
}
