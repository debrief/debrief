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

package ASSET.Util.jip.tools;

import java.net.*;
import java.io.*;

/**
* A simple Jar/Class server.
* 
* Only supports downloading of Jar/Classes in the directory 
* from which the class has been run.
*
*/

public class JarServer{

    public static void main(final String [] args){
        try{
            if(args.length==0){
                
                System.err.println("syntax: JarServer <port>");
                System.exit(1);
            
            }
            
            final ServerSocket server=new ServerSocket(Integer.parseInt(args[0]));            
            while(true){
                final Socket request=server.accept();
                final Thread t = new Thread(){
                    public void run(){
                        serviceRequest(request);
                        
                    }
                };
                t.start();
            }                       
        }catch(Exception ex){
            ex.printStackTrace();
        }
    
    }
    static void serviceRequest(final Socket request){
        
        try{
            
            final BufferedReader br=new BufferedReader(
                new InputStreamReader(request.getInputStream()));
            
            final String ln=br.readLine();
            
            final int spos=ln.indexOf("/");
            final int epos=ln.indexOf(" ",spos);
            final String fileName=ln.substring(spos+1,epos);
            
            final DataOutputStream os= new DataOutputStream(request.getOutputStream());
            
            try{
            
                System.out.println("Request for "+fileName);
                if(!fileName.endsWith(".jar") && !fileName.endsWith(".class")){
                    throw new IOException("Invalid java type: must be .class or .jar");
                }
            
                final File file=new File(fileName);
                final long len=file.length();
                
                final FileInputStream fis=new FileInputStream(file);
                
                os.writeBytes("HTTP/1.0 200 OK\r\n");
		        os.writeBytes("Content-Length: " + len + "\r\n");
		        os.writeBytes("Content-Type: application/java\r\n\r\n");
                
                final byte [] b=new byte[1024];
                int nBytes=fis.read(b);
                while(nBytes!=-1){
                    
                    os.write(b,0,nBytes);
                    nBytes=fis.read(b);
                }
                fis.close();
                
            }catch(IOException ex){
                System.out.println(ex.getMessage());
                os.writeBytes("HTTP/1.0 404 Not found\r\n\r\n");
            }
            
            os.flush();     
            os.close();
            request.close();
            
        }catch(IOException ex){
            System.out.println(ex);
        }
        
    }
}