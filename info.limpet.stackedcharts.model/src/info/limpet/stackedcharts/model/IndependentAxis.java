/**
 */
package info.limpet.stackedcharts.model;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Independent Axis</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.IndependentAxis#getAnnotations
 * <em>Annotations</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getIndependentAxis()
 * @model
 * @generated
 */
public interface IndependentAxis extends AbstractAxis
{
  /**
   * Returns the value of the '<em><b>Annotations</b></em>' containment reference list. The list
   * contents are of type {@link info.limpet.stackedcharts.model.SelectiveAnnotation}. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Annotations</em>' containment reference list isn't clear, there
   * really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Annotations</em>' containment reference list.
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getIndependentAxis_Annotations()
   * @model containment="true"
   * @generated
   */
  EList<SelectiveAnnotation> getAnnotations();

} // IndependentAxis
