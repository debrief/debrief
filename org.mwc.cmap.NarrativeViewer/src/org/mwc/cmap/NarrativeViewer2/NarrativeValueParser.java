package org.mwc.cmap.NarrativeViewer2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NarrativeValueParser {

	public static List<NarrativeEntry> getInput() {
		List<NarrativeEntry> result = new ArrayList<NarrativeEntry>();
		
		try (InputStreamReader ir = new InputStreamReader(NarrativeValueParser.class.getResourceAsStream("test.rep"));
				BufferedReader br = new BufferedReader(ir);) {
			
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.substring(line.indexOf(":")+2);
				
				int logIndex = 0;
				for (int i = 0; i < 4; i++) {
					logIndex = line.indexOf(" ", logIndex) + 1;
				}
				
				String fixed = line.substring(0, logIndex);
				String log = line.substring(logIndex);
				
				String[] values = fixed.split(" ");
				
				result.add(new NarrativeEntry(values[0], values[1], values[2], values[3], log));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		for (NarrativeEntry narrativeEntry : getInput()) {
			System.out.println(narrativeEntry);
		}
	}
}
