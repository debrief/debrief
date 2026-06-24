# 
# Debrief - the Open Source Maritime Analysis Application
# http://debrief.info
#  
# (C) 2000-2020, Deep Blue C Technology Ltd
#  
# This library is free software; you can redistribute it and/or
# modify it under the terms of the Eclipse Public License v1.0
# (http://www.eclipse.org/legal/epl-v10.html)
#  
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
#
import traceback

loadModule('/System/Resources', False);
loadModule('/Debrief/Core'); # date
loadModule('/Debrief/Spatial'); # location
loadModule('/Debrief/Tactical'); # for plot, duration, date

# get project instance
project = getProject("sample_data");

if not project.exists():
	print("Can't find project");
	sys.exit()


# create a file within the project
# @type
file = openFile("workspace://sample_data/other_formats/CSV_EXCHANGE_SAMPLE.csv", READ, True);

if not file.exists():
	print("Can't find file");
	sys.exit()

track = None;

# ok, now loop through the code

try:
	with open(str(file.getPath()), 'r') as f:
		nextLine = f.readline()
		ctr = 1
		while nextLine:
			if ctr > 2:
				
	 			#
				# Note: format looks like this: Two header lines, then rows of CSV entries. 
				# # UK TRACK EXCHANGE FORMAT, V1.0 #
				# Lat,Long,DTG,UnitName,CaseNumber,Type,Flag,Sensor,MajorAxis,SemiMajorAxis,SemiMinorAxis,Course,Speed,Depth,Likelihood,Confidence,SuppliedBy,Provenance,InfoCutoffDate,Purpose,Classification,DistributionStatement
				# 22.1862861,-21.6978806,19951212T050000Z,NELSON,D-112/12,OILER,UK,S2002,1.0,0.5,0.5,269.7000,2.0000,0.0,Remote,Low,UNIT_ALPHA,NELSON,19951212,For
				# planning,PUBLIC,"Quite a content."
				#
				
				partsOfStr = nextLine.split(',');
				
				if track is None:
					# track not created yet. Go for it.
					track = createTrack(partsOfStr[3])
			
				# location
				dLat = float(partsOfStr[0]);
				dLong = float(partsOfStr[1]);
				location = createLocation(dLat, dLong, 0);
				# dtg components
				dtgStr = partsOfStr[2];
				yrs = int(dtgStr[0: 4]);
				mons = int(dtgStr[4: 6]) - 1;
				days = int(dtgStr[6: 8]);
				hrs = int(dtgStr[9: 11]);
				mins = int(dtgStr[11: 13]);
				secs = int(dtgStr[13: 15]);
				
				# date object
				dtg = createDateCalendarFormat(yrs, mons, days, hrs, mins, secs);
				
				# course and speed
				course = partsOfStr[11];
				speed = partsOfStr[12];
				# create the fix
				fix = createFix(dtg, location, course, speed);
				# store the fix
				track.addFix(fix);
				
			nextLine = f.readline()
			ctr += 1

	if track is not None:
		# ok get somewhere to add it to
		plot = getActivePlot();
		layers = plot.getLayers();
		layers.add(track);
		layers.fireModified()

except Exception as e:
	print("Can't read file" + str(e));
	# Get current system exception
	ex_type, ex_value, ex_traceback = sys.exc_info()

	# Extract unformatter stack traces as tuples
	trace_back = traceback.extract_tb(ex_traceback)

	# Format stacktrace
	stack_trace = list()

	for trace in trace_back:
		stack_trace.append("File : %s , Line : %d, Func.Name : %s, Message : %s" % (trace[0], trace[1], trace[2], trace[3]))

	print("Exception type : %s " % ex_type.__name__)
	print("Exception message : %s" %ex_value)
	print("Stack trace : %s" %stack_trace)
    
    
	
	sys.exit()
			
