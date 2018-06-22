/**
 */
package info.limpet.stackedcharts.model.impl;

import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.StackedchartsPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Independent Axis</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.impl.IndependentAxisImpl#getAnnotations
 * <em>Annotations</em>}</li>
 * </ul>
 *
 * @generated
 */
public class IndependentAxisImpl extends AbstractAxisImpl implements
    IndependentAxis
{
  /**
   * The cached value of the '{@link #getAnnotations() <em>Annotations</em>}' containment reference
   * list. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getAnnotations()
   * @generated
   * @ordered
   */
  protected EList<SelectiveAnnotation> annotations;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected IndependentAxisImpl()
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
    return StackedchartsPackage.Literals.INDEPENDENT_AXIS;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EList<SelectiveAnnotation> getAnnotations()
  {
    if (annotations == null)
    {
      annotations = new EObjectContainmentEList<SelectiveAnnotation>(
          SelectiveAnnotation.class, this,
          StackedchartsPackage.INDEPENDENT_AXIS__ANNOTATIONS);
    }
    return annotations;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd,
      int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case StackedchartsPackage.INDEPENDENT_AXIS__ANNOTATIONS:
      return ((InternalEList<?>) getAnnotations()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
    case StackedchartsPackage.INDEPENDENT_AXIS__ANNOTATIONS:
      return getAnnotations();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case StackedchartsPackage.INDEPENDENT_AXIS__ANNOTATIONS:
      getAnnotations().clear();
      getAnnotations().addAll(
          (Collection<? extends SelectiveAnnotation>) newValue);
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
    case StackedchartsPackage.INDEPENDENT_AXIS__ANNOTATIONS:
      getAnnotations().clear();
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
    case StackedchartsPackage.INDEPENDENT_AXIS__ANNOTATIONS:
      return annotations != null && !annotations.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // IndependentAxisImpl
