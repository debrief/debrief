# Parameters:
# group = the Multicast address group to use
# port = the Multicast port to use
# root = the folder destination for file outputs
#
java -Dgroup=239.1.2.3 -Dport=62040 -Droot=out -cp ../target/org.mwc.debrief.dis-1.0.1.jar:../libs/dis-enums_1.1.jar org.mwc.debrief.dis.diagnostics.HeadlessDISLogger
