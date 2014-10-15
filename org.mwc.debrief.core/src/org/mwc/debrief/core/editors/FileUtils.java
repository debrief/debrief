/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.core.editors;

/**
 * Copyright (c) 2003 - 2007 OpenSubsystems s.r.o. Slovak Republic. All rights reserved.
 * 
 * Project: OpenSubsystems
 * 
 * $Id: FileUtils.java,v 1.12 2007/02/01 07:18:32 bastafidli Exp $
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License. 
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
 */


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Collection of methods to make work with files easier.
 * 
 * @version $Id: FileUtils.java,v 1.12 2007/02/01 07:18:32 bastafidli Exp $
 * @author Miro Halas
 * @code.reviewer Miro Halas
 * @code.reviewed 1.7 2006/05/21 03:45:37 bastafidli
 */
public class FileUtils
{
   // Configuration settings ///////////////////////////////////////////////////
   
   /**
    * Default 10 digit file storage distribution array. This means that if I 
    * want to name file as 10 digit number e.g. number 123 as 0000000123 or 
    * number 123456789 as 01234567890. Then the path constructed from number 
    * 1234567890 using distribution 2/2/2/4 would be 12/34/56/0123456789 
    */
   public static final int[] DEFAULT_STRORAGE_TREE_DISTRIBUTION = {2, 2, 2, 4};
   
   /**
    * How big buffer to use to process files.
    */
   public static final int BUFFER_SIZE = 65536;
   
   // Cached values ////////////////////////////////////////////////////////////

   
   /**
    * Temporary directory to use. It is guarantee that it ends with \ (or /)
    */
   protected static String s_strTempDirectory;
   
   // Constructors /////////////////////////////////////////////////////////////

   
  
   
   /**
    * Move file to a new location. If the destination is on different volume,
    * this file will be copied and then original file will be deleted.
    * If the destination already exists, this method renames it with different
    * name and leaves it in that directory and moves the new file along side 
    * the renamed one.
    * 
    * @param flCurrent - file to move
    * @param flDestination - destination file
    * @throws IOException - error message
    * @throws OSSException - error message
    */
   public static void moveFile(
      final File flCurrent, 
      final File flDestination
   ) throws IOException
   {
      // Make sure that the source exist, it might be already moved from 
      // a directory and we just don't know about it
      if (flCurrent.exists())
      {
         // Next check if the destination file exists
         if (flDestination.exists())
         {
            // If the destination exists, that means something went wrong
            // Rename the destination file under temporaty name and try to  
            // move the new file instead of it
          
            renameToTemporaryName(flDestination, "old");
         }
      
         // Make sure the directory exists and if not create it
         File flFolder;
         
         flFolder = flDestination.getParentFile();
         if ((flFolder != null) && (!flFolder.exists()))
         {
            if (!flFolder.mkdirs())
            {
               // Do not throw the exception if the directory already exists
               // because it was created meanwhile for example by a different 
               // thread
               if (!flFolder.exists())
               {
                  throw new IOException("Cannot create directory " + flFolder);
               }
            }
         }
         
         // Now everything should exist so try to rename the file first
         // After testing, this renames files even between volumes C to H 
         // so we don't have to do anything else on Windows but we still
         // have to handle erro on Unix 
         if (!flCurrent.renameTo(flDestination))
         {
            // Try to copy and delete since the rename doesn't work on Solaris
            // between file systems
            copyFile(flCurrent, flDestination);
            
            // Now delete the file
            if (!flCurrent.delete())
            {
               // Delete the destination file first since we haven't really moved
               // the file
               flDestination.delete();
               throw new IOException("Cannot delete already copied file " + flCurrent);
            }
         }
      }   
   }
 
