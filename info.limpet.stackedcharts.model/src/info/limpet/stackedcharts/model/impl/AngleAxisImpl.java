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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import info.limpet.stackedcharts.model.AngleAxis;
import info.limpet.stackedcharts.model.StackedchartsPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Angle
 * Axis</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.impl.AngleAxisImpl#getMinVal
 * <em>Min Val</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.AngleAxisImpl#getMaxVal
 * <em>Max Val</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.AngleAxisImpl#isMidOrigin
 * <em>Mid Origin</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.AngleAxisImpl#isRedGreen
 * <em>Red Green</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AngleAxisImpl extends NumberAxisImpl implements AngleAxis {
	/**
	 * The default value of the '{@link #getMinVal() <em>Min Val</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getMinVal()
	 * @generated
	 * @ordered
	 */
	protected static final double MIN_VAL_EDEFAULT = 0.0;
	/**
	 * The default value of the '{@link #getMaxVal() <em>Max Val</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getMaxVal()
	 * @generated
	 * @ordered
	 */
	protected static final double MAX_VAL_EDEFAULT = 0.0;
	/**
	 * The default value of the '{@link #isMidOrigin() <em>Mid Origin</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #isMidOrigin()
	 * @generated
	 * @ordered
	 */
	protected static final boolean MID_ORIGIN_EDEFAULT = false;
	/**
	 * The default value of the '{@link #isRedGreen() <em>Red Green</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #isRedGreen()
	 * @generated
	 * @ordered
	 */
	protected static final boolean RED_GREEN_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #getMinVal() <em>Min Val</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getMinVal()
	 * @generated
	 * @ordered
	 */
	protected double minVal = MIN_VAL_EDEFAULT;
	/**
	 * The cached value of the '{@link #getMaxVal() <em>Max Val</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getMaxVal()
	 * @generated
	 * @ordered
	 */
	protected double maxVal = MAX_VAL_EDEFAULT;

	/**
	 * The cached value of the '{@link #isMidOrigin() <em>Mid Origin</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #isMidOrigin()
	 * @generated
	 * @ordered
	 */
	protected boolean midOrigin = MID_ORIGIN_EDEFAULT;
	/**
	 * The cached value of the '{@link #isRedGreen() <em>Red Green</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #isRedGreen()
	 * @generated
	 * @ordered
	 */
	protected boolean redGreen = RED_GREEN_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected AngleAxisImpl() {
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
		case StackedchartsPackage.ANGLE_AXIS__MIN_VAL:
			return getMinVal();
		case StackedchartsPackage.ANGLE_AXIS__MAX_VAL:
			return getMaxVal();
		case StackedchartsPackage.ANGLE_AXIS__MID_ORIGIN:
			return isMidOrigin();
		case StackedchartsPackage.ANGLE_AXIS__RED_GREEN:
			return isRedGreen();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public boolean eIsSet(final int featureID) {
		switch (featureID) {
		case StackedchartsPackage.ANGLE_AXIS__MIN_VAL:
			return minVal != MIN_VAL_EDEFAULT;
		case StackedchartsPackage.ANGLE_AXIS__MAX_VAL:
			return maxVal != MAX_VAL_EDEFAULT;
		case StackedchartsPackage.ANGLE_AXIS__MID_ORIGIN:
			return midOrigin != MID_ORIGIN_EDEFAULT;
		case StackedchartsPackage.ANGLE_AXIS__RED_GREEN:
			return redGreen != RED_GREEN_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void eSet(final int featureID, final Object newValue) {
		switch (featureID) {
		case StackedchartsPackage.ANGLE_AXIS__MIN_VAL:
			setMinVal((Double) newValue);
			return;
		case StackedchartsPackage.ANGLE_AXIS__MAX_VAL:
			setMaxVal((Double) newValue);
			return;
		case StackedchartsPackage.ANGLE_AXIS__MID_ORIGIN:
			setMidOrigin((Boolean) newValue);
			return;
		case StackedchartsPackage.ANGLE_AXIS__RED_GREEN:
			setRedGreen((Boolean) newValue);
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
		return StackedchartsPackage.Literals.ANGLE_AXIS;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void eUnset(final int featureID) {
		switch (featureID) {
		case StackedchartsPackage.ANGLE_AXIS__MIN_VAL:
			setMinVal(MIN_VAL_EDEFAULT);
			return;
		case StackedchartsPackage.ANGLE_AXIS__MAX_VAL:
			setMaxVal(MAX_VAL_EDEFAULT);
			return;
		case StackedchartsPackage.ANGLE_AXIS__MID_ORIGIN:
			setMidOrigin(MID_ORIGIN_EDEFAULT);
			return;
		case StackedchartsPackage.ANGLE_AXIS__RED_GREEN:
			setRedGreen(RED_GREEN_EDEFAULT);
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
	public double getMaxVal() {
		return maxVal;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public double getMinVal() {
		return minVal;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public boolean isMidOrigin() {
		return midOrigin;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public boolean isRedGreen() {
		return redGreen;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setMaxVal(final double newMaxVal) {
		final double oldMaxVal = maxVal;
		maxVal = newMaxVal;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ANGLE_AXIS__MAX_VAL, oldMaxVal,
					maxVal));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setMidOrigin(final boolean newMidOrigin) {
		final boolean oldMidOrigin = midOrigin;
		midOrigin = newMidOrigin;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ANGLE_AXIS__MID_ORIGIN,
					oldMidOrigin, midOrigin));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setMinVal(final double newMinVal) {
		final double oldMinVal = minVal;
		minVal = newMinVal;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ANGLE_AXIS__MIN_VAL, oldMinVal,
					minVal));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setRedGreen(final boolean newRedGreen) {
		final boolean oldRedGreen = redGreen;
		redGreen = newRedGreen;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ANGLE_AXIS__RED_GREEN,
					oldRedGreen, redGreen));
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
		result.append(" (minVal: ");
		result.append(minVal);
		result.append(", maxVal: ");
		result.append(maxVal);
		result.append(", midOrigin: ");
		result.append(midOrigin);
		result.append(", redGreen: ");
		result.append(redGreen);
		result.append(')');
		return result.toString();
	}

} // AngleAxisImpl
