/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
/**
 */
package info.limpet.stackedcharts.model.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import info.limpet.stackedcharts.model.AbstractAnnotation;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.StackedchartsPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Dependent Axis</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.impl.DependentAxisImpl#getDatasets
 * <em>Datasets</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.DependentAxisImpl#getAnnotations
 * <em>Annotations</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DependentAxisImpl extends AbstractAxisImpl implements DependentAxis {
	/**
	 * The cached value of the '{@link #getDatasets() <em>Datasets</em>}'
	 * containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getDatasets()
	 * @generated
	 * @ordered
	 */
	protected EList<Dataset> datasets;

	/**
	 * The cached value of the '{@link #getAnnotations() <em>Annotations</em>}'
	 * containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getAnnotations()
	 * @generated
	 * @ordered
	 */
	protected EList<AbstractAnnotation> annotations;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected DependentAxisImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
		switch (featureID) {
		case StackedchartsPackage.DEPENDENT_AXIS__DATASETS:
			return getDatasets();
		case StackedchartsPackage.DEPENDENT_AXIS__ANNOTATIONS:
			return getAnnotations();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(final InternalEObject otherEnd, final int featureID,
			final NotificationChain msgs) {
		switch (featureID) {
		case StackedchartsPackage.DEPENDENT_AXIS__DATASETS:
			return ((InternalEList<?>) getDatasets()).basicRemove(otherEnd, msgs);
		case StackedchartsPackage.DEPENDENT_AXIS__ANNOTATIONS:
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
	public boolean eIsSet(final int featureID) {
		switch (featureID) {
		case StackedchartsPackage.DEPENDENT_AXIS__DATASETS:
			return datasets != null && !datasets.isEmpty();
		case StackedchartsPackage.DEPENDENT_AXIS__ANNOTATIONS:
			return annotations != null && !annotations.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(final int featureID, final Object newValue) {
		switch (featureID) {
		case StackedchartsPackage.DEPENDENT_AXIS__DATASETS:
			getDatasets().clear();
			getDatasets().addAll((Collection<? extends Dataset>) newValue);
			return;
		case StackedchartsPackage.DEPENDENT_AXIS__ANNOTATIONS:
			getAnnotations().clear();
			getAnnotations().addAll((Collection<? extends AbstractAnnotation>) newValue);
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
	protected EClass eStaticClass() {
		return StackedchartsPackage.Literals.DEPENDENT_AXIS;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void eUnset(final int featureID) {
		switch (featureID) {
		case StackedchartsPackage.DEPENDENT_AXIS__DATASETS:
			getDatasets().clear();
			return;
		case StackedchartsPackage.DEPENDENT_AXIS__ANNOTATIONS:
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
	public EList<AbstractAnnotation> getAnnotations() {
		if (annotations == null) {
			annotations = new EObjectContainmentEList<AbstractAnnotation>(AbstractAnnotation.class, this,
					StackedchartsPackage.DEPENDENT_AXIS__ANNOTATIONS);
		}
		return annotations;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public EList<Dataset> getDatasets() {
		if (datasets == null) {
			datasets = new EObjectContainmentEList<Dataset>(Dataset.class, this,
					StackedchartsPackage.DEPENDENT_AXIS__DATASETS);
		}
		return datasets;
	}

} // DependentAxisImpl
