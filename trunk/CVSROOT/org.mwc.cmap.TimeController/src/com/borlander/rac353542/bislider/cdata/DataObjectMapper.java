package com.borlander.rac353542.bislider.cdata;

/**
 * Internally BiSlider works with double data values. This interface extends
 * this default behavior with ability to work with any other data objects,
 * inlcuding singular set of objects which can not be mapped to continuous
 * double's data set.
 */
public interface DataObjectMapper {

    /**
     * Computes the double value for given custom data model object. This double
     * value will be used internally in BiSlider.
     * <p>
     * NOTE: It is responcibility of implementor to assure reasonable mapping
     * between object model and primitive values.
     */
    public double object2double(Object object);

    /**
     * Transforms some double value from BiSlider internals into data model
     * value. Implementation SHOULD NOT expect that the given value exactly
     * matches any datamodel object. In fact BiSlider does not have enough
     * information to determine the "valid" set of double values. Thus,
     * implementor should make some reasonable assumptions about how to round
     * double values to its own singular data set.
     * <p>
     * NOTE: It is responsibility of implementor to assure that any double value
     * is mapped into some data object.
     * 
     * @return never return <code>null</code>
     */
    public Object double2object(double value);

    /**
     * Returns the precision of this mapping.
     * 
     * For any data domain object <code>obj</code>, the following should be
     * <code>true</code>:
     * 
     * <code>
     * (obj != double2object(object2double(obj) + getPrecision())) && 
     * (obj != double2object(object2double(obj) - getPrecision()))
     * </code>
     */
    public double getPrecision();
}
