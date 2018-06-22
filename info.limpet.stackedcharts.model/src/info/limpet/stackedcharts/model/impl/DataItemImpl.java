/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.StackedchartsPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Data Item</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.impl.DataItemImpl#getIndependentVal <em>Independent
 * Val</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.DataItemImpl#getDependentVal <em>Dependent
 * Val</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DataItemImpl extends MinimalEObjectImpl.Container implements
    DataItem
{
  /**
   * The default value of the '{@link #getIndependentVal() <em>Independent Val</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getIndependentVal()
   * @generated
   * @ordered
   */
  protected static final double INDEPENDENT_VAL_EDEFAULT = 0.0;

  /**
   * The cached value of the '{@link #getIndependentVal() <em>Independent Val</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getIndependentVal()
   * @generated
   * @ordered
   */
  protected double independentVal = INDEPENDENT_VAL_EDEFAULT;

  /**
   * The default value of the '{@link #getDependentVal() <em>Dependent Val</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDependentVal()
   * @generated
   * @ordered
   */
  protected static final double DEPENDENT_VAL_EDEFAULT = 0.0;

  /**
   * The cached value of the '{@link #getDependentVal() <em>Dependent Val</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getDependentVal()
   * @generated
   * @ordered
   */
  protected double dependentVal = DEPENDENT_VAL_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected DataItemImpl()
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
    return StackedchartsPackage.Literals.DATA_ITEM;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public double getIndependentVal()
  {
    return independentVal;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setIndependentVal(double newIndependentVal)
  {
    double oldIndependentVal = independentVal;
    independentVal = newIndependentVal;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.DATA_ITEM__INDEPENDENT_VAL, oldIndependentVal,
          independentVal));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public double getDependentVal()
  {
    return dependentVal;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setDependentVal(double newDependentVal)
  {
    double oldDependentVal = dependentVal;
    dependentVal = newDependentVal;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.DATA_ITEM__DEPENDENT_VAL, oldDependentVal,
          dependentVal));
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
    case StackedchartsPackage.DATA_ITEM__INDEPENDENT_VAL:
      return getIndependentVal();
    case StackedchartsPackage.DATA_ITEM__DEPENDENT_VAL:
      return getDependentVal();
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
    case StackedchartsPackage.DATA_ITEM__INDEPENDENT_VAL:
      setIndependentVal((Double) newValue);
      return;
    case StackedchartsPackage.DATA_ITEM__DEPENDENT_VAL:
      setDependentVal((Double) newValue);
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
    case StackedchartsPackage.DATA_ITEM__INDEPENDENT_VAL:
      setIndependentVal(INDEPENDENT_VAL_EDEFAULT);
      return;
    case StackedchartsPackage.DATA_ITEM__DEPENDENT_VAL:
      setDependentVal(DEPENDENT_VAL_EDEFAULT);
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
    case StackedchartsPackage.DATA_ITEM__INDEPENDENT_VAL:
      return independentVal != INDEPENDENT_VAL_EDEFAULT;
    case StackedchartsPackage.DATA_ITEM__DEPENDENT_VAL:
      return dependentVal != DEPENDENT_VAL_EDEFAULT;
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
    result.append(" (independentVal: ");
    result.append(independentVal);
    result.append(", dependentVal: ");
    result.append(dependentVal);
    result.append(')');
    return result.toString();
  }

} // DataItemImpl
