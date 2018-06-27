/**
 */
package info.limpet.stackedcharts.model;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Angle Axis</b></em>'. <!--
 * end-user-doc -->
 *
 * <!-- begin-model-doc --> Axis that wraps around at a particular value - used with cyclic
 * dimensions such as angles
 * 
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.AngleAxis#getMinVal <em>Min Val</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.AngleAxis#getMaxVal <em>Max Val</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.AngleAxis#isMidOrigin <em>Mid Origin</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.AngleAxis#isRedGreen <em>Red Green</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAngleAxis()
 * @model
 * @generated
 */
public interface AngleAxis extends NumberAxis
{

  /**
   * Returns the value of the '<em><b>Min Val</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Min Val</em>' attribute isn't clear, there really should be more of
   * a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Min Val</em>' attribute.
   * @see #setMinVal(double)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAngleAxis_MinVal()
   * @model dataType="org.eclipse.emf.ecore.xml.type.Double"
   * @generated
   */
  double getMinVal();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.AngleAxis#getMinVal <em>Min
   * Val</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Min Val</em>' attribute.
   * @see #getMinVal()
   * @generated
   */
  void setMinVal(double value);

  /**
   * Returns the value of the '<em><b>Max Val</b></em>' attribute. The default value is
   * <code>"0.0"</code>. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Max Val</em>' attribute isn't clear, there really should be more of
   * a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Max Val</em>' attribute.
   * @see #setMaxVal(double)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAngleAxis_MaxVal()
   * @model default="0.0" dataType="org.eclipse.emf.ecore.xml.type.Double"
   * @generated
   */
  double getMaxVal();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.AngleAxis#getMaxVal <em>Max
   * Val</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Max Val</em>' attribute.
   * @see #getMaxVal()
   * @generated
   */
  void setMaxVal(double value);

  /**
   * Returns the value of the '<em><b>Mid Origin</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc --> <!-- begin-model-doc --> Whether to position the origin at the mid-point of
   * the range <!-- end-model-doc -->
   * 
   * @return the value of the '<em>Mid Origin</em>' attribute.
   * @see #setMidOrigin(boolean)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAngleAxis_MidOrigin()
   * @model dataType="org.eclipse.emf.ecore.xml.type.Boolean"
   * @generated
   */
  boolean isMidOrigin();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.AngleAxis#isMidOrigin <em>Mid
   * Origin</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Mid Origin</em>' attribute.
   * @see #isMidOrigin()
   * @generated
   */
  void setMidOrigin(boolean value);

  /**
   * Returns the value of the '<em><b>Red Green</b></em>' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc --> <!-- begin-model-doc --> Whether to display angle using RED for -ve and GREEN
   * for +ve
   * 
   * <!-- end-model-doc -->
   * 
   * @return the value of the '<em>Red Green</em>' attribute.
   * @see #setRedGreen(boolean)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getAngleAxis_RedGreen()
   * @model dataType="org.eclipse.emf.ecore.xml.type.Boolean"
   * @generated
   */
  boolean isRedGreen();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.AngleAxis#isRedGreen <em>Red
   * Green</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Red Green</em>' attribute.
   * @see #isRedGreen()
   * @generated
   */
  void setRedGreen(boolean value);
} // AngleAxis
