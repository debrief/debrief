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
package info.limpet.stackedcharts.model.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

import info.limpet.stackedcharts.model.AbstractAnnotation;
import info.limpet.stackedcharts.model.AbstractAxis;
import info.limpet.stackedcharts.model.AngleAxis;
import info.limpet.stackedcharts.model.AxisType;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DateAxis;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.LinearStyling;
import info.limpet.stackedcharts.model.Marker;
import info.limpet.stackedcharts.model.NumberAxis;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.ScatterSet;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.model.Styling;
import info.limpet.stackedcharts.model.Zone;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance
 * hierarchy. It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object and proceeding up the
 * inheritance hierarchy until a non-null result is returned, which is the
 * result of the switch. <!-- end-user-doc -->
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage
 * @generated
 */
public class StackedchartsSwitch<T> extends Switch<T> {
	/**
	 * The cached model package <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected static StackedchartsPackage modelPackage;

	/**
	 * Creates an instance of the switch. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 *
	 * @generated
	 */
	public StackedchartsSwitch() {
		if (modelPackage == null) {
			modelPackage = StackedchartsPackage.eINSTANCE;
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Abstract
	 * Annotation</em>'. <!-- begin-user-doc --> This implementation returns null;
	 * returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Abstract
	 *         Annotation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAbstractAnnotation(final AbstractAnnotation object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Abstract
	 * Axis</em>'. <!-- begin-user-doc --> This implementation returns null;
	 * returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Abstract
	 *         Axis</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAbstractAxis(final AbstractAxis object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Angle
	 * Axis</em>'. <!-- begin-user-doc --> This implementation returns null;
	 * returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Angle
	 *         Axis</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAngleAxis(final AngleAxis object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Axis
	 * Type</em>'. <!-- begin-user-doc --> This implementation returns null;
	 * returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Axis
	 *         Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAxisType(final AxisType object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of
	 * '<em>Chart</em>'. <!-- begin-user-doc --> This implementation returns null;
	 * returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of
	 *         '<em>Chart</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseChart(final Chart object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Chart
	 * Set</em>'. <!-- begin-user-doc --> This implementation returns null;
	 * returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Chart
	 *         Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseChartSet(final ChartSet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Data
	 * Item</em>'. <!-- begin-user-doc --> This implementation returns null;
	 * returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Data
	 *         Item</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDataItem(final DataItem object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of
	 * '<em>Dataset</em>'. <!-- begin-user-doc --> This implementation returns null;
	 * returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of
	 *         '<em>Dataset</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDataset(final Dataset object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Date
	 * Axis</em>'. <!-- begin-user-doc --> This implementation returns null;
	 * returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Date
	 *         Axis</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDateAxis(final DateAxis object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of
	 * '<em>Datum</em>'. <!-- begin-user-doc --> This implementation returns null;
	 * returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of
	 *         '<em>Datum</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDatum(final Datum object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of
	 * '<em>Dependent Axis</em>'. <!-- begin-user-doc --> This implementation
	 * returns null; returning a non-null result will terminate the switch. <!--
	 * end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of
	 *         '<em>Dependent Axis</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDependentAxis(final DependentAxis object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of
	 * '<em>Independent Axis</em>'. <!-- begin-user-doc --> This implementation
	 * returns null; returning a non-null result will terminate the switch. <!--
	 * end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of
	 *         '<em>Independent Axis</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseIndependentAxis(final IndependentAxis object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Linear
	 * Styling</em>'. <!-- begin-user-doc --> This implementation returns null;
	 * returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Linear
	 *         Styling</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLinearStyling(final LinearStyling object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of
	 * '<em>Marker</em>'. <!-- begin-user-doc --> This implementation returns null;
	 * returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of
	 *         '<em>Marker</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMarker(final Marker object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Number
	 * Axis</em>'. <!-- begin-user-doc --> This implementation returns null;
	 * returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Number
	 *         Axis</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNumberAxis(final NumberAxis object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Plain
	 * Styling</em>'. <!-- begin-user-doc --> This implementation returns null;
	 * returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Plain
	 *         Styling</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePlainStyling(final PlainStyling object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Scatter
	 * Set</em>'. <!-- begin-user-doc --> This implementation returns null;
	 * returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Scatter
	 *         Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseScatterSet(final ScatterSet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of
	 * '<em>Selective Annotation</em>'. <!-- begin-user-doc --> This implementation
	 * returns null; returning a non-null result will terminate the switch. <!--
	 * end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of
	 *         '<em>Selective Annotation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSelectiveAnnotation(final SelectiveAnnotation object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of
	 * '<em>Styling</em>'. <!-- begin-user-doc --> This implementation returns null;
	 * returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of
	 *         '<em>Styling</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStyling(final Styling object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of
	 * '<em>Zone</em>'. <!-- begin-user-doc --> This implementation returns null;
	 * returning a non-null result will terminate the switch. <!-- end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of
	 *         '<em>Zone</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseZone(final Zone object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of
	 * '<em>EObject</em>'. <!-- begin-user-doc --> This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last
	 * case anyway. <!-- end-user-doc -->
	 *
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of
	 *         '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(final EObject object) {
		return null;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a
	 * non null result; it yields that result. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(final int classifierID, final EObject theEObject) {
		switch (classifierID) {
		case StackedchartsPackage.CHART_SET: {
			final ChartSet chartSet = (ChartSet) theEObject;
			T result = caseChartSet(chartSet);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.CHART: {
			final Chart chart = (Chart) theEObject;
			T result = caseChart(chart);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.DEPENDENT_AXIS: {
			final DependentAxis dependentAxis = (DependentAxis) theEObject;
			T result = caseDependentAxis(dependentAxis);
			if (result == null)
				result = caseAbstractAxis(dependentAxis);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.DATASET: {
			final Dataset dataset = (Dataset) theEObject;
			T result = caseDataset(dataset);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.DATA_ITEM: {
			final DataItem dataItem = (DataItem) theEObject;
			T result = caseDataItem(dataItem);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.ABSTRACT_ANNOTATION: {
			final AbstractAnnotation abstractAnnotation = (AbstractAnnotation) theEObject;
			T result = caseAbstractAnnotation(abstractAnnotation);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.ZONE: {
			final Zone zone = (Zone) theEObject;
			T result = caseZone(zone);
			if (result == null)
				result = caseAbstractAnnotation(zone);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.MARKER: {
			final Marker marker = (Marker) theEObject;
			T result = caseMarker(marker);
			if (result == null)
				result = caseAbstractAnnotation(marker);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.STYLING: {
			final Styling styling = (Styling) theEObject;
			T result = caseStyling(styling);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.PLAIN_STYLING: {
			final PlainStyling plainStyling = (PlainStyling) theEObject;
			T result = casePlainStyling(plainStyling);
			if (result == null)
				result = caseStyling(plainStyling);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.LINEAR_STYLING: {
			final LinearStyling linearStyling = (LinearStyling) theEObject;
			T result = caseLinearStyling(linearStyling);
			if (result == null)
				result = caseStyling(linearStyling);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.ABSTRACT_AXIS: {
			final AbstractAxis abstractAxis = (AbstractAxis) theEObject;
			T result = caseAbstractAxis(abstractAxis);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.INDEPENDENT_AXIS: {
			final IndependentAxis independentAxis = (IndependentAxis) theEObject;
			T result = caseIndependentAxis(independentAxis);
			if (result == null)
				result = caseAbstractAxis(independentAxis);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.SCATTER_SET: {
			final ScatterSet scatterSet = (ScatterSet) theEObject;
			T result = caseScatterSet(scatterSet);
			if (result == null)
				result = caseAbstractAnnotation(scatterSet);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.DATUM: {
			final Datum datum = (Datum) theEObject;
			T result = caseDatum(datum);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.SELECTIVE_ANNOTATION: {
			final SelectiveAnnotation selectiveAnnotation = (SelectiveAnnotation) theEObject;
			T result = caseSelectiveAnnotation(selectiveAnnotation);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.AXIS_TYPE: {
			final AxisType axisType = (AxisType) theEObject;
			T result = caseAxisType(axisType);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.DATE_AXIS: {
			final DateAxis dateAxis = (DateAxis) theEObject;
			T result = caseDateAxis(dateAxis);
			if (result == null)
				result = caseAxisType(dateAxis);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.NUMBER_AXIS: {
			final NumberAxis numberAxis = (NumberAxis) theEObject;
			T result = caseNumberAxis(numberAxis);
			if (result == null)
				result = caseAxisType(numberAxis);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case StackedchartsPackage.ANGLE_AXIS: {
			final AngleAxis angleAxis = (AngleAxis) theEObject;
			T result = caseAngleAxis(angleAxis);
			if (result == null)
				result = caseNumberAxis(angleAxis);
			if (result == null)
				result = caseAxisType(angleAxis);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		default:
			return defaultCase(theEObject);
		}
	}

	/**
	 * Checks whether this is a switch for the given package. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 *
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(final EPackage ePackage) {
		return ePackage == modelPackage;
	}

} // StackedchartsSwitch