   /**
    * Copy the current file to the destination file.
    * 
    * @param flCurrent - source file
    * @param flDestination - destination file
    * @throws IOException - error message
    * @throws OSSException - error message
    */  
   public static void copyFile(
      final File flCurrent,
      final File flDestination
   ) throws IOException
   {
     // Make sure the directory exists and if not create it
     File flFolder;
     
     flFolder = flDestination.getParentFile();
     if ((flFolder != null) && (!flFolder.exists()))
     {
        if (!flFolder.mkdirs())
        {
           // Do not throw the exception if the directory already exists
           // because it was created meanwhile for example by a different 
           // thread
           if (!flFolder.exists())
           {
              throw new IOException("Cannot create directory " + flFolder);
           }
        }
     }

      // FileChannel srcChannel = null;
      // FileChannel dstChannel = null;
      FileInputStream finInput = null;
      
      //MHALAS: This code is not working reliably on Solaris 8 with 1.4.1_01
      // Getting exceptions from native code
      /*
      // Create channel on the source
      srcChannel = new FileInputStream(flCurrent).getChannel();
      // Create channel on the destination
      dstChannel = new FileOutputStream(flDestination).getChannel();
 
      // Copy file contents from source to destination
      dstChannel.transferFrom(srcChannel, 0, srcChannel.size());   
         
      Don't forget to close the channels if you enable this code again
      */
      try
      {
         finInput = new FileInputStream(flCurrent);
      }
      catch (final IOException ioExec)
      {
         if (finInput != null)
         {
            try
            {
               finInput.close();
            }
            catch (final Throwable thr)
            {
              
            }
         }
         throw ioExec;
      }

      FileUtils.copyStreamToFile(finInput, flDestination);
   }
   
   /**
    * Rename the file to temporaty name with given prefix
    * 
    * @param flFileToRename - file to rename
    * @param strPrefix - prefix to use
    * @throws IOException - error message
    */
   public static void renameToTemporaryName(
      final File   flFileToRename,
      final String strPrefix
   ) throws IOException
   {
      assert strPrefix != null : "Prefix cannot be null.";
      
      String       strParent;
      final StringBuffer sbBuffer = new StringBuffer();
      File         flTemp;
      int          iIndex = 0;
      
      strParent = flFileToRename.getParent();

      // Generate new name for the file in a deterministic way
      do
      {
         iIndex++;
         sbBuffer.delete(0, sbBuffer.length());
         if (strParent != null) 
         {
            sbBuffer.append(strParent);
            sbBuffer.append(File.separatorChar);
         }
         
         sbBuffer.append(strPrefix);
         sbBuffer.append("_");
         sbBuffer.append(iIndex);
         sbBuffer.append("_");
         sbBuffer.append(flFileToRename.getName());
               
         flTemp = new File(sbBuffer.toString());
      }      
      while (flTemp.exists());
      
      // Now we should have unique name
      if (!flFileToRename.renameTo(flTemp))
      {
         throw new IOException("Cannot rename " + flFileToRename.getAbsolutePath()
                               + " to " + flTemp.getAbsolutePath());
      }
   }

   /** 
    * Delete all files and directories in directory but do not delete the
    * directory itself.
    * 
    * @param strDir - string that specifies directory to delete
    * @return boolean - sucess flag
    */
   public static boolean deleteDirectoryContent(
      final String strDir
   )
   {
      return ((strDir != null) && (strDir.length() > 0)) 
              ? deleteDirectoryContent(new File(strDir)) : false;
   }

   /** 
    * Delete all files and directories in directory but do not delete the
    * directory itself.
    * 
    * @param fDir - directory to delete
    * @return boolean - sucess flag
    */
   public static boolean deleteDirectoryContent(
      final File fDir
   )
   {
      boolean bRetval = false;

      if (fDir != null && fDir.isDirectory()) 
      {
         final File[] files = fDir.listFiles();
   
         if (files != null)
         {
            bRetval = true;
            boolean dirDeleted;
            
            for (int index = 0; index < files.length; index++)
            {
               if (files[index].isDirectory())
               {
                  // TODO: Performance: Implement this as a queue where you add to
                  // the end and take from the beginning, it will be more efficient
                  // than the recursion
                  dirDeleted = deleteDirectoryContent(files[index]);
                  if (dirDeleted)
                  {
                     bRetval = bRetval && files[index].delete();
                  }
                  else
                  {
                     bRetval = false;
                  }
               }
               else
               {
                  bRetval = bRetval && files[index].delete();
               }
            }
         }
      }

      return bRetval;
   }

   /**
    * Deletes all files and subdirectories under the specified directory including 
    * the specified directory
    * 
    * @param strDir - string that specifies directory to be deleted
    * @return boolean - true if directory was successfully deleted
    */
   public static boolean deleteDir(
      final String strDir
   ) 
   {
      return ((strDir != null) && (strDir.length() > 0)) 
                ? deleteDir(new File(strDir)) : false;
   }
   
   /**
    * Deletes all files and subdirectories under the specified directory including 
    * the specified directory
    * 
    * @param fDir - directory to be deleted
    * @return boolean - true if directory was successfully deleted
    */
   public static boolean deleteDir(
      final File fDir
   ) 
   {
      boolean bRetval = false;
      if (fDir != null && fDir.exists())
      {
         bRetval = deleteDirectoryContent(fDir);
         if (bRetval)
         {
            bRetval = bRetval && fDir.delete();         
         }
      }
      return bRetval;
   }
   
