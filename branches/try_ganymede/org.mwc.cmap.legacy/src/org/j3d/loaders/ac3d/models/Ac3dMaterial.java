/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 * This software comes with the standard NO WARRANTY disclaimer for any
 * purpose. Use it at your own risk. If there's a problem you get to fix it.
 *
 ****************************************************************************/

package org.j3d.loaders.ac3d.models;


/**
 * <p><code>Ac3dMaterial</code> defines a model to represent the properties 
 * that define a surface material in the AC3D file format.</p>
 *
 * <p><strong>TODO:</strong><ul>
 * <li> Maybe add sanity checking on value bounds. (Ie, bounded properties.)
 * <li> Cleanup, commentary, and optimization.
 * </ul></p>
 *
 * @author  Ryan Wilhm (ryan@entrophica.com)
 * @version $Revision: 1.1.1.1 $
 * @see org.entrophica.ac3d.parser.Ac3dParser
 */

public class Ac3dMaterial {

    /** Name associated with material. */
    String name;
    
    /** */
    float[] rgb;
    
    /** */
    float[] amb;
    
    /** */
    float[] emis;
    
    /** */
    float[] spec;
    
    /** */
    int shi;
    
    /** */
    float trans;
    
    /** */
    int index;

    
    /**
     * <p>Default constructor. Sets the internal state to initial values.</p>
     */
    
    public Ac3dMaterial() {
        name="";
        rgb=new float[3];
        amb=new float[3];
        emis=new float[3];
        spec=new float[3];
        shi=0;
        trans=0.0f;
        index=-1;
    }

    
    /**
     * <p>
     *
     * @param name
     */
    
    public void setName(String name) {
        this.name=name;
    }
    
    
    /**
     * <p>
     *
     * @return 
     */
    
    public String getName() {
        return name;
    }
    
    
    /**
     * <p>
     *
     * @param rgb
     */
    
    public void setRgb(float[] rgb) {
        if (rgb.length==3) {
            this.rgb=rgb;
        }
    }
    
    
    /**
     * <p>
     *
     * @return
     */
    
    public float[] getRgb() {
        return rgb;
    }
    
    
    /**
     * <p>
     * 
     * @param amb
     */
    
    public void setAmb(float[] amb) {
        if (amb.length==3) {
            this.amb=amb;
        }
    }
    
    public float[] getAmb() {
        return amb;
    }
    
    
    public void setEmis(float[] emis) {
        if (emis.length==3) {
            this.emis=emis;
        }
    }
    
    public float[] getEmis() {
        return emis;
    }
    
    
    public void setSpec(float[] spec) {
        if (spec.length==3) {
            this.spec=spec;
        }
    }
    
    public float[] getSpec() {
        return spec;
    }
    
    
    public void setShi(int shi) {
        this.shi=shi;
    }
    
    public int getShi() {
        return shi;
    }
    
    
    public void setTrans(float trans) {
        this.trans=trans;
    }
    
    public float getTrans() {
        return trans;
    }
    
    
    /**
     * <p>Mutator for the <code>index</code> property, which identifies 
     * the index of the material within the AC3D file that this object 
     * represents.</p>
     *
     * @return The value to set the <code>index</code> property to.
     */
        
    public void setIndex(int index) {
        this.index=index;
    }
    
    
    /**
     * <p>Accessor for the <code>index</code> property, which identifies 
     * the index of the material within the AC3D file that this object 
     * represents.</p>
     *
     * @return The value of the <code>index</code> property.
     */
    
    public int getIndex() {
        return index;
    }
    
    
    /**
     * <p>Provides a humantext stringification of the current object state.</p>
     *
     * @return The humantext stringification of the current object state.
     */
    
    public String toString() {
        StringBuffer rVal=new StringBuffer();
        
        rVal.append("[ name=\"");
        rVal.append(name);
        rVal.append("\", index=");
        rVal.append(index);
        rVal.append(", rgb={");
        rVal.append(stringifyXf(rgb));
        rVal.append("}, amb={");
        rVal.append(stringifyXf(amb));
        rVal.append("}, emis={");
        rVal.append(stringifyXf(emis));
        rVal.append("}, spec={");
        rVal.append(stringifyXf(spec));
        rVal.append("}, shi=");
        rVal.append(shi);
        rVal.append(", trans=");
        rVal.append(trans);
        rVal.append(" ]");
        
        return rVal.toString();
    }
    
    
    /**
     * <p>Simple utility method to generate a stringified representation of 
     * multiple <code>float</code> values.</p>
     *
     * @param vals The array of <code>float</code> values to convert into 
     *             humantext.
     * @return The humantext string representing the values.
     */
    
    private static String stringifyXf(float[] vals) {
        StringBuffer rVal=new StringBuffer();
        
        if (vals.length>0) {
            rVal.append(vals[0]);
            for (int i=1; i<vals.length; i++) {
                rVal.append(",");
                rVal.append(vals[i]);
            }
        }
        
        return rVal.toString();
    }
}
