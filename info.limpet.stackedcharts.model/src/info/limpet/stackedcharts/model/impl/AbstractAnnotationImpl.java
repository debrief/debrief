/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.AbstractAnnotation;
import info.limpet.stackedcharts.model.StackedchartsPackage;

import java.awt.Color;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Abstract
 * Annotation</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.impl.AbstractAnnotationImpl#getName
 * <em>Name</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.AbstractAnnotationImpl#getColor
 * <em>Color</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.AbstractAnnotationImpl#isIncludeInLegend
 * <em>Include In Legend</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class AbstractAnnotationImpl extends
    MinimalEObjectImpl.Container implements AbstractAnnotation
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getColor() <em>Color</em>}' attribute. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @see #getColor()
   * @generated
   * @ordered
   */
  protected static final Color COLOR_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getColor() <em>Color</em>}' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #getColor()
   * @generated
   * @ordered
   */
  protected Color color = COLOR_EDEFAULT;

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
  protected AbstractAnnotationImpl()
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
    return StackedchartsPackage.Literals.ABSTRACT_ANNOTATION;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.ABSTRACT_ANNOTATION__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Color getColor()
  {
    return color;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setColor(Color newColor)
  {
    Color oldColor = color;
    color = newColor;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.ABSTRACT_ANNOTATION__COLOR, oldColor, color));
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
          StackedchartsPackage.ABSTRACT_ANNOTATION__INCLUDE_IN_LEGEND,
          oldIncludeInLegend, includeInLegend));
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
    case StackedchartsPackage.ABSTRACT_ANNOTATION__NAME:
      return getName();
    case StackedchartsPackage.ABSTRACT_ANNOTATION__COLOR:
      return getColor();
    case StackedchartsPackage.ABSTRACT_ANNOTATION__INCLUDE_IN_LEGEND:
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
    case StackedchartsPackage.ABSTRACT_ANNOTATION__NAME:
      setName((String) newValue);
      return;
    case StackedchartsPackage.ABSTRACT_ANNOTATION__COLOR:
      setColor((Color) newValue);
      return;
    case StackedchartsPackage.ABSTRACT_ANNOTATION__INCLUDE_IN_LEGEND:
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
    case StackedchartsPackage.ABSTRACT_ANNOTATION__NAME:
      setName(NAME_EDEFAULT);
      return;
    case StackedchartsPackage.ABSTRACT_ANNOTATION__COLOR:
      setColor(COLOR_EDEFAULT);
      return;
    case StackedchartsPackage.ABSTRACT_ANNOTATION__INCLUDE_IN_LEGEND:
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
    case StackedchartsPackage.ABSTRACT_ANNOTATION__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case StackedchartsPackage.ABSTRACT_ANNOTATION__COLOR:
      return COLOR_EDEFAULT == null ? color != null : !COLOR_EDEFAULT.equals(
          color);
    case StackedchartsPackage.ABSTRACT_ANNOTATION__INCLUDE_IN_LEGEND:
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
    result.append(" (name: ");
    result.append(name);
    result.append(", color: ");
    result.append(color);
    result.append(", includeInLegend: ");
    result.append(includeInLegend);
    result.append(')');
    return result.toString();
  }

} // AbstractAnnotationImpl
