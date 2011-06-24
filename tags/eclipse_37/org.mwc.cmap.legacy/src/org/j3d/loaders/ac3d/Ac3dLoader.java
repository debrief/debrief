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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.j3d.loaders.ac3d.parser.Ac3dParser;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.Loader;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;


/**
 * <p><code>Ac3dLoader</code> provides an implementation of the Java 3D
 * <code>Loader</code> utility class that loads the content of an
 * AC3D file into an instance of the Java3D <code>Scene</code> utility
 * class. This basically just allows Java developers to import any
 * models generated using AC3D into their code.</p>
 *
 * @author  Ryan Wilhm (ryan@entrophica.com)
 * @version $Revision: 1.1.1.1 $
 */

public class Ac3dLoader implements Loader {

    /** The token handler, which populates the <code>Scene</code>. */
    private LoaderTokenHandler tokenHandler;

    /** The flages that specify constraints for the traversal. */
    private int flags;

    /** */
    private String basePath;

    /** */
    private URL baseUrl;


    /**
     * <p>Default constructor, which initializes the token handler.</p>
     */

    public Ac3dLoader() {
        tokenHandler=new LoaderTokenHandler();
        flags=0;
        basePath=null;
        baseUrl=null;
    }


    /**
     *
     * @param fileName
     * @return The scene instance representing the contents of the file
     * @exception FileNotFoundException
     * @exception IncorrectFormatException
     * @exception ParsingException
     */
    public Scene load(String fileName) throws FileNotFoundException,
        IncorrectFormatException, ParsingErrorException {

        Reader reader;

        if (basePath==null) {
            tokenHandler.setBasePath(new File(fileName).getParent());
        } else {
            tokenHandler.setBasePath(basePath);
        }

        reader = new FileReader(fileName);
        return load(reader);
    }


    /**
     *
     * @param reader
     * @return The scene instance representing the contents of the file
     * @exception FileNotFoundException
     * @exception IncorrectFormatException
     * @exception ParsingException
     */
    public Scene load(Reader reader) throws FileNotFoundException,
        IncorrectFormatException, ParsingErrorException {

        Scene rVal=null;
        BufferedReader br=new BufferedReader(reader);
        Ac3dParser parser=new Ac3dParser();


        try {
            parser.setBufferedReader(br);
            tokenHandler.setBufferedReader(br);
            parser.setTokenHandler(tokenHandler);
            parser.parse();
            rVal=tokenHandler.getScene();
        } catch (Exception e) {
            // Deal with exception stuff
            System.err.println("Exception during parse: " + e.getMessage());
            e.printStackTrace(System.err);
        }

        return rVal;
    }


    /**
     *
     * @param url
     * @return The scene instance representing the contents of the file
     * @exception FileNotFoundException
     * @exception IncorrectFormatException
     * @exception ParsingException
     */
    public Scene load(URL url) throws FileNotFoundException,
        IncorrectFormatException, ParsingErrorException {

        Reader reader;

        if (baseUrl==null) {
            tokenHandler.setBaseUrl(url);
        } else {
            tokenHandler.setBaseUrl(baseUrl);
        }

        try {
            reader = new InputStreamReader(url.openStream());
        } catch (IOException e) {
            throw new FileNotFoundException(e.getMessage());
        }

        return load(reader);
    }


    /**
     */

    public void setFlags(int flags) {
        this.flags=flags;
    }


    /**
     * <p>Accessor for the <code>flags</code> property.</p>
     *
     * @return The current state of the <code>flags</code> property.
     */

    public int getFlags() {
        return flags;
    }


    /**
     *
     */

    public void setBaseUrl(URL baseUrl) {
        this.baseUrl=baseUrl;
    }


    /**
     *
     */

    public URL getBaseUrl() {
        return baseUrl;
    }


    /**
     * <p>Mutator that sets the <code>basePath</code> property.</p>
     *
     * @param basePath The value to set the <code>basePath</code> property
     *                 to.
     */

    public void setBasePath(String basePath) {
        this.basePath=basePath;
    }


    /**
     * <p>Accessor for the <code>basePath</code> property.</p>
     *
     * @return The current state of the <code>basePath</code> property.
     */

    public String getBasePath() {
        return basePath;
    }
}
