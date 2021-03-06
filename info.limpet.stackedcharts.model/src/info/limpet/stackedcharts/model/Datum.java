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

import java.awt.Color;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object
 * '<em><b>Datum</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.Datum#getVal <em>Val</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.Datum#getColor
 * <em>Color</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDatum()
 * @model
 * @generated
 */
public interface Datum extends EObject {
	/**
	 * Returns the value of the '<em><b>Color</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc --> <!-- begin-model-doc --> optional
	 * color for this datum
	 *
	 * <!-- end-model-doc -->
	 *
	 * @return the value of the '<em>Color</em>' attribute.
	 * @see #setColor(Color)
	 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDatum_Color()
	 * @model dataType="info.limpet.stackedcharts.model.Color"
	 * @generated
	 */
	Color getColor();

	/**
	 * Returns the value of the '<em><b>Val</b></em>' attribute. <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of the '<em>Val</em>' attribute isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Val</em>' attribute.
	 * @see #setVal(double)
	 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getDatum_Val()
	 * @model required="true"
	 * @generated
	 */
	double getVal();

	/**
	 * Sets the value of the '{@link info.limpet.stackedcharts.model.Datum#getColor
	 * <em>Color</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value the new value of the '<em>Color</em>' attribute.
	 * @see #getColor()
	 * @generated
	 */
	void setColor(Color value);

	/**
	 * Sets the value of the '{@link info.limpet.stackedcharts.model.Datum#getVal
	 * <em>Val</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value the new value of the '<em>Val</em>' attribute.
	 * @see #getVal()
	 * @generated
	 */
	void setVal(double value);

} // Datum
