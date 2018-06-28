/**
 */
package info.limpet.stackedcharts.model;

import java.awt.Color;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Abstract
 * Annotation</b></em>'. <!-- end-user-doc -->
 *
 * <!-- begin-model-doc --> One dimensional data value, to be presented as either a thin line, or a
 * zone/area. <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.AbstractAnnotation#getName <em>Name</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.AbstractAnnotation#getColor <em>Color</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.AbstractAnnotation#isIncludeInLegend <em>Include In
 * Legend</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAbstractAnnotation()
 * @model abstract="true"
 * @generated
 */
public interface AbstractAnnotation extends EObject
{
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
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAbstractAnnotation_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.AbstractAnnotation#getName
   * <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

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
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAbstractAnnotation_Color()
   * @model dataType="info.limpet.stackedcharts.model.Color"
   * @generated
   */
  Color getColor();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.AbstractAnnotation#getColor
   * <em>Color</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Color</em>' attribute.
   * @see #getColor()
   * @generated
   */
  void setColor(Color value);

  /**
   * Returns the value of the '<em><b>Include In Legend</b></em>' attribute. The default value is
   * <code>"true"</code>. <!-- begin-user-doc --> <!-- end-user-doc --> <!-- begin-model-doc -->
   * Whether to show this dataset in the Legend <!-- end-model-doc -->
   * 
   * @return the value of the '<em>Include In Legend</em>' attribute.
   * @see #setIncludeInLegend(boolean)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAbstractAnnotation_IncludeInLegend()
   * @model default="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
   * @generated
   */
  boolean isIncludeInLegend();

  /**
   * Sets the value of the
   * '{@link info.limpet.stackedcharts.model.AbstractAnnotation#isIncludeInLegend <em>Include In
   * Legend</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Include In Legend</em>' attribute.
   * @see #isIncludeInLegend()
   * @generated
   */
  void setIncludeInLegend(boolean value);

} // AbstractAnnotation
