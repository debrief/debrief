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
package org.mwc.cmap.gt2plot.data;

/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Various file utilities useful when dealing with bytes, bits and numbers
 * </p>
 * 
 * @author Andrea Antonello - www.hydrologis.com
 * @since 1.1.0
 */
public class FileUtilities {

    public static void copyFile( final String fromFile, final String toFile ) throws IOException {
        final File in = new File(fromFile);
        final File out = new File(toFile);
        copyFile(in, out);
    }

    public static void copyFile( final File in, final File out ) throws IOException {
        final FileInputStream fis = new FileInputStream(in);
        final FileOutputStream fos = new FileOutputStream(out);
        final byte[] buf = new byte[1024];
        int i = 0;
        while( (i = fis.read(buf)) != -1 ) {
            fos.write(buf, 0, i);
        }
        fis.close();
        fos.close();
    }

    /**
     * Returns true if all deletions were successful. If a deletion fails, the method stops
     * attempting to delete and returns false.
     * 
     * @param filehandle
     * @return true if all deletions were successful
     */
    public static boolean deleteFileOrDir( final File filehandle ) {

        if (filehandle.isDirectory()) {
            final String[] children = filehandle.list();
            for( int i = 0; i < children.length; i++ ) {
                final boolean success = deleteFileOrDir(new File(filehandle, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        final boolean isdel = filehandle.delete();
        if (!isdel) {
            // if it didn't work, which often happens on windows systems,
            // remove on exit
            filehandle.deleteOnExit();
        }

        return isdel;
    }

    /**
     * Delete file or folder recursively on exit of the program
     * 
     * @param filehandle
     * @return true if all went well
     */
    public static boolean deleteFileOrDirOnExit( final File filehandle ) {
        if (filehandle.isDirectory()) {
            final String[] children = filehandle.list();
            for( int i = 0; i < children.length; i++ ) {
                final boolean success = deleteFileOrDir(new File(filehandle, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        filehandle.deleteOnExit();
        return true;
    }

    /**
     * Read from an inoutstream and convert the readed stuff to a String. Usefull for text files
     * that are available as streams.
     * 
     * @param inputStream
     * @return the read string
     * @throws IOException 
     */
    public static String readInputStreamToString( final InputStream inputStream ) throws IOException {
        // Create the byte list to hold the data
        final List<Byte> bytesList = new ArrayList<Byte>();

        byte b = 0;
        while( (b = (byte) inputStream.read()) != -1 ) {
            bytesList.add(b);
        }
        // Close the input stream and return bytes
        inputStream.close();

        final byte[] bArray = new byte[bytesList.size()];
        for( int i = 0; i < bArray.length; i++ ) {
            bArray[i] = bytesList.get(i);
        }

        final String file = new String(bArray);
        return file;
    }

    /**
     * Read text from a file in one line.
     * 
     * @param filePath the path to the file to read.
     * @return the read string.
     * @throws IOException 
     */
    public static String readFile( final String filePath ) throws IOException {
        return readFile(new File(filePath));
    }

    /**
     * Read text from a file in one line.
     * 
     * @param file the file to read.
     * @return the read string.
     * @throws IOException 
     */
    public static String readFile( final File file ) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            final StringBuilder sb = new StringBuilder(200);
            String line = null;
            while( (line = br.readLine()) != null ) {
                sb.append(line);
                sb.append("\n"); //$NON-NLS-1$
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    /**
     * Read text from a file to a list of lines.
     * 
     * @param outFile the path to the file to read.
     * @return the list of lines.
     * @throws IOException 
     */
    public static List<String> readFileToLinesList( final String filePath ) throws IOException {
        return readFileToLinesList(new File(filePath));
    }

    /**
     * Read text from a file to a list of lines.
     * 
     * @param file the file to read.
     * @return the list of lines.
     * @throws IOException 
     */
    public static List<String> readFileToLinesList( final File file ) throws IOException {
        BufferedReader br = null;
        final List<String> lines = new ArrayList<String>();
        try {
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while( (line = br.readLine()) != null ) {
                lines.add(line);
            }
            return lines;
        } finally {
            br.close();
        }
    }

    /**
     * Write text to a file in one line.
     * 
     * @param text the text to write.
     * @param file the file to write to.
     * @throws IOException 
     */
    public static void writeFile( final String text, final File file ) throws IOException {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(text);
        } finally {
            bw.close();
        }
    }

    /**
     * Write a list of lines to a file.
     * 
     * @param lines the list of lines to write.
     * @param outFile the path to the file to write to.
     * @throws IOException 
     */
    public static void writeFile( final List<String> lines, final String filePath ) throws IOException {
        writeFile(lines, new File(filePath));
    }

    /**
     * Write a list of lines to a file.
     * 
     * @param lines the list of lines to write.
     * @param file the file to write to.
     * @throws IOException 
     */
    public static void writeFile( final List<String> lines, final File file ) throws IOException {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            for( final String line : lines ) {
                bw.write(line);
                bw.write("\n"); //$NON-NLS-1$
            }
        } finally {
            bw.close();
        }
    }

    public static String replaceBackSlashes( final String path ) {
        return path.replaceAll("\\\\", "\\\\\\\\"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Returns the name of the file without the extention.
     * 
     * @param file the file to trim.
     * @return the name without extention.
     */
    public static String getNameWithoutExtention( final File file ) {
        String name = file.getName();
        final int lastDot = name.lastIndexOf("."); //$NON-NLS-1$
        name = name.substring(0, lastDot);
        return name;
    }

    /**
     * Substitute the extention of a file.
     * 
     * @param file the file.
     * @param newExtention the new extention.
     * @return the file with the new extention.
     */
    public static File substituteExtention( final File file, final String newExtention ) {
        String path = file.getAbsolutePath();
        final int lastDot = path.lastIndexOf("."); //$NON-NLS-1$
        path = path.substring(0, lastDot) + "." + newExtention; //$NON-NLS-1$
        return new File(path);
    }

    /**
     * Makes a file name safe to be used.
     * 
     * <p>Taken from http://stackoverflow.com/questions/1184176/how-can-i-safely-encode-a-string-in-java-to-use-as-a-filename
     * 
     * @param fileName the file name to "encode".
     * @return the safe filename.
     */
    public static String getSafeFileName(final String fileName){
        final char fileSep = '/'; // ... or do this portably.
        final char escape = '%'; // ... or some other legal char.
        final int len = fileName.length();
        final StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            final char ch = fileName.charAt(i);
            if (ch < ' ' || ch >= 0x7F || ch == fileSep // add other illegal chars
                || (ch == '.' && i == 0) // we don't want to collide with "." or ".."!
                || ch == escape) {
                sb.append(escape);
                if (ch < 0x10) {
                    sb.append('0');
                }
                sb.append(Integer.toHexString(ch));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}