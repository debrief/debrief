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
import java.net.URLClassLoader;
import java.util.jar.Manifest;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class Utils
{

  public static String readManifestVersion() {
    String retVal = null;
    URLClassLoader cl = (URLClassLoader) Utils.class.getClassLoader();
    try {
      URL url = cl.findResource("META-INF/MANIFEST.MF");
      Manifest manifest = new Manifest(url.openStream());
      // do stuff with it
      retVal = manifest.getMainAttributes().getValue("Implementation-Version");
    } catch (IOException E) {
      retVal = null;
      //log error;
    }
    return retVal;
  }
}
