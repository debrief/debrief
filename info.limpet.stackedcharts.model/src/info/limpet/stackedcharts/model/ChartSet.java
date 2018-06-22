/**
 */
package info.limpet.stackedcharts.model;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Chart Set</b></em>'. <!--
 * end-user-doc -->
 *
 * <!-- begin-model-doc --> A stack of charts, sharing a single independent axis. <!-- end-model-doc
 * -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.ChartSet#getCharts <em>Charts</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.ChartSet#getOrientation <em>Orientation</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.ChartSet#getSharedAxis <em>Shared Axis</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getChartSet()
 * @model
 * @generated
 */
public interface ChartSet extends EObject
{
  /**
   * Returns the value of the '<em><b>Charts</b></em>' containment reference list. The list contents
   * are of type {@link info.limpet.stackedcharts.model.Chart}. It is bidirectional and its opposite
   * is '{@link info.limpet.stackedcharts.model.Chart#getParent <em>Parent</em>}'. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Charts</em>' containment reference list isn't clear, there really
   * should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Charts</em>' containment reference list.
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getChartSet_Charts()
   * @see info.limpet.stackedcharts.model.Chart#getParent
   * @model opposite="parent" containment="true" extendedMetaData="name='chart'"
   * @generated
   */
  EList<Chart> getCharts();

  /**
   * Returns the value of the '<em><b>Orientation</b></em>' attribute. The default value is
   * <code>"Vertical"</code>. The literals are from the enumeration
   * {@link info.limpet.stackedcharts.model.Orientation}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Orientation</em>' attribute isn't clear, there really should be more
   * of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Orientation</em>' attribute.
   * @see info.limpet.stackedcharts.model.Orientation
   * @see #setOrientation(Orientation)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getChartSet_Orientation()
   * @model default="Vertical"
   * @generated
   */
  Orientation getOrientation();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.ChartSet#getOrientation
   * <em>Orientation</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Orientation</em>' attribute.
   * @see info.limpet.stackedcharts.model.Orientation
   * @see #getOrientation()
   * @generated
   */
  void setOrientation(Orientation value);

  /**
   * Returns the value of the '<em><b>Shared Axis</b></em>' containment reference. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Shared Axis</em>' containment reference isn't clear, there really
   * should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Shared Axis</em>' containment reference.
   * @see #setSharedAxis(IndependentAxis)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getChartSet_SharedAxis()
   * @model containment="true"
   * @generated
   */
  IndependentAxis getSharedAxis();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.ChartSet#getSharedAxis <em>Shared
   * Axis</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Shared Axis</em>' containment reference.
   * @see #getSharedAxis()
   * @generated
   */
  void setSharedAxis(IndependentAxis value);

} // ChartSet
