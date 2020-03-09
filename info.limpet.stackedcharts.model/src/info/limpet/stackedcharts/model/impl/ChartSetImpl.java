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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.StackedchartsPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Chart
 * Set</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.impl.ChartSetImpl#getCharts
 * <em>Charts</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.ChartSetImpl#getOrientation
 * <em>Orientation</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.ChartSetImpl#getSharedAxis
 * <em>Shared Axis</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ChartSetImpl extends MinimalEObjectImpl.Container implements ChartSet {
	/**
	 * The default value of the '{@link #getOrientation() <em>Orientation</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getOrientation()
	 * @generated
	 * @ordered
	 */
	protected static final Orientation ORIENTATION_EDEFAULT = Orientation.VERTICAL;

	/**
	 * The cached value of the '{@link #getCharts() <em>Charts</em>}' containment
	 * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getCharts()
	 * @generated
	 * @ordered
	 */
	protected EList<Chart> charts;

	/**
	 * The cached value of the '{@link #getOrientation() <em>Orientation</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getOrientation()
	 * @generated
	 * @ordered
	 */
	protected Orientation orientation = ORIENTATION_EDEFAULT;

	/**
	 * The cached value of the '{@link #getSharedAxis() <em>Shared Axis</em>}'
	 * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getSharedAxis()
	 * @generated
	 * @ordered
	 */
	protected IndependentAxis sharedAxis;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected ChartSetImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public NotificationChain basicSetSharedAxis(final IndependentAxis newSharedAxis, NotificationChain msgs) {
		final IndependentAxis oldSharedAxis = sharedAxis;
		sharedAxis = newSharedAxis;
		if (eNotificationRequired()) {
			final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					StackedchartsPackage.CHART_SET__SHARED_AXIS, oldSharedAxis, newSharedAxis);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object eGet(final int featureID, final boolean resolve, final boolean coreType) {
		switch (featureID) {
		case StackedchartsPackage.CHART_SET__CHARTS:
			return getCharts();
		case StackedchartsPackage.CHART_SET__ORIENTATION:
			return getOrientation();
		case StackedchartsPackage.CHART_SET__SHARED_AXIS:
			return getSharedAxis();
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
	public NotificationChain eInverseAdd(final InternalEObject otherEnd, final int featureID,
			final NotificationChain msgs) {
		switch (featureID) {
		case StackedchartsPackage.CHART_SET__CHARTS:
			return ((InternalEList<InternalEObject>) (InternalEList<?>) getCharts()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
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
		case StackedchartsPackage.CHART_SET__CHARTS:
			return ((InternalEList<?>) getCharts()).basicRemove(otherEnd, msgs);
		case StackedchartsPackage.CHART_SET__SHARED_AXIS:
			return basicSetSharedAxis(null, msgs);
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
		case StackedchartsPackage.CHART_SET__CHARTS:
			return charts != null && !charts.isEmpty();
		case StackedchartsPackage.CHART_SET__ORIENTATION:
			return orientation != ORIENTATION_EDEFAULT;
		case StackedchartsPackage.CHART_SET__SHARED_AXIS:
			return sharedAxis != null;
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
		case StackedchartsPackage.CHART_SET__CHARTS:
			getCharts().clear();
			getCharts().addAll((Collection<? extends Chart>) newValue);
			return;
		case StackedchartsPackage.CHART_SET__ORIENTATION:
			setOrientation((Orientation) newValue);
			return;
		case StackedchartsPackage.CHART_SET__SHARED_AXIS:
			setSharedAxis((IndependentAxis) newValue);
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
		return StackedchartsPackage.Literals.CHART_SET;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void eUnset(final int featureID) {
		switch (featureID) {
		case StackedchartsPackage.CHART_SET__CHARTS:
			getCharts().clear();
			return;
		case StackedchartsPackage.CHART_SET__ORIENTATION:
			setOrientation(ORIENTATION_EDEFAULT);
			return;
		case StackedchartsPackage.CHART_SET__SHARED_AXIS:
			setSharedAxis((IndependentAxis) null);
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
	public EList<Chart> getCharts() {
		if (charts == null) {
			charts = new EObjectContainmentWithInverseEList<Chart>(Chart.class, this,
					StackedchartsPackage.CHART_SET__CHARTS, StackedchartsPackage.CHART__PARENT);
		}
		return charts;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Orientation getOrientation() {
		return orientation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public IndependentAxis getSharedAxis() {
		return sharedAxis;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setOrientation(final Orientation newOrientation) {
		final Orientation oldOrientation = orientation;
		orientation = newOrientation == null ? ORIENTATION_EDEFAULT : newOrientation;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.CHART_SET__ORIENTATION,
					oldOrientation, orientation));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setSharedAxis(final IndependentAxis newSharedAxis) {
		if (newSharedAxis != sharedAxis) {
			NotificationChain msgs = null;
			if (sharedAxis != null)
				msgs = ((InternalEObject) sharedAxis).eInverseRemove(this,
						EOPPOSITE_FEATURE_BASE - StackedchartsPackage.CHART_SET__SHARED_AXIS, null, msgs);
			if (newSharedAxis != null)
				msgs = ((InternalEObject) newSharedAxis).eInverseAdd(this,
						EOPPOSITE_FEATURE_BASE - StackedchartsPackage.CHART_SET__SHARED_AXIS, null, msgs);
			msgs = basicSetSharedAxis(newSharedAxis, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.CHART_SET__SHARED_AXIS,
					newSharedAxis, newSharedAxis));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		final StringBuffer result = new StringBuffer(super.toString());
		result.append(" (orientation: ");
		result.append(orientation);
		result.append(')');
		return result.toString();
	}

} // ChartSetImpl
