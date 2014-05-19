package MWC.TacticalData.GND;

import java.util.ArrayList;
import java.util.Date;

public interface IDataset {
	public ArrayList<String> getDataTypes();
	public String getName();
	public double[] getDataset(String name);
	public Date[] getTimes();
}
