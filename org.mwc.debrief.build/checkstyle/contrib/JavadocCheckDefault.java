/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Tool that generates Javadoc description of the default tokens for a Check.
 * @author Rick Giles
 * @version 27-Nov-2002
 */
public class JavadocCheckDefault
{

    private static void usage()
    {
        System.out.println("Usage: java JavadocCheckDefault check element");
        System.exit(0);
    }
    
    public static void main(String[] args)
    {        
        if (args.length < 2) {
            usage();
        }
        final String header =
            " * <p> By default the check will check the following "
                + args[1] + ":\n";
        final String footer = ".\n * </p>\n";
        final String prefix = " *  {@link TokenTypes#";

        try {
            final Class clazz = Class.forName(args[0]);
            final Check check = (Check) clazz.newInstance();            
            final int[] defaultTokens = check.getDefaultTokens();
            if (defaultTokens.length > 0) {
                final StringBuffer buf = new StringBuffer();
                buf.append(header);
                final ArrayList tokenNames = new ArrayList();
                for (int i = 0; i < defaultTokens.length; i++) {
                    tokenNames.add(TokenTypes.getTokenName(defaultTokens[i]));
                }
                Collections.sort(tokenNames);
                final Iterator it = tokenNames.iterator();
                String token = (String) it.next();
                buf.append(prefix + token + " " + token + "}");
                while (it.hasNext()) {
                    token = (String) it.next();
                    buf.append(",\n" + prefix + token + " " + token + "}");
                }
                buf.append(footer);
                System.out.println(buf);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }
}
