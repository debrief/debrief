# Parameters:
# group = the Multicast address group to use
# port = the Multicast port to use
# mode = networking mode (only multicast is expected to work)
# millis = time interval between each message
# participants = number of platforms to simulate
# messages = the number of simulation cycles to process
#
java  -Dgroup=239.1.2.3 -Dport=62040 -Dmode=multicast -Dmillis=1000 -Dparticipants=8 -Dmessages=100 -cp ../target/org.mwc.debrief.dis-1.0.1.jar org.mwc.debrief.dis.diagnostics.PduGenerator
