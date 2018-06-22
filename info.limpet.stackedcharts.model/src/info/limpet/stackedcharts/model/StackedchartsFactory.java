/**
 */
package info.limpet.stackedcharts.model;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each
 * non-abstract class of the model. <!-- end-user-doc -->
 * 
 * @see info.limpet.stackedcharts.model.StackedchartsPackage
 * @generated
 */
public interface StackedchartsFactory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  StackedchartsFactory eINSTANCE =
      info.limpet.stackedcharts.model.impl.StackedchartsFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Chart Set</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @return a new object of class '<em>Chart Set</em>'.
   * @generated
   */
  ChartSet createChartSet();

  /**
   * Returns a new object of class '<em>Chart</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Chart</em>'.
   * @generated
   */
  Chart createChart();

  /**
   * Returns a new object of class '<em>Dependent Axis</em>'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return a new object of class '<em>Dependent Axis</em>'.
   * @generated
   */
  DependentAxis createDependentAxis();

  /**
   * Returns a new object of class '<em>Dataset</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Dataset</em>'.
   * @generated
   */
  Dataset createDataset();

  /**
   * Returns a new object of class '<em>Data Item</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @return a new object of class '<em>Data Item</em>'.
   * @generated
   */
  DataItem createDataItem();

  /**
   * Returns a new object of class '<em>Zone</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Zone</em>'.
   * @generated
   */
  Zone createZone();

  /**
   * Returns a new object of class '<em>Marker</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Marker</em>'.
   * @generated
   */
  Marker createMarker();

  /**
   * Returns a new object of class '<em>Styling</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Styling</em>'.
   * @generated
   */
  Styling createStyling();

  /**
   * Returns a new object of class '<em>Plain Styling</em>'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return a new object of class '<em>Plain Styling</em>'.
   * @generated
   */
  PlainStyling createPlainStyling();

  /**
   * Returns a new object of class '<em>Linear Styling</em>'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return a new object of class '<em>Linear Styling</em>'.
   * @generated
   */
  LinearStyling createLinearStyling();

  /**
   * Returns a new object of class '<em>Independent Axis</em>'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return a new object of class '<em>Independent Axis</em>'.
   * @generated
   */
  IndependentAxis createIndependentAxis();

  /**
   * Returns a new object of class '<em>Scatter Set</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @return a new object of class '<em>Scatter Set</em>'.
   * @generated
   */
  ScatterSet createScatterSet();

  /**
   * Returns a new object of class '<em>Datum</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>Datum</em>'.
   * @generated
   */
  Datum createDatum();

  /**
   * Returns a new object of class '<em>Selective Annotation</em>'. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @return a new object of class '<em>Selective Annotation</em>'.
   * @generated
   */
  SelectiveAnnotation createSelectiveAnnotation();

  /**
   * Returns a new object of class '<em>Date Axis</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @return a new object of class '<em>Date Axis</em>'.
   * @generated
   */
  DateAxis createDateAxis();

  /**
   * Returns a new object of class '<em>Number Axis</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @return a new object of class '<em>Number Axis</em>'.
   * @generated
   */
  NumberAxis createNumberAxis();

  /**
   * Returns a new object of class '<em>Angle Axis</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @return a new object of class '<em>Angle Axis</em>'.
   * @generated
   */
  AngleAxis createAngleAxis();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  StackedchartsPackage getStackedchartsPackage();

} // StackedchartsFactory
