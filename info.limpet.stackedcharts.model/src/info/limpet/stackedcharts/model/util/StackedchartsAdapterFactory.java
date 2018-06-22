/**
 */
package info.limpet.stackedcharts.model.util;

import info.limpet.stackedcharts.model.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see info.limpet.stackedcharts.model.StackedchartsPackage
 * @generated
 */
public class StackedchartsAdapterFactory extends AdapterFactoryImpl
{
  /**
	 * The cached model package.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  protected static StackedchartsPackage modelPackage;

  /**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  public StackedchartsAdapterFactory()
  {
		if (modelPackage == null) {
			modelPackage = StackedchartsPackage.eINSTANCE;
		}
	}

  /**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
   * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
   * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
  @Override
  public boolean isFactoryForType(Object object)
  {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

  /**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @generated
	 */
  protected StackedchartsSwitch<Adapter> modelSwitch =
    new StackedchartsSwitch<Adapter>() {
			@Override
			public Adapter caseChartSet(ChartSet object) {
				return createChartSetAdapter();
			}
			@Override
			public Adapter caseChart(Chart object) {
				return createChartAdapter();
			}
			@Override
			public Adapter caseDependentAxis(DependentAxis object) {
				return createDependentAxisAdapter();
			}
			@Override
			public Adapter caseDataset(Dataset object) {
				return createDatasetAdapter();
			}
			@Override
			public Adapter caseDataItem(DataItem object) {
				return createDataItemAdapter();
			}
			@Override
			public Adapter caseAbstractAnnotation(AbstractAnnotation object) {
				return createAbstractAnnotationAdapter();
			}
			@Override
			public Adapter caseZone(Zone object) {
				return createZoneAdapter();
			}
			@Override
			public Adapter caseMarker(Marker object) {
				return createMarkerAdapter();
			}
			@Override
			public Adapter caseStyling(Styling object) {
				return createStylingAdapter();
			}
			@Override
			public Adapter casePlainStyling(PlainStyling object) {
				return createPlainStylingAdapter();
			}
			@Override
			public Adapter caseLinearStyling(LinearStyling object) {
				return createLinearStylingAdapter();
			}
			@Override
			public Adapter caseAbstractAxis(AbstractAxis object) {
				return createAbstractAxisAdapter();
			}
			@Override
			public Adapter caseIndependentAxis(IndependentAxis object) {
				return createIndependentAxisAdapter();
			}
			@Override
			public Adapter caseScatterSet(ScatterSet object) {
				return createScatterSetAdapter();
			}
			@Override
			public Adapter caseDatum(Datum object) {
				return createDatumAdapter();
			}
			@Override
			public Adapter caseSelectiveAnnotation(SelectiveAnnotation object) {
				return createSelectiveAnnotationAdapter();
			}
			@Override
			public Adapter caseAxisType(AxisType object) {
				return createAxisTypeAdapter();
			}
			@Override
			public Adapter caseDateAxis(DateAxis object) {
				return createDateAxisAdapter();
			}
			@Override
			public Adapter caseNumberAxis(NumberAxis object) {
				return createNumberAxisAdapter();
			}
			@Override
			public Adapter caseAngleAxis(AngleAxis object) {
				return createAngleAxisAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

  /**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
  @Override
  public Adapter createAdapter(Notifier target)
  {
		return modelSwitch.doSwitch((EObject)target);
	}


  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.ChartSet <em>Chart Set</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.ChartSet
	 * @generated
	 */
  public Adapter createChartSetAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.Chart <em>Chart</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.Chart
	 * @generated
	 */
  public Adapter createChartAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.DependentAxis <em>Dependent Axis</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.DependentAxis
	 * @generated
	 */
  public Adapter createDependentAxisAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.Dataset <em>Dataset</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.Dataset
	 * @generated
	 */
  public Adapter createDatasetAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.DataItem <em>Data Item</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.DataItem
	 * @generated
	 */
  public Adapter createDataItemAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.AbstractAnnotation <em>Abstract Annotation</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.AbstractAnnotation
	 * @generated
	 */
  public Adapter createAbstractAnnotationAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.Zone <em>Zone</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.Zone
	 * @generated
	 */
  public Adapter createZoneAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.Marker <em>Marker</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.Marker
	 * @generated
	 */
  public Adapter createMarkerAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.Styling <em>Styling</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.Styling
	 * @generated
	 */
  public Adapter createStylingAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.PlainStyling <em>Plain Styling</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.PlainStyling
	 * @generated
	 */
  public Adapter createPlainStylingAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.LinearStyling <em>Linear Styling</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.LinearStyling
	 * @generated
	 */
  public Adapter createLinearStylingAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.AbstractAxis <em>Abstract Axis</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.AbstractAxis
	 * @generated
	 */
  public Adapter createAbstractAxisAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.IndependentAxis <em>Independent Axis</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.IndependentAxis
	 * @generated
	 */
  public Adapter createIndependentAxisAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.ScatterSet <em>Scatter Set</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.ScatterSet
	 * @generated
	 */
  public Adapter createScatterSetAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.Datum <em>Datum</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.Datum
	 * @generated
	 */
  public Adapter createDatumAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.SelectiveAnnotation <em>Selective Annotation</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.SelectiveAnnotation
	 * @generated
	 */
  public Adapter createSelectiveAnnotationAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.AxisType <em>Axis Type</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.AxisType
	 * @generated
	 */
  public Adapter createAxisTypeAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.DateAxis <em>Date Axis</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.DateAxis
	 * @generated
	 */
  public Adapter createDateAxisAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.NumberAxis <em>Number Axis</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.NumberAxis
	 * @generated
	 */
  public Adapter createNumberAxisAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for an object of class '{@link info.limpet.stackedcharts.model.AngleAxis <em>Angle Axis</em>}'.
	 * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see info.limpet.stackedcharts.model.AngleAxis
	 * @generated
	 */
  public Adapter createAngleAxisAdapter()
  {
		return null;
	}

  /**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
   * This default implementation returns null.
   * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
  public Adapter createEObjectAdapter()
  {
		return null;
	}

} //StackedchartsAdapterFactory
