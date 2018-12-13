/**
 */
package info.limpet.stackedcharts.model;

import java.awt.Color;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Abstract Axis</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.AbstractAxis#getScale <em>Scale</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.AbstractAxis#getName <em>Name</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.AbstractAxis#isAutoScale <em>Auto Scale</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.AbstractAxis#getDirection <em>Direction</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.AbstractAxis#getFont <em>Font</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.AbstractAxis#getColor <em>Color</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.AbstractAxis#getAxisType <em>Axis Type</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAbstractAxis()
 * @model abstract="true"
 * @generated
 */
public interface AbstractAxis extends EObject
{
  /**
   * Returns the value of the '<em><b>Scale</b></em>' attribute. The literals are from the
   * enumeration {@link info.limpet.stackedcharts.model.AxisScale}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Scale</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Scale</em>' attribute.
   * @see info.limpet.stackedcharts.model.AxisScale
   * @see #setScale(AxisScale)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAbstractAxis_Scale()
   * @model
   * @generated
   */
  AxisScale getScale();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.AbstractAxis#getScale
   * <em>Scale</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Scale</em>' attribute.
   * @see info.limpet.stackedcharts.model.AxisScale
   * @see #getScale()
   * @generated
   */
  void setScale(AxisScale value);

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAbstractAxis_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.AbstractAxis#getName
   * <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Auto Scale</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Auto Scale</em>' attribute isn't clear, there really should be more
   * of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Auto Scale</em>' attribute.
   * @see #setAutoScale(boolean)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAbstractAxis_AutoScale()
   * @model dataType="org.eclipse.emf.ecore.xml.type.Boolean"
   * @generated
   */
  boolean isAutoScale();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.AbstractAxis#isAutoScale <em>Auto
   * Scale</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Auto Scale</em>' attribute.
   * @see #isAutoScale()
   * @generated
   */
  void setAutoScale(boolean value);

  /**
   * Returns the value of the '<em><b>Direction</b></em>' attribute. The literals are from the
   * enumeration {@link info.limpet.stackedcharts.model.AxisDirection}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Direction</em>' attribute isn't clear, there really should be more
   * of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Direction</em>' attribute.
   * @see info.limpet.stackedcharts.model.AxisDirection
   * @see #setDirection(AxisDirection)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAbstractAxis_Direction()
   * @model
   * @generated
   */
  AxisDirection getDirection();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.AbstractAxis#getDirection
   * <em>Direction</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Direction</em>' attribute.
   * @see info.limpet.stackedcharts.model.AxisDirection
   * @see #getDirection()
   * @generated
   */
  void setDirection(AxisDirection value);

  /**
   * Returns the value of the '<em><b>Font</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Font</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Font</em>' attribute.
   * @see #setFont(String)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAbstractAxis_Font()
   * @model
   * @generated
   */
  String getFont();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.AbstractAxis#getFont
   * <em>Font</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Font</em>' attribute.
   * @see #getFont()
   */
  void setFont(String value);

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
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAbstractAxis_Color()
   * @model dataType="info.limpet.stackedcharts.model.Color"
   * @generated
   */
  Color getColor();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.AbstractAxis#getColor
   * <em>Color</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Color</em>' attribute.
   * @see #getColor()
   */
  void setColor(Color value);

  /**
   * Returns the value of the '<em><b>Axis Type</b></em>' containment reference. <!-- begin-user-doc
   * -->
   * <p>
   * If the meaning of the '<em>Axis Type</em>' containment reference isn't clear, there really
   * should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Axis Type</em>' containment reference.
   * @see #setAxisType(AxisType)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAbstractAxis_AxisType()
   */
  AxisType getAxisType();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.AbstractAxis#getAxisType <em>Axis
   * Type</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Axis Type</em>' containment reference.
   * @see #getAxisType()
   */
  void setAxisType(AxisType value);

} // AbstractAxis
