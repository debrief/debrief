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
package info.limpet.stackedcharts.model;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object
 * '<em><b>Selective Annotation</b></em>'. <!-- end-user-doc -->
 *
 * <!-- begin-model-doc --> An annotation that may only be visible on some
 * charts <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.SelectiveAnnotation#getAnnotation
 * <em>Annotation</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.SelectiveAnnotation#getAppearsIn
 * <em>Appears In</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getSelectiveAnnotation()
 * @model
 * @generated
 */
public interface SelectiveAnnotation extends EObject {
	/**
	 * Returns the value of the '<em><b>Annotation</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc --> <!-- begin-model-doc --> the
	 * annotation in question <!-- end-model-doc -->
	 *
	 * @return the value of the '<em>Annotation</em>' containment reference.
	 * @see #setAnnotation(AbstractAnnotation)
	 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getSelectiveAnnotation_Annotation()
	 * @model containment="true" required="true"
	 * @generated
	 */
	AbstractAnnotation getAnnotation();

	/**
	 * Returns the value of the '<em><b>Appears In</b></em>' reference list. The
	 * list contents are of type {@link info.limpet.stackedcharts.model.Chart}. <!--
	 * begin-user-doc --> <!-- end-user-doc --> <!-- begin-model-doc --> a list of
	 * which charts this annotation appears in (or null for all charts) <!--
	 * end-model-doc -->
	 *
	 * @return the value of the '<em>Appears In</em>' reference list.
	 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getSelectiveAnnotation_AppearsIn()
	 * @model
	 * @generated
	 */
	EList<Chart> getAppearsIn();

	/**
	 * Sets the value of the
	 * '{@link info.limpet.stackedcharts.model.SelectiveAnnotation#getAnnotation
	 * <em>Annotation</em>}' containment reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @param value the new value of the '<em>Annotation</em>' containment
	 *              reference.
	 * @see #getAnnotation()
	 * @generated
	 */
	void setAnnotation(AbstractAnnotation value);

} // SelectiveAnnotation
