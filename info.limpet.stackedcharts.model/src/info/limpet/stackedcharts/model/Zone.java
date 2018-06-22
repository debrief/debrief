/**
 */
package info.limpet.stackedcharts.model;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Zone</b></em>'. <!--
 * end-user-doc -->
 *
 * <!-- begin-model-doc --> Zonal marker on independent axis <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.Zone#getStart <em>Start</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.Zone#getEnd <em>End</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getZone()
 * @model
 * @generated
 */
public interface Zone extends AbstractAnnotation
{
  /**
   * Returns the value of the '<em><b>Start</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Start</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Start</em>' attribute.
   * @see #setStart(double)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getZone_Start()
   * @model
   * @generated
   */
  double getStart();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Zone#getStart <em>Start</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Start</em>' attribute.
   * @see #getStart()
   * @generated
   */
  void setStart(double value);

  /**
   * Returns the value of the '<em><b>End</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>End</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>End</em>' attribute.
   * @see #setEnd(double)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getZone_End()
   * @model
   * @generated
   */
  double getEnd();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Zone#getEnd <em>End</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>End</em>' attribute.
   * @see #getEnd()
   * @generated
   */
  void setEnd(double value);

} // Zone
