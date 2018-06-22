/**
 */
package info.limpet.stackedcharts.model;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Styling</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.Styling#getMarkerStyle <em>Marker Style</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.Styling#getMarkerSize <em>Marker Size</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.Styling#getLineThickness <em>Line Thickness</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.Styling#getLineStyle <em>Line Style</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.Styling#isIncludeInLegend <em>Include In
 * Legend</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getStyling()
 * @model
 * @generated
 */
public interface Styling extends EObject
{
  /**
   * Returns the value of the '<em><b>Marker Style</b></em>' attribute. The literals are from the
   * enumeration {@link info.limpet.stackedcharts.model.MarkerStyle}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Marker Style</em>' attribute isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Marker Style</em>' attribute.
   * @see info.limpet.stackedcharts.model.MarkerStyle
   * @see #setMarkerStyle(MarkerStyle)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getStyling_MarkerStyle()
   * @model
   * @generated
   */
  MarkerStyle getMarkerStyle();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Styling#getMarkerStyle <em>Marker
   * Style</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Marker Style</em>' attribute.
   * @see info.limpet.stackedcharts.model.MarkerStyle
   * @see #getMarkerStyle()
   * @generated
   */
  void setMarkerStyle(MarkerStyle value);

  /**
   * Returns the value of the '<em><b>Marker Size</b></em>' attribute. The default value is
   * <code>"3"</code>. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Marker Size</em>' attribute isn't clear, there really should be more
   * of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Marker Size</em>' attribute.
   * @see #setMarkerSize(double)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getStyling_MarkerSize()
   * @model default="3" dataType="org.eclipse.emf.ecore.xml.type.Double"
   * @generated
   */
  double getMarkerSize();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Styling#getMarkerSize <em>Marker
   * Size</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Marker Size</em>' attribute.
   * @see #getMarkerSize()
   * @generated
   */
  void setMarkerSize(double value);

  /**
   * Returns the value of the '<em><b>Line Thickness</b></em>' attribute. The default value is
   * <code>"1"</code>. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Line Thickness</em>' attribute isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Line Thickness</em>' attribute.
   * @see #setLineThickness(double)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getStyling_LineThickness()
   * @model default="1" dataType="org.eclipse.emf.ecore.xml.type.Double"
   * @generated
   */
  double getLineThickness();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Styling#getLineThickness <em>Line
   * Thickness</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Line Thickness</em>' attribute.
   * @see #getLineThickness()
   * @generated
   */
  void setLineThickness(double value);

  /**
   * Returns the value of the '<em><b>Line Style</b></em>' attribute. The default value is
   * <code>"Solid"</code>. The literals are from the enumeration
   * {@link info.limpet.stackedcharts.model.LineType}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Line Style</em>' attribute isn't clear, there really should be more
   * of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Line Style</em>' attribute.
   * @see info.limpet.stackedcharts.model.LineType
   * @see #setLineStyle(LineType)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getStyling_LineStyle()
   * @model default="Solid"
   * @generated
   */
  LineType getLineStyle();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Styling#getLineStyle <em>Line
   * Style</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Line Style</em>' attribute.
   * @see info.limpet.stackedcharts.model.LineType
   * @see #getLineStyle()
   * @generated
   */
  void setLineStyle(LineType value);

  /**
   * Returns the value of the '<em><b>Include In Legend</b></em>' attribute. The default value is
   * <code>"true"</code>. <!-- begin-user-doc --> <!-- end-user-doc --> <!-- begin-model-doc -->
   * whether to include this dataset in the chart legend
   * 
   * <!-- end-model-doc -->
   * 
   * @return the value of the '<em>Include In Legend</em>' attribute.
   * @see #setIncludeInLegend(boolean)
   * @see info.limpet.stackedcharts.model.StackedchartsPackage#getStyling_IncludeInLegend()
   * @model default="true"
   * @generated
   */
  boolean isIncludeInLegend();

  /**
   * Sets the value of the '{@link info.limpet.stackedcharts.model.Styling#isIncludeInLegend
   * <em>Include In Legend</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Include In Legend</em>' attribute.
   * @see #isIncludeInLegend()
   * @generated
   */
  void setIncludeInLegend(boolean value);

} // Styling
