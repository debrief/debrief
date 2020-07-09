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

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Number
 * Axis</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link info.limpet.stackedcharts.model.NumberAxis#getNumberFormat
 * <em>Number Format</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.NumberAxis#isAutoIncludesZero
 * <em>Auto Includes Zero</em>}</li>
 * <li>{@link info.limpet.stackedcharts.model.NumberAxis#getUnits
 * <em>Units</em>}</li>
 * </ul>
 *
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getNumberAxis()
 * @model
 * @generated
 */
public interface NumberAxis extends AxisType {
	/**
	 * Returns the value of the '<em><b>Number Format</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Number Format</em>' attribute isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Number Format</em>' attribute.
	 * @see #setNumberFormat(String)
	 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getNumberAxis_NumberFormat()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 * @generated
	 */
	String getNumberFormat();

	/**
	 * Returns the value of the '<em><b>Units</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc --> <!-- begin-model-doc --> the units
	 * displayed on this axis <!-- end-model-doc -->
	 *
	 * @return the value of the '<em>Units</em>' attribute.
	 * @see #setUnits(String)
	 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getNumberAxis_Units()
	 * @model
	 * @generated
	 */
	String getUnits();

	/**
	 * Returns the value of the '<em><b>Auto Includes Zero</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Auto Includes Zero</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 *
	 * @return the value of the '<em>Auto Includes Zero</em>' attribute.
	 * @see #setAutoIncludesZero(boolean)
	 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getNumberAxis_AutoIncludesZero()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 * @generated
	 */
	boolean isAutoIncludesZero();

	/**
	 * Sets the value of the
	 * '{@link info.limpet.stackedcharts.model.NumberAxis#isAutoIncludesZero
	 * <em>Auto Includes Zero</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @param value the new value of the '<em>Auto Includes Zero</em>' attribute.
	 * @see #isAutoIncludesZero()
	 * @generated
	 */
	void setAutoIncludesZero(boolean value);

	/**
	 * Sets the value of the
	 * '{@link info.limpet.stackedcharts.model.NumberAxis#getNumberFormat <em>Number
	 * Format</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value the new value of the '<em>Number Format</em>' attribute.
	 * @see #getNumberFormat()
	 * @generated
	 */
	void setNumberFormat(String value);

	/**
	 * Sets the value of the
	 * '{@link info.limpet.stackedcharts.model.NumberAxis#getUnits <em>Units</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @param value the new value of the '<em>Units</em>' attribute.
	 * @see #getUnits()
	 * @generated
	 */
	void setUnits(String value);

} // NumberAxis
