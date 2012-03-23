package MWC.TacticalData.GND;

import java.net.URL;
import java.util.Enumeration;

import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;

public class GPackage extends BaseLayer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private URL[] _urls;

	public GPackage(String name, URL[] urls)
	{
		super(false);
		super.setName(name);
		
		_urls = urls;
		
	}

	@Override
	public Enumeration<Editable> elements() {
		// have we populated ourselves?
		if(super.size() == 0)
		{
			// have a go at self-populating
			for (int i = 0; i < _urls.length; i++) {
				URL url = _urls[i];
				GDataset data = new GDataset(url);
				GTrack track = new GTrack(data);
				this.add(track);
			}
		}
		
		// TODO Auto-generated method stub
		return super.elements();
	}
	
	
}
