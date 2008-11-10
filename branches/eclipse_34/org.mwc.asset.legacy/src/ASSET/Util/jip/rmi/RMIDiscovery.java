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


package ASSET.Util.jip.rmi;

import java.rmi.*;
import java.net.*;
import java.io.*;

import ASSET.Util.jip.tools.Debug;

/**
 * Utility class used by RMI clients to discover RMI servers in a Jini like way.
 * 
 * e.g Remote server=RMIDiscovery.lookup(interfaceClass,serverName);
 * 
 * @author Phil Bishop
 * @version 1
 * @since 1
 * @see RMILookup
 */
public class RMIDiscovery{
    
    /**
     * The interface class object for the server we are tring to discover
     * 
     * @since 1
     */
    private Class _serviceInterface;

    /**
     * The unique name of the server we are trying to discover
     */
    private String _serviceName;

    /**
     * Unicast listener waiting for responses from the RMILookupService
     */
    private ServerSocket _listener;

    /**
     * The port the _listener socketing is listening on
     */
    private int _listenerPort;

    /**
     * Can either be a Remote object or Exception (if failed)
     */
    private Object _discoveryResult;

    /**
     * thread synchronization/notification lock.
     */
    private Object _lock=new Object();


    /** flag to indicate that we are still requesting listener implementations
     *
     */
    private static boolean _stillLooking = true;

    /** list of remote implementations we've found
     *
     */
    private java.util.Vector _implFound = null;



    private RMIDiscovery(final Class serviceInterface,final String serviceName){
        _serviceInterface=serviceInterface;
        _serviceName=serviceName;
        if(_serviceName==null || _serviceName.length()==0){
            _serviceName=Discovery.ANY;
        }
        
    }
    /**
     * Find first matching services via multicast
     * 
     * @param serviceInterface Interface that the server we are trying to discover
     * @param serviceName Unique name of server we are trying to discover.
     * @return The discovered server ref.
     * @exception java.rmi.ConnectException
     */
    public static Remote lookup(final Class serviceInterface,final String serviceName)
        throws java.rmi.ConnectException{
        
        final RMIDiscovery disco=new RMIDiscovery(serviceInterface,serviceName);
        return disco.lookupImpl();
    }

    /**
     * Find first matching services via multicast
     *
     * @param serviceInterface Interface that the server we are trying to discover
     * @param serviceName Unique name of server we are trying to discover.
     * @return The discovered server ref.
     * @exception java.rmi.ConnectException
     */
    public static Remote[] lookupAll(final Class serviceInterface,final String serviceName)
        throws java.rmi.ConnectException{

        final RMIDiscovery disco=new RMIDiscovery(serviceInterface,serviceName);
        return disco.lookupAllImpl();
    }

    /**
     * return matching service via unicast
     * 
     * @param serviceName Name of service
     * @param host host to attempt lookup on
     */
    public static Remote lookup(final String serviceName,final String host){
        final Remote [] remote=lookupAll(serviceName,new String[]{host},false);
        return remote[0];
    }

    /**
    * return the first match service in the host[] via unicast
    *
    * @param serviceName Name of service
    * @param host hosts to attempt lookups on
    * 
    */
    public static Remote lookup(final String serviceName,final String [] host){
        final Remote [] remote=lookupAll(serviceName,host,false);
        return remote[0];
    }
    
    /**
    * return all matching services via unicast
    *
    * @param serviceName Name of service
    * @param host hosts to attempt lookups on
    */
    
    public static Remote [] lookupAll(final String serviceName,final String [] host){
        return lookupAll(serviceName,host,true);
    }
    
    
    //impl
    private static Remote [] lookupAll(final String serviceName,final String [] host,final boolean tryAll){
        
        final String url="rmi://";
        final String imPrefix=Discovery.getRegistyUrlPrefix();
        final Remote remote[] =new Remote[host.length];
        
        for(int i=0;i<host.length;i++){
            try{
                final String hostAndPort=host[i];
                final StringBuffer buf=new StringBuffer();
                buf.append(url);
                buf.append(hostAndPort);
                buf.append("/");
                buf.append(imPrefix);
                buf.append(serviceName);
                
                Debug.message("RMI discovery: Using unicast url "+buf.toString());
                
                remote[i]=Naming.lookup(buf.toString());
                if(tryAll==false){
                    return new Remote[]{remote[i]};
                }
                
            }catch(Exception ex){
                System.err.println(ex.getMessage());
            }
        }
        return remote;
    }


