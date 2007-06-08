/*
*  javainpractice.net 1999-2001
*
*  This software is intended to be used for educational purposes only.
*
*  We make no representations or warranties about the
*  suitability of the software.
*
*  Any feedback relating to this software can be sent to
*  info@javainpractice.net
*
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
    private static void serviceRequest(final Socket request){
        
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