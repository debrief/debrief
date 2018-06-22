/**
 */
package info.limpet.stackedcharts.model;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Date Axis</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.DateAxis#getDateFormat <em>Date Format</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDateAxis()
 * @model
 * @generated
 */
public interface DateAxis extends AxisType
{
  /**
   * Returns the value of the '<em><b>Date Format</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Date Format</em>' attribute isn't clear, there really should be more
   * of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Date Format</em>' attribute.
   * @see #setDateFormat(String)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDateAxis_DateFormat()
   * @model dataType="org.eclipse.emf.ecore.xml.type.String"
   * @generated
   */
  String getDateFormat();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.DateAxis#getDateFormat <em>Date
   * Format</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Date Format</em>' attribute.
   * @see #getDateFormat()
   * @generated
   */
  void setDateFormat(String value);

} // DateAxis