    private Remote[] lookupAllImpl() throws java.rmi.ConnectException
    {

      _stillLooking = true;
      _implFound = new java.util.Vector(0,1);

      startListener();
      startRequester();
      synchronized(_lock){

          while(_discoveryResult==null){
              try{
                  _lock.wait();
              }catch(InterruptedException ex){
                  ex.printStackTrace();
                  return null;
              }
          }
      }
      try{
          _listener.close();
      }catch(IOException ex){
          ex.printStackTrace(System.err);
      }
      //check if the result is an exception
      if(_discoveryResult instanceof Exception){
          throw new java.rmi.ConnectException("RMI discovery exception",(Exception)_discoveryResult);
      }

      Remote[] res = new Remote[_implFound.size()];
      res = (Remote[])_implFound.toArray(res);
      return res;

    }

    private Remote lookupImpl()
        throws java.rmi.ConnectException{

        startListener();
        startRequester();
        synchronized(_lock){
            
            while(_discoveryResult==null){
                try{
                    _lock.wait();
                }catch(InterruptedException ex){
                    ex.printStackTrace();
                    return null;
                }
            }
        }
        try{
            _listener.close();
        }catch(IOException ex){
            ex.printStackTrace(System.err);
        }
        //check if the result is an exception
        if(_discoveryResult instanceof Exception){
            throw new java.rmi.ConnectException("RMI discovery exception",(Exception)_discoveryResult);
        }
        return (Remote)_discoveryResult;
    }
    private void startListener(){
         final int port=Discovery.getUnicastPort();
         final int range=Discovery.getUnicastPortRange();
         
         for(int i=port;_listener==null && i<port+range;i++){
            try{
                _listener =new ServerSocket(i);
                _listenerPort=i;
            }catch(IOException ex){
                System.err.println("Port "+i+" exception "+ex.getMessage());
            }
         }
         if(_listener==null){
            throw new RuntimeException("Failed to create listener socket in port range "+port+"-"+(port+range));
         }
         final Thread listenerThread=new Thread(){
            public void run(){
                    
                try{
                    final Socket sock=_listener.accept();
                    final ObjectInputStream ois=new ObjectInputStream(sock.getInputStream());
                    final MarshalledObject mo=(MarshalledObject)ois.readObject();
                    sock.close();
                    _discoveryResult=mo.get();
                    if(_implFound != null)
                    {
                      _implFound.add(_discoveryResult);
                    }
                }catch(IOException ex){
                    _discoveryResult=ex;
                }catch(ClassNotFoundException ex){
                    _discoveryResult=ex;
                }
                synchronized(_lock){
                   _lock.notify();
                }
            }
         };
         listenerThread.start();
         Debug.message("RMI discovery: Unicast Listener thread started ");
    }
    private void startRequester(){
        
        final Thread requester=new Thread(){
            public void run(){
                try{
                    final String hostName=InetAddress.getLocalHost().getHostName();
                    
                    final InetAddress address=Discovery.getMulticastAddress();
                    final int multicastPort=Discovery.getMulticastPort();
                    final String header=Discovery.getProtocolHeader();
                    final String delim=Discovery.getProtocolDelim();
                    
                    final String outMsg=header+delim+_listenerPort+delim+_serviceInterface.getName()+delim+_serviceName;
                    final byte [] buf=outMsg.getBytes();
                            	
                    final MulticastSocket socket = new MulticastSocket(multicastPort);
                    socket.joinGroup(address);
                    
//                    int nAttempts=7;
                    final int nAttempts=3;
                    for(int nTimes=0;_discoveryResult==null && nTimes<nAttempts;nTimes++){
                        
                        //can we move this out of the loop?
                        final DatagramPacket packet = new DatagramPacket(buf, buf.length, address,multicastPort);
                             
                        Debug.message("RMI discovery: Sending request "+outMsg);
                        socket.send(packet);
                        Thread.sleep(5000);
                    }        
                    socket.leaveGroup(address);
                    socket.close();
                    _stillLooking = false;
                    if(_discoveryResult==null){
                        throw new Exception("RMI discovery timed out after "+nAttempts);
                    }
                }catch(Exception ex){
                    _discoveryResult=ex;
                    synchronized(_lock){
                        _lock.notifyAll();
                    }
                }
            }
        };
        requester.start();
        Debug.message("RMI discovery: Requester thread started ");
    }
    
}