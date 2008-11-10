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

package org.j3d.loaders.ac3d.parser;

import java.lang.reflect.*;
import org.j3d.loaders.ac3d.parser.handlers.ConsoleOutputHandler;


/**
 * <p><code>TokenHandler</code> is the abstract base class that defines the 
 * callable interface that <code>AC3DFileParser</code> will use to deal 
 * with the tokens extracted. In order to implement a proper subclass, all 
 * that needs to be done is to implement the <code>reset()</code> interface, 
 * which allows instances of <code>TokenHandler</code> to be reused.</p>
 *
 * <p><strong>TODO:</strong> Instead of introspecting and invoking at 
 * runtime for each token, it would be smart to introspect and index once at 
 * instantiation time, and provide a lookup hash table... The currnet method 
 * could be unnecessarily slow for very complex files.</p>
 *
 * @author  Ryan Wilhm (ryan@entrophica.com)
 * @version $Revision: 1.1.1.1 $
 */

public abstract class TokenHandler {
    
    /** The definition of the arguements list for the handler impls. */
    private static Class[] TOKEN_ARGS_LIST;
    
    /** Specifies whether or not to spew debug data. */
    private boolean shouldDebug;
    
    /** Specifies whether or not to spew error data. */
    private boolean shouldError;
    
    /** The <code>OutputHandler</code> for debug messages. */
    private OutputHandler debugHandler;
    
    /** The <code>OutputHandler</code> for error messages. */
    private OutputHandler errorHandler;
    
    /** Specifies the compatibility version. */
    private int version;
    
        
    static {
        TOKEN_ARGS_LIST=new Class[1];
        TOKEN_ARGS_LIST[0]=String[].class;        
    }
    
    /**
     * <p>Default constructor. This sets the version supported to a 
     * ludicrous value, so that the implementing subclasses will 
     * not work unless they explicitly set their version.</p>
     */
    
    public TokenHandler() {
        setVersion(0xffff);
        shouldDebug=false;
        shouldError=true;
        errorHandler=new ConsoleOutputHandler();
    }
    
    
    /**
     * <p>Resets the internal state of the instance. Subclasses must provide 
     * an implementation of this method that properly resets the internal 
     * resources.</p>
     */
    
    public abstract void reset();
    
    
    /**
     * <p></p>
     * 
     * @param tokens
     */
    
    public final void handle(String[] tokens) {
        Method method;
        Object[] args;
        
        try {
            method=(this.getClass()).getMethod("token_" + tokens[0], TOKEN_ARGS_LIST);
            args=new Object[1];
            args[0]=tokens;
            method.invoke(this, args);
        } catch (NoSuchMethodException e) {
            debug("UNKNOWN TOKEN: " + tokens[0]);
        } catch (InvocationTargetException invokeException) {
            Throwable t=invokeException.getTargetException();
            error(t, "Invokation problem encountered on handle.");
        } catch (Exception e) {
            // Should probably throw up
            error(e, "Unanticipated exception when trying to handle tokens.");
        }
    }
    
    
    /**
     * <p>Mutator for the <code>shouldDebug</code> property.</p>
     * 
     * @param shouldDebug Value to set the <code>shouldDebug</code> 
     *                    property to.
     */
    
    public final void setShouldDebug(boolean shouldDebug) {
        this.shouldDebug=shouldDebug;
    }
    
    
    /**
     * <p>Mutator for the <code>shouldError</code> property.</p>
     * 
     * @param shouldError Value to set the <code>shouldError</code> 
     *                    property to.
     */
    
    public final void setShouldError(boolean shouldError) {
        this.shouldError=shouldError;
    }
    
    
    /**
     * <p>Mutator that allows setting of the <code>debugHandler</code> 
     * mechanism.</p>
     *
     * @param debugHandler The <code>OutputHandler</code> to set our 
     *                     debugHandler to.
     */
    
    public final void setDebugHandler(OutputHandler debugHandler) {
        if (debugHandler!=null) {
            this.shouldDebug=true;
            this.debugHandler=debugHandler;
        } else {
            this.shouldDebug=false;
        }
    }
        
    
    /**
     * <p>Mutator that allows setting of the <code>errorHandler</code> 
     * mechanism.</p>
     *
     * @param errorHandler The <code>OutputHandler</code> to set our 
     *                     errorHandler to.
     */
    
    public final void setErrorHandler(OutputHandler errorHandler) {
        if (errorHandler!=null) {
            this.shouldError=true;
            this.errorHandler=errorHandler;
        } else {
            this.shouldError=false;
        }
    }
   
    
    /**
     * <p>Provides outlet for dumping out debug data.</p>
     *
     * @param message
     */
    
    protected final void debug(String message) {
        if (shouldDebug && debugHandler!=null) {
            debugHandler.println(message);
        }
    }
    
    
    /**
     * <p>Provides outlet for dumping out error data.</p>
     *
     * @param throwable
     * @param message
     */
    
    protected final void error(Throwable throwable, String message) {
        if (shouldError && errorHandler!=null) {
            errorHandler.println(message);
            errorHandler.println(throwable.getMessage());
        }
    }
    
    
    /**
     * <p>Mutator for the versioning information. This can only be set by 
     * descendants.</p>
     *
     * @param version The version number to set to.
     */
    
    protected final void setVersion(int version) {
        this.version=version;
    }
    
    
    /**
     * <p>Accessor for the versioning information.</p>
     *
     * @return The version number this handler supports.
     */
    
    public final int getVersion() {
        return version;
    }
}
