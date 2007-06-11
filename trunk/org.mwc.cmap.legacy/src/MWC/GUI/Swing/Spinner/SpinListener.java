package MWC.GUI.Swing.Spinner;

/* All import statements... */
import java.util.EventListener;



/**
 * This is the listener for receiving events in a Spinner.
 *
 * This code is copyright Kevin Mayer (kmayer@layer9.com) 2001 and can
 * be copied and reproduced freely so long as all original comments in the
 * source code remain in tact. Comments may be added but not removed. If
 * you do modify the spinner classes, I would appreciate seeing your
 * modifications (though this is not compulsory).
 * Basically - here's something for all to use!
 *
 * @author Kevin Mayer
 *         (<A HREF="mailto:kmayer@layer9.com">kmayer@layer9.com</A>)
 * @version (RCS: $Revision: 1.1.1.1 $)
 */
public interface SpinListener extends EventListener {
    /**
     * If this listener is registered with a Spinner, this method will
     * be called when the control is 'spun' up.
     *
     * @param event The SpinEvent providing information about the 'spin'.
     */
    public void spinnerSpunUp(SpinEvent event);


    /**
     * If this listener is registered with a Spinner, this method will
     * be called when the control is 'spun' up.
     *
     * @param event The SpinEvent providing information about the 'spin'.
     */
    public void spinnerSpunDown(SpinEvent event);
}
