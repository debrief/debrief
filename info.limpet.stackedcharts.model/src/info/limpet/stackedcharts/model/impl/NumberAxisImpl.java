/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.NumberAxis;
import info.limpet.stackedcharts.model.StackedchartsPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Number Axis</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.impl.NumberAxisImpl#getNumberFormat <em>Number
 * Format</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.NumberAxisImpl#isAutoIncludesZero <em>Auto
 * Includes Zero</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.NumberAxisImpl#getUnits <em>Units</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NumberAxisImpl extends AxisTypeImpl implements NumberAxis
{
  /**
   * The default value of the '{@link #getNumberFormat() <em>Number Format</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getNumberFormat()
   * @generated
   * @ordered
   */
  protected static final String NUMBER_FORMAT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getNumberFormat() <em>Number Format</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getNumberFormat()
   * @generated
   * @ordered
   */
  protected String numberFormat = NUMBER_FORMAT_EDEFAULT;

  /**
   * The default value of the '{@link #isAutoIncludesZero() <em>Auto Includes Zero</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #isAutoIncludesZero()
   * @generated
   * @ordered
   */
  protected static final boolean AUTO_INCLUDES_ZERO_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isAutoIncludesZero() <em>Auto Includes Zero</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #isAutoIncludesZero()
   * @generated
   * @ordered
   */
  protected boolean autoIncludesZero = AUTO_INCLUDES_ZERO_EDEFAULT;

  /**
   * The default value of the '{@link #getUnits() <em>Units</em>}' attribute. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @see #getUnits()
   * @generated
   * @ordered
   */
  protected static final String UNITS_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getUnits() <em>Units</em>}' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #getUnits()
   * @generated
   * @ordered
   */
  protected String units = UNITS_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected NumberAxisImpl()
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
    return StackedchartsPackage.Literals.NUMBER_AXIS;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getNumberFormat()
  {
    return numberFormat;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setNumberFormat(String newNumberFormat)
  {
    String oldNumberFormat = numberFormat;
    numberFormat = newNumberFormat;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.NUMBER_AXIS__NUMBER_FORMAT, oldNumberFormat,
          numberFormat));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public boolean isAutoIncludesZero()
  {
    return autoIncludesZero;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setAutoIncludesZero(boolean newAutoIncludesZero)
  {
    boolean oldAutoIncludesZero = autoIncludesZero;
    autoIncludesZero = newAutoIncludesZero;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.NUMBER_AXIS__AUTO_INCLUDES_ZERO,
          oldAutoIncludesZero, autoIncludesZero));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getUnits()
  {
    return units;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setUnits(String newUnits)
  {
    String oldUnits = units;
    units = newUnits;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.NUMBER_AXIS__UNITS, oldUnits, units));
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
    case StackedchartsPackage.NUMBER_AXIS__NUMBER_FORMAT:
      return getNumberFormat();
    case StackedchartsPackage.NUMBER_AXIS__AUTO_INCLUDES_ZERO:
      return isAutoIncludesZero();
    case StackedchartsPackage.NUMBER_AXIS__UNITS:
      return getUnits();
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
    case StackedchartsPackage.NUMBER_AXIS__NUMBER_FORMAT:
      setNumberFormat((String) newValue);
      return;
    case StackedchartsPackage.NUMBER_AXIS__AUTO_INCLUDES_ZERO:
      setAutoIncludesZero((Boolean) newValue);
      return;
    case StackedchartsPackage.NUMBER_AXIS__UNITS:
      setUnits((String) newValue);
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
    case StackedchartsPackage.NUMBER_AXIS__NUMBER_FORMAT:
      setNumberFormat(NUMBER_FORMAT_EDEFAULT);
      return;
    case StackedchartsPackage.NUMBER_AXIS__AUTO_INCLUDES_ZERO:
      setAutoIncludesZero(AUTO_INCLUDES_ZERO_EDEFAULT);
      return;
    case StackedchartsPackage.NUMBER_AXIS__UNITS:
      setUnits(UNITS_EDEFAULT);
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
    case StackedchartsPackage.NUMBER_AXIS__NUMBER_FORMAT:
      return NUMBER_FORMAT_EDEFAULT == null ? numberFormat != null
          : !NUMBER_FORMAT_EDEFAULT.equals(numberFormat);
    case StackedchartsPackage.NUMBER_AXIS__AUTO_INCLUDES_ZERO:
      return autoIncludesZero != AUTO_INCLUDES_ZERO_EDEFAULT;
    case StackedchartsPackage.NUMBER_AXIS__UNITS:
      return UNITS_EDEFAULT == null ? units != null : !UNITS_EDEFAULT.equals(
          units);
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
    result.append(" (numberFormat: ");
    result.append(numberFormat);
    result.append(", autoIncludesZero: ");
    result.append(autoIncludesZero);
    result.append(", units: ");
    result.append(units);
    result.append(')');
    return result.toString();
  }

} // NumberAxisImpl
