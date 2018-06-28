/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.LinearStyling;
import info.limpet.stackedcharts.model.StackedchartsPackage;

import java.awt.Color;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Linear Styling</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.impl.LinearStylingImpl#getStartColor <em>Start
 * Color</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.LinearStylingImpl#getEndColor <em>End
 * Color</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.LinearStylingImpl#getStartVal <em>Start
 * Val</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.LinearStylingImpl#getEndVal <em>End
 * Val</em>}</li>
 * </ul>
 *
 * @generated
 */
public class LinearStylingImpl extends StylingImpl implements LinearStyling
{
  /**
   * The default value of the '{@link #getStartColor() <em>Start Color</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getStartColor()
   * @generated
   * @ordered
   */
  protected static final Color START_COLOR_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getStartColor() <em>Start Color</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getStartColor()
   * @generated
   * @ordered
   */
  protected Color startColor = START_COLOR_EDEFAULT;

  /**
   * The default value of the '{@link #getEndColor() <em>End Color</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getEndColor()
   * @generated
   * @ordered
   */
  protected static final Color END_COLOR_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getEndColor() <em>End Color</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getEndColor()
   * @generated
   * @ordered
   */
  protected Color endColor = END_COLOR_EDEFAULT;

  /**
   * The default value of the '{@link #getStartVal() <em>Start Val</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getStartVal()
   * @generated
   * @ordered
   */
  protected static final double START_VAL_EDEFAULT = 0.0;

  /**
   * The cached value of the '{@link #getStartVal() <em>Start Val</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getStartVal()
   * @generated
   * @ordered
   */
  protected double startVal = START_VAL_EDEFAULT;

  /**
   * The default value of the '{@link #getEndVal() <em>End Val</em>}' attribute. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @see #getEndVal()
   * @generated
   * @ordered
   */
  protected static final double END_VAL_EDEFAULT = 0.0;

  /**
   * The cached value of the '{@link #getEndVal() <em>End Val</em>}' attribute. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @see #getEndVal()
   * @generated
   * @ordered
   */
  protected double endVal = END_VAL_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected LinearStylingImpl()
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
    return StackedchartsPackage.Literals.LINEAR_STYLING;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Color getStartColor()
  {
    return startColor;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setStartColor(Color newStartColor)
  {
    Color oldStartColor = startColor;
    startColor = newStartColor;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.LINEAR_STYLING__START_COLOR, oldStartColor,
          startColor));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public Color getEndColor()
  {
    return endColor;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setEndColor(Color newEndColor)
  {
    Color oldEndColor = endColor;
    endColor = newEndColor;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.LINEAR_STYLING__END_COLOR, oldEndColor,
          endColor));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public double getStartVal()
  {
    return startVal;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setStartVal(double newStartVal)
  {
    double oldStartVal = startVal;
    startVal = newStartVal;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.LINEAR_STYLING__START_VAL, oldStartVal,
          startVal));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public double getEndVal()
  {
    return endVal;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setEndVal(double newEndVal)
  {
    double oldEndVal = endVal;
    endVal = newEndVal;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.LINEAR_STYLING__END_VAL, oldEndVal, endVal));
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
    case StackedchartsPackage.LINEAR_STYLING__START_COLOR:
      return getStartColor();
    case StackedchartsPackage.LINEAR_STYLING__END_COLOR:
      return getEndColor();
    case StackedchartsPackage.LINEAR_STYLING__START_VAL:
      return getStartVal();
    case StackedchartsPackage.LINEAR_STYLING__END_VAL:
      return getEndVal();
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
    case StackedchartsPackage.LINEAR_STYLING__START_COLOR:
      setStartColor((Color) newValue);
      return;
    case StackedchartsPackage.LINEAR_STYLING__END_COLOR:
      setEndColor((Color) newValue);
      return;
    case StackedchartsPackage.LINEAR_STYLING__START_VAL:
      setStartVal((Double) newValue);
      return;
    case StackedchartsPackage.LINEAR_STYLING__END_VAL:
      setEndVal((Double) newValue);
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
    case StackedchartsPackage.LINEAR_STYLING__START_COLOR:
      setStartColor(START_COLOR_EDEFAULT);
      return;
    case StackedchartsPackage.LINEAR_STYLING__END_COLOR:
      setEndColor(END_COLOR_EDEFAULT);
      return;
    case StackedchartsPackage.LINEAR_STYLING__START_VAL:
      setStartVal(START_VAL_EDEFAULT);
      return;
    case StackedchartsPackage.LINEAR_STYLING__END_VAL:
      setEndVal(END_VAL_EDEFAULT);
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
    case StackedchartsPackage.LINEAR_STYLING__START_COLOR:
      return START_COLOR_EDEFAULT == null ? startColor != null
          : !START_COLOR_EDEFAULT.equals(startColor);
    case StackedchartsPackage.LINEAR_STYLING__END_COLOR:
      return END_COLOR_EDEFAULT == null ? endColor != null : !END_COLOR_EDEFAULT
          .equals(endColor);
    case StackedchartsPackage.LINEAR_STYLING__START_VAL:
      return startVal != START_VAL_EDEFAULT;
    case StackedchartsPackage.LINEAR_STYLING__END_VAL:
      return endVal != END_VAL_EDEFAULT;
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
    result.append(" (startColor: ");
    result.append(startColor);
    result.append(", endColor: ");
    result.append(endColor);
    result.append(", startVal: ");
    result.append(startVal);
    result.append(", endVal: ");
    result.append(endVal);
    result.append(')');
    return result.toString();
  }

} // LinearStylingImpl
