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

public final class Debug
{
    private static boolean debugOn;
    
    static{
        //from System property
        //e.g -Djip.debug=true
        debugOn=false;//Boolean.getBoolean("jip.debug");
    }
    public synchronized static void message(final Object obj){
        if(debugOn){
            final StringBuffer buf=new StringBuffer();
            buf.append("debug: ");
            buf.append(obj);
            System.out.println(buf.toString());
        }
    }
    
}