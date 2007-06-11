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
 * <p><code>AC3DObject</code> provides a modeling of the properties that 
 * constitute an object in the AC3D file format specification.</p>
 *
 * <p><strong>TODO:</strong><ul>
 * <li> Cleanup, commentary, and optimization.
 * </ul></p>
 *
 * @author  Ryan Wilhm (ryan@entrophica.com)
 * @version $Revision: 1.1.1.1 $
 */

public class Ac3dObject {

    /** The humantext name for the object. */
    private String name;
    
    /** The type of object represented. */
    private String type;
    
    /** The number of children for this object. */
    private int numKids;
    
    /** References to all of the children for this object. */
    private Ac3dObject kids[];
    
    /** The number of verticies for this object. */
    private int numvert;
    
    /** The displacement vector for this object. */
    private float[] loc;
    
    /** The rotational matrix for this object. */
    private float[] rot;
    
    /** The number of surfaces that this object has. */
    private int numsurf;
    
    /** The surfaces for the object. */
    private Ac3dSurface[] surfaces;
    
    /** The verticies for the object. */
    private float[] verticies;
    
    /** The texture for the object. */
    private String texture;
    
    /** The data identified for the object. */
    char[] data;
    
    /** Identity matrix. */
    public static float[] IDENTITY_MATRIX_ARRAY = {
        1.0f, 0.0f, 0.0f, 
        0.0f, 1.0f, 0.0f, 
        0.0f, 0.0f, 1.0f
    };
    
    
    /**
     * <p>Default constructor.</p>
     */
    
    public Ac3dObject() {
        name="";
        type="";
        texture="";
        numKids=0;
        kids=new Ac3dObject[numKids];
        numvert=0;
        loc=new float[3];
        data=new char[0];
        rot=IDENTITY_MATRIX_ARRAY;
        numsurf=0;
    }
    
    
    /**
     * <p>Mutator for the <code>name</code> property.</p>
     *
     * @param name The value to set the internal name to.
     */
    
    public void setName(String name) {
        this.name=name;
    }
    
    
    /**
     * <p>Accessor for the <code>name</code> property.</p>
     *
     * @return The value of the internal name.
     */
    
    public String getName() {
        return name;
    }
     
    
    /**
     * <p>Mutator for the type of object that this instance represents.</p>
     *
     * @param type The type to set this object to.
     */
    
    public void setType(String type) {
        this.type=type;
    }
    
    
    /**
     * <p>Accessor for the type of object that this instance represents.</p>
     *
     * @return The type of object that this instance represents.
     */
    
    public String getType() {
        return type;
    }
    
    
    /**
     * <p>Mutator for the number of child objects that this object 
     * aggregates.</p>
     *
     * @param numKids The number of child objects to set to.
     */
    
    public void setNumKids(int numKids) {
        this.numKids=numKids;
        kids=new Ac3dObject[numKids];
    }
    
    
    /**
     * <p>Accessor for the number of children objects that this object 
     * aggregates.</p>
     *
     * @return The number of children that this object has.
     */
    
    public int getNumKids() {
        return numKids;
    }
    
    
    /**
     * <p>Mutator for the number of verticies for the object.</p>
     *
     * @param The number of verticies to set for the object.
     */
    
    public void setNumvert(int numvert) {
        this.numvert=numvert;
        verticies=new float[numvert*3];
    }
    
    
    /**
     * <p>Accessor for the number of verticies for the object.</p>
     *
     * @return The number of verticies for the object.
     */
    
    public int getNumvert() {
        return numvert;
    }
    
    
    /**
     * <p>Mutator to set the number of surfaces for the object.</p>
     *
     * @param numsurf The number of surfaces for the object.
     */
    
    public void setNumsurf(int numsurf) {
        this.numsurf=numsurf;
        surfaces=new Ac3dSurface[numsurf];
    }
    
    
    /**
     * <p>Accessor to get the number of surfaces for the object.</p>
     *
     * @return The number of surfaces for the object.
     */
    
