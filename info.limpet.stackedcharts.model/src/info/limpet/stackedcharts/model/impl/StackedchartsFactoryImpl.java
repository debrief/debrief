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
/**
 */
package info.limpet.stackedcharts.model.impl;

import java.awt.Color;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import info.limpet.stackedcharts.model.AngleAxis;
import info.limpet.stackedcharts.model.AxisDirection;
import info.limpet.stackedcharts.model.AxisScale;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DateAxis;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.LineType;
import info.limpet.stackedcharts.model.LinearStyling;
import info.limpet.stackedcharts.model.Marker;
import info.limpet.stackedcharts.model.MarkerStyle;
import info.limpet.stackedcharts.model.NumberAxis;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.StackedchartsFactory;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.model.Styling;
import info.limpet.stackedcharts.model.Zone;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!--
 * end-user-doc -->
 *
 * @generated
 */
public class StackedchartsFactoryImpl extends EFactoryImpl implements StackedchartsFactory {
	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static StackedchartsPackage getPackage() {
		return StackedchartsPackage.eINSTANCE;
	}

	/**
	 * Creates the default factory implementation. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @generated
	 */
	public static StackedchartsFactory init() {
		try {
			final StackedchartsFactory theStackedchartsFactory = (StackedchartsFactory) EPackage.Registry.INSTANCE
					.getEFactory(StackedchartsPackage.eNS_URI);
			if (theStackedchartsFactory != null) {
				return theStackedchartsFactory;
			}
		} catch (final Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new StackedchartsFactoryImpl();
	}

	/**
	 * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @generated
	 */
	public StackedchartsFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertAxisDirectionToString(final EDataType eDataType, final Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertAxisScaleToString(final EDataType eDataType, final Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 */
	public String convertColorToString(final EDataType eDataType, final Object instanceValue) {
		final String res;
		if (instanceValue != null) {
			final Color theColor = (Color) instanceValue;
			res = "#" + Integer.toHexString(theColor.getRGB()).substring(2).toUpperCase();
		} else {
			res = null;
		}
		return res;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertLineTypeToString(final EDataType eDataType, final Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertMarkerStyleToString(final EDataType eDataType, final Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertOrientationToString(final EDataType eDataType, final Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String convertToString(final EDataType eDataType, final Object instanceValue) {
		switch (eDataType.getClassifierID()) {
		case StackedchartsPackage.AXIS_SCALE:
			return convertAxisScaleToString(eDataType, instanceValue);
		case StackedchartsPackage.ORIENTATION:
			return convertOrientationToString(eDataType, instanceValue);
		case StackedchartsPackage.AXIS_DIRECTION:
			return convertAxisDirectionToString(eDataType, instanceValue);
		case StackedchartsPackage.MARKER_STYLE:
			return convertMarkerStyleToString(eDataType, instanceValue);
		case StackedchartsPackage.LINE_TYPE:
			return convertLineTypeToString(eDataType, instanceValue);
		case StackedchartsPackage.COLOR:
			return convertColorToString(eDataType, instanceValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public EObject create(final EClass eClass) {
		switch (eClass.getClassifierID()) {
		case StackedchartsPackage.CHART_SET:
			return createChartSet();
		case StackedchartsPackage.CHART:
			return createChart();
		case StackedchartsPackage.DEPENDENT_AXIS:
			return createDependentAxis();
		case StackedchartsPackage.DATASET:
			return createDataset();
		case StackedchartsPackage.DATA_ITEM:
			return createDataItem();
		case StackedchartsPackage.ZONE:
			return createZone();
		case StackedchartsPackage.MARKER:
			return createMarker();
		case StackedchartsPackage.STYLING:
			return createStyling();
		case StackedchartsPackage.PLAIN_STYLING:
			return createPlainStyling();
		case StackedchartsPackage.LINEAR_STYLING:
			return createLinearStyling();
		case StackedchartsPackage.INDEPENDENT_AXIS:
			return createIndependentAxis();
		case StackedchartsPackage.SCATTER_SET:
			return createScatterSet();
		case StackedchartsPackage.DATUM:
			return createDatum();
		case StackedchartsPackage.SELECTIVE_ANNOTATION:
			return createSelectiveAnnotation();
		case StackedchartsPackage.DATE_AXIS:
			return createDateAxis();
		case StackedchartsPackage.NUMBER_AXIS:
			return createNumberAxis();
		case StackedchartsPackage.ANGLE_AXIS:
			return createAngleAxis();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public AngleAxis createAngleAxis() {
		final AngleAxisImpl angleAxis = new AngleAxisImpl();
		return angleAxis;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public AxisDirection createAxisDirectionFromString(final EDataType eDataType, final String initialValue) {
		final AxisDirection result = AxisDirection.get(initialValue);
		if (result == null)
			throw new IllegalArgumentException(
					"The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public AxisScale createAxisScaleFromString(final EDataType eDataType, final String initialValue) {
		final AxisScale result = AxisScale.get(initialValue);
		if (result == null)
			throw new IllegalArgumentException(
					"The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Chart createChart() {
		final ChartImpl chart = new ChartImpl();
		return chart;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public ChartSet createChartSet() {
		final ChartSetImpl chartSet = new ChartSetImpl();
		return chartSet;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 */
	public Color createColorFromString(final EDataType eDataType, final String initialValue) {
		final Color res;
		if (initialValue != null && initialValue.length() > 0) {
			res = new Color(Integer.valueOf(initialValue.substring(1, 3), 16),
					Integer.valueOf(initialValue.substring(3, 5), 16),
					Integer.valueOf(initialValue.substring(5, 7), 16));
		} else {
			res = null;
		}
		return res;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public DataItem createDataItem() {
		final DataItemImpl dataItem = new DataItemImpl();
		return dataItem;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Dataset createDataset() {
		final DatasetImpl dataset = new DatasetImpl();
		return dataset;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public DateAxis createDateAxis() {
		final DateAxisImpl dateAxis = new DateAxisImpl();
		return dateAxis;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Datum createDatum() {
		final DatumImpl datum = new DatumImpl();
		return datum;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public DependentAxis createDependentAxis() {
		final DependentAxisImpl dependentAxis = new DependentAxisImpl();
		return dependentAxis;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object createFromString(final EDataType eDataType, final String initialValue) {
		switch (eDataType.getClassifierID()) {
		case StackedchartsPackage.AXIS_SCALE:
			return createAxisScaleFromString(eDataType, initialValue);
		case StackedchartsPackage.ORIENTATION:
			return createOrientationFromString(eDataType, initialValue);
		case StackedchartsPackage.AXIS_DIRECTION:
			return createAxisDirectionFromString(eDataType, initialValue);
		case StackedchartsPackage.MARKER_STYLE:
			return createMarkerStyleFromString(eDataType, initialValue);
		case StackedchartsPackage.LINE_TYPE:
			return createLineTypeFromString(eDataType, initialValue);
		case StackedchartsPackage.COLOR:
			return createColorFromString(eDataType, initialValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IndependentAxis createIndependentAxis() {
		final IndependentAxisImpl independentAxis = new IndependentAxisImpl();
		return independentAxis;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public LinearStyling createLinearStyling() {
		final LinearStylingImpl linearStyling = new LinearStylingImpl();
		return linearStyling;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public LineType createLineTypeFromString(final EDataType eDataType, final String initialValue) {
		final LineType result = LineType.get(initialValue);
		if (result == null)
			throw new IllegalArgumentException(
					"The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Marker createMarker() {
		final MarkerImpl marker = new MarkerImpl();
		return marker;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public MarkerStyle createMarkerStyleFromString(final EDataType eDataType, final String initialValue) {
		final MarkerStyle result = MarkerStyle.get(initialValue);
		if (result == null)
			throw new IllegalArgumentException(
					"The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public NumberAxis createNumberAxis() {
		final NumberAxisImpl numberAxis = new NumberAxisImpl();
		return numberAxis;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Orientation createOrientationFromString(final EDataType eDataType, final String initialValue) {
		final Orientation result = Orientation.get(initialValue);
		if (result == null)
			throw new IllegalArgumentException(
					"The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public PlainStyling createPlainStyling() {
		final PlainStylingImpl plainStyling = new PlainStylingImpl();
		return plainStyling;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public ScatterSet createScatterSet() {
		final ScatterSetImpl scatterSet = new ScatterSetImpl();
		return scatterSet;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public SelectiveAnnotation createSelectiveAnnotation() {
		final SelectiveAnnotationImpl selectiveAnnotation = new SelectiveAnnotationImpl();
		return selectiveAnnotation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Styling createStyling() {
		final StylingImpl styling = new StylingImpl();
		return styling;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Zone createZone() {
		final ZoneImpl zone = new ZoneImpl();
		return zone;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public StackedchartsPackage getStackedchartsPackage() {
		return (StackedchartsPackage) getEPackage();
	}

} // StackedchartsFactoryImpl
