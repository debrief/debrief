/*****************************************************************************
 *                        J3D.org Copyright (c) 2001
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 * This software comes with the standard NO WARRANTY disclaimer for any
 * purpose. Use it at your own risk. If there's a problem you get to fix it.
 *
 ****************************************************************************/

package org.j3d.loaders.ac3d;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.vecmath.*;
import javax.media.j3d.*;
import com.sun.j3d.loaders.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.*;
import org.j3d.loaders.ac3d.util.TextureLoaderHelper;
import org.j3d.loaders.ac3d.models.*;
import org.j3d.loaders.ac3d.parser.*;
import org.j3d.loaders.ac3d.parser.handlers.*;
import org.j3d.loaders.ac3d.parser.exceptions.*;


/**
 * <p>Provides the implementation of the <code>TokenHandler</code> that will 
 * transform the AC3D tokens into valid Java 3D data.</p>
 *
 * <p><strong>TODO:</strong><ul>
 * <li> Implement SURFACE ATTRIBUTES (Fully... missing translucency?)
 * <li> Check on Java 3D branch management... (Compilation, attribute flags)
 * <li> Cleanup, commentary, and optimization. 
 * <ul>
 * <li> Take advantage of pre-existing model indicies --
 *      NOTE:
 *      This should also be done to facilitate smooth shading on normal 
 *      generation.
 * <li> Reverse branch orientation
 * <li> Build materials in <code>HashMap</code> only once on material call.
 * </ul>
 * </ul></p>
 *
 * @author  Ryan Wilhm (ryan@entrophica.com)
 * @version $Revision: 1.1.1.1 $
 */

public class LoaderTokenHandler extends Ac3dTokenHandlerBase {

    /** The container for the scene being rendered. */
    private SceneBase scene;
    
    /** The parent <code>BranchGroup</code> for everything loaded from file. */
    private BranchGroup world;
    
    /** The current <code>BranchGroup</code> being worked on. Always a ref. */
    private BranchGroup currentGroup;
    
    /** The call stack, used to handle objects. */
    private Stack callStack;
    
    /** Indicates the number of children left at a point in the parse. */
    private int kidsLeft;
        
    /** Crease angle definitions. */
    private static final double FLAT_CREASE_ANGLE=0.0, 
        SMOOTH_CREASE_ANGLE=(22.0/7.0);
    
    /** Specify what the base source is. */
    private boolean sourceIsFile;
    
    
    /**
     * <p>Default constructor.</p>
     */
    
    public LoaderTokenHandler() {
        super();
        scene=null;
        sourceIsFile=true;
    }
    
    
    /**
     * <p>Handles the ending tag for the object definitions. At this 
     * point, the <code>Ac3dObject</code> should be fully populated with 
     * the data for the current object, with the exception of its 
     * children.</p>
     *
     * @param tokens
     * @exception AC3DParseException
     */
    
    public void token_kids(String[] tokens) throws AC3DParseException {
        super.token_kids(tokens);
        Ac3dObject obj;
        BranchGroup tmpGroup;
        CallStackPlaceholder placeholder;
        obj = (Ac3dObject)displayList.elementAt(displayList.size()-1);
        
        // hanlde world object       
        if (obj.getType().equals("world")) {
            debug("OBJECT: Creating \"WORLD\" BranchGroup.");
            currentGroup=world;
            kidsLeft=obj.getNumKids();
        }
        
        // handle poly and group objects
        if (obj.getType().equals("poly") || 
            obj.getType().equals("group")) {
                
            if (obj.getType().equals("poly")) {
                debug("OBJECT: Adding \"POLY\" to local BranchGroup.");
                tmpGroup=buildPoly(obj, currentGroup);
            } else {
                debug("OBJECT: Adding \"GROUP\" to local BranchGroup.");
                tmpGroup=buildGroup(obj, currentGroup);      
            }
            
            kidsLeft--;
            callStack.push(new CallStackPlaceholder(currentGroup, kidsLeft));
            scene.addNamedObject(obj.getName(), tmpGroup);
            currentGroup=tmpGroup;
            kidsLeft=obj.getNumKids();
        }
        
        
                
        // Stack Management
        while ((kidsLeft==0) && (callStack.size()>0)) {
            placeholder=(CallStackPlaceholder)callStack.pop();
            kidsLeft=placeholder.getKidsLeft();
            currentGroup=placeholder.getBranchGroup();
        }
    }
    
    
    /**
     * <p>This generates a <code>BranchGroup</code> containing the data for 
     * the polygon object specified. Made <code>private static final</code> 
     * to make a candidate for compiler inlining.</p>
     * 
     * @param obj The <code>Ac3dObject</code> containing the data for the 
     *            polygon definition.
     * @return A <code>BranchGroup</code> containing the object.
     */
    
