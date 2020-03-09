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

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Linear
 * Styling</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.LinearStyling#getStartColor
 * <em>Start Color</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.LinearStyling#getEndColor <em>End
 * Color</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.LinearStyling#getStartVal
 * <em>Start Val</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.LinearStyling#getEndVal <em>End
 * Val</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getLinearStyling()
 * @model
 * @generated
 */
public interface LinearStyling extends Styling {
	/**
	 * Returns the value of the '<em><b>End Color</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>End Color</em>' attribute isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>End Color</em>' attribute.
	 * @see #setEndColor(Color)
	 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getLinearStyling_EndColor()
	 * @model dataType="info.limpet.stackedcharts.model.Color"
	 * @generated
	 */
	Color getEndColor();

	/**
	 * Returns the value of the '<em><b>End Val</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>End Val</em>' attribute isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>End Val</em>' attribute.
	 * @see #setEndVal(double)
	 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getLinearStyling_EndVal()
	 * @model
	 * @generated
	 */
	double getEndVal();

	/**
	 * Returns the value of the '<em><b>Start Color</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Start Color</em>' attribute isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Start Color</em>' attribute.
	 * @see #setStartColor(Color)
	 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getLinearStyling_StartColor()
	 * @model dataType="info.limpet.stackedcharts.model.Color"
	 * @generated
	 */
	Color getStartColor();

	/**
	 * Returns the value of the '<em><b>Start Val</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Start Val</em>' attribute isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Start Val</em>' attribute.
	 * @see #setStartVal(double)
	 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getLinearStyling_StartVal()
	 * @model
	 * @generated
	 */
	double getStartVal();

	/**
	 * Sets the value of the
	 * '{@link info.limpet.stackedcharts.model.LinearStyling#getEndColor <em>End
	 * Color</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value the new value of the '<em>End Color</em>' attribute.
	 * @see #getEndColor()
	 * @generated
	 */
	void setEndColor(Color value);

	/**
	 * Sets the value of the
	 * '{@link info.limpet.stackedcharts.model.LinearStyling#getEndVal <em>End
	 * Val</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value the new value of the '<em>End Val</em>' attribute.
	 * @see #getEndVal()
	 * @generated
	 */
	void setEndVal(double value);

	/**
	 * Sets the value of the
	 * '{@link info.limpet.stackedcharts.model.LinearStyling#getStartColor <em>Start
	 * Color</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value the new value of the '<em>Start Color</em>' attribute.
	 * @see #getStartColor()
	 * @generated
	 */
	void setStartColor(Color value);

	/**
	 * Sets the value of the
	 * '{@link info.limpet.stackedcharts.model.LinearStyling#getStartVal <em>Start
	 * Val</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value the new value of the '<em>Start Val</em>' attribute.
	 * @see #getStartVal()
	 * @generated
	 */
	void setStartVal(double value);

} // LinearStyling
