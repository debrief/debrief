

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