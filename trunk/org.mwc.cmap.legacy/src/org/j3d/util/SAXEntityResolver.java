/*****************************************************************************
 *                        J3D.org Copyright (c) 2000
 *                               Java Source
 *
 * This source is licensed under the GNU LGPL v2.1
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information
 *
 ****************************************************************************/

package org.j3d.util;

// Standard imports
import java.io.InputStream;
import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * An entity resolver for both DOM and SAX models of the SAX document.
 * <p>
 * The entity resolver only handles queries for the DTD. It will find
 * any URI that ends in *.dtd and return an {@link org.xml.sax.InputSource}.
 * <p>
 * As the SAX specification does not yet define what the system resource
 * ID is, we'll take a guess. The current resolution scheme only strips the
 * name of the DTD from the URI and attempts to find that in the classpath.
 * <p>
 * To determine the DTD name it will search from the end of the string until it
 * finds a '/' character. The resulting string is treated as a filename to
 * search for. This filename is then found in the CLASSPATH used by the
 * application using the standard Java resolution rules. Note that we do not
 * need to implement any more intelligent behaviour than this because if the
 * System or PublicID returned are files or URLs, the standard parser
 * mechanisms will load them. The only more intelligent behaviour that we may
 * wish to add in the future will be to resolve a full URN if we are given it.
 * <p>
 * The current implementation ignores the publicId information.
 *
 * @author Justin Couch
 * @version $Revision: 1.1.1.1 $
 */
public class SAXEntityResolver implements EntityResolver
{
    /**
     * Resolve the combination of system and public identifiers. This
     * resolver ignores the publicId information.
     *
     * @param publicId The public identifier to use (if set)
     * @param systemId The system identifier to resolve
     * @return An input source to the entity or null if not handled
     * @throws IOException An error reading the stream
     */
    public InputSource resolveEntity(String publicId, String systemId)
        throws IOException
    {
/*
System.out.println("Entity Resolver called.");
System.out.println(" publicID: " + publicId);
System.out.println(" systemID: " + systemId);
*/

        InputSource ret_val = null;

        InputStream is = resolveDTD(systemId);

        if(is != null)
        {
            ret_val = new InputSource(is);
            ret_val.setPublicId(publicId);
            ret_val.setSystemId(systemId);
        }

        return ret_val;
    }

    /**
     * Resolve the DTD uri and return an InputStream used by this.
     *
     * @param uri The DTD uri to resolve
     * @return An input stream to the entity or null if not handled
     * @throws IOException An error reading the stream
     */
    private InputStream resolveDTD(String uri) throws IOException
    {

        InputStream ret_val = null;

        // grab the system ID and remove the last word from it prior to
        // a '/' (if one exists).
        int pos = uri.lastIndexOf('/');

        String filename = uri;

        if(pos != -1)
            filename = uri.substring(pos + 1);

        ClassLoader cl = ClassLoader.getSystemClassLoader();
        ret_val = cl.getResourceAsStream(filename);

        return ret_val;
    }
}