   /**
    * Compare binary files. Both files must be files (not directories) and exist.
    * 
    * @param first  - first file
    * @param second - second file
    * @return boolean - true if files are binery equal
    * @throws IOException - error in function
    */
   public boolean isFileBinaryEqual(
      final File first,
      final File second
   ) throws IOException
   {
      // TODO: Test: Missing test
      boolean retval = false;
      
      if ((first.exists()) && (second.exists()) 
         && (first.isFile()) && (second.isFile()))
      {
         if (first.getCanonicalPath().equals(second.getCanonicalPath()))
         {
            retval = true;
         }
         else
         {
            FileInputStream firstInput = null;
            FileInputStream secondInput = null;
            BufferedInputStream bufFirstInput = null;
            BufferedInputStream bufSecondInput = null;

            try
            {            
               firstInput = new FileInputStream(first); 
               secondInput = new FileInputStream(second);
               bufFirstInput = new BufferedInputStream(firstInput, BUFFER_SIZE); 
               bufSecondInput = new BufferedInputStream(secondInput, BUFFER_SIZE);
   
               int firstByte;
               int secondByte;
               
               while (true)
               {
                  firstByte = bufFirstInput.read();
                  secondByte = bufSecondInput.read();
                  if (firstByte != secondByte)
                  {
                     break;
                  }
                  if ((firstByte < 0) && (secondByte < 0))
                  {
                     retval = true;
                     break;
                  }
               }
            }
            finally
            {
               try
               {
                  if (bufFirstInput != null)
                  {
                     bufFirstInput.close();
                  }
               }
               finally
               {
                  if (bufSecondInput != null)
                  {
                     bufSecondInput.close();
                  }
               }
            }
         }
      }
      
      return retval;
   }


      
   /**
    * Get path which represents temporary directory. It is guarantee that it 
    * ends with \ (or /).
    * 
    * @return String
    */
   public static String getTemporaryDirectory(
   )
   {
      return s_strTempDirectory;
   }
   

   /**
    * Copy any input stream to output file. Once the data will be copied
    * the stream will be closed.
    * 
    * @param input  - InputStream to copy from
    * @param output - File to copy to
    * @throws IOException - error in function
    * @throws OSSMultiException - double error in function
    */
   public static void copyStreamToFile(
      final InputStream input,
      final File        output
   ) throws IOException
   {
      FileOutputStream foutOutput = null;

      // open input file as stream safe - it can throw some IOException
      try
      {
         foutOutput = new FileOutputStream(output);
      }
      catch (final IOException ioExec)
      {
         if (foutOutput != null)
         {
            try
            {
               foutOutput.close();
            }
            catch (final IOException ioExec2)
            {
               
            }
         }            
         
         throw ioExec;
      }

      // all streams including os are closed in copyStreamToStream function 
      // in any case
      copyStreamToStream(input, foutOutput);
   }

   /**
    * Copy any input stream to output stream. Once the data will be copied
    * both streams will be closed.
    * 
    * @param input  - InputStream to copy from
    * @param output - OutputStream to copy to
    * @throws IOException - io error in function
    * @throws OSSMultiException - double error in function
    */
   public static void copyStreamToStream(
      final InputStream input,
      final OutputStream output
   ) throws IOException
   {
      InputStream is = null;
      OutputStream os = null;
      int                 ch;

      try
      {
         if (input instanceof BufferedInputStream)
         {
            is = input;
         }
         else
         {
            is = new BufferedInputStream(input);
         }
         if (output instanceof BufferedOutputStream)
         {
            os = output;
         }
         else
         {
            os = new BufferedOutputStream(output);
         }
   
         while ((ch = is.read()) != -1)
         {
            os.write(ch);
         }
         os.flush();
      }
      finally
      {
         IOException exec1 = null;
         IOException exec2 = null;
         try
         {
            // because this close can throw exception we do next close in 
            // finally statement
            if (os != null)
            {
               try
               {
                  os.close();
               }
               catch (final IOException exec)
               {
                  exec1 = exec;
               }
            }
         }
         finally
         {
            if (is != null)
            {
               try
               {
                  is.close();
               }
               catch (final IOException exec)
               {
                  exec2 = exec;
               }
            }
         }
         if ((exec1 != null) && (exec2 != null))
         {
           throw exec1;
         }
         else if (exec1 != null)
         {
            throw exec1;
         }
         else if (exec2 != null)
         {
            throw exec2;
         }
      }
   }
}
