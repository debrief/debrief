/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.StackedchartsPackage;

import java.awt.Color;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Plain Styling</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.impl.PlainStylingImpl#getColor <em>Color</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PlainStylingImpl extends StylingImpl implements PlainStyling
{
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
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected PlainStylingImpl()
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
    return StackedchartsPackage.Literals.PLAIN_STYLING;
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
          StackedchartsPackage.PLAIN_STYLING__COLOR, oldColor, color));
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
    case StackedchartsPackage.PLAIN_STYLING__COLOR:
      return getColor();
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
    case StackedchartsPackage.PLAIN_STYLING__COLOR:
      setColor((Color) newValue);
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
    case StackedchartsPackage.PLAIN_STYLING__COLOR:
      setColor(COLOR_EDEFAULT);
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
    case StackedchartsPackage.PLAIN_STYLING__COLOR:
      return COLOR_EDEFAULT == null ? color != null : !COLOR_EDEFAULT.equals(
          color);
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
    result.append(" (color: ");
    result.append(color);
    result.append(')');
    return result.toString();
  }

} // PlainStylingImpl
