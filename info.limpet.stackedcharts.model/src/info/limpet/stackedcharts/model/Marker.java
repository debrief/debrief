/**
 */
package info.limpet.stackedcharts.model;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Marker</b></em>'. <!--
 * end-user-doc -->
 *
 * <!-- begin-model-doc --> Single "discrete" marker on independent axis <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.Marker#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getMarker()
 * @model
 * @generated
 */
public interface Marker extends AbstractAnnotation
{
  /**
   * Returns the value of the '<em><b>Value</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Value</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Value</em>' attribute.
   * @see #setValue(double)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getMarker_Value()
   * @model
   * @generated
   */
  double getValue();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Marker#getValue <em>Value</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Value</em>' attribute.
   * @see #getValue()
   * @generated
   */
  void setValue(double value);

} // Marker
