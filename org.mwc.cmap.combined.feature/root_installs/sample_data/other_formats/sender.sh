# run the PDU generator
# $1 the control file, containing ip, port, network mode, time-step, num participants, num messages
# e.g.  239.1.2.3 62040 multicast 1000 12 15
java -cp ../../plugins/org.mwc.debrief.dis_1.0.2.jar org.mwc.debrief.dis.diagnostics.PduGenerator $1