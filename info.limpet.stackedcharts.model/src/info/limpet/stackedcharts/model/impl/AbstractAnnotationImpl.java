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

import java.awt.Color;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import info.limpet.stackedcharts.model.AbstractAnnotation;
import info.limpet.stackedcharts.model.StackedchartsPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Abstract Annotation</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.impl.AbstractAnnotationImpl#getName
 * <em>Name</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.AbstractAnnotationImpl#getColor
 * <em>Color</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.AbstractAnnotationImpl#isIncludeInLegend
 * <em>Include In Legend</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class AbstractAnnotationImpl extends MinimalEObjectImpl.Container implements AbstractAnnotation {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The default value of the '{@link #getColor() <em>Color</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getColor()
	 * @generated
	 * @ordered
	 */
	protected static final Color COLOR_EDEFAULT = null;

	/**
	 * The default value of the '{@link #isIncludeInLegend() <em>Include In
	 * Legend</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #isIncludeInLegend()
	 * @generated
	 * @ordered
	 */
	protected static final boolean INCLUDE_IN_LEGEND_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getColor() <em>Color</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getColor()
	 * @generated
	 * @ordered
	 */
	protected Color color = COLOR_EDEFAULT;

	/**
	 * The cached value of the '{@link #isIncludeInLegend() <em>Include In
	 * Legend</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #isIncludeInLegend()
	 * @generated
	 * @ordered
	 */
	protected boolean includeInLegend = INCLUDE_IN_LEGEND_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected AbstractAnnotationImpl() {
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
		case StackedchartsPackage.ABSTRACT_ANNOTATION__NAME:
			return getName();
		case StackedchartsPackage.ABSTRACT_ANNOTATION__COLOR:
			return getColor();
		case StackedchartsPackage.ABSTRACT_ANNOTATION__INCLUDE_IN_LEGEND:
			return isIncludeInLegend();
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
		case StackedchartsPackage.ABSTRACT_ANNOTATION__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
		case StackedchartsPackage.ABSTRACT_ANNOTATION__COLOR:
			return COLOR_EDEFAULT == null ? color != null : !COLOR_EDEFAULT.equals(color);
		case StackedchartsPackage.ABSTRACT_ANNOTATION__INCLUDE_IN_LEGEND:
			return includeInLegend != INCLUDE_IN_LEGEND_EDEFAULT;
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
		case StackedchartsPackage.ABSTRACT_ANNOTATION__NAME:
			setName((String) newValue);
			return;
		case StackedchartsPackage.ABSTRACT_ANNOTATION__COLOR:
			setColor((Color) newValue);
			return;
		case StackedchartsPackage.ABSTRACT_ANNOTATION__INCLUDE_IN_LEGEND:
			setIncludeInLegend((Boolean) newValue);
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
		return StackedchartsPackage.Literals.ABSTRACT_ANNOTATION;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void eUnset(final int featureID) {
		switch (featureID) {
		case StackedchartsPackage.ABSTRACT_ANNOTATION__NAME:
			setName(NAME_EDEFAULT);
			return;
		case StackedchartsPackage.ABSTRACT_ANNOTATION__COLOR:
			setColor(COLOR_EDEFAULT);
			return;
		case StackedchartsPackage.ABSTRACT_ANNOTATION__INCLUDE_IN_LEGEND:
			setIncludeInLegend(INCLUDE_IN_LEGEND_EDEFAULT);
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
	public Color getColor() {
		return color;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public boolean isIncludeInLegend() {
		return includeInLegend;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setColor(final Color newColor) {
		final Color oldColor = color;
		color = newColor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ABSTRACT_ANNOTATION__COLOR,
					oldColor, color));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setIncludeInLegend(final boolean newIncludeInLegend) {
		final boolean oldIncludeInLegend = includeInLegend;
		includeInLegend = newIncludeInLegend;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					StackedchartsPackage.ABSTRACT_ANNOTATION__INCLUDE_IN_LEGEND, oldIncludeInLegend, includeInLegend));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setName(final String newName) {
		final String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.ABSTRACT_ANNOTATION__NAME,
					oldName, name));
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
		result.append(" (name: ");
		result.append(name);
		result.append(", color: ");
		result.append(color);
		result.append(", includeInLegend: ");
		result.append(includeInLegend);
		result.append(')');
		return result.toString();
	}

} // AbstractAnnotationImpl
