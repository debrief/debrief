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

/*
System properties required
jip.rmi.multicast.address
jip.rmi.multicast.port
jip.rmi.unicast.port
jip.rmi.unicast.portRange
jip.rmi.protocol.header
jip.rmi.protocol.delim
*/

public final class Discovery{
    
    private static java.util.Properties _props;
    public static final String ANY="any";
    
    private Discovery(){
    }
    
    public static void setProperties(final java.util.Properties props){
        _props=props;
    }
    public static void setProperties(final String fileName)
        throws java.io.IOException{
            
        final java.io.FileInputStream fis=
            new java.io.FileInputStream(fileName);
        _props=new java.util.Properties();
        _props.load(fis);
        fis.close();
        _props.list(System.out);
    }
    public static java.net.InetAddress getMulticastAddress()
        throws java.net.UnknownHostException{
    
        final String multicastAddress=_props.getProperty("jip.rmi.multicast.address");
        return java.net.InetAddress.getByName(multicastAddress); 
    }
    public static int getMulticastPort(){
        return getIntProperty("jip.rmi.multicast.port");
    }
    public static String getProtocolDelim(){
        return _props.getProperty("jip.rmi.protocol.delim"); 
    }
    public static String getProtocolHeader(){
        return _props.getProperty("jip.rmi.protocol.header");
    }
    public static int getUnicastPort(){
          return getIntProperty("jip.rmi.unicast.port");
    }
    public static int getUnicastPortRange(){
          return getIntProperty("jip.rmi.unicast.portRange");
    }
    public static String getRegistyUrlPrefix(){
        return _props.getProperty("jip.rmi.registry.urlPrefix");
    }
    private static int getIntProperty(final String propertyName){
        final String str=_props.getProperty(propertyName);
        return Integer.parseInt(str);
    }
}