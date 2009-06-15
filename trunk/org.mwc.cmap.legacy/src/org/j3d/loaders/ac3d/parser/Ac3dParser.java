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

import java.io.BufferedReader;
import java.io.IOException;

import org.j3d.loaders.ac3d.parser.exceptions.IncompatibleTokenHandlerException;


/**
 * <p><code>AC3DFileParser</code> handles the work of parsing the AC3D data 
 * from a stream. Since the AC3D file format is not only ASCII based, but 
 * also formatted using lines, this gets away with just using a 
 * <code>BufferedReader</code> and picking off the lines one by one.</p>
 *
 * <p>Although this parser was implemented to facilitate building an AC3D 
 * file loader for importing models into Java3D, this parser aims to be 
 * independant of the Java3D API. The intention is to be able to leverage this 
 * code in other applications, as well. (Perhaps in a command line format 
 * conversion tool...) Thus, the separation of Java3D and parsing code.</p>
 *
 * @author  Ryan Wilhm (ryan@entrophica.com)
 * @version $Revision: 1.1.1.1 $
 */

public class Ac3dParser {

    /** The header preamble. */
    public static final String HEADER_PREAMBLE="AC3D";
    
    /** The latest version of the file format this parser supports. */
    public static final int SUPPORTED_FORMAT_VERSION=0xb;
    
    /** Where the data comes from. */
    private BufferedReader bufferedReader;
    
    /** The <code>TokenHandler</code> to use. */
    private TokenHandler tokenHandler;
    
    
    /**
     * <p>Performs the action of parsing the data stream already set.</p>
     */
    
    public void parse() {
        String buffer;
        String[] buffer2;
        
        try {
            // Deal with header
            primeHeader();
            // Reset the TokenHandler set for the parse
            tokenHandler.reset();
            // Read the token input line by line
            while ((buffer=bufferedReader.readLine())!=null) {
                buffer2=LineTokenizer.enumerateTokens(buffer);
                tokenHandler.handle(buffer2);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
    
    
    /**
     * <p>Reads the header and determines if the parser is capable of handling 
     * the data.</p>
     *
     * @exception IOException
     */
    
    private void primeHeader() throws IOException {
        int version;
        String header=bufferedReader.readLine();
                
        if (header.length()<5) {
            System.out.println("AC3D data stream header too short.");
            throw new RuntimeException();
        }
        
        if ((header.substring(0,3)).equals(HEADER_PREAMBLE)) {
            System.out.println("Data stream did not contain the preamble.");
            throw new RuntimeException();
        }

        version=Integer.valueOf(header.substring(4, header.length()), 16).intValue();
        if (version>SUPPORTED_FORMAT_VERSION) {
            System.out.println("Format version in data stream grater than supported.");
            throw new RuntimeException();
        }        
    }
    
    
    /**
     * <p>Mutator for setting the <code>BufferedReader</code> to read the 
     * AC3D data from.</p>
     *
     * @param bufferedReader 
     */
    
    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader=bufferedReader;
    }
    
    
    /**
     * <p>Mutator for setting the <code>TokenHandler</code> implementation to 
     * handle the AC3D token data with.</p>
     *
     * @param tokenHandler
     * @exception IncompatibleTokenHandlerException
     */
    
    public void setTokenHandler(TokenHandler tokenHandler) 
        throws IncompatibleTokenHandlerException {
            
        if (tokenHandler.getVersion()<=SUPPORTED_FORMAT_VERSION) {
            this.tokenHandler=tokenHandler;
        } else {
            throw new IncompatibleTokenHandlerException("TokenHandler not compatible");
        }
    }
}
