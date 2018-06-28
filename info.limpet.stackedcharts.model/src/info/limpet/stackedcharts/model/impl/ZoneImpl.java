/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.StackedchartsPackage;
import info.limpet.stackedcharts.model.Zone;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Zone</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.impl.ZoneImpl#getStart <em>Start</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.ZoneImpl#getEnd <em>End</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ZoneImpl extends AbstractAnnotationImpl implements Zone
{
  /**
   * The default value of the '{@link #getStart() <em>Start</em>}' attribute. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @see #getStart()
   * @generated
   * @ordered
   */
  protected static final double START_EDEFAULT = 0.0;

  /**
   * The cached value of the '{@link #getStart() <em>Start</em>}' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #getStart()
   * @generated
   * @ordered
   */
  protected double start = START_EDEFAULT;

  /**
   * The default value of the '{@link #getEnd() <em>End</em>}' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #getEnd()
   * @generated
   * @ordered
   */
  protected static final double END_EDEFAULT = 0.0;

  /**
   * The cached value of the '{@link #getEnd() <em>End</em>}' attribute. <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * 
   * @see #getEnd()
   * @generated
   * @ordered
   */
  protected double end = END_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected ZoneImpl()
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
    return StackedchartsPackage.Literals.ZONE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public double getStart()
  {
    return start;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setStart(double newStart)
  {
    double oldStart = start;
    start = newStart;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.ZONE__START, oldStart, start));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public double getEnd()
  {
    return end;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setEnd(double newEnd)
  {
    double oldEnd = end;
    end = newEnd;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET,
          StackedchartsPackage.ZONE__END, oldEnd, end));
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
    case StackedchartsPackage.ZONE__START:
      return getStart();
    case StackedchartsPackage.ZONE__END:
      return getEnd();
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
    case StackedchartsPackage.ZONE__START:
      setStart((Double) newValue);
      return;
    case StackedchartsPackage.ZONE__END:
      setEnd((Double) newValue);
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
    case StackedchartsPackage.ZONE__START:
      setStart(START_EDEFAULT);
      return;
    case StackedchartsPackage.ZONE__END:
      setEnd(END_EDEFAULT);
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
    case StackedchartsPackage.ZONE__START:
      return start != START_EDEFAULT;
    case StackedchartsPackage.ZONE__END:
      return end != END_EDEFAULT;
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
    result.append(" (start: ");
    result.append(start);
    result.append(", end: ");
    result.append(end);
    result.append(')');
    return result.toString();
  }

} // ZoneImpl
