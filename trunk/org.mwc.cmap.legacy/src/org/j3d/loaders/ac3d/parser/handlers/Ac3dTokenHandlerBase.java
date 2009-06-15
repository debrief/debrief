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

package org.j3d.loaders.ac3d.parser.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Stack;
import java.util.Vector;

import org.j3d.loaders.ac3d.models.Ac3dMaterial;
import org.j3d.loaders.ac3d.models.Ac3dObject;
import org.j3d.loaders.ac3d.models.Ac3dSurface;
import org.j3d.loaders.ac3d.parser.LineTokenizer;
import org.j3d.loaders.ac3d.parser.TokenHandler;
import org.j3d.loaders.ac3d.parser.exceptions.AC3DParseException;
import org.j3d.loaders.ac3d.parser.exceptions.InvalidTokenOrderingException;


/**
 * <p><code>Ac3dTokenHandlerBase</code> provides the base functionality for 
 * dealing with object tokens present in the AC3D file format.</p>
 *
 * <p><strong>TODO:</strong><ul>
 * <li> Cleanup, commentary, and optimization.
 * </ul></p>
 *
 * @author  Ryan Wilhm (ryan@entrophica.com)
 * @version $Revision: 1.1.1.1 $
 */

public abstract class Ac3dTokenHandlerBase extends TokenHandler {

    /** The location of the data. */
    private BufferedReader bufferedReader;
    
    /** State for the material index during the parse. */
    protected int materialIndexPtr;
    
    /** State for the surface index during the parse. */
    protected int surfaceIndex;
            
    /** The call stack. */
    protected Stack<Object> callStack;
    
    /** The materials index. */
    protected Vector<Ac3dMaterial> materials;
    
    /** The rendering display list. */
    protected Vector<Object> displayList;
    
    /** The latest version of the file format this parser supports. */
    public static final int SUPPORTED_FORMAT_VERSION=0xb;
    
    
    /**
     * <p>Default constructor. Currently, we set the highest version 
     * supported to <code>0xb</code>.</p>
     */
    
    public Ac3dTokenHandlerBase() {
        super();
        setVersion(SUPPORTED_FORMAT_VERSION);
    }
    
    
    /** 
     * <p>Handle the <code>MATERIAL</code> token.</p>
     *
     * @param tokens The tokens to be dealt with.
     */
    
    public void token_MATERIAL(String[] tokens) throws AC3DParseException {
        Ac3dMaterial material=new Ac3dMaterial();
        int ptr=0;
        
        while (++ptr<tokens.length) {
            if (ptr==1) {
                material.setName(tokens[ptr]);
            } else if (tokens[ptr].equals("rgb")) {
                material.setRgb(parseFloats(tokens, ++ptr, 3));
                ptr+=3;
            } else if (tokens[ptr].equals("amb")) {
                material.setAmb(parseFloats(tokens, ++ptr, 3));
                ptr+=3;
            } else if (tokens[ptr].equals("emis")) {
                material.setEmis(parseFloats(tokens, ++ptr, 3));
                ptr+=3;
            } else if (tokens[ptr].equals("spec")) {
                material.setSpec(parseFloats(tokens, ++ptr, 3));
                ptr+=3;
            } else if (tokens[ptr].equals("shi")) {
                material.setShi(parseDecimal(tokens[++ptr]));
                ptr++;
            } else if (tokens[ptr].equals("trans")) {
                material.setTrans(parseFloat(tokens[++ptr]));
                ptr++;
            }
        }
        
        
        material.setIndex(materialIndexPtr);
        materials.addElement(material);
                
        materialIndexPtr++;
    }
    
    
    /**
     * <p>Handles the <code>OBJECT</code> tag and all of its attributes.</p>
     *
     * @param tokens
     */
    
    public void token_OBJECT(String[] tokens) throws AC3DParseException {
        Ac3dObject obj=new Ac3dObject();
        
        if (tokens.length==2) {
            obj.setType(tokens[1]);
            callStack.push(obj);
            surfaceIndex=-1;
        } else {
            // throw exception
        }
    }
    
    
    /**
     * <p>Handles the token <code>kids</code> and all of its attributes.</p>
     *
     * @param tokens
     * @exception AC3DParseException
     */
    
    public void token_kids(String[] tokens) throws AC3DParseException {
        Ac3dObject obj;

        obj=qualifyTagByAC3DObject(tokens, 2);
        obj.setNumKids(parseDecimal(tokens[1]));       
        displayList.addElement(callStack.pop());
    }
    
    
    /**
     * <p></p>
     * 
     * @param tokens All of the tokens associated with the token command.
     */
    
