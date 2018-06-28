/**
 */
package info.limpet.stackedcharts.model;

import java.awt.Color;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Plain Styling</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.PlainStyling#getColor <em>Color</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getPlainStyling()
 * @model
 * @generated
 */
public interface PlainStyling extends Styling
{
  /**
   * Returns the value of the '<em><b>Color</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Color</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Color</em>' attribute.
   * @see #setColor(Color)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getPlainStyling_Color()
   * @model dataType="info.limpet.stackedcharts.model.Color"
   * @generated
   */
  Color getColor();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.PlainStyling#getColor
   * <em>Color</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Color</em>' attribute.
   * @see #getColor()
   * @generated
   */
  void setColor(Color value);

} // PlainStyling
