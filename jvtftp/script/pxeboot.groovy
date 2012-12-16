import slightlysain.mac.*;
//priority LOW_COMMAND
if(request.isRead()) {
	filename = request.getFilename()
	MACMatcher match = new MACMatcher(filename)
	if(match.matches()) {
		println "match"
		accept() {
			println "DEFAULT reb"
			println "LABEL reb"
			println "  COM32 reboot"
		}
	} else if(filename.equals("reboot.c32")) {
		accept("reboot.c32")
	} else if(filename.equals("gpxelinux.0")) {
		accept("pxelinux.0")
	} else if(filename.equals("ldlinux.c32")) {
		accept("ldlinux.c32")
	} else if(filename.equals("libcom32.c32")) {
		accept("libcom32.c32")
	}
}