    public void token_numvert(String[] tokens) throws AC3DParseException {
        Ac3dObject obj;
        String[] ref;
        
        obj=qualifyTagByAC3DObject(tokens, 2);
        obj.setNumvert(parseDecimal(tokens[1]));
        
        try {
            for (int i=0; i<obj.getNumvert(); i++) {
                ref=LineTokenizer.enumerateTokens(bufferedReader.readLine());
                obj.addVertex(i, parseFloats(ref, 0, 3));
            }
        } catch (IOException e) {
            error(e, "Could not read all of the verticies.");
            throw new AC3DParseException(e.getMessage());
        }
    }    
    

    
    /**
     * <p>Extracts the name value.</p>
     *
     * @param tokens
     * @exception AC3DParseException 
     */
    
    public void token_name(String[] tokens) throws AC3DParseException {
        Ac3dObject obj;
        obj=qualifyTagByAC3DObject(tokens, 2);
        obj.setName(tokens[1]);
    }
    
    
    /**
     * <p></p>
     *
     * @param tokens
     * @exception AC3DParseException 
     */
    
    public void token_loc(String[] tokens) throws AC3DParseException {
        Ac3dObject obj;
        
        obj=qualifyTagByAC3DObject(tokens, 4);
        obj.setLoc(parseFloats(tokens, 1, 3));
    }
    
    
    /**
     * <p></p>
     *
     * @param tokens
     * @exception AC3DParseException 
     */
    
    public void token_rot(String[] tokens) throws AC3DParseException {
        Ac3dObject obj;
        
        obj=qualifyTagByAC3DObject(tokens, 10);
        obj.setRot(parseFloats(tokens, 1, 9));
    }
    
    
    /**
     * <p></p>
     *
     * @param tokens
     * @exception AC3DParseException
     */
    
    public void token_numsurf(String[] tokens) throws AC3DParseException {
        Ac3dObject obj;
        
        obj=qualifyTagByAC3DObject(tokens, 2);
        obj.setNumsurf(parseDecimal(tokens[1]));
        surfaceIndex=0;
    }
    
    
    /**
     * <p></p>
     *
     * @param tokens
     * @exception AC3DParseException
     */
    
    public void token_SURF(String[] tokens) throws AC3DParseException {
        Ac3dSurface surface;
        
        qualifyTagByAC3DObject(tokens, 2);
        surface=new Ac3dSurface();
        surface.setFlags(parseHexidecimal(tokens[1]));
        callStack.push(surface);
    }
    
    
    /**
     * ## TODO: Finish
     * 
     * @param tokens 
     * @exception AC3DParseException 
     */
    
    public void token_refs(String[] tokens) throws AC3DParseException {
        Ac3dSurface surface;
        Ac3dObject parent;
        Object tmpObj;
        String[] ref;
        
        surface=qualifyTagByAC3DSurface(tokens, 2);
        
        surface.setNumrefs(parseDecimal(tokens[1]));
        try {
            for (int i=0; i<surface.getNumrefs(); i++) {
                ref=LineTokenizer.enumerateTokens(bufferedReader.readLine());
                surface.addRef(i, parseDecimal(ref[0]), parseFloats(ref, 1, 2));
            }

            debug("SURFACE defined as: " + surface.toString());
            callStack.pop(); // remove from call stack
            
            tmpObj=callStack.peek();
            if (tmpObj instanceof Ac3dObject) {
                parent=(Ac3dObject)tmpObj;
                parent.addSurface(surfaceIndex, surface);
            }
            surfaceIndex++;
        } catch (IOException e) {
            error(e, "Could not read all of the refs.");
            throw new AC3DParseException(e.getMessage());
        }
    }
        
    
    /**
     * <p>Handle the token command for defining <code>mat</code>, the material 
     * reference used for the surface.</p>
     *
     * @param tokens
     * @exception AC3DParseException
     */
    
    public void token_mat(String[] tokens) throws AC3DParseException {
        Ac3dSurface surface;
        
        surface=qualifyTagByAC3DSurface(tokens, 2);
        surface.setMat(parseDecimal(tokens[1]));
    }
    
    
    /**
     * <p>Handles the <code>OBJECT</code> tag and all of its attributes.</p>
     *
     * @param tokens
     */
    