    private final BranchGroup buildPoly(Ac3dObject obj, BranchGroup target) {
            
        BranchGroup rVal;
        Shape3D shape;
        Ac3dSurface surface;
        Texture2D texture=null;
        Appearance tmpAppearance;
        
        
        // Deal with texture here since it is object wide
        if (!obj.getTexture().equals("")) {
            texture=new TextureLoaderHelper().loadTexture(obj.getTexture());
        } 
        
        // Build the transforms and group
        rVal=buildGroup(obj,target);
        
        // Build by surface -- ## TODO... should be cumulative?
        for (int i=0; i<obj.getNumsurf(); i++) {
            surface=obj.getSurface(i);
            shape = new Shape3D();
            
            // Build appearance
            tmpAppearance=buildAppearance(surface, 
                (Ac3dMaterial)materials.elementAt(surface.getMat()));
            if (texture!=null) {
                tmpAppearance.setTexture(texture);
            }
            shape.setAppearance(tmpAppearance);
            
            // Build geometry
            if ((surface.checkFlag(Ac3dSurface.FLAG_CLOSEDLINE)) || 
                (surface.checkFlag(Ac3dSurface.FLAG_LINE)) ) {
                shape.setGeometry(buildLineGeometry(surface, obj));
            } else {
                shape.setGeometry(buildSurfaceGeometry(surface, obj, 
                    (texture!=null)));
            }

            // Add face to branch
            rVal.addChild(shape);
        }

        return rVal;
    }
    
    
    /**
     * <p>Constructs a <code>Group</code> in the rendering tree.</p>
     *
     * @param obj The object representing the data model of the group.
     * @param target The parent <code>BranchGroup</code> to attach to.
     * @return The resulting <code>BranchGroup</code> to attach children to.
     */
    
    private static final BranchGroup buildGroup(Ac3dObject obj, 
        BranchGroup target) {
            
        TransformGroup rotationTG, translationTG;
        BranchGroup rVal=new BranchGroup();
        Transform3D rotation=new Transform3D(), 
            translation=new Transform3D();
        
        // Rotate to proper orientation
        rotation.setRotation(new Matrix3f(obj.getRot()));
        rotationTG=new TransformGroup(rotation);
        
        // Transform to proper location
        translation.set(new Vector3f(obj.getLoc()));
        translationTG=new TransformGroup(translation);
        
        // Set up branches
        target.addChild(translationTG);
        translationTG.addChild(rotationTG);
        rotationTG.addChild(rVal);

        return rVal;
    }
    
    
    /**
     * <p>Returns an instance of <code>Geometry</code> that is populated 
     * with the data for the surface specified by the index and vertices 
     * passed in.</p>
     *
     * @param surf The surface model that contains the face data.
     * @param obj The object, which contains vertex information.
     * @param isTexutred Whether or not to attempt to texture the object.
     * @return A <code>Geometry</code> object that is populated with the 
     *         surface information.
     */
    
    private static final Geometry buildSurfaceGeometry(Ac3dSurface surf, 
        Ac3dObject obj, boolean isTextured) {
        
        GeometryInfo geomInfo;
        int[] index=surf.getVerticiesIndex();
        float[] newVerts = new float[index.length*3];
        float[] vertices = obj.getVerticies();
                
 
        // Set up for vertices population
        geomInfo=new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        
        // Create new verticies index
        for (int i=0; i<index.length; i++) {
            for (int x=0; x<3; x++) {
                newVerts[(i*3)+x]=vertices[((index[i])*3)+x];
            }
        }        
        geomInfo.setCoordinates(newVerts);      
        
        // Create new texture vertices, if textured
        if (isTextured) {
            geomInfo.setTextureCoordinates2(surf.getTextureCoordinates());
        }
        
        // Triangulate polygon
        geomInfo.setStripCounts(new int[] { index.length });
        (new Triangulator()).triangulate(geomInfo);
        
        // Generate normals -- Check to see if they should be smooth or not
        if (surf.checkFlag(Ac3dSurface.FLAG_SHADED)) {
            (new NormalGenerator(SMOOTH_CREASE_ANGLE)).generateNormals(geomInfo);
        } else {
            (new NormalGenerator(FLAT_CREASE_ANGLE)).generateNormals(geomInfo);
        }            

        return geomInfo.getGeometryArray();
    }
    
    
    /**
     * <p>Returns an instance of <code>Geometry</code> that is populated 
     * with the data for the line specified by the index and vertices 
     * passed in. During the building process, it is necessary to take 
     * into account that the AC3D file format will list a line as 
     * a list of verticies that interconnect. The Java 3D API needs line 
     * geometry data in pairs of the individual line segments.</p>
     *
     * @param surface The surface model that contains the line information.
     * @param obj The object model that contains the vertex information.
     * @return The <code>Geometry</code> object, which is populated with the 
     *         line definition data.
     */
    
