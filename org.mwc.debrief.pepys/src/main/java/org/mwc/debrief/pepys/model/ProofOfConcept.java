package org.mwc.debrief.pepys.model;

import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

import org.mwc.debrief.pepys.model.bean.DatafileTypes;
import org.mwc.debrief.pepys.model.bean.Datafiles;
import org.mwc.debrief.pepys.model.bean.States;

public class ProofOfConcept {
	public static void main(String []args)
	{
		try {
			List list = DatabaseConnection.getInstance().listAll(Datafiles.class, null);
			
			List list2 = DatabaseConnection.getInstance().listAll(DatafileTypes.class, null);
			
			List list3 = DatabaseConnection.getInstance().listAll(States.class, null);
			
			List list4 = DatabaseConnection.getInstance().listAll(States.class, "source_id = 3");
			
			for ( Object l : list ) {
				final Datafiles dataFile = (Datafiles)l;
				System.out.println("DataFile ID: " + dataFile.getDatafile_id());
			}
			
			for ( Object l : list2 ) {
				final DatafileTypes datafileTypes = (DatafileTypes)l;
				System.out.println("DataFile ID: " + datafileTypes.getDatafile_type_id());
			}
			
			for ( Object l : list3 ) {
				final States state = (States)l;
				System.out.println("DataFile ID: " + state.getState_id());
			}
			
			for ( Object l : list4 ) {
				final States state = (States)l;
				System.out.println("DataFile Filtered ID: " + state.getState_id());
			}
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | PropertyVetoException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
