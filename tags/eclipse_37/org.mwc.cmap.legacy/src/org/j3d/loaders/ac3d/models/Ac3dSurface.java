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
 * <p><code>AC3DSurface</code> represents the attributes for a polygon 
 * surface in the AC3D file format definition. Primatives are used 
 * wherever possible to reduce memory usage and object clutter.</p>
 *
 * <p><strong>TODO:</strong><ul>
 * <li> Perform bounds checking
 * <li> Cleanup, commentary, and optimization.
 * </ul></p>
 *
 * @author  Ryan Wilhm (ryan@entrophica.com)
 * @version $Revision: 1.1.1.1 $
 */

public class Ac3dSurface {
    
    /** Number of vertex references. */
    int numrefs;
    
    /** Specifies type and flags for surface. */
    int flags;
    
    /** Specifies the material for the surface by index. */
    int mat;
    
    /** The verticies index. */
    int[] verticies;
    
    /** The texture coordinates. */
    float[] textureCoordinates;
    
    /** Surface flag attribute definitions. */
    public static final int FLAG_POLYGON=0, 
        FLAG_CLOSEDLINE=    1<<0, 
        FLAG_LINE=          1<<1,
        FLAG_SHADED=        1<<4, 
        FLAG_TWOSIDED=      1<<5;

    
    /** 
     * <p>Default constructor, which sets up the initial state.</p>
     */
    
    public Ac3dSurface() {
        numrefs=0;
        flags=0;
        mat=-1;
    }
    
    
    /**
     * <p>Mutator to set the number of vertex references for this surface. 
     * This also allocates space for all of the associated data.</p>
     *
     * @param numrefs The number of references this surface is to have.
     */
    
    public void setNumrefs(int numrefs) {
        this.numrefs=numrefs;
        verticies=new int[numrefs];
        textureCoordinates=new float[numrefs*2];
    }
    
    
    /**
     * <p>Accessor to retrieve the number of vertex references for this 
     * surface.</p>
     *
     * @return The number of surface references.
     */
    
    public int getNumrefs() {
        return numrefs;
    }
    
    
    /**
     * <p>Mutator to set the current flag state.</p>
     *
     * @param The value ot set the current flag state to.
     */
    
    public void setFlags(int flags) {
        this.flags=flags;
    }
    
    
    /**
     * <p>Accessor to get the current flag state.</p>
     *
     * @return The current flag state.
     */
    
    public int getFlags() {
        return flags;
    }
    
    
    /**
     * <p>Mutator to set the material index reference.</p>
     * 
     * @param mat The material index to associate this surface to.
     */
    
    public void setMat(int mat) {
        this.mat=mat;
    }
    
    
    /**
     * <p>Accessor to get the material index reference for this surface.</p>
     * 
     * @return The material index associated with this surface.
     */
    
    public int getMat() {
        return mat;
    }
    
    
    /**
     * <p>Mutator that appends an additional reference to the surface. These 
     * identify the coordinates of the vertex, as well as texture 
     * coordinates.</p>
     *
     * @param index Indicates the index number of the surface vertes.
     * @param vertex The index of the vertex in the object the surface is a 
     *               part of.
     * @param texCoord The texture coordinates for the surface vertex relating 
     *                 to the texture map.
     */
    
    public void addRef(int index, int vertex, float[] texCoord) {
        verticies[index]=vertex;
        textureCoordinates[2*index]=texCoord[0];
        textureCoordinates[2*index+1]=texCoord[1];
    }
    
    
    /**
     * <p>Accessor for the array of verticies.</p>
     *
     * @return The array of verticies.
     */
    
    public int[] getVerticiesIndex() {
        return verticies;
    }
    
    
    /**
     * <p>Accessor for the <code>textureCoordinates</code> property.</p>
     *
     * @return The array of texture coordinates.
     */
    
    public float[] getTextureCoordinates() {
        return textureCoordinates;
    }
    
    
    /**
     * <p>Helper function that tests to see if the requested flag is set in 
     * the local instance state.</p>
     *
     * @param flag The flag to check for.
     * @return Whether or not the flag is set in the local state.
     */
    
    public boolean checkFlag(int flag) {
        return checkFlag(flag, flags);
    }
    
        
    /**
     * <p>Creates and returns the stringified version of the internal 
     * state.</p>
     *
     * @return The stringified value for the state.
     */
    
    public String toString() {
        StringBuffer rVal = new StringBuffer();
     
        rVal.append("[ flags=");
        rVal.append(flags);
        rVal.append(" { ");
        rVal.append(stringifyFlags(flags));
        rVal.append(" }, mat=");
        rVal.append(mat);
        rVal.append(", numrefs=");
        rVal.append(numrefs);
        rVal.append(", refs= { ");
        for (int i=0; i<verticies.length; i++) {
            rVal.append(verticies[i]);
            rVal.append("@(");
            rVal.append(textureCoordinates[2*i]);
            rVal.append(", ");
            rVal.append(textureCoordinates[2*i+1]);
            rVal.append(") ");
        }
        rVal.append(" } ]");
        
        return rVal.toString();
    }
    
    
    /**
     * <p>Helper funciton that returns a stringified representation of the 
     * flag state.</p>
     *
     * @param flags The flags to stringify.
     * @return The stringified representation of <code>flags</code>.
     */
    
    private static final String stringifyFlags(int flags) {
        StringBuffer rVal = new StringBuffer();
        
        // Deal with type... Should only be one!
        
        if (checkFlag(FLAG_POLYGON, flags)) {
            rVal.append("FLAG_POLYGON");
        }
        
        if (checkFlag(FLAG_CLOSEDLINE, flags)) {
            rVal.append("FLAG_CLOSEDLINE");
        }
        
        if (checkFlag(FLAG_LINE, flags)) {
            rVal.append("FLAG_LINE");
        }
        
        // Deal with attributes...
        
        if (checkFlag(FLAG_SHADED, flags)) {
            rVal.append(" | FLAG_SHADED");
        }
        
        if (checkFlag(FLAG_TWOSIDED, flags)) {
            rVal.append(" | FLAG_TWOSIDED");
        }
        
        return rVal.toString();
    }
    
    
    /**
     * <p>Returns whether or not the a flag is set. This is defined as 
     * <code>private static final</code> to make the function a candidate 
     * for inlining by the compiler.</p>
     *
     * @param flag The flag being checked for.
     * @param flags The state flags that are being checked.
     * @return Whether or not <code>flag</code> was set in <code>flags</code>.
     */
    
    private static final boolean checkFlag(int flag, int flags) {
        return ((flags & flag)==flag);
    }
}
