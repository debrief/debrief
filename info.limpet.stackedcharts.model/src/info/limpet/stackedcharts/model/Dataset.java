/**
 */
package info.limpet.stackedcharts.model;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Dataset</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.Dataset#getName <em>Name</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.Dataset#getMeasurements <em>Measurements</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.Dataset#getStyling <em>Styling</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.Dataset#getUnits <em>Units</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDataset()
 * @model
 * @generated
 */
public interface Dataset extends EObject
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
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDataset_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Dataset#getName <em>Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Measurements</b></em>' containment reference list. The list
   * contents are of type {@link info.limpet.stackedcharts.model.DataItem}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Measurements</em>' containment reference list isn't clear, there
   * really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Measurements</em>' containment reference list.
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDataset_Measurements()
   * @model containment="true"
   * @generated
   */
  EList<DataItem> getMeasurements();

  /**
   * Returns the value of the '<em><b>Styling</b></em>' containment reference. <!-- begin-user-doc
   * -->
   * <p>
   * If the meaning of the '<em>Styling</em>' containment reference isn't clear, there really should
   * be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Styling</em>' containment reference.
   * @see #setStyling(Styling)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDataset_Styling()
   * @model containment="true" required="true"
   * @generated
   */
  Styling getStyling();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Dataset#getStyling
   * <em>Styling</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Styling</em>' containment reference.
   * @see #getStyling()
   * @generated
   */
  void setStyling(Styling value);

  /**
   * Returns the value of the '<em><b>Units</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc --> <!-- begin-model-doc --> the units used for measurements in this dataset <!--
   * end-model-doc -->
   * 
   * @return the value of the '<em>Units</em>' attribute.
   * @see #setUnits(String)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDataset_Units()
   * @model
   * @generated
   */
  String getUnits();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Dataset#getUnits <em>Units</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Units</em>' attribute.
   * @see #getUnits()
   * @generated
   */
  void setUnits(String value);

} // Dataset