    public int getNumsurf() {
        return numsurf;
    }
    
    
    /**
     * <p>Mutator to the location displacement vector.
     *
     * @param loc The location displacement vector to set to.
     */
    
    public void setLoc(float[] loc) {
        if (loc.length==3) {
            this.loc=loc;
        }
    }
    
    
    /**
     * <p>Accessor for the locational displacement vector.
     *
     * @return The locational displacement vector.
     */
    
    public float[] getLoc() {
        return loc;
    }
    
    
    /**
     * <p>Mutator to set the rotational matrix for the object.</p>
     *
     * @param rot The rotational matrix to set our internal state to.
     */
    
    public void setRot(float[] rot) {
        if (rot.length==9) {
            this.rot=rot;
        }
    }
    
    
    /**
     * <p>Accessor to return the rotational matrix for the object.</p>
     *
     * @return The rotation matrix.
     */
    
    public float[] getRot() {
        return rot;
    }
    
    
    /**
     * <p>Mutator that adds an <code>Ac3dSurface</code> at the given 
     * index.</p>
     *
     * @param index The location at which to append the surface.
     * @param surface The surface to add.
     */
    
    public void addSurface(int index, Ac3dSurface surface) {
        surfaces[index]=surface;
    }
    
    
    /**
     * <p>Accessor that returns the <code>Ac3dSurface</code> at the given 
     * index.</p>
     *
     * @return The surface at the requested index.
     */
    
    public Ac3dSurface getSurface(int index) {
        return surfaces[index];
    }
    
    
    /**
     * <p>Mutator to add one vertex at the specified index.</p>
     *
     * @param index The index at which to add the vertex.
     * @param vertex Tuple of floats specifying the coordinate.
     */
    
    public void addVertex(int index, float[] vertex) {
        verticies[3*index]=vertex[0];
        verticies[3*index+1]=vertex[1];
        verticies[3*index+2]=vertex[2];
    }
    
    
    /**
     * <p>Accessor that returns the entire array of verticies.</p>
     *
     * @return All of the verticies for the object.
     */
    
    public float[] getVerticies() {
        return verticies;
    }
    
    
    /**
     * <p>Accessor for an individual vertex at a given index.</p>
     *
     * @return The vertex requested.
     */
    
    public float[] getVertexAtIndex(int index) {
        float[] rVal = new float[3];
        
        for (int i=0; i<3; i++) {
            rVal[i]=verticies[(3*index)+i];
        }
        
        return rVal;
    }
    
    
    /**
     * <p>Mutator for the <code>texture</code> property.</p>
     *
     * @param texture The value to set the internal texture name to.
     */
    
    public void setTexture(String texture) {
        this.texture=texture;
    }
    
    
    /**
     * <p>Accessor for the <code>texture</code> property.</p>
     *
     * @return The value of the internal texture name.
     */
    
    public String getTexture() {
        return texture;
    }
    
    
    /**
     * <p>Returns a stringified version of the internal state.</p>
     *
     * @return A stringified version of the internal state.
     */
    
    public String toString() {
        StringBuffer rVal=new StringBuffer();
        
        rVal.append("[ name=\"");
        rVal.append(name);
        rVal.append("\", type=\"");
        rVal.append(type);
        rVal.append("\", numKids=");
        rVal.append(numKids);
        rVal.append(", numvert=");
        rVal.append(numvert);
        rVal.append(", loc={");
        rVal.append(stringifyXf(loc));
        rVal.append("}, rot={");
        rVal.append(stringifyXf(rot));
        rVal.append("}, numsurf=");
        rVal.append(numsurf);
        
        rVal.append(" ]");
        
        return rVal.toString();
    }
    
    
    /**
     * <p>Helper that returns a stringified version of float array values.</p> 
     *
     * @param vals Values to stringify.
     * @return Stringified values.
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
