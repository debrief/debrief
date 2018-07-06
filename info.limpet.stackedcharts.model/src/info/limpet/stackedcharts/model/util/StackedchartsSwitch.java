/**
 */
package info.limpet.stackedcharts.model.util;

import info.limpet.stackedcharts.model.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see info.limpet.stackedcharts.model.StackedchartsPackage
 * @generated
 */
public class StackedchartsSwitch<T> extends Switch<T>
{
  /**
	 * The cached model package
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  protected static StackedchartsPackage modelPackage;

  /**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  public StackedchartsSwitch()
  {
		if (modelPackage == null) {
			modelPackage = StackedchartsPackage.eINSTANCE;
		}
	}

  /**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
  @Override
  protected boolean isSwitchFor(EPackage ePackage)
  {
		return ePackage == modelPackage;
	}

  /**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
  @Override
  protected T doSwitch(int classifierID, EObject theEObject)
  {
		switch (classifierID) {
			case StackedchartsPackage.CHART_SET: {
				ChartSet chartSet = (ChartSet)theEObject;
				T result = caseChartSet(chartSet);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.CHART: {
				Chart chart = (Chart)theEObject;
				T result = caseChart(chart);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.DEPENDENT_AXIS: {
				DependentAxis dependentAxis = (DependentAxis)theEObject;
				T result = caseDependentAxis(dependentAxis);
				if (result == null) result = caseAbstractAxis(dependentAxis);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.DATASET: {
				Dataset dataset = (Dataset)theEObject;
				T result = caseDataset(dataset);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.DATA_ITEM: {
				DataItem dataItem = (DataItem)theEObject;
				T result = caseDataItem(dataItem);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.ABSTRACT_ANNOTATION: {
				AbstractAnnotation abstractAnnotation = (AbstractAnnotation)theEObject;
				T result = caseAbstractAnnotation(abstractAnnotation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.ZONE: {
				Zone zone = (Zone)theEObject;
				T result = caseZone(zone);
				if (result == null) result = caseAbstractAnnotation(zone);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.MARKER: {
				Marker marker = (Marker)theEObject;
				T result = caseMarker(marker);
				if (result == null) result = caseAbstractAnnotation(marker);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.STYLING: {
				Styling styling = (Styling)theEObject;
				T result = caseStyling(styling);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.PLAIN_STYLING: {
				PlainStyling plainStyling = (PlainStyling)theEObject;
				T result = casePlainStyling(plainStyling);
				if (result == null) result = caseStyling(plainStyling);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.LINEAR_STYLING: {
				LinearStyling linearStyling = (LinearStyling)theEObject;
				T result = caseLinearStyling(linearStyling);
				if (result == null) result = caseStyling(linearStyling);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.ABSTRACT_AXIS: {
				AbstractAxis abstractAxis = (AbstractAxis)theEObject;
				T result = caseAbstractAxis(abstractAxis);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.INDEPENDENT_AXIS: {
				IndependentAxis independentAxis = (IndependentAxis)theEObject;
				T result = caseIndependentAxis(independentAxis);
				if (result == null) result = caseAbstractAxis(independentAxis);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.SCATTER_SET: {
				ScatterSet scatterSet = (ScatterSet)theEObject;
				T result = caseScatterSet(scatterSet);
				if (result == null) result = caseAbstractAnnotation(scatterSet);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.DATUM: {
				Datum datum = (Datum)theEObject;
				T result = caseDatum(datum);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.SELECTIVE_ANNOTATION: {
				SelectiveAnnotation selectiveAnnotation = (SelectiveAnnotation)theEObject;
				T result = caseSelectiveAnnotation(selectiveAnnotation);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.AXIS_TYPE: {
				AxisType axisType = (AxisType)theEObject;
				T result = caseAxisType(axisType);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.DATE_AXIS: {
				DateAxis dateAxis = (DateAxis)theEObject;
				T result = caseDateAxis(dateAxis);
				if (result == null) result = caseAxisType(dateAxis);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.NUMBER_AXIS: {
				NumberAxis numberAxis = (NumberAxis)theEObject;
				T result = caseNumberAxis(numberAxis);
				if (result == null) result = caseAxisType(numberAxis);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case StackedchartsPackage.ANGLE_AXIS: {
				AngleAxis angleAxis = (AngleAxis)theEObject;
				T result = caseAngleAxis(angleAxis);
				if (result == null) result = caseNumberAxis(angleAxis);
				if (result == null) result = caseAxisType(angleAxis);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Chart Set</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Chart Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseChartSet(ChartSet object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Chart</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Chart</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseChart(Chart object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Dependent Axis</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Dependent Axis</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseDependentAxis(DependentAxis object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Dataset</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Dataset</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseDataset(Dataset object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Data Item</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Data Item</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseDataItem(DataItem object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Abstract Annotation</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Abstract Annotation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseAbstractAnnotation(AbstractAnnotation object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Zone</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Zone</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseZone(Zone object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Marker</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Marker</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseMarker(Marker object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Styling</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Styling</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseStyling(Styling object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Plain Styling</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Plain Styling</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T casePlainStyling(PlainStyling object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Linear Styling</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Linear Styling</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseLinearStyling(LinearStyling object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Abstract Axis</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Abstract Axis</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseAbstractAxis(AbstractAxis object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Independent Axis</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Independent Axis</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseIndependentAxis(IndependentAxis object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Scatter Set</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Scatter Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseScatterSet(ScatterSet object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Datum</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Datum</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseDatum(Datum object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Selective Annotation</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Selective Annotation</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseSelectiveAnnotation(SelectiveAnnotation object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Axis Type</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Axis Type</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseAxisType(AxisType object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Date Axis</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Date Axis</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseDateAxis(DateAxis object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Number Axis</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Number Axis</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseNumberAxis(NumberAxis object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>Angle Axis</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Angle Axis</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
  public T caseAngleAxis(AngleAxis object)
  {
		return null;
	}

  /**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
  @Override
  public T defaultCase(EObject object)
  {
		return null;
	}

} //StackedchartsSwitch
