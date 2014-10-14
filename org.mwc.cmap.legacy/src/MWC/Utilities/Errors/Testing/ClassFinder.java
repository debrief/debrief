/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.Utilities.Errors.Testing;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

/**
 * Find all the files underneath a directory that are class files, compute their
 * class name from their location in the directory structure and build a list of
 * class names.
 */
public class ClassFinder {
    final private Vector<Object> classNameList = new Vector<Object> ();

    public ClassFinder(final File classPathRoot) throws IOException {
        findAndStoreTestClasses (classPathRoot);
    }

    private void findAndStoreTestClasses (final File currentDirectory) throws IOException {
        final String files[] = currentDirectory.list();
        for(int i = 0;i < files.length;i++) {
            final File file = new File(currentDirectory, files[i]);
            final String fileBase = file.getName ();
            final int idx = fileBase.indexOf(".class");
            final int CLASS_EXTENSION_LENGTH = 6;
            if(idx != -1 && (fileBase.length() - idx) == CLASS_EXTENSION_LENGTH) {
//                jcf.io.ClassFileInputStream inputStream = new jcf.io.ClassFileInputStream(new FileInputStream (file));
//                jcf.ClassFile classFile = new jcf.ClassFile (inputStream);
//                if(i == 0)
//                {
//                  System.out.println("");
//                  System.out.print ("Processing: " + classFile.getPackageName());
//                }
//                else
//                {
//                  System.out.print (".");
//                }
//
////                System.out.println ("Processing: " + classFile.getFullName ().replace ('/','.'));
//                classNameList.add (classFile.getFullName ().replace ('/','.'));
            }
            else {
                if(file.isDirectory()) {
                    findAndStoreTestClasses (file);
                }
            }
        }
    }

    public Iterator<?> getClasses () {
        return classNameList.iterator ();
    }
}