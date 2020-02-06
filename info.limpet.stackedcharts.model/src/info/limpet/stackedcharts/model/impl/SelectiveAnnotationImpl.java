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
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import info.limpet.stackedcharts.model.AbstractAnnotation;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.StackedchartsPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Selective Annotation</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.impl.SelectiveAnnotationImpl#getAnnotation
 * <em>Annotation</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.impl.SelectiveAnnotationImpl#getAppearsIn
 * <em>Appears In</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SelectiveAnnotationImpl extends MinimalEObjectImpl.Container implements SelectiveAnnotation {
	/**
	 * The cached value of the '{@link #getAnnotation() <em>Annotation</em>}'
	 * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getAnnotation()
	 * @generated
	 * @ordered
	 */
	protected AbstractAnnotation annotation;

	/**
	 * The cached value of the '{@link #getAppearsIn() <em>Appears In</em>}'
	 * reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @see #getAppearsIn()
	 * @generated
	 * @ordered
	 */
	protected EList<Chart> appearsIn;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	protected SelectiveAnnotationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public NotificationChain basicSetAnnotation(final AbstractAnnotation newAnnotation, NotificationChain msgs) {
		final AbstractAnnotation oldAnnotation = annotation;
		annotation = newAnnotation;
		if (eNotificationRequired()) {
			final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
					StackedchartsPackage.SELECTIVE_ANNOTATION__ANNOTATION, oldAnnotation, newAnnotation);
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
		case StackedchartsPackage.SELECTIVE_ANNOTATION__ANNOTATION:
			return getAnnotation();
		case StackedchartsPackage.SELECTIVE_ANNOTATION__APPEARS_IN:
			return getAppearsIn();
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
		case StackedchartsPackage.SELECTIVE_ANNOTATION__ANNOTATION:
			return basicSetAnnotation(null, msgs);
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
		case StackedchartsPackage.SELECTIVE_ANNOTATION__ANNOTATION:
			return annotation != null;
		case StackedchartsPackage.SELECTIVE_ANNOTATION__APPEARS_IN:
			return appearsIn != null && !appearsIn.isEmpty();
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
		case StackedchartsPackage.SELECTIVE_ANNOTATION__ANNOTATION:
			setAnnotation((AbstractAnnotation) newValue);
			return;
		case StackedchartsPackage.SELECTIVE_ANNOTATION__APPEARS_IN:
			getAppearsIn().clear();
			getAppearsIn().addAll((Collection<? extends Chart>) newValue);
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
		return StackedchartsPackage.Literals.SELECTIVE_ANNOTATION;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void eUnset(final int featureID) {
		switch (featureID) {
		case StackedchartsPackage.SELECTIVE_ANNOTATION__ANNOTATION:
			setAnnotation((AbstractAnnotation) null);
			return;
		case StackedchartsPackage.SELECTIVE_ANNOTATION__APPEARS_IN:
			getAppearsIn().clear();
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
	public AbstractAnnotation getAnnotation() {
		return annotation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public EList<Chart> getAppearsIn() {
		if (appearsIn == null) {
			appearsIn = new EObjectResolvingEList<Chart>(Chart.class, this,
					StackedchartsPackage.SELECTIVE_ANNOTATION__APPEARS_IN);
		}
		return appearsIn;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public void setAnnotation(final AbstractAnnotation newAnnotation) {
		if (newAnnotation != annotation) {
			NotificationChain msgs = null;
			if (annotation != null)
				msgs = ((InternalEObject) annotation).eInverseRemove(this,
						EOPPOSITE_FEATURE_BASE - StackedchartsPackage.SELECTIVE_ANNOTATION__ANNOTATION, null, msgs);
			if (newAnnotation != null)
				msgs = ((InternalEObject) newAnnotation).eInverseAdd(this,
						EOPPOSITE_FEATURE_BASE - StackedchartsPackage.SELECTIVE_ANNOTATION__ANNOTATION, null, msgs);
			msgs = basicSetAnnotation(newAnnotation, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, StackedchartsPackage.SELECTIVE_ANNOTATION__ANNOTATION,
					newAnnotation, newAnnotation));
	}

} // SelectiveAnnotationImpl
