package com.borlander.rac525791.dashboard.layout;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.borlander.rac525791.dashboard.layout.data.SuiteImpl;

public class DashboardUIModel {
	private final List<ControlUISuite> mySuites;
	
	public DashboardUIModel(ControlUISuite... possibleSizes){
		mySuites = new LinkedList<ControlUISuite>();
		for (ControlUISuite next : possibleSizes){
			mySuites.add(next);
		}
		Collections.sort(mySuites, new Comparator<ControlUISuite>(){
			public int compare(ControlUISuite s1, ControlUISuite s2) {
				return s1.getPreferredSizeRO().width - s2.getPreferredSizeRO().width;
			}
		});
	}
	
	public DashboardUIModel(){
		this(SuiteImpl.create280x160(), SuiteImpl.create320x183(), SuiteImpl.create360x206(), SuiteImpl.create400x229());
	}

	public ControlUISuite getUISuite(int actualWidth, int actualHeight) {
		ControlUISuite bestSuite = null;
		for (ControlUISuite next : mySuites){
			if (bestSuite == null){
				bestSuite = next;
			}
			int nextWidth = next.getPreferredSizeRO().width;
			int nextHeight = next.getPreferredSizeRO().height;
			if (nextWidth > actualWidth || nextHeight > actualHeight){
				break;
			}
			bestSuite = next;
		}
		return bestSuite;
	}

	public void dispose() {
		for (ControlUISuite next : mySuites){
			next.dispose();
		}
		//next invocation of any method will fail.
		mySuites.clear();
		
	}

}