    private static final Geometry buildLineGeometry(Ac3dSurface surface, 
        Ac3dObject obj) {
            
        LineArray rVal;
        int vertexCount;
        float[] newVerts;
        int[] index=surface.getVerticiesIndex();
        float[] verticies = obj.getVerticies();
        
        // Determine number of coordinates based on line type
        if (surface.checkFlag(Ac3dSurface.FLAG_CLOSEDLINE)) {
            vertexCount=index.length;
        } else {
            vertexCount=index.length-1;
        }        
        
        newVerts=new float[vertexCount*6];
        rVal=new LineArray(vertexCount*2, GeometryArray.COORDINATES);
        
        // Create new verticies index
        for (int i=0; i<index.length-1; i++) {
            for (int x=0; x<3; x++) {
                newVerts[((2*i)*3)+x]=verticies[((index[i])*3)+x];
                newVerts[((2*i+1)*3)+x]=verticies[((index[i+1])*3)+x];
            }
        }    
        
        // Handle adding closed line last vertex
        if (surface.checkFlag(Ac3dSurface.FLAG_CLOSEDLINE)) {
            for (int i=0; i<3; i++) {
                newVerts[6*vertexCount-(6-i)]=newVerts[6*vertexCount-(9-i)];                
                newVerts[6*vertexCount-(3-i)]=newVerts[i];
            }
        }
        
        // Set up coordinates
        rVal.setCoordinates(0, newVerts);
        
        return rVal;
    }    
    
    
    /**
     * <p>Utility function that will build an <code>Appearance</code> object 
     * based upon the data passed in as arguements.</p>
     * 
     * @param surface The surface model.
     * @param material The material model.
     * @return The populated <code>Appearance</code> object.
     */
    
    private static final Appearance buildAppearance(Ac3dSurface surface, 
        Ac3dMaterial material) {
            
        Appearance rVal;
        PolygonAttributes pat;
        ColoringAttributes cat;
        
        // Build appearance
        rVal = new Appearance();
        pat = new PolygonAttributes();
        cat=new ColoringAttributes();
        //tat=new TransparencyAttributes();

        rVal.setMaterial(buildMaterial(material));
        // ## TODO: Translucence/Alpha??

        // Set type of polygon
        if ( (surface.checkFlag(Ac3dSurface.FLAG_CLOSEDLINE)) || 
             (surface.checkFlag(Ac3dSurface.FLAG_LINE)) ) {
            pat.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        }

        // Determine if we should cull the backface
        if (surface.checkFlag(Ac3dSurface.FLAG_TWOSIDED)) {
            pat.setCullFace(PolygonAttributes.CULL_NONE);
        }
       
        // For now, set as nicest shading... This should really be a flag in 
        // the loader
        cat.setShadeModel(ColoringAttributes.NICEST);

        rVal.setPolygonAttributes(pat);
        rVal.setColoringAttributes(cat);
        //ap.setTransparencyAttributes(tat);
        
        return rVal;
    }
    
    
    /**
     * <p>Utility function for construction of the <code>Material</code> 
     * objects cooresponding to the material information presented within the 
     * AC3D file.</p>
     *
     * @param material The AC3D encapsulation of the material data.
     * @return The Java 3D <code>Material</code> object representing the 
     *         AC3D material definition.
     */
    
    private static final Material buildMaterial(Ac3dMaterial material) {
        Material rVal = new Material();

        rVal.setDiffuseColor(new Color3f(material.getRgb()));
        rVal.setAmbientColor(new Color3f(material.getAmb()));
        rVal.setEmissiveColor(new Color3f(material.getEmis()));
        rVal.setSpecularColor(new Color3f(material.getSpec()));
        rVal.setShininess((float)material.getShi());
        
        return rVal;
    }
    
    
    /** 
     * <p>Provides mechanism for reseting internal state before beginning a 
     * new parse.</p>
     */
    
    public void reset() {
        super.reset();
        scene=new SceneBase();
        world=new BranchGroup();
        currentGroup=null;
        scene.setSceneGroup(world);
        kidsLeft=0;
        callStack=new Stack();
        sourceIsFile=true;
    }
    
    
    /**
     * <p>Accessor for the <code>Scene</code> state.</p>
     *
     * @return The <code>Scene</code> state.
     */
    
    public Scene getScene() {
        return scene;
    }
    
    
    /**
     * <p>Mutator for the <code>basePath</code> property.</p>
     *
     * @param basePath The value to set the <code>basePath</code> property 
     *                 to.
     */
    
    public void setBasePath(String basePath) {
        //textureLoaderHelper.setBasePath(basePath);
        sourceIsFile=true;
    }
    
    
    /**
     * <p>Mutator for the <code>baseUrl</code> property.</p>
     *
     * @param baseUrl The value to set the <code>baseUrl</code> property 
     *                to.
     */
    
    public void setBaseUrl(URL baseUrl) {
        //textureLoaderHelper.setBaseUrl(baseUrl);
        sourceIsFile=false;
    }
    
    
    /**
     * <p>Utility inner class used for call stack state management.</p>
     */
    
    private class CallStackPlaceholder {
        private BranchGroup branchGroup;
        private int kidsLeft;
        
        private CallStackPlaceholder(BranchGroup branchGroup, int kidsLeft) {
            this.branchGroup=branchGroup;
            this.kidsLeft=kidsLeft;
        }
        
        private BranchGroup getBranchGroup() {
            return branchGroup;
        }
        
        private int getKidsLeft() {
            return kidsLeft;
        }        
    }
}
