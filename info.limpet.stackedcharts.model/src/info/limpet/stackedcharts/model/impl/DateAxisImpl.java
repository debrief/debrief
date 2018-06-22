/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.DateAxis;
import info.limpet.stackedcharts.model.StackedchartsPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Date Axis</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.impl.DateAxisImpl#getDateFormat <em>Date
 * Format</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DateAxisImpl extends AxisTypeImpl implements DateAxis
{
  /**
   * The default value of the '{@link #getDateFormat() <em>Date Format</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDateFormat()
   * @generated
   * @ordered
   */
  protected static final String DATE_FORMAT_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDateFormat() <em>Date Format</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDateFormat()
   * @generated
   * @ordered
   */
  protected String dateFormat = DATE_FORMAT_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected DateAxisImpl()
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
    return StackedchartsPackage.Literals.DATE_AXIS;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getDateFormat()
  {
    return dateFormat;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setDateFormat(String newDateFormat)
  {
    String oldDateFormat = dateFormat;
    dateFormat = newDateFormat;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.DATE_AXIS__DATE_FORMAT, oldDateFormat,
          dateFormat));
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
    case StackedchartsPackage.DATE_AXIS__DATE_FORMAT:
      return getDateFormat();
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
    case StackedchartsPackage.DATE_AXIS__DATE_FORMAT:
      setDateFormat((String) newValue);
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
    case StackedchartsPackage.DATE_AXIS__DATE_FORMAT:
      setDateFormat(DATE_FORMAT_EDEFAULT);
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
    case StackedchartsPackage.DATE_AXIS__DATE_FORMAT:
      return DATE_FORMAT_EDEFAULT == null ? dateFormat != null
          : !DATE_FORMAT_EDEFAULT.equals(dateFormat);
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
    result.append(" (dateFormat: ");
    result.append(dateFormat);
    result.append(')');
    return result.toString();
  }

} // DateAxisImpl
