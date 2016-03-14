REM run the PDU generator, passing to it the control file (as parameter one)
REM $1 the control file, containing ip, port, network mode, time-step, num participants, num messages
REM e.g.  239.1.2.3 62040 multicast 1000 12 15
REM 
java -cp ../../plugins/org.mwc.debrief.dis_1.0.6.jar;dis-enums_1.1.jar org.mwc.debrief.dis.diagnostics.PduGenerator %1