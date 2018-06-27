/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.LineType;
import info.limpet.stackedcharts.model.MarkerStyle;
import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.model.Styling;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Styling</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.impl.StylingImpl#getMarkerStyle <em>Marker
 * Style</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.StylingImpl#getMarkerSize <em>Marker
 * Size</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.StylingImpl#getLineThickness <em>Line
 * Thickness</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.StylingImpl#getLineStyle <em>Line
 * Style</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.StylingImpl#isIncludeInLegend <em>Include In
 * Legend</em>}</li>
 * </ul>
 *
 * @generated
 */
public class StylingImpl extends MinimalEObjectImpl.Container implements Styling
{
  /**
   * The default value of the '{@link #getMarkerStyle() <em>Marker Style</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getMarkerStyle()
   * @generated
   * @ordered
   */
  protected static final MarkerStyle MARKER_STYLE_EDEFAULT = MarkerStyle.NONE;

  /**
   * The cached value of the '{@link #getMarkerStyle() <em>Marker Style</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getMarkerStyle()
   * @generated
   * @ordered
   */
  protected MarkerStyle markerStyle = MARKER_STYLE_EDEFAULT;

  /**
   * The default value of the '{@link #getMarkerSize() <em>Marker Size</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getMarkerSize()
   * @generated
   * @ordered
   */
  protected static final double MARKER_SIZE_EDEFAULT = 3.0;

  /**
   * The cached value of the '{@link #getMarkerSize() <em>Marker Size</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getMarkerSize()
   * @generated
   * @ordered
   */
  protected double markerSize = MARKER_SIZE_EDEFAULT;

  /**
   * The default value of the '{@link #getLineThickness() <em>Line Thickness</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getLineThickness()
   * @generated
   * @ordered
   */
  protected static final double LINE_THICKNESS_EDEFAULT = 1.0;

  /**
   * The cached value of the '{@link #getLineThickness() <em>Line Thickness</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getLineThickness()
   * @generated
   * @ordered
   */
  protected double lineThickness = LINE_THICKNESS_EDEFAULT;

  /**
   * The default value of the '{@link #getLineStyle() <em>Line Style</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getLineStyle()
   * @generated
   * @ordered
   */
  protected static final LineType LINE_STYLE_EDEFAULT = LineType.SOLID;

  /**
   * The cached value of the '{@link #getLineStyle() <em>Line Style</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getLineStyle()
   * @generated
   * @ordered
   */
  protected LineType lineStyle = LINE_STYLE_EDEFAULT;

  /**
   * The default value of the '{@link #isIncludeInLegend() <em>Include In Legend</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #isIncludeInLegend()
   * @generated
   * @ordered
   */
  protected static final boolean INCLUDE_IN_LEGEND_EDEFAULT = true;

  /**
   * The cached value of the '{@link #isIncludeInLegend() <em>Include In Legend</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #isIncludeInLegend()
   * @generated
   * @ordered
   */
  protected boolean includeInLegend = INCLUDE_IN_LEGEND_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected StylingImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return StackedchartsPackage.Literals.STYLING;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public MarkerStyle getMarkerStyle()
  {
    return markerStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setMarkerStyle(MarkerStyle newMarkerStyle)
  {
    MarkerStyle oldMarkerStyle = markerStyle;
    markerStyle = newMarkerStyle == null ? MARKER_STYLE_EDEFAULT
        : newMarkerStyle;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.STYLING__MARKER_STYLE, oldMarkerStyle,
          markerStyle));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public double getMarkerSize()
  {
    return markerSize;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setMarkerSize(double newMarkerSize)
  {
    double oldMarkerSize = markerSize;
    markerSize = newMarkerSize;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.STYLING__MARKER_SIZE, oldMarkerSize,
          markerSize));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public double getLineThickness()
  {
    return lineThickness;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setLineThickness(double newLineThickness)
  {
    double oldLineThickness = lineThickness;
    lineThickness = newLineThickness;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.STYLING__LINE_THICKNESS, oldLineThickness,
          lineThickness));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public LineType getLineStyle()
  {
    return lineStyle;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setLineStyle(LineType newLineStyle)
  {
    LineType oldLineStyle = lineStyle;
    lineStyle = newLineStyle == null ? LINE_STYLE_EDEFAULT : newLineStyle;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.STYLING__LINE_STYLE, oldLineStyle, lineStyle));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public boolean isIncludeInLegend()
  {
    return includeInLegend;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setIncludeInLegend(boolean newIncludeInLegend)
  {
    boolean oldIncludeInLegend = includeInLegend;
    includeInLegend = newIncludeInLegend;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.STYLING__INCLUDE_IN_LEGEND, oldIncludeInLegend,
          includeInLegend));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case StackedchartsPackage.STYLING__MARKER_STYLE:
      return getMarkerStyle();
    case StackedchartsPackage.STYLING__MARKER_SIZE:
      return getMarkerSize();
    case StackedchartsPackage.STYLING__LINE_THICKNESS:
      return getLineThickness();
    case StackedchartsPackage.STYLING__LINE_STYLE:
      return getLineStyle();
    case StackedchartsPackage.STYLING__INCLUDE_IN_LEGEND:
      return isIncludeInLegend();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case StackedchartsPackage.STYLING__MARKER_STYLE:
      setMarkerStyle((MarkerStyle) newValue);
      return;
    case StackedchartsPackage.STYLING__MARKER_SIZE:
      setMarkerSize((Double) newValue);
      return;
    case StackedchartsPackage.STYLING__LINE_THICKNESS:
      setLineThickness((Double) newValue);
      return;
    case StackedchartsPackage.STYLING__LINE_STYLE:
      setLineStyle((LineType) newValue);
      return;
    case StackedchartsPackage.STYLING__INCLUDE_IN_LEGEND:
      setIncludeInLegend((Boolean) newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case StackedchartsPackage.STYLING__MARKER_STYLE:
      setMarkerStyle(MARKER_STYLE_EDEFAULT);
      return;
    case StackedchartsPackage.STYLING__MARKER_SIZE:
      setMarkerSize(MARKER_SIZE_EDEFAULT);
      return;
    case StackedchartsPackage.STYLING__LINE_THICKNESS:
      setLineThickness(LINE_THICKNESS_EDEFAULT);
      return;
    case StackedchartsPackage.STYLING__LINE_STYLE:
      setLineStyle(LINE_STYLE_EDEFAULT);
      return;
    case StackedchartsPackage.STYLING__INCLUDE_IN_LEGEND:
      setIncludeInLegend(INCLUDE_IN_LEGEND_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case StackedchartsPackage.STYLING__MARKER_STYLE:
      return markerStyle != MARKER_STYLE_EDEFAULT;
    case StackedchartsPackage.STYLING__MARKER_SIZE:
      return markerSize != MARKER_SIZE_EDEFAULT;
    case StackedchartsPackage.STYLING__LINE_THICKNESS:
      return lineThickness != LINE_THICKNESS_EDEFAULT;
    case StackedchartsPackage.STYLING__LINE_STYLE:
      return lineStyle != LINE_STYLE_EDEFAULT;
    case StackedchartsPackage.STYLING__INCLUDE_IN_LEGEND:
      return includeInLegend != INCLUDE_IN_LEGEND_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
      return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (markerStyle: ");
    result.append(markerStyle);
    result.append(", markerSize: ");
    result.append(markerSize);
    result.append(", lineThickness: ");
    result.append(lineThickness);
    result.append(", lineStyle: ");
    result.append(lineStyle);
    result.append(", includeInLegend: ");
    result.append(includeInLegend);
    result.append(')');
    return result.toString();
  }

} // StylingImpl
