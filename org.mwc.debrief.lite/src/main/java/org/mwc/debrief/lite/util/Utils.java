/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.mwc.debrief.lite.util;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import org.hsqldb.lib.FileUtil;
import org.jfree.io.FileUtilities;
import org.mwc.debrief.lite.DebriefLiteApp;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class Utils
{

  public static String readManifestVersion() {
    String retVal = null;
    Enumeration<URL> resources = null;

    Manifest manifest = getJarManifest(DebriefLiteApp.class);
    // check that this is your manifest and do what you need or get the next one
    if(manifest!=null && manifest.getMainAttributes()!=null &&
        "Debrief Lite".equals(manifest.getMainAttributes().getValue("Implementation-Title"))) {
      retVal = manifest.getMainAttributes().getValue("Implementation-Version");
    }
    return retVal;
  }
  public static Manifest getJarManifest(Class<?> clazz) {
    String className = clazz.getSimpleName() + ".class";    
    String classPath = clazz.getResource(className).toString();
    //IJ.log("classPath = " + classPath);
    if (!classPath.startsWith("jar")) { // Class not from JAR
      return null;
    }
    String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
    Manifest manifest = null;
    try {
      manifest = new Manifest(new URL(manifestPath).openStream());
    } catch (IOException ignore) { }
    return manifest;
  }
}
