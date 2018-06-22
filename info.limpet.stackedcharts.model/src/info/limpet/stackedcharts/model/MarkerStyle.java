/**
 */
package info.limpet.stackedcharts.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Marker
 * Style</b></em>', and utility methods for working with them. <!-- end-user-doc -->
 * 
 * @see info.limpet.stackedcharts.model.StackedchartsPackage#getMarkerStyle()
 * @model
 * @generated
 */
public enum MarkerStyle implements Enumerator
{
  /**
   * The '<em><b>None</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #NONE_VALUE
   * @generated
   * @ordered
   */
  NONE(0, "None", "None"),

  /**
   * The '<em><b>Square</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #SQUARE_VALUE
   * @generated
   * @ordered
   */
  SQUARE(1, "Square", "Square"),

  /**
   * The '<em><b>Circle</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #CIRCLE_VALUE
   * @generated
   * @ordered
   */
  CIRCLE(2, "Circle", "Circle"),

  /**
   * The '<em><b>Triangle</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #TRIANGLE_VALUE
   * @generated
   * @ordered
   */
  TRIANGLE(3, "Triangle", "Triangle"),

  /**
   * The '<em><b>Cross</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #CROSS_VALUE
   * @generated
   * @ordered
   */
  CROSS(4, "Cross", "Cross"),

  /**
   * The '<em><b>Diamond</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #DIAMOND_VALUE
   * @generated
   * @ordered
   */
  DIAMOND(5, "Diamond", "Diamond");

  /**
   * The '<em><b>None</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>None</b></em>' literal object isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #NONE
   * @model name="None"
   * @generated
   * @ordered
   */
  public static final int NONE_VALUE = 0;

  /**
   * The '<em><b>Square</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Square</b></em>' literal object isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #SQUARE
   * @model name="Square"
   * @generated
   * @ordered
   */
  public static final int SQUARE_VALUE = 1;

  /**
   * The '<em><b>Circle</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Circle</b></em>' literal object isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #CIRCLE
   * @model name="Circle"
   * @generated
   * @ordered
   */
  public static final int CIRCLE_VALUE = 2;

  /**
   * The '<em><b>Triangle</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Triangle</b></em>' literal object isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #TRIANGLE
   * @model name="Triangle"
   * @generated
   * @ordered
   */
  public static final int TRIANGLE_VALUE = 3;

  /**
   * The '<em><b>Cross</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Cross</b></em>' literal object isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #CROSS
   * @model name="Cross"
   * @generated
   * @ordered
   */
  public static final int CROSS_VALUE = 4;

  /**
   * The '<em><b>Diamond</b></em>' literal value. <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>Diamond</b></em>' literal object isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @see #DIAMOND
   * @model name="Diamond"
   * @generated
   * @ordered
   */
  public static final int DIAMOND_VALUE = 5;

  /**
   * An array of all the '<em><b>Marker Style</b></em>' enumerators. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @generated
   */
  private static final MarkerStyle[] VALUES_ARRAY = new MarkerStyle[]
  {NONE, SQUARE, CIRCLE, TRIANGLE, CROSS, DIAMOND,};

  /**
   * A public read-only list of all the '<em><b>Marker Style</b></em>' enumerators. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public static final List<MarkerStyle> VALUES = Collections.unmodifiableList(
      Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Marker Style</b></em>' literal with the specified literal value. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param literal
   *          the literal.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static MarkerStyle get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      MarkerStyle result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Marker Style</b></em>' literal with the specified name. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * 
   * @param name
   *          the name.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static MarkerStyle getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      MarkerStyle result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Marker Style</b></em>' literal with the specified integer value. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the integer value.
   * @return the matching enumerator or <code>null</code>.
   * @generated
   */
  public static MarkerStyle get(int value)
  {
    switch (value)
    {
    case NONE_VALUE:
      return NONE;
    case SQUARE_VALUE:
      return SQUARE;
    case CIRCLE_VALUE:
      return CIRCLE;
    case TRIANGLE_VALUE:
      return TRIANGLE;
    case CROSS_VALUE:
      return CROSS;
    case DIAMOND_VALUE:
      return DIAMOND;
    }
    return null;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private final int value;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private final String name;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private final String literal;

  /**
   * Only this class can construct instances. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  private MarkerStyle(int value, String name, String literal)
  {
    this.value = value;
    this.name = name;
    this.literal = literal;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public int getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getLiteral()
  {
    return literal;
  }

  /**
   * Returns the literal value of the enumerator, which is its string representation. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public String toString()
  {
    return literal;
  }

} // MarkerStyle