    public void token_texture(String[] tokens) throws AC3DParseException {
        Ac3dObject obj=new Ac3dObject();
        
        obj=qualifyTagByAC3DObject(tokens, 2);
        obj.setTexture(tokens[1]);
    }
    
    
    /**
     * <p>Mutator to set the <code>bufferedReader</code> property. This is 
     * needed to capture additional lines of crap for a given token.</p>
     *
     * @param bufferedReader The <code>BufferedReader</code> to cull the 
     *                       parsable data from.
     */
    
    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader=bufferedReader;
    }
    
    
    /**
     * <p>Implementation required by superclass <code>TokenHandler</code> to 
     * reset the internal state.</p>
     */
    
    public void reset() {
        materialIndexPtr=0;
        surfaceIndex=-1;
        callStack=new Stack<Object>();
        materials=new Vector<Ac3dMaterial>();
        displayList=new Vector<Object>();
    }
    
    
    /**
     * <p>Helper function that qualifies a tag by whether or not its parent 
     * should be an instance of <code>AC3DObject</code> or not, as well as 
     * the number of arguements for the command.</p>
     *
     * @param tokens All of the tokens for the command.
     * @param numArgsRequired The number of arguements for the token 
     *                        command.
     * @return The <code>Object</code> from the stack.
     * @exception AC3DParseException
     */
    
    private final Ac3dObject qualifyTagByAC3DObject(String[] tokens, 
        int numArgsRequired) throws AC3DParseException {
    
        Object tmpObj=null;
        
        if (!(tokens.length==numArgsRequired)) {
            throw new AC3DParseException("Wrong number of args for " +
                tokens[0] + "; expecting " + numArgsRequired + ", got " + 
                tokens.length);
        }
        
        if (!(callStack.size()>0)) {
            throw new AC3DParseException("Parent not found on stack!");
        }
        
        tmpObj=callStack.peek();
        if (!(tmpObj instanceof Ac3dObject)) {
            throw new InvalidTokenOrderingException("Was expecting: \"" +
                "Ac3dObject" + ", instead got: \"" + 
                tmpObj.getClass().getName() + "\".");
        }
        
        return (Ac3dObject)tmpObj;
    }
    
    
    /**
     * <p>Helper function that qualifies a tag by whether or not its parent 
     * should be an instance of <code>AC3DSurface</code> or not, as well as 
     * the number of arguements for the command.</p>
     *
     * @param tokens All of the tokens for the command.
     * @param numArgsRequired The number of arguements for the token 
     *                        command.
     * @return The <code>Object</code> from the stack.
     * @exception AC3DParseException
     */
    
    private final Ac3dSurface qualifyTagByAC3DSurface(String[] tokens, 
        int numArgsRequired) throws AC3DParseException {
    
        Object tmpObj=null;
        
        if (!(tokens.length==numArgsRequired)) {
            throw new AC3DParseException("Wrong number of args for " +
                tokens[0] + "; expecting " + numArgsRequired + ", got " + 
                tokens.length);
        }
        
        if (!(callStack.size()>0)) {
            throw new AC3DParseException("Parent not found on stack!");
        }
        
        tmpObj=callStack.peek();
        if (!(tmpObj instanceof Ac3dSurface)) {
            throw new InvalidTokenOrderingException("Was expecting: \"" +
                "Ac3dSurface" + ", instead got: \"" + 
                tmpObj.getClass().getName() + "\".");
        }
        
        return (Ac3dSurface)tmpObj;
    }
    
    
    /**
     * <p>Helper function to parse a number of <code>float</code> strings 
     * into an array.</p>
     *
     * @param in The list of strings to parse.
     * @param offset The starting position of the floats to extract.
     * @param num The number of floats to extract from the starting position.
     * @return The array of parsed floats.
     */
    
    private static final float[] parseFloats(String[] in, int offset, int num) {
        float[] rVal = new float[num];
        
        for (int i=0; i<num; i++) {
            rVal[i]=parseFloat(in[offset+i]);
        }
        
        return rVal;
    }
    
    
    /**
     * <p>Helper function to parse a decimal value into an <code>int</code>. 
     * The method definition should present this method as a candidate for 
     * inlining by an optimizing compiler, since it is statically 
     * resolvable.</p>
     *
     * @param in The <code>String</code> to convert into an <code>int</code>.
     * @return The converted <code>int</code>.
     */
    
    private static final int parseDecimal(String in) {
        return (Integer.valueOf(in)).intValue();
    }
    
    
    /**
     * <p>Helper function to convert a decimal presented in hex to an int.
     * The method definition should present this method as a candidate for 
     * inlining by an optimizing compiler, since it is statically 
     * resolvable.</p>
     *
     * @param in The <code>String</code> to convert.
     * @return The converted <code>int</code>.
     */
    
    private static final int parseHexidecimal(String in) {
        
        if (in.startsWith("0x") || in.startsWith("0X")) {
            in=in.substring(2, in.length());
        }
                
        return (Integer.valueOf(in, 16)).intValue();
    }
    
    
    /**
     * <p>Helper function to parse a decimal value into a <code>float</code>. 
     * The method definition should present this method as a candidate for 
     * inlining by an optimizing compiler, since it is statically 
     * resolvable.</p>
     *
     * @param in The <code>String</code> to convert into a <code>float</code>.
     * @return The converted <code>float</code>.
     */
    
    private static final float parseFloat(String in) {
        float rVal;

        if (in.indexOf(".")<0) {
            in+=".0";
        }
        rVal=Float.valueOf(in).floatValue();

        return rVal;
    }
}
