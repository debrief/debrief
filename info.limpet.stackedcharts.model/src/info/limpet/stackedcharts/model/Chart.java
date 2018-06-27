/**
 */
package info.limpet.stackedcharts.model;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Chart</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.Chart#getParent <em>Parent</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.Chart#getMaxAxes <em>Max Axes</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.Chart#getName <em>Name</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.Chart#getTitle <em>Title</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.Chart#getMinAxes <em>Min Axes</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getChart()
 * @model
 * @generated
 */
public interface Chart extends EObject
{
  /**
   * Returns the value of the '<em><b>Parent</b></em>' container reference. It is bidirectional and
   * its opposite is '{@link info.limpet.stackedcharts.model.ChartSet#getCharts <em>Charts</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parent</em>' container reference isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Parent</em>' container reference.
   * @see #setParent(ChartSet)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getChart_Parent()
   * @see info.limpet.stackedcharts.model.ChartSet#getCharts
   * @model opposite="charts" transient="false"
   * @generated
   */
  ChartSet getParent();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Chart#getParent <em>Parent</em>}'
   * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Parent</em>' container reference.
   * @see #getParent()
   * @generated
   */
  void setParent(ChartSet value);

  /**
   * Returns the value of the '<em><b>Max Axes</b></em>' containment reference list. The list
   * contents are of type {@link info.limpet.stackedcharts.model.DependentAxis}. <!-- begin-user-doc
   * -->
   * <p>
   * If the meaning of the '<em>Max Axes</em>' containment reference list isn't clear, there really
   * should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Max Axes</em>' containment reference list.
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getChart_MaxAxes()
   * @model containment="true"
   * @generated
   */
  EList<DependentAxis> getMaxAxes();

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
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getChart_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Chart#getName <em>Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Title</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Title</em>' attribute isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Title</em>' attribute.
   * @see #setTitle(String)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getChart_Title()
   * @model
   * @generated
   */
  String getTitle();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Chart#getTitle <em>Title</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Title</em>' attribute.
   * @see #getTitle()
   * @generated
   */
  void setTitle(String value);

  /**
   * Returns the value of the '<em><b>Min Axes</b></em>' containment reference list. The list
   * contents are of type {@link info.limpet.stackedcharts.model.DependentAxis}. <!-- begin-user-doc
   * -->
   * <p>
   * If the meaning of the '<em>Min Axes</em>' containment reference list isn't clear, there really
   * should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Min Axes</em>' containment reference list.
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getChart_MinAxes()
   * @model containment="true"
   * @generated
   */
  EList<DependentAxis> getMinAxes();

} // Chart
