import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class ControllerUI extends ViewPart {

	public static final String ID = "ControllerUI"; // TODO Needs to be whatever is mentioned in plugin.xml
	private Composite top = null;

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		top = new Composite(parent, SWT.NONE);
		top.setLayout(new GridLayout());
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
